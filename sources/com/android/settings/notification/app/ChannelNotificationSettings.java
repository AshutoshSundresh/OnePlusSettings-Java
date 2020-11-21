package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0019R$xml;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ChannelNotificationSettings extends NotificationSettings {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ChannelSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 265;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.notification.app.NotificationSettings, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Bundle arguments = getArguments();
        if (preferenceScreen != null && arguments != null && !arguments.getBoolean("fromSettings", false)) {
            preferenceScreen.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.notification.app.NotificationSettings, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        NotificationChannel notificationChannel;
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null || (notificationChannel = this.mChannel) == null) {
            Log.w("ChannelSettings", "Missing package or uid or packageinfo or channel");
            finish();
        } else if (notificationChannel == null || TextUtils.isEmpty(notificationChannel.getConversationId()) || this.mChannel.isDemoted()) {
            for (NotificationPreferenceController notificationPreferenceController : ((NotificationSettings) this).mControllers) {
                notificationPreferenceController.onResume(this.mAppRow, this.mChannel, this.mChannelGroup, null, null, this.mSuspendedAppsAdmin);
                notificationPreferenceController.displayPreference(getPreferenceScreen());
            }
            updatePreferenceStates();
        } else {
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
            subSettingLauncher.setDestination(ConversationNotificationSettings.class.getName());
            subSettingLauncher.setArguments(getArguments());
            subSettingLauncher.setExtras(getIntent() != null ? getIntent().getExtras() : null);
            subSettingLauncher.setSourceMetricsCategory(265);
            startActivity(subSettingLauncher.toIntent());
            finish();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        for (NotificationPreferenceController notificationPreferenceController : ((NotificationSettings) this).mControllers) {
            if (notificationPreferenceController instanceof PreferenceManager.OnActivityResultListener) {
                ((PreferenceManager.OnActivityResultListener) notificationPreferenceController).onActivityResult(i, i2, intent);
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.channel_notification_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((NotificationSettings) this).mControllers = arrayList;
        arrayList.add(new HeaderPreferenceController(context, this));
        ((NotificationSettings) this).mControllers.add(new BlockPreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new ImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new MinImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new HighImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new AllowSoundPreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new SoundPreferenceController(context, this, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new VibrationPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new AppLinkPreferenceController(context));
        ((NotificationSettings) this).mControllers.add(new DescriptionPreferenceController(context));
        ((NotificationSettings) this).mControllers.add(new VisibilityPreferenceController(context, new LockPatternUtils(context), this.mBackend));
        ((NotificationSettings) this).mControllers.add(new LightsPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new BadgePreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new DndPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new NotificationsOffPreferenceController(context));
        ((NotificationSettings) this).mControllers.add(new ConversationPromotePreferenceController(context, this, this.mBackend));
        return new ArrayList(((NotificationSettings) this).mControllers);
    }
}
