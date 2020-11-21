package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.ToIntFunction;

/* renamed from: com.android.wifitrackerlib.-$$Lambda$Utils$wGn2sVTZ5l1wFLkqd7rxKtPh0RU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Utils$wGn2sVTZ5l1wFLkqd7rxKtPh0RU implements ToIntFunction {
    public static final /* synthetic */ $$Lambda$Utils$wGn2sVTZ5l1wFLkqd7rxKtPh0RU INSTANCE = new $$Lambda$Utils$wGn2sVTZ5l1wFLkqd7rxKtPh0RU();

    private /* synthetic */ $$Lambda$Utils$wGn2sVTZ5l1wFLkqd7rxKtPh0RU() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        return ((ScanResult) obj).level;
    }
}
