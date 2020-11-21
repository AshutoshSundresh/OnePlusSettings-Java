package com.android.settings.datetime.timezone.model;

import java.util.function.Predicate;
import libcore.timezone.CountryTimeZones;

/* renamed from: com.android.settings.datetime.timezone.model.-$$Lambda$FilteredCountryTimeZones$ZNz2Mv2nKX1oBkvEJWubr7tgzck  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FilteredCountryTimeZones$ZNz2Mv2nKX1oBkvEJWubr7tgzck implements Predicate {
    public static final /* synthetic */ $$Lambda$FilteredCountryTimeZones$ZNz2Mv2nKX1oBkvEJWubr7tgzck INSTANCE = new $$Lambda$FilteredCountryTimeZones$ZNz2Mv2nKX1oBkvEJWubr7tgzck();

    private /* synthetic */ $$Lambda$FilteredCountryTimeZones$ZNz2Mv2nKX1oBkvEJWubr7tgzck() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return FilteredCountryTimeZones.lambda$new$0((CountryTimeZones.TimeZoneMapping) obj);
    }
}
