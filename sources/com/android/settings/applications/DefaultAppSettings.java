package com.android.settings.applications;

import android.content.Context;
import android.provider.SearchIndexableResource;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.assist.DefaultAssistPreferenceController;
import com.android.settings.applications.defaultapps.DefaultBrowserPreferenceController;
import com.android.settings.applications.defaultapps.DefaultEmergencyPreferenceController;
import com.android.settings.applications.defaultapps.DefaultHomePreferenceController;
import com.android.settings.applications.defaultapps.DefaultPhonePreferenceController;
import com.android.settings.applications.defaultapps.DefaultSmsPreferenceController;
import com.android.settings.applications.defaultapps.DefaultWorkBrowserPreferenceController;
import com.android.settings.applications.defaultapps.DefaultWorkPhonePreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.dashboard.SummaryLoader$SummaryProviderFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.defaultapp.DefaultAppLogic;
import com.oneplus.settings.defaultapp.controller.DefaultCameraController;
import com.oneplus.settings.defaultapp.controller.DefaultGalleryController;
import com.oneplus.settings.defaultapp.controller.DefaultMailController;
import com.oneplus.settings.defaultapp.controller.DefaultMusicController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultAppSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.applications.DefaultAppSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.app_default_settings;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            nonIndexableKeys.add("assist_and_voice_input");
            nonIndexableKeys.add("work_default_phone_app");
            nonIndexableKeys.add("work_default_browser");
            return nonIndexableKeys;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return DefaultAppSettings.buildPreferenceControllers(context);
        }
    };
    public static final SummaryLoader$SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader$SummaryProviderFactory() {
        /* class com.android.settings.applications.DefaultAppSettings.AnonymousClass2 */
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DefaultAppSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 130;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.app_default_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(new DefaultWorkPhonePreferenceController(context));
        arrayList2.add(new DefaultWorkBrowserPreferenceController(context));
        arrayList.addAll(arrayList2);
        arrayList.add(new PreferenceCategoryController(context, "work_app_defaults").setChildren(arrayList2));
        arrayList.add(new DefaultAssistPreferenceController(context, "assist_and_voice_input", false));
        arrayList.add(new DefaultBrowserPreferenceController(context));
        arrayList.add(new DefaultPhonePreferenceController(context));
        arrayList.add(new DefaultSmsPreferenceController(context));
        arrayList.add(new DefaultEmergencyPreferenceController(context));
        arrayList.add(new DefaultHomePreferenceController(context));
        DefaultAppLogic.getInstance(SettingsBaseApplication.mApplication).initDefaultAppSettings();
        arrayList.add(new DefaultCameraController(context));
        arrayList.add(new DefaultGalleryController(context));
        arrayList.add(new DefaultMusicController(context));
        arrayList.add(new DefaultMailController(context));
        return arrayList;
    }
}
