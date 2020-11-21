package com.android.settings.datausage.backgrounddata.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.datausage.backgrounddata.bean.BackgroundDataBean;
import com.android.settings.datausage.backgrounddata.db.AppDataUsageHelper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.codeaurora.internal.IExtTelephony;

public class BackgroundDataUtils {
    public static void setAppBackgroundDataType(Context context, String str, int i, int i2) {
        List<BackgroundDataBean> queryApp;
        BackgroundDataBean backgroundDataBean;
        SQLiteDatabase sqliteDatabase = getSqliteDatabase(context);
        if (sqliteDatabase != null && (queryApp = queryApp(sqliteDatabase, str, i)) != null) {
            if (queryApp.isEmpty()) {
                addBackgroundDataApp(sqliteDatabase, str, i, i2);
                backgroundDataBean = new BackgroundDataBean(str, i, i2);
            } else {
                updateBackgroundDataApp(sqliteDatabase, queryApp.get(0).getId(), i2);
                backgroundDataBean = new BackgroundDataBean(str, i, i2);
            }
            changeRoamingAppStatus(context, backgroundDataBean);
        }
    }

    public static void initAppBackgroundDataType(Context context) {
        if (context != null) {
            initAppBackgroundDataType(context, getSqliteDatabase(context));
        }
    }

    public static void initAppBackgroundDataType(Context context, SQLiteDatabase sQLiteDatabase) {
        if (context != null && sQLiteDatabase != null) {
            List<BackgroundDataBean> queryAllApp = queryAllApp(sQLiteDatabase);
            if (queryAllApp == null || queryAllApp.isEmpty()) {
                changeRoamingAppStatus(context, getAllInternetApp(context, sQLiteDatabase));
            }
        }
    }

    public static int getAppType(Context context, String str, int i) {
        SQLiteDatabase sqliteDatabase = getSqliteDatabase(context);
        if (sqliteDatabase == null) {
            return 2;
        }
        initAppBackgroundDataType(context, sqliteDatabase);
        List<BackgroundDataBean> queryApp = queryApp(sqliteDatabase, str, i);
        if (queryApp == null || queryApp.isEmpty()) {
            addBackgroundDataApp(sqliteDatabase, str, i, 2);
            return 2;
        }
        int type = queryApp.get(0).getType();
        if (type > 2 || type < 0) {
            return 2;
        }
        return type;
    }

