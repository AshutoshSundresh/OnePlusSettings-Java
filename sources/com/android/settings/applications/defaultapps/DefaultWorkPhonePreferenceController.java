package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.os.UserHandle;
import com.android.settings.Utils;

public class DefaultWorkPhonePreferenceController extends DefaultPhonePreferenceController {
    private final UserHandle mUserHandle;

    @Override // com.android.settings.applications.defaultapps.DefaultPhonePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "work_default_phone_app";
    }

    public DefaultWorkPhonePreferenceController(Context context) {
        super(context);
        UserHandle managedProfile = Utils.getManagedProfile(this.mUserManager);
        this.mUserHandle = managedProfile;
        if (managedProfile != null) {
            this.mUserId = managedProfile.getIdentifier();
        }
    }

    @Override // com.android.settings.applications.defaultapps.DefaultPhonePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mUserHandle == null) {
            return false;
        }
        return super.isAvailable();
    }
}
