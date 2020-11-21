package com.oneplus.security.network.operator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.oneplus.security.BaseSharePreference;
import com.oneplus.security.utils.OPSNSUtils;

public class NativeOperatorDataManager extends BaseSharePreference {
    public static void savePkgTotalInByte(Context context, int i, long j) {
        Log.d("nOperatorDataModel", "saved value is " + j);
        saveLongValueInSharePreference(context, "key_sim_total_in_byte_" + OPSNSUtils.findSubIdBySlotId(i), j);
    }

    public static long getPkgTotalInByte(Context context, int i) {
        return getLongValueInSharedPreference(context, "key_sim_total_in_byte_" + OPSNSUtils.findSubIdBySlotId(i), -1);
    }

    public static void saveAccountDay(Context context, int i, int i2) {
        AccountDayLocalCache.setAccountDay(context, i, i2);
    }

    public static int getAccountDay(Context context, int i) {
        return AccountDayLocalCache.getAccountDay(context, i);
    }

    public static void savePkgUsedMonthlyInByte(Context context, int i, long j) {
        saveLongValueInSharePreference(context, "key_sim_monthly_usage_in_byte_" + OPSNSUtils.findSubIdBySlotId(i), j);
    }

    public static long getPkgUsedMonthlyInByte(Context context, int i) {
        long longValueInSharedPreference = getLongValueInSharedPreference(context, "key_sim_monthly_usage_in_byte_" + OPSNSUtils.findSubIdBySlotId(i), -1);
        return longValueInSharedPreference == -1 ? PkgInfoLocalCache.getPkgUsedMonthlyLocalCache(context, i) : longValueInSharedPreference;
    }

    private static void saveLongValueInSharePreference(Context context, String str, long j) {
        Log.d("nOperatorDataModel", "saveLongValueInSharePreference: key = " + str + " value = " + j);
        SharedPreferences.Editor edit = BaseSharePreference.getDefaultSharedPreferences("key_native_operator_data_storage").edit();
        edit.putLong(str, j);
        edit.apply();
    }

    private static long getLongValueInSharedPreference(Context context, String str, long j) {
        long j2 = BaseSharePreference.getDefaultSharedPreferences("key_native_operator_data_storage").getLong(str, j);
        Log.d("nOperatorDataModel", "retrieved key  = " + str + " value is " + j2);
        return j2;
    }
}
