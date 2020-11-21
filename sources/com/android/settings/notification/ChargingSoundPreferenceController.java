package com.android.settings.notification;

import android.content.Context;
import com.android.settings.C0005R$bool;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ChargingSoundPreferenceController extends SettingPrefController {
    public ChargingSoundPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, Lifecycle lifecycle) {
        super(context, settingsPreferenceFragment, lifecycle);
        this.mPreference = new SettingPref(3, "charging_sounds", "charging_sounds_enabled", 1, new int[0]);
    }

    @Override // com.android.settings.notification.SettingPrefController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_charging_sounds);
    }
}
