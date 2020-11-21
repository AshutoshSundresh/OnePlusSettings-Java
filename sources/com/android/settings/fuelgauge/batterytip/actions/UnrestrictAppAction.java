package com.android.settings.fuelgauge.batterytip.actions;

import android.content.Context;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.fuelgauge.batterytip.AppInfo;
import com.android.settings.fuelgauge.batterytip.tips.UnrestrictAppTip;

public class UnrestrictAppAction extends BatteryTipAction {
    BatteryUtils mBatteryUtils;
    private UnrestrictAppTip mUnRestrictAppTip;

    public UnrestrictAppAction(Context context, UnrestrictAppTip unrestrictAppTip) {
        super(context);
        this.mUnRestrictAppTip = unrestrictAppTip;
        this.mBatteryUtils = BatteryUtils.getInstance(context);
    }

    @Override // com.android.settings.fuelgauge.batterytip.actions.BatteryTipAction
    public void handlePositiveAction(int i) {
        AppInfo unrestrictAppInfo = this.mUnRestrictAppTip.getUnrestrictAppInfo();
        this.mBatteryUtils.setForceAppStandby(unrestrictAppInfo.uid, unrestrictAppInfo.packageName, 0);
        this.mMetricsFeatureProvider.action(0, 1363, i, unrestrictAppInfo.packageName, 0);
    }
}
