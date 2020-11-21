package com.oneplus.settings.better;

import android.app.Application;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.ui.RadioButtonPreference;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;

public class OPGameModeBatterySaver extends SettingsPreferenceFragment implements RadioButtonPreference.OnClickListener {
    private RadioButtonPreference mBatterySaveCloseButton;
    private RadioButtonPreference mBatterySaveHighButton;
    private RadioButtonPreference mBatterySaveLightButton;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_game_mode_battery_saver);
        Application application = SettingsBaseApplication.mApplication;
        this.mBatterySaveCloseButton = (RadioButtonPreference) findPreference("battery_saver_close");
        this.mBatterySaveLightButton = (RadioButtonPreference) findPreference("battery_saver_light");
        this.mBatterySaveHighButton = (RadioButtonPreference) findPreference("battery_saver_high");
        this.mBatterySaveCloseButton.setOnClickListener(this);
        this.mBatterySaveLightButton.setOnClickListener(this);
        this.mBatterySaveHighButton.setOnClickListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (this.mBatterySaveCloseButton != null && this.mBatterySaveLightButton != null && this.mBatterySaveHighButton != null) {
            String stringForUser = Settings.System.getStringForUser(getContentResolver(), "game_mode_battery_saver", -2);
            this.mBatterySaveCloseButton.setChecked("0_0".equalsIgnoreCase(stringForUser) || TextUtils.isEmpty(stringForUser));
            this.mBatterySaveLightButton.setChecked("56_0".equalsIgnoreCase(stringForUser));
            this.mBatterySaveHighButton.setChecked("56_30".equalsIgnoreCase(stringForUser));
        }
    }

    @Override // com.android.settings.ui.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        RadioButtonPreference radioButtonPreference2 = this.mBatterySaveCloseButton;
        if (radioButtonPreference == radioButtonPreference2) {
            radioButtonPreference2.setChecked(true);
            this.mBatterySaveLightButton.setChecked(false);
            this.mBatterySaveHighButton.setChecked(false);
            Settings.System.putStringForUser(getContentResolver(), "game_mode_battery_saver", "0_0", -2);
            OPUtils.sendAppTracker("game_mode_battery_saver", "0_0");
        } else if (radioButtonPreference == this.mBatterySaveLightButton) {
            radioButtonPreference2.setChecked(false);
            this.mBatterySaveLightButton.setChecked(true);
            this.mBatterySaveHighButton.setChecked(false);
            Settings.System.putStringForUser(getContentResolver(), "game_mode_battery_saver", "56_0", -2);
            OPUtils.sendAppTracker("game_mode_battery_saver", "56_0");
        } else if (radioButtonPreference == this.mBatterySaveHighButton) {
            radioButtonPreference2.setChecked(false);
            this.mBatterySaveLightButton.setChecked(false);
            this.mBatterySaveHighButton.setChecked(true);
            Settings.System.putStringForUser(getContentResolver(), "game_mode_battery_saver", "56_30", -2);
            OPUtils.sendAppTracker("game_mode_battery_saver", "56_30");
        }
    }
}
