package com.android.settingslib;

import android.content.Context;
import android.provider.Settings;

public class WirelessUtils {
    public static boolean isRadioAllowed(Context context, String str) {
        if (!isAirplaneModeOn(context)) {
            return true;
        }
        String string = Settings.Global.getString(context.getContentResolver(), "airplane_mode_toggleable_radios");
        if (string == null || !string.contains(str)) {
            return false;
        }
        return true;
    }

    public static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0;
    }
}
