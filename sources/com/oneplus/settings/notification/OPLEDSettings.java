package com.oneplus.settings.notification;

import android.content.ContentResolver;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.ui.OPLedColorPickerPreference;

public class OPLEDSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private OPLedColorPickerPreference mBatteryChargingPreference;
    private OPLedColorPickerPreference mBatteryFullPreference;
    private OPLedColorPickerPreference mBatteryLowPreference;
    private String[] mDialogColorPalette = {"#FF0000FF", "#FF40FFFF", "#FFFFAE00", "#FF40FF00", "#FFFF0000", "#FFFFFF00", "#FF9E00F9", "#FFEC407A"};
    private OPLedColorPickerPreference mGlobalNotificationPreference;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    private String getDriverCode(String str) {
        char c;
        switch (str.hashCode()) {
            case -1995913790:
                if (str.equals("#FF9E00F9")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1654092313:
                if (str.equals("#FFEC407A")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1622811997:
                if (str.equals("#FFFF0000")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1622285369:
                if (str.equals("#FFFFAE00")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1622135453:
                if (str.equals("#FFFFFF00")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 2021997219:
                if (str.equals("#FF0000FF")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 2137189663:
                if (str.equals("#FF40FF00")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 2137190367:
                if (str.equals("#FF40FFFF")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return "#FF0000FF";
            case 1:
                return "#FF40FFFF";
            case 2:
                return "#FFFF4000";
            case 3:
                return "#FF40FF00";
            case 4:
                return "#FFFF0000";
            case 5:
                return "#FFFFFF00";
            case 6:
                return "#FFFF00FF";
            case 7:
                return "#FFFF0040";
            default:
                return "";
        }
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    private String getDialogCode(String str) {
        char c;
        switch (str.hashCode()) {
            case -1622811997:
                if (str.equals("#FFFF0000")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1622811873:
                if (str.equals("#FFFF0040")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1622811293:
                if (str.equals("#FFFF00FF")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1622692833:
                if (str.equals("#FFFF4000")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1622135453:
                if (str.equals("#FFFFFF00")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 2021997219:
                if (str.equals("#FF0000FF")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 2137189663:
                if (str.equals("#FF40FF00")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 2137190367:
                if (str.equals("#FF40FFFF")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return "#FF0000FF";
            case 1:
                return "#FF40FFFF";
            case 2:
                return "#FFFFAE00";
            case 3:
                return "#FF40FF00";
            case 4:
                return "#FFFF0000";
            case 5:
                return "#FFFFFF00";
            case 6:
                return "#FF9E00F9";
            case 7:
                return "#FFEC407A";
            default:
                return "";
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_led_settings);
        OPLedColorPickerPreference oPLedColorPickerPreference = (OPLedColorPickerPreference) findPreference("led_settings_global_notification");
        this.mGlobalNotificationPreference = oPLedColorPickerPreference;
        oPLedColorPickerPreference.setColorPalette(this.mDialogColorPalette);
        this.mGlobalNotificationPreference.setDefaultColor("#FF00FF00");
        String format = String.format("#%06X", Integer.valueOf(Settings.System.getInt(getActivity().getContentResolver(), "notification_light_pulse_color", Color.parseColor("#FF00FF00"))));
        if (!TextUtils.isEmpty(format)) {
            this.mGlobalNotificationPreference.setColor(getDialogCode(format));
        }
        this.mGlobalNotificationPreference.setMessageText(C0017R$string.color_picker_led_color_message);
        this.mGlobalNotificationPreference.setImageViewVisibility();
        this.mGlobalNotificationPreference.setOnPreferenceChangeListener(this);
        OPLedColorPickerPreference oPLedColorPickerPreference2 = (OPLedColorPickerPreference) findPreference("led_settings_battery_full");
        this.mBatteryFullPreference = oPLedColorPickerPreference2;
        oPLedColorPickerPreference2.setColorPalette(this.mDialogColorPalette);
        this.mBatteryFullPreference.setDefaultColor("#FF00FF00");
        String format2 = String.format("#%06X", Integer.valueOf(Settings.System.getInt(getActivity().getContentResolver(), "battery_light_full_color", Color.parseColor("#FF00FF00"))));
        if (!TextUtils.isEmpty(format2)) {
            this.mBatteryFullPreference.setColor(getDialogCode(format2));
        }
        this.mBatteryFullPreference.setMessageText(C0017R$string.color_picker_led_color_message);
        this.mBatteryFullPreference.setImageViewVisibility();
        this.mBatteryFullPreference.setOnPreferenceChangeListener(this);
        OPLedColorPickerPreference oPLedColorPickerPreference3 = (OPLedColorPickerPreference) findPreference("led_settings_battery_charging");
        this.mBatteryChargingPreference = oPLedColorPickerPreference3;
        oPLedColorPickerPreference3.setColorPalette(this.mDialogColorPalette);
        this.mBatteryChargingPreference.setDefaultColor("#FEFF0000");
        String format3 = String.format("#%06X", Integer.valueOf(Settings.System.getInt(getActivity().getContentResolver(), "battery_light_medium_color", Color.parseColor("#FEFF0000"))));
        if (!TextUtils.isEmpty(format3)) {
            this.mBatteryChargingPreference.setColor(getDialogCode(format3));
        }
        this.mBatteryChargingPreference.setMessageText(C0017R$string.color_picker_led_color_message);
        this.mBatteryChargingPreference.setImageViewVisibility();
        this.mBatteryChargingPreference.setOnPreferenceChangeListener(this);
        OPLedColorPickerPreference oPLedColorPickerPreference4 = (OPLedColorPickerPreference) findPreference("led_settings_battery_low");
        this.mBatteryLowPreference = oPLedColorPickerPreference4;
        oPLedColorPickerPreference4.setColorPalette(this.mDialogColorPalette);
        this.mBatteryLowPreference.setDefaultColor("#FEFF0000");
        String format4 = String.format("#%06X", Integer.valueOf(Settings.System.getInt(getActivity().getContentResolver(), "battery_light_low_color", Color.parseColor("#FEFF0000"))));
        if (!TextUtils.isEmpty(format4)) {
            this.mBatteryLowPreference.setColor(getDialogCode(format4));
        }
        this.mBatteryLowPreference.setMessageText(C0017R$string.color_picker_led_color_message);
        this.mBatteryLowPreference.setImageViewVisibility();
        this.mBatteryLowPreference.setOnPreferenceChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String str;
        String key = preference.getKey();
        String driverCode = getDriverCode((String) obj);
        boolean z = driverCode != null && !TextUtils.isEmpty(driverCode);
        String str2 = "#FF00FF00";
        if ("led_settings_global_notification".equals(key)) {
            Settings.System.putInt(getActivity().getContentResolver(), "notification_light_pulse_color", Color.parseColor(z ? driverCode : str2));
        }
        if ("led_settings_battery_full".equals(key)) {
            ContentResolver contentResolver = getActivity().getContentResolver();
            if (z) {
                str2 = driverCode;
            }
            Settings.System.putInt(contentResolver, "battery_light_full_color", Color.parseColor(str2));
        }
        if ("led_settings_battery_charging".equals(key)) {
            ContentResolver contentResolver2 = getActivity().getContentResolver();
            if (z) {
                str = driverCode;
            } else {
                str = "#FEFF0000";
            }
            Settings.System.putInt(contentResolver2, "battery_light_medium_color", Color.parseColor(str));
        }
        if ("led_settings_battery_low".equals(key)) {
            ContentResolver contentResolver3 = getActivity().getContentResolver();
            if (!z) {
                driverCode = "#FEFF0000";
            }
            Settings.System.putInt(contentResolver3, "battery_light_low_color", Color.parseColor(driverCode));
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (this.mBatteryLowPreference != null) {
            this.mBatteryLowPreference.setSummary(getResources().getString(C0017R$string.led_settings_battery_low_summary).replace(" 5%", " 15%"));
        }
    }
}
