package com.android.settings.development;

import android.bluetooth.BluetoothA2dp;

public interface BluetoothServiceConnectionListener {
    void onBluetoothCodecUpdated();

    void onBluetoothServiceConnected(BluetoothA2dp bluetoothA2dp);

    void onBluetoothServiceDisconnected();
}
