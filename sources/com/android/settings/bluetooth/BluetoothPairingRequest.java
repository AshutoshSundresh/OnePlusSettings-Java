package com.android.settings.bluetooth;

import android.app.KeyguardManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.UserHandle;

public final class BluetoothPairingRequest extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
            Intent pairingDialogIntent = BluetoothPairingService.getPairingDialogIntent(context, intent);
            PowerManager powerManager = (PowerManager) context.getSystemService("power");
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            String str = null;
            String address = bluetoothDevice != null ? bluetoothDevice.getAddress() : null;
            if (bluetoothDevice != null) {
                str = bluetoothDevice.getName();
            }
            boolean shouldShowDialogInForeground = LocalBluetoothPreferences.shouldShowDialogInForeground(context, address, str);
            if (!powerManager.isInteractive() || !shouldShowDialogInForeground || isScreenLocked(context)) {
                intent.setClass(context, BluetoothPairingService.class);
                context.startServiceAsUser(intent, UserHandle.CURRENT);
                return;
            }
            context.startActivityAsUser(pairingDialogIntent, UserHandle.CURRENT);
        }
    }

    private boolean isScreenLocked(Context context) {
        return ((KeyguardManager) context.getSystemService("keyguard")).isKeyguardLocked();
    }
}
