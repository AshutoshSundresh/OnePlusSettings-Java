package com.oneplus.settings;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.Arrays;
import java.util.List;

public class OPVideoGraphicsDynamicOptimizationSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.OPVideoGraphicsDynamicOptimizationSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            if (OPUtils.isSupportMotionGraphicsCompensation()) {
                searchIndexableResource.xmlResId = C0019R$xml.op_video_graphics_dynamic_optimization;
            }
            return Arrays.asList(searchIndexableResource);
        }
    };
    private Context mContext;
    private SwitchPreference mVideoEnhancerSwitch;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPVideoGraphicsDynamicOptimizationSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = SettingsBaseApplication.mApplication;
        SwitchPreference switchPreference = (SwitchPreference) findPreference("video_enhancer_switch");
        this.mVideoEnhancerSwitch = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        boolean z = false;
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "op_iris_video_sdr2hdr_status", 0);
        SwitchPreference switchPreference2 = this.mVideoEnhancerSwitch;
        if (i == 1) {
            z = true;
        }
        switchPreference2.setChecked(z);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_video_graphics_dynamic_optimization;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Settings.System.putInt(this.mContext.getContentResolver(), "op_iris_video_sdr2hdr_status", booleanValue ? 1 : 0);
        OPUtils.sendAnalytics("video_enhancer", "status", booleanValue ? "1" : "0");
        return true;
    }
}
