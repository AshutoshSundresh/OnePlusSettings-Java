package com.oneplus.settings.defaultapp;

import android.content.Context;
import android.provider.Settings;

public class DataHelper {
    public static String getDefaultAppPackageName(Context context, String str) {
        return Settings.System.getString(context.getContentResolver(), str);
    }

    public static void setDefaultAppPackageName(Context context, String str, String str2) {
        Settings.System.putString(context.getContentResolver(), str, str2);
    }

    public static boolean isDefaultAppInited(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "op_default_app_init", 0) != 0;
    }

    public static void setDefaultAppInited(Context context) {
        Settings.System.putInt(context.getContentResolver(), "op_default_app_init", 1);
    }
}
