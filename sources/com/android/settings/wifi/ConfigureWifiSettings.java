package com.android.settings.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import com.android.settings.C0005R$bool;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.p2p.WifiP2pPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.android.wifi.OpWifiCustomizeReader;
import com.oneplus.settings.controllers.OPPasspointPreferenceController;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class ConfigureWifiSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.wifi_configure_settings) {
        /* class com.android.settings.wifi.ConfigureWifiSettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return context.getResources().getBoolean(C0005R$bool.config_show_wifi_settings);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (OPUtils.isO2()) {
                nonIndexableKeys.add("wapi_cert_manage");
            }
            if (!OpWifiCustomizeReader.isSupportPasspoint() || OPUtils.isGuestMode()) {
                nonIndexableKeys.add(OPPasspointPreferenceController.KEY_ONEPLUS_PASSPOINT);
            }
            return nonIndexableKeys;
        }
    };
    private UseOpenWifiPreferenceController mUseOpenWifiPreferenceController;
    private WifiWakeupPreferenceController mWifiWakeupPreferenceController;

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getInitialExpandedChildCount() {
        return 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ConfigureWifiSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 338;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.wifi_configure_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        WifiManager wifiManager = (WifiManager) getSystemService("wifi");
        ArrayList arrayList = new ArrayList();
        this.mWifiWakeupPreferenceController = new WifiWakeupPreferenceController(context);
        this.mUseOpenWifiPreferenceController = new UseOpenWifiPreferenceController(context);
        arrayList.add(new WifiP2pPreferenceController(context, getSettingsLifecycle(), wifiManager));
        arrayList.add(new OPIntelligentlySelectBestWifiPreferenceController(context, getSettingsLifecycle()));
        arrayList.add(new OPWifiScanAlwaysAvailablePreferenceController(context, getSettingsLifecycle(), this.mWifiWakeupPreferenceController));
        arrayList.add(new OPPasspointPreferenceController(context, getSettingsLifecycle()));
        arrayList.add(new OPWapiCertManagePreferenceController(context));
        arrayList.add(new OPWifiInfoPreferenceController(context, getSettingsLifecycle(), wifiManager));
        return arrayList;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        WifiWakeupPreferenceController wifiWakeupPreferenceController = (WifiWakeupPreferenceController) use(WifiWakeupPreferenceController.class);
        this.mWifiWakeupPreferenceController = wifiWakeupPreferenceController;
        wifiWakeupPreferenceController.setFragment(this);
        UseOpenWifiPreferenceController useOpenWifiPreferenceController = (UseOpenWifiPreferenceController) use(UseOpenWifiPreferenceController.class);
        this.mUseOpenWifiPreferenceController = useOpenWifiPreferenceController;
        useOpenWifiPreferenceController.setFragment(this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 600) {
            this.mWifiWakeupPreferenceController.onActivityResult(i, i2);
        } else if (i == 400) {
            this.mUseOpenWifiPreferenceController.onActivityResult(i, i2);
        } else {
            super.onActivityResult(i, i2, intent);
        }
    }
}
