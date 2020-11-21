package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.applications.AppStateNotificationBridge;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settingslib.RestrictedSwitchPreference;

public class BadgePreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "badge";
    }

    public BadgePreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        if (!super.isAvailable()) {
            return false;
        }
        if ((this.mAppRow == null && this.mChannel == null) || Settings.Secure.getInt(((NotificationPreferenceController) this).mContext.getContentResolver(), "notification_badging", 1) == 0) {
            return false;
        }
        if (this.mChannel == null || isDefaultChannel()) {
            return true;
        }
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null) {
            return false;
        }
        return appRow.showBadge;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mAppRow == null) {
            return;
        }
        if (preference instanceof MasterSwitchPreference) {
            MasterSwitchPreference masterSwitchPreference = (MasterSwitchPreference) preference;
            masterSwitchPreference.setDisabledByAdmin(this.mAdmin);
            NotificationChannel notificationChannel = this.mChannel;
            if (notificationChannel != null) {
                masterSwitchPreference.setChecked(notificationChannel.canShowBadge());
                if ("com.android.dialer".equals(this.mAppRow.pkg) || "com.google.android.dialer".equals(this.mAppRow.pkg) || "com.oneplus.dialer".equals(this.mAppRow.pkg)) {
                    masterSwitchPreference.setEnabled(false);
                } else {
                    masterSwitchPreference.setEnabled(true);
                }
            } else {
                masterSwitchPreference.setChecked(this.mAppRow.showBadge);
            }
        } else {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            restrictedSwitchPreference.setDisabledByAdmin(this.mAdmin);
            NotificationChannel notificationChannel2 = this.mChannel;
            if (notificationChannel2 != null) {
                restrictedSwitchPreference.setChecked(notificationChannel2.canShowBadge());
                if ("com.android.dialer".equals(this.mAppRow.pkg) || "com.google.android.dialer".equals(this.mAppRow.pkg) || "com.oneplus.dialer".equals(this.mAppRow.pkg)) {
                    restrictedSwitchPreference.setEnabled(false);
                } else {
                    restrictedSwitchPreference.setEnabled(!restrictedSwitchPreference.isDisabledByAdmin());
                }
            } else {
                restrictedSwitchPreference.setChecked(this.mAppRow.showBadge);
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (!TextUtils.isEmpty(this.mBackend.mInstantAppPKG)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("badge", Integer.valueOf(booleanValue ? 1 : 0));
            ((NotificationPreferenceController) this).mContext.getContentResolver().update(Uri.withAppendedPath(AppStateNotificationBridge.BASE_URI, this.mBackend.mInstantAppPKG), contentValues, null, null);
            return true;
        }
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel != null) {
            notificationChannel.setShowBadge(booleanValue);
            saveChannel();
        } else {
            NotificationBackend.AppRow appRow = this.mAppRow;
            if (appRow != null) {
                appRow.showBadge = booleanValue;
                this.mBackend.setShowBadge(appRow.pkg, appRow.uid, booleanValue);
            }
        }
        return true;
    }
}
