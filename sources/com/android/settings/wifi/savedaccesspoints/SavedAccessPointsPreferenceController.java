package com.android.settings.wifi.savedaccesspoints;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPointPreference;
import com.android.settingslib.wifi.WifiSavedConfigUtils;
import java.util.List;
import java.util.stream.Collectors;

public class SavedAccessPointsPreferenceController extends BasePreferenceController implements Preference.OnPreferenceClickListener {
    List<AccessPoint> mAccessPoints;
    private SavedAccessPointsWifiSettings mHost;
    private PreferenceGroup mPreferenceGroup;
    private final AccessPointPreference.UserBadgeCache mUserBadgeCache;
    protected final WifiManager mWifiManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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

    public SavedAccessPointsPreferenceController(Context context, String str) {
        super(context, str);
        this.mUserBadgeCache = new AccessPointPreference.UserBadgeCache(context.getPackageManager());
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
    }

    public SavedAccessPointsPreferenceController setHost(SavedAccessPointsWifiSettings savedAccessPointsWifiSettings) {
        this.mHost = savedAccessPointsWifiSettings;
        return this;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        refreshSavedAccessPoints();
        return this.mAccessPoints.size() > 0 ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        refreshSavedAccessPoints();
        updatePreference();
        super.displayPreference(preferenceScreen);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (this.mHost == null) {
            return false;
        }
        this.mHost.showWifiPage((AccessPointPreference) this.mPreferenceGroup.findPreference(preference.getKey()));
        return false;
    }

    /* access modifiers changed from: protected */
    public void refreshSavedAccessPoints() {
        this.mAccessPoints = (List) WifiSavedConfigUtils.getAllConfigs(this.mContext, this.mWifiManager).stream().filter($$Lambda$SavedAccessPointsPreferenceController$RMhZcqBmN7Sfb8TDyzEEsGMXe08.INSTANCE).sorted(SavedNetworkComparator.INSTANCE).collect(Collectors.toList());
    }

    static /* synthetic */ boolean lambda$refreshSavedAccessPoints$0(AccessPoint accessPoint) {
        return !accessPoint.isPasspointConfig();
    }

    private void updatePreference() {
        this.mPreferenceGroup.removeAll();
        for (AccessPoint accessPoint : this.mAccessPoints) {
            String key = accessPoint.getKey();
            AccessPointPreference accessPointPreference = new AccessPointPreference(accessPoint, this.mContext, this.mUserBadgeCache, true);
            accessPointPreference.setKey(key);
            accessPointPreference.setIcon((Drawable) null);
            accessPointPreference.setOnPreferenceClickListener(this);
            this.mPreferenceGroup.addPreference(accessPointPreference);
        }
    }
}
