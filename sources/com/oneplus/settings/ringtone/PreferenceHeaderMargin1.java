package com.oneplus.settings.ringtone;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import com.android.settings.C0012R$layout;

public class PreferenceHeaderMargin1 extends Preference {
    public PreferenceHeaderMargin1(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews();
    }

    public PreferenceHeaderMargin1(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews();
    }

    private void initViews() {
        setLayoutResource(C0012R$layout.op_preference_header_margin1);
        setEnabled(false);
    }
}
