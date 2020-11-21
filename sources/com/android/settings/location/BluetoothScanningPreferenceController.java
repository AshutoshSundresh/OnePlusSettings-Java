package com.android.settings.location;

import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class BluetoothScanningPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_always_scanning";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public BluetoothScanningPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        SwitchPreference switchPreference = (SwitchPreference) preference;
        boolean z = false;
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "ble_scan_always_enabled", 0) == 1) {
            z = true;
        }
        switchPreference.setChecked(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!"bluetooth_always_scanning".equals(preference.getKey())) {
            return false;
        }
        Settings.Global.putInt(this.mContext.getContentResolver(), "ble_scan_always_enabled", ((SwitchPreference) preference).isChecked() ? 1 : 0);
        return true;
    }
}
