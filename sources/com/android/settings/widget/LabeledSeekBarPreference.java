package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.R$attr;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.R$styleable;

public class LabeledSeekBarPreference extends SeekBarPreference {
    private Preference.OnPreferenceChangeListener mStopListener;
    private final int mTextEndId;
    private final int mTextStartId;

    public LabeledSeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(C0012R$layout.preference_labeled_slider);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.LabeledSeekBarPreference);
        this.mTextStartId = obtainStyledAttributes.getResourceId(R$styleable.LabeledSeekBarPreference_textStart, C0017R$string.summary_placeholder);
        this.mTextEndId = obtainStyledAttributes.getResourceId(R$styleable.LabeledSeekBarPreference_textEnd, C0017R$string.summary_placeholder);
        obtainStyledAttributes.recycle();
    }

    public LabeledSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.seekBarPreferenceStyle, 17957071), 0);
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settings.widget.SeekBarPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((TextView) preferenceViewHolder.findViewById(16908308)).setText(this.mTextStartId);
        ((TextView) preferenceViewHolder.findViewById(16908309)).setText(this.mTextEndId);
    }

    public void setOnPreferenceChangeStopListener(Preference.OnPreferenceChangeListener onPreferenceChangeListener) {
        this.mStopListener = onPreferenceChangeListener;
    }

    @Override // com.android.settings.widget.SeekBarPreference
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        Preference.OnPreferenceChangeListener onPreferenceChangeListener = this.mStopListener;
        if (onPreferenceChangeListener != null) {
            onPreferenceChangeListener.onPreferenceChange(this, Integer.valueOf(seekBar.getProgress()));
        }
    }
}
