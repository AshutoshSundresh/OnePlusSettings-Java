package com.oneplus.security.network.simcard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.settings.C0017R$string;
import com.oneplus.security.SecureService;
import com.oneplus.security.network.trafficalarm.TrafficUsageAlarmIntentService;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.OPSNSUtils;
import java.util.ArrayList;
import java.util.List;

public class SimcardDataModel implements SimcardDataModelInterface {
    private static SimcardDataModel sInstance;
    private Context mContext;
    int[] mMSimState;
    private boolean[] mOperatorSdkSupported;
    private Object mOperatorSdkSupportedSyncLock = new Object();
    private int mPhoneCount;
    String[] mSimOperatorCode = new String[2];
    private IntentFilter mSimStateChangeActionFilter = new IntentFilter();
    private BroadcastReceiver mSimStateChangeReceiver = new BroadcastReceiver() {
        /* class com.oneplus.security.network.simcard.SimcardDataModel.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String stringExtra = intent.getStringExtra("ss");
            if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                boolean z = false;
                for (int i = 0; i < SimcardDataModel.this.mPhoneCount; i++) {
                    int[] iArr = SimcardDataModel.this.mMSimState;
                    int i2 = iArr[i];
                    iArr[i] = TelephonyManager.getDefault().getSimState(i);
                    if (i2 != SimcardDataModel.this.mMSimState[i]) {
                        z = true;
                    }
                    LogUtils.d("SimcardDataModel", "original state " + i2 + " new state " + SimcardDataModel.this.mMSimState[i]);
                    SimcardDataModel simcardDataModel = SimcardDataModel.this;
                    simcardDataModel.parseOperatorCode(i, simcardDataModel.getOperatorCode(i));
                    if (SimcardDataModel.this.mMSimState[i] == 5) {
                        LogUtils.d("SimcardDataModel", "simStatus==5");
                        SecureService.startServiceForDataUsage(SimcardDataModel.this.mContext, i);
                        TrafficUsageAlarmIntentService.startSimStatusService(SimcardDataModel.this.mContext);
                    }
                }
                if (z) {
                    SimcardDataModel.this.notifySimStateChanged(stringExtra);
                }
            }
        }
    };
    private List<SimStateListener> mSimStateListeners;
    private byte[] mSimStateListenersLock = new byte[0];
    private int[] mSubIds;
    private TelephonyManager mTelephonyManager;

    private SimcardDataModel(Context context) {
        int i;
        this.mContext = context.getApplicationContext();
        this.mSimStateListeners = new ArrayList();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mTelephonyManager = telephonyManager;
        int phoneCount = telephonyManager.getPhoneCount();
        this.mPhoneCount = phoneCount;
        this.mSubIds = new int[phoneCount];
        this.mMSimState = new int[phoneCount];
        this.mOperatorSdkSupported = new boolean[phoneCount];
        for (int i2 = 0; i2 < this.mPhoneCount; i2++) {
            this.mMSimState[i2] = TelephonyManager.getDefault().getSimState(i2);
            int[] subId = SubscriptionManager.getSubId(i2);
            if (subId != null && (i = subId[0]) > 0) {
                this.mSubIds[i2] = i;
            }
            parseOperatorCode(i2, getOperatorCode(i2));
        }
        this.mSimStateChangeActionFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
    }

    public static SimcardDataModel getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SimcardDataModel.class) {
                if (sInstance == null) {
                    sInstance = new SimcardDataModel(context);
                }
            }
        }
        return sInstance;
    }

    @Override // com.oneplus.security.network.simcard.SimcardDataModelInterface
    public void registerSimStateListener(SimStateListener simStateListener) {
        synchronized (this.mSimStateListenersLock) {
            if (this.mSimStateListeners.size() == 0) {
                this.mContext.registerReceiver(this.mSimStateChangeReceiver, this.mSimStateChangeActionFilter);
            }
            this.mSimStateListeners.add(simStateListener);
        }
    }

    @Override // com.oneplus.security.network.simcard.SimcardDataModelInterface
    public void removeSimStateListener(SimStateListener simStateListener) {
        synchronized (this.mSimStateListenersLock) {
            if (simStateListener != null) {
                this.mSimStateListeners.remove(simStateListener);
            }
            if (this.mSimStateListeners.size() == 0) {
                this.mContext.unregisterReceiver(this.mSimStateChangeReceiver);
            }
        }
    }

    @Override // com.oneplus.security.network.simcard.SimcardDataModelInterface
    public int getCurrentTrafficRunningSlotId() {
        return OPSNSUtils.findSlotIdBySubId(SubscriptionManager.getDefaultDataSubscriptionId());
    }

    @Override // com.oneplus.security.network.simcard.SimcardDataModelInterface
    public boolean isSlotSimInserted(int i) {
        int simState;
        return i >= 0 && i <= 1 && 1 != (simState = this.mTelephonyManager.getSimState(i)) && simState != 0;
    }

    @Override // com.oneplus.security.network.simcard.SimcardDataModelInterface
    public boolean isSlotSimReady(int i) {
        int[] iArr;
        if (i >= this.mPhoneCount || (iArr = this.mMSimState) == null || i < 0 || i >= iArr.length || iArr[i] != 5) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void notifySimStateChanged(String str) {
        if ("ABSENT".equals(str) || "READY".equals(str)) {
            synchronized (this.mSimStateListenersLock) {
                for (SimStateListener simStateListener : this.mSimStateListeners) {
                    if (simStateListener != null) {
                        simStateListener.onSimStateChanged(str);
                    }
                }
            }
            return;
        }
        LogUtils.e("SimcardDataModel", "simState value " + str + " not supported yet.");
    }

    @Override // com.oneplus.security.network.simcard.SimcardDataModelInterface
    public String getSlotOperatorName(int i) {
        return parseOperatorCode(i, getOperatorCode(i));
    }

    @Override // com.oneplus.security.network.simcard.SimcardDataModelInterface
    public boolean isSlotOperatorSupportedBySdk(int i) {
        boolean z;
        if (i < 0) {
            LogUtils.e("SimcardDataModel", "provide unsupported slot id " + i);
            return false;
        }
        synchronized (this.mOperatorSdkSupportedSyncLock) {
            z = this.mOperatorSdkSupported[i];
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public String parseOperatorCode(int i, String str) {
        LogUtils.d("SimcardDataModel", "slot is " + i + "op code " + str);
        setOperatorSdkSupportState(i, true);
        String string = this.mContext.getResources().getString(C0017R$string.carrier_info_default_summary);
        if (-1 == i) {
            return string;
        }
        String[] strArr = this.mSimOperatorCode;
        String str2 = strArr[i];
        strArr[i] = string;
        if (str == null || str.length() <= 0) {
            setOperatorSdkSupportState(i, false);
        } else {
            String simName = OPSNSUtils.getSimName(this.mContext, i, false);
            if (!TextUtils.isEmpty(simName)) {
                this.mSimOperatorCode[i] = simName;
            } else {
                setOperatorSdkSupportState(i, false);
            }
        }
        if (str2 != null && !str2.equals(this.mSimOperatorCode[i])) {
            synchronized (this.mSimStateListenersLock) {
                for (SimStateListener simStateListener : this.mSimStateListeners) {
                    if (simStateListener != null) {
                        simStateListener.onSimOperatorCodeChanged(i, this.mSimOperatorCode[i]);
                    }
                }
            }
        }
        return this.mSimOperatorCode[i];
    }

    private void setOperatorSdkSupportState(int i, boolean z) {
        synchronized (this.mOperatorSdkSupportedSyncLock) {
            if (i >= 0) {
                if (i < this.mOperatorSdkSupported.length) {
                    this.mOperatorSdkSupported[i] = z;
                    return;
                }
            }
            LogUtils.e("SimcardDataModel", "slot id is invalid " + i);
        }
    }

    public String getOperatorCode(int i) {
        return this.mTelephonyManager.getSimOperator(getSubIdBySlotId(i));
    }

    public int getSubIdBySlotId(int i) {
        return staticGetSubIdBySlotId(i);
    }

    public static int staticGetSubIdBySlotId(int i) {
        int[] subId = SubscriptionManager.getSubId(i);
        if (subId != null) {
            return subId[0];
        }
        return -1;
    }

    @Override // com.oneplus.security.network.simcard.SimcardDataModelInterface
    public void setDataEnabled(boolean z) {
        this.mTelephonyManager.setDataEnabled(z);
    }
}
