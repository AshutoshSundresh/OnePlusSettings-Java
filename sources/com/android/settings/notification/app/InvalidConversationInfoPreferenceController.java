package com.android.settings.notification.app;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.notification.NotificationBackend;

public class InvalidConversationInfoPreferenceController extends NotificationPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "invalid_conversation_info";
    }

    public InvalidConversationInfoPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow != null && !appRow.banned) {
            return this.mBackend.isInInvalidMsgState(appRow.pkg, appRow.uid);
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow != null) {
            preference.setSummary(((NotificationPreferenceController) this).mContext.getString(C0017R$string.convo_not_supported_summary, appRow.label));
        }
    }
}
