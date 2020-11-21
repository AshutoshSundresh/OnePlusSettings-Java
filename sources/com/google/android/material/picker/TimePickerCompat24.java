package com.google.android.material.picker;

import android.annotation.TargetApi;
import android.icu.text.DecimalFormatSymbols;
import java.util.Locale;

@TargetApi(24)
public final class TimePickerCompat24 {
    public static void setHourFormat(TextInputTimePickerView textInputTimePickerView, Locale locale) {
        char[] digits = DecimalFormatSymbols.getInstance(locale).getDigits();
        int i = 0;
        for (int i2 = 0; i2 < 10; i2++) {
            i = Math.max(i, String.valueOf(digits[i2]).length());
        }
        textInputTimePickerView.setHourFormat(i * 2);
    }
}
