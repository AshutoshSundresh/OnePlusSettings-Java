package com.android.settings.notification.app;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;

public class NotificationsOffPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "block_desc";
    }

    public NotificationsOffPreferenceController(Context context) {
        super(context, null);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        if (this.mAppRow == null) {
            return false;
        }
        return !super.isAvailable();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mAppRow != null) {
            if (this.mChannel != null) {
                preference.setTitle(C0017R$string.channel_notifications_off_desc);
            } else if (this.mChannelGroup != null) {
                preference.setTitle(C0017R$string.channel_group_notifications_off_desc);
            } else {
                preference.setTitle(C0017R$string.app_notifications_off_desc);
            }
        }
        preference.setSelectable(false);
    }
}
