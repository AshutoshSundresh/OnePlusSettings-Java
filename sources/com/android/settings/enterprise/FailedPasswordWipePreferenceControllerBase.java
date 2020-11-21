package com.android.settings.enterprise;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0015R$plurals;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;

public abstract class FailedPasswordWipePreferenceControllerBase extends AbstractPreferenceController implements PreferenceControllerMixin {
    protected final EnterprisePrivacyFeatureProvider mFeatureProvider;

    /* access modifiers changed from: protected */
    public abstract int getMaximumFailedPasswordsBeforeWipe();

    public FailedPasswordWipePreferenceControllerBase(Context context) {
        super(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int maximumFailedPasswordsBeforeWipe = getMaximumFailedPasswordsBeforeWipe();
        preference.setSummary(this.mContext.getResources().getQuantityString(C0015R$plurals.enterprise_privacy_number_failed_password_wipe, maximumFailedPasswordsBeforeWipe, Integer.valueOf(maximumFailedPasswordsBeforeWipe)));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return getMaximumFailedPasswordsBeforeWipe() > 0;
    }
}
