package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.AppPreference;
import com.android.settingslib.location.RecentLocationApps;
import java.util.ArrayList;

public class RecentLocationRequestPreferenceController extends LocationBasePreferenceController {
    public static final int MAX_APPS = 3;
    private PreferenceCategory mCategoryRecentLocationRequests;
    RecentLocationApps mRecentLocationApps;
    private int mType = 3;

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    /* access modifiers changed from: package-private */
    public static class PackageEntryClickedListener implements Preference.OnPreferenceClickListener {
        private final DashboardFragment mFragment;
        private final String mPackage;
        private final UserHandle mUserHandle;

        public PackageEntryClickedListener(DashboardFragment dashboardFragment, String str, UserHandle userHandle) {
            this.mFragment = dashboardFragment;
            this.mPackage = str;
            this.mUserHandle = userHandle;
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Bundle bundle = new Bundle();
            bundle.putString("package", this.mPackage);
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mFragment.getContext());
            subSettingLauncher.setDestination(AppInfoDashboardFragment.class.getName());
            subSettingLauncher.setArguments(bundle);
            subSettingLauncher.setTitleRes(C0017R$string.application_info_label);
            subSettingLauncher.setUserHandle(this.mUserHandle);
            subSettingLauncher.setSourceMetricsCategory(this.mFragment.getMetricsCategory());
            subSettingLauncher.launch();
            return true;
        }
    }

    public RecentLocationRequestPreferenceController(Context context, String str) {
        super(context, str);
        this.mRecentLocationApps = new RecentLocationApps(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mCategoryRecentLocationRequests = preferenceCategory;
        Context context = preferenceCategory.getContext();
        ArrayList<RecentLocationApps.Request> arrayList = new ArrayList();
        UserManager userManager = UserManager.get(this.mContext);
        for (RecentLocationApps.Request request : this.mRecentLocationApps.getAppListSorted(false)) {
            if (isRequestMatchesProfileType(userManager, request, this.mType)) {
                arrayList.add(request);
                if (arrayList.size() == 3) {
                    break;
                }
            }
        }
        if (arrayList.size() > 0) {
            for (RecentLocationApps.Request request2 : arrayList) {
                this.mCategoryRecentLocationRequests.addPreference(createAppPreference(context, request2, this.mFragment));
            }
            return;
        }
        AppPreference appPreference = new AppPreference(context);
        appPreference.setTitle(C0017R$string.location_no_recent_apps);
        appPreference.setSelectable(false);
        this.mCategoryRecentLocationRequests.addPreference(appPreference);
    }

    @Override // com.android.settings.location.LocationEnabler.LocationModeChangeListener, com.android.settings.location.LocationBasePreferenceController
    public void onLocationModeChanged(int i, boolean z) {
        this.mCategoryRecentLocationRequests.setEnabled(this.mLocationEnabler.isEnabled(i));
    }

    public void setProfileType(int i) {
        this.mType = i;
    }

    public static AppPreference createAppPreference(Context context, RecentLocationApps.Request request, DashboardFragment dashboardFragment) {
        AppPreference appPreference = new AppPreference(context);
        appPreference.setIcon(request.icon);
        appPreference.setTitle(request.label);
        appPreference.setOnPreferenceClickListener(new PackageEntryClickedListener(dashboardFragment, request.packageName, request.userHandle));
        return appPreference;
    }

    public static boolean isRequestMatchesProfileType(UserManager userManager, RecentLocationApps.Request request, int i) {
        boolean isManagedProfile = userManager.isManagedProfile(request.userHandle.getIdentifier());
        if (!isManagedProfile || (i & 2) == 0) {
            return !isManagedProfile && (i & 1) != 0;
        }
        return true;
    }
}
