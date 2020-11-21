package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.core.text.BidiFormatter;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settingslib.RestrictedSwitchPreference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppChannelsBypassingDndPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, LifecycleObserver {
    private RestrictedSwitchPreference mAllNotificationsToggle;
    private final List<NotificationChannel> mChannels = new ArrayList();
    private PreferenceCategory mPreferenceCategory;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_bypassing_app_channels_list";
    }

    public AppChannelsBypassingDndPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("zen_mode_bypassing_app_channels_list");
        this.mPreferenceCategory = preferenceCategory;
        RestrictedSwitchPreference restrictedSwitchPreference = new RestrictedSwitchPreference(preferenceCategory.getContext());
        this.mAllNotificationsToggle = restrictedSwitchPreference;
        restrictedSwitchPreference.setTitle(C0017R$string.zen_mode_bypassing_app_channels_toggle_all);
        this.mAllNotificationsToggle.setDisabledByAdmin(this.mAdmin);
        RestrictedSwitchPreference restrictedSwitchPreference2 = this.mAllNotificationsToggle;
        restrictedSwitchPreference2.setEnabled(this.mAdmin == null || !restrictedSwitchPreference2.isDisabledByAdmin());
        this.mAllNotificationsToggle.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.notification.app.AppChannelsBypassingDndPreferenceController.AnonymousClass1 */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                boolean isChecked = ((SwitchPreference) preference).isChecked();
                for (NotificationChannel notificationChannel : AppChannelsBypassingDndPreferenceController.this.mChannels) {
                    if (AppChannelsBypassingDndPreferenceController.this.showNotification(notificationChannel) && AppChannelsBypassingDndPreferenceController.this.isChannelConfigurable(notificationChannel)) {
                        notificationChannel.setBypassDnd(isChecked);
                        notificationChannel.lockFields(1);
                        AppChannelsBypassingDndPreferenceController appChannelsBypassingDndPreferenceController = AppChannelsBypassingDndPreferenceController.this;
                        NotificationBackend notificationBackend = appChannelsBypassingDndPreferenceController.mBackend;
                        NotificationBackend.AppRow appRow = appChannelsBypassingDndPreferenceController.mAppRow;
                        notificationBackend.updateChannel(appRow.pkg, appRow.uid, notificationChannel);
                    }
                }
                for (int i = 1; i < AppChannelsBypassingDndPreferenceController.this.mPreferenceCategory.getPreferenceCount(); i++) {
                    AppChannelsBypassingDndPreferenceController appChannelsBypassingDndPreferenceController2 = AppChannelsBypassingDndPreferenceController.this;
                    ((MasterSwitchPreference) AppChannelsBypassingDndPreferenceController.this.mPreferenceCategory.getPreference(i)).setChecked(appChannelsBypassingDndPreferenceController2.showNotificationInDnd((NotificationChannel) appChannelsBypassingDndPreferenceController2.mChannels.get(i - 1)));
                }
                return true;
            }
        });
        loadAppChannels();
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        return this.mAppRow != null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mAppRow != null) {
            loadAppChannels();
        }
    }

    private void loadAppChannels() {
        new AsyncTask<Void, Void, Void>() {
            /* class com.android.settings.notification.app.AppChannelsBypassingDndPreferenceController.AnonymousClass2 */

            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                AppChannelsBypassingDndPreferenceController appChannelsBypassingDndPreferenceController = AppChannelsBypassingDndPreferenceController.this;
                NotificationBackend notificationBackend = appChannelsBypassingDndPreferenceController.mBackend;
                NotificationBackend.AppRow appRow = appChannelsBypassingDndPreferenceController.mAppRow;
                List<NotificationChannelGroup> list = notificationBackend.getGroups(appRow.pkg, appRow.uid).getList();
                AppChannelsBypassingDndPreferenceController.this.mChannels.clear();
                for (NotificationChannelGroup notificationChannelGroup : list) {
                    for (NotificationChannel notificationChannel : notificationChannelGroup.getChannels()) {
                        if (!AppChannelsBypassingDndPreferenceController.this.isConversation(notificationChannel)) {
                            AppChannelsBypassingDndPreferenceController.this.mChannels.add(notificationChannel);
                        }
                    }
                }
                Collections.sort(AppChannelsBypassingDndPreferenceController.this.mChannels, NotificationPreferenceController.CHANNEL_COMPARATOR);
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void r1) {
                AppChannelsBypassingDndPreferenceController appChannelsBypassingDndPreferenceController = AppChannelsBypassingDndPreferenceController.this;
                if (((NotificationPreferenceController) appChannelsBypassingDndPreferenceController).mContext != null) {
                    appChannelsBypassingDndPreferenceController.populateList();
                }
            }
        }.execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void populateList() {
        PreferenceCategory preferenceCategory = this.mPreferenceCategory;
        if (preferenceCategory != null) {
            preferenceCategory.removeAll();
            this.mPreferenceCategory.addPreference(this.mAllNotificationsToggle);
            for (final NotificationChannel notificationChannel : this.mChannels) {
                MasterSwitchPreference masterSwitchPreference = new MasterSwitchPreference(((NotificationPreferenceController) this).mContext);
                masterSwitchPreference.setDisabledByAdmin(this.mAdmin);
                masterSwitchPreference.setSwitchEnabled((this.mAdmin == null || !masterSwitchPreference.isDisabledByAdmin()) && isChannelConfigurable(notificationChannel) && showNotification(notificationChannel));
                masterSwitchPreference.setTitle(BidiFormatter.getInstance().unicodeWrap(notificationChannel.getName()));
                masterSwitchPreference.setChecked(showNotificationInDnd(notificationChannel));
                masterSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    /* class com.android.settings.notification.app.AppChannelsBypassingDndPreferenceController.AnonymousClass3 */

                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public boolean onPreferenceChange(Preference preference, Object obj) {
                        notificationChannel.setBypassDnd(((Boolean) obj).booleanValue());
                        notificationChannel.lockFields(1);
                        AppChannelsBypassingDndPreferenceController appChannelsBypassingDndPreferenceController = AppChannelsBypassingDndPreferenceController.this;
                        NotificationBackend notificationBackend = appChannelsBypassingDndPreferenceController.mBackend;
                        NotificationBackend.AppRow appRow = appChannelsBypassingDndPreferenceController.mAppRow;
                        notificationBackend.updateChannel(appRow.pkg, appRow.uid, notificationChannel);
                        AppChannelsBypassingDndPreferenceController.this.mAllNotificationsToggle.setChecked(AppChannelsBypassingDndPreferenceController.this.areAllChannelsBypassing());
                        return true;
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putInt("uid", this.mAppRow.uid);
                bundle.putString("package", this.mAppRow.pkg);
                bundle.putString("android.provider.extra.CHANNEL_ID", notificationChannel.getId());
                bundle.putBoolean("fromSettings", true);
                masterSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(bundle) {
                    /* class com.android.settings.notification.app.$$Lambda$AppChannelsBypassingDndPreferenceController$E15JWGyR44xiadvzpg3gNkL7qs */
                    public final /* synthetic */ Bundle f$1;

                    {
                        this.f$1 = r2;
                    }

                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return AppChannelsBypassingDndPreferenceController.this.lambda$populateList$0$AppChannelsBypassingDndPreferenceController(this.f$1, preference);
                    }
                });
                this.mPreferenceCategory.addPreference(masterSwitchPreference);
            }
            this.mAllNotificationsToggle.setChecked(areAllChannelsBypassing());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$populateList$0 */
    public /* synthetic */ boolean lambda$populateList$0$AppChannelsBypassingDndPreferenceController(Bundle bundle, Preference preference) {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(((NotificationPreferenceController) this).mContext);
        subSettingLauncher.setDestination(ChannelNotificationSettings.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setUserHandle(UserHandle.of(this.mAppRow.userId));
        subSettingLauncher.setTitleRes(C0017R$string.notification_channel_title);
        subSettingLauncher.setSourceMetricsCategory(1840);
        subSettingLauncher.launch();
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean areAllChannelsBypassing() {
        boolean z = true;
        for (NotificationChannel notificationChannel : this.mChannels) {
            if (showNotification(notificationChannel)) {
                z &= showNotificationInDnd(notificationChannel);
            }
        }
        return z;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean showNotificationInDnd(NotificationChannel notificationChannel) {
        return notificationChannel.canBypassDnd() && showNotification(notificationChannel);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean showNotification(NotificationChannel notificationChannel) {
        return notificationChannel.getImportance() != 0;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isConversation(NotificationChannel notificationChannel) {
        return notificationChannel.getConversationId() != null && !notificationChannel.isDemoted();
    }
}
