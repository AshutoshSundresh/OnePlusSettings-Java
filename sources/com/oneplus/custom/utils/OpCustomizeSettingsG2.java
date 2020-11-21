package com.oneplus.custom.utils;

import androidx.constraintlayout.widget.R$styleable;
import com.oneplus.custom.utils.OpCustomizeSettings;

public class OpCustomizeSettingsG2 extends OpCustomizeSettings {
    /* access modifiers changed from: protected */
    @Override // com.oneplus.custom.utils.OpCustomizeSettings
    public OpCustomizeSettings.SW_TYPE getSoftwareType() {
        OpCustomizeSettings.SW_TYPE sw_type = OpCustomizeSettings.SW_TYPE.DEFAULT;
        int swTypeVal = ParamReader.getSwTypeVal();
        if (swTypeVal == 0) {
            return OpCustomizeSettings.SW_TYPE.DEFAULT;
        }
        if (swTypeVal == 1) {
            return OpCustomizeSettings.SW_TYPE.O2;
        }
        if (swTypeVal == 2) {
            return OpCustomizeSettings.SW_TYPE.H2;
        }
        if (swTypeVal == 3) {
            return OpCustomizeSettings.SW_TYPE.IN;
        }
        if (swTypeVal == 4) {
            return OpCustomizeSettings.SW_TYPE.EU;
        }
        switch (swTypeVal) {
            case R$styleable.Constraint_layout_goneMarginRight /* 101 */:
                return OpCustomizeSettings.SW_TYPE.TMO;
            case R$styleable.Constraint_layout_goneMarginStart /* 102 */:
                return OpCustomizeSettings.SW_TYPE.SPRINT;
            case R$styleable.Constraint_layout_goneMarginTop /* 103 */:
                return OpCustomizeSettings.SW_TYPE.VERIZON;
            case R$styleable.Constraint_motionStagger /* 104 */:
                return OpCustomizeSettings.SW_TYPE.ATT;
            case R$styleable.Constraint_pathMotionArc /* 105 */:
                return OpCustomizeSettings.SW_TYPE.C532;
            default:
                return sw_type;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.custom.utils.OpCustomizeSettings
    public OpCustomizeSettings.CUSTOM_TYPE getCustomization() {
        OpCustomizeSettings.CUSTOM_TYPE custom_type = OpCustomizeSettings.CUSTOM_TYPE.NONE;
        int custFlagVal = ParamReader.getCustFlagVal();
        if (custFlagVal == 3) {
            return OpCustomizeSettings.CUSTOM_TYPE.AVG;
        }
        if (custFlagVal == 6) {
            return OpCustomizeSettings.CUSTOM_TYPE.MCL;
        }
        if (custFlagVal == 7) {
            return OpCustomizeSettings.CUSTOM_TYPE.OPR_RETAIL;
        }
        if (custFlagVal != 8) {
            return custom_type;
        }
        return OpCustomizeSettings.CUSTOM_TYPE.CYB;
    }
}
