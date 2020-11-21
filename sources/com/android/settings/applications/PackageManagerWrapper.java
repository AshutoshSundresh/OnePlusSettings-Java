package com.android.settings.applications;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.List;

public class PackageManagerWrapper {
    private final PackageManager mPm;

    public PackageManagerWrapper(PackageManager packageManager) {
        this.mPm = packageManager;
    }

    public List<ResolveInfo> queryIntentActivities(Intent intent, int i) {
        return this.mPm.queryIntentActivities(intent, i);
    }
}
