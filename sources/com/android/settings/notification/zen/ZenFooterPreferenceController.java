package com.android.settings.notification.zen;

import android.app.NotificationManager;
import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenFooterPreferenceController extends AbstractZenModePreferenceController {
    public ZenFooterPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        int i = this.mBackend.mPolicy.suppressedVisualEffects;
        return i == 0 || NotificationManager.Policy.areAllVisualEffectsSuppressed(i);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int i = this.mBackend.mPolicy.suppressedVisualEffects;
        if (i == 0) {
            preference.setTitle(C0017R$string.zen_mode_restrict_notifications_mute_footer);
        } else if (NotificationManager.Policy.areAllVisualEffectsSuppressed(i)) {
            preference.setTitle(C0017R$string.zen_mode_restrict_notifications_hide_footer);
        } else {
            preference.setTitle((CharSequence) null);
        }
    }
}
