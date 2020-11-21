package com.oneplus.settings.gestures;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.net.Uri;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.C0017R$string;
import java.util.List;

public class OPGestureUtils {
    public static int get(int i, int i2) {
        return (i & (1 << i2)) >> i2;
    }

    public static String getGestureTypebyGestureKey(String str) {
        if (str.equals("oneplus_draw_o_start_app")) {
            return "oem_acc_blackscreen_gesture_o";
        }
        if (str.equals("oneplus_draw_v_start_app")) {
            return "oem_acc_blackscreen_gesture_v";
        }
        if (str.equals("oneplus_draw_s_start_app")) {
            return "oem_acc_blackscreen_gesture_s";
        }
        if (str.equals("oneplus_draw_m_start_app")) {
            return "oem_acc_blackscreen_gesture_m";
        }
        return str.equals("oneplus_draw_w_start_app") ? "oem_acc_blackscreen_gesture_w" : "";
    }

    public static String getGesturePackageName(Context context, String str) {
        context.getString(C0017R$string.oneplus_draw_gesture_start_none);
        if (!str.startsWith("oem_acc_blackscreen_gesture")) {
            str = getGestureTypebyGestureKey(str);
        }
        String string = Settings.System.getString(context.getContentResolver(), str);
        if (TextUtils.isEmpty(string)) {
            return context.getString(C0017R$string.oneplus_draw_gesture_start_none);
        }
        String[] split = string.split(";");
        String str2 = split[0];
        if (str2.startsWith("OpenApp:")) {
            return str2.substring(8);
        }
        if (!str2.startsWith("OpenShortcut:")) {
            return "";
        }
        String substring = str2.substring(13);
        if (hasShortCutsId(context, substring, split[1])) {
            return substring;
        }
        return context.getString(C0017R$string.oneplus_draw_gesture_start_none);
    }

    public static String getGesturePacakgeUid(Context context, String str) {
        context.getString(C0017R$string.oneplus_draw_gesture_start_none);
        if (!str.startsWith("oem_acc_blackscreen_gesture")) {
            str = getGestureTypebyGestureKey(str);
        }
        String string = Settings.System.getString(context.getContentResolver(), str);
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        String[] split = string.split(";");
        String str2 = split[0];
        if (str2.startsWith("OpenApp:")) {
            if (split.length > 1) {
                return split[1];
            }
            return "";
        } else if (!str2.startsWith("OpenShortcut:")) {
            return "";
        } else {
            str2.substring(13);
            if (split.length > 2) {
                return split[2];
            }
            return "";
        }
    }

    public static String getGestureSummarybyGestureKey(Context context, String str) {
        context.getString(C0017R$string.oneplus_draw_gesture_start_none);
        if (!str.startsWith("oem_acc_blackscreen_gesture")) {
            str = getGestureTypebyGestureKey(str);
        }
        String string = Settings.System.getString(context.getContentResolver(), str);
        if (TextUtils.isEmpty(string)) {
            return context.getString(C0017R$string.oneplus_draw_gesture_start_none);
        }
        if ("OpenCamera".equals(string)) {
            return context.getString(C0017R$string.oneplus_gestures_open_camera);
        }
        if ("FrontCamera".equals(string)) {
            return context.getString(C0017R$string.oneplus_gestures_open_front_camera);
        }
        if ("TakeVideo".equals(string)) {
            return context.getString(C0017R$string.oneplus_gestures_take_video);
        }
        if ("OpenTorch".equals(string)) {
            return context.getString(C0017R$string.oneplus_gestures_open_flashlight);
        }
        if ("OpenShelf".equals(string)) {
            return context.getString(C0017R$string.hardware_keys_action_shelf);
        }
        String[] split = string.split(";");
        String str2 = split[0];
        if (str2.startsWith("OpenApp:")) {
            str2 = str2.substring(8);
        } else if (str2.startsWith("OpenShortcut:")) {
            String substring = str2.substring(13);
            String str3 = split[1];
            if (!hasShortCutsId(context, substring, str3)) {
                return context.getString(C0017R$string.oneplus_draw_gesture_start_none);
            }
            return getAppNameByPackageName(context, substring) + "/" + getShortCutsNameByID(context, substring, str3);
        }
        return getAppNameByPackageName(context, str2);
    }

    public static String getShortCutIdByGestureKey(Context context, String str) {
        context.getString(C0017R$string.oneplus_draw_gesture_start_none);
        if (!str.startsWith("oem_acc_blackscreen_gesture")) {
            str = getGestureTypebyGestureKey(str);
        }
        String string = Settings.System.getString(context.getContentResolver(), str);
        if (TextUtils.isEmpty(string)) {
            return context.getString(C0017R$string.oneplus_draw_gesture_start_none);
        }
        String[] split = string.split(";");
        return split[0].startsWith("OpenShortcut:") ? split[1] : "";
    }

