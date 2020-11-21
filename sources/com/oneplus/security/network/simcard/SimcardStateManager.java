package com.oneplus.security.network.simcard;

import android.content.Context;
import android.content.SharedPreferences;
import com.oneplus.security.utils.LogUtils;

public class SimcardStateManager {
    public static void setShouldAlertSimcardHasPopedOut(Context context, boolean z, int i) {
        SharedPreferences.Editor edit = getSimOutAlertSharePreference(context).edit();
        if (i == 0) {
            edit.putBoolean("key_should_alert_sim_has_poped_slot_one", z);
        } else if (1 == i) {
            edit.putBoolean("key_should_alert_sim_has_poped_slot_two", z);
        } else {
            LogUtils.d("SimcardStateManager", "set with invalid slotId, error.");
        }
        edit.apply();
    }

    public static boolean getShouldAlertSimcardHasPopedOut(Context context, int i) {
        SharedPreferences simOutAlertSharePreference = getSimOutAlertSharePreference(context);
        if (i == 0) {
            return simOutAlertSharePreference.getBoolean("key_should_alert_sim_has_poped_slot_one", false);
        }
        if (1 == i) {
            return simOutAlertSharePreference.getBoolean("key_should_alert_sim_has_poped_slot_two", false);
        }
        LogUtils.d("SimcardStateManager", "invalid slotId " + i);
        return false;
    }

    private static SharedPreferences getSimOutAlertSharePreference(Context context) {
        return context.getSharedPreferences("key_sp_sim_out_alert", 0);
    }
}
