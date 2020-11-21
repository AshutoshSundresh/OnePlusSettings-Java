package com.android.settings.privacy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.LockScreenNotificationPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.oneplus.settings.controllers.OPDashboardTilePlaceholderPreferenceCategoryController;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class PrivacyDashboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.privacy_dashboard_settings) {
        /* class com.android.settings.privacy.PrivacyDashboardFragment.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return PrivacyDashboardFragment.buildPreferenceControllers(context, null, PrivacyDashboardFragment.class.getName());
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PrivacyDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1587;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Preference findPreference = findPreference("privacy_manage_perms");
        if (findPreference != null && Build.VERSION.IS_CTA_BUILD && OPUtils.isActionExist(getActivity(), null, "com.oneplus.permissioncontroller.action.OPPERMISSION")) {
            findPreference.setIntent(new Intent("com.oneplus.permissioncontroller.action.OPPERMISSION"));
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.privacy_dashboard_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_privacy_dashboard;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), PrivacyDashboardFragment.class.getName());
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle, String str) {
        ArrayList arrayList = new ArrayList();
        LockScreenNotificationPreferenceController lockScreenNotificationPreferenceController = new LockScreenNotificationPreferenceController(context, "privacy_lock_screen_notifications", "privacy_work_profile_notifications_category", "privacy_lock_screen_work_profile_notifications");
        if (lifecycle != null) {
            lifecycle.addObserver(lockScreenNotificationPreferenceController);
        }
        arrayList.add(lockScreenNotificationPreferenceController);
        arrayList.add(new OPDashboardTilePlaceholderPreferenceCategoryController(context, "google_services", str));
        return arrayList;
    }
}
