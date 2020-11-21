package com.android.wifitrackerlib;

import android.net.NetworkKey;
import android.net.wifi.ScanResult;
import java.util.function.Function;

/* renamed from: com.android.wifitrackerlib.-$$Lambda$F7I5Dkjpf4Rkj99qB_bsUx3MJiA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$F7I5Dkjpf4Rkj99qB_bsUx3MJiA implements Function {
    public static final /* synthetic */ $$Lambda$F7I5Dkjpf4Rkj99qB_bsUx3MJiA INSTANCE = new $$Lambda$F7I5Dkjpf4Rkj99qB_bsUx3MJiA();

    private /* synthetic */ $$Lambda$F7I5Dkjpf4Rkj99qB_bsUx3MJiA() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return NetworkKey.createFromScanResult((ScanResult) obj);
    }
}
