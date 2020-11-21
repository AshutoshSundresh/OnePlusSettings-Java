package com.android.settingslib.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class RadioButtonPreference extends CheckBoxPreference {
    private TextView mAppendix;
    private int mAppendixVisibility = -1;
    private OnClickListener mListener = null;

    public interface OnClickListener {
        void onRadioButtonClicked(RadioButtonPreference radioButtonPreference);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    @Override // androidx.preference.TwoStatePreference, androidx.preference.Preference
    public void onClick() {
        OnClickListener onClickListener = this.mListener;
        if (onClickListener != null) {
            onClickListener.onRadioButtonClicked(this);
        }
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.summary_container);
        if (findViewById != null) {
            findViewById.setVisibility(TextUtils.isEmpty(getSummary()) ? 8 : 0);
            TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.appendix);
            this.mAppendix = textView;
            if (textView != null) {
                int i = this.mAppendixVisibility;
                if (i != -1) {
                    textView.setVisibility(i);
                } else if (TextUtils.isEmpty(getAppendix())) {
                    this.mAppendix.setVisibility(8);
                }
            }
        }
        TextView textView2 = (TextView) preferenceViewHolder.findViewById(16908310);
        if (textView2 != null) {
            textView2.setSingleLine(false);
            textView2.setMaxLines(3);
        }
    }

    public CharSequence getAppendix() {
        TextView textView = this.mAppendix;
        if (textView != null) {
            return textView.getText();
        }
        return null;
    }

    private void init() {
        setWidgetLayoutResource(C0012R$layout.preference_widget_radiobutton);
        setLayoutResource(C0012R$layout.op_preference_radio);
        setIconSpaceReserved(false);
    }
}
