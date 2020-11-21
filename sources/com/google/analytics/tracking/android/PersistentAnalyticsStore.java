package com.google.analytics.tracking.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import com.google.android.gms.analytics.internal.Command;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.http.impl.client.DefaultHttpClient;

/* access modifiers changed from: package-private */
public class PersistentAnalyticsStore implements AnalyticsStore {
    private static final String CREATE_HITS_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( '%s' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '%s' INTEGER NOT NULL, '%s' TEXT NOT NULL, '%s' TEXT NOT NULL, '%s' INTEGER);", HITS_TABLE, HIT_ID, HIT_TIME, HIT_URL, HIT_STRING, HIT_APP_ID);
    static final String HITS_TABLE = "hits2";
    static final String HIT_APP_ID = "hit_app_id";
    static final String HIT_ID = "hit_id";
    static final String HIT_STRING = "hit_string";
    static final String HIT_TIME = "hit_time";
    static final String HIT_URL = "hit_url";
    private Clock mClock;
    private final Context mContext;
    private final String mDatabaseName;
    private final AnalyticsDatabaseHelper mDbHelper;
    private volatile Dispatcher mDispatcher;
    private long mLastDeleteStaleHitsTime;
    private final AnalyticsStoreStateListener mListener;

    PersistentAnalyticsStore(AnalyticsStoreStateListener analyticsStoreStateListener, Context context) {
        this(analyticsStoreStateListener, context, "google_analytics_v2.db");
    }

    PersistentAnalyticsStore(AnalyticsStoreStateListener analyticsStoreStateListener, Context context, String str) {
        this.mContext = context.getApplicationContext();
        this.mDatabaseName = str;
        this.mListener = analyticsStoreStateListener;
        this.mClock = new Clock(this) {
            /* class com.google.analytics.tracking.android.PersistentAnalyticsStore.AnonymousClass1 */

            @Override // com.google.analytics.tracking.android.Clock
            public long currentTimeMillis() {
                return System.currentTimeMillis();
            }
        };
        this.mDbHelper = new AnalyticsDatabaseHelper(this.mContext, this.mDatabaseName);
        this.mDispatcher = new SimpleNetworkDispatcher(new DefaultHttpClient(), this.mContext);
        this.mLastDeleteStaleHitsTime = 0;
    }

    public void setClock(Clock clock) {
        this.mClock = clock;
    }

    public AnalyticsDatabaseHelper getDbHelper() {
        return this.mDbHelper;
    }

    /* access modifiers changed from: package-private */
    public void setDispatcher(Dispatcher dispatcher) {
        this.mDispatcher = dispatcher;
    }

    @Override // com.google.analytics.tracking.android.AnalyticsStore
    public void clearHits(long j) {
        SQLiteDatabase writableDatabase = getWritableDatabase("Error opening database for clearHits");
        if (writableDatabase != null) {
            boolean z = false;
            if (j == 0) {
                writableDatabase.delete(HITS_TABLE, null, null);
            } else {
                writableDatabase.delete(HITS_TABLE, "hit_app_id = ?", new String[]{Long.valueOf(j).toString()});
            }
            AnalyticsStoreStateListener analyticsStoreStateListener = this.mListener;
            if (getNumStoredHits() == 0) {
                z = true;
            }
            analyticsStoreStateListener.reportStoreIsEmpty(z);
        }
    }

    @Override // com.google.analytics.tracking.android.AnalyticsStore
    public void putHit(Map<String, String> map, long j, String str, Collection<Command> collection) {
        deleteStaleHits();
        removeOldHitIfFull();
        fillVersionParameter(map, collection);
        writeHitToDatabase(map, j, str);
    }

    private void fillVersionParameter(Map<String, String> map, Collection<Command> collection) {
        if (collection != null) {
            for (Command command : collection) {
                if ("appendVersion".equals(command.getId())) {
                    map.put("_v", command.getValue());
                    return;
                }
            }
        }
    }

    private void removeOldHitIfFull() {
        int numStoredHits = (getNumStoredHits() - 2000) + 1;
        if (numStoredHits > 0) {
            List<String> peekHitIds = peekHitIds(numStoredHits);
            Log.v("Store full, deleting " + peekHitIds.size() + " hits to make room.");
            deleteHits((String[]) peekHitIds.toArray(new String[0]));
        }
    }

    private void writeHitToDatabase(Map<String, String> map, long j, String str) {
        SQLiteDatabase writableDatabase = getWritableDatabase("Error opening database for putHit");
        if (writableDatabase != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(HIT_STRING, generateHitString(map));
            contentValues.put(HIT_TIME, Long.valueOf(j));
            long j2 = 0;
            if (map.containsKey("AppUID")) {
                try {
                    j2 = Long.parseLong(map.get("AppUID"));
                } catch (NumberFormatException unused) {
                }
            }
            contentValues.put(HIT_APP_ID, Long.valueOf(j2));
            if (str == null) {
                str = "http://www.google-analytics.com/collect";
            }
            if (str.length() == 0) {
                Log.w("Empty path: not sending hit");
                return;
            }
            contentValues.put(HIT_URL, str);
            try {
                writableDatabase.insert(HITS_TABLE, null, contentValues);
                this.mListener.reportStoreIsEmpty(false);
            } catch (SQLiteException unused2) {
                Log.w("Error storing hit");
            }
        }
    }

