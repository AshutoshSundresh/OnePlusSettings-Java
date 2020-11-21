package com.oneplus.settings.backgroundoptimize.funcswitch;

import android.app.AppOpsManager;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.fuelgauge.batterytip.BatteryTipUtils;
import com.android.settings.overlay.FeatureFactory;
import com.oneplus.settings.utils.OPUtils;

public class BgOptimizeAdvanceSettings extends DashboardFragment {
    private AppOpsManager mAppOpsManager;
    private PowerUsageFeatureProvider mPowerUsageFeatureProvider;
    private SwitchPreference mSleepStandBySwitchPreference;
    private SwitchPreference mSmartatteryBySwitchPreference;
    private UserManager mUserManager;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BgOptimizeAdvanceSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initData();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        boolean z = true;
        if (Settings.Global.getInt(getContext().getContentResolver(), this.mPowerUsageFeatureProvider.isSmartBatterySupported() ? "adaptive_battery_management_enabled" : "app_auto_restriction_enabled", 1) != 1) {
            z = false;
        }
        SwitchPreference switchPreference = this.mSmartatteryBySwitchPreference;
        if (switchPreference != null) {
            switchPreference.setChecked(z);
        }
    }

    private void initData() {
        this.mPowerUsageFeatureProvider = FeatureFactory.getFactory(getContext()).getPowerUsageFeatureProvider(getContext());
        this.mAppOpsManager = (AppOpsManager) getContext().getSystemService(AppOpsManager.class);
        UserManager userManager = (UserManager) getContext().getSystemService(UserManager.class);
        this.mUserManager = userManager;
        int size = BatteryTipUtils.getRestrictedAppsList(this.mAppOpsManager, userManager).size();
        SwitchPreference switchPreference = (SwitchPreference) findPreference("smart_battery_manager");
        this.mSmartatteryBySwitchPreference = switchPreference;
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(size) {
                /* class com.oneplus.settings.backgroundoptimize.funcswitch.$$Lambda$BgOptimizeAdvanceSettings$m6K78XVJyX1S38ETA3FWrGbZqv8 */
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return BgOptimizeAdvanceSettings.this.lambda$initData$0$BgOptimizeAdvanceSettings(this.f$1, preference, obj);
                }
            });
        }
        this.mSleepStandBySwitchPreference = (SwitchPreference) findPreference("sleep_standby");
        boolean z = false;
        int intForUser = Settings.System.getIntForUser(getContentResolver(), "optimal_power_save_mode_enabled", 0, -2);
        if (!OPUtils.isSupportSleepStandby()) {
            this.mSleepStandBySwitchPreference.setVisible(false);
        }
        SwitchPreference switchPreference2 = this.mSleepStandBySwitchPreference;
        if (switchPreference2 != null) {
            if (intForUser > 0) {
                z = true;
            }
            switchPreference2.setChecked(z);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initData$0 */
    public /* synthetic */ boolean lambda$initData$0$BgOptimizeAdvanceSettings(int i, Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Settings.Global.putInt(getContentResolver(), this.mPowerUsageFeatureProvider.isSmartBatterySupported() ? "adaptive_battery_management_enabled" : "app_auto_restriction_enabled", booleanValue ? 1 : 0);
        if (i > 0) {
            preference.setSummary(getContext().getResources().getQuantityString(C0015R$plurals.battery_manager_app_restricted, i, Integer.valueOf(i)));
        } else if (booleanValue) {
            preference.setSummary(C0017R$string.battery_manager_on);
            if (preference instanceof SwitchPreference) {
                ((SwitchPreference) preference).setChecked(true);
            }
        } else {
            preference.setSummary(C0017R$string.battery_manager_off);
            if (preference instanceof SwitchPreference) {
                ((SwitchPreference) preference).setChecked(false);
            }
        }
        return true;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener, com.android.settings.dashboard.DashboardFragment
    public boolean onPreferenceTreeClick(Preference preference) {
        if (!"sleep_standby".equals(preference.getKey())) {
            return super.onPreferenceTreeClick(preference);
        }
        Settings.System.putIntForUser(getContentResolver(), "optimal_power_save_mode_enabled", this.mSleepStandBySwitchPreference.isChecked() ? 1 : 0, -2);
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.bg_optimize_prefs;
    }
}
