package com.android.settings.datetime.timezone.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import libcore.timezone.CountryTimeZones;

public class FilteredCountryTimeZones {
    private final CountryTimeZones mCountryTimeZones;
    private final List<String> mTimeZoneIds;

    public FilteredCountryTimeZones(CountryTimeZones countryTimeZones) {
        this.mCountryTimeZones = countryTimeZones;
        this.mTimeZoneIds = Collections.unmodifiableList((List) countryTimeZones.getTimeZoneMappings().stream().filter($$Lambda$FilteredCountryTimeZones$ZNz2Mv2nKX1oBkvEJWubr7tgzck.INSTANCE).map($$Lambda$FilteredCountryTimeZones$FsMOOJ1705oUaAkdAvlcbIu0Itk.INSTANCE).collect(Collectors.toList()));
    }

    static /* synthetic */ boolean lambda$new$0(CountryTimeZones.TimeZoneMapping timeZoneMapping) {
        return timeZoneMapping.isShownInPicker() && (timeZoneMapping.getNotUsedAfter() == null || timeZoneMapping.getNotUsedAfter().longValue() >= 1546300800000L);
    }

    public List<String> getTimeZoneIds() {
        return this.mTimeZoneIds;
    }

    public String getRegionId() {
        return TimeZoneData.normalizeRegionId(this.mCountryTimeZones.getCountryIso());
    }
}
