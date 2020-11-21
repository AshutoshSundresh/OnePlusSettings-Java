package com.android.settings.localepicker;

import android.os.LocaleList;
import com.android.internal.app.LocaleHelper;
import com.android.internal.app.LocalePicker;
import java.util.Locale;

public class LocaleFeatureProviderImpl implements LocaleFeatureProvider {
    @Override // com.android.settings.localepicker.LocaleFeatureProvider
    public String getLocaleNames() {
        LocaleList locales = LocalePicker.getLocales();
        Locale locale = Locale.getDefault();
        return LocaleHelper.toSentenceCase(LocaleHelper.getDisplayLocaleList(locales, locale, 2), locale);
    }
}
