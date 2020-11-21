package com.android.settings.wifi;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import com.android.internal.util.ArrayUtils;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;

public class AppStateChangeWifiStateBridge extends AppStateAppOpsBridge {
    public static final ApplicationsState.AppFilter FILTER_CHANGE_WIFI_STATE = new ApplicationsState.AppFilter() {
        /* class com.android.settings.wifi.AppStateChangeWifiStateBridge.AnonymousClass1 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            Object obj;
            if (appEntry == null || (obj = appEntry.extraInfo) == null || !(obj instanceof WifiSettingsState)) {
                return false;
            }
            WifiSettingsState wifiSettingsState = (WifiSettingsState) obj;
            PackageInfo packageInfo = wifiSettingsState.packageInfo;
            if (packageInfo == null || !ArrayUtils.contains(packageInfo.requestedPermissions, "android.permission.NETWORK_SETTINGS")) {
                return wifiSettingsState.permissionDeclared;
            }
            return false;
        }
    };
    private static final String[] PM_PERMISSIONS = {"android.permission.CHANGE_WIFI_STATE"};

    public AppStateChangeWifiStateBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(context, applicationsState, callback, 71, PM_PERMISSIONS);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = getWifiSettingsInfo(str, i);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateAppOpsBridge, com.android.settings.applications.AppStateBaseBridge
    public void loadAllExtraInfo() {
        for (ApplicationsState.AppEntry appEntry : this.mAppSession.getAllApps()) {
            ApplicationInfo applicationInfo = appEntry.info;
            updateExtraInfo(appEntry, applicationInfo.packageName, applicationInfo.uid);
        }
    }

    public WifiSettingsState getWifiSettingsInfo(String str, int i) {
        return new WifiSettingsState(super.getPermissionInfo(str, i));
    }

    public static class WifiSettingsState extends AppStateAppOpsBridge.PermissionState {
        public WifiSettingsState(AppStateAppOpsBridge.PermissionState permissionState) {
            super(permissionState.packageName, permissionState.userHandle);
            this.packageInfo = permissionState.packageInfo;
            this.appOpMode = permissionState.appOpMode;
            this.permissionDeclared = permissionState.permissionDeclared;
            this.staticPermissionGranted = permissionState.staticPermissionGranted;
        }
    }
}
