package com.android.settings.applications.specialaccess.deviceadmin;

import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class DeviceAdminSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.device_admin_settings);

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DeviceAdminSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 516;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.device_admin_settings;
    }
}
