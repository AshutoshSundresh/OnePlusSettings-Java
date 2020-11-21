package com.oneplus.security;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.INetworkManagementEventObserver;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.format.Time;
import android.util.Log;
import com.android.server.net.BaseNetworkObserver;
import com.oneplus.security.network.NetworkPolicyEditor;
import com.oneplus.security.network.operator.OperatorDataModelFactory;
import com.oneplus.security.network.operator.OperatorModelInterface;
import com.oneplus.security.network.operator.PkgInfoLocalCache;
import com.oneplus.security.network.simcard.SimcardDataModel;
import com.oneplus.security.network.simcard.SimcardDataModelInterface;
import com.oneplus.security.network.trafficalarm.TrafficUsageAlarmIntentService;
import com.oneplus.security.network.trafficalarm.TrafficUsageAlarmUtils;
import com.oneplus.security.network.trafficinfo.NativeTrafficDataModel;
import com.oneplus.security.network.trafficinfo.TrafficDataModelInterface;
import com.oneplus.security.receiver.NetworkStateUtils;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.OPSNSUtils;
import com.oneplus.security.utils.Utils;
import com.oneplus.settings.SettingsBaseApplication;
import java.lang.ref.WeakReference;

public final class SecureService extends Service {
    private final INetworkManagementEventObserver mAlertObserver = new BaseNetworkObserver() {
        /* class com.oneplus.security.SecureService.AnonymousClass1 */

        public void limitReached(String str, String str2) {
            LogUtils.d("SecureService", "limitReached........limitName:" + str + ", iface:" + str2);
            if (str2.startsWith("rmnet_data")) {
                boolean shouldStartDataWarnMonitorService = TrafficUsageAlarmUtils.shouldStartDataWarnMonitorService(SecureService.this.mContext);
                boolean shouldStartRunningOutMonitorService = TrafficUsageAlarmUtils.shouldStartRunningOutMonitorService(SecureService.this.mContext);
                if (shouldStartDataWarnMonitorService || shouldStartRunningOutMonitorService) {
                    TrafficUsageAlarmIntentService.startService(SecureService.this.mContext);
                }
            }
        }
    };
    private Context mContext;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private NetworkChangeReceive mNetworkChangeReceive;
    private INetworkManagementService mNetworkManagementService;
    private NetworkPolicyEditor mPolicyEditor;
    private NetworkPolicyManager mPolicyManager;
    private SimcardDataModelInterface mSimcardDataModel;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService(Context context) {
        try {
            context.startServiceAsUser(new Intent(context, SecureService.class), new UserHandle(-2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startServiceForDataUsage(Context context, int i) {
        Intent intent = new Intent(context, SecureService.class);
        intent.putExtra("arg_task", "com.oneplus.security.task.INIT_DATAUSAGE_ALERT_SETTINGS");
        intent.putExtra("arg_parameter_slotid", i);
        context.startService(intent);
    }

    static class AsynHandler extends Handler {
        private final WeakReference<SecureService> serviceReference;

        public AsynHandler(Looper looper, SecureService secureService) {
            super(looper);
            this.serviceReference = new WeakReference<>(secureService);
        }

        public void handleMessage(Message message) {
            SecureService secureService = this.serviceReference.get();
            if (secureService != null) {
                int i = message.what;
                if (i == 1) {
                    NetworkStateUtils.onReceiveNetWorkStateChanged(secureService);
                    secureService.initDataUsageAlertSettings();
                } else if (i == 4) {
                    int i2 = message.arg1;
                    if (i2 < 0 || i2 > 1) {
                        secureService.initDataUsageAlertSettings();
                    } else {
                        secureService.initDataUsageAlertSettings(i2);
                    }
                }
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        HandlerThread handlerThread = new HandlerThread("SecureService");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new AsynHandler(this.mHandlerThread.getLooper(), this);
        getPackageManager();
        INetworkManagementService asInterface = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
        this.mNetworkManagementService = asInterface;
        try {
            asInterface.registerObserver(this.mAlertObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.intent.action.ANY_DATA_STATE");
        intentFilter.addAction("android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED");
        this.mNetworkChangeReceive = new NetworkChangeReceive();
        getApplicationContext().registerReceiver(this.mNetworkChangeReceive, intentFilter);
        if (Utils.hasSDK28()) {
            new IntentFilter("android.intent.action.DATE_CHANGED");
        }
        NetworkPolicyManager from = NetworkPolicyManager.from(this.mContext.getApplicationContext());
        this.mPolicyManager = from;
        NetworkPolicyEditor networkPolicyEditor = new NetworkPolicyEditor(from);
        this.mPolicyEditor = networkPolicyEditor;
        networkPolicyEditor.read();
        SimcardDataModel instance = SimcardDataModel.getInstance(this.mContext.getApplicationContext());
        this.mSimcardDataModel = instance;
        instance.registerSimStateListener(null);
        LogUtils.d("SecureService", "SecureService onCreate....");
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        String str;
        super.onStartCommand(intent, i, i2);
        if (intent == null) {
            str = "";
        } else {
            str = intent.getStringExtra("arg_task");
        }
        if ("com.oneplus.security.task.INIT_DATAUSAGE_ALERT_SETTINGS".equals(str)) {
            int intExtra = intent.getIntExtra("arg_parameter_slotid", -1);
            Message obtainMessage = this.mHandler.obtainMessage(4);
            obtainMessage.arg1 = intExtra;
            this.mHandler.removeMessages(4);
            this.mHandler.sendMessageDelayed(obtainMessage, 1000);
            return 1;
        }
        LogUtils.d("SecureService", "msg check notify sdk updated");
        this.mHandler.sendEmptyMessage(4);
        return 1;
    }

    public void onDestroy() {
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quit();
            this.mHandlerThread = null;
        }
        INetworkManagementService iNetworkManagementService = this.mNetworkManagementService;
        if (iNetworkManagementService != null) {
            try {
                iNetworkManagementService.unregisterObserver(this.mAlertObserver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getApplicationContext().unregisterReceiver(this.mNetworkChangeReceive);
        Utils.hasSDK28();
        LogUtils.d("SecureService", "SecureService onDestroy....");
        this.mSimcardDataModel.removeSimStateListener(null);
        super.onDestroy();
    }

    class NetworkChangeReceive extends BroadcastReceiver {
        NetworkChangeReceive() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d("SecureService", "onReceive action:" + action);
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
                SecureService.this.mHandler.removeMessages(1);
                SecureService.this.mHandler.sendEmptyMessageDelayed(1, 1000);
            } else if ("android.intent.action.ANY_DATA_STATE".equals(action)) {
                SecureService.this.sendBroadcast(new Intent("oneplus.intent.action.ANY_DATA_STATE"));
            } else if ("android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED".equals(action)) {
                SecureService.this.sendBroadcast(new Intent("oneplus.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED"));
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void initDataUsageAlertSettings() {
        initDataUsageAlertSettings(0);
        initDataUsageAlertSettings(1);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void initDataUsageAlertSettings(int i) {
        long j;
        long j2;
        long j3;
        if (!this.mSimcardDataModel.isSlotSimInserted(i)) {
            Log.d("SecureService", "initDataUsageAlertSettings: not card ");
        } else if (this.mSimcardDataModel.isSlotSimReady(i)) {
            this.mSimcardDataModel.isSlotOperatorSupportedBySdk(i);
            OperatorModelInterface operatorDataModel = OperatorDataModelFactory.getOperatorDataModel(SettingsBaseApplication.getContext());
            int accountDay = operatorDataModel.getAccountDay(i);
            boolean dataTotalState = TrafficUsageAlarmUtils.getDataTotalState(this.mContext, i);
            if (dataTotalState) {
                j = TrafficUsageAlarmUtils.getSystemDataLimitValue(this.mContext, i, -1);
                Log.d("SecureService", "limitByte one: " + j);
                if (j == -1) {
                    j = operatorDataModel.getPkgTotalInByte(i);
                    Log.d("SecureService", "limitByte two: " + j);
                }
                if (j < 0) {
                    j = 0;
                }
            } else {
                j = -1;
            }
            boolean dataWarnState = TrafficUsageAlarmUtils.getDataWarnState(this.mContext, false, i);
            if (dataWarnState) {
                j2 = TrafficUsageAlarmUtils.getSystemDataWarnValue(this.mContext, i, -1);
                if (j2 == -1) {
                    j2 = TrafficUsageAlarmUtils.getDataWarnValue(this.mContext, i, -1);
                }
                if (j2 < 0) {
                    j2 = 0;
                }
            } else {
                j2 = -1;
            }
            Log.d("SecureService", "initDataUsageAlertSettings: limitState = " + dataTotalState + "  limitByte =  " + j);
            Log.d("SecureService", "initDataUsageAlertSettings: warnState = " + dataWarnState + "   warnByte =  " + j2);
            StringBuilder sb = new StringBuilder();
            sb.append("initDataUsageAlertSettings: isTrafficRunningOutAlready ");
            sb.append(TrafficUsageAlarmUtils.isTrafficRunningOutAlreadyAlerted(this.mContext, false, i));
            Log.d("SecureService", sb.toString());
            NetworkTemplate networkTemplate = NativeTrafficDataModel.getNetworkTemplate(OPSNSUtils.findSubIdBySlotId(i));
            this.mPolicyEditor.setPolicyWarningBytes(networkTemplate, j2);
            if (TrafficUsageAlarmUtils.isTrafficRunningOutAlreadyAlerted(this.mContext, false, i)) {
                this.mPolicyEditor.setPolicyLimitBytes(networkTemplate, -1);
                Log.d("SecureService", "setPolicyLimitBytes: Already exist ");
            } else {
                long pkgUsedMonthlyLocalCache = PkgInfoLocalCache.getPkgUsedMonthlyLocalCache(this.mContext, i);
                TrafficDataModelInterface trafficModelInstance = NativeTrafficDataModel.getTrafficModelInstance();
                if (trafficModelInstance != null) {
                    j3 = trafficModelInstance.getExtraDataUsage(i, pkgUsedMonthlyLocalCache, false);
                } else {
                    LogUtils.e("SecureService", "mNativeTrafficDataModel is null ,not load extra data usage");
                    j3 = 0;
                }
                if (pkgUsedMonthlyLocalCache != -1) {
                    j3 = (pkgUsedMonthlyLocalCache * 1024) + j3;
                }
                Log.d("SecureService", "usedInByte: " + j3);
                TrafficUsageAlarmIntentService.startService(this.mContext);
                this.mPolicyEditor.setPolicyLimitBytes(networkTemplate, j);
                Log.d("SecureService", "initDataUsageAlertSettings: limitByte " + j);
                Log.d("SecureService", "setPolicyLimitBytes: NO Already exist ");
            }
            this.mPolicyEditor.setPolicyCycleDay(networkTemplate, accountDay, new Time().timezone);
        }
    }
}
