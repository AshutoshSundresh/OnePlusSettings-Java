package com.android.settings.applications;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserManager;

public abstract class InstalledAppLister extends AppLister {
    public InstalledAppLister(PackageManager packageManager, UserManager userManager) {
        super(packageManager, userManager);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppLister
    public boolean includeInCount(ApplicationInfo applicationInfo) {
        return InstalledAppCounter.includeInCount(1, this.mPm, applicationInfo);
    }
}
