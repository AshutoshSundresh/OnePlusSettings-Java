package com.oneplus.common;

import android.os.Build;

public class SystemUtils {
    public static boolean isAtLeastM() {
        return Build.VERSION.SDK_INT >= 23;
    }
}
