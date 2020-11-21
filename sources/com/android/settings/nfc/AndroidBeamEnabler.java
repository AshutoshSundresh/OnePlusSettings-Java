package com.android.settings.nfc;

import android.content.Context;
import android.os.UserHandle;
import com.android.settings.C0017R$string;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;

public class AndroidBeamEnabler extends BaseNfcEnabler {
    private final boolean mBeamDisallowedBySystem;
    private final RestrictedPreference mPreference;

    public AndroidBeamEnabler(Context context, RestrictedPreference restrictedPreference) {
        super(context);
        this.mPreference = restrictedPreference;
        this.mBeamDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_outgoing_beam", UserHandle.myUserId());
        if (!isNfcAvailable()) {
            this.mPreference.setEnabled(false);
        } else if (this.mBeamDisallowedBySystem) {
            this.mPreference.setEnabled(false);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.nfc.BaseNfcEnabler
    public void handleNfcStateChanged(int i) {
        if (i == 1) {
            this.mPreference.setEnabled(false);
            this.mPreference.setSummary(C0017R$string.nfc_disabled_summary);
        } else if (i == 2) {
            this.mPreference.setEnabled(false);
        } else if (i == 3) {
            if (this.mBeamDisallowedBySystem) {
                this.mPreference.setDisabledByAdmin(null);
                this.mPreference.setEnabled(false);
            } else {
                this.mPreference.checkRestrictionAndSetDisabled("no_outgoing_beam");
            }
            if (!this.mNfcAdapter.isNdefPushEnabled() || !this.mPreference.isEnabled()) {
                this.mPreference.setSummary(C0017R$string.android_beam_off_summary);
            } else {
                this.mPreference.setSummary(C0017R$string.android_beam_on_summary);
            }
        } else if (i == 4) {
            this.mPreference.setEnabled(false);
        }
    }
}
