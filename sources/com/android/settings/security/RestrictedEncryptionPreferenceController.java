package com.android.settings.security;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public abstract class RestrictedEncryptionPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final UserHandle mUserHandle = UserHandle.of(UserHandle.myUserId());
    protected final UserManager mUserManager;
    private final String mUserRestriction;

    public RestrictedEncryptionPreferenceController(Context context, String str) {
        super(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mUserRestriction = str;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !this.mUserManager.hasBaseUserRestriction(this.mUserRestriction, this.mUserHandle);
    }
}
