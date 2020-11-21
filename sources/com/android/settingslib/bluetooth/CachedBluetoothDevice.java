package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CachedBluetoothDevice implements Comparable<CachedBluetoothDevice> {
    private final Collection<Callback> mCallbacks = new CopyOnWriteArrayList();
    private long mConnectAttempted;
    private final Context mContext;
    BluetoothDevice mDevice;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.android.settingslib.bluetooth.CachedBluetoothDevice.AnonymousClass1 */

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                CachedBluetoothDevice.this.mIsHeadsetProfileConnectedFail = true;
            } else if (i == 2) {
                CachedBluetoothDevice.this.mIsA2dpProfileConnectedFail = true;
            } else if (i != 21) {
                Log.w("CachedBluetoothDevice", "handleMessage(): unknown message : " + message.what);
            } else {
                CachedBluetoothDevice.this.mIsHearingAidProfileConnectedFail = true;
            }
            Log.w("CachedBluetoothDevice", "Connect to profile : " + message.what + " timeout, show error message !");
            CachedBluetoothDevice.this.refresh();
        }
    };
    private long mHiSyncId;
    private boolean mIsA2dpProfileConnectedFail = false;
    private boolean mIsActiveDeviceA2dp = false;
    private boolean mIsActiveDeviceHeadset = false;
    private boolean mIsActiveDeviceHearingAid = false;
    private boolean mIsHeadsetProfileConnectedFail = false;
    private boolean mIsHearingAidProfileConnectedFail = false;
    boolean mJustDiscovered;
    private final BluetoothAdapter mLocalAdapter;
    private boolean mLocalNapRoleConnected;
    private final Object mProfileLock = new Object();
    private final LocalBluetoothProfileManager mProfileManager;
    private final Collection<LocalBluetoothProfile> mProfiles = new CopyOnWriteArrayList();
    private final Collection<LocalBluetoothProfile> mRemovedProfiles = new CopyOnWriteArrayList();
    short mRssi;
    private CachedBluetoothDevice mSubDevice;
    public int mTwspBatteryLevel;
    public int mTwspBatteryState;

    public interface Callback {
        void onDeviceAttributesChanged();
    }

    private boolean isTwsBatteryAvailable(int i, int i2) {
        return i >= 0 && i2 >= 0;
    }

    CachedBluetoothDevice(Context context, LocalBluetoothProfileManager localBluetoothProfileManager, BluetoothDevice bluetoothDevice) {
        this.mContext = context;
        this.mLocalAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mProfileManager = localBluetoothProfileManager;
        this.mDevice = bluetoothDevice;
        fillData();
        this.mHiSyncId = 0;
        this.mTwspBatteryState = -1;
        this.mTwspBatteryLevel = -1;
    }

    private BluetoothDevice getTwsPeerDevice() {
        if (this.mDevice.isTwsPlusDevice()) {
            return BluetoothAdapter.getDefaultAdapter().getRemoteDevice(this.mDevice.getTwsPlusPeerAddress());
        }
        return null;
    }

    private String describe(LocalBluetoothProfile localBluetoothProfile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Address:");
        sb.append(this.mDevice);
        if (localBluetoothProfile != null) {
            sb.append(" Profile:");
            sb.append(localBluetoothProfile);
        }
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public void onProfileStateChanged(LocalBluetoothProfile localBluetoothProfile, int i) {
        Log.d("CachedBluetoothDevice", "onProfileStateChanged: profile " + localBluetoothProfile + ", device=" + this.mDevice + ", newProfileState " + i);
        if (this.mLocalAdapter.getState() == 13) {
            Log.d("CachedBluetoothDevice", " BT Turninig Off...Profile conn state change ignored...");
            return;
        }
        synchronized (this.mProfileLock) {
            if ((localBluetoothProfile instanceof A2dpProfile) || (localBluetoothProfile instanceof HeadsetProfile) || (localBluetoothProfile instanceof HearingAidProfile)) {
                setProfileConnectedStatus(localBluetoothProfile.getProfileId(), false);
                if (i != 0) {
                    if (i == 1) {
                        this.mHandler.sendEmptyMessageDelayed(localBluetoothProfile.getProfileId(), 60000);
                    } else if (i == 2) {
                        this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                    } else if (i != 3) {
                        Log.w("CachedBluetoothDevice", "onProfileStateChanged(): unknown profile state : " + i);
                    } else if (this.mHandler.hasMessages(localBluetoothProfile.getProfileId())) {
                        this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                    }
                } else if (this.mHandler.hasMessages(localBluetoothProfile.getProfileId())) {
                    this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                    setProfileConnectedStatus(localBluetoothProfile.getProfileId(), true);
                }
            }
            if (i == 2) {
                if (localBluetoothProfile instanceof MapProfile) {
                    localBluetoothProfile.setEnabled(this.mDevice, true);
                }
                if (!this.mProfiles.contains(localBluetoothProfile)) {
                    this.mRemovedProfiles.remove(localBluetoothProfile);
                    this.mProfiles.add(localBluetoothProfile);
                    if ((localBluetoothProfile instanceof PanProfile) && ((PanProfile) localBluetoothProfile).isLocalRoleNap(this.mDevice)) {
                        this.mLocalNapRoleConnected = true;
                    }
                }
            } else if ((localBluetoothProfile instanceof MapProfile) && i == 0) {
                localBluetoothProfile.setEnabled(this.mDevice, false);
            } else if (this.mLocalNapRoleConnected && (localBluetoothProfile instanceof PanProfile) && ((PanProfile) localBluetoothProfile).isLocalRoleNap(this.mDevice) && i == 0) {
                Log.d("CachedBluetoothDevice", "Removing PanProfile from device after NAP disconnect");
                this.mProfiles.remove(localBluetoothProfile);
                this.mRemovedProfiles.add(localBluetoothProfile);
                this.mLocalNapRoleConnected = false;
            } else if ((localBluetoothProfile instanceof HeadsetProfile) && i == 0) {
                this.mTwspBatteryState = -1;
                this.mTwspBatteryLevel = -1;
            }
        }
        fetchActiveDevices();
    }

    /* access modifiers changed from: package-private */
    public void setProfileConnectedStatus(int i, boolean z) {
        if (i == 1) {
            this.mIsHeadsetProfileConnectedFail = z;
        } else if (i == 2) {
            this.mIsA2dpProfileConnectedFail = z;
        } else if (i != 21) {
            Log.w("CachedBluetoothDevice", "setProfileConnectedStatus(): unknown profile id : " + i);
        } else {
            this.mIsHearingAidProfileConnectedFail = z;
        }
    }

    public void disconnect() {
        synchronized (this.mProfileLock) {
            this.mLocalAdapter.disconnectAllEnabledProfiles(this.mDevice);
        }
        PbapServerProfile pbapProfile = this.mProfileManager.getPbapProfile();
        if (pbapProfile != null && isConnectedProfile(pbapProfile)) {
            pbapProfile.setEnabled(this.mDevice, false);
        }
    }

    public void connect() {
        if (ensurePaired()) {
            this.mConnectAttempted = SystemClock.elapsedRealtime();
            Log.d("CachedBluetoothDevice", "connect: mConnectAttempted = " + this.mConnectAttempted);
            connectAllEnabledProfiles();
        }
    }

    public long getHiSyncId() {
        return this.mHiSyncId;
    }

    public void setHiSyncId(long j) {
        Log.d("CachedBluetoothDevice", "setHiSyncId: mDevice " + this.mDevice + ", id " + j);
        this.mHiSyncId = j;
    }

    public boolean isHearingAidDevice() {
        return this.mHiSyncId != 0;
    }

    private void connectAllEnabledProfiles() {
        synchronized (this.mProfileLock) {
            if (this.mProfiles.isEmpty()) {
                Log.d("CachedBluetoothDevice", "No profiles. Maybe we will connect later for device " + this.mDevice);
                return;
            }
            if (this.mDevice.isBondingInitiatedLocally()) {
                Log.w("CachedBluetoothDevice", "reset BondingInitiatedLocally flag");
                this.mDevice.setBondingInitiatedLocally(false);
            }
            this.mLocalAdapter.connectAllEnabledProfiles(this.mDevice);
        }
    }

    private boolean ensurePaired() {
        if (getBondState() != 10) {
            return true;
        }
        startPairing();
        return false;
    }

    public boolean startPairing() {
        if (this.mLocalAdapter.isDiscovering()) {
            this.mLocalAdapter.cancelDiscovery();
        }
        return this.mDevice.createBond();
    }

    public void unpair() {
        BluetoothDevice twsPeerDevice;
        int bondState = getBondState();
        if (bondState == 11) {
            this.mDevice.cancelBondProcess();
        }
        if (bondState != 10) {
            BluetoothDevice bluetoothDevice = this.mDevice;
            if (bluetoothDevice.isTwsPlusDevice() && (twsPeerDevice = getTwsPeerDevice()) != null && twsPeerDevice.removeBond()) {
                Log.d("CachedBluetoothDevice", "Command sent successfully:REMOVE_BOND " + twsPeerDevice.getName());
            }
            if (bluetoothDevice != null && bluetoothDevice.removeBond()) {
                Log.d("CachedBluetoothDevice", "Command sent successfully:REMOVE_BOND " + describe(null));
            }
        }
    }

    public int getProfileConnectionState(LocalBluetoothProfile localBluetoothProfile) {
        if (localBluetoothProfile != null) {
            return localBluetoothProfile.getConnectionStatus(this.mDevice);
        }
        return 0;
    }

    private void fillData() {
        updateProfiles();
        fetchActiveDevices();
        migratePhonebookPermissionChoice();
        migrateMessagePermissionChoice();
        dispatchAttributesChanged();
    }

    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public String getAddress() {
        return this.mDevice.getAddress();
    }

    public String getName() {
        String alias = this.mDevice.getAlias();
        return TextUtils.isEmpty(alias) ? getAddress() : alias;
    }

    public void setName(String str) {
        if (str != null && !TextUtils.equals(str, getName())) {
            this.mDevice.setAlias(str);
            dispatchAttributesChanged();
        }
    }

    public boolean setActive() {
        boolean z;
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile == null || !isConnectedProfile(a2dpProfile) || !a2dpProfile.setActiveDevice(getDevice())) {
            z = false;
        } else {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: A2DP active device=" + this);
            z = true;
        }
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile != null && isConnectedProfile(headsetProfile) && headsetProfile.setActiveDevice(getDevice())) {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: Headset active device=" + this);
            z = true;
        }
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile == null || !isConnectedProfile(hearingAidProfile) || !hearingAidProfile.setActiveDevice(getDevice())) {
            return z;
        }
        Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: Hearing Aid active device=" + this);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void refreshName() {
        Log.d("CachedBluetoothDevice", "Device name: " + getName());
        dispatchAttributesChanged();
    }

    public boolean hasHumanReadableName() {
        return !TextUtils.isEmpty(this.mDevice.getAlias());
    }

    public int getBatteryLevel() {
        return this.mDevice.getBatteryLevel();
    }

    /* access modifiers changed from: package-private */
    public void refresh() {
        dispatchAttributesChanged();
    }

    public void setJustDiscovered(boolean z) {
        if (this.mJustDiscovered != z) {
            this.mJustDiscovered = z;
            dispatchAttributesChanged();
        }
    }

    public int getBondState() {
        Log.d("CachedBluetoothDevice", "device name : " + this.mDevice.getName() + " bond state : " + this.mDevice.getBondState());
        return this.mDevice.getBondState();
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:23:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActiveDeviceChanged(boolean r4, int r5) {
        /*
            r3 = this;
            r0 = 1
            r1 = 0
            if (r5 == r0) goto L_0x003c
            r2 = 2
            if (r5 == r2) goto L_0x0033
            r2 = 21
            if (r5 == r2) goto L_0x002a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "onActiveDeviceChanged: unknown profile "
            r0.append(r2)
            r0.append(r5)
            java.lang.String r5 = " isActive "
            r0.append(r5)
            r0.append(r4)
            java.lang.String r4 = r0.toString()
            java.lang.String r5 = "CachedBluetoothDevice"
            android.util.Log.w(r5, r4)
            goto L_0x0045
        L_0x002a:
            boolean r5 = r3.mIsActiveDeviceHearingAid
            if (r5 == r4) goto L_0x002f
            goto L_0x0030
        L_0x002f:
            r0 = r1
        L_0x0030:
            r3.mIsActiveDeviceHearingAid = r4
            goto L_0x0044
        L_0x0033:
            boolean r5 = r3.mIsActiveDeviceA2dp
            if (r5 == r4) goto L_0x0038
            goto L_0x0039
        L_0x0038:
            r0 = r1
        L_0x0039:
            r3.mIsActiveDeviceA2dp = r4
            goto L_0x0044
        L_0x003c:
            boolean r5 = r3.mIsActiveDeviceHeadset
            if (r5 == r4) goto L_0x0041
            goto L_0x0042
        L_0x0041:
            r0 = r1
        L_0x0042:
            r3.mIsActiveDeviceHeadset = r4
        L_0x0044:
            r1 = r0
        L_0x0045:
            if (r1 == 0) goto L_0x004a
            r3.dispatchAttributesChanged()
        L_0x004a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.bluetooth.CachedBluetoothDevice.onActiveDeviceChanged(boolean, int):void");
    }

    /* access modifiers changed from: package-private */
    public void onAudioModeChanged() {
        dispatchAttributesChanged();
    }

    public boolean isActiveDevice(int i) {
        if (i == 1) {
            return this.mIsActiveDeviceHeadset;
        }
        if (i == 2) {
            return this.mIsActiveDeviceA2dp;
        }
        if (i == 21) {
            return this.mIsActiveDeviceHearingAid;
        }
        Log.w("CachedBluetoothDevice", "getActiveDevice: unknown profile " + i);
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setRssi(short s) {
        if (this.mRssi != s) {
            this.mRssi = s;
            dispatchAttributesChanged();
        }
    }

    public boolean isConnected() {
        synchronized (this.mProfileLock) {
            for (LocalBluetoothProfile localBluetoothProfile : this.mProfiles) {
                if (getProfileConnectionState(localBluetoothProfile) == 2) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isConnectedProfile(LocalBluetoothProfile localBluetoothProfile) {
        return getProfileConnectionState(localBluetoothProfile) == 2;
    }

    public boolean isBusy() {
        int profileConnectionState;
        synchronized (this.mProfileLock) {
            Iterator<LocalBluetoothProfile> it = this.mProfiles.iterator();
            do {
                boolean z = true;
                if (it.hasNext()) {
                    profileConnectionState = getProfileConnectionState(it.next());
                    if (profileConnectionState == 1) {
                        break;
                    }
                } else {
                    if (getBondState() != 11) {
                        z = false;
                    }
                    return z;
                }
            } while (profileConnectionState != 3);
            return true;
        }
    }

    private boolean updateProfiles() {
        ParcelUuid[] uuids;
        ParcelUuid[] uuids2 = this.mDevice.getUuids();
        if (uuids2 == null || (uuids = this.mLocalAdapter.getUuids()) == null) {
            return false;
        }
        processPhonebookAccess();
        synchronized (this.mProfileLock) {
            this.mProfileManager.updateProfiles(uuids2, uuids, this.mProfiles, this.mRemovedProfiles, this.mLocalNapRoleConnected, this.mDevice);
        }
        Log.e("CachedBluetoothDevice", "updating profiles for " + this.mDevice.getAlias() + ", " + this.mDevice);
        BluetoothClass bluetoothClass = this.mDevice.getBluetoothClass();
        if (bluetoothClass != null) {
            Log.v("CachedBluetoothDevice", "Class: " + bluetoothClass.toString());
        }
        Log.v("CachedBluetoothDevice", "UUID:");
        for (ParcelUuid parcelUuid : uuids2) {
            Log.v("CachedBluetoothDevice", "  " + parcelUuid);
        }
        return true;
    }

    private void fetchActiveDevices() {
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile != null) {
            this.mIsActiveDeviceA2dp = this.mDevice.equals(a2dpProfile.getActiveDevice());
        }
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile != null) {
            this.mIsActiveDeviceHeadset = this.mDevice.equals(headsetProfile.getActiveDevice());
        }
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile != null) {
            this.mIsActiveDeviceHearingAid = hearingAidProfile.getActiveDevices().contains(this.mDevice);
        }
    }

    /* access modifiers changed from: package-private */
    public void onUuidChanged() {
        long j;
        updateProfiles();
        ParcelUuid[] uuids = this.mDevice.getUuids();
        if (ArrayUtils.contains(uuids, BluetoothUuid.HOGP)) {
            j = 30000;
        } else {
            j = ArrayUtils.contains(uuids, BluetoothUuid.HEARING_AID) ? 15000 : 5000;
        }
        Log.d("CachedBluetoothDevice", "onUuidChanged: Time since last connect=" + (SystemClock.elapsedRealtime() - this.mConnectAttempted));
        if (this.mConnectAttempted + j > SystemClock.elapsedRealtime()) {
            Log.d("CachedBluetoothDevice", "onUuidChanged: triggering connectAllEnabledProfiles");
            connectAllEnabledProfiles();
        }
        dispatchAttributesChanged();
    }

    /* access modifiers changed from: package-private */
    public void onBondingStateChanged(int i) {
        if (i == 10) {
            synchronized (this.mProfileLock) {
                this.mProfiles.clear();
            }
            this.mDevice.setPhonebookAccessPermission(0);
            this.mDevice.setMessageAccessPermission(0);
            this.mDevice.setSimAccessPermission(0);
        }
        refresh();
        if (i == 12) {
            boolean isBondingInitiatedLocally = this.mDevice.isBondingInitiatedLocally();
            Log.w("CachedBluetoothDevice", "mIsBondingInitiatedLocally" + isBondingInitiatedLocally);
            if (isBondingInitiatedLocally) {
                connect();
            }
        }
    }

    public BluetoothClass getBtClass() {
        return this.mDevice.getBluetoothClass();
    }

    public List<LocalBluetoothProfile> getProfiles() {
        return new ArrayList(this.mProfiles);
    }

    public List<LocalBluetoothProfile> getConnectableProfiles() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mProfileLock) {
            for (LocalBluetoothProfile localBluetoothProfile : this.mProfiles) {
                if (localBluetoothProfile.accessProfileEnabled()) {
                    arrayList.add(localBluetoothProfile);
                }
            }
        }
        return arrayList;
    }

    public List<LocalBluetoothProfile> getRemovedProfiles() {
        return new ArrayList(this.mRemovedProfiles);
    }

    public void registerCallback(Callback callback) {
        this.mCallbacks.add(callback);
    }

    public void unregisterCallback(Callback callback) {
        this.mCallbacks.remove(callback);
    }

    /* access modifiers changed from: package-private */
    public void dispatchAttributesChanged() {
        for (Callback callback : this.mCallbacks) {
            callback.onDeviceAttributesChanged();
        }
    }

    public String toString() {
        return this.mDevice.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CachedBluetoothDevice)) {
            return false;
        }
        return this.mDevice.equals(((CachedBluetoothDevice) obj).mDevice);
    }

    public int hashCode() {
        return this.mDevice.getAddress().hashCode();
    }

    public int compareTo(CachedBluetoothDevice cachedBluetoothDevice) {
        int i = (cachedBluetoothDevice.isConnected() ? 1 : 0) - (isConnected() ? 1 : 0);
        if (i != 0) {
            return i;
        }
        int i2 = 1;
        int i3 = cachedBluetoothDevice.getBondState() == 12 ? 1 : 0;
        if (getBondState() != 12) {
            i2 = 0;
        }
        int i4 = i3 - i2;
        if (i4 != 0) {
            return i4;
        }
        int i5 = (cachedBluetoothDevice.mJustDiscovered ? 1 : 0) - (this.mJustDiscovered ? 1 : 0);
        if (i5 != 0) {
            return i5;
        }
        int i6 = cachedBluetoothDevice.mRssi - this.mRssi;
        if (i6 != 0) {
            return i6;
        }
        return getName().compareTo(cachedBluetoothDevice.getName());
    }

    private void migratePhonebookPermissionChoice() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("bluetooth_phonebook_permission", 0);
        if (sharedPreferences.contains(this.mDevice.getAddress())) {
            if (this.mDevice.getPhonebookAccessPermission() == 0) {
                int i = sharedPreferences.getInt(this.mDevice.getAddress(), 0);
                if (i == 1) {
                    this.mDevice.setPhonebookAccessPermission(1);
                } else if (i == 2) {
                    this.mDevice.setPhonebookAccessPermission(2);
                }
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.remove(this.mDevice.getAddress());
            edit.commit();
        }
    }

    private void migrateMessagePermissionChoice() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("bluetooth_message_permission", 0);
        if (sharedPreferences.contains(this.mDevice.getAddress())) {
            if (this.mDevice.getMessageAccessPermission() == 0) {
                int i = sharedPreferences.getInt(this.mDevice.getAddress(), 0);
                if (i == 1) {
                    this.mDevice.setMessageAccessPermission(1);
                } else if (i == 2) {
                    this.mDevice.setMessageAccessPermission(2);
                }
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.remove(this.mDevice.getAddress());
            edit.commit();
        }
    }

    private void processPhonebookAccess() {
        if (this.mDevice.getBondState() == 12 && BluetoothUuid.containsAnyUuid(this.mDevice.getUuids(), PbapServerProfile.PBAB_CLIENT_UUIDS) && this.mDevice.getPhonebookAccessPermission() == 0) {
            if (this.mDevice.getBluetoothClass() != null && (this.mDevice.getBluetoothClass().getDeviceClass() == 1032 || this.mDevice.getBluetoothClass().getDeviceClass() == 1028)) {
                EventLog.writeEvent(1397638484, "138529441", -1, "");
            }
            this.mDevice.setPhonebookAccessPermission(2);
        }
    }

    public String getConnectionSummary() {
        return getConnectionSummary(false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0072, code lost:
        r8 = -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x007a, code lost:
        if (r13.mDevice.isTwsPlusDevice() == false) goto L_0x00b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x007c, code lost:
        r0 = r13.mTwspBatteryState;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x007e, code lost:
        if (r0 == -1) goto L_0x00b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0082, code lost:
        if (r13.mTwspBatteryLevel == -1) goto L_0x00b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0084, code lost:
        if (r0 != 1) goto L_0x0089;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0086, code lost:
        r0 = "Charging, ";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0089, code lost:
        r0 = "Discharging, ";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x008b, code lost:
        r0 = "TWSP: ".concat(r0).concat(com.android.settingslib.Utils.formatPercentage(r13.mTwspBatteryLevel));
        android.util.Log.i("CachedBluetoothDevice", "UI string" + r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00b2, code lost:
        r0 = getBatteryLevel();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00b6, code lost:
        if (r0 <= -1) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00b8, code lost:
        r0 = com.android.settingslib.Utils.formatPercentage(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00bd, code lost:
        r0 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00be, code lost:
        r10 = com.android.settingslib.R$string.bluetooth_pairing;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00c2, code lost:
        if (r4 == false) goto L_0x011b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00cb, code lost:
        if (com.android.settingslib.bluetooth.BluetoothUtils.getBooleanMetaData(r13.mDevice, 6) == false) goto L_0x00dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00cd, code lost:
        r8 = com.android.settingslib.bluetooth.BluetoothUtils.getIntMetaData(r13.mDevice, 10);
        r4 = com.android.settingslib.bluetooth.BluetoothUtils.getIntMetaData(r13.mDevice, 11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00dc, code lost:
        r4 = -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00e1, code lost:
        if (isTwsBatteryAvailable(r8, r4) == false) goto L_0x00e6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00e3, code lost:
        r10 = com.android.settingslib.R$string.bluetooth_battery_level_untethered;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00e6, code lost:
        if (r0 == null) goto L_0x00ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00e8, code lost:
        r10 = com.android.settingslib.R$string.bluetooth_battery_level;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00ea, code lost:
        if (r5 != false) goto L_0x00f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00ec, code lost:
        if (r6 != false) goto L_0x00f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x00ee, code lost:
        if (r7 == false) goto L_0x011c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x00f0, code lost:
        r5 = com.android.settingslib.Utils.isAudioModeOngoingCall(r13.mContext);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x00f8, code lost:
        if (r13.mIsActiveDeviceHearingAid != false) goto L_0x0106;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x00fc, code lost:
        if (r13.mIsActiveDeviceHeadset == false) goto L_0x0100;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x00fe, code lost:
        if (r5 != false) goto L_0x0106;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0102, code lost:
        if (r13.mIsActiveDeviceA2dp == false) goto L_0x011c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0104, code lost:
        if (r5 != false) goto L_0x011c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x010a, code lost:
        if (isTwsBatteryAvailable(r8, r4) == false) goto L_0x0111;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x010c, code lost:
        if (r14 != false) goto L_0x0111;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x010e, code lost:
        r10 = com.android.settingslib.R$string.bluetooth_active_battery_level_untethered;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0111, code lost:
        if (r0 == null) goto L_0x0118;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0113, code lost:
        if (r14 != false) goto L_0x0118;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0115, code lost:
        r10 = com.android.settingslib.R$string.bluetooth_active_battery_level;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0118, code lost:
        r10 = com.android.settingslib.R$string.bluetooth_active_no_battery_level;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x011b, code lost:
        r4 = -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x011e, code lost:
        if (r10 != com.android.settingslib.R$string.bluetooth_pairing) goto L_0x0128;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0124, code lost:
        if (getBondState() != 11) goto L_0x0127;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x0127, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x012c, code lost:
        if (isTwsBatteryAvailable(r8, r4) == false) goto L_0x0143;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x0142, code lost:
        return r13.mContext.getString(r10, com.android.settingslib.Utils.formatPercentage(r8), com.android.settingslib.Utils.formatPercentage(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x014d, code lost:
        return r13.mContext.getString(r10, r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getConnectionSummary(boolean r14) {
        /*
        // Method dump skipped, instructions count: 337
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.bluetooth.CachedBluetoothDevice.getConnectionSummary(boolean):java.lang.String");
    }

    private boolean isProfileConnectedFail() {
        return this.mIsA2dpProfileConnectedFail || this.mIsHearingAidProfileConnectedFail || this.mIsHeadsetProfileConnectedFail;
    }

    public boolean isConnectedA2dpDevice() {
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        A2dpSinkProfile a2dpSinkProfile = this.mProfileManager.getA2dpSinkProfile();
        Log.i("CachedBluetoothDevice", "a2dpProfile :" + a2dpProfile + " a2dpSinkProfile :" + a2dpSinkProfile);
        if (a2dpProfile != null) {
            return a2dpProfile.getConnectionStatus(this.mDevice) == 2;
        }
        if (a2dpSinkProfile != null) {
            return a2dpSinkProfile.getConnectionStatus(this.mDevice) == 2;
        }
        return false;
    }

    public boolean isConnectedHfpDevice() {
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        return headsetProfile != null && headsetProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedHearingAidDevice() {
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        return hearingAidProfile != null && hearingAidProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public CachedBluetoothDevice getSubDevice() {
        return this.mSubDevice;
    }

    public void setSubDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        this.mSubDevice = cachedBluetoothDevice;
    }

    public void switchSubDeviceContent() {
        BluetoothDevice bluetoothDevice = this.mDevice;
        short s = this.mRssi;
        boolean z = this.mJustDiscovered;
        CachedBluetoothDevice cachedBluetoothDevice = this.mSubDevice;
        this.mDevice = cachedBluetoothDevice.mDevice;
        this.mRssi = cachedBluetoothDevice.mRssi;
        this.mJustDiscovered = cachedBluetoothDevice.mJustDiscovered;
        cachedBluetoothDevice.mDevice = bluetoothDevice;
        cachedBluetoothDevice.mRssi = s;
        cachedBluetoothDevice.mJustDiscovered = z;
        fetchActiveDevices();
    }
}
