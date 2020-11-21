package com.android.wifitrackerlib;

import android.net.wifi.hotspot2.PasspointConfiguration;
import java.util.function.Function;

/* renamed from: com.android.wifitrackerlib.-$$Lambda$SavedNetworkTracker$GiPU-7UrK85F3w9N7PMlA7M9niw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SavedNetworkTracker$GiPU7UrK85F3w9N7PMlA7M9niw implements Function {
    public static final /* synthetic */ $$Lambda$SavedNetworkTracker$GiPU7UrK85F3w9N7PMlA7M9niw INSTANCE = new $$Lambda$SavedNetworkTracker$GiPU7UrK85F3w9N7PMlA7M9niw();

    private /* synthetic */ $$Lambda$SavedNetworkTracker$GiPU7UrK85F3w9N7PMlA7M9niw() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(((PasspointConfiguration) obj).getUniqueId());
    }
}
