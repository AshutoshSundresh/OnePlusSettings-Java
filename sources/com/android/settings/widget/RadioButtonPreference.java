package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class RadioButtonPreference extends androidx.preference.RadioButtonPreference {
    private View appendix;
    private OnClickListener mListener;

    public interface OnClickListener {
        void onRadioButtonClicked(RadioButtonPreference radioButtonPreference);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mListener = null;
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mListener = null;
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

    public void setAppendixVisibility(int i) {
        View view = this.appendix;
        if (view != null) {
            view.setVisibility(i);
        }
    }
}
