package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.Context;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;

public class DescriptionPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "desc";
    }

    public DescriptionPreferenceController(Context context) {
        super(context, null);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        if (!super.isAvailable()) {
            return false;
        }
        if (this.mChannel == null && !hasValidGroup()) {
            return false;
        }
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel != null && !TextUtils.isEmpty(notificationChannel.getDescription())) {
            return true;
        }
        if (!hasValidGroup() || TextUtils.isEmpty(this.mChannelGroup.getDescription())) {
            return false;
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mAppRow != null) {
            NotificationChannel notificationChannel = this.mChannel;
            if (notificationChannel != null) {
                preference.setTitle(notificationChannel.getDescription());
            } else if (hasValidGroup()) {
                preference.setTitle(this.mChannelGroup.getDescription());
            }
        }
        preference.setEnabled(false);
        preference.setSelectable(false);
    }
}
