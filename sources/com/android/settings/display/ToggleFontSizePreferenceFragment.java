package com.android.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import com.android.settings.C0003R$array;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.search.BaseSearchIndexProvider;

public class ToggleFontSizePreferenceFragment extends PreviewSeekBarPreferenceFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.display.ToggleFontSizePreferenceFragment.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return false;
        }
    };
    private float[] mValues;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 340;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    public int getActivityLayoutResId() {
        return C0012R$layout.font_size_activity;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    public int[] getPreviewSampleResIds() {
        return new int[]{C0012R$layout.font_size_preview};
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Resources resources = getContext().getResources();
        ContentResolver contentResolver = getContext().getContentResolver();
        this.mEntries = resources.getStringArray(C0003R$array.entries_font_size);
        String[] stringArray = resources.getStringArray(C0003R$array.entryvalues_font_size);
        this.mInitialIndex = fontSizeValueToIndex(Settings.System.getFloat(contentResolver, "font_scale", 1.0f), stringArray);
        this.mValues = new float[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            this.mValues[i] = Float.parseFloat(stringArray[i]);
        }
        getActivity().setTitle(C0017R$string.title_font_size);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    public Configuration createConfig(Configuration configuration, int i) {
        Configuration configuration2 = new Configuration(configuration);
        configuration2.fontScale = this.mValues[i];
        return configuration2;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    public void commit() {
        if (getContext() != null) {
            Settings.System.putFloat(getContext().getContentResolver(), "font_scale", this.mValues[this.mCurrentIndex]);
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_font_size;
    }

    public static int fontSizeValueToIndex(float f, String[] strArr) {
        float parseFloat = Float.parseFloat(strArr[0]);
        int i = 1;
        while (i < strArr.length) {
            float parseFloat2 = Float.parseFloat(strArr[i]);
            if (f < parseFloat + ((parseFloat2 - parseFloat) * 0.5f)) {
                return i - 1;
            }
            i++;
            parseFloat = parseFloat2;
        }
        return strArr.length - 1;
    }
}
