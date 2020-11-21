package com.oneplus.settings.widget;

import android.content.Context;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.SetPreferenceScreen;

public class FooterPreferenceMixinCompat implements LifecycleObserver, SetPreferenceScreen {
    private OPFooterPreference mFooterPreference;
    private final PreferenceFragmentCompat mFragment;

    public FooterPreferenceMixinCompat(PreferenceFragmentCompat preferenceFragmentCompat, Lifecycle lifecycle) {
        this.mFragment = preferenceFragmentCompat;
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.SetPreferenceScreen
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        OPFooterPreference oPFooterPreference = this.mFooterPreference;
        if (oPFooterPreference != null) {
            preferenceScreen.addPreference(oPFooterPreference);
        }
    }

    public OPFooterPreference createFooterPreference() {
        PreferenceScreen preferenceScreen = this.mFragment.getPreferenceScreen();
        OPFooterPreference oPFooterPreference = this.mFooterPreference;
        if (!(oPFooterPreference == null || preferenceScreen == 0)) {
            preferenceScreen.removePreference(oPFooterPreference);
        }
        OPFooterPreference oPFooterPreference2 = new OPFooterPreference(getPrefContext());
        this.mFooterPreference = oPFooterPreference2;
        if (preferenceScreen != null) {
            preferenceScreen.addPreference(oPFooterPreference2);
        }
        return this.mFooterPreference;
    }

    private Context getPrefContext() {
        return this.mFragment.getPreferenceManager().getContext();
    }

    public boolean hasFooter() {
        return this.mFooterPreference != null;
    }
}
