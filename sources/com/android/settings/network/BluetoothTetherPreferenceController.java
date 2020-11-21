package com.android.settings.network;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.slices.SliceBackgroundWorker;

public final class BluetoothTetherPreferenceController extends TetherBasePreferenceController {
    final BroadcastReceiver mBluetoothChangeReceiver = new BroadcastReceiver() {
        /* class com.android.settings.network.BluetoothTetherPreferenceController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals("android.bluetooth.adapter.action.STATE_CHANGED", intent.getAction())) {
                BluetoothTetherPreferenceController.this.mBluetoothState = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                BluetoothTetherPreferenceController bluetoothTetherPreferenceController = BluetoothTetherPreferenceController.this;
                bluetoothTetherPreferenceController.updateState(bluetoothTetherPreferenceController.mPreference);
            }
        }
    };
    private int mBluetoothState;

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public int getTetherType() {
        return 2;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BluetoothTetherPreferenceController(Context context, String str) {
        super(context, str);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mBluetoothState = BluetoothAdapter.getDefaultAdapter().getState();
        this.mContext.registerReceiver(this.mBluetoothChangeReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mContext.unregisterReceiver(this.mBluetoothChangeReceiver);
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldEnable() {
        int i = this.mBluetoothState;
        return i == Integer.MIN_VALUE || i == 10 || i == 12;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldShow() {
        String[] tetherableBluetoothRegexs = this.mCm.getTetherableBluetoothRegexs();
        return (tetherableBluetoothRegexs == null || tetherableBluetoothRegexs.length == 0) ? false : true;
    }
}
