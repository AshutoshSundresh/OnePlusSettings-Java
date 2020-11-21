package com.oneplus.settings.system;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.android.settings.C0012R$layout;

public class RamBoostLottieAnimPreference extends Preference {
    private int resId = C0012R$layout.op_preference_ramboost_lottie;

    public RamBoostLottieAnimPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews();
    }

    public RamBoostLottieAnimPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews();
    }

    public RamBoostLottieAnimPreference(Context context) {
        super(context);
        initViews();
    }

    private void initViews() {
        setLayoutResource(this.resId);
        setSelectable(false);
    }
}
