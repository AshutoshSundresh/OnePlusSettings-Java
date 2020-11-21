package com.android.settings.bluetooth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.C0017R$string;

public final class BluetoothPairingService extends Service {
    private final BroadcastReceiver mCancelReceiver = new BroadcastReceiver() {
        /* class com.android.settings.bluetooth.BluetoothPairingService.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.bluetooth.device.action.BOND_STATE_CHANGED")) {
                int intExtra = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
                if (!(intExtra == 10 || intExtra == 12)) {
                    return;
                }
            } else if (action.equals("com.android.settings.bluetooth.ACTION_DISMISS_PAIRING")) {
                Log.d("BluetoothPairingService", "Notification cancel " + BluetoothPairingService.this.mDevice.getAddress() + " (" + BluetoothPairingService.this.mDevice.getName() + ")");
                BluetoothPairingService.this.mDevice.cancelPairing();
            } else {
                int intExtra2 = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
                Log.d("BluetoothPairingService", "Dismiss pairing for " + BluetoothPairingService.this.mDevice.getAddress() + " (" + BluetoothPairingService.this.mDevice.getName() + "), BondState: " + intExtra2);
            }
            BluetoothPairingService.this.stopForeground(true);
            BluetoothPairingService.this.stopSelf();
        }
    };
    private BluetoothDevice mDevice;
    private boolean mRegistered = false;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent getPairingDialogIntent(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", Integer.MIN_VALUE);
        Intent intent2 = new Intent();
        intent2.setClass(context, BluetoothPairingDialog.class);
        intent2.putExtra("android.bluetooth.device.extra.DEVICE", (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
        intent2.putExtra("android.bluetooth.device.extra.PAIRING_VARIANT", intExtra);
        if (intExtra == 2 || intExtra == 4 || intExtra == 5) {
            intent2.putExtra("android.bluetooth.device.extra.PAIRING_KEY", intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", Integer.MIN_VALUE));
        }
        intent2.setAction("android.bluetooth.device.action.PAIRING_REQUEST");
        intent2.setFlags(268435456);
        return intent2;
    }

    public void onCreate() {
        ((NotificationManager) getSystemService("notification")).createNotificationChannel(new NotificationChannel("bluetooth_notification_channel", getString(C0017R$string.bluetooth), 4));
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null) {
            Log.e("BluetoothPairingService", "Can't start: null intent!");
            stopSelf();
            return 2;
        }
        Resources resources = getResources();
        Notification.Builder localOnly = new Notification.Builder(this, "bluetooth_notification_channel").setSmallIcon(17301632).setTicker(resources.getString(C0017R$string.bluetooth_notif_ticker)).setLocalOnly(true);
        PendingIntent activity = PendingIntent.getActivity(this, 0, getPairingDialogIntent(this, intent), 1207959552);
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, new Intent("com.android.settings.bluetooth.ACTION_DISMISS_PAIRING"), 1073741824);
        BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        this.mDevice = bluetoothDevice;
        if (bluetoothDevice == null || bluetoothDevice.getBondState() == 11) {
            String stringExtra = intent.getStringExtra("android.bluetooth.device.extra.NAME");
            if (TextUtils.isEmpty(stringExtra)) {
                BluetoothDevice bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                stringExtra = bluetoothDevice2 != null ? bluetoothDevice2.getAlias() : resources.getString(17039374);
            }
            Log.d("BluetoothPairingService", "Show pairing notification for " + this.mDevice.getAddress() + " (" + stringExtra + ")");
            localOnly.setContentTitle(resources.getString(C0017R$string.bluetooth_notif_title)).setContentText(resources.getString(C0017R$string.bluetooth_notif_message, stringExtra)).setContentIntent(activity).setDefaults(1).setColor(getColor(17170460)).addAction(new Notification.Action.Builder(0, resources.getString(C0017R$string.bluetooth_device_context_pair_connect), activity).build()).addAction(new Notification.Action.Builder(0, resources.getString(17039360), broadcast).build());
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
            intentFilter.addAction("android.bluetooth.device.action.PAIRING_CANCEL");
            intentFilter.addAction("com.android.settings.bluetooth.ACTION_DISMISS_PAIRING");
            registerReceiver(this.mCancelReceiver, intentFilter);
            this.mRegistered = true;
            startForeground(17301632, localOnly.getNotification());
            return 3;
        }
        Log.w("BluetoothPairingService", "Device " + this.mDevice + " not bonding: " + this.mDevice.getBondState());
        stopSelf();
        return 2;
    }

    public void onDestroy() {
        if (this.mRegistered) {
            unregisterReceiver(this.mCancelReceiver);
            this.mRegistered = false;
        }
        stopForeground(true);
    }
}
