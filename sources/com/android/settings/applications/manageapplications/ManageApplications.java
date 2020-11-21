package com.android.settings.applications.manageapplications;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.IUsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.PreferenceFrameLayout;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.compat.IPlatformCompat;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0013R$menu;
import com.android.settings.C0017R$string;
import com.android.settings.Settings;
import com.android.settings.Utils;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.applications.AppStateInstallAppsBridge;
import com.android.settings.applications.AppStateManageExternalStorageBridge;
import com.android.settings.applications.AppStateNotificationBridge;
import com.android.settings.applications.AppStateOverlayBridge;
import com.android.settings.applications.AppStatePowerBridge;
import com.android.settings.applications.AppStateUsageBridge;
import com.android.settings.applications.AppStateWriteSettingsBridge;
import com.android.settings.applications.AppStorageSettings;
import com.android.settings.applications.UsageAccessDetails;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.applications.appinfo.DrawOverlayDetails;
import com.android.settings.applications.appinfo.ExternalSourcesDetails;
import com.android.settings.applications.appinfo.ManageExternalStorageDetails;
import com.android.settings.applications.appinfo.WriteSettingsDetails;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.fuelgauge.AdvancedPowerUsageDetail;
import com.android.settings.fuelgauge.BatteryEntry;
import com.android.settings.fuelgauge.BatteryStatsHelperLoader;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.fuelgauge.HighPowerDetail;
import com.android.settings.notification.ConfigureNotificationSettings;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.AppNotificationSettings;
import com.android.settings.widget.LoadingViewController;
import com.android.settings.wifi.AppStateChangeWifiStateBridge;
import com.android.settings.wifi.ChangeWifiStateDetails;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.fuelgauge.PowerWhitelistBackend;
import com.android.settingslib.utils.ThreadUtils;
import com.google.android.material.emptyview.EmptyPageView;
import com.oneplus.settings.backgroundoptimize.AppBgOptimizeBridge;
import com.oneplus.settings.better.ReadingModeEffectDetail;
import com.oneplus.settings.better.ReadingModeEffectSelectBridge;
import com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge;
import com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionDetail;
import com.oneplus.settings.edgeeffect.SpringRelativeLayout;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;
import com.oneplus.settings.widget.OPSettingsSpinnerAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ManageApplications extends InstrumentedFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<BatteryStatsHelper> {
    static final boolean DEBUG = Build.IS_DEBUGGABLE;
    static final String EXTRA_EXPAND_SEARCH_VIEW = "expand_search_view";
    public static final Set<Integer> LIST_TYPES_WITH_INSTANT = new ArraySet(Arrays.asList(0, 3));
    private ApplicationsAdapter mApplications;
    private ApplicationsState mApplicationsState;
    private BatteryStatsHelper mBatteryHelper;
    private AppStateNotificationBridge.NotificationsSentState mCurrentNotificationsSentState;
    private String mCurrentPkgName;
    private int mCurrentUid;
    private View mEmptyView;
    boolean mExpandSearch;
    private AppFilterItem mFilter;
    FilterSpinnerAdapter mFilterAdapter;
    private Spinner mFilterSpinner;
    private int mFilterType;
    CharSequence mInvalidSizeStr;
    private boolean mIsPersonalOnly;
    private boolean mIsWorkOnly;
    private View mListContainer;
    public int mListType;
    private View mLoadingContainer;
    private NotificationBackend mNotificationBackend;
    private Menu mOptionsMenu;
    RecyclerView mRecyclerView;
    private ResetAppsHelper mResetAppsHelper;
    private View mRootView;
    private SearchView mSearchView;
    private boolean mShowSystem;
    int mSortOrder = C0010R$id.sort_order_alpha;
    View mSpinnerHeader;
    private int mStorageType;
    private IUsageStatsManager mUsageStatsManager;
    private UserManager mUserManager;
    private String mVolumeUuid;
    private int mWorkUserId;

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<BatteryStatsHelper> loader) {
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextSubmit(String str) {
        return DEBUG;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boolean z = true;
        setHasOptionsMenu(true);
        FragmentActivity activity = getActivity();
        this.mUserManager = (UserManager) activity.getSystemService(UserManager.class);
        this.mApplicationsState = ApplicationsState.getInstance(activity.getApplication());
        Intent intent = activity.getIntent();
        Bundle arguments = getArguments();
        int intExtra = intent.getIntExtra(":settings:show_fragment_title_resid", C0017R$string.application_info_label);
        String string = arguments != null ? arguments.getString("classname") : null;
        if (string == null) {
            string = intent.getComponent().getClassName();
        }
        if (string.equals(Settings.StorageUseActivity.class.getName())) {
            if (arguments == null || !arguments.containsKey("volumeUuid")) {
                this.mListType = 0;
            } else {
                this.mVolumeUuid = arguments.getString("volumeUuid");
                this.mStorageType = arguments.getInt("storageType", 0);
                this.mListType = 3;
            }
            this.mSortOrder = C0010R$id.sort_order_size;
        } else if (string.equals(Settings.UsageAccessSettingsActivity.class.getName())) {
            this.mListType = 4;
            intExtra = C0017R$string.usage_access;
        } else if (string.equals(Settings.HighPowerApplicationsActivity.class.getName())) {
            if (ProductUtils.isUsvMode()) {
                this.mShowSystem = true;
            }
            LoaderManager.getInstance(this).restartLoader(4, Bundle.EMPTY, this);
            this.mListType = 16;
            intExtra = C0017R$string.high_power_apps;
        } else if (string.equals(Settings.OverlaySettingsActivity.class.getName())) {
            this.mListType = 6;
            intExtra = C0017R$string.system_alert_window_settings;
            reportIfRestrictedSawIntent(intent);
        } else if (string.equals(Settings.WriteSettingsActivity.class.getName())) {
            this.mListType = 7;
            intExtra = C0017R$string.write_settings;
        } else if (string.equals(Settings.ManageExternalSourcesActivity.class.getName())) {
            this.mListType = 8;
            intExtra = C0017R$string.install_other_apps;
        } else if (string.equals(Settings.GamesStorageActivity.class.getName())) {
            this.mListType = 9;
            this.mSortOrder = C0010R$id.sort_order_size;
        } else if (string.equals(Settings.MoviesStorageActivity.class.getName())) {
            this.mListType = 10;
            this.mSortOrder = C0010R$id.sort_order_size;
        } else if (string.equals(Settings.PhotosStorageActivity.class.getName())) {
            this.mListType = 11;
            this.mSortOrder = C0010R$id.sort_order_size;
            this.mStorageType = arguments.getInt("storageType", 0);
        } else if (string.equals(Settings.ChangeWifiStateActivity.class.getName())) {
            this.mListType = 13;
            intExtra = C0017R$string.change_wifi_state_title;
        } else if (string.equals(Settings.ManageExternalStorageActivity.class.getName())) {
            this.mListType = 14;
            intExtra = C0017R$string.manage_external_storage_title;
        } else if (string.equals(Settings.NotificationAppListActivity.class.getName())) {
            this.mListType = 1;
            this.mUsageStatsManager = IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats"));
            this.mNotificationBackend = new NotificationBackend();
            this.mSortOrder = C0010R$id.sort_order_recent_notification;
            intExtra = C0017R$string.app_notifications_title;
        } else if (string.equals(Settings.DisplaySizeAdaptionAppListActivity.class.getName())) {
            this.mListType = 15;
            intExtra = C0017R$string.oneplus_app_display_fullscreen_title;
        } else if (string.equals(Settings.ReadingModeAppListActivity.class.getName())) {
            this.mListType = 17;
            intExtra = C0017R$string.oneplus_read_mode_app_list;
        } else if (string.equals(Settings.BgOptimizeAppListActivity.class.getName())) {
            LoaderManager.getInstance(this).restartLoader(4, Bundle.EMPTY, this);
            this.mListType = 16;
            intExtra = C0017R$string.high_power_apps;
            this.mShowSystem = true;
        } else {
            if (intExtra == -1) {
                intExtra = C0017R$string.application_info_label;
            }
            this.mListType = 0;
        }
        AppFilterRegistry instance = AppFilterRegistry.getInstance();
        this.mFilter = instance.get(instance.getDefaultFilterType(this.mListType));
        this.mIsPersonalOnly = arguments != null && arguments.getInt("profile") == 1;
        if (arguments == null || arguments.getInt("profile") != 2) {
            z = false;
        }
        this.mIsWorkOnly = z;
        int i = arguments != null ? arguments.getInt("workId") : UserHandle.myUserId();
        this.mWorkUserId = i;
        if (this.mIsWorkOnly && i == UserHandle.myUserId()) {
            this.mWorkUserId = Utils.getManagedProfileId(this.mUserManager, UserHandle.myUserId());
        }
        this.mExpandSearch = activity.getIntent().getBooleanExtra(EXTRA_EXPAND_SEARCH_VIEW, DEBUG);
        if (bundle != null) {
            this.mSortOrder = bundle.getInt("sortOrder", this.mSortOrder);
            this.mShowSystem = bundle.getBoolean("showSystem", this.mShowSystem);
            this.mFilterType = bundle.getInt("filterType", 2);
            this.mExpandSearch = bundle.getBoolean(EXTRA_EXPAND_SEARCH_VIEW);
        }
        this.mInvalidSizeStr = activity.getText(C0017R$string.invalid_size_value);
        this.mResetAppsHelper = new ResetAppsHelper(activity);
        if (intExtra > 0) {
            activity.setTitle(intExtra);
        }
    }

    private void reportIfRestrictedSawIntent(Intent intent) {
        try {
            Uri data = intent.getData();
            if (data == null) {
                return;
            }
            if (TextUtils.equals("package", data.getScheme())) {
                int launchedFromUid = ActivityManager.getService().getLaunchedFromUid(getActivity().getActivityToken());
                if (launchedFromUid == -1) {
                    Log.w("ManageApplications", "Error obtaining calling uid");
                    return;
                }
                IPlatformCompat asInterface = IPlatformCompat.Stub.asInterface(ServiceManager.getService("platform_compat"));
                if (asInterface == null) {
                    Log.w("ManageApplications", "Error obtaining IPlatformCompat service");
                } else {
                    asInterface.reportChangeByUid(135920175, launchedFromUid);
                }
            }
        } catch (RemoteException e) {
            Log.w("ManageApplications", "Error reporting SAW intent restriction", e);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (this.mListType != 6 || Utils.isSystemAlertWindowEnabled(getContext())) {
            View inflate = layoutInflater.inflate(C0012R$layout.manage_applications_apps, (ViewGroup) null);
            this.mRootView = inflate;
            this.mLoadingContainer = inflate.findViewById(C0010R$id.loading_container);
            View findViewById = this.mRootView.findViewById(C0010R$id.list_container);
            this.mListContainer = findViewById;
            if (findViewById != null) {
                this.mEmptyView = findViewById.findViewById(16908292);
                View findViewById2 = this.mListContainer.findViewById(16908292);
                this.mEmptyView = findViewById2;
                if (findViewById2 instanceof EmptyPageView) {
                    ((EmptyPageView) findViewById2).getEmptyTextView().setText(C0017R$string.oneplus_no_applications);
                    ((EmptyPageView) this.mEmptyView).getEmptyImageView().setImageResource(C0008R$drawable.op_empty);
                }
                ApplicationsAdapter applicationsAdapter = new ApplicationsAdapter(this.mApplicationsState, this, this.mFilter, bundle);
                this.mApplications = applicationsAdapter;
                if (bundle != null) {
                    applicationsAdapter.mHasReceivedLoadEntries = bundle.getBoolean("hasEntries", DEBUG);
                    this.mApplications.mHasReceivedBridgeCallback = bundle.getBoolean("hasBridge", DEBUG);
                }
                int myUserId = this.mIsWorkOnly ? this.mWorkUserId : UserHandle.myUserId();
                int i = this.mStorageType;
                if (i == 1) {
                    Context context = getContext();
                    this.mApplications.setExtraViewController(new MusicViewHolderController(context, new StorageStatsSource(context), this.mVolumeUuid, UserHandle.of(myUserId)));
                } else if (i == 3) {
                    Context context2 = getContext();
                    this.mApplications.setExtraViewController(new PhotosViewHolderController(context2, new StorageStatsSource(context2), this.mVolumeUuid, UserHandle.of(myUserId)));
                }
                RecyclerView recyclerView = (RecyclerView) this.mListContainer.findViewById(C0010R$id.apps_list);
                this.mRecyclerView = recyclerView;
                recyclerView.setItemAnimator(null);
                this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 1, DEBUG));
                this.mRecyclerView.setAdapter(this.mApplications);
                final SpringRelativeLayout springRelativeLayout = (SpringRelativeLayout) this.mListContainer.findViewById(C0010R$id.spring_layout);
                springRelativeLayout.addSpringView(C0010R$id.apps_list);
                this.mRecyclerView.setEdgeEffectFactory(springRelativeLayout.createEdgeEffectFactory());
                this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(this) {
                    /* class com.android.settings.applications.manageapplications.ManageApplications.AnonymousClass1 */
                    int state = 0;

                    @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                    public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                        this.state = i;
                    }

                    @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                    public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                        if (this.state == 1 && i != 0) {
                            springRelativeLayout.onRecyclerViewScrolled();
                        }
                    }
                });
            }
            if (viewGroup instanceof PreferenceFrameLayout) {
                this.mRootView.getLayoutParams().removeBorders = true;
            }
            createHeader();
            this.mResetAppsHelper.onRestoreInstanceState(bundle);
            return this.mRootView;
        }
        this.mRootView = layoutInflater.inflate(C0012R$layout.manage_applications_apps_unsupported, (ViewGroup) null);
        setHasOptionsMenu(DEBUG);
        return this.mRootView;
    }

    /* access modifiers changed from: package-private */
    public void createHeader() {
        FragmentActivity activity = getActivity();
        FrameLayout frameLayout = (FrameLayout) this.mRootView.findViewById(C0010R$id.pinned_header);
        View inflate = activity.getLayoutInflater().inflate(C0012R$layout.apps_filter_spinner, frameLayout, (boolean) DEBUG);
        this.mSpinnerHeader = inflate;
        this.mFilterSpinner = (Spinner) inflate.findViewById(C0010R$id.filter_spinner);
        FilterSpinnerAdapter filterSpinnerAdapter = new FilterSpinnerAdapter(this);
        this.mFilterAdapter = filterSpinnerAdapter;
        this.mFilterSpinner.setAdapter((SpinnerAdapter) filterSpinnerAdapter);
        this.mFilterSpinner.setOnItemSelectedListener(this);
        frameLayout.addView(this.mSpinnerHeader, 0);
        this.mFilterAdapter.enableFilter(AppFilterRegistry.getInstance().getDefaultFilterType(this.mListType));
        if (this.mListType == 0) {
            if (OPUtils.hasMultiAppProfiles(UserManager.get(getActivity()))) {
                if (UserManager.get(getActivity()).getUserProfiles().size() > 2 && !this.mIsWorkOnly && !this.mIsPersonalOnly) {
                    this.mFilterAdapter.enableFilter(8);
                    this.mFilterAdapter.enableFilter(9);
                }
            } else if (UserManager.get(getActivity()).getUserProfiles().size() > 1 && !this.mIsWorkOnly && !this.mIsPersonalOnly) {
                this.mFilterAdapter.enableFilter(8);
                this.mFilterAdapter.enableFilter(9);
            }
        }
        if (this.mListType == 1) {
            this.mFilterAdapter.enableFilter(6);
            this.mFilterAdapter.enableFilter(7);
            this.mFilterAdapter.enableFilter(16);
            this.mFilterAdapter.disableFilter(2);
        }
        if (this.mListType == 5) {
            this.mFilterAdapter.enableFilter(1);
        }
        if (this.mListType == 15) {
            this.mFilterAdapter.disableFilter(2);
            this.mFilterAdapter.enableFilter(18);
            this.mFilterAdapter.enableFilter(19);
            this.mFilterAdapter.enableFilter(20);
        }
        if (this.mListType == 16) {
            this.mFilterAdapter.disableFilter(2);
            this.mFilterAdapter.enableFilter(21);
            this.mFilterAdapter.enableFilter(22);
        }
        if (this.mListType == 17) {
            this.mFilterAdapter.disableFilter(2);
            this.mFilterAdapter.enableFilter(23);
            this.mFilterAdapter.enableFilter(24);
            this.mFilterAdapter.enableFilter(25);
            this.mFilterAdapter.enableFilter(26);
        }
        setCompositeFilter();
    }

    static ApplicationsState.AppFilter getCompositeFilter(int i, int i2, String str) {
        ApplicationsState.CompoundFilter compoundFilter;
        ApplicationsState.VolumeFilter volumeFilter = new ApplicationsState.VolumeFilter(str);
        if (i == 3) {
            if (i2 == 1) {
                compoundFilter = new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_AUDIO, volumeFilter);
            } else if (i2 != 0) {
                return volumeFilter;
            } else {
                compoundFilter = new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_OTHER_APPS, volumeFilter);
            }
            return compoundFilter;
        } else if (i == 9) {
            return new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_GAMES, volumeFilter);
        } else {
            if (i == 10) {
                return new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_MOVIES, volumeFilter);
            }
            if (i == 11) {
                return new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_PHOTOS, volumeFilter);
            }
            return null;
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        switch (this.mListType) {
            case 0:
                return 65;
            case 1:
                return 133;
            case 2:
            case 12:
            default:
                return 0;
            case 3:
                return this.mStorageType == 1 ? 839 : 182;
            case 4:
                return 95;
            case 5:
                return 184;
            case 6:
            case 7:
                return 221;
            case 8:
                return 808;
            case 9:
                return 838;
            case 10:
                return 935;
            case 11:
                return 1092;
            case 13:
                return 338;
            case 14:
                return 1822;
            case 15:
            case 16:
            case 17:
                return 65;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        updateView();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.resume(this.mSortOrder);
            this.mApplications.updateLoading();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mResetAppsHelper.onSaveInstanceState(bundle);
        bundle.putInt("sortOrder", this.mSortOrder);
        bundle.putInt("filterType", this.mFilter.getFilterType());
        bundle.putBoolean("showSystem", this.mShowSystem);
        bundle.putBoolean("hasEntries", this.mApplications.mHasReceivedLoadEntries);
        bundle.putBoolean("hasBridge", this.mApplications.mHasReceivedBridgeCallback);
        SearchView searchView = this.mSearchView;
        if (searchView != null) {
            bundle.putBoolean(EXTRA_EXPAND_SEARCH_VIEW, !searchView.isIconified());
        }
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.onSaveInstanceState(bundle);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.pause();
        }
        FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            this.mResetAppsHelper.stop();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.release();
        }
        this.mRootView = null;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        String str;
        if (i == 1 && (str = this.mCurrentPkgName) != null) {
            int i3 = this.mListType;
            if (i3 == 1) {
                this.mApplications.mExtraInfoBridge.forceUpdate(this.mCurrentPkgName, this.mCurrentUid);
            } else if (i3 == 5 || i3 == 6 || i3 == 7 || i3 == 15 || i3 == 16 || i3 == 17) {
                this.mApplications.mExtraInfoBridge.forceUpdate(this.mCurrentPkgName, this.mCurrentUid);
            } else {
                this.mApplicationsState.requestSize(str, UserHandle.getUserId(this.mCurrentUid));
            }
        }
    }

    private void setCompositeFilter() {
        ApplicationsState.CompoundFilter compositeFilter = getCompositeFilter(this.mListType, this.mStorageType, this.mVolumeUuid);
        if (compositeFilter == null) {
            compositeFilter = this.mFilter.getFilter();
        }
        if (this.mIsWorkOnly) {
            compositeFilter = new ApplicationsState.CompoundFilter(compositeFilter, ApplicationsState.FILTER_WORK);
        }
        if (this.mIsPersonalOnly) {
            compositeFilter = new ApplicationsState.CompoundFilter(compositeFilter, ApplicationsState.FILTER_PERSONAL);
        }
        this.mApplications.setCompositeFilter(compositeFilter);
    }

    private void startApplicationDetailsActivity() {
        switch (this.mListType) {
            case 1:
                startAppInfoFragmentForNotification(AppNotificationSettings.class, C0017R$string.notifications_title, this.mCurrentNotificationsSentState);
                return;
            case 2:
            case 12:
            default:
                startAppInfoFragment(AppInfoDashboardFragment.class, C0017R$string.application_info_label);
                return;
            case 3:
                startAppInfoFragment(AppStorageSettings.class, C0017R$string.storage_settings);
                return;
            case 4:
                startAppInfoFragment(UsageAccessDetails.class, C0017R$string.usage_access);
                return;
            case 5:
                HighPowerDetail.show(this, this.mCurrentUid, this.mCurrentPkgName, 1);
                return;
            case 6:
                startAppInfoFragment(DrawOverlayDetails.class, C0017R$string.overlay_settings);
                return;
            case 7:
                startAppInfoFragment(WriteSettingsDetails.class, C0017R$string.write_system_settings);
                return;
            case 8:
                startAppInfoFragment(ExternalSourcesDetails.class, C0017R$string.install_other_apps);
                return;
            case 9:
                startAppInfoFragment(AppStorageSettings.class, C0017R$string.game_storage_settings);
                return;
            case 10:
                startAppInfoFragment(AppStorageSettings.class, C0017R$string.storage_movies_tv);
                return;
            case 11:
                startAppInfoFragment(AppStorageSettings.class, C0017R$string.storage_photos_videos);
                return;
            case 13:
                startAppInfoFragment(ChangeWifiStateDetails.class, C0017R$string.change_wifi_state_title);
                return;
            case 14:
                startAppInfoFragment(ManageExternalStorageDetails.class, C0017R$string.manage_external_storage_title);
                return;
            case 15:
                if (getActivity() != null && !getActivity().isFinishing()) {
                    DisplaySizeAdaptionDetail.show(this, this.mCurrentUid, this.mCurrentPkgName, 1);
                    return;
                }
                return;
            case 16:
                FragmentActivity activity = getActivity();
                if (activity != null && !activity.isFinishing()) {
                    BatteryUtils instance = BatteryUtils.getInstance(getContext());
                    BatterySipper findTargetSipper = findTargetSipper(this.mBatteryHelper, this.mCurrentUid);
                    BatteryStatsHelper batteryStatsHelper = this.mBatteryHelper;
                    if (batteryStatsHelper == null || findTargetSipper == null) {
                        AdvancedPowerUsageDetail.startBatteryDetailPage(activity, this.mCurrentPkgName);
                        return;
                    }
                    int dischargeAmount = batteryStatsHelper.getStats().getDischargeAmount(0);
                    double removeHiddenBatterySippers = instance.removeHiddenBatterySippers(new ArrayList(this.mBatteryHelper.getUsageList()));
                    BatteryEntry batteryEntry = new BatteryEntry(getContext(), null, (UserManager) getContext().getSystemService("user"), findTargetSipper);
                    batteryEntry.defaultPackageName = this.mCurrentPkgName;
                    AdvancedPowerUsageDetail.startBatteryDetailPage(activity, this.mBatteryHelper, 0, batteryEntry, com.android.settingslib.Utils.formatPercentage((int) instance.calculateBatteryPercent(findTargetSipper.totalPowerMah, this.mBatteryHelper.getTotalPower(), removeHiddenBatterySippers, dischargeAmount)));
                    return;
                }
                return;
            case 17:
                if (getActivity() != null && !getActivity().isFinishing()) {
                    ReadingModeEffectDetail.show(this, this.mCurrentUid, this.mCurrentPkgName, 1);
                    return;
                }
                return;
        }
    }

    private BatterySipper findTargetSipper(BatteryStatsHelper batteryStatsHelper, int i) {
        if (batteryStatsHelper == null) {
            return null;
        }
        List usageList = batteryStatsHelper.getUsageList();
        int size = usageList.size();
        for (int i2 = 0; i2 < size; i2++) {
            BatterySipper batterySipper = (BatterySipper) usageList.get(i2);
            if (batterySipper.getUid() == i) {
                return batterySipper;
            }
        }
        return null;
    }

    private void startAppInfoFragment(Class<?> cls, int i) {
        AppInfoBase.startAppInfoFragment(cls, i, this.mCurrentPkgName, this.mCurrentUid, this, 1, getMetricsCategory());
    }

    private void startAppInfoFragmentForNotification(Class<?> cls, int i, AppStateNotificationBridge.NotificationsSentState notificationsSentState) {
        Bundle bundle = new Bundle();
        bundle.putString("package", this.mCurrentPkgName);
        bundle.putInt("uid", this.mCurrentUid);
        if (notificationsSentState != null && !TextUtils.isEmpty(notificationsSentState.instantAppPKG)) {
            bundle.putString("arg_instant_package_name", notificationsSentState.instantAppPKG);
        }
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(cls.getName());
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setTitleRes(i);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setUserHandle(new UserHandle(UserHandle.getUserId(this.mCurrentUid)));
        subSettingLauncher.setResultListener(this, 1);
        subSettingLauncher.launch();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        FragmentActivity activity = getActivity();
        if (activity != null && this.mListType != 15) {
            HelpUtils.prepareHelpMenuItem(activity, menu, getHelpResource(), getClass().getName());
            this.mOptionsMenu = menu;
            menuInflater.inflate(C0013R$menu.manage_apps, menu);
            MenuItem findItem = menu.findItem(C0010R$id.search_app_list_menu);
            if (findItem != null) {
                SearchView searchView = (SearchView) findItem.getActionView();
                this.mSearchView = searchView;
                searchView.setQueryHint(getText(C0017R$string.search_settings));
                this.mSearchView.setOnQueryTextListener(this);
                TextView textView = (TextView) this.mSearchView.findViewById(C0010R$id.search_src_text);
                Context context = this.mSearchView.getContext();
                if (OPUtils.isWhiteModeOn(context.getContentResolver())) {
                    textView.setTextColor(context.getResources().getColor(C0006R$color.op_control_text_color_primary_light));
                    textView.setHintTextColor(Color.parseColor("#44444444"));
                } else {
                    textView.setTextColor(context.getResources().getColor(C0006R$color.op_control_text_color_primary_dark));
                    textView.setHintTextColor(Color.parseColor("#88888888"));
                }
                if (this.mExpandSearch) {
                    findItem.expandActionView();
                }
            }
            updateOptionsMenu();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        updateOptionsMenu();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyOptionsMenu() {
        this.mOptionsMenu = null;
    }

    /* access modifiers changed from: package-private */
    public int getHelpResource() {
        switch (this.mListType) {
            case 1:
                return C0017R$string.help_uri_notifications;
            case 2:
            case 12:
            default:
                return C0017R$string.help_uri_apps;
            case 3:
                return C0017R$string.help_uri_apps_storage;
            case 4:
                return C0017R$string.help_url_usage_access;
            case 5:
                return C0017R$string.help_uri_apps_high_power;
            case 6:
                return C0017R$string.help_uri_apps_overlay;
            case 7:
                return C0017R$string.help_uri_apps_write_settings;
            case 8:
                return C0017R$string.help_uri_apps_manage_sources;
            case 9:
                return C0017R$string.help_uri_apps_overlay;
            case 10:
                return C0017R$string.help_uri_apps_movies;
            case 11:
                return C0017R$string.help_uri_apps_photography;
            case 13:
                return C0017R$string.help_uri_apps_wifi_access;
            case 14:
                return C0017R$string.help_uri_manage_external_storage;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateOptionsMenu() {
        Menu menu = this.mOptionsMenu;
        if (menu != null) {
            menu.findItem(C0010R$id.advanced).setVisible(DEBUG);
            boolean z = true;
            this.mOptionsMenu.findItem(C0010R$id.sort_order_alpha).setVisible(this.mListType == 3 && this.mSortOrder != C0010R$id.sort_order_alpha);
            this.mOptionsMenu.findItem(C0010R$id.sort_order_size).setVisible(this.mListType == 3 && this.mSortOrder != C0010R$id.sort_order_size);
            this.mOptionsMenu.findItem(C0010R$id.show_system).setVisible(!this.mShowSystem && this.mListType != 5);
            this.mOptionsMenu.findItem(C0010R$id.hide_system).setVisible(this.mShowSystem && this.mListType != 5);
            this.mOptionsMenu.findItem(C0010R$id.reset_app_preferences).setVisible(this.mListType == 0);
            this.mOptionsMenu.findItem(C0010R$id.show_system).setVisible(!this.mShowSystem && this.mListType != 5);
            this.mOptionsMenu.findItem(C0010R$id.hide_system).setVisible(this.mShowSystem && this.mListType != 5);
            this.mOptionsMenu.findItem(C0010R$id.sort_order_recent_notification).setVisible(DEBUG);
            this.mOptionsMenu.findItem(C0010R$id.sort_order_frequent_notification).setVisible(DEBUG);
            MenuItem findItem = this.mOptionsMenu.findItem(C0010R$id.bg_optimize_preferences);
            if (this.mListType != 16) {
                z = false;
            }
            findItem.setVisible(z);
            int i = this.mListType;
            if (i == 16 || i == 17) {
                this.mOptionsMenu.findItem(C0010R$id.show_system).setVisible(DEBUG);
                this.mOptionsMenu.findItem(C0010R$id.hide_system).setVisible(DEBUG);
                this.mOptionsMenu.findItem(C0010R$id.reset_app_preferences).setVisible(DEBUG);
            }
            MenuItem findItem2 = this.mOptionsMenu.findItem(11);
            if (findItem2 != null) {
                findItem2.setVisible(DEBUG);
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        int itemId2 = menuItem.getItemId();
        if (itemId2 == C0010R$id.sort_order_alpha || itemId2 == C0010R$id.sort_order_size) {
            ApplicationsAdapter applicationsAdapter = this.mApplications;
            if (applicationsAdapter != null) {
                applicationsAdapter.rebuild(itemId);
            }
        } else if (itemId2 == C0010R$id.show_system || itemId2 == C0010R$id.hide_system) {
            this.mShowSystem = !this.mShowSystem;
            this.mApplications.rebuild();
        } else if (itemId2 == C0010R$id.reset_app_preferences) {
            this.mResetAppsHelper.buildResetDialog();
            return true;
        } else if (itemId2 == C0010R$id.advanced) {
            if (this.mListType == 1) {
                SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
                subSettingLauncher.setDestination(ConfigureNotificationSettings.class.getName());
                subSettingLauncher.setTitleRes(C0017R$string.configure_notification_settings);
                subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
                subSettingLauncher.setResultListener(this, 2);
                subSettingLauncher.launch();
            } else {
                startActivityForResult(new Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"), 2);
            }
            return true;
        } else if (itemId2 != C0010R$id.bg_optimize_preferences) {
            return DEBUG;
        } else {
            Intent intent = null;
            try {
                Intent intent2 = new Intent("com.android.settings.action.BACKGROUND_OPTIMIZE_SWITCH");
                try {
                    intent2.putExtra("classname", Settings.BgOptimizeSwitchActivity.class.getName());
                    getActivity().startActivity(intent2);
                } catch (ActivityNotFoundException unused) {
                    intent = intent2;
                }
            } catch (ActivityNotFoundException unused2) {
                Log.d("ManageApplications", "No activity found for " + intent);
                return true;
            }
            return true;
        }
        updateOptionsMenu();
        return true;
    }

    public void onClick(View view) {
        if (this.mApplications != null) {
            int childAdapterPosition = this.mRecyclerView.getChildAdapterPosition(view);
            if (childAdapterPosition == -1) {
                Log.w("ManageApplications", "Cannot find position for child, skipping onClick handling");
            } else if (this.mApplications.getApplicationCount() > childAdapterPosition) {
                ApplicationsState.AppEntry appEntry = this.mApplications.getAppEntry(childAdapterPosition);
                Object obj = appEntry.extraInfo;
                if (obj instanceof AppStateNotificationBridge.NotificationsSentState) {
                    this.mCurrentNotificationsSentState = (AppStateNotificationBridge.NotificationsSentState) obj;
                }
                ApplicationInfo applicationInfo = appEntry.info;
                this.mCurrentPkgName = applicationInfo.packageName;
                this.mCurrentUid = applicationInfo.uid;
                startApplicationDetailsActivity();
            } else {
                this.mApplications.mExtraViewController.onClick(this);
            }
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        this.mFilter = this.mFilterAdapter.getFilter(i);
        setCompositeFilter();
        this.mApplications.setFilter(this.mFilter);
        if (DEBUG) {
            Log.d("ManageApplications", "Selecting filter " + ((Object) getContext().getText(this.mFilter.getTitle())));
        }
    }

    @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextChange(String str) {
        this.mApplications.filterSearch(str);
        return DEBUG;
    }

    public void updateView() {
        updateOptionsMenu();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    public void setHasDisabled(boolean z) {
        if (this.mListType == 0) {
            this.mFilterAdapter.setFilterEnabled(3, z);
            this.mFilterAdapter.setFilterEnabled(5, z);
        }
    }

    public void setHasInstant(boolean z) {
        if (LIST_TYPES_WITH_INSTANT.contains(Integer.valueOf(this.mListType))) {
            this.mFilterAdapter.setFilterEnabled(4, z);
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<BatteryStatsHelper> onCreateLoader(int i, Bundle bundle) {
        return new BatteryStatsHelperLoader(getContext());
    }

    public void onLoadFinished(Loader<BatteryStatsHelper> loader, BatteryStatsHelper batteryStatsHelper) {
        this.mBatteryHelper = batteryStatsHelper;
    }

    /* access modifiers changed from: package-private */
    public static class FilterSpinnerAdapter extends OPSettingsSpinnerAdapter<CharSequence> {
        private final Context mContext;
        private final ArrayList<AppFilterItem> mFilterOptions = new ArrayList<>();
        private final ManageApplications mManageApplications;

        public FilterSpinnerAdapter(ManageApplications manageApplications) {
            super(manageApplications.getContext());
            this.mContext = manageApplications.getContext();
            this.mManageApplications = manageApplications;
        }

        public AppFilterItem getFilter(int i) {
            return this.mFilterOptions.get(i);
        }

        public void setFilterEnabled(int i, boolean z) {
            if (z) {
                enableFilter(i);
            } else {
                disableFilter(i);
            }
        }

        public void enableFilter(int i) {
            int indexOf;
            AppFilterItem appFilterItem = AppFilterRegistry.getInstance().get(i);
            if (!this.mFilterOptions.contains(appFilterItem)) {
                if (ManageApplications.DEBUG) {
                    Log.d("ManageApplications", "Enabling filter " + ((Object) this.mContext.getText(appFilterItem.getTitle())));
                }
                this.mFilterOptions.add(appFilterItem);
                Collections.sort(this.mFilterOptions);
                updateFilterView(this.mFilterOptions.size() > 1);
                notifyDataSetChanged();
                if (this.mFilterOptions.size() == 1) {
                    if (ManageApplications.DEBUG) {
                        Log.d("ManageApplications", "Auto selecting filter " + appFilterItem + " " + ((Object) this.mContext.getText(appFilterItem.getTitle())));
                    }
                    this.mManageApplications.mFilterSpinner.setSelection(0);
                    this.mManageApplications.onItemSelected(null, null, 0, 0);
                }
                if (this.mFilterOptions.size() > 1 && (indexOf = this.mFilterOptions.indexOf(AppFilterRegistry.getInstance().get(this.mManageApplications.mFilterType))) != -1) {
                    this.mManageApplications.mFilterSpinner.setSelection(indexOf);
                    this.mManageApplications.onItemSelected(null, null, indexOf, 0);
                }
            }
        }

        public void disableFilter(int i) {
            AppFilterItem appFilterItem = AppFilterRegistry.getInstance().get(i);
            if (this.mFilterOptions.remove(appFilterItem)) {
                if (ManageApplications.DEBUG) {
                    Log.d("ManageApplications", "Disabling filter " + appFilterItem + " " + ((Object) this.mContext.getText(appFilterItem.getTitle())));
                }
                Collections.sort(this.mFilterOptions);
                boolean z = true;
                if (this.mFilterOptions.size() <= 1) {
                    z = false;
                }
                updateFilterView(z);
                notifyDataSetChanged();
                if (this.mManageApplications.mFilter == appFilterItem && this.mFilterOptions.size() > 0) {
                    if (ManageApplications.DEBUG) {
                        Log.d("ManageApplications", "Auto selecting filter " + this.mFilterOptions.get(0) + ((Object) this.mContext.getText(this.mFilterOptions.get(0).getTitle())));
                    }
                    this.mManageApplications.mFilterSpinner.setSelection(0);
                    this.mManageApplications.onItemSelected(null, null, 0, 0);
                }
            }
        }

        public int getCount() {
            return this.mFilterOptions.size();
        }

        @Override // android.widget.ArrayAdapter
        public CharSequence getItem(int i) {
            return this.mContext.getText(this.mFilterOptions.get(i).getTitle());
        }

        /* access modifiers changed from: package-private */
        public void updateFilterView(boolean z) {
            if (z) {
                this.mManageApplications.mSpinnerHeader.setVisibility(0);
                this.mManageApplications.mRecyclerView.setPadding(0, this.mContext.getResources().getDimensionPixelSize(C0007R$dimen.app_bar_height), 0, 0);
                return;
            }
            this.mManageApplications.mSpinnerHeader.setVisibility(8);
            this.mManageApplications.mRecyclerView.setPadding(0, this.mContext.getResources().getDimensionPixelSize(C0007R$dimen.op_control_margin_space4), 0, 0);
        }
    }

    /* access modifiers changed from: package-private */
    public static class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationViewHolder> implements ApplicationsState.Callbacks, AppStateBaseBridge.Callback {
        private AppFilterItem mAppFilter;
        private PowerWhitelistBackend mBackend;
        private ApplicationsState.AppFilter mCompositeFilter;
        private final Context mContext;
        private ArrayList<ApplicationsState.AppEntry> mEntries;
        private final AppStateBaseBridge mExtraInfoBridge;
        private FileViewHolderController mExtraViewController;
        private boolean mHasReceivedBridgeCallback;
        private boolean mHasReceivedLoadEntries;
        private int mLastIndex = -1;
        private int mLastSortMode = -1;
        private final LoadingViewController mLoadingViewController;
        private final ManageApplications mManageApplications;
        OnScrollListener mOnScrollListener;
        private ArrayList<ApplicationsState.AppEntry> mOriginalEntries;
        private RecyclerView mRecyclerView;
        private boolean mResumed;
        private SearchFilter mSearchFilter;
        private final ApplicationsState.Session mSession;
        private final ApplicationsState mState;
        private int mWhichSize = 0;

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageIconChanged() {
        }

        public ApplicationsAdapter(ApplicationsState applicationsState, ManageApplications manageApplications, AppFilterItem appFilterItem, Bundle bundle) {
            setHasStableIds(true);
            this.mState = applicationsState;
            this.mSession = applicationsState.newSession(this);
            this.mManageApplications = manageApplications;
            this.mLoadingViewController = new LoadingViewController(manageApplications.mLoadingContainer, this.mManageApplications.mListContainer);
            FragmentActivity activity = manageApplications.getActivity();
            this.mContext = activity;
            IconDrawableFactory.newInstance(activity);
            this.mAppFilter = appFilterItem;
            this.mBackend = PowerWhitelistBackend.getInstance(this.mContext);
            int i = this.mManageApplications.mListType;
            if (i == 1) {
                this.mExtraInfoBridge = new AppStateNotificationBridge(this.mContext, this.mState, this, manageApplications.mUsageStatsManager, manageApplications.mUserManager, manageApplications.mNotificationBackend);
            } else if (i == 4) {
                this.mExtraInfoBridge = new AppStateUsageBridge(this.mContext, this.mState, this);
            } else if (i == 5) {
                this.mExtraInfoBridge = new AppStatePowerBridge(this.mContext, this.mState, this);
            } else if (i == 6) {
                this.mExtraInfoBridge = new AppStateOverlayBridge(this.mContext, this.mState, this);
            } else if (i == 7) {
                this.mExtraInfoBridge = new AppStateWriteSettingsBridge(this.mContext, this.mState, this);
            } else if (i == 8) {
                this.mExtraInfoBridge = new AppStateInstallAppsBridge(this.mContext, this.mState, this);
            } else if (i == 13) {
                this.mExtraInfoBridge = new AppStateChangeWifiStateBridge(this.mContext, this.mState, this);
            } else if (i == 14) {
                this.mExtraInfoBridge = new AppStateManageExternalStorageBridge(this.mContext, this.mState, this);
            } else if (i == 15) {
                this.mExtraInfoBridge = new DisplaySizeAdaptionBridge(this.mContext, this.mState, this);
            } else if (i == 16) {
                this.mExtraInfoBridge = new AppBgOptimizeBridge(this.mContext, this.mState, this);
            } else if (i == 17) {
                this.mExtraInfoBridge = new ReadingModeEffectSelectBridge(this.mContext, this.mState, this);
            } else {
                this.mExtraInfoBridge = null;
            }
            if (bundle != null) {
                this.mLastIndex = bundle.getInt("state_last_scroll_index");
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            this.mRecyclerView = recyclerView;
            OnScrollListener onScrollListener = new OnScrollListener(this);
            this.mOnScrollListener = onScrollListener;
            this.mRecyclerView.addOnScrollListener(onScrollListener);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            this.mRecyclerView.removeOnScrollListener(this.mOnScrollListener);
            this.mOnScrollListener = null;
            this.mRecyclerView = null;
        }

        public void setCompositeFilter(ApplicationsState.AppFilter appFilter) {
            this.mCompositeFilter = appFilter;
            rebuild();
        }

        public void setFilter(AppFilterItem appFilterItem) {
            this.mAppFilter = appFilterItem;
            if (7 == appFilterItem.getFilterType()) {
                rebuild(C0010R$id.sort_order_frequent_notification);
            } else if (6 == appFilterItem.getFilterType()) {
                rebuild(C0010R$id.sort_order_recent_notification);
            } else if (16 == appFilterItem.getFilterType()) {
                rebuild(C0010R$id.sort_order_alpha);
            } else {
                rebuild();
            }
        }

        public void setExtraViewController(FileViewHolderController fileViewHolderController) {
            this.mExtraViewController = fileViewHolderController;
            ThreadUtils.postOnBackgroundThread(new Runnable() {
                /* class com.android.settings.applications.manageapplications.$$Lambda$ManageApplications$ApplicationsAdapter$qMEtWjKuRu1RgrWKYhFScJDD7E */

                public final void run() {
                    ManageApplications.ApplicationsAdapter.this.lambda$setExtraViewController$1$ManageApplications$ApplicationsAdapter();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$setExtraViewController$1 */
        public /* synthetic */ void lambda$setExtraViewController$1$ManageApplications$ApplicationsAdapter() {
            this.mExtraViewController.queryStats();
            ThreadUtils.postOnMainThread(new Runnable() {
                /* class com.android.settings.applications.manageapplications.$$Lambda$ManageApplications$ApplicationsAdapter$zUDf4sT2ElTE4vuQaXRj16znehk */

                public final void run() {
                    ManageApplications.ApplicationsAdapter.this.lambda$setExtraViewController$0$ManageApplications$ApplicationsAdapter();
                }
            });
        }

        public void resume(int i) {
            if (ManageApplications.DEBUG) {
                Log.i("ManageApplications", "Resume!  mResumed=" + this.mResumed);
            }
            if (!this.mResumed) {
                this.mResumed = true;
                this.mSession.onResume();
                this.mLastSortMode = i;
                AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
                if (appStateBaseBridge != null) {
                    appStateBaseBridge.resume();
                }
                rebuild();
                return;
            }
            rebuild(i);
        }

        public void pause() {
            if (this.mResumed) {
                this.mResumed = ManageApplications.DEBUG;
                this.mSession.onPause();
                AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
                if (appStateBaseBridge != null) {
                    appStateBaseBridge.pause();
                }
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            bundle.putInt("state_last_scroll_index", ((LinearLayoutManager) this.mManageApplications.mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
        }

        public void release() {
            this.mSession.onDestroy();
            AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
            if (appStateBaseBridge != null) {
                appStateBaseBridge.release();
            }
        }

        public void rebuild(int i) {
            if (i != this.mLastSortMode) {
                this.mManageApplications.mSortOrder = i;
                this.mLastSortMode = i;
                rebuild();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ApplicationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (this.mManageApplications.mListType == 1) {
                view = ApplicationViewHolder.newView(viewGroup, true);
            } else {
                view = ApplicationViewHolder.newView(viewGroup, ManageApplications.DEBUG);
            }
            return new ApplicationViewHolder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return (!hasExtraView() || !(getItemCount() - 1 == i)) ? 0 : 1;
        }

        public void rebuild() {
            Comparator<ApplicationsState.AppEntry> comparator;
            ApplicationsState.CompoundFilter compoundFilter;
            boolean z = this.mHasReceivedLoadEntries;
            boolean z2 = ManageApplications.DEBUG;
            if (z && (this.mExtraInfoBridge == null || this.mHasReceivedBridgeCallback)) {
                if (Environment.isExternalStorageEmulated()) {
                    this.mWhichSize = 0;
                } else {
                    this.mWhichSize = 1;
                }
                ApplicationsState.CompoundFilter filter = this.mAppFilter.getFilter();
                ApplicationsState.AppFilter appFilter = this.mCompositeFilter;
                if (appFilter != null) {
                    filter = new ApplicationsState.CompoundFilter(filter, appFilter);
                }
                if (!this.mManageApplications.mShowSystem) {
                    if (ManageApplications.LIST_TYPES_WITH_INSTANT.contains(Integer.valueOf(this.mManageApplications.mListType))) {
                        compoundFilter = new ApplicationsState.CompoundFilter(filter, ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER_AND_INSTANT);
                    } else {
                        compoundFilter = new ApplicationsState.CompoundFilter(filter, ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER);
                    }
                    filter = compoundFilter;
                }
                int i = this.mLastSortMode;
                if (i == C0010R$id.sort_order_size) {
                    int i2 = this.mWhichSize;
                    if (i2 == 1) {
                        comparator = ApplicationsState.INTERNAL_SIZE_COMPARATOR;
                    } else if (i2 != 2) {
                        comparator = ApplicationsState.SIZE_COMPARATOR;
                    } else {
                        comparator = ApplicationsState.EXTERNAL_SIZE_COMPARATOR;
                    }
                } else if (i == C0010R$id.sort_order_recent_notification) {
                    comparator = AppStateNotificationBridge.RECENT_NOTIFICATION_COMPARATOR;
                } else if (i == C0010R$id.sort_order_frequent_notification) {
                    comparator = AppStateNotificationBridge.FREQUENCY_NOTIFICATION_COMPARATOR;
                } else {
                    comparator = ApplicationsState.ALPHA_COMPARATOR;
                }
                ThreadUtils.postOnBackgroundThread(new Runnable(new ApplicationsState.CompoundFilter(filter, ApplicationsState.FILTER_NOT_HIDE), comparator) {
                    /* class com.android.settings.applications.manageapplications.$$Lambda$ManageApplications$ApplicationsAdapter$pKSMVqhB3s4VkYX3RVHDOzUgo */
                    public final /* synthetic */ ApplicationsState.AppFilter f$1;
                    public final /* synthetic */ Comparator f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        ManageApplications.ApplicationsAdapter.this.lambda$rebuild$2$ManageApplications$ApplicationsAdapter(this.f$1, this.f$2);
                    }
                });
            } else if (ManageApplications.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Not rebuilding until all the app entries loaded. !mHasReceivedLoadEntries=");
                sb.append(!this.mHasReceivedLoadEntries);
                sb.append(" !mExtraInfoBridgeNull=");
                if (this.mExtraInfoBridge != null) {
                    z2 = true;
                }
                sb.append(z2);
                sb.append(" !mHasReceivedBridgeCallback=");
                sb.append(!this.mHasReceivedBridgeCallback);
                Log.d("ManageApplications", sb.toString());
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$rebuild$2 */
        public /* synthetic */ void lambda$rebuild$2$ManageApplications$ApplicationsAdapter(ApplicationsState.AppFilter appFilter, Comparator comparator) {
            this.mSession.rebuild(appFilter, comparator, ManageApplications.DEBUG);
        }

        /* access modifiers changed from: package-private */
        public void filterSearch(String str) {
            if (this.mSearchFilter == null) {
                this.mSearchFilter = new SearchFilter();
            }
            if (this.mOriginalEntries == null) {
                Log.w("ManageApplications", "Apps haven't loaded completely yet, so nothing can be filtered");
            } else {
                this.mSearchFilter.filter(str);
            }
        }

        private static boolean packageNameEquals(PackageItemInfo packageItemInfo, PackageItemInfo packageItemInfo2) {
            String str;
            String str2;
            if (packageItemInfo == null || packageItemInfo2 == null || (str = packageItemInfo.packageName) == null || (str2 = packageItemInfo2.packageName) == null) {
                return ManageApplications.DEBUG;
            }
            return str.equals(str2);
        }

        private ArrayList<ApplicationsState.AppEntry> removeDuplicateIgnoringUser(ArrayList<ApplicationsState.AppEntry> arrayList) {
            int size = arrayList.size();
            ArrayList<ApplicationsState.AppEntry> arrayList2 = new ArrayList<>(size);
            ApplicationInfo applicationInfo = null;
            int i = 0;
            while (i < size) {
                ApplicationsState.AppEntry appEntry = arrayList.get(i);
                ApplicationInfo applicationInfo2 = appEntry.info;
                if (!packageNameEquals(applicationInfo, applicationInfo2)) {
                    arrayList2.add(appEntry);
                }
                i++;
                applicationInfo = applicationInfo2;
            }
            arrayList2.trimToSize();
            return arrayList2;
        }

        @SuppressLint({"VisibleForTests"})
        private ArrayList<ApplicationsState.AppEntry> addInstantAppIgnoringUser(ArrayList<ApplicationsState.AppEntry> arrayList) {
            Cursor query = this.mContext.getContentResolver().query(AppStateNotificationBridge.BASE_URI, null, null, null, null);
            if (query == null) {
                return arrayList;
            }
            ArrayList<ApplicationsState.AppEntry> arrayList2 = new ArrayList<>(arrayList);
            if (OPUtils.isAppExist(this.mContext, "com.nearme.instant.platform")) {
                ApplicationInfo applicationInfo = null;
                try {
                    applicationInfo = this.mContext.getPackageManager().getApplicationInfo("com.nearme.instant.platform", 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                int i = 1;
                while (query.moveToNext() && applicationInfo != null) {
                    int i2 = i + 1;
                    ApplicationsState.AppEntry appEntry = new ApplicationsState.AppEntry(this.mContext, applicationInfo, (long) i);
                    AppStateNotificationBridge.NotificationsSentState notificationsSentState = new AppStateNotificationBridge.NotificationsSentState();
                    notificationsSentState.avgSentDaily = 1;
                    notificationsSentState.avgSentWeekly = 1;
                    notificationsSentState.instantApp = true;
                    notificationsSentState.systemApp = ManageApplications.DEBUG;
                    notificationsSentState.lastSent = System.currentTimeMillis() - 1000;
                    notificationsSentState.sentCount = 1;
                    String string = query.getString(query.getColumnIndex("name"));
                    String string2 = query.getString(query.getColumnIndex("pkg"));
                    byte[] blob = query.getBlob(query.getColumnIndex("icon"));
                    Bitmap decodeByteArray = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                    notificationsSentState.instantAppPKG = string2;
                    notificationsSentState.instantAppName = string;
                    notificationsSentState.instantAppIcon = new BitmapDrawable(this.mContext.getResources(), decodeByteArray);
                    notificationsSentState.blocked = query.getInt(query.getColumnIndex("notify")) == 0;
                    notificationsSentState.blockable = true;
                    appEntry.extraInfo = notificationsSentState;
                    arrayList2.add(appEntry);
                    i = i2;
                }
                query.close();
            }
            arrayList2.trimToSize();
            return arrayList2;
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
            if (ManageApplications.DEBUG) {
                Log.d("ManageApplications", "onRebuildComplete size=" + arrayList.size());
            }
            int filterType = this.mAppFilter.getFilterType();
            if (filterType == 0 || filterType == 1) {
                arrayList = removeDuplicateIgnoringUser(arrayList);
            }
            if (this.mManageApplications.mListType == 1) {
                arrayList = addInstantAppIgnoringUser(arrayList);
            }
            this.mEntries = arrayList;
            this.mOriginalEntries = arrayList;
            notifyDataSetChanged();
            if (getItemCount() == 0) {
                this.mManageApplications.mRecyclerView.setVisibility(8);
                this.mManageApplications.mEmptyView.setVisibility(0);
            } else {
                this.mManageApplications.mEmptyView.setVisibility(8);
                this.mManageApplications.mRecyclerView.setVisibility(0);
                if (this.mManageApplications.mSearchView != null && this.mManageApplications.mSearchView.isVisibleToUser()) {
                    CharSequence query = this.mManageApplications.mSearchView.getQuery();
                    if (!TextUtils.isEmpty(query)) {
                        filterSearch(query.toString());
                    }
                }
            }
            if (this.mLastIndex != -1 && getItemCount() > this.mLastIndex) {
                this.mManageApplications.mRecyclerView.getLayoutManager().scrollToPosition(this.mLastIndex);
                this.mLastIndex = -1;
            }
            if (!(this.mSession.getAllApps().size() == 0 || this.mManageApplications.mListContainer.getVisibility() == 0)) {
                this.mLoadingViewController.showContent(true);
            }
            ManageApplications manageApplications = this.mManageApplications;
            if (manageApplications.mListType != 4) {
                manageApplications.setHasDisabled(this.mState.haveDisabledApps());
                this.mManageApplications.setHasInstant(this.mState.haveInstantApps());
            }
        }

        /* access modifiers changed from: package-private */
        public void updateLoading() {
            if (this.mHasReceivedLoadEntries && this.mSession.getAllApps().size() != 0) {
                this.mLoadingViewController.showContent(ManageApplications.DEBUG);
            } else {
                this.mLoadingViewController.showLoadingViewDelayed();
            }
        }

        @Override // com.android.settings.applications.AppStateBaseBridge.Callback
        public void onExtraInfoUpdated() {
            this.mHasReceivedBridgeCallback = true;
            rebuild();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onRunningStateChanged(boolean z) {
            this.mManageApplications.getActivity().setProgressBarIndeterminateVisibility(z);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageListChanged() {
            rebuild();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onLoadEntriesCompleted() {
            this.mHasReceivedLoadEntries = true;
            rebuild();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageSizeChanged(String str) {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    ApplicationInfo applicationInfo = this.mEntries.get(i).info;
                    if (applicationInfo != null || TextUtils.equals(str, applicationInfo.packageName)) {
                        if (TextUtils.equals(this.mManageApplications.mCurrentPkgName, applicationInfo.packageName)) {
                            rebuild();
                            return;
                        }
                        this.mOnScrollListener.postNotifyItemChange(i);
                    }
                }
            }
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onLauncherInfoChanged() {
            if (!this.mManageApplications.mShowSystem) {
                rebuild();
            }
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onAllSizesComputed() {
            if (this.mLastSortMode == C0010R$id.sort_order_size) {
                rebuild();
            }
        }

        /* renamed from: onExtraViewCompleted */
        public void lambda$setExtraViewController$0() {
            if (hasExtraView()) {
                notifyItemChanged(getItemCount() - 1);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.size() + (hasExtraView() ? 1 : 0);
        }

        public int getApplicationCount() {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            if (arrayList != null) {
                return arrayList.size();
            }
            return 0;
        }

        public ApplicationsState.AppEntry getAppEntry(int i) {
            return this.mEntries.get(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            if (i == this.mEntries.size()) {
                return -1;
            }
            return this.mEntries.get(i).id;
        }

        public boolean isEnabled(int i) {
            if (getItemViewType(i) == 1 || this.mManageApplications.mListType != 5) {
                return true;
            }
            ApplicationsState.AppEntry appEntry = this.mEntries.get(i);
            if (this.mBackend.isSysWhitelisted(appEntry.info.packageName) || this.mBackend.isDefaultActiveApp(appEntry.info.packageName)) {
                return ManageApplications.DEBUG;
            }
            return true;
        }

        public void onBindViewHolder(ApplicationViewHolder applicationViewHolder, int i) {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            if (arrayList == null || this.mExtraViewController == null || i != arrayList.size()) {
                ApplicationsState.AppEntry appEntry = this.mEntries.get(i);
                synchronized (appEntry) {
                    applicationViewHolder.setTitle(appEntry.label);
                    this.mState.ensureLabelDescription(appEntry);
                    applicationViewHolder.itemView.setContentDescription(appEntry.labelDescription);
                    this.mState.ensureIcon(appEntry);
                    applicationViewHolder.setIcon(appEntry.icon);
                    updateSummary(applicationViewHolder, appEntry);
                    updateSwitch(applicationViewHolder, appEntry);
                    applicationViewHolder.updateDisableView(appEntry.info);
                }
                applicationViewHolder.setEnabled(isEnabled(i));
            } else {
                this.mExtraViewController.setupView(applicationViewHolder);
            }
            applicationViewHolder.itemView.setOnClickListener(this.mManageApplications);
        }

        private void updateSummary(ApplicationViewHolder applicationViewHolder, ApplicationsState.AppEntry appEntry) {
            int i;
            ManageApplications manageApplications = this.mManageApplications;
            int i2 = manageApplications.mListType;
            if (i2 != 1) {
                switch (i2) {
                    case 4:
                        Object obj = appEntry.extraInfo;
                        if (obj != null) {
                            if (new AppStateUsageBridge.UsageState((AppStateAppOpsBridge.PermissionState) obj).isPermissible()) {
                                i = C0017R$string.app_permission_summary_allowed;
                            } else {
                                i = C0017R$string.app_permission_summary_not_allowed;
                            }
                            applicationViewHolder.setSummary(i);
                            return;
                        }
                        applicationViewHolder.setSummary((CharSequence) null);
                        return;
                    case 5:
                        applicationViewHolder.setSummary(HighPowerDetail.getSummary(this.mContext, appEntry));
                        return;
                    case 6:
                        applicationViewHolder.setSummary(DrawOverlayDetails.getSummary(this.mContext, appEntry));
                        return;
                    case 7:
                        applicationViewHolder.setSummary(WriteSettingsDetails.getSummary(this.mContext, appEntry));
                        return;
                    case 8:
                        applicationViewHolder.setSummary(ExternalSourcesDetails.getPreferenceSummary(this.mContext, appEntry));
                        return;
                    default:
                        switch (i2) {
                            case 13:
                                applicationViewHolder.setSummary(ChangeWifiStateDetails.getSummary(this.mContext, appEntry));
                                return;
                            case 14:
                                applicationViewHolder.setSummary(ManageExternalStorageDetails.getSummary(this.mContext, appEntry));
                                return;
                            case 15:
                                applicationViewHolder.setSummary(DisplaySizeAdaptionDetail.getSummary(this.mContext, appEntry));
                                return;
                            case 16:
                                applicationViewHolder.setSummary(AppInfoBase.getSummary(this.mContext, appEntry));
                                return;
                            case 17:
                                applicationViewHolder.setSummary(ReadingModeEffectDetail.getSummary(this.mContext, appEntry));
                                return;
                            default:
                                applicationViewHolder.updateSizeText(appEntry, manageApplications.mInvalidSizeStr, this.mWhichSize);
                                return;
                        }
                }
            } else {
                Object obj2 = appEntry.extraInfo;
                if (obj2 instanceof AppStateNotificationBridge.NotificationsSentState) {
                    applicationViewHolder.setSummary(AppStateNotificationBridge.getSummary(this.mContext, (AppStateNotificationBridge.NotificationsSentState) obj2, this.mLastSortMode));
                    Object obj3 = appEntry.extraInfo;
                    if (((AppStateNotificationBridge.NotificationsSentState) obj3).instantApp) {
                        applicationViewHolder.setIcon(((AppStateNotificationBridge.NotificationsSentState) obj3).instantAppIcon);
                        applicationViewHolder.setTitle(((AppStateNotificationBridge.NotificationsSentState) appEntry.extraInfo).instantAppName);
                        return;
                    }
                    return;
                }
                applicationViewHolder.setSummary((CharSequence) null);
            }
        }

        private void updateSwitch(ApplicationViewHolder applicationViewHolder, ApplicationsState.AppEntry appEntry) {
            if (this.mManageApplications.mListType == 1) {
                if ("com.oneplus.deskclock".equals(appEntry.info.packageName) || "com.android.incallui".equals(appEntry.info.packageName) || "com.google.android.calendar".equals(appEntry.info.packageName) || "com.oneplus.calendar".equals(appEntry.info.packageName) || "com.android.dialer".equals(appEntry.info.packageName) || "com.google.android.dialer".equals(appEntry.info.packageName) || "com.oneplus.dialer".equals(appEntry.info.packageName)) {
                    applicationViewHolder.updateSwitch(((AppStateNotificationBridge) this.mExtraInfoBridge).getSwitchOnClickListener(appEntry), ManageApplications.DEBUG, AppStateNotificationBridge.checkSwitch(appEntry));
                } else {
                    applicationViewHolder.updateSwitch(((AppStateNotificationBridge) this.mExtraInfoBridge).getSwitchOnClickListener(appEntry), AppStateNotificationBridge.enableSwitch(appEntry), AppStateNotificationBridge.checkSwitch(appEntry));
                }
                Object obj = appEntry.extraInfo;
                if (obj == null || !(obj instanceof AppStateNotificationBridge.NotificationsSentState)) {
                    applicationViewHolder.setSummary((CharSequence) null);
                } else {
                    applicationViewHolder.setSummary(AppStateNotificationBridge.getSummary(this.mContext, (AppStateNotificationBridge.NotificationsSentState) obj, this.mLastSortMode));
                }
            }
        }

        private boolean hasExtraView() {
            FileViewHolderController fileViewHolderController = this.mExtraViewController;
            if (fileViewHolderController == null || !fileViewHolderController.shouldShow()) {
                return ManageApplications.DEBUG;
            }
            return true;
        }

        public static class OnScrollListener extends RecyclerView.OnScrollListener {
            private ApplicationsAdapter mAdapter;
            private boolean mDelayNotifyDataChange;
            private int mScrollState = 0;

            public OnScrollListener(ApplicationsAdapter applicationsAdapter) {
                this.mAdapter = applicationsAdapter;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                this.mScrollState = i;
                if (i == 0 && this.mDelayNotifyDataChange) {
                    this.mDelayNotifyDataChange = ManageApplications.DEBUG;
                    this.mAdapter.notifyDataSetChanged();
                }
            }

            public void postNotifyItemChange(int i) {
                if (this.mScrollState == 0) {
                    this.mAdapter.notifyItemChanged(i);
                } else {
                    this.mDelayNotifyDataChange = true;
                }
            }
        }

        /* access modifiers changed from: private */
        public class SearchFilter extends Filter {
            private SearchFilter() {
            }

            /* access modifiers changed from: protected */
            public Filter.FilterResults performFiltering(CharSequence charSequence) {
                ArrayList arrayList;
                if (TextUtils.isEmpty(charSequence)) {
                    arrayList = ApplicationsAdapter.this.mOriginalEntries;
                } else {
                    ArrayList arrayList2 = new ArrayList();
                    Iterator it = ApplicationsAdapter.this.mOriginalEntries.iterator();
                    while (it.hasNext()) {
                        ApplicationsState.AppEntry appEntry = (ApplicationsState.AppEntry) it.next();
                        if (appEntry.label.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            arrayList2.add(appEntry);
                        }
                    }
                    arrayList = arrayList2;
                }
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = arrayList;
                filterResults.count = arrayList.size();
                return filterResults;
            }

            /* access modifiers changed from: protected */
            public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                ApplicationsAdapter.this.mEntries = (ArrayList) filterResults.values;
                ApplicationsAdapter.this.notifyDataSetChanged();
            }
        }
    }
}
