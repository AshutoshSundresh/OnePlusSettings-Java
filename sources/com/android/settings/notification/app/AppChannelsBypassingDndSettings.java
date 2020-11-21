package com.android.settings.notification.app;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.C0019R$xml;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class AppChannelsBypassingDndSettings extends NotificationSettings {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AppChannelsBypassingDndSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1840;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.notification.app.NotificationSettings, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null) {
            Log.w("AppChannelsBypassingDndSettings", "Missing package or uid or packageinfo");
            finish();
            return;
        }
        for (NotificationPreferenceController notificationPreferenceController : ((NotificationSettings) this).mControllers) {
            notificationPreferenceController.onResume(this.mAppRow, null, null, null, null, this.mSuspendedAppsAdmin);
            notificationPreferenceController.displayPreference(getPreferenceScreen());
        }
        updatePreferenceStates();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.app_channels_bypassing_dnd_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((NotificationSettings) this).mControllers = arrayList;
        arrayList.add(new HeaderPreferenceController(context, this));
        ((NotificationSettings) this).mControllers.add(new AppChannelsBypassingDndPreferenceController(context, new NotificationBackend()));
        return new ArrayList(((NotificationSettings) this).mControllers);
    }
}
