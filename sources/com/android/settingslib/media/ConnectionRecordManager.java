package com.android.settingslib.media;

import android.content.Context;
import android.content.SharedPreferences;

public class ConnectionRecordManager {
    private static ConnectionRecordManager sInstance;
    private static final Object sInstanceSync = new Object();
    private String mLastSelectedDevice;

    public static ConnectionRecordManager getInstance() {
        synchronized (sInstanceSync) {
            if (sInstance == null) {
                sInstance = new ConnectionRecordManager();
            }
        }
        return sInstance;
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("seamless_transfer_record", 0);
    }

    public synchronized int fetchConnectionRecord(Context context, String str) {
        return getSharedPreferences(context).getInt(str, 0);
    }

    public synchronized void fetchLastSelectedDevice(Context context) {
        this.mLastSelectedDevice = getSharedPreferences(context).getString("last_selected_device", null);
    }

    public synchronized void setConnectionRecord(Context context, String str, int i) {
        SharedPreferences.Editor edit = getSharedPreferences(context).edit();
        this.mLastSelectedDevice = str;
        edit.putInt(str, i);
        edit.putString("last_selected_device", this.mLastSelectedDevice);
        edit.apply();
    }

    public synchronized String getLastSelectedDevice() {
        return this.mLastSelectedDevice;
    }
}
