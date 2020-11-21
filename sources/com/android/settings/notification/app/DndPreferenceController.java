package com.android.settings.notification.app;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.RestrictedSwitchPreference;

public class DndPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bypass_dnd";
    }

    public DndPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        return super.isAvailable() && this.mChannel != null;
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
            restrictedSwitchPreference.setChecked(this.mChannel.canBypassDnd());
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mChannel != null) {
            this.mChannel.setBypassDnd(((Boolean) obj).booleanValue());
            this.mChannel.lockFields(1);
            saveChannel();
        }
        return true;
    }
}
