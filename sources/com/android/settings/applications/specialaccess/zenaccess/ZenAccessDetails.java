package com.android.settings.applications.specialaccess.zenaccess;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.specialaccess.zenaccess.ZenAccessSettingObserverMixin;

public class ZenAccessDetails extends AppInfoWithHeader implements ZenAccessSettingObserverMixin.Listener {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1692;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.applications.AppInfoBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.zen_access_permission_details);
        getSettingsLifecycle().addObserver(new ZenAccessSettingObserverMixin(getContext(), this));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public boolean refreshUi() {
        Context context = getContext();
        if (!ZenAccessController.isSupported((ActivityManager) context.getSystemService(ActivityManager.class)) || !ZenAccessController.getPackagesRequestingNotificationPolicyAccess().contains(this.mPackageName)) {
            return false;
        }
        updatePreference(context, (SwitchPreference) findPreference("zen_access_switch"));
        return true;
    }

    public void updatePreference(Context context, SwitchPreference switchPreference) {
        CharSequence loadLabel = this.mPackageInfo.applicationInfo.loadLabel(this.mPm);
        if (ZenAccessController.getAutoApprovedPackages(context).contains(this.mPackageName)) {
            switchPreference.setEnabled(false);
            switchPreference.setSummary(getString(C0017R$string.zen_access_disabled_package_warning));
            return;
        }
        switchPreference.setChecked(ZenAccessController.hasAccess(context, this.mPackageName));
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(loadLabel) {
            /* class com.android.settings.applications.specialaccess.zenaccess.$$Lambda$ZenAccessDetails$rKJyvX6IObyOz60FeyFja12ZgrM */
            public final /* synthetic */ CharSequence f$1;

            {
                this.f$1 = r2;
            }

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return ZenAccessDetails.this.lambda$updatePreference$0$ZenAccessDetails(this.f$1, preference, obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updatePreference$0 */
    public /* synthetic */ boolean lambda$updatePreference$0$ZenAccessDetails(CharSequence charSequence, Preference preference, Object obj) {
        if (((Boolean) obj).booleanValue()) {
            ScaryWarningDialogFragment scaryWarningDialogFragment = new ScaryWarningDialogFragment();
            scaryWarningDialogFragment.setPkgInfo(this.mPackageName, charSequence);
            scaryWarningDialogFragment.show(getFragmentManager(), "dialog");
            return false;
        }
        FriendlyWarningDialogFragment friendlyWarningDialogFragment = new FriendlyWarningDialogFragment();
        friendlyWarningDialogFragment.setPkgInfo(this.mPackageName, charSequence);
        friendlyWarningDialogFragment.show(getFragmentManager(), "dialog");
        return false;
    }

    @Override // com.android.settings.applications.specialaccess.zenaccess.ZenAccessSettingObserverMixin.Listener
    public void onZenAccessPolicyChanged() {
        refreshUi();
    }
}
