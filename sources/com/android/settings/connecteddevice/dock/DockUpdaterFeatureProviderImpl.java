package com.android.settings.connecteddevice.dock;

import android.content.Context;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.overlay.DockUpdaterFeatureProvider;

public class DockUpdaterFeatureProviderImpl implements DockUpdaterFeatureProvider {
    @Override // com.android.settings.overlay.DockUpdaterFeatureProvider
    public DockUpdater getConnectedDockUpdater(Context context, DevicePreferenceCallback devicePreferenceCallback) {
        return new DockUpdater(this) {
            /* class com.android.settings.connecteddevice.dock.DockUpdaterFeatureProviderImpl.AnonymousClass1 */
        };
    }

    @Override // com.android.settings.overlay.DockUpdaterFeatureProvider
    public DockUpdater getSavedDockUpdater(Context context, DevicePreferenceCallback devicePreferenceCallback) {
        return new DockUpdater(this) {
            /* class com.android.settings.connecteddevice.dock.DockUpdaterFeatureProviderImpl.AnonymousClass2 */
        };
    }
}
