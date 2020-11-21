package com.oneplus.security;

import android.content.SharedPreferences;
import com.oneplus.settings.SettingsBaseApplication;

public class BaseSharePreference {
    protected static SharedPreferences getDefaultSharedPreferences() {
        return SettingsBaseApplication.getContext().getSharedPreferences("security_preferance", 0);
    }

    public static SharedPreferences getDefaultSharedPreferences(String str) {
        return SettingsBaseApplication.getContext().getSharedPreferences(str, 0);
    }
}
