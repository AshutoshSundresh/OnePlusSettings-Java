package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.connecteddevice.OPBluetoothCarKitDevicePreference;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public class OPPairedBluetoothDeviceUpdater extends OPBluetoothCarKitDeviceUpdater {
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public OPPairedBluetoothDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback) {
        super(context, dashboardFragment, devicePreferenceCallback);
    }

    OPPairedBluetoothDeviceUpdater(DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback, LocalBluetoothManager localBluetoothManager) {
        super(dashboardFragment, devicePreferenceCallback, localBluetoothManager);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback, com.android.settings.bluetooth.OPBluetoothCarKitDeviceUpdater
    public void onAudioModeChanged() {
        forceUpdate();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback, com.android.settings.bluetooth.OPBluetoothCarKitDeviceUpdater
    public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        Log.d("OPPairedBluetoothDeviceUpdater", "onProfileConnectionStateChanged() device: " + cachedBluetoothDevice.getName() + ", state: " + i + ", bluetoothProfile: " + i2);
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
        if (!isDeviceConnected(cachedBluetoothDevice) || this.mBluetoothAdapter.isCarkit(cachedBluetoothDevice.getDevice())) {
            return false;
        }
        Log.d("OPPairedBluetoothDeviceUpdater", "isFilterMatched() device : " + cachedBluetoothDevice.getName() + ", isFilterMatched : true");
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.OPBluetoothCarKitDeviceUpdater
    public void addPreference(CachedBluetoothDevice cachedBluetoothDevice) {
        super.addPreference(cachedBluetoothDevice);
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        if (this.mPreferenceMap.containsKey(device)) {
            OPBluetoothCarKitDevicePreference oPBluetoothCarKitDevicePreference = (OPBluetoothCarKitDevicePreference) this.mPreferenceMap.get(device);
        }
    }
}
