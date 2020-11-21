package com.oneplus.settings.highpowerapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PackageUtils {
    public static boolean isSystemApplication(Context context, String str) {
        if (context == null) {
            return false;
        }
        return isSystemApplication(context.getPackageManager(), str);
    }

    public static boolean isSystemApplication(PackageManager packageManager, String str) {
        if (!(packageManager == null || str == null || str.length() == 0)) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
                if (applicationInfo == null || (applicationInfo.flags & 1) <= 0) {
                    return false;
                }
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
