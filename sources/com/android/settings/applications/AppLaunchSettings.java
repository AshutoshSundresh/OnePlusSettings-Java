package com.android.settings.applications;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.ArraySet;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.applications.AppUtils;

public class AppLaunchSettings extends AppInfoWithHeader implements View.OnClickListener, Preference.OnPreferenceChangeListener {
    private AppDomainsPreference mAppDomainUrls;
    private Preference mAppLinkState;
    private ClearDefaultsPreference mClearDefaultsPreference;
    private boolean mHasDomainUrls;
    private boolean mIsBrowser;
    private PackageManager mPm;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 17;
    }

    public void onClick(View view) {
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.applications.AppInfoBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.installed_app_launch_settings);
        this.mAppDomainUrls = (AppDomainsPreference) findPreference("app_launch_supported_domain_urls");
        this.mClearDefaultsPreference = (ClearDefaultsPreference) findPreference("app_launch_clear_defaults");
        Preference findPreference = findPreference("app_link_state");
        this.mAppLinkState = findPreference;
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.applications.$$Lambda$AppLaunchSettings$g_t6BJ5Y7yCxTwc4xAP7Gknul5U */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return AppLaunchSettings.this.lambda$onCreate$0$AppLaunchSettings(preference);
            }
        });
        this.mPm = getActivity().getPackageManager();
        this.mIsBrowser = AppUtils.isBrowserApp(getContext(), this.mPackageName, UserHandle.myUserId());
        this.mHasDomainUrls = (this.mAppEntry.info.privateFlags & 16) != 0;
        if (!this.mIsBrowser) {
            CharSequence[] entries = getEntries(this.mPackageName);
            this.mAppDomainUrls.setTitles(entries);
            this.mAppDomainUrls.setValues(new int[entries.length]);
            this.mAppLinkState.setEnabled(this.mHasDomainUrls);
            return;
        }
        this.mAppLinkState.setShouldDisableView(true);
        this.mAppLinkState.setEnabled(false);
        this.mAppDomainUrls.setShouldDisableView(true);
        this.mAppDomainUrls.setEnabled(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ boolean lambda$onCreate$0$AppLaunchSettings(Preference preference) {
        Bundle bundle = new Bundle();
        bundle.putString("package", this.mPackageName);
        bundle.putInt("uid", this.mUserId);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination("com.android.settings.applications.OpenSupportedLinks");
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(17);
        subSettingLauncher.setTitleRes(-1);
        subSettingLauncher.launch();
        return true;
    }

    private int linkStateToResourceId(int i) {
        if (i == 2) {
            return C0017R$string.app_link_open_always;
        }
        if (i != 3) {
            return C0017R$string.app_link_open_ask;
        }
        return C0017R$string.app_link_open_never;
    }

    private CharSequence[] getEntries(String str) {
        ArraySet<String> handledDomains = Utils.getHandledDomains(this.mPm, str);
        return (CharSequence[]) handledDomains.toArray(new CharSequence[handledDomains.size()]);
    }

    private void setAppLinkStateSummary() {
        this.mAppLinkState.setSummary(linkStateToResourceId(this.mPm.getIntentVerificationStatusAsUser(this.mPackageName, UserHandle.myUserId())));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public boolean refreshUi() {
        if (this.mHasDomainUrls) {
            setAppLinkStateSummary();
        }
        this.mClearDefaultsPreference.setPackageName(this.mPackageName);
        this.mClearDefaultsPreference.setAppEntry(this.mAppEntry);
        return true;
    }
}
