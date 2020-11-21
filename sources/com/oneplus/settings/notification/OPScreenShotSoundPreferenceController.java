package com.oneplus.settings.notification;

import android.content.Context;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.notification.SettingPref;
import com.android.settings.notification.SettingPrefController;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class OPScreenShotSoundPreferenceController extends SettingPrefController {
    @Override // com.android.settings.notification.SettingPrefController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public OPScreenShotSoundPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, Lifecycle lifecycle) {
        super(context, settingsPreferenceFragment, lifecycle);
        this.mPreference = new SettingPref(2, "screenshot_sounds", "oem_screenshot_sound_enable", 1, new int[0]);
    }
}
