package com.android.settings.notification;

import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.applications.specialaccess.notificationaccess.NotificationAccessDetails;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.utils.ManagedServiceSettings;
import com.android.settings.widget.EmptyTextSettings;
import com.android.settingslib.applications.ServiceListing;
import java.util.List;

public class NotificationAccessSettings extends EmptyTextSettings {
    private static final ManagedServiceSettings.Config CONFIG;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.notification_access_settings);
    protected Context mContext;
    private DevicePolicyManager mDpm;
    private IconDrawableFactory mIconDrawableFactory;
    private NotificationManager mNm;
    private PackageManager mPm;
    private ServiceListing mServiceListing;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 179;
    }

    static {
        ManagedServiceSettings.Config.Builder builder = new ManagedServiceSettings.Config.Builder();
        builder.setTag("NotifAccessSettings");
        builder.setSetting("enabled_notification_listeners");
        builder.setIntentAction("android.service.notification.NotificationListenerService");
        builder.setPermission("android.permission.BIND_NOTIFICATION_LISTENER_SERVICE");
        builder.setNoun("notification listener");
        builder.setWarningDialogTitle(C0017R$string.notification_listener_security_warning_title);
        builder.setWarningDialogSummary(C0017R$string.notification_listener_security_warning_summary);
        builder.setEmptyText(C0017R$string.no_notification_listeners);
        CONFIG = builder.build();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mPm = activity.getPackageManager();
        this.mDpm = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(this.mContext);
        ServiceListing.Builder builder = new ServiceListing.Builder(this.mContext);
        builder.setPermission(CONFIG.permission);
        builder.setIntentAction(CONFIG.intentAction);
        builder.setNoun(CONFIG.noun);
        builder.setSetting(CONFIG.setting);
        builder.setTag(CONFIG.tag);
        ServiceListing build = builder.build();
        this.mServiceListing = build;
        build.addCallback(new ServiceListing.Callback() {
            /* class com.android.settings.notification.$$Lambda$NotificationAccessSettings$wuvC5lnf_Osc19b5bvxPsXy0jA4 */

            @Override // com.android.settingslib.applications.ServiceListing.Callback
            public final void onServicesReloaded(List list) {
                NotificationAccessSettings.this.updateList(list);
            }
        });
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this.mContext));
        if (UserManager.get(this.mContext).isManagedProfile()) {
            Toast.makeText(this.mContext, C0017R$string.notification_settings_work_profile, 0).show();
            finish();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settings.widget.EmptyTextSettings
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setEmptyText(CONFIG.emptyText);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mServiceListing.reload();
        this.mServiceListing.setListening(true);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mServiceListing.setListening(false);
    }

    /* access modifiers changed from: private */
    public void updateList(List<ServiceInfo> list) {
        int i;
        int managedProfileId = Utils.getManagedProfileId((UserManager) this.mContext.getSystemService("user"), UserHandle.myUserId());
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        list.sort(new PackageItemInfo.DisplayNameComparator(this.mPm));
        for (ServiceInfo serviceInfo : list) {
            ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
            CharSequence charSequence = null;
            try {
                charSequence = this.mPm.getApplicationInfoAsUser(serviceInfo.packageName, 0, UserHandle.myUserId()).loadLabel(this.mPm);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("NotifAccessSettings", "can't find package name", e);
            }
            Preference preference = new Preference(getPrefContext());
            preference.setTitle(charSequence);
            IconDrawableFactory iconDrawableFactory = this.mIconDrawableFactory;
            ApplicationInfo applicationInfo = serviceInfo.applicationInfo;
            preference.setIcon(iconDrawableFactory.getBadgedIcon(serviceInfo, applicationInfo, UserHandle.getUserId(applicationInfo.uid)));
            preference.setKey(componentName.flattenToString());
            if (this.mNm.isNotificationListenerAccessGranted(componentName)) {
                i = C0017R$string.app_permission_summary_allowed;
            } else {
                i = C0017R$string.app_permission_summary_not_allowed;
            }
            preference.setSummary(i);
            if (managedProfileId != -10000 && !this.mDpm.isNotificationListenerServicePermitted(serviceInfo.packageName, managedProfileId)) {
                preference.setSummary(C0017R$string.work_profile_notification_access_blocked_summary);
            }
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(componentName, serviceInfo) {
                /* class com.android.settings.notification.$$Lambda$NotificationAccessSettings$VWKihdNPOl2SzWKemgIgmu_lvno */
                public final /* synthetic */ ComponentName f$1;
                public final /* synthetic */ ServiceInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return NotificationAccessSettings.this.lambda$updateList$0$NotificationAccessSettings(this.f$1, this.f$2, preference);
                }
            });
            preference.setKey(componentName.flattenToString());
            preferenceScreen.addPreference(preference);
        }
        highlightPreferenceIfNeeded();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateList$0 */
    public /* synthetic */ boolean lambda$updateList$0$NotificationAccessSettings(ComponentName componentName, ServiceInfo serviceInfo, Preference preference) {
        Bundle bundle = new Bundle();
        bundle.putString("package", componentName.getPackageName());
        bundle.putInt("uid", serviceInfo.applicationInfo.uid);
        Bundle bundle2 = new Bundle();
        bundle2.putString("android.provider.extra.NOTIFICATION_LISTENER_COMPONENT_NAME", componentName.flattenToString());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(NotificationAccessDetails.class.getName());
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setTitleRes(C0017R$string.manage_zen_access_title);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setExtras(bundle2);
        subSettingLauncher.setUserHandle(UserHandle.getUserHandleForUid(serviceInfo.applicationInfo.uid));
        subSettingLauncher.launch();
        return true;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mNm = (NotificationManager) context.getSystemService(NotificationManager.class);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.notification_access_settings;
    }
}
