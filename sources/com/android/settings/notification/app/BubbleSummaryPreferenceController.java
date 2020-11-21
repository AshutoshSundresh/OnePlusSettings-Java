package com.android.settings.notification.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.notification.NotificationBackend;

public class BubbleSummaryPreferenceController extends NotificationPreferenceController {
    static final int ON = 1;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bubble_pref_link";
    }

    public BubbleSummaryPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        if (!super.isAvailable() || this.mAppRow == null) {
            return false;
        }
        if (this.mChannel != null) {
            if (!isGloballyEnabled()) {
                return false;
            }
            if (isDefaultChannel()) {
                return true;
            }
            if (this.mAppRow != null) {
                return true;
            }
            return false;
        } else if (!isGloballyEnabled()) {
            return false;
        } else {
            NotificationBackend notificationBackend = this.mBackend;
            NotificationBackend.AppRow appRow = this.mAppRow;
            if (notificationBackend.hasSentValidMsg(appRow.pkg, appRow.uid)) {
                return true;
            }
            return false;
        }
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

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null) {
            return null;
        }
        int i = appRow.bubblePreference;
        Resources resources = ((NotificationPreferenceController) this).mContext.getResources();
        if (i == 0 || !isGloballyEnabled()) {
            return resources.getString(C0017R$string.bubble_app_setting_none);
        }
        if (i == 1) {
            return resources.getString(C0017R$string.bubble_app_setting_all);
        }
        return resources.getString(C0017R$string.bubble_app_setting_selected);
    }

    private boolean isGloballyEnabled() {
        return Settings.Global.getInt(((NotificationPreferenceController) this).mContext.getContentResolver(), "notification_bubbles", 1) == 1;
    }
}
