package com.android.settings.notification.app;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0019R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ConversationNotificationSettings extends NotificationSettings {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ConvoSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1830;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.notification.app.NotificationSettings, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null || this.mChannel == null) {
            Log.w("ConvoSettings", "Missing package or uid or packageinfo or channel");
            finish();
            return;
        }
        for (NotificationPreferenceController notificationPreferenceController : ((NotificationSettings) this).mControllers) {
            notificationPreferenceController.onResume(this.mAppRow, this.mChannel, this.mChannelGroup, this.mConversationDrawable, this.mConversationInfo, this.mSuspendedAppsAdmin);
            notificationPreferenceController.displayPreference(getPreferenceScreen());
        }
        updatePreferenceStates();
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
        return C0019R$xml.conversation_notification_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((NotificationSettings) this).mControllers = arrayList;
        arrayList.add(new ConversationHeaderPreferenceController(context, this));
        ((NotificationSettings) this).mControllers.add(new ConversationPriorityPreferenceController(context, this.mBackend, this.mDependentFieldListener));
        ((NotificationSettings) this).mControllers.add(new HighImportancePreferenceController(context, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new SoundPreferenceController(context, this, this.mDependentFieldListener, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new VibrationPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new VisibilityPreferenceController(context, new LockPatternUtils(context), this.mBackend));
        ((NotificationSettings) this).mControllers.add(new LightsPreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new BadgePreferenceController(context, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new NotificationsOffPreferenceController(context));
        ((NotificationSettings) this).mControllers.add(new BubblePreferenceController(context, getChildFragmentManager(), this.mBackend, false, null));
        ((NotificationSettings) this).mControllers.add(new ConversationDemotePreferenceController(context, this, this.mBackend));
        ((NotificationSettings) this).mControllers.add(new BubbleCategoryPreferenceController(context));
        ((NotificationSettings) this).mControllers.add(new BubbleLinkPreferenceController(context));
        return new ArrayList(((NotificationSettings) this).mControllers);
    }
}
