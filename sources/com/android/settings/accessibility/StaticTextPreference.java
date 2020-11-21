package com.android.settings.accessibility;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0012R$layout;

public class StaticTextPreference extends Preference {
    StaticTextPreference(Context context) {
        super(context);
        setLayoutResource(C0012R$layout.preference_static_text);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
    }
}
