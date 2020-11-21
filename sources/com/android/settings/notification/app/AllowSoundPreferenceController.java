package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.NotificationSettings;
import com.android.settingslib.RestrictedSwitchPreference;

public class AllowSoundPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private NotificationSettings.DependentFieldListener mDependentFieldListener;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "allow_sound";
    }

    public AllowSoundPreferenceController(Context context, NotificationSettings.DependentFieldListener dependentFieldListener, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mDependentFieldListener = dependentFieldListener;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        NotificationChannel notificationChannel;
        if (super.isAvailable() && (notificationChannel = this.mChannel) != null && "miscellaneous".equals(notificationChannel.getId())) {
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mChannel != null) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            restrictedSwitchPreference.setDisabledByAdmin(this.mAdmin);
            boolean z = true;
            restrictedSwitchPreference.setEnabled(!restrictedSwitchPreference.isDisabledByAdmin());
            if (this.mChannel.getImportance() < 3 && this.mChannel.getImportance() != -1000) {
                z = false;
            }
            restrictedSwitchPreference.setChecked(z);
            return;
        }
        Log.i("AllowSoundPrefContr", "tried to updatestate on a null channel?!");
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mChannel == null) {
            return true;
        }
        this.mChannel.setImportance(((Boolean) obj).booleanValue() ? -1000 : 2);
        this.mChannel.lockFields(4);
        saveChannel();
        this.mDependentFieldListener.onFieldValueChanged();
        return true;
    }
}
