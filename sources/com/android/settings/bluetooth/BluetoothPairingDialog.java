package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

public class BluetoothPairingDialog extends FragmentActivity {
    private BluetoothPairingController mBluetoothPairingController;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.bluetooth.BluetoothPairingDialog.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(action)) {
                int intExtra = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
                if (intExtra == 12 || intExtra == 10) {
                    BluetoothPairingDialog.this.dismiss();
                }
            } else if ("android.bluetooth.device.action.PAIRING_CANCEL".equals(action)) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (bluetoothDevice == null || BluetoothPairingDialog.this.mBluetoothPairingController.deviceEquals(bluetoothDevice)) {
                    BluetoothPairingDialog.this.dismiss();
                }
            }
        }
    };
    private boolean mReceiverRegistered = false;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle) {
        boolean z;
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        Intent intent = getIntent();
        if (intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE") == null) {
            finish();
            return;
        }
        this.mBluetoothPairingController = new BluetoothPairingController(intent, this);
        BluetoothPairingDialogFragment bluetoothPairingDialogFragment = (BluetoothPairingDialogFragment) getSupportFragmentManager().findFragmentByTag("bluetooth.pairing.fragment");
        if (bluetoothPairingDialogFragment != null && (bluetoothPairingDialogFragment.isPairingControllerSet() || bluetoothPairingDialogFragment.isPairingDialogActivitySet())) {
            bluetoothPairingDialogFragment.dismiss();
            bluetoothPairingDialogFragment = null;
        }
        if (bluetoothPairingDialogFragment == null) {
            z = false;
            bluetoothPairingDialogFragment = new BluetoothPairingDialogFragment();
        } else {
            z = true;
        }
        bluetoothPairingDialogFragment.setPairingController(this.mBluetoothPairingController);
        bluetoothPairingDialogFragment.setPairingDialogActivity(this);
        if (!z) {
            bluetoothPairingDialogFragment.show(getSupportFragmentManager(), "bluetooth.pairing.fragment");
        }
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.PAIRING_CANCEL"));
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED"));
        this.mReceiverRegistered = true;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
        if (this.mReceiverRegistered) {
            this.mReceiverRegistered = false;
            unregisterReceiver(this.mReceiver);
        }
    }

    /* access modifiers changed from: package-private */
    public void dismiss() {
        if (!isFinishing()) {
            finish();
        }
    }
}
