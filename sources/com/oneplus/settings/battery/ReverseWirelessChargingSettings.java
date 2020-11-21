package com.oneplus.settings.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;

public class ReverseWirelessChargingSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static String TAG = "ReverseWirelessChargingSettings";
    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        /* class com.oneplus.settings.battery.ReverseWirelessChargingSettings.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                boolean z = true;
                if (intent.getBooleanExtra("level", true)) {
                    if (intent.getIntExtra("level", 0) <= 15) {
                        ReverseWirelessChargingSettings.this.mSwitch.setEnabled(false);
                    } else {
                        ReverseWirelessChargingSettings.this.mSwitch.setEnabled(true);
                    }
                }
                if (intent.getIntExtra("plugged", 0) == 0) {
                    z = false;
                }
                if (ReverseWirelessChargingSettings.this.mSwitch != null && z) {
                    ReverseWirelessChargingSettings.this.mSwitch.setEnabled(false);
                }
            }
        }
    };
    private BatteryManager mBatteryManager;
    final ContentObserver mRWCObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.battery.ReverseWirelessChargingSettings.AnonymousClass1 */

        public void onChange(boolean z, Uri uri) {
            boolean z2 = true;
            if (Settings.System.getUriFor("reverse_wireless_charging_status").equals(uri)) {
                int intForUser = Settings.System.getIntForUser(ReverseWirelessChargingSettings.this.getContentResolver(), "reverse_wireless_charging_status", 0, -2);
                SwitchPreference switchPreference = ReverseWirelessChargingSettings.this.mSwitch;
                if (intForUser == 0) {
                    z2 = false;
                }
                switchPreference.setChecked(z2);
            } else if (Settings.System.getUriFor("REVERSE_WIRELESS_DISABLE_REASON").equals(uri)) {
                String disabledReason = ReverseWirelessChargingSettings.this.getDisabledReason();
                boolean equals = "low_power".equals(disabledReason);
                if ("temp_over_heat".equals(disabledReason) || equals) {
                    ReverseWirelessChargingSettings.this.mSwitch.setEnabled(false);
                } else {
                    ReverseWirelessChargingSettings.this.mSwitch.setEnabled(true);
                }
            }
        }
    };
    private SwitchPreference mSwitch;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mBatteryManager = (BatteryManager) getSystemService("batterymanager");
        SwitchPreference switchPreference = (SwitchPreference) findPreference("reverse_wireless_charging_switch");
        this.mSwitch = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String getDisabledReason() {
        String stringForUser = Settings.System.getStringForUser(getContentResolver(), "reverse_wireless_disable_reason", 0);
        String str = TAG;
        Log.d(str, "disable_reason=" + stringForUser);
        return stringForUser;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(Settings.System.getUriFor("reverse_wireless_charging_status"), false, this.mRWCObserver);
        getContentResolver().registerContentObserver(Settings.System.getUriFor("reverse_wireless_disable_reason"), false, this.mRWCObserver);
        getContext().registerReceiver(this.mBatteryInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (this.mSwitch != null) {
            this.mSwitch.setChecked(Settings.System.getIntForUser(getContentResolver(), "reverse_wireless_charging_status", 0, -2) != 0);
        }
        int intProperty = this.mBatteryManager.getIntProperty(4);
        int intProperty2 = this.mBatteryManager.getIntProperty(6);
        if (intProperty <= 15 || intProperty2 == 2) {
            this.mSwitch.setEnabled(false);
        } else {
            this.mSwitch.setEnabled(true);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(this.mBatteryInfoReceiver);
        getContentResolver().unregisterContentObserver(this.mRWCObserver);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_reverse_wireless_charging_settings;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.System.putIntForUser(getContentResolver(), "reverse_wireless_charging_status", ((Boolean) obj).booleanValue() ? 1 : 0, -2);
        return true;
    }
}
