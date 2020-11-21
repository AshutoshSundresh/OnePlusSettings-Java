package com.oneplus.settings.development;

import android.content.Context;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class OPWifiVerboseMultiBroadcastPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "op_wifi_verbose_multi_broadcast";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public OPWifiVerboseMultiBroadcastPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (this.mPreference != null && !isAdminUser()) {
            this.mPreference.setEnabled(false);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), "op_wifi_verbose_multi_broadcast") || !(preference instanceof SwitchPreference)) {
            return false;
        }
        Settings.Global.putInt(this.mContext.getContentResolver(), "op_enable_wifi_multi_broadcast", ((SwitchPreference) preference).isChecked() ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            boolean z = true;
            if (Settings.Global.getInt(this.mContext.getContentResolver(), "op_enable_wifi_multi_broadcast", 1) != 1) {
                z = false;
            }
            switchPreference.setChecked(z);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAdminUser() {
        return ((UserManager) this.mContext.getSystemService("user")).isAdminUser();
    }
}
