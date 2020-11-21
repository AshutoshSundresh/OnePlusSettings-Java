package com.oneplus.accountsdk.utils;

import android.util.Log;

public class OnePlusAuthLogUtils {
    public static void e(String str, Object... objArr) {
        if (!(objArr == null || objArr.length == 0)) {
            str = String.format(str, objArr);
        }
        Log.e("OPAccountSDK", str);
    }
}
