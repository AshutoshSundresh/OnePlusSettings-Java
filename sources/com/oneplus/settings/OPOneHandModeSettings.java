package com.oneplus.settings;

import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;

public class OPOneHandModeSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private SwitchPreference mSwitchOneHand;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_one_hand_settings);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("one_hand_mode");
        this.mSwitchOneHand = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        if (this.mSwitchOneHand != null) {
            boolean z = false;
            int intForUser = Settings.System.getIntForUser(getContentResolver(), "op_one_hand_mode_setting", 0, -2);
            SwitchPreference switchPreference2 = this.mSwitchOneHand;
            if (intForUser == 1) {
                z = true;
            }
            switchPreference2.setChecked(z);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.System.putIntForUser(getContentResolver(), "op_one_hand_mode_setting", ((Boolean) obj).booleanValue() ? 1 : 0, -2);
        return true;
    }
}
