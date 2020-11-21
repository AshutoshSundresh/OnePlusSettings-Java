package com.android.settings.fuelgauge.batterytip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.SparseLongArray;
import java.util.Collections;
import java.util.List;

public class BatteryDatabaseManager {
    private static BatteryDatabaseManager sSingleton;
    private AnomalyDatabaseHelper mDatabaseHelper;

    private BatteryDatabaseManager(Context context) {
        this.mDatabaseHelper = AnomalyDatabaseHelper.getInstance(context);
    }

    public static synchronized BatteryDatabaseManager getInstance(Context context) {
        BatteryDatabaseManager batteryDatabaseManager;
        synchronized (BatteryDatabaseManager.class) {
            if (sSingleton == null) {
                sSingleton = new BatteryDatabaseManager(context);
            }
            batteryDatabaseManager = sSingleton;
        }
        return batteryDatabaseManager;
    }

    public static void setUpForTest(BatteryDatabaseManager batteryDatabaseManager) {
        sSingleton = batteryDatabaseManager;
    }

    public synchronized boolean insertAnomaly(int i, String str, int i2, int i3, long j) {
        SQLiteDatabase writableDatabase;
        ContentValues contentValues;
        writableDatabase = this.mDatabaseHelper.getWritableDatabase();
        contentValues = new ContentValues();
        contentValues.put("uid", Integer.valueOf(i));
        contentValues.put("package_name", str);
        contentValues.put("anomaly_type", Integer.valueOf(i2));
        contentValues.put("anomaly_state", Integer.valueOf(i3));
        contentValues.put("time_stamp_ms", Long.valueOf(j));
        return writableDatabase.insertWithOnConflict("anomaly", null, contentValues, 4) != -1;
    }

    public synchronized void deleteAllAnomaliesBeforeTimeStamp(long j) {
        this.mDatabaseHelper.getWritableDatabase().delete("anomaly", "time_stamp_ms < ?", new String[]{String.valueOf(j)});
    }

    public synchronized void updateAnomalies(List<AppInfo> list, int i) {
        if (!list.isEmpty()) {
            int size = list.size();
            String[] strArr = new String[size];
            for (int i2 = 0; i2 < size; i2++) {
                strArr[i2] = list.get(i2).packageName;
            }
            SQLiteDatabase writableDatabase = this.mDatabaseHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("anomaly_state", Integer.valueOf(i));
            writableDatabase.update("anomaly", contentValues, "package_name IN (" + TextUtils.join(",", Collections.nCopies(list.size(), "?")) + ")", strArr);
        }
    }

    public synchronized SparseLongArray queryActionTime(int i) {
        SparseLongArray sparseLongArray;
        sparseLongArray = new SparseLongArray();
        Cursor query = this.mDatabaseHelper.getReadableDatabase().query("action", new String[]{"uid", "time_stamp_ms"}, "action_type = ? ", new String[]{String.valueOf(i)}, null, null, null);
        try {
            int columnIndex = query.getColumnIndex("uid");
            int columnIndex2 = query.getColumnIndex("time_stamp_ms");
            while (query.moveToNext()) {
                sparseLongArray.append(query.getInt(columnIndex), query.getLong(columnIndex2));
            }
            if (query != null) {
                query.close();
            }
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        return sparseLongArray;
        throw th;
    }

    public synchronized boolean insertAction(int i, int i2, String str, long j) {
        SQLiteDatabase writableDatabase;
        ContentValues contentValues;
        writableDatabase = this.mDatabaseHelper.getWritableDatabase();
        contentValues = new ContentValues();
        contentValues.put("uid", Integer.valueOf(i2));
        contentValues.put("package_name", str);
        contentValues.put("action_type", Integer.valueOf(i));
        contentValues.put("time_stamp_ms", Long.valueOf(j));
        return writableDatabase.insertWithOnConflict("action", null, contentValues, 5) != -1;
    }

    public synchronized boolean deleteAction(int i, int i2, String str) {
        boolean z;
        z = false;
        if (this.mDatabaseHelper.getWritableDatabase().delete("action", "action_type = ? AND uid = ? AND package_name = ? ", new String[]{String.valueOf(i), String.valueOf(i2), String.valueOf(str)}) != 0) {
            z = true;
        }
        return z;
    }
}
