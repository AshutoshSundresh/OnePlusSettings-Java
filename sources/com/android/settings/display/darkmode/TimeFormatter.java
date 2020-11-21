package com.android.settings.display.darkmode;

import android.content.Context;
import android.text.format.DateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeFormatter {
    private final Context mContext;
    private final DateTimeFormatter mFormatter;

    public TimeFormatter(Context context) {
        this.mContext = context;
        Locale locale = context.getResources().getConfiguration().locale;
        this.mFormatter = DateTimeFormatter.ofPattern("hh:mm a", locale == null ? Locale.getDefault() : locale);
    }

    public String of(LocalTime localTime) {
        return this.mFormatter.format(localTime);
    }

    public boolean is24HourFormat() {
        return DateFormat.is24HourFormat(this.mContext);
    }
}
