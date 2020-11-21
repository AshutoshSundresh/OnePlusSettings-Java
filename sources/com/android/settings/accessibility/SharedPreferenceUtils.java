package com.android.settings.accessibility;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public final class SharedPreferenceUtils {
    private static SharedPreferences getSharedPreferences(Context context, String str) {
        return context.getSharedPreferences(str, 0);
    }

    public static Set<String> getUserShortcutTypes(Context context) {
        return getSharedPreferences(context, "accessibility_prefs").getStringSet("user_shortcut_type", ImmutableSet.of());
    }

    public static void setUserShortcutType(Context context, Set<String> set) {
        SharedPreferences.Editor edit = getSharedPreferences(context, "accessibility_prefs").edit();
        edit.remove("user_shortcut_type").apply();
        edit.putStringSet("user_shortcut_type", set).apply();
    }
}