    public static boolean hasShortCutsGesture(Context context, String str) {
        context.getString(C0017R$string.oneplus_draw_gesture_start_none);
        if (!str.startsWith("oem_acc_blackscreen_gesture")) {
            str = getGestureTypebyGestureKey(str);
        }
        String string = Settings.System.getString(context.getContentResolver(), str);
        return !TextUtils.isEmpty(string) && string.contains("OpenShortcut:");
    }

    public static String getAppNameByPackageName(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(str);
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        if (queryIntentActivities.size() > 0) {
            return (String) queryIntentActivities.get(0).loadLabel(packageManager);
        }
        return context.getString(C0017R$string.oneplus_draw_gesture_start_none);
    }

    public static int getIndexByGestureValueKey(String str) {
        if (str.equals("oem_acc_blackscreen_gesture_o")) {
            return 6;
        }
        if (str.equals("oem_acc_blackscreen_gesture_v")) {
            return 0;
        }
        if (str.equals("oem_acc_blackscreen_gesture_s")) {
            return 8;
        }
        if (str.equals("oem_acc_blackscreen_gesture_m")) {
            return 9;
        }
        if (str.equals("oem_acc_blackscreen_gesture_w")) {
            return 10;
        }
        return 0;
    }

    public static int set1(Context context, int i) {
        int i2;
        if (i != 15) {
            switch (i) {
                case 0:
                    i2 = 1;
                    break;
                case 1:
                    i2 = 2;
                    break;
                case 2:
                    i2 = 4;
                    break;
                case 3:
                    i2 = 8;
                    break;
                case 4:
                    i2 = 16;
                    break;
                case 5:
                    i2 = 32;
                    break;
                case 6:
                    i2 = 64;
                    break;
                case 7:
                    i2 = 128;
                    break;
                case 8:
                    i2 = 256;
                    break;
                case 9:
                    i2 = 512;
                    break;
                case 10:
                    i2 = 1024;
                    break;
                case 11:
                    i2 = 2048;
                    break;
                default:
                    i2 = 0;
                    break;
            }
        } else {
            i2 = 32768;
        }
        int i3 = i2 | Settings.System.getInt(context.getContentResolver(), "oem_acc_blackscreen_gestrue_enable", 0);
        Settings.System.putInt(context.getContentResolver(), "oem_acc_blackscreen_gestrue_enable", i3);
        return i3;
    }

    public static int set0(Context context, int i) {
        int i2;
        if (i != 15) {
            switch (i) {
                case 0:
                    i2 = 65534;
                    break;
                case 1:
                    i2 = 65533;
                    break;
                case 2:
                    i2 = 65531;
                    break;
                case 3:
                    i2 = 65527;
                    break;
                case 4:
                    i2 = 65519;
                    break;
                case 5:
                    i2 = 65503;
                    break;
                case 6:
                    i2 = 65471;
                    break;
                case 7:
                    i2 = 65407;
                    break;
                case 8:
                    i2 = 65279;
                    break;
                case 9:
                    i2 = 65023;
                    break;
                case 10:
                    i2 = 64511;
                    break;
                case 11:
                    i2 = 63487;
                    break;
                default:
                    i2 = 65535;
                    break;
            }
        } else {
            i2 = 32767;
        }
        int i3 = i2 & Settings.System.getInt(context.getContentResolver(), "oem_acc_blackscreen_gestrue_enable", 0);
        Settings.System.putInt(context.getContentResolver(), "oem_acc_blackscreen_gestrue_enable", i3);
        return i3;
    }

    public static List<ShortcutInfo> loadShortCuts(Context context, String str) {
        LauncherApps.ShortcutQuery shortcutQuery = new LauncherApps.ShortcutQuery();
        shortcutQuery.setQueryFlags(11);
        shortcutQuery.setPackage(str);
        return ((LauncherApps) context.getSystemService("launcherapps")).getShortcuts(shortcutQuery, Process.myUserHandle());
    }

    public static boolean hasShortCuts(Context context, String str) {
        List<ShortcutInfo> loadShortCuts = loadShortCuts(context, str);
        if (loadShortCuts != null && loadShortCuts.size() > 0) {
            return true;
        }
        return false;
    }

    public static boolean hasShortCutsId(Context context, String str, String str2) {
        List<ShortcutInfo> loadShortCuts = loadShortCuts(context, str);
        if (loadShortCuts == null) {
            return false;
        }
        for (ShortcutInfo shortcutInfo : loadShortCuts) {
            if (shortcutInfo.getId().equals(str2)) {
                return true;
            }
        }
        return false;
    }

    public static String getShortCutsNameByID(Context context, String str, String str2) {
        List<ShortcutInfo> loadShortCuts = loadShortCuts(context, str);
        String str3 = "";
        if (loadShortCuts != null) {
            for (ShortcutInfo shortcutInfo : loadShortCuts) {
                if (shortcutInfo.getId().equals(str2)) {
                    CharSequence shortLabel = shortcutInfo.getShortLabel();
                    if (TextUtils.isEmpty(shortLabel)) {
                        shortLabel = str2;
                    }
                    str3 = shortLabel.toString();
                }
            }
        }
        return str3;
    }
}
