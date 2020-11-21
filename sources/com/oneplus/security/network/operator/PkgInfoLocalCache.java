package com.oneplus.security.network.operator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.oneplus.security.BaseSharePreference;
import com.oneplus.security.utils.OPSNSUtils;

public class PkgInfoLocalCache extends BaseSharePreference {
    private static Object mLock = new Object();

    public static long getPkgUsedMonthlyLocalCache(Context context, int i) {
        long j;
        SharedPreferences sharedPreferences;
        synchronized (mLock) {
            SharedPreferences defaultSharedPreferences = BaseSharePreference.getDefaultSharedPreferences("key_pkg_info_local_cache");
            try {
                j = defaultSharedPreferences.getLong("key_pkg_used_this_month_subid_" + OPSNSUtils.findSubIdBySlotId(i), -1);
            } catch (ClassCastException unused) {
                j = (long) sharedPreferences.getInt("key_pkg_used_this_month_subid_" + OPSNSUtils.findSubIdBySlotId(i), -1);
            }
        }
        Log.d("PkgInfoLocalCache", "get slot id is " + i + " value is " + j);
        return j;
    }
}
