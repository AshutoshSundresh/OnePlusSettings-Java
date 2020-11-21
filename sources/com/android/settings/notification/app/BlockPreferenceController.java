package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Switch;
import androidx.preference.Preference;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.applications.AppStateNotificationBridge;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.NotificationSettings;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.widget.LayoutPreference;
import com.oneplus.settings.utils.OPUtils;

public class BlockPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, SwitchBar.OnSwitchChangeListener {
    private NotificationSettings.DependentFieldListener mDependentFieldListener;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "block";
    }

    public BlockPreferenceController(Context context, NotificationSettings.DependentFieldListener dependentFieldListener, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mDependentFieldListener = dependentFieldListener;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        return this.mAppRow != null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        NotificationBackend.AppRow appRow;
        LayoutPreference layoutPreference = (LayoutPreference) preference;
        boolean z = false;
        layoutPreference.setSelectable(false);
        SwitchBar switchBar = (SwitchBar) layoutPreference.findViewById(C0010R$id.switch_bar);
        if (switchBar != null && (appRow = this.mAppRow) != null) {
            Context context = ((NotificationPreferenceController) this).mContext;
            String string = context.getString(C0017R$string.notification_switch_label, OPUtils.getAppLabel(context, appRow.pkg));
            Context context2 = ((NotificationPreferenceController) this).mContext;
            switchBar.setSwitchBarText(string, context2.getString(C0017R$string.notification_switch_label, OPUtils.getAppLabel(context2, this.mAppRow.pkg)));
            if ("com.oneplus.deskclock".equals(this.mAppRow.pkg) || "com.android.incallui".equals(this.mAppRow.pkg) || "com.google.android.calendar".equals(this.mAppRow.pkg) || "com.oneplus.calendar".equals(this.mAppRow.pkg) || "com.android.dialer".equals(this.mAppRow.pkg) || "com.google.android.dialer".equals(this.mAppRow.pkg) || "com.oneplus.screenrecord".equals(this.mAppRow.pkg) || "com.oneplus.dialer".equals(this.mAppRow.pkg)) {
                switchBar.hide();
            } else {
                switchBar.show();
            }
            try {
                switchBar.addOnSwitchChangeListener(this);
            } catch (IllegalStateException unused) {
            }
            switchBar.setDisabledByAdmin(this.mAdmin);
            if (this.mChannel != null && !isChannelBlockable()) {
                switchBar.setEnabled(false);
                switchBar.hide();
            }
            if (this.mChannelGroup != null && !isChannelGroupBlockable()) {
                switchBar.setEnabled(false);
                switchBar.hide();
            }
            if (this.mChannel == null) {
                NotificationBackend.AppRow appRow2 = this.mAppRow;
                if (appRow2.systemApp && (!appRow2.banned || appRow2.lockedImportance)) {
                    switchBar.setEnabled(false);
                    switchBar.hide();
                }
            }
            if ("com.tmobile.pr.adapt".equals(this.mAppRow.pkg)) {
                switchBar.setEnabled(false);
                switchBar.hide();
            }
            NotificationChannel notificationChannel = this.mChannel;
            if (notificationChannel != null) {
                if (!this.mAppRow.banned && notificationChannel.getImportance() != 0) {
                    z = true;
                }
                switchBar.setChecked(z);
                return;
            }
            NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
            if (notificationChannelGroup != null) {
                if (!this.mAppRow.banned && !notificationChannelGroup.isBlocked()) {
                    z = true;
                }
                switchBar.setChecked(z);
                return;
            }
            switchBar.setChecked(!this.mAppRow.banned);
        }
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r4, boolean z) {
        int i;
        boolean z2 = !z;
        if (!TextUtils.isEmpty(this.mBackend.mInstantAppPKG)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("notify", Integer.valueOf(!z2));
            try {
                ((NotificationPreferenceController) this).mContext.getContentResolver().update(Uri.withAppendedPath(AppStateNotificationBridge.BASE_URI, this.mBackend.mInstantAppPKG), contentValues, null, null);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            this.mAppRow.banned = z2;
            this.mDependentFieldListener.onImportanceChangedForInstant();
            return;
        }
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel != null) {
            int importance = notificationChannel.getImportance();
            if (z2 || importance == 0) {
                if (z2) {
                    i = 0;
                } else {
                    i = isDefaultChannel() ? -1000 : 3;
                }
                this.mChannel.setImportance(i);
                saveChannel();
            }
            NotificationBackend notificationBackend = this.mBackend;
            NotificationBackend.AppRow appRow = this.mAppRow;
            if (notificationBackend.onlyHasDefaultChannel(appRow.pkg, appRow.uid)) {
                NotificationBackend.AppRow appRow2 = this.mAppRow;
                if (appRow2.banned != z2) {
                    appRow2.banned = z2;
                    this.mBackend.setNotificationsEnabledForPackage(appRow2.pkg, appRow2.uid, !z2);
                }
            }
        } else {
            NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
            if (notificationChannelGroup != null) {
                notificationChannelGroup.setBlocked(z2);
                NotificationBackend notificationBackend2 = this.mBackend;
                NotificationBackend.AppRow appRow3 = this.mAppRow;
                notificationBackend2.updateChannelGroup(appRow3.pkg, appRow3.uid, this.mChannelGroup);
            } else {
                NotificationBackend.AppRow appRow4 = this.mAppRow;
                if (appRow4 != null) {
                    appRow4.banned = z2;
                    this.mBackend.setNotificationsEnabledForPackage(appRow4.pkg, appRow4.uid, !z2);
                }
            }
        }
        this.mDependentFieldListener.onFieldValueChanged();
    }
}
