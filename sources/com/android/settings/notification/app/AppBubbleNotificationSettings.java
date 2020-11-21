package com.android.settings.notification.app;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.C0019R$xml;
import com.android.settings.notification.AppBubbleListPreferenceController;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.GlobalBubblePermissionObserverMixin;
import com.android.settings.notification.app.NotificationSettings;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class AppBubbleNotificationSettings extends NotificationSettings implements GlobalBubblePermissionObserverMixin.Listener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.notification.app.AppBubbleNotificationSettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return false;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return new ArrayList(AppBubbleNotificationSettings.getPreferenceControllers(context, null, null));
        }
    };
    private GlobalBubblePermissionObserverMixin mObserverMixin;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AppBubNotiSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1700;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.app_bubble_notification_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ((NotificationSettings) this).mControllers = getPreferenceControllers(context, this, this.mDependentFieldListener);
        return new ArrayList(((NotificationSettings) this).mControllers);
    }

    protected static List<NotificationPreferenceController> getPreferenceControllers(Context context, AppBubbleNotificationSettings appBubbleNotificationSettings, NotificationSettings.DependentFieldListener dependentFieldListener) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new HeaderPreferenceController(context, appBubbleNotificationSettings));
        arrayList.add(new BubblePreferenceController(context, appBubbleNotificationSettings != null ? appBubbleNotificationSettings.getChildFragmentManager() : null, new NotificationBackend(), true, dependentFieldListener));
        arrayList.add(new AppBubbleListPreferenceController(context, new NotificationBackend()));
        return arrayList;
    }

    @Override // com.android.settings.notification.app.GlobalBubblePermissionObserverMixin.Listener
    public void onGlobalBubblePermissionChanged() {
        updatePreferenceStates();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.notification.app.NotificationSettings, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null) {
            Log.w("AppBubNotiSettings", "Missing package or uid or packageinfo");
            finish();
            return;
        }
        for (NotificationPreferenceController notificationPreferenceController : ((NotificationSettings) this).mControllers) {
            notificationPreferenceController.onResume(this.mAppRow, null, null, null, null, this.mSuspendedAppsAdmin);
            notificationPreferenceController.displayPreference(getPreferenceScreen());
        }
        updatePreferenceStates();
        GlobalBubblePermissionObserverMixin globalBubblePermissionObserverMixin = new GlobalBubblePermissionObserverMixin(getContext(), this);
        this.mObserverMixin = globalBubblePermissionObserverMixin;
        globalBubblePermissionObserverMixin.onStart();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        this.mObserverMixin.onStop();
        super.onPause();
    }
}
