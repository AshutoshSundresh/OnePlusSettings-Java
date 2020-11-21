package com.android.settings.wifi.tether;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ServiceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import com.android.settings.C0017R$string;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.widget.SwitchBar;
import com.android.settings.wifi.tether.utils.TetherUtils;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.oneplus.settings.utils.OPUtils;
import java.lang.reflect.Method;
import org.codeaurora.internal.IExtTelephony;

public class WifiTetherSwitchBarController implements LifecycleObserver, OnStart, OnStop, DataSaverBackend.Listener, View.OnClickListener, TetherUtils.OnDialogConfirmCallback {
    private static final Uri SOFTSIM_URL = Uri.parse("content://com.redteamobile.provider");
    private static final IntentFilter WIFI_INTENT_FILTER;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    final DataSaverBackend mDataSaverBackend;
    private Handler mHandler = new Handler();
    final ConnectivityManager.OnStartTetheringCallback mOnStartTetheringCallback = new ConnectivityManager.OnStartTetheringCallback() {
        /* class com.android.settings.wifi.tether.WifiTetherSwitchBarController.AnonymousClass1 */

        public void onTetheringFailed() {
            WifiTetherSwitchBarController.super.onTetheringFailed();
            WifiTetherSwitchBarController.this.mSwitchBar.setChecked(false);
            WifiTetherSwitchBarController.this.updateWifiSwitch();
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.wifi.tether.WifiTetherSwitchBarController.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
                WifiTetherSwitchBarController.this.handleWifiApStateChanged(intent.getIntExtra("wifi_state", 14));
            } else if ("android.intent.action.SIM_STATE_CHANGED".equals(action)) {
                Log.i("WifiTetherSwitchBarController", "action = " + action);
                WifiTetherSwitchBarController wifiTetherSwitchBarController = WifiTetherSwitchBarController.this;
                wifiTetherSwitchBarController.mSoftSimPilotModeEnabled = wifiTetherSwitchBarController.isPilotModeEnabled(context);
                WifiTetherSwitchBarController.this.updateWifiSwitch();
                if (OPUtils.isSupportUss()) {
                    WifiTetherSwitchBarController.this.updateSimStatus(TetherUtils.isSimStatusChange(context));
                }
            } else if ("android.intent.action.setupDataError_tether".equals(action)) {
                Log.d("WifiTetherSwitchBarController", "onReceive tether error braodcast");
                if (OPUtils.isSupportUss() && intent.getBooleanExtra("data_call_error", false) && intent.getIntExtra("data_call_code", 0) == 67) {
                    WifiTetherSwitchBarController.this.tetherError(2);
                }
            }
        }
    };
    private boolean mSoftSimPilotModeEnabled;
    private final Switch mSwitch;
    private final SwitchBar mSwitchBar;
    private final WifiManager mWifiManager;

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onBlacklistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onWhitelistStatusChanged(int i, boolean z) {
    }

    static {
        IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
        WIFI_INTENT_FILTER = intentFilter;
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        if (OPUtils.isSupportUss()) {
            WIFI_INTENT_FILTER.addAction("android.intent.action.setupDataError_tether");
        }
    }

    WifiTetherSwitchBarController(Context context, SwitchBar switchBar) {
        this.mContext = context;
        this.mSwitchBar = switchBar;
        this.mSwitch = switchBar.getSwitch();
        this.mDataSaverBackend = new DataSaverBackend(context);
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        this.mWifiManager = wifiManager;
        this.mSwitchBar.setChecked(wifiManager.getWifiApState() == 13);
        updateWifiSwitch();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mDataSaverBackend.addListener(this);
        this.mSwitch.setOnClickListener(this);
        this.mContext.registerReceiver(this.mReceiver, WIFI_INTENT_FILTER);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mDataSaverBackend.remListener(this);
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    public void onClick(View view) {
        if (((Switch) view).isChecked()) {
            Log.d("WifiTetherSwitchBarController", "onClick: start tether");
            startTether();
            return;
        }
        SwitchBar switchBar = this.mSwitchBar;
        if (switchBar != null) {
            switchBar.setEnabled(false);
        }
        this.mHandler.postDelayed(new Runnable() {
            /* class com.android.settings.wifi.tether.$$Lambda$WifiTetherSwitchBarController$1Q77u4UUtMOQY8TZ1sxe4HO3zzE */

            public final void run() {
                WifiTetherSwitchBarController.this.lambda$onClick$0$WifiTetherSwitchBarController();
            }
        }, 300);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: stopTether */
    public void lambda$onClick$0() {
        this.mConnectivityManager.stopTethering(0);
        if (TetherUtils.getUstWifiTetheringStatus(this.mContext)) {
            TetherUtils.setTetherState(this.mContext, false);
            TetherUtils.setUstWifiTetheringStatus(this.mContext, 0);
        }
    }

    /* access modifiers changed from: package-private */
    public void startTether() {
        this.mSwitchBar.setEnabled(false);
        if (OPUtils.isSupportUstMode() && TetherUtils.isWifiEnable(this.mContext)) {
            TetherUtils.startUstTethering(this.mContext, this);
        } else if (OPUtils.isSupportUss()) {
            startUssTethering();
        } else {
            openHotspot();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleWifiApStateChanged(int i) {
        switch (i) {
            case 10:
                if (this.mSwitch.isChecked()) {
                    this.mSwitch.setChecked(false);
                }
                this.mSwitchBar.setEnabled(false);
                return;
            case 11:
                this.mSwitch.setChecked(false);
                updateWifiSwitch();
                return;
            case 12:
                this.mSwitchBar.setEnabled(false);
                return;
            case 13:
                if (!this.mSwitch.isChecked()) {
                    this.mSwitch.setChecked(true);
                }
                updateWifiSwitch();
                return;
            default:
                this.mSwitch.setChecked(false);
                updateWifiSwitch();
                return;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateWifiSwitch() {
        this.mSwitchBar.setEnabled(!this.mDataSaverBackend.isDataSaverEnabled());
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        updateWifiSwitch();
    }

    private boolean getPilotModeFromSim(Context context, int i) {
        try {
            Cursor query = context.getContentResolver().query(SOFTSIM_URL, new String[]{"slot", "iccid", "permit_package", "forbid_package", "pilot"}, new StringBuilder("slot=\"" + i + "\"").toString(), null, "slot");
            if (query == null) {
                return false;
            }
            query.moveToFirst();
            while (!query.isAfterLast()) {
                String string = query.getString(4);
                boolean equals = "1".equals(string);
                Log.d("WifiTetherSwitchBarController", "getPilotModeFromSim: isPilotMode = " + equals + " sPilot: " + string);
                if (equals) {
                    query.close();
                    return true;
                }
                query.moveToNext();
            }
            query.close();
            return false;
        } catch (SQLiteException e) {
            Log.e("WifiTetherSwitchBarController", "getPilotModeFromSim SQLiteException ", e);
            return false;
        }
    }

    private boolean isSoftSim(int i) {
        try {
            IExtTelephony asInterface = IExtTelephony.Stub.asInterface(ServiceManager.getService("extphone"));
            if (asInterface != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("phone", i);
                Log.d("WifiTetherSwitchBarController", "isSoftSIM slot = " + i);
                Method declaredMethod = asInterface.getClass().getDeclaredMethod("generalGetter", String.class, Bundle.class);
                declaredMethod.setAccessible(true);
                if (((Bundle) declaredMethod.invoke(asInterface, "isSoftSIM", bundle)).getBoolean("isSoftSIM", false)) {
                    Log.d("WifiTetherSwitchBarController", "slot " + i + " is softsim");
                    return true;
                }
                Log.d("WifiTetherSwitchBarController", "slot " + i + " is NOT softsim");
            }
        } catch (Exception e) {
            Log.e("WifiTetherSwitchBarController", "exception : " + e);
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isPilotModeEnabled(Context context) {
        int simCount = TelephonyManager.getDefault().getSimCount();
        for (int i = 0; i < simCount; i++) {
            boolean isSoftSim = isSoftSim(i);
            boolean pilotModeFromSim = getPilotModeFromSim(context, i);
            Log.i("WifiTetherSwitchBarController", "hasVirtualSim:" + isSoftSim + " hasPilot:" + pilotModeFromSim);
            if (isSoftSim && pilotModeFromSim) {
                Log.i("WifiTetherSwitchBarController", "Soft sim is in pilot mode");
                return true;
            }
        }
        Log.i("WifiTetherSwitchBarController", "No SIM is in pilot mode");
        return false;
    }

    private void startUssTethering() {
        if (TetherUtils.isNoSimCard(this.mContext)) {
            tetherError(1);
        } else if (TetherUtils.isHaveProfile(this.mContext)) {
            openHotspot();
        } else {
            tetherError(2);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void tetherError(int i) {
        if (i == 1) {
            Context context = this.mContext;
            TetherUtils.showTertheringErrorDialog(context, context.getString(C0017R$string.tether_no_sim_title), this.mContext.getString(C0017R$string.tether_no_sim_message));
            SwitchBar switchBar = this.mSwitchBar;
            if (switchBar != null) {
                switchBar.setChecked(false);
                this.mSwitchBar.setEnabled(false);
            }
        } else if (i == 2) {
            String string = this.mContext.getString(C0017R$string.wifi_hotspot_checkbox_text);
            Context context2 = this.mContext;
            TetherUtils.showTertheringErrorDialog(context2, context2.getString(C0017R$string.tether_error_title, string), this.mContext.getString(C0017R$string.tether_error_message, string));
            openTetheringFail();
        }
    }

    private void openTetheringFail() {
        SwitchBar switchBar = this.mSwitchBar;
        if (switchBar != null) {
            switchBar.setChecked(false);
            this.mSwitchBar.setEnabled(true);
        }
        stopUssTethering();
    }

    private void stopUssTethering() {
        ConnectivityManager connectivityManager = this.mConnectivityManager;
        if (connectivityManager != null) {
            connectivityManager.stopTethering(0);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSimStatus(boolean z) {
        SwitchBar switchBar;
        if (z && (switchBar = this.mSwitchBar) != null) {
            switchBar.setEnabled(true);
        }
    }

    private void openHotspot() {
        if (this.mConnectivityManager != null) {
            Log.d("WifiTetherSwitchBarController", "openHotspot: start tethering");
            this.mConnectivityManager.startTethering(0, true, this.mOnStartTetheringCallback, new Handler(Looper.getMainLooper()));
        }
    }

    @Override // com.android.settings.wifi.tether.utils.TetherUtils.OnDialogConfirmCallback
    public void onConfirm() {
        openHotspot();
    }
}
