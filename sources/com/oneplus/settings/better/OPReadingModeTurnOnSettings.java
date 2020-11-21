package com.oneplus.settings.better;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.RadioButtonPreference;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.widget.OPFooterPreference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPReadingModeTurnOnSettings extends DashboardFragment implements RadioButtonPreference.OnClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.better.OPReadingModeTurnOnSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_reading_mode_turn_on_settings;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            return new ArrayList();
        }
    };
    private RadioButtonPreference mAskPreference;
    private RadioButtonPreference mChromaticPreference;
    private Context mContext;
    private OPFooterPreference mFooterPreference;
    private RadioButtonPreference mMonoPreference;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPReadingModeTurnOnSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mContext = getActivity();
        this.mAskPreference = (RadioButtonPreference) findPreference("ask");
        this.mChromaticPreference = (RadioButtonPreference) findPreference("chromatic");
        this.mMonoPreference = (RadioButtonPreference) findPreference("mono");
        this.mAskPreference.setOnClickListener(this);
        this.mChromaticPreference.setOnClickListener(this);
        this.mMonoPreference.setOnClickListener(this);
        OPFooterPreference createFooterPreference = this.mFooterPreferenceMixin.createFooterPreference();
        this.mFooterPreference = createFooterPreference;
        createFooterPreference.setTitle(C0017R$string.oneplus_reading_mode_turn_on_tips);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateDefaultSelectStatus(Settings.System.getInt(this.mContext.getContentResolver(), "reading_mode_option_manual", 0));
    }

    @Override // com.android.settings.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        String key = radioButtonPreference.getKey();
        int i = 0;
        if (!"ask".equals(key)) {
            if ("chromatic".equals(key)) {
                if (Settings.System.getIntForUser(this.mContext.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS, 0, -2) != 0) {
                    Settings.System.putStringForUser(this.mContext.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS_MANUAL, "force-on-color", -2);
                }
                i = 2;
            } else if ("mono".equals(key)) {
                if (Settings.System.getIntForUser(this.mContext.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS, 0, -2) != 0) {
                    Settings.System.putStringForUser(this.mContext.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS_MANUAL, "force-on", -2);
                }
                i = 1;
            }
        }
        OPUtils.sendAnalytics("read_def", "status", Integer.toString(i + 1));
        Settings.System.putInt(this.mContext.getContentResolver(), "reading_mode_option_manual", i);
        updateDefaultSelectStatus(i);
    }

    private void updateDefaultSelectStatus(int i) {
        boolean z = false;
        this.mAskPreference.setChecked(i == 0);
        this.mChromaticPreference.setChecked(i == 2);
        RadioButtonPreference radioButtonPreference = this.mMonoPreference;
        if (i == 1) {
            z = true;
        }
        radioButtonPreference.setChecked(z);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_reading_mode_turn_on_settings;
    }
}
