package com.android.wifitrackerlib;

import android.net.wifi.hotspot2.PasspointConfiguration;
import java.util.function.Function;

/* renamed from: com.android.wifitrackerlib.-$$Lambda$WifiPickerTracker$NZj8llmsS2xh549r2-eluZN8xzY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$WifiPickerTracker$NZj8llmsS2xh549r2eluZN8xzY implements Function {
    public static final /* synthetic */ $$Lambda$WifiPickerTracker$NZj8llmsS2xh549r2eluZN8xzY INSTANCE = new $$Lambda$WifiPickerTracker$NZj8llmsS2xh549r2eluZN8xzY();

    private /* synthetic */ $$Lambda$WifiPickerTracker$NZj8llmsS2xh549r2eluZN8xzY() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(((PasspointConfiguration) obj).getUniqueId());
    }
}
