package com.android.settings.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.util.Log;

public class AppStoreUtil {
    private static Intent resolveIntent(Context context, Intent intent) {
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 0);
        if (resolveActivity == null) {
            return null;
        }
        Intent intent2 = new Intent(intent.getAction());
        ActivityInfo activityInfo = resolveActivity.activityInfo;
        return intent2.setClassName(activityInfo.packageName, activityInfo.name);
    }

    public static String getInstallerPackageName(Context context, String str) {
        try {
            return context.getPackageManager().getInstallerPackageName(str);
        } catch (IllegalArgumentException e) {
            Log.e("AppStoreUtil", "Exception while retrieving the package installer of " + str, e);
            return null;
        }
    }

    public static Intent getAppStoreLink(Context context, String str, String str2) {
        Intent resolveIntent = resolveIntent(context, new Intent("android.intent.action.SHOW_APP_INFO").setPackage(str));
        if (resolveIntent == null) {
            return null;
        }
        resolveIntent.putExtra("android.intent.extra.PACKAGE_NAME", str2);
        return resolveIntent;
    }

    public static Intent getAppStoreLink(Context context, String str) {
        return getAppStoreLink(context, getInstallerPackageName(context, str), str);
    }
}
