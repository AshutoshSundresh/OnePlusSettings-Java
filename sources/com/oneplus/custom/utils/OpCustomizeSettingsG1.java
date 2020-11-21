package com.oneplus.custom.utils;

import com.oneplus.custom.utils.OpCustomizeSettings;

public class OpCustomizeSettingsG1 extends OpCustomizeSettings {
    /* access modifiers changed from: protected */
    @Override // com.oneplus.custom.utils.OpCustomizeSettings
    public OpCustomizeSettings.CUSTOM_TYPE getCustomization() {
        OpCustomizeSettings.CUSTOM_TYPE custom_type = OpCustomizeSettings.CUSTOM_TYPE.NONE;
        int custFlagVal = ParamReader.getCustFlagVal();
        if (custFlagVal == 1) {
            return OpCustomizeSettings.CUSTOM_TYPE.JCC;
        }
        if (custFlagVal != 2) {
            return custom_type;
        }
        return OpCustomizeSettings.CUSTOM_TYPE.SW;
    }
}
