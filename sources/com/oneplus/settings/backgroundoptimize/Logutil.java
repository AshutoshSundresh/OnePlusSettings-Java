package com.oneplus.settings.backgroundoptimize;

import android.util.Log;
import com.oneplus.settings.SettingsBaseApplication;

public class Logutil {
    public static void loge(String str, String str2) {
        if (SettingsBaseApplication.ONEPLUS_DEBUG) {
            Log.e(str, str2);
        }
    }
}
