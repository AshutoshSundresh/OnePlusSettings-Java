package com.google.android.setupcompat.internal;

import com.google.android.setupcompat.internal.ClockProvider;
import java.util.concurrent.TimeUnit;

public class ClockProvider {
    private static final Ticker SYSTEM_TICKER;
    private static Ticker ticker;

    public interface Supplier<T> {
        T get();
    }

    public static long timeInNanos() {
        return ticker.read();
    }

    public static long timeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(timeInNanos());
    }

    public static void resetInstance() {
        ticker = SYSTEM_TICKER;
    }

    public static void setInstance(Supplier<Long> supplier) {
        ticker = new Ticker() {
            /* class com.google.android.setupcompat.internal.$$Lambda$ClockProvider$yv5DtHvw2C6wuTWjvKViPvtokI */

            @Override // com.google.android.setupcompat.internal.Ticker
            public final long read() {
                return ((Long) ClockProvider.Supplier.this.get()).longValue();
            }
        };
    }

    static {
        $$Lambda$ClockProvider$Xhv6ez4NTBFUn6cNE_oxYP2IXvc r0 = $$Lambda$ClockProvider$Xhv6ez4NTBFUn6cNE_oxYP2IXvc.INSTANCE;
        SYSTEM_TICKER = r0;
        ticker = r0;
    }
}
