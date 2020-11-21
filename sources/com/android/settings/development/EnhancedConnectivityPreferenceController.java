package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0005R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class EnhancedConnectivityPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    @VisibleForTesting
    static final int ENHANCED_CONNECTIVITY_OFF = 0;
    @VisibleForTesting
    static final int ENHANCED_CONNECTIVITY_ON = 1;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "enhanced_connectivity";
    }

    public EnhancedConnectivityPreferenceController(Context context) {
        super(context);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "enhanced_connectivity_enable", ((Boolean) obj).booleanValue() ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z = true;
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "enhanced_connectivity_enable", 1);
        SwitchPreference switchPreference = (SwitchPreference) this.mPreference;
        if (i != 1) {
            z = false;
        }
        switchPreference.setChecked(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_enhanced_connectivity);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.Global.putInt(this.mContext.getContentResolver(), "enhanced_connectivity_enable", 1);
        ((SwitchPreference) this.mPreference).setChecked(true);
    }
}
