package com.android.settings.display;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import com.android.settings.C0005R$bool;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.display.DisplayDensityConfiguration;
import com.android.settingslib.display.DisplayDensityUtils;
import com.oneplus.settings.utils.OPDisplayDensityUtils;

public class ScreenZoomSettings extends PreviewSeekBarPreferenceFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.display.ScreenZoomSettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return false;
        }
    };
    private int mDefaultDensity;
    private int[] mValues;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 339;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    public int getActivityLayoutResId() {
        return C0012R$layout.screen_zoom_activity;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    public int[] getPreviewSampleResIds() {
        if (getContext().getResources().getBoolean(C0005R$bool.config_enable_extra_screen_zoom_preview)) {
            return new int[]{C0012R$layout.screen_zoom_preview_1, C0012R$layout.screen_zoom_preview_settings};
        }
        return new int[]{C0012R$layout.screen_zoom_preview_1};
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (new DisplayDensityUtils(getContext()).getCurrentIndex() < 0) {
            int i = getResources().getDisplayMetrics().densityDpi;
            this.mValues = new int[]{i};
            this.mEntries = new String[]{getString(DisplayDensityUtils.SUMMARY_DEFAULT)};
            this.mInitialIndex = 0;
            this.mDefaultDensity = i;
        } else {
            OPDisplayDensityUtils oPDisplayDensityUtils = new OPDisplayDensityUtils(getContext());
            this.mValues = oPDisplayDensityUtils.getValues();
            this.mEntries = oPDisplayDensityUtils.getEntries();
            this.mInitialIndex = oPDisplayDensityUtils.getCurrentIndex();
            this.mDefaultDensity = oPDisplayDensityUtils.getDefaultDensity();
        }
        getActivity().setTitle(C0017R$string.screen_zoom_title);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    public Configuration createConfig(Configuration configuration, int i) {
        Configuration configuration2 = new Configuration(configuration);
        configuration2.densityDpi = this.mValues[i];
        return configuration2;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    public void commit() {
        int i = this.mValues[this.mCurrentIndex];
        if (i == this.mDefaultDensity) {
            DisplayDensityConfiguration.clearForcedDisplayDensity(0);
        } else {
            DisplayDensityConfiguration.setForcedDisplayDensity(0, i);
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_display_size;
    }
}
