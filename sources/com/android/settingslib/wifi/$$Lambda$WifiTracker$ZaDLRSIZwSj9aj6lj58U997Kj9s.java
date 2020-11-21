package com.android.settingslib.wifi;

import android.net.wifi.WifiConfiguration;
import java.util.function.Predicate;

/* renamed from: com.android.settingslib.wifi.-$$Lambda$WifiTracker$ZaDLRSIZwSj9aj6lj58U997Kj9s  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$WifiTracker$ZaDLRSIZwSj9aj6lj58U997Kj9s implements Predicate {
    public static final /* synthetic */ $$Lambda$WifiTracker$ZaDLRSIZwSj9aj6lj58U997Kj9s INSTANCE = new $$Lambda$WifiTracker$ZaDLRSIZwSj9aj6lj58U997Kj9s();

    private /* synthetic */ $$Lambda$WifiTracker$ZaDLRSIZwSj9aj6lj58U997Kj9s() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return WifiTracker.isSaeOrOwe((WifiConfiguration) obj);
    }
}
