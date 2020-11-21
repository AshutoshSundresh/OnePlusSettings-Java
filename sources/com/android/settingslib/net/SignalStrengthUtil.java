package com.android.settingslib.net;

import android.content.Context;
import android.telephony.SubscriptionManager;

public class SignalStrengthUtil {
    public static boolean shouldInflateSignalStrength(Context context, int i) {
        return SubscriptionManager.getResourcesForSubId(context, i).getBoolean(17891477);
    }
}
