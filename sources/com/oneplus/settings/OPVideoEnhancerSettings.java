package com.oneplus.settings;

import android.content.Context;
import android.provider.SearchIndexableResource;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPVideoEnhancerSettings extends DashboardFragment {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.OPVideoEnhancerSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_video_enhancer;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return OPVideoEnhancerSettings.buildPreferenceControllers(context, null);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            ArrayList arrayList = new ArrayList();
            if (!OPUtils.isSupportVideoEnhancer() || OPUtils.isSupportMotionGraphicsCompensation()) {
                arrayList.add("video_enhancer_switch");
            }
            return arrayList;
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPVideoEnhancerSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_video_enhancer;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new OPVideoEnhancerSwitchPreferenceController(context));
        arrayList.add(new OPVideoEnhancerPreferenceController(context, lifecycle));
        return arrayList;
    }
}
