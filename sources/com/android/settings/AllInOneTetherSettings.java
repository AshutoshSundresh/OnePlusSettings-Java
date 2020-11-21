package com.android.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.network.BluetoothTetherPreferenceController;
import com.android.settings.network.EthernetTetherPreferenceController;
import com.android.settings.network.TetherEnabler;
import com.android.settings.network.UsbTetherPreferenceController;
import com.android.settings.network.WifiTetherDisablePreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBarController;
import com.android.settings.wifi.tether.WifiTetherApBandPreferenceController;
import com.android.settings.wifi.tether.WifiTetherAutoOffPreferenceController;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settings.wifi.tether.WifiTetherFooterPreferenceController;
import com.android.settings.wifi.tether.WifiTetherPasswordPreferenceController;
import com.android.settings.wifi.tether.WifiTetherSSIDPreferenceController;
import com.android.settings.wifi.tether.WifiTetherSecurityPreferenceController;
import com.android.settingslib.TetherUtil;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AllInOneTetherSettings extends RestrictedDashboardFragment implements DataSaverBackend.Listener, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener {
    static final int EXPANDED_CHILD_COUNT_DEFAULT = 4;
    static final int EXPANDED_CHILD_COUNT_MAX = Integer.MAX_VALUE;
    static final int EXPANDED_CHILD_COUNT_WITH_SECURITY_NON = 3;
    static final String KEY_WIFI_TETHER_AUTO_OFF = "wifi_tether_auto_turn_off_2";
    static final String KEY_WIFI_TETHER_NETWORK_AP_BAND = "wifi_tether_network_ap_band_2";
    static final String KEY_WIFI_TETHER_NETWORK_NAME = "wifi_tether_network_name_2";
    static final String KEY_WIFI_TETHER_NETWORK_PASSWORD = "wifi_tether_network_password_2";
    static final String KEY_WIFI_TETHER_SECURITY = "wifi_tether_security_2";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.all_tether_prefs) {
        /* class com.android.settings.AllInOneTetherSettings.AnonymousClass3 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!TetherUtil.isTetherAvailable(context)) {
                nonIndexableKeys.add(AllInOneTetherSettings.KEY_WIFI_TETHER_NETWORK_NAME);
                nonIndexableKeys.add(AllInOneTetherSettings.KEY_WIFI_TETHER_NETWORK_PASSWORD);
                nonIndexableKeys.add(AllInOneTetherSettings.KEY_WIFI_TETHER_AUTO_OFF);
                nonIndexableKeys.add(AllInOneTetherSettings.KEY_WIFI_TETHER_NETWORK_AP_BAND);
                nonIndexableKeys.add(AllInOneTetherSettings.KEY_WIFI_TETHER_SECURITY);
            }
            return nonIndexableKeys;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return FeatureFlagUtils.isEnabled(context, "settings_tether_all_in_one");
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return AllInOneTetherSettings.buildPreferenceControllers(context, null);
        }
    };
    private WifiTetherApBandPreferenceController mApBandPreferenceController;
    private final AtomicReference<BluetoothPan> mBluetoothPan = new AtomicReference<>();
    private DataSaverBackend mDataSaverBackend;
    private boolean mDataSaverEnabled;
    private Preference mDataSaverFooter;
    private boolean mHasShownAdvance;
    private WifiTetherPasswordPreferenceController mPasswordPreferenceController;
    private final BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {
        /* class com.android.settings.AllInOneTetherSettings.AnonymousClass2 */

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            AllInOneTetherSettings.this.mBluetoothPan.set((BluetoothPan) bluetoothProfile);
        }

        public void onServiceDisconnected(int i) {
            AllInOneTetherSettings.this.mBluetoothPan.set(null);
        }
    };
    private boolean mRestartWifiApAfterConfigChange;
    private WifiTetherSSIDPreferenceController mSSIDPreferenceController;
    private WifiTetherSecurityPreferenceController mSecurityPreferenceController;
    private boolean mShouldShowWifiConfig = true;
    final TetherEnabler.OnTetherStateUpdateListener mStateUpdateListener = new TetherEnabler.OnTetherStateUpdateListener() {
        /* class com.android.settings.$$Lambda$AllInOneTetherSettings$6gBAxf2biT13gKStOW2dqkms4p8 */

        @Override // com.android.settings.network.TetherEnabler.OnTetherStateUpdateListener
        public final void onTetherStateUpdated(int i) {
            AllInOneTetherSettings.this.lambda$new$0$AllInOneTetherSettings(i);
        }
    };
    private final BroadcastReceiver mTetherChangeReceiver = new BroadcastReceiver() {
        /* class com.android.settings.AllInOneTetherSettings.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Log.isLoggable("AllInOneTetherSettings", 3)) {
                Log.d("AllInOneTetherSettings", "updating display config due to receiving broadcast action " + action);
            }
            AllInOneTetherSettings.this.updateDisplayWithNewConfig();
            if (TextUtils.equals(action, "android.net.conn.TETHER_STATE_CHANGED")) {
                restartWifiTetherIfNeed(AllInOneTetherSettings.this.mWifiManager.getWifiApState());
            } else if (TextUtils.equals(action, "android.net.wifi.WIFI_AP_STATE_CHANGED")) {
                restartWifiTetherIfNeed(intent.getIntExtra("wifi_state", 0));
            }
        }

        private void restartWifiTetherIfNeed(int i) {
            if (i == 11 && AllInOneTetherSettings.this.mRestartWifiApAfterConfigChange) {
                AllInOneTetherSettings.this.mRestartWifiApAfterConfigChange = false;
                AllInOneTetherSettings.this.mTetherEnabler.startTethering(0);
            }
        }
    };
    private TetherEnabler mTetherEnabler;
    private boolean mUnavailable;
    private WifiManager mWifiManager;
    private PreferenceGroup mWifiTetherGroup;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AllInOneTetherSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 90;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onBlacklistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onWhitelistStatusChanged(int i, boolean z) {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$AllInOneTetherSettings(int i) {
        boolean z = false;
        if (TetherEnabler.isTethering(i, 0) || i == 0) {
            z = true;
        }
        this.mShouldShowWifiConfig = z;
        getPreferenceScreen().setInitialExpandedChildrenCount(getInitialExpandedChildCount());
        this.mWifiTetherGroup.setVisible(this.mShouldShowWifiConfig);
    }

    public AllInOneTetherSettings() {
        super("no_config_tethering");
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mSSIDPreferenceController = (WifiTetherSSIDPreferenceController) use(WifiTetherSSIDPreferenceController.class);
        this.mSecurityPreferenceController = (WifiTetherSecurityPreferenceController) use(WifiTetherSecurityPreferenceController.class);
        this.mPasswordPreferenceController = (WifiTetherPasswordPreferenceController) use(WifiTetherPasswordPreferenceController.class);
        this.mApBandPreferenceController = (WifiTetherApBandPreferenceController) use(WifiTetherApBandPreferenceController.class);
        getSettingsLifecycle().addObserver((LifecycleObserver) use(UsbTetherPreferenceController.class));
        getSettingsLifecycle().addObserver((LifecycleObserver) use(BluetoothTetherPreferenceController.class));
        getSettingsLifecycle().addObserver((LifecycleObserver) use(EthernetTetherPreferenceController.class));
        getSettingsLifecycle().addObserver((LifecycleObserver) use(WifiTetherDisablePreferenceController.class));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DataSaverBackend dataSaverBackend = new DataSaverBackend(getContext());
        this.mDataSaverBackend = dataSaverBackend;
        this.mDataSaverEnabled = dataSaverBackend.isDataSaverEnabled();
        this.mDataSaverFooter = findPreference("disabled_on_data_saver_2");
        this.mWifiTetherGroup = (PreferenceGroup) findPreference("wifi_tether_settings_group");
        setIfOnlyAvailableForAdmins(true);
        if (isUiRestricted()) {
            this.mUnavailable = true;
            return;
        }
        this.mDataSaverBackend.addListener(this);
        onDataSaverChanged(this.mDataSaverBackend.isDataSaverEnabled());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (!this.mUnavailable) {
            SettingsActivity settingsActivity = (SettingsActivity) getActivity();
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter != null) {
                defaultAdapter.getProfileProxy(settingsActivity.getApplicationContext(), this.mProfileServiceListener, 5);
            }
            SwitchBar switchBar = settingsActivity.getSwitchBar();
            this.mTetherEnabler = new TetherEnabler(settingsActivity, new SwitchBarController(switchBar), this.mBluetoothPan);
            getSettingsLifecycle().addObserver(this.mTetherEnabler);
            ((UsbTetherPreferenceController) use(UsbTetherPreferenceController.class)).setTetherEnabler(this.mTetherEnabler);
            ((BluetoothTetherPreferenceController) use(BluetoothTetherPreferenceController.class)).setTetherEnabler(this.mTetherEnabler);
            ((EthernetTetherPreferenceController) use(EthernetTetherPreferenceController.class)).setTetherEnabler(this.mTetherEnabler);
            ((WifiTetherDisablePreferenceController) use(WifiTetherDisablePreferenceController.class)).setTetherEnabler(this.mTetherEnabler);
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
            IntentFilter intentFilter = new IntentFilter("android.net.conn.TETHER_STATE_CHANGED");
            intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
            context.registerReceiver(this.mTetherChangeReceiver, intentFilter);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        TetherEnabler tetherEnabler;
        super.onResume();
        if (!this.mUnavailable && (tetherEnabler = this.mTetherEnabler) != null) {
            tetherEnabler.addListener(this.mStateUpdateListener);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        TetherEnabler tetherEnabler;
        super.onPause();
        if (!this.mUnavailable && (tetherEnabler = this.mTetherEnabler) != null) {
            tetherEnabler.removeListener(this.mStateUpdateListener);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        Context context;
        super.onStop();
        if (!this.mUnavailable && (context = getContext()) != null) {
            context.unregisterReceiver(this.mTetherChangeReceiver);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        this.mDataSaverBackend.remListener(this);
        super.onDestroy();
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        this.mDataSaverEnabled = z;
        this.mDataSaverFooter.setVisible(z);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WifiTetherSSIDPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherPasswordPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherApBandPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherSecurityPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherAutoOffPreferenceController(context, KEY_WIFI_TETHER_AUTO_OFF));
        arrayList.add(new WifiTetherFooterPreferenceController(context));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.all_tether_prefs;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_tether;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController.OnTetherConfigUpdateListener
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onTetherConfigUpdated(com.android.settings.core.BasePreferenceController r5) {
        /*
        // Method dump skipped, instructions count: 101
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.AllInOneTetherSettings.onTetherConfigUpdated(com.android.settings.core.BasePreferenceController):void");
    }

    private SoftApConfiguration buildNewConfig() {
        SoftApConfiguration.Builder builder = new SoftApConfiguration.Builder();
        int securityType = this.mSecurityPreferenceController.getSecurityType();
        int bandIndex = this.mApBandPreferenceController.getBandIndex();
        builder.setSsid(this.mSSIDPreferenceController.getSSID());
        builder.setPassphrase(this.mPasswordPreferenceController.getPasswordValidated(securityType), securityType);
        if (securityType == 4 && bandIndex == 8) {
            builder.setBand(1);
        } else {
            builder.setBand(bandIndex);
        }
        return builder.build();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateDisplayWithNewConfig() {
        this.mSSIDPreferenceController.updateDisplay();
        this.mSecurityPreferenceController.updateDisplay();
        this.mPasswordPreferenceController.updateDisplay();
        this.mApBandPreferenceController.updateDisplay();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getInitialExpandedChildCount() {
        if (this.mHasShownAdvance || !this.mShouldShowWifiConfig) {
            this.mHasShownAdvance = true;
            return EXPANDED_CHILD_COUNT_MAX;
        }
        WifiTetherSecurityPreferenceController wifiTetherSecurityPreferenceController = this.mSecurityPreferenceController;
        if (wifiTetherSecurityPreferenceController != null && wifiTetherSecurityPreferenceController.getSecurityType() == 0) {
            return 3;
        }
        return 4;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, androidx.preference.PreferenceGroup.OnExpandButtonClickListener
    public void onExpandButtonClick() {
        super.onExpandButtonClick();
        this.mHasShownAdvance = true;
    }
}
