package com.oneplus.settings.utils;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import com.android.settings.C0017R$string;

public class OPAuthenticationInformationUtils {
    public static boolean isNeedShowAuthenticationInformation(Context context) {
        if (Build.MODEL.equals(context.getString(C0017R$string.oneplus_oneplus_model_18821_for_cn)) || Build.MODEL.equals(context.getString(C0017R$string.oneplus_oneplus_model_18857_for_cn))) {
            return true;
        }
        if (Build.MODEL.equals(context.getString(C0017R$string.oneplus_oneplus_model_18825_for_us)) && !OPUtils.isO2()) {
            return true;
        }
        if (Build.MODEL.equals(context.getString(C0017R$string.oneplus_model_19801_for_cn)) && !OPUtils.isMEARom()) {
            return true;
        }
        if (Build.MODEL.equals(context.getString(C0017R$string.oneplus_model_18865_for_cn)) && !OPUtils.isMEARom()) {
            return true;
        }
        if (!Build.MODEL.equals(context.getString(C0017R$string.oneplus_model_19811_for_cn)) || !OPUtils.isOnePlusBrand()) {
            return Build.MODEL.equals(context.getString(C0017R$string.oneplus_model_19821_for_cn)) && OPUtils.isOnePlusBrand();
        }
        return true;
    }

    public static boolean isNeedAddAuthenticationInfo(Context context) {
        String str = Build.MODEL;
        if (((!str.equals(context.getString(C0017R$string.oneplus_oneplus_model_18821_for_eu)) && !str.equals(context.getString(C0017R$string.oneplus_model_18865_for_eu)) && !str.equals(context.getString(C0017R$string.oneplus_model_19801_for_eu)) && !str.equals(context.getString(C0017R$string.oneplus_oneplus_model_18857_for_eu))) || !OPUtils.isEUVersion()) && ((!str.equals(context.getString(C0017R$string.oneplus_model_19821_for_eea)) || !OPUtils.isOnePlusBrand()) && ((!str.equals(context.getString(C0017R$string.oneplus_model_19811_for_eea)) || !OPUtils.isOnePlusBrand()) && !str.equals(context.getString(C0017R$string.oneplus_oneplus_model_18821_for_in)) && !str.equals(context.getString(C0017R$string.oneplus_oneplus_model_18857_for_in)) && !str.equals(context.getString(C0017R$string.oneplus_oneplus_model_18827_for_eu)) && !str.equals(context.getString(C0017R$string.oneplus_oneplus_model_18857_for_us)) && !str.equals("ONEPLUS A3003")))) {
            if (str.equals("ONEPLUS A3000") || str.equals("ONEPLUS A3010")) {
                String str2 = SystemProperties.get("ro.rf_version");
                if (str2.contains("Eu") || str2.contains("In")) {
                    return false;
                }
                return true;
            } else if (str.equals(context.getString(C0017R$string.oneplus_model_19801_for_in)) || str.equals(context.getString(C0017R$string.oneplus_model_18865_for_in)) || ((str.equals(context.getString(C0017R$string.oneplus_model_19821_for_in)) && OPUtils.isOnePlusBrand()) || (str.equals(context.getString(C0017R$string.oneplus_model_19811_for_in)) && OPUtils.isOnePlusBrand()))) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isOlder6tProducts() {
        return Build.MODEL.contains("A30") || Build.MODEL.contains("A50") || Build.MODEL.contains("A600");
    }
}
