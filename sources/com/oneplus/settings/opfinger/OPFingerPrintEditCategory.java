package com.oneplus.settings.opfinger;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class OPFingerPrintEditCategory extends Preference {
    private CharSequence mFingerprintName;
    private TextView mFingerprintNameView;
    private int mLayoutResId = C0012R$layout.op_fingerprint_edit_category;

    public OPFingerPrintEditCategory(Context context) {
        super(context);
        initViews(context);
    }

    public OPFingerPrintEditCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPFingerPrintEditCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(this.mLayoutResId);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.op_fingerprint_name);
        this.mFingerprintNameView = textView;
        textView.setText(this.mFingerprintName);
        preferenceViewHolder.setDividerAllowedBelow(false);
    }

    public void setFingerprintName(CharSequence charSequence) {
        this.mFingerprintName = charSequence;
        TextView textView = this.mFingerprintNameView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }
}
