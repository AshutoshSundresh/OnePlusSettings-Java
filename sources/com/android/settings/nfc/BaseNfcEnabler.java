package com.android.settings.nfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;

public abstract class BaseNfcEnabler {
    protected final Context mContext;
    private final IntentFilter mIntentFilter;
    protected final NfcAdapter mNfcAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.nfc.BaseNfcEnabler.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if ("android.nfc.action.ADAPTER_STATE_CHANGED".equals(intent.getAction())) {
                BaseNfcEnabler.this.handleNfcStateChanged(intent.getIntExtra("android.nfc.extra.ADAPTER_STATE", 1));
            }
        }
    };

    /* access modifiers changed from: protected */
    public abstract void handleNfcStateChanged(int i);

    public BaseNfcEnabler(Context context) {
        this.mContext = context;
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (!isNfcAvailable()) {
            this.mIntentFilter = null;
        } else {
            this.mIntentFilter = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");
        }
    }

    public void resume() {
        if (isNfcAvailable()) {
            handleNfcStateChanged(this.mNfcAdapter.getAdapterState());
            this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        }
    }

    public void pause() {
        if (isNfcAvailable()) {
            this.mContext.unregisterReceiver(this.mReceiver);
        }
    }

    public boolean isNfcAvailable() {
        return this.mNfcAdapter != null;
    }
}
