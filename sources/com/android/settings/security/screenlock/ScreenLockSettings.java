package com.android.settings.security.screenlock;

import android.content.Context;
import android.os.UserHandle;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.LockScreenNotificationPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.security.OwnerInfoPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.oneplus.settings.security.DisableQuickSettingsWhenLockedPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ScreenLockSettings extends DashboardFragment implements OwnerInfoPreferenceController.OwnerInfoCallback {
    private static final int MY_USER_ID = UserHandle.myUserId();
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.screen_lock_settings) {
        /* class com.android.settings.security.screenlock.ScreenLockSettings.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ScreenLockSettings.buildPreferenceControllers(context, null, null, new LockPatternUtils(context));
        }
    };
    private LockPatternUtils mLockPatternUtils;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ScreenLockSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1265;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.screen_lock_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        this.mLockPatternUtils = new LockPatternUtils(context);
        return buildPreferenceControllers(context, this, getSettingsLifecycle(), this.mLockPatternUtils);
    }

    @Override // com.android.settings.security.OwnerInfoPreferenceController.OwnerInfoCallback
    public void onOwnerInfoUpdated() {
        ((OwnerInfoPreferenceController) use(OwnerInfoPreferenceController.class)).updateSummary();
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, DashboardFragment dashboardFragment, Lifecycle lifecycle, LockPatternUtils lockPatternUtils) {
        ArrayList arrayList = new ArrayList();
        LockScreenNotificationPreferenceController lockScreenNotificationPreferenceController = new LockScreenNotificationPreferenceController(context, "security_setting_lock_screen_notif", "security_setting_lock_screen_notif_work_header", "security_setting_lock_screen_notif_work");
        if (lifecycle != null) {
            lifecycle.addObserver(lockScreenNotificationPreferenceController);
        }
        arrayList.add(lockScreenNotificationPreferenceController);
        arrayList.add(new PatternVisiblePreferenceController(context, MY_USER_ID, lockPatternUtils));
        arrayList.add(new PowerButtonInstantLockPreferenceController(context, MY_USER_ID, lockPatternUtils));
        arrayList.add(new LockAfterTimeoutPreferenceController(context, MY_USER_ID, lockPatternUtils));
        arrayList.add(new OwnerInfoPreferenceController(context, dashboardFragment));
        arrayList.add(new DisableQuickSettingsWhenLockedPreferenceController(context, MY_USER_ID, lockPatternUtils));
        return arrayList;
    }
}
