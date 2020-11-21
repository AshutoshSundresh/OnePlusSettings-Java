package com.android.settings.applications;

import android.content.Intent;
import java.util.List;
import java.util.Set;

public interface ApplicationFeatureProvider {

    public interface ListOfAppsCallback {
        void onListOfAppsResult(List<UserAppInfo> list);
    }

    public interface NumberOfAppsCallback {
        void onNumberOfAppsResult(int i);
    }

    void calculateNumberOfAppsWithAdminGrantedPermissions(String[] strArr, boolean z, NumberOfAppsCallback numberOfAppsCallback);

    void calculateNumberOfPolicyInstalledApps(boolean z, NumberOfAppsCallback numberOfAppsCallback);

    List<UserAppInfo> findPersistentPreferredActivities(int i, Intent[] intentArr);

    Set<String> getKeepEnabledPackages();

    default CharSequence getTimeSpentInApp(String str) {
        return null;
    }

    void listAppsWithAdminGrantedPermissions(String[] strArr, ListOfAppsCallback listOfAppsCallback);

    void listPolicyInstalledApps(ListOfAppsCallback listOfAppsCallback);
}
