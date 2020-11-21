package com.oneplus.settings.ui;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import com.android.settings.C0012R$layout;

public class OPPreferenceDivider2 extends PreferenceCategory {
    public OPPreferenceDivider2(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPPreferenceDivider2(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(C0012R$layout.op_preference_divider);
    }
}
