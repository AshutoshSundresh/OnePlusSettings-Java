package com.oneplus.settings.ringtone;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import com.android.settings.C0012R$layout;

public class OPPaddingPreferenceCategory extends PreferenceCategory {
    public OPPaddingPreferenceCategory(Context context) {
        super(context);
        setLayoutResource(C0012R$layout.op_padding_preference_category);
    }

    public OPPaddingPreferenceCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.op_padding_preference_category);
    }

    public OPPaddingPreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(C0012R$layout.op_padding_preference_category);
    }
}
