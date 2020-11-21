package com.android.settings.applications;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.UserHandle;

public abstract class AppWithAdminGrantedPermissionsCounter extends AppCounter {
    private final DevicePolicyManager mDevicePolicyManager;
    private final IPackageManager mPackageManagerService;
    private final String[] mPermissions;

    public AppWithAdminGrantedPermissionsCounter(Context context, String[] strArr, PackageManager packageManager, IPackageManager iPackageManager, DevicePolicyManager devicePolicyManager) {
        super(context, packageManager);
        this.mPermissions = strArr;
        this.mPackageManagerService = iPackageManager;
        this.mDevicePolicyManager = devicePolicyManager;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppCounter
    public boolean includeInCount(ApplicationInfo applicationInfo) {
        return includeInCount(this.mPermissions, this.mDevicePolicyManager, this.mPm, this.mPackageManagerService, applicationInfo);
    }

    public static boolean includeInCount(String[] strArr, DevicePolicyManager devicePolicyManager, PackageManager packageManager, IPackageManager iPackageManager, ApplicationInfo applicationInfo) {
        if (applicationInfo.targetSdkVersion >= 23) {
            for (String str : strArr) {
                if (devicePolicyManager.getPermissionGrantState(null, applicationInfo.packageName, str) == 1) {
                    return true;
                }
            }
            return false;
        } else if (packageManager.getInstallReason(applicationInfo.packageName, new UserHandle(UserHandle.getUserId(applicationInfo.uid))) != 1) {
            return false;
        } else {
            try {
                for (String str2 : strArr) {
                    if (iPackageManager.checkUidPermission(str2, applicationInfo.uid) == 0) {
                        return true;
                    }
                }
            } catch (RemoteException unused) {
            }
            return false;
        }
    }
}
