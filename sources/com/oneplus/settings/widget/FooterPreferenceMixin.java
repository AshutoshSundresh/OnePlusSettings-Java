package com.oneplus.settings.widget;

import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.SetPreferenceScreen;

public class FooterPreferenceMixin implements LifecycleObserver, SetPreferenceScreen {
    private OPFooterPreference mFooterPreference;

    @Override // com.android.settingslib.core.lifecycle.events.SetPreferenceScreen
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        OPFooterPreference oPFooterPreference = this.mFooterPreference;
        if (oPFooterPreference != null) {
            preferenceScreen.addPreference(oPFooterPreference);
        }
    }
}
