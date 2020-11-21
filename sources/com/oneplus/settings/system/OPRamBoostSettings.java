package com.oneplus.settings.system;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;

public class OPRamBoostSettings extends SettingsPreferenceFragment {
    private Context mContext;
    private SwitchPreference mSwitchPreference;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        addPreferencesFromResource(C0019R$xml.op_ramboost_settings);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mSwitchPreference = (SwitchPreference) preferenceScreen.findPreference("op_ramboost_switch");
        RamBoostLottieAnimPreference ramBoostLottieAnimPreference = (RamBoostLottieAnimPreference) preferenceScreen.findPreference("op_ramboost_instructions");
        this.mSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /* class com.oneplus.settings.system.OPRamBoostSettings.AnonymousClass1 */

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Boolean bool = (Boolean) obj;
                OPRamBoostSettings.setRamBoostState(OPRamBoostSettings.this.mContext, bool.booleanValue());
                OPUtils.sendAnalytics("ramboost", "status", bool.booleanValue() ? "1" : "0");
                return true;
            }
        });
        refreshUI();
        super.onViewCreated(view, bundle);
    }

    private void refreshUI() {
        this.mSwitchPreference.setChecked(getRamBoostState(this.mContext));
    }

    public static boolean getRamBoostState(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), "op_smartboost_enable", 0, -2) == 1;
    }

    public static void setRamBoostState(Context context, boolean z) {
        Settings.System.putIntForUser(context.getContentResolver(), "op_smartboost_enable", z ? 1 : 0, -2);
    }

    public static void sendDefaultAppTracker() {
        OPUtils.sendAppTracker("op_ramboost_instructions", getRamBoostState(SettingsBaseApplication.mApplication) ? 1 : 0);
    }

    public static void sendRamboostAppTracker() {
        OPUtils.sendAnalytics("ramboost", "status", getRamBoostState(SettingsBaseApplication.mApplication) ? "1" : "0");
    }
}
