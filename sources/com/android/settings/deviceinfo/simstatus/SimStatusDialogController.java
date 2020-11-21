package com.android.settings.deviceinfo.simstatus;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.telephony.CarrierConfigManager;
import android.telephony.CellSignalStrength;
import android.telephony.ICellBroadcastService;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.euicc.EuiccManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ImsReasonInfo;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.deviceinfo.simstatus.FiveGNetWorkStatus;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.utils.ThreadUtils;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SimStatusDialogController implements LifecycleObserver, OnResume, OnPause, FiveGNetWorkStatus.On5gInfomationUpdateListener {
    static final int CELLULAR_NETWORK_STATE = C0010R$id.data_state_value;
    static final int CELL_DATA_NETWORK_TYPE_VALUE_ID = C0010R$id.data_network_type_value;
    static final int CELL_VOICE_NETWORK_TYPE_VALUE_ID = C0010R$id.voice_network_type_value;
    static final int EID_INFO_LABEL_ID = C0010R$id.esim_id_label;
    static final int EID_INFO_VALUE_ID = C0010R$id.esim_id_value;
    static final int ICCID_INFO_LABEL_ID = C0010R$id.icc_id_label;
    static final int ICCID_INFO_VALUE_ID = C0010R$id.icc_id_value;
    static final int IMSI_INFO_LABEL_ID = C0010R$id.imsi_label;
    static final int IMSI_INFO_VALUE_ID = C0010R$id.imsi_value;
    static final int IMS_REGISTRATION_STATE_LABEL_ID = C0010R$id.ims_reg_state_label;
    static final int IMS_REGISTRATION_STATE_VALUE_ID = C0010R$id.ims_reg_state_value;
    static final int MAX_PHONE_COUNT_SINGLE_SIM = 1;
    static final int MEID_INFO_LABEL_ID = C0010R$id.meid_label;
    static final int MEID_INFO_VALUE_ID = C0010R$id.meid_value;
    static final int NETWORK_PROVIDER_VALUE_ID = C0010R$id.operator_name_value;
    static final int OPERATOR_INFO_LABEL_ID = C0010R$id.latest_area_info_label;
    static final int OPERATOR_INFO_VALUE_ID = C0010R$id.latest_area_info_value;
    static final int PHONE_NUMBER_VALUE_ID = C0010R$id.number_value;
    static final int ROAMING_INFO_VALUE_ID = C0010R$id.roaming_state_value;
    static final int SERVICE_STATE_VALUE_ID = C0010R$id.service_state_value;
    static final int SIGNAL_STRENGTH_LABEL_ID = C0010R$id.signal_strength_label;
    static final int SIGNAL_STRENGTH_VALUE_ID = C0010R$id.signal_strength_value;
    private final BroadcastReceiver mAreaInfoReceiver = new BroadcastReceiver() {
        /* class com.android.settings.deviceinfo.simstatus.SimStatusDialogController.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            if ("android.telephony.action.AREA_INFO_UPDATED".equals(intent.getAction()) && intent.getIntExtra("android.telephony.extra.SLOT_INDEX", 0) == SimStatusDialogController.this.mSlotIndex) {
                SimStatusDialogController.this.updateAreaInfoText();
            }
        }
    };
    private final CarrierConfigManager mCarrierConfigManager;
    private CellBroadcastServiceConnection mCellBroadcastServiceConnection;
    private final Context mContext;
    private final SimStatusDialogFragment mDialog;
    private final EuiccManager mEuiccManager;
    private FiveGNetWorkStatus mFiveGNetWorkStatus;
    private ImsMmTelManager.RegistrationCallback mImsRegStateCallback = new ImsMmTelManager.RegistrationCallback() {
        /* class com.android.settings.deviceinfo.simstatus.SimStatusDialogController.AnonymousClass3 */

        public void onRegistered(int i) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(C0017R$string.ims_reg_status_registered));
        }

        public void onRegistering(int i) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(C0017R$string.ims_reg_status_not_registered));
        }

        public void onUnregistered(ImsReasonInfo imsReasonInfo) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(C0017R$string.ims_reg_status_not_registered));
        }

        public void onTechnologyChangeFailed(int i, ImsReasonInfo imsReasonInfo) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(C0017R$string.ims_reg_status_not_registered));
        }
    };
    private boolean mIsRegisteredListener = false;
    private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() {
        /* class com.android.settings.deviceinfo.simstatus.SimStatusDialogController.AnonymousClass1 */

        public void onSubscriptionsChanged() {
            int i = -1;
            int subscriptionId = SimStatusDialogController.this.mSubscriptionInfo != null ? SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId() : -1;
            SimStatusDialogController simStatusDialogController = SimStatusDialogController.this;
            simStatusDialogController.mSubscriptionInfo = simStatusDialogController.getPhoneSubscriptionInfo(simStatusDialogController.mSlotIndex);
            if (SimStatusDialogController.this.mSubscriptionInfo != null) {
                i = SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId();
            }
            if (subscriptionId != i) {
                if (SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
                    SimStatusDialogController.this.unregisterImsRegistrationCallback(subscriptionId);
                }
                if (SubscriptionManager.isValidSubscriptionId(i)) {
                    SimStatusDialogController simStatusDialogController2 = SimStatusDialogController.this;
                    simStatusDialogController2.mTelephonyManager = simStatusDialogController2.mTelephonyManager.createForSubscriptionId(i);
                    SimStatusDialogController.this.registerImsRegistrationCallback(i);
                }
            }
            SimStatusDialogController.this.updateSubscriptionStatus();
        }
    };
    private PhoneStateListener mPhoneStateListener;
    private final Resources mRes;
    private boolean mShowLatestAreaInfo;
    private int mSlotID;
    private final int mSlotIndex;
    private SubscriptionInfo mSubscriptionInfo;
    private final SubscriptionManager mSubscriptionManager;
    private TelephonyDisplayInfo mTelephonyDisplayInfo;
    private TelephonyManager mTelephonyManager;
    private SignalStrength mTempSignalStrength;

    static String getNetworkTypeName(int i) {
        switch (i) {
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA";
            case 5:
                return "CDMA - EvDo rev. 0";
            case 6:
                return "CDMA - EvDo rev. A";
            case 7:
                return "CDMA - 1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "iDEN";
            case 12:
                return "CDMA - EvDo rev. B";
            case 13:
                return "LTE";
            case 14:
                return "CDMA - eHRPD";
            case 15:
                return "HSPA+";
            case 16:
                return "GSM";
            case 17:
                return "TD_SCDMA";
            case 18:
                return "IWLAN";
            case 19:
            default:
                return "UNKNOWN";
            case 20:
                return "NR";
        }
    }

    private boolean isSupport5G() {
        return true;
    }

    /* access modifiers changed from: private */
    public class CellBroadcastServiceConnection implements ServiceConnection {
        private IBinder mService;

        private CellBroadcastServiceConnection() {
        }

        public IBinder getService() {
            return this.mService;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("SimStatusDialogCtrl", "connected to CellBroadcastService");
            this.mService = iBinder;
            SimStatusDialogController.this.updateAreaInfoText();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "mICellBroadcastService has disconnected unexpectedly");
        }

        public void onBindingDied(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "Binding died");
        }

        public void onNullBinding(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "Null binding");
        }
    }

    public SimStatusDialogController(SimStatusDialogFragment simStatusDialogFragment, Lifecycle lifecycle, int i) {
        this.mDialog = simStatusDialogFragment;
        this.mContext = simStatusDialogFragment.getContext();
        this.mSlotIndex = i;
        this.mSubscriptionInfo = getPhoneSubscriptionInfo(i);
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        this.mCarrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        this.mEuiccManager = (EuiccManager) this.mContext.getSystemService(EuiccManager.class);
        this.mSubscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
        this.mRes = this.mContext.getResources();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        Log.d("SimStatusDialogCtrl", "slotId = " + i);
        this.mSlotID = i;
    }

    public void initialize() {
        requestForUpdateEid();
        if (this.mSubscriptionInfo != null) {
            this.mPhoneStateListener = getPhoneStateListener();
            updateLatestAreaInfo();
            updateSubscriptionStatus();
            updateMeid();
            updateImsi();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSubscriptionStatus() {
        updateNetworkProvider();
        if (isSupport5G() && this.mFiveGNetWorkStatus == null) {
            FiveGNetWorkStatus fiveGNetWorkStatus = new FiveGNetWorkStatus(this.mContext);
            this.mFiveGNetWorkStatus = fiveGNetWorkStatus;
            fiveGNetWorkStatus.registerListener(this.mSlotID, this);
        }
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo != null) {
            SignalStrength signalStrength = this.mTelephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId()).getSignalStrength();
            updatePhoneNumber();
            updateServiceState(serviceState);
            updateSignalStrength(signalStrength);
            updateNetworkType();
            updateRoamingStatus(serviceState);
            updateIccidNumber();
            updateImsRegistrationState();
        }
    }

    public void deinitialize() {
        if (this.mShowLatestAreaInfo) {
            CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
            if (!(cellBroadcastServiceConnection == null || cellBroadcastServiceConnection.getService() == null)) {
                this.mContext.unbindService(this.mCellBroadcastServiceConnection);
            }
            this.mCellBroadcastServiceConnection = null;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        FiveGNetWorkStatus fiveGNetWorkStatus;
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo != null) {
            TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
            this.mTelephonyManager = createForSubscriptionId;
            createForSubscriptionId.listen(this.mPhoneStateListener, 1048897);
            this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mContext.getMainExecutor(), this.mOnSubscriptionsChangedListener);
            registerImsRegistrationCallback(this.mSubscriptionInfo.getSubscriptionId());
            if (this.mShowLatestAreaInfo) {
                updateAreaInfoText();
                this.mContext.registerReceiver(this.mAreaInfoReceiver, new IntentFilter("android.telephony.action.AREA_INFO_UPDATED"));
            }
            this.mIsRegisteredListener = true;
            if (isSupport5G() && (fiveGNetWorkStatus = this.mFiveGNetWorkStatus) != null) {
                fiveGNetWorkStatus.registerListener(this.mSlotID, this);
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        FiveGNetWorkStatus fiveGNetWorkStatus;
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo != null) {
            unregisterImsRegistrationCallback(subscriptionInfo.getSubscriptionId());
            this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangedListener);
            this.mTelephonyManager.createForSubscriptionId(this.mSubscriptionInfo.getSubscriptionId()).listen(this.mPhoneStateListener, 0);
            if (this.mShowLatestAreaInfo) {
                this.mContext.unregisterReceiver(this.mAreaInfoReceiver);
            }
            if (isSupport5G() && (fiveGNetWorkStatus = this.mFiveGNetWorkStatus) != null) {
                fiveGNetWorkStatus.unregisterListener(this.mSlotID);
            }
        } else if (this.mIsRegisteredListener) {
            this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangedListener);
            this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
            if (this.mShowLatestAreaInfo) {
                this.mContext.unregisterReceiver(this.mAreaInfoReceiver);
            }
            this.mIsRegisteredListener = false;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateNetworkProvider() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        this.mDialog.setText(NETWORK_PROVIDER_VALUE_ID, subscriptionInfo != null ? subscriptionInfo.getCarrierName() : null);
    }

    private void updatePhoneNumber() {
        this.mDialog.setText(PHONE_NUMBER_VALUE_ID, DeviceInfoUtils.getBidiFormattedPhoneNumber(this.mContext, this.mSubscriptionInfo));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateDataState(int i) {
        String str;
        if (i == 0) {
            str = this.mRes.getString(C0017R$string.radioInfo_data_disconnected);
        } else if (i == 1) {
            str = this.mRes.getString(C0017R$string.radioInfo_data_connecting);
        } else if (i != 2) {
            str = i != 3 ? this.mRes.getString(C0017R$string.radioInfo_unknown) : this.mRes.getString(C0017R$string.radioInfo_data_suspended);
        } else {
            str = this.mRes.getString(C0017R$string.radioInfo_data_connected);
        }
        this.mDialog.setText(CELLULAR_NETWORK_STATE, str);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateAreaInfoText() {
        CellBroadcastServiceConnection cellBroadcastServiceConnection;
        ICellBroadcastService asInterface;
        if (this.mShowLatestAreaInfo && (cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection) != null && (asInterface = ICellBroadcastService.Stub.asInterface(cellBroadcastServiceConnection.getService())) != null) {
            try {
                this.mDialog.setText(OPERATOR_INFO_VALUE_ID, asInterface.getCellBroadcastAreaInfo(this.mSlotIndex));
            } catch (RemoteException e) {
                Log.d("SimStatusDialogCtrl", "Can't get area info. e=" + e);
            }
        }
    }

    private void bindCellBroadcastService() {
        this.mCellBroadcastServiceConnection = new CellBroadcastServiceConnection();
        Intent intent = new Intent("android.telephony.CellBroadcastService");
        String cellBroadcastServicePackage = getCellBroadcastServicePackage();
        if (!TextUtils.isEmpty(cellBroadcastServicePackage)) {
            intent.setPackage(cellBroadcastServicePackage);
            CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
            if (cellBroadcastServiceConnection == null || cellBroadcastServiceConnection.getService() != null) {
                Log.d("SimStatusDialogCtrl", "skipping bindService because connection already exists");
            } else if (!this.mContext.bindService(intent, this.mCellBroadcastServiceConnection, 1)) {
                Log.e("SimStatusDialogCtrl", "Unable to bind to service");
            }
        }
    }

    private String getCellBroadcastServicePackage() {
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(new Intent("android.telephony.CellBroadcastService"), 1048576);
        if (queryIntentServices.size() != 1) {
            Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: found " + queryIntentServices.size() + " CBS packages");
        }
        for (ResolveInfo resolveInfo : queryIntentServices) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo != null) {
                String str = serviceInfo.packageName;
                if (TextUtils.isEmpty(str)) {
                    Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: found a CBS package but packageName is null/empty");
                } else if (packageManager.checkPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", str) == 0) {
                    Log.d("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: " + str);
                    return str;
                } else {
                    Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: " + str + " does not have READ_PRIVILEGED_PHONE_STATE permission");
                }
            }
        }
        Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: package name not found");
        return null;
    }

    private void updateLatestAreaInfo() {
        boolean z = Resources.getSystem().getBoolean(17891525) && this.mTelephonyManager.getPhoneType() != 2;
        this.mShowLatestAreaInfo = z;
        if (z) {
            bindCellBroadcastService();
            return;
        }
        this.mDialog.removeSettingFromScreen(OPERATOR_INFO_LABEL_ID);
        this.mDialog.removeSettingFromScreen(OPERATOR_INFO_VALUE_ID);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateServiceState(ServiceState serviceState) {
        String str;
        int combinedServiceState = Utils.getCombinedServiceState(serviceState);
        if (!Utils.isInService(serviceState)) {
            resetSignalStrength();
        }
        if (combinedServiceState == 0) {
            str = this.mRes.getString(C0017R$string.radioInfo_service_in);
        } else if (combinedServiceState == 1 || combinedServiceState == 2) {
            str = this.mRes.getString(C0017R$string.radioInfo_service_out);
        } else if (combinedServiceState != 3) {
            str = this.mRes.getString(C0017R$string.radioInfo_unknown);
        } else {
            str = this.mRes.getString(C0017R$string.radioInfo_service_off);
        }
        this.mDialog.setText(SERVICE_STATE_VALUE_ID, str);
    }

    private int getDataNetworkType() {
        ServiceState currentServiceState = getCurrentServiceState();
        if (currentServiceState != null) {
            return currentServiceState.getDataNetworkType();
        }
        return 0;
    }

    private boolean isDataRegisteredOnLteNr() {
        int dataNetworkType = getDataNetworkType();
        return dataNetworkType == 13 || dataNetworkType == 19 || dataNetworkType == 20;
    }

    private boolean is5GConnected() {
        FiveGNetWorkStatus fiveGNetWorkStatus = this.mFiveGNetWorkStatus;
        if (fiveGNetWorkStatus == null) {
            Log.d("SimStatusDialogCtrl", "mFiveGState is null");
            return false;
        } else if (fiveGNetWorkStatus.getCurrentServiceState(this.mSlotID).isConnectedOnSaMode() || (this.mFiveGNetWorkStatus.getCurrentServiceState(this.mSlotID).isConnectedOnNsaMode() && isDataRegisteredOnLteNr())) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSignalStrength(SignalStrength signalStrength) {
        FiveGNetWorkStatus fiveGNetWorkStatus;
        PersistableBundle configForSubId;
        if (signalStrength != null) {
            this.mTempSignalStrength = signalStrength;
            SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
            if (subscriptionInfo != null) {
                if (!((subscriptionInfo == null || (configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionInfo.getSubscriptionId())) == null) ? true : configForSubId.getBoolean("show_signal_strength_in_sim_status_bool"))) {
                    this.mDialog.removeSettingFromScreen(SIGNAL_STRENGTH_LABEL_ID);
                    this.mDialog.removeSettingFromScreen(SIGNAL_STRENGTH_VALUE_ID);
                    return;
                }
                ServiceState serviceState = this.mTelephonyManager.getServiceState();
                if (serviceState != null && Utils.isInService(serviceState)) {
                    int dbm = getDbm(signalStrength);
                    int asuLevel = getAsuLevel(signalStrength);
                    if (dbm == -1) {
                        dbm = 0;
                    }
                    if (asuLevel == -1) {
                        asuLevel = 0;
                    }
                    Log.d("SimStatusDialogCtrl", "SimStatusDialogController--isSupport:" + isSupport5G() + " mFiveGNetWorkStatus:" + this.mFiveGNetWorkStatus);
                    if (isSupport5G() && (fiveGNetWorkStatus = this.mFiveGNetWorkStatus) != null) {
                        Log.d("SimStatusDialogCtrl", "SimStatusDialogController--updateSignalStrength-configTypeTemp:" + fiveGNetWorkStatus.getCurrentServiceState(this.mSlotID).getNrConfigType());
                        int defaultDataPhoneId = this.mSubscriptionManager.getDefaultDataPhoneId();
                        int i = this.mSlotID;
                        if (defaultDataPhoneId != i || !this.mFiveGNetWorkStatus.getCurrentServiceState(i).getShowFiveGService() || !is5GConnected()) {
                            this.mDialog.setText(SIGNAL_STRENGTH_VALUE_ID, this.mRes.getString(C0017R$string.sim_signal_strength, Integer.valueOf(dbm), Integer.valueOf(asuLevel)));
                            return;
                        }
                        int rsrp = this.mFiveGNetWorkStatus.getCurrentServiceState(this.mSlotID).getRsrp();
                        if (rsrp < -140 || rsrp > -44) {
                            rsrp = 0;
                        }
                        int snr = this.mFiveGNetWorkStatus.getCurrentServiceState(this.mSlotID).getSnr();
                        if (snr < -230 || snr > 400) {
                            snr = 0;
                        }
                        int nrConfigType = this.mFiveGNetWorkStatus.getCurrentServiceState(this.mSlotID).getNrConfigType();
                        Log.d("SimStatusDialogCtrl", "SimStatusDialogController--updateSignalStrength-configType:" + nrConfigType);
                        if (nrConfigType == 1) {
                            this.mDialog.setText(SIGNAL_STRENGTH_VALUE_ID, "NR " + this.mRes.getString(C0017R$string.sim_signal_strength, Integer.valueOf(rsrp), Integer.valueOf(snr)));
                            return;
                        }
                        this.mDialog.setText(SIGNAL_STRENGTH_VALUE_ID, "LTE " + this.mRes.getString(C0017R$string.sim_signal_strength, Integer.valueOf(dbm), Integer.valueOf(asuLevel)) + "\nNR " + this.mRes.getString(C0017R$string.sim_signal_strength, Integer.valueOf(rsrp), Integer.valueOf(snr)));
                    }
                }
            }
        }
    }

    private void resetSignalStrength() {
        this.mDialog.setText(SIGNAL_STRENGTH_VALUE_ID, "0");
    }

    @Override // com.android.settings.deviceinfo.simstatus.FiveGNetWorkStatus.On5gInfomationUpdateListener
    public void On5gInfomationUpdate(int i) {
        Log.d("SimStatusDialogCtrl", "SimStatusDialogController--On5gInfomationUpdate-nRConfigType:" + i);
        updateNetworkType();
        updateSignalStrength(this.mTempSignalStrength);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateNetworkType() {
        int i;
        FiveGNetWorkStatus fiveGNetWorkStatus;
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        boolean z = false;
        if (subscriptionInfo == null) {
            String networkTypeName = getNetworkTypeName(0);
            this.mDialog.setText(CELL_VOICE_NETWORK_TYPE_VALUE_ID, networkTypeName);
            this.mDialog.setText(CELL_DATA_NETWORK_TYPE_VALUE_ID, networkTypeName);
            return;
        }
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        int dataNetworkType = this.mTelephonyManager.getDataNetworkType();
        int voiceNetworkType = this.mTelephonyManager.getVoiceNetworkType();
        TelephonyDisplayInfo telephonyDisplayInfo = this.mTelephonyDisplayInfo;
        if (telephonyDisplayInfo == null) {
            i = 0;
        } else {
            i = telephonyDisplayInfo.getOverrideNetworkType();
        }
        String str = null;
        String networkTypeName2 = dataNetworkType != 0 ? getNetworkTypeName(dataNetworkType) : null;
        if (voiceNetworkType != 0) {
            str = getNetworkTypeName(voiceNetworkType);
        }
        if (i == 4 || i == 3) {
            networkTypeName2 = "NR NSA";
        }
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionId);
        if (configForSubId != null) {
            z = configForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
        }
        if (z && !OPUtils.isSupportUstMode()) {
            if ("LTE".equals(networkTypeName2)) {
                networkTypeName2 = "4G";
            }
            if ("LTE".equals(str)) {
                str = "4G";
            }
        }
        Log.d("SimStatusDialogCtrl", "SimStatusDialogController--isSupport:" + isSupport5G() + " mFiveGNetWorkStatus:" + this.mFiveGNetWorkStatus);
        if (isSupport5G() && (fiveGNetWorkStatus = this.mFiveGNetWorkStatus) != null) {
            Log.d("SimStatusDialogCtrl", "SimStatusDialogController--updateNetworkType-configTypeTemp:" + fiveGNetWorkStatus.getCurrentServiceState(this.mSlotID).getNrConfigType());
            int defaultDataPhoneId = this.mSubscriptionManager.getDefaultDataPhoneId();
            Log.d("SimStatusDialogCtrl", "show 5G service : " + this.mFiveGNetWorkStatus.getCurrentServiceState(this.mSlotID).getShowFiveGService() + " is5GConnected:" + is5GConnected() + " currentDataPhoneId:" + defaultDataPhoneId + " mSlotID:" + this.mSlotID);
            int i2 = this.mSlotID;
            if (defaultDataPhoneId == i2 && this.mFiveGNetWorkStatus.getCurrentServiceState(i2).getShowFiveGService() && is5GConnected()) {
                networkTypeName2 = this.mFiveGNetWorkStatus.getCurrentServiceState(this.mSlotID).getNrConfigType() == 1 ? "NR" : "LTE & NR";
            }
        }
        Log.d("SimStatusDialogCtrl", "SimStatusDialogController--actualDataNetworkType:" + dataNetworkType);
        Log.d("SimStatusDialogCtrl", "SimStatusDialogController--actualVoiceNetworkType:" + voiceNetworkType);
        Log.d("SimStatusDialogCtrl", "SimStatusDialogController--voiceNetworkTypeName:" + str);
        Log.d("SimStatusDialogCtrl", "SimStatusDialogController--dataNetworkTypeName:" + networkTypeName2);
        this.mDialog.setText(CELL_VOICE_NETWORK_TYPE_VALUE_ID, str);
        this.mDialog.setText(CELL_DATA_NETWORK_TYPE_VALUE_ID, networkTypeName2);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateRoamingStatus(ServiceState serviceState) {
        if (serviceState.getRoaming()) {
            this.mDialog.setText(ROAMING_INFO_VALUE_ID, this.mRes.getString(C0017R$string.radioInfo_roaming_in));
        } else {
            this.mDialog.setText(ROAMING_INFO_VALUE_ID, this.mRes.getString(C0017R$string.radioInfo_roaming_not));
        }
    }

    private void updateIccidNumber() {
        PersistableBundle configForSubId;
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        boolean z = (subscriptionInfo == null || (configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionInfo.getSubscriptionId())) == null) ? false : configForSubId.getBoolean("show_iccid_in_sim_status_bool");
        if (OPUtils.isSupportUstMode() || OPUtils.isSupportUss() || ProductUtils.isUsvMode()) {
            z = true;
        }
        if (!z) {
            this.mDialog.removeSettingFromScreen(ICCID_INFO_LABEL_ID);
            this.mDialog.removeSettingFromScreen(ICCID_INFO_VALUE_ID);
            return;
        }
        this.mDialog.setText(ICCID_INFO_VALUE_ID, this.mTelephonyManager.getSimSerialNumber());
    }

    /* access modifiers changed from: package-private */
    public void requestForUpdateEid() {
        ThreadUtils.postOnBackgroundThread(new Runnable() {
            /* class com.android.settings.deviceinfo.simstatus.$$Lambda$SimStatusDialogController$BPmoDDOgteCLAB8fs2bKsI4oj6Q */

            public final void run() {
                SimStatusDialogController.this.lambda$requestForUpdateEid$1$SimStatusDialogController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$requestForUpdateEid$1 */
    public /* synthetic */ void lambda$requestForUpdateEid$1$SimStatusDialogController() {
        ThreadUtils.postOnMainThread(new Runnable(getEid(this.mSlotIndex)) {
            /* class com.android.settings.deviceinfo.simstatus.$$Lambda$SimStatusDialogController$IuqQyBSvgf5T93pp2XNH5DLlB0 */
            public final /* synthetic */ AtomicReference f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SimStatusDialogController.this.lambda$requestForUpdateEid$0$SimStatusDialogController(this.f$1);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public AtomicReference<String> getEid(int i) {
        String str;
        boolean z = true;
        if (this.mTelephonyManager.getActiveModemCount() > 1) {
            int intValue = ((Integer) this.mTelephonyManager.getLogicalToPhysicalSlotMapping().getOrDefault(Integer.valueOf(i), -1)).intValue();
            if (intValue != -1) {
                Iterator<UiccCardInfo> it = this.mTelephonyManager.getUiccCardsInfo().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    UiccCardInfo next = it.next();
                    if (next.getSlotIndex() == intValue) {
                        if (next.isEuicc()) {
                            str = next.getEid();
                            if (TextUtils.isEmpty(str)) {
                                str = this.mEuiccManager.createForCardId(next.getCardId()).getEid();
                            }
                        }
                    }
                }
            }
        } else if (this.mEuiccManager.isEnabled()) {
            str = this.mEuiccManager.getEid();
            if (!z || str != null) {
                return new AtomicReference<>(str);
            }
            return null;
        }
        str = null;
        z = false;
        if (!z) {
        }
        return new AtomicReference<>(str);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: updateEid */
    public void lambda$requestForUpdateEid$0(AtomicReference<String> atomicReference) {
        if (atomicReference == null) {
            this.mDialog.removeSettingFromScreen(EID_INFO_LABEL_ID);
            this.mDialog.removeSettingFromScreen(EID_INFO_VALUE_ID);
        } else if (atomicReference.get() != null) {
            this.mDialog.setText(EID_INFO_VALUE_ID, atomicReference.get());
        }
    }

    private boolean isImsRegistrationStateShowUp() {
        PersistableBundle configForSubId;
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null || (configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionInfo.getSubscriptionId())) == null) {
            return false;
        }
        return configForSubId.getBoolean("show_ims_registration_status_bool");
    }

    private void updateImsRegistrationState() {
        if (!isImsRegistrationStateShowUp()) {
            this.mDialog.removeSettingFromScreen(IMS_REGISTRATION_STATE_LABEL_ID);
            this.mDialog.removeSettingFromScreen(IMS_REGISTRATION_STATE_VALUE_ID);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void registerImsRegistrationCallback(int i) {
        if (isImsRegistrationStateShowUp()) {
            try {
                ImsMmTelManager.createForSubscriptionId(i).registerImsRegistrationCallback(this.mDialog.getContext().getMainExecutor(), this.mImsRegStateCallback);
            } catch (ImsException e) {
                Log.w("SimStatusDialogCtrl", "fail to register IMS status for subId=" + i, e);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void unregisterImsRegistrationCallback(int i) {
        if (isImsRegistrationStateShowUp()) {
            ImsMmTelManager.createForSubscriptionId(i).unregisterImsRegistrationCallback(this.mImsRegStateCallback);
        }
    }

    private void updateMeid() {
        if (OPUtils.isSupportUss() || OPUtils.isSupportUstUnify()) {
            TelephonyManager telephonyManager = this.mTelephonyManager;
            if (telephonyManager != null) {
                this.mDialog.setText(MEID_INFO_VALUE_ID, telephonyManager.getMeid());
                return;
            }
            return;
        }
        this.mDialog.removeSettingFromScreen(MEID_INFO_LABEL_ID);
        this.mDialog.removeSettingFromScreen(MEID_INFO_VALUE_ID);
    }

    private void updateImsi() {
        if (OPUtils.isSupportUss() || OPUtils.isSupportUstUnify()) {
            TelephonyManager telephonyManager = this.mTelephonyManager;
            if (telephonyManager != null) {
                this.mDialog.setText(IMSI_INFO_VALUE_ID, telephonyManager.getSubscriberId());
                return;
            }
            return;
        }
        this.mDialog.removeSettingFromScreen(IMSI_INFO_LABEL_ID);
        this.mDialog.removeSettingFromScreen(IMSI_INFO_VALUE_ID);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private SubscriptionInfo getPhoneSubscriptionInfo(int i) {
        return SubscriptionManager.from(this.mContext).getActiveSubscriptionInfoForSimSlotIndex(i);
    }

    /* access modifiers changed from: package-private */
    public ServiceState getCurrentServiceState() {
        return this.mTelephonyManager.getServiceStateForSubscriber(this.mSubscriptionInfo.getSubscriptionId());
    }

    private int getDbm(SignalStrength signalStrength) {
        List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
        if (cellSignalStrengths == null) {
            return -1;
        }
        for (CellSignalStrength cellSignalStrength : cellSignalStrengths) {
            if (cellSignalStrength.getDbm() != -1) {
                return cellSignalStrength.getDbm();
            }
        }
        return -1;
    }

    private int getAsuLevel(SignalStrength signalStrength) {
        List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
        if (cellSignalStrengths == null) {
            return -1;
        }
        for (CellSignalStrength cellSignalStrength : cellSignalStrengths) {
            if (cellSignalStrength.getAsuLevel() != -1) {
                return cellSignalStrength.getAsuLevel();
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public PhoneStateListener getPhoneStateListener() {
        return new PhoneStateListener() {
            /* class com.android.settings.deviceinfo.simstatus.SimStatusDialogController.AnonymousClass4 */

            public void onDataConnectionStateChanged(int i) {
                SimStatusDialogController.this.updateDataState(i);
                SimStatusDialogController.this.updateNetworkType();
            }

            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                SimStatusDialogController.this.updateSignalStrength(signalStrength);
            }

            public void onServiceStateChanged(ServiceState serviceState) {
                SimStatusDialogController.this.updateNetworkProvider();
                SimStatusDialogController.this.updateServiceState(serviceState);
                SimStatusDialogController.this.updateRoamingStatus(serviceState);
                SimStatusDialogController.this.updateNetworkType();
            }

            public void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) {
                SimStatusDialogController.this.mTelephonyDisplayInfo = telephonyDisplayInfo;
                SimStatusDialogController.this.updateNetworkType();
            }
        };
    }
}
