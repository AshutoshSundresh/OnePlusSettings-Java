package com.google.android.material.indicator.utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class DensityUtils {
    public static int dpToPx(int i) {
        return (int) TypedValue.applyDimension(1, (float) i, Resources.getSystem().getDisplayMetrics());
    }
}
