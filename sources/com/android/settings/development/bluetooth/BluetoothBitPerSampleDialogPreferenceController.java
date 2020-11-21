package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class BluetoothBitPerSampleDialogPreferenceController extends AbstractBluetoothDialogPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_bit_per_sample_settings";
    }

    public BluetoothBitPerSampleDialogPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ((BaseBluetoothDialogPreference) this.mPreference).setCallback(this);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    public void writeConfigurationValues(int i) {
        int i2 = 2;
        if (i != 0) {
            if (i == 1) {
                i2 = 1;
            } else if (i != 2) {
                if (i == 3) {
                    i2 = 4;
                }
            }
            this.mBluetoothA2dpConfigStore.setBitsPerSample(i2);
        }
        BluetoothCodecConfig currentCodecConfig = getCurrentCodecConfig();
        if (currentCodecConfig != null) {
            i2 = AbstractBluetoothDialogPreferenceController.getHighestBitsPerSample(getSelectableByCodecType(currentCodecConfig.getCodecType()));
            this.mBluetoothA2dpConfigStore.setBitsPerSample(i2);
        }
        i2 = 0;
        this.mBluetoothA2dpConfigStore.setBitsPerSample(i2);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    public int getCurrentIndexByConfig(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.e("BtBitPerSampleCtr", "Unable to get current config index. Config is null.");
        }
        return convertCfgToBtnIndex(bluetoothCodecConfig.getBitsPerSample());
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference.Callback
    public List<Integer> getSelectableIndex() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(getDefaultIndex()));
        BluetoothCodecConfig currentCodecConfig = getCurrentCodecConfig();
        if (currentCodecConfig != null) {
            int bitsPerSample = getSelectableByCodecType(currentCodecConfig.getCodecType()).getBitsPerSample();
            int i = 0;
            while (true) {
                int[] iArr = AbstractBluetoothDialogPreferenceController.BITS_PER_SAMPLES;
                if (i >= iArr.length) {
                    break;
                }
                if ((iArr[i] & bitsPerSample) != 0) {
                    arrayList.add(Integer.valueOf(convertCfgToBtnIndex(iArr[i])));
                }
                i++;
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public int convertCfgToBtnIndex(int i) {
        int defaultIndex = getDefaultIndex();
        if (i == 1) {
            return 1;
        }
        if (i == 2) {
            return 2;
        }
        if (i == 4) {
            return 3;
        }
        Log.e("BtBitPerSampleCtr", "Unsupported config:" + i);
        return defaultIndex;
    }
}
