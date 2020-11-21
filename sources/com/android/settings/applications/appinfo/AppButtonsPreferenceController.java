package com.android.settings.applications.appinfo;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.om.OverlayInfo;
import android.content.om.OverlayManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0003R$array;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.applications.appinfo.ButtonActionDialogFragment;
import com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.instrumentation.SettingsStatsLog;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.ActionButtonsPreference;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;
import java.util.ArrayList;
import java.util.HashSet;

public class AppButtonsPreferenceController extends BasePreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnResume, OnDestroy, ApplicationsState.Callbacks {
    private static final String ACTION_QUICK_SETTING_TILE = "com.oneplus.systemui.qs.hide_tile";
    public static final String APP_CHG = "chg";
    private static final String BRICK_MODE_SERVICE_TILE = "custom(com.oneplus.brickmode/.service.BreathModeTileService)";
    private static final String KEY_ACTION_BUTTONS = "action_buttons";
    private static final String KEY_GOOGLE__INPUTMETHOD = "com.google.android.inputmethod.latin";
    private static final String KEY_LATIN_INPUTMETHOD = "com.android.inputmethod.latin";
    private static final String KEY_MARKET = "com.heytap.market";
    public static final String KEY_REMOVE_TASK_WHEN_FINISHING = "remove_task_when_finishing";
    private static final boolean LOCAL_LOGV = false;
    private static final String PACKAGENAME_BRICKMODE = "com.oneplus.brickmode";
    private static final String PACKAGENAME_FAST_APPLICATION = "com.nearme.instant.platform";
    private static final String PACKAGENAME_HEYTAP_CLOUD = "com.heytap.cloud";
    private static final String PACKAGENAME_MOBILE_INSTALLER = "com.sprint.ce.updater";
    private static final String PACKAGENAME_WALLPAPERRESOURCES = "net.oneplus.wallpaperresources";
    private static final String TAG = "AppButtonsPrefCtl";
    private static final int TILE_LOCATION = 16;
    private boolean mAccessedFromAutoRevoke;
    private final SettingsActivity mActivity;
    ApplicationsState.AppEntry mAppEntry;
    private Intent mAppLaunchIntent;
    private final ApplicationFeatureProvider mApplicationFeatureProvider;
    private RestrictedLockUtils.EnforcedAdmin mAppsControlDisallowedAdmin;
    private boolean mAppsControlDisallowedBySystem;
    ActionButtonsPreference mButtonsPref;
    private final BroadcastReceiver mCheckKillProcessesReceiver;
    boolean mDisableAfterUninstall;
    private final DevicePolicyManager mDpm;
    private boolean mFinishing;
    private final InstrumentedPreferenceFragment mFragment;
    final HashSet<String> mHomePackages = new HashSet<>();
    private boolean mListeningToPackageRemove;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final OverlayManager mOverlayManager;
    PackageInfo mPackageInfo;
    String mPackageName;
    private final BroadcastReceiver mPackageRemovedReceiver;
    private final PackageManager mPm;
    private final int mRequestRemoveDeviceAdmin;
    private final int mRequestUninstall;
    private PreferenceScreen mScreen;
    private ApplicationsState.Session mSession;
    private long mSessionId;
    ApplicationsState mState;
    private boolean mUpdatedSysApp;
    private final int mUserId;
    private final UserManager mUserManager;

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

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_ACTION_BUTTONS;
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

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onAllSizesComputed() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLauncherInfoChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppButtonsPreferenceController(SettingsActivity settingsActivity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, Lifecycle lifecycle, String str, ApplicationsState applicationsState, int i, int i2) {
        super(settingsActivity, KEY_ACTION_BUTTONS);
        boolean z = LOCAL_LOGV;
        this.mDisableAfterUninstall = LOCAL_LOGV;
        this.mUpdatedSysApp = LOCAL_LOGV;
        this.mListeningToPackageRemove = LOCAL_LOGV;
        this.mFinishing = LOCAL_LOGV;
        this.mCheckKillProcessesReceiver = new BroadcastReceiver() {
            /* class com.android.settings.applications.appinfo.AppButtonsPreferenceController.AnonymousClass1 */

            public void onReceive(Context context, Intent intent) {
                boolean z = getResultCode() != 0 ? true : AppButtonsPreferenceController.LOCAL_LOGV;
                Log.d(AppButtonsPreferenceController.TAG, "Got broadcast response: Restart status for " + AppButtonsPreferenceController.this.mAppEntry.info.packageName + " " + z);
                AppButtonsPreferenceController.this.updateForceStopButtonInner(z);
            }
        };
        this.mPackageRemovedReceiver = new BroadcastReceiver() {
            /* class com.android.settings.applications.appinfo.AppButtonsPreferenceController.AnonymousClass2 */

            public void onReceive(Context context, Intent intent) {
                String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
                if (!AppButtonsPreferenceController.this.mFinishing && AppButtonsPreferenceController.this.mAppEntry.info.packageName.equals(schemeSpecificPart)) {
                    AppButtonsPreferenceController.this.mActivity.finishAndRemoveTask();
                }
            }
        };
        if (instrumentedPreferenceFragment instanceof ButtonActionDialogFragment.AppButtonsDialogListener) {
            FeatureFactory factory = FeatureFactory.getFactory(settingsActivity);
            this.mMetricsFeatureProvider = factory.getMetricsFeatureProvider();
            this.mApplicationFeatureProvider = factory.getApplicationFeatureProvider(settingsActivity);
            this.mState = applicationsState;
            this.mDpm = (DevicePolicyManager) settingsActivity.getSystemService("device_policy");
            this.mUserManager = (UserManager) settingsActivity.getSystemService("user");
            this.mPm = settingsActivity.getPackageManager();
            this.mOverlayManager = (OverlayManager) settingsActivity.getSystemService(OverlayManager.class);
            this.mPackageName = str;
            this.mActivity = settingsActivity;
            this.mFragment = instrumentedPreferenceFragment;
            this.mUserId = UserHandle.myUserId();
            this.mRequestUninstall = i;
            this.mRequestRemoveDeviceAdmin = i2;
            this.mAppLaunchIntent = this.mPm.getLaunchIntentForPackage(this.mPackageName);
            long longExtra = settingsActivity.getIntent().getLongExtra("android.intent.action.AUTO_REVOKE_PERMISSIONS", 0);
            this.mSessionId = longExtra;
            this.mAccessedFromAutoRevoke = longExtra != 0 ? true : z;
            if (str != null) {
                this.mAppEntry = this.mState.getEntry(str, this.mUserId);
                this.mSession = this.mState.newSession(this, lifecycle);
                lifecycle.addObserver(this);
                return;
            }
            this.mFinishing = true;
            return;
        }
        throw new IllegalArgumentException("Fragment should implement AppButtonsDialogListener");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mFinishing || isInstantApp() || isSystemModule()) ? 4 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
        if (isAvailable()) {
            initButtonPreference();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            this.mAppsControlDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mActivity, "no_control_apps", this.mUserId);
            this.mAppsControlDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mActivity, "no_control_apps", this.mUserId);
            if (!refreshUi()) {
                setIntentAndFinish(true, LOCAL_LOGV);
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        stopListeningToPackageRemove();
    }

    /* access modifiers changed from: private */
    public class UninstallAndDisableButtonListener implements View.OnClickListener {
        private UninstallAndDisableButtonListener() {
        }

        public void onClick(View view) {
            if (AppButtonsPreferenceController.this.mAccessedFromAutoRevoke) {
                Log.i(AppButtonsPreferenceController.TAG, "sessionId: " + AppButtonsPreferenceController.this.mSessionId + " uninstalling " + AppButtonsPreferenceController.this.mPackageName + " with uid " + AppButtonsPreferenceController.this.getUid() + ", reached from auto revoke");
                SettingsStatsLog.write(272, AppButtonsPreferenceController.this.mSessionId, AppButtonsPreferenceController.this.getUid(), AppButtonsPreferenceController.this.mPackageName, 5);
            }
            AppButtonsPreferenceController appButtonsPreferenceController = AppButtonsPreferenceController.this;
            String str = appButtonsPreferenceController.mAppEntry.info.packageName;
            if (appButtonsPreferenceController.mDpm.packageHasActiveAdmins(AppButtonsPreferenceController.this.mPackageInfo.packageName)) {
                AppButtonsPreferenceController.this.stopListeningToPackageRemove();
                Intent intent = new Intent(AppButtonsPreferenceController.this.mActivity, DeviceAdminAdd.class);
                intent.putExtra("android.app.extra.DEVICE_ADMIN_PACKAGE_NAME", str);
                AppButtonsPreferenceController.this.mMetricsFeatureProvider.action(AppButtonsPreferenceController.this.mActivity, 873, new Pair[0]);
                AppButtonsPreferenceController.this.mFragment.startActivityForResult(intent, AppButtonsPreferenceController.this.mRequestRemoveDeviceAdmin);
                return;
            }
            RestrictedLockUtils.EnforcedAdmin checkIfUninstallBlocked = RestrictedLockUtilsInternal.checkIfUninstallBlocked(AppButtonsPreferenceController.this.mActivity, str, AppButtonsPreferenceController.this.mUserId);
            boolean z = AppButtonsPreferenceController.this.mAppsControlDisallowedBySystem || RestrictedLockUtilsInternal.hasBaseUserRestriction(AppButtonsPreferenceController.this.mActivity, str, AppButtonsPreferenceController.this.mUserId);
            if (checkIfUninstallBlocked == null || z) {
                AppButtonsPreferenceController appButtonsPreferenceController2 = AppButtonsPreferenceController.this;
                ApplicationInfo applicationInfo = appButtonsPreferenceController2.mAppEntry.info;
                int i = applicationInfo.flags;
                if ((i & 1) != 0) {
                    if (!applicationInfo.enabled || appButtonsPreferenceController2.isDisabledUntilUsed()) {
                        AppButtonsPreferenceController.this.mMetricsFeatureProvider.action(AppButtonsPreferenceController.this.mActivity, AppButtonsPreferenceController.this.mAppEntry.info.enabled ? 874 : 875, new Pair[0]);
                        AppButtonsPreferenceController appButtonsPreferenceController3 = AppButtonsPreferenceController.this;
                        AsyncTask.execute(new DisableChangerRunnable(appButtonsPreferenceController3.mPm, AppButtonsPreferenceController.this.mAppEntry.info.packageName, 0));
                    } else if (!AppButtonsPreferenceController.this.mUpdatedSysApp || !AppButtonsPreferenceController.this.isSingleUser()) {
                        AppButtonsPreferenceController.this.showDialogInner(0);
                    } else {
                        AppButtonsPreferenceController.this.showDialogInner(1);
                    }
                } else if ((8388608 & i) == 0) {
                    appButtonsPreferenceController2.uninstallPkg(str, true, AppButtonsPreferenceController.LOCAL_LOGV);
                } else {
                    appButtonsPreferenceController2.uninstallPkg(str, AppButtonsPreferenceController.LOCAL_LOGV, AppButtonsPreferenceController.LOCAL_LOGV);
                }
            } else {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(AppButtonsPreferenceController.this.mActivity, checkIfUninstallBlocked);
            }
        }
    }

    /* access modifiers changed from: private */
    public class ForceStopButtonListener implements View.OnClickListener {
        private ForceStopButtonListener() {
        }

        public void onClick(View view) {
            if (AppButtonsPreferenceController.this.mAppsControlDisallowedAdmin == null || AppButtonsPreferenceController.this.mAppsControlDisallowedBySystem) {
                AppButtonsPreferenceController.this.showDialogInner(2);
            } else {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(AppButtonsPreferenceController.this.mActivity, AppButtonsPreferenceController.this.mAppsControlDisallowedAdmin);
            }
        }
    }

    public void handleActivityResult(int i, int i2, Intent intent) {
        if (i == this.mRequestUninstall) {
            if (this.mDisableAfterUninstall) {
                this.mDisableAfterUninstall = LOCAL_LOGV;
                AsyncTask.execute(new DisableChangerRunnable(this.mPm, this.mAppEntry.info.packageName, 3));
            }
            refreshAndFinishIfPossible(true);
        } else if (i == this.mRequestRemoveDeviceAdmin) {
            refreshAndFinishIfPossible(LOCAL_LOGV);
        }
    }

    public void handleDialogClick(int i) {
        if (i == 0) {
            this.mMetricsFeatureProvider.action(this.mActivity, 874, new Pair[0]);
            AsyncTask.execute(new DisableChangerRunnable(this.mPm, this.mAppEntry.info.packageName, 3));
        } else if (i == 1) {
            this.mMetricsFeatureProvider.action(this.mActivity, 874, new Pair[0]);
            uninstallPkg(this.mAppEntry.info.packageName, LOCAL_LOGV, true);
        } else if (i == 2) {
            forceStopPackage(this.mAppEntry.info.packageName);
        }
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
        if (isAvailable()) {
            refreshUi();
        }
    }

    /* access modifiers changed from: package-private */
    public void retrieveAppEntry() {
        ApplicationsState.AppEntry entry = this.mState.getEntry(this.mPackageName, this.mUserId);
        this.mAppEntry = entry;
        if (entry != null) {
            try {
                this.mPackageInfo = this.mPm.getPackageInfo(entry.info.packageName, 4198976);
                this.mPackageName = this.mAppEntry.info.packageName;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Exception when retrieving package:" + this.mAppEntry.info.packageName, e);
                this.mPackageInfo = null;
            }
        } else {
            this.mPackageInfo = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateOpenButton() {
        Intent launchIntentForPackage = this.mPm.getLaunchIntentForPackage(this.mPackageName);
        this.mAppLaunchIntent = launchIntentForPackage;
        this.mButtonsPref.setButton1Visible(launchIntentForPackage != null ? true : LOCAL_LOGV);
    }

    /* access modifiers changed from: package-private */
    public void updateUninstallButton() {
        boolean z;
        Context context;
        ApplicationInfo applicationInfo;
        OverlayInfo overlayInfo;
        ApplicationInfo applicationInfo2 = this.mAppEntry.info;
        boolean z2 = true;
        int i = applicationInfo2.flags & 1;
        boolean z3 = LOCAL_LOGV;
        boolean z4 = i != 0 || PACKAGENAME_WALLPAPERRESOURCES.equals(applicationInfo2.packageName);
        if (z4) {
            z = handleDisableable();
        } else {
            z = (this.mPackageInfo.applicationInfo.flags & 8388608) != 0 || this.mUserManager.getUsers().size() < 2;
        }
        if (z4 && this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName)) {
            z = false;
        }
        if (!isSystemPackage(this.mActivity.getResources(), this.mPm, this.mPackageInfo) ? Utils.isProfileOrDeviceOwner(this.mDpm, this.mPackageInfo.packageName, this.mUserId) : Utils.isProfileOrDeviceOwner(this.mUserManager, this.mDpm, this.mPackageInfo.packageName)) {
            z = false;
        }
        if (com.android.settingslib.Utils.isDeviceProvisioningPackage(this.mContext.getResources(), this.mAppEntry.info.packageName)) {
            z = false;
        }
        if (this.mDpm.isUninstallInQueue(this.mPackageName)) {
            z = false;
        }
        if (z && this.mHomePackages.contains(this.mPackageInfo.packageName)) {
            if (z4) {
                z = false;
            } else {
                ComponentName homeActivities = this.mPm.getHomeActivities(new ArrayList());
                if (homeActivities == null) {
                    if (this.mHomePackages.size() <= 1) {
                        z2 = false;
                    }
                    z = z2;
                } else {
                    z = !this.mPackageInfo.packageName.equals(homeActivities.getPackageName());
                }
            }
        }
        Context context2 = this.mContext;
        if (context2 != null) {
            if (TextUtils.equals(context2.getString(C0017R$string.oneplus_cannot_disable_package_1), this.mAppEntry.info.packageName) || TextUtils.equals(this.mContext.getString(C0017R$string.oneplus_cannot_disable_package_2), this.mAppEntry.info.packageName)) {
                z = false;
            }
            if (OPUtils.isSupportUss() || OPUtils.isSupportUstUnify()) {
                String[] stringArray = this.mContext.getResources().getStringArray(C0003R$array.oneplus_cannot_disable_package);
                if (stringArray.length > 0) {
                    for (String str : stringArray) {
                        if (TextUtils.equals(str, this.mAppEntry.info.packageName)) {
                            z = false;
                        }
                    }
                }
            }
        }
        if (this.mAppsControlDisallowedBySystem) {
            z = false;
        }
        if (this.mAppEntry.info.isResourceOverlay() && (z4 || !((overlayInfo = this.mOverlayManager.getOverlayInfo((applicationInfo = this.mAppEntry.info).packageName, UserHandle.getUserHandleForUid(applicationInfo.uid))) == null || !overlayInfo.isEnabled() || this.mState.getEntry(overlayInfo.targetPackageName, UserHandle.getUserId(this.mAppEntry.info.uid)) == null))) {
            z = false;
        }
        if (TextUtils.equals(this.mContext.getString(C0017R$string.oneplus_cannot_disable_package_1), this.mAppEntry.info.packageName) || TextUtils.equals(this.mContext.getString(C0017R$string.oneplus_cannot_disable_package_2), this.mAppEntry.info.packageName)) {
            z = false;
        }
        if ("com.google.android.apps.wellbeing".equals(this.mAppEntry.info.packageName)) {
            z = false;
        }
        if (ProductUtils.isUsvMode() && "com.verizon.mips.services".equals(this.mAppEntry.info.packageName)) {
            z = false;
        }
        if (OPUtils.isSupportUss() && (context = this.mContext) != null) {
            for (String str2 : context.getResources().getStringArray(C0003R$array.oneplus_cannot_disable_package)) {
                if (str2.equals(this.mAppEntry.info.packageName)) {
                    z = false;
                }
            }
        }
        if (isSystemPersistApp()) {
            z = false;
        }
        if (!PACKAGENAME_FAST_APPLICATION.equals(this.mAppEntry.info.packageName) || !OPUtils.isApplicationEnabled(this.mContext, this.mAppEntry.info.packageName)) {
            z3 = z;
        }
        this.mButtonsPref.setButton2Enabled(z3);
    }

    private void setIntentAndFinish(boolean z, boolean z2) {
        Intent intent = new Intent();
        intent.putExtra(APP_CHG, z);
        intent.putExtra(KEY_REMOVE_TASK_WHEN_FINISHING, z2);
        this.mActivity.finishPreferencePanel(-1, intent);
        this.mFinishing = true;
    }

    private void refreshAndFinishIfPossible(boolean z) {
        if (!refreshUi()) {
            setIntentAndFinish(true, z);
        } else {
            startListeningToPackageRemove();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateForceStopButton() {
        if (this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName)) {
            Log.w(TAG, "User can't force stop device admin");
            updateForceStopButtonInner(LOCAL_LOGV);
        } else if ((this.mAppEntry.info.flags & 2097152) != 0) {
            Intent intent = new Intent("android.intent.action.QUERY_PACKAGE_RESTART", Uri.fromParts("package", this.mAppEntry.info.packageName, null));
            intent.putExtra("android.intent.extra.PACKAGES", new String[]{this.mAppEntry.info.packageName});
            intent.putExtra("android.intent.extra.UID", this.mAppEntry.info.uid);
            intent.putExtra("android.intent.extra.user_handle", UserHandle.getUserId(this.mAppEntry.info.uid));
            Log.d(TAG, "Sending broadcast to query restart status for " + this.mAppEntry.info.packageName);
            if (UserHandle.getUserId(this.mAppEntry.info.uid) == 999) {
                this.mActivity.sendOrderedBroadcastAsUser(intent, new UserHandle(999), null, this.mCheckKillProcessesReceiver, null, 0, null, null);
            } else {
                this.mActivity.sendOrderedBroadcastAsUser(intent, UserHandle.CURRENT, null, this.mCheckKillProcessesReceiver, null, 0, null, null);
            }
        } else if ((OPUtils.isSupportUss() || OPUtils.isSupportUstUnify()) && PACKAGENAME_MOBILE_INSTALLER.equals(this.mPackageInfo.packageName)) {
            updateForceStopButtonInner(LOCAL_LOGV);
        } else {
            boolean isSystemPersistApp = isSystemPersistApp();
            Log.w(TAG, "App is not explicitly stopped and isSystemPersistApp:" + isSystemPersistApp);
            updateForceStopButtonInner(isSystemPersistApp ^ true);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateForceStopButtonInner(boolean z) {
        if (this.mAppsControlDisallowedBySystem) {
            this.mButtonsPref.setButton3Enabled(LOCAL_LOGV);
        } else {
            this.mButtonsPref.setButton3Enabled(z);
        }
    }

    /* access modifiers changed from: package-private */
    public void uninstallPkg(String str, boolean z, boolean z2) {
        stopListeningToPackageRemove();
        Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + str));
        if (OPUtils.hasMultiApp(this.mContext, str)) {
            intent.setAction("oneplus.intent.action.DELETE");
        }
        intent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", z);
        this.mMetricsFeatureProvider.action(this.mActivity, 872, new Pair[0]);
        this.mFragment.startActivityForResult(intent, this.mRequestUninstall);
        this.mDisableAfterUninstall = z2;
    }

    /* access modifiers changed from: package-private */
    public void forceStopPackage(String str) {
        MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(this.mActivity), 807, this.mFragment.getMetricsCategory(), str, 0);
        Log.d(TAG, "Stopping package " + str);
        ((ActivityManager) this.mActivity.getSystemService("activity")).forceStopPackage(str);
        int userId = UserHandle.getUserId(this.mAppEntry.info.uid);
        this.mState.invalidatePackage(str, userId);
        ApplicationsState.AppEntry entry = this.mState.getEntry(str, userId);
        if (entry != null) {
            this.mAppEntry = entry;
        }
        updateForceStopButton();
    }

    /* access modifiers changed from: package-private */
    public boolean handleDisableable() {
        if (this.mHomePackages.contains(this.mAppEntry.info.packageName) || isSystemPackage(this.mActivity.getResources(), this.mPm, this.mPackageInfo) || KEY_LATIN_INPUTMETHOD.contains(this.mAppEntry.info.packageName) || KEY_GOOGLE__INPUTMETHOD.contains(this.mAppEntry.info.packageName) || KEY_MARKET.contains(this.mAppEntry.info.packageName) || PACKAGENAME_WALLPAPERRESOURCES.equals(this.mAppEntry.info.packageName) || PACKAGENAME_HEYTAP_CLOUD.equals(this.mAppEntry.info.packageName)) {
            ActionButtonsPreference actionButtonsPreference = this.mButtonsPref;
            actionButtonsPreference.setButton2Text(C0017R$string.disable_text);
            actionButtonsPreference.setButton2Icon(C0008R$drawable.ic_settings_disable);
            return LOCAL_LOGV;
        } else if (!this.mAppEntry.info.enabled || isDisabledUntilUsed()) {
            ActionButtonsPreference actionButtonsPreference2 = this.mButtonsPref;
            actionButtonsPreference2.setButton2Text(C0017R$string.enable_text);
            actionButtonsPreference2.setButton2Icon(C0008R$drawable.ic_settings_enable);
            return true;
        } else {
            ActionButtonsPreference actionButtonsPreference3 = this.mButtonsPref;
            actionButtonsPreference3.setButton2Text(C0017R$string.disable_text);
            actionButtonsPreference3.setButton2Icon(C0008R$drawable.ic_settings_disable);
            return true ^ this.mApplicationFeatureProvider.getKeepEnabledPackages().contains(this.mAppEntry.info.packageName);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isSystemPackage(Resources resources, PackageManager packageManager, PackageInfo packageInfo) {
        if (!com.android.settingslib.Utils.isSystemPackage(resources, packageManager, packageInfo) || !isFilterPackage(this.mAppEntry.info.packageName)) {
            return LOCAL_LOGV;
        }
        return true;
    }

    private boolean isFilterPackage(String str) {
        return !PACKAGENAME_BRICKMODE.equals(str);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isDisabledUntilUsed() {
        if (this.mAppEntry.info.enabledSetting == 4) {
            return true;
        }
        return LOCAL_LOGV;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showDialogInner(int i) {
        ButtonActionDialogFragment newInstance = ButtonActionDialogFragment.newInstance(i);
        newInstance.setTargetFragment(this.mFragment, 0);
        FragmentManager supportFragmentManager = this.mActivity.getSupportFragmentManager();
        newInstance.show(supportFragmentManager, "dialog " + i);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isSingleUser() {
        int userCount = this.mUserManager.getUserCount();
        if (userCount == 1) {
            return true;
        }
        if (!UserManager.isSplitSystemUser() || userCount != 2) {
            return LOCAL_LOGV;
        }
        return true;
    }

    private boolean signaturesMatch(String str, String str2) {
        if (str == null || str2 == null) {
            return LOCAL_LOGV;
        }
        try {
            if (this.mPm.checkSignatures(str, str2) >= 0) {
                return true;
            }
            return LOCAL_LOGV;
        } catch (Exception unused) {
            return LOCAL_LOGV;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean refreshUi() {
        String str = this.mPackageName;
        boolean z = LOCAL_LOGV;
        z = LOCAL_LOGV;
        if (str == null) {
            return LOCAL_LOGV;
        }
        retrieveAppEntry();
        if (!(this.mAppEntry == null || this.mPackageInfo == null)) {
            ArrayList arrayList = new ArrayList();
            this.mPm.getHomeActivities(arrayList);
            this.mHomePackages.clear();
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ResolveInfo resolveInfo = (ResolveInfo) arrayList.get(i);
                String str2 = resolveInfo.activityInfo.packageName;
                this.mHomePackages.add(str2);
                Bundle bundle = resolveInfo.activityInfo.metaData;
                if (bundle != null) {
                    String string = bundle.getString("android.app.home.alternate");
                    if (signaturesMatch(string, str2)) {
                        this.mHomePackages.add(string);
                    }
                }
            }
            z = true;
            if (this.mButtonsPref == null) {
                initButtonPreference();
                this.mButtonsPref.setVisible(true);
            }
            updateOpenButton();
            updateUninstallButton();
            updateForceStopButton();
        }
        return z;
    }

    private void initButtonPreference() {
        ActionButtonsPreference actionButtonsPreference = (ActionButtonsPreference) this.mScreen.findPreference(KEY_ACTION_BUTTONS);
        actionButtonsPreference.setButton1Text(C0017R$string.launch_instant_app);
        actionButtonsPreference.setButton1Icon(C0008R$drawable.ic_settings_open);
        actionButtonsPreference.setButton1OnClickListener(new View.OnClickListener() {
            /* class com.android.settings.applications.appinfo.$$Lambda$AppButtonsPreferenceController$enw5jpYO1cJrg3i8F_CFkg0AY6w */

            public final void onClick(View view) {
                AppButtonsPreferenceController.this.lambda$initButtonPreference$0$AppButtonsPreferenceController(view);
            }
        });
        actionButtonsPreference.setButton2Text(C0017R$string.uninstall_text);
        actionButtonsPreference.setButton2Icon(C0008R$drawable.ic_settings_delete);
        actionButtonsPreference.setButton2OnClickListener(new UninstallAndDisableButtonListener());
        actionButtonsPreference.setButton3Text(C0017R$string.force_stop);
        actionButtonsPreference.setButton3Icon(C0008R$drawable.ic_settings_force_stop);
        actionButtonsPreference.setButton3OnClickListener(new ForceStopButtonListener());
        actionButtonsPreference.setButton3Enabled(LOCAL_LOGV);
        this.mButtonsPref = actionButtonsPreference;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initButtonPreference$0 */
    public /* synthetic */ void lambda$initButtonPreference$0$AppButtonsPreferenceController(View view) {
        launchApplication();
    }

    private void startListeningToPackageRemove() {
        if (!this.mListeningToPackageRemove) {
            this.mListeningToPackageRemove = true;
            IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addDataScheme("package");
            this.mActivity.registerReceiver(this.mPackageRemovedReceiver, intentFilter);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void stopListeningToPackageRemove() {
        if (this.mListeningToPackageRemove) {
            this.mListeningToPackageRemove = LOCAL_LOGV;
            this.mActivity.unregisterReceiver(this.mPackageRemovedReceiver);
        }
    }

    private void launchApplication() {
        if (this.mAppLaunchIntent != null) {
            if (this.mAccessedFromAutoRevoke) {
                Log.i(TAG, "sessionId: " + this.mSessionId + " uninstalling " + this.mPackageName + " with uid " + getUid() + ", reached from auto revoke");
                SettingsStatsLog.write(272, this.mSessionId, getUid(), this.mPackageName, 6);
            }
            this.mContext.startActivityAsUser(this.mAppLaunchIntent, new UserHandle(this.mUserId));
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getUid() {
        if (this.mPackageInfo == null) {
            retrieveAppEntry();
        }
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo != null) {
            return packageInfo.applicationInfo.uid;
        }
        return -1;
    }

    private boolean isInstantApp() {
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        if (appEntry == null || !AppUtils.isInstant(appEntry.info)) {
            return LOCAL_LOGV;
        }
        return true;
    }

    private boolean isSystemModule() {
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        if (appEntry == null || !AppUtils.isSystemModule(this.mContext, appEntry.info.packageName)) {
            return LOCAL_LOGV;
        }
        return true;
    }

    private boolean isSystemPersistApp() {
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        if (appEntry == null || ((!AppUtils.isSystemModule(this.mContext, appEntry.info.packageName) && !AppUtils.isMainlineModule(this.mPm, this.mAppEntry.info.packageName)) || !isFilterPackage(this.mAppEntry.info.packageName))) {
            return LOCAL_LOGV;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public class DisableChangerRunnable implements Runnable {
        final String mPackageName;
        final PackageManager mPm;
        final int mState;

        public DisableChangerRunnable(PackageManager packageManager, String str, int i) {
            this.mPm = packageManager;
            this.mPackageName = str;
            this.mState = i;
        }

        public void run() {
            this.mPm.setApplicationEnabledSetting(this.mPackageName, this.mState, 0);
            if (AppButtonsPreferenceController.PACKAGENAME_BRICKMODE.equals(this.mPackageName)) {
                int i = this.mState;
                if (i == 3) {
                    AppButtonsPreferenceController.this.hideBrickModeInSystemUI(true);
                    OPUtils.sendAnalytics("L9YJTAKVM7", "permission", "forbid", "forbid");
                } else if (i == 0) {
                    AppButtonsPreferenceController.this.hideBrickModeInSystemUI(AppButtonsPreferenceController.LOCAL_LOGV);
                    OPUtils.sendAnalytics("L9YJTAKVM7", "permission", "open", "open");
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void hideBrickModeInSystemUI(boolean z) {
        Intent intent = new Intent(ACTION_QUICK_SETTING_TILE);
        intent.putExtra("tile", BRICK_MODE_SERVICE_TILE);
        intent.putExtra("hide", z);
        if (z) {
            saveBrickModeLocation();
        } else {
            int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "sysui_qs_tiles_brickmode_location", 16);
            Log.d("BrickModeLocation", "get location : " + i);
            intent.putExtra("position", i);
        }
        this.mActivity.sendBroadcast(intent);
    }

    private void saveBrickModeLocation() {
        String string = Settings.Secure.getString(this.mContext.getContentResolver(), "sysui_qs_tiles");
        if (string != null) {
            int i = 0;
            if (string.contains(BRICK_MODE_SERVICE_TILE)) {
                String[] split = string.split(",");
                int length = split.length;
                int i2 = 0;
                while (i < length && !split[i].equalsIgnoreCase(BRICK_MODE_SERVICE_TILE)) {
                    i2++;
                    i++;
                }
                i = i2;
            }
            Log.d("BrickModeLocation", "save location : " + i);
            Settings.Secure.putInt(this.mContext.getContentResolver(), "sysui_qs_tiles_brickmode_location", i);
        }
    }
}
