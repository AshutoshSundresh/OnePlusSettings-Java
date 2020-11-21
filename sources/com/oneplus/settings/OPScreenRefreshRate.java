package com.oneplus.settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.RadioButtonPreference;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.widget.OPFooterPreference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPScreenRefreshRate extends SettingsPreferenceFragment implements RadioButtonPreference.OnClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.OPScreenRefreshRate.AnonymousClass2 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            if (OPUtils.isSupportScreenRefreshRate()) {
                searchIndexableResource.xmlResId = C0019R$xml.op_screen_refresh_rate_select;
            }
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            ArrayList arrayList = new ArrayList();
            if (OPUtils.isSupportHighVsync()) {
                arrayList.add("op_auto_mode");
            } else {
                arrayList.add("high_vsync_mode");
            }
            return arrayList;
        }
    };
    private RadioButtonPreference m60HzMode;
    private RadioButtonPreference mAutoMode;
    private Context mContext;
    private int mEnterValue;
    private OPFooterPreference mFooterPreference;
    private Handler mHandler = new Handler();
    private RadioButtonPreference mHighVsyncMode;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_screen_refresh_rate_select);
        this.mContext = SettingsBaseApplication.mApplication;
        this.mAutoMode = (RadioButtonPreference) findPreference("op_auto_mode");
        this.m60HzMode = (RadioButtonPreference) findPreference("op_60hz_mode");
        this.mHighVsyncMode = (RadioButtonPreference) findPreference("high_vsync_mode");
        this.mAutoMode.setOnClickListener(this);
        this.m60HzMode.setOnClickListener(this);
        this.mHighVsyncMode.setOnClickListener(this);
        if (OPUtils.isSupportHighVsync()) {
            this.mAutoMode.setVisible(false);
        } else {
            this.mHighVsyncMode.setVisible(false);
        }
        OPFooterPreference oPFooterPreference = (OPFooterPreference) findPreference("footer_preference");
        this.mFooterPreference = oPFooterPreference;
        updateFooterPreference(oPFooterPreference);
    }

    private void updateFooterPreference(Preference preference) {
        if (preference != null) {
            String string = getString(C0017R$string.oneplus_screen_refresh_rate_info);
            if (OPUtils.isSupportHighVsync()) {
                string = string.replace("90", "120");
            }
            preference.setTitle(string);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_screen_refresh_rate", 2);
        boolean z = false;
        this.mAutoMode.setChecked(i == 2);
        this.m60HzMode.setChecked(i == 1);
        RadioButtonPreference radioButtonPreference = this.mHighVsyncMode;
        if (i == 2) {
            z = true;
        }
        radioButtonPreference.setChecked(z);
        this.mEnterValue = Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_screen_refresh_rate", 2);
    }

    @Override // com.android.settings.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        RadioButtonPreference radioButtonPreference2 = this.mAutoMode;
        if (radioButtonPreference == radioButtonPreference2) {
            radioButtonPreference2.setChecked(true);
            this.m60HzMode.setChecked(false);
            Settings.Global.putInt(this.mContext.getContentResolver(), "oneplus_screen_refresh_rate", 2);
        } else if (radioButtonPreference == this.m60HzMode) {
            radioButtonPreference2.setChecked(false);
            this.m60HzMode.setChecked(true);
            this.mHighVsyncMode.setChecked(false);
            Settings.Global.putInt(this.mContext.getContentResolver(), "oneplus_screen_refresh_rate", 1);
        } else {
            RadioButtonPreference radioButtonPreference3 = this.mHighVsyncMode;
            if (radioButtonPreference == radioButtonPreference3) {
                radioButtonPreference3.setChecked(true);
                this.m60HzMode.setChecked(false);
                Settings.Global.putInt(this.mContext.getContentResolver(), "oneplus_screen_refresh_rate", 2);
            }
        }
        delayRefreshUI();
    }

    private void delayRefreshUI() {
        this.mAutoMode.setEnabled(false);
        this.m60HzMode.setEnabled(false);
        this.mHighVsyncMode.setEnabled(false);
        this.mHandler.postDelayed(new Runnable() {
            /* class com.oneplus.settings.OPScreenRefreshRate.AnonymousClass1 */

            public void run() {
                OPScreenRefreshRate.this.mAutoMode.setEnabled(true);
                OPScreenRefreshRate.this.m60HzMode.setEnabled(true);
                OPScreenRefreshRate.this.mHighVsyncMode.setEnabled(true);
            }
        }, 1000);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_screen_refresh_rate", 2);
        if (i == this.mEnterValue) {
            return;
        }
        if (i == 2) {
            OPUtils.sendAnalytics("refresh rate", "status", "0");
        } else if (i == 1) {
            OPUtils.sendAnalytics("refresh rate", "status", "1");
        }
    }
}
