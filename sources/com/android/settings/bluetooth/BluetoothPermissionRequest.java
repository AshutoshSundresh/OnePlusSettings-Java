package com.android.settings.bluetooth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.UserManager;
import android.util.Log;
import com.android.settings.C0017R$string;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;

public final class BluetoothPermissionRequest extends BroadcastReceiver {
    Context mContext;
    BluetoothDevice mDevice;
    private NotificationChannel mNotificationChannel = null;
    int mRequestType;
    String mReturnClass = null;
    String mReturnPackage = null;

    public void onReceive(Context context, Intent intent) {
        String str;
        String str2;
        this.mContext = context;
        String action = intent.getAction();
        if (action.equals("android.bluetooth.device.action.CONNECTION_ACCESS_REQUEST")) {
            if (!((UserManager) context.getSystemService("user")).isManagedProfile()) {
                this.mDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                this.mRequestType = intent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 1);
                this.mReturnPackage = intent.getStringExtra("android.bluetooth.device.extra.PACKAGE_NAME");
                this.mReturnClass = intent.getStringExtra("android.bluetooth.device.extra.CLASS_NAME");
                if (!checkUserChoice()) {
                    Intent intent2 = new Intent(action);
                    intent2.setClass(context, BluetoothPermissionActivity.class);
                    intent2.setFlags(402653184);
                    intent2.setType(Integer.toString(this.mRequestType));
                    intent2.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
                    intent2.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
                    intent2.putExtra("android.bluetooth.device.extra.PACKAGE_NAME", this.mReturnPackage);
                    intent2.putExtra("android.bluetooth.device.extra.CLASS_NAME", this.mReturnClass);
                    BluetoothDevice bluetoothDevice = this.mDevice;
                    String str3 = null;
                    String address = bluetoothDevice != null ? bluetoothDevice.getAddress() : null;
                    BluetoothDevice bluetoothDevice2 = this.mDevice;
                    if (bluetoothDevice2 != null) {
                        str3 = bluetoothDevice2.getName();
                    }
                    if (!((PowerManager) context.getSystemService("power")).isScreenOn() || !LocalBluetoothPreferences.shouldShowDialogInForeground(context, address, str3)) {
                        Intent intent3 = new Intent("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY");
                        intent3.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
                        intent3.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", 2);
                        intent3.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
                        String createRemoteName = Utils.createRemoteName(context, this.mDevice);
                        int i = this.mRequestType;
                        if (i == 2) {
                            str = context.getString(C0017R$string.bluetooth_phonebook_request);
                            str2 = context.getString(C0017R$string.bluetooth_pb_acceptance_dialog_text, createRemoteName, createRemoteName);
                        } else if (i == 3) {
                            str = context.getString(C0017R$string.bluetooth_map_request);
                            str2 = context.getString(C0017R$string.bluetooth_map_acceptance_dialog_text, createRemoteName, createRemoteName);
                        } else if (i != 4) {
                            str = context.getString(C0017R$string.bluetooth_connection_permission_request);
                            str2 = context.getString(C0017R$string.bluetooth_connection_dialog_text, createRemoteName, createRemoteName);
                        } else {
                            str = context.getString(C0017R$string.bluetooth_sap_request);
                            str2 = context.getString(C0017R$string.bluetooth_sap_acceptance_dialog_text, createRemoteName, createRemoteName);
                        }
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                        if (this.mNotificationChannel == null) {
                            NotificationChannel notificationChannel = new NotificationChannel("bluetooth_notification_channel", context.getString(C0017R$string.bluetooth), 4);
                            this.mNotificationChannel = notificationChannel;
                            notificationManager.createNotificationChannel(notificationChannel);
                        }
                        Notification build = new Notification.Builder(context, "bluetooth_notification_channel").setContentTitle(str).setTicker(str2).setContentText(str2).setSmallIcon(17301632).setAutoCancel(true).setPriority(2).setOnlyAlertOnce(false).setDefaults(-1).setContentIntent(PendingIntent.getActivity(context, 0, intent2, 0)).setDeleteIntent(PendingIntent.getBroadcast(context, 0, intent3, 0)).setColor(context.getColor(17170460)).setLocalOnly(true).build();
                        build.flags |= 32;
                        notificationManager.notify(getNotificationTag(this.mRequestType), 17301632, build);
                        return;
                    }
                    context.startActivity(intent2);
                }
            }
        } else if (action.equals("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL")) {
            int intExtra = intent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2);
            this.mRequestType = intExtra;
            ((NotificationManager) context.getSystemService("notification")).cancel(getNotificationTag(intExtra), 17301632);
        }
    }

    private String getNotificationTag(int i) {
        if (i == 2) {
            return "Phonebook Access";
        }
        int i2 = this.mRequestType;
        if (i2 == 3) {
            return "Message Access";
        }
        if (i2 == 4) {
            return "SIM Access";
        }
        return null;
    }

    private boolean checkUserChoice() {
        int simAccessPermission;
        int i = this.mRequestType;
        if (i != 2 && i != 3 && i != 4) {
            return false;
        }
        CachedBluetoothDeviceManager cachedDeviceManager = Utils.getLocalBtManager(this.mContext).getCachedDeviceManager();
        if (cachedDeviceManager.findDevice(this.mDevice) == null) {
            cachedDeviceManager.addDevice(this.mDevice);
        }
        int i2 = this.mRequestType;
        if (i2 == 2) {
            int phonebookAccessPermission = this.mDevice.getPhonebookAccessPermission();
            if (phonebookAccessPermission == 0) {
                return false;
            }
            if (phonebookAccessPermission == 1) {
                sendReplyIntentToReceiver(true);
            } else if (phonebookAccessPermission == 2) {
                sendReplyIntentToReceiver(false);
            } else {
                Log.e("BluetoothPermissionRequest", "Bad phonebookPermission: " + phonebookAccessPermission);
                return false;
            }
        } else if (i2 == 3) {
            int messageAccessPermission = this.mDevice.getMessageAccessPermission();
            if (messageAccessPermission == 0) {
                return false;
            }
            if (messageAccessPermission == 1) {
                sendReplyIntentToReceiver(true);
            } else if (messageAccessPermission == 2) {
                sendReplyIntentToReceiver(false);
            } else {
                Log.e("BluetoothPermissionRequest", "Bad messagePermission: " + messageAccessPermission);
                return false;
            }
        } else if (i2 != 4 || (simAccessPermission = this.mDevice.getSimAccessPermission()) == 0) {
            return false;
        } else {
            if (simAccessPermission == 1) {
                sendReplyIntentToReceiver(true);
            } else if (simAccessPermission == 2) {
                sendReplyIntentToReceiver(false);
            } else {
                Log.e("BluetoothPermissionRequest", "Bad simPermission: " + simAccessPermission);
                return false;
            }
        }
        return true;
    }

    private void sendReplyIntentToReceiver(boolean z) {
        String str;
        Intent intent = new Intent("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY");
        String str2 = this.mReturnPackage;
        if (!(str2 == null || (str = this.mReturnClass) == null)) {
            intent.setClassName(str2, str);
        }
        intent.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", z ? 1 : 2);
        intent.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
        intent.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
        this.mContext.sendBroadcast(intent, "android.permission.BLUETOOTH_ADMIN");
    }
}
