package com.oneplus.settings.development;

import android.content.Context;
import android.os.SystemProperties;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.oneplus.settings.SettingsBaseApplication;

public class OPWirlessAdbDebuggingPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "op_wireless_adb_debugging";
    }

    public OPWirlessAdbDebuggingPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (this.mPreference != null && !isAdminUser()) {
            this.mPreference.setEnabled(false);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return SettingsBaseApplication.mApplication.getPackageManager().hasSystemFeature("oem.service.adb.tcp.port.support");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), "op_wireless_adb_debugging") || !(preference instanceof SwitchPreference)) {
            return false;
        }
        SystemProperties.set("service.adb.tcp.port", ((SwitchPreference) preference).isChecked() ? "5555" : "-1");
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof SwitchPreference) {
            ((SwitchPreference) preference).setChecked("5555".equals(SystemProperties.get("service.adb.tcp.port")));
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAdminUser() {
        return ((UserManager) this.mContext.getSystemService("user")).isAdminUser();
    }
}
