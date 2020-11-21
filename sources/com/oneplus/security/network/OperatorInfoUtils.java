package com.oneplus.security.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class OperatorInfoUtils {
    public static void setCurrentDisplayingSlotId(Context context, int i) {
        SharedPreferences.Editor edit = context.getSharedPreferences("key_operator_info_shared_pref", 0).edit();
        edit.putInt("key_current_using_sim_slot_id", i);
        edit.apply();
        Log.d("OperatorInfoUtils", "current using slot id is " + i);
    }

    public static int getCurrentDisplayingSlotId(Context context) {
        int i = context.getSharedPreferences("key_operator_info_shared_pref", 0).getInt("key_current_using_sim_slot_id", -1);
        Log.d("OperatorInfoUtils", "current fetched using slot id is " + i);
        return i;
    }
}
