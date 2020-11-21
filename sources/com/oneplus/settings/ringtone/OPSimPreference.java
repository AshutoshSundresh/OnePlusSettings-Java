package com.oneplus.settings.ringtone;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.settings.C0018R$style;

public class OPSimPreference extends Preference {
    public OPSimPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public OPSimPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public OPSimPreference(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        ((TextView) view.findViewById(16908310)).setTextAppearance(C0018R$style.OnePlus_TextAppearance_List_Title);
        ((TextView) view.findViewById(16908304)).setTextAppearance(C0018R$style.OnePlus_TextAppearance_List_Summary);
    }
}
