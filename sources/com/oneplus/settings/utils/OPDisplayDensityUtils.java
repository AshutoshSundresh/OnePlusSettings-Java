package com.oneplus.settings.utils;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settingslib.display.DisplayDensityUtils;

public class OPDisplayDensityUtils {
    private static int[] mValues = {420, 450, 480, 510, 540};
    private int mCurrentIndex;
    private int mDefaultDensity;
    private String[] mEntries;

    static {
        SystemProperties.get("ro.display.series", "");
    }

    public OPDisplayDensityUtils(Context context) {
        SystemProperties.get("ro.sf.lcd_density", "480");
        this.mDefaultDensity = new DisplayDensityUtils(context).getDefaultDensity();
        if (useDefault560Dpi(context)) {
            mValues = context.getResources().getIntArray(C0003R$array.oneplus_screen_dpi_values);
        } else if (useNew1080Dpi()) {
            mValues = context.getResources().getIntArray(C0003R$array.oneplus_screen_1080_new_dpi_values);
        }
        this.mEntries = new String[]{context.getResources().getString(C0017R$string.screen_zoom_summary_small), context.getResources().getString(C0017R$string.screen_zoom_summary_default), context.getResources().getString(C0017R$string.screen_zoom_summary_large), context.getResources().getString(C0017R$string.screen_zoom_summary_very_large), context.getResources().getString(C0017R$string.screen_zoom_summary_extremely_large)};
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "display_density_forced", -2);
        if (!TextUtils.isEmpty(stringForUser)) {
            int i = 0;
            while (true) {
                int[] iArr = mValues;
                if (i >= iArr.length) {
                    break;
                }
                if (stringForUser.equals(String.valueOf(iArr[i]))) {
                    this.mCurrentIndex = i;
                }
                i++;
            }
        } else {
            this.mCurrentIndex = 1;
        }
        int i2 = this.mCurrentIndex;
        int[] iArr2 = mValues;
        if (i2 >= iArr2.length - 1) {
            this.mCurrentIndex = iArr2.length - 1;
        }
        if (this.mCurrentIndex <= 0) {
            this.mCurrentIndex = 0;
        }
    }

    public static boolean useDefault560Dpi(Context context) {
        SystemProperties.get("ro.sf.lcd_density", "480");
        int i = Settings.Global.getInt(context.getContentResolver(), "oneplus_screen_resolution_adjust", 2);
        return i == 0 || i == 2;
    }

    public static boolean useNew1080Dpi() {
        String str = SystemProperties.get("ro.sf.lcd_density", "480");
        return TextUtils.equals("400", str) || TextUtils.equals("420", str) || TextUtils.equals("450", str);
    }

    public static int[] get1080Dpi(Context context) {
        if (useNew1080Dpi()) {
            return context.getResources().getIntArray(C0003R$array.oneplus_screen_1080_new_dpi_values);
        }
        return mValues;
    }

    public String[] getEntries() {
        return this.mEntries;
    }

    public int[] getValues() {
        return mValues;
    }

    public int getCurrentIndex() {
        return this.mCurrentIndex;
    }

    public int getDefaultDensity() {
        return this.mDefaultDensity;
    }
}
