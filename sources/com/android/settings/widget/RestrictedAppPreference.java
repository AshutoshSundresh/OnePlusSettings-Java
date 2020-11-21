package com.android.settings.widget;

import android.content.Context;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedPreferenceHelper;
import com.android.settingslib.widget.apppreference.AppPreference;

public class RestrictedAppPreference extends AppPreference {
    private RestrictedPreferenceHelper mHelper;
    private String userRestriction;

    public RestrictedAppPreference(Context context, String str) {
        super(context);
        initialize(null, str);
    }

    private void initialize(AttributeSet attributeSet, String str) {
        setWidgetLayoutResource(C0012R$layout.restricted_icon);
        this.mHelper = new RestrictedPreferenceHelper(getContext(), this, attributeSet);
        this.userRestriction = str;
    }

    @Override // com.android.settingslib.widget.apppreference.AppPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.restricted_icon);
        if (findViewById != null) {
            findViewById.setVisibility(isDisabledByAdmin() ? 0 : 8);
        }
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (!this.mHelper.performClick()) {
            super.performClick();
        }
    }

    @Override // androidx.preference.Preference
    public void setEnabled(boolean z) {
        if (!isDisabledByAdmin() || !z) {
            super.setEnabled(z);
        }
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        this.mHelper.onAttachedToHierarchy();
        super.onAttachedToHierarchy(preferenceManager);
    }

    public void checkRestrictionAndSetDisabled() {
        if (!TextUtils.isEmpty(this.userRestriction)) {
            this.mHelper.checkRestrictionAndSetDisabled(this.userRestriction, UserHandle.myUserId());
        }
    }
}
