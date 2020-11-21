package com.oneplus.inner.os;

import android.os.SystemProperties;

public class SystemPropertiesWrapper {
    public static String get(String str) {
        return SystemProperties.get(str);
    }
}
