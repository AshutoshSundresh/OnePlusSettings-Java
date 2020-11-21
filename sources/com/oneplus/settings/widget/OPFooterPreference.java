package com.oneplus.settings.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.android.settingslib.widget.FooterPreference;

public class OPFooterPreference extends FooterPreference {
    public OPFooterPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public OPFooterPreference(Context context) {
        this(context, null);
    }

    private void init() {
        setIcon((Drawable) null);
    }
}
