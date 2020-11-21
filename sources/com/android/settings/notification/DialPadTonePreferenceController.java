package com.android.settings.notification;

import android.content.Context;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class DialPadTonePreferenceController extends SettingPrefController {
    public DialPadTonePreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, Lifecycle lifecycle) {
        super(context, settingsPreferenceFragment, lifecycle);
        this.mPreference = new SettingPref(this, 2, "dial_pad_tones", "dtmf_tone", 1, new int[0]) {
            /* class com.android.settings.notification.DialPadTonePreferenceController.AnonymousClass1 */

            @Override // com.android.settings.notification.SettingPref
            public boolean isApplicable(Context context) {
                return Utils.isVoiceCapable(context);
            }
        };
    }
}
