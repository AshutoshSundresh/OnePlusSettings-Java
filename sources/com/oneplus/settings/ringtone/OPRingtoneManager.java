package com.oneplus.settings.ringtone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.ExternalRingtonesCursorWrapper;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemProperties;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;
import libcore.io.IoUtils;

public class OPRingtoneManager {
    private static final String[] INTERNAL_COLUMNS = {"_id", "title", "title", "title_key", "_data"};
    private static Uri mDefaultUri;
    private Activity mActivity;
    private Context mContext;
    private Cursor mCursor;
    private final List<String> mFilterColumns = new ArrayList();
    private int mType = 1;

    public static String getSettingForType(int i) {
        if (i == 1) {
            return "ringtone";
        }
        if (i == 2) {
            return "notification_sound";
        }
        if (i == 4) {
            return "alarm_alert";
        }
        if (i == 8) {
            return "mms_notification";
        }
        return null;
    }

    public OPRingtoneManager(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity;
        setType(1);
    }

    public void setType(int i) {
        if (this.mCursor == null) {
            this.mType = i;
            setFilterColumnsList(i);
            return;
        }
        throw new IllegalStateException("Setting filter columns should be done before querying for ringtones.");
    }

    private void setFilterColumnsList(int i) {
        List<String> list = this.mFilterColumns;
        list.clear();
        if ((i & 1) != 0) {
            list.add("is_ringtone");
        }
        if ((i & 2) != 0) {
            list.add("is_notification");
        }
        if ((i & 8) != 0) {
            list.add("is_notification");
        }
        if ((i & 4) != 0) {
            list.add("is_alarm");
        }
    }

    public int inferStreamType() {
        int i = this.mType;
        if (i == 2) {
            return 5;
        }
        if (i != 4) {
            return i != 8 ? 2 : 5;
        }
        return 4;
    }

    public Cursor getCursor() {
        Cursor cursor = this.mCursor;
        if (cursor != null && cursor.requery()) {
            return this.mCursor;
        }
        Cursor internalRingtones = getInternalRingtones();
        this.mCursor = internalRingtones;
        return internalRingtones;
    }

    private Cursor getInternalRingtones() {
        String str = constructBooleanTrueWhereClause(this.mFilterColumns) + " and (_data like ? or _data like ? )";
        return new ExternalRingtonesCursorWrapper(query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, INTERNAL_COLUMNS, constructBooleanTrueWhereClause(this.mFilterColumns), null, "title_key"), MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
    }

    private static String constructBooleanTrueWhereClause(List<String> list) {
        if (list == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int size = list.size() - 1; size >= 0; size--) {
            sb.append(list.get(size));
            sb.append("=1 or ");
        }
        if (list.size() > 0) {
            sb.setLength(sb.length() - 4);
        }
        sb.append(") ");
        return sb.toString();
    }

    private static String[] constructWhereClauseWithOP1(int i) {
        String str;
        String str2 = OPUtils.isSupportSystemProductionRingtone() ? "/system/product/media/audio/" : "/product/media/audio/";
        if (i != 2) {
            if (i == 4) {
                str = "alarms/%";
            } else if (i != 8) {
                str = "ringtones/%";
            }
            return new String[]{str2 + str, "/op1/" + str};
        }
        str = "notifications/%";
        return new String[]{str2 + str, "/op1/" + str};
    }

    private static String[] constructWhereClause(int i) {
        StringBuilder sb = new StringBuilder();
        if (i != 2) {
            if (i != 4) {
                if (i != 8) {
                    if (OPUtils.isSupportSystemProductionRingtone()) {
                        sb.append("/system/product/media/audio/ringtones/%");
                    } else {
                        sb.append("/product/media/audio/ringtones/%");
                    }
                }
            } else if (OPUtils.isSupportSystemProductionRingtone()) {
                sb.append("/system/product/media/audio/alarms/%");
            } else {
                sb.append("/product/media/audio/alarms/%");
            }
            return new String[]{sb.toString()};
        }
        if (OPUtils.isSupportSystemProductionRingtone()) {
            sb.append("/system/product/media/audio/notifications/%");
        } else {
            sb.append("/product/media/audio/notifications/%");
        }
        return new String[]{sb.toString()};
    }

