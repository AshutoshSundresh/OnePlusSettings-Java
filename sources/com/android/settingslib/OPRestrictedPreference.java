package com.android.settingslib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0004R$attr;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class OPRestrictedPreference extends TwoTargetPreference {
    private View mDivider;
    RestrictedPreferenceHelper mHelper;
    private boolean mHideDivider;

    public OPRestrictedPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mHideDivider = false;
        setLayoutResource(C0012R$layout.op_preference_two_target_layout_center);
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
        setIconSpaceReserved(false);
    }

    public OPRestrictedPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
        setIconSpaceReserved(false);
    }

    public OPRestrictedPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, C0004R$attr.preferenceStyle, 16842894));
        setIconSpaceReserved(false);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference
    public int getSecondTargetResId() {
        return C0012R$layout.restricted_icon;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference
    public boolean shouldHideSecondTarget() {
        return !isDisabledByAdmin();
    }

    @Override // com.android.settingslib.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.restricted_icon);
        if (findViewById != null) {
            findViewById.setVisibility(isDisabledByAdmin() ? 0 : 8);
        }
        View findViewById2 = preferenceViewHolder.findViewById(C0010R$id.view_divider);
        this.mDivider = findViewById2;
        if (!this.mHideDivider || findViewById2 == null) {
            this.mDivider.setVisibility(0);
        } else {
            findViewById2.setVisibility(4);
        }
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (!this.mHelper.performClick()) {
            super.performClick();
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        this.mHelper.onAttachedToHierarchy();
        super.onAttachedToHierarchy(preferenceManager);
    }

    @Override // androidx.preference.Preference
    public void setEnabled(boolean z) {
        if (!z || !isDisabledByAdmin()) {
            super.setEnabled(z);
        } else {
            this.mHelper.setDisabledByAdmin(null);
        }
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }
}
