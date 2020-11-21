package com.android.settings.notification.app;

import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.RestrictedSwitchPreference;

public class LightsPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "lights";
    }

    public LightsPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        if (super.isAvailable() && this.mChannel != null && checkCanBeVisible(3) && canPulseLight() && !isDefaultChannel()) {
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mChannel != null) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            restrictedSwitchPreference.setDisabledByAdmin(this.mAdmin);
            restrictedSwitchPreference.setEnabled(!restrictedSwitchPreference.isDisabledByAdmin());
            restrictedSwitchPreference.setChecked(this.mChannel.shouldShowLights());
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mChannel == null) {
            return true;
        }
        this.mChannel.enableLights(((Boolean) obj).booleanValue());
        saveChannel();
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean canPulseLight() {
        if (((NotificationPreferenceController) this).mContext.getResources().getBoolean(17891478) && Settings.System.getInt(((NotificationPreferenceController) this).mContext.getContentResolver(), "notification_light_pulse", 0) == 1) {
            return true;
        }
        return false;
    }
}
