package com.oneplus.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.internal.statusbar.IStatusBarService;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;

public class OPPowerOffControlPreferenceController extends BasePreferenceController implements LifecycleObserver, OnDestroy {
    private static final String KEY_POWER_OFF = "power_off";
    private IStatusBarService mBarService;
    private AlertDialog mDialog;
    private Preference mPreference;
    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        /* class com.oneplus.settings.OPPowerOffControlPreferenceController.AnonymousClass1 */

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                Log.d("OPPowerOffControlPreferenceController", "click dialog positive button");
                OPPowerOffControlPreferenceController.this.fireShutDown();
            } else if (i == -2) {
                Log.d("OPPowerOffControlPreferenceController", "click dialog negative button");
                OPPowerOffControlPreferenceController.this.mDialog.dismiss();
            }
        }
    };

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
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

    public OPPowerOffControlPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_POWER_OFF);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDialog = null;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_POWER_OFF.equals(preference.getKey())) {
            return false;
        }
        if (this.mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            builder.setTitle(C0017R$string.power_off_summary);
            builder.setPositiveButton(17039370, this.onClickListener);
            builder.setNegativeButton(17039360, this.onClickListener);
            this.mDialog = builder.create();
        }
        this.mDialog.show();
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void fireShutDown() {
        StringBuilder sb = new StringBuilder();
        sb.append("isdebug : ");
        sb.append(!SystemProperties.getBoolean("sys.debug.watchdog", false));
        Log.d("OPPowerOffControlPreferenceController", sb.toString());
        if (!SystemProperties.getBoolean("sys.debug.watchdog", false)) {
            try {
                Log.d("OPPowerOffControlPreferenceController", "now shutdown !!!!!!!!!!!");
                this.mBarService.shutdown();
            } catch (RemoteException unused) {
            }
        }
    }
}
