package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.appcompat.R$styleable;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class OPBluetoothDiscoverablePreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private static final String KEY_DISCOVERABLE_DEVICE = "discoverable_device";
    private static final String SETTINGS_SYSTEM_BLUETOOTH_DEFAULT_SCAN_MODE = "bluetooth_default_scan_mode";
    private final BluetoothAdapter mBluetoothAdapter;
    private int mBluetoothScanMode = 23;
    private Context mContext;
    private SettingObserver mSettingObserver;
    private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {
        /* class com.android.settings.bluetooth.OPBluetoothDiscoverablePreferenceController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (((action.hashCode() == -1530327060 && action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) ? (char) 0 : 65535) == 0) {
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 0);
                if (intExtra == 10) {
                    OPBluetoothDiscoverablePreferenceController.this.mSwitchPreference.setVisible(false);
                } else if (intExtra == 12) {
                    OPBluetoothDiscoverablePreferenceController.this.mSwitchPreference.setVisible(true);
                    OPBluetoothDiscoverablePreferenceController oPBluetoothDiscoverablePreferenceController = OPBluetoothDiscoverablePreferenceController.this;
                    oPBluetoothDiscoverablePreferenceController.mSwitchPreference.setChecked(oPBluetoothDiscoverablePreferenceController.getSwitchCheckedStatus());
                }
            }
        }
    };
    SwitchPreference mSwitchPreference;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_DISCOVERABLE_DEVICE;
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPBluetoothDiscoverablePreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_DISCOVERABLE_DEVICE);
        this.mContext = context;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mSettingObserver = new SettingObserver(preferenceScreen.findPreference(KEY_DISCOVERABLE_DEVICE));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver(), true);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        this.mContext.registerReceiver(this.mStatusReceive, intentFilter);
    }

    public void setVisible() {
        if (this.mBluetoothAdapter.getState() == 12) {
            this.mSwitchPreference.setVisible(true);
        } else {
            this.mSwitchPreference.setVisible(false);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean getSwitchCheckedStatus() {
        return Settings.System.getInt(this.mContext.getContentResolver(), SETTINGS_SYSTEM_BLUETOOTH_DEFAULT_SCAN_MODE, 21) == 23;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver(), false);
        }
        this.mContext.unregisterReceiver(this.mStatusReceive);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mBluetoothAdapter.getState() == 12 ? 0 : 2;
    }

    private void saveScanModeToSettingsProvider(int i) {
        Settings.System.putInt(this.mContext.getContentResolver(), SETTINGS_SYSTEM_BLUETOOTH_DEFAULT_SCAN_MODE, i);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), KEY_DISCOVERABLE_DEVICE) || !(preference instanceof SwitchPreference)) {
            return false;
        }
        if (((SwitchPreference) preference).isChecked()) {
            this.mBluetoothAdapter.setScanMode(23);
            this.mBluetoothAdapter.setDiscoverableTimeout(R$styleable.AppCompatTheme_windowFixedHeightMajor);
            saveScanModeToSettingsProvider(23);
            return true;
        }
        this.mBluetoothAdapter.setScanMode(21);
        saveScanModeToSettingsProvider(21);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            int i = Settings.System.getInt(this.mContext.getContentResolver(), SETTINGS_SYSTEM_BLUETOOTH_DEFAULT_SCAN_MODE, 21);
            this.mBluetoothScanMode = i;
            if (i == 23) {
                this.mBluetoothAdapter.setScanMode(23);
                switchPreference.setChecked(true);
            } else if (i == 21) {
                this.mBluetoothAdapter.setScanMode(21);
                switchPreference.setChecked(false);
            }
            switchPreference.setEnabled(this.mBluetoothAdapter.isEnabled());
        }
    }

    class SettingObserver extends ContentObserver {
        private final Uri SETTINGS_SYSTEM_BLUETOOTH_DEFAULT_SCAN_MODE_URI = Settings.System.getUriFor(OPBluetoothDiscoverablePreferenceController.SETTINGS_SYSTEM_BLUETOOTH_DEFAULT_SCAN_MODE);
        private final Preference mPreference;

        public SettingObserver(Preference preference) {
            super(new Handler());
            this.mPreference = preference;
        }

        public void register(ContentResolver contentResolver, boolean z) {
            if (z) {
                contentResolver.registerContentObserver(this.SETTINGS_SYSTEM_BLUETOOTH_DEFAULT_SCAN_MODE_URI, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.SETTINGS_SYSTEM_BLUETOOTH_DEFAULT_SCAN_MODE_URI.equals(uri)) {
                OPBluetoothDiscoverablePreferenceController.this.updateState(this.mPreference);
            }
        }
    }
}
