package com.android.settings.dashboard.profileselector;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.location.LocationPersonalSettings;
import com.android.settings.location.LocationSwitchBarController;
import com.android.settings.location.LocationWorkProfileSettings;
import com.android.settings.widget.SwitchBar;

public class ProfileSelectLocationFragment extends ProfileSelectFragment {
    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SwitchBar switchBar = settingsActivity.getSwitchBar();
        int i = C0017R$string.location_settings_master_switch_title;
        switchBar.setSwitchBarText(i, i);
        new LocationSwitchBarController(settingsActivity, switchBar, getSettingsLifecycle());
        switchBar.show();
    }

    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    public Fragment[] getFragments() {
        Bundle bundle = new Bundle();
        bundle.putInt("profile", 2);
        LocationWorkProfileSettings locationWorkProfileSettings = new LocationWorkProfileSettings();
        locationWorkProfileSettings.setArguments(bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("profile", 1);
        LocationPersonalSettings locationPersonalSettings = new LocationPersonalSettings();
        locationPersonalSettings.setArguments(bundle2);
        return new Fragment[]{locationPersonalSettings, locationWorkProfileSettings};
    }
}
