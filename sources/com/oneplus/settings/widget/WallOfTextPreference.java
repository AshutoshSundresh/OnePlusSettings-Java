package com.oneplus.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;

public class WallOfTextPreference extends DividerPreference {
    public WallOfTextPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.oneplus.settings.widget.DividerPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((TextView) preferenceViewHolder.findViewById(16908304)).setMaxLines(20);
    }
}
