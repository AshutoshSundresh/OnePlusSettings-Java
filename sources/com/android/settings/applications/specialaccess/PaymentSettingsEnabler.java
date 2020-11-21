package com.android.settings.applications.specialaccess;

import android.content.Context;
import android.nfc.cardemulation.CardEmulation;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.nfc.BaseNfcEnabler;

public class PaymentSettingsEnabler extends BaseNfcEnabler {
    private final CardEmulation mCardEmuManager = CardEmulation.getInstance(this.mNfcAdapter);
    boolean mIsPaymentAvailable = false;
    private final Preference mPreference;

    public PaymentSettingsEnabler(Context context, Preference preference) {
        super(context);
        this.mPreference = preference;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.nfc.BaseNfcEnabler
    public void handleNfcStateChanged(int i) {
        if (i == 1) {
            this.mPreference.setSummary(C0017R$string.nfc_and_payment_settings_payment_off_nfc_off_summary);
            this.mPreference.setEnabled(false);
        } else if (i == 3) {
            if (this.mIsPaymentAvailable) {
                this.mPreference.setSummary((CharSequence) null);
                this.mPreference.setEnabled(true);
                return;
            }
            this.mPreference.setSummary(C0017R$string.nfc_and_payment_settings_no_payment_installed_summary);
            this.mPreference.setEnabled(false);
        }
    }

    @Override // com.android.settings.nfc.BaseNfcEnabler
    public void resume() {
        if (isNfcAvailable()) {
            if (this.mCardEmuManager.getServices("payment").isEmpty()) {
                this.mIsPaymentAvailable = false;
            } else {
                this.mIsPaymentAvailable = true;
            }
            super.resume();
        }
    }
}
