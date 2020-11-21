package com.oneplus.settings.multiapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.net.URISyntaxException;

public class ShortcutUtil {
    public static final Uri LAUNCHER_FAVORITES_CONTENT_URI = Uri.parse("content://net.oneplus.launcher.settings/favorites");

    /* JADX INFO: finally extract failed */
    public static void removeShortcut(Context context, String str, String str2, int i, boolean z) {
        Log.d("Settings_ShortcutUtil", "removeShortcut shortcutName:" + str + " pkgName:" + str2 + " uid:" + i);
        ContentResolver contentResolver = context.getContentResolver();
        boolean z2 = true;
        Cursor query = contentResolver.query(LAUNCHER_FAVORITES_CONTENT_URI, new String[]{"_id", "intent"}, "title=?", new String[]{str}, null);
        if (query == null) {
            Log.e("Settings_ShortcutUtil", "removeShortcut error, Cursor is null !!!!!");
            return;
        }
        int columnIndexOrThrow = query.getColumnIndexOrThrow("intent");
        int columnIndexOrThrow2 = query.getColumnIndexOrThrow("_id");
        String shortcutIdWithNoTime = getShortcutIdWithNoTime(str2, i);
        boolean z3 = false;
        while (true) {
            try {
                if (!query.moveToNext()) {
                    z2 = z3;
                    break;
                }
                try {
                    Intent parseUri = Intent.parseUri(query.getString(columnIndexOrThrow), 0);
                    if (parseUri != null) {
                        String stringExtra = parseUri.getStringExtra("shortcut_id");
                        Log.d("Settings_ShortcutUtil", "removeShortcut shortcutIdInDb:" + stringExtra + " shortcutId:" + shortcutIdWithNoTime);
                        if (!TextUtils.isEmpty(stringExtra) && stringExtra.contains(shortcutIdWithNoTime)) {
                            contentResolver.delete(getLauncherFavoritesContentUri(query.getLong(columnIndexOrThrow2)), null, null);
                            if (!z) {
                                break;
                            }
                            z3 = true;
                        } else {
                            continue;
                        }
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } catch (Throwable th) {
                query.close();
                throw th;
            }
        }
        query.close();
        Log.d("Settings_ShortcutUtil", "removeShortcut changed:" + z2 + " shortcut uninstalled");
        if (z2) {
            contentResolver.notifyChange(LAUNCHER_FAVORITES_CONTENT_URI, null);
        }
    }

    private static String getShortcutIdWithNoTime(String str, int i) {
        if (i > 0) {
            return "com.oneplus.gamespace.shortcut:" + str + ":" + i;
        }
        return "com.oneplus.gamespace.shortcut:" + str;
    }

    private static Uri getLauncherFavoritesContentUri(long j) {
        return Uri.parse("content://net.oneplus.launcher.settings/favorites/" + j);
    }
}
