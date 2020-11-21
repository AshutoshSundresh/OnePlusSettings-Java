package com.oneplus.settings.ringtone;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import com.oneplus.settings.utils.OPUtils;

public class OPPaddingPreference extends Preference {
    public OPPaddingPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public OPPaddingPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        view.setPadding(OPUtils.dip2px(view.getContext(), 64.0f), 0, view.getPaddingRight(), 0);
    }
}
