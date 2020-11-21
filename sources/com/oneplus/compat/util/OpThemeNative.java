package com.oneplus.compat.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.HashMap;

public class OpThemeNative {
    public static void enableTheme(Context context, HashMap<String, String> hashMap) {
        try {
            Intent intent = new Intent("android.settings.oneplus_theme_enable");
            intent.putExtra("category_map", hashMap);
            intent.addFlags(268435456);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e("OpTheme", "Exception " + e);
        }
    }

    public static void disableTheme(Context context, HashMap<String, String> hashMap) {
        try {
            Intent intent = new Intent("android.settings.oneplus_theme_disable");
            intent.putExtra("category_map", hashMap);
            intent.addFlags(268435456);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e("OpTheme", "Exception " + e);
        }
    }
}
