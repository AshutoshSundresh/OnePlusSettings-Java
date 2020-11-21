package com.oneplus.custom.utils;

import android.util.Log;
import com.oneplus.compat.os.SystemPropertiesNative;

public class MyLog {
    protected static final boolean DBG = "true".equals(SystemPropertiesNative.get("persist.sys.assert.panic"));

    protected static void verb(String str, String str2) {
        if (DBG) {
            Log.v(str, str2);
        }
    }

    protected static void warn(String str, String str2) {
        if (DBG) {
            Log.w(str, str2);
        }
    }

    protected static void err(String str, String str2) {
        if (DBG) {
            Log.e(str, str2);
        }
    }
}
