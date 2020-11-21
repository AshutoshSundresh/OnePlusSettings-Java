package com.oneplus.settings.timer.timepower;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class TimepowerPreference extends Preference {
    private View.OnClickListener mSettingsViewClicklistener;

    public void setViewClickListener(View.OnClickListener onClickListener) {
        this.mSettingsViewClicklistener = onClickListener;
    }

    public TimepowerPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(C0012R$layout.op_timepower_preference_layout);
    }

    public TimepowerPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842894);
    }

    public TimepowerPreference(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.findViewById(C0010R$id.time_power_pref).setClickable(false);
        preferenceViewHolder.findViewById(C0010R$id.time_power_settings).setOnClickListener(this.mSettingsViewClicklistener);
    }
}
