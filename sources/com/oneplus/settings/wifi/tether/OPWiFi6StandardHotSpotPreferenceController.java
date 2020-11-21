package com.oneplus.settings.wifi.tether;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.oneplus.settings.utils.OPUtils;

public class OPWiFi6StandardHotSpotPreferenceController extends WifiTetherBasePreferenceController implements OnDestroy {
    public static final String KEY_WIFI_6_STANDARD_HOTSPOT_TITLE = "wifi_6_standard_hotspot_title";
    SwitchPreference enableSwitch;
    private AlertDialog mDialog;

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_WIFI_6_STANDARD_HOTSPOT_TITLE;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPWiFi6StandardHotSpotPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener, KEY_WIFI_6_STANDARD_HOTSPOT_TITLE);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public int getAvailabilityStatus() {
        return OPUtils.isSM8250Products() ? 0 : 3;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        this.enableSwitch = (SwitchPreference) this.mPreference;
        boolean z = false;
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "softap_enable_11ax", 0) == 1) {
            z = true;
        }
        this.enableSwitch.setChecked(z);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener, com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (((Boolean) obj).booleanValue()) {
            setSoftApEnable11AX((SwitchPreference) preference);
            return true;
        }
        Settings.Global.putInt(this.mContext.getContentResolver(), "softap_enable_11ax", 0);
        OPUtils.sendAnalytics("wifi6", "status", "off");
        this.mListener.onTetherConfigUpdated(this);
        return true;
    }

    private void setSoftApEnable11AX(final SwitchPreference switchPreference) {
        AnonymousClass1 r0 = new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.wifi.tether.OPWiFi6StandardHotSpotPreferenceController.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    Settings.Global.putInt(((AbstractPreferenceController) OPWiFi6StandardHotSpotPreferenceController.this).mContext.getContentResolver(), "softap_enable_11ax", 1);
                    ((WifiTetherBasePreferenceController) OPWiFi6StandardHotSpotPreferenceController.this).mListener.onTetherConfigUpdated(OPWiFi6StandardHotSpotPreferenceController.this);
                    OPUtils.sendAnalytics("wifi6", "status", "on");
                } else if (i == -2) {
                    switchPreference.setChecked(false);
                }
            }
        };
        AnonymousClass2 r1 = new DialogInterface.OnDismissListener() {
            /* class com.oneplus.settings.wifi.tether.OPWiFi6StandardHotSpotPreferenceController.AnonymousClass2 */

            public void onDismiss(DialogInterface dialogInterface) {
                SwitchPreference switchPreference = switchPreference;
                boolean z = false;
                if (Settings.Global.getInt(((AbstractPreferenceController) OPWiFi6StandardHotSpotPreferenceController.this).mContext.getContentResolver(), "softap_enable_11ax", 0) != 0) {
                    z = true;
                }
                switchPreference.setChecked(z);
            }
        };
        if (this.mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            builder.setTitle(C0017R$string.oneplus_wifi_6_standard_hotspot_warning);
            builder.setPositiveButton(C0017R$string.oneplus_wifi_6_standard_hotspot_enable, r0);
            builder.setNegativeButton(C0017R$string.oneplus_wifi_6_standard_hotspot_cancel, r0);
            builder.setOnDismissListener(r1);
            this.mDialog = builder.create();
        }
        this.mDialog.show();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDialog = null;
        }
    }
}
