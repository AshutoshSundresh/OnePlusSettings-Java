package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.android.settings.C0012R$layout;

public class OPReadingModeEffectShow extends Preference {
    private int mLayoutResId = C0012R$layout.op_reading_mode_effect_show;

    public OPReadingModeEffectShow(Context context) {
        super(context);
        initViews(context);
    }

    public OPReadingModeEffectShow(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPReadingModeEffectShow(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(this.mLayoutResId);
    }
}
