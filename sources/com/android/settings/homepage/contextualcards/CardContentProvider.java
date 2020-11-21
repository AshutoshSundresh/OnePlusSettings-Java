package com.android.settings.homepage.contextualcards;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settings.C0005R$bool;
import com.android.settingslib.utils.ThreadUtils;

public class CardContentProvider extends ContentProvider {
    public static final Uri DELETE_CARD_URI = new Uri.Builder().scheme("content").authority("com.android.settings.homepage.CardContentProvider").appendPath("dismissed_timestamp").build();
    public static final Uri REFRESH_CARD_URI = new Uri.Builder().scheme("content").authority("com.android.settings.homepage.CardContentProvider").appendPath("cards").build();
    private static final UriMatcher URI_MATCHER;
    private CardDatabaseHelper mDBHelper;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        URI_MATCHER = uriMatcher;
        uriMatcher.addURI("com.android.settings.homepage.CardContentProvider", "cards", 100);
    }

    public boolean onCreate() {
        this.mDBHelper = CardDatabaseHelper.getInstance(getContext());
        return true;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        bulkInsert(uri, new ContentValues[]{contentValues});
        return uri;
    }

    public int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        String str;
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        SQLiteDatabase writableDatabase = this.mDBHelper.getWritableDatabase();
        boolean z = getContext().getResources().getBoolean(C0005R$bool.config_keep_contextual_card_dismissal_timestamp);
        ArrayMap arrayMap = new ArrayMap();
        try {
            maybeEnableStrictMode();
            String tableFromMatch = getTableFromMatch(uri);
            writableDatabase.beginTransaction();
            if (z) {
                str = "name";
                Cursor query = writableDatabase.query(tableFromMatch, new String[]{"name", "dismissed_timestamp"}, "dismissed_timestamp IS NOT NULL", null, null, null, null);
                try {
                    query.moveToFirst();
                    while (!query.isAfterLast()) {
                        arrayMap.put(query.getString(query.getColumnIndex(str)), Long.valueOf(query.getLong(query.getColumnIndex("dismissed_timestamp"))));
                        query.moveToNext();
                    }
                    if (query != null) {
                        query.close();
                    }
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            } else {
                str = "name";
            }
            String str2 = null;
            writableDatabase.delete(tableFromMatch, null, null);
            int length = contentValuesArr.length;
            int i = 0;
            int i2 = 0;
            while (i < length) {
                ContentValues contentValues = contentValuesArr[i];
                if (z) {
                    String obj = contentValues.get(str).toString();
                    if (arrayMap.containsKey(obj)) {
                        contentValues.put("dismissed_timestamp", (Long) arrayMap.get(obj));
                        Log.d("CardContentProvider", "Replace dismissed time: " + obj);
                        str2 = null;
                    }
                }
                if (writableDatabase.insert(tableFromMatch, str2, contentValues) != -1) {
                    i2++;
                } else {
                    Log.e("CardContentProvider", "The row " + contentValues.getAsString(str) + " insertion failed! Please check your data.");
                }
                i++;
                str2 = null;
            }
            writableDatabase.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            return i2;
            throw th;
        } finally {
            writableDatabase.endTransaction();
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }

    public int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException("delete operation not supported currently.");
    }

    public String getType(Uri uri) {
        throw new UnsupportedOperationException("getType operation not supported currently.");
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        try {
            maybeEnableStrictMode();
            SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
            sQLiteQueryBuilder.setTables(getTableFromMatch(uri));
            Cursor query = sQLiteQueryBuilder.query(this.mDBHelper.getReadableDatabase(), strArr, str, strArr2, null, null, str2);
            query.setNotificationUri(getContext().getContentResolver(), uri);
            return query;
        } finally {
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException("update operation not supported currently.");
    }

    /* access modifiers changed from: package-private */
    public void maybeEnableStrictMode() {
        if (Build.IS_DEBUGGABLE && ThreadUtils.isMainThread()) {
            enableStrictMode();
        }
    }

    /* access modifiers changed from: package-private */
    public void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
    }

    /* access modifiers changed from: package-private */
    public String getTableFromMatch(Uri uri) {
        if (URI_MATCHER.match(uri) == 100) {
            return "cards";
        }
        throw new IllegalArgumentException("Unknown Uri format: " + uri);
    }
}
