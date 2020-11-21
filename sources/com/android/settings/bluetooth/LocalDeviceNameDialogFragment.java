package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.android.settings.C0017R$string;

public class LocalDeviceNameDialogFragment extends BluetoothNameDialogFragment {
    private BluetoothAdapter mBluetoothAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.bluetooth.LocalDeviceNameDialogFragment.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED".equals(action) || ("android.bluetooth.adapter.action.STATE_CHANGED".equals(action) && intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) == 12)) {
                LocalDeviceNameDialogFragment.this.updateDeviceName();
            }
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 538;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.bluetooth.BluetoothNameDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED");
        getActivity().registerReceiver(this.mReceiver, intentFilter);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mReceiver);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    public int getDialogTitle() {
        return C0017R$string.bluetooth_rename_device;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    public String getDeviceName() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return null;
        }
        return this.mBluetoothAdapter.getName();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    public void setDeviceName(String str) {
        this.mBluetoothAdapter.setName(str);
    }
}
