package com.android.settings.fuelgauge.batterytip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AnomalyDetectionReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        long longExtra = intent.getLongExtra("android.app.extra.STATS_CONFIG_UID", -1);
        long longExtra2 = intent.getLongExtra("android.app.extra.STATS_CONFIG_KEY", -1);
        long longExtra3 = intent.getLongExtra("android.app.extra.STATS_SUBSCRIPTION_ID", -1);
        Log.i("SettingsAnomalyReceiver", "Anomaly intent received.  configUid = " + longExtra + " configKey = " + longExtra2 + " subscriptionId = " + longExtra3);
        intent.getExtras().putLong("key_anomaly_timestamp", System.currentTimeMillis());
        AnomalyDetectionJobService.scheduleAnomalyDetection(context, intent);
    }
}
