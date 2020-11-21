package com.android.settings.dashboard.profileselector;

import androidx.fragment.app.Fragment;
import com.android.settings.accounts.AccountPersonalDashboardFragment;
import com.android.settings.accounts.AccountWorkProfileDashboardFragment;

public class ProfileSelectAccountFragment extends ProfileSelectFragment {
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    public Fragment[] getFragments() {
        return new Fragment[]{new AccountPersonalDashboardFragment(), new AccountWorkProfileDashboardFragment()};
    }
}
