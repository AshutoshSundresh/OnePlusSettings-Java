package com.android.settingslib.search;

import com.android.settings.accessibility.AccessibilityControlTimeoutPreferenceFragment;
import com.android.settings.accessibility.AccessibilitySettings;
import com.android.settings.accessibility.CaptionAppearanceFragment;
import com.android.settings.accessibility.CaptionMoreOptionsFragment;
import com.android.settings.accessibility.CaptionPropertiesFragment;
import com.android.settings.accessibility.MagnificationPreferenceFragment;
import com.android.settings.accessibility.MagnificationSettingsFragment;
import com.android.settings.accessibility.ToggleAutoclickPreferenceFragment;
import com.android.settings.accessibility.ToggleDaltonizerPreferenceFragment;
import com.android.settings.accessibility.VibrationSettings;
import com.android.settings.applications.defaultapps.AutofillPicker;
import com.android.settings.applications.managedomainurls.ManageDomainUrls;
import com.android.settings.connecteddevice.AdvancedConnectedDeviceDashboardFragment;
import com.android.settings.connecteddevice.BluetoothDashboardFragment;
import com.android.settings.connecteddevice.PreviouslyConnectedDeviceDashboardFragment;
import com.android.settings.connecteddevice.usb.UsbDetailsFragment;
import com.android.settings.development.DevelopmentSettingsDashboardFragment;
import com.android.settings.display.AdaptiveSleepSettings;
import com.android.settings.display.ScreenZoomSettings;
import com.android.settings.display.darkmode.DarkModeSettingsFragment;
import com.android.settings.flashlight.FlashlightHandleActivity;
import com.android.settings.fuelgauge.PowerUsageAdvanced;
import com.android.settings.fuelgauge.PowerUsageSummary;
import com.android.settings.fuelgauge.SmartBatterySettings;
import com.android.settings.fuelgauge.batterysaver.BatterySaverSettings;
import com.android.settings.homepage.TopLevelSettings;
import com.android.settings.location.ScanningSettings;
import com.android.settings.network.MobileNetworkListFragment;
import com.android.settings.notification.zen.ZenModeRestrictNotificationsSettings;
import com.android.settings.wfd.WifiDisplaySettings;

public class SearchIndexableResourcesMobile extends SearchIndexableResourcesBase {
    public SearchIndexableResourcesMobile() {
        addIndex(new SearchIndexableData(AccessibilityControlTimeoutPreferenceFragment.class, AccessibilityControlTimeoutPreferenceFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(AccessibilitySettings.class, AccessibilitySettings.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(CaptionAppearanceFragment.class, CaptionAppearanceFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(CaptionMoreOptionsFragment.class, CaptionMoreOptionsFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(CaptionPropertiesFragment.class, CaptionPropertiesFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(MagnificationPreferenceFragment.class, MagnificationPreferenceFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(MagnificationSettingsFragment.class, MagnificationSettingsFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(ToggleAutoclickPreferenceFragment.class, ToggleAutoclickPreferenceFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(ToggleDaltonizerPreferenceFragment.class, ToggleDaltonizerPreferenceFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(VibrationSettings.class, VibrationSettings.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(AutofillPicker.class, AutofillPicker.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(ManageDomainUrls.class, ManageDomainUrls.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(AdvancedConnectedDeviceDashboardFragment.class, AdvancedConnectedDeviceDashboardFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(BluetoothDashboardFragment.class, BluetoothDashboardFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(PreviouslyConnectedDeviceDashboardFragment.class, PreviouslyConnectedDeviceDashboardFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(UsbDetailsFragment.class, UsbDetailsFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(DevelopmentSettingsDashboardFragment.class, DevelopmentSettingsDashboardFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(AdaptiveSleepSettings.class, AdaptiveSleepSettings.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(ScreenZoomSettings.class, ScreenZoomSettings.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(DarkModeSettingsFragment.class, DarkModeSettingsFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(FlashlightHandleActivity.class, FlashlightHandleActivity.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(PowerUsageAdvanced.class, PowerUsageAdvanced.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(PowerUsageSummary.class, PowerUsageSummary.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(SmartBatterySettings.class, SmartBatterySettings.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(BatterySaverSettings.class, BatterySaverSettings.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(TopLevelSettings.class, TopLevelSettings.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(ScanningSettings.class, ScanningSettings.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(MobileNetworkListFragment.class, MobileNetworkListFragment.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(ZenModeRestrictNotificationsSettings.class, ZenModeRestrictNotificationsSettings.SEARCH_INDEX_DATA_PROVIDER));
        addIndex(new SearchIndexableData(WifiDisplaySettings.class, WifiDisplaySettings.SEARCH_INDEX_DATA_PROVIDER));
    }
}
