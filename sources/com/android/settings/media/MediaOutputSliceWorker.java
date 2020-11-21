package com.android.settings.media;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.util.Log;
import com.android.settings.core.instrumentation.SettingsStatsLog;
import com.android.settings.wifi.UseOpenWifiPreferenceController;
import com.android.settingslib.media.MediaDevice;

public class MediaOutputSliceWorker extends MediaDeviceUpdateWorker {
    private static final boolean DBG = Log.isLoggable("MediaOutputSliceWorker", 3);
    private int mAppliedDeviceCountWithinRemoteGroup;
    private int mConnectedBluetoothDeviceCount;
    private int mRemoteDeviceCount;
    private MediaDevice mSourceDevice;
    private MediaDevice mTargetDevice;
    private int mWiredDeviceCount;

    private int getLoggingSwitchOpSubResult(int i) {
        if (i == 1) {
            return 2;
        }
        if (i == 2) {
            return 3;
        }
        if (i != 3) {
            return i != 4 ? 0 : 5;
        }
        return 4;
    }

    public MediaOutputSliceWorker(Context context, Uri uri) {
        super(context, uri);
    }

    @Override // com.android.settings.media.MediaDeviceUpdateWorker
    public void connectDevice(MediaDevice mediaDevice) {
        this.mSourceDevice = this.mLocalMediaManager.getCurrentConnectedDevice();
        this.mTargetDevice = mediaDevice;
        if (DBG) {
            Log.d("MediaOutputSliceWorker", "connectDevice - source:" + this.mSourceDevice.toString() + " target:" + this.mTargetDevice.toString());
        }
        super.connectDevice(mediaDevice);
    }

    private int getLoggingDeviceType(MediaDevice mediaDevice, boolean z) {
        int deviceType = mediaDevice.getDeviceType();
        if (deviceType == 1) {
            return 200;
        }
        if (deviceType == 2) {
            return 100;
        }
        if (deviceType == 4) {
            return 300;
        }
        if (deviceType == 5) {
            return UseOpenWifiPreferenceController.REQUEST_CODE_OPEN_WIFI_AUTOMATICALLY;
        }
        if (deviceType != 6) {
            return deviceType != 7 ? 0 : 1;
        }
        return 500;
    }

    private String getLoggingPackageName() {
        String packageName = getPackageName();
        if (packageName == null || packageName.isEmpty()) {
            return "";
        }
        try {
            ApplicationInfo applicationInfo = ((MediaDeviceUpdateWorker) this).mContext.getPackageManager().getApplicationInfo(packageName, 0);
            if ((applicationInfo.flags & 1) == 0 && (applicationInfo.flags & 128) == 0) {
                return "";
            }
            return packageName;
        } catch (Exception unused) {
            Log.e("MediaOutputSliceWorker", packageName + "is invalid.");
            return "";
        }
    }

    private void updateLoggingDeviceCount() {
        this.mRemoteDeviceCount = 0;
        this.mConnectedBluetoothDeviceCount = 0;
        this.mWiredDeviceCount = 0;
        this.mAppliedDeviceCountWithinRemoteGroup = 0;
        for (MediaDevice mediaDevice : this.mMediaDevices) {
            if (mediaDevice.isConnected()) {
                int deviceType = mediaDevice.getDeviceType();
                if (deviceType == 1 || deviceType == 2) {
                    this.mWiredDeviceCount++;
                } else if (deviceType == 4) {
                    this.mConnectedBluetoothDeviceCount++;
                } else if (deviceType == 5 || deviceType == 6) {
                    this.mRemoteDeviceCount++;
                }
            }
        }
        if (DBG) {
            Log.d("MediaOutputSliceWorker", "connected devices: wired: " + this.mWiredDeviceCount + " bluetooth: " + this.mConnectedBluetoothDeviceCount + " remote: " + this.mRemoteDeviceCount);
        }
    }

    @Override // com.android.settings.media.MediaDeviceUpdateWorker, com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
        if (DBG) {
            Log.d("MediaOutputSliceWorker", "onSelectedDeviceStateChanged - " + mediaDevice.toString());
        }
        updateLoggingDeviceCount();
        SettingsStatsLog.write(277, getLoggingDeviceType(this.mSourceDevice, true), getLoggingDeviceType(this.mTargetDevice, false), 1, 1, getLoggingPackageName(), this.mWiredDeviceCount, this.mConnectedBluetoothDeviceCount, this.mRemoteDeviceCount, this.mAppliedDeviceCountWithinRemoteGroup);
        super.onSelectedDeviceStateChanged(mediaDevice, i);
    }

    @Override // com.android.settings.media.MediaDeviceUpdateWorker, com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onRequestFailed(int i) {
        if (DBG) {
            Log.e("MediaOutputSliceWorker", "onRequestFailed - " + i);
        }
        updateLoggingDeviceCount();
        SettingsStatsLog.write(277, getLoggingDeviceType(this.mSourceDevice, true), getLoggingDeviceType(this.mTargetDevice, false), 0, getLoggingSwitchOpSubResult(i), getLoggingPackageName(), this.mWiredDeviceCount, this.mConnectedBluetoothDeviceCount, this.mRemoteDeviceCount, this.mAppliedDeviceCountWithinRemoteGroup);
        super.onRequestFailed(i);
    }
}
