package com.oneplus.security.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.text.TextUtils;
import android.util.OpFeatures;
import com.oneplus.common.ReflectUtil;

public class FunctionUtils {
    public static Boolean isUstModeEnabled;

    public static boolean isH2OS() {
        return OpFeatures.isSupport(new int[]{0});
    }

    public static boolean isSupportUstMode() {
        Boolean bool = isUstModeEnabled;
        if (bool != null) {
            return bool.booleanValue();
        }
        Boolean valueOf = Boolean.valueOf(ReflectUtil.isFeatureSupported("OP_FEATURE_UST_MODE"));
        isUstModeEnabled = valueOf;
        return valueOf.booleanValue();
    }

    public static boolean checkProviderPermission(PackageManager packageManager, String str, int i) {
        if (i == Process.myPid()) {
            return true;
        }
        return isSystemApplication(packageManager, str);
    }

    public static boolean isSystemApplication(PackageManager packageManager, String str) {
        if (packageManager != null && !TextUtils.isEmpty(str)) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
                if (applicationInfo == null || (applicationInfo.flags & 1) <= 0) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isSupportTransparent(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        ActivityInfo activityInfo = packageManager.resolveActivity(intent, 0).activityInfo;
        if (activityInfo == null) {
            return false;
        }
        String str = activityInfo.packageName;
        LogUtils.i("FunctionUtils", "isSupportTransparent pkgName = " + str);
        if (str == null || !str.equals("net.oneplus.launcher")) {
            return false;
        }
        return true;
    }

    public static boolean isUsvMode() {
        return ReflectUtil.isFeatureSupported("OP_FEATURE_USV_MODE");
    }
}
