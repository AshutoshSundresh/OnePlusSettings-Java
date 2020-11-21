package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.AppNotificationSettings;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.ApplicationsState;

public class AppNotificationPreferenceController extends AppInfoPreferenceControllerBase {
    private final NotificationBackend mBackend = new NotificationBackend();
    private String mChannelId = null;

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppNotificationPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public void setParentFragment(AppInfoDashboardFragment appInfoDashboardFragment) {
        super.setParentFragment(appInfoDashboardFragment);
        if (appInfoDashboardFragment != null && appInfoDashboardFragment.getActivity() != null && appInfoDashboardFragment.getActivity().getIntent() != null) {
            this.mChannelId = appInfoDashboardFragment.getActivity().getIntent().getStringExtra(":settings:fragment_args_key");
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setSummary(getNotificationSummary(this.mParent.getAppEntry(), this.mContext, this.mBackend));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return AppNotificationSettings.class;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public Bundle getArguments() {
        if (this.mChannelId == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putString(":settings:fragment_args_key", this.mChannelId);
        return bundle;
    }

    private CharSequence getNotificationSummary(ApplicationsState.AppEntry appEntry, Context context, NotificationBackend notificationBackend) {
        return getNotificationSummary(notificationBackend.loadAppRow(context, context.getPackageManager(), appEntry.info), context);
    }

    public static CharSequence getNotificationSummary(NotificationBackend.AppRow appRow, Context context) {
        if (appRow == null) {
            return "";
        }
        if (appRow.banned) {
            return context.getText(C0017R$string.notifications_disabled);
        }
        int i = appRow.channelCount;
        if (i == 0) {
            return NotificationBackend.getSentSummary(context, appRow.sentByApp, false);
        }
        int i2 = appRow.blockedChannelCount;
        if (i == i2) {
            return context.getText(C0017R$string.notifications_disabled);
        }
        if (i2 == 0) {
            return NotificationBackend.getSentSummary(context, appRow.sentByApp, false);
        }
        int i3 = C0017R$string.notifications_enabled_with_info;
        Resources resources = context.getResources();
        int i4 = C0015R$plurals.notifications_categories_off;
        int i5 = appRow.blockedChannelCount;
        return context.getString(i3, NotificationBackend.getSentSummary(context, appRow.sentByApp, false), resources.getQuantityString(i4, i5, Integer.valueOf(i5)));
    }
}
