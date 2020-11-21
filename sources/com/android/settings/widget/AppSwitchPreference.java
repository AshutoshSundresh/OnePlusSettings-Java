package com.android.settings.widget;

import android.content.Context;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import com.android.settings.C0012R$layout;

public class AppSwitchPreference extends SwitchPreference {
    public AppSwitchPreference(Context context) {
        super(context);
        setLayoutResource(C0012R$layout.preference_app);
    }

    @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908352);
        if (findViewById != null) {
            findViewById.setFilterTouchesWhenObscured(true);
        }
    }
}
