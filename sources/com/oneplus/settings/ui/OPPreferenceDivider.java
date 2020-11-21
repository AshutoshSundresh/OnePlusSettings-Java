package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0012R$layout;

public class OPPreferenceDivider extends Preference {
    public OPPreferenceDivider(Context context) {
        super(context);
        initViews(context);
    }

    public OPPreferenceDivider(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPPreferenceDivider(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(C0012R$layout.op_preference_divider);
        setEnabled(false);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedBelow(false);
        preferenceViewHolder.setDividerAllowedAbove(false);
    }
}
