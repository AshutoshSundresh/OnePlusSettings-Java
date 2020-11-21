package com.oneplus.compat.os;

import android.os.Build;
import com.oneplus.compat.exception.OPRuntimeException;
import com.oneplus.inner.os.SystemPropertiesWrapper;
import com.oneplus.utils.Utils;
import com.oneplus.utils.reflection.ClassReflection;
import com.oneplus.utils.reflection.MethodReflection;

public class SystemPropertiesNative {
    public static String get(String str) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 29 && Utils.isWrapperSupport()) {
            return SystemPropertiesWrapper.get(str);
        }
        if ((i >= 29 && !Utils.isWrapperSupport()) || i == 28 || i == 26) {
            return (String) MethodReflection.invokeMethod(MethodReflection.findMethod(ClassReflection.findClass("android.os.SystemProperties"), "get", String.class), null, str);
        }
        throw new OPRuntimeException("not Supported");
    }
}
