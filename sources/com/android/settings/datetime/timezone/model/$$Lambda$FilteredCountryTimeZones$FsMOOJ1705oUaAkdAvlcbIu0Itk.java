package com.android.settings.datetime.timezone.model;

import java.util.function.Function;
import libcore.timezone.CountryTimeZones;

/* renamed from: com.android.settings.datetime.timezone.model.-$$Lambda$FilteredCountryTimeZones$FsMOOJ1705oUaAkdAvlcbIu0Itk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FilteredCountryTimeZones$FsMOOJ1705oUaAkdAvlcbIu0Itk implements Function {
    public static final /* synthetic */ $$Lambda$FilteredCountryTimeZones$FsMOOJ1705oUaAkdAvlcbIu0Itk INSTANCE = new $$Lambda$FilteredCountryTimeZones$FsMOOJ1705oUaAkdAvlcbIu0Itk();

    private /* synthetic */ $$Lambda$FilteredCountryTimeZones$FsMOOJ1705oUaAkdAvlcbIu0Itk() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return ((CountryTimeZones.TimeZoneMapping) obj).getTimeZoneId();
    }
}
