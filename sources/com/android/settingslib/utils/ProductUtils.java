package com.android.settingslib.utils;

import android.util.OpFeatures;

public class ProductUtils {
    public static boolean isUsvMode() {
        return OpFeatures.isSupport(new int[]{239});
    }
}
