package com.oneplus.settings.defaultapp;

import android.content.Context;
import android.text.TextUtils;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeCamera;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeEmail;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeGallery;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeMusic;
import com.oneplus.settings.utils.OPUtils;
import java.util.List;

public class DefaultAppUtils {
    public static String getKeyTypeString(int i) {
        return DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY[i];
    }

    public static int getKeyTypeInt(String str) {
        String[] strArr = DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY;
        for (int i = 0; i < strArr.length; i++) {
            if (strArr[i].equals(str)) {
                return i;
            }
        }
        return 0;
    }

    public static DefaultAppTypeInfo create(Context context, String str) {
        String[] strArr = DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY;
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= strArr.length) {
                break;
            } else if (strArr[i2].equals(str)) {
                i = i2;
                break;
            } else {
                i2++;
            }
        }
        return create(context, i);
    }

    public static DefaultAppTypeInfo create(Context context, int i) {
        if (i == 0) {
            return new DefaultAppTypeCamera();
        }
        if (i == 1) {
            return new DefaultAppTypeGallery();
        }
        if (i == 2) {
            return new DefaultAppTypeMusic();
        }
        if (i != 3) {
            return null;
        }
        return new DefaultAppTypeEmail();
    }

    public static String[] getDefaultAppValueList() {
        if (!OPUtils.isO2()) {
            return DefaultAppConstants.DEFAULTAPP_VALUE_LIST_H2OS;
        }
        return DefaultAppConstants.DEFAULTAPP_VALUE_LIST_O2OS;
    }

    public static String getSystemDefaultPackageName(Context context, String str) {
        String[] strArr = DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY;
        String[] defaultAppValueList = getDefaultAppValueList();
        for (int i = 0; i < strArr.length; i++) {
            if (strArr[i].equals(str)) {
                return defaultAppValueList[i];
            }
        }
        return null;
    }

    public static String getDefaultAppPackageName(Context context, String str) {
        String defaultAppPackageName = DataHelper.getDefaultAppPackageName(context, str);
        boolean isAppExist = isAppExist(context, defaultAppPackageName);
        if (TextUtils.isEmpty(defaultAppPackageName) || !isAppExist) {
            return null;
        }
        return defaultAppPackageName;
    }

    public static boolean isAppExist(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(str, 128);
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static void resetDefaultApp(Context context, String str) {
        DefaultAppLogic instance = DefaultAppLogic.getInstance(context);
        List<DefaultAppActivityInfo> appInfoList = instance.getAppInfoList(str);
        List<String> appPackageNameList = instance.getAppPackageNameList(str, appInfoList);
        instance.setDefaultAppPosition(str, appInfoList, appPackageNameList, instance.getDefaultAppPosition(str, appPackageNameList, getSystemDefaultPackageName(context, str)));
    }
}
