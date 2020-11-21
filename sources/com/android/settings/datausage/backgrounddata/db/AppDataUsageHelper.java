package com.android.settings.datausage.backgrounddata.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppDataUsageHelper extends SQLiteOpenHelper {
    public AppDataUsageHelper(Context context) {
        super(context, "op_app_datausage.db", (SQLiteDatabase.CursorFactory) null, 2);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table background_data(id integer primary key autoincrement,package_name text,uid integer,type integer DEFAULT 2)");
        Log.d("AppDataUsageHelper", "onCreate");
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("drop table if exists background_data");
        onCreate(sQLiteDatabase);
    }
}
