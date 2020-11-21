package com.oneplus.settings.ringtone;

import android.util.Log;

public class OPMyLog {
    public static void d(String str, String str2) {
    }

    public static void e(String str, String str2) {
        Log.e("chenhl", "[" + str + "] " + str2);
    }

    public static void e(String str, String str2, Throwable th) {
        Log.e("chenhl", "[" + str + "] " + str2, th);
    }
}
