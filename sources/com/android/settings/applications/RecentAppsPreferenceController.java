package com.android.settings.applications;

import android.app.Application;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.IntentFilter;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.UserHandle;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.applications.RecentAppStatsMixin;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.widget.AppEntitiesHeaderController;
import com.android.settingslib.widget.AppEntityInfo;
import com.android.settingslib.widget.LayoutPreference;
import com.oneplus.settings.ui.OPPreferenceHeaderMargin;
import java.util.List;

public class RecentAppsPreferenceController extends BasePreferenceController implements RecentAppStatsMixin.RecentAppStatsListener {
    static final String KEY_DIVIDER = "recent_apps_divider";
    AppEntitiesHeaderController mAppEntitiesController;
    private final ApplicationsState mApplicationsState = ApplicationsState.getInstance((Application) this.mContext.getApplicationContext());
    private Fragment mHost;
    private final MetricsFeatureProvider mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
    private List<UsageStats> mRecentApps;
    LayoutPreference mRecentAppsPreference;
    private PreferenceScreen mScreen;
    private final int mUserId = UserHandle.myUserId();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public RecentAppsPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void setFragment(Fragment fragment) {
        this.mHost = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mRecentAppsPreference = layoutPreference;
        AppEntitiesHeaderController newInstance = AppEntitiesHeaderController.newInstance(this.mContext, layoutPreference.findViewById(C0010R$id.op_app_entities_header));
        newInstance.setHeaderTitleRes(C0017R$string.recent_app_category_title);
        newInstance.setHeaderDetailsClickListener(new View.OnClickListener() {
            /* class com.android.settings.applications.$$Lambda$RecentAppsPreferenceController$lSTV08Ev1kIAejEfOXr6eUBel8 */

            public final void onClick(View view) {
                RecentAppsPreferenceController.this.lambda$displayPreference$0$RecentAppsPreferenceController(view);
            }
        });
        this.mAppEntitiesController = newInstance;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$RecentAppsPreferenceController(View view) {
        this.mMetricsFeatureProvider.logClickedPreference(this.mRecentAppsPreference, getMetricsCategory());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ManageApplications.class.getName());
        subSettingLauncher.setArguments(null);
        subSettingLauncher.setTitleRes(C0017R$string.application_info_label);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }

    @Override // com.android.settings.applications.RecentAppStatsMixin.RecentAppStatsListener
    public void onReloadDataCompleted(List<UsageStats> list) {
        this.mRecentApps = list;
        refreshUi();
        Context context = this.mContext;
        new InstalledAppCounter(context, -1, context.getPackageManager()) {
            /* class com.android.settings.applications.RecentAppsPreferenceController.AnonymousClass1 */

            /* access modifiers changed from: protected */
            @Override // com.android.settings.applications.AppCounter
            public void onCountComplete(int i) {
                RecentAppsPreferenceController recentAppsPreferenceController = RecentAppsPreferenceController.this;
                recentAppsPreferenceController.mAppEntitiesController.setHeaderDetails(((AbstractPreferenceController) recentAppsPreferenceController).mContext.getResources().getQuantityString(C0015R$plurals.see_all_apps_title, i, Integer.valueOf(i)));
                RecentAppsPreferenceController.this.mAppEntitiesController.apply();
            }
        }.execute(new Void[0]);
    }

    private void refreshUi() {
        if (!this.mRecentApps.isEmpty()) {
            displayRecentApps();
            this.mRecentAppsPreference.setVisible(true);
            return;
        }
        this.mRecentAppsPreference.setVisible(false);
        PreferenceScreen preferenceScreen = this.mScreen;
        if (preferenceScreen != null && !(preferenceScreen.getPreference(0) instanceof OPPreferenceHeaderMargin)) {
            OPPreferenceHeaderMargin oPPreferenceHeaderMargin = new OPPreferenceHeaderMargin(this.mContext);
            oPPreferenceHeaderMargin.setOrder(-1000);
            this.mScreen.addPreference(oPPreferenceHeaderMargin);
        }
    }

    private void displayRecentApps() {
        int i = 0;
        for (UsageStats usageStats : this.mRecentApps) {
            AppEntityInfo createAppEntity = createAppEntity(usageStats);
            if (createAppEntity != null) {
                this.mAppEntitiesController.setAppEntity(i, createAppEntity);
                i++;
            }
            if (i == 3) {
                return;
            }
        }
    }

    private AppEntityInfo createAppEntity(UsageStats usageStats) {
        String packageName = usageStats.getPackageName();
        ApplicationsState.AppEntry entry = this.mApplicationsState.getEntry(packageName, this.mUserId);
        if (entry == null) {
            return null;
        }
        AppEntityInfo.Builder builder = new AppEntityInfo.Builder();
        builder.setIcon(Utils.getBadgedIcon(this.mContext, entry.info));
        builder.setTitle(entry.label);
        builder.setSummary(StringUtil.formatRelativeTime(this.mContext, (double) (System.currentTimeMillis() - usageStats.getLastTimeUsed()), false, RelativeDateTimeFormatter.Style.SHORT));
        builder.setOnClickListener(new View.OnClickListener(packageName, entry) {
            /* class com.android.settings.applications.$$Lambda$RecentAppsPreferenceController$M3cWnW9MwZL03os_mLS9QZCJMw */
            public final /* synthetic */ String f$1;
            public final /* synthetic */ ApplicationsState.AppEntry f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(View view) {
                RecentAppsPreferenceController.this.lambda$createAppEntity$1$RecentAppsPreferenceController(this.f$1, this.f$2, view);
            }
        });
        return builder.build();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createAppEntity$1 */
    public /* synthetic */ void lambda$createAppEntity$1$RecentAppsPreferenceController(String str, ApplicationsState.AppEntry appEntry, View view) {
        this.mMetricsFeatureProvider.logClickedPreference(this.mRecentAppsPreference, getMetricsCategory());
        AppInfoBase.startAppInfoFragment(AppInfoDashboardFragment.class, C0017R$string.application_info_label, str, appEntry.info.uid, this.mHost, 1001, getMetricsCategory());
    }
}
