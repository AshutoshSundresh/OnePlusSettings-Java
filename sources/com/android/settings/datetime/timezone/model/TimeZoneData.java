package com.android.settings.datetime.timezone.model;

import androidx.collection.ArraySet;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import libcore.timezone.CountryTimeZones;
import libcore.timezone.CountryZonesFinder;
import libcore.timezone.TimeZoneFinder;

public class TimeZoneData {
    private static WeakReference<TimeZoneData> sCache;
    private final CountryZonesFinder mCountryZonesFinder;
    private final Set<String> mRegionIds;

    public static synchronized TimeZoneData getInstance() {
        synchronized (TimeZoneData.class) {
            TimeZoneData timeZoneData = sCache == null ? null : sCache.get();
            if (timeZoneData != null) {
                return timeZoneData;
            }
            TimeZoneData timeZoneData2 = new TimeZoneData(TimeZoneFinder.getInstance().getCountryZonesFinder());
            sCache = new WeakReference<>(timeZoneData2);
            return timeZoneData2;
        }
    }

    public TimeZoneData(CountryZonesFinder countryZonesFinder) {
        this.mCountryZonesFinder = countryZonesFinder;
        this.mRegionIds = getNormalizedRegionIds(countryZonesFinder.lookupAllCountryIsoCodes());
    }

    public Set<String> getRegionIds() {
        return this.mRegionIds;
    }

    public Set<String> lookupCountryCodesForZoneId(String str) {
        if (str == null) {
            return Collections.emptySet();
        }
        List<CountryTimeZones> lookupCountryTimeZonesForZoneId = this.mCountryZonesFinder.lookupCountryTimeZonesForZoneId(str);
        ArraySet arraySet = new ArraySet();
        for (CountryTimeZones countryTimeZones : lookupCountryTimeZonesForZoneId) {
            FilteredCountryTimeZones filteredCountryTimeZones = new FilteredCountryTimeZones(countryTimeZones);
            if (filteredCountryTimeZones.getTimeZoneIds().contains(str)) {
                arraySet.add(filteredCountryTimeZones.getRegionId());
            }
        }
        return arraySet;
    }

    public FilteredCountryTimeZones lookupCountryTimeZones(String str) {
        CountryTimeZones lookupCountryTimeZones = str == null ? null : this.mCountryZonesFinder.lookupCountryTimeZones(str);
        if (lookupCountryTimeZones == null) {
            return null;
        }
        return new FilteredCountryTimeZones(lookupCountryTimeZones);
    }

    private static Set<String> getNormalizedRegionIds(List<String> list) {
        HashSet hashSet = new HashSet(list.size());
        for (String str : list) {
            hashSet.add(normalizeRegionId(str));
        }
        return Collections.unmodifiableSet(hashSet);
    }

    public static String normalizeRegionId(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase(Locale.US);
    }
}
