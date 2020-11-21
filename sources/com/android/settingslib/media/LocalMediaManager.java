package com.android.settingslib.media;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.media.RoutingSessionInfo;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.bluetooth.A2dpProfile;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.HearingAidProfile;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.media.MediaManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocalMediaManager implements BluetoothCallback {
    private static final Comparator<MediaDevice> COMPARATOR = Comparator.naturalOrder();
    @VisibleForTesting
    BluetoothAdapter mBluetoothAdapter;
    private final Collection<DeviceCallback> mCallbacks = new CopyOnWriteArrayList();
    private Context mContext;
    @VisibleForTesting
    MediaDevice mCurrentConnectedDevice;
    @VisibleForTesting
    DeviceAttributeChangeCallback mDeviceAttributeChangeCallback = new DeviceAttributeChangeCallback();
    @VisibleForTesting
    List<MediaDevice> mDisconnectedMediaDevices = new CopyOnWriteArrayList();
    private InfoMediaManager mInfoMediaManager;
    private LocalBluetoothManager mLocalBluetoothManager;
    @VisibleForTesting
    final MediaDeviceCallback mMediaDeviceCallback = new MediaDeviceCallback();
    @VisibleForTesting
    List<MediaDevice> mMediaDevices = new CopyOnWriteArrayList();
    private final Object mMediaDevicesLock = new Object();
    private MediaDevice mOnTransferBluetoothDevice;
    private String mPackageName;
    @VisibleForTesting
    MediaDevice mPhoneDevice;

    public interface DeviceCallback {
        default void onDeviceAttributesChanged() {
        }

        default void onDeviceListUpdate(List<MediaDevice> list) {
        }

        default void onRequestFailed(int i) {
        }

        default void onSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
        }
    }

    public void registerCallback(DeviceCallback deviceCallback) {
        this.mCallbacks.add(deviceCallback);
    }

    public void unregisterCallback(DeviceCallback deviceCallback) {
        this.mCallbacks.remove(deviceCallback);
    }

    public LocalMediaManager(Context context, String str, Notification notification) {
        this.mContext = context;
        this.mPackageName = str;
        this.mLocalBluetoothManager = LocalBluetoothManager.getInstance(context, null);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mLocalBluetoothManager == null) {
            Log.e("LocalMediaManager", "Bluetooth is not supported on this device");
        } else {
            this.mInfoMediaManager = new InfoMediaManager(context, str, notification, this.mLocalBluetoothManager);
        }
    }

    public boolean connectDevice(MediaDevice mediaDevice) {
        MediaDevice mediaDeviceById;
        synchronized (this.mMediaDevicesLock) {
            mediaDeviceById = getMediaDeviceById(this.mMediaDevices, mediaDevice.getId());
        }
        if (mediaDeviceById == null) {
            Log.w("LocalMediaManager", "connectDevice() connectDevice not in the list!");
            return false;
        }
        if (mediaDeviceById instanceof BluetoothMediaDevice) {
            CachedBluetoothDevice cachedDevice = ((BluetoothMediaDevice) mediaDeviceById).getCachedDevice();
            if (!cachedDevice.isConnected() && !cachedDevice.isBusy()) {
                this.mOnTransferBluetoothDevice = mediaDevice;
                mediaDeviceById.setState(1);
                cachedDevice.connect();
                return true;
            }
        }
        MediaDevice mediaDevice2 = this.mCurrentConnectedDevice;
        if (mediaDeviceById == mediaDevice2) {
            Log.d("LocalMediaManager", "connectDevice() this device all ready connected! : " + mediaDeviceById.getName());
            return false;
        }
        if (mediaDevice2 != null) {
            mediaDevice2.disconnect();
        }
        mediaDeviceById.setState(1);
        if (TextUtils.isEmpty(this.mPackageName)) {
            this.mInfoMediaManager.connectDeviceWithoutPackageName(mediaDeviceById);
        } else {
            mediaDeviceById.connect();
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void dispatchSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
        for (DeviceCallback deviceCallback : getCallbacks()) {
            deviceCallback.onSelectedDeviceStateChanged(mediaDevice, i);
        }
    }

    public void startScan() {
        synchronized (this.mMediaDevicesLock) {
            this.mMediaDevices.clear();
        }
        this.mInfoMediaManager.registerCallback(this.mMediaDeviceCallback);
        this.mInfoMediaManager.startScan();
    }

    /* access modifiers changed from: package-private */
    public void dispatchDeviceListUpdate() {
        ArrayList arrayList = new ArrayList(this.mMediaDevices);
        Collections.sort(arrayList, COMPARATOR);
        for (DeviceCallback deviceCallback : getCallbacks()) {
            deviceCallback.onDeviceListUpdate(arrayList);
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchDeviceAttributesChanged() {
        for (DeviceCallback deviceCallback : getCallbacks()) {
            deviceCallback.onDeviceAttributesChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchOnRequestFailed(int i) {
        for (DeviceCallback deviceCallback : getCallbacks()) {
            deviceCallback.onRequestFailed(i);
        }
    }

    public void stopScan() {
        this.mInfoMediaManager.unregisterCallback(this.mMediaDeviceCallback);
        this.mInfoMediaManager.stopScan();
        unRegisterDeviceAttributeChangeCallback();
    }

    public MediaDevice getMediaDeviceById(List<MediaDevice> list, String str) {
        for (MediaDevice mediaDevice : list) {
            if (TextUtils.equals(mediaDevice.getId(), str)) {
                return mediaDevice;
            }
        }
        Log.i("LocalMediaManager", "getMediaDeviceById() can't found device");
        return null;
    }

    public MediaDevice getCurrentConnectedDevice() {
        return this.mCurrentConnectedDevice;
    }

    public boolean addDeviceToPlayMedia(MediaDevice mediaDevice) {
        return this.mInfoMediaManager.addDeviceToPlayMedia(mediaDevice);
    }

    public boolean removeDeviceFromPlayMedia(MediaDevice mediaDevice) {
        return this.mInfoMediaManager.removeDeviceFromPlayMedia(mediaDevice);
    }

    public List<MediaDevice> getSelectableMediaDevice() {
        return this.mInfoMediaManager.getSelectableMediaDevice();
    }

    public List<MediaDevice> getDeselectableMediaDevice() {
        return this.mInfoMediaManager.getDeselectableMediaDevice();
    }

    public boolean releaseSession() {
        return this.mInfoMediaManager.releaseSession();
    }

    public List<MediaDevice> getSelectedMediaDevice() {
        return this.mInfoMediaManager.getSelectedMediaDevice();
    }

    public void adjustSessionVolume(String str, int i) {
        for (RoutingSessionInfo routingSessionInfo : getActiveMediaSession()) {
            if (TextUtils.equals(str, routingSessionInfo.getId())) {
                this.mInfoMediaManager.adjustSessionVolume(routingSessionInfo, i);
                return;
            }
        }
        Log.w("LocalMediaManager", "adjustSessionVolume: Unable to find session: " + str);
    }

    public void adjustSessionVolume(int i) {
        this.mInfoMediaManager.adjustSessionVolume(i);
    }

    public int getSessionVolumeMax() {
        return this.mInfoMediaManager.getSessionVolumeMax();
    }

    public int getSessionVolume() {
        return this.mInfoMediaManager.getSessionVolume();
    }

    public CharSequence getSessionName() {
        return this.mInfoMediaManager.getSessionName();
    }

    public List<RoutingSessionInfo> getActiveMediaSession() {
        return this.mInfoMediaManager.getActiveMediaSession();
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public MediaDevice updateCurrentConnectedDevice() {
        synchronized (this.mMediaDevicesLock) {
            MediaDevice mediaDevice = null;
            for (MediaDevice mediaDevice2 : this.mMediaDevices) {
                if (mediaDevice2 instanceof BluetoothMediaDevice) {
                    if (isActiveDevice(((BluetoothMediaDevice) mediaDevice2).getCachedDevice()) && mediaDevice2.isConnected()) {
                        return mediaDevice2;
                    }
                } else if (mediaDevice2 instanceof PhoneMediaDevice) {
                    mediaDevice = mediaDevice2;
                }
            }
            return mediaDevice;
        }
    }

    private boolean isActiveDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        HearingAidProfile hearingAidProfile;
        A2dpProfile a2dpProfile = this.mLocalBluetoothManager.getProfileManager().getA2dpProfile();
        boolean equals = a2dpProfile != null ? cachedBluetoothDevice.getDevice().equals(a2dpProfile.getActiveDevice()) : false;
        boolean contains = (equals || (hearingAidProfile = this.mLocalBluetoothManager.getProfileManager().getHearingAidProfile()) == null) ? false : hearingAidProfile.getActiveDevices().contains(cachedBluetoothDevice.getDevice());
        if (equals || contains) {
            return true;
        }
        return false;
    }

    private Collection<DeviceCallback> getCallbacks() {
        return new CopyOnWriteArrayList(this.mCallbacks);
    }

    class MediaDeviceCallback implements MediaManager.MediaDeviceCallback {
        MediaDeviceCallback() {
        }

        @Override // com.android.settingslib.media.MediaManager.MediaDeviceCallback
        public void onDeviceListAdded(List<MediaDevice> list) {
            synchronized (LocalMediaManager.this.mMediaDevicesLock) {
                LocalMediaManager.this.mMediaDevices.clear();
                LocalMediaManager.this.mMediaDevices.addAll(list);
                LocalMediaManager.this.mMediaDevices.addAll(buildDisconnectedBluetoothDevice());
            }
            MediaDevice currentConnectedDevice = LocalMediaManager.this.mInfoMediaManager.getCurrentConnectedDevice();
            LocalMediaManager localMediaManager = LocalMediaManager.this;
            if (currentConnectedDevice == null) {
                currentConnectedDevice = localMediaManager.updateCurrentConnectedDevice();
            }
            localMediaManager.mCurrentConnectedDevice = currentConnectedDevice;
            LocalMediaManager.this.dispatchDeviceListUpdate();
            if (LocalMediaManager.this.mOnTransferBluetoothDevice != null && LocalMediaManager.this.mOnTransferBluetoothDevice.isConnected()) {
                LocalMediaManager localMediaManager2 = LocalMediaManager.this;
                localMediaManager2.connectDevice(localMediaManager2.mOnTransferBluetoothDevice);
                LocalMediaManager.this.mOnTransferBluetoothDevice.setState(0);
                LocalMediaManager localMediaManager3 = LocalMediaManager.this;
                localMediaManager3.dispatchSelectedDeviceStateChanged(localMediaManager3.mOnTransferBluetoothDevice, 0);
                LocalMediaManager.this.mOnTransferBluetoothDevice = null;
            }
        }

        private List<MediaDevice> buildDisconnectedBluetoothDevice() {
            BluetoothAdapter bluetoothAdapter = LocalMediaManager.this.mBluetoothAdapter;
            if (bluetoothAdapter == null) {
                Log.w("LocalMediaManager", "buildDisconnectedBluetoothDevice() BluetoothAdapter is null");
                return new ArrayList();
            }
            List<BluetoothDevice> mostRecentlyConnectedDevices = bluetoothAdapter.getMostRecentlyConnectedDevices();
            CachedBluetoothDeviceManager cachedDeviceManager = LocalMediaManager.this.mLocalBluetoothManager.getCachedDeviceManager();
            ArrayList<CachedBluetoothDevice> arrayList = new ArrayList();
            int i = 0;
            for (BluetoothDevice bluetoothDevice : mostRecentlyConnectedDevices) {
                CachedBluetoothDevice findDevice = cachedDeviceManager.findDevice(bluetoothDevice);
                if (findDevice != null && findDevice.getBondState() == 12 && !findDevice.isConnected() && isA2dpOrHearingAidDevice(findDevice)) {
                    i++;
                    arrayList.add(findDevice);
                    if (i >= 5) {
                        break;
                    }
                }
            }
            LocalMediaManager.this.unRegisterDeviceAttributeChangeCallback();
            LocalMediaManager.this.mDisconnectedMediaDevices.clear();
            for (CachedBluetoothDevice cachedBluetoothDevice : arrayList) {
                BluetoothMediaDevice bluetoothMediaDevice = new BluetoothMediaDevice(LocalMediaManager.this.mContext, cachedBluetoothDevice, null, null, LocalMediaManager.this.mPackageName);
                if (!LocalMediaManager.this.mMediaDevices.contains(bluetoothMediaDevice)) {
                    cachedBluetoothDevice.registerCallback(LocalMediaManager.this.mDeviceAttributeChangeCallback);
                    LocalMediaManager.this.mDisconnectedMediaDevices.add(bluetoothMediaDevice);
                }
            }
            return new ArrayList(LocalMediaManager.this.mDisconnectedMediaDevices);
        }

        /* JADX WARNING: Removed duplicated region for block: B:3:0x000e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean isA2dpOrHearingAidDevice(com.android.settingslib.bluetooth.CachedBluetoothDevice r2) {
            /*
                r1 = this;
                java.util.List r1 = r2.getConnectableProfiles()
                java.util.Iterator r1 = r1.iterator()
            L_0x0008:
                boolean r2 = r1.hasNext()
                if (r2 == 0) goto L_0x001e
                java.lang.Object r2 = r1.next()
                com.android.settingslib.bluetooth.LocalBluetoothProfile r2 = (com.android.settingslib.bluetooth.LocalBluetoothProfile) r2
                boolean r0 = r2 instanceof com.android.settingslib.bluetooth.A2dpProfile
                if (r0 != 0) goto L_0x001c
                boolean r2 = r2 instanceof com.android.settingslib.bluetooth.HearingAidProfile
                if (r2 == 0) goto L_0x0008
            L_0x001c:
                r1 = 1
                return r1
            L_0x001e:
                r1 = 0
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.media.LocalMediaManager.MediaDeviceCallback.isA2dpOrHearingAidDevice(com.android.settingslib.bluetooth.CachedBluetoothDevice):boolean");
        }

        @Override // com.android.settingslib.media.MediaManager.MediaDeviceCallback
        public void onConnectedDeviceChanged(String str) {
            MediaDevice mediaDeviceById;
            synchronized (LocalMediaManager.this.mMediaDevicesLock) {
                mediaDeviceById = LocalMediaManager.this.getMediaDeviceById(LocalMediaManager.this.mMediaDevices, str);
            }
            if (mediaDeviceById == null) {
                mediaDeviceById = LocalMediaManager.this.updateCurrentConnectedDevice();
            }
            LocalMediaManager.this.mCurrentConnectedDevice = mediaDeviceById;
            if (mediaDeviceById != null) {
                mediaDeviceById.setState(0);
                LocalMediaManager localMediaManager = LocalMediaManager.this;
                localMediaManager.dispatchSelectedDeviceStateChanged(localMediaManager.mCurrentConnectedDevice, 0);
            }
        }

        @Override // com.android.settingslib.media.MediaManager.MediaDeviceCallback
        public void onDeviceAttributesChanged() {
            LocalMediaManager.this.dispatchDeviceAttributesChanged();
        }

        @Override // com.android.settingslib.media.MediaManager.MediaDeviceCallback
        public void onRequestFailed(int i) {
            synchronized (LocalMediaManager.this.mMediaDevicesLock) {
                for (MediaDevice mediaDevice : LocalMediaManager.this.mMediaDevices) {
                    if (mediaDevice.getState() == 1) {
                        mediaDevice.setState(3);
                    }
                }
            }
            LocalMediaManager.this.dispatchOnRequestFailed(i);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void unRegisterDeviceAttributeChangeCallback() {
        Iterator<MediaDevice> it = this.mDisconnectedMediaDevices.iterator();
        while (it.hasNext()) {
            ((BluetoothMediaDevice) it.next()).getCachedDevice().unregisterCallback(this.mDeviceAttributeChangeCallback);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public class DeviceAttributeChangeCallback implements CachedBluetoothDevice.Callback {
        DeviceAttributeChangeCallback() {
        }

        @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
        public void onDeviceAttributesChanged() {
            if (LocalMediaManager.this.mOnTransferBluetoothDevice != null && !((BluetoothMediaDevice) LocalMediaManager.this.mOnTransferBluetoothDevice).getCachedDevice().isBusy() && !LocalMediaManager.this.mOnTransferBluetoothDevice.isConnected()) {
                LocalMediaManager.this.mOnTransferBluetoothDevice.setState(3);
                LocalMediaManager.this.mOnTransferBluetoothDevice = null;
                LocalMediaManager.this.dispatchOnRequestFailed(0);
            }
            LocalMediaManager.this.dispatchDeviceAttributesChanged();
        }
    }
}
