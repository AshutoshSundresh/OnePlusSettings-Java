package androidx.leanback.widget.picker;

import android.content.res.Resources;
import android.os.Build;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/* access modifiers changed from: package-private */
public class PickerUtility {
    static final boolean SUPPORTS_BEST_DATE_TIME_PATTERN = (Build.VERSION.SDK_INT >= 18);

    public static class DateConstant {
        public final Locale locale;
        public final String[] months;

        DateConstant(Locale locale2, Resources resources) {
            this.locale = locale2;
            this.months = DateFormatSymbols.getInstance(locale2).getShortMonths();
            Calendar instance = Calendar.getInstance(locale2);
            PickerUtility.createStringIntArrays(instance.getMinimum(5), instance.getMaximum(5), "%02d");
        }
    }

    public static class TimeConstant {
        public final String[] ampm;
        public final String[] hours24 = PickerUtility.createStringIntArrays(0, 23, "%02d");
        public final Locale locale;
        public final String[] minutes = PickerUtility.createStringIntArrays(0, 59, "%02d");

        TimeConstant(Locale locale2, Resources resources) {
            this.locale = locale2;
            DateFormatSymbols instance = DateFormatSymbols.getInstance(locale2);
            PickerUtility.createStringIntArrays(1, 12, "%02d");
            this.ampm = instance.getAmPmStrings();
        }
    }

    public static DateConstant getDateConstantInstance(Locale locale, Resources resources) {
        return new DateConstant(locale, resources);
    }

    public static TimeConstant getTimeConstantInstance(Locale locale, Resources resources) {
        return new TimeConstant(locale, resources);
    }

    public static String[] createStringIntArrays(int i, int i2, String str) {
        String[] strArr = new String[((i2 - i) + 1)];
        for (int i3 = i; i3 <= i2; i3++) {
            if (str != null) {
                strArr[i3 - i] = String.format(str, Integer.valueOf(i3));
            } else {
                strArr[i3 - i] = String.valueOf(i3);
            }
        }
        return strArr;
    }

    public static Calendar getCalendarForLocale(Calendar calendar, Locale locale) {
        if (calendar == null) {
            return Calendar.getInstance(locale);
        }
        long timeInMillis = calendar.getTimeInMillis();
        Calendar instance = Calendar.getInstance(locale);
        instance.setTimeInMillis(timeInMillis);
        return instance;
    }
}
