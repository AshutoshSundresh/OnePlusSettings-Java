package com.oneplus.security.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class TimeRegionUtils {
    public static Calendar getCalendarDate(String str) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");
        try {
            Date parse = simpleDateFormat.parse(str);
            Calendar instance = Calendar.getInstance();
            instance.setTime(parse);
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String timeMillisToDate(long j) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(j));
    }

    public static long[] getRegionTimeDefault(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        int actualMinimum = instance.getActualMinimum(5);
        int actualMaximum = instance.getActualMaximum(5);
        Calendar instance2 = Calendar.getInstance();
        instance2.set(instance.get(1), instance.get(2), actualMinimum, 0, 0, 0);
        Calendar instance3 = Calendar.getInstance();
        instance3.set(instance.get(1), instance.get(2), actualMaximum, 23, 59, 59);
        return new long[]{instance2.getTimeInMillis(), instance3.getTimeInMillis()};
    }

    public static long[] getRegionTime(int i, long j) {
        if (i < 1 || i > 31) {
            throw new IllegalArgumentException("Reset day should between 1 - 31.");
        }
        long timeInMillis = getCalendarDate(timeMillisToDate(j)).getTimeInMillis();
        long[] jArr = new long[2];
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date(timeInMillis));
        int i2 = instance.get(1);
        int i3 = instance.get(2);
        instance.get(5);
        int actualMaximum = instance.getActualMaximum(5);
        instance.add(2, 1);
        int i4 = instance.get(2);
        int i5 = instance.get(1);
        instance.set(1, i5);
        instance.set(2, i4);
        int actualMaximum2 = instance.getActualMaximum(5);
        instance.setTime(new Date(timeInMillis));
        instance.add(2, -1);
        int i6 = instance.get(2);
        int i7 = instance.get(1);
        instance.set(1, i7);
        instance.set(2, i6);
        int actualMaximum3 = instance.getActualMaximum(5);
        if (i > actualMaximum) {
            jArr[0] = getUnixTime(instance, i5, i4, 1);
        } else {
            jArr[0] = getUnixTime(instance, i2, i3, i);
        }
        if (i > actualMaximum2) {
            jArr[1] = getUnixTime(instance, i5, i4, actualMaximum2);
        } else {
            jArr[1] = getUnixTime(instance, i5, i4, i - 1);
        }
        if (timeInMillis < jArr[0] || timeInMillis > jArr[1]) {
            if (i > actualMaximum3) {
                jArr[0] = getUnixTime(instance, i2, i3, 1);
            } else {
                jArr[0] = getUnixTime(instance, i7, i6, i);
            }
            if (i > actualMaximum) {
                jArr[1] = getUnixTime(instance, i2, i3, actualMaximum);
            } else {
                jArr[1] = getUnixTime(instance, i2, i3, i - 1);
            }
        }
        jArr[1] = jArr[1] + 86399999;
        LogUtils.i("RegionTimeUtils", "region:" + Arrays.toString(jArr));
        return jArr;
    }

    private static long getUnixTime(Calendar calendar, int i, int i2, int i3) {
        calendar.set(i, i2, i3);
        return calendar.getTimeInMillis();
    }
}
