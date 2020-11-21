package com.android.settings.security;

import android.content.Context;

public class InstallCertificatePreferenceController extends RestrictedEncryptionPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "install_certificate";
    }

    public InstallCertificatePreferenceController(Context context) {
        super(context, "no_config_credentials");
    }
}
