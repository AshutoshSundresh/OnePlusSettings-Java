package com.android.settingslib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import com.android.settingslib.RestrictedLockUtilsInternal;

/* renamed from: com.android.settingslib.-$$Lambda$RestrictedLockUtilsInternal$GXYFzBzGab6v5GcOkljXViw5O7I  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$RestrictedLockUtilsInternal$GXYFzBzGab6v5GcOkljXViw5O7I implements RestrictedLockUtilsInternal.LockSettingCheck {
    public static final /* synthetic */ $$Lambda$RestrictedLockUtilsInternal$GXYFzBzGab6v5GcOkljXViw5O7I INSTANCE = new $$Lambda$RestrictedLockUtilsInternal$GXYFzBzGab6v5GcOkljXViw5O7I();

    private /* synthetic */ $$Lambda$RestrictedLockUtilsInternal$GXYFzBzGab6v5GcOkljXViw5O7I() {
    }

    @Override // com.android.settingslib.RestrictedLockUtilsInternal.LockSettingCheck
    public final boolean isEnforcing(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i) {
        return RestrictedLockUtilsInternal.lambda$checkIfMaximumTimeToLockIsSet$2(devicePolicyManager, componentName, i);
    }
}
