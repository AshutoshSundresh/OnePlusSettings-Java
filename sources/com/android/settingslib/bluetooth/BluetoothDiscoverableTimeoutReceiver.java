package com.android.settingslib.bluetooth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothDiscoverableTimeoutReceiver extends BroadcastReceiver {
    public static void setDiscoverableAlarm(Context context, long j) {
        Log.d("BluetoothDiscoverableTimeoutReceiver", "setDiscoverableAlarm(): alarmTime = " + j);
        Intent intent = new Intent("android.bluetooth.intent.DISCOVERABLE_TIMEOUT");
        intent.setClass(context, BluetoothDiscoverableTimeoutReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
        if (broadcast != null) {
            alarmManager.cancel(broadcast);
            Log.d("BluetoothDiscoverableTimeoutReceiver", "setDiscoverableAlarm(): cancel prev alarm");
        }
        alarmManager.set(0, j, PendingIntent.getBroadcast(context, 0, intent, 0));
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.bluetooth.intent.DISCOVERABLE_TIMEOUT")) {
            LocalBluetoothAdapter instance = LocalBluetoothAdapter.getInstance();
            if (instance == null || instance.getState() != 12) {
                Log.e("BluetoothDiscoverableTimeoutReceiver", "localBluetoothAdapter is NULL!!");
                return;
            }
            Log.d("BluetoothDiscoverableTimeoutReceiver", "Disable discoverable...");
            instance.setScanMode(21);
        }
    }
}
