package com.oneplus.settings.statusbar;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;

public class Utils {
    private ContentResolver mContentResolver;
    private Context mContext;
    private int mCurrentUser;

    public Utils(Context context) {
        this.mContext = context;
        if (context != null) {
            this.mContentResolver = context.getContentResolver();
        }
        this.mCurrentUser = ActivityManager.getCurrentUser();
    }

    public static ArraySet<String> getIconBlacklist(String str) {
        ArraySet<String> arraySet = new ArraySet<>();
        if (str == null) {
            TelephonyManager telephonyManager = TelephonyManager.getDefault();
            if (telephonyManager == null || !TextUtils.equals(telephonyManager.getSimOperatorNumeric(SubscriptionManager.getDefaultDataSubscriptionId()), "23410")) {
                str = "rotate,networkspeed";
            } else {
                Log.d("Utils", "O2 UK sim, add volte/vowifi to blacklist by default");
                str = "rotate,networkspeed,volte,vowifi";
            }
        }
        String[] split = str.split(",");
        for (String str2 : split) {
            if (!TextUtils.isEmpty(str2)) {
                arraySet.add(str2);
            }
        }
        return arraySet;
    }

    public String getValue(String str) {
        return Settings.Secure.getStringForUser(this.mContentResolver, str, this.mCurrentUser);
    }

    public void setValue(String str, String str2) {
        Settings.Secure.putStringForUser(this.mContentResolver, str, str2, this.mCurrentUser);
    }

    public int getValue(String str, int i) {
        return Settings.Secure.getIntForUser(this.mContentResolver, str, i, this.mCurrentUser);
    }

    public void setValue(String str, int i) {
        Settings.Secure.putIntForUser(this.mContentResolver, str, i, this.mCurrentUser);
    }
}
