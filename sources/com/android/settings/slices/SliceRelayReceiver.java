package com.android.settings.slices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class SliceRelayReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra("uri");
        if (!TextUtils.isEmpty(stringExtra)) {
            context.getContentResolver().notifyChange(Uri.parse(stringExtra), null);
        }
    }
}
