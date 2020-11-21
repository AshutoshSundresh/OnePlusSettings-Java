package com.oneplus.settings.chargingstations;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class OPChargingStationUtils {
    public static ILocationUpdate locationUpdate;

    /* access modifiers changed from: package-private */
    public interface ILocationUpdate {
        void onOPLocationUpdate();
    }

    public static void sendBroadcastToApp(Context context, String str) {
        Intent intent = new Intent("com.oneplus.intent.ACTION_NOTIFICATION");
        intent.putExtra("notif_type", str);
        intent.putExtra("is_from_settings", true);
        intent.setPackage("com.oneplus.chargingpilar");
        context.sendBroadcast(intent);
    }

    public static boolean putStringSystemProperty(Context context, String str, String str2) {
        return Settings.System.putString(context.getContentResolver(), str, str2);
    }

    public static String getStringSystemProperty(Context context, String str) {
        return Settings.System.getString(context.getContentResolver(), str);
    }

    public static String getStringGlobalProperty(Context context, String str) {
        return Settings.Global.getString(context.getContentResolver(), str);
    }

    public static boolean putIntSystemProperty(Context context, String str, int i) {
        return Settings.System.putInt(context.getContentResolver(), str, i);
    }

    public static int getIntSystemProperty(Context context, String str, int i) {
        return Settings.System.getInt(context.getContentResolver(), str, i);
    }

    public static boolean putLongSystemProperty(Context context, String str, long j) {
        return Settings.System.putLong(context.getContentResolver(), str, j);
    }

    public static long getLongSystemProperty(Context context, String str, long j) {
        return Settings.System.getLong(context.getContentResolver(), str, j);
    }

    public static void setLocationUpdate(ILocationUpdate iLocationUpdate) {
        locationUpdate = iLocationUpdate;
    }

    public static ILocationUpdate getLocationUpdate() {
        return locationUpdate;
    }
}
