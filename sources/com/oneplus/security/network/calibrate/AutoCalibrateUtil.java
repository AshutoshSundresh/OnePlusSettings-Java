package com.oneplus.security.network.calibrate;

import android.content.Context;
import android.content.SharedPreferences;
import com.oneplus.security.BaseSharePreference;
import com.oneplus.security.network.simcard.SimcardDataModel;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.OPSNSUtils;

public class AutoCalibrateUtil extends BaseSharePreference {
    public static void setLastCalibrateTime(Context context, long j, int i, boolean z) {
        if (-1 != i) {
            if (!SimcardDataModel.getInstance(context).isSlotSimReady(i)) {
                LogUtils.w("AutoCalibrateUtil", "The sim card is not ready , simIndex" + i);
                return;
            }
            SharedPreferences.Editor edit = BaseSharePreference.getDefaultSharedPreferences("auto_calibrate_function").edit();
            edit.putLong("key_sim_last_calibrate_time_" + OPSNSUtils.findSubIdBySlotId(i), j);
            edit.commit();
            if (z) {
                AutoSaveNativeTrafficUsageService.getAndSaveCurrentNativeTrafficTotalUsage(context, i);
            }
            LogUtils.i("AutoCalibrateUtil", "save last calibrate time " + j);
        }
    }

    public static long getNativeTotalUsageWhenLastCalibrated(Context context, int i) {
        SharedPreferences defaultSharedPreferences = BaseSharePreference.getDefaultSharedPreferences("key_native_total_usage_last_calibrate_shared_preference");
        return defaultSharedPreferences.getLong("key_native_total_usage_last_calibrated_" + OPSNSUtils.findSubIdBySlotId(i), -1);
    }

    public static long getLastCalibrateTime(Context context, int i) {
        SharedPreferences defaultSharedPreferences = BaseSharePreference.getDefaultSharedPreferences("auto_calibrate_function");
        return defaultSharedPreferences.getLong("key_sim_last_calibrate_time_" + OPSNSUtils.findSubIdBySlotId(i), -1);
    }

    public static void saveNativeTotalUsageWhenLastCalibrated(Context context, int i, long j) {
        SharedPreferences.Editor edit = BaseSharePreference.getDefaultSharedPreferences("key_native_total_usage_last_calibrate_shared_preference").edit();
        edit.putLong("key_native_total_usage_last_calibrated_" + OPSNSUtils.findSubIdBySlotId(i), j);
        edit.apply();
    }
}
