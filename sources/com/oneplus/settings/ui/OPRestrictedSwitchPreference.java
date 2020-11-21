package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedSwitchPreference;

public class OPRestrictedSwitchPreference extends RestrictedSwitchPreference {
    public OPRestrictedSwitchPreference(Context context) {
        super(context);
        initViews(context);
    }

    public OPRestrictedSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPRestrictedSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(C0012R$layout.op_preference_material);
        setWidgetLayoutResource(C0012R$layout.op_preference_widget_switch);
    }
}
