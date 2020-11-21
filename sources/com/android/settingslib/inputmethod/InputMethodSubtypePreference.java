package com.android.settingslib.inputmethod;

import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import java.text.Collator;
import java.util.Locale;

public class InputMethodSubtypePreference extends SwitchWithNoTextPreference {
    private final boolean mIsSystemLanguage;
    private final boolean mIsSystemLocale;

    public InputMethodSubtypePreference(Context context, InputMethodSubtype inputMethodSubtype, InputMethodInfo inputMethodInfo) {
        this(context, inputMethodInfo.getId() + inputMethodSubtype.hashCode(), InputMethodAndSubtypeUtil.getSubtypeLocaleNameAsSentence(inputMethodSubtype, context, inputMethodInfo), inputMethodSubtype.getLocaleObject(), context.getResources().getConfiguration().locale);
    }

    @VisibleForTesting
    InputMethodSubtypePreference(Context context, String str, CharSequence charSequence, Locale locale, Locale locale2) {
        super(context);
        boolean z = false;
        setPersistent(false);
        setKey(str);
        setTitle(charSequence);
        if (locale == null) {
            this.mIsSystemLocale = false;
            this.mIsSystemLanguage = false;
            return;
        }
        boolean equals = locale.equals(locale2);
        this.mIsSystemLocale = equals;
        this.mIsSystemLanguage = (equals || TextUtils.equals(locale.getLanguage(), locale2.getLanguage())) ? true : z;
    }

    public int compareTo(Preference preference, Collator collator) {
        int i = 0;
        if (this == preference) {
            return 0;
        }
        if (!(preference instanceof InputMethodSubtypePreference)) {
            return super.compareTo(preference);
        }
        InputMethodSubtypePreference inputMethodSubtypePreference = (InputMethodSubtypePreference) preference;
        if (this.mIsSystemLocale && !inputMethodSubtypePreference.mIsSystemLocale) {
            return -1;
        }
        if (!this.mIsSystemLocale && inputMethodSubtypePreference.mIsSystemLocale) {
            return 1;
        }
        if (this.mIsSystemLanguage && !inputMethodSubtypePreference.mIsSystemLanguage) {
            return -1;
        }
        if (!this.mIsSystemLanguage && inputMethodSubtypePreference.mIsSystemLanguage) {
            return 1;
        }
        CharSequence title = getTitle();
        CharSequence title2 = preference.getTitle();
        boolean isEmpty = TextUtils.isEmpty(title);
        boolean isEmpty2 = TextUtils.isEmpty(title2);
        if (!isEmpty && !isEmpty2) {
            return collator.compare(title.toString(), title2.toString());
        }
        int i2 = isEmpty ? -1 : 0;
        if (isEmpty2) {
            i = -1;
        }
        return i2 - i;
    }
}
