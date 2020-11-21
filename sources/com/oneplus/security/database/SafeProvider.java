package com.oneplus.security.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import com.android.settings.wifi.UseOpenWifiPreferenceController;
import com.oneplus.security.firewall.NetworkRestrictService;
import com.oneplus.security.network.operator.AccountDayLocalCache;
import com.oneplus.security.network.operator.OperatorDataModelFactory;
import com.oneplus.security.network.operator.OperatorModelInterface;
import com.oneplus.security.network.simcard.SimcardDataModel;
import com.oneplus.security.network.trafficalarm.TrafficUsageAlarmUtils;
import com.oneplus.security.utils.FunctionUtils;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.TimeRegionUtils;
import com.oneplus.settings.SettingsBaseApplication;
import java.util.Map;
import java.util.UUID;

public class SafeProvider extends ContentProvider {
    private static final UriMatcher sMatcher;
    private DatabaseHelper dbOpenHelper = null;
    private PackageManager packageManager;
    private ContentResolver resolver = null;

    public String getType(Uri uri) {
        return null;
    }

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sMatcher = uriMatcher;
        uriMatcher.addURI("com.oneplus.security.database.SafeProvider", "tm_network_control", UseOpenWifiPreferenceController.REQUEST_CODE_OPEN_WIFI_AUTOMATICALLY);
        sMatcher.addURI("com.oneplus.security.database.SafeProvider", "intercept_logs", 401);
        sMatcher.addURI("com.oneplus.security.database.SafeProvider", "intercept_logs/#", 402);
        sMatcher.addURI("com.oneplus.security.database.SafeProvider", "intercept_logs/phonenumber/*", 501);
        sMatcher.addURI("com.oneplus.security.database.SafeProvider", "network_restrict", 403);
        sMatcher.addURI("com.oneplus.security.database.SafeProvider", "network_restrict/#", 404);
    }

    public boolean onCreate() {
        Context context = getContext();
        this.dbOpenHelper = new DatabaseHelper(context);
        this.resolver = context.getContentResolver();
        this.packageManager = context.getPackageManager();
        return false;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        String callingPackage = getCallingPackage();
        LogUtils.d("SafeProvider", "call oneplus security provider,method:" + str + ",caller:" + callingPackage);
        if ("query_oneplus_security_uuid".equals(str)) {
            String securityUuid = getSecurityUuid();
            Bundle bundle2 = new Bundle();
            bundle2.putString("op_security_uuid", securityUuid);
            return bundle2;
        } else if (!FunctionUtils.checkProviderPermission(this.packageManager, callingPackage, Binder.getCallingPid())) {
            LogUtils.w("SafeProvider", "This method can only be called by system app.callPackage:" + callingPackage);
            return null;
        } else if ("method_query_oneplus_datausage".equals(str) && bundle != null) {
            int i = bundle.getInt("oneplus_datausage_slotid", -1);
            if (i != -1) {
                return queryOneplusDataUsage(i, bundle.getBoolean("oneplus_datausage_cache", true));
            }
            LogUtils.d("SafeProvider", "query oneplus datausage error slotid .");
            throw new IllegalArgumentException("Error extras,slotId: " + i);
        } else if ("method_query_oneplus_datausage_region".equals(str) && bundle != null) {
            return queryOneplusDataUsageRegion(bundle.getInt("oneplus_datausage_slotid", -1));
        } else {
            throw new IllegalArgumentException("Error method: " + str);
        }
    }

    private static String getSecurityUuid() {
        String stringForUser = Settings.Secure.getStringForUser(SettingsBaseApplication.getContext().getContentResolver(), "op_security_uuid", 0);
        if (!TextUtils.isEmpty(stringForUser)) {
            return stringForUser;
        }
        String uuid = UUID.randomUUID().toString();
        Settings.Secure.putStringForUser(SettingsBaseApplication.getContext().getContentResolver(), "op_security_uuid", uuid, 0);
        return uuid;
    }

    private Bundle queryOneplusDataUsage(int i, boolean z) {
        LogUtils.d("SafeProvider", "queryOneplusDataUsage slotId:" + i + ",isFromCache:" + z);
        Bundle bundle = new Bundle();
        bundle.putInt("oneplus_datausage_slotid", i);
        bundle.putInt("oneplus_datausage_error_code", 0);
        try {
            if (SimcardDataModel.getInstance(getContext()).isSlotSimInserted(i)) {
                long[] dataUsageSectionTimeMillByAccountDay = AccountDayLocalCache.getDataUsageSectionTimeMillByAccountDay(getContext(), i);
                bundle.putLong("oneplus_datausage_time_start", dataUsageSectionTimeMillByAccountDay[0]);
                bundle.putLong("oneplus_datausage_time_end", dataUsageSectionTimeMillByAccountDay[1]);
                boolean dataWarnState = TrafficUsageAlarmUtils.getDataWarnState(getContext(), false, i);
                bundle.putBoolean("oneplus_datausage_warn_state", dataWarnState);
                long j = -1;
                if (dataWarnState) {
                    j = TrafficUsageAlarmUtils.getDataWarnValue(getContext(), i, -1);
                }
                bundle.putLong("oneplus_datausage_warn_value", j);
                OperatorModelInterface operatorDataModel = OperatorDataModelFactory.getOperatorDataModel(getContext());
                bundle.putInt("oneplus_datausage_accountday", operatorDataModel.getAccountDay(i));
                Map<String, Object> requesetDataUsage = operatorDataModel.requesetDataUsage(i, z);
                long longValue = ((Long) requesetDataUsage.get("total")).longValue();
                long longValue2 = ((Long) requesetDataUsage.get("used")).longValue();
                boolean dataTotalState = TrafficUsageAlarmUtils.getDataTotalState(getContext(), i);
                bundle.putBoolean("oneplus_datausage_cache", z);
                bundle.putLong("oneplus_datausage_total", longValue);
                bundle.putLong("oneplus_datausage_used", longValue2);
                bundle.putBoolean("oneplus_datausage_limit_state", dataTotalState);
            } else {
                bundle.putInt("oneplus_datausage_error_code", 1);
            }
        } catch (Exception e) {
            bundle.putInt("oneplus_datausage_error_code", 2);
            e.printStackTrace();
        }
        return bundle;
    }

    private Bundle queryOneplusDataUsageRegion(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("oneplus_datausage_slotid", i);
        if (i == -1) {
            try {
                LogUtils.d("SafeProvider", "query oneplus datausage region for invalid slotid ,return default region for result.");
                long[] regionTimeDefault = TimeRegionUtils.getRegionTimeDefault(System.currentTimeMillis());
                bundle.putLong("oneplus_datausage_time_start", regionTimeDefault[0]);
                bundle.putLong("oneplus_datausage_time_end", regionTimeDefault[1]);
                bundle.putInt("oneplus_datausage_error_code", 1);
            } catch (Exception e) {
                bundle.putInt("oneplus_datausage_error_code", 2);
                e.printStackTrace();
            }
        } else if (SimcardDataModel.getInstance(getContext()).isSlotSimInserted(i)) {
            long[] dataUsageSectionTimeMillByAccountDay = AccountDayLocalCache.getDataUsageSectionTimeMillByAccountDay(getContext(), i);
            bundle.putLong("oneplus_datausage_time_start", dataUsageSectionTimeMillByAccountDay[0]);
            bundle.putLong("oneplus_datausage_time_end", dataUsageSectionTimeMillByAccountDay[1]);
            bundle.putInt("oneplus_datausage_error_code", 0);
        } else {
            long[] dataUsageSectionTimeMillByAccountDay2 = AccountDayLocalCache.getDataUsageSectionTimeMillByAccountDay(getContext(), i);
            bundle.putLong("oneplus_datausage_time_start", dataUsageSectionTimeMillByAccountDay2[0]);
            bundle.putLong("oneplus_datausage_time_end", dataUsageSectionTimeMillByAccountDay2[1]);
            bundle.putInt("oneplus_datausage_error_code", 1);
        }
        return bundle;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        String str3;
        String str4;
        String str5 = str;
        String callingPackage = getCallingPackage();
        if (!FunctionUtils.checkProviderPermission(this.packageManager, callingPackage, Binder.getCallingPid())) {
            LogUtils.w("SafeProvider", "This uri can only be called by system app.callPackage:" + callingPackage);
            return null;
        }
        SQLiteDatabase readableDatabase = this.dbOpenHelper.getReadableDatabase();
        String table = getTable(uri);
        if (table == null) {
            int match = sMatcher.match(uri);
            if (match == 402) {
                String lastPathSegment = uri.getLastPathSegment();
                str4 = concatSelections(str5, "_id=" + lastPathSegment);
            } else if (match == 404) {
                String lastPathSegment2 = uri.getLastPathSegment();
                str3 = "network_restrict";
                str5 = concatSelections(str5, "pkg=" + lastPathSegment2);
            } else if (match == 501) {
                String lastPathSegment3 = uri.getLastPathSegment();
                str4 = concatSelections(str5, "number=" + lastPathSegment3);
            } else {
                throw new IllegalArgumentException("Error Uri: " + uri);
            }
            str5 = str4;
            str3 = "intercept_logs";
        } else {
            str3 = table;
        }
        try {
            return readableDatabase.query(str3, strArr, str5, strArr2, null, null, str2);
        } catch (Exception e) {
            LogUtils.e("SafeProvider", e.toString());
            throw new IllegalArgumentException("Error query Uri: " + uri + ",selection=" + str5 + ",selectionArgs=" + strArr2);
        }
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        long j;
        String callingPackage = getCallingPackage();
        if (!FunctionUtils.checkProviderPermission(this.packageManager, callingPackage, Binder.getCallingPid())) {
            LogUtils.w("SafeProvider", "This uri can only be called by system app.callPackage:" + callingPackage);
            return null;
        }
        SQLiteDatabase writableDatabase = this.dbOpenHelper.getWritableDatabase();
        String table = getTable(uri);
        if (table != null) {
            if ("intercept_logs".equals(table)) {
                String asString = contentValues.getAsString("number");
                if (TextUtils.isEmpty(asString)) {
                    LogUtils.d("SafeProvider", "Inserting empty number record");
                } else if (!PhoneNumberUtils.isGlobalPhoneNumber(asString)) {
                    throw new SQLiteException("Invalid phoneNumber, Unable to insert " + contentValues + " for " + uri);
                }
            }
            try {
                j = writableDatabase.insert(table, null, contentValues);
            } catch (Exception e) {
                LogUtils.e("SafeProvider", e.toString());
                j = 0;
            }
            if (j >= 0) {
                Uri withAppendedId = ContentUris.withAppendedId(uri, j);
                this.resolver.notifyChange(withAppendedId, null);
                if ("network_restrict".equals(table)) {
                    logCallerInfo();
                    NetworkRestrictService.applyRules(getContext());
                }
                return withAppendedId;
            }
            throw new SQLiteException("Unable to insert " + contentValues + " for " + uri);
        }
        throw new IllegalArgumentException("Error Uri: " + uri);
    }

    public int delete(Uri uri, String str, String[] strArr) {
        String callingPackage = getCallingPackage();
        if (!FunctionUtils.checkProviderPermission(this.packageManager, callingPackage, Binder.getCallingPid())) {
            LogUtils.w("SafeProvider", "This uri can only be called by system app.callPackage:" + callingPackage);
            return 0;
        }
        SQLiteDatabase writableDatabase = this.dbOpenHelper.getWritableDatabase();
        String table = getTable(uri);
        if (table == null) {
            int match = sMatcher.match(uri);
            if (match == 402) {
                String lastPathSegment = uri.getLastPathSegment();
                str = concatSelections(str, "_id=" + lastPathSegment);
                table = "intercept_logs";
            } else if (match == 404) {
                String lastPathSegment2 = uri.getLastPathSegment();
                str = concatSelections(str, "pkg=" + lastPathSegment2);
                table = "network_restrict";
            } else {
                throw new IllegalArgumentException("Error Uri: " + uri);
            }
        }
        try {
            int delete = writableDatabase.delete(table, str, strArr);
            this.resolver.notifyChange(uri, null);
            if ("network_restrict".equals(table)) {
                logCallerInfo();
                NetworkRestrictService.applyRules(getContext());
            }
            return delete;
        } catch (SQLException e) {
            LogUtils.e("SafeProvider", "delete entry error!" + e.toString());
            throw new IllegalArgumentException("Error delete Uri: " + uri + ",selection=" + str + ",selectionArgs=" + strArr);
        }
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (this.dbOpenHelper == null) {
            LogUtils.d("SafeProvider", "create DatabaseHelper is null pointer!");
            return -1;
        }
        String callingPackage = getCallingPackage();
        if (!FunctionUtils.checkProviderPermission(this.packageManager, callingPackage, Binder.getCallingPid())) {
            LogUtils.w("SafeProvider", "This uri can only be called by system app.callPackage:" + callingPackage);
            return 0;
        }
        SQLiteDatabase writableDatabase = this.dbOpenHelper.getWritableDatabase();
        if (writableDatabase == null) {
            LogUtils.d("SafeProvider", "create SQLiteDatabase is null pointer!");
            return -1;
        }
        String table = getTable(uri);
        if (table == null) {
            int match = sMatcher.match(uri);
            if (match == 402) {
                String lastPathSegment = uri.getLastPathSegment();
                str = concatSelections(str, "_id=" + lastPathSegment);
                table = "intercept_logs";
            } else if (match == 404) {
                String lastPathSegment2 = uri.getLastPathSegment();
                str = concatSelections(str, "pkg=" + lastPathSegment2);
                table = "network_restrict";
            } else {
                throw new IllegalArgumentException("Error Uri: " + uri);
            }
        }
        try {
            int update = writableDatabase.update(table, contentValues, str, strArr);
            this.resolver.notifyChange(uri, null);
            if ("network_restrict".equals(table)) {
                logCallerInfo();
                NetworkRestrictService.applyRules(getContext());
            }
            return update;
        } catch (Exception e) {
            LogUtils.e("SafeProvider", e.toString());
            throw new IllegalArgumentException("Error update Uri: " + uri + ",values=" + contentValues + ",selection=" + str + ",selectionArgs=" + strArr);
        }
    }

    private String getTable(Uri uri) {
        int match = sMatcher.match(uri);
        if (match == 400) {
            return "tm_network_control";
        }
        if (match == 401) {
            return "intercept_logs";
        }
        if (match != 403) {
            return null;
        }
        return "network_restrict";
    }

    private static String concatSelections(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return str2;
        }
        if (TextUtils.isEmpty(str2)) {
            return str;
        }
        return str + " AND " + str2;
    }

    private void logCallerInfo() {
        int callingUid = Binder.getCallingUid();
        String callingPackage = getCallingPackage();
        LogUtils.d("SafeProvider", "call uid=" + callingUid + ",pkg=" + callingPackage);
    }
}
