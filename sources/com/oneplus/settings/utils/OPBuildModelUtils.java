package com.oneplus.settings.utils;

import android.os.Build;
import com.android.settings.C0003R$array;
import com.oneplus.settings.SettingsBaseApplication;
import java.util.Arrays;
import java.util.List;

public class OPBuildModelUtils {
    public static boolean is19811() {
        List asList = Arrays.asList(SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_model_19811));
        if (asList == null) {
            return false;
        }
        return asList.contains(Build.MODEL);
    }

    public static boolean is19821() {
        List asList = Arrays.asList(SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_model_19821));
        if (asList == null) {
            return false;
        }
        return asList.contains(Build.MODEL);
    }

    public static boolean is19855() {
        List asList = Arrays.asList(SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_model_19855));
        if (asList == null) {
            return false;
        }
        return asList.contains(Build.MODEL);
    }

    public static boolean is19867() {
        List asList = Arrays.asList(SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_model_19867));
        if (asList == null) {
            return false;
        }
        return asList.contains(Build.MODEL);
    }
}
