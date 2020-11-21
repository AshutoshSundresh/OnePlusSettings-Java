package com.android.settings.nfc;

import android.content.Context;
import android.provider.Settings;
import androidx.preference.SwitchPreference;

public class NfcEnabler extends BaseNfcEnabler {
    private final SwitchPreference mPreference;

    public NfcEnabler(Context context, SwitchPreference switchPreference) {
        super(context);
        this.mPreference = switchPreference;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.nfc.BaseNfcEnabler
    public void handleNfcStateChanged(int i) {
        if (i == 1) {
            this.mPreference.setChecked(false);
            this.mPreference.setEnabled(isToggleable());
        } else if (i == 2) {
            this.mPreference.setChecked(true);
            this.mPreference.setEnabled(false);
        } else if (i == 3) {
            this.mPreference.setChecked(true);
            this.mPreference.setEnabled(true);
        } else if (i == 4) {
            this.mPreference.setChecked(false);
            this.mPreference.setEnabled(false);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isToggleable() {
        if (NfcPreferenceController.isToggleableInAirplaneMode(this.mContext) || !NfcPreferenceController.shouldTurnOffNFCInAirplaneMode(this.mContext) || Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 1) {
            return true;
        }
        return false;
    }
}
