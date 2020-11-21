package com.oneplus.security.utils;

import android.util.Log;

public class LogUtils {
    public static boolean isLogEnabled = true;

    public static void d(String str, String str2) {
        if (isLogEnabled) {
            Log.d(str, str2);
        }
    }

    public static void e(String str, String str2) {
        if (isLogEnabled) {
            Log.e(str, str2);
        }
    }

    public static void i(String str, String str2) {
        if (isLogEnabled) {
            Log.i(str, str2);
        }
    }

    public static void w(String str, String str2) {
        if (isLogEnabled) {
            Log.w(str, str2);
        }
    }
}
