package com.android.settings.notification.app;

import android.content.Context;
import android.os.Vibrator;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.RestrictedSwitchPreference;

public class VibrationPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final Vibrator mVibrator;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "vibrate";
    }

    public VibrationPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mVibrator = (Vibrator) context.getSystemService("vibrator");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        Vibrator vibrator;
        if (!super.isAvailable() || this.mChannel == null || !checkCanBeVisible(3) || isDefaultChannel() || (vibrator = this.mVibrator) == null || !vibrator.hasVibrator()) {
            return false;
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mChannel != null) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            restrictedSwitchPreference.setDisabledByAdmin(this.mAdmin);
            if ("com.android.dialer".equals(this.mAppRow.pkg) || "com.google.android.dialer".equals(this.mAppRow.pkg) || "com.oneplus.dialer".equals(this.mAppRow.pkg)) {
                restrictedSwitchPreference.setEnabled(false);
            } else {
                restrictedSwitchPreference.setEnabled(!restrictedSwitchPreference.isDisabledByAdmin());
            }
            restrictedSwitchPreference.setChecked(this.mChannel.shouldVibrate());
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mChannel == null) {
            return true;
        }
        this.mChannel.enableVibration(((Boolean) obj).booleanValue());
        saveChannel();
        return true;
    }
}
