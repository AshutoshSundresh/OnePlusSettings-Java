package com.android.settings.wifi;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkTemplate;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.R$styleable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.LinkifyUtils;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.SettingsActivity;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.datausage.DataUsagePreference;
import com.android.settings.datausage.DataUsageUtils;
import com.android.settings.location.ScanningSettings;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBarController;
import com.android.settings.wifi.ConnectedAccessPointPreference;
import com.android.settings.wifi.WifiDialog;
import com.android.settings.wifi.details.WifiNetworkDetailsFragment;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPointPreference;
import com.android.settingslib.wifi.WifiSavedConfigUtils;
import com.android.settingslib.wifi.WifiTracker;
import com.android.settingslib.wifi.WifiTrackerFactory;
import java.util.List;

public class WifiSettings extends RestrictedSettingsFragment implements WifiTracker.WifiListener, AccessPoint.AccessPointListener, WifiDialog.WifiDialogListener, DialogInterface.OnDismissListener {
    static final int ADD_NETWORK_REQUEST = 2;
    static final int MENU_ID_FORGET = 8;
    static final String PREF_KEY_DATA_USAGE = "wifi_data_usage";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.wifi_settings) {
        /* class com.android.settings.wifi.WifiSettings.AnonymousClass7 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (WifiSavedConfigUtils.getAllConfigsCount(context, (WifiManager) context.getSystemService(WifiManager.class)) == 0) {
                nonIndexableKeys.add("saved_networks");
            }
            if (!DataUsageUtils.hasWifiRadio(context)) {
                nonIndexableKeys.add(WifiSettings.PREF_KEY_DATA_USAGE);
            }
            return nonIndexableKeys;
        }
    };
    private Bundle mAccessPointSavedState;
    private PreferenceCategory mAccessPointsPreferenceCategory;
    AddWifiNetworkPreference mAddWifiNetworkPreference;
    CaptivePortalNetworkCallback mCaptivePortalNetworkCallback;
    private boolean mClickedConnect;
    Preference mConfigureWifiSettingsPreference;
    private WifiManager.ActionListener mConnectListener;
    int mConnectSource = 0;
    private PreferenceCategory mConnectedAccessPointPreferenceCategory;
    ConnectivityManager mConnectivityManager;
    DataUsagePreference mDataUsagePreference;
    private WifiDialog mDialog;
    private int mDialogMode;
    private AccessPoint mDlgAccessPoint;
    private boolean mEnableNextOnConnection;
    private WifiManager.ActionListener mForgetListener;
    private final Runnable mHideProgressBarRunnable = new Runnable() {
        /* class com.android.settings.wifi.$$Lambda$WifiSettings$ojra5gZ2Zt1OL2cVDalsbhFOQY0 */

        public final void run() {
            WifiSettings.this.lambda$new$1$WifiSettings();
        }
    };
    private boolean mIsRestricted;
    private Network mLastNetworkCaptivePortalAppStarted;
    private String mOpenSsid;
    private View mProgressHeader;
    private WifiManager.ActionListener mSaveListener;
    Preference mSavedNetworksPreference;
    private AccessPoint mSelectedAccessPoint;
    private LinkablePreference mStatusMessagePreference;
    private final Runnable mUpdateAccessPointsRunnable = new Runnable() {
        /* class com.android.settings.wifi.$$Lambda$WifiSettings$Dc8tARLt9797q5fiCWMG56ysJZ4 */

        public final void run() {
            WifiSettings.this.lambda$new$0$WifiSettings();
        }
    };
    private AccessPointPreference.UserBadgeCache mUserBadgeCache;
    private WifiEnabler mWifiEnabler;
    WifiManager mWifiManager;
    WifiTracker mWifiTracker;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return i != 1 ? 0 : 603;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return R$styleable.Constraint_layout_goneMarginTop;
    }

    private static boolean isVerboseLoggingEnabled() {
        return WifiTracker.sVerboseLogging || Log.isLoggable("WifiSettings", 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$WifiSettings() {
        setProgressBarVisible(false);
    }

    public WifiSettings() {
        super("no_config_wifi");
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            this.mProgressHeader = setPinnedHeaderView(C0012R$layout.op_progress_header).findViewById(C0010R$id.progress_bar_animation);
            setProgressBarVisible(false);
        }
        SwitchBar switchBar = ((SettingsActivity) activity).getSwitchBar();
        int i = C0017R$string.wifi_settings_master_switch_title;
        switchBar.setSwitchBarText(i, i);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (FeatureFlagUtils.isEnabled(getContext(), "settings_wifitracker2")) {
            Intent intent = new Intent("android.settings.WIFI_SETTINGS2");
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) {
                intent.putExtras(extras);
            }
            intent.setFlags(67108864);
            getContext().startActivity(intent);
            finish();
            return;
        }
        setAnimationAllowed(false);
        addPreferences();
        this.mIsRestricted = isUiRestricted();
    }

    private void addPreferences() {
        addPreferencesFromResource(C0019R$xml.wifi_settings);
        this.mConnectedAccessPointPreferenceCategory = (PreferenceCategory) findPreference("connected_access_point");
        this.mAccessPointsPreferenceCategory = (PreferenceCategory) findPreference("access_points");
        this.mConfigureWifiSettingsPreference = findPreference("configure_wifi_settings");
        this.mSavedNetworksPreference = findPreference("saved_networks");
        this.mAddWifiNetworkPreference = new AddWifiNetworkPreference(getPrefContext());
        this.mStatusMessagePreference = (LinkablePreference) findPreference("wifi_status_message");
        this.mUserBadgeCache = new AccessPointPreference.UserBadgeCache(getPackageManager());
        DataUsagePreference dataUsagePreference = (DataUsagePreference) findPreference(PREF_KEY_DATA_USAGE);
        this.mDataUsagePreference = dataUsagePreference;
        dataUsagePreference.setVisible(DataUsageUtils.hasWifiRadio(getContext()));
        this.mDataUsagePreference.setTemplate(NetworkTemplate.buildTemplateWifiWildcard(), 0, null);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        ConnectivityManager connectivityManager;
        super.onActivityCreated(bundle);
        WifiTracker create = WifiTrackerFactory.create(getActivity(), this, getSettingsLifecycle(), true, true);
        this.mWifiTracker = create;
        this.mWifiManager = create.getManager();
        if (getActivity() != null) {
            this.mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(ConnectivityManager.class);
        }
        this.mConnectListener = new WifiConnectListener(getActivity());
        this.mSaveListener = new WifiManager.ActionListener() {
            /* class com.android.settings.wifi.WifiSettings.AnonymousClass1 */

            public void onSuccess() {
            }

            public void onFailure(int i) {
                FragmentActivity activity = WifiSettings.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, C0017R$string.wifi_failed_save_message, 0).show();
                }
            }
        };
        this.mForgetListener = new WifiManager.ActionListener() {
            /* class com.android.settings.wifi.WifiSettings.AnonymousClass2 */

            public void onSuccess() {
            }

            public void onFailure(int i) {
                FragmentActivity activity = WifiSettings.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, C0017R$string.wifi_failed_forget_message, 0).show();
                }
            }
        };
        if (bundle != null) {
            this.mDialogMode = bundle.getInt("dialog_mode");
            if (bundle.containsKey("wifi_ap_state")) {
                this.mAccessPointSavedState = bundle.getBundle("wifi_ap_state");
            }
        }
        Intent intent = getActivity().getIntent();
        boolean booleanExtra = intent.getBooleanExtra("wifi_enable_next_on_connect", false);
        this.mEnableNextOnConnection = booleanExtra;
        if (booleanExtra && hasNextButton() && (connectivityManager = (ConnectivityManager) getActivity().getSystemService("connectivity")) != null) {
            changeNextButtonState(connectivityManager.getNetworkInfo(1).isConnected());
        }
        registerForContextMenu(getListView());
        setHasOptionsMenu(true);
        if (intent.hasExtra("wifi_start_connect_ssid")) {
            this.mOpenSsid = intent.getStringExtra("wifi_start_connect_ssid");
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.teardownSwitchController();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        this.mWifiEnabler = createWifiEnabler();
        if (this.mIsRestricted) {
            restrictUi();
        } else {
            onWifiStateChanged(this.mWifiManager.getWifiState());
        }
    }

    private void restrictUi() {
        if (!isUiRestrictedByOnlyAdmin()) {
            getEmptyTextView().setText(C0017R$string.wifi_empty_list_user_restricted);
        }
        getPreferenceScreen().removeAll();
    }

    private WifiEnabler createWifiEnabler() {
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        return new WifiEnabler(settingsActivity, new SwitchBarController(settingsActivity.getSwitchBar()), this.mMetricsFeatureProvider);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        FragmentActivity activity = getActivity();
        super.onResume();
        boolean z = this.mIsRestricted;
        boolean isUiRestricted = isUiRestricted();
        this.mIsRestricted = isUiRestricted;
        if (!z && isUiRestricted) {
            restrictUi();
        }
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.resume(activity);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.pause();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        getView().removeCallbacks(this.mUpdateAccessPointsRunnable);
        getView().removeCallbacks(this.mHideProgressBarRunnable);
        unregisterCaptivePortalNetworkCallback();
        super.onStop();
    }

    @Override // com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 2) {
            handleAddNetworkRequest(i2, intent);
        } else if (i == 0) {
            if (i2 == -1) {
                WifiDialog wifiDialog = this.mDialog;
                if (wifiDialog != null) {
                    wifiDialog.dismiss();
                }
                this.mWifiTracker.resumeScanning();
            }
        } else if (i != 3) {
            boolean z = this.mIsRestricted;
            boolean isUiRestricted = isUiRestricted();
            this.mIsRestricted = isUiRestricted;
            if (z && !isUiRestricted && getPreferenceScreen().getPreferenceCount() == 0) {
                addPreferences();
            }
        } else if (i2 == -1) {
            handleConfigNetworkSubmitEvent(intent);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        RecyclerView.Adapter onCreateAdapter = super.onCreateAdapter(preferenceScreen);
        onCreateAdapter.setHasStableIds(true);
        return onCreateAdapter;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mDialog != null) {
            bundle.putInt("dialog_mode", this.mDialogMode);
            if (this.mDlgAccessPoint != null) {
                Bundle bundle2 = new Bundle();
                this.mAccessPointSavedState = bundle2;
                this.mDlgAccessPoint.saveWifiState(bundle2);
                bundle.putBundle("wifi_ap_state", this.mAccessPointSavedState);
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        Preference preference = (Preference) view.getTag();
        if (preference instanceof LongPressAccessPointPreference) {
            AccessPoint accessPoint = ((LongPressAccessPointPreference) preference).getAccessPoint();
            this.mSelectedAccessPoint = accessPoint;
            contextMenu.setHeaderTitle(accessPoint.getTitle());
            if (this.mSelectedAccessPoint.isConnectable()) {
                contextMenu.add(0, 7, 0, C0017R$string.wifi_connect);
            }
            if (!WifiUtils.isNetworkLockedDown(getActivity(), this.mSelectedAccessPoint.getConfig())) {
                if (this.mSelectedAccessPoint.isSaved() || this.mSelectedAccessPoint.isEphemeral()) {
                    contextMenu.add(0, 8, 0, this.mSelectedAccessPoint.isEphemeral() ? C0017R$string.wifi_disconnect_button_text : C0017R$string.forget);
                }
                if (this.mSelectedAccessPoint.isSaved()) {
                    contextMenu.add(0, 9, 0, C0017R$string.wifi_modify);
                }
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onContextItemSelected(MenuItem menuItem) {
        if (this.mSelectedAccessPoint == null) {
            return super.onContextItemSelected(menuItem);
        }
        int itemId = menuItem.getItemId();
        if (itemId == 7) {
            boolean isSaved = this.mSelectedAccessPoint.isSaved();
            if (isSaved) {
                connect(this.mSelectedAccessPoint.getConfig(), isSaved, 1);
            } else if (this.mSelectedAccessPoint.getSecurity() == 0 || this.mSelectedAccessPoint.getSecurity() == 4) {
                this.mSelectedAccessPoint.generateOpenNetworkConfig();
                connect(this.mSelectedAccessPoint.getConfig(), isSaved, 1);
            } else {
                showDialog(this.mSelectedAccessPoint, 1);
            }
            return true;
        } else if (itemId == 8) {
            forget();
            return true;
        } else if (itemId != 9) {
            return super.onContextItemSelected(menuItem);
        } else {
            showDialog(this.mSelectedAccessPoint, 2);
            return true;
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getFragment() != null) {
            preference.setOnPreferenceClickListener(null);
            return super.onPreferenceTreeClick(preference);
        }
        if (preference instanceof LongPressAccessPointPreference) {
            LongPressAccessPointPreference longPressAccessPointPreference = (LongPressAccessPointPreference) preference;
            AccessPoint accessPoint = longPressAccessPointPreference.getAccessPoint();
            this.mSelectedAccessPoint = accessPoint;
            if (accessPoint == null) {
                return false;
            }
            if (accessPoint.isActive()) {
                return super.onPreferenceTreeClick(preference);
            }
            int connectingType = WifiUtils.getConnectingType(this.mSelectedAccessPoint);
            if (connectingType == 1) {
                this.mSelectedAccessPoint.generateOpenNetworkConfig();
                connect(this.mSelectedAccessPoint.getConfig(), this.mSelectedAccessPoint.isSaved(), 2);
            } else if (connectingType == 2) {
                connect(this.mSelectedAccessPoint.getConfig(), true, 2);
            } else if (connectingType != 3) {
                Bundle extras = longPressAccessPointPreference.getExtras();
                this.mSelectedAccessPoint.saveWifiState(extras);
                launchConfigNewNetworkFragment(this.mSelectedAccessPoint, 1, extras);
            } else {
                this.mSelectedAccessPoint.startOsuProvisioning(this.mConnectListener);
                this.mClickedConnect = true;
            }
        } else if (preference != this.mAddWifiNetworkPreference) {
            return super.onPreferenceTreeClick(preference);
        } else {
            onAddNetworkPressed();
        }
        return true;
    }

    private void showDialog(AccessPoint accessPoint, int i) {
        if (accessPoint != null) {
            if (WifiUtils.isNetworkLockedDown(getActivity(), accessPoint.getConfig()) && accessPoint.isActive()) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), RestrictedLockUtilsInternal.getDeviceOwner(getActivity()));
                return;
            }
        }
        if (this.mDialog != null) {
            removeDialog(1);
            this.mDialog = null;
        }
        this.mDlgAccessPoint = accessPoint;
        this.mDialogMode = i;
        showDialog(1);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 1) {
            return super.onCreateDialog(i);
        }
        if (this.mDlgAccessPoint == null && this.mAccessPointSavedState != null) {
            this.mDlgAccessPoint = new AccessPoint(getActivity(), this.mAccessPointSavedState);
            this.mAccessPointSavedState = null;
        }
        WifiDialog createModal = WifiDialog.createModal(getActivity(), this, this.mDlgAccessPoint, this.mDialogMode);
        this.mDialog = createModal;
        this.mSelectedAccessPoint = this.mDlgAccessPoint;
        return createModal;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onDialogShowing() {
        super.onDialogShowing();
        setOnDismissListener(this);
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.mDialog = null;
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onAccessPointsChanged() {
        Log.d("WifiSettings", "onAccessPointsChanged (WifiTracker) callback initiated");
        updateAccessPointsDelayed();
    }

    private void updateAccessPointsDelayed() {
        if (getActivity() != null && !this.mIsRestricted && this.mWifiManager.isWifiEnabled()) {
            View view = getView();
            Handler handler = view.getHandler();
            if (handler == null || !handler.hasCallbacks(this.mUpdateAccessPointsRunnable)) {
                setProgressBarVisible(true);
                view.postDelayed(this.mUpdateAccessPointsRunnable, 300);
            }
        }
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onWifiStateChanged(int i) {
        if (!this.mIsRestricted) {
            int wifiState = this.mWifiManager.getWifiState();
            if (wifiState == 0) {
                removeConnectedAccessPointPreference();
                removeAccessPointPreference();
                addMessagePreference(C0017R$string.wifi_stopping);
            } else if (wifiState == 1) {
                setOffMessage();
                setAdditionalSettingsSummaries();
                setProgressBarVisible(false);
                this.mConnectSource = 0;
                this.mClickedConnect = false;
            } else if (wifiState == 2) {
                removeConnectedAccessPointPreference();
                removeAccessPointPreference();
                addMessagePreference(C0017R$string.wifi_starting);
                setProgressBarVisible(true);
            } else if (wifiState == 3) {
                lambda$new$0();
            }
        }
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onConnectedChanged() {
        changeNextButtonState(this.mWifiTracker.isConnected());
    }

    private static boolean isDisabledByWrongPassword(AccessPoint accessPoint) {
        WifiConfiguration.NetworkSelectionStatus networkSelectionStatus;
        WifiConfiguration config = accessPoint.getConfig();
        if (config == null || (networkSelectionStatus = config.getNetworkSelectionStatus()) == null || networkSelectionStatus.getNetworkSelectionStatus() == 0 || 8 != networkSelectionStatus.getNetworkSelectionDisableReason()) {
            return false;
        }
        return true;
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:35:0x00ab */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r9v0, types: [com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.wifi.WifiSettings, com.android.settingslib.wifi.AccessPoint$AccessPointListener, androidx.fragment.app.Fragment] */
    /* JADX WARN: Type inference failed for: r1v6, types: [int] */
    /* JADX WARN: Type inference failed for: r0v8, types: [androidx.preference.Preference] */
    /* JADX WARN: Type inference failed for: r9v2, types: [androidx.preference.PreferenceGroup, androidx.preference.PreferenceCategory] */
    /* JADX WARN: Type inference failed for: r7v2, types: [com.android.settings.wifi.LongPressAccessPointPreference, androidx.preference.Preference] */
    /* JADX WARN: Type inference failed for: r7v3, types: [com.android.settings.wifi.LongPressAccessPointPreference, com.android.settingslib.wifi.AccessPointPreference, androidx.preference.Preference] */
    /* JADX WARN: Type inference failed for: r5v6, types: [androidx.preference.PreferenceGroup, androidx.preference.PreferenceCategory] */
    /* JADX WARN: Type inference failed for: r1v13 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 5 */
    /* renamed from: updateAccessPointPreferences */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$new$0() {
        /*
        // Method dump skipped, instructions count: 243
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiSettings.lambda$new$0():void");
    }

    private LongPressAccessPointPreference createLongPressAccessPointPreference(AccessPoint accessPoint) {
        return new LongPressAccessPointPreference(accessPoint, getPrefContext(), this.mUserBadgeCache, false, C0008R$drawable.ic_wifi_signal_0, this);
    }

    /* access modifiers changed from: package-private */
    public ConnectedAccessPointPreference createConnectedAccessPointPreference(AccessPoint accessPoint, Context context) {
        return new ConnectedAccessPointPreference(accessPoint, context, this.mUserBadgeCache, C0008R$drawable.ic_wifi_signal_0, false, this);
    }

    private boolean configureConnectedAccessPointPreferenceCategory(List<AccessPoint> list) {
        if (list.size() == 0) {
            removeConnectedAccessPointPreference();
            return false;
        }
        AccessPoint accessPoint = list.get(0);
        if (!accessPoint.isActive()) {
            removeConnectedAccessPointPreference();
            return false;
        } else if (this.mConnectedAccessPointPreferenceCategory.getPreferenceCount() == 0) {
            addConnectedAccessPointPreference(accessPoint);
            return true;
        } else {
            ConnectedAccessPointPreference connectedAccessPointPreference = (ConnectedAccessPointPreference) this.mConnectedAccessPointPreferenceCategory.getPreference(0);
            if (connectedAccessPointPreference.getAccessPoint() != accessPoint) {
                removeConnectedAccessPointPreference();
                addConnectedAccessPointPreference(accessPoint);
                return true;
            }
            connectedAccessPointPreference.refresh();
            registerCaptivePortalNetworkCallback(getCurrentWifiNetwork(), connectedAccessPointPreference);
            return true;
        }
    }

    private void addConnectedAccessPointPreference(AccessPoint accessPoint) {
        ConnectedAccessPointPreference createConnectedAccessPointPreference = createConnectedAccessPointPreference(accessPoint, getPrefContext());
        registerCaptivePortalNetworkCallback(getCurrentWifiNetwork(), createConnectedAccessPointPreference);
        createConnectedAccessPointPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(createConnectedAccessPointPreference) {
            /* class com.android.settings.wifi.$$Lambda$WifiSettings$dvoN6ah4HmI5buWC6GAjN7HS4rw */
            public final /* synthetic */ ConnectedAccessPointPreference f$1;

            {
                this.f$1 = r2;
            }

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return WifiSettings.this.lambda$addConnectedAccessPointPreference$2$WifiSettings(this.f$1, preference);
            }
        });
        createConnectedAccessPointPreference.setOnGearClickListener(new ConnectedAccessPointPreference.OnGearClickListener(createConnectedAccessPointPreference) {
            /* class com.android.settings.wifi.$$Lambda$WifiSettings$gxNoP_iqTz6xulv3o7cQv7agDKI */
            public final /* synthetic */ ConnectedAccessPointPreference f$1;

            {
                this.f$1 = r2;
            }

            @Override // com.android.settings.wifi.ConnectedAccessPointPreference.OnGearClickListener
            public final void onGearClick(ConnectedAccessPointPreference connectedAccessPointPreference) {
                WifiSettings.this.lambda$addConnectedAccessPointPreference$3$WifiSettings(this.f$1, connectedAccessPointPreference);
            }
        });
        createConnectedAccessPointPreference.refresh();
        this.mConnectedAccessPointPreferenceCategory.addPreference(createConnectedAccessPointPreference);
        this.mConnectedAccessPointPreferenceCategory.setVisible(true);
        if (this.mClickedConnect) {
            this.mClickedConnect = false;
            scrollToPreference(this.mConnectedAccessPointPreferenceCategory);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addConnectedAccessPointPreference$2 */
    public /* synthetic */ boolean lambda$addConnectedAccessPointPreference$2$WifiSettings(ConnectedAccessPointPreference connectedAccessPointPreference, Preference preference) {
        connectedAccessPointPreference.getAccessPoint().saveWifiState(connectedAccessPointPreference.getExtras());
        CaptivePortalNetworkCallback captivePortalNetworkCallback = this.mCaptivePortalNetworkCallback;
        if (captivePortalNetworkCallback == null || !captivePortalNetworkCallback.isCaptivePortal()) {
            launchNetworkDetailsFragment(connectedAccessPointPreference);
            return true;
        }
        startCaptivePortalApp(this.mCaptivePortalNetworkCallback.getNetwork());
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addConnectedAccessPointPreference$3 */
    public /* synthetic */ void lambda$addConnectedAccessPointPreference$3$WifiSettings(ConnectedAccessPointPreference connectedAccessPointPreference, ConnectedAccessPointPreference connectedAccessPointPreference2) {
        connectedAccessPointPreference.getAccessPoint().saveWifiState(connectedAccessPointPreference.getExtras());
        launchNetworkDetailsFragment(connectedAccessPointPreference);
    }

    private void registerCaptivePortalNetworkCallback(Network network, ConnectedAccessPointPreference connectedAccessPointPreference) {
        if (network == null || connectedAccessPointPreference == null) {
            Log.w("WifiSettings", "Network or Preference were null when registering callback.");
            return;
        }
        CaptivePortalNetworkCallback captivePortalNetworkCallback = this.mCaptivePortalNetworkCallback;
        if (captivePortalNetworkCallback == null || !captivePortalNetworkCallback.isSameNetworkAndPreference(network, connectedAccessPointPreference)) {
            unregisterCaptivePortalNetworkCallback();
            this.mCaptivePortalNetworkCallback = new CaptivePortalNetworkCallback(network, connectedAccessPointPreference) {
                /* class com.android.settings.wifi.WifiSettings.AnonymousClass3 */

                @Override // com.android.settings.wifi.CaptivePortalNetworkCallback
                public void onCaptivePortalCapabilityChanged() {
                    WifiSettings.this.checkStartCaptivePortalApp();
                }
            };
            this.mConnectivityManager.registerNetworkCallback(new NetworkRequest.Builder().clearCapabilities().addTransportType(1).build(), this.mCaptivePortalNetworkCallback, new Handler(Looper.getMainLooper()));
        }
    }

    private void unregisterCaptivePortalNetworkCallback() {
        CaptivePortalNetworkCallback captivePortalNetworkCallback = this.mCaptivePortalNetworkCallback;
        if (captivePortalNetworkCallback != null) {
            try {
                this.mConnectivityManager.unregisterNetworkCallback(captivePortalNetworkCallback);
            } catch (RuntimeException e) {
                Log.e("WifiSettings", "Unregistering CaptivePortalNetworkCallback failed.", e);
            }
            this.mCaptivePortalNetworkCallback = null;
        }
    }

    private void launchAddNetworkFragment() {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setTitleRes(C0017R$string.wifi_add_network);
        subSettingLauncher.setDestination(AddNetworkFragment.class.getName());
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setResultListener(this, 2);
        subSettingLauncher.launch();
    }

    private void launchNetworkDetailsFragment(ConnectedAccessPointPreference connectedAccessPointPreference) {
        CharSequence charSequence;
        AccessPoint accessPoint = connectedAccessPointPreference.getAccessPoint();
        Context context = getContext();
        if (FeatureFlagUtils.isEnabled(context, "settings_wifi_details_datausage_header")) {
            charSequence = accessPoint.getTitle();
        } else {
            charSequence = context.getText(C0017R$string.pref_title_network_details);
        }
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setTitleText(charSequence);
        subSettingLauncher.setDestination(WifiNetworkDetailsFragment.class.getName());
        subSettingLauncher.setArguments(connectedAccessPointPreference.getExtras());
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }

    private Network getCurrentWifiNetwork() {
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager != null) {
            return wifiManager.getCurrentNetwork();
        }
        return null;
    }

    private void removeConnectedAccessPointPreference() {
        this.mConnectedAccessPointPreferenceCategory.removeAll();
        this.mConnectedAccessPointPreferenceCategory.setVisible(false);
        unregisterCaptivePortalNetworkCallback();
    }

    private void removeAccessPointPreference() {
        this.mAccessPointsPreferenceCategory.removeAll();
        this.mAccessPointsPreferenceCategory.setVisible(false);
    }

    /* access modifiers changed from: package-private */
    public void setAdditionalSettingsSummaries() {
        int i;
        Preference preference = this.mConfigureWifiSettingsPreference;
        if (isWifiWakeupEnabled()) {
            i = C0017R$string.wifi_configure_settings_preference_summary_wakeup_on;
        } else {
            i = C0017R$string.wifi_configure_settings_preference_summary_wakeup_off;
        }
        preference.setSummary(getString(i));
        List<AccessPoint> allConfigs = WifiSavedConfigUtils.getAllConfigs(getContext(), this.mWifiManager);
        boolean z = false;
        int size = allConfigs != null ? allConfigs.size() : 0;
        Preference preference2 = this.mSavedNetworksPreference;
        if (size > 0) {
            z = true;
        }
        preference2.setVisible(z);
        if (size > 0) {
            this.mSavedNetworksPreference.setSummary(getSavedNetworkSettingsSummaryText(allConfigs, size));
        }
    }

    private String getSavedNetworkSettingsSummaryText(List<AccessPoint> list, int i) {
        int i2 = 0;
        for (AccessPoint accessPoint : list) {
            if (accessPoint.isPasspointConfig() || accessPoint.isPasspoint()) {
                i2++;
            }
        }
        int i3 = i - i2;
        if (i == i3) {
            return getResources().getQuantityString(C0015R$plurals.wifi_saved_access_points_summary, i3, Integer.valueOf(i3));
        } else if (i == i2) {
            return getResources().getQuantityString(C0015R$plurals.wifi_saved_passpoint_access_points_summary, i2, Integer.valueOf(i2));
        } else {
            return getResources().getQuantityString(C0015R$plurals.wifi_saved_all_access_points_summary, i, Integer.valueOf(i));
        }
    }

    private boolean isWifiWakeupEnabled() {
        Context context = getContext();
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        ContentResolver contentResolver = context.getContentResolver();
        if (!this.mWifiManager.isAutoWakeupEnabled() || !this.mWifiManager.isScanAlwaysAvailable() || Settings.Global.getInt(contentResolver, "airplane_mode_on", 0) != 0 || powerManager.isPowerSaveMode()) {
            return false;
        }
        return true;
    }

    private void setOffMessage() {
        CharSequence charSequence;
        CharSequence text = getText(C0017R$string.wifi_empty_list_wifi_off);
        if (this.mWifiManager.isScanAlwaysAvailable()) {
            charSequence = getText(C0017R$string.wifi_scan_notify_text);
        } else {
            charSequence = getText(C0017R$string.wifi_scan_notify_text_scanning_off);
        }
        this.mStatusMessagePreference.setText(text, charSequence, new LinkifyUtils.OnClickListener() {
            /* class com.android.settings.wifi.$$Lambda$WifiSettings$G0vWzmi3g45SjhkhuPVMzYpO5w */

            @Override // com.android.settings.LinkifyUtils.OnClickListener
            public final void onClick() {
                WifiSettings.this.lambda$setOffMessage$4$WifiSettings();
            }
        });
        removeConnectedAccessPointPreference();
        removeAccessPointPreference();
        this.mStatusMessagePreference.setVisible(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setOffMessage$4 */
    public /* synthetic */ void lambda$setOffMessage$4$WifiSettings() {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(ScanningSettings.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.location_scanning_screen_title);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }

    private void addMessagePreference(int i) {
        this.mStatusMessagePreference.setTitle(i);
        this.mStatusMessagePreference.setVisible(true);
    }

    /* access modifiers changed from: protected */
    public void setProgressBarVisible(boolean z) {
        View view = this.mProgressHeader;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
    }

    private void changeNextButtonState(boolean z) {
        if (this.mEnableNextOnConnection && hasNextButton()) {
            getNextButton().setEnabled(z);
        }
    }

    @Override // com.android.settings.wifi.WifiDialog.WifiDialogListener
    public void onForget(WifiDialog wifiDialog) {
        forget();
    }

    @Override // com.android.settings.wifi.WifiDialog.WifiDialogListener
    public void onSubmit(WifiDialog wifiDialog) {
        WifiDialog wifiDialog2 = this.mDialog;
        if (wifiDialog2 != null) {
            submit(wifiDialog2.getController());
        }
    }

    @Override // com.android.settings.wifi.WifiDialog.WifiDialogListener
    public void onScan(WifiDialog wifiDialog, String str) {
        startActivityForResult(WifiDppUtils.getEnrolleeQrCodeScannerIntent(str), 0);
    }

    /* access modifiers changed from: package-private */
    public void submit(WifiConfigController wifiConfigController) {
        WifiConfiguration config = wifiConfigController.getConfig();
        if (config == null) {
            AccessPoint accessPoint = this.mSelectedAccessPoint;
            if (accessPoint != null && accessPoint.isSaved()) {
                connect(this.mSelectedAccessPoint.getConfig(), true, 0);
            }
        } else if (wifiConfigController.getMode() == 2) {
            if (wifiConfigController.checkWapiParam()) {
                this.mWifiManager.save(config, this.mSaveListener);
            } else if (wifiConfigController.getCurSecurity() == 9) {
                startWapiCertManage();
                return;
            } else {
                return;
            }
        } else if (wifiConfigController.checkWapiParam()) {
            this.mWifiManager.save(config, this.mSaveListener);
            if (this.mSelectedAccessPoint != null) {
                connect(config, false, 0);
            }
        } else if (wifiConfigController.getCurSecurity() == 9) {
            startWapiCertManage();
            return;
        } else {
            return;
        }
        this.mWifiTracker.resumeScanning();
    }

    /* access modifiers changed from: package-private */
    public void forget() {
        this.mMetricsFeatureProvider.action(getActivity(), 137, new Pair[0]);
        if (!this.mSelectedAccessPoint.isSaved()) {
            if (this.mSelectedAccessPoint.getNetworkInfo() == null || this.mSelectedAccessPoint.getNetworkInfo().getState() == NetworkInfo.State.DISCONNECTED) {
                Log.e("WifiSettings", "Failed to forget invalid network " + this.mSelectedAccessPoint.getConfig());
                return;
            }
            this.mWifiManager.disableEphemeralNetwork(AccessPoint.convertToQuotedString(this.mSelectedAccessPoint.getSsidStr()));
        } else if (this.mSelectedAccessPoint.getConfig().isPasspoint()) {
            try {
                this.mWifiManager.removePasspointConfiguration(this.mSelectedAccessPoint.getConfig().FQDN);
            } catch (IllegalArgumentException e) {
                Log.e("WifiSettings", "Failed to remove Passpoint configuration with error: " + e);
                return;
            }
        } else {
            this.mWifiManager.forget(this.mSelectedAccessPoint.getConfig().networkId, this.mForgetListener);
        }
        this.mWifiTracker.resumeScanning();
        changeNextButtonState(false);
    }

    /* access modifiers changed from: protected */
    public void connect(WifiConfiguration wifiConfiguration, boolean z, int i) {
        this.mMetricsFeatureProvider.action(getContext(), 135, z);
        this.mConnectSource = i;
        this.mWifiManager.connect(wifiConfiguration, this.mConnectListener);
        this.mClickedConnect = true;
    }

    /* access modifiers changed from: package-private */
    public void handleAddNetworkRequest(int i, Intent intent) {
        if (i == -1) {
            handleAddNetworkSubmitEvent(intent);
        }
        this.mWifiTracker.resumeScanning();
    }

    private void handleAddNetworkSubmitEvent(Intent intent) {
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("wifi_config_key");
        if (wifiConfiguration != null) {
            this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
        }
    }

    private void onAddNetworkPressed() {
        this.mSelectedAccessPoint = null;
        launchAddNetworkFragment();
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_wifi;
    }

    @Override // com.android.settingslib.wifi.AccessPoint.AccessPointListener
    public void onAccessPointChanged(final AccessPoint accessPoint) {
        Log.d("WifiSettings", "onAccessPointChanged (singular) callback initiated");
        View view = getView();
        if (view != null) {
            view.post(new Runnable(this) {
                /* class com.android.settings.wifi.WifiSettings.AnonymousClass4 */

                public void run() {
                    Object tag = accessPoint.getTag();
                    if (tag != null) {
                        ((AccessPointPreference) tag).refresh();
                    }
                }
            });
        }
    }

    private void startWapiCertManage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(C0017R$string.wapi_no_vaild_cert));
        builder.setCancelable(false);
        builder.setPositiveButton(C0017R$string.wapi_yes, new DialogInterface.OnClickListener() {
            /* class com.android.settings.wifi.WifiSettings.AnonymousClass5 */

            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("WifiSettings", "startWapiCertManage: yes");
                WifiSettings.this.startActivity(new Intent("android.Wapi.CertManage"));
            }
        });
        builder.setNegativeButton(C0017R$string.wapi_no, new DialogInterface.OnClickListener(this) {
            /* class com.android.settings.wifi.WifiSettings.AnonymousClass6 */

            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("WifiSettings", "startWapiCertManage: no");
            }
        });
        builder.create().show();
    }

    @Override // com.android.settingslib.wifi.AccessPoint.AccessPointListener
    public void onLevelChanged(AccessPoint accessPoint) {
        ((AccessPointPreference) accessPoint.getTag()).onLevelChanged();
    }

    private void handleConfigNetworkSubmitEvent(Intent intent) {
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("network_config_key");
        if (wifiConfiguration != null) {
            this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
            if (this.mSelectedAccessPoint != null) {
                connect(wifiConfiguration, false, 0);
            }
            this.mWifiTracker.resumeScanning();
        }
    }

    private void launchConfigNewNetworkFragment(AccessPoint accessPoint, int i, Bundle bundle) {
        this.mDialogMode = i;
        String title = accessPoint.getTitle();
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setTitleText(title);
        subSettingLauncher.setDestination(ConfigureAccessPointFragment.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setResultListener(this, 3);
        subSettingLauncher.launch();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void checkStartCaptivePortalApp() {
        Network currentWifiNetwork = getCurrentWifiNetwork();
        CaptivePortalNetworkCallback captivePortalNetworkCallback = this.mCaptivePortalNetworkCallback;
        if (captivePortalNetworkCallback != null && currentWifiNetwork != null && currentWifiNetwork.equals(captivePortalNetworkCallback.getNetwork()) && this.mCaptivePortalNetworkCallback.isCaptivePortal()) {
            int i = this.mConnectSource;
            if (i == 2 || i == 1) {
                Network network = this.mLastNetworkCaptivePortalAppStarted;
                if (network == null || !network.equals(currentWifiNetwork)) {
                    startCaptivePortalApp(currentWifiNetwork);
                }
            }
        }
    }

    private void startCaptivePortalApp(Network network) {
        ConnectivityManager connectivityManager = this.mConnectivityManager;
        if (connectivityManager != null && network != null) {
            this.mLastNetworkCaptivePortalAppStarted = network;
            connectivityManager.startCaptivePortalApp(network);
        }
    }
}
