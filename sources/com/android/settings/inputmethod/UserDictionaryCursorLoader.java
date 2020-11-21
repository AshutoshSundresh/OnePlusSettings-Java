package com.android.settings.inputmethod;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.UserDictionary;
import android.util.ArraySet;
import androidx.loader.content.CursorLoader;
import java.util.Locale;
import java.util.Objects;

public class UserDictionaryCursorLoader extends CursorLoader {
    static final String[] QUERY_PROJECTION = {"_id", "word", "shortcut"};
    private final String mLocale;

    public UserDictionaryCursorLoader(Context context, String str) {
        super(context);
        this.mLocale = str;
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public Cursor loadInBackground() {
        Cursor cursor;
        MatrixCursor matrixCursor = new MatrixCursor(QUERY_PROJECTION);
        if ("".equals(this.mLocale)) {
            cursor = getContext().getContentResolver().query(UserDictionary.Words.CONTENT_URI, QUERY_PROJECTION, "locale is null", null, "UPPER(word)");
        } else {
            String str = this.mLocale;
            if (str == null) {
                str = Locale.getDefault().toString();
            }
            cursor = getContext().getContentResolver().query(UserDictionary.Words.CONTENT_URI, QUERY_PROJECTION, "locale=?", new String[]{str}, "UPPER(word)");
        }
        ArraySet arraySet = new ArraySet();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int i = cursor.getInt(0);
            String string = cursor.getString(1);
            String string2 = cursor.getString(2);
            int hash = Objects.hash(string, string2);
            if (!arraySet.contains(Integer.valueOf(hash))) {
                arraySet.add(Integer.valueOf(hash));
                matrixCursor.addRow(new Object[]{Integer.valueOf(i), string, string2});
            }
            cursor.moveToNext();
        }
        return matrixCursor;
    }
}
