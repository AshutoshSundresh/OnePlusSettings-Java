package com.android.settings.location;

import android.content.Context;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;

public class LocationPersonalSettings extends DashboardFragment {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "LocationPersonal";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 63;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.location_settings_personal;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AppLocationPermissionPreferenceController) use(AppLocationPermissionPreferenceController.class)).init(this);
        ((LocationServicePreferenceController) use(LocationServicePreferenceController.class)).init(this);
        ((LocationFooterPreferenceController) use(LocationFooterPreferenceController.class)).init(this);
        int i = getArguments().getInt("profile");
        RecentLocationRequestPreferenceController recentLocationRequestPreferenceController = (RecentLocationRequestPreferenceController) use(RecentLocationRequestPreferenceController.class);
        recentLocationRequestPreferenceController.init(this);
        recentLocationRequestPreferenceController.setProfileType(i);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_location_access;
    }
}
