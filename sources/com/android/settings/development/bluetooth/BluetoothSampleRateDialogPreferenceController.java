package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class BluetoothSampleRateDialogPreferenceController extends AbstractBluetoothDialogPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_sample_rate_settings";
    }

    public BluetoothSampleRateDialogPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
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
        int i2 = 4;
        if (i != 0) {
            if (i == 1) {
                i2 = 1;
            } else if (i == 2) {
                i2 = 2;
            } else if (i != 3) {
                if (i == 4) {
                    i2 = 8;
                }
            }
            this.mBluetoothA2dpConfigStore.setSampleRate(i2);
        }
        BluetoothCodecConfig currentCodecConfig = getCurrentCodecConfig();
        if (currentCodecConfig != null) {
            i2 = AbstractBluetoothDialogPreferenceController.getHighestSampleRate(getSelectableByCodecType(currentCodecConfig.getCodecType()));
            this.mBluetoothA2dpConfigStore.setSampleRate(i2);
        }
        i2 = 0;
        this.mBluetoothA2dpConfigStore.setSampleRate(i2);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    public int getCurrentIndexByConfig(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.e("BtSampleRateCtr", "Unable to get current config index. Config is null.");
        }
        return convertCfgToBtnIndex(bluetoothCodecConfig.getSampleRate());
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference.Callback
    public List<Integer> getSelectableIndex() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(getDefaultIndex()));
        BluetoothCodecConfig currentCodecConfig = getCurrentCodecConfig();
        if (currentCodecConfig != null) {
            int sampleRate = getSelectableByCodecType(currentCodecConfig.getCodecType()).getSampleRate();
            int[] iArr = AbstractBluetoothDialogPreferenceController.SAMPLE_RATES;
            for (int i : iArr) {
                if ((sampleRate & i) != 0) {
                    arrayList.add(Integer.valueOf(convertCfgToBtnIndex(i)));
                }
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
        if (i == 8) {
            return 4;
        }
        Log.e("BtSampleRateCtr", "Unsupported config:" + i);
        return defaultIndex;
    }
}
