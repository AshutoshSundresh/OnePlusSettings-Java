package com.google.tagmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import java.util.HashSet;
import java.util.concurrent.Executor;

class DataLayerPersistentStoreImpl {
    private static final String CREATE_MAPS_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( '%s' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '%s' STRING NOT NULL, '%s' BLOB NOT NULL, '%s' INTEGER NOT NULL);", "datalayer", "ID", "key", "value", "expires");
    private final Context mContext;

    DataLayerPersistentStoreImpl(Context context, Clock clock, String str, int i, Executor executor) {
        this.mContext = context;
        new DatabaseHelper(this.mContext, str);
    }

    class DatabaseHelper extends SQLiteOpenHelper {
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }

        DatabaseHelper(Context context, String str) {
            super(context, str, (SQLiteDatabase.CursorFactory) null, 1);
        }

        /* JADX WARNING: Can't wrap try/catch for region: R(4:7|8|(1:10)|11) */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x003e, code lost:
            return r9;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x003f, code lost:
            if (r0 != null) goto L_0x0041;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0041, code lost:
            r0.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0044, code lost:
            throw r9;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0023, code lost:
            r9 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
            com.google.tagmanager.Log.w("Error querying for table " + r10);
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0025 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean tablePresent(java.lang.String r10, android.database.sqlite.SQLiteDatabase r11) {
            /*
                r9 = this;
                r9 = 0
                r0 = 0
                java.lang.String r2 = "SQLITE_MASTER"
                java.lang.String r1 = "name"
                java.lang.String[] r3 = new java.lang.String[]{r1}     // Catch:{ SQLiteException -> 0x0025 }
                java.lang.String r4 = "name=?"
                r1 = 1
                java.lang.String[] r5 = new java.lang.String[r1]     // Catch:{ SQLiteException -> 0x0025 }
                r5[r9] = r10     // Catch:{ SQLiteException -> 0x0025 }
                r6 = 0
                r7 = 0
                r8 = 0
                r1 = r11
                android.database.Cursor r0 = r1.query(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ SQLiteException -> 0x0025 }
                boolean r9 = r0.moveToFirst()     // Catch:{ SQLiteException -> 0x0025 }
                if (r0 == 0) goto L_0x0022
                r0.close()
            L_0x0022:
                return r9
            L_0x0023:
                r9 = move-exception
                goto L_0x003f
            L_0x0025:
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0023 }
                r11.<init>()     // Catch:{ all -> 0x0023 }
                java.lang.String r1 = "Error querying for table "
                r11.append(r1)     // Catch:{ all -> 0x0023 }
                r11.append(r10)     // Catch:{ all -> 0x0023 }
                java.lang.String r10 = r11.toString()     // Catch:{ all -> 0x0023 }
                com.google.tagmanager.Log.w(r10)     // Catch:{ all -> 0x0023 }
                if (r0 == 0) goto L_0x003e
                r0.close()
            L_0x003e:
                return r9
            L_0x003f:
                if (r0 == 0) goto L_0x0044
                r0.close()
            L_0x0044:
                throw r9
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.tagmanager.DataLayerPersistentStoreImpl.DatabaseHelper.tablePresent(java.lang.String, android.database.sqlite.SQLiteDatabase):boolean");
        }

        public SQLiteDatabase getWritableDatabase() {
            SQLiteDatabase sQLiteDatabase;
            try {
                sQLiteDatabase = super.getWritableDatabase();
            } catch (SQLiteException unused) {
                DataLayerPersistentStoreImpl.this.mContext.getDatabasePath("google_tagmanager.db").delete();
                sQLiteDatabase = null;
            }
            return sQLiteDatabase == null ? super.getWritableDatabase() : sQLiteDatabase;
        }

        public void onOpen(SQLiteDatabase sQLiteDatabase) {
            if (Build.VERSION.SDK_INT < 15) {
                Cursor rawQuery = sQLiteDatabase.rawQuery("PRAGMA journal_mode=memory", null);
                try {
                    rawQuery.moveToFirst();
                } finally {
                    rawQuery.close();
                }
            }
            if (!tablePresent("datalayer", sQLiteDatabase)) {
                sQLiteDatabase.execSQL(DataLayerPersistentStoreImpl.CREATE_MAPS_TABLE);
            } else {
                validateColumnsPresent(sQLiteDatabase);
            }
        }

        /* JADX INFO: finally extract failed */
        private void validateColumnsPresent(SQLiteDatabase sQLiteDatabase) {
            String[] columnNames;
            Cursor rawQuery = sQLiteDatabase.rawQuery("SELECT * FROM datalayer WHERE 0", null);
            HashSet hashSet = new HashSet();
            try {
                for (String str : rawQuery.getColumnNames()) {
                    hashSet.add(str);
                }
                rawQuery.close();
                if (!hashSet.remove("key") || !hashSet.remove("value") || !hashSet.remove("ID") || !hashSet.remove("expires")) {
                    throw new SQLiteException("Database column missing");
                } else if (!hashSet.isEmpty()) {
                    throw new SQLiteException("Database has extra columns");
                }
            } catch (Throwable th) {
                rawQuery.close();
                throw th;
            }
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            FutureApis.setOwnerOnlyReadWrite(sQLiteDatabase.getPath());
        }
    }
}
