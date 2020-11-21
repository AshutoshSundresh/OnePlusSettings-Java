package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;

public class ConnectedBluetoothDeviceUpdater extends BluetoothDeviceUpdater {
    private static final boolean DBG = Log.isLoggable("ConnBluetoothDeviceUpdater", 3);
    private final AudioManager mAudioManager;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public String getPreferenceKey() {
        return "connected_bt";
    }

    public ConnectedBluetoothDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback) {
        super(context, dashboardFragment, devicePreferenceCallback);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onAudioModeChanged() {
        forceUpdate();
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x004f  */
    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isFilterMatched(com.android.settingslib.bluetooth.CachedBluetoothDevice r7) {
        /*
        // Method dump skipped, instructions count: 112
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.ConnectedBluetoothDeviceUpdater.isFilterMatched(com.android.settingslib.bluetooth.CachedBluetoothDevice):boolean");
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public void addPreference(CachedBluetoothDevice cachedBluetoothDevice) {
        super.addPreference(cachedBluetoothDevice);
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        if (this.mPreferenceMap.containsKey(device)) {
            BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) this.mPreferenceMap.get(device);
            bluetoothDevicePreference.setOnGearClickListener(null);
            bluetoothDevicePreference.hideSecondTarget(true);
            bluetoothDevicePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                /* class com.android.settings.bluetooth.$$Lambda$ConnectedBluetoothDeviceUpdater$T3urOfMHy8RLQrXI0UXFpS1IUU */

                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return ConnectedBluetoothDeviceUpdater.this.lambda$addPreference$0$ConnectedBluetoothDeviceUpdater(preference);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addPreference$0 */
    public /* synthetic */ boolean lambda$addPreference$0$ConnectedBluetoothDeviceUpdater(Preference preference) {
        lambda$new$0(preference);
        return true;
    }
}
