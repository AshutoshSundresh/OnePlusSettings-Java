package com.android.settings.applications;

import android.content.pm.PackageInfo;
import java.util.function.Predicate;

/* renamed from: com.android.settings.applications.-$$Lambda$AppPermissionsPreferenceController$V5FV8sM4sykbVAV6lAvbDY5J6b0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AppPermissionsPreferenceController$V5FV8sM4sykbVAV6lAvbDY5J6b0 implements Predicate {
    public static final /* synthetic */ $$Lambda$AppPermissionsPreferenceController$V5FV8sM4sykbVAV6lAvbDY5J6b0 INSTANCE = new $$Lambda$AppPermissionsPreferenceController$V5FV8sM4sykbVAV6lAvbDY5J6b0();

    private /* synthetic */ $$Lambda$AppPermissionsPreferenceController$V5FV8sM4sykbVAV6lAvbDY5J6b0() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return AppPermissionsPreferenceController.lambda$queryPermissionSummary$0((PackageInfo) obj);
    }
}
