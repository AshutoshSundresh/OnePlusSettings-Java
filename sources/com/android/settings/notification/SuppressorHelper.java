package com.android.settings.notification;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.util.Log;

public class SuppressorHelper {
    public static String getSuppressionText(Context context, ComponentName componentName) {
        if (componentName == null) {
            return null;
        }
        return context.getString(17040662, getSuppressorCaption(context, componentName));
    }

    static String getSuppressorCaption(Context context, ComponentName componentName) {
        CharSequence loadLabel;
        PackageManager packageManager = context.getPackageManager();
        try {
            ServiceInfo serviceInfo = packageManager.getServiceInfo(componentName, 0);
            if (!(serviceInfo == null || (loadLabel = serviceInfo.loadLabel(packageManager)) == null)) {
                String trim = loadLabel.toString().trim();
                if (trim.length() > 0) {
                    return trim;
                }
            }
        } catch (Throwable th) {
            Log.w("SuppressorHelper", "Error loading suppressor caption", th);
        }
        return componentName.getPackageName();
    }
}
