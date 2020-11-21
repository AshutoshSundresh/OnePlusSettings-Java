package com.android.settings.applications.specialaccess.deviceadmin;

import android.app.AppGlobals;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.FilterTouchesSwitchPreference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.widget.FooterPreference;
import com.oneplus.settings.utils.OPUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class DeviceAdminListPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final IntentFilter FILTER;
    private static final String KEY_DEVICE_ADMIN_FOOTER = "device_admin_footer";
    private static final String TAG = "DeviceAdminListPrefCtrl";
    private final ArrayList<DeviceAdminListItem> mAdmins = new ArrayList<>();
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        /* class com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminListPreferenceController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED", intent.getAction())) {
                DeviceAdminListPreferenceController.this.updateList();
            }
        }
    };
    private final DevicePolicyManager mDPM;
    private FooterPreference mFooterPreference;
    private final IPackageManager mIPackageManager;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final PackageManager mPackageManager;
    private PreferenceGroup mPreferenceGroup;
    private final SparseArray<ComponentName> mProfileOwnerComponents = new SparseArray<>();
    private final UserManager mUm;
    protected long[] mVibratePattern;
    protected Vibrator mVibrator;

    static /* synthetic */ boolean lambda$bindPreference$1(Preference preference, Object obj) {
        return false;
    }

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

    static {
        IntentFilter intentFilter = new IntentFilter();
        FILTER = intentFilter;
        intentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
    }

    public DeviceAdminListPreferenceController(Context context, String str) {
        super(context, str);
        this.mDPM = (DevicePolicyManager) context.getSystemService("device_policy");
        this.mUm = (UserManager) context.getSystemService("user");
        this.mPackageManager = this.mContext.getPackageManager();
        this.mIPackageManager = AppGlobals.getPackageManager();
        if (OPUtils.isSupportXVibrate()) {
            this.mVibrator = (Vibrator) context.getSystemService("vibrator");
        }
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceGroup = preferenceGroup;
        this.mFooterPreference = (FooterPreference) preferenceGroup.findPreference(KEY_DEVICE_ADMIN_FOOTER);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, FILTER, null, null);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mProfileOwnerComponents.clear();
        List<UserHandle> userProfiles = this.mUm.getUserProfiles();
        int size = userProfiles.size();
        for (int i = 0; i < size; i++) {
            int identifier = userProfiles.get(i).getIdentifier();
            this.mProfileOwnerComponents.put(identifier, this.mDPM.getProfileOwnerAsUser(identifier));
        }
        updateList();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
    }

    /* access modifiers changed from: package-private */
    public void updateList() {
        refreshData();
        refreshUI();
    }

    private void refreshData() {
        this.mAdmins.clear();
        for (UserHandle userHandle : this.mUm.getUserProfiles()) {
            updateAvailableAdminsForProfile(userHandle.getIdentifier());
        }
        Collections.sort(this.mAdmins);
    }

    private void refreshUI() {
        if (this.mPreferenceGroup != null) {
            FooterPreference footerPreference = this.mFooterPreference;
            if (footerPreference != null) {
                footerPreference.setVisible(this.mAdmins.isEmpty());
            }
            ArrayMap arrayMap = new ArrayMap();
            Context context = this.mPreferenceGroup.getContext();
            int preferenceCount = this.mPreferenceGroup.getPreferenceCount();
            for (int i = 0; i < preferenceCount; i++) {
                Preference preference = this.mPreferenceGroup.getPreference(i);
                if (preference instanceof FilterTouchesSwitchPreference) {
                    FilterTouchesSwitchPreference filterTouchesSwitchPreference = (FilterTouchesSwitchPreference) preference;
                    arrayMap.put(filterTouchesSwitchPreference.getKey(), filterTouchesSwitchPreference);
                }
            }
            Iterator<DeviceAdminListItem> it = this.mAdmins.iterator();
            while (it.hasNext()) {
                DeviceAdminListItem next = it.next();
                FilterTouchesSwitchPreference filterTouchesSwitchPreference2 = (FilterTouchesSwitchPreference) arrayMap.remove(next.getKey());
                if (filterTouchesSwitchPreference2 == null) {
                    filterTouchesSwitchPreference2 = new FilterTouchesSwitchPreference(context);
                    this.mPreferenceGroup.addPreference(filterTouchesSwitchPreference2);
                }
                bindPreference(next, filterTouchesSwitchPreference2);
            }
            for (FilterTouchesSwitchPreference filterTouchesSwitchPreference3 : arrayMap.values()) {
                this.mPreferenceGroup.removePreference(filterTouchesSwitchPreference3);
            }
        }
    }

    private void bindPreference(DeviceAdminListItem deviceAdminListItem, FilterTouchesSwitchPreference filterTouchesSwitchPreference) {
        filterTouchesSwitchPreference.setKey(deviceAdminListItem.getKey());
        filterTouchesSwitchPreference.setTitle(deviceAdminListItem.getName());
        filterTouchesSwitchPreference.setIcon(deviceAdminListItem.getIcon());
        filterTouchesSwitchPreference.setChecked(deviceAdminListItem.isActive());
        filterTouchesSwitchPreference.setSummary(deviceAdminListItem.getDescription());
        filterTouchesSwitchPreference.setEnabled(deviceAdminListItem.isEnabled());
        filterTouchesSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(deviceAdminListItem) {
            /* class com.android.settings.applications.specialaccess.deviceadmin.$$Lambda$DeviceAdminListPreferenceController$xq7jFVPzZr40edp_AZG9ENMKlVs */
            public final /* synthetic */ DeviceAdminListItem f$1;

            {
                this.f$1 = r2;
            }

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return DeviceAdminListPreferenceController.this.lambda$bindPreference$0$DeviceAdminListPreferenceController(this.f$1, preference);
            }
        });
        filterTouchesSwitchPreference.setOnPreferenceChangeListener($$Lambda$DeviceAdminListPreferenceController$km1xGgD4wmcfa_hK12gqjXdyDbw.INSTANCE);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$bindPreference$0 */
    public /* synthetic */ boolean lambda$bindPreference$0$DeviceAdminListPreferenceController(DeviceAdminListItem deviceAdminListItem, Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, getMetricsCategory());
        UserHandle user = deviceAdminListItem.getUser();
        Context context = this.mContext;
        context.startActivityAsUser(deviceAdminListItem.getLaunchIntent(context), user);
        return true;
    }

    private void updateAvailableAdminsForProfile(int i) {
        List<ComponentName> activeAdminsAsUser = this.mDPM.getActiveAdminsAsUser(i);
        addActiveAdminsForProfile(activeAdminsAsUser, i);
        addDeviceAdminBroadcastReceiversForProfile(activeAdminsAsUser, i);
    }

    private void addActiveAdminsForProfile(List<ComponentName> list, int i) {
        if (list != null) {
            for (ComponentName componentName : list) {
                try {
                    DeviceAdminInfo createDeviceAdminInfo = createDeviceAdminInfo(this.mContext, this.mIPackageManager.getReceiverInfo(componentName, 819328, i));
                    if (!(createDeviceAdminInfo == null || getUserId(createDeviceAdminInfo) == 999)) {
                        this.mAdmins.add(new DeviceAdminListItem(this.mContext, createDeviceAdminInfo));
                    }
                } catch (RemoteException unused) {
                    Log.w(TAG, "Unable to load component: " + componentName);
                }
            }
        }
    }

    private void addDeviceAdminBroadcastReceiversForProfile(Collection<ComponentName> collection, int i) {
        DeviceAdminInfo createDeviceAdminInfo;
        List<ResolveInfo> queryBroadcastReceiversAsUser = this.mPackageManager.queryBroadcastReceiversAsUser(new Intent("android.app.action.DEVICE_ADMIN_ENABLED"), 32896, i);
        if (queryBroadcastReceiversAsUser != null) {
            for (ResolveInfo resolveInfo : queryBroadcastReceiversAsUser) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                ComponentName componentName = new ComponentName(activityInfo.packageName, activityInfo.name);
                if ((collection == null || !collection.contains(componentName)) && (createDeviceAdminInfo = createDeviceAdminInfo(this.mContext, resolveInfo.activityInfo)) != null && createDeviceAdminInfo.isVisible() && createDeviceAdminInfo.getActivityInfo().applicationInfo.isInternal() && getUserId(createDeviceAdminInfo) != 999) {
                    this.mAdmins.add(new DeviceAdminListItem(this.mContext, createDeviceAdminInfo));
                }
            }
        }
    }

    private int getUserId(DeviceAdminInfo deviceAdminInfo) {
        return UserHandle.getUserId(deviceAdminInfo.getActivityInfo().applicationInfo.uid);
    }

    private static DeviceAdminInfo createDeviceAdminInfo(Context context, ActivityInfo activityInfo) {
        try {
            return new DeviceAdminInfo(context, activityInfo);
        } catch (IOException | XmlPullParserException e) {
            Log.w(TAG, "Skipping " + activityInfo, e);
            return null;
        }
    }
}
