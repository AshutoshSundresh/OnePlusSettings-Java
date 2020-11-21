package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class BluetoothQualityDialogPreferenceController extends AbstractBluetoothDialogPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_a2dp_ldac_playback_quality";
    }

    public BluetoothQualityDialogPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
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
        this.mBluetoothA2dpConfigStore.setCodecSpecific1Value((i == 0 || i == 1 || i == 2 || i == 3) ? (long) (i + 1000) : 0);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    public int getCurrentIndexByConfig(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.e("BtQualityCtr", "Unable to get current config index. Config is null.");
        }
        return convertCfgToBtnIndex((int) bluetoothCodecConfig.getCodecSpecific1());
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference.Callback
    public List<Integer> getSelectableIndex() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 4; i++) {
            arrayList.add(Integer.valueOf(i));
        }
        return arrayList;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        BluetoothCodecConfig currentCodecConfig = getCurrentCodecConfig();
        if (currentCodecConfig == null || currentCodecConfig.getCodecType() != 5) {
            preference.setEnabled(false);
            preference.setSummary("");
            return;
        }
        preference.setEnabled(true);
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    public void onHDAudioEnabled(boolean z) {
        Log.d("BtQualityCtr", "onHDAudioEnabled: " + z);
        this.mPreference.setEnabled(z);
    }

    /* access modifiers changed from: package-private */
    public int convertCfgToBtnIndex(int i) {
        int i2 = i - 1000;
        return i2 < 0 ? getDefaultIndex() : i2;
    }
}
