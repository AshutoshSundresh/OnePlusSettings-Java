package com.android.settings.datausage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.datausage.AppStateDataUsageBridge;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.widget.AppSwitchPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreferenceHelper;
import com.android.settingslib.applications.ApplicationsState;

public class UnrestrictedDataAccessPreference extends AppSwitchPreference implements DataSaverBackend.Listener {
    private final ApplicationsState mApplicationsState;
    private final DataSaverBackend mDataSaverBackend;
    private final AppStateDataUsageBridge.DataUsageState mDataUsageState;
    private final ApplicationsState.AppEntry mEntry;
    private final RestrictedPreferenceHelper mHelper;
    private final DashboardFragment mParentFragment;

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
    }

    public UnrestrictedDataAccessPreference(Context context, ApplicationsState.AppEntry appEntry, ApplicationsState applicationsState, DataSaverBackend dataSaverBackend, DashboardFragment dashboardFragment) {
        super(context);
        setWidgetLayoutResource(C0012R$layout.restricted_switch_widget);
        this.mHelper = new RestrictedPreferenceHelper(context, this, null);
        this.mEntry = appEntry;
        this.mDataUsageState = (AppStateDataUsageBridge.DataUsageState) appEntry.extraInfo;
        appEntry.ensureLabel(context);
        this.mApplicationsState = applicationsState;
        this.mDataSaverBackend = dataSaverBackend;
        this.mParentFragment = dashboardFragment;
        ApplicationInfo applicationInfo = appEntry.info;
        setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfMeteredDataRestricted(context, applicationInfo.packageName, UserHandle.getUserId(applicationInfo.uid)));
        updateState();
        setKey(generateKey(this.mEntry));
        Drawable drawable = this.mEntry.icon;
        if (drawable != null) {
            setIcon(drawable);
        }
    }

    static String generateKey(ApplicationsState.AppEntry appEntry) {
        return appEntry.info.packageName + "|" + appEntry.info.uid;
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mDataSaverBackend.addListener(this);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        this.mDataSaverBackend.remListener(this);
        super.onDetached();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.SwitchPreference, androidx.preference.TwoStatePreference, androidx.preference.Preference
    public void onClick() {
        if (this.mDataUsageState.isDataSaverBlacklisted) {
            AppInfoDashboardFragment.startAppInfoFragment(AppDataUsage.class, C0017R$string.data_usage_app_summary_title, null, this.mParentFragment, this.mEntry);
        } else {
            super.onClick();
        }
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (!this.mHelper.performClick()) {
            super.performClick();
        }
    }

    @Override // com.android.settings.widget.AppSwitchPreference, androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        if (this.mEntry.icon == null) {
            preferenceViewHolder.itemView.post(new Runnable() {
                /* class com.android.settings.datausage.UnrestrictedDataAccessPreference.AnonymousClass1 */

                public void run() {
                    UnrestrictedDataAccessPreference.this.mApplicationsState.ensureIcon(UnrestrictedDataAccessPreference.this.mEntry);
                    UnrestrictedDataAccessPreference unrestrictedDataAccessPreference = UnrestrictedDataAccessPreference.this;
                    unrestrictedDataAccessPreference.setIcon(unrestrictedDataAccessPreference.mEntry.icon);
                }
            });
        }
        boolean isDisabledByAdmin = isDisabledByAdmin();
        View findViewById = preferenceViewHolder.findViewById(16908312);
        int i = 0;
        if (isDisabledByAdmin) {
            findViewById.setVisibility(0);
        } else {
            AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
            findViewById.setVisibility((dataUsageState == null || !dataUsageState.isDataSaverBlacklisted) ? 0 : 4);
        }
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.findViewById(C0010R$id.restricted_icon).setVisibility(isDisabledByAdmin ? 0 : 8);
        View findViewById2 = preferenceViewHolder.findViewById(16908352);
        if (isDisabledByAdmin) {
            i = 8;
        }
        findViewById2.setVisibility(i);
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onWhitelistStatusChanged(int i, boolean z) {
        AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
        if (dataUsageState != null && this.mEntry.info.uid == i) {
            dataUsageState.isDataSaverWhitelisted = z;
            updateState();
        }
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onBlacklistStatusChanged(int i, boolean z) {
        AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
        if (dataUsageState != null && this.mEntry.info.uid == i) {
            dataUsageState.isDataSaverBlacklisted = z;
            updateState();
        }
    }

    public AppStateDataUsageBridge.DataUsageState getDataUsageState() {
        return this.mDataUsageState;
    }

    public ApplicationsState.AppEntry getEntry() {
        return this.mEntry;
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mHelper.setDisabledByAdmin(enforcedAdmin);
    }

    public void updateState() {
        setTitle(this.mEntry.label);
        AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
        if (dataUsageState != null) {
            setChecked(dataUsageState.isDataSaverWhitelisted);
            if (isDisabledByAdmin()) {
                setSummary(C0017R$string.disabled_by_admin);
            } else if (this.mDataUsageState.isDataSaverBlacklisted) {
                setSummary(C0017R$string.restrict_background_blacklisted);
            } else {
                setSummary("");
            }
        }
        notifyChanged();
    }
}
