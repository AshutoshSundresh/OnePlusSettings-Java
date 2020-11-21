package com.android.settings.notification;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import com.android.settings.C0005R$bool;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class TouchSoundPreferenceController extends SettingPrefController {
    public TouchSoundPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, Lifecycle lifecycle) {
        super(context, settingsPreferenceFragment, lifecycle);
        this.mPreference = new SettingPref(this, 2, "touch_sounds", "sound_effects_enabled", 1, new int[0]) {
            /* class com.android.settings.notification.TouchSoundPreferenceController.AnonymousClass1 */

            /* access modifiers changed from: protected */
            @Override // com.android.settings.notification.SettingPref
            public boolean setSetting(final Context context, final int i) {
                AsyncTask.execute(new Runnable(this) {
                    /* class com.android.settings.notification.TouchSoundPreferenceController.AnonymousClass1.AnonymousClass1 */

                    public void run() {
                        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
                        if (i != 0) {
                            audioManager.loadSoundEffects();
                        } else {
                            audioManager.unloadSoundEffects();
                        }
                    }
                });
                return super.setSetting(context, i);
            }
        };
    }

    @Override // com.android.settings.notification.SettingPrefController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_touch_sounds);
    }
}
