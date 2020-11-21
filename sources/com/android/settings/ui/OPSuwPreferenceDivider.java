package com.android.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.OpFeatures;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0012R$layout;

public class OPSuwPreferenceDivider extends PreferenceCategory {
    public OPSuwPreferenceDivider(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPSuwPreferenceDivider(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        if (OpFeatures.isSupport(new int[]{1})) {
            setLayoutResource(C0012R$layout.op_suw_preference_divider);
        }
    }
}
