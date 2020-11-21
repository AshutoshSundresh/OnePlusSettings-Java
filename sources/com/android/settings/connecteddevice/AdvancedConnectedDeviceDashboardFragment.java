package com.android.settings.connecteddevice;

import android.content.Context;
import android.provider.SearchIndexableResource;
import androidx.fragment.app.Fragment;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.specialaccess.DefaultPaymentSettingsPreferenceController;
import com.android.settings.bluetooth.OPBluetoothSwitchPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.nfc.AndroidBeamPreferenceController;
import com.android.settings.print.PrintSettingPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedConnectedDeviceDashboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.connecteddevice.AdvancedConnectedDeviceDashboardFragment.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.connected_devices_advanced;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!context.getPackageManager().hasSystemFeature("android.hardware.nfc")) {
                nonIndexableKeys.add(AndroidBeamPreferenceController.KEY_ANDROID_BEAM_SETTINGS);
            }
            if (!OPUtils.isAppExist(context, "com.oneplus.share")) {
                nonIndexableKeys.add("oneplus_share_settings");
            }
            return nonIndexableKeys;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return AdvancedConnectedDeviceDashboardFragment.buildControllers(context, null, null);
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AdvancedConnectedDeviceFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1264;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_connected_devices;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.connected_devices_advanced;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((OPUSBConnectedDeviceGroupController) use(OPUSBConnectedDeviceGroupController.class)).init(this);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildControllers(context, getSettingsLifecycle(), this);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildControllers(Context context, Lifecycle lifecycle, Fragment fragment) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new OPBluetoothSwitchPreferenceController(context, lifecycle));
        DefaultPaymentSettingsPreferenceController defaultPaymentSettingsPreferenceController = new DefaultPaymentSettingsPreferenceController(context, "default_payment_app");
        if (fragment != null) {
            defaultPaymentSettingsPreferenceController.setFragment(fragment);
        }
        arrayList.add(defaultPaymentSettingsPreferenceController);
        PrintSettingPreferenceController printSettingPreferenceController = new PrintSettingPreferenceController(context);
        if (lifecycle != null) {
            lifecycle.addObserver(printSettingPreferenceController);
        }
        arrayList.add(printSettingPreferenceController);
        return arrayList;
    }
}
