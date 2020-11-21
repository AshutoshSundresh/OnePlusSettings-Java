package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settings.development.BluetoothServiceConnectionListener;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public abstract class AbstractBluetoothPreferenceController extends DeveloperOptionsPreferenceController implements BluetoothServiceConnectionListener, LifecycleObserver, OnDestroy, PreferenceControllerMixin {
    protected volatile BluetoothA2dp mBluetoothA2dp;

    public interface Callback {
        void onBluetoothCodecChanged();

        void onBluetoothHDAudioEnabled(boolean z);
    }

    public AbstractBluetoothPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settings.development.BluetoothServiceConnectionListener
    public void onBluetoothServiceConnected(BluetoothA2dp bluetoothA2dp) {
        this.mBluetoothA2dp = bluetoothA2dp;
        updateState(this.mPreference);
    }

    @Override // com.android.settings.development.BluetoothServiceConnectionListener
    public void onBluetoothCodecUpdated() {
        updateState(this.mPreference);
    }

    @Override // com.android.settings.development.BluetoothServiceConnectionListener
    public void onBluetoothServiceDisconnected() {
        this.mBluetoothA2dp = null;
        updateState(this.mPreference);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        this.mBluetoothA2dp = null;
    }
}
