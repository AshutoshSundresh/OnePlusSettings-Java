package com.oneplus.security.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CardPackageDataClearReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "android.intent.action.PACKAGE_DATA_CLEARED".equals(intent.getAction())) {
            String dataString = intent.getDataString();
            Log.d("CardPackageDataClearRec", "onReceive: " + dataString);
            if (dataString != null && dataString.contains("com.oneplus.card")) {
                Intent intent2 = new Intent();
                intent2.setAction("com.oneplus.card.DATA_CLEARED");
                intent2.setPackage("com.oneplus.card");
                context.sendBroadcast(intent2);
            }
        }
    }
}
