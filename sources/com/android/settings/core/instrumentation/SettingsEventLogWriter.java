package com.android.settings.core.instrumentation;

import android.content.Context;
import android.provider.DeviceConfig;
import com.android.settingslib.core.instrumentation.EventLogWriter;

public class SettingsEventLogWriter extends EventLogWriter {
    @Override // com.android.settingslib.core.instrumentation.LogWriter, com.android.settingslib.core.instrumentation.EventLogWriter
    public void visible(Context context, int i, int i2, int i3) {
        if (!shouldDisableGenericEventLogging()) {
            super.visible(context, i, i2, i3);
        }
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter, com.android.settingslib.core.instrumentation.EventLogWriter
    public void hidden(Context context, int i, int i2) {
        if (!shouldDisableGenericEventLogging()) {
            super.hidden(context, i, i2);
        }
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter, com.android.settingslib.core.instrumentation.EventLogWriter
    public void action(Context context, int i, String str) {
        if (!shouldDisableGenericEventLogging()) {
            super.action(context, i, str);
        }
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter, com.android.settingslib.core.instrumentation.EventLogWriter
    public void action(Context context, int i, int i2) {
        if (!shouldDisableGenericEventLogging()) {
            super.action(context, i, i2);
        }
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter, com.android.settingslib.core.instrumentation.EventLogWriter
    public void action(Context context, int i, boolean z) {
        if (!shouldDisableGenericEventLogging()) {
            super.action(context, i, z);
        }
    }

    private static boolean shouldDisableGenericEventLogging() {
        return !DeviceConfig.getBoolean("settings_ui", "event_logging_enabled", true);
    }
}
