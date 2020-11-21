package com.android.settings.bluetooth;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.C0017R$string;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public class RemoteDeviceNameDialogFragment extends BluetoothNameDialogFragment {
    private CachedBluetoothDevice mDevice;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1015;
    }

    public static RemoteDeviceNameDialogFragment newInstance(CachedBluetoothDevice cachedBluetoothDevice) {
        Bundle bundle = new Bundle(1);
        bundle.putString("cached_device", cachedBluetoothDevice.getDevice().getAddress());
        RemoteDeviceNameDialogFragment remoteDeviceNameDialogFragment = new RemoteDeviceNameDialogFragment();
        remoteDeviceNameDialogFragment.setArguments(bundle);
        return remoteDeviceNameDialogFragment;
    }

    /* access modifiers changed from: package-private */
    public CachedBluetoothDevice getDevice(Context context) {
        String string = getArguments().getString("cached_device");
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(context);
        return localBtManager.getCachedDeviceManager().findDevice(localBtManager.getBluetoothAdapter().getRemoteDevice(string));
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mDevice = getDevice(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    public int getDialogTitle() {
        return C0017R$string.bluetooth_device_name;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    public String getDeviceName() {
        CachedBluetoothDevice cachedBluetoothDevice = this.mDevice;
        if (cachedBluetoothDevice != null) {
            return cachedBluetoothDevice.getName();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    public void setDeviceName(String str) {
        CachedBluetoothDevice cachedBluetoothDevice = this.mDevice;
        if (cachedBluetoothDevice != null) {
            cachedBluetoothDevice.setName(str);
        }
    }
}
