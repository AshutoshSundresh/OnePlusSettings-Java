package com.android.settings.wifi.savedaccesspoints;

import com.android.settingslib.wifi.AccessPoint;
import java.util.function.Predicate;

/* renamed from: com.android.settings.wifi.savedaccesspoints.-$$Lambda$SubscribedAccessPointsPreferenceController$hMbCNMfk1vTBjfR8IBrpTCOpm4Y  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SubscribedAccessPointsPreferenceController$hMbCNMfk1vTBjfR8IBrpTCOpm4Y implements Predicate {
    public static final /* synthetic */ $$Lambda$SubscribedAccessPointsPreferenceController$hMbCNMfk1vTBjfR8IBrpTCOpm4Y INSTANCE = new $$Lambda$SubscribedAccessPointsPreferenceController$hMbCNMfk1vTBjfR8IBrpTCOpm4Y();

    private /* synthetic */ $$Lambda$SubscribedAccessPointsPreferenceController$hMbCNMfk1vTBjfR8IBrpTCOpm4Y() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((AccessPoint) obj).isPasspointConfig();
    }
}
