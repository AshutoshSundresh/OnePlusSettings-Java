package com.oneplus.custom.utils;

import com.oneplus.compat.os.SystemPropertiesNative;

public class OpCustomizeSettings {
    private static final String PROJECT_NAME = SystemPropertiesNative.get("ro.boot.project_name");
    private static OpCustomizeSettings sOpCustomizeSettings;

    public enum CUSTOM_TYPE {
        NONE,
        JCC,
        SW,
        AVG,
        MCL,
        OPR_RETAIL,
        CYB
    }

    public enum SW_TYPE {
        DEFAULT,
        O2,
        H2,
        IN,
        EU,
        TMO,
        SPRINT,
        VERIZON,
        ATT,
        C532
    }

    public static SW_TYPE getSwType() {
        return getInstance().getSoftwareType();
    }

    public static CUSTOM_TYPE getCustomType() {
        return getInstance().getCustomization();
    }

    private static OpCustomizeSettings getInstance() {
        if (sOpCustomizeSettings == null) {
            MyLog.verb("OpCustomizeSettings", "PROJECT_NAME = " + PROJECT_NAME);
            if ("16859".equals(PROJECT_NAME) || "17801".equals(PROJECT_NAME)) {
                sOpCustomizeSettings = new OpCustomizeSettingsG1();
            } else if ("15801".equals(PROJECT_NAME) || "15811".equals(PROJECT_NAME)) {
                sOpCustomizeSettings = new OpCustomizeSettings();
            } else {
                sOpCustomizeSettings = new OpCustomizeSettingsG2();
            }
        }
        return sOpCustomizeSettings;
    }

    /* access modifiers changed from: protected */
    public SW_TYPE getSoftwareType() {
        return SW_TYPE.DEFAULT;
    }

    /* access modifiers changed from: protected */
    public CUSTOM_TYPE getCustomization() {
        return CUSTOM_TYPE.NONE;
    }
}
