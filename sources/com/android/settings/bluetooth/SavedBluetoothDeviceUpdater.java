package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SavedBluetoothDeviceUpdater extends BluetoothDeviceUpdater implements Preference.OnPreferenceClickListener {
    private static final boolean DBG = Log.isLoggable("SavedBluetoothDeviceUpdater", 3);
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public String getPreferenceKey() {
        return "saved_bt";
    }

    public SavedBluetoothDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback) {
        super(context, dashboardFragment, devicePreferenceCallback);
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public void forceUpdate() {
        if (this.mBluetoothAdapter.isEnabled()) {
            CachedBluetoothDeviceManager cachedDeviceManager = this.mLocalManager.getCachedDeviceManager();
            List<BluetoothDevice> mostRecentlyConnectedDevices = this.mBluetoothAdapter.getMostRecentlyConnectedDevices();
            removePreferenceIfNecessary(mostRecentlyConnectedDevices, cachedDeviceManager);
            for (BluetoothDevice bluetoothDevice : mostRecentlyConnectedDevices) {
                CachedBluetoothDevice findDevice = cachedDeviceManager.findDevice(bluetoothDevice);
                if (findDevice != null) {
                    update(findDevice);
                }
            }
            return;
        }
        removeAllDevicesFromPreference();
    }

    private void removePreferenceIfNecessary(List<BluetoothDevice> list, CachedBluetoothDeviceManager cachedBluetoothDeviceManager) {
        CachedBluetoothDevice findDevice;
        Iterator it = new ArrayList(this.mPreferenceMap.keySet()).iterator();
        while (it.hasNext()) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) it.next();
            if (!list.contains(bluetoothDevice) && (findDevice = cachedBluetoothDeviceManager.findDevice(bluetoothDevice)) != null) {
                removePreference(findDevice);
            }
        }
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public void update(CachedBluetoothDevice cachedBluetoothDevice) {
        if (isFilterMatched(cachedBluetoothDevice)) {
            addPreference(cachedBluetoothDevice, 3);
        } else {
            removePreference(cachedBluetoothDevice);
        }
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public boolean isFilterMatched(CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        if (DBG) {
            Log.d("SavedBluetoothDeviceUpdater", "isFilterMatched() device name : " + cachedBluetoothDevice.getName() + ", is connected : " + device.isConnected() + ", is profile connected : " + cachedBluetoothDevice.isConnected() + ", is twsplusdevice : " + device.isTwsPlusDevice());
        }
        return device.getBondState() == 12 && !device.isConnected();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, this.mFragment.getMetricsCategory());
        ((BluetoothDevicePreference) preference).getBluetoothDevice().connect();
        return true;
    }
}
