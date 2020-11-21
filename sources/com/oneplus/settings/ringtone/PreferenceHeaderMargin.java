package com.oneplus.settings.ringtone;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import com.android.settings.C0012R$layout;

public class PreferenceHeaderMargin extends Preference {
    public PreferenceHeaderMargin(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews();
    }

    public PreferenceHeaderMargin(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews();
    }

    private void initViews() {
        setLayoutResource(C0012R$layout.op_preference_header_margin);
        setEnabled(false);
    }
}
