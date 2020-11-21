package com.android.settings.applications;

import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;

public class AppStateInstallAppsBridge extends AppStateBaseBridge {
    public static final ApplicationsState.AppFilter FILTER_APP_SOURCES = new ApplicationsState.AppFilter() {
        /* class com.android.settings.applications.AppStateInstallAppsBridge.AnonymousClass1 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            Object obj = appEntry.extraInfo;
            if (obj == null || !(obj instanceof InstallAppsState) || UserHandle.getUserId(appEntry.info.uid) == 999) {
                return false;
            }
            return ((InstallAppsState) appEntry.extraInfo).isPotentialAppSource();
        }
    };
    private static final String TAG = "AppStateInstallAppsBridge";
    private final AppOpsManager mAppOpsManager;
    private final IPackageManager mIpm = AppGlobals.getPackageManager();

    public AppStateInstallAppsBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = createInstallAppsStateFor(str, i);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        for (int i = 0; i < allApps.size(); i++) {
            ApplicationsState.AppEntry appEntry = allApps.get(i);
            ApplicationInfo applicationInfo = appEntry.info;
            updateExtraInfo(appEntry, applicationInfo.packageName, applicationInfo.uid);
        }
    }

    private boolean hasRequestedAppOpPermission(String str, String str2) {
        try {
            return ArrayUtils.contains(this.mIpm.getAppOpPermissionPackages(str), str2);
        } catch (RemoteException unused) {
            Log.e(TAG, "PackageManager dead. Cannot get permission info");
            return false;
        }
    }

    private int getAppOpMode(int i, int i2, String str) {
        return this.mAppOpsManager.checkOpNoThrow(i, i2, str);
    }

    private boolean isSystemApp(int i) {
        return !UserHandle.isApp(i);
    }

    public InstallAppsState createInstallAppsStateFor(String str, int i) {
        int uid;
        int appOpMode;
        int i2;
        InstallAppsState installAppsState = new InstallAppsState();
        installAppsState.permissionRequested = hasRequestedAppOpPermission("android.permission.REQUEST_INSTALL_PACKAGES", str);
        installAppsState.appOpMode = getAppOpMode(66, i, str);
        if (UserHandle.getUserId(i) == 0 && (i2 = installAppsState.appOpMode) != (appOpMode = getAppOpMode(66, (uid = UserHandle.getUid(999, UserHandle.getAppId(i))), str))) {
            if (i2 == 3) {
                try {
                    this.mAppOpsManager.setMode(66, i, str, appOpMode);
                } catch (Exception e) {
                    String str2 = TAG;
                    Log.e(str2, "mAppOpsManager.setMode error 1 :" + e.getMessage());
                    e.printStackTrace();
                }
                installAppsState.appOpMode = getAppOpMode(66, i, str);
            } else {
                try {
                    this.mAppOpsManager.setMode(66, uid, str, i2);
                } catch (Exception e2) {
                    String str3 = TAG;
                    Log.e(str3, "mAppOpsManager.setMode error 2 :" + e2.getMessage());
                    e2.printStackTrace();
                }
            }
        }
        installAppsState.isSystemApp = isSystemApp(i);
        return installAppsState;
    }

    public static class InstallAppsState {
        int appOpMode = 3;
        boolean isSystemApp = false;
        boolean permissionRequested;

        public boolean canInstallApps() {
            return this.appOpMode == 0;
        }

        public boolean isPotentialAppSource() {
            if (this.isSystemApp) {
                return false;
            }
            if (this.appOpMode != 3 || this.permissionRequested) {
                return true;
            }
            return false;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[permissionRequested: " + this.permissionRequested);
            sb.append(", appOpMode: " + this.appOpMode);
            sb.append("]");
            return sb.toString();
        }
    }
}