    public static SQLiteDatabase getSqliteDatabase(Context context) {
        if (context == null) {
            return null;
        }
        try {
            return new AppDataUsageHelper(context).getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SQLiteDatabase getSqliteDatabase(AppDataUsageHelper appDataUsageHelper) {
        if (appDataUsageHelper == null) {
            return null;
        }
        try {
            return appDataUsageHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<BackgroundDataBean> queryAllApp(SQLiteDatabase sQLiteDatabase) {
        return queryApp(sQLiteDatabase, "", 0, 1);
    }

    public static List<BackgroundDataBean> queryApp(SQLiteDatabase sQLiteDatabase, String str, int i) {
        return queryApp(sQLiteDatabase, str, i, 0);
    }

    public static List<BackgroundDataBean> queryApp(SQLiteDatabase sQLiteDatabase, String str, int i, int i2) {
        Cursor cursor;
        if (i2 == 1) {
            cursor = sQLiteDatabase.query("background_data", null, null, null, null, null, null, null);
        } else if (TextUtils.isEmpty(str)) {
            cursor = sQLiteDatabase.query("background_data", null, "uid = ?", new String[]{String.valueOf(i)}, null, null, null);
        } else {
            cursor = sQLiteDatabase.query("background_data", null, "package_name = ? and uid = ?", new String[]{str, String.valueOf(i)}, null, null, null);
        }
        ArrayList arrayList = new ArrayList();
        if (cursor == null || cursor.getCount() <= 0 || !cursor.moveToFirst()) {
            releaseCursor(cursor);
            return arrayList;
        }
        do {
            String string = cursor.getString(cursor.getColumnIndex("package_name"));
            if (TextUtils.isEmpty(string)) {
                string = "";
            }
            arrayList.add(new BackgroundDataBean(cursor.getInt(cursor.getColumnIndex("id")), string, cursor.getInt(cursor.getColumnIndex("uid")), cursor.getInt(cursor.getColumnIndex("type"))));
        } while (cursor.moveToNext());
        releaseCursor(cursor);
        return arrayList;
    }

    public static BackgroundDataBean queryApp(SQLiteDatabase sQLiteDatabase, int i) {
        if (sQLiteDatabase != null && i >= 0) {
            Cursor query = sQLiteDatabase.query("background_data", null, "id = ?", new String[]{String.valueOf(i)}, null, null, null);
            if (query == null || query.getCount() <= 0 || !query.moveToFirst()) {
                releaseCursor(query);
            } else {
                String string = query.getString(query.getColumnIndex("package_name"));
                if (TextUtils.isEmpty(string)) {
                    string = "";
                }
                int i2 = query.getInt(query.getColumnIndex("uid"));
                int i3 = query.getInt(query.getColumnIndex("type"));
                releaseCursor(query);
                return new BackgroundDataBean(i, string, i2, i3);
            }
        }
        return null;
    }

    public static void addBackgroundDataApp(Context context, String str, int i) {
        if (context != null && !TextUtils.isEmpty(str) && i >= 0) {
            addBackgroundDataApp(getSqliteDatabase(context), str, i, 2);
        }
    }

    public static void addBackgroundDataApp(SQLiteDatabase sQLiteDatabase, String str, int i, int i2) {
        if (sQLiteDatabase != null && !TextUtils.isEmpty(str) && i >= 0) {
            List<BackgroundDataBean> queryApp = queryApp(sQLiteDatabase, str, i);
            if (queryApp == null || queryApp.isEmpty()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("package_name", str);
                contentValues.put("uid", Integer.valueOf(i));
                contentValues.put("type", Integer.valueOf(i2));
                sQLiteDatabase.insert("background_data", null, contentValues);
            }
        }
    }

    public static int updateBackgroundDataApp(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        BackgroundDataBean queryApp;
        if (sQLiteDatabase == null || i < 0 || (queryApp = queryApp(sQLiteDatabase, i)) == null || queryApp.getId() <= 0) {
            return -1;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", Integer.valueOf(i2));
        return sQLiteDatabase.update("background_data", contentValues, "id = ?", new String[]{String.valueOf(i)});
    }

    public static int deleteBackgroundDataApp(Context context, String str, int i) {
        if (context == null || i < 0) {
            return -1;
        }
        SQLiteDatabase sqliteDatabase = getSqliteDatabase(context);
        if (str == null) {
            str = "";
        }
        return deleteBackgroundDataApp(sqliteDatabase, str, i);
    }

    public static int deleteBackgroundDataApp(SQLiteDatabase sQLiteDatabase, String str, int i) {
        if (sQLiteDatabase != null && i >= 0) {
            if (str == null) {
                str = "";
            }
            List<BackgroundDataBean> queryApp = queryApp(sQLiteDatabase, str, i);
            if (queryApp != null && queryApp.size() > 0) {
                if (TextUtils.isEmpty(str)) {
                    return sQLiteDatabase.delete("background_data", "uid = ?", new String[]{String.valueOf(i)});
                }
                return sQLiteDatabase.delete("background_data", "package_name = ? and uid = ?", new String[]{str, String.valueOf(i)});
            }
        }
        return -1;
    }

    public static boolean isAppInstalled(Context context, String str, int i) {
        if (context == null || TextUtils.isEmpty(str)) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            return true;
        }
        SQLiteDatabase sqliteDatabase = getSqliteDatabase(context);
        if (sqliteDatabase != null && i > 0) {
            deleteBackgroundDataApp(sqliteDatabase, str, i);
        }
        return false;
    }

    public static void changeRoamingAppStatus(Context context) {
        SQLiteDatabase sqliteDatabase;
        if (context != null && (sqliteDatabase = getSqliteDatabase(context)) != null) {
            List<BackgroundDataBean> queryAllApp = queryAllApp(sqliteDatabase);
            if (queryAllApp == null || queryAllApp.isEmpty()) {
                initAppBackgroundDataType(context, sqliteDatabase);
            } else {
                changeRoamingAppStatus(context, queryAllApp);
            }
        }
    }

    public static void changeRoamingAppStatus(Context context, List<BackgroundDataBean> list) {
        if (!(context == null || list == null || list.isEmpty())) {
            for (BackgroundDataBean backgroundDataBean : list) {
                try {
                    changeRoamingAppStatus(context, backgroundDataBean);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void changeRoamingAppStatus(Context context, BackgroundDataBean backgroundDataBean) {
        Boolean bool = Boolean.TRUE;
        Boolean bool2 = Boolean.FALSE;
        if (context != null && backgroundDataBean != null && !TextUtils.isEmpty(backgroundDataBean.getPackage_name()) && backgroundDataBean.getUid() >= 0 && isAppInstalled(context, backgroundDataBean.getPackage_name(), backgroundDataBean.getUid())) {
            int type = backgroundDataBean.getType();
            String package_name = backgroundDataBean.getPackage_name();
            int uid = backgroundDataBean.getUid();
            if (type == 0) {
                setBlackList(context, uid, package_name, bool2);
            } else if (type == 1) {
                setBlackList(context, uid, package_name, bool);
            } else if (type == 2) {
                try {
                    if (!isRoaming()) {
                        setBlackList(context, uid, package_name, bool2);
                    } else {
                        setBlackList(context, uid, package_name, bool);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    SQLiteDatabase sqliteDatabase = getSqliteDatabase(context);
                    if (sqliteDatabase != null) {
                        deleteBackgroundDataApp(sqliteDatabase, backgroundDataBean.getPackage_name(), backgroundDataBean.getUid());
                    }
                }
            }
        }
    }

    public static boolean isRoaming() {
        try {
            IExtTelephony asInterface = IExtTelephony.Stub.asInterface(ServiceManager.getService("extphone"));
            Bundle bundle = new Bundle();
            Method declaredMethod = asInterface.getClass().getDeclaredMethod("generalGetter", String.class, Bundle.class);
            declaredMethod.setAccessible(true);
            return ((Bundle) declaredMethod.invoke(asInterface, "getRoamingReduction", bundle)).getBoolean("getRoamingReduction", false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setBlackList(Context context, int i, String str, Boolean bool) {
        new DataSaverBackend(context).setIsBlacklisted(i, str, bool.booleanValue());
    }

    public static List<BackgroundDataBean> getAllInternetApp(Context context, SQLiteDatabase sQLiteDatabase) {
        List<PackageInfo> installedPackages;
        String[] strArr;
        ArrayList arrayList = null;
        if (!(context == null || sQLiteDatabase == null || (installedPackages = context.getPackageManager().getInstalledPackages(4096)) == null || installedPackages.isEmpty())) {
            arrayList = new ArrayList();
            List<Integer> filterApp = filterApp(context);
            for (int i = 0; i < installedPackages.size(); i++) {
                PackageInfo packageInfo = installedPackages.get(i);
                String str = packageInfo.packageName;
                if (!TextUtils.isEmpty(str) && (strArr = packageInfo.requestedPermissions) != null) {
                    for (String str2 : strArr) {
                        if (str2.equals("android.permission.INTERNET")) {
                            int appUid = getAppUid(context, packageInfo);
                            if (!filterApp.contains(Integer.valueOf(appUid)) && UserHandle.isApp(appUid)) {
                                addBackgroundDataApp(sQLiteDatabase, str, appUid, 2);
                                arrayList.add(new BackgroundDataBean(str, appUid, 2));
                            }
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public static int getAppUid(Context context, PackageInfo packageInfo) {
        return getAppUid(context, packageInfo.packageName);
    }

    public static int getAppUid(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 1).uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isQueryDataEmpty(SQLiteDatabase sQLiteDatabase, ContentValues contentValues) {
        if (!(sQLiteDatabase == null || contentValues == null)) {
            String asString = contentValues.getAsString("package_name");
            Integer asInteger = contentValues.getAsInteger("uid");
            if (!(asString == null || asInteger == null)) {
                Cursor query = sQLiteDatabase.query("background_data", null, "package_name = ? and uid = ?", new String[]{asString, String.valueOf(asInteger)}, null, null, null);
                if (query == null || query.getCount() == 0) {
                    releaseCursor(query);
                    return true;
                }
                releaseCursor(query);
            }
        }
        return false;
    }

    public static boolean isQueryDataEmpty(Context context, String str, int i) {
        SQLiteDatabase sqliteDatabase = getSqliteDatabase(context);
        if (!(sqliteDatabase == null || str == null)) {
            Cursor query = sqliteDatabase.query("background_data", null, "package_name = ? and uid = ?", new String[]{str, String.valueOf(i)}, null, null, null);
            if (query == null || query.getCount() == 0) {
                releaseCursor(query);
                return true;
            }
            releaseCursor(query);
        }
        return false;
    }

    public static boolean isHaveInternetPermission(Context context, ContentValues contentValues) {
        if (context == null || contentValues == null) {
            return false;
        }
        return isHaveInternetPermission(context, contentValues.getAsString("package_name"));
    }

    public static boolean isHaveInternetPermission(Context context, String str) {
        if (context != null && !TextUtils.isEmpty(str)) {
            try {
                String[] strArr = context.getPackageManager().getPackageInfo(str, 4096).requestedPermissions;
                if (strArr != null) {
                    for (String str2 : strArr) {
                        if (str2.equals("android.permission.INTERNET")) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static List<Integer> filterApp(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(getAppUid(context, "com.android.providers.downloads")));
        arrayList.add(Integer.valueOf(getAppUid(context, "com.google.android.gms")));
        return arrayList;
    }

    private static void releaseCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
