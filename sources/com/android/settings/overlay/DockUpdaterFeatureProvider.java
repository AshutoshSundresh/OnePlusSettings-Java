package com.android.settings.overlay;

import android.content.Context;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.connecteddevice.dock.DockUpdater;

public interface DockUpdaterFeatureProvider {
    DockUpdater getConnectedDockUpdater(Context context, DevicePreferenceCallback devicePreferenceCallback);

    DockUpdater getSavedDockUpdater(Context context, DevicePreferenceCallback devicePreferenceCallback);
}
