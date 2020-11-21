package com.android.settings.development;

import android.content.Context;
import android.hardware.dumpstate.V1_0.IDumpstateDevice;
import android.os.RemoteException;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.util.NoSuchElementException;

public class EnableVerboseVendorLoggingPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private static final boolean DBG = Log.isLoggable("EnableVerboseVendorLoggingPreferenceController", 3);
    private int mDumpstateHalVersion = -1;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "enable_verbose_vendor_logging";
    }

    public EnableVerboseVendorLoggingPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return isIDumpstateDeviceV1_1ServiceAvailable();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        setVerboseLoggingEnabled(((Boolean) obj).booleanValue());
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(getVerboseLoggingEnabled());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        setVerboseLoggingEnabled(false);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    /* access modifiers changed from: package-private */
    public boolean isIDumpstateDeviceV1_1ServiceAvailable() {
        IDumpstateDevice dumpstateDeviceService = getDumpstateDeviceService();
        if (dumpstateDeviceService == null && DBG) {
            Log.d("EnableVerboseVendorLoggingPreferenceController", "IDumpstateDevice service is not available.");
        }
        if (dumpstateDeviceService == null || this.mDumpstateHalVersion < 1) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void setVerboseLoggingEnabled(boolean z) {
        IDumpstateDevice dumpstateDeviceService = getDumpstateDeviceService();
        if (dumpstateDeviceService != null && this.mDumpstateHalVersion >= 1) {
            try {
                android.hardware.dumpstate.V1_1.IDumpstateDevice iDumpstateDevice = (android.hardware.dumpstate.V1_1.IDumpstateDevice) dumpstateDeviceService;
                if (iDumpstateDevice != null) {
                    iDumpstateDevice.setVerboseLoggingEnabled(z);
                }
            } catch (RemoteException | RuntimeException e) {
                if (DBG) {
                    Log.e("EnableVerboseVendorLoggingPreferenceController", "setVerboseLoggingEnabled fail: " + e);
                }
            }
        } else if (DBG) {
            Log.d("EnableVerboseVendorLoggingPreferenceController", "setVerboseLoggingEnabled not supported.");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean getVerboseLoggingEnabled() {
        IDumpstateDevice dumpstateDeviceService = getDumpstateDeviceService();
        if (dumpstateDeviceService == null || this.mDumpstateHalVersion < 1) {
            if (DBG) {
                Log.d("EnableVerboseVendorLoggingPreferenceController", "getVerboseLoggingEnabled not supported.");
            }
            return false;
        }
        try {
            android.hardware.dumpstate.V1_1.IDumpstateDevice iDumpstateDevice = (android.hardware.dumpstate.V1_1.IDumpstateDevice) dumpstateDeviceService;
            if (iDumpstateDevice != null) {
                return iDumpstateDevice.getVerboseLoggingEnabled();
            }
        } catch (RemoteException | RuntimeException e) {
            if (DBG) {
                Log.e("EnableVerboseVendorLoggingPreferenceController", "getVerboseLoggingEnabled fail: " + e);
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public IDumpstateDevice getDumpstateDeviceService() {
        IDumpstateDevice iDumpstateDevice = null;
        try {
            iDumpstateDevice = android.hardware.dumpstate.V1_1.IDumpstateDevice.getService(true);
            this.mDumpstateHalVersion = 1;
        } catch (RemoteException | NoSuchElementException unused) {
        }
        if (iDumpstateDevice == null) {
            try {
                iDumpstateDevice = IDumpstateDevice.getService(true);
                this.mDumpstateHalVersion = 0;
            } catch (RemoteException | NoSuchElementException unused2) {
            }
        }
        if (iDumpstateDevice == null) {
            this.mDumpstateHalVersion = -1;
        }
        return iDumpstateDevice;
    }
}
