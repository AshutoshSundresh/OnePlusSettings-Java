package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.Uri;

public class BluetoothFeatureProviderImpl implements BluetoothFeatureProvider {
    public BluetoothFeatureProviderImpl(Context context) {
    }

    @Override // com.android.settings.bluetooth.BluetoothFeatureProvider
    public Uri getBluetoothDeviceSettingsUri(BluetoothDevice bluetoothDevice) {
        byte[] metadata = bluetoothDevice.getMetadata(16);
        if (metadata == null) {
            return null;
        }
        return Uri.parse(new String(metadata));
    }
}
