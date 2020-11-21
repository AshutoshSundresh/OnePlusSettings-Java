package com.android.settings.notification.zen;

import android.app.Application;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.icu.text.ListFormatter;
import android.text.TextUtils;
import android.util.ArraySet;
import androidx.core.text.BidiFormatter;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class ZenModeBypassingAppsPreferenceController extends AbstractZenModePreferenceController implements PreferenceControllerMixin {
    private ApplicationsState.Session mAppSession;
    private final ApplicationsState.Callbacks mAppSessionCallbacks;
    private NotificationBackend mNotificationBackend;
    @VisibleForTesting
    protected Preference mPreference;
    private String mSummary;

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return "zen_mode_behavior_apps";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public ZenModeBypassingAppsPreferenceController(Context context, Application application, Fragment fragment, Lifecycle lifecycle) {
        this(context, application == null ? null : ApplicationsState.getInstance(application), fragment, lifecycle);
    }

    private ZenModeBypassingAppsPreferenceController(Context context, ApplicationsState applicationsState, Fragment fragment, Lifecycle lifecycle) {
        super(context, "zen_mode_behavior_apps", lifecycle);
        this.mNotificationBackend = new NotificationBackend();
        AnonymousClass1 r2 = new ApplicationsState.Callbacks() {
            /* class com.android.settings.notification.zen.ZenModeBypassingAppsPreferenceController.AnonymousClass1 */

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onAllSizesComputed() {
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLauncherInfoChanged() {
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageIconChanged() {
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRunningStateChanged(boolean z) {
                ZenModeBypassingAppsPreferenceController.this.updateAppsBypassingDndSummaryText();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageListChanged() {
                ZenModeBypassingAppsPreferenceController.this.updateAppsBypassingDndSummaryText();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
                ZenModeBypassingAppsPreferenceController.this.updateAppsBypassingDndSummaryText(arrayList);
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageSizeChanged(String str) {
                ZenModeBypassingAppsPreferenceController.this.updateAppsBypassingDndSummaryText();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLoadEntriesCompleted() {
                ZenModeBypassingAppsPreferenceController.this.updateAppsBypassingDndSummaryText();
            }
        };
        this.mAppSessionCallbacks = r2;
        if (applicationsState != null && fragment != null) {
            this.mAppSession = applicationsState.newSession(r2, fragment.getLifecycle());
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreference = preferenceScreen.findPreference("zen_mode_behavior_apps");
        updateAppsBypassingDndSummaryText();
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getSummary() {
        return this.mSummary;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateAppsBypassingDndSummaryText() {
        ApplicationsState.Session session = this.mAppSession;
        if (session != null) {
            updateAppsBypassingDndSummaryText(session.rebuild(ApplicationsState.FILTER_ALL_ENABLED, ApplicationsState.ALPHA_COMPARATOR));
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateAppsBypassingDndSummaryText(List<ApplicationsState.AppEntry> list) {
        int zenMode = getZenMode();
        if (zenMode == 2 || zenMode == 3) {
            this.mPreference.setEnabled(false);
            this.mSummary = this.mContext.getResources().getString(C0017R$string.zen_mode_bypassing_apps_subtext_none);
            return;
        }
        this.mPreference.setEnabled(true);
        if (list != null) {
            ArraySet arraySet = new ArraySet();
            for (ApplicationsState.AppEntry appEntry : list) {
                ApplicationInfo applicationInfo = appEntry.info;
                for (NotificationChannel notificationChannel : this.mNotificationBackend.getNotificationChannelsBypassingDnd(applicationInfo.packageName, applicationInfo.uid).getList()) {
                    if (TextUtils.isEmpty(notificationChannel.getConversationId()) || notificationChannel.isDemoted()) {
                        arraySet.add(BidiFormatter.getInstance().unicodeWrap(appEntry.label));
                    }
                }
            }
            int size = arraySet.size();
            if (size == 0) {
                this.mSummary = this.mContext.getResources().getString(C0017R$string.zen_mode_bypassing_apps_subtext_none);
                refreshSummary(this.mPreference);
                return;
            }
            ArrayList arrayList = new ArrayList();
            if (size <= 2) {
                arrayList.addAll(arraySet);
            } else {
                String[] strArr = (String[]) arraySet.toArray(new String[size]);
                arrayList.add(strArr[0]);
                arrayList.add(strArr[1]);
                arrayList.add(this.mContext.getResources().getString(C0017R$string.zen_mode_apps_bypassing_list_count, Integer.valueOf(size - 2)));
            }
            this.mSummary = this.mContext.getResources().getQuantityString(C0015R$plurals.zen_mode_bypassing_apps_subtext, size, ListFormatter.getInstance().format(arrayList));
            refreshSummary(this.mPreference);
        }
    }
}
