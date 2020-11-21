package com.android.settings.wfd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplayStatus;
import android.media.MediaRouter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.R$styleable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import com.android.internal.app.MediaRouteDialogPresenter;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.TwoTargetPreference;

public final class WifiDisplaySettings extends SettingsPreferenceFragment implements SwitchBar.OnSwitchChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.wifi_display_settings);
    private boolean mAutoGO;
    private PreferenceGroup mCertCategory;
    private Preference mDescriptionPreference;
    private DisplayManager mDisplayManager;
    private final Handler mHandler = new Handler();
    private PreferenceCategory mListPreferenceCategory;
    private boolean mListen;
    private int mListenChannel;
    private Runnable mNoDeviceRunnable = new Runnable() {
        /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass2 */

        public void run() {
            WifiDisplaySettings.this.mTipsPreference.setSummary(C0017R$string.op_cast_no_devices);
        }
    };
    private int mOperatingChannel;
    private int mPendingChanges;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass16 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED")) {
                WifiDisplaySettings.this.scheduleUpdate(4);
            } else if (action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
                WifiDisplaySettings.this.scheduleUpdate(4);
            }
        }
    };
    private MediaRouter mRouter;
    private final MediaRouter.Callback mRouterCallback = new MediaRouter.SimpleCallback() {
        /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass18 */

        public void onRouteAdded(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            WifiDisplaySettings.this.scheduleUpdate(2);
        }

        public void onRouteChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            WifiDisplaySettings.this.scheduleUpdate(2);
        }

        public void onRouteRemoved(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            WifiDisplaySettings.this.scheduleUpdate(2);
        }

        public void onRouteSelected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
            WifiDisplaySettings.this.scheduleUpdate(2);
        }

        public void onRouteUnselected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
            WifiDisplaySettings.this.scheduleUpdate(2);
        }
    };
    private final ContentObserver mSettingsObserver = new ContentObserver(new Handler()) {
        /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass17 */

        public void onChange(boolean z, Uri uri) {
            WifiDisplaySettings.this.scheduleUpdate(1);
        }
    };
    private boolean mStarted;
    private SwitchBar mSwitchBar;
    private Preference mTipsPreference;
    private final Runnable mUpdateRunnable = new Runnable() {
        /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass15 */

        public void run() {
            int i = WifiDisplaySettings.this.mPendingChanges;
            WifiDisplaySettings.this.mPendingChanges = 0;
            WifiDisplaySettings.this.update(i);
        }
    };
    private boolean mWifiDisplayCertificationOn;
    private boolean mWifiDisplayOnSetting;
    private WifiDisplayStatus mWifiDisplayStatus;
    private WifiManager mWifiManager;
    private WifiP2pManager.Channel mWifiP2pChannel;
    private WifiP2pManager mWifiP2pManager;
    private int mWpsConfig = 4;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return R$styleable.Constraint_layout_goneMarginStart;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        MediaRouter mediaRouter = (MediaRouter) activity.getSystemService("media_router");
        this.mRouter = mediaRouter;
        mediaRouter.setRouterGroupId("android.media.mirroring_group");
        this.mDisplayManager = (DisplayManager) activity.getSystemService("display");
        WifiP2pManager wifiP2pManager = (WifiP2pManager) activity.getSystemService("wifip2p");
        this.mWifiP2pManager = wifiP2pManager;
        this.mWifiP2pChannel = wifiP2pManager.initialize(activity, Looper.getMainLooper(), null);
        addPreferencesFromResource(C0019R$xml.wifi_display_settings);
        setHasOptionsMenu(true);
        this.mWifiManager = (WifiManager) activity.getSystemService("wifi");
        Preference findPreference = getPreferenceScreen().findPreference("wifi_display_settings_description");
        this.mDescriptionPreference = findPreference;
        if (findPreference != null) {
            findPreference.setEnabled(false);
        }
        this.mListPreferenceCategory = (PreferenceCategory) getPreferenceScreen().findPreference("wifi_display_settings_list");
        this.mTipsPreference = getPreferenceScreen().findPreference("wifi_display_settings_tips");
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_remote_display;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        this.mStarted = true;
        if (getActivity() != null) {
            SwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
            this.mSwitchBar = switchBar;
            if (switchBar != null) {
                switchBar.show();
                SwitchBar switchBar2 = this.mSwitchBar;
                int i = C0017R$string.wifi_display_enable_menu_item;
                switchBar2.setSwitchBarText(i, i);
            }
        }
        FragmentActivity activity = getActivity();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        activity.registerReceiver(this.mReceiver, intentFilter);
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_on"), false, this.mSettingsObserver);
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_certification_on"), false, this.mSettingsObserver);
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_display_wps_config"), false, this.mSettingsObserver);
        this.mRouter.addCallback(4, this.mRouterCallback, 1);
        update(-1);
        this.mHandler.postDelayed(new Runnable() {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass1 */

            public void run() {
                if (WifiDisplaySettings.this.mSwitchBar != null) {
                    WifiDisplaySettings.this.mSwitchBar.addOnSwitchChangeListener(WifiDisplaySettings.this);
                }
                WifiDisplaySettings.this.initSwitchState();
            }
        }, 100);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        this.mStarted = false;
        getActivity().unregisterReceiver(this.mReceiver);
        getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        this.mRouter.removeCallback(this.mRouterCallback);
        SwitchBar switchBar = this.mSwitchBar;
        if (switchBar != null) {
            switchBar.removeOnSwitchChangeListener(this);
        }
        this.mHandler.removeCallbacksAndMessages(null);
        unscheduleUpdate();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void initSwitchState() {
        SwitchBar switchBar;
        WifiDisplayStatus wifiDisplayStatus = this.mWifiDisplayStatus;
        if (wifiDisplayStatus != null && wifiDisplayStatus.getFeatureState() != 0 && (switchBar = this.mSwitchBar) != null) {
            switchBar.setChecked(this.mWifiDisplayOnSetting);
        }
    }

    public static boolean isAvailable(Context context) {
        return (context.getSystemService("display") == null || !context.getPackageManager().hasSystemFeature("android.hardware.wifi.direct") || context.getSystemService("wifip2p") == null) ? false : true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void scheduleUpdate(int i) {
        if (this.mStarted) {
            if (this.mPendingChanges == 0) {
                this.mHandler.post(this.mUpdateRunnable);
            }
            this.mPendingChanges = i | this.mPendingChanges;
        }
    }

    private void unscheduleUpdate() {
        if (this.mPendingChanges != 0) {
            this.mPendingChanges = 0;
            this.mHandler.removeCallbacks(this.mUpdateRunnable);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void update(int i) {
        PreferenceGroup preferenceGroup;
        Log.d("WifiDisplaySettings", "changes = " + i);
        if ((i & 1) != 0) {
            this.mWifiDisplayOnSetting = Settings.Global.getInt(getContentResolver(), "wifi_display_on", 0) != 0;
            this.mWifiDisplayCertificationOn = Settings.Global.getInt(getContentResolver(), "wifi_display_certification_on", 0) != 0;
            this.mWpsConfig = Settings.Global.getInt(getContentResolver(), "wifi_display_wps_config", 4);
        }
        if ((i & 4) != 0) {
            this.mWifiDisplayStatus = this.mDisplayManager.getWifiDisplayStatus();
        }
        this.mListPreferenceCategory.removeAll();
        this.mListPreferenceCategory.setVisible(false);
        this.mDescriptionPreference.setVisible(true);
        PreferenceGroup preferenceGroup2 = this.mCertCategory;
        if (preferenceGroup2 != null) {
            preferenceGroup2.setVisible(true);
        }
        this.mHandler.removeCallbacks(this.mNoDeviceRunnable);
        int routeCount = this.mRouter.getRouteCount();
        for (int i2 = 0; i2 < routeCount; i2++) {
            MediaRouter.RouteInfo routeAt = this.mRouter.getRouteAt(i2);
            if (routeAt.matchesTypes(4)) {
                this.mListPreferenceCategory.addPreference(createRoutePreference(routeAt));
            }
        }
        if (this.mWifiDisplayStatus != null) {
            Log.d("WifiDisplaySettings", "mWifiDisplayStatus.getFeatureState() = " + this.mWifiDisplayStatus.getFeatureState());
            if (this.mWifiDisplayStatus.getFeatureState() == 3) {
                handleStateOn();
            } else if (this.mWifiDisplayStatus.getFeatureState() == 1) {
                if (this.mWifiDisplayOnSetting) {
                    Log.d("WifiDisplaySettings", "mWifiManager.isWifiApEnabled() = " + this.mWifiManager.isWifiApEnabled());
                    Log.d("WifiDisplaySettings", "mWifiManager.isWifiEnabled() = " + this.mWifiManager.isWifiEnabled());
                    if (!this.mWifiManager.isWifiApEnabled() || !this.mWifiManager.isWifiEnabled()) {
                        this.mListPreferenceCategory.setVisible(true);
                        this.mDescriptionPreference.setVisible(false);
                        if (this.mListPreferenceCategory.getPreferenceCount() == 0) {
                            this.mTipsPreference.setSummary(C0017R$string.op_cast_prompt);
                            this.mListPreferenceCategory.addPreference(this.mTipsPreference);
                            return;
                        }
                        return;
                    }
                    handleStateOn();
                }
            } else if (this.mWifiDisplayStatus.getFeatureState() == 2 && !this.mWifiDisplayOnSetting && (preferenceGroup = this.mCertCategory) != null) {
                preferenceGroup.setVisible(false);
            }
        }
    }

    private void handleStateOn() {
        WifiDisplay[] displays = this.mWifiDisplayStatus.getDisplays();
        for (WifiDisplay wifiDisplay : displays) {
            if (!wifiDisplay.isRemembered() && wifiDisplay.isAvailable() && !wifiDisplay.equals(this.mWifiDisplayStatus.getActiveDisplay())) {
                this.mListPreferenceCategory.addPreference(new UnpairedWifiDisplayPreference(getPrefContext(), wifiDisplay));
            }
        }
        this.mListPreferenceCategory.setVisible(true);
        this.mDescriptionPreference.setVisible(false);
        if (this.mListPreferenceCategory.getPreferenceCount() == 0) {
            this.mTipsPreference.setSummary(C0017R$string.op_cast_searching);
            this.mListPreferenceCategory.addPreference(this.mTipsPreference);
            this.mHandler.postDelayed(this.mNoDeviceRunnable, 4000);
        }
        if (this.mListPreferenceCategory.getPreferenceCount() != 1 || !"wifi_display_settings_tips".equals(this.mListPreferenceCategory.getPreference(0).getKey())) {
            this.mHandler.removeCallbacks(this.mNoDeviceRunnable);
        }
        if (this.mWifiDisplayCertificationOn) {
            buildCertificationMenu(getPreferenceScreen());
        }
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        this.mWifiDisplayCertificationOn = !z ? 1 : 0;
        Settings.Global.putInt(getContentResolver(), "wifi_display_on", z ? 1 : 0);
    }

    private RoutePreference createRoutePreference(MediaRouter.RouteInfo routeInfo) {
        WifiDisplay findWifiDisplay = findWifiDisplay(routeInfo.getDeviceAddress());
        if (findWifiDisplay != null) {
            return new WifiDisplayRoutePreference(getPrefContext(), routeInfo, findWifiDisplay);
        }
        return new RoutePreference(getPrefContext(), routeInfo);
    }

    private WifiDisplay findWifiDisplay(String str) {
        WifiDisplayStatus wifiDisplayStatus = this.mWifiDisplayStatus;
        if (wifiDisplayStatus == null || str == null) {
            return null;
        }
        WifiDisplay[] displays = wifiDisplayStatus.getDisplays();
        for (WifiDisplay wifiDisplay : displays) {
            if (wifiDisplay.getDeviceAddress().equals(str)) {
                return wifiDisplay;
            }
        }
        return null;
    }

    private void buildCertificationMenu(PreferenceScreen preferenceScreen) {
        PreferenceGroup preferenceGroup = this.mCertCategory;
        if (preferenceGroup == null) {
            PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
            this.mCertCategory = preferenceCategory;
            preferenceCategory.setTitle(C0017R$string.wifi_display_certification_heading);
            this.mCertCategory.setOrder(1);
        } else {
            preferenceGroup.removeAll();
        }
        preferenceScreen.addPreference(this.mCertCategory);
        if (!this.mWifiDisplayStatus.getSessionInfo().getGroupId().isEmpty()) {
            Preference preference = new Preference(getPrefContext());
            preference.setTitle(C0017R$string.wifi_display_session_info);
            preference.setSummary(this.mWifiDisplayStatus.getSessionInfo().toString());
            this.mCertCategory.addPreference(preference);
            if (this.mWifiDisplayStatus.getSessionInfo().getSessionId() != 0) {
                this.mCertCategory.addPreference(new Preference(getPrefContext()) {
                    /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass3 */

                    @Override // androidx.preference.Preference
                    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
                        super.onBindViewHolder(preferenceViewHolder);
                        Button button = (Button) preferenceViewHolder.findViewById(C0010R$id.left_button);
                        button.setText(C0017R$string.wifi_display_pause);
                        button.setOnClickListener(new View.OnClickListener() {
                            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass3.AnonymousClass1 */

                            public void onClick(View view) {
                                WifiDisplaySettings.this.mDisplayManager.pauseWifiDisplay();
                            }
                        });
                        Button button2 = (Button) preferenceViewHolder.findViewById(C0010R$id.right_button);
                        button2.setText(C0017R$string.wifi_display_resume);
                        button2.setOnClickListener(new View.OnClickListener() {
                            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass3.AnonymousClass2 */

                            public void onClick(View view) {
                                WifiDisplaySettings.this.mDisplayManager.resumeWifiDisplay();
                            }
                        });
                    }
                });
                this.mCertCategory.setLayoutResource(C0012R$layout.two_buttons_panel);
            }
        }
        AnonymousClass4 r11 = new SwitchPreference(getPrefContext()) {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass4 */

            /* access modifiers changed from: protected */
            @Override // androidx.preference.SwitchPreference, androidx.preference.TwoStatePreference, androidx.preference.Preference
            public void onClick() {
                WifiDisplaySettings wifiDisplaySettings = WifiDisplaySettings.this;
                wifiDisplaySettings.mListen = !wifiDisplaySettings.mListen;
                WifiDisplaySettings wifiDisplaySettings2 = WifiDisplaySettings.this;
                wifiDisplaySettings2.setListenMode(wifiDisplaySettings2.mListen);
                setChecked(WifiDisplaySettings.this.mListen);
            }
        };
        r11.setTitle(C0017R$string.wifi_display_listen_mode);
        r11.setChecked(this.mListen);
        this.mCertCategory.addPreference(r11);
        AnonymousClass5 r112 = new SwitchPreference(getPrefContext()) {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass5 */

            /* access modifiers changed from: protected */
            @Override // androidx.preference.SwitchPreference, androidx.preference.TwoStatePreference, androidx.preference.Preference
            public void onClick() {
                WifiDisplaySettings wifiDisplaySettings = WifiDisplaySettings.this;
                wifiDisplaySettings.mAutoGO = !wifiDisplaySettings.mAutoGO;
                if (WifiDisplaySettings.this.mAutoGO) {
                    WifiDisplaySettings.this.startAutoGO();
                } else {
                    WifiDisplaySettings.this.stopAutoGO();
                }
                setChecked(WifiDisplaySettings.this.mAutoGO);
            }
        };
        r112.setTitle(C0017R$string.wifi_display_autonomous_go);
        r112.setChecked(this.mAutoGO);
        this.mCertCategory.addPreference(r112);
        ListPreference listPreference = new ListPreference(getPrefContext());
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass6 */

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                int parseInt = Integer.parseInt((String) obj);
                if (parseInt == WifiDisplaySettings.this.mWpsConfig) {
                    return true;
                }
                WifiDisplaySettings.this.mWpsConfig = parseInt;
                WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
                Settings.Global.putInt(WifiDisplaySettings.this.getActivity().getContentResolver(), "wifi_display_wps_config", WifiDisplaySettings.this.mWpsConfig);
                return true;
            }
        });
        this.mWpsConfig = Settings.Global.getInt(getActivity().getContentResolver(), "wifi_display_wps_config", 4);
        listPreference.setKey("wps");
        listPreference.setTitle(C0017R$string.wifi_display_wps_config);
        listPreference.setEntries(new String[]{"Default", "PBC", "KEYPAD", "DISPLAY"});
        listPreference.setEntryValues(new String[]{"4", "0", "2", "1"});
        listPreference.setValue("" + this.mWpsConfig);
        listPreference.setSummary("%1$s");
        this.mCertCategory.addPreference(listPreference);
        ListPreference listPreference2 = new ListPreference(getPrefContext());
        listPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass7 */

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                int parseInt = Integer.parseInt((String) obj);
                if (parseInt == WifiDisplaySettings.this.mListenChannel) {
                    return true;
                }
                WifiDisplaySettings.this.mListenChannel = parseInt;
                WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
                WifiDisplaySettings wifiDisplaySettings = WifiDisplaySettings.this;
                wifiDisplaySettings.setWifiP2pChannels(wifiDisplaySettings.mListenChannel, WifiDisplaySettings.this.mOperatingChannel);
                return true;
            }
        });
        listPreference2.setKey("listening_channel");
        listPreference2.setTitle(C0017R$string.wifi_display_listen_channel);
        listPreference2.setEntries(new String[]{"Auto", "1", "6", "11"});
        listPreference2.setEntryValues(new String[]{"0", "1", "6", "11"});
        listPreference2.setValue("" + this.mListenChannel);
        listPreference2.setSummary("%1$s");
        this.mCertCategory.addPreference(listPreference2);
        ListPreference listPreference3 = new ListPreference(getPrefContext());
        listPreference3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass8 */

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                int parseInt = Integer.parseInt((String) obj);
                if (parseInt == WifiDisplaySettings.this.mOperatingChannel) {
                    return true;
                }
                WifiDisplaySettings.this.mOperatingChannel = parseInt;
                WifiDisplaySettings.this.getActivity().invalidateOptionsMenu();
                WifiDisplaySettings wifiDisplaySettings = WifiDisplaySettings.this;
                wifiDisplaySettings.setWifiP2pChannels(wifiDisplaySettings.mListenChannel, WifiDisplaySettings.this.mOperatingChannel);
                return true;
            }
        });
        listPreference3.setKey("operating_channel");
        listPreference3.setTitle(C0017R$string.wifi_display_operating_channel);
        listPreference3.setEntries(new String[]{"Auto", "1", "6", "11", "36"});
        listPreference3.setEntryValues(new String[]{"0", "1", "6", "11", "36"});
        listPreference3.setValue("" + this.mOperatingChannel);
        listPreference3.setSummary("%1$s");
        this.mCertCategory.addPreference(listPreference3);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startAutoGO() {
        this.mWifiP2pManager.createGroup(this.mWifiP2pChannel, new WifiP2pManager.ActionListener(this) {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass9 */

            public void onSuccess() {
            }

            public void onFailure(int i) {
                Slog.e("WifiDisplaySettings", "Failed to start AutoGO with reason " + i + ".");
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void stopAutoGO() {
        this.mWifiP2pManager.removeGroup(this.mWifiP2pChannel, new WifiP2pManager.ActionListener(this) {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass10 */

            public void onSuccess() {
            }

            public void onFailure(int i) {
                Slog.e("WifiDisplaySettings", "Failed to stop AutoGO with reason " + i + ".");
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setListenMode(final boolean z) {
        AnonymousClass11 r0 = new WifiP2pManager.ActionListener(this) {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass11 */

            public void onSuccess() {
            }

            public void onFailure(int i) {
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to ");
                sb.append(z ? "entered" : "exited");
                sb.append(" listen mode with reason ");
                sb.append(i);
                sb.append(".");
                Slog.e("WifiDisplaySettings", sb.toString());
            }
        };
        if (z) {
            this.mWifiP2pManager.startListening(this.mWifiP2pChannel, r0);
        } else {
            this.mWifiP2pManager.stopListening(this.mWifiP2pChannel, r0);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setWifiP2pChannels(int i, int i2) {
        this.mWifiP2pManager.setWifiP2pChannels(this.mWifiP2pChannel, i, i2, new WifiP2pManager.ActionListener(this) {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass12 */

            public void onSuccess() {
            }

            public void onFailure(int i) {
                Slog.e("WifiDisplaySettings", "Failed to set wifi p2p channels with reason " + i + ".");
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void toggleRoute(MediaRouter.RouteInfo routeInfo) {
        if (routeInfo.isSelected()) {
            MediaRouteDialogPresenter.showDialogFragment(getActivity(), 4, (View.OnClickListener) null);
        } else {
            routeInfo.select();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void pairWifiDisplay(WifiDisplay wifiDisplay) {
        if (wifiDisplay.canConnect()) {
            this.mDisplayManager.connectWifiDisplay(wifiDisplay.getDeviceAddress());
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showWifiDisplayOptionsDialog(final WifiDisplay wifiDisplay) {
        View inflate = getActivity().getLayoutInflater().inflate(C0012R$layout.wifi_display_options, (ViewGroup) null);
        final EditText editText = (EditText) inflate.findViewById(C0010R$id.name);
        editText.setText(wifiDisplay.getFriendlyDisplayName());
        AnonymousClass13 r2 = new DialogInterface.OnClickListener() {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass13 */

            public void onClick(DialogInterface dialogInterface, int i) {
                String trim = editText.getText().toString().trim();
                if (trim.isEmpty() || trim.equals(wifiDisplay.getDeviceName())) {
                    trim = null;
                }
                WifiDisplaySettings.this.mDisplayManager.renameWifiDisplay(wifiDisplay.getDeviceAddress(), trim);
            }
        };
        AnonymousClass14 r1 = new DialogInterface.OnClickListener() {
            /* class com.android.settings.wfd.WifiDisplaySettings.AnonymousClass14 */

            public void onClick(DialogInterface dialogInterface, int i) {
                WifiDisplaySettings.this.mDisplayManager.forgetWifiDisplay(wifiDisplay.getDeviceAddress());
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(C0017R$string.wifi_display_options_title);
        builder.setView(inflate);
        builder.setPositiveButton(C0017R$string.wifi_display_options_done, r2);
        builder.setNegativeButton(C0017R$string.wifi_display_options_forget, r1);
        builder.create().show();
    }

    /* access modifiers changed from: private */
    public class RoutePreference extends TwoTargetPreference implements Preference.OnPreferenceClickListener {
        private final MediaRouter.RouteInfo mRoute;

        public RoutePreference(Context context, MediaRouter.RouteInfo routeInfo) {
            super(context);
            this.mRoute = routeInfo;
            setTitle(routeInfo.getName());
            setSummary(routeInfo.getDescription());
            setEnabled(routeInfo.isEnabled());
            if (routeInfo.isSelected()) {
                setOrder(2);
                if (routeInfo.isConnecting()) {
                    setSummary(C0017R$string.wifi_display_status_connecting);
                } else {
                    setSummary(C0017R$string.wifi_display_status_connected);
                }
            } else if (isEnabled()) {
                setOrder(3);
            } else {
                setOrder(4);
                if (routeInfo.getStatusCode() == 5) {
                    setSummary(C0017R$string.wifi_display_status_in_use);
                } else {
                    setSummary(C0017R$string.wifi_display_status_not_available);
                }
            }
            setOnPreferenceClickListener(this);
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            WifiDisplaySettings.this.toggleRoute(this.mRoute);
            return true;
        }
    }

    /* access modifiers changed from: private */
    public class WifiDisplayRoutePreference extends RoutePreference implements View.OnClickListener {
        private final WifiDisplay mDisplay;

        /* access modifiers changed from: protected */
        @Override // com.android.settingslib.TwoTargetPreference
        public int getSecondTargetResId() {
            return C0012R$layout.preference_widget_gear;
        }

        public WifiDisplayRoutePreference(Context context, MediaRouter.RouteInfo routeInfo, WifiDisplay wifiDisplay) {
            super(context, routeInfo);
            this.mDisplay = wifiDisplay;
        }

        @Override // com.android.settingslib.TwoTargetPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            ImageView imageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.settings_button);
            if (imageView != null) {
                imageView.setOnClickListener(this);
                if (!isEnabled()) {
                    TypedValue typedValue = new TypedValue();
                    getContext().getTheme().resolveAttribute(16842803, typedValue, true);
                    imageView.setImageAlpha((int) (typedValue.getFloat() * 255.0f));
                    imageView.setEnabled(true);
                }
            }
        }

        public void onClick(View view) {
            WifiDisplaySettings.this.showWifiDisplayOptionsDialog(this.mDisplay);
        }
    }

    /* access modifiers changed from: private */
    public class UnpairedWifiDisplayPreference extends Preference implements Preference.OnPreferenceClickListener {
        private final WifiDisplay mDisplay;

        public UnpairedWifiDisplayPreference(Context context, WifiDisplay wifiDisplay) {
            super(context);
            this.mDisplay = wifiDisplay;
            setTitle(wifiDisplay.getFriendlyDisplayName());
            setSummary(17041511);
            setEnabled(wifiDisplay.canConnect());
            if (isEnabled()) {
                setOrder(3);
            } else {
                setOrder(4);
                setSummary(C0017R$string.wifi_display_status_in_use);
            }
            setOnPreferenceClickListener(this);
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            WifiDisplaySettings.this.pairWifiDisplay(this.mDisplay);
            return true;
        }
    }
}
