package com.android.settings.notification.app;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0019R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class AppNotificationSettings extends NotificationSettings {
    private static String KEY_ADVANCED_CATEGORY = "app_advanced";
    private static String KEY_APP_LINK = "app_link";
    private static String KEY_BADGE = "badge";
    private static String[] LEGACY_NON_ADVANCED_KEYS = {"badge", "app_link"};

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AppNotificationSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 72;
    }

    static {
        Log.isLoggable("AppNotificationSettings", 3);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.notification.app.NotificationSettings, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (this.mShowLegacyChannelConfig && preferenceScreen != null) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference(KEY_ADVANCED_CATEGORY);
            removePreference(KEY_ADVANCED_CATEGORY);
            if (preferenceGroup != null) {
                for (String str : LEGACY_NON_ADVANCED_KEYS) {
                    Preference findPreference = preferenceGroup.findPreference(str);
                    if (findPreference != null) {
                        preferenceGroup.removePreference(findPreference);
                        if (findPreference != null) {
                            preferenceScreen.addPreference(findPreference);
                        }
                    }
                }
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.notification.app.NotificationSettings, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null) {
            Log.w("AppNotificationSettings", "Missing package or uid or packageinfo");
            finish();
            return;
        }
        for (NotificationPreferenceController notificationPreferenceController : ((NotificationSettings) this).mControllers) {
            notificationPreferenceController.onResume(this.mAppRow, this.mChannel, this.mChannelGroup, null, null, this.mSuspendedAppsAdmin, this.mInstantAppPKG);
            notificationPreferenceController.displayPreference(getPreferenceScreen());
        }
        updatePreferenceStates();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.app_notification_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((NotificationSettings) this).mControllers = arrayList;
        arrayList.add(new HeaderPreferenceController(context, this));
        ((NotificationSettings) this).mControllers.add(new BlockPreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new BadgePreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new AllowSoundPreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new ImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new MinImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new HighImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new SoundPreferenceController(context, this, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new LightsPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new VibrationPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new VisibilityPreferenceController(context, new LockPatternUtils(context), this.mBackend));
        ((NotificationSettings) this).mControllers.add(new DndPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new AppLinkPreferenceController(context));
        ((NotificationSettings) this).mControllers.add(new DescriptionPreferenceController(context));
        ((NotificationSettings) this).mControllers.add(new NotificationsOffPreferenceController(context));
        ((NotificationSettings) this).mControllers.add(new DeletedChannelsPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new ChannelListPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new AppConversationListPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new InvalidConversationInfoPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new InvalidConversationPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new BubbleSummaryPreferenceController(context, this.mBackend));
        return new ArrayList(((NotificationSettings) this).mControllers);
    }
}
