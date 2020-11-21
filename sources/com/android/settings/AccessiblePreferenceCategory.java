package com.android.settings;

import android.content.Context;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

public class AccessiblePreferenceCategory extends PreferenceCategory {
    private String mContentDescription;

    public AccessiblePreferenceCategory(Context context) {
        super(context);
    }

    public void setContentDescription(String str) {
        this.mContentDescription = str;
    }

    @Override // androidx.preference.PreferenceCategory, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setContentDescription(this.mContentDescription);
    }
}
