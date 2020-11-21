package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;
import android.sysprop.DisplayProperties;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.internal.app.LocalePicker;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class RtlLayoutPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final int SETTING_VALUE_OFF = 0;
    static final int SETTING_VALUE_ON = 1;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "force_rtl_layout_all_locales";
    }

    public RtlLayoutPreferenceController(Context context) {
        super(context);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeToForceRtlLayoutSetting(((Boolean) obj).booleanValue());
        updateLocales();
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z = false;
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "debug.force_rtl", 0);
        SwitchPreference switchPreference = (SwitchPreference) this.mPreference;
        if (i != 0) {
            z = true;
        }
        switchPreference.setChecked(z);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeToForceRtlLayoutSetting(false);
        updateLocales();
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    /* access modifiers changed from: package-private */
    public void updateLocales() {
        LocalePicker.updateLocales(this.mContext.getResources().getConfiguration().getLocales());
    }

    private void writeToForceRtlLayoutSetting(boolean z) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "debug.force_rtl", z ? 1 : 0);
        DisplayProperties.debug_force_rtl(Boolean.valueOf(z));
    }
}
