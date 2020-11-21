package com.oneplus.settings.ringtone;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import com.android.settings.C0012R$layout;

public class OPRadioButtonPreference extends CheckBoxPreference {
    /* access modifiers changed from: protected */
    public void onClick() {
    }

    public OPRadioButtonPreference(Context context) {
        super(context);
        initViews();
    }

    public OPRadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews();
    }

    public OPRadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews();
    }

    private void initViews() {
        setWidgetLayoutResource(C0012R$layout.preference_widget_radiobutton);
        setLayoutResource(C0012R$layout.op_preference_radio);
    }
}
