package com.oneplus.custom.utils;

import android.os.IBinder;
import java.util.ArrayList;

public class ParamReader {
    public static int getSwTypeVal() {
        int i = 0;
        try {
            Object invoke = Class.forName("android.os.ServiceManager").getMethod("getService", String.class).invoke(null, "ParamService");
            Object invoke2 = Class.forName("com.oneplus.os.IParamService$Stub").getMethod("asInterface", IBinder.class).invoke(null, invoke);
            i = ((Integer) invoke2.getClass().getMethod("getParamIntSYNC", Integer.TYPE).invoke(invoke2, 24)).intValue();
            MyLog.verb("ParamReader", "getSwTypeVal result = " + i);
            return i;
        } catch (Exception e) {
            MyLog.err("ParamReader", "getSwTypeVal throws exception: " + e);
            return i;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x0095 A[SYNTHETIC, Splitter:B:45:0x0095] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00b7 A[SYNTHETIC, Splitter:B:55:0x00b7] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getCustFlagVal() {
        /*
        // Method dump skipped, instructions count: 368
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.custom.utils.ParamReader.getCustFlagVal():int");
    }

    static {
        new ArrayList();
    }
}
