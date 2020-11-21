package com.oneplus.settings.utils;

import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

public class OPSNSUtils {
    public static int findSlotIdBySubId(int i) {
        int phoneCount = TelephonyManager.getDefault().getPhoneCount();
        for (int i2 = 0; i2 < phoneCount; i2++) {
            int[] subId = SubscriptionManager.getSubId(i2);
            if (subId != null && subId.length > 0 && i == subId[0]) {
                return i2;
            }
        }
        return 0;
    }
}
