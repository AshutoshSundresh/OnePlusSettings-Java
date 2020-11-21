package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.oneplus.settings.utils.OPUtils;

public class BluetoothDeviceRenamePreferenceController extends BluetoothDeviceNamePreferenceController {
    private Fragment mFragment;
    private MetricsFeatureProvider mMetricsFeatureProvider;

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BluetoothDeviceRenamePreferenceController(Context context, String str) {
        super(context, str);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController
    public void updatePreferenceState(Preference preference) {
        preference.setSummary(getSummary());
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        preference.setVisible(bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController
    public CharSequence getSummary() {
        String string = Settings.System.getString(this.mContext.getContentResolver(), "oem_oneplus_devicename");
        return (!OPUtils.isEF009Project() || !OPUtils.isContainSymbol(string)) ? string : OPUtils.getSymbolDeviceName(string);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(getPreferenceKey(), preference.getKey()) || this.mFragment == null) {
            return false;
        }
        this.mMetricsFeatureProvider.action(this.mContext, 161, new Pair[0]);
        this.mContext.startActivity(new Intent("com.oneplus.intent.OPDeviceNameActivity"));
        return true;
    }
}
