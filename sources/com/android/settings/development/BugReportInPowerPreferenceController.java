package com.android.settings.development;

import android.content.Context;
import android.os.UserManager;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class BugReportInPowerPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static int SETTING_VALUE_OFF = 0;
    static int SETTING_VALUE_ON = 1;
    private final UserManager mUserManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bugreport_in_power";
    }

    public BugReportInPowerPreferenceController(Context context) {
        super(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return !this.mUserManager.hasUserRestriction("no_debugging_features");
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "bugreport_in_power_menu", ((Boolean) obj).booleanValue() ? SETTING_VALUE_ON : SETTING_VALUE_OFF);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(Settings.Secure.getInt(this.mContext.getContentResolver(), "bugreport_in_power_menu", SETTING_VALUE_OFF) != SETTING_VALUE_OFF);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.Secure.putInt(this.mContext.getContentResolver(), "bugreport_in_power_menu", SETTING_VALUE_OFF);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }
}
