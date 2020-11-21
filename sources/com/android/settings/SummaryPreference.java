package com.android.settings;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class SummaryPreference extends Preference {
    private String mAmount;
    private boolean mChartEnabled = true;
    private String mEndLabel;
    private float mLeftRatio;
    private float mMiddleRatio;
    private String mStartLabel;
    private String mUnits;

    public SummaryPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.settings_summary_preference);
    }

    public void setAmount(String str) {
        this.mAmount = str;
        if (str != null && this.mUnits != null) {
            setTitle(TextUtils.expandTemplate(getContext().getText(C0017R$string.storage_size_large), this.mAmount, this.mUnits));
        }
    }

    public void setUnits(String str) {
        this.mUnits = str;
        if (this.mAmount != null && str != null) {
            setTitle(TextUtils.expandTemplate(getContext().getText(C0017R$string.storage_size_large), this.mAmount, this.mUnits));
        }
    }

    public void setRatios(float f, float f2, float f3) {
        this.mLeftRatio = f;
        this.mMiddleRatio = f2;
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ProgressBar progressBar = (ProgressBar) preferenceViewHolder.itemView.findViewById(C0010R$id.color_bar);
        if (this.mChartEnabled) {
            progressBar.setVisibility(0);
            int i = (int) (this.mLeftRatio * 100.0f);
            progressBar.setProgress(i);
            progressBar.setSecondaryProgress(i + ((int) (this.mMiddleRatio * 100.0f)));
        } else {
            progressBar.setVisibility(8);
        }
        if (!this.mChartEnabled || (TextUtils.isEmpty(this.mStartLabel) && TextUtils.isEmpty(this.mEndLabel))) {
            preferenceViewHolder.findViewById(C0010R$id.label_bar).setVisibility(8);
            return;
        }
        preferenceViewHolder.findViewById(C0010R$id.label_bar).setVisibility(0);
        ((TextView) preferenceViewHolder.findViewById(16908308)).setText(this.mStartLabel);
        ((TextView) preferenceViewHolder.findViewById(16908309)).setText(this.mEndLabel);
    }
}
