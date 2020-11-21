package com.android.settings.connecteddevice.usb;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class ConnectedUsbDeviceUpdater {
    private DevicePreferenceCallback mDevicePreferenceCallback;
    private DashboardFragment mFragment;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private UsbBackend mUsbBackend;
    UsbConnectionBroadcastReceiver.UsbConnectionListener mUsbConnectionListener;
    Preference mUsbPreference;
    UsbConnectionBroadcastReceiver mUsbReceiver;

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ConnectedUsbDeviceUpdater(boolean z, long j, int i, int i2) {
        if (z) {
            Preference preference = this.mUsbPreference;
            if (i2 != 2) {
                j = 0;
            }
            preference.setSummary(getSummary(j, i));
            this.mDevicePreferenceCallback.onDeviceAdded(this.mUsbPreference);
            return;
        }
        this.mDevicePreferenceCallback.onDeviceRemoved(this.mUsbPreference);
    }

    public ConnectedUsbDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback) {
        this(context, dashboardFragment, devicePreferenceCallback, new UsbBackend(context));
    }

    ConnectedUsbDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback, UsbBackend usbBackend) {
        this.mUsbConnectionListener = new UsbConnectionBroadcastReceiver.UsbConnectionListener() {
            /* class com.android.settings.connecteddevice.usb.$$Lambda$ConnectedUsbDeviceUpdater$8_8ZhYJMgnzGVqi7esENaXwOM */

            @Override // com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver.UsbConnectionListener
            public final void onUsbConnectionChanged(boolean z, long j, int i, int i2) {
                ConnectedUsbDeviceUpdater.this.lambda$new$0$ConnectedUsbDeviceUpdater(z, j, i, i2);
            }
        };
        this.mFragment = dashboardFragment;
        this.mDevicePreferenceCallback = devicePreferenceCallback;
        this.mUsbBackend = usbBackend;
        this.mUsbReceiver = new UsbConnectionBroadcastReceiver(context, this.mUsbConnectionListener, this.mUsbBackend);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(this.mFragment.getContext()).getMetricsFeatureProvider();
    }

    public void registerCallback() {
        this.mUsbReceiver.register();
    }

    public void unregisterCallback() {
        this.mUsbReceiver.unregister();
    }

    public void initUsbPreference(Context context) {
        Preference preference = new Preference(context, null);
        this.mUsbPreference = preference;
        preference.setTitle(C0017R$string.usb_pref);
        this.mUsbPreference.setKey("connected_usb");
        this.mUsbPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.connecteddevice.usb.$$Lambda$ConnectedUsbDeviceUpdater$GzbX4qf24akYMeF2cR6p1BWlpo */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return ConnectedUsbDeviceUpdater.this.lambda$initUsbPreference$1$ConnectedUsbDeviceUpdater(preference);
            }
        });
        forceUpdate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initUsbPreference$1 */
    public /* synthetic */ boolean lambda$initUsbPreference$1$ConnectedUsbDeviceUpdater(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, this.mFragment.getMetricsCategory());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mFragment.getContext());
        subSettingLauncher.setDestination(UsbDetailsFragment.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.device_details_title);
        subSettingLauncher.setSourceMetricsCategory(this.mFragment.getMetricsCategory());
        subSettingLauncher.launch();
        return true;
    }

    private void forceUpdate() {
        this.mUsbReceiver.register();
    }

    public static int getSummary(long j, int i) {
        if (i != 1) {
            if (i != 2) {
                return C0017R$string.usb_summary_charging_only;
            }
            if (j == 4) {
                return C0017R$string.usb_summary_file_transfers;
            }
            if (j == 32) {
                return C0017R$string.usb_summary_tether;
            }
            if (j == 16) {
                return C0017R$string.usb_summary_photo_transfers;
            }
            if (j == 8) {
                return C0017R$string.usb_summary_MIDI;
            }
            return C0017R$string.usb_summary_charging_only;
        } else if (j == 4) {
            return C0017R$string.usb_summary_file_transfers_power;
        } else {
            if (j == 32) {
                return C0017R$string.usb_summary_tether_power;
            }
            if (j == 16) {
                return C0017R$string.usb_summary_photo_transfers_power;
            }
            if (j == 8) {
                return C0017R$string.usb_summary_MIDI_power;
            }
            return C0017R$string.usb_summary_power_only;
        }
    }
}
