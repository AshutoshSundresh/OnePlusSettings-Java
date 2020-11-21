package com.oneplus.settings.utils;

import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.UserInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.OpFeatures;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import androidx.constraintlayout.widget.R$styleable;
import androidx.fragment.app.ListFragment;
import androidx.preference.ListPreference;
import com.android.internal.app.AssistUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0003R$array;
import com.android.settings.C0006R$color;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.wifi.OPAutoSwitchMobileDataPreferenceController;
import com.android.settings.wifi.OPIntelligentlySelectBestWifiPreferenceController;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;
import com.google.analytics.tracking.android.MapBuilder;
import com.oneplus.common.ReflectUtil;
import com.oneplus.custom.utils.OpCustomizeSettings;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.backgroundoptimize.BgOActivityManager;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.better.OPHapticFeedback;
import com.oneplus.settings.better.OPNightMode;
import com.oneplus.settings.better.OPReadingModeTurnOnPreferenceController;
import com.oneplus.settings.better.OPScreenColorMode;
import com.oneplus.settings.edgeeffect.SpringListView;
import com.oneplus.settings.edgeeffect.SpringRelativeLayout;
import com.oneplus.settings.gestures.OPGestureUtils;
import com.oneplus.settings.navigationbargestures.OPNavigationBarGesturesSettings;
import com.oneplus.settings.system.OPRamBoostSettings;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class OPUtils {
    public static final String[] GBGSERVICEPACKAGES;
    private static final Uri LAUNCHER_FEATURE_URI = Uri.parse("content://net.oneplus.launcher.features");
    public static final String[] UNIT_OF_STORAGE = {"(?<![吉千兆太])比特", "(?<![吉千兆太])字节", "吉比特", "吉字节", "千比特", "千字节", "兆比特", "兆字节", "太比特", "太字节"};
    public static final String[] UNIT_OF_STORAGE_REPLACE = {"b", "B", "Gb", "GB", "Kb", "KB", "Mb", "MB", "Tb", "TB"};
    public static Boolean isUstModeEnabled = null;
    public static boolean mAppUpdated = false;
    public static Boolean mIsExistCloudPackage;

    public static boolean supportGestureAudioRoute() {
        return true;
    }

    static {
        String[] strArr = {"com.oneplus.card", "com.oneplus.cloud", "com.oneplus.appupgrader", "com.oneplus.dirac.simplemanager", "com.oneplus.soundrecorder", "com.oneplus.sound.tuner", "com.google.android.gms"};
        GBGSERVICEPACKAGES = strArr;
        Arrays.asList(strArr);
    }

    public static boolean isSupportUstMode() {
        Boolean bool = isUstModeEnabled;
        if (bool != null) {
            return bool.booleanValue();
        }
        if (!ReflectUtil.isFeatureSupported("OP_FEATURE_UST_MODE") || !"tmo".equals(SystemProperties.get("ro.boot.opcarrier"))) {
            isUstModeEnabled = Boolean.FALSE;
        } else {
            isUstModeEnabled = Boolean.TRUE;
        }
        return isUstModeEnabled.booleanValue();
    }

    public static String replaceFileSize(String str) {
        int i = 0;
        while (true) {
            String[] strArr = UNIT_OF_STORAGE;
            if (i >= strArr.length) {
                return str;
            }
            str = str.replaceAll(strArr[i], UNIT_OF_STORAGE_REPLACE[i]);
            i++;
        }
    }

    public static void setAppUpdated(boolean z) {
        mAppUpdated = z;
        Log.i("OPUtils", "setAppUpdated:" + mAppUpdated);
    }

    public static boolean isAppExist(Context context, String str) {
        if (!"com.oneplus.cloud".equals(str)) {
            return getApplicationInfo(context, str) != null;
        }
        Boolean bool = mIsExistCloudPackage;
        if (bool != null) {
            return bool.booleanValue();
        }
        if (getApplicationInfo(context, str) != null) {
            mIsExistCloudPackage = Boolean.TRUE;
        } else {
            mIsExistCloudPackage = Boolean.FALSE;
        }
        return mIsExistCloudPackage.booleanValue();
    }

    public static ResolveInfo getResolveInfoByPackageName(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(str);
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        if (queryIntentActivities == null || queryIntentActivities.size() <= 0) {
            return null;
        }
        return queryIntentActivities.get(0);
    }

    private static ApplicationInfo getApplicationInfo(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ApplicationInfo getApplicationInfoByUserId(Context context, String str, int i) {
        try {
            return context.getPackageManager().getApplicationInfoAsUser(str, 0, UserHandle.getUserId(i));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isNumeric(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    public static boolean isAppPakExist(Context context, String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            applicationInfo = null;
        }
        if (applicationInfo != null) {
            return true;
        }
        return false;
    }

    public static boolean isActionExist(Context context, Intent intent, String str) {
        Intent intent2;
        PackageManager packageManager = context.getPackageManager();
        if (intent == null) {
            intent2 = new Intent();
        } else {
            intent2 = (Intent) intent.clone();
        }
        intent2.setAction(str);
        return packageManager.queryIntentActivities(intent2, 65536).size() > 0;
    }

    public static boolean isGuestMode() {
        return UserHandle.myUserId() != 0;
    }

    public static void sendAppTrackerForQuickReplyIMStatus() {
        String[] strArr = {"com.tencent.mm", "com.whatsapp", "com.instagram.android", "com.tencent.mobileqq"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String str = strArr[i];
            if (isAppExist(SettingsBaseApplication.mApplication, str)) {
                sb.append(str + ":" + (isQuickReplyAppSelected(str) ? 1 : 0) + ",");
            }
        }
        if (!TextUtils.isEmpty(sb)) {
            sendAppTracker("lqr_im_states", sb.toString());
        }
    }

    public static void sendAppTrackerForQuickReplyKeyboardStatus() {
        sendAppTracker("lqr_fk_switch", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "op_quickreply_ime_adjust", 0));
    }

    public static void sendAppTrackerForFodAnimStyle() {
        sendAppTracker("fod_style", Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "op_custom_unlock_animation_style", 0, -2));
    }

    public static void sendAppTrackerForHorizonLightAnimStyle() {
        sendAppTracker("horizon_light_style", Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "op_custom_horizon_light_animation_style", 0, -2));
    }

    public static void sendAppTrackerForTrueColor() {
        sendAnalytics("true_tone", "status", String.valueOf(Settings.Secure.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "display_white_balance_enabled", 0)));
    }

    public static void sendAppTrackerForClockStyle() {
        int intForUser = Settings.Secure.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "aod_clock_style", 1, -2);
        sendAnalytics("AOD", "Clock", intForUser > 0 ? String.valueOf(intForUser) : "1");
    }

    public static void sendAppTrackerForGestureAndButton() {
        if (isAllowSendAppTracker(SettingsBaseApplication.mApplication.getApplicationContext())) {
            int i = Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "op_navigation_bar_type", 1);
            if (i == 3) {
                sendAppTracker("swap_button", 0);
            } else {
                sendAppTracker("swap_button", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "oem_acc_key_define", 0));
            }
            if (i == 1 && Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "op_gesture_button_enabled", 0) == 1) {
                i = 4;
            }
            sendAppTracker("nav&gestures_settings", i);
        }
    }

    public static void sendAppTrackerForQuickLaunchToggle() {
        sendAnalytics("quick_launch_settings", "quick_launch", String.valueOf(Settings.Secure.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "op_quickpay_enable", 0)));
    }

    public static void sendAppTrackerForQuickLaunch() {
        sendAnalytics("quick_launch_settings", "quick_launch_shortcuts", getAllQuickLaunchStrings(SettingsBaseApplication.mApplication.getApplicationContext()));
    }

    public static void sendAppTrackerForAssistantAPP() {
        boolean z = false;
        if (Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "quick_turn_on_voice_assistant", 0) == 1) {
            z = true;
        }
        sendAppTracker("quick_turn_on_voice_assistant", z ? "on" : "off");
    }

    public static void sendAppTrackerForAutoBrightness() {
        sendAppTracker("adaptive_brightness_click_auto_open", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "screen_brightness_mode", 0));
    }

    public static void sendAppTrackerForBrightness() {
        int intForUser = Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "screen_brightness", 0, -2);
        sendAppTracker("adaptive_brightness_manual_slider", intForUser < 40 ? 1 : intForUser < 140 ? 2 : intForUser < 220 ? 3 : intForUser < 420 ? 4 : 5);
    }

    public static void sendAppTrackerForAutoNightMode() {
        ColorDisplayManager colorDisplayManager = (ColorDisplayManager) SettingsBaseApplication.mApplication.getSystemService(ColorDisplayManager.class);
        if (colorDisplayManager != null) {
            sendAppTracker("night_mode_auto_open", colorDisplayManager.getNightDisplayAutoMode());
        }
    }

    public static void sendAppTrackerForNightMode() {
        ColorDisplayManager colorDisplayManager = (ColorDisplayManager) SettingsBaseApplication.mApplication.getSystemService(ColorDisplayManager.class);
        if (colorDisplayManager != null) {
            sendAppTracker("night_mode_manual_open", colorDisplayManager.isNightDisplayActivated() ? 1 : 0);
        }
    }

    public static void sendAppTrackerForEffectStrength() {
        int intForUser = Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "oem_nightmode_progress_status", R$styleable.Constraint_layout_goneMarginTop, -2);
        sendAppTracker("night_mode_effect_strength", intForUser < 44 ? 1 : intForUser < 88 ? 2 : 3);
    }

    public static void sendAppTrackerForReadingModeApps(String str) {
        sendAppTracker("read_mode_apps", str);
    }

    public static void sendAppTrackerForReadingModeNotification() {
        sendAppTracker("read_mode_block_peek_noti", Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "reading_mode_block_notification", 0, -2));
    }

    public static void sendAppTrackerForReadingMode() {
        String stringForUser = Settings.System.getStringForUser(SettingsBaseApplication.mApplication.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS_MANUAL, -2);
        int i = 0;
        if ("force-on".equals(stringForUser)) {
            i = 1;
        } else {
            "force-off".equals(stringForUser);
        }
        sendAppTracker("read_mode_manual_open", i);
    }

    public static void sendAppTrackerForScreenColorMode() {
        sendAppTracker("screen_calibration_screen_calibration", Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "screen_color_mode_settings_value", 1, -2));
    }

    public static void sendAppTrackerForScreenCustomColorMode() {
        double d = (double) Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "oem_screen_better_value", 0);
        double d2 = (double) (isSupportReadingModeInterpolater() ? 56 : 100);
        sendAppTracker("screen_calibration_custom_color", d < 0.33d * d2 ? 1 : d < d2 * 0.66d ? 2 : 3);
    }

    public static void sendAppTrackerForThemes() {
        sendAppTracker("theme_theme", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "oem_black_mode", 0));
    }

    public static void sendAppTrackerForAccentColor() {
        sendAppTracker("theme_accent_color_white", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "oem_white_mode_accent_color_index", 0));
        sendAppTracker("theme_accent_color_black", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "oem_black_mode_accent_color_index", 0));
    }

    public static void sendAppTrackerForDefaultHomeAppByComponentName(String str) {
        sendAppTracker("default_app_home_app", str);
        Log.d("OPUtils", "sendAppTrackerForDefaultHomeAppByPackageName componentName is:" + str);
    }

    public static void sendAppTrackerForAssistApp() {
        try {
            ComponentName assistComponentForUser = new AssistUtils(SettingsBaseApplication.mApplication).getAssistComponentForUser(UserHandle.myUserId());
            Log.d("OPUtils", "sendAppTrackerForAssistApp componentNamePkg is:" + assistComponentForUser);
            if (assistComponentForUser != null) {
                sendAppTracker("default_app_assist&voice input", assistComponentForUser.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("OPUtils", "sendAppTrackerForAssistApp componentNamePkg is not exist");
        }
    }

    public static void sendAppTrackerForDefaultHomeApp() {
        try {
            ComponentName homeActivities = SettingsBaseApplication.mApplication.getPackageManager().getHomeActivities(new ArrayList());
            if (homeActivities != null) {
                sendAppTracker("default_app_home_app", homeActivities.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("OPUtils", "sendAppTrackerForDefaultJHomeApp componentNamePkg is not exist");
        }
    }

    public static void sendAppTrackerForGameModeSpeakerAnswer() {
        sendAppTracker("game_mode_speaker_answer", Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "game_mode_answer_no_incallui", 0, -2));
    }

    public static void sendAppTrackerForGameModeNotificationShow() {
        sendAppTracker("game_mode_notification_show", Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "game_mode_block_notification", 0, -2));
    }

    public static void sendAppTrackerForGameMode3drPartyCalls() {
        sendAppTracker("game_mode_3rd_party_calls", Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "game_mode_notifications_3rd_calls", 0, -2));
    }

    public static void sendAppTrackerForGameModeAdEnable() {
        sendAppTracker("game_mode_ad_enable", Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "op_game_mode_ad_enable", 0, -2));
    }

    public static void sendAppTrackerForGameModeBrightness() {
        sendAppTracker("game_mode_disable_auto_brightness", Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "game_mode_close_automatic_brightness", 0, -2));
    }

    public static void sendAppTrackerForGameModeNetWorkBoost() {
        sendAppTracker("game_mode_network_boost", Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "game_mode_network_acceleration", 0, -2));
    }

    public static void sendAppTrackerForGameModeApps(String str) {
        sendAppTracker("game_mode_apps", str);
    }

    public static void sendAppTrackerForSmartWifiSwitch() {
        sendAppTracker("wifi_smart_choice", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), OPIntelligentlySelectBestWifiPreferenceController.WIFI_SHOULD_SWITCH_NETWORK, 0));
    }

    public static void sendAppTrackerForDataAutoSwitch() {
        sendAppTracker("data_auto_switch", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), OPAutoSwitchMobileDataPreferenceController.WIFI_AUTO_CHANGE_TO_MOBILE_DATA, 0));
    }

    public static void sendDisplaySettingsAnalytics() {
        Application application = SettingsBaseApplication.mApplication;
        sendResolutionAnalytics(application);
        sendRefreshRateAnalytics(application);
        sendNightModeAnalytics(application);
        sendScreenColorModeAnalytics(application);
    }

    private static void sendResolutionAnalytics(Context context) {
        if (isSupportScreenRefreshRate() && !isGuestMode()) {
            if (Log.isLoggable("OPUtils", 3)) {
                Log.d("OPUtils", "sendResolutionAnalytics");
            }
            int i = Settings.Global.getInt(context.getContentResolver(), "oneplus_screen_resolution_adjust", 2);
            if (i == 2) {
                sendAnalytics("resolution", "status", "1");
            } else if (i == 0) {
                sendAnalytics("resolution", "status", "2");
            } else if (i == 1) {
                sendAnalytics("resolution", "status", OPMemberController.CLIENT_TYPE);
            }
        }
    }

    private static void sendRefreshRateAnalytics(Context context) {
        if (isSupportScreenRefreshRate()) {
            if (Log.isLoggable("OPUtils", 3)) {
                Log.d("OPUtils", "sendRefreshRateAnalytics");
            }
            int i = Settings.Global.getInt(context.getContentResolver(), "oneplus_screen_refresh_rate", 2);
            if (i == 2) {
                sendAnalytics("refresh rate", "status", "0");
            } else if (i == 1) {
                sendAnalytics("refresh rate", "status", "1");
            }
        }
    }

    private static void sendNightModeAnalytics(Context context) {
        if (Log.isLoggable("OPUtils", 3)) {
            Log.d("OPUtils", "sendNightModeAnalytics");
        }
        sendAnalytics("night_mode", "auto_open", String.valueOf(OPNightMode.convertAutoMode(((ColorDisplayManager) context.getSystemService(ColorDisplayManager.class)).getNightDisplayAutoMode())));
        int intForUser = Settings.System.getIntForUser(context.getContentResolver(), "oem_nightmode_progress_status", OPScreenColorMode.DEFAULT_COLOR_PROGRESS, -2);
        isSupportReadingModeInterpolater();
        double d = (double) intForUser;
        double d2 = (double) 100;
        double d3 = 0.33d * d2;
        if (d <= d3) {
            sendAnalytics("night_mode", "screen_color", "1");
        } else if (d <= d2 * 0.66d) {
            sendAnalytics("night_mode", "screen_color", "2");
        } else if (intForUser <= 100) {
            sendAnalytics("night_mode", "screen_color", OPMemberController.CLIENT_TYPE);
        }
        float floatForUser = Settings.System.getFloatForUser(context.getContentResolver(), "oem_nightmode_brightness_progress", 0.0f, -2);
        double d4 = (double) floatForUser;
        if (d4 <= d3) {
            sendAnalytics("night_mode", "brightness", "1");
        } else if (d4 <= d2 * 0.66d) {
            sendAnalytics("night_mode", "brightness", "2");
        } else if (floatForUser <= ((float) 100)) {
            sendAnalytics("night_mode", "brightness", OPMemberController.CLIENT_TYPE);
        }
    }

    private static void sendScreenColorModeAnalytics(Context context) {
        if (Log.isLoggable("OPUtils", 3)) {
            Log.d("OPUtils", "sendScreenColorModeAnalytics");
        }
        int intForUser = Settings.System.getIntForUser(context.getContentResolver(), "screen_color_mode_settings_value", 1, -2);
        if (intForUser == 1) {
            sendAnalytics("screen_calibration", "status", "1");
        } else if (intForUser == 10) {
            sendAnalytics("screen_calibration", "status", "2");
        } else if (intForUser == 3) {
            sendAnalytics("screen_calibration", "status", OPMemberController.CLIENT_TYPE);
        }
        if (intForUser == 3) {
            int intForUser2 = Settings.System.getIntForUser(context.getContentResolver(), "screen_color_mode_advanced_settings_value", 0, -2);
            if (intForUser2 == 0) {
                sendAnalytics("screen_calibration", "advanced", "1");
            } else if (intForUser2 == 1) {
                sendAnalytics("screen_calibration", "advanced", "2");
            } else if (intForUser2 == 2) {
                sendAnalytics("screen_calibration", "advanced", OPMemberController.CLIENT_TYPE);
            }
        }
        int i = Settings.System.getInt(context.getContentResolver(), "oem_screen_better_value", OPScreenColorMode.DEFAULT_COLOR_PROGRESS);
        int i2 = 100;
        if (isSupportReadingModeInterpolater()) {
            i2 = 56;
        }
        double d = (double) i;
        double d2 = (double) i2;
        if (d <= 0.33d * d2) {
            sendAnalytics("screen_calibration", "custom", "1");
        } else if (d <= d2 * 0.66d) {
            sendAnalytics("screen_calibration", "custom", "2");
        } else if (i <= i2) {
            sendAnalytics("screen_calibration", "custom", OPMemberController.CLIENT_TYPE);
        }
    }

    /* access modifiers changed from: private */
    public static void sendFODAnimAnalytics(Context context) {
        int intForUser = Settings.System.getIntForUser(context.getContentResolver(), "op_custom_unlock_animation_style", 0, -2);
        if (intForUser == 0) {
            sendAnalytics("fod_effect", "status", "1");
        } else if (intForUser == 1) {
            sendAnalytics("fod_effect", "status", "2");
        } else if (intForUser == 2) {
            sendAnalytics("fod_effect", "status", OPMemberController.CLIENT_TYPE);
        } else if (intForUser == 9) {
            sendAnalytics("fod_effect", "status", "4");
        } else if (intForUser == 4) {
            sendAnalytics("fod_effect", "status", "5");
        }
    }

    /* access modifiers changed from: private */
    public static void sendNaviGestureAnalytics(Context context) {
        if (OPNavigationBarGesturesSettings.is2ButtonEnabled(context)) {
            sendAnalytics("nav_gesture", "status", "2");
        } else if (OPNavigationBarGesturesSettings.is3ButtonEnabled(context)) {
            sendAnalytics("nav_gesture", "status", "1");
        } else if (OPNavigationBarGesturesSettings.isGesturalEnabled(context)) {
            sendAnalytics("nav_gesture", "status", OPMemberController.CLIENT_TYPE);
        }
    }

    public static void sendAppTrackerForAllSettings() {
        SettingsBaseApplication.getHandler().postDelayed(new Runnable() {
            /* class com.oneplus.settings.utils.OPUtils.AnonymousClass1 */

            public void run() {
                Log.i("OPUtils", "sendAppTrackerForAllSettings for device boot.");
                OPUtils.sendAppTrackerForTrueColor();
                OPUtils.sendAppTracker("auto_face_unlock", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "oneplus_auto_face_unlock_enable", 0));
                OPUtils.sendAppTracker("op_three_key_screenshots_enabled", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "oem_acc_sensor_three_finger", 0));
                OPUtils.sendAppTrackerForGestureAndButton();
                OPUtils.sendAppTrackerForAssistantAPP();
                OPUtils.sendAppTracker("notch_display", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "op_camera_notch_ignore", 0));
                OPUtils.sendAppTrackerForQuickLaunchToggle();
                OPUtils.sendAppTrackerForQuickLaunch();
                OPUtils.sendAppTrackerForFodAnimStyle();
                OPUtils.sendAppTrackerForAutoBrightness();
                OPUtils.sendAppTrackerForBrightness();
                OPUtils.sendAppTrackerForAutoNightMode();
                OPUtils.sendAppTrackerForNightMode();
                OPUtils.sendAppTrackerForEffectStrength();
                OPUtils.sendAppTrackerForReadingModeApps(Settings.System.getString(SettingsBaseApplication.mApplication.getContentResolver(), "read_mode_apps"));
                OPUtils.sendAppTrackerForReadingModeNotification();
                OPUtils.sendAppTrackerForReadingMode();
                OPUtils.sendAppTrackerForScreenColorMode();
                OPUtils.sendAppTrackerForScreenCustomColorMode();
                OPUtils.sendAppTrackerForThemes();
                OPUtils.sendAppTrackerForAccentColor();
                OPUtils.sendAppTrackerForAssistApp();
                OPUtils.sendAppTrackerForDefaultHomeApp();
                OPUtils.sendAppTrackerForGameModeSpeakerAnswer();
                OPUtils.sendAppTrackerForGameModeNotificationShow();
                OPUtils.sendAppTrackerForGameMode3drPartyCalls();
                OPUtils.sendAppTrackerForGameModeAdEnable();
                OPUtils.sendAppTrackerForGameModeBrightness();
                OPUtils.sendAppTrackerForGameModeNetWorkBoost();
                OPUtils.sendAppTrackerForGameModeApps(Settings.System.getString(SettingsBaseApplication.mApplication.getContentResolver(), "game_mode_apps"));
                OPUtils.sendAppTrackerForGameModeRemovedApps();
                OPUtils.sendAppTrackerForSmartWifiSwitch();
                OPUtils.sendAppTrackerForDataAutoSwitch();
                OPUtils.sendAppTrackerForQuickReply();
                OPUtils.sendAppTrackerForQuickReplyIMStatus();
                OPUtils.sendAppTrackerForQuickReplyKeyboardStatus();
                OPHapticFeedback.sendDefaultAppTracker();
                OPRamBoostSettings.sendDefaultAppTracker();
                OPUtils.sendAppTracker("pop_up_face_unlock", Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "oneplus_face_unlock_powerkey_recognize_enable", 0));
                OPUtils.sendAppTrackerForHorizonLightAnimStyle();
                OPUtils.sendAppTrackerForClockStyle();
                OPUtils.sendDisplaySettingsAnalytics();
                OPUtils.sendFODAnimAnalytics(SettingsBaseApplication.mApplication);
                OPUtils.sendNaviGestureAnalytics(SettingsBaseApplication.mApplication);
                OPRamBoostSettings.sendRamboostAppTracker();
            }
        }, 10000);
    }

    @Deprecated
    public static void sendAppTracker(String str, String str2) {
        if (isAllowSendAppTracker(SettingsBaseApplication.mApplication.getApplicationContext())) {
            sendAnalytics(str, str, str2);
            sendGoogleTracker("OPSettings", str, str2);
        }
    }

    @Deprecated
    public static void sendAppTracker(String str, boolean z) {
        sendAppTracker(str, Boolean.toString(z));
    }

    @Deprecated
    public static void sendAppTracker(String str, int i) {
        sendAppTracker(str, Integer.toString(i));
    }

    public static boolean isO2() {
        return OpFeatures.isSupport(new int[]{1});
    }

    public static boolean isH2() {
        return OpFeatures.isSupport(new int[]{0});
    }

    public static boolean isIndia() {
        return OpCustomizeSettings.getSwType() == OpCustomizeSettings.SW_TYPE.IN;
    }

    public static boolean isOP3() {
        return "15801".equals(SystemProperties.get("ro.boot.project_name"));
    }

    public static boolean isOP3T() {
        return "15811".equals(SystemProperties.get("ro.boot.project_name"));
    }

    public static boolean isSurportGesture20(Context context) {
        return context.getPackageManager().hasSystemFeature("oem.blackScreenGesture_2.support");
    }

    public static boolean isSurportNoNeedPowerOnPassword(Context context) {
        return context.getPackageManager().hasSystemFeature("oem.no_need_power_on_password.support");
    }

    public static boolean isSurportProductInfo16859(Context context) {
        return context.getPackageManager().hasSystemFeature("oem.product_info_cheeseburger.support");
    }

    public static boolean isSurportProductInfo17801(Context context) {
        return context.getPackageManager().hasSystemFeature("oem.product_info_dumpling.support");
    }

    public static boolean isSurportProductInfo(Context context) {
        return isSurportProductInfo16859(context) || isSurportProductInfo17801(context);
    }

    public static boolean isSurportSimNfc(Context context) {
        return context.getPackageManager().hasSystemFeature("oem.sim_nfc.support");
    }

    public static boolean isSurportBackFingerprint(Context context) {
        return context.getResources().getBoolean(17891529);
    }

    public static boolean isSurportNavigationBarOnly(Context context) {
        return context.getResources().getBoolean(17891529);
    }

    public static int getFingerprintScaleAnimStep(Context context) {
        return (!isSurportBackFingerprint(context) || isFingerprintNeedEnrollTime20(context)) ? 10 : 8;
    }

    public static void disableAospFaceUnlock(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.setApplicationEnabledSetting("com.android.facelock", 2, 1);
            packageManager.setApplicationHiddenSettingAsUser("com.android.facelock", true, UserHandle.OWNER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreBackupEntranceInLauncher(final Context context) {
        new Thread(new Runnable() {
            /* class com.oneplus.settings.utils.OPUtils.AnonymousClass2 */

            public void run() {
                ContentResolver contentResolver = context.getContentResolver();
                if (Settings.System.getInt(contentResolver, "oneplus_backuprestore_disabled", 0) == 1) {
                    try {
                        Log.d("OPUtils", "restore entry");
                        context.getPackageManager().setComponentEnabledSetting(new ComponentName("com.oneplus.backuprestore", "com.oneplus.backuprestore.activity.BootActivity"), 1, 1);
                        Settings.System.putInt(contentResolver, "oneplus_backuprestore_disabled", 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void enableAppBgService(final Context context) {
        new Thread(new Runnable() {
            /* class com.oneplus.settings.utils.OPUtils.AnonymousClass3 */

            public void run() {
                BatteryUtils instance = BatteryUtils.getInstance(context);
                BgOActivityManager.getInstance(context);
                String[] strArr = OPUtils.GBGSERVICEPACKAGES;
                for (String str : strArr) {
                    if (OPUtils.isAppExist(context, str) && BgOActivityManager.getInstance(context).getAppControlMode(str, 0) == 0) {
                        instance.setForceAppStandby(instance.getPackageUid(str), str, 0);
                        Log.d("OPUtils", "enableAppBgService pkg:" + str);
                    }
                }
            }
        }).start();
    }

    public static void disableWirelessAdbDebuging() {
        SystemProperties.set("service.adb.tcp.port", "-1");
    }

    public static boolean isApplicationEnabled(Context context, String str) {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager.getApplicationEnabledSetting(str) == 2 || packageManager.getApplicationEnabledSetting(str) == 3) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void disableCloundServiceApp(final Context context) {
        new Thread(new Runnable() {
            /* class com.oneplus.settings.utils.OPUtils.AnonymousClass4 */

            public void run() {
                context.getContentResolver();
                if (OPUtils.isAppPakExist(context, "com.heytap.cloud")) {
                    try {
                        Log.d("OPUtils", "disableCloundServiceApp");
                        context.getPackageManager().setApplicationEnabledSetting("com.oneplus.cloud", 2, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void enablePackageInstaller(final Context context) {
        new Thread(new Runnable() {
            /* class com.oneplus.settings.utils.OPUtils.AnonymousClass6 */

            public void run() {
                UserInfo userInfo = ((UserManager) context.getSystemService("user")).getUserInfo(UserHandle.myUserId());
                if (userInfo != null && userInfo.id == 999) {
                    try {
                        PackageManager packageManager = context.getPackageManager();
                        if (OPUtils.isO2()) {
                            ComponentName componentName = new ComponentName("com.google.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity");
                            if (packageManager.getComponentEnabledSetting(componentName) != 1) {
                                packageManager.setComponentEnabledSetting(componentName, 1, 1);
                                return;
                            }
                            return;
                        }
                        ComponentName componentName2 = new ComponentName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity");
                        if (packageManager.getComponentEnabledSetting(componentName2) != 1) {
                            packageManager.setComponentEnabledSetting(componentName2, 1, 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static boolean isFaceUnlockEnabled(Context context) {
        try {
            int applicationEnabledSetting = context.getPackageManager().getApplicationEnabledSetting("com.android.facelock");
            if (applicationEnabledSetting == 1 || applicationEnabledSetting == 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String convertToRGB(int i) {
        String hexString = Integer.toHexString(Color.red(i));
        String hexString2 = Integer.toHexString(Color.green(i));
        String hexString3 = Integer.toHexString(Color.blue(i));
        if (hexString.length() == 1) {
            hexString = "0" + hexString;
        }
        if (hexString2.length() == 1) {
            hexString2 = "0" + hexString2;
        }
        if (hexString3.length() == 1) {
            hexString3 = "0" + hexString3;
        }
        return hexString + hexString2 + hexString3;
    }

    public static int convertToColorInt(String str) throws IllegalArgumentException {
        if (!str.startsWith("#")) {
            str = "#" + str;
        }
        return parseColor(str);
    }

    public static String getTextAccentColor(String str) {
        Log.d("OPUtils", "getTextAccentColor:" + str);
        float[] fArr = new float[3];
        Color.colorToHSV(convertToColorInt(str), fArr);
        float f = fArr[0];
        float min = Math.min(fArr[1], 0.4f);
        float max = Math.max(fArr[2], 0.7f);
        return "#" + convertToRGB(Color.HSVToColor(new float[]{f, min, max}));
    }

    public static int getAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843829, typedValue, true);
        return context.getColor(typedValue.resourceId);
    }

    public static String showROMStorage(Context context) {
        return Formatter.formatFileSize(context, PrivateStorageInfo.getPrivateStorageInfo(new StorageManagerVolumeProvider((StorageManager) context.getSystemService(StorageManager.class))).totalBytes).replace(" ", "").replace(".00", "");
    }

    private static String formatMemoryDisplay(long j) {
        long j2 = (j * 1024) / 1000000;
        int i = (int) (j2 / 512);
        int i2 = (int) (j2 % 512);
        if (i == 0) {
            return j2 + "MB";
        } else if (i2 > 256) {
            int i3 = i + 1;
            if (i3 % 2 == 0) {
                return ((int) (((float) i3) * 0.5f)) + "GB";
            }
            return (((float) i3) * 0.5f) + "GB";
        } else {
            return ((((float) i) * 0.5f) + 0.25f) + "GB";
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x0067 A[SYNTHETIC, Splitter:B:37:0x0067] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0081 A[SYNTHETIC, Splitter:B:48:0x0081] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x008b A[SYNTHETIC, Splitter:B:53:0x008b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getTotalMemory() {
        /*
        // Method dump skipped, instructions count: 148
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.utils.OPUtils.getTotalMemory():java.lang.String");
    }

    public static boolean isFingerprintNeedEnrollTime20(Context context) {
        String str = SystemProperties.get("persist.vendor.oem.fp.version", "5");
        Log.d("OPUtils", "fpVersion = " + str);
        return "4".equals(str);
    }

    public static boolean isFingerprintNeedEnrollTime16(Context context) {
        return isSurportBackFingerprint(context) && !"ONEPLUS A5000".equalsIgnoreCase(Build.MODEL) && !"ONEPLUS A5010".equalsIgnoreCase(Build.MODEL);
    }

    public static int getThemeMode(ContentResolver contentResolver) {
        return getThemeMode();
    }

    private static int getThemeMode() {
        return Integer.parseInt(SystemProperties.get("persist.sys.theme.status", "0"));
    }

    public static boolean isWhiteModeOn(ContentResolver contentResolver) {
        return getThemeMode() == 0;
    }

    public static boolean isBlackModeOn(ContentResolver contentResolver) {
        return getThemeMode() == 1;
    }

    public static boolean isAndroidModeOn(ContentResolver contentResolver) {
        return getThemeMode() == 2;
    }

    public static boolean isThemeOn(ContentResolver contentResolver) {
        return Settings.System.getIntForUser(contentResolver, "oem_special_theme", 0, 0) == 1;
    }

    public static ColorStateList createColorStateList(int i, int i2, int i3, int i4) {
        return new ColorStateList(new int[][]{new int[]{16842919}, new int[]{16842913}, new int[]{16842910}, new int[0]}, new int[]{i, i2, i3, i4});
    }

    public static ColorStateList creatOneplusPrimaryColorStateList(Context context) {
        int accentColor = getAccentColor(context);
        int color = context.getResources().getColor(C0006R$color.oneplus_font_list_setting_title);
        int color2 = context.getResources().getColor(C0006R$color.oneplus_font_list_subtitle);
        return createColorStateList(color2, color2, accentColor, color);
    }

    public static int dp2Px(DisplayMetrics displayMetrics, float f) {
        return (int) TypedValue.applyDimension(1, f, displayMetrics);
    }

    public static int dip2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int sp2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static boolean isLTRLayout(Context context) {
        return context.getResources().getConfiguration().getLayoutDirection() == 0;
    }

    public static boolean isInQuickLaunchList(Context context, OPAppModel oPAppModel) {
        if (oPAppModel == null) {
            return false;
        }
        String allQuickLaunchStrings = getAllQuickLaunchStrings(context);
        if ((oPAppModel.getType() != 0 || !allQuickLaunchStrings.contains(getQuickLaunchAppString(oPAppModel))) && ((oPAppModel.getType() != 1 || !allQuickLaunchStrings.contains(getQuickLaunchShortcutsString(oPAppModel))) && ((oPAppModel.getType() != 2 || !allQuickLaunchStrings.contains(getQuickPayAppString(oPAppModel))) && (oPAppModel.getType() != 3 || !allQuickLaunchStrings.contains(getQuickMiniProgrameString(oPAppModel)))))) {
            return false;
        }
        return true;
    }

    public static boolean isQuickReplyAppSelected(OPAppModel oPAppModel) {
        String quickReplyAppListString = getQuickReplyAppListString(SettingsBaseApplication.mApplication);
        if (!TextUtils.isEmpty(quickReplyAppListString)) {
            return new HashSet(Arrays.asList(quickReplyAppListString.split(";"))).contains(oPAppModel.getPkgName());
        }
        return false;
    }

    public static boolean isQuickReplyAppSelected(String str) {
        String quickReplyAppListString = getQuickReplyAppListString(SettingsBaseApplication.mApplication);
        if (!TextUtils.isEmpty(quickReplyAppListString)) {
            return new HashSet(Arrays.asList(quickReplyAppListString.split(";"))).contains(str);
        }
        return false;
    }

    public static String getQuickReplyAppString(OPAppModel oPAppModel) {
        if (oPAppModel == null) {
            return "";
        }
        return oPAppModel.getPkgName() + ";";
    }

    public static String getQuickReplyAppListString(Context context) {
        String string = Settings.System.getString(context.getContentResolver(), "op_quickreply_im_list");
        return TextUtils.isEmpty(string) ? "" : string;
    }

    public static void saveQuickReplyAppLisStrings(Context context, String str) {
        Settings.System.putString(context.getContentResolver(), "op_quickreply_im_list", removeRepeatedStrings(str));
        sendAppTrackerForQuickReply();
    }

    public static String removeRepeatedStrings(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        HashSet hashSet = new HashSet(Arrays.asList(str.split(";")));
        StringBuilder sb = new StringBuilder();
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            sb.append(((String) it.next()) + ";");
        }
        if (TextUtils.isEmpty(sb)) {
            return "";
        }
        return sb.toString();
    }

    public static void sendAppTrackerForQuickReply() {
        sendAppTracker("im_quick_reply", getQuickReplyAppListString(SettingsBaseApplication.mApplication.getApplicationContext()));
    }

    public static String getGameModeAppString(OPAppModel oPAppModel) {
        if (oPAppModel == null) {
            return "";
        }
        return oPAppModel.getPkgName() + ";";
    }

    public static String getGameModeAppListString(Context context) {
        String string = Settings.Global.getString(context.getContentResolver(), "op_gamemode_removed_packages_by_user");
        return TextUtils.isEmpty(string) ? "" : string;
    }

    public static boolean isInRemovedGameAppListString(Context context, OPAppModel oPAppModel) {
        String string = Settings.Global.getString(context.getContentResolver(), "op_gamemode_removed_packages_by_user");
        return !TextUtils.isEmpty(string) && string.contains(oPAppModel.getPkgName());
    }

    public static void saveGameModeRemovedAppLisStrings(Context context, String str) {
        Settings.Global.putString(context.getContentResolver(), "op_gamemode_removed_packages_by_user", str);
    }

    public static void sendAppTrackerForGameModeRemovedApps() {
        sendAppTracker("op_gamemode_removed_packages_by_user", Settings.Global.getString(SettingsBaseApplication.mApplication.getContentResolver(), "op_gamemode_removed_packages_by_user"));
    }

    public static boolean isWeChatMiniProgrameModel(OPAppModel oPAppModel) {
        if (oPAppModel != null && 3 == oPAppModel.getType() && String.valueOf(0).equals(oPAppModel.getShortCutId())) {
            return true;
        }
        return false;
    }

    public static boolean isQuickPayModel(OPAppModel oPAppModel) {
        if (oPAppModel == null || 2 != oPAppModel.getType()) {
            return false;
        }
        if ("0".equals(oPAppModel.getShortCutId()) || "1".equals(oPAppModel.getShortCutId()) || "2".equals(oPAppModel.getShortCutId()) || OPMemberController.CLIENT_TYPE.equals(oPAppModel.getShortCutId()) || "4".equals(oPAppModel.getShortCutId())) {
            return true;
        }
        return false;
    }

    public static String getQuickMiniProgrameString(OPAppModel oPAppModel) {
        if (oPAppModel == null) {
            return "";
        }
        return "OpenWxMiniProgram:" + oPAppModel.getPkgName() + ";" + oPAppModel.getShortCutId() + ",";
    }

    public static String getQuickPayAppString(OPAppModel oPAppModel) {
        if (oPAppModel == null) {
            return "";
        }
        return "OpenQuickPay:" + oPAppModel.getPkgName() + ";" + oPAppModel.getShortCutId() + ",";
    }

    public static String getQuickLaunchAppString(OPAppModel oPAppModel) {
        if (oPAppModel == null) {
            return "";
        }
        return "OpenApp:" + oPAppModel.getPkgName() + ";" + oPAppModel.getUid() + ",";
    }

    public static String getQuickLaunchShortcutsString(OPAppModel oPAppModel) {
        if (oPAppModel == null) {
            return "";
        }
        return "OpenShortcut:" + oPAppModel.getPkgName() + ";" + oPAppModel.getShortCutId() + ";" + oPAppModel.getUid() + ",";
    }

    public static int getQuickLaunchShortcutsAccount(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "op_quick_launch_apps");
        if (!TextUtils.isEmpty(string)) {
            return string.split(",").length;
        }
        return 0;
    }

    public static String getAllQuickLaunchStrings(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "op_quick_launch_apps");
        return TextUtils.isEmpty(string) ? "" : string;
    }

    public static Drawable getQuickMiniProgrameconByType(Context context, int i) {
        return context.getDrawable(C0008R$drawable.op_wechat_ridecode);
    }

    public static Drawable getQuickPayIconByType(Context context, int i) {
        int i2 = C0008R$drawable.op_wechat_qrcode;
        if (i != 0) {
            if (i == 1) {
                i2 = C0008R$drawable.op_wechat_scanning;
            } else if (i == 2) {
                i2 = C0008R$drawable.op_alipay_qrcode;
            } else if (i == 3) {
                i2 = C0008R$drawable.op_alipay_scanning;
            }
        }
        return context.getDrawable(i2);
    }

    public static OPAppModel loadShortcutByPackageNameAndShortcutId(Context context, String str, String str2, int i) {
        List<ShortcutInfo> loadShortCuts = OPGestureUtils.loadShortCuts(context, str);
        OPAppModel oPAppModel = null;
        if (loadShortCuts == null) {
            return null;
        }
        int size = loadShortCuts.size();
        LauncherApps launcherApps = (LauncherApps) context.getSystemService("launcherapps");
        int i2 = 0;
        while (true) {
            if (i2 >= size) {
                break;
            }
            ShortcutInfo shortcutInfo = loadShortCuts.get(i2);
            if (!str2.equals(shortcutInfo.getId())) {
                i2++;
            } else {
                CharSequence longLabel = shortcutInfo.getLongLabel();
                if (TextUtils.isEmpty(longLabel)) {
                    longLabel = shortcutInfo.getShortLabel();
                }
                if (TextUtils.isEmpty(longLabel)) {
                    longLabel = shortcutInfo.getId();
                }
                oPAppModel = new OPAppModel(shortcutInfo.getPackage(), longLabel.toString(), shortcutInfo.getId(), i, false);
                oPAppModel.setAppLabel(getAppLabel(context, shortcutInfo.getPackage()));
                oPAppModel.setType(1);
                oPAppModel.setSelected(isInQuickLaunchList(context, oPAppModel));
                oPAppModel.setAppIcon(getAppIcon(context, str));
                try {
                    oPAppModel.setShortCutIcon(launcherApps.getShortcutIconDrawable(shortcutInfo, 0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return oPAppModel;
    }

    public static Drawable getBadgedIcon(PackageManager packageManager, ApplicationInfo applicationInfo) {
        return packageManager.getUserBadgedIcon(packageManager.loadUnbadgedItemIcon(applicationInfo, applicationInfo), new UserHandle(UserHandle.getUserId(applicationInfo.uid)));
    }

    public static List<OPAppModel> parseAllQuickLaunchStrings(Context context) {
        int i;
        PackageManager packageManager = context.getPackageManager();
        ArrayList arrayList = new ArrayList();
        String string = Settings.Secure.getString(context.getContentResolver(), "op_quick_launch_apps");
        StringBuilder sb = new StringBuilder(getAllQuickLaunchStrings(context));
        if (!TextUtils.isEmpty(string)) {
            String[] split = string.split(",");
            int i2 = 0;
            int i3 = 0;
            while (i3 < split.length) {
                if (split[i3].startsWith("OpenApp:")) {
                    String[] split2 = split[i3].split(";");
                    String substring = split2[i2].substring(split2[i2].indexOf(":") + 1);
                    if (!isAppExist(context, substring) || getResolveInfoByPackageName(context, substring) == null) {
                        int indexOf = sb.indexOf(split[i3]);
                        sb.delete(indexOf, split[i3].length() + indexOf + 1);
                    } else {
                        String str = split2[1];
                        OPAppModel oPAppModel = new OPAppModel(substring, getAppLabel(context, substring), "", Integer.valueOf(str).intValue(), false);
                        oPAppModel.setAppIcon(getBadgedIcon(packageManager, getApplicationInfoByUserId(context, substring, Integer.valueOf(str).intValue())));
                        oPAppModel.setType(i2);
                        arrayList.add(oPAppModel);
                    }
                } else if (split[i3].startsWith("OpenShortcut:")) {
                    String[] split3 = split[i3].split(";");
                    String substring2 = split3[i2].substring(split3[i2].indexOf(":") + 1);
                    if (!isAppExist(context, substring2) || getResolveInfoByPackageName(context, substring2) == null) {
                        int indexOf2 = sb.indexOf(split[i3]);
                        sb.delete(indexOf2, split[i3].length() + indexOf2 + 1);
                    } else {
                        OPAppModel loadShortcutInfoByPackageName = loadShortcutInfoByPackageName(context, substring2, split3[1], Integer.valueOf(split3[2]).intValue());
                        if (loadShortcutInfoByPackageName == null) {
                            int indexOf3 = sb.indexOf(split[i3]);
                            sb.delete(indexOf3, split[i3].length() + indexOf3 + 1);
                        } else {
                            loadShortcutInfoByPackageName.setType(1);
                            arrayList.add(loadShortcutInfoByPackageName);
                        }
                    }
                } else {
                    if (split[i3].startsWith("OpenQuickPay:")) {
                        String[] split4 = split[i3].split(";");
                        String substring3 = split4[i2].substring(split4[i2].indexOf(":") + 1);
                        if (!isAppExist(context, substring3) || getResolveInfoByPackageName(context, substring3) == null) {
                            int indexOf4 = sb.indexOf(split[i3]);
                            sb.delete(indexOf4, split[i3].length() + indexOf4 + 1);
                        } else {
                            String str2 = split4[1];
                            int intValue = Integer.valueOf(str2).intValue();
                            OPAppModel oPAppModel2 = new OPAppModel(substring3, context.getResources().getStringArray(C0003R$array.oneplus_quicklaunch_ways_name)[intValue > 4 ? 4 : intValue], str2, 0, false);
                            if (intValue == 4) {
                                oPAppModel2.setAppIcon(getAppIcon(context, substring3));
                            } else {
                                oPAppModel2.setAppIcon(getQuickPayIconByType(context, intValue));
                            }
                            oPAppModel2.setType(2);
                            arrayList.add(oPAppModel2);
                        }
                    } else if (split[i3].startsWith("OpenWxMiniProgram:")) {
                        String[] split5 = split[i3].split(";");
                        i = 0;
                        String substring4 = split5[0].substring(split5[0].indexOf(":") + 1);
                        if (!isAppExist(context, substring4) || getResolveInfoByPackageName(context, substring4) == null) {
                            int indexOf5 = sb.indexOf(split[i3]);
                            sb.delete(indexOf5, split[i3].length() + indexOf5 + 1);
                            i3++;
                            i2 = i;
                        } else {
                            String str3 = split5[1];
                            int intValue2 = Integer.valueOf(str3).intValue();
                            OPAppModel oPAppModel3 = new OPAppModel(substring4, context.getString(C0017R$string.oneplus_quickpay_way_wecaht_ridecode), str3, 0, false);
                            oPAppModel3.setAppIcon(getQuickMiniProgrameconByType(context, intValue2));
                            oPAppModel3.setType(3);
                            arrayList.add(oPAppModel3);
                            i3++;
                            i2 = i;
                        }
                    }
                    i = 0;
                    i3++;
                    i2 = i;
                }
                i = i2;
                i3++;
                i2 = i;
            }
        }
        saveQuickLaunchStrings(context, sb.toString());
        return arrayList;
    }

    public static OPAppModel loadShortcutInfoByPackageName(Context context, String str, String str2, int i) {
        List<ShortcutInfo> loadShortCuts = OPGestureUtils.loadShortCuts(context, str);
        if (loadShortCuts == null) {
            return null;
        }
        int size = loadShortCuts.size();
        LauncherApps launcherApps = (LauncherApps) context.getSystemService("launcherapps");
        for (int i2 = 0; i2 < size; i2++) {
            ShortcutInfo shortcutInfo = loadShortCuts.get(i2);
            CharSequence longLabel = shortcutInfo.getLongLabel();
            if (TextUtils.isEmpty(longLabel)) {
                longLabel = shortcutInfo.getShortLabel();
            }
            if (TextUtils.isEmpty(longLabel)) {
                longLabel = shortcutInfo.getId();
            }
            if (str2.equals(shortcutInfo.getId())) {
                OPAppModel oPAppModel = new OPAppModel(shortcutInfo.getPackage(), longLabel.toString(), shortcutInfo.getId(), i, false);
                oPAppModel.setType(1);
                oPAppModel.setSelected(isInQuickLaunchList(context, oPAppModel));
                oPAppModel.setAppIcon(getAppIcon(context, str));
                try {
                    oPAppModel.setShortCutIcon(launcherApps.getShortcutIconDrawable(shortcutInfo, 0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return oPAppModel;
            }
        }
        return null;
    }

    public static String getAppLabel(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 128)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Drawable getAppIcon(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationIcon(packageManager.getApplicationInfo(str, 128));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveQuickLaunchStrings(Context context, String str) {
        Settings.Secure.putString(context.getContentResolver(), "op_quick_launch_apps", str);
    }

    public static void sendGoogleTracker(String str, String str2, String str3) {
        SettingsBaseApplication settingsBaseApplication = (SettingsBaseApplication) SettingsBaseApplication.mApplication;
        if (!TextUtils.isEmpty(str3) && settingsBaseApplication != null) {
            try {
                if (settingsBaseApplication.isBetaRom()) {
                    long j = 0;
                    if (isNumeric(str3)) {
                        j = (long) Integer.valueOf(str3).intValue();
                        str3 = null;
                    }
                    settingsBaseApplication.getDefaultTracker().send(MapBuilder.createEvent(str, str2, str3, Long.valueOf(j)).build());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isBetaRom() {
        return "1".equals(SystemProperties.get("ro.build.beta")) || "1".equals(SystemProperties.get("persist.op.ga"));
    }

    public static boolean isBeta() {
        return "1".equals(SystemProperties.get("ro.build.beta")) && !"1".equals(SystemProperties.get("ro.build.alpha"));
    }

    public static boolean isSupportFontStyleSetting() {
        String country = Locale.getDefault().getCountry();
        String language = Locale.getDefault().getLanguage();
        String[] stringArray = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_font_style_support_language);
        Log.d("FontStyleSetting", "language = " + language + " country = " + country);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].equalsIgnoreCase(language)) {
                Log.d("FontStyleSetting", "support language = " + language);
                return true;
            }
        }
        return false;
    }

    public static String getDeviceModel() {
        Log.i("OPUtils", "DeviceModel = " + Build.MODEL);
        return Build.MODEL;
    }

    public static String getDeviceName() {
        return Build.DEVICE;
    }

    public static String getOPSafeUUID(Context context) {
        try {
            Bundle call = context.getContentResolver().call(Uri.parse("content://com.oneplus.security.database.SafeProvider"), "query_oneplus_security_uuid", (String) null, (Bundle) null);
            if (call != null) {
                return call.getString("op_security_uuid");
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getLang() {
        return Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
    }

    public static boolean hasMultiAppProfiles(UserManager userManager) {
        for (UserInfo userInfo : userManager.getProfiles(UserHandle.myUserId())) {
            if (userInfo.id == 999) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMultiAppUser() {
        return UserHandle.myUserId() == 999;
    }

    public static boolean hasMultiApp(Context context, String str) {
        boolean z = false;
        Iterator it = context.getPackageManager().getInstalledApplicationsAsUser(0, 999).iterator();
        while (true) {
            if (it.hasNext()) {
                if (str.equals(((ApplicationInfo) it.next()).packageName)) {
                    z = true;
                    break;
                }
            } else {
                break;
            }
        }
        Log.d("OPUtils", "hasMultiApp ," + str + " hasMultiApp:" + z);
        return z;
    }

    public static void installMultiApp(Context context, String str, int i) {
        Log.e("OPUtils", "installMultiApp" + str);
        try {
            int installExistingPackageAsUser = context.getPackageManager().installExistingPackageAsUser(str, i);
            if (installExistingPackageAsUser == -111) {
                Log.e("OPUtils", "Could not install mobile device management app on managed profile because the user is restricted");
            } else if (installExistingPackageAsUser == -3) {
                Log.e("OPUtils", "Could not install mobile device management app on managed profile because the package could not be found");
            } else if (installExistingPackageAsUser != 1) {
                Log.e("OPUtils", "Could not install mobile device management app on managed profile. Unknown status: " + installExistingPackageAsUser);
            } else {
                Log.e("OPUtils", "installMultiApp" + str + "success");
            }
        } catch (Exception e) {
            Log.e("OPUtils", "This should not happen.", e);
        }
    }

    public static UserInfo getCorpUserInfo(Context context) {
        UserInfo profileParent;
        UserManager userManager = (UserManager) context.getSystemService("user");
        int userHandle = userManager.getUserHandle();
        for (UserInfo userInfo : userManager.getUsers()) {
            if (userInfo.isManagedProfile() && (profileParent = userManager.getProfileParent(userInfo.id)) != null && profileParent.id == userHandle) {
                return userInfo;
            }
        }
        return null;
    }

    public static void stopTethering(Context context) {
        ((ConnectivityManager) context.getSystemService("connectivity")).stopTethering(0);
    }

    public static String resetDeviceNameIfInvalid(Context context) {
        String str = SystemProperties.get("ro.display.series");
        String string = Settings.System.getString(context.getContentResolver(), "oem_oneplus_modified_devicename");
        String string2 = Settings.System.getString(context.getContentResolver(), "oem_oneplus_devicename");
        if (string == null || !TextUtils.isEmpty(string2)) {
            return string2;
        }
        Settings.System.putString(context.getContentResolver(), "oem_oneplus_devicename", str);
        return str;
    }

    public static String getFileNameNoEx(String str) {
        int lastIndexOf;
        return (str == null || str.length() <= 0 || (lastIndexOf = str.lastIndexOf(46)) <= -1 || lastIndexOf >= str.length()) ? str : str.substring(0, lastIndexOf);
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x009d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isLaboratoryFeatureExist() {
        /*
        // Method dump skipped, instructions count: 165
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.utils.OPUtils.isLaboratoryFeatureExist():boolean");
    }

    public static boolean isSupportSocTriState() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_MM_ALERTSLIDER");
    }

    public static boolean isSupportVideoEnhancer() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_MM_VIDEO_ENHANCEMENT");
    }

    public static boolean isSupportMotionGraphicsCompensation() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_MM_IRIS_CHIP_SUPPORT");
    }

    public static boolean isSupportHolePunchFrontCam() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_HOLE_PUNCH_FRONT_CAM");
    }

    public static boolean isSupportTapCoexist() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_ENABLE_TAP_COEXIST");
    }

    public static boolean isSupportAskAlexa() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_DOUBLE_TAP_POWER_KEY_ALEXA");
    }

    public static boolean isAllowSendAppTracker(Context context) {
        if (Settings.System.getIntForUser(context.getContentResolver(), "oem_join_user_plan_settings", 0, 0) == 1) {
            return true;
        }
        return false;
    }

    public static boolean isSupportHearingAid() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_MM_HAC");
    }

    public static boolean isSupportGesturePullNotificationBar() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_SHOW_NOTIFICATION_BAR_BY_FINGERPRINT_SENSOR");
    }

    public static boolean isSupportScreenCutting() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_CAMERA_NOTCH");
    }

    public static boolean isSupportGameModeNetBoost() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_GAMEMODE_NETBOOST");
    }

    public static boolean isSupportNewPlanPowerOffAlarm() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_NEW_PLAN_POWEWR_OFF_ALARM");
    }

    public static boolean isSupportAppSecureRecommd() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_INSTALL_FROM_MARKET");
    }

    public static boolean isSupportScreenDisplayAdaption() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_SCREEN_COMPAT");
    }

    public static boolean isSupportAlwaysOnDisplay() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_ALWAYS_ON_DISPLAY");
    }

    public static int parseColor(String str) {
        if (!TextUtils.isEmpty(str) && !str.contains("#")) {
            str = "#" + str;
        }
        return Color.parseColor(str);
    }

    public static boolean isSupportSleepStandby() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_OPSM");
    }

    public static boolean isSupportCustomFingerprint() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_SUPPORT_CUSTOM_FINGERPRINT");
    }

    public static boolean isSupportGameModePowerSaver() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_GAMEMODE_POWERSAVER");
    }

    public static boolean isSupportCustomBlinkLight() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_CUSTOM_BLINK_LIGHT");
    }

    public static boolean isProductSwarpChargSupport() {
        return OpFeatures.isSupport(new int[]{245});
    }

    public static boolean isProductRTTSupport() {
        return OpFeatures.isSupport(new int[]{73});
    }

    public static boolean isSupportQuickLaunch() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_QUICK_LAUNCH");
    }

    public static boolean isSupportNewGesture() {
        if (Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "op_gesture_button_launcher", 0) == 1) {
            return true;
        }
        return false;
    }

    public static boolean isSupportScreenRefreshRate() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_SCREEN_REFRESH_RATE");
    }

    public static boolean isSupportXVibrate() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_X_LINEAR_VIBRATION_MOTOR");
    }

    public static boolean isSupportQuickReply() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_QUICK_REPLY");
    }

    public static boolean isSupportGameAdMode() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_AD_MODE");
    }

    public static boolean isSupportXCamera() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_MOTOR_CONTROL");
    }

    public static boolean isSupportPocketMode() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_ENABLE_POCKETMODE_SWITCH");
    }

    public static boolean isGesturalEnabled(Context context) {
        return 2 == context.getResources().getInteger(17694854);
    }

    public static boolean isSideEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "op_gesture_button_side_enabled", 0) == 1;
    }

    public static void setLightNavigationBar(Window window, int i) {
        int i2;
        Log.d("OPUtils", "setLightNavigationBar theme =  " + i);
        if (window != null) {
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            if (i == 0) {
                i2 = systemUiVisibility | 16 | 8192;
            } else {
                i2 = (i == 2 ? systemUiVisibility | 16 : systemUiVisibility & -17) & -8193;
            }
            if (isGesturalEnabled(SettingsBaseApplication.mApplication) && isSideEnabled(SettingsBaseApplication.mApplication)) {
                window.setNavigationBarColor(0);
            }
            window.getDecorView().setSystemUiVisibility(i2);
        }
    }

    public static boolean isSupportEarphoneMode() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_EARPHONE_MODE");
    }

    public static boolean isSM8150Products() {
        return Build.BOARD.equals("msmnile");
    }

    public static boolean isSM8X50Products() {
        return Build.BOARD.equals("msmnile") || Build.BOARD.equals("kona");
    }

    public static boolean isSM8250Products() {
        return Build.BOARD.equals("kona");
    }

    public static boolean isSM8750Products() {
        return Build.BOARD.equals("lito");
    }

    public static boolean isSupportAppsDisplayInFullscreen() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_APPS_DISPLAY_IN_FULLSCREEN");
    }

    public static SpannableStringBuilder parseLink(String str, String str2, String str3, String str4) {
        Spannable spannable = (Spannable) Html.fromHtml(str + "<a href=\"" + str2 + "\">" + str3 + "</a>" + str4);
        URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(0, spannable.length(), URLSpan.class);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannable);
        spannableStringBuilder.clearSpans();
        for (URLSpan uRLSpan : uRLSpanArr) {
            spannableStringBuilder.setSpan(new URLSpan(uRLSpan.getURL()), spannable.getSpanStart(uRLSpan), spannable.getSpanEnd(uRLSpan), 33);
        }
        return spannableStringBuilder;
    }

    public static boolean isHDProject() {
        String[] stringArray = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_hd_project);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i] != null && stringArray[i].equalsIgnoreCase(Build.MODEL)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGuaProject() {
        String[] stringArray = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_guacamole_project);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i] != null && stringArray[i].equalsIgnoreCase(Build.MODEL)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSupportDynamicEnrollAnimation() {
        return Build.MODEL.equalsIgnoreCase("HD1925") || Build.BOARD.equals("kona");
    }

    public static boolean is18857Project() {
        return Build.MODEL.equalsIgnoreCase(SettingsBaseApplication.mApplication.getResources().getString(C0017R$string.oneplus_oneplus_model_18857_for_cn)) || Build.MODEL.equalsIgnoreCase(SettingsBaseApplication.mApplication.getResources().getString(C0017R$string.oneplus_oneplus_model_18857_for_in)) || Build.MODEL.equalsIgnoreCase(SettingsBaseApplication.mApplication.getResources().getString(C0017R$string.oneplus_oneplus_model_18857_for_eu)) || Build.MODEL.equalsIgnoreCase(SettingsBaseApplication.mApplication.getResources().getString(C0017R$string.oneplus_oneplus_model_18857_for_us));
    }

    public static boolean isSupportZVibrationMotor() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_Z_VIBRATION_MOTOR");
    }

    public static boolean isSupportSmartBoost() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_SMART_BOOST");
    }

    public static boolean isSupportNotificationLight() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_NOTIFICATION_LIGHT");
    }

    public static boolean isSupportReadingModeInterpolater() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_READING_MODE_INTERPOLATER");
    }

    public static boolean isnoDisplaySarValueProject() {
        String[] stringArray = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_no_display_sar_value_project);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i] != null && stringArray[i].equalsIgnoreCase(Build.MODEL)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSupportUss() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_USS") && "sprint".equals(SystemProperties.get("ro.boot.opcarrier"));
    }

    public static boolean isEUVersion() {
        return "true".equals(SystemProperties.get("ro.build.eu", "false"));
    }

    public static void sendAnalytics(String str, String str2, String str3) {
        AppTrackerHelper.getInstance().putAnalytics(str, str2, str3);
        Log.d("AppTracker Analytics", "eventname : " + str + " label : " + str2 + " value : " + str3);
    }

    public static void sendAnalytics(String str, String str2, String str3, String str4) {
        AppTrackerHelper.getInstance().putAnalytics(str, str2, str3, str4);
        Log.d("AppTracker Analytics", "eventname : " + str2 + " label : " + str3 + " value : " + str4);
    }

    public static void startFragment(Context context, String str, int i) {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(context);
        subSettingLauncher.setDestination(str);
        subSettingLauncher.setSourceMetricsCategory(i);
        subSettingLauncher.launch();
    }

    public static boolean getApnEditable() {
        if (Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "op_apn_editable_enable", 0) == 1) {
            return true;
        }
        return false;
    }

    public static boolean isSupportMMDisplayColorScreenMode() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_MM_DISPLAY_COLOR_SCREEN_MODE");
    }

    public static boolean isSupportSystemProductionRingtone() {
        return SystemProperties.getInt("ro.product.first_api_level", 0) < 29;
    }

    public static boolean isSupportTrueColorMode(Context context) {
        ColorDisplayManager colorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
        return ColorDisplayManager.isDisplayWhiteBalanceAvailable(context);
    }

    public static boolean isSupportMultiScreenResolution(Context context) {
        Display.Mode[] supportedModes = ((DisplayManager) context.getSystemService("display")).getDisplay(0).getSupportedModes();
        if (supportedModes == null || supportedModes.length <= 2) {
            return false;
        }
        return true;
    }

    public static void savePINPasswordLength(LockPatternUtils lockPatternUtils, int i, int i2) {
        try {
            lockPatternUtils.getLockSettings().setLong("lockscreen.pin_password_length", (long) i, i2);
        } catch (Exception e) {
            Log.d("savePINPasswordLength", "saveLong error: " + e.getMessage());
        }
    }

    public static boolean isOP_19_2nd() {
        String[] stringArray = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_19_2nd_projects);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i] != null && stringArray[i].equalsIgnoreCase(Build.MODEL)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLargerFontSize(Context context) {
        return context.getResources().getConfiguration().fontScale >= 1.15f;
    }

    public static boolean isLargerScreenZoom(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return is2KResolution(context) ? configuration.densityDpi >= 600 : configuration.densityDpi >= 500;
    }

    public static boolean is2KResolution(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels * displayMetrics.heightPixels >= 3686400;
    }

    public static void notifyMultiPackageRemoved(Context context, String str, int i, boolean z) {
        Log.d("OPUtils", "notifyMultiPackageRemoved pkg:" + str + " uid:" + i + " removed:" + z);
        Intent intent = new Intent();
        intent.setAction("oneplus.intent.action.MULTI_APP_CHANGED");
        intent.putExtra("package_name", str);
        intent.putExtra("package_uid", i);
        intent.putExtra("status_removed", z);
        intent.setPackage("com.oneplus.gamespace");
        context.sendBroadcast(intent);
    }

    public static boolean isMEARom() {
        return "1".equals(SystemProperties.get("ro.build.mea"));
    }

    public static boolean isSupportHighVsync() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_HIGH_VSYNC");
    }

    public static boolean isInSlaDownLoadOpenAppsListString(Context context, OPAppModel oPAppModel) {
        String string = Settings.System.getString(context.getContentResolver(), "sla_download_open_apps_list");
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(oPAppModel.getPkgName());
        sb.append(";");
        return string.contains(sb.toString());
    }

    public static boolean isSupportOPSLA() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_OPSLA");
    }

    public static boolean isSupportUssOnly() {
        return isSupportUss() && "18825".equals(SystemProperties.get("ro.boot.project_name"));
    }

    public static boolean isSupportGoogleCommSuit() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_GOOGLE_COMM_SUIT");
    }

    public static boolean isSupportUstUnify() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_UNIFIED_DEVICE");
    }

    public static void initHwId() {
        if (TextUtils.isEmpty(Settings.System.getString(SettingsBaseApplication.mApplication.getContentResolver(), "hw_version_ui"))) {
            setHardwareVersion();
        }
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    private static void setHardwareVersion() {
        char c;
        String str = SystemProperties.get("ro.boot.project_name");
        switch (str.hashCode()) {
            case 46976244:
                if (str.equals("18825")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 47006128:
                if (str.equals("19855")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 47006155:
                if (str.equals("19861")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 47006157:
                if (str.equals("19863")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 47661379:
                if (str.equals("20809")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        Settings.System.putString(SettingsBaseApplication.mApplication.getContentResolver(), "hw_version_ui", c != 0 ? (c == 1 || c == 2) ? "13" : c != 3 ? c != 4 ? SystemProperties.get("ro.boot.hw_version", "") : "53" : "15" : "31");
    }

    public static void replaceListViewForListFragment(ListFragment listFragment) {
        View listView = listFragment.getListView();
        ViewParent parent = listView.getParent();
        if (parent != null) {
            ViewGroup viewGroup = (ViewGroup) parent;
            viewGroup.removeView(listView);
            SpringRelativeLayout springRelativeLayout = new SpringRelativeLayout(listFragment.getContext());
            springRelativeLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            springRelativeLayout.setFocusable(true);
            springRelativeLayout.setFocusableInTouchMode(true);
            springRelativeLayout.setSaveEnabled(false);
            SpringListView springListView = (SpringListView) LayoutInflater.from(listFragment.getContext()).inflate(C0012R$layout.spring_preference_listview, (ViewGroup) null, false);
            springRelativeLayout.addView(springListView);
            springListView.setId(16908298);
            springRelativeLayout.addSpringView(16908298);
            springListView.setEdgeEffectFactory(springRelativeLayout.createViewEdgeEffectFactory());
            viewGroup.addView(springRelativeLayout);
            try {
                Field declaredField = ListFragment.class.getDeclaredField("mList");
                declaredField.setAccessible(true);
                declaredField.set(listFragment, null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            try {
                Method declaredMethod = ListFragment.class.getDeclaredMethod("ensureList", new Class[0]);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(listFragment, new Object[0]);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static boolean methodIsMigrated(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString("minus_one_page_content", "news_only");
        try {
            Bundle call = context.getContentResolver().call(LAUNCHER_FEATURE_URI, "checkFeature", "minus_one_page", bundle);
            if (call == null) {
                Log.d("OPUtils", "methodIsMigrated bundle null");
                return false;
            } else if (call.getInt("result_code", -1) != 0) {
                Log.e("OPUtils", "methodIsMigrated " + call.getString("result_message", null));
                return false;
            } else if (!call.getBoolean("is_supported", false)) {
                return true;
            } else {
                Log.d("OPUtils", "methodIsMigrated hideShelfOption");
                return false;
            }
        } catch (Exception e) {
            Log.d("OPUtils", "methodIsMigrated e1 = " + e);
            return false;
        }
    }

    public static void removeSomeEntryAndValue(ListPreference listPreference, String str) {
        if (!(listPreference == null || str == null)) {
            CharSequence[] entries = listPreference.getEntries();
            CharSequence[] entryValues = listPreference.getEntryValues();
            if (!(entries == null || entryValues == null || entries.length != entryValues.length)) {
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (int i = 0; i < entries.length; i++) {
                    if (!str.equals(entries[i])) {
                        arrayList.add(entries[i]);
                        arrayList2.add(entryValues[i]);
                    }
                }
                CharSequence[] charSequenceArr = new CharSequence[arrayList.size()];
                CharSequence[] charSequenceArr2 = new CharSequence[arrayList2.size()];
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    charSequenceArr[i2] = (CharSequence) arrayList.get(i2);
                    charSequenceArr2[i2] = (CharSequence) arrayList2.get(i2);
                }
                listPreference.setEntries(charSequenceArr);
                listPreference.setEntryValues(charSequenceArr2);
            }
        }
    }

    public static boolean isOnePlusBrand() {
        return "OnePlus".equalsIgnoreCase(Build.BRAND);
    }

    public static void setCustomToneDarkModeLocation() {
        Intent intent = new Intent("com.oneplus.systemui.qs.hide_tile");
        intent.putExtra("tile", "custom(com.android.settings/com.oneplus.settings.darkmode.OPCustomToneDarkModeTileService)");
        intent.putExtra("position", 13);
        SettingsBaseApplication.getContext().sendBroadcast(intent);
    }

    public static boolean isDeviceProvisioned(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
    }

    public static String getOtherPackageString(Resources resources, String str, String str2) {
        int identifier = resources.getIdentifier(str2, "string", str);
        return identifier != 0 ? resources.getString(identifier) : "";
    }

    public static boolean hasOnePlusDialer(Context context) {
        return isAppExist(context, "com.android.dialer") || isAppExist(context, "com.oneplus.dialer");
    }

    public static boolean isEF009Project() {
        return TextUtils.equals("20809", SystemProperties.get("ro.boot.project_name"));
    }

    public static boolean isContainSymbol(String str) {
        return !TextUtils.isEmpty(str) && str.contains("T+");
    }

    public static Spanned getSymbolDeviceName(String str) {
        return Html.fromHtml(str.replace("+", "<small><sup>+</sup></small>"));
    }

    public static void startVibratePattern(Context context) {
        Vibrator vibrator = isSupportXVibrate() ? (Vibrator) context.getSystemService("vibrator") : null;
        if (vibrator != null && VibratorSceneUtils.systemVibrateEnabled(context)) {
            vibrator.cancel();
            VibratorSceneUtils.vibrateIfNeeded(VibratorSceneUtils.getVibratorScenePattern(context, vibrator, 1003), vibrator);
        }
    }
}
