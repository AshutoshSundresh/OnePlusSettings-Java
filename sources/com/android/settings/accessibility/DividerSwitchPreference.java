package com.android.settings.accessibility;

import android.content.Context;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;

public final class DividerSwitchPreference extends SwitchPreference {
    private Boolean mDividerAllowBelow;
    private Boolean mDividerAllowedAbove;
    private int mSwitchVisibility = 0;

    public DividerSwitchPreference(Context context) {
        super(context);
        Boolean bool = Boolean.TRUE;
        this.mDividerAllowedAbove = bool;
        this.mDividerAllowBelow = bool;
    }

    @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(this.mDividerAllowedAbove.booleanValue());
        preferenceViewHolder.setDividerAllowedBelow(this.mDividerAllowBelow.booleanValue());
        View findViewById = preferenceViewHolder.itemView.findViewById(16908312);
        if (findViewById != null) {
            findViewById.setVisibility(this.mSwitchVisibility);
        }
    }

    public void setSwitchVisibility(int i) {
        if (this.mSwitchVisibility != i) {
            this.mSwitchVisibility = i;
            notifyChanged();
        }
    }
}
