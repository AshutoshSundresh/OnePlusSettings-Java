package com.android.settings.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class AppPreference extends Preference {
    private int mProgress;
    private boolean mProgressVisible;

    public AppPreference(Context context) {
        super(context);
        setLayoutResource(C0012R$layout.op_preference_app);
    }

    public AppPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.op_preference_app);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.findViewById(C0010R$id.summary_container).setVisibility(TextUtils.isEmpty(getSummary()) ? 8 : 0);
        ProgressBar progressBar = (ProgressBar) preferenceViewHolder.findViewById(16908301);
        if (this.mProgressVisible) {
            progressBar.setProgress(this.mProgress);
            progressBar.setVisibility(0);
            return;
        }
        progressBar.setVisibility(8);
    }
}
