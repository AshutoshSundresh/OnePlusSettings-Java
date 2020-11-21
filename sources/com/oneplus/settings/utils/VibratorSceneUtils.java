package com.oneplus.settings.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.provider.Settings;
import java.lang.reflect.Method;

public class VibratorSceneUtils {
    public static long[] getVibratorScenePattern(Context context, Vibrator vibrator, int i) {
        int i2 = new int[]{-1, -2, -3}[Settings.System.getInt(context.getContentResolver(), "vibrate_on_touch_intensity", 0)];
        if (vibrator == null) {
            return null;
        }
        try {
            Method declaredMethod = vibrator.getClass().getDeclaredMethod("setVibratorEffect", Integer.TYPE);
            if (declaredMethod == null) {
                return null;
            }
            declaredMethod.setAccessible(true);
            return new long[]{(long) i2, 0, (long) ((Integer) declaredMethod.invoke(vibrator, Integer.valueOf(i))).intValue()};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint({"MissingPermission"})
    public static void vibrateIfNeeded(long[] jArr, Vibrator vibrator) {
        if (vibrator != null && jArr != null) {
            vibrator.vibrate(jArr, -1);
        }
    }

    public static boolean systemVibrateEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "haptic_feedback_enabled", 0) == 1;
    }
}
