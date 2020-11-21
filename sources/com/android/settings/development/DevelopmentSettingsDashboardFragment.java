package com.android.settings.development;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.development.BluetoothA2dpHwOffloadRebootDialog;
import com.android.settings.development.DevelopmentSettingsDashboardFragment;
import com.android.settings.development.autofill.AutofillLoggingLevelPreferenceController;
import com.android.settings.development.autofill.AutofillResetOptionsPreferenceController;
import com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController;
import com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController;
import com.android.settings.development.bluetooth.BluetoothBitPerSampleDialogPreferenceController;
import com.android.settings.development.bluetooth.BluetoothChannelModeDialogPreferenceController;
import com.android.settings.development.bluetooth.BluetoothCodecDialogPreferenceController;
import com.android.settings.development.bluetooth.BluetoothHDAudioPreferenceController;
import com.android.settings.development.bluetooth.BluetoothQualityDialogPreferenceController;
import com.android.settings.development.bluetooth.BluetoothSampleRateDialogPreferenceController;
import com.android.settings.development.storage.SharedDataPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.development.SystemPropPoker;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.oneplus.settings.development.BluetoothHidDeviceProfilePreferenceController;
import com.oneplus.settings.development.OPAdvancedRebootPreferenceController;
import com.oneplus.settings.development.OPGetLogsPreferenceController;
import com.oneplus.settings.development.OPWifiVerboseMultiBroadcastPreferenceController;
import com.oneplus.settings.development.OPWirlessAdbDebuggingPreferenceController;
import com.oneplus.settings.utils.ProductUtils;
import java.util.ArrayList;
import java.util.List;

