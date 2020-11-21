package com.android.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0012R$layout;

public class OPSuwPreferenceCategory extends PreferenceCategory {
    public OPSuwPreferenceCategory(Context context) {
        super(context);
        initViews(context);
    }

    public OPSuwPreferenceCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPSuwPreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(C0012R$layout.op_suw_preference_category_material);
    }
}
