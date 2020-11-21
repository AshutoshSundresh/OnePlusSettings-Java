package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.settings.Settings;

public class TestingSettingsBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.telephony.action.SECRET_CODE")) {
            Intent intent2 = new Intent("android.intent.action.MAIN");
            intent2.setClass(context, Settings.TestingSettingsActivity.class);
            intent2.setFlags(268435456);
            context.startActivity(intent2);
        }
    }
}
