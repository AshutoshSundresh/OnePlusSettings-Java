package com.oneplus.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$styleable;

public class DividerPreference extends Preference {
    private Boolean mAllowAbove;
    private Boolean mAllowBelow;

    public DividerPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.DividerPreference, 0, 0);
        if (obtainStyledAttributes.hasValue(R$styleable.DividerPreference_allowDividerAbove)) {
            this.mAllowAbove = Boolean.valueOf(obtainStyledAttributes.getBoolean(R$styleable.DividerPreference_allowDividerAbove, false));
        }
        if (obtainStyledAttributes.hasValue(R$styleable.DividerPreference_allowDividerBelow)) {
            this.mAllowBelow = Boolean.valueOf(obtainStyledAttributes.getBoolean(R$styleable.DividerPreference_allowDividerBelow, false));
        }
        obtainStyledAttributes.recycle();
    }

    public DividerPreference(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Boolean bool = this.mAllowAbove;
        if (bool != null) {
            preferenceViewHolder.setDividerAllowedAbove(bool.booleanValue());
        }
        Boolean bool2 = this.mAllowBelow;
        if (bool2 != null) {
            preferenceViewHolder.setDividerAllowedBelow(bool2.booleanValue());
        }
    }
}
