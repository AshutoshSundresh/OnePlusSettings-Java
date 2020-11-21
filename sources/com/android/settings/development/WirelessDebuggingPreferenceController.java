package com.android.settings.development;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.debug.IAdbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class WirelessDebuggingPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin, LifecycleObserver, OnResume, OnPause {
    private final IAdbManager mAdbManager;
    private final ContentResolver mContentResolver;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ContentObserver mSettingsObserver;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "toggle_adb_wireless";
    }

    public WirelessDebuggingPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mAdbManager = IAdbManager.Stub.asInterface(ServiceManager.getService("adb"));
        this.mSettingsObserver = new ContentObserver(this.mHandler) {
            /* class com.android.settings.development.WirelessDebuggingPreferenceController.AnonymousClass1 */

            public void onChange(boolean z, Uri uri) {
                WirelessDebuggingPreferenceController wirelessDebuggingPreferenceController = WirelessDebuggingPreferenceController.this;
                wirelessDebuggingPreferenceController.updateState(((DeveloperOptionsPreferenceController) wirelessDebuggingPreferenceController).mPreference);
            }
        };
        this.mContentResolver = context.getContentResolver();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        try {
            return this.mAdbManager.isAdbWifiSupported();
        } catch (RemoteException e) {
            Log.e("WirelessDebugPrefCtrl", "Unable to check if adb wifi is supported.", e);
            return false;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchEnabled() {
        super.onDeveloperOptionsSwitchEnabled();
        this.mPreference.setEnabled(true);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        this.mPreference.setEnabled(false);
        Settings.Global.putInt(this.mContext.getContentResolver(), "adb_wifi_enabled", 0);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("adb_wifi_enabled"), false, this.mSettingsObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z = false;
        if (Settings.Global.getInt(this.mContentResolver, "adb_wifi_enabled", 0) != 0) {
            z = true;
        }
        ((MasterSwitchPreference) preference).setChecked(z);
    }

    public static boolean isWifiConnected(Context context) {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null || !activeNetworkInfo.isConnected() || activeNetworkInfo.getType() != 1) {
            return false;
        }
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (!booleanValue || isWifiConnected(this.mContext)) {
            Settings.Global.putInt(this.mContext.getContentResolver(), "adb_wifi_enabled", booleanValue ? 1 : 0);
            return true;
        }
        Toast.makeText(this.mContext, C0017R$string.adb_wireless_no_network_msg, 1).show();
        return false;
    }
}
