package com.android.settings.wifi;

import android.content.Intent;
import android.util.FeatureFlagUtils;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.ButtonBarHandler;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.wifi.p2p.WifiP2pSettings;
import com.android.settings.wifi.savedaccesspoints.SavedAccessPointsWifiSettings;
import com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsWifiSettings2;

public class WifiPickerActivity extends SettingsActivity implements ButtonBarHandler {
    @Override // com.android.settings.SettingsActivity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        if (!intent.hasExtra(":settings:show_fragment")) {
            intent.putExtra(":settings:show_fragment", getWifiSettingsClass().getName());
            intent.putExtra(":settings:show_fragment_title_resid", C0017R$string.wifi_select_network);
        }
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        boolean z;
        if (FeatureFlagUtils.isEnabled(this, "settings_wifitracker2")) {
            z = SavedAccessPointsWifiSettings2.class.getName().equals(str);
        } else {
            z = SavedAccessPointsWifiSettings.class.getName().equals(str);
        }
        return WifiSettings.class.getName().equals(str) || WifiP2pSettings.class.getName().equals(str) || z;
    }

    /* access modifiers changed from: package-private */
    public Class<? extends PreferenceFragmentCompat> getWifiSettingsClass() {
        return WifiSettings.class;
    }
}
