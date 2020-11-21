package com.android.settings.applications.manageapplications;

import com.android.settings.C0017R$string;
import com.android.settings.applications.AppStateInstallAppsBridge;
import com.android.settings.applications.AppStateManageExternalStorageBridge;
import com.android.settings.applications.AppStateNotificationBridge;
import com.android.settings.applications.AppStateOverlayBridge;
import com.android.settings.applications.AppStatePowerBridge;
import com.android.settings.applications.AppStateUsageBridge;
import com.android.settings.applications.AppStateWriteSettingsBridge;
import com.android.settings.wifi.AppStateChangeWifiStateBridge;
import com.android.settingslib.applications.ApplicationsState;
import com.oneplus.settings.backgroundoptimize.AppBgOptimizeBridge;
import com.oneplus.settings.better.ReadingModeEffectSelectBridge;
import com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge;
import com.oneplus.settings.utils.OPUtils;

public class AppFilterRegistry {
    private static AppFilterRegistry sRegistry;
    private final AppFilterItem[] mFilters;

    public int getDefaultFilterType(int i) {
        if (i == 1) {
            return 6;
        }
        if (i == 13) {
            return 15;
        }
        if (i == 14) {
            return 17;
        }
        switch (i) {
            case 4:
                return 10;
            case 5:
                return 0;
            case 6:
                return 11;
            case 7:
                return 12;
            case 8:
                return 13;
            default:
                return 2;
        }
    }

    private AppFilterRegistry() {
        ApplicationsState.AppFilter appFilter;
        ApplicationsState.AppFilter appFilter2;
        AppFilterItem[] appFilterItemArr = new AppFilterItem[27];
        this.mFilters = appFilterItemArr;
        appFilterItemArr[0] = new AppFilterItem(new ApplicationsState.CompoundFilter(AppStatePowerBridge.FILTER_POWER_WHITELISTED, ApplicationsState.FILTER_ALL_ENABLED), 0, C0017R$string.high_power_filter_on);
        this.mFilters[1] = new AppFilterItem(new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_WITHOUT_DISABLED_UNTIL_USED, ApplicationsState.FILTER_ALL_ENABLED), 1, C0017R$string.filter_all_apps);
        this.mFilters[2] = new AppFilterItem(ApplicationsState.FILTER_EVERYTHING, 2, C0017R$string.filter_all_apps);
        this.mFilters[3] = new AppFilterItem(ApplicationsState.FILTER_ALL_ENABLED, 3, C0017R$string.filter_enabled_apps);
        this.mFilters[5] = new AppFilterItem(ApplicationsState.FILTER_DISABLED, 5, C0017R$string.filter_apps_disabled);
        this.mFilters[4] = new AppFilterItem(ApplicationsState.FILTER_INSTANT, 4, C0017R$string.filter_instant_apps);
        this.mFilters[6] = new AppFilterItem(AppStateNotificationBridge.FILTER_APP_NOTIFICATION_RECENCY, 6, C0017R$string.sort_order_recent_notification);
        this.mFilters[7] = new AppFilterItem(AppStateNotificationBridge.FILTER_APP_NOTIFICATION_FREQUENCY, 7, C0017R$string.sort_order_frequent_notification);
        this.mFilters[8] = new AppFilterItem(ApplicationsState.FILTER_PERSONAL, 8, C0017R$string.category_personal);
        this.mFilters[9] = new AppFilterItem(ApplicationsState.FILTER_WORK, 9, C0017R$string.category_work);
        this.mFilters[10] = new AppFilterItem(AppStateUsageBridge.FILTER_APP_USAGE, 10, C0017R$string.filter_all_apps);
        this.mFilters[11] = new AppFilterItem(AppStateOverlayBridge.FILTER_SYSTEM_ALERT_WINDOW, 11, C0017R$string.filter_overlay_apps);
        this.mFilters[12] = new AppFilterItem(AppStateWriteSettingsBridge.FILTER_WRITE_SETTINGS, 12, C0017R$string.filter_write_settings_apps);
        this.mFilters[13] = new AppFilterItem(AppStateInstallAppsBridge.FILTER_APP_SOURCES, 13, C0017R$string.filter_install_sources_apps);
        this.mFilters[15] = new AppFilterItem(AppStateChangeWifiStateBridge.FILTER_CHANGE_WIFI_STATE, 15, C0017R$string.filter_write_settings_apps);
        this.mFilters[16] = new AppFilterItem(AppStateNotificationBridge.FILTER_APP_NOTIFICATION_BLOCKED, 16, C0017R$string.filter_notif_blocked_apps);
        this.mFilters[17] = new AppFilterItem(AppStateManageExternalStorageBridge.FILTER_MANAGE_EXTERNAL_STORAGE, 17, C0017R$string.filter_manage_external_storage);
        this.mFilters[18] = new AppFilterItem(DisplaySizeAdaptionBridge.FILTER_APP_ALL_SCREENS, 18, C0017R$string.filter_all_apps);
        AppFilterItem[] appFilterItemArr2 = this.mFilters;
        if (!OPUtils.isSupportScreenCutting()) {
            appFilter = DisplaySizeAdaptionBridge.FILTER_APP_FULL_SCREEN;
        } else {
            appFilter = DisplaySizeAdaptionBridge.FILTER_APP_DEFAULT;
        }
        appFilterItemArr2[19] = new AppFilterItem(appFilter, 19, C0017R$string.oneplus_app_display_fullscreen);
        AppFilterItem[] appFilterItemArr3 = this.mFilters;
        if (!OPUtils.isSupportScreenCutting()) {
            appFilter2 = DisplaySizeAdaptionBridge.FILTER_APP_ORIGINAL_SIZE;
        } else {
            appFilter2 = DisplaySizeAdaptionBridge.FILTER_APP_FULL_SCREEN;
        }
        appFilterItemArr3[20] = new AppFilterItem(appFilter2, 20, !OPUtils.isSupportScreenCutting() ? C0017R$string.oneplus_app_display_compatibility : C0017R$string.default_keyboard_layout);
        this.mFilters[21] = new AppFilterItem(AppBgOptimizeBridge.FILTER_APP_BG_All, 21, C0017R$string.filter_all_apps);
        this.mFilters[22] = new AppFilterItem(AppBgOptimizeBridge.FILTER_APP_BG_NOT_OPTIMIZE, 22, C0017R$string.not_optimized_apps);
        this.mFilters[23] = new AppFilterItem(ReadingModeEffectSelectBridge.FILTER_ALL, 23, C0017R$string.filter_all_apps);
        this.mFilters[24] = new AppFilterItem(ReadingModeEffectSelectBridge.FILTER_CHROMATIC, 24, C0017R$string.oneplus_reading_mode_chromatic);
        this.mFilters[25] = new AppFilterItem(ReadingModeEffectSelectBridge.FILTER_MONO, 25, C0017R$string.oneplus_reading_mode_mono);
        this.mFilters[26] = new AppFilterItem(ReadingModeEffectSelectBridge.FILTER_AVAILABLE, 26, C0017R$string.oneplus_reading_mode_available);
    }

    public static AppFilterRegistry getInstance() {
        if (sRegistry == null) {
            sRegistry = new AppFilterRegistry();
        }
        return sRegistry;
    }

    public AppFilterItem get(int i) {
        return this.mFilters[i];
    }
}
