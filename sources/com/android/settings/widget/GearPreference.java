package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedPreference;

public class GearPreference extends RestrictedPreference implements View.OnClickListener {
    private OnGearClickListener mOnGearClickListener;

    public interface OnGearClickListener {
        void onGearClick(GearPreference gearPreference);
    }

    public GearPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setOnGearClickListener(OnGearClickListener onGearClickListener) {
        this.mOnGearClickListener = onGearClickListener;
        notifyChanged();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference
    public int getSecondTargetResId() {
        return C0012R$layout.preference_widget_gear;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference
    public boolean shouldHideSecondTarget() {
        return this.mOnGearClickListener == null;
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.settings_button);
        if (this.mOnGearClickListener != null) {
            findViewById.setVisibility(0);
            findViewById.setOnClickListener(this);
        } else {
            findViewById.setVisibility(8);
            findViewById.setOnClickListener(null);
        }
        findViewById.setEnabled(true);
    }

    public void onClick(View view) {
        OnGearClickListener onGearClickListener;
        if (view.getId() == C0010R$id.settings_button && (onGearClickListener = this.mOnGearClickListener) != null) {
            onGearClickListener.onGearClick(this);
        }
    }
}
