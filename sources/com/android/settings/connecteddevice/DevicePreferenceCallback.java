package com.android.settings.connecteddevice;

import androidx.preference.Preference;

public interface DevicePreferenceCallback {
    void onDeviceAdded(Preference preference);

    void onDeviceRemoved(Preference preference);
}
