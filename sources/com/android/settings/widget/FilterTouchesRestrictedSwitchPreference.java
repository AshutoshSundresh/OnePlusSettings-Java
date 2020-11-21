package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.RestrictedSwitchPreference;

public class FilterTouchesRestrictedSwitchPreference extends RestrictedSwitchPreference {
    public FilterTouchesRestrictedSwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public FilterTouchesRestrictedSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public FilterTouchesRestrictedSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FilterTouchesRestrictedSwitchPreference(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.RestrictedSwitchPreference, androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908352);
        if (findViewById != null) {
            findViewById.setFilterTouchesWhenObscured(true);
        }
    }
}
