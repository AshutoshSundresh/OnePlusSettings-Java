package com.android.settings.development;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class HardwareOverlaysPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final int SURFACE_FLINGER_READ_CODE = 1010;
    private final IBinder mSurfaceFlinger = ServiceManager.getService("SurfaceFlinger");

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "disable_overlays";
    }

    public HardwareOverlaysPreferenceController(Context context) {
        super(context);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeHardwareOverlaysSetting(((Boolean) obj).booleanValue());
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateHardwareOverlaysSetting();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        SwitchPreference switchPreference = (SwitchPreference) this.mPreference;
        if (switchPreference.isChecked()) {
            writeHardwareOverlaysSetting(false);
            switchPreference.setChecked(false);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateHardwareOverlaysSetting() {
        if (this.mSurfaceFlinger != null) {
            try {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
                boolean z = false;
                this.mSurfaceFlinger.transact(SURFACE_FLINGER_READ_CODE, obtain, obtain2, 0);
                obtain2.readInt();
                obtain2.readInt();
                obtain2.readInt();
                obtain2.readInt();
                int readInt = obtain2.readInt();
                SwitchPreference switchPreference = (SwitchPreference) this.mPreference;
                if (readInt != 0) {
                    z = true;
                }
                switchPreference.setChecked(z);
                obtain2.recycle();
                obtain.recycle();
            } catch (RemoteException unused) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writeHardwareOverlaysSetting(boolean z) {
        if (this.mSurfaceFlinger != null) {
            try {
                Parcel obtain = Parcel.obtain();
                obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
                obtain.writeInt(z ? 1 : 0);
                this.mSurfaceFlinger.transact(1008, obtain, null, 0);
                obtain.recycle();
            } catch (RemoteException unused) {
            }
            updateHardwareOverlaysSetting();
        }
    }
}
