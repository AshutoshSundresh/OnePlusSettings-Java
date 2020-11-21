package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.CaptioningManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.google.common.primitives.Floats;
import java.util.ArrayList;
import java.util.List;

public class CaptionPropertiesFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.captioning_settings);
    private CaptioningManager mCaptioningManager;
    private float[] mFontSizeValuesArray;
    private Preference mMoreOptions;
    private final List<Preference> mPreferenceList = new ArrayList();
    private SwitchPreference mSwitch;
    private Preference mTextAppearance;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 3;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mCaptioningManager = (CaptioningManager) getSystemService("captioning");
        addPreferencesFromResource(C0019R$xml.captioning_settings);
        initializeAllPreferences();
        installUpdateListeners();
        initFontSizeValuesArray();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateAllPreferences();
    }

    private void initializeAllPreferences() {
        this.mSwitch = (SwitchPreference) findPreference("captioning_preference_switch");
        this.mTextAppearance = findPreference("captioning_caption_appearance");
        this.mMoreOptions = findPreference("captioning_more_options");
        this.mPreferenceList.add(this.mTextAppearance);
        this.mPreferenceList.add(this.mMoreOptions);
    }

    private void installUpdateListeners() {
        this.mSwitch.setOnPreferenceChangeListener(this);
    }

    private void initFontSizeValuesArray() {
        String[] stringArray = getPrefContext().getResources().getStringArray(C0003R$array.captioning_font_size_selector_values);
        int length = stringArray.length;
        this.mFontSizeValuesArray = new float[length];
        for (int i = 0; i < length; i++) {
            this.mFontSizeValuesArray[i] = Float.parseFloat(stringArray[i]);
        }
    }

    private void updateAllPreferences() {
        this.mSwitch.setChecked(this.mCaptioningManager.isEnabled());
        this.mTextAppearance.setSummary(geTextAppearanceSummary(getPrefContext()));
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        if (this.mSwitch != preference) {
            return true;
        }
        Settings.Secure.putInt(contentResolver, "accessibility_captioning_enabled", ((Boolean) obj).booleanValue() ? 1 : 0);
        return true;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_caption;
    }

    private CharSequence geTextAppearanceSummary(Context context) {
        String[] stringArray = context.getResources().getStringArray(C0003R$array.captioning_font_size_selector_summaries);
        int indexOf = Floats.indexOf(this.mFontSizeValuesArray, this.mCaptioningManager.getFontScale());
        if (indexOf == -1) {
            indexOf = 0;
        }
        return stringArray[indexOf];
    }
}
