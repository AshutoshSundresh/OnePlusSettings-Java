package com.android.settingslib.bluetooth;

public interface BluetoothCallback {
    default void onAclConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    default void onActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    default void onAudioModeChanged() {
    }

    default void onBluetoothStateChanged(int i) {
    }

    default void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    default void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
    }

    default void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    default void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
    }

    default void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
    }

    default void onScanningStateChanged(boolean z) {
    }
}
