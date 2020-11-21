package com.oneplus.security.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import com.oneplus.security.utils.LogUtils;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, "safe.db", (SQLiteDatabase.CursorFactory) null, 8);
        this.mContext = context;
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        Log.d("DatabaseHelper", "create network control table");
        sQLiteDatabase.execSQL("create table if not exists tm_network_control (_id INTEGER PRIMARY KEY AUTOINCREMENT,pkg_name TEXT,app_name TEXT NOT NULL,app_flow_size INTEGER,app_uid INTEGER,wifi_state INTEGER,data_state INTEGER);");
        initTrafficNetworkControl(sQLiteDatabase);
        sQLiteDatabase.execSQL("create table if not exists intercept_logs (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,number TEXT,lookupuri TEXT,intercepttime TEXT,location TEXT,contactid INTEGER,subid INTEGER,yulorename TEXT,yuloretype INTEGER );");
        sQLiteDatabase.execSQL("create table if not exists network_restrict (_id INTEGER PRIMARY KEY AUTOINCREMENT,pkg TEXT,mobile INTEGER,wlan INTEGER);");
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Log.d("DatabaseHelper", "------onUpgrade---------oldVersion:" + i + "-------newVersion=" + i2);
        if (i < 2) {
            sQLiteDatabase.execSQL("create table if not exists intercept_logs (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,number TEXT,lookupuri TEXT,intercepttime TEXT,location TEXT,contactid INTEGER,subid INTEGER,yulorename TEXT,yuloretype INTEGER );");
        }
        if (i < 3) {
            sQLiteDatabase.execSQL("create table if not exists network_restrict (_id INTEGER PRIMARY KEY AUTOINCREMENT,pkg TEXT,mobile INTEGER,wlan INTEGER);");
        }
        if (i < 8) {
            sQLiteDatabase.execSQL("ALTER TABLE intercept_logs ADD yulorename TEXT");
            sQLiteDatabase.execSQL("ALTER TABLE intercept_logs ADD yuloretype INTEGER ");
            LogUtils.d("DatabaseHelper", "Add yulore name and type colums ");
        }
    }

    private void initTrafficNetworkControl(SQLiteDatabase sQLiteDatabase) {
        boolean z;
        PackageManager packageManager = this.mContext.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        HashMap hashMap = new HashMap();
        try {
            sQLiteDatabase.beginTransaction();
            for (int i = 0; i < installedPackages.size(); i++) {
                String[] strArr = packageManager.getPackageInfo(installedPackages.get(i).applicationInfo.packageName, 4096).requestedPermissions;
                int length = strArr != null ? strArr.length : 0;
                int i2 = 0;
                while (true) {
                    if (length <= 0 || i2 >= length) {
                        z = false;
                    } else if (strArr[i2].equals("android.permission.INTERNET")) {
                        z = true;
                        break;
                    } else {
                        i2++;
                    }
                }
                z = false;
                if (z && ((ContentValues) hashMap.get(Integer.valueOf(installedPackages.get(i).applicationInfo.uid))) == null) {
                    String str = installedPackages.get(i).sharedUserId;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("app_name", (String) installedPackages.get(i).applicationInfo.loadLabel(packageManager));
                    Log.d("DatabaseHelper", "XXXXXXXXXXXXXXXXXXXXXXXXX " + installedPackages.get(i).applicationInfo.packageName);
                    if (!TextUtils.isEmpty(str) && str.equals("android.uid.system")) {
                        contentValues.put("pkg_name", installedPackages.get(i).applicationInfo.packageName);
                    } else if (!installedPackages.get(i).applicationInfo.packageName.equals("com.oupeng.max.sdk")) {
                        contentValues.put("pkg_name", installedPackages.get(i).applicationInfo.packageName);
                    } else {
                        Log.d("DatabaseHelper", "XXXXXXXXXXXXXXXXXXXXXXXXX");
                        Log.d("DatabaseHelper", "XXXXXXXXXXXXXXXXXXXXXXXXX " + installedPackages.get(i).applicationInfo.packageName);
                    }
                    contentValues.put("wifi_state", (Integer) 1);
                    contentValues.put("data_state", (Integer) 1);
                    contentValues.put("app_flow_size", (Integer) 0);
                    contentValues.put("app_uid", Integer.valueOf(installedPackages.get(i).applicationInfo.uid));
                    hashMap.put(Integer.valueOf(installedPackages.get(i).applicationInfo.uid), contentValues);
                    sQLiteDatabase.insert("tm_network_control", "_id", contentValues);
                }
            }
            hashMap.clear();
            sQLiteDatabase.setTransactionSuccessful();
            sQLiteDatabase.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
