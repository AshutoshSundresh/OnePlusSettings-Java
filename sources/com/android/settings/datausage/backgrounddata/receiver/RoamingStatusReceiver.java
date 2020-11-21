package com.android.settings.datausage.backgrounddata.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.android.settings.datausage.backgrounddata.utils.BackgroundDataUtils;
import com.oneplus.settings.utils.OPUtils;

public class RoamingStatusReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (OPUtils.isSupportUss()) {
            BackgroundDataUtils.changeRoamingAppStatus(context);
            Log.d("RoamingStatusReceiver", "RoamingStatusReceiver onReceive");
        }
    }
}
