package com.android.settings.accessibility;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class DividerAllowedBelowPreference extends Preference {
    public DividerAllowedBelowPreference(Context context) {
        super(context);
    }

    public DividerAllowedBelowPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DividerAllowedBelowPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedBelow(true);
    }
}
