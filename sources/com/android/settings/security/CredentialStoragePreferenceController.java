package com.android.settings.security;

import android.content.Context;
import android.security.KeyStore;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;

public class CredentialStoragePreferenceController extends RestrictedEncryptionPreferenceController {
    private final KeyStore mKeyStore = KeyStore.getInstance();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "credential_storage_type";
    }

    public CredentialStoragePreferenceController(Context context) {
        super(context, "no_config_credentials");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i;
        if (this.mKeyStore.isHardwareBacked()) {
            i = C0017R$string.credential_storage_type_hardware;
        } else {
            i = C0017R$string.credential_storage_type_software;
        }
        preference.setSummary(i);
    }
}
