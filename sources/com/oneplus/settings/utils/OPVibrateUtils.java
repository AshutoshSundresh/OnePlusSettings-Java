package com.oneplus.settings.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ringtone.OPRingtoneManager;
import com.oneplus.util.RingtoneManagerUtils;
import java.util.Arrays;

public final class OPVibrateUtils {
    private static final long[] mAlarmVibratePattern = {500, 500};
    private static long[][] sVibratePatternrhythm = {new long[]{-2, 0, 1000, 1000, 1000, 1000}, new long[]{-2, 0, 500, 250, 10, 1000, 500, 250, 10, 1000}, new long[]{-2, 0, 300, 400, 300, 400, 300, 1000, 300, 400, 300, 400, 300, 1000}, new long[]{-2, 0, 30, 80, 30, 80, 50, 180, 600, 1000, 30, 80, 30, 80, 50, 180, 600, 1000}, new long[]{-2, 0, 80, 200, 600, 150, 10, 1000, 80, 200, 600, 150, 10, 1000}};

    private static int getVibrateLevel(int i) {
        if (i != 0) {
            return i != 2 ? -2 : -3;
        }
        return -1;
    }

    public static void startVibrateByType(Vibrator vibrator) {
        long[][] jArr = sVibratePatternrhythm;
        if (vibrator != null) {
            int i = Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "incoming_call_vibrate_intensity", -1);
            vibrator.cancel();
            int ringtoneVibrateMode = getRingtoneVibrateMode(SettingsBaseApplication.mApplication);
            if (ringtoneVibrateMode >= 5) {
                ringtoneVibrateMode = 0;
            }
            if (i == 0) {
                jArr[ringtoneVibrateMode][0] = -1;
            } else if (i == 1) {
                jArr[ringtoneVibrateMode][0] = -2;
            } else if (i == 2) {
                jArr[ringtoneVibrateMode][0] = -3;
            }
            vibrator.vibrate(jArr[ringtoneVibrateMode], 0);
            Log.d("OPVibrateUtils", "startVibrateByType--type:" + ringtoneVibrateMode + " pattern:" + Arrays.toString(jArr[ringtoneVibrateMode]));
        }
    }

    public static boolean isDynamicVibrateMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "incoming_call_vibrate_mode", 0) == 5;
    }

    public static int getRingtoneVibrateMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "incoming_call_vibrate_mode", 0);
    }

    public static boolean isThreeKeyVibrateMode(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "three_Key_mode", 1) == 2;
    }

    public static boolean isThreeKeyMuteMode(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "three_Key_mode", 1) == 1;
    }

    public static boolean isThreeKeyRingMode(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "three_Key_mode", 1) == 3;
    }

    public static boolean isVibrateWhenRinging(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "vibrate_when_ringing", 0) == 1;
    }

    public static boolean isSystemRingtone(Uri uri, int i) {
        return OPRingtoneManager.isSystemRingtone(SettingsBaseApplication.mApplication, uri, i);
    }

    public static void startVibrateForRingtone(Context context, Uri uri, Vibrator vibrator) {
        if (OPUtils.isSupportXVibrate() && isSystemRingtone(uri, 1) && !isThreeKeyMuteMode(context) && isVibrateWhenRinging(context)) {
            if ((isThreeKeyVibrateMode(context) && isDynamicVibrateMode(context)) || !isThreeKeyRingMode(context)) {
                return;
            }
            if (isDynamicVibrateMode(context)) {
                startVibrateWithRingtoneUri(context, "incoming_call_vibrate_intensity", uri, vibrator);
            } else {
                startVibrateByType(vibrator);
            }
        }
    }

    public static void startVibrateForSms(Context context, Uri uri, Vibrator vibrator) {
        if (isThreeKeyVibrateMode(context) || !OPUtils.isSupportXVibrate() || isThreeKeyMuteMode(context)) {
            return;
        }
        if (isSystemRingtone(uri, 8)) {
            startVibrateWithRingtoneUri(context, "notice_vibrate_intensity", uri, vibrator);
        } else if (!isThreeKeyVibrateMode(context)) {
            Log.d("OPVibrateUtils", "startVibrateForSms--normal--vibrate");
        }
    }

    public static void startVibrateForNotification(Context context, Uri uri, Vibrator vibrator) {
        if (isThreeKeyVibrateMode(context) || !OPUtils.isSupportXVibrate() || isThreeKeyMuteMode(context)) {
            return;
        }
        if (isSystemRingtone(uri, 2)) {
            startVibrateWithRingtoneUri(context, "notice_vibrate_intensity", uri, vibrator);
        } else if (!isThreeKeyVibrateMode(context)) {
            Log.d("OPVibrateUtils", "startVibrateForNotification--normal-vibrate");
        }
    }

    public static void startVibrateForAlarm(Context context, Uri uri, Vibrator vibrator) {
        long[] jArr = mAlarmVibratePattern;
        if (((AudioManager) context.getSystemService("audio")).getStreamVolume(4) == 0 || !OPUtils.isSupportXVibrate() || !isThreeKeyRingMode(context)) {
            return;
        }
        if (isSystemRingtone(uri, 4)) {
            startVibrateWithRingtoneUri(context, "incoming_call_vibrate_intensity", uri, vibrator);
            return;
        }
        Log.d("OPVibrateUtils", "startVibrateForAlarm--normal-vibrate");
        if (Build.VERSION.SDK_INT >= 21) {
            vibrator.vibrate(jArr, 0, new AudioAttributes.Builder().setUsage(4).setContentType(4).build());
        } else {
            vibrator.vibrate(jArr, 0);
        }
    }

    public static void startVibrateWithRingtoneUri(Context context, String str, Uri uri, Vibrator vibrator) {
        vibrator.cancel();
        int vibratorSceneId = RingtoneManagerUtils.getVibratorSceneId(context, uri);
        int vibratorEffect = vibrator.setVibratorEffect(vibratorSceneId);
        int i = Settings.System.getInt(context.getContentResolver(), str, 1);
        long[] jArr = {(long) getVibrateLevel(i), 0, (long) vibratorEffect};
        Log.d("OPVibrateUtils", "OPVibrateUtils--sceneId:" + vibratorSceneId + " ringtoneUri:" + uri + " key: " + str + "vibrateTime:" + vibratorEffect + " delayTime:0 vibrateLevel:" + getVibrateLevel(i));
        vibrator.vibrate(jArr, 0);
    }
}
