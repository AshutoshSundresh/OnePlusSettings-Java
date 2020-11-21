package com.oneplus.settings;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.preference.Preference;
import com.android.settings.C0019R$xml;
import com.oneplus.settings.ui.OPCustomClockPreference;

public class OPCustomClockSettings extends OPQuitConfirmFragment implements Preference.OnPreferenceClickListener, OnPressListener {
    private OPCustomClockPreference mOPCustomClockPreference;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPCustomClockSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setOnPressListener(this);
        OPCustomClockPreference oPCustomClockPreference = (OPCustomClockPreference) findPreference("op_custom_clock_preference");
        this.mOPCustomClockPreference = oPCustomClockPreference;
        oPCustomClockPreference.setSettingsPreferenceFragment(this);
    }

    @Override // com.oneplus.settings.OnPressListener
    public void onCancelPressed() {
        finish();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_custom_clock_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.OPQuitConfirmFragment
    public boolean needShowWarningDialog() {
        OPCustomClockPreference oPCustomClockPreference = this.mOPCustomClockPreference;
        if (oPCustomClockPreference != null) {
            return oPCustomClockPreference.needShowWarningDialog();
        }
        return false;
    }
}