public class DevelopmentSettingsDashboardFragment extends RestrictedDashboardFragment implements SwitchBar.OnSwitchChangeListener, OemUnlockDialogHost, AdbDialogHost, AdbClearKeysDialogHost, LogPersistDialogHost, BluetoothA2dpHwOffloadRebootDialog.OnA2dpHwDialogConfirmedListener, AbstractBluetoothPreferenceController.Callback {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.development_settings) {
        /* class com.android.settings.development.DevelopmentSettingsDashboardFragment.AnonymousClass5 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return DevelopmentSettingsDashboardFragment.buildPreferenceControllers(context, null, null, null, null);
        }
    };
    private static OemUnlockPreferenceController mOemController;
    private BluetoothA2dp mBluetoothA2dp;
    private final BluetoothA2dpConfigStore mBluetoothA2dpConfigStore = new BluetoothA2dpConfigStore();
    private final BroadcastReceiver mBluetoothA2dpReceiver = new BroadcastReceiver() {
        /* class com.android.settings.development.DevelopmentSettingsDashboardFragment.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            Log.d("DevSettingsDashboard", "mBluetoothA2dpReceiver.onReceive intent=" + intent);
            if ("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED".equals(intent.getAction())) {
                Log.d("DevSettingsDashboard", "Received BluetoothCodecStatus=" + intent.getParcelableExtra("android.bluetooth.extra.CODEC_STATUS"));
                for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                    if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                        ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothCodecUpdated();
                    }
                }
            }
        }
    };
    private final BluetoothProfile.ServiceListener mBluetoothA2dpServiceListener = new BluetoothProfile.ServiceListener() {
        /* class com.android.settings.development.DevelopmentSettingsDashboardFragment.AnonymousClass3 */

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            synchronized (DevelopmentSettingsDashboardFragment.this.mBluetoothA2dpConfigStore) {
                DevelopmentSettingsDashboardFragment.this.mBluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
            }
            for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                    ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothServiceConnected(DevelopmentSettingsDashboardFragment.this.mBluetoothA2dp);
                }
            }
        }

        public void onServiceDisconnected(int i) {
            synchronized (DevelopmentSettingsDashboardFragment.this.mBluetoothA2dpConfigStore) {
                DevelopmentSettingsDashboardFragment.this.mBluetoothA2dp = null;
            }
            for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                    ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothServiceDisconnected();
                }
            }
        }
    };
    private final BroadcastReceiver mEnableAdbReceiver = new BroadcastReceiver() {
        /* class com.android.settings.development.DevelopmentSettingsDashboardFragment.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                if (abstractPreferenceController instanceof AdbOnChangeListener) {
                    ((AdbOnChangeListener) abstractPreferenceController).onAdbSettingChanged();
                }
            }
        }
    };
    private boolean mIsAvailable = true;
    private List<AbstractPreferenceController> mPreferenceControllers = new ArrayList();
    private SwitchBar mSwitchBar;
    private final Runnable mSystemPropertiesChanged = new Runnable() {
        /* class com.android.settings.development.DevelopmentSettingsDashboardFragment.AnonymousClass4 */

        public void run() {
            synchronized (this) {
                FragmentActivity activity = DevelopmentSettingsDashboardFragment.this.getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        /* class com.android.settings.development.$$Lambda$DevelopmentSettingsDashboardFragment$4$iLzi7EVzpr8j1T9wKt38aUr5Cc */

                        public final void run() {
                            DevelopmentSettingsDashboardFragment.AnonymousClass4.this.lambda$run$0$DevelopmentSettingsDashboardFragment$4();
                        }
                    });
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$run$0 */
        public /* synthetic */ void lambda$run$0$DevelopmentSettingsDashboardFragment$4() {
            DevelopmentSettingsDashboardFragment.this.updatePreferenceStates();
        }
    };

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DevSettingsDashboard";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 39;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public boolean isParalleledControllers() {
        return true;
    }

    public DevelopmentSettingsDashboardFragment() {
        super("no_debugging_features");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Utils.isMonkeyRunning()) {
            getActivity().finish();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setIfOnlyAvailableForAdmins(true);
        if (isUiRestricted() || !WizardManagerHelper.isDeviceProvisioned(getActivity())) {
            this.mIsAvailable = false;
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(C0017R$string.development_settings_not_available);
            }
            getPreferenceScreen().removeAll();
            return;
        }
        SwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        new DevelopmentSwitchBarController(this, switchBar, this.mIsAvailable, getSettingsLifecycle());
        this.mSwitchBar.show();
        if (DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(getContext())) {
            enableDeveloperOptions();
        } else {
            disableDeveloperOptions();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        registerReceivers();
        SystemProperties.addChangeCallback(this.mSystemPropertiesChanged);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.getProfileProxy(getActivity(), this.mBluetoothA2dpServiceListener, 2);
        }
        if (ProductUtils.isUsvMode()) {
            ((PreferenceCategory) findPreference("debug_misc_category")).removePreference(findPreference("demo_mode"));
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        unregisterReceivers();
        OemUnlockPreferenceController oemUnlockPreferenceController = mOemController;
        if (oemUnlockPreferenceController != null) {
            oemUnlockPreferenceController.unBindSimlockConnection();
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.closeProfileProxy(2, this.mBluetoothA2dp);
            this.mBluetoothA2dp = null;
        }
        SystemProperties.removeChangeCallback(this.mSystemPropertiesChanged);
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        if (r2 != this.mSwitchBar.getSwitch() || z == DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(getContext())) {
            return;
        }
        if (z) {
            EnableDevelopmentSettingWarningDialog.show(this);
            return;
        }
        BluetoothA2dpHwOffloadPreferenceController bluetoothA2dpHwOffloadPreferenceController = (BluetoothA2dpHwOffloadPreferenceController) getDevelopmentOptionsController(BluetoothA2dpHwOffloadPreferenceController.class);
        if (bluetoothA2dpHwOffloadPreferenceController == null || bluetoothA2dpHwOffloadPreferenceController.isDefaultValue()) {
            disableDeveloperOptions();
        } else {
            DisableDevSettingsDialogFragment.show(this);
        }
    }

    @Override // com.android.settings.development.OemUnlockDialogHost
    public void onOemUnlockDialogConfirmed() {
        ((OemUnlockPreferenceController) getDevelopmentOptionsController(OemUnlockPreferenceController.class)).onOemUnlockConfirmed();
    }

    @Override // com.android.settings.development.OemUnlockDialogHost
    public void onOemUnlockDialogDismissed() {
        ((OemUnlockPreferenceController) getDevelopmentOptionsController(OemUnlockPreferenceController.class)).onOemUnlockDismissed();
    }

    @Override // com.android.settings.development.AdbDialogHost
    public void onEnableAdbDialogConfirmed() {
        ((AdbPreferenceController) getDevelopmentOptionsController(AdbPreferenceController.class)).onAdbDialogConfirmed();
    }

    @Override // com.android.settings.development.AdbDialogHost
    public void onEnableAdbDialogDismissed() {
        ((AdbPreferenceController) getDevelopmentOptionsController(AdbPreferenceController.class)).onAdbDialogDismissed();
    }

    @Override // com.android.settings.development.AdbClearKeysDialogHost
    public void onAdbClearKeysDialogConfirmed() {
        ((ClearAdbKeysPreferenceController) getDevelopmentOptionsController(ClearAdbKeysPreferenceController.class)).onClearAdbKeysConfirmed();
    }

    @Override // com.android.settings.development.LogPersistDialogHost
    public void onDisableLogPersistDialogConfirmed() {
        ((LogPersistPreferenceController) getDevelopmentOptionsController(LogPersistPreferenceController.class)).onDisableLogPersistDialogConfirmed();
    }

    @Override // com.android.settings.development.LogPersistDialogHost
    public void onDisableLogPersistDialogRejected() {
        ((LogPersistPreferenceController) getDevelopmentOptionsController(LogPersistPreferenceController.class)).onDisableLogPersistDialogRejected();
    }

    @Override // com.android.settings.development.BluetoothA2dpHwOffloadRebootDialog.OnA2dpHwDialogConfirmedListener
    public void onA2dpHwDialogConfirmed() {
        ((BluetoothA2dpHwOffloadPreferenceController) getDevelopmentOptionsController(BluetoothA2dpHwOffloadPreferenceController.class)).onA2dpHwDialogConfirmed();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment
    public void onActivityResult(int i, int i2, Intent intent) {
        boolean z = false;
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if (abstractPreferenceController instanceof OnActivityResultListener) {
                z |= ((OnActivityResultListener) abstractPreferenceController).onActivityResult(i, i2, intent);
            }
        }
        if (!z) {
            super.onActivityResult(i, i2, intent);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return Utils.isMonkeyRunning() ? C0019R$xml.placeholder_prefs : C0019R$xml.development_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        if (Utils.isMonkeyRunning()) {
            this.mPreferenceControllers = new ArrayList();
            return null;
        }
        List<AbstractPreferenceController> buildPreferenceControllers = buildPreferenceControllers(context, getActivity(), getSettingsLifecycle(), this, new BluetoothA2dpConfigStore());
        this.mPreferenceControllers = buildPreferenceControllers;
        return buildPreferenceControllers;
    }

    private void registerReceivers() {
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(this.mEnableAdbReceiver, new IntentFilter("com.android.settingslib.development.AbstractEnableAdbController.ENABLE_ADB_STATE_CHANGED"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED");
        getActivity().registerReceiver(this.mBluetoothA2dpReceiver, intentFilter);
    }

    private void unregisterReceivers() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.mEnableAdbReceiver);
        getActivity().unregisterReceiver(this.mBluetoothA2dpReceiver);
    }

    private void enableDeveloperOptions() {
        if (!Utils.isMonkeyRunning()) {
            DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(getContext(), true);
            for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
                if (abstractPreferenceController instanceof DeveloperOptionsPreferenceController) {
                    ((DeveloperOptionsPreferenceController) abstractPreferenceController).onDeveloperOptionsEnabled();
                }
            }
        }
    }

    private void disableDeveloperOptions() {
        if (!Utils.isMonkeyRunning()) {
            DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(getContext(), false);
            SystemPropPoker instance = SystemPropPoker.getInstance();
            instance.blockPokes();
            for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
                if (abstractPreferenceController instanceof DeveloperOptionsPreferenceController) {
                    ((DeveloperOptionsPreferenceController) abstractPreferenceController).onDeveloperOptionsDisabled();
                }
            }
            instance.unblockPokes();
            instance.poke();
        }
    }

    /* access modifiers changed from: package-private */
    public void onEnableDevelopmentOptionsConfirmed() {
        enableDeveloperOptions();
    }

    /* access modifiers changed from: package-private */
    public void onEnableDevelopmentOptionsRejected() {
        this.mSwitchBar.setChecked(false);
    }

    /* access modifiers changed from: package-private */
    public void onDisableDevelopmentOptionsConfirmed() {
        disableDeveloperOptions();
    }

    /* access modifiers changed from: package-private */
    public void onDisableDevelopmentOptionsRejected() {
        this.mSwitchBar.setChecked(true);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Activity activity, Lifecycle lifecycle, DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MemoryUsagePreferenceController(context));
        arrayList.add(new BugReportPreferenceController(context));
        arrayList.add(new BugReportHandlerPreferenceController(context));
        arrayList.add(new SystemServerHeapDumpPreferenceController(context));
        arrayList.add(new LocalBackupPasswordPreferenceController(context));
        arrayList.add(new OPGetLogsPreferenceController(context));
        arrayList.add(new OPAdvancedRebootPreferenceController(context, lifecycle));
        arrayList.add(new OPWirlessAdbDebuggingPreferenceController(context));
        arrayList.add(new OPWifiVerboseMultiBroadcastPreferenceController(context));
        arrayList.add(new StayAwakePreferenceController(context, lifecycle));
        arrayList.add(new HdcpCheckingPreferenceController(context));
        arrayList.add(new BluetoothSnoopLogPreferenceController(context));
        arrayList.add(new OemUnlockPreferenceController(context, activity, developmentSettingsDashboardFragment));
        arrayList.add(new FileEncryptionPreferenceController(context));
        arrayList.add(new WebViewAppPreferenceController(context));
        arrayList.add(new CoolColorTemperaturePreferenceController(context));
        arrayList.add(new DisableAutomaticUpdatesPreferenceController(context));
        arrayList.add(new SelectDSUPreferenceController(context));
        arrayList.add(new AdbPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new ClearAdbKeysPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new WirelessDebuggingPreferenceController(context, lifecycle));
        arrayList.add(new AdbAuthorizationTimeoutPreferenceController(context));
        arrayList.add(new LocalTerminalPreferenceController(context));
        arrayList.add(new BugReportInPowerPreferenceController(context));
        arrayList.add(new AutomaticSystemServerHeapDumpPreferenceController(context));
        arrayList.add(new MockLocationAppPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new DebugViewAttributesPreferenceController(context));
        arrayList.add(new SelectDebugAppPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new WaitForDebuggerPreferenceController(context));
        arrayList.add(new EnableGpuDebugLayersPreferenceController(context));
        arrayList.add(new ForcePeakRefreshRatePreferenceController(context));
        arrayList.add(new EnableVerboseVendorLoggingPreferenceController(context));
        arrayList.add(new VerifyAppsOverUsbPreferenceController(context));
        arrayList.add(new ArtVerifierPreferenceController(context));
        arrayList.add(new LogdSizePreferenceController(context));
        if (!"user".equalsIgnoreCase(Build.TYPE)) {
            arrayList.add(new LogPersistPreferenceController(context, developmentSettingsDashboardFragment, lifecycle));
        }
        arrayList.add(new CameraLaserSensorPreferenceController(context));
        arrayList.add(new WifiDisplayCertificationPreferenceController(context));
        arrayList.add(new WifiCoverageExtendPreferenceController(context));
        arrayList.add(new WifiVerboseLoggingPreferenceController(context));
        arrayList.add(new WifiScanThrottlingPreferenceController(context));
        arrayList.add(new WifiEnhancedMacRandomizationPreferenceController(context));
        arrayList.add(new MobileDataAlwaysOnPreferenceController(context));
        arrayList.add(new TetheringHardwareAccelPreferenceController(context));
        arrayList.add(new BluetoothDeviceNoNamePreferenceController(context));
        arrayList.add(new BluetoothAbsoluteVolumePreferenceController(context));
        arrayList.add(new BluetoothHidDeviceProfilePreferenceController(context));
        arrayList.add(new BluetoothGabeldorschePreferenceController(context));
        arrayList.add(new BluetoothAvrcpVersionPreferenceController(context));
        arrayList.add(new BluetoothMapVersionPreferenceController(context));
        arrayList.add(new BluetoothA2dpHwOffloadPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new BluetoothMaxConnectedAudioDevicesPreferenceController(context));
        arrayList.add(new EnhancedConnectivityPreferenceController(context));
        arrayList.add(new ShowTapsPreferenceController(context));
        arrayList.add(new PointerLocationPreferenceController(context));
        arrayList.add(new ShowSurfaceUpdatesPreferenceController(context));
        arrayList.add(new ShowLayoutBoundsPreferenceController(context));
        arrayList.add(new ShowRefreshRatePreferenceController(context));
        arrayList.add(new RtlLayoutPreferenceController(context));
        arrayList.add(new WindowAnimationScalePreferenceController(context));
        arrayList.add(new EmulateDisplayCutoutPreferenceController(context));
        arrayList.add(new TransitionAnimationScalePreferenceController(context));
        arrayList.add(new AnimatorDurationScalePreferenceController(context));
        arrayList.add(new SecondaryDisplayPreferenceController(context));
        arrayList.add(new GpuViewUpdatesPreferenceController(context));
        arrayList.add(new HardwareLayersUpdatesPreferenceController(context));
        arrayList.add(new DebugGpuOverdrawPreferenceController(context));
        arrayList.add(new DebugNonRectClipOperationsPreferenceController(context));
        arrayList.add(new ForceDarkPreferenceController(context));
        arrayList.add(new EnableBlursPreferenceController(context));
        arrayList.add(new ForceMSAAPreferenceController(context));
        arrayList.add(new HardwareOverlaysPreferenceController(context));
        arrayList.add(new SimulateColorSpacePreferenceController(context));
        arrayList.add(new UsbAudioRoutingPreferenceController(context));
        arrayList.add(new StrictModePreferenceController(context));
        arrayList.add(new ProfileGpuRenderingPreferenceController(context));
        arrayList.add(new KeepActivitiesPreferenceController(context));
        arrayList.add(new BackgroundProcessLimitPreferenceController(context));
        arrayList.add(new CachedAppsFreezerPreferenceController(context));
        arrayList.add(new ShowFirstCrashDialogPreferenceController(context));
        arrayList.add(new AppsNotRespondingPreferenceController(context));
        arrayList.add(new NotificationChannelWarningsPreferenceController(context));
        arrayList.add(new AllowAppsOnExternalPreferenceController(context));
        arrayList.add(new ResizableActivityPreferenceController(context));
        arrayList.add(new FreeformWindowsPreferenceController(context));
        arrayList.add(new DesktopModePreferenceController(context));
        arrayList.add(new SizeCompatFreeformPreferenceController(context));
        arrayList.add(new ShortcutManagerThrottlingPreferenceController(context));
        arrayList.add(new EnableGnssRawMeasFullTrackingPreferenceController(context));
        arrayList.add(new DefaultLaunchPreferenceController(context, "running_apps"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "demo_mode"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "quick_settings_tiles"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "feature_flags_dashboard"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "default_usb_configuration"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "density"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "background_check"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "inactive_apps"));
        arrayList.add(new AutofillLoggingLevelPreferenceController(context, lifecycle));
        arrayList.add(new AutofillResetOptionsPreferenceController(context));
        arrayList.add(new BluetoothCodecDialogPreferenceController(context, lifecycle, bluetoothA2dpConfigStore, developmentSettingsDashboardFragment));
        arrayList.add(new BluetoothSampleRateDialogPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothBitPerSampleDialogPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothQualityDialogPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothChannelModeDialogPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothHDAudioPreferenceController(context, lifecycle, bluetoothA2dpConfigStore, developmentSettingsDashboardFragment));
        arrayList.add(new SharedDataPreferenceController(context));
        arrayList.add(new OverlaySettingsPreferenceController(context));
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public <T extends AbstractPreferenceController> T getDevelopmentOptionsController(Class<T> cls) {
        return (T) use(cls);
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController.Callback
    public void onBluetoothCodecChanged() {
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if ((abstractPreferenceController instanceof AbstractBluetoothDialogPreferenceController) && !(abstractPreferenceController instanceof BluetoothCodecDialogPreferenceController)) {
                ((AbstractBluetoothDialogPreferenceController) abstractPreferenceController).onBluetoothCodecUpdated();
            }
        }
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController.Callback
    public void onBluetoothHDAudioEnabled(boolean z) {
        Log.d("DevSettingsDashboard", "onBluetoothHDAudioEnabled: " + z);
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if (abstractPreferenceController instanceof AbstractBluetoothDialogPreferenceController) {
                ((AbstractBluetoothDialogPreferenceController) abstractPreferenceController).onHDAudioEnabled(z);
            }
        }
    }
}
