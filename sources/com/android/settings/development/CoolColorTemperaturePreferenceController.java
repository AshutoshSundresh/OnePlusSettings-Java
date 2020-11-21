package com.android.settings.development;

import android.content.Context;
import android.os.SystemProperties;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.development.SystemPropPoker;

public class CoolColorTemperaturePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final String COLOR_TEMPERATURE_PROPERTY = "persist.sys.debug.color_temp";

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "color_temperature";
    }

    public CoolColorTemperaturePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_enableColorTemperature);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        SystemProperties.set(COLOR_TEMPERATURE_PROPERTY, Boolean.toString(((Boolean) obj).booleanValue()));
        SystemPropPoker.getInstance().poke();
        displayColorTemperatureToast();
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(SystemProperties.getBoolean(COLOR_TEMPERATURE_PROPERTY, false));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        SystemProperties.set(COLOR_TEMPERATURE_PROPERTY, Boolean.toString(false));
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    /* access modifiers changed from: package-private */
    public void displayColorTemperatureToast() {
        Toast.makeText(this.mContext, C0017R$string.color_temperature_toast, 1).show();
    }
}
