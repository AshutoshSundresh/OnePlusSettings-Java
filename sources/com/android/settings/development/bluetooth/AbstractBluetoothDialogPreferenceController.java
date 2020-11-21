package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settings.development.bluetooth.BaseBluetoothDialogPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;

public abstract class AbstractBluetoothDialogPreferenceController extends AbstractBluetoothPreferenceController implements BaseBluetoothDialogPreference.Callback {
    protected static final int[] BITS_PER_SAMPLES = {4, 2, 1};
    protected static final int[] CHANNEL_MODES = {2, 1};
    protected static final int[] CODEC_TYPES = {6, 5, 4, 3, 2, 1, 0};
    protected static final int[] SAMPLE_RATES = {32, 16, 8, 4, 2, 1};
    protected final BluetoothA2dpConfigStore mBluetoothA2dpConfigStore;

    /* access modifiers changed from: protected */
    public abstract int getCurrentIndexByConfig(BluetoothCodecConfig bluetoothCodecConfig);

    public void onHDAudioEnabled(boolean z) {
    }

    /* access modifiers changed from: protected */
    public abstract void writeConfigurationValues(int i);

    public AbstractBluetoothDialogPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
        this.mBluetoothA2dpConfigStore = bluetoothA2dpConfigStore;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return ((BaseBluetoothDialogPreference) this.mPreference).generateSummary(getCurrentConfigIndex());
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference.Callback
    public void onIndexUpdated(int i) {
        BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
        if (bluetoothA2dp != null) {
            writeConfigurationValues(i);
            BluetoothCodecConfig createCodecConfig = this.mBluetoothA2dpConfigStore.createCodecConfig();
            BluetoothDevice activeDevice = this.mBluetoothA2dp.getActiveDevice();
            if (activeDevice != null) {
                bluetoothA2dp.setCodecConfigPreference(activeDevice, createCodecConfig);
            }
            Preference preference = this.mPreference;
            preference.setSummary(((BaseBluetoothDialogPreference) preference).generateSummary(i));
        }
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference.Callback
    public int getCurrentConfigIndex() {
        BluetoothCodecConfig currentCodecConfig = getCurrentCodecConfig();
        if (currentCodecConfig != null) {
            return getCurrentIndexByConfig(currentCodecConfig);
        }
        Log.d("AbstractBtDlgCtr", "Unable to get current config index. Current codec Config is null.");
        return getDefaultIndex();
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController, com.android.settings.development.BluetoothServiceConnectionListener
    public void onBluetoothServiceConnected(BluetoothA2dp bluetoothA2dp) {
        super.onBluetoothServiceConnected(bluetoothA2dp);
        initConfigStore();
    }

    private void initConfigStore() {
        BluetoothCodecConfig currentCodecConfig = getCurrentCodecConfig();
        if (currentCodecConfig != null) {
            this.mBluetoothA2dpConfigStore.setCodecType(currentCodecConfig.getCodecType());
            this.mBluetoothA2dpConfigStore.setSampleRate(currentCodecConfig.getSampleRate());
            this.mBluetoothA2dpConfigStore.setBitsPerSample(currentCodecConfig.getBitsPerSample());
            this.mBluetoothA2dpConfigStore.setChannelMode(currentCodecConfig.getChannelMode());
            this.mBluetoothA2dpConfigStore.setCodecPriority(currentCodecConfig.getCodecPriority());
            this.mBluetoothA2dpConfigStore.setCodecSpecific1Value(currentCodecConfig.getCodecSpecific1());
        }
    }

    /* access modifiers changed from: protected */
    public int getDefaultIndex() {
        return ((BaseBluetoothDialogPreference) this.mPreference).getDefaultIndex();
    }

    /* access modifiers changed from: protected */
    public BluetoothCodecConfig getCurrentCodecConfig() {
        BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
        if (bluetoothA2dp == null) {
            return null;
        }
        BluetoothDevice activeDevice = bluetoothA2dp.getActiveDevice();
        if (activeDevice == null) {
            Log.d("AbstractBtDlgCtr", "Unable to get current codec config. No active device.");
            return null;
        }
        BluetoothCodecStatus codecStatus = bluetoothA2dp.getCodecStatus(activeDevice);
        if (codecStatus != null) {
            return codecStatus.getCodecConfig();
        }
        Log.d("AbstractBtDlgCtr", "Unable to get current codec config. Codec status is null");
        return null;
    }

    /* access modifiers changed from: protected */
    public BluetoothCodecConfig[] getSelectableConfigs(BluetoothDevice bluetoothDevice) {
        BluetoothCodecStatus codecStatus;
        BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
        if (bluetoothA2dp == null) {
            return null;
        }
        if (bluetoothDevice == null) {
            bluetoothDevice = bluetoothA2dp.getActiveDevice();
        }
        if (bluetoothDevice == null || (codecStatus = bluetoothA2dp.getCodecStatus(bluetoothDevice)) == null) {
            return null;
        }
        return codecStatus.getCodecsSelectableCapabilities();
    }

    /* access modifiers changed from: protected */
    public BluetoothCodecConfig getSelectableByCodecType(int i) {
        BluetoothDevice activeDevice = this.mBluetoothA2dp.getActiveDevice();
        if (activeDevice == null) {
            Log.d("AbstractBtDlgCtr", "Unable to get selectable config. No active device.");
            return null;
        }
        BluetoothCodecConfig[] selectableConfigs = getSelectableConfigs(activeDevice);
        if (selectableConfigs == null) {
            Log.d("AbstractBtDlgCtr", "Unable to get selectable config. Selectable configs is empty.");
            return null;
        }
        for (BluetoothCodecConfig bluetoothCodecConfig : selectableConfigs) {
            if (bluetoothCodecConfig.getCodecType() == i) {
                return bluetoothCodecConfig;
            }
        }
        Log.d("AbstractBtDlgCtr", "Unable to find matching codec config, type is " + i);
        return null;
    }

    static int getHighestCodec(BluetoothCodecConfig[] bluetoothCodecConfigArr) {
        int[] iArr = CODEC_TYPES;
        if (bluetoothCodecConfigArr == null) {
            Log.d("AbstractBtDlgCtr", "Unable to get highest codec. Configs are empty");
            return 1000000;
        }
        Log.d("AbstractBtDlgCtr", "CODEC_TYPES len: " + iArr.length + " codec_config len: " + bluetoothCodecConfigArr.length);
        for (int i = 0; i < iArr.length; i++) {
            for (BluetoothCodecConfig bluetoothCodecConfig : bluetoothCodecConfigArr) {
                if (bluetoothCodecConfig.getCodecType() == iArr[i]) {
                    return iArr[i];
                }
            }
        }
        return 1000000;
    }

    static int getHighestSampleRate(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.d("AbstractBtDlgCtr", "Unable to get highest sample rate. Config is empty");
            return 0;
        }
        int sampleRate = bluetoothCodecConfig.getSampleRate();
        int i = 0;
        while (true) {
            int[] iArr = SAMPLE_RATES;
            if (i >= iArr.length) {
                return 0;
            }
            if ((iArr[i] & sampleRate) != 0) {
                return iArr[i];
            }
            i++;
        }
    }

    static int getHighestBitsPerSample(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.d("AbstractBtDlgCtr", "Unable to get highest bits per sample. Config is empty");
            return 0;
        }
        int bitsPerSample = bluetoothCodecConfig.getBitsPerSample();
        int i = 0;
        while (true) {
            int[] iArr = BITS_PER_SAMPLES;
            if (i >= iArr.length) {
                return 0;
            }
            if ((iArr[i] & bitsPerSample) != 0) {
                return iArr[i];
            }
            i++;
        }
    }

    static int getHighestChannelMode(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.d("AbstractBtDlgCtr", "Unable to get highest channel mode. Config is empty");
            return 0;
        }
        int channelMode = bluetoothCodecConfig.getChannelMode();
        int i = 0;
        while (true) {
            int[] iArr = CHANNEL_MODES;
            if (i >= iArr.length) {
                return 0;
            }
            if ((iArr[i] & channelMode) != 0) {
                return iArr[i];
            }
            i++;
        }
    }
}
