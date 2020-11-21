package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.ToIntFunction;

/* renamed from: com.android.wifitrackerlib.-$$Lambda$StandardWifiEntry$ulMGK6KYyQVXHFy8lpHK9UIg2Q4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$StandardWifiEntry$ulMGK6KYyQVXHFy8lpHK9UIg2Q4 implements ToIntFunction {
    public static final /* synthetic */ $$Lambda$StandardWifiEntry$ulMGK6KYyQVXHFy8lpHK9UIg2Q4 INSTANCE = new $$Lambda$StandardWifiEntry$ulMGK6KYyQVXHFy8lpHK9UIg2Q4();

    private /* synthetic */ $$Lambda$StandardWifiEntry$ulMGK6KYyQVXHFy8lpHK9UIg2Q4() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        return ((ScanResult) obj).level;
    }
}
