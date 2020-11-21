package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.location.ScanningSettings;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.utils.AnnotationSpan;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.widget.OPFooterPreference;

public class BluetoothSwitchPreferenceController implements LifecycleObserver, OnStart, OnStop, SwitchWidgetController.OnSwitchChangeListener, View.OnClickListener {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothEnabler mBluetoothEnabler;
    private Context mContext;
    private OPFooterPreference mFooterPreference;
    private RestrictionUtils mRestrictionUtils;
    private SwitchWidgetController mSwitch;

    public BluetoothSwitchPreferenceController(Context context, SwitchWidgetController switchWidgetController, OPFooterPreference oPFooterPreference) {
        this(context, new RestrictionUtils(), switchWidgetController, oPFooterPreference);
    }

    public BluetoothSwitchPreferenceController(Context context, RestrictionUtils restrictionUtils, SwitchWidgetController switchWidgetController, OPFooterPreference oPFooterPreference) {
        this.mRestrictionUtils = restrictionUtils;
        this.mSwitch = switchWidgetController;
        this.mContext = context;
        this.mFooterPreference = oPFooterPreference;
        switchWidgetController.setupView();
        updateText(this.mSwitch.isChecked());
        BluetoothEnabler bluetoothEnabler = new BluetoothEnabler(context, switchWidgetController, FeatureFactory.getFactory(context).getMetricsFeatureProvider(), 870, this.mRestrictionUtils);
        this.mBluetoothEnabler = bluetoothEnabler;
        bluetoothEnabler.setToggleCallback(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter != null) {
            setBluetoothDiscoverableState();
            Settings.System.getString(this.mContext.getContentResolver(), "oem_oneplus_devicename");
            String resetDeviceNameIfInvalid = OPUtils.resetDeviceNameIfInvalid(this.mContext);
            if (!TextUtils.isEmpty(resetDeviceNameIfInvalid)) {
                this.mBluetoothAdapter.setName(resetDeviceNameIfInvalid);
            }
        }
        this.mBluetoothEnabler.resume(this.mContext);
        SwitchWidgetController switchWidgetController = this.mSwitch;
        if (switchWidgetController != null) {
            updateText(switchWidgetController.isChecked());
        }
    }

    private void setBluetoothDiscoverableState() {
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "bluetooth_default_scan_mode", 21);
        if (i == 23) {
            this.mBluetoothAdapter.setScanMode(23);
        } else if (i == 21) {
            this.mBluetoothAdapter.setScanMode(21);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mBluetoothEnabler.pause();
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        updateText(z);
        setBluetoothDiscoverableState();
        return true;
    }

    public void onClick(View view) {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ScanningSettings.class.getName());
        subSettingLauncher.setSourceMetricsCategory(1390);
        subSettingLauncher.launch();
    }

    /* access modifiers changed from: package-private */
    public void updateText(boolean z) {
        if (z || !Utils.isBluetoothScanningEnabled(this.mContext)) {
            this.mFooterPreference.setTitle(C0017R$string.bluetooth_empty_list_bluetooth_off);
            return;
        }
        AnnotationSpan.LinkInfo linkInfo = new AnnotationSpan.LinkInfo("link", this);
        this.mFooterPreference.setTitle(AnnotationSpan.linkify(this.mContext.getText(C0017R$string.bluetooth_scanning_on_info_message), linkInfo));
    }
}
