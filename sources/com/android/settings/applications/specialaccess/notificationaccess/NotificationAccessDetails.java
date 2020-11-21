package com.android.settings.applications.specialaccess.notificationaccess;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserManager;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.util.Slog;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.applications.AppUtils;
import java.util.Objects;

public class NotificationAccessDetails extends AppInfoBase {
    private ComponentName mComponentName;
    private boolean mCreated;
    private boolean mIsNls;
    private NotificationManager mNm;
    private PackageManager mPm;
    private CharSequence mServiceName;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1804;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.applications.AppInfoBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        String stringExtra;
        Intent intent = getIntent();
        if (!(this.mComponentName != null || intent == null || (stringExtra = intent.getStringExtra("android.provider.extra.NOTIFICATION_LISTENER_COMPONENT_NAME")) == null)) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(stringExtra);
            this.mComponentName = unflattenFromString;
            if (unflattenFromString != null) {
                getArguments().putString("package", this.mComponentName.getPackageName());
            }
        }
        super.onCreate(bundle);
        this.mNm = (NotificationManager) getContext().getSystemService(NotificationManager.class);
        this.mPm = getPackageManager();
        addPreferencesFromResource(C0019R$xml.notification_access_permission_details);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mCreated) {
            Log.w("NotifAccessDetails", "onActivityCreated: ignoring duplicate call");
            return;
        }
        this.mCreated = true;
        if (this.mPackageInfo != null) {
            loadNotificationListenerService();
            FragmentActivity activity = getActivity();
            EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, this, null);
            newInstance.setRecyclerView(getListView(), getSettingsLifecycle());
            newInstance.setIcon(IconDrawableFactory.newInstance(getContext()).getBadgedIcon(this.mPackageInfo.applicationInfo));
            newInstance.setLabel(this.mPackageInfo.applicationInfo.loadLabel(this.mPm));
            newInstance.setSummary(this.mServiceName);
            newInstance.setIsInstantApp(AppUtils.isInstant(this.mPackageInfo.applicationInfo));
            newInstance.setPackageName(this.mPackageName);
            newInstance.setUid(this.mPackageInfo.applicationInfo.uid);
            newInstance.setHasAppInfoLink(true);
            newInstance.setButtonActions(0, 0);
            getPreferenceScreen().addPreference(newInstance.done(activity, getPrefContext()));
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public boolean refreshUi() {
        getContext();
        if (this.mComponentName == null) {
            Slog.d("NotifAccessDetails", "No component name provided");
            return false;
        } else if (!this.mIsNls) {
            Slog.d("NotifAccessDetails", "Provided component name is not an NLS");
            return false;
        } else if (UserManager.get(getContext()).isManagedProfile()) {
            Slog.d("NotifAccessDetails", "NLSes aren't allowed in work profiles");
            return false;
        } else {
            updatePreference((SwitchPreference) findPreference("notification_access_switch"));
            return true;
        }
    }

    public void updatePreference(SwitchPreference switchPreference) {
        CharSequence loadLabel = this.mPackageInfo.applicationInfo.loadLabel(this.mPm);
        switchPreference.setChecked(isServiceEnabled(this.mComponentName));
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(loadLabel) {
            /* class com.android.settings.applications.specialaccess.notificationaccess.$$Lambda$NotificationAccessDetails$HX0Vw3d5CHFGJfgiZyBWSySJKY */
            public final /* synthetic */ CharSequence f$1;

            {
                this.f$1 = r2;
            }

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return NotificationAccessDetails.this.lambda$updatePreference$0$NotificationAccessDetails(this.f$1, preference, obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updatePreference$0 */
    public /* synthetic */ boolean lambda$updatePreference$0$NotificationAccessDetails(CharSequence charSequence, Preference preference, Object obj) {
        if (!((Boolean) obj).booleanValue()) {
            if (!isServiceEnabled(this.mComponentName)) {
                return true;
            }
            FriendlyWarningDialogFragment friendlyWarningDialogFragment = new FriendlyWarningDialogFragment();
            friendlyWarningDialogFragment.setServiceInfo(this.mComponentName, charSequence, this);
            friendlyWarningDialogFragment.show(getFragmentManager(), "friendlydialog");
            return false;
        } else if (isServiceEnabled(this.mComponentName)) {
            return true;
        } else {
            ScaryWarningDialogFragment scaryWarningDialogFragment = new ScaryWarningDialogFragment();
            scaryWarningDialogFragment.setServiceInfo(this.mComponentName, charSequence, this);
            scaryWarningDialogFragment.show(getFragmentManager(), "dialog");
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void logSpecialPermissionChange(boolean z, String str) {
        FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider().action(getContext(), z ? 776 : 777, str);
    }

    public void disable(ComponentName componentName) {
        logSpecialPermissionChange(true, componentName.getPackageName());
        this.mNm.setNotificationListenerAccessGranted(componentName, false);
        AsyncTask.execute(new Runnable(componentName) {
            /* class com.android.settings.applications.specialaccess.notificationaccess.$$Lambda$NotificationAccessDetails$UBGbMM0AAv6qe5KZxTHD8nsmw_U */
            public final /* synthetic */ ComponentName f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationAccessDetails.this.lambda$disable$1$NotificationAccessDetails(this.f$1);
            }
        });
        refreshUi();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$disable$1 */
    public /* synthetic */ void lambda$disable$1$NotificationAccessDetails(ComponentName componentName) {
        if (!this.mNm.isNotificationPolicyAccessGrantedForPackage(componentName.getPackageName())) {
            this.mNm.removeAutomaticZenRules(componentName.getPackageName());
        }
    }

    /* access modifiers changed from: protected */
    public void enable(ComponentName componentName) {
        logSpecialPermissionChange(true, componentName.getPackageName());
        this.mNm.setNotificationListenerAccessGranted(componentName, true);
        refreshUi();
    }

    /* access modifiers changed from: protected */
    public boolean isServiceEnabled(ComponentName componentName) {
        return this.mNm.isNotificationListenerAccessGranted(componentName);
    }

    /* access modifiers changed from: protected */
    public void loadNotificationListenerService() {
        this.mIsNls = false;
        if (this.mComponentName != null) {
            for (ResolveInfo resolveInfo : this.mPm.queryIntentServicesAsUser(new Intent("android.service.notification.NotificationListenerService").setComponent(this.mComponentName), 132, this.mUserId)) {
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                if ("android.permission.BIND_NOTIFICATION_LISTENER_SERVICE".equals(serviceInfo.permission) && Objects.equals(this.mComponentName, serviceInfo.getComponentName())) {
                    this.mIsNls = true;
                    this.mServiceName = serviceInfo.loadLabel(this.mPm);
                    return;
                }
            }
        }
    }
}
