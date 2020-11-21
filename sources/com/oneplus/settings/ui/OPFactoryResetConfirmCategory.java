package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class OPFactoryResetConfirmCategory extends PreferenceCategory implements View.OnClickListener {
    private Button mConfirmButton;
    private int mLayoutResId = C0012R$layout.op_master_clear_preference_list_fragment;
    public OnFactoryResetConfirmListener mOnFactoryResetConfirmListener;

    public interface OnFactoryResetConfirmListener {
        void onFactoryResetConfirmClick();
    }

    public OPFactoryResetConfirmCategory(Context context) {
        super(context);
        initViews(context);
    }

    public OPFactoryResetConfirmCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPFactoryResetConfirmCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(this.mLayoutResId);
    }

    @Override // androidx.preference.PreferenceCategory, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedBelow(false);
        preferenceViewHolder.setDividerAllowedAbove(false);
        Button button = (Button) preferenceViewHolder.findViewById(C0010R$id.execute_master_clear);
        this.mConfirmButton = button;
        button.setEnabled(true);
        this.mConfirmButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        OnFactoryResetConfirmListener onFactoryResetConfirmListener = this.mOnFactoryResetConfirmListener;
        if (onFactoryResetConfirmListener != null) {
            onFactoryResetConfirmListener.onFactoryResetConfirmClick();
        }
    }

    public void setOnFactoryResetConfirmListener(OnFactoryResetConfirmListener onFactoryResetConfirmListener) {
        this.mOnFactoryResetConfirmListener = onFactoryResetConfirmListener;
    }

    public void setConfirmButtonText(int i) {
        this.mConfirmButton.setText(i);
        notifyChanged();
    }
}
