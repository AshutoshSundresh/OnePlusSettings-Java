package com.oneplus.security.network.trafficalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import com.oneplus.security.BaseSharePreference;
import com.oneplus.security.utils.OPSNSUtils;

public class TrafficUsageAlarmUtils extends BaseSharePreference {
    public static void setDataTotalState(Context context, boolean z, int i) {
        String str = "key_data_usage_total_state_subid_" + OPSNSUtils.findSubIdBySlotId(i);
        Settings.System.putIntForUser(context.getContentResolver(), str, z ? 1 : 0, 0);
        SharedPreferences.Editor edit = BaseSharePreference.getDefaultSharedPreferences("traffic_usage_alert").edit();
        edit.putBoolean(str, z);
        edit.commit();
    }

    public static boolean getDataTotalState(Context context, int i) {
        return BaseSharePreference.getDefaultSharedPreferences("traffic_usage_alert").getBoolean("key_data_usage_total_state_subid_" + OPSNSUtils.findSubIdBySlotId(i), false);
    }

    public static void setDataWarnState(Context context, boolean z, int i) {
        String tenPercentDataLeftConfigKey = getTenPercentDataLeftConfigKey(i);
        setTrafficUsagePreferenceBooleanValue(context, tenPercentDataLeftConfigKey, z);
        Settings.System.putIntForUser(context.getContentResolver(), tenPercentDataLeftConfigKey, z ? 1 : 0, 0);
    }

    public static boolean getDataWarnState(Context context, boolean z, int i) {
        if (-1 == i) {
            return false;
        }
        return getTrafficUsagePreferenceBooleanValue(context, getTenPercentDataLeftConfigKey(i), z);
    }

    public static boolean getDataWarnState(Context context, boolean z) {
        return getDataWarnState(context, z, 0) || getDataWarnState(context, z, 1);
    }

    public static void setDataWarnValue(Context context, long j, int i) {
        String dataUsageLeftAlertConfigKey = getDataUsageLeftAlertConfigKey(i);
        setTrafficUsagePreferenceStringValue(context, dataUsageLeftAlertConfigKey, j);
        Settings.System.putLongForUser(context.getContentResolver(), dataUsageLeftAlertConfigKey, j, 0);
    }

    public static void setDataLimitValue(Context context, long j, int i) {
        Settings.System.putLongForUser(context.getContentResolver(), getDataUsageLimitValueConfigKey(i), j, 0);
    }

    public static void setSystemDataLimitValue(Context context, long j, int i) {
        Log.d("TrafficUsageAlarmUtils", "setSystemDataLimitValue: value " + j);
        setTrafficUsagePreferenceStringValue(context, "system_datausage_limit_value_sim_" + OPSNSUtils.findSubIdBySlotId(i), j);
    }

    public static void setSystemDataWarnValue(Context context, long j, int i) {
        setTrafficUsagePreferenceStringValue(context, "system_datausage_warn_value_sim_" + OPSNSUtils.findSubIdBySlotId(i), j);
    }

    public static long getSystemDataWarnValue(Context context, int i, long j) {
        return getTrafficUsagePreferenceStringValue(context, "system_datausage_warn_value_sim_" + OPSNSUtils.findSubIdBySlotId(i), j);
    }

    public static long getSystemDataLimitValue(Context context, int i, long j) {
        return getTrafficUsagePreferenceStringValue(context, "system_datausage_limit_value_sim_" + OPSNSUtils.findSubIdBySlotId(i), j);
    }

    public static long getDataWarnValue(Context context, int i, long j) {
        return getTrafficUsagePreferenceStringValue(context, getDataUsageLeftAlertConfigKey(i), j);
    }

    public static void setHasDataWarnAlerted(Context context, boolean z, int i) {
        if (i != -1) {
            setTrafficUsagePreferenceBooleanValue(context, "key_has_sim_alert_ten_percent_left_" + OPSNSUtils.findSubIdBySlotId(i), z);
            return;
        }
        logOutUsingInvalidSlotId();
    }

    public static boolean getHasDataWarnAlerted(Context context, boolean z, int i) {
        if (i != -1) {
            return getTrafficUsagePreferenceBooleanValue(context, "key_has_sim_alert_ten_percent_left_" + OPSNSUtils.findSubIdBySlotId(i), z);
        }
        logOutUsingInvalidSlotId();
        return z;
    }

    public static void setHasAlertedTrafficRunningOut(Context context, boolean z, int i) {
        if (-1 != i) {
            setTrafficUsagePreferenceBooleanValue(context, "key_has_sim_alert_running_out" + OPSNSUtils.findSubIdBySlotId(i), z);
            return;
        }
        logOutUsingInvalidSlotId();
    }

