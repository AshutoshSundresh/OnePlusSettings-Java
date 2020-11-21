package com.android.settings;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.PreferenceCategory;

public abstract class ProgressCategoryBase extends PreferenceCategory {
    public ProgressCategoryBase(Context context) {
        super(context);
    }

    public ProgressCategoryBase(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ProgressCategoryBase(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public ProgressCategoryBase(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }
}
