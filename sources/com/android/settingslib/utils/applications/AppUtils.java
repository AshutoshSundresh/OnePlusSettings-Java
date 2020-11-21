package com.android.settingslib.utils.applications;

import android.content.pm.PackageManager;
import android.util.Log;

public class AppUtils {
    private static final String TAG = "AppUtils";

    public static CharSequence getApplicationLabel(PackageManager packageManager, String str) {
        try {
            return packageManager.getApplicationInfo(str, 4194816).loadLabel(packageManager);
        } catch (PackageManager.NameNotFoundException unused) {
            String str2 = TAG;
            Log.w(str2, "Unable to find info for package: " + str);
            return null;
        }
    }
}
