package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.ToIntFunction;

/* renamed from: com.android.wifitrackerlib.-$$Lambda$StandardWifiEntry$Lr4BrIBW8EpwljEjYsXvjw-UzPU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$StandardWifiEntry$Lr4BrIBW8EpwljEjYsXvjwUzPU implements ToIntFunction {
    public static final /* synthetic */ $$Lambda$StandardWifiEntry$Lr4BrIBW8EpwljEjYsXvjwUzPU INSTANCE = new $$Lambda$StandardWifiEntry$Lr4BrIBW8EpwljEjYsXvjwUzPU();

    private /* synthetic */ $$Lambda$StandardWifiEntry$Lr4BrIBW8EpwljEjYsXvjwUzPU() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        return StandardWifiEntry.lambda$getScanResultDescription$3((ScanResult) obj);
    }
}
