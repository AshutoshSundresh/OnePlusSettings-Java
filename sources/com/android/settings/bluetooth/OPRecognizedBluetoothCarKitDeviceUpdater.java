package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.connecteddevice.OPBluetoothCarKitDevicePreference;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public class OPRecognizedBluetoothCarKitDeviceUpdater extends OPBluetoothCarKitDeviceUpdater implements Preference.OnPreferenceClickListener {
    private final AudioManager mAudioManager;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public OPRecognizedBluetoothCarKitDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback) {
        super(context, dashboardFragment, devicePreferenceCallback);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    OPRecognizedBluetoothCarKitDeviceUpdater(DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback, LocalBluetoothManager localBluetoothManager) {
        super(dashboardFragment, devicePreferenceCallback, localBluetoothManager);
        this.mAudioManager = (AudioManager) dashboardFragment.getContext().getSystemService("audio");
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback, com.android.settings.bluetooth.OPBluetoothCarKitDeviceUpdater
    public void onAudioModeChanged() {
        forceUpdate();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback, com.android.settings.bluetooth.OPBluetoothCarKitDeviceUpdater
    public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        Log.d("OPRecognizedBluetoothCarKitDeviceUpdater", "onProfileConnectionStateChanged() device: " + cachedBluetoothDevice.getName() + ", state: " + i + ", bluetoothProfile: " + i2);
        if (i == 2) {
            if (isFilterMatched(cachedBluetoothDevice)) {
                addPreference(cachedBluetoothDevice);
            } else {
                removePreference(cachedBluetoothDevice);
            }
        } else if (i == 0) {
            removePreference(cachedBluetoothDevice);
        }
    }

    @Override // com.android.settings.bluetooth.OPBluetoothCarKitDeviceUpdater
    public boolean isFilterMatched(CachedBluetoothDevice cachedBluetoothDevice) {
        int mode = this.mAudioManager.getMode();
        if (!(mode == 1 || mode == 2)) {
        }
        if (!isDeviceConnected(cachedBluetoothDevice)) {
            return false;
        }
        boolean isCarkit = this.mBluetoothAdapter.isCarkit(cachedBluetoothDevice.getDevice());
        Log.d("OPRecognizedBluetoothCarKitDeviceUpdater", "isFilterMatched() device : " + cachedBluetoothDevice.getName() + ", isFilterMatched : " + isCarkit);
        return isCarkit;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return ((OPBluetoothCarKitDevicePreference) preference).getBluetoothDevice().setActive();
    }
}
