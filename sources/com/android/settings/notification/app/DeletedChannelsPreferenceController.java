package com.android.settings.notification.app;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0015R$plurals;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;

public class DeletedChannelsPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "deleted";
    }

    public DeletedChannelsPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        if (!super.isAvailable() || this.mChannel != null || hasValidGroup()) {
            return false;
        }
        NotificationBackend notificationBackend = this.mBackend;
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (notificationBackend.getDeletedChannelCount(appRow.pkg, appRow.uid) > 0) {
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow != null) {
            int deletedChannelCount = this.mBackend.getDeletedChannelCount(appRow.pkg, appRow.uid);
            preference.setTitle(((NotificationPreferenceController) this).mContext.getResources().getQuantityString(C0015R$plurals.deleted_channels, deletedChannelCount, Integer.valueOf(deletedChannelCount)));
        }
        preference.setSelectable(false);
    }
}
