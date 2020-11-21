package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceViewHolder;
import com.android.internal.R;

public class DisabledCheckBoxPreference extends CheckBoxPreference {
    private View mCheckBox;
    private boolean mEnabledCheckBox;
    private PreferenceViewHolder mViewHolder;

    public DisabledCheckBoxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setupDisabledCheckBoxPreference(context, attributeSet, i, i2);
    }

    public DisabledCheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setupDisabledCheckBoxPreference(context, attributeSet, i, 0);
    }

    public DisabledCheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupDisabledCheckBoxPreference(context, attributeSet, 0, 0);
    }

    public DisabledCheckBoxPreference(Context context) {
        super(context);
        setupDisabledCheckBoxPreference(context, null, 0, 0);
    }

    private void setupDisabledCheckBoxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Preference, i, i2);
        for (int indexCount = obtainStyledAttributes.getIndexCount() - 1; indexCount >= 0; indexCount--) {
            int index = obtainStyledAttributes.getIndex(indexCount);
            if (index == 2) {
                this.mEnabledCheckBox = obtainStyledAttributes.getBoolean(index, true);
            }
        }
        obtainStyledAttributes.recycle();
        super.setEnabled(true);
        enableCheckbox(this.mEnabledCheckBox);
    }

    public void enableCheckbox(boolean z) {
        View view;
        this.mEnabledCheckBox = z;
        if (this.mViewHolder != null && (view = this.mCheckBox) != null) {
            view.setEnabled(z);
            this.mViewHolder.itemView.setEnabled(this.mEnabledCheckBox);
        }
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mViewHolder = preferenceViewHolder;
        this.mCheckBox = preferenceViewHolder.findViewById(16908289);
        enableCheckbox(this.mEnabledCheckBox);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908310);
        if (textView != null) {
            textView.setSingleLine(false);
            textView.setMaxLines(2);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void performClick(View view) {
        if (this.mEnabledCheckBox) {
            super.performClick(view);
        }
    }
}
