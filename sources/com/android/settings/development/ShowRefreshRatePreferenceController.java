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

public class ShowRefreshRatePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final int SURFACE_FLINGER_CODE = 1034;
    static final String SURFACE_FLINGER_SERVICE_KEY = "SurfaceFlinger";
    private final IBinder mSurfaceFlinger = ServiceManager.getService(SURFACE_FLINGER_SERVICE_KEY);

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "show_refresh_rate";
    }

    public ShowRefreshRatePreferenceController(Context context) {
        super(context);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeShowRefreshRateSetting(((Boolean) obj).booleanValue());
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateShowRefreshRateSetting();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        SwitchPreference switchPreference = (SwitchPreference) this.mPreference;
        if (switchPreference.isChecked()) {
            writeShowRefreshRateSetting(false);
            switchPreference.setChecked(false);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateShowRefreshRateSetting() {
        try {
            if (this.mSurfaceFlinger != null) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
                obtain.writeInt(2);
                this.mSurfaceFlinger.transact(SURFACE_FLINGER_CODE, obtain, obtain2, 0);
                ((SwitchPreference) this.mPreference).setChecked(obtain2.readBoolean());
                obtain2.recycle();
                obtain.recycle();
            }
        } catch (RemoteException unused) {
        }
    }

    /* access modifiers changed from: package-private */
    public void writeShowRefreshRateSetting(boolean z) {
        try {
            if (this.mSurfaceFlinger != null) {
                Parcel obtain = Parcel.obtain();
                obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
                obtain.writeInt(z ? 1 : 0);
                this.mSurfaceFlinger.transact(SURFACE_FLINGER_CODE, obtain, null, 0);
                obtain.recycle();
            }
        } catch (RemoteException unused) {
        }
        updateShowRefreshRateSetting();
    }
}
