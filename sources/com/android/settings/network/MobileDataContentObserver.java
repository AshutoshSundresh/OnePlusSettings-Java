package com.android.settings.network;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class MobileDataContentObserver extends ContentObserver {
    private OnMobileDataChangedListener mListener;

    public interface OnMobileDataChangedListener {
        void onMobileDataChanged();
    }

    public MobileDataContentObserver(Handler handler) {
        super(handler);
    }

    public static Uri getObservableUri(Context context, int i) {
        Uri uriFor = Settings.Global.getUriFor("mobile_data");
        if (((TelephonyManager) context.getSystemService(TelephonyManager.class)).getActiveModemCount() == 1) {
            return uriFor;
        }
        return Settings.Global.getUriFor("mobile_data" + i);
    }

    public void setOnMobileDataChangedListener(OnMobileDataChangedListener onMobileDataChangedListener) {
        this.mListener = onMobileDataChangedListener;
    }

    public void onChange(boolean z) {
        super.onChange(z);
        OnMobileDataChangedListener onMobileDataChangedListener = this.mListener;
        if (onMobileDataChangedListener != null) {
            onMobileDataChangedListener.onMobileDataChanged();
        }
    }

    public void register(Context context, int i) {
        context.getContentResolver().registerContentObserver(getObservableUri(context, i), false, this);
    }

    public void unRegister(Context context) {
        context.getContentResolver().unregisterContentObserver(this);
    }
}
