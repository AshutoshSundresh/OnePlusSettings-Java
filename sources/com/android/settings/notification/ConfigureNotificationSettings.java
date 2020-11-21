package com.android.settings.notification;

import android.app.Application;
import android.app.usage.IUsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0019R$xml;
import com.android.settings.RingtonePreference;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.ringtone.OPRingtonePickerActivity;
import java.util.ArrayList;
import java.util.List;

public class ConfigureNotificationSettings extends DashboardFragment implements OnActivityResultListener {
    static final String KEY_SWIPE_DOWN = "gesture_swipe_down_fingerprint_notifications";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.configure_notification_settings) {
        /* class com.android.settings.notification.ConfigureNotificationSettings.AnonymousClass2 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ConfigureNotificationSettings.buildPreferenceControllers(context, null, null);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            nonIndexableKeys.add(ConfigureNotificationSettings.KEY_SWIPE_DOWN);
            return nonIndexableKeys;
        }
    };
    private RingtonePreference mRequestPreference;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ConfigNotiSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 337;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public boolean isParalleledControllers() {
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.configure_notification_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        FragmentActivity activity = getActivity();
        return buildPreferenceControllers(context, activity != null ? activity.getApplication() : null, this);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Application application, Fragment fragment) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new RecentNotifyingAppsPreferenceController(context, new NotificationBackend(), IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats")), (UserManager) context.getSystemService(UserManager.class), application, fragment));
        arrayList.add(new ShowOnLockScreenNotificationPreferenceController(context, "lock_screen_notifications"));
        arrayList.add(new NotificationRingtonePreferenceController(context) {
            /* class com.android.settings.notification.ConfigureNotificationSettings.AnonymousClass1 */

            @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.NotificationRingtonePreferenceController
            public String getPreferenceKey() {
                return "notification_default_ringtone";
            }
        });
        return arrayList;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Bundle arguments = getArguments();
        if (preferenceScreen != null && arguments != null && !TextUtils.isEmpty(arguments.getString(":settings:fragment_args_key"))) {
            PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("configure_notifications_advanced");
            preferenceCategory.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
            scrollToPreference(preferenceCategory);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener, com.android.settings.dashboard.DashboardFragment
    public boolean onPreferenceTreeClick(Preference preference) {
        if (!(preference instanceof RingtonePreference)) {
            return super.onPreferenceTreeClick(preference);
        }
        writePreferenceClickMetric(preference);
        RingtonePreference ringtonePreference = (RingtonePreference) preference;
        this.mRequestPreference = ringtonePreference;
        ringtonePreference.onPrepareRingtonePickerIntent(new Intent(getActivity(), OPRingtonePickerActivity.class));
        return true;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            ringtonePreference.onActivityResult(i, i2, intent);
            this.mRequestPreference = null;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            bundle.putString("selected_preference", ringtonePreference.getKey());
        }
    }
}
