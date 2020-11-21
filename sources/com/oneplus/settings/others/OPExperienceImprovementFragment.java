package com.oneplus.settings.others;

import android.content.Context;
import android.provider.SearchIndexableResource;
import android.util.OpFeatures;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.oneplus.settings.utils.OPPreferenceDividerLine;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPExperienceImprovementFragment extends DashboardFragment {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.others.OPExperienceImprovementFragment.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_experience_improvement_programs;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (OpFeatures.isSupport(new int[]{0}) || !OPUtils.isAppExist(context, "com.heytap.mcs")) {
                nonIndexableKeys.add("oneplus_service_messaging");
            }
            if (OpFeatures.isSupport(new int[]{0})) {
                nonIndexableKeys.add("built_in_app_updates");
            }
            if (!OPUtils.isAppExist(context, "com.oneplus.appupgrader")) {
                nonIndexableKeys.add("built_in_app_updates");
            }
            return nonIndexableKeys;
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPExperienceImprovementFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_experience_improvement_programs;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        OPReceiveNotificationsSwitchPreferenceController oPReceiveNotificationsSwitchPreferenceController = new OPReceiveNotificationsSwitchPreferenceController(context);
        OPUserExperienceSwitchPreferenceController oPUserExperienceSwitchPreferenceController = new OPUserExperienceSwitchPreferenceController(context);
        OPSystemStabilitySwitchPreferenceController oPSystemStabilitySwitchPreferenceController = new OPSystemStabilitySwitchPreferenceController(context);
        OPSplashScreeenInfoServiceSwitchPreferenceController oPSplashScreeenInfoServiceSwitchPreferenceController = new OPSplashScreeenInfoServiceSwitchPreferenceController(context);
        OPServiceMessageSwitchPreferenceController oPServiceMessageSwitchPreferenceController = new OPServiceMessageSwitchPreferenceController(context);
        OPServiceAppUpdatePreferenceController oPServiceAppUpdatePreferenceController = new OPServiceAppUpdatePreferenceController(context);
        arrayList.add(new OPPreferenceDividerLine(context));
        getSettingsLifecycle().addObserver(oPReceiveNotificationsSwitchPreferenceController);
        getSettingsLifecycle().addObserver(oPUserExperienceSwitchPreferenceController);
        getSettingsLifecycle().addObserver(oPSystemStabilitySwitchPreferenceController);
        getSettingsLifecycle().addObserver(oPSplashScreeenInfoServiceSwitchPreferenceController);
        getSettingsLifecycle().addObserver(oPServiceMessageSwitchPreferenceController);
        getSettingsLifecycle().addObserver(oPServiceAppUpdatePreferenceController);
        arrayList.add(oPReceiveNotificationsSwitchPreferenceController);
        arrayList.add(oPUserExperienceSwitchPreferenceController);
        arrayList.add(oPSystemStabilitySwitchPreferenceController);
        arrayList.add(oPSplashScreeenInfoServiceSwitchPreferenceController);
        arrayList.add(oPServiceMessageSwitchPreferenceController);
        arrayList.add(oPServiceAppUpdatePreferenceController);
        return arrayList;
    }
}
