package com.android.settings.notification;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.TwoTargetPreference;

public class OPZenNoDividerCustomRadioButtonPreference extends TwoTargetPreference implements View.OnClickListener {
    private RadioButton mButton;
    private boolean mChecked;
    private OnGearClickListener mOnGearClickListener;
    private OnRadioButtonClickListener mOnRadioButtonClickListener;

    public interface OnGearClickListener {
        void onGearClick(OPZenNoDividerCustomRadioButtonPreference oPZenNoDividerCustomRadioButtonPreference);
    }

    public interface OnRadioButtonClickListener {
        void onRadioButtonClick(OPZenNoDividerCustomRadioButtonPreference oPZenNoDividerCustomRadioButtonPreference);
    }

    public OPZenNoDividerCustomRadioButtonPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(C0012R$layout.op_preference_no_divivider_two_target_radio);
    }

    public OPZenNoDividerCustomRadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(C0012R$layout.op_preference_no_divivider_two_target_radio);
    }

    public OPZenNoDividerCustomRadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.op_preference_no_divivider_two_target_radio);
    }

    public OPZenNoDividerCustomRadioButtonPreference(Context context) {
        super(context);
        setLayoutResource(C0012R$layout.op_preference_no_divivider_two_target_radio);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference
    public int getSecondTargetResId() {
        return C0012R$layout.preference_widget_gear;
    }

    public void setOnRadioButtonClickListener(OnRadioButtonClickListener onRadioButtonClickListener) {
        this.mOnRadioButtonClickListener = onRadioButtonClickListener;
        notifyChanged();
    }

    @Override // com.android.settingslib.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.checkbox_frame);
        if (findViewById != null) {
            findViewById.setOnClickListener(this);
        }
        RadioButton radioButton = (RadioButton) preferenceViewHolder.findViewById(16908289);
        this.mButton = radioButton;
        if (radioButton != null) {
            radioButton.setChecked(this.mChecked);
        }
        View findViewById2 = preferenceViewHolder.findViewById(C0010R$id.preference);
        if (this.mOnGearClickListener != null) {
            findViewById2.setOnClickListener(this);
        }
    }

    public void setChecked(boolean z) {
        this.mChecked = z;
        RadioButton radioButton = this.mButton;
        if (radioButton != null) {
            radioButton.setChecked(z);
        }
    }

    @Override // androidx.preference.Preference
    public void onClick() {
        OnRadioButtonClickListener onRadioButtonClickListener = this.mOnRadioButtonClickListener;
        if (onRadioButtonClickListener != null) {
            onRadioButtonClickListener.onRadioButtonClick(this);
        }
    }

    public void onClick(View view) {
        OnRadioButtonClickListener onRadioButtonClickListener;
        if (view.getId() == 16908312) {
            OnGearClickListener onGearClickListener = this.mOnGearClickListener;
            if (onGearClickListener != null) {
                onGearClickListener.onGearClick(this);
            }
        } else if (view.getId() == C0010R$id.checkbox_frame && (onRadioButtonClickListener = this.mOnRadioButtonClickListener) != null) {
            onRadioButtonClickListener.onRadioButtonClick(this);
        }
    }
}
