package com.android.settings.wifi.calling;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;

@VisibleForTesting
public abstract class DisclaimerItem {
    private final CarrierConfigManager mCarrierConfigManager;
    protected final Context mContext;
    protected final int mSubId;

    /* access modifiers changed from: protected */
    public abstract int getMessageId();

    /* access modifiers changed from: protected */
    public abstract String getName();

    /* access modifiers changed from: protected */
    public abstract String getPrefKey();

    /* access modifiers changed from: protected */
    public abstract int getTitleId();

    DisclaimerItem(Context context, int i) {
        this.mContext = context;
        this.mSubId = i;
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    /* access modifiers changed from: package-private */
    public void onAgreed() {
        setBooleanSharedPrefs(getPrefKey(), true);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldShow() {
        if (getBooleanSharedPrefs(getPrefKey(), false)) {
            logd("shouldShow: false due to a user has already agreed.");
            return false;
        }
        logd("shouldShow: true");
        return true;
    }

    /* access modifiers changed from: protected */
    public PersistableBundle getCarrierConfig() {
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(this.mSubId);
        if (configForSubId != null) {
            return configForSubId;
        }
        return CarrierConfigManager.getDefaultConfig();
    }

    /* access modifiers changed from: protected */
    public void logd(String str) {
        String name = getName();
        Log.d(name, "[" + this.mSubId + "] " + str);
    }

    private boolean getBooleanSharedPrefs(String str, boolean z) {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("wfc_disclaimer_prefs", 0);
        return sharedPreferences.getBoolean(str + this.mSubId, z);
    }

    private void setBooleanSharedPrefs(String str, boolean z) {
        SharedPreferences.Editor edit = this.mContext.getSharedPreferences("wfc_disclaimer_prefs", 0).edit();
        edit.putBoolean(str + this.mSubId, z).apply();
    }
}
