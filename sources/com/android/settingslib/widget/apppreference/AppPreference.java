package com.android.settingslib.widget.apppreference;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.widget.R$layout;

public class AppPreference extends Preference {
    private int mProgress;
    private boolean mProgressVisible;

    public AppPreference(Context context) {
        super(context);
        setLayoutResource(R$layout.preference_app);
    }

    public AppPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R$layout.preference_app);
    }

    public void setProgress(int i) {
        this.mProgress = i;
        this.mProgressVisible = true;
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ProgressBar progressBar = (ProgressBar) preferenceViewHolder.findViewById(16908301);
        if (this.mProgressVisible) {
            progressBar.setProgress(this.mProgress);
            progressBar.setVisibility(0);
            return;
        }
        progressBar.setVisibility(8);
    }
}
