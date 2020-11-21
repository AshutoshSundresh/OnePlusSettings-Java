package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableData;
import android.text.BidiFormatter;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.AccessiblePreferenceCategory;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.search.SearchIndexableRaw;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AccountPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, AuthenticatorHelper.OnAccountsUpdateListener, Preference.OnPreferenceClickListener, LifecycleObserver, OnPause, OnResume {
    private int mAccountProfileOrder;
    private String[] mAuthorities;
    private int mAuthoritiesCount;
    private SettingsPreferenceFragment mFragment;
    private AccountRestrictionHelper mHelper;
    private ManagedProfileBroadcastReceiver mManagedProfileBroadcastReceiver;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private Preference mProfileNotAvailablePreference;
    private SparseArray<ProfileData> mProfiles;
    private int mType;
    private UserManager mUm;

    public static class ProfileData {
        public ArrayMap<String, AccountTypePreference> accountPreferences = new ArrayMap<>();
        public RestrictedPreference addAccountPreference;
        public AuthenticatorHelper authenticatorHelper;
        public Preference managedProfilePreference;
        public boolean pendingRemoval;
        public PreferenceGroup preferenceGroup;
        public RestrictedPreference removeWorkProfilePreference;
        public UserInfo userInfo;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "user_and_account_settings_screen";
    }

    public AccountPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, String[] strArr, int i) {
        this(context, settingsPreferenceFragment, strArr, new AccountRestrictionHelper(context), i);
    }

    AccountPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, String[] strArr, AccountRestrictionHelper accountRestrictionHelper, int i) {
        super(context);
        this.mProfiles = new SparseArray<>();
        this.mManagedProfileBroadcastReceiver = new ManagedProfileBroadcastReceiver();
        this.mAuthoritiesCount = 0;
        this.mAccountProfileOrder = 1;
        this.mUm = (UserManager) context.getSystemService("user");
        this.mAuthorities = strArr;
        this.mFragment = settingsPreferenceFragment;
        if (strArr != null) {
            this.mAuthoritiesCount = strArr.length;
        }
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        this.mHelper = accountRestrictionHelper;
        this.mType = i;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !this.mUm.isManagedProfile();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        updateUi();
    }

    @Override // com.android.settings.core.PreferenceControllerMixin
    public void updateDynamicRawDataToIndex(List<SearchIndexableRaw> list) {
        if (isAvailable()) {
            Resources resources = this.mContext.getResources();
            String string = resources.getString(C0017R$string.account_settings_title);
            for (UserInfo userInfo : this.mUm.getProfiles(UserHandle.myUserId())) {
                if (userInfo.isEnabled() && userInfo.isManagedProfile() && userInfo.id != 999) {
                    if (!this.mHelper.hasBaseUserRestriction("no_remove_managed_profile", UserHandle.myUserId())) {
                        SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(this.mContext);
                        ((SearchIndexableData) searchIndexableRaw).key = "remove_profile";
                        searchIndexableRaw.title = resources.getString(C0017R$string.remove_managed_profile_label);
                        searchIndexableRaw.screenTitle = string;
                        list.add(searchIndexableRaw);
                    }
                    SearchIndexableRaw searchIndexableRaw2 = new SearchIndexableRaw(this.mContext);
                    ((SearchIndexableData) searchIndexableRaw2).key = "work_profile_setting";
                    searchIndexableRaw2.title = resources.getString(C0017R$string.managed_profile_settings_title);
                    searchIndexableRaw2.screenTitle = string;
                    list.add(searchIndexableRaw2);
                }
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        updateUi();
        this.mManagedProfileBroadcastReceiver.register(this.mContext);
        listenToAccountUpdates();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        stopListeningToAccountUpdates();
        this.mManagedProfileBroadcastReceiver.unregister(this.mContext);
    }

    @Override // com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener
    public void onAccountsUpdate(UserHandle userHandle) {
        ProfileData profileData = this.mProfiles.get(userHandle.getIdentifier());
        if (profileData != null) {
            updateAccountTypes(profileData);
            return;
        }
        Log.w("AccountPrefController", "Missing Settings screen for: " + userHandle.getIdentifier());
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        int metricsCategory = this.mFragment.getMetricsCategory();
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            ProfileData valueAt = this.mProfiles.valueAt(i);
            if (preference == valueAt.addAccountPreference) {
                this.mMetricsFeatureProvider.logClickedPreference(preference, metricsCategory);
                Intent intent = new Intent("android.settings.ADD_ACCOUNT_SETTINGS");
                intent.putExtra("android.intent.extra.USER", valueAt.userInfo.getUserHandle());
                intent.putExtra("authorities", this.mAuthorities);
                this.mContext.startActivity(intent);
                return true;
            } else if (preference == valueAt.removeWorkProfilePreference) {
                this.mMetricsFeatureProvider.logClickedPreference(preference, metricsCategory);
                RemoveUserFragment.newInstance(valueAt.userInfo.id).show(this.mFragment.getFragmentManager(), "removeUser");
                return true;
            } else if (preference == valueAt.managedProfilePreference) {
                this.mMetricsFeatureProvider.logClickedPreference(preference, metricsCategory);
                Bundle bundle = new Bundle();
                bundle.putParcelable("android.intent.extra.USER", valueAt.userInfo.getUserHandle());
                SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
                subSettingLauncher.setSourceMetricsCategory(metricsCategory);
                subSettingLauncher.setDestination(ManagedProfileSettings.class.getName());
                subSettingLauncher.setTitleRes(C0017R$string.managed_profile_settings_title);
                subSettingLauncher.setArguments(bundle);
                subSettingLauncher.launch();
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateUi() {
        if (!isAvailable()) {
            Log.e("AccountPrefController", "We should not be showing settings for a managed profile");
            return;
        }
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            this.mProfiles.valueAt(i).pendingRemoval = true;
        }
        if (this.mUm.isRestrictedProfile()) {
            updateProfileUi(this.mUm.getUserInfo(UserHandle.myUserId()));
        } else {
            List profiles = this.mUm.getProfiles(UserHandle.myUserId());
            int size2 = profiles.size();
            isMultiAppEnable(profiles);
            for (int i2 = 0; i2 < size2; i2++) {
                if (profiles.get(i2).id != 999) {
                    if (profiles.get(i2).isManagedProfile() && (this.mType & 2) != 0) {
                        updateProfileUi(profiles.get(i2));
                    } else if (!profiles.get(i2).isManagedProfile() && (this.mType & 1) != 0) {
                        updateProfileUi(profiles.get(i2));
                    }
                }
            }
        }
        cleanUpPreferences();
        int size3 = this.mProfiles.size();
        for (int i3 = 0; i3 < size3; i3++) {
            updateAccountTypes(this.mProfiles.valueAt(i3));
        }
    }

    private void updateProfileUi(UserInfo userInfo) {
        updateProfileUi(userInfo, true);
    }

    private void updateProfileUi(UserInfo userInfo, boolean z) {
        if (this.mFragment.getPreferenceManager() != null) {
            ProfileData profileData = this.mProfiles.get(userInfo.id);
            if (profileData != null) {
                profileData.pendingRemoval = false;
                profileData.userInfo = userInfo;
                if (userInfo.isEnabled()) {
                    profileData.authenticatorHelper = new AuthenticatorHelper(this.mContext, userInfo.getUserHandle(), this);
                    return;
                }
                return;
            }
            Context context = this.mContext;
            ProfileData profileData2 = new ProfileData();
            profileData2.userInfo = userInfo;
            AccessiblePreferenceCategory createAccessiblePreferenceCategory = this.mHelper.createAccessiblePreferenceCategory(this.mFragment.getPreferenceManager().getContext());
            int i = this.mAccountProfileOrder;
            this.mAccountProfileOrder = i + 1;
            createAccessiblePreferenceCategory.setOrder(i);
            if (isSingleProfile()) {
                createAccessiblePreferenceCategory.setTitle(context.getString(C0017R$string.account_for_section_header, BidiFormatter.getInstance().unicodeWrap(userInfo.name)));
                createAccessiblePreferenceCategory.setContentDescription(this.mContext.getString(C0017R$string.account_settings));
            } else if (userInfo.isManagedProfile()) {
                if (this.mType == 3) {
                    createAccessiblePreferenceCategory.setTitle(C0017R$string.category_work);
                    String workGroupSummary = getWorkGroupSummary(context, userInfo);
                    createAccessiblePreferenceCategory.setSummary(workGroupSummary);
                    createAccessiblePreferenceCategory.setContentDescription(this.mContext.getString(C0017R$string.accessibility_category_work, workGroupSummary));
                }
                RestrictedPreference newRemoveWorkProfilePreference = newRemoveWorkProfilePreference();
                profileData2.removeWorkProfilePreference = newRemoveWorkProfilePreference;
                this.mHelper.enforceRestrictionOnPreference(newRemoveWorkProfilePreference, "no_remove_managed_profile", UserHandle.myUserId());
                profileData2.managedProfilePreference = newManagedProfileSettings();
            } else if (this.mType == 3) {
                createAccessiblePreferenceCategory.setTitle(C0017R$string.category_personal);
                createAccessiblePreferenceCategory.setContentDescription(this.mContext.getString(C0017R$string.accessibility_category_personal));
            }
            PreferenceScreen preferenceScreen = this.mFragment.getPreferenceScreen();
            if (preferenceScreen != null && z) {
                preferenceScreen.addPreference(createAccessiblePreferenceCategory);
            }
            if (z) {
                profileData2.preferenceGroup = createAccessiblePreferenceCategory;
            } else {
                profileData2.preferenceGroup = preferenceScreen;
            }
            if (userInfo.isEnabled()) {
                profileData2.authenticatorHelper = new AuthenticatorHelper(context, userInfo.getUserHandle(), this);
                RestrictedPreference newAddAccountPreference = newAddAccountPreference();
                profileData2.addAccountPreference = newAddAccountPreference;
                this.mHelper.enforceRestrictionOnPreference(newAddAccountPreference, "no_modify_accounts", userInfo.id);
            }
            this.mProfiles.put(userInfo.id, profileData2);
        }
    }

    private RestrictedPreference newAddAccountPreference() {
        RestrictedPreference restrictedPreference = new RestrictedPreference(this.mFragment.getPreferenceManager().getContext());
        restrictedPreference.setKey("add_account");
        restrictedPreference.setTitle(C0017R$string.add_account_label);
        restrictedPreference.setLayoutResource(C0012R$layout.op_add_account_preference);
        restrictedPreference.setOnPreferenceClickListener(this);
        restrictedPreference.setOrder(1000);
        return restrictedPreference;
    }

    private RestrictedPreference newRemoveWorkProfilePreference() {
        RestrictedPreference restrictedPreference = new RestrictedPreference(this.mFragment.getPreferenceManager().getContext());
        restrictedPreference.setKey("remove_profile");
        restrictedPreference.setTitle(C0017R$string.remove_managed_profile_label);
        restrictedPreference.setOnPreferenceClickListener(this);
        restrictedPreference.setOrder(1002);
        return restrictedPreference;
    }

    private Preference newManagedProfileSettings() {
        Preference preference = new Preference(this.mFragment.getPreferenceManager().getContext());
        preference.setKey("work_profile_setting");
        preference.setTitle(C0017R$string.managed_profile_settings_title);
        preference.setOnPreferenceClickListener(this);
        preference.setOrder(1001);
        return preference;
    }

    private String getWorkGroupSummary(Context context, UserInfo userInfo) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo adminApplicationInfo = Utils.getAdminApplicationInfo(context, userInfo.id);
        if (adminApplicationInfo == null) {
            return null;
        }
        CharSequence applicationLabel = packageManager.getApplicationLabel(adminApplicationInfo);
        return this.mContext.getString(C0017R$string.managing_admin, applicationLabel);
    }

    /* access modifiers changed from: package-private */
    public void cleanUpPreferences() {
        PreferenceScreen preferenceScreen = this.mFragment.getPreferenceScreen();
        if (preferenceScreen != null) {
            for (int size = this.mProfiles.size() - 1; size >= 0; size--) {
                ProfileData valueAt = this.mProfiles.valueAt(size);
                if (valueAt.pendingRemoval) {
                    preferenceScreen.removePreference(valueAt.preferenceGroup);
                    this.mProfiles.removeAt(size);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void listenToAccountUpdates() {
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            AuthenticatorHelper authenticatorHelper = this.mProfiles.valueAt(i).authenticatorHelper;
            if (authenticatorHelper != null) {
                authenticatorHelper.listenToAccountUpdates();
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void stopListeningToAccountUpdates() {
        int size = this.mProfiles.size();
        for (int i = 0; i < size; i++) {
            AuthenticatorHelper authenticatorHelper = this.mProfiles.valueAt(i).authenticatorHelper;
            if (authenticatorHelper != null) {
                authenticatorHelper.stopListeningToAccountUpdates();
            }
        }
    }

    private void updateAccountTypes(ProfileData profileData) {
        PreferenceGroup preferenceGroup;
        if (!(this.mFragment.getPreferenceManager() == null || profileData.preferenceGroup.getPreferenceManager() == null)) {
            if (profileData.userInfo.isEnabled()) {
                ArrayMap<String, AccountTypePreference> arrayMap = new ArrayMap<>(profileData.accountPreferences);
                ArrayList<AccountTypePreference> accountTypePreferences = getAccountTypePreferences(profileData.authenticatorHelper, profileData.userInfo.getUserHandle(), arrayMap);
                int size = accountTypePreferences.size();
                for (int i = 0; i < size; i++) {
                    AccountTypePreference accountTypePreference = accountTypePreferences.get(i);
                    accountTypePreference.setOrder(i);
                    String key = accountTypePreference.getKey();
                    if (!profileData.accountPreferences.containsKey(key)) {
                        profileData.preferenceGroup.addPreference(accountTypePreference);
                        profileData.accountPreferences.put(key, accountTypePreference);
                    }
                }
                if (profileData.addAccountPreference != null) {
                    if (OPUtils.isAppExist(this.mContext, "com.oneplus.account") && (preferenceGroup = profileData.preferenceGroup) != null && preferenceGroup.findPreference("ONEPLUS_ACCOUNT") == null) {
                        Log.d("AccountPrefController", "add newAddOneplusAccountPreference");
                        preferenceGroup.addPreference(newAddOneplusAccountPreference(this.mContext));
                    }
                    profileData.preferenceGroup.addPreference(profileData.addAccountPreference);
                }
                for (String str : arrayMap.keySet()) {
                    profileData.preferenceGroup.removePreference(profileData.accountPreferences.get(str));
                    profileData.accountPreferences.remove(str);
                }
            } else {
                profileData.preferenceGroup.removeAll();
                if (this.mProfileNotAvailablePreference == null) {
                    this.mProfileNotAvailablePreference = new Preference(this.mFragment.getPreferenceManager().getContext());
                }
                this.mProfileNotAvailablePreference.setEnabled(false);
                this.mProfileNotAvailablePreference.setIcon(C0008R$drawable.empty_icon);
                this.mProfileNotAvailablePreference.setTitle((CharSequence) null);
                this.mProfileNotAvailablePreference.setSummary(C0017R$string.managed_profile_not_available_label);
                profileData.preferenceGroup.addPreference(this.mProfileNotAvailablePreference);
            }
            RestrictedPreference restrictedPreference = profileData.removeWorkProfilePreference;
            if (restrictedPreference != null) {
                profileData.preferenceGroup.addPreference(restrictedPreference);
            }
            Preference preference = profileData.managedProfilePreference;
            if (preference != null) {
                profileData.preferenceGroup.addPreference(preference);
            }
        }
    }

    private ArrayList<AccountTypePreference> getAccountTypePreferences(AuthenticatorHelper authenticatorHelper, UserHandle userHandle, ArrayMap<String, AccountTypePreference> arrayMap) {
        String[] strArr;
        CharSequence labelForType;
        int i;
        Account[] accountArr;
        int i2;
        String[] strArr2;
        int i3;
        UserHandle userHandle2 = userHandle;
        String[] enabledAccountTypes = authenticatorHelper.getEnabledAccountTypes();
        ArrayList<AccountTypePreference> arrayList = new ArrayList<>(enabledAccountTypes.length);
        int i4 = 0;
        while (i4 < enabledAccountTypes.length) {
            String str = enabledAccountTypes[i4];
            if (accountTypeHasAnyRequestedAuthorities(authenticatorHelper, str) && (labelForType = authenticatorHelper.getLabelForType(this.mContext, str)) != null) {
                String packageForType = authenticatorHelper.getPackageForType(str);
                int labelIdForType = authenticatorHelper.getLabelIdForType(str);
                Account[] accountsByTypeAsUser = AccountManager.get(this.mContext).getAccountsByTypeAsUser(str, userHandle2);
                Drawable drawableForType = authenticatorHelper.getDrawableForType(this.mContext, str);
                Context context = this.mFragment.getPreferenceManager().getContext();
                int length = accountsByTypeAsUser.length;
                int i5 = 0;
                while (i5 < length) {
                    Account account = accountsByTypeAsUser[i5];
                    AccountTypePreference remove = arrayMap.remove(AccountTypePreference.buildKey(account));
                    if (remove != null) {
                        arrayList.add(remove);
                    } else if (AccountRestrictionHelper.showAccount(this.mAuthorities, authenticatorHelper.getAuthoritiesForAccountType(account.type))) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("account", account);
                        bundle.putParcelable("user_handle", userHandle2);
                        bundle.putString("account_type", str);
                        strArr2 = enabledAccountTypes;
                        bundle.putString("account_label", labelForType.toString());
                        bundle.putInt("account_title_res", labelIdForType);
                        bundle.putParcelable("android.intent.extra.USER", userHandle2);
                        i3 = i5;
                        i2 = length;
                        accountArr = accountsByTypeAsUser;
                        i = labelIdForType;
                        arrayList.add(new AccountTypePreference(context, this.mMetricsFeatureProvider.getMetricsCategory(this.mFragment), account, packageForType, labelIdForType, labelForType, AccountDetailDashboardFragment.class.getName(), bundle, drawableForType));
                        i5 = i3 + 1;
                        userHandle2 = userHandle;
                        enabledAccountTypes = strArr2;
                        length = i2;
                        accountsByTypeAsUser = accountArr;
                        labelIdForType = i;
                    }
                    strArr2 = enabledAccountTypes;
                    i3 = i5;
                    i2 = length;
                    accountArr = accountsByTypeAsUser;
                    i = labelIdForType;
                    i5 = i3 + 1;
                    userHandle2 = userHandle;
                    enabledAccountTypes = strArr2;
                    length = i2;
                    accountsByTypeAsUser = accountArr;
                    labelIdForType = i;
                }
                strArr = enabledAccountTypes;
                authenticatorHelper.preloadDrawableForType(this.mContext, str);
            } else {
                strArr = enabledAccountTypes;
            }
            i4++;
            userHandle2 = userHandle;
            enabledAccountTypes = strArr;
        }
        Collections.sort(arrayList, new Comparator<AccountTypePreference>(this) {
            /* class com.android.settings.accounts.AccountPreferenceController.AnonymousClass1 */

            public int compare(AccountTypePreference accountTypePreference, AccountTypePreference accountTypePreference2) {
                int compareTo = accountTypePreference.getSummary().toString().compareTo(accountTypePreference2.getSummary().toString());
                return compareTo != 0 ? compareTo : accountTypePreference.getTitle().toString().compareTo(accountTypePreference2.getTitle().toString());
            }
        });
        return arrayList;
    }

    private boolean accountTypeHasAnyRequestedAuthorities(AuthenticatorHelper authenticatorHelper, String str) {
        if (this.mAuthoritiesCount == 0) {
            return true;
        }
        ArrayList<String> authoritiesForAccountType = authenticatorHelper.getAuthoritiesForAccountType(str);
        if (authoritiesForAccountType == null) {
            Log.d("AccountPrefController", "No sync authorities for account type: " + str);
            return false;
        }
        for (int i = 0; i < this.mAuthoritiesCount; i++) {
            if (authoritiesForAccountType.contains(this.mAuthorities[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isSingleProfile() {
        return this.mUm.isLinkedUser() || this.mUm.getProfiles(UserHandle.myUserId()).size() == 1;
    }

    private boolean isMultiAppEnable(List<UserInfo> list) {
        for (UserInfo userInfo : list) {
            if (userInfo.id == 999) {
                return true;
            }
        }
        return false;
    }

    private RestrictedPreference newAddOneplusAccountPreference(Context context) {
        RestrictedPreference restrictedPreference = new RestrictedPreference(context);
        restrictedPreference.setKey("ONEPLUS_ACCOUNT");
        restrictedPreference.setTitle(C0017R$string.add_oneplus_account_label);
        restrictedPreference.setIcon(C0008R$drawable.op_ic_oneplus_account_icon);
        restrictedPreference.setOrder(999);
        restrictedPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.accounts.AccountPreferenceController.AnonymousClass2 */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                if (!OPUtils.isAppExist(((AbstractPreferenceController) AccountPreferenceController.this).mContext, "com.oneplus.account")) {
                    return true;
                }
                AccountManager accountManager = AccountManager.get(((AbstractPreferenceController) AccountPreferenceController.this).mContext);
                Bundle bundle = new Bundle();
                bundle.putString("come_from", "from_settings");
                accountManager.addAccount("com.oneplus.account", "", null, bundle, AccountPreferenceController.this.mFragment.getActivity(), null, null);
                return true;
            }
        });
        return restrictedPreference;
    }

    private class ManagedProfileBroadcastReceiver extends BroadcastReceiver {
        private boolean mListeningToManagedProfileEvents;

        private ManagedProfileBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("AccountPrefController", "Received broadcast: " + action);
            if (!action.equals("android.intent.action.MANAGED_PROFILE_REMOVED") && !action.equals("android.intent.action.MANAGED_PROFILE_ADDED")) {
                Log.w("AccountPrefController", "Cannot handle received broadcast: " + intent.getAction());
            } else if (AccountPreferenceController.this.mFragment instanceof AccountWorkProfileDashboardFragment) {
                AccountPreferenceController.this.mFragment.getActivity().finish();
            } else {
                AccountPreferenceController.this.stopListeningToAccountUpdates();
                AccountPreferenceController.this.updateUi();
                AccountPreferenceController.this.listenToAccountUpdates();
            }
        }

        public void register(Context context) {
            if (!this.mListeningToManagedProfileEvents) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
                intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
                context.registerReceiver(this, intentFilter);
                this.mListeningToManagedProfileEvents = true;
            }
        }

        public void unregister(Context context) {
            if (this.mListeningToManagedProfileEvents) {
                context.unregisterReceiver(this);
                this.mListeningToManagedProfileEvents = false;
            }
        }
    }
}
