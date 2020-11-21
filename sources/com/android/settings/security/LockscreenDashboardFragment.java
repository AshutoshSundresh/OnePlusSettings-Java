package com.android.settings.security;

import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.display.AmbientDisplayAlwaysOnPreferenceController;
import com.android.settings.display.AmbientDisplayNotificationsPreferenceController;
import com.android.settings.gestures.DoubleTapScreenPreferenceController;
import com.android.settings.gestures.PickupGesturePreferenceController;
import com.android.settings.notification.LockScreenNotificationPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.security.OwnerInfoPreferenceController;
import com.android.settings.security.screenlock.LockScreenPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class LockscreenDashboardFragment extends DashboardFragment implements OwnerInfoPreferenceController.OwnerInfoCallback {
    static final String KEY_ADD_USER_FROM_LOCK_SCREEN = "security_lockscreen_add_users_when_locked";
    static final String KEY_LOCK_SCREEN_NOTIFICATON = "security_setting_lock_screen_notif";
    static final String KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE = "security_setting_lock_screen_notif_work";
    static final String KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE_HEADER = "security_setting_lock_screen_notif_work_header";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.security_lockscreen_settings) {
        /* class com.android.settings.security.LockscreenDashboardFragment.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new LockScreenNotificationPreferenceController(context));
            arrayList.add(new OwnerInfoPreferenceController(context, null));
            return arrayList;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            nonIndexableKeys.add(LockscreenDashboardFragment.KEY_ADD_USER_FROM_LOCK_SCREEN);
            return nonIndexableKeys;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return new LockScreenPreferenceController(context, "anykey").isAvailable();
        }
    };
    private AmbientDisplayConfiguration mConfig;
    private OwnerInfoPreferenceController mOwnerInfoPreferenceController;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "LockscreenDashboardFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 882;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.security_lockscreen_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_lockscreen;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AmbientDisplayAlwaysOnPreferenceController) use(AmbientDisplayAlwaysOnPreferenceController.class)).setConfig(getConfig(context)).setCallback(new AmbientDisplayAlwaysOnPreferenceController.OnPreferenceChangedCallback() {
            /* class com.android.settings.security.$$Lambda$LockscreenDashboardFragment$iqjy4HrwhPteLl6wL10EqUYKM */

            @Override // com.android.settings.display.AmbientDisplayAlwaysOnPreferenceController.OnPreferenceChangedCallback
            public final void onPreferenceChanged() {
                LockscreenDashboardFragment.this.updatePreferenceStates();
            }
        });
        ((AmbientDisplayNotificationsPreferenceController) use(AmbientDisplayNotificationsPreferenceController.class)).setConfig(getConfig(context));
        ((DoubleTapScreenPreferenceController) use(DoubleTapScreenPreferenceController.class)).setConfig(getConfig(context));
        ((PickupGesturePreferenceController) use(PickupGesturePreferenceController.class)).setConfig(getConfig(context));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        Lifecycle settingsLifecycle = getSettingsLifecycle();
        LockScreenNotificationPreferenceController lockScreenNotificationPreferenceController = new LockScreenNotificationPreferenceController(context, KEY_LOCK_SCREEN_NOTIFICATON, KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE_HEADER, KEY_LOCK_SCREEN_NOTIFICATON_WORK_PROFILE);
        settingsLifecycle.addObserver(lockScreenNotificationPreferenceController);
        arrayList.add(lockScreenNotificationPreferenceController);
        OwnerInfoPreferenceController ownerInfoPreferenceController = new OwnerInfoPreferenceController(context, this);
        this.mOwnerInfoPreferenceController = ownerInfoPreferenceController;
        arrayList.add(ownerInfoPreferenceController);
        return arrayList;
    }

    @Override // com.android.settings.security.OwnerInfoPreferenceController.OwnerInfoCallback
    public void onOwnerInfoUpdated() {
        OwnerInfoPreferenceController ownerInfoPreferenceController = this.mOwnerInfoPreferenceController;
        if (ownerInfoPreferenceController != null) {
            ownerInfoPreferenceController.updateSummary();
        }
    }

    private AmbientDisplayConfiguration getConfig(Context context) {
        if (this.mConfig == null) {
            this.mConfig = new AmbientDisplayConfiguration(context);
        }
        return this.mConfig;
    }
}
