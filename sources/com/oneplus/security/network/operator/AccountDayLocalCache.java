package com.oneplus.security.network.operator;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import com.oneplus.security.BaseSharePreference;
import com.oneplus.security.utils.OPSNSUtils;
import com.oneplus.security.utils.TimeRegionUtils;

public class AccountDayLocalCache extends BaseSharePreference {
    public static void setAccountDay(Context context, int i, int i2) {
        String str = "key_account_day_slot_" + OPSNSUtils.findSubIdBySlotId(i);
        SharedPreferences.Editor edit = BaseSharePreference.getDefaultSharedPreferences("key_account_day_local_cache").edit();
        edit.putInt(str, i2);
        edit.apply();
        Settings.System.putIntForUser(context.getContentResolver(), str, i2, 0);
    }

    public static int getAccountDay(Context context, int i) {
        SharedPreferences defaultSharedPreferences = BaseSharePreference.getDefaultSharedPreferences("key_account_day_local_cache");
        return defaultSharedPreferences.getInt("key_account_day_slot_" + OPSNSUtils.findSubIdBySlotId(i), 1);
    }

    public static long[] getDataUsageSectionTimeMillByAccountDay(Context context, int i) {
        return TimeRegionUtils.getRegionTime(getAccountDay(context, i), System.currentTimeMillis());
    }
}
