package com.android.settings.fuelgauge.batterytip.actions;

import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.fuelgauge.RestrictedAppDetails;
import com.android.settings.fuelgauge.batterytip.AppInfo;
import com.android.settings.fuelgauge.batterytip.BatteryDatabaseManager;
import com.android.settings.fuelgauge.batterytip.tips.RestrictAppTip;
import com.android.settingslib.utils.ThreadUtils;
import java.util.List;

public class OpenRestrictAppFragmentAction extends BatteryTipAction {
    BatteryDatabaseManager mBatteryDatabaseManager = BatteryDatabaseManager.getInstance(this.mContext);
    private final InstrumentedPreferenceFragment mFragment;
    private final RestrictAppTip mRestrictAppTip;

    public OpenRestrictAppFragmentAction(InstrumentedPreferenceFragment instrumentedPreferenceFragment, RestrictAppTip restrictAppTip) {
        super(instrumentedPreferenceFragment.getContext());
        this.mFragment = instrumentedPreferenceFragment;
        this.mRestrictAppTip = restrictAppTip;
    }

    @Override // com.android.settings.fuelgauge.batterytip.actions.BatteryTipAction
    public void handlePositiveAction(int i) {
        this.mMetricsFeatureProvider.action(this.mContext, 1361, i);
        List<AppInfo> restrictAppList = this.mRestrictAppTip.getRestrictAppList();
        RestrictedAppDetails.startRestrictedAppDetails(this.mFragment, restrictAppList);
        ThreadUtils.postOnBackgroundThread(new Runnable(restrictAppList) {
            /* class com.android.settings.fuelgauge.batterytip.actions.$$Lambda$OpenRestrictAppFragmentAction$EtKh55lPJMI0rxkM0QFArF_zK8E */
            public final /* synthetic */ List f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                OpenRestrictAppFragmentAction.this.lambda$handlePositiveAction$0$OpenRestrictAppFragmentAction(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handlePositiveAction$0 */
    public /* synthetic */ void lambda$handlePositiveAction$0$OpenRestrictAppFragmentAction(List list) {
        this.mBatteryDatabaseManager.updateAnomalies(list, 1);
    }
}
