package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

public class UserDictionaryAddWordContents {
    private static final String[] HAS_WORD_PROJECTION = {"word"};
    private String mLocale;
    private final int mMode;
    private final String mOldShortcut;
    private final String mOldWord;
    private String mSavedShortcut;
    private String mSavedWord;
    private final EditText mShortcutEditText;
    private final EditText mWordEditText;

    UserDictionaryAddWordContents(View view, Bundle bundle) {
        EditText editText;
        this.mWordEditText = (EditText) view.findViewById(C0010R$id.user_dictionary_add_word_text);
        this.mShortcutEditText = (EditText) view.findViewById(C0010R$id.user_dictionary_add_shortcut);
        String string = bundle.getString("word");
        if (string != null) {
            this.mWordEditText.setText(string);
            EditText editText2 = this.mWordEditText;
            editText2.setSelection(editText2.getText().length());
        }
        String string2 = bundle.getString("shortcut");
        if (!(string2 == null || (editText = this.mShortcutEditText) == null)) {
            editText.setText(string2);
        }
        this.mMode = bundle.getInt("mode");
        this.mOldWord = bundle.getString("word");
        this.mOldShortcut = bundle.getString("shortcut");
        updateLocale(bundle.getString("locale"));
    }

    UserDictionaryAddWordContents(View view, UserDictionaryAddWordContents userDictionaryAddWordContents) {
        this.mWordEditText = (EditText) view.findViewById(C0010R$id.user_dictionary_add_word_text);
        this.mShortcutEditText = (EditText) view.findViewById(C0010R$id.user_dictionary_add_shortcut);
        this.mMode = 0;
        this.mOldWord = userDictionaryAddWordContents.mSavedWord;
        this.mOldShortcut = userDictionaryAddWordContents.mSavedShortcut;
        updateLocale(userDictionaryAddWordContents.getCurrentUserDictionaryLocale());
    }

    /* access modifiers changed from: package-private */
    public void updateLocale(String str) {
        if (str == null) {
            str = Locale.getDefault().toString();
        }
        this.mLocale = str;
    }

    /* access modifiers changed from: package-private */
    public void saveStateIntoBundle(Bundle bundle) {
        bundle.putString("word", this.mWordEditText.getText().toString());
        bundle.putString("originalWord", this.mOldWord);
        EditText editText = this.mShortcutEditText;
        if (editText != null) {
            bundle.putString("shortcut", editText.getText().toString());
        }
        String str = this.mOldShortcut;
        if (str != null) {
            bundle.putString("originalShortcut", str);
        }
        bundle.putString("locale", this.mLocale);
    }

    /* access modifiers changed from: package-private */
    public void delete(Context context) {
        if (this.mMode == 0 && !TextUtils.isEmpty(this.mOldWord)) {
            UserDictionarySettings.deleteWord(this.mOldWord, this.mOldShortcut, context.getContentResolver());
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0039, code lost:
        if (android.text.TextUtils.isEmpty(r1) != false) goto L_0x002b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int apply(android.content.Context r5, android.os.Bundle r6) {
        /*
        // Method dump skipped, instructions count: 124
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.inputmethod.UserDictionaryAddWordContents.apply(android.content.Context, android.os.Bundle):int");
    }

    private boolean hasWord(String str, Context context) {
        Cursor cursor;
        boolean z = true;
        if ("".equals(this.mLocale)) {
            cursor = context.getContentResolver().query(UserDictionary.Words.CONTENT_URI, HAS_WORD_PROJECTION, "word=? AND locale is null", new String[]{str}, null);
        } else {
            cursor = context.getContentResolver().query(UserDictionary.Words.CONTENT_URI, HAS_WORD_PROJECTION, "word=? AND locale=?", new String[]{str, this.mLocale}, null);
        }
        if (cursor == null) {
            if (cursor != null) {
                cursor.close();
            }
            return false;
        }
        try {
            if (cursor.getCount() <= 0) {
                z = false;
            }
            return z;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static class LocaleRenderer {
        private final String mDescription;

        public LocaleRenderer(Context context, String str) {
            if (str == null) {
                this.mDescription = context.getString(C0017R$string.user_dict_settings_more_languages);
            } else if ("".equals(str)) {
                this.mDescription = context.getString(C0017R$string.user_dict_settings_all_languages);
            } else {
                this.mDescription = Utils.createLocaleFromString(str).getDisplayName();
            }
        }

        public String toString() {
            return this.mDescription;
        }
    }

    private static void addLocaleDisplayNameToList(Context context, ArrayList<LocaleRenderer> arrayList, String str) {
        if (str != null) {
            arrayList.add(new LocaleRenderer(context, str));
        }
    }

    public ArrayList<LocaleRenderer> getLocalesList(Activity activity) {
        TreeSet<String> userDictionaryLocalesSet = UserDictionaryListPreferenceController.getUserDictionaryLocalesSet(activity);
        userDictionaryLocalesSet.remove(this.mLocale);
        String locale = Locale.getDefault().toString();
        userDictionaryLocalesSet.remove(locale);
        userDictionaryLocalesSet.remove("");
        ArrayList<LocaleRenderer> arrayList = new ArrayList<>();
        addLocaleDisplayNameToList(activity, arrayList, this.mLocale);
        if (!locale.equals(this.mLocale)) {
            addLocaleDisplayNameToList(activity, arrayList, locale);
        }
        Iterator<String> it = userDictionaryLocalesSet.iterator();
        while (it.hasNext()) {
            addLocaleDisplayNameToList(activity, arrayList, it.next());
        }
        if (!"".equals(this.mLocale)) {
            addLocaleDisplayNameToList(activity, arrayList, "");
        }
        arrayList.add(new LocaleRenderer(activity, null));
        return arrayList;
    }

    public String getCurrentUserDictionaryLocale() {
        return this.mLocale;
    }
}
