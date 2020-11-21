package com.android.settings.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.Utils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class LocationEnabler implements LifecycleObserver, OnStart, OnStop {
    static final IntentFilter INTENT_FILTER_LOCATION_MODE_CHANGED = new IntentFilter("android.location.MODE_CHANGED");
    private final Context mContext;
    private final LocationModeChangeListener mListener;
    BroadcastReceiver mReceiver;
    private final UserManager mUserManager;

    public interface LocationModeChangeListener {
        void onLocationModeChanged(int i, boolean z);
    }

    public LocationEnabler(Context context, LocationModeChangeListener locationModeChangeListener, Lifecycle lifecycle) {
        this.mContext = context;
        this.mListener = locationModeChangeListener;
        this.mUserManager = (UserManager) context.getSystemService("user");
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mReceiver == null) {
            this.mReceiver = new BroadcastReceiver() {
                /* class com.android.settings.location.LocationEnabler.AnonymousClass1 */

                public void onReceive(Context context, Intent intent) {
                    if (Log.isLoggable("LocationEnabler", 3)) {
                        Log.d("LocationEnabler", "Received location mode change intent: " + intent);
                    }
                    LocationEnabler.this.refreshLocationMode();
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

    /* access modifiers changed from: package-private */
    public void refreshLocationMode() {
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "location_mode", 0);
        if (Log.isLoggable("LocationEnabler", 4)) {
            Log.i("LocationEnabler", "Location mode has been changed");
        }
        LocationModeChangeListener locationModeChangeListener = this.mListener;
        if (locationModeChangeListener != null) {
            locationModeChangeListener.onLocationModeChanged(i, isRestricted());
        }
    }

    /* access modifiers changed from: package-private */
    public void setLocationEnabled(boolean z) {
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "location_mode", 0);
        if (isRestricted()) {
            if (Log.isLoggable("LocationEnabler", 4)) {
                Log.i("LocationEnabler", "Restricted user, not setting location mode");
            }
            LocationModeChangeListener locationModeChangeListener = this.mListener;
            if (locationModeChangeListener != null) {
                locationModeChangeListener.onLocationModeChanged(i, true);
                return;
            }
            return;
        }
        Utils.updateLocationEnabled(this.mContext, z, UserHandle.myUserId(), 1);
        refreshLocationMode();
    }

    /* access modifiers changed from: package-private */
    public boolean isEnabled(int i) {
        return i != 0 && !isRestricted();
    }

    /* access modifiers changed from: package-private */
    public boolean isManagedProfileRestrictedByBase() {
        UserHandle managedProfile = com.android.settings.Utils.getManagedProfile(this.mUserManager);
        return managedProfile != null && hasShareLocationRestriction(managedProfile.getIdentifier());
    }

    /* access modifiers changed from: package-private */
    public RestrictedLockUtils.EnforcedAdmin getShareLocationEnforcedAdmin(int i) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_share_location", i);
        return checkIfRestrictionEnforced == null ? RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_config_location", i) : checkIfRestrictionEnforced;
    }

    /* access modifiers changed from: package-private */
    public boolean hasShareLocationRestriction(int i) {
        return RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_share_location", i);
    }

    private boolean isRestricted() {
        return this.mUserManager.hasUserRestriction("no_share_location");
    }
}
