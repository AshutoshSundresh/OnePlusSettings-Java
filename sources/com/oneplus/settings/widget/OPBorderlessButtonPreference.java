package com.oneplus.settings.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedPreference;

public class OPBorderlessButtonPreference extends RestrictedPreference {
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        /* class com.oneplus.settings.widget.OPBorderlessButtonPreference.AnonymousClass1 */

        @SuppressLint({"RestrictedApi"})
        public void onClick(View view) {
            OPBorderlessButtonPreference.this.performClick();
        }
    };

    public OPBorderlessButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    public OPBorderlessButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public OPBorderlessButtonPreference(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setLayoutResource(C0012R$layout.op_boderless_button_preference);
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((TextView) preferenceViewHolder.findViewById(16908310)).setOnClickListener(this.onClickListener);
        preferenceViewHolder.itemView.setOnClickListener(null);
    }
}
