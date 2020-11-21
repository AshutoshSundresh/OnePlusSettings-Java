package com.android.settings.bluetooth;

import android.content.Context;
import android.os.UserHandle;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;

public class RestrictionUtils {
    public RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced(Context context, String str) {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, str, UserHandle.myUserId());
    }
}
