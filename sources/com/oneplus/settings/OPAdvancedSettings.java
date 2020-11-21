package com.oneplus.settings;

import android.content.Context;
import android.provider.SearchIndexableData;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexableRaw;
import com.oneplus.settings.controllers.OPNotchDisplayGuidePreferenceController;
import com.oneplus.settings.controllers.OPScreenColorModePreferenceController;
import com.oneplus.settings.controllers.OPScreenRefreshRatePreferenceController;
import com.oneplus.settings.controllers.OPScreenResolutionAdjustPreferenceController;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPAdvancedSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.op_advanced_settings) {
        /* class com.oneplus.settings.OPAdvancedSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (new OPNotchDisplayGuidePreferenceController(context, "oneplus_notch_display_guide").isAvailable() && OPUtils.isSupportHolePunchFrontCam()) {
                SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                searchIndexableRaw.title = context.getString(C0017R$string.oneplus_front_camera_display_title);
                ((SearchIndexableData) searchIndexableRaw).key = "oneplus_front_camera_display_guide";
                searchIndexableRaw.screenTitle = context.getString(C0017R$string.op_display_advanced_settings);
                arrayList.add(searchIndexableRaw);
            }
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return OPAdvancedSettings.buildPreferenceControllers(context, null, null);
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPAdvancedSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_advanced_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle, OPAdvancedSettings oPAdvancedSettings) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new OPScreenColorModePreferenceController(context, lifecycle));
        arrayList.add(new OPScreenResolutionAdjustPreferenceController(context, lifecycle));
        arrayList.add(new OPScreenRefreshRatePreferenceController(context, lifecycle));
        return arrayList;
    }
}
