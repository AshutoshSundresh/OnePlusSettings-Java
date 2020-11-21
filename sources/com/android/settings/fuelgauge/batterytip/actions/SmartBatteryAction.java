package com.android.settings.fuelgauge.batterytip.actions;

import androidx.fragment.app.Fragment;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.fuelgauge.SmartBatterySettings;
import com.android.settingslib.core.instrumentation.Instrumentable;

public class SmartBatteryAction extends BatteryTipAction {
    private Fragment mFragment;
    private SettingsActivity mSettingsActivity;

    public SmartBatteryAction(SettingsActivity settingsActivity, Fragment fragment) {
        super(settingsActivity.getApplicationContext());
        this.mSettingsActivity = settingsActivity;
        this.mFragment = fragment;
    }

    @Override // com.android.settings.fuelgauge.batterytip.actions.BatteryTipAction
    public void handlePositiveAction(int i) {
        this.mMetricsFeatureProvider.action(this.mContext, 1364, i);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mSettingsActivity);
        Fragment fragment = this.mFragment;
        subSettingLauncher.setSourceMetricsCategory(fragment instanceof Instrumentable ? ((Instrumentable) fragment).getMetricsCategory() : 0);
        subSettingLauncher.setDestination(SmartBatterySettings.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.op_smart_battery_manager_title);
        subSettingLauncher.launch();
    }
}
