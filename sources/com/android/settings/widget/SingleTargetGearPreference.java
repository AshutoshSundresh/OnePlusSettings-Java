package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0012R$layout;
import com.android.settingslib.R$id;

public class SingleTargetGearPreference extends Preference {
    public SingleTargetGearPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    public SingleTargetGearPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public SingleTargetGearPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        setLayoutResource(C0012R$layout.preference_single_target);
        setWidgetLayoutResource(C0012R$layout.preference_widget_gear_optional_background);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(R$id.two_target_divider);
        if (findViewById != null) {
            findViewById.setVisibility(4);
        }
    }
}
