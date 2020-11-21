package com.android.settings.bluetooth;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;

public class AvailableMediaBluetoothDeviceUpdater extends BluetoothDeviceUpdater implements Preference.OnPreferenceClickListener {
    private static final boolean DBG = Log.isLoggable("AvailableMediaBluetoothDeviceUpdater", 3);
    private final AudioManager mAudioManager;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public String getPreferenceKey() {
        return "available_media_bt";
    }

    public AvailableMediaBluetoothDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback) {
        super(context, dashboardFragment, devicePreferenceCallback);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onAudioModeChanged() {
        forceUpdate();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public boolean isFilterMatched(CachedBluetoothDevice cachedBluetoothDevice) {
        int mode = this.mAudioManager.getMode();
        int i = (mode == 1 || mode == 2 || mode == 3) ? 1 : 2;
        boolean z = false;
        if (isDeviceConnected(cachedBluetoothDevice)) {
            if (DBG) {
                Log.d("AvailableMediaBluetoothDeviceUpdater", "isFilterMatched() current audio profile : " + i);
            }
            if (cachedBluetoothDevice.isConnectedHearingAidDevice()) {
                return true;
            }
            if (i == 1) {
                z = cachedBluetoothDevice.isConnectedHfpDevice();
            } else if (i == 2) {
                z = cachedBluetoothDevice.isConnectedA2dpDevice();
            }
            if (DBG) {
                Log.d("AvailableMediaBluetoothDeviceUpdater", "isFilterMatched() device : " + cachedBluetoothDevice.getName() + ", isFilterMatched : " + z);
            }
        }
        return z;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, this.mFragment.getMetricsCategory());
        return ((BluetoothDevicePreference) preference).getBluetoothDevice().setActive();
    }
}
