package com.android.settings.wifi;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkTemplate;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.constraintlayout.widget.R$styleable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
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
import com.android.settings.wifi.ConnectedWifiEntryPreference;
import com.android.settings.wifi.WifiDialog2;
import com.android.settings.wifi.details2.WifiNetworkDetailsFragment2;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.wifi.LongPressWifiEntryPreference;
import com.android.settingslib.wifi.WifiEntryPreference;
import com.android.settingslib.wifi.WifiSavedConfigUtils;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class WifiSettings2 extends RestrictedSettingsFragment implements WifiPickerTracker.WifiPickerTrackerCallback, WifiDialog2.WifiDialog2Listener, DialogInterface.OnDismissListener {
    static final int ADD_NETWORK_REQUEST = 2;
    static final int MENU_ID_DISCONNECT = 3;
    static final int MENU_ID_FORGET = 4;
    static final String PREF_KEY_DATA_USAGE = "wifi_data_usage";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.wifi_settings2) {
        /* class com.android.settings.wifi.WifiSettings2.AnonymousClass4 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (WifiSavedConfigUtils.getAllConfigsCount(context, (WifiManager) context.getSystemService(WifiManager.class)) == 0) {
                nonIndexableKeys.add("saved_networks");
            }
            if (!DataUsageUtils.hasWifiRadio(context)) {
                nonIndexableKeys.add(WifiSettings2.PREF_KEY_DATA_USAGE);
            }
            return nonIndexableKeys;
        }
    };
    AddWifiNetworkPreference mAddWifiNetworkPreference;
    private boolean mClickedConnect;
    Preference mConfigureWifiSettingsPreference;
    private PreferenceCategory mConnectedWifiEntryPreferenceCategory;
    DataUsagePreference mDataUsagePreference;
    private WifiDialog2 mDialog;
    private int mDialogMode;
    private WifiEntry mDialogWifiEntry;
    private String mDialogWifiEntryKey;
    private boolean mEnableNextOnConnection;
    private final Runnable mHideProgressBarRunnable = new Runnable() {
        /* class com.android.settings.wifi.$$Lambda$WifiSettings2$6chcgQukKxPOnrvAKZ5_TyYzTKI */

        public final void run() {
            WifiSettings2.this.lambda$new$1$WifiSettings2();
        }
    };
    private boolean mIsRestricted;
    private String mOpenSsid;
    private View mProgressHeader;
    private WifiManager.ActionListener mSaveListener;
    Preference mSavedNetworksPreference;
    private WifiEntry mSelectedWifiEntry;
    private LinkablePreference mStatusMessagePreference;
    private final Runnable mUpdateWifiEntryPreferencesRunnable = new Runnable() {
        /* class com.android.settings.wifi.$$Lambda$WifiSettings2$V99arYdpdtZ6EE21Rup8DDbFhmA */

        public final void run() {
            WifiSettings2.this.lambda$new$0$WifiSettings2();
        }
    };
    private WifiEnabler mWifiEnabler;
    private PreferenceCategory mWifiEntryPreferenceCategory;
    protected WifiManager mWifiManager;
    WifiPickerTracker mWifiPickerTracker;
    private HandlerThread mWorkerThread;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return i != 1 ? 0 : 603;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return R$styleable.Constraint_layout_goneMarginTop;
    }

    private static boolean isVerboseLoggingEnabled() {
        return BaseWifiTracker.isVerboseLoggingEnabled();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$WifiSettings2() {
        setProgressBarVisible(false);
    }

    public WifiSettings2() {
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
        setAnimationAllowed(false);
        addPreferences();
        this.mIsRestricted = isUiRestricted();
    }

    private void addPreferences() {
        addPreferencesFromResource(C0019R$xml.wifi_settings2);
        this.mConnectedWifiEntryPreferenceCategory = (PreferenceCategory) findPreference("connected_access_point");
        this.mWifiEntryPreferenceCategory = (PreferenceCategory) findPreference("access_points");
        this.mConfigureWifiSettingsPreference = findPreference("configure_wifi_settings");
        this.mSavedNetworksPreference = findPreference("saved_networks");
        this.mAddWifiNetworkPreference = new AddWifiNetworkPreference(getPrefContext());
        this.mStatusMessagePreference = (LinkablePreference) findPreference("wifi_status_message");
        DataUsagePreference dataUsagePreference = (DataUsagePreference) findPreference(PREF_KEY_DATA_USAGE);
        this.mDataUsagePreference = dataUsagePreference;
        dataUsagePreference.setVisible(DataUsageUtils.hasWifiRadio(getContext()));
        this.mDataUsagePreference.setTemplate(NetworkTemplate.buildTemplateWifiWildcard(), 0, null);
    }

    /* JADX WARN: Type inference failed for: r8v0, types: [com.android.settings.wifi.WifiSettings2$1, java.time.Clock] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityCreated(android.os.Bundle r17) {
        /*
        // Method dump skipped, instructions count: 218
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiSettings2.onActivityCreated(android.os.Bundle):void");
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.teardownSwitchController();
        }
        this.mWorkerThread.quit();
        super.onDestroyView();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        this.mWifiEnabler = createWifiEnabler();
        if (this.mIsRestricted) {
            restrictUi();
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
        changeNextButtonState(this.mWifiPickerTracker.getConnectedWifiEntry() != null);
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
        getView().removeCallbacks(this.mUpdateWifiEntryPreferencesRunnable);
        getView().removeCallbacks(this.mHideProgressBarRunnable);
        super.onStop();
    }

    @Override // com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        WifiConfiguration wifiConfiguration;
        WifiDialog2 wifiDialog2;
        super.onActivityResult(i, i2, intent);
        if (i == 2) {
            handleAddNetworkRequest(i2, intent);
        } else if (i == 0) {
            if (i2 == -1 && (wifiDialog2 = this.mDialog) != null) {
                wifiDialog2.dismiss();
            }
        } else if (i == 3) {
            if (i2 == -1 && (wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("network_config_key")) != null) {
                this.mWifiManager.connect(wifiConfiguration, new WifiConnectActionListener());
            }
        } else if (i != 4) {
            boolean z = this.mIsRestricted;
            boolean isUiRestricted = isUiRestricted();
            this.mIsRestricted = isUiRestricted;
            if (z && !isUiRestricted && getPreferenceScreen().getPreferenceCount() == 0) {
                addPreferences();
            }
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
            bundle.putString("wifi_ap_key", this.mDialogWifiEntryKey);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        Preference preference = (Preference) view.getTag();
        if (preference instanceof LongPressWifiEntryPreference) {
            WifiEntry wifiEntry = ((LongPressWifiEntryPreference) preference).getWifiEntry();
            this.mSelectedWifiEntry = wifiEntry;
            contextMenu.setHeaderTitle(wifiEntry.getTitle());
            if (this.mSelectedWifiEntry.canConnect()) {
                contextMenu.add(0, 2, 0, C0017R$string.wifi_connect);
            }
            if (this.mSelectedWifiEntry.canDisconnect()) {
                contextMenu.add(0, 3, 0, C0017R$string.wifi_disconnect_button_text);
            }
            if (canForgetNetwork()) {
                contextMenu.add(0, 4, 0, C0017R$string.forget);
            }
            if (!WifiUtils.isNetworkLockedDown(getActivity(), this.mSelectedWifiEntry.getWifiConfiguration()) && this.mSelectedWifiEntry.isSaved() && this.mSelectedWifiEntry.getConnectedState() != 2) {
                contextMenu.add(0, 5, 0, C0017R$string.wifi_modify);
            }
        }
    }

    private boolean canForgetNetwork() {
        return this.mSelectedWifiEntry.canForget() && !WifiUtils.isNetworkLockedDown(getActivity(), this.mSelectedWifiEntry.getWifiConfiguration());
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onContextItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2) {
            connect(this.mSelectedWifiEntry, true, false);
            return true;
        } else if (itemId == 3) {
            this.mSelectedWifiEntry.disconnect(null);
            return true;
        } else if (itemId == 4) {
            forget(this.mSelectedWifiEntry);
            return true;
        } else if (itemId != 5) {
            return super.onContextItemSelected(menuItem);
        } else {
            showDialog(this.mSelectedWifiEntry, 2);
            return true;
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getFragment() != null) {
            preference.setOnPreferenceClickListener(null);
            return super.onPreferenceTreeClick(preference);
        }
        if (preference instanceof LongPressWifiEntryPreference) {
            WifiEntry wifiEntry = ((LongPressWifiEntryPreference) preference).getWifiEntry();
            if (wifiEntry.shouldEditBeforeConnect()) {
                launchConfigNewNetworkFragment(wifiEntry);
                return true;
            }
            connect(wifiEntry, true, true);
        } else if (preference != this.mAddWifiNetworkPreference) {
            return super.onPreferenceTreeClick(preference);
        } else {
            onAddNetworkPressed();
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showDialog(WifiEntry wifiEntry, int i) {
        if (!WifiUtils.isNetworkLockedDown(getActivity(), wifiEntry.getWifiConfiguration()) || wifiEntry.getConnectedState() != 2) {
            if (this.mDialog != null) {
                removeDialog(1);
                this.mDialog = null;
            }
            this.mDialogWifiEntry = wifiEntry;
            this.mDialogWifiEntryKey = wifiEntry.getKey();
            this.mDialogMode = i;
            showDialog(1);
            return;
        }
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), RestrictedLockUtilsInternal.getDeviceOwner(getActivity()));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 1) {
            return super.onCreateDialog(i);
        }
        WifiDialog2 createModal = WifiDialog2.createModal(getActivity(), this, this.mDialogWifiEntry, this.mDialogMode);
        this.mDialog = createModal;
        return createModal;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onDialogShowing() {
        super.onDialogShowing();
        setOnDismissListener(this);
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.mDialog = null;
        this.mDialogWifiEntry = null;
        this.mDialogWifiEntryKey = null;
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    public void onWifiStateChanged() {
        if (!this.mIsRestricted) {
            int wifiState = this.mWifiPickerTracker.getWifiState();
            if (isVerboseLoggingEnabled()) {
                Log.i("WifiSettings2", "onWifiStateChanged called with wifi state: " + wifiState);
            }
            if (wifiState == 0) {
                removeConnectedWifiEntryPreference();
                removeWifiEntryPreference();
                addMessagePreference(C0017R$string.wifi_stopping);
            } else if (wifiState == 1) {
                setOffMessage();
                setAdditionalSettingsSummaries();
                setProgressBarVisible(false);
                this.mClickedConnect = false;
            } else if (wifiState == 2) {
                removeConnectedWifiEntryPreference();
                removeWifiEntryPreference();
                addMessagePreference(C0017R$string.wifi_starting);
                setProgressBarVisible(true);
            } else if (wifiState == 3) {
                lambda$new$0();
            }
        }
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        updateWifiEntryPreferencesDelayed();
        changeNextButtonState(this.mWifiPickerTracker.getConnectedWifiEntry() != null);
        if (this.mOpenSsid != null) {
            Optional<WifiEntry> findFirst = this.mWifiPickerTracker.getWifiEntries().stream().filter(new Predicate() {
                /* class com.android.settings.wifi.$$Lambda$WifiSettings2$ONu6f9uIZFPvNdpqmjqPZEYKv8 */

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return WifiSettings2.this.lambda$onWifiEntriesChanged$2$WifiSettings2((WifiEntry) obj);
                }
            }).filter($$Lambda$WifiSettings2$BCp0XHoIZEERzX8T_KGso62F1g.INSTANCE).filter($$Lambda$WifiSettings2$YMjSsDH3G5ATY01tOtfOmyk7hCA.INSTANCE).findFirst();
            if (findFirst.isPresent()) {
                this.mOpenSsid = null;
                launchConfigNewNetworkFragment(findFirst.get());
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onWifiEntriesChanged$2 */
    public /* synthetic */ boolean lambda$onWifiEntriesChanged$2$WifiSettings2(WifiEntry wifiEntry) {
        return TextUtils.equals(this.mOpenSsid, wifiEntry.getSsid());
    }

    static /* synthetic */ boolean lambda$onWifiEntriesChanged$3(WifiEntry wifiEntry) {
        return (wifiEntry.getSecurity() == 0 || wifiEntry.getSecurity() == 4) ? false : true;
    }

    static /* synthetic */ boolean lambda$onWifiEntriesChanged$4(WifiEntry wifiEntry) {
        return !wifiEntry.isSaved() || isDisabledByWrongPassword(wifiEntry);
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedNetworksChanged() {
        if (!isFinishingOrDestroyed()) {
            setAdditionalSettingsSummaries();
        }
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedSubscriptionsChanged() {
        if (!isFinishingOrDestroyed()) {
            setAdditionalSettingsSummaries();
        }
    }

    private void updateWifiEntryPreferencesDelayed() {
        if (getActivity() != null && !this.mIsRestricted && this.mWifiPickerTracker.getWifiState() == 3) {
            View view = getView();
            Handler handler = view.getHandler();
            if (handler == null || !handler.hasCallbacks(this.mUpdateWifiEntryPreferencesRunnable)) {
                setProgressBarVisible(true);
                view.postDelayed(this.mUpdateWifiEntryPreferencesRunnable, 300);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: updateWifiEntryPreferences */
    public void lambda$new$0() {
        if (this.mWifiPickerTracker.getWifiState() == 3) {
            this.mStatusMessagePreference.setVisible(false);
            this.mWifiEntryPreferenceCategory.setVisible(true);
            WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
            this.mConnectedWifiEntryPreferenceCategory.setVisible(connectedWifiEntry != null);
            if (connectedWifiEntry != null) {
                LongPressWifiEntryPreference longPressWifiEntryPreference = (LongPressWifiEntryPreference) this.mConnectedWifiEntryPreferenceCategory.findPreference(connectedWifiEntry.getKey());
                if (longPressWifiEntryPreference == null || longPressWifiEntryPreference.getWifiEntry() != connectedWifiEntry) {
                    this.mConnectedWifiEntryPreferenceCategory.removeAll();
                    ConnectedWifiEntryPreference connectedWifiEntryPreference = new ConnectedWifiEntryPreference(getPrefContext(), connectedWifiEntry, this);
                    connectedWifiEntryPreference.setKey(connectedWifiEntry.getKey());
                    connectedWifiEntryPreference.refresh();
                    this.mConnectedWifiEntryPreferenceCategory.addPreference(connectedWifiEntryPreference);
                    connectedWifiEntryPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(connectedWifiEntry, connectedWifiEntryPreference) {
                        /* class com.android.settings.wifi.$$Lambda$WifiSettings2$jjCTMtU0PoePhdCev7p2mUW44 */
                        public final /* synthetic */ WifiEntry f$1;
                        public final /* synthetic */ ConnectedWifiEntryPreference f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference) {
                            return WifiSettings2.this.lambda$updateWifiEntryPreferences$5$WifiSettings2(this.f$1, this.f$2, preference);
                        }
                    });
                    connectedWifiEntryPreference.setOnGearClickListener(new ConnectedWifiEntryPreference.OnGearClickListener(connectedWifiEntryPreference) {
                        /* class com.android.settings.wifi.$$Lambda$WifiSettings2$dKVcYyHrAQgiC7BycznqOzkdLXE */
                        public final /* synthetic */ ConnectedWifiEntryPreference f$1;

                        {
                            this.f$1 = r2;
                        }

                        @Override // com.android.settings.wifi.ConnectedWifiEntryPreference.OnGearClickListener
                        public final void onGearClick(ConnectedWifiEntryPreference connectedWifiEntryPreference) {
                            WifiSettings2.this.lambda$updateWifiEntryPreferences$6$WifiSettings2(this.f$1, connectedWifiEntryPreference);
                        }
                    });
                    if (this.mClickedConnect) {
                        this.mClickedConnect = false;
                        scrollToPreference(this.mConnectedWifiEntryPreferenceCategory);
                    }
                }
            } else {
                this.mConnectedWifiEntryPreferenceCategory.removeAll();
            }
            cacheRemoveAllPrefs(this.mWifiEntryPreferenceCategory);
            boolean z = false;
            int i = 0;
            for (WifiEntry wifiEntry : this.mWifiPickerTracker.getWifiEntries()) {
                String key = wifiEntry.getKey();
                LongPressWifiEntryPreference longPressWifiEntryPreference2 = (LongPressWifiEntryPreference) getCachedPreference(key);
                if (longPressWifiEntryPreference2 != null) {
                    if (longPressWifiEntryPreference2.getWifiEntry() == wifiEntry) {
                        longPressWifiEntryPreference2.setOrder(i);
                        i++;
                        z = true;
                    } else {
                        removePreference(key);
                    }
                }
                LongPressWifiEntryPreference createLongPressWifiEntryPreference = createLongPressWifiEntryPreference(wifiEntry);
                createLongPressWifiEntryPreference.setKey(wifiEntry.getKey());
                int i2 = i + 1;
                createLongPressWifiEntryPreference.setOrder(i);
                createLongPressWifiEntryPreference.refresh();
                if (wifiEntry.getHelpUriString() != null) {
                    createLongPressWifiEntryPreference.setOnButtonClickListener(new WifiEntryPreference.OnButtonClickListener(wifiEntry) {
                        /* class com.android.settings.wifi.$$Lambda$WifiSettings2$oYysfaV3KYcupIpqOiIAN4wzwI */
                        public final /* synthetic */ WifiEntry f$1;

                        {
                            this.f$1 = r2;
                        }

                        @Override // com.android.settingslib.wifi.WifiEntryPreference.OnButtonClickListener
                        public final void onButtonClick(WifiEntryPreference wifiEntryPreference) {
                            WifiSettings2.this.lambda$updateWifiEntryPreferences$7$WifiSettings2(this.f$1, wifiEntryPreference);
                        }
                    });
                }
                this.mWifiEntryPreferenceCategory.addPreference(createLongPressWifiEntryPreference);
                z = true;
                i = i2;
            }
            removeCachedPrefs(this.mWifiEntryPreferenceCategory);
            if (!z) {
                setProgressBarVisible(true);
                Preference preference = new Preference(getPrefContext());
                preference.setSelectable(false);
                preference.setSummary(C0017R$string.wifi_empty_list_wifi_on);
                preference.setOrder(i);
                preference.setKey("wifi_empty_list");
                this.mWifiEntryPreferenceCategory.addPreference(preference);
                i++;
            } else if (getView() != null) {
                getView().postDelayed(this.mHideProgressBarRunnable, 1700);
            }
            this.mAddWifiNetworkPreference.setOrder(i);
            this.mWifiEntryPreferenceCategory.addPreference(this.mAddWifiNetworkPreference);
            setAdditionalSettingsSummaries();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateWifiEntryPreferences$5 */
    public /* synthetic */ boolean lambda$updateWifiEntryPreferences$5$WifiSettings2(WifiEntry wifiEntry, ConnectedWifiEntryPreference connectedWifiEntryPreference, Preference preference) {
        if (wifiEntry.canSignIn()) {
            wifiEntry.signIn(null);
            return true;
        }
        launchNetworkDetailsFragment(connectedWifiEntryPreference);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateWifiEntryPreferences$6 */
    public /* synthetic */ void lambda$updateWifiEntryPreferences$6$WifiSettings2(ConnectedWifiEntryPreference connectedWifiEntryPreference, ConnectedWifiEntryPreference connectedWifiEntryPreference2) {
        launchNetworkDetailsFragment(connectedWifiEntryPreference);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateWifiEntryPreferences$7 */
    public /* synthetic */ void lambda$updateWifiEntryPreferences$7$WifiSettings2(WifiEntry wifiEntry, WifiEntryPreference wifiEntryPreference) {
        openSubscriptionHelpPage(wifiEntry);
    }

    private void launchNetworkDetailsFragment(LongPressWifiEntryPreference longPressWifiEntryPreference) {
        CharSequence charSequence;
        WifiEntry wifiEntry = longPressWifiEntryPreference.getWifiEntry();
        Context context = getContext();
        if (FeatureFlagUtils.isEnabled(context, "settings_wifi_details_datausage_header")) {
            charSequence = wifiEntry.getTitle();
        } else {
            charSequence = context.getText(C0017R$string.pref_title_network_details);
        }
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", wifiEntry.getKey());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(context);
        subSettingLauncher.setTitleText(charSequence);
        subSettingLauncher.setDestination(WifiNetworkDetailsFragment2.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }

    /* access modifiers changed from: package-private */
    public LongPressWifiEntryPreference createLongPressWifiEntryPreference(WifiEntry wifiEntry) {
        return new LongPressWifiEntryPreference(getPrefContext(), wifiEntry, this);
    }

    private void launchAddNetworkFragment() {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setTitleRes(C0017R$string.wifi_add_network);
        subSettingLauncher.setDestination(AddNetworkFragment.class.getName());
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setResultListener(this, 2);
        subSettingLauncher.launch();
    }

    private void removeConnectedWifiEntryPreference() {
        this.mConnectedWifiEntryPreferenceCategory.removeAll();
        this.mConnectedWifiEntryPreferenceCategory.setVisible(false);
    }

    private void removeWifiEntryPreference() {
        this.mWifiEntryPreferenceCategory.removeAll();
        this.mWifiEntryPreferenceCategory.setVisible(false);
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
        int numSavedNetworks = this.mWifiPickerTracker.getNumSavedNetworks();
        int numSavedSubscriptions = this.mWifiPickerTracker.getNumSavedSubscriptions();
        if (numSavedNetworks + numSavedSubscriptions > 0) {
            this.mSavedNetworksPreference.setVisible(true);
            this.mSavedNetworksPreference.setSummary(getSavedNetworkSettingsSummaryText(numSavedNetworks, numSavedSubscriptions));
            return;
        }
        this.mSavedNetworksPreference.setVisible(false);
    }

    private String getSavedNetworkSettingsSummaryText(int i, int i2) {
        if (i2 == 0) {
            return getResources().getQuantityString(C0015R$plurals.wifi_saved_access_points_summary, i, Integer.valueOf(i));
        } else if (i == 0) {
            return getResources().getQuantityString(C0015R$plurals.wifi_saved_passpoint_access_points_summary, i2, Integer.valueOf(i2));
        } else {
            int i3 = i + i2;
            return getResources().getQuantityString(C0015R$plurals.wifi_saved_all_access_points_summary, i3, Integer.valueOf(i3));
        }
    }

    private boolean isWifiWakeupEnabled() {
        Context prefContext = getPrefContext();
        PowerManager powerManager = (PowerManager) prefContext.getSystemService(PowerManager.class);
        ContentResolver contentResolver = prefContext.getContentResolver();
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
            /* class com.android.settings.wifi.$$Lambda$WifiSettings2$1IC8PJNv_zTcWPSWVMpJQHPcl4 */

            @Override // com.android.settings.LinkifyUtils.OnClickListener
            public final void onClick() {
                WifiSettings2.this.lambda$setOffMessage$8$WifiSettings2();
            }
        });
        removeConnectedWifiEntryPreference();
        removeWifiEntryPreference();
        this.mStatusMessagePreference.setVisible(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setOffMessage$8 */
    public /* synthetic */ void lambda$setOffMessage$8$WifiSettings2() {
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

    /* access modifiers changed from: package-private */
    public void handleAddNetworkRequest(int i, Intent intent) {
        if (i == -1) {
            handleAddNetworkSubmitEvent(intent);
        }
    }

    private void handleAddNetworkSubmitEvent(Intent intent) {
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("wifi_config_key");
        if (wifiConfiguration != null) {
            this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
        }
    }

    private void onAddNetworkPressed() {
        launchAddNetworkFragment();
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_wifi;
    }

    /* access modifiers changed from: package-private */
    public void changeNextButtonState(boolean z) {
        if (this.mEnableNextOnConnection && hasNextButton()) {
            getNextButton().setEnabled(z);
        }
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onForget(WifiDialog2 wifiDialog2) {
        forget(wifiDialog2.getWifiEntry());
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onSubmit(WifiDialog2 wifiDialog2) {
        int mode = wifiDialog2.getMode();
        WifiConfiguration config = wifiDialog2.getController().getConfig();
        WifiEntry wifiEntry = wifiDialog2.getWifiEntry();
        if (mode == 2) {
            if (config == null) {
                Toast.makeText(getContext(), C0017R$string.wifi_failed_save_message, 0).show();
            } else {
                this.mWifiManager.save(config, this.mSaveListener);
            }
        } else if (mode != 1 && (mode != 0 || !wifiEntry.canConnect())) {
        } else {
            if (config == null) {
                connect(wifiEntry, false, false);
            } else {
                this.mWifiManager.connect(config, new WifiConnectActionListener());
            }
        }
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onScan(WifiDialog2 wifiDialog2, String str) {
        startActivityForResult(WifiDppUtils.getEnrolleeQrCodeScannerIntent(str), 0);
    }

    private void forget(WifiEntry wifiEntry) {
        this.mMetricsFeatureProvider.action(getActivity(), 137, new Pair[0]);
        wifiEntry.forget(null);
    }

    /* access modifiers changed from: package-private */
    public void connect(WifiEntry wifiEntry, boolean z, boolean z2) {
        this.mMetricsFeatureProvider.action(getActivity(), 135, wifiEntry.isSaved());
        wifiEntry.connect(new WifiEntryConnectCallback(wifiEntry, z, z2));
    }

    private class WifiConnectActionListener implements WifiManager.ActionListener {
        private WifiConnectActionListener() {
        }

        public void onSuccess() {
            WifiSettings2.this.mClickedConnect = true;
        }

        public void onFailure(int i) {
            if (!WifiSettings2.this.isFinishingOrDestroyed()) {
                Toast.makeText(WifiSettings2.this.getContext(), C0017R$string.wifi_failed_connect_message, 0).show();
            }
        }
    }

    /* access modifiers changed from: private */
    public class WifiEntryConnectCallback implements WifiEntry.ConnectCallback {
        final WifiEntry mConnectWifiEntry;
        final boolean mEditIfNoConfig;
        final boolean mFullScreenEdit;

        WifiEntryConnectCallback(WifiEntry wifiEntry, boolean z, boolean z2) {
            this.mConnectWifiEntry = wifiEntry;
            this.mEditIfNoConfig = z;
            this.mFullScreenEdit = z2;
        }

        @Override // com.android.wifitrackerlib.WifiEntry.ConnectCallback
        public void onConnectResult(int i) {
            if (!WifiSettings2.this.isFinishingOrDestroyed()) {
                if (i == 0) {
                    WifiSettings2.this.mClickedConnect = true;
                } else if (i == 1) {
                    if (!this.mEditIfNoConfig) {
                        return;
                    }
                    if (this.mFullScreenEdit) {
                        WifiSettings2.this.launchConfigNewNetworkFragment(this.mConnectWifiEntry);
                    } else {
                        WifiSettings2.this.showDialog(this.mConnectWifiEntry, 1);
                    }
                } else if (i == 2) {
                    Toast.makeText(WifiSettings2.this.getContext(), C0017R$string.wifi_failed_connect_message, 0).show();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void launchConfigNewNetworkFragment(WifiEntry wifiEntry) {
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", wifiEntry.getKey());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setTitleText(wifiEntry.getTitle());
        subSettingLauncher.setDestination(ConfigureWifiEntryFragment.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setResultListener(this, 3);
        subSettingLauncher.launch();
    }

    private static boolean isDisabledByWrongPassword(WifiEntry wifiEntry) {
        WifiConfiguration.NetworkSelectionStatus networkSelectionStatus;
        WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
        if (wifiConfiguration == null || (networkSelectionStatus = wifiConfiguration.getNetworkSelectionStatus()) == null || networkSelectionStatus.getNetworkSelectionStatus() == 0 || 8 != networkSelectionStatus.getNetworkSelectionDisableReason()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void openSubscriptionHelpPage(WifiEntry wifiEntry) {
        Intent helpIntent = getHelpIntent(getContext(), wifiEntry.getHelpUriString());
        if (helpIntent != null) {
            try {
                startActivityForResult(helpIntent, 4);
            } catch (ActivityNotFoundException unused) {
                Log.e("WifiSettings2", "Activity was not found for intent, " + helpIntent.toString());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Intent getHelpIntent(Context context, String str) {
        return HelpUtils.getHelpIntent(context, str, context.getClass().getName());
    }
}
