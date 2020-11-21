package com.android.settings;

import android.os.Bundle;
import android.os.UserManager;
import androidx.preference.PreferenceScreen;

public class TestingSettings extends SettingsPreferenceFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 89;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.testing_settings);
        if (!UserManager.get(getContext()).isAdminUser()) {
            getPreferenceScreen().removePreference((PreferenceScreen) findPreference("radio_info_settings"));
        }
    }
}
