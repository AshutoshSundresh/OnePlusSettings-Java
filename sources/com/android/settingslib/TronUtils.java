package com.android.settingslib;

import android.content.Context;
import com.android.internal.logging.MetricsLogger;

public final class TronUtils {
    public static void logWifiSettingsSpeed(Context context, int i) {
        MetricsLogger.histogram(context, "settings_wifi_speed_labels", i);
    }
}
