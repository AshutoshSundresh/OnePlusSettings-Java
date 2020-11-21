package com.oneplus.settings.utils;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0006R$color;
import com.oneplus.compat.util.OpThemeNative;
import com.oneplus.custom.utils.OpCustomizeSettings;
import com.oneplus.settings.SettingsBaseApplication;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;

public final class OPThemeUtils {
    public static String getCurrentHorizonLightByIndex(Context context, int i) {
        return i != 1 ? i != 2 ? i != 3 ? i != 10 ? "blue" : "mcl" : "purple" : "gold" : "red";
    }

    public static String getCurrentShapeByIndex(int i) {
        return i != 2 ? i != 3 ? i != 4 ? "circle" : "squircle" : "teardrop" : "roundedrect";
    }

    public static int getCurrentCustomizationTheme(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "op_customization_theme_style", 0);
    }

    public static int getCurrentBasicColorMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "oem_black_mode", 0);
    }

    public static void setCurrentBasicColorMode(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "oem_black_mode", i);
        SystemProperties.set("persist.sys.theme.status", String.valueOf(i));
    }

    public static int getCurrentShape(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "oneplus_shape", 1);
    }

    public static void setCurrentShape(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "oneplus_shape", i);
    }

    public static int getCurrentFont(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "oem_font_mode", 1);
    }

    public static void setCurrentHorizonLight(Context context, int i) {
        Settings.System.putIntForUser(context.getContentResolver(), "op_custom_horizon_light_animation_style", i, -2);
    }

    public static void enableTheme(String str, String str2, Context context) {
        HashMap hashMap = new HashMap();
        if (TextUtils.equals(str, "oneplus_basiccolor")) {
            hashMap.put("oneplus_basiccolor", "white");
            OpThemeNative.disableTheme(context, hashMap);
            hashMap.put("oneplus_basiccolor", "black");
            OpThemeNative.disableTheme(context, hashMap);
        } else if (TextUtils.equals(str, "oneplus_dynamicfont")) {
            hashMap.put("oneplus_dynamicfont", "2");
            OpThemeNative.disableTheme(context, hashMap);
            hashMap.put("oneplus_dynamicfont", "1");
            OpThemeNative.disableTheme(context, hashMap);
        } else if (TextUtils.equals(str, "oneplus_shape")) {
            hashMap.put("oneplus_shape", "squircle");
            OpThemeNative.disableTheme(context, hashMap);
            hashMap.put("oneplus_shape", "circle");
            OpThemeNative.disableTheme(context, hashMap);
            hashMap.put("oneplus_shape", "teardrop");
            OpThemeNative.disableTheme(context, hashMap);
            hashMap.put("oneplus_shape", "roundedrect");
            OpThemeNative.disableTheme(context, hashMap);
        } else if (TextUtils.equals(str, "oneplus_aodnotification")) {
            hashMap.put("oneplus_aodnotification", "gold");
            OpThemeNative.disableTheme(context, hashMap);
            hashMap.put("oneplus_aodnotification", "red");
            OpThemeNative.disableTheme(context, hashMap);
            hashMap.put("oneplus_aodnotification", "purple");
            OpThemeNative.disableTheme(context, hashMap);
        }
        OpThemeNative.disableTheme(context, hashMap);
        PrintStream printStream = System.out;
        printStream.println("oneplus--enableTheme-category:" + str + " secondCategory:" + str2);
        HashMap hashMap2 = new HashMap();
        hashMap2.put(str, str2);
        OpThemeNative.enableTheme(context, hashMap2);
    }

    public static boolean isSupportMclTheme() {
        return OpCustomizeSettings.CUSTOM_TYPE.MCL.equals(OpCustomizeSettings.getCustomType());
    }

    public static boolean isSupportAVGTheme() {
        return OpCustomizeSettings.CUSTOM_TYPE.AVG.equals(OpCustomizeSettings.getCustomType());
    }

    public static boolean isSupportSwTheme() {
        return OpCustomizeSettings.CUSTOM_TYPE.SW.equals(OpCustomizeSettings.getCustomType());
    }

    public static boolean isSupportCustomeTheme() {
        return isSupportMclTheme() || isSupportAVGTheme() || isSupportSwTheme();
    }

    public static void setDialogTextColor(AlertDialog alertDialog) {
        try {
            Field declaredField = AlertDialog.class.getDeclaredField("mAlert");
            declaredField.setAccessible(true);
            PrintStream printStream = System.out;
            printStream.println("zhuyang--setDialogTextColor-dialog:" + alertDialog + " mAlert:" + declaredField);
            Object obj = declaredField.get(alertDialog);
            Field declaredField2 = obj.getClass().getDeclaredField("mTitleView");
            declaredField2.setAccessible(true);
            ((TextView) declaredField2.get(obj)).setTextColor(SettingsBaseApplication.mApplication.getResources().getColor(C0006R$color.op_control_text_color_primary_dark));
            Field declaredField3 = obj.getClass().getDeclaredField("mMessageView");
            declaredField3.setAccessible(true);
            ((TextView) declaredField3.get(obj)).setTextColor(SettingsBaseApplication.mApplication.getResources().getColor(C0006R$color.op_control_text_color_secondary_dark));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
        }
    }
}
