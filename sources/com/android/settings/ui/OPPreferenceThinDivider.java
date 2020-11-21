package com.android.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0012R$layout;

public class OPPreferenceThinDivider extends PreferenceCategory {
    public OPPreferenceThinDivider(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPPreferenceThinDivider(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(C0012R$layout.op_preference_thindivider);
    }
}