    public static boolean isTrafficRunningOutAlreadyAlerted(Context context, boolean z, int i) {
        Log.d("TrafficUsageAlarmUtils", "isTrafficRunningOutAlreadyAlerted: defaultValue" + z);
        Log.d("TrafficUsageAlarmUtils", "isTrafficRunningOutAlreadyAlerted: INVALID_SIM_SLOT_ID-1");
        Log.d("TrafficUsageAlarmUtils", "isTrafficRunningOutAlreadyAlerted: slotId" + i);
        if (-1 != i) {
            return getTrafficUsagePreferenceBooleanValue(context, "key_has_sim_alert_running_out" + OPSNSUtils.findSubIdBySlotId(i), z);
        }
        logOutUsingInvalidSlotId();
        return z;
    }

    private static void logOutUsingInvalidSlotId() {
        Log.e("TrafficUsageAlarmUtils", "save has traffic running out auto close on invalid slot id");
    }

    private static void logOutUsingInvalidKey() {
        Log.e("TrafficUsageAlarmUtils", "save has traffic running out auto close on invalid key");
    }

    private static void setTrafficUsagePreferenceBooleanValue(Context context, String str, boolean z) {
        if (str == null) {
            logOutUsingInvalidKey();
            return;
        }
        Log.d("TrafficUsageAlarmUtils", "setTrafficUsagePreferenceBooleanValue: state " + z);
        SharedPreferences.Editor edit = BaseSharePreference.getDefaultSharedPreferences("traffic_usage_alert").edit();
        edit.putBoolean(str, z);
        edit.commit();
    }

    private static boolean getTrafficUsagePreferenceBooleanValue(Context context, String str, boolean z) {
        if (str == null) {
            logOutUsingInvalidKey();
            return false;
        }
        Log.d("TrafficUsageAlarmUtils", "getTrafficUsagePreferenceBooleanValue: key " + str);
        boolean z2 = BaseSharePreference.getDefaultSharedPreferences("traffic_usage_alert").getBoolean(str, z);
        Log.d("TrafficUsageAlarmUtils", "getTrafficUsagePreferenceBooleanValue: state" + z2);
        return z2;
    }

    private static void setTrafficUsagePreferenceStringValue(Context context, String str, long j) {
        if (str == null) {
            logOutUsingInvalidKey();
            return;
        }
        SharedPreferences.Editor edit = BaseSharePreference.getDefaultSharedPreferences("traffic_usage_alert").edit();
        edit.putLong(str, j);
        edit.commit();
    }

    private static long getTrafficUsagePreferenceStringValue(Context context, String str, long j) {
        if (str != null) {
            return BaseSharePreference.getDefaultSharedPreferences("traffic_usage_alert").getLong(str, j);
        }
        logOutUsingInvalidKey();
        return j;
    }

    public static boolean shouldAlertTrafficRunningOut(Context context, int i) {
        boolean isTrafficRunningOutAlreadyAlerted = isTrafficRunningOutAlreadyAlerted(context, true, i);
        boolean dataTotalState = getDataTotalState(context, i);
        Log.d("TrafficUsageAlarmUtils", "shouldAlertTrafficRunningOut: isTrafficRunningOutAlreadyAlerted " + isTrafficRunningOutAlreadyAlerted);
        Log.d("TrafficUsageAlarmUtils", "shouldAlertTrafficRunningOut: limitState" + dataTotalState);
        if (isTrafficRunningOutAlreadyAlerted || !dataTotalState) {
            return false;
        }
        return true;
    }

    public static boolean shouldStartRunningOutMonitorService(Context context) {
        return !isTrafficRunningOutAlreadyAlerted(context, true, 0) || !isTrafficRunningOutAlreadyAlerted(context, true, 1);
    }

    public static boolean shouldAlertDataWarn(Context context, int i) {
        boolean dataWarnState = getDataWarnState(context, false, i);
        boolean hasDataWarnAlerted = getHasDataWarnAlerted(context, false, i);
        if (!dataWarnState || hasDataWarnAlerted) {
            return false;
        }
        return true;
    }

    public static boolean shouldStartDataWarnMonitorService(Context context) {
        return shouldAlertDataWarn(context, 0) || shouldAlertDataWarn(context, 1);
    }

    private static String getTenPercentDataLeftConfigKey(int i) {
        if (-1 == i) {
            return null;
        }
        return "key_ten_percent_low_remaining_state_sim_" + OPSNSUtils.findSubIdBySlotId(i);
    }

    private static String getDataUsageLeftAlertConfigKey(int i) {
        if (-1 == i) {
            return null;
        }
        return "key_datausage_alert_number_sim_" + OPSNSUtils.findSubIdBySlotId(i);
    }

    private static String getDataUsageLimitValueConfigKey(int i) {
        if (-1 == i) {
            return null;
        }
        return "key_datausage_limit_number_sim_" + OPSNSUtils.findSubIdBySlotId(i);
    }

    public static void resetTrafficDialogAlertedState(Context context, int i) {
        setHasDataWarnAlerted(context, false, i);
        setHasAlertedTrafficRunningOut(context, false, i);
    }
}
