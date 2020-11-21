package com.android.settingslib.media;

import android.media.MediaRoute2Info;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;

public class MediaDeviceUtils {
    public static String getId(CachedBluetoothDevice cachedBluetoothDevice) {
        if (cachedBluetoothDevice.isHearingAidDevice()) {
            return Long.toString(cachedBluetoothDevice.getHiSyncId());
        }
        return cachedBluetoothDevice.getAddress();
    }

    public static String getId(MediaRoute2Info mediaRoute2Info) {
        return mediaRoute2Info.getId();
    }
}
