package com.android.settings.wifi.p2p;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.utils.OPUtils;

public class P2pThisDevicePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private Preference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "p2p_this_device";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public P2pThisDevicePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    public void setEnabled(boolean z) {
        Preference preference = this.mPreference;
        if (preference != null) {
            preference.setEnabled(z);
        }
    }

    public void updateDeviceName(WifiP2pDevice wifiP2pDevice) {
        if (this.mPreference != null && wifiP2pDevice != null) {
            Settings.System.getString(this.mContext.getContentResolver(), "oem_oneplus_devicename");
            String resetDeviceNameIfInvalid = OPUtils.resetDeviceNameIfInvalid(this.mContext);
            if (resetDeviceNameIfInvalid.length() > 32) {
                resetDeviceNameIfInvalid = resetDeviceNameIfInvalid.substring(0, 31);
                Settings.System.putString(this.mContext.getContentResolver(), "oem_oneplus_devicename", resetDeviceNameIfInvalid);
            }
            if (OPUtils.isEF009Project()) {
                this.mPreference.setTitle(OPUtils.isContainSymbol(resetDeviceNameIfInvalid) ? OPUtils.getSymbolDeviceName(resetDeviceNameIfInvalid) : resetDeviceNameIfInvalid);
            } else {
                this.mPreference.setTitle(resetDeviceNameIfInvalid);
            }
            if (!wifiP2pDevice.deviceName.equals(resetDeviceNameIfInvalid)) {
                WifiP2pManager wifiP2pManager = (WifiP2pManager) this.mContext.getSystemService("wifip2p");
                Context context = this.mContext;
                WifiP2pManager.Channel initialize = wifiP2pManager.initialize(context, context.getMainLooper(), null);
                if (wifiP2pManager != null) {
                    wifiP2pManager.setDeviceName(initialize, resetDeviceNameIfInvalid, null);
                }
            }
        }
    }
}
