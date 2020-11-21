package com.android.settings.fuelgauge;

import android.content.Intent;
import android.util.Log;
import com.android.settings.utils.VoiceSettingsActivity;
import com.android.settingslib.fuelgauge.BatterySaverUtils;

public class BatterySaverModeVoiceActivity extends VoiceSettingsActivity {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.utils.VoiceSettingsActivity
    public boolean onVoiceSettingInteraction(Intent intent) {
        if (!intent.hasExtra("android.settings.extra.battery_saver_mode_enabled")) {
            Log.v("BatterySaverModeVoiceActivity", "Missing battery saver mode extra");
        } else if (BatterySaverUtils.setPowerSaveMode(this, intent.getBooleanExtra("android.settings.extra.battery_saver_mode_enabled", false), true)) {
            notifySuccess(null);
        } else {
            Log.v("BatterySaverModeVoiceActivity", "Unable to set power mode");
            notifyFailure(null);
        }
        return true;
    }
}
