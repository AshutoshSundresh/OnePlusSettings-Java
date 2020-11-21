package com.google.analytics.tracking.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

class GANetworkReceiver extends BroadcastReceiver {
    static final String SELF_IDENTIFYING_EXTRA = GANetworkReceiver.class.getName();
    private final ServiceManager mManager;

    GANetworkReceiver(ServiceManager serviceManager) {
        this.mManager = serviceManager;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            this.mManager.updateConnectivityStatus(!intent.getBooleanExtra("noConnectivity", false));
        } else if ("com.google.analytics.RADIO_POWERED".equals(action) && !intent.hasExtra(SELF_IDENTIFYING_EXTRA)) {
            this.mManager.onRadioPowered();
        }
    }

    public void register(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(this, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.google.analytics.RADIO_POWERED");
        intentFilter2.addCategory(context.getPackageName());
        context.registerReceiver(this, intentFilter2);
    }

    public static void sendRadioPoweredBroadcast(Context context) {
        Intent intent = new Intent("com.google.analytics.RADIO_POWERED");
        intent.addCategory(context.getPackageName());
        intent.putExtra(SELF_IDENTIFYING_EXTRA, true);
        context.sendBroadcast(intent);
    }
}
