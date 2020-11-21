package com.oneplus.utils;

import com.oneplus.utils.reflection.ClassReflection;
import com.oneplus.utils.reflection.MethodReflection;

public class Utils {
    private static String sCurrentVersion = null;
    private static String sDefaultWrapper = "10.11.0";
    private static boolean sIsWrapperChecked = false;
    private static boolean sIsWrapperExist = false;

    public static boolean isWrapperSupport() {
        if (sIsWrapperChecked) {
            return sIsWrapperExist;
        }
        boolean isWrapperSupport = isWrapperSupport(sDefaultWrapper);
        sIsWrapperExist = isWrapperSupport;
        sIsWrapperChecked = true;
        return isWrapperSupport;
    }

    public static boolean isWrapperSupport(String str) {
        Class findClass = ClassReflection.findClass("android.os.SystemProperties");
        if (sCurrentVersion == null) {
            sCurrentVersion = (String) MethodReflection.invokeMethod(MethodReflection.findMethod(findClass, "get", String.class), null, "ro.sys.oneplus.support");
        }
        if (sCurrentVersion == "") {
            return false;
        }
        try {
            String[] split = str.split("\\.");
            String[] split2 = sCurrentVersion.split("\\.");
            int i = 0;
            while (true) {
                if (i >= split.length) {
                    if (i >= split2.length) {
                        return true;
                    }
                }
                if (i >= split.length) {
                    if (i >= split2.length) {
                        if (i < split.length) {
                            return false;
                        }
                        if (i < split2.length) {
                            return true;
                        }
                        i++;
                    }
                }
                if (Integer.parseInt(split2[i]) < Integer.parseInt(split[i])) {
                    return false;
                }
                if (Integer.parseInt(split2[i]) > Integer.parseInt(split[i])) {
                    return true;
                }
                i++;
            }
        } catch (Exception unused) {
            return false;
        }
    }
}