    private Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Activity activity = this.mActivity;
        if (activity != null) {
            return activity.managedQuery(uri, strArr, str, strArr2, str2);
        }
        return this.mContext.getContentResolver().query(uri, strArr, str, strArr2, str2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0086, code lost:
        if (r0 != null) goto L_0x0088;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0088, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00a5, code lost:
        if (0 == 0) goto L_0x00a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a8, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.net.Uri getActualRingtoneUriBySubId(android.content.Context r9, int r10) {
        /*
        // Method dump skipped, instructions count: 176
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.ringtone.OPRingtoneManager.getActualRingtoneUriBySubId(android.content.Context, int):android.net.Uri");
    }

    public static void setActualRingtoneUriBySubId(Context context, int i, Uri uri) {
        if (i >= 0 && i < 2) {
            String str = "none";
            if (i == 0) {
                ContentResolver contentResolver = context.getContentResolver();
                if (uri != null) {
                    str = uri.toString();
                }
                Settings.System.putString(contentResolver, "op_ringtone1_df", str);
            } else {
                String str2 = "ringtone_" + (i + 1);
                ContentResolver contentResolver2 = context.getContentResolver();
                if (uri != null) {
                    str = uri.toString();
                }
                Settings.System.putString(contentResolver2, "op_ringtone2_df", str);
            }
            RingtoneManager.setActualRingtoneUriBySubId(context, i, uri);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0060, code lost:
        if (r1 != null) goto L_0x0062;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0062, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x007f, code lost:
        if (0 == 0) goto L_0x0082;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0082, code lost:
        return r9;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.net.Uri getActualDefaultRingtoneUri(android.content.Context r8, int r9) {
        /*
        // Method dump skipped, instructions count: 144
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.ringtone.OPRingtoneManager.getActualDefaultRingtoneUri(android.content.Context, int):android.net.Uri");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0040, code lost:
        if (r0 != null) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004d, code lost:
        if (0 == 0) goto L_0x0052;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004f, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0054, code lost:
        return com.oneplus.settings.ringtone.OPRingtoneManager.mDefaultUri;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.net.Uri getStaticDefaultRingtoneUri(android.content.Context r9) {
        /*
            android.net.Uri r0 = com.oneplus.settings.ringtone.OPRingtoneManager.mDefaultUri
            if (r0 == 0) goto L_0x0005
            return r0
        L_0x0005:
            r0 = 0
            android.content.ContentResolver r1 = r9.getContentResolver()     // Catch:{ Exception -> 0x0045 }
            android.net.Uri r2 = android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x0045 }
            java.lang.String r3 = "_id"
            java.lang.String[] r3 = new java.lang.String[]{r3}     // Catch:{ Exception -> 0x0045 }
            java.lang.String r4 = "_display_name=?"
            r5 = 1
            java.lang.String[] r5 = new java.lang.String[r5]     // Catch:{ Exception -> 0x0045 }
            java.lang.String r6 = "ringtone"
            java.lang.String r9 = getDefaultRingtoneFileName(r9, r6)     // Catch:{ Exception -> 0x0045 }
            r8 = 0
            r5[r8] = r9     // Catch:{ Exception -> 0x0045 }
            r6 = 0
            r7 = 0
            android.database.Cursor r0 = r1.query(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0045 }
            if (r0 == 0) goto L_0x0040
            int r9 = r0.getCount()     // Catch:{ Exception -> 0x0045 }
            if (r9 <= 0) goto L_0x0040
            boolean r9 = r0.moveToFirst()     // Catch:{ Exception -> 0x0045 }
            if (r9 == 0) goto L_0x0040
            long r1 = r0.getLong(r8)     // Catch:{ Exception -> 0x0045 }
            android.net.Uri r9 = android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x0045 }
            android.net.Uri r9 = android.content.ContentUris.withAppendedId(r9, r1)     // Catch:{ Exception -> 0x0045 }
            com.oneplus.settings.ringtone.OPRingtoneManager.mDefaultUri = r9     // Catch:{ Exception -> 0x0045 }
        L_0x0040:
            if (r0 == 0) goto L_0x0052
            goto L_0x004f
        L_0x0043:
            r9 = move-exception
            goto L_0x0055
        L_0x0045:
            r9 = move-exception
            java.lang.String r1 = "RingtoneManager"
            java.lang.String r2 = "RemoteException: "
            com.oneplus.settings.ringtone.OPMyLog.e(r1, r2, r9)     // Catch:{ all -> 0x0043 }
            if (r0 == 0) goto L_0x0052
        L_0x004f:
            r0.close()
        L_0x0052:
            android.net.Uri r9 = com.oneplus.settings.ringtone.OPRingtoneManager.mDefaultUri
            return r9
        L_0x0055:
            if (r0 == 0) goto L_0x005a
            r0.close()
        L_0x005a:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.ringtone.OPRingtoneManager.getStaticDefaultRingtoneUri(android.content.Context):android.net.Uri");
    }

    private static String getDefaultRingtoneFileName(Context context, String str) {
        return SystemProperties.get("ro.config." + str);
    }

    public static Uri getUriFromCursor(Cursor cursor) {
        return ContentUris.withAppendedId(Uri.parse(cursor.getString(2)), cursor.getLong(0));
    }

    public static Ringtone getRingtone(Context context, Uri uri) {
        return RingtoneManager.getRingtone(context, uri);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0043, code lost:
        if (r1 != null) goto L_0x0045;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0045, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0062, code lost:
        if (0 == 0) goto L_0x0065;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0065, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isSystemRingtone(android.content.Context r9, android.net.Uri r10, int r11) {
        /*
        // Method dump skipped, instructions count: 108
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.ringtone.OPRingtoneManager.isSystemRingtone(android.content.Context, android.net.Uri, int):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0032, code lost:
        if (r1 != null) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0034, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0051, code lost:
        if (0 == 0) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0054, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isSystemRingtoneForMCL(android.content.Context r9, android.net.Uri r10, int r11) {
        /*
            r0 = 0
            if (r10 != 0) goto L_0x0004
            return r0
        L_0x0004:
            r1 = 0
            java.lang.String r2 = "media"
            java.lang.String r3 = r10.getAuthority()     // Catch:{ SQLiteException -> 0x003a }
            boolean r2 = r2.equals(r3)     // Catch:{ SQLiteException -> 0x003a }
            if (r2 != 0) goto L_0x0012
            return r0
        L_0x0012:
            java.lang.String r6 = "_data like ? or _data like ? "
            android.content.ContentResolver r3 = r9.getContentResolver()     // Catch:{ SQLiteException -> 0x003a }
            r5 = 0
            java.lang.String[] r7 = constructWhereClauseWithOP1(r11)     // Catch:{ SQLiteException -> 0x003a }
            r8 = 0
            r4 = r10
            android.database.Cursor r1 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ SQLiteException -> 0x003a }
            if (r1 == 0) goto L_0x0032
            int r9 = r1.getCount()     // Catch:{ SQLiteException -> 0x003a }
            if (r9 <= 0) goto L_0x0032
            r9 = 1
            if (r1 == 0) goto L_0x0031
            r1.close()
        L_0x0031:
            return r9
        L_0x0032:
            if (r1 == 0) goto L_0x0054
        L_0x0034:
            r1.close()
            goto L_0x0054
        L_0x0038:
            r9 = move-exception
            goto L_0x0055
        L_0x003a:
            r9 = move-exception
            java.lang.String r10 = "RingtoneManager"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0038 }
            r11.<init>()     // Catch:{ all -> 0x0038 }
            java.lang.String r2 = "ex "
            r11.append(r2)     // Catch:{ all -> 0x0038 }
            r11.append(r9)     // Catch:{ all -> 0x0038 }
            java.lang.String r9 = r11.toString()     // Catch:{ all -> 0x0038 }
            android.util.Log.e(r10, r9)     // Catch:{ all -> 0x0038 }
            if (r1 == 0) goto L_0x0054
            goto L_0x0034
        L_0x0054:
            return r0
        L_0x0055:
            if (r1 == 0) goto L_0x005a
            r1.close()
        L_0x005a:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.ringtone.OPRingtoneManager.isSystemRingtoneForMCL(android.content.Context, android.net.Uri, int):boolean");
    }

    public static Uri ringtoneRestoreFromDefault(Context context, int i, Uri uri) {
        Exception e;
        String settingForType = getSettingForType(i);
        Cursor cursor = null;
        String str = (i & 1) != 0 ? "is_ringtone" : null;
        if ((i & 2) != 0) {
            str = "is_notification";
        }
        if ((i & 4) != 0) {
            str = "is_alarm";
        }
        if (str != null) {
            if (settingForType.startsWith("ringtone")) {
                settingForType = "ringtone";
            }
            String str2 = SystemProperties.get("ro.config.ringtone");
            String str3 = SystemProperties.get("ro.config." + settingForType, str2);
            String substring = str3.substring(0, str3.lastIndexOf("."));
            OPMyLog.d("RingtoneManager", "ringtoneRestoreFromDefault: title = " + substring);
            try {
                cursor = context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, new String[]{"_id"}, str + "=1 and title=?", new String[]{substring}, null, null);
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    Uri withAppendedId = ContentUris.withAppendedId(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, cursor.getLong(0));
                    try {
                        Log.d("RingtoneManager", "ringtoneRestoreFromDefault: [" + settingForType + "] = " + withAppendedId.toString());
                        uri = withAppendedId;
                    } catch (Exception e2) {
                        e = e2;
                        uri = withAppendedId;
                        try {
                            Log.w("RingtoneManager", "RemoteException: ", e);
                            IoUtils.closeQuietly(cursor);
                            return uri;
                        } catch (Throwable th) {
                            IoUtils.closeQuietly(cursor);
                            throw th;
                        }
                    }
                }
            } catch (Exception e3) {
                e = e3;
                Log.w("RingtoneManager", "RemoteException: ", e);
                IoUtils.closeQuietly(cursor);
                return uri;
            }
            IoUtils.closeQuietly(cursor);
        }
        return uri;
    }

    public static class ResultRing {
        Uri ringUri;
        String title;

        public ResultRing(String str, Uri uri) {
            this.title = str;
            this.ringUri = uri;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0086, code lost:
        if (r8 != null) goto L_0x00ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00b8, code lost:
        if (0 == 0) goto L_0x00bd;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.oneplus.settings.ringtone.OPRingtoneManager.ResultRing getLocatRingtoneTitle(android.content.Context r11, android.net.Uri r12, int r13, int r14) {
        /*
        // Method dump skipped, instructions count: 197
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.ringtone.OPRingtoneManager.getLocatRingtoneTitle(android.content.Context, android.net.Uri, int, int):com.oneplus.settings.ringtone.OPRingtoneManager$ResultRing");
    }

    public static Uri updateRingtoneForInternal(Context context, Uri uri, Cursor cursor, int i, int i2) {
        Uri uri2;
        Boolean bool = Boolean.TRUE;
        if (!uri.toString().contains(MediaStore.Audio.Media.INTERNAL_CONTENT_URI.toString())) {
            return uri;
        }
        String string = cursor.getString(1);
        if (OPUtils.isSupportSystemProductionRingtone()) {
            if (string == null || string.startsWith("/system/product/media/audio/") || string.startsWith("/op1/")) {
                return uri;
            }
        } else if (string == null || string.startsWith("/product/media/audio/") || string.startsWith("/op1/")) {
            return uri;
        }
        String replace = string.replace("/storage/emulated/legacy", Environment.getExternalStorageDirectory().getAbsolutePath());
        String string2 = cursor.getString(0);
        String string3 = cursor.getString(2);
        Uri uri3 = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        Cursor query = context.getContentResolver().query(uri3, new String[]{"_id"}, "_data=?", new String[]{replace}, null);
        if (query == null || !query.moveToFirst()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_data", replace);
            contentValues.put("title", string2);
            contentValues.put("mime_type", string3);
            if (i == 1) {
                contentValues.put("is_ringtone", bool);
            } else if (i == 2 || i == 8) {
                contentValues.put("is_notification", bool);
            } else {
                contentValues.put("is_alarm", bool);
            }
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.delete(uri3, "_data=\"" + replace + "\"", null);
            uri2 = context.getContentResolver().insert(uri3, contentValues);
        } else {
            uri2 = ContentUris.withAppendedId(uri3, query.getLong(0));
            ContentValues contentValues2 = new ContentValues();
            if (i == 1) {
                contentValues2.put("is_ringtone", bool);
            } else if (i == 2 || i == 8) {
                contentValues2.put("is_notification", bool);
            } else {
                contentValues2.put("is_alarm", bool);
            }
            context.getContentResolver().update(uri2, contentValues2, null, null);
        }
        if (query != null) {
            query.close();
        }
        if (i2 > 0) {
            setActualRingtoneUriBySubId(context, i2 - 1, uri2);
        } else {
            setActualDefaultRingtoneUri(context, i, uri2);
        }
        return uri2;
    }

    public static void setActualDefaultRingtoneUri(Context context, int i, Uri uri) {
        if (getSettingForType(i) != null) {
            if (i == 1) {
                Settings.System.putString(context.getContentResolver(), "op_ringtone_df", uri != null ? uri.toString() : "none");
                Settings.System.putString(context.getContentResolver(), "ringtone_2", uri != null ? uri.toString() : null);
            }
            RingtoneManager.setActualDefaultRingtoneUri(context, i, uri);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0042  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void updateActualRingtone(android.content.Context r7) {
        /*
            android.content.ContentResolver r0 = r7.getContentResolver()
            java.lang.String r1 = "op_ringtone_df"
            java.lang.String r0 = android.provider.Settings.System.getString(r0, r1)
            android.net.Uri r1 = getStaticDefaultRingtoneUri(r7)
            android.content.ContentResolver r2 = r7.getContentResolver()
            java.lang.String r3 = "none"
            r4 = 0
            if (r0 == 0) goto L_0x0023
            boolean r5 = r0.equals(r3)
            if (r5 == 0) goto L_0x001e
            goto L_0x002a
        L_0x001e:
            java.lang.String r5 = r0.toString()
            goto L_0x002b
        L_0x0023:
            if (r1 == 0) goto L_0x002a
            java.lang.String r5 = r1.toString()
            goto L_0x002b
        L_0x002a:
            r5 = r4
        L_0x002b:
            java.lang.String r6 = "ringtone"
            android.provider.Settings.System.putString(r2, r6, r5)
            android.content.ContentResolver r7 = r7.getContentResolver()
            if (r0 == 0) goto L_0x0042
            boolean r1 = r0.equals(r3)
            if (r1 == 0) goto L_0x003d
            goto L_0x0048
        L_0x003d:
            java.lang.String r4 = r0.toString()
            goto L_0x0048
        L_0x0042:
            if (r1 == 0) goto L_0x0048
            java.lang.String r4 = r1.toString()
        L_0x0048:
            java.lang.String r0 = "ringtone_2"
            android.provider.Settings.System.putString(r7, r0, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.ringtone.OPRingtoneManager.updateActualRingtone(android.content.Context):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0049  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0052  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0033  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void updateActualRingtone2(android.content.Context r6) {
        /*
            android.content.ContentResolver r0 = r6.getContentResolver()
            java.lang.String r1 = "op_ringtone1_df"
            java.lang.String r0 = android.provider.Settings.System.getString(r0, r1)
            android.content.ContentResolver r1 = r6.getContentResolver()
            java.lang.String r2 = "op_ringtone2_df"
            java.lang.String r1 = android.provider.Settings.System.getString(r1, r2)
            android.net.Uri r2 = getStaticDefaultRingtoneUri(r6)
            java.lang.String r3 = "none"
            r4 = 0
            if (r0 == 0) goto L_0x0029
            boolean r5 = r0.equals(r3)
            if (r5 == 0) goto L_0x0024
            goto L_0x0030
        L_0x0024:
            java.lang.String r0 = r0.toString()
            goto L_0x0031
        L_0x0029:
            if (r2 == 0) goto L_0x0030
            java.lang.String r0 = r2.toString()
            goto L_0x0031
        L_0x0030:
            r0 = r4
        L_0x0031:
            if (r1 == 0) goto L_0x003f
            boolean r2 = r1.equals(r3)
            if (r2 == 0) goto L_0x003a
            goto L_0x0046
        L_0x003a:
            java.lang.String r1 = r1.toString()
            goto L_0x0047
        L_0x003f:
            if (r2 == 0) goto L_0x0046
            java.lang.String r1 = r2.toString()
            goto L_0x0047
        L_0x0046:
            r1 = r4
        L_0x0047:
            if (r0 != 0) goto L_0x004b
            r0 = r4
            goto L_0x004f
        L_0x004b:
            android.net.Uri r0 = android.net.Uri.parse(r0)
        L_0x004f:
            if (r1 != 0) goto L_0x0052
            goto L_0x0056
        L_0x0052:
            android.net.Uri r4 = android.net.Uri.parse(r1)
        L_0x0056:
            r1 = 0
            android.media.RingtoneManager.setActualRingtoneUriBySubId(r6, r1, r0)
            r0 = 1
            android.media.RingtoneManager.setActualRingtoneUriBySubId(r6, r0, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.ringtone.OPRingtoneManager.updateActualRingtone2(android.content.Context):void");
    }

    public static void updateDb(Context context, Uri uri, int i) {
        Boolean bool = Boolean.TRUE;
        if (uri != null) {
            ContentValues contentValues = new ContentValues();
            if (i != 2) {
                if (i == 4) {
                    contentValues.put("is_alarm", bool);
                } else if (i != 8) {
                    contentValues.put("is_ringtone", bool);
                }
                context.getContentResolver().update(uri, contentValues, null, null);
            }
            contentValues.put("is_notification", bool);
            context.getContentResolver().update(uri, contentValues, null, null);
        }
    }

    public static void setRingSimSwitch(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "op_sim_sw", i);
    }

    public static boolean isRingSimSwitchOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "op_sim_sw", 0) == 1;
    }

    public static boolean isDefault(Uri uri) {
        return RingtoneManager.isDefault(uri);
    }
}
