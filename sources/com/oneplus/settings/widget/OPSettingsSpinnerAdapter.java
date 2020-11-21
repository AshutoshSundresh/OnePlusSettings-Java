package com.oneplus.settings.widget;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.android.settings.C0012R$layout;

public class OPSettingsSpinnerAdapter<T> extends ArrayAdapter<T> {
    public OPSettingsSpinnerAdapter(Context context) {
        super(context, C0012R$layout.op_settings_spinner_view);
        setDropDownViewResource(17367049);
    }
}
