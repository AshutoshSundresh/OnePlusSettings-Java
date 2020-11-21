package com.oneplus.security.network.trafficalarm;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.oneplus.security.network.NetworkPolicyEditor;
import com.oneplus.security.network.operator.OperatorDataModelFactory;
import com.oneplus.security.network.operator.OperatorModelInterface;
import com.oneplus.security.network.operator.OperatorPackageUsageUpdater;
import com.oneplus.security.network.simcard.SimcardDataModel;
import com.oneplus.security.network.simcard.SimcardDataModelInterface;
import com.oneplus.security.network.trafficinfo.NativeTrafficDataModel;
import com.oneplus.security.utils.FunctionUtils;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.OPSNSUtils;
import com.oneplus.security.utils.Utils;

public class TrafficUsageAlarmIntentService extends Service implements Handler.Callback, OperatorPackageUsageUpdater {
    private static final Object sDialogShowLock = new byte[0];
    private Context mContext;
    private int mCurrentSlotId;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private Handler mMainThreadHandler;
    private OperatorModelInterface mOperatorDataModel;
    private NetworkPolicyEditor mPolicyEditor;
    private NetworkPolicyManager mPolicyManager;
    private AlertDialog mRunningOutAlertDialog;
    private AlertDialog mRunningOutAndCloseNetworkDialog;
    private SimcardDataModelInterface mSimcardDataModel;
    private AlertDialog mTenPercentLeftAlertDialog;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, TrafficUsageAlarmIntentService.class));
    }

    public static void startSimStatusService(Context context) {
        Intent intent = new Intent(context, TrafficUsageAlarmIntentService.class);
        intent.setAction("com.oneplus.security.sim_status");
        context.startService(intent);
    }

    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        LogUtils.d("TrafficUsageAlarmIntentService", "create usage alarm service");
        HandlerThread handlerThread = new HandlerThread("TrafficUsageAlarmIntentService");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper(), this);
        this.mMainThreadHandler = new Handler(getMainLooper(), this);
        this.mSimcardDataModel = SimcardDataModel.getInstance(getApplicationContext());
        iniOperatorDataModel();
        this.mSimcardDataModel.registerSimStateListener(null);
        NetworkPolicyManager from = NetworkPolicyManager.from(this.mContext.getApplicationContext());
        this.mPolicyManager = from;
        this.mPolicyEditor = new NetworkPolicyEditor(from);
    }

    private void iniOperatorDataModel() {
        int currentTrafficRunningSlotId = this.mSimcardDataModel.getCurrentTrafficRunningSlotId();
        this.mCurrentSlotId = currentTrafficRunningSlotId;
        if (currentTrafficRunningSlotId < 0) {
            LogUtils.e("TrafficUsageAlarmIntentService", "query data alert with invalid slotId.");
        }
        Log.d("TrafficUsageAlarmIntentService", "iniOperatorDataModel: " + this.mSimcardDataModel.isSlotOperatorSupportedBySdk(this.mCurrentSlotId));
        OperatorModelInterface operatorDataModel = OperatorDataModelFactory.getOperatorDataModel(getApplicationContext());
        this.mOperatorDataModel = operatorDataModel;
        operatorDataModel.addTrafficUsageUpdater(this);
    }

    private void refreshDataModelIfNeeded() {
        int currentTrafficRunningSlotId = this.mSimcardDataModel.getCurrentTrafficRunningSlotId();
        if (currentTrafficRunningSlotId < 0) {
            LogUtils.e("TrafficUsageAlarmIntentService", "query data alert with invalid slotId.");
        }
        if (this.mCurrentSlotId != currentTrafficRunningSlotId) {
            LogUtils.e("TrafficUsageAlarmIntentService", "initDataModelIfNeeded");
            this.mCurrentSlotId = currentTrafficRunningSlotId;
            this.mOperatorDataModel.removeTrafficUsageUpdater(this);
            this.mOperatorDataModel.clearData();
            OperatorModelInterface operatorDataModel = OperatorDataModelFactory.getOperatorDataModel(getApplicationContext());
            this.mOperatorDataModel = operatorDataModel;
            operatorDataModel.addTrafficUsageUpdater(this);
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null && "com.oneplus.security.sim_status".equals(intent.getAction()) && this.mSimcardDataModel.isSlotSimReady(this.mCurrentSlotId)) {
            Log.d("TrafficUsageAlarmIntentService", "SlotSimReady ");
            iniOperatorDataModel();
        }
        refreshDataModelIfNeeded();
        boolean z = false;
        if (intent != null) {
            String action = intent.getAction();
            LogUtils.e("TrafficUsageAlarmIntentService", "onStartCommand:" + intent.getAction());
            if ("com.oneplus.security.network.trafficalarm.ten_percent_alarm".equals(action) || "com.oneplus.security.network.trafficalarm.running_out_data".equals(action) || "com.oneplus.security.network.trafficalarm.running_out_data_and_close_network".equals(action)) {
                this.mOperatorDataModel.requesetPkgMonthlyUsageAndTotalInByte(this.mCurrentSlotId);
                z = true;
            }
        }
        if (!z) {
            this.mOperatorDataModel.requesetDataUsageAndNotify(this.mCurrentSlotId);
        }
        return super.onStartCommand(intent, i, i2);
    }

    public void onDestroy() {
        super.onDestroy();
        this.mOperatorDataModel.removeTrafficUsageUpdater(this);
        this.mOperatorDataModel.clearData();
        this.mHandlerThread.quit();
        LogUtils.d("TrafficUsageAlarmIntentService", "destroy usage alarm service");
        this.mHandler = null;
        this.mSimcardDataModel.removeSimStateListener(null);
    }

    public boolean handleMessage(Message message) {
        int currentTrafficRunningSlotId = this.mSimcardDataModel.getCurrentTrafficRunningSlotId();
        this.mCurrentSlotId = currentTrafficRunningSlotId;
        if (currentTrafficRunningSlotId != message.arg1) {
            LogUtils.d("TrafficUsageAlarmIntentService", "current default data sim card slotId not eq the slotId.");
            return false;
        }
        int i = message.what;
        if (i == 0) {
            showDataWarnAlertDialog();
        } else if (i == 1) {
            showPkgRunningOutAlertDialog();
        } else if (i == 2) {
            showPkgRunningOutAutoCloseAlertDialog();
        } else if (i == 3) {
            iniOperatorDataModel();
            if (this.mSimcardDataModel.isSlotOperatorSupportedBySdk(this.mCurrentSlotId)) {
                this.mOperatorDataModel.requesetPkgMonthlyUsageAndTotalInByte(this.mCurrentSlotId);
            } else {
                checkDataUsedShouldAlert(this.mOperatorDataModel.getPkgTotalInByte(this.mCurrentSlotId), this.mOperatorDataModel.getPkgUsedMonthlyInByte(this.mCurrentSlotId), this.mCurrentSlotId);
            }
        }
        return false;
    }

    private void showPkgRunningOutAutoCloseAlertDialog() {
        CharSequence charSequence;
        CharSequence charSequence2;
        Log.d("TrafficUsageAlarmIntentService", "mRunningOutAndCloseNetworkDialog: " + this.mRunningOutAndCloseNetworkDialog);
        if (this.mRunningOutAndCloseNetworkDialog != null) {
            Log.d("TrafficUsageAlarmIntentService", "mRunningOutAndCloseNetworkDialog.isshow: " + this.mRunningOutAndCloseNetworkDialog.isShowing());
        }
        AlertDialog alertDialog = this.mRunningOutAndCloseNetworkDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            synchronized (sDialogShowLock) {
                this.mSimcardDataModel.setDataEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(this, C0018R$style.OnePlus_Theme_Dialog_Pop);
                if (FunctionUtils.isUsvMode()) {
                    builder.setTitle(C0017R$string.traffic_usage_runout_title_vzw);
                    builder.setMessage(getText(C0017R$string.hint_traffic_auto_close_network_title_vzw));
                    charSequence2 = getText(C0017R$string.traffic_auto_close_network_reopen_vzw);
                    charSequence = getText(C0017R$string.traffic_auto_close_network_keep_closed_vzw);
                } else {
                    builder.setTitle(C0017R$string.traffic_usage_runout_title);
                    builder.setMessage(getText(C0017R$string.hint_traffic_auto_close_network_title));
                    charSequence2 = getText(C0017R$string.traffic_auto_close_network_reopen);
                    charSequence = getText(C0017R$string.traffic_auto_close_network_keep_closed);
                }
                builder.setPositiveButton(charSequence2, new DialogInterface.OnClickListener() {
                    /* class com.oneplus.security.network.trafficalarm.TrafficUsageAlarmIntentService.AnonymousClass1 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        TrafficUsageAlarmUtils.setHasAlertedTrafficRunningOut(TrafficUsageAlarmIntentService.this.mContext, true, TrafficUsageAlarmIntentService.this.mCurrentSlotId);
                        NetworkTemplate networkTemplate = NativeTrafficDataModel.getNetworkTemplate(OPSNSUtils.findSubIdBySlotId(TrafficUsageAlarmIntentService.this.mCurrentSlotId));
                        Utils.sendAppTracker("data_limit", 0);
                        TrafficUsageAlarmUtils.setDataTotalState(TrafficUsageAlarmIntentService.this.mContext, true, TrafficUsageAlarmIntentService.this.mCurrentSlotId);
                        TrafficUsageAlarmIntentService.this.mPolicyEditor.setPolicyLimitBytes(networkTemplate, -1);
                        Log.d("setPolicyLimitBytes ", "setPolicyLimitBytes:3 ");
                        TrafficUsageAlarmIntentService.this.mSimcardDataModel.setDataEnabled(true);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(charSequence, new DialogInterface.OnClickListener() {
                    /* class com.oneplus.security.network.trafficalarm.TrafficUsageAlarmIntentService.AnonymousClass2 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        TrafficUsageAlarmUtils.setHasAlertedTrafficRunningOut(TrafficUsageAlarmIntentService.this.mContext, true, TrafficUsageAlarmIntentService.this.mCurrentSlotId);
                        NetworkTemplate networkTemplate = NativeTrafficDataModel.getNetworkTemplate(OPSNSUtils.findSubIdBySlotId(TrafficUsageAlarmIntentService.this.mCurrentSlotId));
                        TrafficUsageAlarmUtils.setDataTotalState(TrafficUsageAlarmIntentService.this.mContext, true, TrafficUsageAlarmIntentService.this.mCurrentSlotId);
                        TrafficUsageAlarmIntentService.this.mPolicyEditor.setPolicyLimitBytes(networkTemplate, -1);
                        Log.d("setPolicyLimitBytes ", "setPolicyLimitBytes:4 ");
                        TrafficUsageAlarmIntentService.this.mSimcardDataModel.setDataEnabled(false);
                        dialogInterface.dismiss();
                    }
                });
                TrafficUsageAlarmUtils.setHasAlertedTrafficRunningOut(this.mContext, true, this.mCurrentSlotId);
                builder.setCancelable(false);
                AlertDialog create = builder.create();
                this.mRunningOutAndCloseNetworkDialog = create;
                create.getWindow().setType(2003);
                this.mRunningOutAndCloseNetworkDialog.show();
                LogUtils.d("TrafficUsageAlarmIntentService", "showPkgRunningOutAutoCloseAlertDialog");
            }
        }
    }

    private void showPkgRunningOutAlertDialog() {
        AlertDialog alertDialog = this.mRunningOutAlertDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            synchronized (sDialogShowLock) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, C0018R$style.OnePlus_Theme_Dialog_Pop);
                builder.setTitle(C0017R$string.traffic_usage_runout_title);
                builder.setMessage(getText(C0017R$string.hint_traffic_is_running_out));
                builder.setPositiveButton(getText(C0017R$string.confirm_disable_data_network), new DialogInterface.OnClickListener() {
                    /* class com.oneplus.security.network.trafficalarm.TrafficUsageAlarmIntentService.AnonymousClass3 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        TrafficUsageAlarmIntentService.this.mPolicyEditor.read();
                        TrafficUsageAlarmIntentService.this.mPolicyEditor.setPolicyLimitBytes(NativeTrafficDataModel.getNetworkTemplate(OPSNSUtils.findSubIdBySlotId(TrafficUsageAlarmIntentService.this.mCurrentSlotId)), -1);
                        Log.d("setPolicyLimitBytes ", "setPolicyLimitBytes:1 ");
                        TrafficUsageAlarmUtils.setHasAlertedTrafficRunningOut(TrafficUsageAlarmIntentService.this.mContext, true, TrafficUsageAlarmIntentService.this.mCurrentSlotId);
                        TrafficUsageAlarmIntentService.this.mSimcardDataModel.setDataEnabled(false);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(getText(C0017R$string.cancel_disable_data_network), new DialogInterface.OnClickListener() {
                    /* class com.oneplus.security.network.trafficalarm.TrafficUsageAlarmIntentService.AnonymousClass4 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        TrafficUsageAlarmUtils.setHasAlertedTrafficRunningOut(TrafficUsageAlarmIntentService.this.mContext, true, TrafficUsageAlarmIntentService.this.mCurrentSlotId);
                        dialogInterface.dismiss();
                    }
                });
                builder.setCancelable(false);
                AlertDialog create = builder.create();
                this.mRunningOutAlertDialog = create;
                create.getWindow().setType(2003);
                this.mRunningOutAlertDialog.show();
                LogUtils.d("TrafficUsageAlarmIntentService", "showPkgRunningOutAlertDialog");
            }
        }
    }

    private void showDataWarnAlertDialog() {
        AlertDialog alertDialog = this.mTenPercentLeftAlertDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            synchronized (sDialogShowLock) {
                String[] formattedFileSizeAndUnitForDisplay = Utils.getFormattedFileSizeAndUnitForDisplay(this.mContext, TrafficUsageAlarmUtils.getDataWarnValue(this.mContext, this.mCurrentSlotId, 0), true, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(this, C0018R$style.OnePlus_Theme_Dialog_Pop);
                builder.setTitle(C0017R$string.traffic_usage_warn_title);
                int i = C0017R$string.hint_data_used_alert;
                builder.setMessage(getString(i, new Object[]{formattedFileSizeAndUnitForDisplay[0] + formattedFileSizeAndUnitForDisplay[1]}));
                builder.setPositiveButton(getText(C0017R$string.confirm_below_ten_percent_alert), new DialogInterface.OnClickListener() {
                    /* class com.oneplus.security.network.trafficalarm.TrafficUsageAlarmIntentService.AnonymousClass5 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        TrafficUsageAlarmUtils.setHasDataWarnAlerted(TrafficUsageAlarmIntentService.this.mContext, true, TrafficUsageAlarmIntentService.this.mCurrentSlotId);
                    }
                });
                builder.setCancelable(false);
                AlertDialog create = builder.create();
                this.mTenPercentLeftAlertDialog = create;
                create.getWindow().setType(2003);
                this.mTenPercentLeftAlertDialog.show();
                LogUtils.d("TrafficUsageAlarmIntentService", "showTenPercentLeftAlertDialog");
            }
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorPackageUsageUpdater
    public void onTrafficTotalAndUsedUpdate(long j, long j2, int i) {
        LogUtils.d("TrafficUsageAlarmIntentService", "onTrafficTotalAndUsedUpdate totalByte:" + j + ",usedByte:" + j2 + ",slotId:" + i);
        if (this.mHandler == null) {
            stopSelf();
            return;
        }
        SimcardDataModelInterface simcardDataModelInterface = this.mSimcardDataModel;
        if (simcardDataModelInterface != null) {
            this.mCurrentSlotId = simcardDataModelInterface.getCurrentTrafficRunningSlotId();
        }
        if (this.mCurrentSlotId == i) {
            LogUtils.d("TrafficUsageAlarmIntentService", "total is " + j + " used is " + j2);
            if (j == -1 && j2 == -1) {
                LogUtils.d("TrafficUsageAlarmIntentService", "total pkg usage returned is invalid");
                this.mHandler.removeMessages(3);
                Message obtainMessage = this.mHandler.obtainMessage(3);
                obtainMessage.arg1 = i;
                this.mHandler.sendMessageDelayed(obtainMessage, 2000);
                return;
            }
            checkDataUsedShouldAlert(j, j2, i);
        }
    }

    private void checkDataUsedShouldAlert(long j, long j2, int i) {
        Log.d("TrafficUsageAlarmIntentService", "checkDataUsedShouldAlert: mHandler" + this.mHandler);
        if (this.mHandler == null) {
            stopSelf();
            return;
        }
        Log.d("TrafficUsageAlarmIntentService", "checkDataUsedShouldAlert: usedbyte" + j2);
        Log.d("TrafficUsageAlarmIntentService", "checkDataUsedShouldAlert: totalByte" + j);
        boolean z = j2 >= j && j > 0;
        Log.d("TrafficUsageAlarmIntentService", "shouldAlertTrafficRunningOut 1: " + TrafficUsageAlarmUtils.shouldAlertTrafficRunningOut(this.mContext, this.mCurrentSlotId));
        if (z && TrafficUsageAlarmUtils.shouldAlertTrafficRunningOut(this.mContext, this.mCurrentSlotId)) {
            Message obtainMessage = this.mMainThreadHandler.obtainMessage(2);
            obtainMessage.arg1 = i;
            this.mMainThreadHandler.sendMessage(obtainMessage);
            notifyUsageRunningOutOfData(this.mContext);
        } else if (!z || TrafficUsageAlarmUtils.isTrafficRunningOutAlreadyAlerted(this.mContext, false, this.mCurrentSlotId) || TrafficUsageAlarmUtils.shouldAlertDataWarn(this.mContext, this.mCurrentSlotId)) {
            long dataWarnValue = TrafficUsageAlarmUtils.getDataWarnValue(this.mContext, this.mCurrentSlotId, -1);
            if (-1 == dataWarnValue) {
                LogUtils.d("TrafficUsageAlarmIntentService", "dataUsageLeftNumberAlert is OperaConst.PKG_USAGE_INVALID_VALUE");
                return;
            }
            if (j2 >= dataWarnValue) {
                if (TrafficUsageAlarmUtils.shouldAlertDataWarn(this.mContext, this.mCurrentSlotId)) {
                    notifyDataUsage(this.mContext);
                    Message obtainMessage2 = this.mMainThreadHandler.obtainMessage(0);
                    obtainMessage2.arg1 = i;
                    this.mMainThreadHandler.sendMessage(obtainMessage2);
                } else {
                    LogUtils.d("TrafficUsageAlarmIntentService", "shouldAlert is false");
                }
                Settings.Global.putInt(getContentResolver(), "is_exceeded", 1);
                return;
            }
            LogUtils.d("TrafficUsageAlarmIntentService", "isPkgRunOut is false and hasMoreThanAlertData is false");
            Settings.Global.putInt(getContentResolver(), "is_exceeded", 0);
        }
    }

    private void notifyDataUsage(Context context) {
        context.sendBroadcast(new Intent("com.oneplus.security.network.trafficalarm.ten_percent_alarm"));
    }

    private void notifyUsageRunningOutOfData(Context context) {
        context.sendBroadcast(new Intent("com.oneplus.security.network.trafficalarm.running_out_data"));
    }
}
