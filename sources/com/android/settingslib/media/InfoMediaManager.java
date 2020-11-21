package com.android.settingslib.media;

import android.app.Notification;
import android.content.Context;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import android.media.RoutingSessionInfo;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InfoMediaManager extends MediaManager {
    private static final boolean DEBUG = Log.isLoggable("InfoMediaManager", 3);
    private LocalBluetoothManager mBluetoothManager;
    private MediaDevice mCurrentConnectedDevice;
    @VisibleForTesting
    final Executor mExecutor = Executors.newSingleThreadExecutor();
    @VisibleForTesting
    final RouterManagerCallback mMediaRouterCallback = new RouterManagerCallback();
    @VisibleForTesting
    String mPackageName;
    @VisibleForTesting
    MediaRouter2Manager mRouterManager;

    public InfoMediaManager(Context context, String str, Notification notification, LocalBluetoothManager localBluetoothManager) {
        super(context, notification);
        this.mRouterManager = MediaRouter2Manager.getInstance(context);
        this.mBluetoothManager = localBluetoothManager;
        if (!TextUtils.isEmpty(str)) {
            this.mPackageName = str;
        }
    }

    public void startScan() {
        this.mMediaDevices.clear();
        this.mRouterManager.registerCallback(this.mExecutor, this.mMediaRouterCallback);
        refreshDevices();
    }

    public void stopScan() {
        this.mRouterManager.unregisterCallback(this.mMediaRouterCallback);
    }

    /* access modifiers changed from: package-private */
    public MediaDevice getCurrentConnectedDevice() {
        return this.mCurrentConnectedDevice;
    }

    /* access modifiers changed from: package-private */
    public boolean connectDeviceWithoutPackageName(MediaDevice mediaDevice) {
        List activeSessions = this.mRouterManager.getActiveSessions();
        if (activeSessions.size() <= 0) {
            return false;
        }
        this.mRouterManager.transfer((RoutingSessionInfo) activeSessions.get(0), mediaDevice.mRouteInfo);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean addDeviceToPlayMedia(MediaDevice mediaDevice) {
        if (TextUtils.isEmpty(this.mPackageName)) {
            Log.w("InfoMediaManager", "addDeviceToPlayMedia() package name is null or empty!");
            return false;
        }
        RoutingSessionInfo routingSessionInfo = getRoutingSessionInfo();
        if (routingSessionInfo == null || !routingSessionInfo.getSelectableRoutes().contains(mediaDevice.mRouteInfo.getId())) {
            Log.w("InfoMediaManager", "addDeviceToPlayMedia() Ignoring selecting a non-selectable device : " + mediaDevice.getName());
            return false;
        }
        this.mRouterManager.selectRoute(routingSessionInfo, mediaDevice.mRouteInfo);
        return true;
    }

    private RoutingSessionInfo getRoutingSessionInfo() {
        List routingSessions = this.mRouterManager.getRoutingSessions(this.mPackageName);
        return (RoutingSessionInfo) routingSessions.get(routingSessions.size() - 1);
    }

    /* access modifiers changed from: package-private */
    public boolean removeDeviceFromPlayMedia(MediaDevice mediaDevice) {
        if (TextUtils.isEmpty(this.mPackageName)) {
            Log.w("InfoMediaManager", "removeDeviceFromMedia() package name is null or empty!");
            return false;
        }
        RoutingSessionInfo routingSessionInfo = getRoutingSessionInfo();
        if (routingSessionInfo == null || !routingSessionInfo.getSelectedRoutes().contains(mediaDevice.mRouteInfo.getId())) {
            Log.w("InfoMediaManager", "removeDeviceFromMedia() Ignoring deselecting a non-deselectable device : " + mediaDevice.getName());
            return false;
        }
        this.mRouterManager.deselectRoute(routingSessionInfo, mediaDevice.mRouteInfo);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean releaseSession() {
        if (TextUtils.isEmpty(this.mPackageName)) {
            Log.w("InfoMediaManager", "releaseSession() package name is null or empty!");
            return false;
        }
        RoutingSessionInfo routingSessionInfo = getRoutingSessionInfo();
        if (routingSessionInfo != null) {
            this.mRouterManager.releaseSession(routingSessionInfo);
            return true;
        }
        Log.w("InfoMediaManager", "releaseSession() Ignoring release session : " + this.mPackageName);
        return false;
    }

    /* access modifiers changed from: package-private */
    public List<MediaDevice> getSelectableMediaDevice() {
        ArrayList arrayList = new ArrayList();
        if (TextUtils.isEmpty(this.mPackageName)) {
            Log.w("InfoMediaManager", "getSelectableMediaDevice() package name is null or empty!");
            return arrayList;
        }
        RoutingSessionInfo routingSessionInfo = getRoutingSessionInfo();
        if (routingSessionInfo != null) {
            for (MediaRoute2Info mediaRoute2Info : this.mRouterManager.getSelectableRoutes(routingSessionInfo)) {
                arrayList.add(new InfoMediaDevice(this.mContext, this.mRouterManager, mediaRoute2Info, this.mPackageName));
            }
            return arrayList;
        }
        Log.w("InfoMediaManager", "getSelectableMediaDevice() cannot found selectable MediaDevice from : " + this.mPackageName);
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public List<MediaDevice> getDeselectableMediaDevice() {
        ArrayList arrayList = new ArrayList();
        if (TextUtils.isEmpty(this.mPackageName)) {
            Log.d("InfoMediaManager", "getDeselectableMediaDevice() package name is null or empty!");
            return arrayList;
        }
        RoutingSessionInfo routingSessionInfo = getRoutingSessionInfo();
        if (routingSessionInfo != null) {
            for (MediaRoute2Info mediaRoute2Info : this.mRouterManager.getDeselectableRoutes(routingSessionInfo)) {
                arrayList.add(new InfoMediaDevice(this.mContext, this.mRouterManager, mediaRoute2Info, this.mPackageName));
                Log.d("InfoMediaManager", ((Object) mediaRoute2Info.getName()) + " is deselectable for " + this.mPackageName);
            }
            return arrayList;
        }
        Log.d("InfoMediaManager", "getDeselectableMediaDevice() cannot found deselectable MediaDevice from : " + this.mPackageName);
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public List<MediaDevice> getSelectedMediaDevice() {
        ArrayList arrayList = new ArrayList();
        if (TextUtils.isEmpty(this.mPackageName)) {
            Log.w("InfoMediaManager", "getSelectedMediaDevice() package name is null or empty!");
            return arrayList;
        }
        RoutingSessionInfo routingSessionInfo = getRoutingSessionInfo();
        if (routingSessionInfo != null) {
            for (MediaRoute2Info mediaRoute2Info : this.mRouterManager.getSelectedRoutes(routingSessionInfo)) {
                arrayList.add(new InfoMediaDevice(this.mContext, this.mRouterManager, mediaRoute2Info, this.mPackageName));
            }
            return arrayList;
        }
        Log.w("InfoMediaManager", "getSelectedMediaDevice() cannot found selectable MediaDevice from : " + this.mPackageName);
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public void adjustSessionVolume(RoutingSessionInfo routingSessionInfo, int i) {
        if (routingSessionInfo == null) {
            Log.w("InfoMediaManager", "Unable to adjust session volume. RoutingSessionInfo is empty");
        } else {
            this.mRouterManager.setSessionVolume(routingSessionInfo, i);
        }
    }

    /* access modifiers changed from: package-private */
    public void adjustSessionVolume(int i) {
        if (TextUtils.isEmpty(this.mPackageName)) {
            Log.w("InfoMediaManager", "adjustSessionVolume() package name is null or empty!");
            return;
        }
        RoutingSessionInfo routingSessionInfo = getRoutingSessionInfo();
        if (routingSessionInfo != null) {
            Log.d("InfoMediaManager", "adjustSessionVolume() adjust volume : " + i + ", with : " + this.mPackageName);
            this.mRouterManager.setSessionVolume(routingSessionInfo, i);
            return;
        }
        Log.w("InfoMediaManager", "adjustSessionVolume() can't found corresponding RoutingSession with : " + this.mPackageName);
    }

    public int getSessionVolumeMax() {
        if (TextUtils.isEmpty(this.mPackageName)) {
            Log.w("InfoMediaManager", "getSessionVolumeMax() package name is null or empty!");
            return -1;
        }
        RoutingSessionInfo routingSessionInfo = getRoutingSessionInfo();
        if (routingSessionInfo != null) {
            return routingSessionInfo.getVolumeMax();
        }
        Log.w("InfoMediaManager", "getSessionVolumeMax() can't found corresponding RoutingSession with : " + this.mPackageName);
        return -1;
    }

    public int getSessionVolume() {
        if (TextUtils.isEmpty(this.mPackageName)) {
            Log.w("InfoMediaManager", "getSessionVolume() package name is null or empty!");
            return -1;
        }
        RoutingSessionInfo routingSessionInfo = getRoutingSessionInfo();
        if (routingSessionInfo != null) {
            return routingSessionInfo.getVolume();
        }
        Log.w("InfoMediaManager", "getSessionVolume() can't found corresponding RoutingSession with : " + this.mPackageName);
        return -1;
    }

    /* access modifiers changed from: package-private */
    public CharSequence getSessionName() {
        if (TextUtils.isEmpty(this.mPackageName)) {
            Log.w("InfoMediaManager", "Unable to get session name. The package name is null or empty!");
            return null;
        }
        RoutingSessionInfo routingSessionInfo = getRoutingSessionInfo();
        if (routingSessionInfo != null) {
            return routingSessionInfo.getName();
        }
        Log.w("InfoMediaManager", "Unable to get session name for package: " + this.mPackageName);
        return null;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshDevices() {
        this.mMediaDevices.clear();
        this.mCurrentConnectedDevice = null;
        if (TextUtils.isEmpty(this.mPackageName)) {
            buildAllRoutes();
        } else {
            buildAvailableRoutes();
        }
        dispatchDeviceListAdded();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void buildAllRoutes() {
        for (MediaRoute2Info mediaRoute2Info : this.mRouterManager.getAllRoutes()) {
            if (DEBUG) {
                Log.d("InfoMediaManager", "buildAllRoutes() route : " + ((Object) mediaRoute2Info.getName()) + ", volume : " + mediaRoute2Info.getVolume() + ", type : " + mediaRoute2Info.getType());
            }
            if (mediaRoute2Info.isSystemRoute()) {
                addMediaDevice(mediaRoute2Info);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public List<RoutingSessionInfo> getActiveMediaSession() {
        return this.mRouterManager.getActiveSessions();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void buildAvailableRoutes() {
        for (MediaRoute2Info mediaRoute2Info : this.mRouterManager.getAvailableRoutes(this.mPackageName)) {
            if (DEBUG) {
                Log.d("InfoMediaManager", "buildAvailableRoutes() route : " + ((Object) mediaRoute2Info.getName()) + ", volume : " + mediaRoute2Info.getVolume() + ", type : " + mediaRoute2Info.getType());
            }
            addMediaDevice(mediaRoute2Info);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addMediaDevice(android.media.MediaRoute2Info r9) {
        /*
        // Method dump skipped, instructions count: 182
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.media.InfoMediaManager.addMediaDevice(android.media.MediaRoute2Info):void");
    }

    /* access modifiers changed from: package-private */
    public class RouterManagerCallback extends MediaRouter2Manager.Callback {
        RouterManagerCallback() {
        }

        public void onRoutesAdded(List<MediaRoute2Info> list) {
            InfoMediaManager.this.refreshDevices();
        }

        public void onPreferredFeaturesChanged(String str, List<String> list) {
            if (TextUtils.equals(InfoMediaManager.this.mPackageName, str)) {
                InfoMediaManager.this.refreshDevices();
            }
        }

        public void onRoutesChanged(List<MediaRoute2Info> list) {
            InfoMediaManager.this.refreshDevices();
        }

        public void onRoutesRemoved(List<MediaRoute2Info> list) {
            InfoMediaManager.this.refreshDevices();
        }

        public void onTransferred(RoutingSessionInfo routingSessionInfo, RoutingSessionInfo routingSessionInfo2) {
            if (InfoMediaManager.DEBUG) {
                Log.d("InfoMediaManager", "onTransferred() oldSession : " + ((Object) routingSessionInfo.getName()) + ", newSession : " + ((Object) routingSessionInfo2.getName()));
            }
            InfoMediaManager.this.mMediaDevices.clear();
            String str = null;
            InfoMediaManager.this.mCurrentConnectedDevice = null;
            if (TextUtils.isEmpty(InfoMediaManager.this.mPackageName)) {
                InfoMediaManager.this.buildAllRoutes();
            } else {
                InfoMediaManager.this.buildAvailableRoutes();
            }
            if (InfoMediaManager.this.mCurrentConnectedDevice != null) {
                str = InfoMediaManager.this.mCurrentConnectedDevice.getId();
            }
            InfoMediaManager.this.dispatchConnectedDeviceChanged(str);
        }

        public void onTransferFailed(RoutingSessionInfo routingSessionInfo, MediaRoute2Info mediaRoute2Info) {
            InfoMediaManager.this.dispatchOnRequestFailed(0);
        }

        public void onRequestFailed(int i) {
            InfoMediaManager.this.dispatchOnRequestFailed(i);
        }

        public void onSessionUpdated(RoutingSessionInfo routingSessionInfo) {
            InfoMediaManager.this.dispatchDataChanged();
        }
    }
}
