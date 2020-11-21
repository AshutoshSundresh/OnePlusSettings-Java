package com.android.settings.datetime.timezone;

import android.icu.text.TimeZoneFormat;
import android.icu.text.TimeZoneNames;
import android.icu.util.TimeZone;
import android.text.TextUtils;
import com.android.settingslib.datetime.ZoneGetter;
import java.util.Date;
import java.util.Locale;

public class TimeZoneInfo {
    private final String mDaylightName;
    private final String mExemplarLocation;
    private final String mGenericName;
    private final CharSequence mGmtOffset;
    private final String mId;
    private final String mStandardName;
    private final TimeZone mTimeZone;

    public TimeZoneInfo(Builder builder) {
        TimeZone timeZone = builder.mTimeZone;
        this.mTimeZone = timeZone;
        this.mId = timeZone.getID();
        this.mGenericName = builder.mGenericName;
        this.mStandardName = builder.mStandardName;
        this.mDaylightName = builder.mDaylightName;
        this.mExemplarLocation = builder.mExemplarLocation;
        this.mGmtOffset = builder.mGmtOffset;
    }

    public String getId() {
        return this.mId;
    }

    public TimeZone getTimeZone() {
        return this.mTimeZone;
    }

    public String getExemplarLocation() {
        return this.mExemplarLocation;
    }

    public String getGenericName() {
        return this.mGenericName;
    }

    public String getStandardName() {
        return this.mStandardName;
    }

    public String getDaylightName() {
        return this.mDaylightName;
    }

    public CharSequence getGmtOffset() {
        return this.mGmtOffset;
    }

    public static class Builder {
        private String mDaylightName;
        private String mExemplarLocation;
        private String mGenericName;
        private CharSequence mGmtOffset;
        private String mStandardName;
        private final TimeZone mTimeZone;

        public Builder(TimeZone timeZone) {
            if (timeZone != null) {
                this.mTimeZone = timeZone;
                return;
            }
            throw new IllegalArgumentException("TimeZone must not be null!");
        }

        public Builder setGenericName(String str) {
            this.mGenericName = str;
            return this;
        }

        public Builder setStandardName(String str) {
            this.mStandardName = str;
            return this;
        }

        public Builder setDaylightName(String str) {
            this.mDaylightName = str;
            return this;
        }

        public Builder setExemplarLocation(String str) {
            this.mExemplarLocation = str;
            return this;
        }

        public Builder setGmtOffset(CharSequence charSequence) {
            this.mGmtOffset = charSequence;
            return this;
        }

        public TimeZoneInfo build() {
            if (!TextUtils.isEmpty(this.mGmtOffset)) {
                return new TimeZoneInfo(this);
            }
            throw new IllegalStateException("gmtOffset must not be empty!");
        }
    }

    public static class Formatter {
        private final Locale mLocale;
        private final Date mNow;
        private final TimeZoneFormat mTimeZoneFormat;

        public Formatter(Locale locale, Date date) {
            this.mLocale = locale;
            this.mNow = date;
            this.mTimeZoneFormat = TimeZoneFormat.getInstance(locale);
        }

        public TimeZoneInfo format(String str) {
            return format(TimeZone.getFrozenTimeZone(str));
        }

        public TimeZoneInfo format(TimeZone timeZone) {
            String canonicalZoneId = getCanonicalZoneId(timeZone);
            TimeZoneNames timeZoneNames = this.mTimeZoneFormat.getTimeZoneNames();
            CharSequence gmtOffsetText = ZoneGetter.getGmtOffsetText(this.mTimeZoneFormat, this.mLocale, java.util.TimeZone.getTimeZone(canonicalZoneId), this.mNow);
            Builder builder = new Builder(timeZone);
            builder.setGenericName(timeZoneNames.getDisplayName(canonicalZoneId, TimeZoneNames.NameType.LONG_GENERIC, this.mNow.getTime()));
            builder.setStandardName(timeZoneNames.getDisplayName(canonicalZoneId, TimeZoneNames.NameType.LONG_STANDARD, this.mNow.getTime()));
            builder.setDaylightName(timeZoneNames.getDisplayName(canonicalZoneId, TimeZoneNames.NameType.LONG_DAYLIGHT, this.mNow.getTime()));
            builder.setExemplarLocation(timeZoneNames.getExemplarLocationName(canonicalZoneId));
            builder.setGmtOffset(gmtOffsetText);
            return builder.build();
        }

        private static String getCanonicalZoneId(TimeZone timeZone) {
            String id = timeZone.getID();
            String canonicalID = TimeZone.getCanonicalID(id);
            return canonicalID != null ? canonicalID : id;
        }
    }
}
