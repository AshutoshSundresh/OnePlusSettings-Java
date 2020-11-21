package com.android.settings.utils;

import android.graphics.drawable.Drawable;
import com.android.settingslib.widget.CandidateInfo;

public class CandidateInfoExtra extends CandidateInfo {
    private final String mKey;
    private final CharSequence mLabel;
    private final CharSequence mSummary;

    @Override // com.android.settingslib.widget.CandidateInfo
    public Drawable loadIcon() {
        return null;
    }

    public CandidateInfoExtra(CharSequence charSequence, CharSequence charSequence2, String str, boolean z) {
        super(z);
        this.mLabel = charSequence;
        this.mSummary = charSequence2;
        this.mKey = str;
    }

    @Override // com.android.settingslib.widget.CandidateInfo
    public CharSequence loadLabel() {
        return this.mLabel;
    }

    public CharSequence loadSummary() {
        return this.mSummary;
    }

    @Override // com.android.settingslib.widget.CandidateInfo
    public String getKey() {
        return this.mKey;
    }
}
