package com.oneplus.settings.timer.timepower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverPowerOn extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = new Intent("com.android.settings.SET_CHANGED");
        intent2.setFlags(285212672);
        context.sendBroadcast(intent2);
    }
}
