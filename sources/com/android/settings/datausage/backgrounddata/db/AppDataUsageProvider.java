package com.android.settings.datausage.backgrounddata.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.settings.datausage.backgrounddata.utils.BackgroundDataUtils;
import com.oneplus.settings.utils.OPUtils;

public class AppDataUsageProvider extends ContentProvider {
    private static UriMatcher mUriMatcher;
    private AppDataUsageHelper mHelper;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        mUriMatcher = uriMatcher;
        uriMatcher.addURI("com.android.settings.app.datausage", "background_data", 0);
        mUriMatcher.addURI("com.android.settings.app.datausage", "background_data/#", 1);
    }

    public boolean onCreate() {
        this.mHelper = new AppDataUsageHelper(getContext());
        try {
            if (!OPUtils.isSupportUss()) {
                return true;
            }
            addPackageMonitor();
            return true;
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            Process.killProcess(Process.myPid());
            return true;
        }
    }

    private void addPackageMonitor() {
        new PackageMonitor() {
            /* class com.android.settings.datausage.backgrounddata.db.AppDataUsageProvider.AnonymousClass1 */

            public void onPackageRemoved(String str, int i) {
                synchronized (this) {
                    BackgroundDataUtils.deleteBackgroundDataApp(AppDataUsageProvider.this.getContext(), str, i);
                    Log.d("AppDataUsageProvider", "onPackageRemoved: packageName = " + str + ", uid = " + i);
                }
            }

            public void onPackageAdded(String str, int i) {
                synchronized (this) {
                    if (BackgroundDataUtils.isQueryDataEmpty(AppDataUsageProvider.this.getContext(), str, i) && BackgroundDataUtils.isHaveInternetPermission(AppDataUsageProvider.this.getContext(), str)) {
                        BackgroundDataUtils.addBackgroundDataApp(AppDataUsageProvider.this.getContext(), str, i);
                    }
                    Log.d("AppDataUsageProvider", "onPackageAdded: packageName = " + str + ", uid = " + i);
                }
            }
        }.register(getContext(), BackgroundThread.getHandler().getLooper(), UserHandle.ALL, true);
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteDatabase sqliteDatabase = BackgroundDataUtils.getSqliteDatabase(this.mHelper);
        if (sqliteDatabase == null) {
            return null;
        }
        int match = mUriMatcher.match(uri);
        if (match == 0) {
            return sqliteDatabase.query("background_data", strArr, str, strArr2, null, null, str2);
        }
        if (match != 1) {
            return null;
        }
        return sqliteDatabase.query("background_data", strArr, "id = ?", new String[]{uri.getPathSegments().get(1)}, null, null, str2);
    }

    public String getType(Uri uri) {
        int match = mUriMatcher.match(uri);
        if (match != 0) {
            return match != 1 ? "" : "vnd.android.cursor.item/com.android.settings.app.datausage.background_data";
        }
        return "vnd.android.cursor.dir/com.android.settings.app.datausage.background_data";
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase sqliteDatabase = BackgroundDataUtils.getSqliteDatabase(this.mHelper);
        if (sqliteDatabase == null) {
            return null;
        }
        int match = mUriMatcher.match(uri);
        if ((match == 0 || match == 1) && BackgroundDataUtils.isQueryDataEmpty(sqliteDatabase, contentValues) && BackgroundDataUtils.isHaveInternetPermission(getContext(), contentValues)) {
            sqliteDatabase.insert("background_data", null, contentValues);
        }
        return null;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        SQLiteDatabase sqliteDatabase = BackgroundDataUtils.getSqliteDatabase(this.mHelper);
        if (sqliteDatabase == null) {
            return -1;
        }
        int match = mUriMatcher.match(uri);
        if (match == 0) {
            return sqliteDatabase.delete("background_data", str, strArr);
        }
        if (match != 1) {
            return 0;
        }
        return sqliteDatabase.delete("background_data", "id = ?", new String[]{uri.getPathSegments().get(1)});
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        SQLiteDatabase sqliteDatabase = BackgroundDataUtils.getSqliteDatabase(this.mHelper);
        if (sqliteDatabase == null) {
            return -1;
        }
        int match = mUriMatcher.match(uri);
        if (match == 0) {
            return sqliteDatabase.update("background_data", contentValues, str, strArr);
        }
        if (match != 1) {
            return 0;
        }
        return sqliteDatabase.update("background_data", contentValues, "id = ?", new String[]{uri.getPathSegments().get(1)});
    }
}
