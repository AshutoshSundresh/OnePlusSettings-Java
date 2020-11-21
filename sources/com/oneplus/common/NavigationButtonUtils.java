package com.oneplus.common;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class NavigationButtonUtils {
    public static boolean is3ButtonNavigationBar(Context context) {
        return getSystemIntegerRes(context, "config_navBarInteractionMode") == 0;
    }

    public static int getSystemIntegerRes(Context context, String str) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier(str, "integer", "android");
        if (identifier != 0) {
            Log.e("NavigationButtonUtils", "res.getInteger(resId) = " + resources.getInteger(identifier));
            return resources.getInteger(identifier);
        }
        Log.e("NavigationButtonUtils", "Failed to get system resource ID. Incompatible framework version?");
        return -1;
    }
}
