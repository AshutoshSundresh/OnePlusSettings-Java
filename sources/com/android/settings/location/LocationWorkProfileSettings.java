package com.android.settings.location;

import android.content.Context;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;

public class LocationWorkProfileSettings extends DashboardFragment {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "LocationWorkProfile";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1806;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.location_settings_workprofile;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AppLocationPermissionPreferenceController) use(AppLocationPermissionPreferenceController.class)).init(this);
        ((LocationServiceForWorkPreferenceController) use(LocationServiceForWorkPreferenceController.class)).init(this);
        ((LocationFooterPreferenceController) use(LocationFooterPreferenceController.class)).init(this);
        ((LocationForWorkPreferenceController) use(LocationForWorkPreferenceController.class)).init(this);
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
