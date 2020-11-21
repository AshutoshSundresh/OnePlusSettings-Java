package com.android.wifitrackerlib;

import android.net.wifi.WifiConfiguration;
import java.util.function.Function;

/* renamed from: com.android.wifitrackerlib.-$$Lambda$eRhiL3TPu1j8op3nmit378jGeyk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$eRhiL3TPu1j8op3nmit378jGeyk implements Function {
    public static final /* synthetic */ $$Lambda$eRhiL3TPu1j8op3nmit378jGeyk INSTANCE = new $$Lambda$eRhiL3TPu1j8op3nmit378jGeyk();

    private /* synthetic */ $$Lambda$eRhiL3TPu1j8op3nmit378jGeyk() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return StandardWifiEntry.wifiConfigToStandardWifiEntryKey((WifiConfiguration) obj);
    }
}
