package com.oneplus.accountsdk.utils;

import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.RequiresApi;
import java.util.Locale;

public class OnePlusAuthDeviceIdUtils {
    @RequiresApi(api = 21)
    public static String getLanguage() {
        return (Build.VERSION.SDK_INT >= 24 ? LocaleList.getDefault().get(0) : Locale.getDefault()).toLanguageTag();
    }
}
