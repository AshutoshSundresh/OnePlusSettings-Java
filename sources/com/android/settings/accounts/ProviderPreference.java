package com.android.settings.accounts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;

public class ProviderPreference extends RestrictedPreference {
    private String mAccountType;

    public ProviderPreference(Context context, String str, Drawable drawable, CharSequence charSequence) {
        super(context);
        setIconSize(1);
        this.mAccountType = str;
        setIcon(drawable);
        setPersistent(false);
        setTitle(charSequence);
        useAdminDisabledSummary(true);
    }

    public String getAccountType() {
        return this.mAccountType;
    }

    public void checkAccountManagementAndSetDisabled(int i) {
        setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfAccountManagementDisabled(getContext(), getAccountType(), i));
    }
}
