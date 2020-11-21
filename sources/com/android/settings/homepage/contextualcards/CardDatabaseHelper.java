package com.android.settings.homepage.contextualcards;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CardDatabaseHelper extends SQLiteOpenHelper {
    static CardDatabaseHelper sCardDatabaseHelper;

    public CardDatabaseHelper(Context context) {
        super(context, "homepage_cards.db", (SQLiteDatabase.CursorFactory) null, 7);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE cards(name TEXT NOT NULL PRIMARY KEY, type INTEGER NOT NULL, score DOUBLE NOT NULL, slice_uri TEXT, category INTEGER DEFAULT 0, package_name TEXT NOT NULL, app_version INTEGER NOT NULL, dismissed_timestamp INTEGER);");
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i < i2) {
            Log.d("CardDatabaseHelper", "Reconstructing DB from " + i + " to " + i2);
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS cards");
            onCreate(sQLiteDatabase);
        }
    }

    public static synchronized CardDatabaseHelper getInstance(Context context) {
        CardDatabaseHelper cardDatabaseHelper;
        synchronized (CardDatabaseHelper.class) {
            if (sCardDatabaseHelper == null) {
                sCardDatabaseHelper = new CardDatabaseHelper(context.getApplicationContext());
            }
            cardDatabaseHelper = sCardDatabaseHelper;
        }
        return cardDatabaseHelper;
    }
}
