package com.oneplus.security.utils;

import android.content.SharedPreferences;
import com.oneplus.security.BaseSharePreference;

public class SharedPreferenceHelper extends BaseSharePreference {
    private static SharedPreferences mSharedPreferences = BaseSharePreference.getDefaultSharedPreferences();

    public static int getInt(String str, int i) {
        return mSharedPreferences.getInt(str, i);
    }

    public static void putInt(String str, int i) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(str, i);
        edit.commit();
    }
}
