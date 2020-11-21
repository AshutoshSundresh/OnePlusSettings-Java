package com.android.wifitrackerlib;

import android.net.wifi.WifiConfiguration;
import java.util.function.Predicate;

/* renamed from: com.android.wifitrackerlib.-$$Lambda$WifiPickerTracker$Ge-PaFPqD_aenaiEba32ZGYPo9E  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$WifiPickerTracker$GePaFPqD_aenaiEba32ZGYPo9E implements Predicate {
    public static final /* synthetic */ $$Lambda$WifiPickerTracker$GePaFPqD_aenaiEba32ZGYPo9E INSTANCE = new $$Lambda$WifiPickerTracker$GePaFPqD_aenaiEba32ZGYPo9E();

    private /* synthetic */ $$Lambda$WifiPickerTracker$GePaFPqD_aenaiEba32ZGYPo9E() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return WifiPickerTracker.lambda$updateWifiConfigurations$12((WifiConfiguration) obj);
    }
}
