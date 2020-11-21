package com.android.settings.notification;

import android.content.Context;
import com.android.settings.C0005R$bool;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class DockingSoundPreferenceController extends SettingPrefController {
    public DockingSoundPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, Lifecycle lifecycle) {
        super(context, settingsPreferenceFragment, lifecycle);
        this.mPreference = new SettingPref(this, 1, "docking_sounds", "dock_sounds_enabled", 1, new int[0]) {
            /* class com.android.settings.notification.DockingSoundPreferenceController.AnonymousClass1 */

            @Override // com.android.settings.notification.SettingPref
            public boolean isApplicable(Context context) {
                return context.getResources().getBoolean(C0005R$bool.has_dock_settings);
            }
        };
    }
}
