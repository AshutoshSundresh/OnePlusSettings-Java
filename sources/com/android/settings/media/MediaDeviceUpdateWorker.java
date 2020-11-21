package com.android.settings.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RoutingSessionInfo;
import android.net.Uri;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.Utils;
import com.android.settingslib.media.LocalMediaManager;
import com.android.settingslib.media.MediaDevice;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MediaDeviceUpdateWorker extends SliceBackgroundWorker implements LocalMediaManager.DeviceCallback {
    protected final Context mContext;
    private boolean mIsTouched;
    LocalMediaManager mLocalMediaManager;
    protected final Collection<MediaDevice> mMediaDevices = new CopyOnWriteArrayList();
    private final String mPackageName;
    private final DevicesChangedBroadcastReceiver mReceiver;
    private MediaDevice mTopDevice;

    public MediaDeviceUpdateWorker(Context context, Uri uri) {
        super(context, uri);
        this.mContext = context;
        this.mPackageName = uri.getQueryParameter("media_package_name");
        this.mReceiver = new DevicesChangedBroadcastReceiver();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.slices.SliceBackgroundWorker
    public void onSlicePinned() {
        this.mMediaDevices.clear();
        this.mIsTouched = false;
        LocalMediaManager localMediaManager = this.mLocalMediaManager;
        if (localMediaManager == null || !TextUtils.equals(this.mPackageName, localMediaManager.getPackageName())) {
            this.mLocalMediaManager = new LocalMediaManager(this.mContext, this.mPackageName, null);
        }
        this.mLocalMediaManager.registerCallback(this);
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("android.media.STREAM_DEVICES_CHANGED_ACTION"));
        this.mLocalMediaManager.startScan();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.slices.SliceBackgroundWorker
    public void onSliceUnpinned() {
        this.mLocalMediaManager.unregisterCallback(this);
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mLocalMediaManager.stopScan();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.mLocalMediaManager = null;
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onDeviceListUpdate(List<MediaDevice> list) {
        buildMediaDevices(list);
        notifySliceChange();
    }

    private void buildMediaDevices(List<MediaDevice> list) {
        this.mMediaDevices.clear();
        this.mMediaDevices.addAll(list);
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
        notifySliceChange();
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onDeviceAttributesChanged() {
        notifySliceChange();
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onRequestFailed(int i) {
        notifySliceChange();
    }

    public Collection<MediaDevice> getMediaDevices() {
        return this.mMediaDevices;
    }

    public void connectDevice(MediaDevice mediaDevice) {
        ThreadUtils.postOnBackgroundThread(new Runnable(mediaDevice) {
            /* class com.android.settings.media.$$Lambda$MediaDeviceUpdateWorker$StU8riXozfT724iqWr07BomaPA */
            public final /* synthetic */ MediaDevice f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDeviceUpdateWorker.this.lambda$connectDevice$1$MediaDeviceUpdateWorker(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$connectDevice$1 */
    public /* synthetic */ void lambda$connectDevice$1$MediaDeviceUpdateWorker(MediaDevice mediaDevice) {
        if (this.mLocalMediaManager.connectDevice(mediaDevice)) {
            ThreadUtils.postOnMainThread(new Runnable() {
                /* class com.android.settings.media.$$Lambda$MediaDeviceUpdateWorker$uxnvOiUPk4EhidwtbhIg6HGJ_mw */

                public final void run() {
                    MediaDeviceUpdateWorker.this.lambda$connectDevice$0$MediaDeviceUpdateWorker();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$connectDevice$0 */
    public /* synthetic */ void lambda$connectDevice$0$MediaDeviceUpdateWorker() {
        notifySliceChange();
    }

    public MediaDevice getMediaDeviceById(String str) {
        return this.mLocalMediaManager.getMediaDeviceById(new ArrayList(this.mMediaDevices), str);
    }

    public MediaDevice getCurrentConnectedMediaDevice() {
        return this.mLocalMediaManager.getCurrentConnectedDevice();
    }

    /* access modifiers changed from: package-private */
    public void setIsTouched(boolean z) {
        this.mIsTouched = z;
    }

    /* access modifiers changed from: package-private */
    public boolean getIsTouched() {
        return this.mIsTouched;
    }

    /* access modifiers changed from: package-private */
    public void setTopDevice(MediaDevice mediaDevice) {
        this.mTopDevice = mediaDevice;
    }

    /* access modifiers changed from: package-private */
    public MediaDevice getTopDevice() {
        return getMediaDeviceById(this.mTopDevice.getId());
    }

    /* access modifiers changed from: package-private */
    public boolean addDeviceToPlayMedia(MediaDevice mediaDevice) {
        return this.mLocalMediaManager.addDeviceToPlayMedia(mediaDevice);
    }

    /* access modifiers changed from: package-private */
    public boolean removeDeviceFromPlayMedia(MediaDevice mediaDevice) {
        return this.mLocalMediaManager.removeDeviceFromPlayMedia(mediaDevice);
    }

    /* access modifiers changed from: package-private */
    public List<MediaDevice> getSelectableMediaDevice() {
        return this.mLocalMediaManager.getSelectableMediaDevice();
    }

    /* access modifiers changed from: package-private */
    public List<MediaDevice> getSelectedMediaDevice() {
        return this.mLocalMediaManager.getSelectedMediaDevice();
    }

    /* access modifiers changed from: package-private */
    public List<MediaDevice> getDeselectableMediaDevice() {
        return this.mLocalMediaManager.getDeselectableMediaDevice();
    }

    /* access modifiers changed from: package-private */
    public boolean isDeviceIncluded(Collection<MediaDevice> collection, MediaDevice mediaDevice) {
        for (MediaDevice mediaDevice2 : collection) {
            if (TextUtils.equals(mediaDevice2.getId(), mediaDevice.getId())) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void adjustSessionVolume(String str, int i) {
        this.mLocalMediaManager.adjustSessionVolume(str, i);
    }

    /* access modifiers changed from: package-private */
    public void adjustSessionVolume(int i) {
        this.mLocalMediaManager.adjustSessionVolume(i);
    }

    /* access modifiers changed from: package-private */
    public int getSessionVolumeMax() {
        return this.mLocalMediaManager.getSessionVolumeMax();
    }

    /* access modifiers changed from: package-private */
    public int getSessionVolume() {
        return this.mLocalMediaManager.getSessionVolume();
    }

    /* access modifiers changed from: package-private */
    public CharSequence getSessionName() {
        return this.mLocalMediaManager.getSessionName();
    }

    /* access modifiers changed from: package-private */
    public List<RoutingSessionInfo> getActiveRemoteMediaDevice() {
        ArrayList arrayList = new ArrayList();
        for (RoutingSessionInfo routingSessionInfo : this.mLocalMediaManager.getActiveMediaSession()) {
            if (!routingSessionInfo.isSystemSession()) {
                arrayList.add(routingSessionInfo);
            }
        }
        return arrayList;
    }

    public void adjustVolume(MediaDevice mediaDevice, int i) {
        ThreadUtils.postOnBackgroundThread(new Runnable(i) {
            /* class com.android.settings.media.$$Lambda$MediaDeviceUpdateWorker$rzN5nt74lJqnQLJYe8ZTW3eLXtE */
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDeviceUpdateWorker.lambda$adjustVolume$2(MediaDevice.this, this.f$1);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public String getPackageName() {
        return this.mPackageName;
    }

    /* access modifiers changed from: package-private */
    public boolean hasAdjustVolumeUserRestriction() {
        if (RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_adjust_volume", UserHandle.myUserId()) != null) {
            return true;
        }
        return ((UserManager) this.mContext.getSystemService("user")).hasBaseUserRestriction("no_adjust_volume", UserHandle.of(UserHandle.myUserId()));
    }

    private class DevicesChangedBroadcastReceiver extends BroadcastReceiver {
        private DevicesChangedBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals("android.media.STREAM_DEVICES_CHANGED_ACTION", intent.getAction()) && Utils.isAudioModeOngoingCall(MediaDeviceUpdateWorker.this.mContext)) {
                MediaDeviceUpdateWorker.this.notifySliceChange();
            }
        }
    }
}
