package com.oneplus.settings.timer.timepower;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class SettingsUtil {
    private static long mCurrentTime;

    public static long[] getNearestTime(String str) {
        mCurrentTime = System.currentTimeMillis();
        long[] jArr = {0, 0};
        if (str == null) {
            return jArr;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 1; i <= 2; i++) {
            if (1 == i) {
                int i2 = i * 6;
                int i3 = i2 - 4;
                arrayList.add(Long.valueOf(getUTC(Integer.parseInt(str.substring(i2 - 6, i3)), Integer.parseInt(str.substring(i3, i2 - 2)))));
            } else if (2 == i) {
                int i4 = i * 6;
                int i5 = i4 - 4;
                arrayList2.add(Long.valueOf(getUTC(Integer.parseInt(str.substring(i4 - 6, i5)), Integer.parseInt(str.substring(i5, i4 - 2)))));
            }
        }
        if (arrayList.size() != 0) {
            arrayList.add(Long.valueOf(mCurrentTime));
            Collections.sort(arrayList);
            long longValue = ((Long) arrayList.get(arrayList.size() - 1)).longValue();
            long j = mCurrentTime;
            if (longValue == j) {
                jArr[0] = ((Long) arrayList.get(0)).longValue() + 86400000;
            } else {
                jArr[0] = ((Long) arrayList.get(arrayList.lastIndexOf(Long.valueOf(j)) + 1)).longValue();
            }
        }
        if (arrayList2.size() != 0) {
            arrayList2.add(Long.valueOf(mCurrentTime));
            Collections.sort(arrayList2);
            long longValue2 = ((Long) arrayList2.get(arrayList2.size() - 1)).longValue();
            long j2 = mCurrentTime;
            if (longValue2 == j2) {
                jArr[1] = ((Long) arrayList2.get(0)).longValue() + 86400000;
            } else {
                jArr[1] = ((Long) arrayList2.get(arrayList2.lastIndexOf(Long.valueOf(j2)) + 1)).longValue();
            }
        }
        return jArr;
    }

    private static long getUTC(int i, int i2) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(mCurrentTime);
        instance.set(11, i);
        instance.set(12, i2);
        instance.set(13, 0);
        instance.set(14, 0);
        return instance.getTimeInMillis();
    }
}
