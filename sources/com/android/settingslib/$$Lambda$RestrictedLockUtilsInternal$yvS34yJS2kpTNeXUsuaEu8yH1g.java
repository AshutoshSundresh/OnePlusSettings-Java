package com.android.settingslib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import com.android.settingslib.RestrictedLockUtilsInternal;

/* renamed from: com.android.settingslib.-$$Lambda$RestrictedLockUtilsInternal$yvS34yJS2kpTNeXUsuaEu-8yH1g  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$RestrictedLockUtilsInternal$yvS34yJS2kpTNeXUsuaEu8yH1g implements RestrictedLockUtilsInternal.LockSettingCheck {
    public static final /* synthetic */ $$Lambda$RestrictedLockUtilsInternal$yvS34yJS2kpTNeXUsuaEu8yH1g INSTANCE = new $$Lambda$RestrictedLockUtilsInternal$yvS34yJS2kpTNeXUsuaEu8yH1g();

    private /* synthetic */ $$Lambda$RestrictedLockUtilsInternal$yvS34yJS2kpTNeXUsuaEu8yH1g() {
    }

    @Override // com.android.settingslib.RestrictedLockUtilsInternal.LockSettingCheck
    public final boolean isEnforcing(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i) {
        return RestrictedLockUtilsInternal.lambda$checkIfPasswordQualityIsSet$1(devicePolicyManager, componentName, i);
    }
}
