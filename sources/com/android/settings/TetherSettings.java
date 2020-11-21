package com.android.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.TetheringManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.TetherSettings;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.tether.TetherDataObserver;
import com.android.settings.wifi.tether.WifiTetherPreferenceController;
import com.android.settings.wifi.tether.utils.TetherUtils;
import com.android.settingslib.TetherUtil;
import com.oneplus.settings.utils.OPUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TetherSettings extends RestrictedSettingsFragment implements DataSaverBackend.Listener, TetherDataObserver.OnTetherDataChangeCallback {
    static final String KEY_ENABLE_BLUETOOTH_TETHERING = "enable_bluetooth_tethering";
    static final String KEY_TETHER_PREFS_FOOTER = "tether_prefs_footer";
    static final String KEY_TETHER_PREFS_SCREEN = "tether_prefs_screen";
    static final String KEY_USB_TETHER_SETTINGS = "usb_tether_settings";
    static final String KEY_WIFI_TETHER = "wifi_tether";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.TetherSettings.AnonymousClass2 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.tether_prefs;
            return Arrays.asList(searchIndexableResource);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !FeatureFlagUtils.isEnabled(context, "settings_tether_all_in_one");
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
            if (!TetherUtil.isTetherAvailable(context)) {
                nonIndexableKeys.add(TetherSettings.KEY_TETHER_PREFS_SCREEN);
                nonIndexableKeys.add(TetherSettings.KEY_WIFI_TETHER);
            }
            boolean z = false;
            if (!(connectivityManager.getTetherableUsbRegexs().length != 0) || Utils.isMonkeyRunning()) {
                nonIndexableKeys.add(TetherSettings.KEY_USB_TETHER_SETTINGS);
            }
            if (connectivityManager.getTetherableBluetoothRegexs().length != 0) {
                z = true;
            }
            if (!z) {
                nonIndexableKeys.add(TetherSettings.KEY_ENABLE_BLUETOOTH_TETHERING);
            }
            if (!(!TextUtils.isEmpty(context.getResources().getString(17039904)))) {
                nonIndexableKeys.add("enable_ethernet_tethering");
            }
            return nonIndexableKeys;
        }
    };
    private int lastTetherData = 3;
    private boolean mBluetoothEnableForTether;
    private AtomicReference<BluetoothPan> mBluetoothPan = new AtomicReference<>();
    private String[] mBluetoothRegexs;
    private SwitchPreference mBluetoothTether;
    private int mChoiceItem = -1;
    private String mChoiceItemValue;
    private SwitchPreference mChoicePreference;
    private ConnectivityManager mCm;
    private DataSaverBackend mDataSaverBackend;
    private boolean mDataSaverEnabled;
    private Preference mDataSaverFooter;
    private EthernetManager mEm;
    private EthernetListener mEthernetListener;
    private String mEthernetRegex;
    private SwitchPreference mEthernetTether;
    private Handler mHandler = new Handler();
    private boolean mMassStorageActive;
    private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {
        /* class com.android.settings.TetherSettings.AnonymousClass1 */

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            TetherSettings.this.mBluetoothPan.set((BluetoothPan) bluetoothProfile);
        }

        public void onServiceDisconnected(int i) {
            TetherSettings.this.mBluetoothPan.set(null);
        }
    };
    private OnStartTetheringCallback mStartTetheringCallback;
    private BroadcastReceiver mTetherChangeReceiver;
    private TetherDataObserver mTetherDataObserver;
    private TetheringEventCallback mTetheringEventCallback;
    private TetheringManager mTm;
    private boolean mUnavailable;
    private boolean mUsbConnected;
    private String[] mUsbRegexs;
    private SwitchPreference mUsbTether;
    private WifiTetherPreferenceController mWifiTetherPreferenceController;

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

    public TetherSettings() {
        super("no_config_tethering");
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mWifiTetherPreferenceController = new WifiTetherPreferenceController(context, getSettingsLifecycle());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.tether_prefs);
        DataSaverBackend dataSaverBackend = new DataSaverBackend(getContext());
        this.mDataSaverBackend = dataSaverBackend;
        this.mDataSaverEnabled = dataSaverBackend.isDataSaverEnabled();
        this.mDataSaverFooter = findPreference("disabled_on_data_saver");
        setIfOnlyAvailableForAdmins(true);
        if (isUiRestricted()) {
            this.mUnavailable = true;
            getPreferenceScreen().removeAll();
            return;
        }
        FragmentActivity activity = getActivity();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.getState() == 12) {
            defaultAdapter.getProfileProxy(activity.getApplicationContext(), this.mProfileServiceListener, 5);
        }
        this.mUsbTether = (SwitchPreference) findPreference(KEY_USB_TETHER_SETTINGS);
        this.mBluetoothTether = (SwitchPreference) findPreference(KEY_ENABLE_BLUETOOTH_TETHERING);
        this.mEthernetTether = (SwitchPreference) findPreference("enable_ethernet_tethering");
        setFooterPreferenceTitle();
        this.mDataSaverBackend.addListener(this);
        this.mCm = (ConnectivityManager) getSystemService("connectivity");
        this.mEm = (EthernetManager) getSystemService("ethernet");
        this.mTm = (TetheringManager) getSystemService("tethering");
        this.mUsbRegexs = this.mCm.getTetherableUsbRegexs();
        this.mBluetoothRegexs = this.mCm.getTetherableBluetoothRegexs();
        this.mEthernetRegex = getContext().getResources().getString(17039904);
        boolean z = this.mUsbRegexs.length != 0;
        boolean z2 = (defaultAdapter == null || this.mBluetoothRegexs.length == 0) ? false : true;
        boolean z3 = !TextUtils.isEmpty(this.mEthernetRegex);
        if (!z || Utils.isMonkeyRunning()) {
            getPreferenceScreen().removePreference(this.mUsbTether);
        }
        this.mWifiTetherPreferenceController.displayPreference(getPreferenceScreen());
        if (!z2) {
            getPreferenceScreen().removePreference(this.mBluetoothTether);
        } else {
            BluetoothPan bluetoothPan = this.mBluetoothPan.get();
            if (bluetoothPan == null || !bluetoothPan.isTetheringOn()) {
                this.mBluetoothTether.setChecked(false);
            } else {
                this.mBluetoothTether.setChecked(true);
            }
        }
        if (!z3) {
            getPreferenceScreen().removePreference(this.mEthernetTether);
        }
        onDataSaverChanged(this.mDataSaverBackend.isDataSaverEnabled());
    }

    @Override // com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        this.mDataSaverBackend.remListener(this);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothProfile bluetoothProfile = (BluetoothProfile) this.mBluetoothPan.getAndSet(null);
        if (!(bluetoothProfile == null || defaultAdapter == null)) {
            defaultAdapter.closeProfileProxy(5, bluetoothProfile);
        }
        super.onDestroy();
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        this.mDataSaverEnabled = z;
        this.mUsbTether.setEnabled(!z);
        this.mBluetoothTether.setEnabled(!this.mDataSaverEnabled);
        this.mEthernetTether.setEnabled(!this.mDataSaverEnabled);
        this.mDataSaverFooter.setVisible(this.mDataSaverEnabled);
    }

    /* access modifiers changed from: package-private */
    public void setFooterPreferenceTitle() {
        Preference findPreference = findPreference(KEY_TETHER_PREFS_FOOTER);
        if (((WifiManager) getContext().getSystemService("wifi")).isStaApConcurrencySupported()) {
            findPreference.setSummary(C0017R$string.tethering_footer_info_sta_ap_concurrency);
        } else {
            findPreference.setSummary(C0017R$string.tethering_footer_info);
        }
    }

    private class TetherChangeReceiver extends BroadcastReceiver {
        private TetherChangeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            BluetoothAdapter defaultAdapter;
            BluetoothAdapter defaultAdapter2;
            String action = intent.getAction();
            if (action.equals("android.net.conn.TETHER_STATE_CHANGED")) {
                ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("availableArray");
                ArrayList<String> stringArrayListExtra2 = intent.getStringArrayListExtra("tetherArray");
                ArrayList<String> stringArrayListExtra3 = intent.getStringArrayListExtra("erroredArray");
                TetherSettings.this.updateState((String[]) stringArrayListExtra.toArray(new String[stringArrayListExtra.size()]), (String[]) stringArrayListExtra2.toArray(new String[stringArrayListExtra2.size()]), (String[]) stringArrayListExtra3.toArray(new String[stringArrayListExtra3.size()]));
            } else if (action.equals("android.intent.action.MEDIA_SHARED")) {
                TetherSettings.this.mMassStorageActive = true;
                TetherSettings.this.updateState();
            } else if (action.equals("android.intent.action.MEDIA_UNSHARED")) {
                TetherSettings.this.mMassStorageActive = false;
                TetherSettings.this.updateState();
            } else if (action.equals("android.hardware.usb.action.USB_STATE")) {
                TetherSettings.this.mUsbConnected = intent.getBooleanExtra("connected", false);
                TetherSettings.this.updateState();
            } else if (action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                if (TetherSettings.this.mBluetoothEnableForTether) {
                    if (intExtra == Integer.MIN_VALUE || intExtra == 10) {
                        TetherSettings.this.mBluetoothEnableForTether = false;
                    } else if (intExtra == 12) {
                        if (!OPUtils.isSupportUss()) {
                            TetherSettings.this.startTethering(2);
                        } else {
                            TetherSettings tetherSettings = TetherSettings.this;
                            tetherSettings.startUssTethering(tetherSettings.mBluetoothTether, 2);
                        }
                        TetherSettings.this.mBluetoothEnableForTether = false;
                        if (TetherSettings.this.mBluetoothPan.get() == null && (defaultAdapter2 = BluetoothAdapter.getDefaultAdapter()) != null) {
                            defaultAdapter2.getProfileProxy(TetherSettings.this.getActivity().getApplicationContext(), TetherSettings.this.mProfileServiceListener, 5);
                        }
                    }
                } else if (intExtra == 12 && TetherSettings.this.mBluetoothPan.get() == null && (defaultAdapter = BluetoothAdapter.getDefaultAdapter()) != null) {
                    defaultAdapter.getProfileProxy(TetherSettings.this.getActivity().getApplicationContext(), TetherSettings.this.mProfileServiceListener, 5);
                }
                TetherSettings.this.updateState();
            } else if (action.equals("android.intent.action.setupDataError_tether")) {
                Log.d("TetheringSettings", "onReceive tether error braodcast");
                if (intent.getBooleanExtra("data_call_error", false) && intent.getIntExtra("data_call_code", 0) == 67) {
                    TetherSettings.this.tetherError(2);
                    TetherSettings.this.stopTethering();
                }
            } else if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                TetherSettings.this.updateSimStatus(TetherUtils.isSimStatusChange(context));
            }
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        if (this.mUnavailable) {
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(C0017R$string.tethering_settings_not_available);
            }
            getPreferenceScreen().removeAll();
            return;
        }
        FragmentActivity activity = getActivity();
        this.mStartTetheringCallback = new OnStartTetheringCallback(this);
        this.mTetheringEventCallback = new TetheringEventCallback();
        this.mTm.registerTetheringEventCallback(new HandlerExecutor(this.mHandler), this.mTetheringEventCallback);
        this.mMassStorageActive = "shared".equals(Environment.getExternalStorageState());
        this.mTetherChangeReceiver = new TetherChangeReceiver();
        Intent registerReceiver = activity.registerReceiver(this.mTetherChangeReceiver, new IntentFilter("android.net.conn.TETHER_STATE_CHANGED"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_STATE");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.MEDIA_SHARED");
        intentFilter2.addAction("android.intent.action.MEDIA_UNSHARED");
        intentFilter2.addDataScheme("file");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter2);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter3);
        if (OPUtils.isSupportUss()) {
            IntentFilter intentFilter4 = new IntentFilter();
            intentFilter4.addAction("android.intent.action.setupDataError_tether");
            intentFilter4.addAction("android.intent.action.SIM_STATE_CHANGED");
            activity.registerReceiver(this.mTetherChangeReceiver, intentFilter4);
        }
        if (registerReceiver != null) {
            this.mTetherChangeReceiver.onReceive(activity, registerReceiver);
        }
        EthernetListener ethernetListener = new EthernetListener();
        this.mEthernetListener = ethernetListener;
        EthernetManager ethernetManager = this.mEm;
        if (ethernetManager != null) {
            ethernetManager.addListener(ethernetListener);
        }
        updateState();
        if (OPUtils.isSupportUss() && getContentResolver() != null) {
            checkTetherData();
            this.mTetherDataObserver = new TetherDataObserver(this);
            getContentResolver().registerContentObserver(Settings.Global.getUriFor("TetheredData"), true, this.mTetherDataObserver);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        if (!this.mUnavailable) {
            getActivity().unregisterReceiver(this.mTetherChangeReceiver);
            this.mTm.unregisterTetheringEventCallback(this.mTetheringEventCallback);
            EthernetManager ethernetManager = this.mEm;
            if (ethernetManager != null) {
                ethernetManager.removeListener(this.mEthernetListener);
            }
            this.mTetherChangeReceiver = null;
            this.mStartTetheringCallback = null;
            this.mTetheringEventCallback = null;
            this.mEthernetListener = null;
            if (OPUtils.isSupportUss() && this.mTetherDataObserver != null && getContentResolver() != null) {
                getContentResolver().unregisterContentObserver(this.mTetherDataObserver);
                this.mTetherDataObserver = null;
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    public void updateState() {
        updateState(this.mCm.getTetherableIfaces(), this.mCm.getTetheredIfaces(), this.mCm.getTetheringErroredIfaces());
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateState(String[] strArr, String[] strArr2, String[] strArr3) {
        updateUsbState(strArr, strArr2, strArr3);
        updateBluetoothState();
        updateEthernetState(strArr, strArr2);
    }

    private void updateUsbState(String[] strArr, String[] strArr2, String[] strArr3) {
        boolean z = this.mUsbConnected && !this.mMassStorageActive;
        int i = 0;
        for (String str : strArr) {
            for (String str2 : this.mUsbRegexs) {
                if (str.matches(str2) && i == 0) {
                    i = this.mCm.getLastTetherError(str);
                }
            }
        }
        boolean z2 = false;
        for (String str3 : strArr2) {
            for (String str4 : this.mUsbRegexs) {
                if (str3.matches(str4)) {
                    z2 = true;
                }
            }
        }
        for (String str5 : strArr3) {
            for (String str6 : this.mUsbRegexs) {
                str5.matches(str6);
            }
        }
        if (z2) {
            this.mUsbTether.setEnabled(!this.mDataSaverEnabled);
            this.mUsbTether.setChecked(true);
        } else if (z) {
            this.mUsbTether.setEnabled(!this.mDataSaverEnabled);
            this.mUsbTether.setChecked(false);
        } else {
            this.mUsbTether.setEnabled(false);
            this.mUsbTether.setChecked(false);
        }
    }

    private void updateBluetoothState() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            int state = defaultAdapter.getState();
            if (state == 13) {
                this.mBluetoothTether.setEnabled(false);
            } else if (state == 11) {
                this.mBluetoothTether.setEnabled(false);
            } else {
                BluetoothPan bluetoothPan = this.mBluetoothPan.get();
                if (state != 12 || bluetoothPan == null || !bluetoothPan.isTetheringOn()) {
                    this.mBluetoothTether.setEnabled(!this.mDataSaverEnabled);
                    this.mBluetoothTether.setChecked(false);
                    return;
                }
                this.mBluetoothTether.setChecked(true);
                this.mBluetoothTether.setEnabled(!this.mDataSaverEnabled);
            }
        }
    }

    private void updateEthernetState(String[] strArr, String[] strArr2) {
        EthernetManager ethernetManager;
        boolean z = false;
        for (String str : strArr) {
            if (str.matches(this.mEthernetRegex)) {
                z = true;
            }
        }
        boolean z2 = false;
        for (String str2 : strArr2) {
            if (str2.matches(this.mEthernetRegex)) {
                z2 = true;
            }
        }
        if (z2) {
            this.mEthernetTether.setEnabled(!this.mDataSaverEnabled);
            this.mEthernetTether.setChecked(true);
        } else if (z || ((ethernetManager = this.mEm) != null && ethernetManager.isAvailable())) {
            this.mEthernetTether.setEnabled(!this.mDataSaverEnabled);
            this.mEthernetTether.setChecked(false);
        } else {
            this.mEthernetTether.setEnabled(false);
            this.mEthernetTether.setChecked(false);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startTethering(int i) {
        if (i == 2) {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter.getState() == 10) {
                this.mBluetoothEnableForTether = true;
                defaultAdapter.enable();
                this.mBluetoothTether.setEnabled(false);
                return;
            }
        }
        this.mCm.startTethering(i, true, this.mStartTetheringCallback, this.mHandler);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        SwitchPreference switchPreference = this.mUsbTether;
        if (preference != switchPreference) {
            SwitchPreference switchPreference2 = this.mBluetoothTether;
            if (preference != switchPreference2) {
                SwitchPreference switchPreference3 = this.mEthernetTether;
                if (preference == switchPreference3) {
                    if (switchPreference3.isChecked()) {
                        startTethering(5);
                    } else {
                        this.mCm.stopTethering(5);
                    }
                }
            } else if (!switchPreference2.isChecked()) {
                this.mCm.stopTethering(2);
            } else if (!OPUtils.isSupportUss()) {
                startTethering(2);
            } else {
                startUssTethering(this.mBluetoothTether, 2);
            }
        } else if (!switchPreference.isChecked()) {
            this.mCm.stopTethering(1);
        } else if (!OPUtils.isSupportUss()) {
            startTethering(1);
        } else {
            startUssTethering(this.mUsbTether, 1);
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_tether;
    }

    /* access modifiers changed from: private */
    public static final class OnStartTetheringCallback extends ConnectivityManager.OnStartTetheringCallback {
        final WeakReference<TetherSettings> mTetherSettings;

        OnStartTetheringCallback(TetherSettings tetherSettings) {
            this.mTetherSettings = new WeakReference<>(tetherSettings);
        }

        public void onTetheringStarted() {
            update();
        }

        public void onTetheringFailed() {
            update();
        }

        private void update() {
            TetherSettings tetherSettings = this.mTetherSettings.get();
            if (tetherSettings != null) {
                tetherSettings.updateState();
            }
        }
    }

    private final class TetheringEventCallback implements TetheringManager.TetheringEventCallback {
        private TetheringEventCallback() {
        }

        public void onTetheredInterfacesChanged(List<String> list) {
            TetherSettings.this.updateState();
        }
    }

    /* access modifiers changed from: private */
    public final class EthernetListener implements EthernetManager.Listener {
        private EthernetListener() {
        }

        public void onAvailabilityChanged(String str, boolean z) {
            TetherSettings.this.mHandler.post(new Runnable() {
                /* class com.android.settings.$$Lambda$TetherSettings$EthernetListener$h2_T0AJJu00WYdLgLWHNeRaOLw */

                public final void run() {
                    TetherSettings.EthernetListener.lambda$onAvailabilityChanged$0(TetherSettings.this);
                }
            });
        }
    }

    private void checkTetherData() {
        int tetherData;
        if (OPUtils.isSupportUss() && this.lastTetherData != (tetherData = TetherUtils.getTetherData(getPrefContext()))) {
            this.lastTetherData = tetherData;
            if (tetherData == 1) {
                finish();
            } else if (tetherData == 2) {
                if (this.mCm != null) {
                    SwitchPreference switchPreference = this.mBluetoothTether;
                    if (switchPreference != null && switchPreference.isChecked()) {
                        this.mCm.stopTethering(2);
                    }
                    SwitchPreference switchPreference2 = this.mUsbTether;
                    if (switchPreference2 != null && switchPreference2.isChecked()) {
                        this.mCm.stopTethering(1);
                    }
                }
                if (getPreferenceScreen() != null) {
                    if (this.mUsbTether != null) {
                        getPreferenceScreen().removePreference(this.mUsbTether);
                    }
                    if (this.mBluetoothTether != null) {
                        getPreferenceScreen().removePreference(this.mBluetoothTether);
                    }
                }
            } else if (tetherData == 3) {
                if (this.mUsbTether != null) {
                    getPreferenceScreen().addPreference(this.mUsbTether);
                }
                if (this.mBluetoothTether != null) {
                    getPreferenceScreen().addPreference(this.mBluetoothTether);
                }
            }
        }
    }

    @Override // com.android.settings.wifi.tether.TetherDataObserver.OnTetherDataChangeCallback
    public void onTetherDataChange() {
        checkTetherData();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startUssTethering(SwitchPreference switchPreference, int i) {
        this.mChoicePreference = switchPreference;
        this.mChoiceItem = i;
        if (!TetherUtils.isNoSimCard(getContext())) {
            if (i == 1) {
                this.mChoiceItemValue = getPrefContext().getString(C0017R$string.usb_tethering_button_text);
            } else {
                this.mChoiceItemValue = getPrefContext().getString(C0017R$string.bluetooth_tether_checkbox_text);
            }
            if (TetherUtils.isHaveProfile(getPrefContext())) {
                startTethering(i);
            } else {
                tetherError(2);
            }
        } else {
            tetherError(1);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void tetherError(int i) {
        if (i == 1) {
            TetherUtils.showTertheringErrorDialog(getContext(), getPrefContext().getString(C0017R$string.tether_no_sim_title), getPrefContext().getString(C0017R$string.tether_no_sim_message));
            SwitchPreference switchPreference = this.mChoicePreference;
            if (switchPreference != null) {
                switchPreference.setChecked(false);
                this.mChoicePreference.setEnabled(false);
            }
        } else if (i == 2) {
            String str = this.mChoiceItemValue;
            if (str == null || str.isEmpty()) {
                this.mChoiceItemValue = getPrefContext().getString(C0017R$string.tether_error_item_default);
            }
            TetherUtils.showTertheringErrorDialog(getContext(), getPrefContext().getString(C0017R$string.tether_error_title, this.mChoiceItemValue), getPrefContext().getString(C0017R$string.tether_error_message, this.mChoiceItemValue));
            SwitchPreference switchPreference2 = this.mChoicePreference;
            if (switchPreference2 != null) {
                switchPreference2.setChecked(false);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void stopTethering() {
        SwitchPreference switchPreference;
        if (this.mCm != null && this.mChoiceItem >= 0 && (switchPreference = this.mChoicePreference) != null && switchPreference.isChecked()) {
            int i = this.mChoiceItem;
            if (i == 1) {
                this.mCm.stopTethering(1);
            } else if (i == 2) {
                this.mCm.stopTethering(2);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSimStatus(boolean z) {
        if (z) {
            boolean z2 = this.mUsbConnected && !this.mMassStorageActive;
            SwitchPreference switchPreference = this.mUsbTether;
            if (switchPreference != null && z2) {
                switchPreference.setEnabled(true);
            }
            SwitchPreference switchPreference2 = this.mBluetoothTether;
            if (switchPreference2 != null) {
                switchPreference2.setEnabled(true);
            }
        }
    }
}
