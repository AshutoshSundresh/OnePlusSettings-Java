package com.android.settings.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.UserHandle;
import java.util.List;

public abstract class InstalledAppCounter extends AppCounter {
    private final int mInstallReason;

    public InstalledAppCounter(Context context, int i, PackageManager packageManager) {
        super(context, packageManager);
        this.mInstallReason = i;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppCounter
    public boolean includeInCount(ApplicationInfo applicationInfo) {
        return includeInCount(this.mInstallReason, this.mPm, applicationInfo);
    }

    public static boolean includeInCount(int i, PackageManager packageManager, ApplicationInfo applicationInfo) {
        int userId = UserHandle.getUserId(applicationInfo.uid);
        if (i != -1 && packageManager.getInstallReason(applicationInfo.packageName, new UserHandle(userId)) != i) {
            return false;
        }
        int i2 = applicationInfo.flags;
        if ((i2 & 128) != 0 || (i2 & 1) == 0) {
            return true;
        }
        List queryIntentActivitiesAsUser = packageManager.queryIntentActivitiesAsUser(new Intent("android.intent.action.MAIN", (Uri) null).addCategory("android.intent.category.LAUNCHER").setPackage(applicationInfo.packageName), 786944, userId);
        if (queryIntentActivitiesAsUser == null || queryIntentActivitiesAsUser.size() == 0) {
            return false;
        }
        return true;
    }
}
