package com.android.settings.development;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class OverlaySettingsPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "overlay_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public OverlaySettingsPreferenceController(Context context) {
        super(context);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        setOverlaySettingsEnabled(this.mContext, ((Boolean) obj).booleanValue());
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) preference).setChecked(isOverlaySettingsEnabled(this.mContext));
    }

    public static boolean isOverlaySettingsEnabled(Context context) {
        return context.getSharedPreferences("overlay_settings", 0).getBoolean("overlay_settings", false);
    }

    static void setOverlaySettingsEnabled(Context context, boolean z) {
        context.getSharedPreferences("overlay_settings", 0).edit().putBoolean("overlay_settings", z).apply();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        setOverlaySettingsEnabled(this.mContext, false);
    }
}