    static String generateHitString(Map<String, String> map) {
        ArrayList arrayList = new ArrayList(map.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            arrayList.add(HitBuilder.encode(entry.getKey()) + "=" + HitBuilder.encode(entry.getValue()));
        }
        return TextUtils.join("&", arrayList);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x004e, code lost:
        if (r13 != null) goto L_0x006e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x006c, code lost:
        if (0 == 0) goto L_0x0071;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x006e, code lost:
        r13.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0071, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<java.lang.String> peekHitIds(int r14) {
        /*
        // Method dump skipped, instructions count: 120
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.tracking.android.PersistentAnalyticsStore.peekHitIds(int):java.util.List");
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x0108 A[Catch:{ all -> 0x00dd }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0122 A[DONT_GENERATE] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x014d A[DONT_GENERATE] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0120 A[EDGE_INSN: B:67:0x0120->B:47:0x0120 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.google.analytics.tracking.android.Hit> peekHits(int r17) {
        /*
        // Method dump skipped, instructions count: 343
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.tracking.android.PersistentAnalyticsStore.peekHits(int):java.util.List");
    }

    /* access modifiers changed from: package-private */
    public void setLastDeleteStaleHitsTime(long j) {
        this.mLastDeleteStaleHitsTime = j;
    }

    /* access modifiers changed from: package-private */
    public int deleteStaleHits() {
        long currentTimeMillis = this.mClock.currentTimeMillis();
        boolean z = false;
        if (currentTimeMillis <= this.mLastDeleteStaleHitsTime + 86400000) {
            return 0;
        }
        this.mLastDeleteStaleHitsTime = currentTimeMillis;
        SQLiteDatabase writableDatabase = getWritableDatabase("Error opening database for deleteStaleHits.");
        if (writableDatabase == null) {
            return 0;
        }
        int delete = writableDatabase.delete(HITS_TABLE, "HIT_TIME < ?", new String[]{Long.toString(this.mClock.currentTimeMillis() - 2592000000L)});
        AnalyticsStoreStateListener analyticsStoreStateListener = this.mListener;
        if (getNumStoredHits() == 0) {
            z = true;
        }
        analyticsStoreStateListener.reportStoreIsEmpty(z);
        return delete;
    }

    /* access modifiers changed from: package-private */
    @Deprecated
    public void deleteHits(Collection<Hit> collection) {
        if (collection == null || collection.isEmpty()) {
            Log.w("Empty/Null collection passed to deleteHits.");
            return;
        }
        String[] strArr = new String[collection.size()];
        int i = 0;
        for (Hit hit : collection) {
            strArr[i] = String.valueOf(hit.getHitId());
            i++;
        }
        deleteHits(strArr);
    }

    /* access modifiers changed from: package-private */
    public void deleteHits(String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            Log.w("Empty hitIds passed to deleteHits.");
            return;
        }
        SQLiteDatabase writableDatabase = getWritableDatabase("Error opening database for deleteHits.");
        if (writableDatabase != null) {
            boolean z = true;
            try {
                writableDatabase.delete(HITS_TABLE, String.format("HIT_ID in (%s)", TextUtils.join(",", Collections.nCopies(strArr.length, "?"))), strArr);
                AnalyticsStoreStateListener analyticsStoreStateListener = this.mListener;
                if (getNumStoredHits() != 0) {
                    z = false;
                }
                analyticsStoreStateListener.reportStoreIsEmpty(z);
            } catch (SQLiteException unused) {
                Log.w("Error deleting hits " + strArr);
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001f, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002a, code lost:
        if (0 == 0) goto L_0x002d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002d, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001d, code lost:
        if (r1 != null) goto L_0x001f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getNumStoredHits() {
        /*
            r4 = this;
            java.lang.String r0 = "Error opening database for getNumStoredHits."
            android.database.sqlite.SQLiteDatabase r4 = r4.getWritableDatabase(r0)
            r0 = 0
            if (r4 != 0) goto L_0x000a
            return r0
        L_0x000a:
            r1 = 0
            java.lang.String r2 = "SELECT COUNT(*) from hits2"
            android.database.Cursor r1 = r4.rawQuery(r2, r1)     // Catch:{ SQLiteException -> 0x0025 }
            boolean r4 = r1.moveToFirst()     // Catch:{ SQLiteException -> 0x0025 }
            if (r4 == 0) goto L_0x001d
            long r2 = r1.getLong(r0)     // Catch:{ SQLiteException -> 0x0025 }
            int r4 = (int) r2
            r0 = r4
        L_0x001d:
            if (r1 == 0) goto L_0x002d
        L_0x001f:
            r1.close()
            goto L_0x002d
        L_0x0023:
            r4 = move-exception
            goto L_0x002e
        L_0x0025:
            java.lang.String r4 = "Error getting numStoredHits"
            com.google.analytics.tracking.android.Log.w(r4)     // Catch:{ all -> 0x0023 }
            if (r1 == 0) goto L_0x002d
            goto L_0x001f
        L_0x002d:
            return r0
        L_0x002e:
            if (r1 == 0) goto L_0x0033
            r1.close()
        L_0x0033:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.tracking.android.PersistentAnalyticsStore.getNumStoredHits():int");
    }

    @Override // com.google.analytics.tracking.android.AnalyticsStore
    public void dispatch() {
        Log.v("Dispatch running...");
        if (this.mDispatcher.okToDispatch()) {
            List<Hit> peekHits = peekHits(40);
            if (peekHits.isEmpty()) {
                Log.v("...nothing to dispatch");
                this.mListener.reportStoreIsEmpty(true);
                return;
            }
            int dispatchHits = this.mDispatcher.dispatchHits(peekHits);
            Log.v("sent " + dispatchHits + " of " + peekHits.size() + " hits");
            deleteHits(peekHits.subList(0, Math.min(dispatchHits, peekHits.size())));
            if (dispatchHits == peekHits.size() && getNumStoredHits() > 0) {
                GAServiceManager.getInstance().dispatchLocalHits();
            }
        }
    }

    @Override // com.google.analytics.tracking.android.AnalyticsStore
    public Dispatcher getDispatcher() {
        return this.mDispatcher;
    }

    /* access modifiers changed from: package-private */
    public AnalyticsDatabaseHelper getHelper() {
        return this.mDbHelper;
    }

    private SQLiteDatabase getWritableDatabase(String str) {
        try {
            return this.mDbHelper.getWritableDatabase();
        } catch (SQLiteException unused) {
            Log.w(str);
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public class AnalyticsDatabaseHelper extends SQLiteOpenHelper {
        private boolean mBadDatabase;
        private long mLastDatabaseCheckTime = 0;

        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }

        AnalyticsDatabaseHelper(Context context, String str) {
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
            com.google.analytics.tracking.android.Log.w("Error querying for table " + r10);
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
                com.google.analytics.tracking.android.Log.w(r10)     // Catch:{ all -> 0x0023 }
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
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.tracking.android.PersistentAnalyticsStore.AnalyticsDatabaseHelper.tablePresent(java.lang.String, android.database.sqlite.SQLiteDatabase):boolean");
        }

        public SQLiteDatabase getWritableDatabase() {
            if (!this.mBadDatabase || this.mLastDatabaseCheckTime + 3600000 <= PersistentAnalyticsStore.this.mClock.currentTimeMillis()) {
                SQLiteDatabase sQLiteDatabase = null;
                this.mBadDatabase = true;
                this.mLastDatabaseCheckTime = PersistentAnalyticsStore.this.mClock.currentTimeMillis();
                try {
                    sQLiteDatabase = super.getWritableDatabase();
                } catch (SQLiteException unused) {
                    PersistentAnalyticsStore.this.mContext.getDatabasePath(PersistentAnalyticsStore.this.mDatabaseName).delete();
                }
                if (sQLiteDatabase == null) {
                    sQLiteDatabase = super.getWritableDatabase();
                }
                this.mBadDatabase = false;
                return sQLiteDatabase;
            }
            throw new SQLiteException("Database creation failed");
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
            if (!tablePresent(PersistentAnalyticsStore.HITS_TABLE, sQLiteDatabase)) {
                sQLiteDatabase.execSQL(PersistentAnalyticsStore.CREATE_HITS_TABLE);
            } else {
                validateColumnsPresent(sQLiteDatabase);
            }
        }

        /* JADX INFO: finally extract failed */
        private void validateColumnsPresent(SQLiteDatabase sQLiteDatabase) {
            String[] columnNames;
            Cursor rawQuery = sQLiteDatabase.rawQuery("SELECT * FROM hits2 WHERE 0", null);
            HashSet hashSet = new HashSet();
            try {
                for (String str : rawQuery.getColumnNames()) {
                    hashSet.add(str);
                }
                rawQuery.close();
                if (!hashSet.remove(PersistentAnalyticsStore.HIT_ID) || !hashSet.remove(PersistentAnalyticsStore.HIT_URL) || !hashSet.remove(PersistentAnalyticsStore.HIT_STRING) || !hashSet.remove(PersistentAnalyticsStore.HIT_TIME)) {
                    throw new SQLiteException("Database column missing");
                }
                boolean z = !hashSet.remove(PersistentAnalyticsStore.HIT_APP_ID);
                if (!hashSet.isEmpty()) {
                    throw new SQLiteException("Database has extra columns");
                } else if (z) {
                    sQLiteDatabase.execSQL("ALTER TABLE hits2 ADD COLUMN hit_app_id");
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
