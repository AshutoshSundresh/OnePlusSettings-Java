package com.android.settings.wifi.tether;

import android.database.ContentObserver;
import android.os.Handler;

public class TetherDataObserver extends ContentObserver {
    private OnTetherDataChangeCallback mCallback;

    public interface OnTetherDataChangeCallback {
        void onTetherDataChange();
    }

    public TetherDataObserver(OnTetherDataChangeCallback onTetherDataChangeCallback) {
        super(new Handler());
        this.mCallback = onTetherDataChangeCallback;
    }

    public void onChange(boolean z) {
        super.onChange(z);
        OnTetherDataChangeCallback onTetherDataChangeCallback = this.mCallback;
        if (onTetherDataChangeCallback != null) {
            onTetherDataChangeCallback.onTetherDataChange();
        }
    }
}
