package com.android.settings.deviceinfo;

import android.content.Context;
import android.graphics.Color;
import android.util.MathUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0012R$layout;

public class StorageSummaryPreference extends Preference {
    private int mPercent = -1;

    public StorageSummaryPreference(Context context) {
        super(context);
        setLayoutResource(C0012R$layout.storage_summary);
        setEnabled(false);
    }

    public void setPercent(long j, long j2) {
        this.mPercent = MathUtils.constrain((int) ((100 * j) / j2), j > 0 ? 1 : 0, 100);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        ProgressBar progressBar = (ProgressBar) preferenceViewHolder.findViewById(16908301);
        if (this.mPercent != -1) {
            progressBar.setVisibility(0);
            progressBar.setProgress(this.mPercent);
            progressBar.setScaleY(7.0f);
        } else {
            progressBar.setVisibility(8);
        }
        ((TextView) preferenceViewHolder.findViewById(16908304)).setTextColor(Color.parseColor("#8a000000"));
        super.onBindViewHolder(preferenceViewHolder);
    }
}
