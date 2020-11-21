package com.android.settings.notification.app;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import androidx.preference.Preference;

public class BubbleLinkPreferenceController extends NotificationPreferenceController {
    static final int ON = 1;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "notification_bubbles";
    }

    public BubbleLinkPreferenceController(Context context) {
        super(context, null);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        if (!super.isAvailable()) {
            return false;
        }
        return areBubblesEnabled();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mAppRow != null) {
            Intent intent = new Intent("android.settings.APP_NOTIFICATION_BUBBLE_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", this.mAppRow.pkg);
            intent.putExtra("app_uid", this.mAppRow.uid);
            preference.setIntent(intent);
        }
    }

    private boolean areBubblesEnabled() {
        return Settings.Global.getInt(((NotificationPreferenceController) this).mContext.getContentResolver(), "notification_bubbles", 1) == 1;
    }
}
