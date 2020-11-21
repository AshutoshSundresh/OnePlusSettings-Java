package com.android.settingslib.utils;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.MeasureFormat;
import android.icu.util.Measure;
import android.icu.util.MeasureUnit;
import android.text.TextUtils;
import com.android.settingslib.R$string;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PowerUtil {
    private static final long FIFTEEN_MINUTES_MILLIS = TimeUnit.MINUTES.toMillis(15);
    private static final long ONE_DAY_MILLIS = TimeUnit.DAYS.toMillis(1);
    private static final long ONE_HOUR_MILLIS = TimeUnit.HOURS.toMillis(1);
    private static final long SEVEN_MINUTES_MILLIS = TimeUnit.MINUTES.toMillis(7);
    private static final long TWO_DAYS_MILLIS = TimeUnit.DAYS.toMillis(2);

    public static long convertMsToUs(long j) {
        return j * 1000;
    }

    public static String getBatteryRemainingStringFormatted(Context context, long j, String str, boolean z) {
        if (j <= 0) {
            return null;
        }
        if (j <= SEVEN_MINUTES_MILLIS) {
            return getShutdownImminentString(context, str);
        }
        long j2 = FIFTEEN_MINUTES_MILLIS;
        if (j <= j2) {
            return getUnderFifteenString(context, StringUtil.formatElapsedTime(context, (double) j2, false), str);
        }
        if (j >= TWO_DAYS_MILLIS) {
            return getMoreThanTwoDaysString(context, str);
        }
        if (j >= ONE_DAY_MILLIS) {
            return getMoreThanOneDayString(context, j, str, z);
        }
        return getRegularTimeRemainingString(context, j, str, z);
    }

    public static String getBatteryTipStringFormatted(Context context, long j) {
        if (j <= 0) {
            return null;
        }
        if (j > ONE_DAY_MILLIS) {
            return getMoreThanOneDayShortString(context, j, R$string.power_remaining_only_more_than_subtext);
        }
        return context.getString(R$string.power_suggestion_battery_run_out, getDateTimeStringFromMs(context, j));
    }

    private static String getShutdownImminentString(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return context.getString(R$string.power_remaining_duration_only_shutdown_imminent);
        }
        return context.getString(R$string.power_remaining_duration_shutdown_imminent, str);
    }

    private static String getUnderFifteenString(Context context, CharSequence charSequence, String str) {
        if (TextUtils.isEmpty(str)) {
            return context.getString(R$string.power_remaining_less_than_duration_only, charSequence);
        }
        return context.getString(R$string.power_remaining_less_than_duration, charSequence, str);
    }

    private static String getMoreThanOneDayString(Context context, long j, String str, boolean z) {
        int i;
        int i2;
        CharSequence formatElapsedTime = StringUtil.formatElapsedTime(context, (double) roundTimeToNearestThreshold(j, ONE_HOUR_MILLIS), false);
        if (TextUtils.isEmpty(str)) {
            if (z) {
                i2 = R$string.power_remaining_duration_only_enhanced;
            } else {
                i2 = R$string.power_remaining_duration_only;
            }
            return context.getString(i2, formatElapsedTime);
        }
        if (z) {
            i = R$string.power_discharging_duration_enhanced;
        } else {
            i = R$string.power_discharging_duration;
        }
        return context.getString(i, formatElapsedTime, str);
    }

    private static String getMoreThanOneDayShortString(Context context, long j, int i) {
        return context.getString(i, StringUtil.formatElapsedTime(context, (double) roundTimeToNearestThreshold(j, ONE_HOUR_MILLIS), false));
    }

    private static String getMoreThanTwoDaysString(Context context, String str) {
        MeasureFormat instance = MeasureFormat.getInstance(context.getResources().getConfiguration().getLocales().get(0), MeasureFormat.FormatWidth.SHORT);
        Measure measure = new Measure(2, MeasureUnit.DAY);
        if (TextUtils.isEmpty(str)) {
            return context.getString(R$string.power_remaining_only_more_than_subtext, instance.formatMeasures(measure));
        }
        return context.getString(R$string.power_remaining_more_than_subtext, instance.formatMeasures(measure), str);
    }

    private static String getRegularTimeRemainingString(Context context, long j, String str, boolean z) {
        int i;
        int i2;
        CharSequence dateTimeStringFromMs = getDateTimeStringFromMs(context, j);
        if (TextUtils.isEmpty(str)) {
            if (z) {
                i2 = R$string.power_discharge_by_only_enhanced;
            } else {
                i2 = R$string.power_discharge_by_only;
            }
            return context.getString(i2, dateTimeStringFromMs);
        }
        if (z) {
            i = R$string.power_discharge_by_enhanced;
        } else {
            i = R$string.power_discharge_by;
        }
        return context.getString(i, dateTimeStringFromMs, str);
    }

    private static CharSequence getDateTimeStringFromMs(Context context, long j) {
        return DateFormat.getInstanceForSkeleton(android.text.format.DateFormat.getTimeFormatString(context)).format(Date.from(Instant.ofEpochMilli(roundTimeToNearestThreshold(System.currentTimeMillis() + j, FIFTEEN_MINUTES_MILLIS))));
    }

    public static long convertUsToMs(long j) {
        return j / 1000;
    }

    public static long roundTimeToNearestThreshold(long j, long j2) {
        long abs = Math.abs(j);
        long abs2 = Math.abs(j2);
        long j3 = abs % abs2;
        return j3 < abs2 / 2 ? abs - j3 : (abs - j3) + abs2;
    }
}
