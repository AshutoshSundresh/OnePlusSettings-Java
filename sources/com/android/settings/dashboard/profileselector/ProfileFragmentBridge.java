package com.android.settings.dashboard.profileselector;

import android.util.ArrayMap;
import com.android.settings.accounts.AccountDashboardFragment;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.location.LocationSettings;
import com.android.settings.location.RecentLocationRequestSeeAllFragment;
import java.util.Map;

public class ProfileFragmentBridge {
    public static final Map<String, String> FRAGMENT_MAP;

    static {
        ArrayMap arrayMap = new ArrayMap();
        FRAGMENT_MAP = arrayMap;
        arrayMap.put(AccountDashboardFragment.class.getName(), ProfileSelectAccountFragment.class.getName());
        FRAGMENT_MAP.put(ManageApplications.class.getName(), ProfileSelectManageApplications.class.getName());
        FRAGMENT_MAP.put(LocationSettings.class.getName(), ProfileSelectLocationFragment.class.getName());
        FRAGMENT_MAP.put(RecentLocationRequestSeeAllFragment.class.getName(), ProfileSelectRecentLocationRequestFragment.class.getName());
    }
}
