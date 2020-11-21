package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.AnnotationSpan;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class WifiWakeupPreferenceController extends TogglePreferenceController implements LifecycleObserver, OnPause, OnResume {
    private static final String KEY_ENABLE_WIFI_WAKEUP = "enable_wifi_wakeup";
    private static final String TAG = "WifiWakeupPrefController";
    private Fragment mFragment;
    private final IntentFilter mLocationFilter = new IntentFilter("android.location.MODE_CHANGED");
    LocationManager mLocationManager;
    private final BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        /* class com.android.settings.wifi.WifiWakeupPreferenceController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            WifiWakeupPreferenceController wifiWakeupPreferenceController = WifiWakeupPreferenceController.this;
            wifiWakeupPreferenceController.updateState(wifiWakeupPreferenceController.mPreference);
        }
    };
    SwitchPreference mPreference;
    private SettingObserver mSettingObserver;
    WifiManager mWifiManager;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiWakeupPreferenceController(Context context) {
        super(context, KEY_ENABLE_WIFI_WAKEUP);
        this.mLocationManager = (LocationManager) context.getSystemService("location");
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        this.mSettingObserver = new SettingObserver();
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return getWifiWakeupEnabled() && getWifiScanningEnabled() && this.mLocationManager.isLocationEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z) {
            if (this.mFragment == null) {
                throw new IllegalStateException("No fragment to start activity");
            } else if (!this.mLocationManager.isLocationEnabled()) {
                this.mFragment.startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 600);
                return false;
            } else if (!getWifiScanningEnabled()) {
                showScanningDialog();
                return false;
            }
        }
        setWifiWakeupEnabled(z);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshSummary(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (!this.mLocationManager.isLocationEnabled()) {
            return getNoLocationSummary();
        }
        return this.mContext.getText(C0017R$string.wifi_wakeup_summary);
    }

    /* access modifiers changed from: package-private */
    public CharSequence getNoLocationSummary() {
        AnnotationSpan.LinkInfo linkInfo = new AnnotationSpan.LinkInfo("link", null);
        return AnnotationSpan.linkify(this.mContext.getText(C0017R$string.wifi_wakeup_summary_no_location), linkInfo);
    }

    public void onActivityResult(int i, int i2) {
        if (i == 600 && this.mLocationManager.isLocationEnabled() && getWifiScanningEnabled()) {
            setWifiWakeupEnabled(true);
            updateState(this.mPreference);
        }
    }

    private boolean getWifiScanningEnabled() {
        return this.mWifiManager.isScanAlwaysAvailable();
    }

    private void showScanningDialog() {
        WifiScanningRequiredFragment newInstance = WifiScanningRequiredFragment.newInstance();
        newInstance.setTargetFragment(this.mFragment, 600);
        newInstance.show(this.mFragment.getFragmentManager(), TAG);
    }

    private boolean getWifiWakeupEnabled() {
        return this.mWifiManager.isAutoWakeupEnabled();
    }

    private void setWifiWakeupEnabled(boolean z) {
        this.mWifiManager.setAutoWakeupEnabled(z);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContext.registerReceiver(this.mLocationReceiver, this.mLocationFilter);
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver(), true);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContext.unregisterReceiver(this.mLocationReceiver);
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver(), false);
        }
    }

    class SettingObserver extends ContentObserver {
        private final Uri WIFI_WAKE_UP_ENABLED_URI = Settings.Global.getUriFor("wifi_wakeup_enabled");

        public SettingObserver() {
            super(new Handler(Looper.getMainLooper()));
        }

        public void register(ContentResolver contentResolver, boolean z) {
            contentResolver.registerContentObserver(this.WIFI_WAKE_UP_ENABLED_URI, false, this);
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.WIFI_WAKE_UP_ENABLED_URI.equals(uri)) {
                WifiWakeupPreferenceController wifiWakeupPreferenceController = WifiWakeupPreferenceController.this;
                wifiWakeupPreferenceController.updateState(wifiWakeupPreferenceController.mPreference);
                WifiWakeupPreferenceController.this.isChecked();
            }
        }
    }
}
