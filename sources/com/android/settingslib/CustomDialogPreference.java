package com.android.settingslib;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.DialogPreference;

@Deprecated
public class CustomDialogPreference extends DialogPreference {
    public CustomDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public CustomDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CustomDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
