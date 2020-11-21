package com.google.android.material.picker.calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class OnepulsCalendarUtil {
    private static final int[] LUNAR_INFOS = {19416, 19168, 42352, 21717, 53856, 55632, 91476, 22176, 39632, 21970, 19168, 42422, 42192, 53840, 119381, 46400, 54944, 44450, 38320, 84343, 18800, 42160, 46261, 27216, 27968, 109396, 11104, 38256, 21234, 18800, 25958, 54432, 59984, 28309, 23248, 11104, 100067, 37600, 116951, 51536, 54432, 120998, 46416, 22176, 107956, 9680, 37584, 53938, 43344, 46423, 27808, 46416, 86869, 19872, 42448, 83315, 21200, 43432, 59728, 27296, 44710, 43856, 19296, 43748, 42352, 21088, 62051, 55632, 23383, 22176, 38608, 19925, 19152, 42192, 54484, 53840, 54616, 46400, 46496, 103846, 38320, 18864, 43380, 42160, 45690, 27216, 27968, 44870, 43872, 38256, 19189, 18800, 25776, 29859, 59984, 27480, 22208, 43872, 38613, 37600, 51552, 55636, 54432, 55888, 30034, 22176, 43959, 9680, 37584, 51893, 43344, 46240, 47780, 44368, 21977, 19360, 42416, 86390, 21168, 43312, 31060, 27296, 44368, 23378, 19296, 42726, 42208, 53856, 60005, 54576, 23200, 30371, 38608, 19415, 19152, 42192, 118966, 53840, 54560, 56645, 46496, 22224, 21938, 18864, 42359, 42160, 43600, 111189, 27936, 44448};
    private static final GregorianCalendar START_CALENDAR = new GregorianCalendar(1900, 0, 30);

    public static int getLeapMonth(int i) {
        return LUNAR_INFOS[i - 1900] & 15;
    }

    public static int getLeapMonthDays(int i) {
        if (getLeapMonth(i) != 0) {
            return (LUNAR_INFOS[i + -1900] & 983040) == 0 ? 29 : 30;
        }
        return 0;
    }

    public static int getMonthDays(int i, int i2) throws IllegalArgumentException {
        if (i2 > 12 || i2 < 0) {
            throw new IllegalArgumentException("month over erro:" + i2);
        }
        return ((LUNAR_INFOS[i + -1900] & 65535) & (1 << (16 - i2))) == 0 ? 29 : 30;
    }

    private static int getYearDays(int i) {
        int i2 = 348;
        for (int i3 = 32768; i3 >= 8; i3 >>= 1) {
            if ((LUNAR_INFOS[i - 1900] & 65520 & i3) != 0) {
                i2++;
            }
        }
        return i2 + getLeapMonthDays(i);
    }

    private static int daysBetween(Calendar calendar, Calendar calendar2) throws IllegalArgumentException {
        int i = calendar.get(1);
        int i2 = calendar2.get(1);
        if (calendar.before(calendar2)) {
            int i3 = (0 - calendar.get(6)) + calendar2.get(6);
            for (int i4 = 0; i4 < Math.abs(i2 - i); i4++) {
                i3 += calendar.getActualMaximum(6);
                calendar.add(1, 1);
            }
            return i3;
        }
        throw new IllegalArgumentException("start after end erro:" + calendar.toString() + calendar2.toString());
    }

    public static OneplusLunarCalendar solarToLunar(Calendar calendar) throws IllegalArgumentException {
        int yearDays;
        Calendar instance = Calendar.getInstance();
        boolean z = true;
        instance.set(START_CALENDAR.get(1), START_CALENDAR.get(2), START_CALENDAR.get(5));
        int daysBetween = daysBetween(instance, calendar);
        int i = 1900;
        int i2 = 0;
        while (i <= 2049 && (yearDays = daysBetween - (i2 = getYearDays(i))) >= 1) {
            i++;
            daysBetween = yearDays;
        }
        int leapMonth = getLeapMonth(i);
        boolean z2 = leapMonth > 0;
        boolean z3 = false;
        int i3 = 1;
        while (i3 <= 12) {
            if (i3 != leapMonth + 1 || !z2) {
                i2 = getMonthDays(i, i3);
            } else {
                i2 = getLeapMonthDays(i);
                i3--;
                z2 = false;
                z3 = true;
            }
            daysBetween -= i2;
            if (daysBetween <= 0) {
                break;
            }
            i3++;
        }
        int i4 = daysBetween + i2;
        if (i3 != leapMonth) {
            z = false;
        }
        return new OneplusLunarCalendar(i, i3, i4, z3 & z);
    }
}
