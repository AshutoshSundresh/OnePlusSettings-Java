package com.android.settings.development;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.debug.IAdbManager;
import android.debug.PairDevice;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.development.AdbWirelessDialog;
import com.android.settings.development.WirelessDebuggingEnabler;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBarController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.widget.FooterPreference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class WirelessDebuggingFragment extends DashboardFragment implements WirelessDebuggingEnabler.OnEnabledListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.adb_wireless_settings) {
        /* class com.android.settings.development.WirelessDebuggingFragment.AnonymousClass2 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
        }
    };
    private static AdbIpAddressPreferenceController sAdbIpAddressPreferenceController;
    private IAdbManager mAdbManager;
    private Preference mCodePairingPreference;
    private int mConnectionPort;
    private Preference mDeviceNamePreference;
    private PreferenceCategory mFooterCategory;
    private IntentFilter mIntentFilter;
    private Preference mIpAddrPreference;
    private FooterPreference mOffMessagePreference;
    private Map<String, AdbPairedDevicePreference> mPairedDevicePreferences;
    private PreferenceCategory mPairedDevicesCategory;
    private AdbWirelessDialog mPairingCodeDialog;
    private final PairingCodeDialogListener mPairingCodeDialogListener = new PairingCodeDialogListener();
    private PreferenceCategory mPairingMethodsCategory;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.development.WirelessDebuggingFragment.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.android.server.adb.WIRELESS_DEBUG_PAIRED_DEVICES".equals(action)) {
                WirelessDebuggingFragment.this.updatePairedDevicePreferences((HashMap) intent.getSerializableExtra("devices_map"));
            } else if ("com.android.server.adb.WIRELESS_DEBUG_STATUS".equals(action)) {
                int intExtra = intent.getIntExtra("status", 5);
                if (intExtra == 4 || intExtra == 5) {
                    WirelessDebuggingFragment.sAdbIpAddressPreferenceController.updateState(WirelessDebuggingFragment.this.mIpAddrPreference);
                }
            } else if ("com.android.server.adb.WIRELESS_DEBUG_PAIRING_RESULT".equals(action)) {
                Integer valueOf = Integer.valueOf(intent.getIntExtra("status", 0));
                if (valueOf.equals(3)) {
                    String stringExtra = intent.getStringExtra("pairing_code");
                    if (WirelessDebuggingFragment.this.mPairingCodeDialog != null) {
                        WirelessDebuggingFragment.this.mPairingCodeDialog.getController().setPairingCode(stringExtra);
                    }
                } else if (valueOf.equals(1)) {
                    WirelessDebuggingFragment.this.removeDialog(0);
                    WirelessDebuggingFragment.this.mPairingCodeDialog = null;
                } else if (valueOf.equals(0)) {
                    WirelessDebuggingFragment.this.removeDialog(0);
                    WirelessDebuggingFragment.this.mPairingCodeDialog = null;
                    WirelessDebuggingFragment.this.showDialog(2);
                } else if (valueOf.equals(4)) {
                    int intExtra2 = intent.getIntExtra("adb_port", 0);
                    Log.i("WirelessDebuggingFrag", "Got pairing code port=" + intExtra2);
                    String str = WirelessDebuggingFragment.sAdbIpAddressPreferenceController.getIpv4Address() + ":" + intExtra2;
                    if (WirelessDebuggingFragment.this.mPairingCodeDialog != null) {
                        WirelessDebuggingFragment.this.mPairingCodeDialog.getController().setIpAddr(str);
                    }
                }
            }
        }
    };
    private WirelessDebuggingEnabler mWifiDebuggingEnabler;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return 1832;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "WirelessDebuggingFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1831;
    }

    class PairingCodeDialogListener implements AdbWirelessDialog.AdbWirelessDialogListener {
        PairingCodeDialogListener() {
        }

        @Override // com.android.settings.development.AdbWirelessDialog.AdbWirelessDialogListener
        public void onDismiss() {
            Log.i("WirelessDebuggingFrag", "onDismiss");
            WirelessDebuggingFragment.this.mPairingCodeDialog = null;
            try {
                WirelessDebuggingFragment.this.mAdbManager.disablePairing();
            } catch (RemoteException unused) {
                Log.e("WirelessDebuggingFrag", "Unable to cancel pairing");
            }
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AdbQrCodePreferenceController) use(AdbQrCodePreferenceController.class)).setParentFragment(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        this.mWifiDebuggingEnabler = new WirelessDebuggingEnabler(settingsActivity, new SwitchBarController(settingsActivity.getSwitchBar()), this, getSettingsLifecycle());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferences();
        IntentFilter intentFilter = new IntentFilter("com.android.server.adb.WIRELESS_DEBUG_PAIRED_DEVICES");
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("com.android.server.adb.WIRELESS_DEBUG_STATUS");
        this.mIntentFilter.addAction("com.android.server.adb.WIRELESS_DEBUG_PAIRING_RESULT");
    }

    private void addPreferences() {
        this.mDeviceNamePreference = findPreference("adb_device_name_pref");
        this.mIpAddrPreference = findPreference("adb_ip_addr_pref");
        this.mPairingMethodsCategory = (PreferenceCategory) findPreference("adb_pairing_methods_category");
        Preference findPreference = findPreference("adb_pair_method_code_pref");
        this.mCodePairingPreference = findPreference;
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.development.$$Lambda$WirelessDebuggingFragment$VSfpfFdzqTI_GZWxi9rHtRVtkpY */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return WirelessDebuggingFragment.this.lambda$addPreferences$0$WirelessDebuggingFragment(preference);
            }
        });
        this.mPairedDevicesCategory = (PreferenceCategory) findPreference("adb_paired_devices_category");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("adb_wireless_footer_category");
        this.mFooterCategory = preferenceCategory;
        this.mOffMessagePreference = new FooterPreference(preferenceCategory.getContext());
        this.mOffMessagePreference.setTitle(getText(C0017R$string.adb_wireless_list_empty_off));
        this.mFooterCategory.addPreference(this.mOffMessagePreference);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addPreferences$0 */
    public /* synthetic */ boolean lambda$addPreferences$0$WirelessDebuggingFragment(Preference preference) {
        showDialog(0);
        return true;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mWifiDebuggingEnabler.teardownSwitchController();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.mReceiver, this.mIntentFilter);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        removeDialog(0);
        getActivity().unregisterReceiver(this.mReceiver);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 0) {
            handlePairedDeviceRequest(i2, intent);
        } else if (i == 1) {
            handlePairingDeviceRequest(i2, intent);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        AdbWirelessDialog createModal = AdbWirelessDialog.createModal(getActivity(), i == 0 ? this.mPairingCodeDialogListener : null, i);
        if (i == 0) {
            this.mPairingCodeDialog = createModal;
            try {
                this.mAdbManager.enablePairingByPairingCode();
            } catch (RemoteException unused) {
                Log.e("WirelessDebuggingFrag", "Unable to enable pairing");
                this.mPairingCodeDialog = null;
                createModal = AdbWirelessDialog.createModal(getActivity(), null, 2);
            }
        }
        if (createModal != null) {
            return createModal;
        }
        return super.onCreateDialog(i);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.adb_wireless_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getActivity(), this, getSettingsLifecycle());
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Activity activity, WirelessDebuggingFragment wirelessDebuggingFragment, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        AdbIpAddressPreferenceController adbIpAddressPreferenceController = new AdbIpAddressPreferenceController(context, lifecycle);
        sAdbIpAddressPreferenceController = adbIpAddressPreferenceController;
        arrayList.add(adbIpAddressPreferenceController);
        return arrayList;
    }

    @Override // com.android.settings.development.WirelessDebuggingEnabler.OnEnabledListener
    public void onEnabled(boolean z) {
        if (z) {
            showDebuggingPreferences();
            IAdbManager asInterface = IAdbManager.Stub.asInterface(ServiceManager.getService("adb"));
            this.mAdbManager = asInterface;
            try {
                updatePairedDevicePreferences(asInterface.getPairedDevices());
                int adbWirelessPort = this.mAdbManager.getAdbWirelessPort();
                this.mConnectionPort = adbWirelessPort;
                if (adbWirelessPort > 0) {
                    Log.i("WirelessDebuggingFrag", "onEnabled(): connect_port=" + this.mConnectionPort);
                }
            } catch (RemoteException unused) {
                Log.e("WirelessDebuggingFrag", "Unable to request the paired list for Adb wireless");
            }
            sAdbIpAddressPreferenceController.updateState(this.mIpAddrPreference);
            return;
        }
        showOffMessage();
    }

    private void showOffMessage() {
        this.mDeviceNamePreference.setVisible(false);
        this.mIpAddrPreference.setVisible(false);
        this.mPairingMethodsCategory.setVisible(false);
        this.mPairedDevicesCategory.setVisible(false);
        this.mFooterCategory.setVisible(true);
    }

    private void showDebuggingPreferences() {
        this.mDeviceNamePreference.setVisible(true);
        this.mIpAddrPreference.setVisible(true);
        this.mPairingMethodsCategory.setVisible(true);
        this.mPairedDevicesCategory.setVisible(true);
        this.mFooterCategory.setVisible(false);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updatePairedDevicePreferences(Map<String, PairDevice> map) {
        if (map == null) {
            this.mPairedDevicesCategory.removeAll();
            return;
        }
        if (this.mPairedDevicePreferences == null) {
            this.mPairedDevicePreferences = new HashMap();
        }
        if (this.mPairedDevicePreferences.isEmpty()) {
            for (Map.Entry<String, PairDevice> entry : map.entrySet()) {
                AdbPairedDevicePreference adbPairedDevicePreference = new AdbPairedDevicePreference(entry.getValue(), this.mPairedDevicesCategory.getContext());
                this.mPairedDevicePreferences.put(entry.getKey(), adbPairedDevicePreference);
                adbPairedDevicePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    /* class com.android.settings.development.$$Lambda$WirelessDebuggingFragment$_GS84t9j_sWbtkE5uHAMipVNgI */

                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return WirelessDebuggingFragment.this.lambda$updatePairedDevicePreferences$1$WirelessDebuggingFragment(preference);
                    }
                });
                this.mPairedDevicesCategory.addPreference(adbPairedDevicePreference);
            }
            return;
        }
        this.mPairedDevicePreferences.entrySet().removeIf(new Predicate(map) {
            /* class com.android.settings.development.$$Lambda$WirelessDebuggingFragment$KlJSDXCVDHOWqAykWYffj2twmA */
            public final /* synthetic */ Map f$1;

            {
                this.f$1 = r2;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return WirelessDebuggingFragment.this.lambda$updatePairedDevicePreferences$2$WirelessDebuggingFragment(this.f$1, (Map.Entry) obj);
            }
        });
        for (Map.Entry<String, PairDevice> entry2 : map.entrySet()) {
            if (this.mPairedDevicePreferences.get(entry2.getKey()) == null) {
                AdbPairedDevicePreference adbPairedDevicePreference2 = new AdbPairedDevicePreference(entry2.getValue(), this.mPairedDevicesCategory.getContext());
                this.mPairedDevicePreferences.put(entry2.getKey(), adbPairedDevicePreference2);
                adbPairedDevicePreference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    /* class com.android.settings.development.$$Lambda$WirelessDebuggingFragment$Tif4ZCCSdhH6GOBDbHbvgJiWs0 */

                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return WirelessDebuggingFragment.this.lambda$updatePairedDevicePreferences$3$WirelessDebuggingFragment(preference);
                    }
                });
                this.mPairedDevicesCategory.addPreference(adbPairedDevicePreference2);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updatePairedDevicePreferences$1 */
    public /* synthetic */ boolean lambda$updatePairedDevicePreferences$1$WirelessDebuggingFragment(Preference preference) {
        launchPairedDeviceDetailsFragment((AdbPairedDevicePreference) preference);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updatePairedDevicePreferences$2 */
    public /* synthetic */ boolean lambda$updatePairedDevicePreferences$2$WirelessDebuggingFragment(Map map, Map.Entry entry) {
        if (map.get(entry.getKey()) == null) {
            this.mPairedDevicesCategory.removePreference((Preference) entry.getValue());
            return true;
        }
        AdbPairedDevicePreference adbPairedDevicePreference = (AdbPairedDevicePreference) entry.getValue();
        adbPairedDevicePreference.setPairedDevice((PairDevice) map.get(entry.getKey()));
        adbPairedDevicePreference.refresh();
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updatePairedDevicePreferences$3 */
    public /* synthetic */ boolean lambda$updatePairedDevicePreferences$3$WirelessDebuggingFragment(Preference preference) {
        launchPairedDeviceDetailsFragment((AdbPairedDevicePreference) preference);
        return true;
    }

    private void launchPairedDeviceDetailsFragment(AdbPairedDevicePreference adbPairedDevicePreference) {
        adbPairedDevicePreference.savePairedDeviceToExtras(adbPairedDevicePreference.getExtras());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setTitleRes(C0017R$string.adb_wireless_device_details_title);
        subSettingLauncher.setDestination(AdbDeviceDetailsFragment.class.getName());
        subSettingLauncher.setArguments(adbPairedDevicePreference.getExtras());
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setResultListener(this, 0);
        subSettingLauncher.launch();
    }

    /* access modifiers changed from: package-private */
    public void handlePairedDeviceRequest(int i, Intent intent) {
        if (i == -1) {
            Log.i("WirelessDebuggingFrag", "Processing paired device request");
            if (intent.getIntExtra("request_type", -1) == 0) {
                try {
                    this.mAdbManager.unpairDevice(intent.getParcelableExtra("paired_device").getGuid());
                } catch (RemoteException unused) {
                    Log.e("WirelessDebuggingFrag", "Unable to forget the device");
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void handlePairingDeviceRequest(int i, Intent intent) {
        if (i == -1) {
            if (intent.getIntExtra("request_type_pairing", -1) != 1) {
                Log.d("WirelessDebuggingFrag", "Successfully paired device");
            } else {
                showDialog(2);
            }
        }
    }
}
