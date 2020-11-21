package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.net.Uri;

public interface BluetoothFeatureProvider {
    Uri getBluetoothDeviceSettingsUri(BluetoothDevice bluetoothDevice);
}
