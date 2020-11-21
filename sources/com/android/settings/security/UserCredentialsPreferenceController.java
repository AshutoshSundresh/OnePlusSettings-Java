package com.android.settings.security;

import android.content.Context;

public class UserCredentialsPreferenceController extends RestrictedEncryptionPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "user_credentials";
    }

    public UserCredentialsPreferenceController(Context context) {
        super(context, "no_config_credentials");
    }
}
