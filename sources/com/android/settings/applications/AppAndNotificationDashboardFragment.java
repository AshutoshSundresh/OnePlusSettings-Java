package com.android.settings.applications;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.view.View;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.RecentAppStatsMixin;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.EmergencyBroadcastPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.utils.OPPreferenceDividerLine;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppAndNotificationDashboardFragment extends DashboardFragment implements RecentAppStatsMixin.RecentAppStatsListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.applications.AppAndNotificationDashboardFragment.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.app_and_notification;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return AppAndNotificationDashboardFragment.buildPreferenceControllers(context);
        }
    };
    private AllAppsInfoPreferenceController mAllAppsInfoPreferenceController;
    private RecentAppStatsMixin mRecentAppStatsMixin;
    private RecentAppsPreferenceController mRecentAppsPreferenceController;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AppAndNotifDashboard";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 748;
    }

    @Override // com.android.settings.applications.RecentAppStatsMixin.RecentAppStatsListener
    public void onReloadDataCompleted(List<UsageStats> list) {
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_apps_and_notifications;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.app_and_notification;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Preference findPreference = findPreference("manage_perms");
        if (findPreference != null && Build.VERSION.IS_CTA_BUILD && OPUtils.isActionExist(getActivity(), null, "com.oneplus.permissioncontroller.action.OPPERMISSION")) {
            findPreference.setIntent(new Intent("com.oneplus.permissioncontroller.action.OPPERMISSION"));
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (use(SpecialAppAccessPreferenceController.class) != null) {
            ((SpecialAppAccessPreferenceController) use(SpecialAppAccessPreferenceController.class)).setSession(getSettingsLifecycle());
        }
        this.mRecentAppStatsMixin = new RecentAppStatsMixin(context, 3);
        getSettingsLifecycle().addObserver(this.mRecentAppStatsMixin);
        this.mRecentAppStatsMixin.addListener(this);
        RecentAppsPreferenceController recentAppsPreferenceController = (RecentAppsPreferenceController) use(RecentAppsPreferenceController.class);
        this.mRecentAppsPreferenceController = recentAppsPreferenceController;
        recentAppsPreferenceController.setFragment(this);
        this.mRecentAppStatsMixin.addListener(this.mRecentAppsPreferenceController);
        AllAppsInfoPreferenceController allAppsInfoPreferenceController = (AllAppsInfoPreferenceController) use(AllAppsInfoPreferenceController.class);
        this.mAllAppsInfoPreferenceController = allAppsInfoPreferenceController;
        this.mRecentAppStatsMixin.addListener(allAppsInfoPreferenceController);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new OPDataUsageControlPreferenceController(context));
        arrayList.add(new OPPreferenceDividerLine(context));
        arrayList.add(new EmergencyBroadcastPreferenceController(context, "app_and_notif_cell_broadcast_settings"));
        return arrayList;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener, com.android.settings.dashboard.DashboardFragment
    public boolean onPreferenceTreeClick(Preference preference) {
        if (!"app_and_notif_cell_broadcast_settings".equals(preference.getKey())) {
            return super.onPreferenceTreeClick(preference);
        }
        try {
            Intent intent = new Intent();
            if (OPUtils.isApplicationEnabled(getContext(), "com.android.cellbroadcastreceiver")) {
                intent.setClassName("com.android.cellbroadcastreceiver", "com.android.cellbroadcastreceiver.CellBroadcastSettings");
            } else if (OPUtils.isApplicationEnabled(getContext(), "com.google.android.cellbroadcastreceiver")) {
                intent.setClassName("com.google.android.cellbroadcastreceiver", "com.android.cellbroadcastreceiver.CellBroadcastSettings");
            }
            intent.setFlags(268435456);
            getActivity().startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
