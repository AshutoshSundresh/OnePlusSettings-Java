package com.google.android.setupcompat.internal;

/* renamed from: com.google.android.setupcompat.internal.-$$Lambda$ClockProvider$Xhv6ez4NTBFUn6cNE_oxYP2IXvc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ClockProvider$Xhv6ez4NTBFUn6cNE_oxYP2IXvc implements Ticker {
    public static final /* synthetic */ $$Lambda$ClockProvider$Xhv6ez4NTBFUn6cNE_oxYP2IXvc INSTANCE = new $$Lambda$ClockProvider$Xhv6ez4NTBFUn6cNE_oxYP2IXvc();

    private /* synthetic */ $$Lambda$ClockProvider$Xhv6ez4NTBFUn6cNE_oxYP2IXvc() {
    }

    @Override // com.google.android.setupcompat.internal.Ticker
    public final long read() {
        return System.nanoTime();
    }
}
