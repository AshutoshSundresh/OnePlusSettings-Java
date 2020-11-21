package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;

public class MasterSwitchPreference extends RestrictedPreference {
    private boolean mChecked;
    private boolean mCheckedSet;
    private boolean mEnableSwitch = true;
    private SwitchCompat mSwitch;

    public MasterSwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(C0012R$layout.op_preference_two_target);
    }

    public MasterSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(C0012R$layout.op_preference_two_target);
    }

    public MasterSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.op_preference_two_target);
    }

    public MasterSwitchPreference(Context context) {
        super(context);
        setLayoutResource(C0012R$layout.op_preference_two_target);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference
    public int getSecondTargetResId() {
        return C0012R$layout.restricted_preference_widget_master_switch;
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.switchWidget);
        if (findViewById != null) {
            findViewById.setVisibility(isDisabledByAdmin() ? 8 : 0);
            findViewById.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.widget.MasterSwitchPreference.AnonymousClass1 */

                public void onClick(View view) {
                    if (MasterSwitchPreference.this.mSwitch == null || MasterSwitchPreference.this.mSwitch.isEnabled()) {
                        MasterSwitchPreference masterSwitchPreference = MasterSwitchPreference.this;
                        masterSwitchPreference.setChecked(!masterSwitchPreference.mChecked);
                        MasterSwitchPreference masterSwitchPreference2 = MasterSwitchPreference.this;
                        if (!masterSwitchPreference2.callChangeListener(Boolean.valueOf(masterSwitchPreference2.mChecked))) {
                            MasterSwitchPreference masterSwitchPreference3 = MasterSwitchPreference.this;
                            masterSwitchPreference3.setChecked(!masterSwitchPreference3.mChecked);
                            return;
                        }
                        MasterSwitchPreference masterSwitchPreference4 = MasterSwitchPreference.this;
                        masterSwitchPreference4.persistBoolean(masterSwitchPreference4.mChecked);
                    }
                }
            });
            findViewById.setOnTouchListener($$Lambda$MasterSwitchPreference$MOwqTCx9EkHhner_fFxqj10fKpk.INSTANCE);
        }
        SwitchCompat switchCompat = (SwitchCompat) preferenceViewHolder.findViewById(C0010R$id.switchWidget);
        this.mSwitch = switchCompat;
        if (switchCompat != null) {
            switchCompat.setContentDescription(getTitle());
            this.mSwitch.setChecked(this.mChecked);
            this.mSwitch.setEnabled(this.mEnableSwitch);
        }
    }

    static /* synthetic */ boolean lambda$onBindViewHolder$0(View view, MotionEvent motionEvent) {
        return motionEvent.getActionMasked() == 2;
    }

    public boolean isChecked() {
        return this.mSwitch != null && this.mChecked;
    }

    public void setChecked(boolean z) {
        if ((this.mChecked != z) || !this.mCheckedSet) {
            this.mChecked = z;
            this.mCheckedSet = true;
            SwitchCompat switchCompat = this.mSwitch;
            if (switchCompat != null) {
                switchCompat.setChecked(z);
            }
        }
    }

    public void setSwitchEnabled(boolean z) {
        this.mEnableSwitch = z;
        SwitchCompat switchCompat = this.mSwitch;
        if (switchCompat != null) {
            switchCompat.setEnabled(z);
        }
    }

    @Override // com.android.settingslib.RestrictedPreference
    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        super.setDisabledByAdmin(enforcedAdmin);
        setSwitchEnabled(enforcedAdmin == null);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference
    public boolean shouldHideSecondTarget() {
        return getSecondTargetResId() == 0;
    }
}
