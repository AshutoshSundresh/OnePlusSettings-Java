package com.android.settings.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.FeatureFlagUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.TetherUtil;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.util.concurrent.atomic.AtomicReference;

public class TetherPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnCreate, OnResume, OnPause, OnDestroy {
    private final boolean mAdminDisallowedTetherConfig;
    private SettingObserver mAirplaneModeObserver;
    private final BluetoothAdapter mBluetoothAdapter;
    private final AtomicReference<BluetoothPan> mBluetoothPan;
    final BluetoothProfile.ServiceListener mBtProfileServiceListener;
    private final ConnectivityManager mConnectivityManager;
    private Preference mPreference;
    private TetherBroadcastReceiver mTetherReceiver;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "tether_settings";
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
    }

    TetherPreferenceController() {
        super(null);
        this.mBtProfileServiceListener = new BluetoothProfile.ServiceListener() {
            /* class com.android.settings.network.TetherPreferenceController.AnonymousClass1 */

            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                TetherPreferenceController.this.mBluetoothPan.set((BluetoothPan) bluetoothProfile);
                TetherPreferenceController.this.updateSummary();
            }

            public void onServiceDisconnected(int i) {
                BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothProfile bluetoothProfile = (BluetoothProfile) TetherPreferenceController.this.mBluetoothPan.getAndSet(null);
                if (bluetoothProfile != null && defaultAdapter != null) {
                    defaultAdapter.closeProfileProxy(5, bluetoothProfile);
                }
            }
        };
        this.mAdminDisallowedTetherConfig = false;
        this.mBluetoothPan = new AtomicReference<>();
        this.mConnectivityManager = null;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public TetherPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mBtProfileServiceListener = new BluetoothProfile.ServiceListener() {
            /* class com.android.settings.network.TetherPreferenceController.AnonymousClass1 */

            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                TetherPreferenceController.this.mBluetoothPan.set((BluetoothPan) bluetoothProfile);
                TetherPreferenceController.this.updateSummary();
            }

            public void onServiceDisconnected(int i) {
                BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothProfile bluetoothProfile = (BluetoothProfile) TetherPreferenceController.this.mBluetoothPan.getAndSet(null);
                if (bluetoothProfile != null && defaultAdapter != null) {
                    defaultAdapter.closeProfileProxy(5, bluetoothProfile);
                }
            }
        };
        this.mBluetoothPan = new AtomicReference<>();
        this.mAdminDisallowedTetherConfig = isTetherConfigDisallowed(context);
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("tether_settings");
        this.mPreference = findPreference;
        if (findPreference != null && !this.mAdminDisallowedTetherConfig) {
            findPreference.setTitle(Utils.getTetheringLabel(this.mConnectivityManager));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return TetherUtil.isTetherAvailable(this.mContext) && !FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateSummary();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter != null && bluetoothAdapter.getState() == 12 && this.mBluetoothPan.get() == null) {
            this.mBluetoothAdapter.getProfileProxy(this.mContext, this.mBtProfileServiceListener, 5);
        }
        if (this.mAirplaneModeObserver == null) {
            this.mAirplaneModeObserver = new SettingObserver();
        }
        if (this.mTetherReceiver == null) {
            this.mTetherReceiver = new TetherBroadcastReceiver();
        }
        this.mContext.registerReceiver(this.mTetherReceiver, new IntentFilter("android.net.conn.TETHER_STATE_CHANGED"));
        ContentResolver contentResolver = this.mContext.getContentResolver();
        SettingObserver settingObserver = this.mAirplaneModeObserver;
        contentResolver.registerContentObserver(settingObserver.uri, false, settingObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        BluetoothAdapter bluetoothAdapter;
        BluetoothProfile andSet = this.mBluetoothPan.getAndSet(null);
        if (!(andSet == null || (bluetoothAdapter = this.mBluetoothAdapter) == null)) {
            bluetoothAdapter.closeProfileProxy(5, andSet);
        }
        if (this.mAirplaneModeObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mAirplaneModeObserver);
        }
        TetherBroadcastReceiver tetherBroadcastReceiver = this.mTetherReceiver;
        if (tetherBroadcastReceiver != null) {
            this.mContext.unregisterReceiver(tetherBroadcastReceiver);
        }
    }

    public static boolean isTetherConfigDisallowed(Context context) {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, "no_config_tethering", UserHandle.myUserId()) != null;
    }

    /* access modifiers changed from: package-private */
    public void updateSummary() {
        boolean z;
        boolean z2;
        BluetoothAdapter bluetoothAdapter;
        if (this.mPreference != null) {
            String[] tetheredIfaces = this.mConnectivityManager.getTetheredIfaces();
            String[] tetherableWifiRegexs = this.mConnectivityManager.getTetherableWifiRegexs();
            String[] tetherableBluetoothRegexs = this.mConnectivityManager.getTetherableBluetoothRegexs();
            boolean z3 = false;
            if (tetheredIfaces != null) {
                if (tetherableWifiRegexs != null) {
                    z = false;
                    for (String str : tetheredIfaces) {
                        int length = tetherableWifiRegexs.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            } else if (str.matches(tetherableWifiRegexs[i])) {
                                z = true;
                                break;
                            } else {
                                i++;
                            }
                        }
                    }
                } else {
                    z = false;
                }
                z2 = tetheredIfaces.length > 1 ? true : tetheredIfaces.length == 1 ? !z : false;
            } else {
                z2 = false;
                z = false;
            }
            if (!z2 && tetherableBluetoothRegexs != null && tetherableBluetoothRegexs.length > 0 && (bluetoothAdapter = this.mBluetoothAdapter) != null && bluetoothAdapter.getState() == 12) {
                BluetoothPan bluetoothPan = this.mBluetoothPan.get();
                if (bluetoothPan != null && bluetoothPan.isTetheringOn()) {
                    z3 = true;
                }
                z2 = z3;
            }
            if (!z && !z2) {
                this.mPreference.setSummary(C0017R$string.switch_off_text);
            } else if (z && z2) {
                this.mPreference.setSummary(C0017R$string.tether_settings_summary_hotspot_on_tether_on);
            } else if (z) {
                this.mPreference.setSummary(C0017R$string.tether_settings_summary_hotspot_on_tether_off);
            } else {
                this.mPreference.setSummary(C0017R$string.tether_settings_summary_hotspot_off_tether_on);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSummaryToOff() {
        Preference preference = this.mPreference;
        if (preference != null) {
            preference.setSummary(C0017R$string.switch_off_text);
        }
    }

    class SettingObserver extends ContentObserver {
        public final Uri uri = Settings.Global.getUriFor("airplane_mode_on");

        public SettingObserver() {
            super(new Handler());
        }

        public void onChange(boolean z, Uri uri2) {
            super.onChange(z, uri2);
            if (this.uri.equals(uri2)) {
                boolean z2 = false;
                if (Settings.Global.getInt(((AbstractPreferenceController) TetherPreferenceController.this).mContext.getContentResolver(), "airplane_mode_on", 0) != 0) {
                    z2 = true;
                }
                if (z2) {
                    TetherPreferenceController.this.updateSummaryToOff();
                }
            }
        }
    }

    class TetherBroadcastReceiver extends BroadcastReceiver {
        TetherBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            TetherPreferenceController.this.updateSummary();
        }
    }
}
