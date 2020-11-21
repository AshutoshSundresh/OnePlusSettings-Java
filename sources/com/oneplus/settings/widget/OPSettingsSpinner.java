package com.oneplus.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;
import com.android.settings.C0008R$drawable;

public class OPSettingsSpinner extends Spinner {
    public OPSettingsSpinner(Context context) {
        super(context);
        setBackgroundResource(C0008R$drawable.op_settings_spinner_background);
    }

    public OPSettingsSpinner(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(C0008R$drawable.op_settings_spinner_background);
    }

    public OPSettingsSpinner(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setBackgroundResource(C0008R$drawable.op_settings_spinner_background);
    }
}
