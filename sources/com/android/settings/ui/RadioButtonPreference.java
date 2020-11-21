package com.android.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0004R$attr;
import com.android.settings.C0012R$layout;
import com.android.settings.C0018R$style;

public class RadioButtonPreference extends CheckBoxPreference {
    private OnClickListener mListener;

    public interface OnClickListener {
        void onRadioButtonClicked(RadioButtonPreference radioButtonPreference);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mListener = null;
        setWidgetLayoutResource(C0012R$layout.preference_widget_radiobutton);
        setLayoutResource(C0012R$layout.op_preference_radio);
        setIconSpaceReserved(true);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, C0004R$attr.checkBoxPreferenceStyle);
    }

    public RadioButtonPreference(Context context) {
        this(context, null);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mListener = onClickListener;
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
        preferenceViewHolder.setDividerAllowedBelow(false);
        preferenceViewHolder.setDividerAllowedAbove(false);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908310);
        if (textView != null) {
            textView.setSingleLine(false);
            textView.setMaxLines(3);
            textView.setTextAppearance(getContext(), C0018R$style.OnePlus_TextAppearance_List_Title);
        }
    }
}
