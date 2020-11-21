package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.Context;
import android.media.RingtoneManager;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.NotificationSettings;

public class ImportancePreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private NotificationSettings.DependentFieldListener mDependentFieldListener;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "importance";
    }

    public ImportancePreferenceController(Context context, NotificationSettings.DependentFieldListener dependentFieldListener, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mDependentFieldListener = dependentFieldListener;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        if (super.isAvailable() && this.mChannel != null) {
            return !isDefaultChannel();
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        NotificationChannel notificationChannel;
        if (this.mAppRow != null && (notificationChannel = this.mChannel) != null) {
            boolean z = false;
            preference.setEnabled(this.mAdmin == null && !notificationChannel.isImportanceLockedByOEM());
            ImportancePreference importancePreference = (ImportancePreference) preference;
            importancePreference.setConfigurable(!this.mChannel.isImportanceLockedByOEM());
            importancePreference.setImportance(this.mChannel.getImportance());
            importancePreference.setDisplayInStatusBar(this.mBackend.showSilentInStatusBar(((NotificationPreferenceController) this).mContext.getPackageName()));
            if (Settings.Secure.getInt(((NotificationPreferenceController) this).mContext.getContentResolver(), "lock_screen_show_silent_notifications", 1) == 1) {
                z = true;
            }
            importancePreference.setDisplayOnLockscreen(z);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mChannel == null) {
            return true;
        }
        int intValue = ((Integer) obj).intValue();
        if (this.mChannel.getImportance() < 3 && !SoundPreferenceController.hasValidSound(this.mChannel) && intValue >= 3) {
            this.mChannel.setSound(RingtoneManager.getDefaultUri(2), this.mChannel.getAudioAttributes());
            this.mChannel.lockFields(32);
        }
        this.mChannel.setImportance(intValue);
        this.mChannel.lockFields(4);
        saveChannel();
        this.mDependentFieldListener.onFieldValueChanged();
        return true;
    }
}
