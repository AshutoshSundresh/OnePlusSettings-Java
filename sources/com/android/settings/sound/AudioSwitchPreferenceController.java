package com.android.settings.sound;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.os.Handler;
import android.os.Looper;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.bluetooth.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.bluetooth.A2dpProfile;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.HeadsetProfile;
import com.android.settingslib.bluetooth.HearingAidProfile;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public abstract class AudioSwitchPreferenceController extends BasePreferenceController implements BluetoothCallback, LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "AudioSwitchPrefCtrl";
    protected final AudioManager mAudioManager;
    private final AudioManagerAudioDeviceCallback mAudioManagerAudioDeviceCallback = new AudioManagerAudioDeviceCallback();
    protected AudioSwitchCallback mAudioSwitchPreferenceCallback;
    protected final List<BluetoothDevice> mConnectedDevices = new ArrayList();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private LocalBluetoothManager mLocalBluetoothManager;
    protected final MediaRouter mMediaRouter;
    protected Preference mPreference;
    protected LocalBluetoothProfileManager mProfileManager;
    private final WiredHeadsetBroadcastReceiver mReceiver = new WiredHeadsetBroadcastReceiver();
    protected int mSelectedIndex;

    public interface AudioSwitchCallback {
        void onPreferenceDataChanged(ListPreference listPreference);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public abstract BluetoothDevice findActiveDevice();

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

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onAclConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onAclConnectionStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onConnectionStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onDeviceBondStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        super.onDeviceDeleted(cachedBluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onScanningStateChanged(boolean z) {
        super.onScanningStateChanged(z);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AudioSwitchPreferenceController(Context context, String str) {
        super(context, str);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        this.mMediaRouter = (MediaRouter) context.getSystemService("media_router");
        FutureTask futureTask = new FutureTask(new Callable() {
            /* class com.android.settings.sound.$$Lambda$AudioSwitchPreferenceController$GC_sYSWqqCmy3hCGLKM8AEFN_Y */

            @Override // java.util.concurrent.Callable
            public final Object call() {
                return AudioSwitchPreferenceController.this.lambda$new$0$AudioSwitchPreferenceController();
            }
        });
        try {
            futureTask.run();
            LocalBluetoothManager localBluetoothManager = (LocalBluetoothManager) futureTask.get();
            this.mLocalBluetoothManager = localBluetoothManager;
            if (localBluetoothManager == null) {
                Log.e(TAG, "Bluetooth is not supported on this device");
            } else {
                this.mProfileManager = localBluetoothManager.getProfileManager();
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.w(TAG, "Error getting LocalBluetoothManager.", e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ LocalBluetoothManager lambda$new$0$AudioSwitchPreferenceController() throws Exception {
        return Utils.getLocalBtManager(this.mContext);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public final int getAvailabilityStatus() {
        return (!FeatureFlagUtils.isEnabled(this.mContext, "settings_audio_switcher") || !this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth")) ? 2 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(this.mPreferenceKey);
        this.mPreference = findPreference;
        findPreference.setVisible(false);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        LocalBluetoothManager localBluetoothManager = this.mLocalBluetoothManager;
        if (localBluetoothManager == null) {
            Log.e(TAG, "Bluetooth is not supported on this device");
            return;
        }
        localBluetoothManager.setForegroundActivity(this.mContext);
        register();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        LocalBluetoothManager localBluetoothManager = this.mLocalBluetoothManager;
        if (localBluetoothManager == null) {
            Log.e(TAG, "Bluetooth is not supported on this device");
            return;
        }
        localBluetoothManager.setForegroundActivity(null);
        unregister();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
        updateState(this.mPreference);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        updateState(this.mPreference);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onAudioModeChanged() {
        updateState(this.mPreference);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        updateState(this.mPreference);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        updateState(this.mPreference);
    }

    public void setCallback(AudioSwitchCallback audioSwitchCallback) {
        this.mAudioSwitchPreferenceCallback = audioSwitchCallback;
    }

    /* access modifiers changed from: protected */
    public boolean isStreamFromOutputDevice(int i, int i2) {
        return (this.mAudioManager.getDevicesForStream(i) & i2) != 0;
    }

    /* access modifiers changed from: protected */
    public List<BluetoothDevice> getConnectedHfpDevices() {
        ArrayList arrayList = new ArrayList();
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile == null) {
            return arrayList;
        }
        for (BluetoothDevice bluetoothDevice : headsetProfile.getConnectedDevices()) {
            if (bluetoothDevice.isConnected()) {
                arrayList.add(bluetoothDevice);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public List<BluetoothDevice> getConnectedA2dpDevices() {
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile == null) {
            return new ArrayList();
        }
        return a2dpProfile.getConnectedDevices();
    }

    /* access modifiers changed from: protected */
    public List<BluetoothDevice> getConnectedHearingAidDevices() {
        ArrayList arrayList = new ArrayList();
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile == null) {
            return arrayList;
        }
        ArrayList arrayList2 = new ArrayList();
        for (BluetoothDevice bluetoothDevice : hearingAidProfile.getConnectedDevices()) {
            long hiSyncId = hearingAidProfile.getHiSyncId(bluetoothDevice);
            if (!arrayList2.contains(Long.valueOf(hiSyncId)) && bluetoothDevice.isConnected()) {
                arrayList2.add(Long.valueOf(hiSyncId));
                arrayList.add(bluetoothDevice);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public BluetoothDevice findActiveHearingAidDevice() {
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile == null) {
            return null;
        }
        for (BluetoothDevice bluetoothDevice : hearingAidProfile.getActiveDevices()) {
            if (bluetoothDevice != null && this.mConnectedDevices.contains(bluetoothDevice)) {
                return bluetoothDevice;
            }
        }
        return null;
    }

    private void register() {
        this.mLocalBluetoothManager.getEventManager().registerCallback(this);
        this.mAudioManager.registerAudioDeviceCallback(this.mAudioManagerAudioDeviceCallback, this.mHandler);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.HEADSET_PLUG");
        intentFilter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
    }

    private void unregister() {
        this.mLocalBluetoothManager.getEventManager().unregisterCallback(this);
        this.mAudioManager.unregisterAudioDeviceCallback(this.mAudioManagerAudioDeviceCallback);
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    /* access modifiers changed from: private */
    public class AudioManagerAudioDeviceCallback extends AudioDeviceCallback {
        private AudioManagerAudioDeviceCallback() {
        }

        public void onAudioDevicesAdded(AudioDeviceInfo[] audioDeviceInfoArr) {
            AudioSwitchPreferenceController audioSwitchPreferenceController = AudioSwitchPreferenceController.this;
            audioSwitchPreferenceController.updateState(audioSwitchPreferenceController.mPreference);
        }

        public void onAudioDevicesRemoved(AudioDeviceInfo[] audioDeviceInfoArr) {
            AudioSwitchPreferenceController audioSwitchPreferenceController = AudioSwitchPreferenceController.this;
            audioSwitchPreferenceController.updateState(audioSwitchPreferenceController.mPreference);
        }
    }

    /* access modifiers changed from: private */
    public class WiredHeadsetBroadcastReceiver extends BroadcastReceiver {
        private WiredHeadsetBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.HEADSET_PLUG".equals(action) || "android.media.STREAM_DEVICES_CHANGED_ACTION".equals(action)) {
                AudioSwitchPreferenceController audioSwitchPreferenceController = AudioSwitchPreferenceController.this;
                audioSwitchPreferenceController.updateState(audioSwitchPreferenceController.mPreference);
            }
        }
    }
}
