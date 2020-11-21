package com.oneplus.settings.ringtone;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import com.android.settings.C0012R$layout;

public class OPPaddingSwitchPreference extends SwitchPreference {
    public OPPaddingSwitchPreference(Context context) {
        super(context);
        setLayoutResource(C0012R$layout.op_switch_preference_material);
        setWidgetLayoutResource(C0012R$layout.op_switch_preference_widget);
    }

    public OPPaddingSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.op_switch_preference_material);
        setWidgetLayoutResource(C0012R$layout.op_switch_preference_widget);
    }

    public OPPaddingSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(C0012R$layout.op_switch_preference_material);
        setWidgetLayoutResource(C0012R$layout.op_switch_preference_widget);
    }
}
