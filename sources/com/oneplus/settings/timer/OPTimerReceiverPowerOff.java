package com.oneplus.settings.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import com.oneplus.settings.timer.timepower.OPPowerOffPromptActivity;
import com.oneplus.settings.timer.timepower.SettingsUtil;
import com.oneplus.settings.utils.OPUtils;
import java.lang.reflect.Array;
import java.util.Calendar;

public class OPTimerReceiverPowerOff extends BroadcastReceiver {
    private PowerManager.WakeLock mLock = null;
    private PowerManager pm = null;

    static boolean intToBool(int i) {
        return i != 0;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        long[] nearestTime = SettingsUtil.getNearestTime(Settings.System.getString(context.getContentResolver(), "def_timepower_config"));
        this.pm = (PowerManager) context.getSystemService("power");
        if (action.equals("com.android.settings.action.REQUEST_POWER_OFF") || "android.intent.action.TIME_SET".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action) || "android.intent.action.BOOT_COMPLETED".equals(action)) {
            if (isPowerOffEnable(context)) {
                Intent intent2 = new Intent("com.android.settings.POWER_OP_OFF");
                intent2.setFlags(285212672);
                if (nearestTime[1] != 0) {
                    Calendar instance = Calendar.getInstance();
                    instance.setTimeInMillis(nearestTime[0]);
                    Log.d("boot", "Power on alarm with flag set:" + instance.getTime().toString());
                    ((AlarmManager) context.getSystemService("alarm")).setExactAndAllowWhileIdle(0, nearestTime[1], PendingIntent.getBroadcast(context, 0, intent2, 134217728));
                }
            }
        } else if (action.equals("com.android.settings.POWER_OP_OFF")) {
            long currentTimeMillis = ((System.currentTimeMillis() - nearestTime[1]) - 86400000) % 86400000;
            int i = (currentTimeMillis > 0 ? 1 : (currentTimeMillis == 0 ? 0 : -1));
            if (i >= 0 && currentTimeMillis > 60000) {
                return;
            }
            if (i >= 0 || currentTimeMillis <= -86340000) {
                Intent intent3 = new Intent(context, OPPowerOffPromptActivity.class);
                intent3.setFlags(268435456);
                context.startActivity(intent3);
            } else {
                return;
            }
        } else if (action.equals("com.android.settings.POWER_CONFIRM_OP_OFF")) {
            PowerManager.WakeLock wakeLock = this.mLock;
            if (wakeLock != null) {
                wakeLock.release();
                this.mLock = null;
            }
            PowerManager.WakeLock newWakeLock = this.pm.newWakeLock(268435466, "TimepowerWakeLock");
            this.mLock = newWakeLock;
            newWakeLock.acquire();
            Intent intent4 = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
            intent4.putExtra("android.intent.extra.KEY_CONFIRM", false);
            intent4.setFlags(268435456);
            context.startActivity(intent4);
        } else if (action.equals("com.android.settings.POWER_CANCEL_OP_OFF")) {
            Intent intent5 = new Intent("com.android.settings.POWER_OP_OFF");
            intent5.setFlags(285212672);
            ((AlarmManager) context.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(context, 0, intent5, 134217728));
        }
        if (!action.equals("com.android.settings.POWER_OP_ON") && !"android.intent.action.TIME_SET".equals(action) && !"android.intent.action.TIMEZONE_CHANGED".equals(action) && !"android.intent.action.BOOT_COMPLETED".equals(action)) {
            return;
        }
        if (!OPUtils.isSupportNewPlanPowerOffAlarm()) {
            Intent intent6 = new Intent("com.android.settings.POWER_OP_ON");
            intent6.setFlags(285212672);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
            PendingIntent broadcast = PendingIntent.getBroadcast(context, 1, intent6, 0);
            if (isPowerOnEnable(context)) {
                alarmManager.setExactAndAllowWhileIdle(0, nearestTime[0], broadcast);
            } else {
                alarmManager.cancel(broadcast);
            }
        } else {
            Intent intent7 = new Intent("com.android.settings.POWER_OP_ON");
            intent7.setFlags(285212672);
            AlarmManager alarmManager2 = (AlarmManager) context.getSystemService("alarm");
            PendingIntent broadcast2 = PendingIntent.getBroadcast(context, 0, intent7, 134217728);
            if (isPowerOnEnable(context)) {
                cancleNewPlanLastPowerOn(context);
                alarmManager2.setExactAndAllowWhileIdle(0, nearestTime[0], broadcast2);
                Intent intent8 = new Intent("org.codeaurora.poweroffalarm.action.SET_ALARM");
                intent8.addFlags(285212672);
                intent8.setPackage("com.qualcomm.qti.poweroffalarm");
                intent8.putExtra("time", nearestTime[0]);
                context.sendBroadcast(intent8);
                return;
            }
            alarmManager2.cancel(broadcast2);
        }
    }

    private void cancleNewPlanLastPowerOn(Context context) {
        long[] nearestTime = SettingsUtil.getNearestTime(Settings.System.getString(context.getContentResolver(), "def_timepower_config"));
        Intent intent = new Intent("org.codeaurora.poweroffalarm.action.CANCEL_ALARM");
        intent.addFlags(285212672);
        intent.putExtra("time", nearestTime[0]);
        intent.setPackage("com.qualcomm.qti.poweroffalarm");
        context.sendBroadcast(intent);
    }

    public static boolean isPowerOnEnable(Context context) {
        return checkSwitch(context, true);
    }

    public static boolean isPowerOffEnable(Context context) {
        return checkSwitch(context, false);
    }

    public static boolean checkSwitch(Context context, boolean z) {
        String string = Settings.System.getString(context.getContentResolver(), "def_timepower_config");
        if (string == null) {
            return false;
        }
        int[][] iArr = (int[][]) Array.newInstance(int.class, 2, 2);
        boolean[][] zArr = (boolean[][]) Array.newInstance(boolean.class, 2, 2);
        int i = 0;
        int i2 = 0;
        while (i <= 6) {
            int i3 = i + 6;
            String substring = string.substring(i, i3);
            iArr[i2][0] = Integer.parseInt(substring.substring(0, 2));
            iArr[i2][1] = Integer.parseInt(substring.substring(2, 4));
            zArr[i2][0] = intToBool(Integer.parseInt(substring.substring(4, 5)));
            zArr[i2][1] = intToBool(Integer.parseInt(substring.substring(5, 6)));
            i2++;
            i = i3;
        }
        if (z) {
            if (zArr[0][1]) {
                return true;
            }
        } else if (zArr[1][1]) {
            return true;
        }
        return false;
    }
}
