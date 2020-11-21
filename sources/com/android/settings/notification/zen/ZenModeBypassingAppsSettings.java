package com.android.settings.notification.zen;

import android.app.Application;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0019R$xml;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ZenModeBypassingAppsSettings extends ZenModeSettingsBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.zen_mode_bypassing_apps) {
        /* class com.android.settings.notification.zen.ZenModeBypassingAppsSettings.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ZenModeBypassingAppsSettings.buildPreferenceControllers(context, null, null, null);
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ZenBypassingApps";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1588;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        FragmentActivity activity = getActivity();
        return buildPreferenceControllers(context, activity != null ? activity.getApplication() : null, this, new NotificationBackend());
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Application application, Fragment fragment, NotificationBackend notificationBackend) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ZenModeAllBypassingAppsPreferenceController(context, application, fragment, notificationBackend));
        arrayList.add(new ZenModeAddBypassingAppsPreferenceController(context, application, fragment, notificationBackend));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.zen_mode_bypassing_apps;
    }
}
