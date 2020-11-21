package com.android.settings.enterprise;

import android.content.Context;

public class FailedPasswordWipeManagedProfilePreferenceController extends FailedPasswordWipePreferenceControllerBase {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "failed_password_wipe_managed_profile";
    }

    public FailedPasswordWipeManagedProfilePreferenceController(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.enterprise.FailedPasswordWipePreferenceControllerBase
    public int getMaximumFailedPasswordsBeforeWipe() {
        return this.mFeatureProvider.getMaximumFailedPasswordsBeforeWipeInManagedProfile();
    }
}
