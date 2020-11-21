package com.android.settings.applications;

import android.content.Context;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;

public class AppStateWriteSettingsBridge extends AppStateAppOpsBridge {
    public static final ApplicationsState.AppFilter FILTER_WRITE_SETTINGS = new ApplicationsState.AppFilter() {
        /* class com.android.settings.applications.AppStateWriteSettingsBridge.AnonymousClass1 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            return appEntry.extraInfo != null;
        }
    };
    private static final String[] PM_PERMISSIONS = {"android.permission.WRITE_SETTINGS"};

    public AppStateWriteSettingsBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(context, applicationsState, callback, 23, PM_PERMISSIONS);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = getWriteSettingsInfo(str, i);
    }

    public WriteSettingsState getWriteSettingsInfo(String str, int i) {
        return new WriteSettingsState(super.getPermissionInfo(str, i));
    }

    public static class WriteSettingsState extends AppStateAppOpsBridge.PermissionState {
        public WriteSettingsState(AppStateAppOpsBridge.PermissionState permissionState) {
            super(permissionState.packageName, permissionState.userHandle);
            this.packageInfo = permissionState.packageInfo;
            this.appOpMode = permissionState.appOpMode;
            this.permissionDeclared = permissionState.permissionDeclared;
            this.staticPermissionGranted = permissionState.staticPermissionGranted;
        }
    }
}
