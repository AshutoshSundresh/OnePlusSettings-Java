package com.android.settings.wfd;

import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaRouter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class WifiDisplayPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private Preference mPreference;
    private final MediaRouter mRouter;
    private final MediaRouter.Callback mRouterCallback = new MediaRouter.SimpleCallback() {
        /* class com.android.settings.wfd.WifiDisplayPreferenceController.AnonymousClass1 */

        public void onRouteSelected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
            WifiDisplayPreferenceController wifiDisplayPreferenceController = WifiDisplayPreferenceController.this;
            wifiDisplayPreferenceController.refreshSummary(wifiDisplayPreferenceController.mPreference);
        }

        public void onRouteUnselected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
            WifiDisplayPreferenceController wifiDisplayPreferenceController = WifiDisplayPreferenceController.this;
            wifiDisplayPreferenceController.refreshSummary(wifiDisplayPreferenceController.mPreference);
        }

        public void onRouteAdded(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            WifiDisplayPreferenceController wifiDisplayPreferenceController = WifiDisplayPreferenceController.this;
            wifiDisplayPreferenceController.refreshSummary(wifiDisplayPreferenceController.mPreference);
        }

        public void onRouteRemoved(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            WifiDisplayPreferenceController wifiDisplayPreferenceController = WifiDisplayPreferenceController.this;
            wifiDisplayPreferenceController.refreshSummary(wifiDisplayPreferenceController.mPreference);
        }

        public void onRouteChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            WifiDisplayPreferenceController wifiDisplayPreferenceController = WifiDisplayPreferenceController.this;
            wifiDisplayPreferenceController.refreshSummary(wifiDisplayPreferenceController.mPreference);
        }
    };

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiDisplayPreferenceController(Context context, String str) {
        super(context, str);
        MediaRouter mediaRouter = (MediaRouter) context.getSystemService(MediaRouter.class);
        this.mRouter = mediaRouter;
        mediaRouter.setRouterGroupId("android.media.mirroring_group");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return WifiDisplaySettings.isAvailable(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        String string = this.mContext.getString(C0017R$string.disconnected);
        int routeCount = this.mRouter.getRouteCount();
        for (int i = 0; i < routeCount; i++) {
            MediaRouter.RouteInfo routeAt = this.mRouter.getRouteAt(i);
            if (routeAt.matchesTypes(4) && routeAt.isSelected() && !routeAt.isConnecting()) {
                return this.mContext.getString(C0017R$string.wifi_display_status_connected);
            }
        }
        return string;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mRouter.addCallback(4, this.mRouterCallback);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mRouter.removeCallback(this.mRouterCallback);
    }
}
