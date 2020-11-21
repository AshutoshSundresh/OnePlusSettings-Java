package com.bumptech.glide.load.data.mediastore;

import android.content.ContentResolver;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class ThumbnailStreamOpener {
    private static final FileService DEFAULT_SERVICE = new FileService();
    private final ArrayPool byteArrayPool;
    private final ContentResolver contentResolver;
    private final List<ImageHeaderParser> parsers;
    private final ThumbnailQuery query;
    private final FileService service;

    ThumbnailStreamOpener(List<ImageHeaderParser> list, ThumbnailQuery thumbnailQuery, ArrayPool arrayPool, ContentResolver contentResolver2) {
        this(list, DEFAULT_SERVICE, thumbnailQuery, arrayPool, contentResolver2);
    }

    ThumbnailStreamOpener(List<ImageHeaderParser> list, FileService fileService, ThumbnailQuery thumbnailQuery, ArrayPool arrayPool, ContentResolver contentResolver2) {
        this.service = fileService;
        this.query = thumbnailQuery;
        this.byteArrayPool = arrayPool;
        this.contentResolver = contentResolver2;
        this.parsers = list;
    }

    /* access modifiers changed from: package-private */
    public int getOrientation(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = this.contentResolver.openInputStream(uri);
            int orientation = ImageHeaderParserUtils.getOrientation(this.parsers, inputStream, this.byteArrayPool);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused) {
                }
            }
            return orientation;
        } catch (IOException | NullPointerException e) {
            if (Log.isLoggable("ThumbStreamOpener", 3)) {
                Log.d("ThumbStreamOpener", "Failed to open uri: " + uri, e);
            }
            if (inputStream == null) {
                return -1;
            }
            try {
                inputStream.close();
                return -1;
            } catch (IOException unused2) {
                return -1;
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused3) {
                }
            }
            throw th;
        }
    }

    public InputStream open(Uri uri) throws FileNotFoundException {
        String path = getPath(uri);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File file = this.service.get(path);
        if (!isValid(file)) {
            return null;
        }
        Uri fromFile = Uri.fromFile(file);
        try {
            return this.contentResolver.openInputStream(fromFile);
        } catch (NullPointerException e) {
            throw ((FileNotFoundException) new FileNotFoundException("NPE opening uri: " + uri + " -> " + fromFile).initCause(e));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x002f A[Catch:{ all -> 0x0049 }] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x004d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getPath(android.net.Uri r6) {
        /*
            r5 = this;
            java.lang.String r0 = "ThumbStreamOpener"
            r1 = 0
            com.bumptech.glide.load.data.mediastore.ThumbnailQuery r5 = r5.query     // Catch:{ SecurityException -> 0x0026, all -> 0x0024 }
            android.database.Cursor r5 = r5.query(r6)     // Catch:{ SecurityException -> 0x0026, all -> 0x0024 }
            if (r5 == 0) goto L_0x001e
            boolean r2 = r5.moveToFirst()     // Catch:{ SecurityException -> 0x001c }
            if (r2 == 0) goto L_0x001e
            r2 = 0
            java.lang.String r6 = r5.getString(r2)     // Catch:{ SecurityException -> 0x001c }
            if (r5 == 0) goto L_0x001b
            r5.close()
        L_0x001b:
            return r6
        L_0x001c:
            r2 = move-exception
            goto L_0x0028
        L_0x001e:
            if (r5 == 0) goto L_0x0023
            r5.close()
        L_0x0023:
            return r1
        L_0x0024:
            r6 = move-exception
            goto L_0x004b
        L_0x0026:
            r2 = move-exception
            r5 = r1
        L_0x0028:
            r3 = 3
            boolean r3 = android.util.Log.isLoggable(r0, r3)     // Catch:{ all -> 0x0049 }
            if (r3 == 0) goto L_0x0043
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0049 }
            r3.<init>()     // Catch:{ all -> 0x0049 }
            java.lang.String r4 = "Failed to query for thumbnail for Uri: "
            r3.append(r4)     // Catch:{ all -> 0x0049 }
            r3.append(r6)     // Catch:{ all -> 0x0049 }
            java.lang.String r6 = r3.toString()     // Catch:{ all -> 0x0049 }
            android.util.Log.d(r0, r6, r2)     // Catch:{ all -> 0x0049 }
        L_0x0043:
            if (r5 == 0) goto L_0x0048
            r5.close()
        L_0x0048:
            return r1
        L_0x0049:
            r6 = move-exception
            r1 = r5
        L_0x004b:
            if (r1 == 0) goto L_0x0050
            r1.close()
        L_0x0050:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.data.mediastore.ThumbnailStreamOpener.getPath(android.net.Uri):java.lang.String");
    }

    private boolean isValid(File file) {
        return this.service.exists(file) && 0 < this.service.length(file);
    }
}
