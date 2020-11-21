package com.android.settingslib.utils;

import android.content.Context;
import android.content.res.TypedArray;

public class ColorUtil {
    public static float getDisabledAlpha(Context context) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{16842803});
        float f = obtainStyledAttributes.getFloat(0, 0.0f);
        obtainStyledAttributes.recycle();
        return f;
    }
}
