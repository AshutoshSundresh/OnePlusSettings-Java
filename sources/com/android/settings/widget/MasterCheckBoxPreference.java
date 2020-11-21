package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.TwoTargetPreference;

public class MasterCheckBoxPreference extends TwoTargetPreference {
    private CheckBox mCheckBox;
    private boolean mChecked;
    private boolean mEnableCheckBox = true;

    public MasterCheckBoxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public MasterCheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public MasterCheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference
    public int getSecondTargetResId() {
        return C0012R$layout.preference_widget_master_checkbox;
    }

    @Override // com.android.settingslib.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908312);
        if (findViewById != null) {
            findViewById.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.widget.MasterCheckBoxPreference.AnonymousClass1 */

                public void onClick(View view) {
                    if (MasterCheckBoxPreference.this.mCheckBox == null || MasterCheckBoxPreference.this.mCheckBox.isEnabled()) {
                        MasterCheckBoxPreference masterCheckBoxPreference = MasterCheckBoxPreference.this;
                        masterCheckBoxPreference.setChecked(!masterCheckBoxPreference.mChecked);
                        MasterCheckBoxPreference masterCheckBoxPreference2 = MasterCheckBoxPreference.this;
                        if (!masterCheckBoxPreference2.callChangeListener(Boolean.valueOf(masterCheckBoxPreference2.mChecked))) {
                            MasterCheckBoxPreference masterCheckBoxPreference3 = MasterCheckBoxPreference.this;
                            masterCheckBoxPreference3.setChecked(!masterCheckBoxPreference3.mChecked);
                            return;
                        }
                        MasterCheckBoxPreference masterCheckBoxPreference4 = MasterCheckBoxPreference.this;
                        masterCheckBoxPreference4.persistBoolean(masterCheckBoxPreference4.mChecked);
                    }
                }
            });
        }
        CheckBox checkBox = (CheckBox) preferenceViewHolder.findViewById(C0010R$id.checkboxWidget);
        this.mCheckBox = checkBox;
        if (checkBox != null) {
            checkBox.setContentDescription(getTitle());
            this.mCheckBox.setChecked(this.mChecked);
            this.mCheckBox.setEnabled(this.mEnableCheckBox);
        }
    }

    @Override // androidx.preference.Preference
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        setCheckBoxEnabled(z);
    }

    public void setChecked(boolean z) {
        this.mChecked = z;
        CheckBox checkBox = this.mCheckBox;
        if (checkBox != null) {
            checkBox.setChecked(z);
        }
    }

    public void setCheckBoxEnabled(boolean z) {
        this.mEnableCheckBox = z;
        CheckBox checkBox = this.mCheckBox;
        if (checkBox != null) {
            checkBox.setEnabled(z);
        }
    }
}
