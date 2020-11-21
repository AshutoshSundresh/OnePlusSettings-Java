package com.android.settings.fuelgauge;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.BatteryStats;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;
import com.android.internal.util.ArrayUtils;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.ui.RadioButtonPreference;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.fuelgauge.PowerWhitelistBackend;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.widget.LayoutPreference;
import com.oneplus.settings.backgroundoptimize.AppBgOptimizeBridge;
import com.oneplus.settings.backgroundoptimize.BgOActivityManager;
import com.oneplus.settings.utils.OPApplicationUtils;

public class AdvancedPowerUsageDetail extends DashboardFragment implements RadioButtonPreference.OnClickListener {
    private static PowerWhitelistBackend mPowerWhitelistBackend;
    ApplicationsState.AppEntry mAppEntry;
    Preference mBackgroundPreference;
    BatteryUtils mBatteryUtils;
    Preference mForegroundPreference;
    LayoutPreference mHeaderPreference;
    private RadioButtonPreference mNoOptimze;
    private RadioButtonPreference mOptimze;
    private String mPackageName;
    private RadioButtonPreference mSmartOptimze;
    ApplicationsState mState;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AdvancedPowerDetail";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 53;
    }

    static void startBatteryDetailPage(Activity activity, BatteryUtils batteryUtils, InstrumentedPreferenceFragment instrumentedPreferenceFragment, BatteryStatsHelper batteryStatsHelper, int i, BatteryEntry batteryEntry, String str) {
        long j;
        batteryStatsHelper.getStats();
        Bundle bundle = new Bundle();
        BatterySipper batterySipper = batteryEntry.sipper;
        BatteryStats.Uid uid = batterySipper.uidObj;
        boolean z = batterySipper.drainType == BatterySipper.DrainType.APP;
        if (z) {
            j = batteryUtils.getProcessTimeMs(1, uid, i);
        } else {
            j = batterySipper.usageTimeMs;
        }
        long processTimeMs = z ? batteryUtils.getProcessTimeMs(2, uid, i) : 0;
        if (ArrayUtils.isEmpty(batterySipper.mPackages)) {
            bundle.putString("extra_label", batteryEntry.getLabel());
            bundle.putInt("extra_icon_id", batteryEntry.iconId);
            bundle.putString("extra_package_name", null);
        } else {
            String str2 = batteryEntry.defaultPackageName;
            if (str2 == null) {
                str2 = batterySipper.mPackages[0];
            }
            bundle.putString("extra_package_name", str2);
        }
        bundle.putInt("extra_uid", batterySipper.getUid());
        bundle.putLong("extra_background_time", processTimeMs);
        bundle.putLong("extra_foreground_time", j);
        bundle.putString("extra_power_usage_percent", str);
        bundle.putInt("extra_power_usage_amount", (int) batterySipper.totalPowerMah);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(activity);
        subSettingLauncher.setDestination(AdvancedPowerUsageDetail.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.battery_details_title);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(instrumentedPreferenceFragment.getMetricsCategory());
        subSettingLauncher.setUserHandle(new UserHandle(getUserIdToLaunchAdvancePowerUsageDetail(batterySipper)));
        subSettingLauncher.launch();
    }

    private static int getUserIdToLaunchAdvancePowerUsageDetail(BatterySipper batterySipper) {
        if (batterySipper.drainType == BatterySipper.DrainType.USER) {
            return ActivityManager.getCurrentUser();
        }
        return UserHandle.getUserId(batterySipper.getUid());
    }

    public static void startBatteryDetailPage(Activity activity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, BatteryStatsHelper batteryStatsHelper, int i, BatteryEntry batteryEntry, String str) {
        startBatteryDetailPage(activity, BatteryUtils.getInstance(activity), instrumentedPreferenceFragment, batteryStatsHelper, i, batteryEntry, str);
    }

    public static void startBatteryDetailPage(Activity activity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, String str) {
        Bundle bundle = new Bundle(3);
        PackageManager packageManager = activity.getPackageManager();
        bundle.putString("extra_package_name", str);
        bundle.putString("extra_power_usage_percent", Utils.formatPercentage(0));
        try {
            bundle.putInt("extra_uid", packageManager.getPackageUid(str, 0));
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("AdvancedPowerDetail", "Cannot find package: " + str, e);
        }
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(activity);
        subSettingLauncher.setDestination(AdvancedPowerUsageDetail.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.battery_details_title);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(instrumentedPreferenceFragment.getMetricsCategory());
        subSettingLauncher.launch();
    }

    public static void startBatteryDetailPage(Activity activity, BatteryStatsHelper batteryStatsHelper, int i, BatteryEntry batteryEntry, String str) {
        startBatteryDetailPage(activity, BatteryUtils.getInstance(activity), batteryStatsHelper, i, batteryEntry, str);
    }

    public static void startBatteryDetailPage(Activity activity, String str) {
        Bundle bundle = new Bundle(3);
        PackageManager packageManager = activity.getPackageManager();
        bundle.putString("extra_package_name", str);
        bundle.putString("extra_power_usage_percent", Utils.formatPercentage(0));
        try {
            bundle.putInt("extra_uid", packageManager.getPackageUid(str, 0));
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("AdvancedPowerDetail", "Cannot find package: " + str, e);
        }
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(activity);
        subSettingLauncher.setDestination(AdvancedPowerUsageDetail.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.battery_details_title);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(9999);
        subSettingLauncher.launch();
    }

    static void startBatteryDetailPage(Activity activity, BatteryUtils batteryUtils, BatteryStatsHelper batteryStatsHelper, int i, BatteryEntry batteryEntry, String str) {
        long j;
        batteryStatsHelper.getStats();
        Bundle bundle = new Bundle();
        BatterySipper batterySipper = batteryEntry.sipper;
        BatteryStats.Uid uid = batterySipper.uidObj;
        boolean z = batterySipper.drainType == BatterySipper.DrainType.APP;
        if (z) {
            j = batteryUtils.getProcessTimeMs(1, uid, i);
        } else {
            j = batterySipper.usageTimeMs;
        }
        long processTimeMs = z ? batteryUtils.getProcessTimeMs(2, uid, i) : 0;
        if (ArrayUtils.isEmpty(batterySipper.mPackages)) {
            bundle.putString("extra_label", batteryEntry.getLabel());
            bundle.putInt("extra_icon_id", batteryEntry.iconId);
            bundle.putString("extra_package_name", null);
        } else {
            String str2 = batteryEntry.defaultPackageName;
            if (str2 == null) {
                str2 = batterySipper.mPackages[0];
            }
            bundle.putString("extra_package_name", str2);
        }
        bundle.putInt("extra_uid", batterySipper.getUid());
        bundle.putLong("extra_background_time", processTimeMs);
        bundle.putLong("extra_foreground_time", j);
        bundle.putString("extra_power_usage_percent", str);
        bundle.putInt("extra_power_usage_amount", (int) batterySipper.totalPowerMah);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(activity);
        subSettingLauncher.setDestination(AdvancedPowerUsageDetail.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.battery_details_title);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setUserHandle(new UserHandle(getUserIdToLaunchAdvancePowerUsageDetail(batterySipper)));
        subSettingLauncher.setSourceMetricsCategory(9999);
        subSettingLauncher.launch();
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mState = ApplicationsState.getInstance(getActivity().getApplication());
        this.mBatteryUtils = BatteryUtils.getInstance(getContext());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mPackageName = getArguments().getString("extra_package_name");
        this.mForegroundPreference = findPreference("app_usage_foreground");
        this.mBackgroundPreference = findPreference("app_usage_background");
        this.mHeaderPreference = (LayoutPreference) findPreference("header_view");
        this.mSmartOptimze = (RadioButtonPreference) findPreference("smart_optimze");
        this.mOptimze = (RadioButtonPreference) findPreference("optimze");
        try {
            ApplicationInfo applicationInfo = getContext().getPackageManager().getApplicationInfo(this.mPackageName, 4194304);
            if (OPApplicationUtils.isSystemAndNonUpdate(applicationInfo) || OPApplicationUtils.isSystemUpdateAndOneplus(applicationInfo)) {
                this.mOptimze.setVisible(false);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        this.mNoOptimze = (RadioButtonPreference) findPreference("no_optimze");
        this.mSmartOptimze.setOnClickListener(this);
        this.mOptimze.setOnClickListener(this);
        this.mNoOptimze.setOnClickListener(this);
        mPowerWhitelistBackend = PowerWhitelistBackend.getInstance(getContext());
        if (TextUtils.isEmpty(this.mPackageName) || !AppBgOptimizeBridge.needShown(this.mPackageName, mPowerWhitelistBackend)) {
            this.mSmartOptimze.setVisible(false);
            this.mOptimze.setVisible(false);
            this.mNoOptimze.setVisible(false);
        }
        if (!TextUtils.isEmpty(this.mPackageName)) {
            this.mAppEntry = this.mState.getEntry(this.mPackageName, UserHandle.myUserId());
        }
    }

    private void updateUI() {
        int appControlMode = BgOActivityManager.getInstance(getContext()).getAppControlMode(this.mPackageName, 0);
        if (appControlMode == 0) {
            this.mSmartOptimze.setChecked(true);
            this.mOptimze.setChecked(false);
            this.mNoOptimze.setChecked(false);
        }
        if (appControlMode == 1) {
            this.mSmartOptimze.setChecked(false);
            this.mOptimze.setChecked(false);
            this.mNoOptimze.setChecked(true);
        }
        if (appControlMode == 2) {
            this.mSmartOptimze.setChecked(false);
            this.mOptimze.setChecked(true);
            this.mNoOptimze.setChecked(false);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateUI();
        initHeader();
        initPreference();
    }

    /* access modifiers changed from: package-private */
    public void initHeader() {
        View findViewById = this.mHeaderPreference.findViewById(C0010R$id.entity_header);
        FragmentActivity activity = getActivity();
        Bundle arguments = getArguments();
        EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, this, findViewById);
        newInstance.setRecyclerView(getListView(), getSettingsLifecycle());
        newInstance.setButtonActions(0, 0);
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        if (appEntry == null) {
            newInstance.setLabel(arguments.getString("extra_label"));
            if (arguments.getInt("extra_icon_id", 0) == 0) {
                newInstance.setIcon(activity.getPackageManager().getDefaultActivityIcon());
            } else {
                newInstance.setIcon(activity.getDrawable(arguments.getInt("extra_icon_id")));
            }
        } else {
            this.mState.ensureIcon(appEntry);
            newInstance.setLabel(this.mAppEntry);
            newInstance.setIcon(this.mAppEntry);
            AppUtils.isInstant(this.mAppEntry.info);
            newInstance.setIsInstantApp(AppUtils.isInstant(this.mAppEntry.info));
        }
        newInstance.done((Activity) activity, true);
    }

    /* access modifiers changed from: package-private */
    public void initPreference() {
        Bundle arguments = getArguments();
        Context context = getContext();
        long j = arguments.getLong("extra_foreground_time");
        long j2 = arguments.getLong("extra_background_time");
        this.mForegroundPreference.setSummary(TextUtils.expandTemplate(getText(C0017R$string.battery_used_for), StringUtil.formatElapsedTime(context, (double) j, false)));
        this.mBackgroundPreference.setSummary(TextUtils.expandTemplate(getText(C0017R$string.battery_active_for), StringUtil.formatElapsedTime(context, (double) j2, false)));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.power_usage_detail;
    }

    @Override // com.android.settings.ui.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        RadioButtonPreference radioButtonPreference2 = this.mSmartOptimze;
        if (radioButtonPreference == radioButtonPreference2) {
            radioButtonPreference2.setChecked(true);
            this.mOptimze.setChecked(false);
            this.mNoOptimze.setChecked(false);
            BgOActivityManager.getInstance(getContext()).setAppControlMode(this.mPackageName, 0, 0);
        } else if (radioButtonPreference == this.mOptimze) {
            radioButtonPreference2.setChecked(false);
            this.mOptimze.setChecked(true);
            this.mNoOptimze.setChecked(false);
            BgOActivityManager.getInstance(getContext()).setAppControlMode(this.mPackageName, 0, 2);
        } else if (radioButtonPreference == this.mNoOptimze) {
            radioButtonPreference2.setChecked(false);
            this.mOptimze.setChecked(false);
            this.mNoOptimze.setChecked(true);
            BgOActivityManager.getInstance(getContext()).setAppControlMode(this.mPackageName, 0, 1);
        }
    }
}
