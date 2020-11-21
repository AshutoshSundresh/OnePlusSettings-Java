package com.oneplus.accountsdk.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OPAuthReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        "com.onplus.account.login.broadcast".equals(intent.getAction());
    }
}
