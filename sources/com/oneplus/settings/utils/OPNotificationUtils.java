package com.oneplus.settings.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.SystemProperties;
import com.android.settings.C0017R$string;
import com.oneplus.settings.ringtone.OPMyLog;

public class OPNotificationUtils {
    public static String getRingtoneAlias(Context context, int i, String str) {
        return str;
    }

    public static String replaceWith(Context context, String str, String str2) {
        String string = context.getResources().getString(C0017R$string.oneplus_unknown_ringtone);
        if (str2 == null) {
            return str;
        }
        int i = 1;
        if (str2.endsWith("notification_sound") || str2.endsWith("mms_notification")) {
            i = 2;
        } else if (str2.endsWith("alarm_alert")) {
            i = 4;
        }
        OPMyLog.d("", "type:" + i + " settingsName:" + str2);
        getRingtoneAlias(context, i, str);
        if (!str.contains(string)) {
            return str;
        }
        restoreRingtoneIfNotExist(context, str2);
        if (str2.endsWith("ringtone")) {
            restoreRingtoneIfNotExist(context, "ringtone_2");
        }
        if (str2.endsWith("ringtone")) {
            return context.getResources().getString(C0017R$string.oneplus_ringtones_oneplus_tune);
        }
        if (str2.endsWith("notification_sound")) {
            return context.getResources().getString(C0017R$string.oneplus_notifications_meet);
        }
        return context.getResources().getString(C0017R$string.oneplus_notifications_free);
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void restoreRingtoneIfNotExist(android.content.Context r10, java.lang.String r11) {
        /*
        // Method dump skipped, instructions count: 157
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.utils.OPNotificationUtils.restoreRingtoneIfNotExist(android.content.Context, java.lang.String):void");
    }

    private static boolean hasData(Cursor cursor) {
        return cursor != null && cursor.getCount() > 0;
    }

    private static String getDefaultRingtoneFileName(Context context, String str) {
        return SystemProperties.get("ro.config." + str);
    }
}
