package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0012R$layout;

public class OPPreferenceDividerMarginStart extends Preference {
    public OPPreferenceDividerMarginStart(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPPreferenceDividerMarginStart(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(C0012R$layout.op_preference_divider_margin_start);
        setEnabled(false);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedBelow(false);
        preferenceViewHolder.setDividerAllowedAbove(false);
    }
}
