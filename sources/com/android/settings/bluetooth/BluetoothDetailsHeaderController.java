package com.android.settings.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.Pair;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;
import java.util.HashMap;

public class BluetoothDetailsHeaderController extends BluetoothDetailsController {
    private CachedBluetoothDeviceManager mDeviceManager;
    private EntityHeaderController mHeaderController;
    private LocalBluetoothManager mLocalManager;
    private boolean mTwsAddress = false;
    private HashMap<String, String> mTwsBatteryInfo = new HashMap<>();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_device_header";
    }

    public BluetoothDetailsHeaderController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle, LocalBluetoothManager localBluetoothManager) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
        this.mLocalManager = localBluetoothManager;
        this.mDeviceManager = localBluetoothManager.getCachedDeviceManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.bluetooth.BluetoothDetailsController
    public boolean isAvailable() {
        if (!DeviceConfig.getBoolean("settings_ui", "bt_advanced_header_enabled", true) || !BluetoothUtils.getBooleanMetaData(this.mCachedDevice.getDevice(), 6)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    public void init(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference("bluetooth_device_header");
        this.mHeaderController = EntityHeaderController.newInstance(this.mFragment.getActivity(), this.mFragment, layoutPreference.findViewById(C0010R$id.entity_header));
        preferenceScreen.addPreference(layoutPreference);
    }

    /* access modifiers changed from: protected */
    public void setHeaderProperties() {
        Pair<Drawable, String> btClassDrawableWithDescription = BluetoothUtils.getBtClassDrawableWithDescription(((BluetoothDetailsController) this).mContext, this.mCachedDevice);
        String str = this.mTwsBatteryInfo.get(this.mCachedDevice.getAddress());
        String connectionSummary = this.mCachedDevice.getConnectionSummary();
        if (TextUtils.isEmpty(connectionSummary)) {
            this.mHeaderController.setSecondSummary(null);
        } else {
            this.mHeaderController.setSecondSummary(this.mDeviceManager.getSubDeviceSummary(this.mCachedDevice));
        }
        this.mHeaderController.setLabel(this.mCachedDevice.getName());
        this.mHeaderController.setIcon((Drawable) btClassDrawableWithDescription.first);
        this.mHeaderController.setIconContentDescription((String) btClassDrawableWithDescription.second);
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(connectionSummary)) {
            this.mHeaderController.setSummary(str);
        } else if (!this.mTwsAddress) {
            this.mHeaderController.setSummary(connectionSummary);
        } else if (TextUtils.isEmpty(connectionSummary)) {
            this.mHeaderController.setSummary(connectionSummary);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    public void refresh() {
        if (isAvailable()) {
            setHeaderProperties();
            this.mHeaderController.done((Activity) this.mFragment.getActivity(), true);
        }
    }

    public void updateSumary(String str, String str2) {
        if (this.mHeaderController != null) {
            this.mTwsBatteryInfo.put(str, str2);
            this.mHeaderController.setSummary(str2);
            this.mHeaderController.done((Activity) this.mFragment.getActivity(), true);
        }
    }

    public void setTwsAddress(boolean z) {
        this.mTwsAddress = z;
    }
}
