package com.android.settings.deviceinfo.legal;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import androidx.core.util.Preconditions;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class ModuleLicenseProvider extends ContentProvider {
    public boolean onCreate() {
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        throw new UnsupportedOperationException();
    }

    public String getType(Uri uri) {
        checkUri(getContext(), uri);
        return "text/html";
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException();
    }

    public int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String str) {
        Context context = getContext();
        checkUri(context, uri);
        Preconditions.checkArgument("r".equals(str), "Read is the only supported mode");
        try {
            String str2 = uri.getPathSegments().get(0);
            File cachedHtmlFile = getCachedHtmlFile(context, str2);
            if (isCachedHtmlFileOutdated(context, str2)) {
                GZIPInputStream gZIPInputStream = new GZIPInputStream(getPackageAssetManager(context.getPackageManager(), str2).open("NOTICE.html.gz"));
                try {
                    File cachedFileDirectory = getCachedFileDirectory(context, str2);
                    if (!cachedFileDirectory.exists()) {
                        cachedFileDirectory.mkdir();
                    }
                    Files.copy(gZIPInputStream, cachedHtmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    gZIPInputStream.close();
                    getPrefs(context).edit().putLong(str2, getPackageInfo(context, str2).getLongVersionCode()).commit();
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            }
            return ParcelFileDescriptor.open(cachedHtmlFile, 268435456);
            throw th;
        } catch (PackageManager.NameNotFoundException e) {
            Log.wtf("ModuleLicenseProvider", "checkUri should have already caught this error", e);
            return null;
        } catch (IOException e2) {
            Log.e("ModuleLicenseProvider", "Could not open file descriptor", e2);
            return null;
        }
    }

    static boolean isCachedHtmlFileOutdated(Context context, String str) throws PackageManager.NameNotFoundException {
        SharedPreferences prefs = getPrefs(context);
        File cachedHtmlFile = getCachedHtmlFile(context, str);
        return !prefs.contains(str) || prefs.getLong(str, 0) != getPackageInfo(context, str).getLongVersionCode() || !cachedHtmlFile.exists() || cachedHtmlFile.length() == 0;
    }

    static AssetManager getPackageAssetManager(PackageManager packageManager, String str) throws PackageManager.NameNotFoundException {
        return packageManager.getResourcesForApplication(packageManager.getPackageInfo(str, 1073741824).applicationInfo).getAssets();
    }

    static Uri getUriForPackage(String str) {
        return new Uri.Builder().scheme("content").authority("com.android.settings.module_licenses").appendPath(str).appendPath("NOTICE.html").build();
    }

    private static void checkUri(Context context, Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        if (!"content".equals(uri.getScheme()) || !"com.android.settings.module_licenses".equals(uri.getAuthority()) || pathSegments == null || pathSegments.size() != 2 || !"NOTICE.html".equals(pathSegments.get(1))) {
            throw new IllegalArgumentException(uri + "is not a valid URI");
        }
        try {
            context.getPackageManager().getModuleInfo(pathSegments.get(0), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(uri + "is not a valid URI", e);
        }
    }

    private static File getCachedFileDirectory(Context context, String str) {
        return new File(context.getCacheDir(), str);
    }

    private static File getCachedHtmlFile(Context context, String str) {
        return new File(context.getCacheDir() + "/" + str, "NOTICE.html");
    }

    private static PackageInfo getPackageInfo(Context context, String str) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(str, 1073741824);
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("ModuleLicenseProvider", 0);
    }
}
