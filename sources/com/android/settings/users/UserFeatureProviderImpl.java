package com.android.settings.users;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.List;

public class UserFeatureProviderImpl implements UserFeatureProvider {
    UserManager mUm;

    public UserFeatureProviderImpl(Context context) {
        this.mUm = (UserManager) context.getSystemService("user");
    }

    @Override // com.android.settings.users.UserFeatureProvider
    public List<UserHandle> getUserProfiles() {
        return this.mUm.getUserProfiles();
    }
}
