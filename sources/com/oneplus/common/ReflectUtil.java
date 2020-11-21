package com.oneplus.common;

import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {
    public static boolean isFeatureSupported(String str) {
        try {
            Class<?> cls = Class.forName("android.util.OpFeatures");
            Method declaredMethod = cls.getDeclaredMethod("isSupport", int[].class);
            Field declaredField = cls.getDeclaredField(str);
            declaredMethod.setAccessible(true);
            declaredField.setAccessible(true);
            return ((Boolean) declaredMethod.invoke(null, new int[]{declaredField.getInt(null)})).booleanValue();
        } catch (Exception unused) {
            Log.i("isFeatureSupported", str + " is not supported");
            return false;
        }
    }
}
