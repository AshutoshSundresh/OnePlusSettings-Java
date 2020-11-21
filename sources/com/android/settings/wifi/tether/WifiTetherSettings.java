package com.android.settings.wifi.tether;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settings.wifi.WifiUtils;
import com.android.settings.wifi.tether.TetherDataObserver;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settings.wifi.tether.utils.TetherUtils;
import com.android.settingslib.TetherUtil;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.wifi.tether.OPWiFi6StandardHotSpotPreferenceController;
import com.oneplus.settings.wifi.tether.OPWifiTetherApBandPreferenceController;
import com.oneplus.settings.wifi.tether.OPWifiTetherCustomAutoTurnOffPreferenceController;
import com.oneplus.settings.wifi.tether.OPWifiTetherDeviceManagerController;
import java.util.ArrayList;
import java.util.List;

public class WifiTetherSettings extends RestrictedDashboardFragment implements WifiTetherBasePreferenceController.OnTetherConfigUpdateListener, TetherDataObserver.OnTetherDataChangeCallback {
    static final String KEY_WIFI_TETHER_AUTO_OFF = "wifi_tether_auto_turn_off";
    static final String KEY_WIFI_TETHER_NETWORK_AP_BAND = "wifi_tether_network_ap_band";
    static final String KEY_WIFI_TETHER_NETWORK_NAME = "wifi_tether_network_name";
    static final String KEY_WIFI_TETHER_NETWORK_PASSWORD = "wifi_tether_network_password";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.wifi_tether_settings) {
        /* class com.android.settings.wifi.tether.WifiTetherSettings.AnonymousClass2 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!TetherUtil.isTetherAvailable(context)) {
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_NAME);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_PASSWORD);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_AUTO_OFF);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_AP_BAND);
            }
            if (OPUtils.isGuestMode()) {
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_NAME);
                nonIndexableKeys.add("wifi_tether_security");
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_PASSWORD);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_AP_BAND);
                nonIndexableKeys.add("wifi_tether_security");
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_AUTO_OFF);
                nonIndexableKeys.add("wifi_tether_custom_auto_turn_off");
                nonIndexableKeys.add("wifi_tether_network_ap_band_single_select");
                nonIndexableKeys.add(OPWifiTetherDeviceManagerController.PREF_KEY);
            }
            if (isConnManagerEnable(context)) {
                nonIndexableKeys.add(OPWifiTetherDeviceManagerController.PREF_KEY);
            }
            nonIndexableKeys.add("wifi_tether_settings_screen");
            return nonIndexableKeys;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !FeatureFlagUtils.isEnabled(context, "settings_tether_all_in_one");
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return WifiTetherSettings.buildPreferenceControllers(context, null);
        }

        private boolean isConnManagerEnable(Context context) {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            return OPUtils.isAppPakExist(context, "com.oneplus.wifiapsettings") && wifiManager != null && wifiManager.getWifiApState() == 13;
        }
    };
    private static final IntentFilter TETHER_STATE_CHANGE_FILTER;
    private WifiTetherApBandPreferenceController mApBandPreferenceController;
    private OPWifiTetherDeviceManagerController mConnectedDeviceManagerController;
    private Handler mHandler = new Handler() {
        /* class com.android.settings.wifi.tether.WifiTetherSettings.AnonymousClass1 */

        public void handleMessage(Message message) {
            if (message.what == 1) {
                WifiTetherSettings.this.startTether();
            }
        }
    };
    private OPWifiTetherApBandPreferenceController mOPApBandPreferenceController;
    private WifiTetherPasswordPreferenceController mPasswordPreferenceController;
    private boolean mRestartWifiApAfterConfigChange;
    private WifiTetherSSIDPreferenceController mSSIDPreferenceController;
    private WifiTetherSecurityPreferenceController mSecurityPreferenceController;
    private WifiTetherSwitchBarController mSwitchBarController;
    TetherChangeReceiver mTetherChangeReceiver;
    private TetherDataObserver mTetherDataObserver;
    private boolean mUnavailable;
    private WifiManager mWifiManager;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "WifiTetherSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1014;
    }

    static {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.TETHER_STATE_CHANGED");
        TETHER_STATE_CHANGE_FILTER = intentFilter;
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        TETHER_STATE_CHANGE_FILTER.addAction("android.net.wifi.COUNTRY_CODE_CHANGED");
    }

    public WifiTetherSettings() {
        super("no_config_tethering");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setIfOnlyAvailableForAdmins(true);
        if (isUiRestricted()) {
            this.mUnavailable = true;
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mTetherChangeReceiver = new TetherChangeReceiver();
        this.mSSIDPreferenceController = (WifiTetherSSIDPreferenceController) use(WifiTetherSSIDPreferenceController.class);
        this.mSecurityPreferenceController = (WifiTetherSecurityPreferenceController) use(WifiTetherSecurityPreferenceController.class);
        this.mPasswordPreferenceController = (WifiTetherPasswordPreferenceController) use(WifiTetherPasswordPreferenceController.class);
        this.mApBandPreferenceController = (WifiTetherApBandPreferenceController) use(WifiTetherApBandPreferenceController.class);
        this.mConnectedDeviceManagerController = (OPWifiTetherDeviceManagerController) use(OPWifiTetherDeviceManagerController.class);
        this.mOPApBandPreferenceController = (OPWifiTetherApBandPreferenceController) use(OPWifiTetherApBandPreferenceController.class);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (!this.mUnavailable) {
            SettingsActivity settingsActivity = (SettingsActivity) getActivity();
            SwitchBar switchBar = settingsActivity.getSwitchBar();
            this.mSwitchBarController = new WifiTetherSwitchBarController(settingsActivity, switchBar);
            getSettingsLifecycle().addObserver(this.mSwitchBarController);
            switchBar.show();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        if (this.mUnavailable) {
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(C0017R$string.tethering_settings_not_available);
            }
            getPreferenceScreen().removeAll();
            return;
        }
        Context context = getContext();
        if (context != null) {
            context.registerReceiver(this.mTetherChangeReceiver, TETHER_STATE_CHANGE_FILTER);
        }
        if (OPUtils.isSupportUss()) {
            checkTetherData();
            this.mTetherDataObserver = new TetherDataObserver(this);
            getContentResolver().registerContentObserver(Settings.Global.getUriFor("TetheredData"), true, this.mTetherDataObserver);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        if (!this.mUnavailable) {
            Context context = getContext();
            if (context != null) {
                context.unregisterReceiver(this.mTetherChangeReceiver);
            }
            if (!this.mRestartWifiApAfterConfigChange) {
                this.mHandler.removeMessages(1);
            }
            if (OPUtils.isSupportUss() && this.mTetherDataObserver != null) {
                getContentResolver().unregisterContentObserver(this.mTetherDataObserver);
                this.mTetherDataObserver = null;
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.wifi_tether_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, new WifiTetherBasePreferenceController.OnTetherConfigUpdateListener() {
            /* class com.android.settings.wifi.tether.$$Lambda$gcILfsBNAxrC_fmlZxbsxqSkp8 */

            @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController.OnTetherConfigUpdateListener
            public final void onTetherConfigUpdated(BasePreferenceController basePreferenceController) {
                WifiTetherSettings.this.onTetherConfigUpdated(basePreferenceController);
            }
        });
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WifiTetherSSIDPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherSecurityPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherPasswordPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherApBandPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherAutoOffPreferenceController(context, KEY_WIFI_TETHER_AUTO_OFF));
        arrayList.add(new OPWiFi6StandardHotSpotPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new OPWifiTetherApBandPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new OPWifiTetherDeviceManagerController(context, onTetherConfigUpdateListener));
        arrayList.add(new OPWifiTetherCustomAutoTurnOffPreferenceController(context));
        return arrayList;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController.OnTetherConfigUpdateListener
    public void onTetherConfigUpdated(BasePreferenceController basePreferenceController) {
        SoftApConfiguration buildNewConfig = buildNewConfig();
        this.mPasswordPreferenceController.updateVisibility(buildNewConfig.getSecurityType());
        if (this.mApBandPreferenceController.isVendorDualApSupported() && this.mSecurityPreferenceController.isOweSapSupported()) {
            if ((buildNewConfig.getSecurityType() == 4) == this.mApBandPreferenceController.isBandEntriesHasDualband()) {
                this.mApBandPreferenceController.updatePreferenceEntries(buildNewConfig);
            }
        }
        if (this.mWifiManager.getWifiApState() == 13) {
            Log.d("TetheringSettings", "Wifi AP config changed while enabled, stop and restart");
            this.mRestartWifiApAfterConfigChange = true;
            this.mSwitchBarController.lambda$onClick$0();
        }
        this.mWifiManager.setSoftApConfiguration(buildNewConfig);
    }

    private SoftApConfiguration buildNewConfig() {
        SoftApConfiguration.Builder builder;
        int i;
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (softApConfiguration == null) {
            builder = new SoftApConfiguration.Builder();
        } else {
            builder = new SoftApConfiguration.Builder(softApConfiguration);
        }
        int securityType = this.mSecurityPreferenceController.getSecurityType();
        builder.setSsid(this.mSSIDPreferenceController.getSSID());
        builder.setPassphrase(this.mPasswordPreferenceController.getPasswordValidated(securityType), securityType);
        if (!WifiUtils.isSupportDualBand()) {
            i = this.mOPApBandPreferenceController.getBand();
        } else {
            i = this.mApBandPreferenceController.getBandIndex();
        }
        if (securityType == 4 && i == 8) {
            builder.setBand(1);
            Log.d("WifiTetherSettings", "buildNewConfig band is default 2GHZ");
        } else {
            builder.setBand(i);
            Log.d("WifiTetherSettings", "buildNewConfig band = " + i);
        }
        if (OPUtils.isSupportUstMode()) {
            if (Settings.Secure.getIntForUser(getContext().getContentResolver(), "oneplus_is_broadcat_wifi_name", 0, -2) == 0) {
                builder.setHiddenSsid(true);
            } else {
                builder.setHiddenSsid(false);
            }
        }
        return builder.build();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startTether() {
        this.mRestartWifiApAfterConfigChange = false;
        this.mSwitchBarController.startTether();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateDisplayWithNewConfig() {
        ((WifiTetherSSIDPreferenceController) use(WifiTetherSSIDPreferenceController.class)).updateDisplay();
        ((WifiTetherSecurityPreferenceController) use(WifiTetherSecurityPreferenceController.class)).updateDisplay();
        ((WifiTetherPasswordPreferenceController) use(WifiTetherPasswordPreferenceController.class)).updateDisplay();
        ((WifiTetherApBandPreferenceController) use(WifiTetherApBandPreferenceController.class)).updateDisplay();
    }

    class TetherChangeReceiver extends BroadcastReceiver {
        TetherChangeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("WifiTetherSettings", "updating display config due to receiving broadcast action " + action);
            WifiTetherSettings.this.updateDisplayWithNewConfig();
            if (action.equals("android.net.conn.TETHER_STATE_CHANGED")) {
                if (WifiTetherSettings.this.mWifiManager.getWifiApState() == 11 && WifiTetherSettings.this.mRestartWifiApAfterConfigChange) {
                    WifiTetherSettings.this.startTether();
                }
            } else if (action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {
                int intExtra = intent.getIntExtra("wifi_state", 0);
                if (!(WifiTetherSettings.this.mOPApBandPreferenceController == null || WifiTetherSettings.this.mConnectedDeviceManagerController == null)) {
                    WifiTetherSettings.this.mHandler.postDelayed(new Runnable() {
                        /* class com.android.settings.wifi.tether.WifiTetherSettings.TetherChangeReceiver.AnonymousClass1 */

                        public void run() {
                            WifiTetherSettings.this.mOPApBandPreferenceController.updateDisplay();
                            WifiTetherSettings.this.mConnectedDeviceManagerController.updateDisplay();
                        }
                    }, 200);
                }
                if (intExtra == 11 && WifiTetherSettings.this.mRestartWifiApAfterConfigChange) {
                    WifiTetherSettings.this.startTether();
                } else if (intExtra == 14) {
                    int intExtra2 = intent.getIntExtra("android.net.wifi.extra.WIFI_AP_FAILURE_REASON", 0);
                    String stringExtra = intent.getStringExtra("wifi_ap_error_description");
                    if (intExtra2 == 1 && stringExtra != null && stringExtra.equals("wifi_ap_error_no_5g_support")) {
                        Toast.makeText(context, "5Ghz band not supported. band selection disabled", 1).show();
                    }
                }
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getInitialExpandedChildCount() {
        WifiTetherSecurityPreferenceController wifiTetherSecurityPreferenceController = this.mSecurityPreferenceController;
        if (wifiTetherSecurityPreferenceController == null) {
            return 0;
        }
        wifiTetherSecurityPreferenceController.getSecurityType();
        return 0;
    }

    @Override // com.android.settings.wifi.tether.TetherDataObserver.OnTetherDataChangeCallback
    public void onTetherDataChange() {
        checkTetherData();
    }

    private void checkTetherData() {
        if (OPUtils.isSupportUss() && TetherUtils.getTetherData(getPrefContext()) == 1) {
            finish();
            WifiTetherSwitchBarController wifiTetherSwitchBarController = this.mSwitchBarController;
            if (wifiTetherSwitchBarController != null) {
                wifiTetherSwitchBarController.lambda$onClick$0();
            }
        }
    }
}
