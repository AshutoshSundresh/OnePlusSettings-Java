package com.android.settings.datetime.timezone;

import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import java.util.Formatter;
import java.util.Locale;

public class SpannableUtil {
    public static Spannable getResourcesText(Resources resources, int i, Object... objArr) {
        Locale locale = resources.getConfiguration().getLocales().get(0);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        new Formatter(spannableStringBuilder, locale).format(resources.getString(i), objArr);
        return spannableStringBuilder;
    }
}
