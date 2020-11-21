package com.android.settings.users;

import android.content.Context;
import android.os.UserHandle;
import androidx.fragment.app.Fragment;
import com.oneplus.settings.utils.OPUtils;

public class AutoSyncPersonalDataPreferenceController extends AutoSyncDataPreferenceController {
    @Override // com.android.settings.users.AutoSyncDataPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "auto_sync_personal_account_data";
    }

    public AutoSyncPersonalDataPreferenceController(Context context, Fragment fragment) {
        super(context, fragment);
    }

    @Override // com.android.settings.users.AutoSyncDataPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if ((this.mUserManager.getProfiles(UserHandle.myUserId()).size() >= 3 || !OPUtils.hasMultiAppProfiles(this.mUserManager)) && !this.mUserManager.isManagedProfile() && !this.mUserManager.isLinkedUser() && this.mUserManager.getProfiles(UserHandle.myUserId()).size() > 1) {
            return true;
        }
        return false;
    }
}
