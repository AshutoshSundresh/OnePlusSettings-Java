package com.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

public class DBReadAsyncTask extends AsyncTask<Void, Void, Boolean> {
    final Uri CONTENT_URI;
    final Uri SNAP_CONTENT_URI;
    Context mContext;

    public DBReadAsyncTask(Context context) {
        Uri parse = Uri.parse("content://com.qti.smq.Feedback.provider");
        this.CONTENT_URI = parse;
        this.SNAP_CONTENT_URI = Uri.withAppendedPath(parse, "smq_settings");
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public Boolean doInBackground(Void... voidArr) {
        Cursor query = this.mContext.getContentResolver().query(this.SNAP_CONTENT_URI, null, "key=?", new String[]{"app_status"}, null);
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("smqpreferences", 0);
        if (query == null || query.getCount() <= 0) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt("app_status", 0);
            edit.commit();
        } else {
            query.moveToFirst();
            int i = query.getInt(1);
            if (sharedPreferences.getInt("app_status", 0) != i) {
                SharedPreferences.Editor edit2 = sharedPreferences.edit();
                edit2.putInt("app_status", i);
                edit2.commit();
            }
        }
        if (query != null) {
            query.close();
        }
        return Boolean.TRUE;
    }
}
