package com.android.settings.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.permission.PermissionControllerManager;
import androidx.preference.Preference;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TopLevelLocationPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final IntentFilter INTENT_FILTER_LOCATION_MODE_CHANGED = new IntentFilter("android.location.MODE_CHANGED");
    private AtomicInteger loadingInProgress = new AtomicInteger(0);
    private final LocationManager mLocationManager;
    private int mNumTotal = -1;
    private int mNumTotalLoading = 0;
    private Preference mPreference;
    private BroadcastReceiver mReceiver;

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

    public TopLevelLocationPreferenceController(Context context, String str) {
        super(context, str);
        this.mLocationManager = (LocationManager) context.getSystemService("location");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (!this.mLocationManager.isLocationEnabled()) {
            return this.mContext.getString(C0017R$string.location_settings_summary_location_off);
        }
        if (this.mNumTotal == -1) {
            return this.mContext.getString(C0017R$string.location_settings_loading_app_permission_stats);
        }
        Resources resources = this.mContext.getResources();
        int i = C0015R$plurals.op_location_settings_summary_location_on;
        int i2 = this.mNumTotal;
        return resources.getQuantityString(i, i2, Integer.valueOf(i2));
    }

    /* access modifiers changed from: package-private */
    public void setLocationAppCount(int i) {
        this.mNumTotal = i;
        refreshSummary(this.mPreference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference = preference;
        refreshSummary(preference);
        if (this.mLocationManager.isLocationEnabled() && this.loadingInProgress.get() == 0) {
            this.mNumTotalLoading = 0;
            List<UserHandle> userProfiles = ((UserManager) this.mContext.getSystemService(UserManager.class)).getUserProfiles();
            this.loadingInProgress.set(userProfiles.size());
            for (UserHandle userHandle : userProfiles) {
                Context createPackageContextAsUser = Utils.createPackageContextAsUser(this.mContext, userHandle.getIdentifier());
                if (createPackageContextAsUser != null) {
                    ((PermissionControllerManager) createPackageContextAsUser.getSystemService(PermissionControllerManager.class)).countPermissionApps(Arrays.asList("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"), 1, new PermissionControllerManager.OnCountPermissionAppsResultCallback() {
                        /* class com.android.settings.location.$$Lambda$TopLevelLocationPreferenceController$ndKcHy_TjWWkiukewrb7I3jrOBk */

                        public final void onCountPermissionApps(int i) {
                            TopLevelLocationPreferenceController.this.lambda$updateState$0$TopLevelLocationPreferenceController(i);
                        }
                    }, (Handler) null);
                } else if (this.loadingInProgress.decrementAndGet() == 0) {
                    setLocationAppCount(this.mNumTotalLoading);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateState$0 */
    public /* synthetic */ void lambda$updateState$0$TopLevelLocationPreferenceController(int i) {
        this.mNumTotalLoading += i;
        if (this.loadingInProgress.decrementAndGet() == 0) {
            setLocationAppCount(this.mNumTotalLoading);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mReceiver == null) {
            this.mReceiver = new BroadcastReceiver() {
                /* class com.android.settings.location.TopLevelLocationPreferenceController.AnonymousClass1 */

                public void onReceive(Context context, Intent intent) {
                    TopLevelLocationPreferenceController.this.refreshLocationMode();
                }
            };
        }
        this.mContext.registerReceiver(this.mReceiver, INTENT_FILTER_LOCATION_MODE_CHANGED);
        refreshLocationMode();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshLocationMode() {
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }
}
