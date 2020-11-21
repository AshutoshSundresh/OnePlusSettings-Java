package com.android.settings.network;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

public class PreferredNetworkModeContentObserver extends ContentObserver {
    OnPreferredNetworkModeChangedListener mListener;

    public interface OnPreferredNetworkModeChangedListener {
        void onPreferredNetworkModeChanged();
    }

    public PreferredNetworkModeContentObserver(Handler handler) {
        super(handler);
    }

    public void setPreferredNetworkModeChangedListener(OnPreferredNetworkModeChangedListener onPreferredNetworkModeChangedListener) {
        this.mListener = onPreferredNetworkModeChangedListener;
    }

    public void onChange(boolean z) {
        super.onChange(z);
        OnPreferredNetworkModeChangedListener onPreferredNetworkModeChangedListener = this.mListener;
        if (onPreferredNetworkModeChangedListener != null) {
            onPreferredNetworkModeChangedListener.onPreferredNetworkModeChanged();
        }
    }

    public void register(Context context, int i) {
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("preferred_network_mode" + i), false, this);
    }

    public void unregister(Context context) {
        context.getContentResolver().unregisterContentObserver(this);
    }
}
