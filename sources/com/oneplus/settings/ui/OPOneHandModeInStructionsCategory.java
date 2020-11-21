package com.oneplus.settings.ui;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class OPOneHandModeInStructionsCategory extends Preference {
    private int mLayoutResId = C0012R$layout.op_one_hand_mode_instructions_category;

    public OPOneHandModeInStructionsCategory(Context context) {
        super(context);
        initViews(context);
    }

    public OPOneHandModeInStructionsCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPOneHandModeInStructionsCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(this.mLayoutResId);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedBelow(true);
        LottieAnimationView lottieAnimationView = (LottieAnimationView) preferenceViewHolder.findViewById(C0010R$id.animation_view);
        if (lottieAnimationView == null) {
            return;
        }
        if (Settings.System.getInt(getContext().getContentResolver(), "oem_black_mode", 2) == 1) {
            lottieAnimationView.setAnimation("one_hand_mode_dark.json");
        } else {
            lottieAnimationView.setAnimation("one_hand_mode_light.json");
        }
    }
}
