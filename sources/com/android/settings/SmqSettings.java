package com.android.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SmqSettings {
    private Context mContext;
    private SharedPreferences mSmqPreferences = this.mContext.getSharedPreferences("smqpreferences", 0);

    public SmqSettings(Context context) {
        this.mContext = context;
        new DBReadAsyncTask(this.mContext).execute(new Void[0]);
    }

    public boolean isShowSmqSettings() {
        return this.mSmqPreferences.getInt("app_status", 0) > 0;
    }
}
