package com.android.settingslib.widget.settingsspinner;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;
import com.android.settingslib.widget.R$drawable;

public class SettingsSpinner extends Spinner {
    public SettingsSpinner(Context context) {
        super(context);
        setBackgroundResource(R$drawable.settings_spinner_background);
    }

    public SettingsSpinner(Context context, int i) {
        super(context, i);
        setBackgroundResource(R$drawable.settings_spinner_background);
    }

    public SettingsSpinner(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(R$drawable.settings_spinner_background);
    }

    public SettingsSpinner(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setBackgroundResource(R$drawable.settings_spinner_background);
    }

    public SettingsSpinner(Context context, AttributeSet attributeSet, int i, int i2, int i3) {
        super(context, attributeSet, i, i2, i3, null);
    }
}
