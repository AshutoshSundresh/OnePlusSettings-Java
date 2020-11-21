package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.location.LocationSettings;
import com.android.settings.utils.LocalClassLoaderContextThemeWrapper;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.core.instrumentation.Instrumentable;

public class AccountTypePreferenceLoader {
    private AuthenticatorHelper mAuthenticatorHelper;
    private PreferenceFragmentCompat mFragment;
    private UserHandle mUserHandle;

    public AccountTypePreferenceLoader(PreferenceFragmentCompat preferenceFragmentCompat, AuthenticatorHelper authenticatorHelper, UserHandle userHandle) {
        this.mFragment = preferenceFragmentCompat;
        this.mAuthenticatorHelper = authenticatorHelper;
        this.mUserHandle = userHandle;
    }

    public PreferenceScreen addPreferencesForType(String str, PreferenceScreen preferenceScreen) {
        AuthenticatorDescription authenticatorDescription;
        if (!this.mAuthenticatorHelper.containsAccountType(str)) {
            return null;
        }
        try {
            authenticatorDescription = this.mAuthenticatorHelper.getAccountTypeDescription(str);
            if (authenticatorDescription == null) {
                return null;
            }
            try {
                if (authenticatorDescription.accountPreferencesId == 0) {
                    return null;
                }
                Context createPackageContextAsUser = this.mFragment.getActivity().createPackageContextAsUser(authenticatorDescription.packageName, 0, this.mUserHandle);
                Resources.Theme newTheme = this.mFragment.getResources().newTheme();
                newTheme.applyStyle(C0018R$style.Theme_SettingsBase, true);
                LocalClassLoaderContextThemeWrapper localClassLoaderContextThemeWrapper = new LocalClassLoaderContextThemeWrapper(getClass(), createPackageContextAsUser, 0);
                localClassLoaderContextThemeWrapper.getTheme().setTo(newTheme);
                return this.mFragment.getPreferenceManager().inflateFromResource(localClassLoaderContextThemeWrapper, authenticatorDescription.accountPreferencesId, preferenceScreen);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("AccountTypePrefLoader", "Couldn't load preferences.xml file from " + authenticatorDescription.packageName);
                return null;
            } catch (Resources.NotFoundException unused2) {
                Log.w("AccountTypePrefLoader", "Couldn't load preferences.xml file from " + authenticatorDescription.packageName);
                return null;
            }
        } catch (PackageManager.NameNotFoundException unused3) {
            authenticatorDescription = null;
            Log.w("AccountTypePrefLoader", "Couldn't load preferences.xml file from " + authenticatorDescription.packageName);
            return null;
        } catch (Resources.NotFoundException unused4) {
            authenticatorDescription = null;
            Log.w("AccountTypePrefLoader", "Couldn't load preferences.xml file from " + authenticatorDescription.packageName);
            return null;
        }
    }

    public void updatePreferenceIntents(PreferenceGroup preferenceGroup, final String str, Account account) {
        final PackageManager packageManager = this.mFragment.getActivity().getPackageManager();
        int i = 0;
        while (i < preferenceGroup.getPreferenceCount()) {
            Preference preference = preferenceGroup.getPreference(i);
            if (preference instanceof PreferenceGroup) {
                updatePreferenceIntents((PreferenceGroup) preference, str, account);
            }
            Intent intent = preference.getIntent();
            if (intent != null) {
                if (TextUtils.equals(intent.getAction(), "android.settings.LOCATION_SOURCE_SETTINGS")) {
                    preference.setOnPreferenceClickListener(new FragmentStarter(LocationSettings.class.getName(), C0017R$string.location_settings_title));
                } else if (packageManager.resolveActivityAsUser(intent, 65536, this.mUserHandle.getIdentifier()) == null) {
                    preferenceGroup.removePreference(preference);
                } else {
                    intent.putExtra("account", account);
                    intent.setFlags(intent.getFlags() | 268435456);
                    preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        /* class com.android.settings.accounts.AccountTypePreferenceLoader.AnonymousClass1 */

                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public boolean onPreferenceClick(Preference preference) {
                            Intent intent = preference.getIntent();
                            if (AccountTypePreferenceLoader.this.isSafeIntent(packageManager, intent, str)) {
                                AccountTypePreferenceLoader.this.mFragment.getActivity().startActivityAsUser(intent, AccountTypePreferenceLoader.this.mUserHandle);
                                return true;
                            }
                            Log.e("AccountTypePrefLoader", "Refusing to launch authenticator intent becauseit exploits Settings permissions: " + intent);
                            return true;
                        }
                    });
                }
            }
            i++;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isSafeIntent(PackageManager packageManager, Intent intent, String str) {
        AuthenticatorDescription accountTypeDescription = this.mAuthenticatorHelper.getAccountTypeDescription(str);
        ResolveInfo resolveActivityAsUser = packageManager.resolveActivityAsUser(intent, 0, this.mUserHandle.getIdentifier());
        if (resolveActivityAsUser == null) {
            return false;
        }
        try {
            if (resolveActivityAsUser.activityInfo.applicationInfo.uid == packageManager.getApplicationInfo(accountTypeDescription.packageName, 0).uid) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AccountTypePrefLoader", "Intent considered unsafe due to exception.", e);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public class FragmentStarter implements Preference.OnPreferenceClickListener {
        private final String mClass;
        private final int mTitleRes;

        public FragmentStarter(String str, int i) {
            this.mClass = str;
            this.mTitleRes = i;
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            int metricsCategory = AccountTypePreferenceLoader.this.mFragment instanceof Instrumentable ? ((Instrumentable) AccountTypePreferenceLoader.this.mFragment).getMetricsCategory() : 0;
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(preference.getContext());
            subSettingLauncher.setTitleRes(this.mTitleRes);
            subSettingLauncher.setDestination(this.mClass);
            subSettingLauncher.setSourceMetricsCategory(metricsCategory);
            subSettingLauncher.launch();
            if (!this.mClass.equals(LocationSettings.class.getName())) {
                return true;
            }
            AccountTypePreferenceLoader.this.mFragment.getActivity().sendBroadcast(new Intent("com.android.settings.accounts.LAUNCHING_LOCATION_SETTINGS"), "android.permission.WRITE_SECURE_SETTINGS");
            return true;
        }
    }
}
