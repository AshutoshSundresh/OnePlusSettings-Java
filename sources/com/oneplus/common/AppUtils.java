package com.oneplus.common;

import android.content.Context;
import android.util.TypedValue;

public abstract class AppUtils {
    public static float dpToPx(Context context, int i) {
        return TypedValue.applyDimension(1, (float) i, context.getResources().getDisplayMetrics());
    }
}
