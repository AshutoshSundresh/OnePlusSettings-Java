package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedPreference;

public class OPSeekbarPreferenceCategory extends RestrictedPreference {
    public OPSeekbarPreferenceCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPSeekbarPreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(C0012R$layout.op_seekbar_preference_category);
    }
}
