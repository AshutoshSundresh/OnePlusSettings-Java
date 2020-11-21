package com.oneplus.settings.utils;

import android.content.SharedPreferences;
import com.oneplus.settings.SettingsBaseApplication;

public class OPPrefUtil {
    private static SharedPreferences getSharedPreferences() {
        return SettingsBaseApplication.mApplication.getSharedPreferences("OPSettingsPrefs", 0);
    }

    private static SharedPreferences.Editor getSharedPreferencesEditor() {
        return SettingsBaseApplication.mApplication.getSharedPreferences("OPSettingsPrefs", 0).edit();
    }

    public static void putInt(String str, int i) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferencesEditor();
        sharedPreferencesEditor.putInt(str, i);
        sharedPreferencesEditor.apply();
    }

    public static int getInt(String str, int i) {
        return getSharedPreferences().getInt(str, i);
    }

    public static void putString(String str, String str2) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferencesEditor();
        sharedPreferencesEditor.putString(str, str2);
        sharedPreferencesEditor.apply();
    }

    public static String getString(String str, String str2) {
        return getSharedPreferences().getString(str, str2);
    }
}
