package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.CaptioningManager;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class CaptionMoreOptionsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.captioning_more_options);
    private CaptioningManager mCaptioningManager;
    private LocalePreference mLocale;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1820;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mCaptioningManager = (CaptioningManager) getSystemService("captioning");
        addPreferencesFromResource(C0019R$xml.captioning_more_options);
        initializeAllPreferences();
        updateAllPreferences();
        installUpdateListeners();
    }

    private void initializeAllPreferences() {
        this.mLocale = (LocalePreference) findPreference("captioning_locale");
    }

    private void installUpdateListeners() {
        this.mLocale.setOnPreferenceChangeListener(this);
    }

    private void updateAllPreferences() {
        String rawLocale = this.mCaptioningManager.getRawLocale();
        LocalePreference localePreference = this.mLocale;
        if (rawLocale == null) {
            rawLocale = "";
        }
        localePreference.setValue(rawLocale);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        if (this.mLocale != preference) {
            return true;
        }
        Settings.Secure.putString(contentResolver, "accessibility_captioning_locale", (String) obj);
        return true;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_caption;
    }
}
