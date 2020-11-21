package com.android.settings.development.qstile;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.internal.statusbar.IStatusBarService;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class DevelopmentTilePreferenceController extends BasePreferenceController {
    private static final String TAG = "DevTilePrefController";
    private final OnChangeHandler mOnChangeHandler;
    private final PackageManager mPackageManager;

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

    public DevelopmentTilePreferenceController(Context context, String str) {
        super(context, str);
        this.mOnChangeHandler = new OnChangeHandler(context);
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Context context = preferenceScreen.getContext();
        for (ResolveInfo resolveInfo : this.mPackageManager.queryIntentServices(new Intent("android.service.quicksettings.action.QS_TILE").setPackage(context.getPackageName()), 512)) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            int componentEnabledSetting = this.mPackageManager.getComponentEnabledSetting(new ComponentName(serviceInfo.packageName, serviceInfo.name));
            boolean z = true;
            if (componentEnabledSetting != 1 && (componentEnabledSetting != 0 || !serviceInfo.enabled)) {
                z = false;
            }
            SwitchPreference switchPreference = new SwitchPreference(context);
            switchPreference.setTitle(serviceInfo.loadLabel(this.mPackageManager));
            switchPreference.setIcon(serviceInfo.icon);
            switchPreference.setKey(serviceInfo.name);
            switchPreference.setChecked(z);
            switchPreference.setOnPreferenceChangeListener(this.mOnChangeHandler);
            preferenceScreen.addPreference(switchPreference);
        }
    }

    static class OnChangeHandler implements Preference.OnPreferenceChangeListener {
        private final Context mContext;
        private final PackageManager mPackageManager;
        private IStatusBarService mStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.checkService("statusbar"));

        public OnChangeHandler(Context context) {
            this.mContext = context;
            this.mPackageManager = context.getPackageManager();
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            ComponentName componentName = new ComponentName(this.mContext.getPackageName(), preference.getKey());
            this.mPackageManager.setComponentEnabledSetting(componentName, booleanValue ? 1 : 2, 1);
            try {
                if (this.mStatusBarService != null) {
                    if (booleanValue) {
                        this.mStatusBarService.addTile(componentName);
                    } else {
                        this.mStatusBarService.remTile(componentName);
                    }
                }
            } catch (RemoteException e) {
                Log.e(DevelopmentTilePreferenceController.TAG, "Failed to modify QS tile for component " + componentName.toString(), e);
            }
            return true;
        }
    }
}
