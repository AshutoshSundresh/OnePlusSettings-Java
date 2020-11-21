package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import com.android.settingslib.bluetooth.BluetoothEventManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocalBluetoothProfileManager {
    private A2dpProfile mA2dpProfile;
    private A2dpSinkProfile mA2dpSinkProfile;
    private final Context mContext;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private DunServerProfile mDunProfile;
    private final BluetoothEventManager mEventManager;
    private HeadsetProfile mHeadsetProfile;
    private HearingAidProfile mHearingAidProfile;
    private HfpClientProfile mHfpClientProfile;
    private HidDeviceProfile mHidDeviceProfile;
    private HidProfile mHidProfile;
    private MapClientProfile mMapClientProfile;
    private MapProfile mMapProfile;
    private OppProfile mOppProfile;
    private PanProfile mPanProfile;
    private PbapClientProfile mPbapClientProfile;
    private PbapServerProfile mPbapProfile;
    private final Map<String, LocalBluetoothProfile> mProfileNameMap = new HashMap();
    private SapProfile mSapProfile;
    private final Collection<ServiceListener> mServiceListeners = new CopyOnWriteArrayList();

    public interface ServiceListener {
        void onServiceConnected();

        void onServiceDisconnected();
    }

    LocalBluetoothProfileManager(Context context, LocalBluetoothAdapter localBluetoothAdapter, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, BluetoothEventManager bluetoothEventManager) {
        this.mContext = context;
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mEventManager = bluetoothEventManager;
        localBluetoothAdapter.setProfileManager(this);
        Log.d("LocalBluetoothProfileManager", "LocalBluetoothProfileManager construction complete");
    }

    /* access modifiers changed from: package-private */
    public void updateLocalProfiles() {
        List supportedProfiles = BluetoothAdapter.getDefaultAdapter().getSupportedProfiles();
        if (CollectionUtils.isEmpty(supportedProfiles)) {
            Log.d("LocalBluetoothProfileManager", "supportedList is null");
            return;
        }
        if (this.mA2dpProfile == null && supportedProfiles.contains(2)) {
            Log.d("LocalBluetoothProfileManager", "Adding local A2DP profile");
            A2dpProfile a2dpProfile = new A2dpProfile(this.mContext, this.mDeviceManager, this);
            this.mA2dpProfile = a2dpProfile;
            addProfile(a2dpProfile, "A2DP", "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mA2dpSinkProfile == null && supportedProfiles.contains(11)) {
            Log.d("LocalBluetoothProfileManager", "Adding local A2DP SINK profile");
            A2dpSinkProfile a2dpSinkProfile = new A2dpSinkProfile(this.mContext, this.mDeviceManager, this);
            this.mA2dpSinkProfile = a2dpSinkProfile;
            addProfile(a2dpSinkProfile, "A2DPSink", "android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mHeadsetProfile == null && supportedProfiles.contains(1)) {
            Log.d("LocalBluetoothProfileManager", "Adding local HEADSET profile");
            HeadsetProfile headsetProfile = new HeadsetProfile(this.mContext, this.mDeviceManager, this);
            this.mHeadsetProfile = headsetProfile;
            addHeadsetProfile(headsetProfile, "HEADSET", "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED", "android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED", 10);
        }
        if (this.mHfpClientProfile == null && supportedProfiles.contains(16)) {
            Log.d("LocalBluetoothProfileManager", "Adding local HfpClient profile");
            HfpClientProfile hfpClientProfile = new HfpClientProfile(this.mContext, this.mDeviceManager, this);
            this.mHfpClientProfile = hfpClientProfile;
            addHeadsetProfile(hfpClientProfile, "HEADSET_CLIENT", "android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED", "android.bluetooth.headsetclient.profile.action.AUDIO_STATE_CHANGED", 0);
        }
        if (this.mMapClientProfile == null && supportedProfiles.contains(18)) {
            Log.d("LocalBluetoothProfileManager", "Adding local MAP CLIENT profile");
            MapClientProfile mapClientProfile = new MapClientProfile(this.mContext, this.mDeviceManager, this);
            this.mMapClientProfile = mapClientProfile;
            addProfile(mapClientProfile, "MAP Client", "android.bluetooth.mapmce.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mMapProfile == null && supportedProfiles.contains(9)) {
            Log.d("LocalBluetoothProfileManager", "Adding local MAP profile");
            MapProfile mapProfile = new MapProfile(this.mContext, this.mDeviceManager, this);
            this.mMapProfile = mapProfile;
            addProfile(mapProfile, "MAP", "android.bluetooth.map.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mOppProfile == null && supportedProfiles.contains(20)) {
            Log.d("LocalBluetoothProfileManager", "Adding local OPP profile");
            OppProfile oppProfile = new OppProfile();
            this.mOppProfile = oppProfile;
            this.mProfileNameMap.put("OPP", oppProfile);
        }
        if (this.mHearingAidProfile == null && supportedProfiles.contains(21)) {
            Log.d("LocalBluetoothProfileManager", "Adding local Hearing Aid profile");
            HearingAidProfile hearingAidProfile = new HearingAidProfile(this.mContext, this.mDeviceManager, this);
            this.mHearingAidProfile = hearingAidProfile;
            addProfile(hearingAidProfile, "HearingAid", "android.bluetooth.hearingaid.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mHidProfile == null && supportedProfiles.contains(4)) {
            Log.d("LocalBluetoothProfileManager", "Adding local HID_HOST profile");
            HidProfile hidProfile = new HidProfile(this.mContext, this.mDeviceManager, this);
            this.mHidProfile = hidProfile;
            addProfile(hidProfile, "HID", "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mHidDeviceProfile == null && supportedProfiles.contains(19)) {
            Log.d("LocalBluetoothProfileManager", "Adding local HID_DEVICE profile");
            HidDeviceProfile hidDeviceProfile = new HidDeviceProfile(this.mContext, this.mDeviceManager, this);
            this.mHidDeviceProfile = hidDeviceProfile;
            addProfile(hidDeviceProfile, "HID DEVICE", "android.bluetooth.hiddevice.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mPanProfile == null && supportedProfiles.contains(5)) {
            Log.d("LocalBluetoothProfileManager", "Adding local PAN profile");
            PanProfile panProfile = new PanProfile(this.mContext);
            this.mPanProfile = panProfile;
            addPanProfile(panProfile, "PAN", "android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mPbapProfile == null && supportedProfiles.contains(6)) {
            Log.d("LocalBluetoothProfileManager", "Adding local PBAP profile");
            PbapServerProfile pbapServerProfile = new PbapServerProfile(this.mContext);
            this.mPbapProfile = pbapServerProfile;
            addProfile(pbapServerProfile, PbapServerProfile.NAME, "android.bluetooth.pbap.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mPbapClientProfile == null && supportedProfiles.contains(17)) {
            Log.d("LocalBluetoothProfileManager", "Adding local PBAP Client profile");
            PbapClientProfile pbapClientProfile = new PbapClientProfile(this.mContext, this.mDeviceManager, this);
            this.mPbapClientProfile = pbapClientProfile;
            addProfile(pbapClientProfile, "PbapClient", "android.bluetooth.pbapclient.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mSapProfile == null && supportedProfiles.contains(10)) {
            Log.d("LocalBluetoothProfileManager", "Adding local SAP profile");
            SapProfile sapProfile = new SapProfile(this.mContext, this.mDeviceManager, this);
            this.mSapProfile = sapProfile;
            addProfile(sapProfile, "SAP", "android.bluetooth.sap.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mDunProfile == null && supportedProfiles.contains(22)) {
            Log.d("LocalBluetoothProfileManager", "Adding local DUN profile");
            DunServerProfile dunServerProfile = new DunServerProfile(this.mContext);
            this.mDunProfile = dunServerProfile;
            addProfile(dunServerProfile, "DUN Server", "codeaurora.bluetooth.dun.profile.action.CONNECTION_STATE_CHANGED");
        }
        this.mEventManager.registerProfileIntentReceiver();
    }

    private void addHeadsetProfile(LocalBluetoothProfile localBluetoothProfile, String str, String str2, String str3, int i) {
        HeadsetStateChangeHandler headsetStateChangeHandler = new HeadsetStateChangeHandler(this, localBluetoothProfile, str3, i);
        this.mEventManager.addProfileHandler(str2, headsetStateChangeHandler);
        this.mEventManager.addProfileHandler(str3, headsetStateChangeHandler);
        this.mProfileNameMap.put(str, localBluetoothProfile);
    }

    private void addProfile(LocalBluetoothProfile localBluetoothProfile, String str, String str2) {
        this.mEventManager.addProfileHandler(str2, new StateChangedHandler(localBluetoothProfile));
        this.mProfileNameMap.put(str, localBluetoothProfile);
    }

    private void addPanProfile(LocalBluetoothProfile localBluetoothProfile, String str, String str2) {
        this.mEventManager.addProfileHandler(str2, new PanStateChangedHandler(this, localBluetoothProfile));
        this.mProfileNameMap.put(str, localBluetoothProfile);
    }

    public LocalBluetoothProfile getProfileByName(String str) {
        return this.mProfileNameMap.get(str);
    }

    /* access modifiers changed from: package-private */
    public void setBluetoothStateOn() {
        updateLocalProfiles();
        this.mEventManager.readPairedDevices();
    }

    /* access modifiers changed from: private */
    public class StateChangedHandler implements BluetoothEventManager.Handler {
        final LocalBluetoothProfile mProfile;

        StateChangedHandler(LocalBluetoothProfile localBluetoothProfile) {
            this.mProfile = localBluetoothProfile;
        }

        @Override // com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            if (bluetoothDevice == null) {
                Log.w("LocalBluetoothProfileManager", "StateChangedHandler receives state-change for invalid device");
                return;
            }
            CachedBluetoothDevice findDevice = LocalBluetoothProfileManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice == null) {
                Log.w("LocalBluetoothProfileManager", "StateChangedHandler found new device: " + bluetoothDevice);
                findDevice = LocalBluetoothProfileManager.this.mDeviceManager.addDevice(bluetoothDevice);
            }
            onReceiveInternal(intent, findDevice);
        }

        /* access modifiers changed from: protected */
        public void onReceiveInternal(Intent intent, CachedBluetoothDevice cachedBluetoothDevice) {
            int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
            int intExtra2 = intent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 0);
            if (intExtra == 0 && intExtra2 == 1) {
                Log.i("LocalBluetoothProfileManager", "Failed to connect " + this.mProfile + " device");
            }
            if (LocalBluetoothProfileManager.this.getHearingAidProfile() != null && (this.mProfile instanceof HearingAidProfile) && intExtra == 2 && cachedBluetoothDevice.getHiSyncId() == 0) {
                long hiSyncId = LocalBluetoothProfileManager.this.getHearingAidProfile().getHiSyncId(cachedBluetoothDevice.getDevice());
                if (hiSyncId != 0) {
                    cachedBluetoothDevice.setHiSyncId(hiSyncId);
                }
            }
            cachedBluetoothDevice.onProfileStateChanged(this.mProfile, intExtra);
            if (cachedBluetoothDevice.getHiSyncId() == 0 || !LocalBluetoothProfileManager.this.mDeviceManager.onProfileConnectionStateChangedIfProcessed(cachedBluetoothDevice, intExtra)) {
                cachedBluetoothDevice.refresh();
                LocalBluetoothProfileManager.this.mEventManager.dispatchProfileConnectionStateChanged(cachedBluetoothDevice, intExtra, this.mProfile.getProfileId());
            }
        }
    }

    /* access modifiers changed from: private */
    public class HeadsetStateChangeHandler extends StateChangedHandler {
        private final String mAudioChangeAction;
        private final int mAudioDisconnectedState;

        HeadsetStateChangeHandler(LocalBluetoothProfileManager localBluetoothProfileManager, LocalBluetoothProfile localBluetoothProfile, String str, int i) {
            super(localBluetoothProfile);
            this.mAudioChangeAction = str;
            this.mAudioDisconnectedState = i;
        }

        @Override // com.android.settingslib.bluetooth.LocalBluetoothProfileManager.StateChangedHandler
        public void onReceiveInternal(Intent intent, CachedBluetoothDevice cachedBluetoothDevice) {
            if (this.mAudioChangeAction.equals(intent.getAction())) {
                if (intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) != this.mAudioDisconnectedState) {
                    cachedBluetoothDevice.onProfileStateChanged(this.mProfile, 2);
                }
                cachedBluetoothDevice.refresh();
                return;
            }
            super.onReceiveInternal(intent, cachedBluetoothDevice);
        }
    }

    /* access modifiers changed from: private */
    public class PanStateChangedHandler extends StateChangedHandler {
        PanStateChangedHandler(LocalBluetoothProfileManager localBluetoothProfileManager, LocalBluetoothProfile localBluetoothProfile) {
            super(localBluetoothProfile);
        }

        @Override // com.android.settingslib.bluetooth.LocalBluetoothProfileManager.StateChangedHandler, com.android.settingslib.bluetooth.BluetoothEventManager.Handler
        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            ((PanProfile) this.mProfile).setLocalRole(bluetoothDevice, intent.getIntExtra("android.bluetooth.pan.extra.LOCAL_ROLE", 0));
            super.onReceive(context, intent, bluetoothDevice);
        }
    }

    public void addServiceListener(ServiceListener serviceListener) {
        this.mServiceListeners.add(serviceListener);
    }

    public void removeServiceListener(ServiceListener serviceListener) {
        this.mServiceListeners.remove(serviceListener);
    }

    /* access modifiers changed from: package-private */
    public void callServiceConnectedListeners() {
        for (ServiceListener serviceListener : new ArrayList(this.mServiceListeners)) {
            serviceListener.onServiceConnected();
        }
    }

    /* access modifiers changed from: package-private */
    public void callServiceDisconnectedListeners() {
        for (ServiceListener serviceListener : new ArrayList(this.mServiceListeners)) {
            serviceListener.onServiceDisconnected();
        }
    }

    public A2dpProfile getA2dpProfile() {
        return this.mA2dpProfile;
    }

    public A2dpSinkProfile getA2dpSinkProfile() {
        A2dpSinkProfile a2dpSinkProfile = this.mA2dpSinkProfile;
        if (a2dpSinkProfile == null || !a2dpSinkProfile.isProfileReady()) {
            return null;
        }
        return this.mA2dpSinkProfile;
    }

    public HeadsetProfile getHeadsetProfile() {
        return this.mHeadsetProfile;
    }

    public PbapClientProfile getPbapClientProfile() {
        return this.mPbapClientProfile;
    }

    public PbapServerProfile getPbapProfile() {
        return this.mPbapProfile;
    }

    public MapProfile getMapProfile() {
        return this.mMapProfile;
    }

    public HearingAidProfile getHearingAidProfile() {
        return this.mHearingAidProfile;
    }

    /* access modifiers changed from: package-private */
    public HidProfile getHidProfile() {
        return this.mHidProfile;
    }

    /* access modifiers changed from: package-private */
    public HidDeviceProfile getHidDeviceProfile() {
        return this.mHidDeviceProfile;
    }

    /* access modifiers changed from: package-private */
    public synchronized void updateProfiles(ParcelUuid[] parcelUuidArr, ParcelUuid[] parcelUuidArr2, Collection<LocalBluetoothProfile> collection, Collection<LocalBluetoothProfile> collection2, boolean z, BluetoothDevice bluetoothDevice) {
        collection2.clear();
        collection2.addAll(collection);
        Log.d("LocalBluetoothProfileManager", "Current Profiles" + collection.toString());
        collection.clear();
        if (parcelUuidArr != null) {
            if (this.mHeadsetProfile != null && ((ArrayUtils.contains(parcelUuidArr2, BluetoothUuid.HSP_AG) && ArrayUtils.contains(parcelUuidArr, BluetoothUuid.HSP)) || ((ArrayUtils.contains(parcelUuidArr2, BluetoothUuid.HFP_AG) && ArrayUtils.contains(parcelUuidArr, BluetoothUuid.HFP)) || this.mHeadsetProfile.getConnectionStatus(bluetoothDevice) == 2))) {
                collection.add(this.mHeadsetProfile);
                collection2.remove(this.mHeadsetProfile);
            }
            if (this.mHfpClientProfile != null && ArrayUtils.contains(parcelUuidArr, BluetoothUuid.HFP_AG) && ArrayUtils.contains(parcelUuidArr2, BluetoothUuid.HFP)) {
                collection.add(this.mHfpClientProfile);
                collection2.remove(this.mHfpClientProfile);
            }
            if (this.mA2dpProfile != null && (BluetoothUuid.containsAnyUuid(parcelUuidArr, A2dpProfile.SINK_UUIDS) || this.mA2dpProfile.getConnectionStatus(bluetoothDevice) == 2)) {
                collection.add(this.mA2dpProfile);
                collection2.remove(this.mA2dpProfile);
            }
            if (BluetoothUuid.containsAnyUuid(parcelUuidArr, A2dpSinkProfile.SRC_UUIDS) && this.mA2dpSinkProfile != null) {
                collection.add(this.mA2dpSinkProfile);
                collection2.remove(this.mA2dpSinkProfile);
            }
            if (ArrayUtils.contains(parcelUuidArr, BluetoothUuid.OBEX_OBJECT_PUSH) && this.mOppProfile != null) {
                collection.add(this.mOppProfile);
                collection2.remove(this.mOppProfile);
            }
            if ((ArrayUtils.contains(parcelUuidArr, BluetoothUuid.HID) || ArrayUtils.contains(parcelUuidArr, BluetoothUuid.HOGP)) && this.mHidProfile != null) {
                collection.add(this.mHidProfile);
                collection2.remove(this.mHidProfile);
            }
            if (!(this.mHidDeviceProfile == null || this.mHidDeviceProfile.getConnectionStatus(bluetoothDevice) == 0)) {
                collection.add(this.mHidDeviceProfile);
                collection2.remove(this.mHidDeviceProfile);
            }
            if (z) {
                Log.d("LocalBluetoothProfileManager", "Valid PAN-NAP connection exists.");
            }
            if ((ArrayUtils.contains(parcelUuidArr, BluetoothUuid.NAP) && this.mPanProfile != null) || z) {
                collection.add(this.mPanProfile);
                collection2.remove(this.mPanProfile);
            }
            if (this.mMapProfile != null && this.mMapProfile.getConnectionStatus(bluetoothDevice) == 2) {
                collection.add(this.mMapProfile);
                collection2.remove(this.mMapProfile);
                this.mMapProfile.setEnabled(bluetoothDevice, true);
            }
            if (this.mPbapProfile != null && this.mPbapProfile.getConnectionStatus(bluetoothDevice) == 2) {
                collection.add(this.mPbapProfile);
                collection2.remove(this.mPbapProfile);
                this.mPbapProfile.setEnabled(bluetoothDevice, true);
            }
            if (this.mMapClientProfile != null) {
                collection.add(this.mMapClientProfile);
                collection2.remove(this.mMapClientProfile);
            }
            if (this.mPbapClientProfile != null && ArrayUtils.contains(parcelUuidArr2, BluetoothUuid.PBAP_PCE) && BluetoothUuid.containsAnyUuid(parcelUuidArr, PbapClientProfile.SRC_UUIDS)) {
                collection.add(this.mPbapClientProfile);
                collection2.remove(this.mPbapClientProfile);
            }
            if (ArrayUtils.contains(parcelUuidArr, BluetoothUuid.HEARING_AID) && this.mHearingAidProfile != null) {
                collection.add(this.mHearingAidProfile);
                collection2.remove(this.mHearingAidProfile);
            }
            if (this.mSapProfile != null && ArrayUtils.contains(parcelUuidArr, BluetoothUuid.SAP)) {
                collection.add(this.mSapProfile);
                collection2.remove(this.mSapProfile);
            }
            Log.d("LocalBluetoothProfileManager", "New Profiles" + collection.toString());
        }
    }
}
