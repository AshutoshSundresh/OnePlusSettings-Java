package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.SyncStatusInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.LayoutPreference;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AccountSyncSettings extends AccountPreferenceBase {
    private Account mAccount;
    private ArrayList<SyncAdapterType> mInvisibleAdapters = Lists.newArrayList();
    private HashMap<Integer, Integer> mUidRequestCodeMap = new HashMap<>();

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return i != 102 ? 0 : 587;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 102) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.cant_sync_dialog_title);
        builder.setMessage(C0017R$string.cant_sync_dialog_message);
        builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.accounts.AccountPreferenceBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.account_sync_settings);
        getPreferenceScreen().setOrderingAsAdded(false);
        setAccessibilityTitle();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (!this.mUidRequestCodeMap.isEmpty()) {
            bundle.putSerializable("uid_request_code", this.mUidRequestCodeMap);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accounts.AccountPreferenceBase, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Bundle arguments = getArguments();
        if (arguments == null) {
            Log.e("AccountPreferenceBase", "No arguments provided when starting intent. ACCOUNT_KEY needed.");
            finish();
            return;
        }
        Account account = (Account) arguments.getParcelable("account");
        this.mAccount = account;
        if (!accountExists(account)) {
            Log.e("AccountPreferenceBase", "Account provided does not exist: " + this.mAccount);
            finish();
            return;
        }
        if (Log.isLoggable("AccountPreferenceBase", 2)) {
            Log.v("AccountPreferenceBase", "Got account: " + this.mAccount);
        }
        FragmentActivity activity = getActivity();
        EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, this, null);
        newInstance.setRecyclerView(getListView(), getSettingsLifecycle());
        newInstance.setIcon(getDrawableForType(this.mAccount.type));
        newInstance.setLabel(this.mAccount.name);
        newInstance.setSummary(getLabelForType(this.mAccount.type));
        LayoutPreference done = newInstance.done(activity, getPrefContext());
        done.setOrder(0);
        getPreferenceScreen().addPreference(done);
        if (bundle != null && bundle.containsKey("uid_request_code")) {
            this.mUidRequestCodeMap = (HashMap) bundle.getSerializable("uid_request_code");
        }
    }

    private void setAccessibilityTitle() {
        int i;
        UserInfo userInfo = ((UserManager) getSystemService("user")).getUserInfo(this.mUserHandle.getIdentifier());
        boolean isManagedProfile = userInfo != null ? userInfo.isManagedProfile() : false;
        CharSequence title = getActivity().getTitle();
        if (isManagedProfile) {
            i = C0017R$string.accessibility_work_account_title;
        } else {
            i = C0017R$string.accessibility_personal_account_title;
        }
        getActivity().setTitle(Utils.createAccessibleSequence(title, getString(i, title)));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.accounts.AccountPreferenceBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        this.mAuthenticatorHelper.listenToAccountUpdates();
        updateAuthDescriptions();
        onAccountsUpdate(Binder.getCallingUserHandle());
        super.onResume();
    }

    @Override // com.android.settings.accounts.AccountPreferenceBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mAuthenticatorHelper.stopListeningToAccountUpdates();
    }

    private void addSyncStateSwitch(Account account, String str, String str2, int i) {
        SyncStateSwitchPreference syncStateSwitchPreference = (SyncStateSwitchPreference) getCachedPreference(str);
        if (syncStateSwitchPreference == null) {
            syncStateSwitchPreference = new SyncStateSwitchPreference(getPrefContext(), account, str, str2, i);
            getPreferenceScreen().addPreference(syncStateSwitchPreference);
        } else {
            syncStateSwitchPreference.setup(account, str, str2, i);
        }
        PackageManager packageManager = getPackageManager();
        syncStateSwitchPreference.setPersistent(false);
        ProviderInfo resolveContentProviderAsUser = packageManager.resolveContentProviderAsUser(str, 0, this.mUserHandle.getIdentifier());
        if (resolveContentProviderAsUser != null) {
            CharSequence loadLabel = resolveContentProviderAsUser.loadLabel(packageManager);
            if (TextUtils.isEmpty(loadLabel)) {
                Log.e("AccountPreferenceBase", "Provider needs a label for authority '" + str + "'");
                return;
            }
            syncStateSwitchPreference.setTitle(loadLabel);
            syncStateSwitchPreference.setKey(str);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, getString(C0017R$string.sync_menu_sync_now));
        MenuItem add2 = menu.add(0, 2, 0, getString(C0017R$string.sync_menu_sync_cancel));
        add.setShowAsAction(4);
        add2.setShowAsAction(4);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean z = !ContentResolver.getCurrentSyncsAsUser(this.mUserHandle.getIdentifier()).isEmpty();
        menu.findItem(1).setVisible(!z).setEnabled(enabledSyncNowMenu());
        menu.findItem(2).setVisible(z);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            startSyncForEnabledProviders();
            return true;
        } else if (itemId != 2) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            cancelSyncForEnabledProviders();
            return true;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            int preferenceCount = getPreferenceScreen().getPreferenceCount();
            for (int i3 = 0; i3 < preferenceCount; i3++) {
                Preference preference = getPreferenceScreen().getPreference(i3);
                if (preference instanceof SyncStateSwitchPreference) {
                    SyncStateSwitchPreference syncStateSwitchPreference = (SyncStateSwitchPreference) preference;
                    if (getRequestCodeByUid(syncStateSwitchPreference.getUid()) == i) {
                        onPreferenceTreeClick(syncStateSwitchPreference);
                        return;
                    }
                }
            }
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (getActivity() == null) {
            return false;
        }
        if (!(preference instanceof SyncStateSwitchPreference)) {
            return super.onPreferenceTreeClick(preference);
        }
        SyncStateSwitchPreference syncStateSwitchPreference = (SyncStateSwitchPreference) preference;
        String authority = syncStateSwitchPreference.getAuthority();
        if (TextUtils.isEmpty(authority)) {
            return false;
        }
        Account account = syncStateSwitchPreference.getAccount();
        int identifier = this.mUserHandle.getIdentifier();
        String packageName = syncStateSwitchPreference.getPackageName();
        boolean syncAutomaticallyAsUser = ContentResolver.getSyncAutomaticallyAsUser(account, authority, identifier);
        if (!syncStateSwitchPreference.isOneTimeSyncMode()) {
            boolean isChecked = syncStateSwitchPreference.isChecked();
            if (isChecked == syncAutomaticallyAsUser || (isChecked && requestAccountAccessIfNeeded(packageName))) {
                return true;
            }
            ContentResolver.setSyncAutomaticallyAsUser(account, authority, isChecked, identifier);
            if (!ContentResolver.getMasterSyncAutomaticallyAsUser(identifier) || !isChecked) {
                requestOrCancelSync(account, authority, isChecked);
            }
        } else if (requestAccountAccessIfNeeded(packageName)) {
            return true;
        } else {
            requestOrCancelSync(account, authority, true);
        }
        return true;
    }

    private boolean requestAccountAccessIfNeeded(String str) {
        IntentSender createRequestAccountAccessIntentSenderAsUser;
        if (str == null) {
            return false;
        }
        try {
            int packageUidAsUser = getContext().getPackageManager().getPackageUidAsUser(str, this.mUserHandle.getIdentifier());
            AccountManager accountManager = (AccountManager) getContext().getSystemService(AccountManager.class);
            if (!accountManager.hasAccountAccess(this.mAccount, str, this.mUserHandle) && (createRequestAccountAccessIntentSenderAsUser = accountManager.createRequestAccountAccessIntentSenderAsUser(this.mAccount, str, this.mUserHandle)) != null) {
                try {
                    startIntentSenderForResult(createRequestAccountAccessIntentSenderAsUser, addUidAndGenerateRequestCode(packageUidAsUser), null, 0, 0, 0, null);
                    return true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e("AccountPreferenceBase", "Error requesting account access", e);
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e("AccountPreferenceBase", "Invalid sync ", e2);
            return false;
        }
    }

    private void startSyncForEnabledProviders() {
        requestOrCancelSyncForEnabledProviders(true);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    private void cancelSyncForEnabledProviders() {
        requestOrCancelSyncForEnabledProviders(false);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    private void requestOrCancelSyncForEnabledProviders(boolean z) {
        int preferenceCount = getPreferenceScreen().getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof SyncStateSwitchPreference) {
                SyncStateSwitchPreference syncStateSwitchPreference = (SyncStateSwitchPreference) preference;
                if (syncStateSwitchPreference.isChecked()) {
                    requestOrCancelSync(syncStateSwitchPreference.getAccount(), syncStateSwitchPreference.getAuthority(), z);
                }
            }
        }
        if (this.mAccount != null) {
            Iterator<SyncAdapterType> it = this.mInvisibleAdapters.iterator();
            while (it.hasNext()) {
                requestOrCancelSync(this.mAccount, it.next().authority, z);
            }
        }
    }

    private void requestOrCancelSync(Account account, String str, boolean z) {
        if (z) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("force", true);
            ContentResolver.requestSyncAsUser(account, str, this.mUserHandle.getIdentifier(), bundle);
            return;
        }
        ContentResolver.cancelSyncAsUser(account, str, this.mUserHandle.getIdentifier());
    }

    private boolean isSyncing(List<SyncInfo> list, Account account, String str) {
        for (SyncInfo syncInfo : list) {
            if (syncInfo.account.equals(account) && syncInfo.authority.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accounts.AccountPreferenceBase
    public void onSyncStateUpdated() {
        if (isResumed()) {
            setFeedsState();
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.invalidateOptionsMenu();
            }
        }
    }

    private void setFeedsState() {
        List<SyncInfo> list;
        int i;
        int i2;
        boolean z;
        boolean z2;
        long j;
        boolean z3;
        AccountSyncSettings accountSyncSettings = this;
        Date date = new Date();
        int identifier = accountSyncSettings.mUserHandle.getIdentifier();
        List<SyncInfo> currentSyncsAsUser = ContentResolver.getCurrentSyncsAsUser(identifier);
        updateAccountSwitches();
        int preferenceCount = getPreferenceScreen().getPreferenceCount();
        int i3 = 0;
        boolean z4 = false;
        while (i3 < preferenceCount) {
            Preference preference = getPreferenceScreen().getPreference(i3);
            if (!(preference instanceof SyncStateSwitchPreference)) {
                list = currentSyncsAsUser;
                i2 = preferenceCount;
                i = i3;
            } else {
                SyncStateSwitchPreference syncStateSwitchPreference = (SyncStateSwitchPreference) preference;
                String authority = syncStateSwitchPreference.getAuthority();
                Account account = syncStateSwitchPreference.getAccount();
                SyncStatusInfo syncStatusAsUser = ContentResolver.getSyncStatusAsUser(account, authority, identifier);
                boolean syncAutomaticallyAsUser = ContentResolver.getSyncAutomaticallyAsUser(account, authority, identifier);
                if (syncStatusAsUser == null) {
                    z = false;
                } else {
                    z = syncStatusAsUser.pending;
                }
                if (syncStatusAsUser == null) {
                    z2 = false;
                } else {
                    z2 = syncStatusAsUser.initialize;
                }
                boolean isSyncing = accountSyncSettings.isSyncing(currentSyncsAsUser, account, authority);
                i = i3;
                boolean z5 = (syncStatusAsUser == null || syncStatusAsUser.lastFailureTime == 0 || syncStatusAsUser.getLastFailureMesgAsInt(0) == 1) ? false : true;
                if (!syncAutomaticallyAsUser) {
                    z5 = false;
                }
                if (z5 && !isSyncing && !z) {
                    z4 = true;
                }
                if (Log.isLoggable("AccountPreferenceBase", 3)) {
                    StringBuilder sb = new StringBuilder();
                    list = currentSyncsAsUser;
                    sb.append("Update sync status: ");
                    sb.append(account);
                    sb.append(" ");
                    sb.append(authority);
                    sb.append(" active = ");
                    sb.append(isSyncing);
                    sb.append(" pend =");
                    sb.append(z);
                    Log.d("AccountPreferenceBase", sb.toString());
                } else {
                    list = currentSyncsAsUser;
                }
                if (syncStatusAsUser == null) {
                    j = 0;
                } else {
                    j = syncStatusAsUser.lastSuccessTime;
                }
                if (!syncAutomaticallyAsUser) {
                    syncStateSwitchPreference.setSummary(C0017R$string.sync_disabled);
                } else if (isSyncing) {
                    syncStateSwitchPreference.setSummary(C0017R$string.sync_in_progress);
                } else {
                    if (j != 0) {
                        date.setTime(j);
                        i2 = preferenceCount;
                        z3 = false;
                        syncStateSwitchPreference.setSummary(getResources().getString(C0017R$string.last_synced, formatSyncDate(getContext(), date)));
                    } else {
                        i2 = preferenceCount;
                        z3 = false;
                        syncStateSwitchPreference.setSummary("");
                    }
                    int isSyncableAsUser = ContentResolver.getIsSyncableAsUser(account, authority, identifier);
                    syncStateSwitchPreference.setActive((isSyncing || isSyncableAsUser < 0 || z2) ? z3 : true);
                    syncStateSwitchPreference.setPending((z || isSyncableAsUser < 0 || z2) ? z3 : true);
                    syncStateSwitchPreference.setFailed(z5);
                    boolean z6 = !ContentResolver.getMasterSyncAutomaticallyAsUser(identifier);
                    syncStateSwitchPreference.setOneTimeSyncMode(z6);
                    syncStateSwitchPreference.setChecked((!z6 || syncAutomaticallyAsUser) ? true : z3);
                }
                i2 = preferenceCount;
                z3 = false;
                int isSyncableAsUser2 = ContentResolver.getIsSyncableAsUser(account, authority, identifier);
                syncStateSwitchPreference.setActive((isSyncing || isSyncableAsUser2 < 0 || z2) ? z3 : true);
                syncStateSwitchPreference.setPending((z || isSyncableAsUser2 < 0 || z2) ? z3 : true);
                syncStateSwitchPreference.setFailed(z5);
                boolean z62 = !ContentResolver.getMasterSyncAutomaticallyAsUser(identifier);
                syncStateSwitchPreference.setOneTimeSyncMode(z62);
                syncStateSwitchPreference.setChecked((!z62 || syncAutomaticallyAsUser) ? true : z3);
            }
            i3 = i + 1;
            accountSyncSettings = this;
            preferenceCount = i2;
            currentSyncsAsUser = list;
        }
        if (z4) {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            FooterPreference.Builder builder = new FooterPreference.Builder(getActivity());
            builder.setTitle(C0017R$string.sync_is_failing);
            preferenceScreen.addPreference(builder.build());
        }
    }

    @Override // com.android.settings.accounts.AccountPreferenceBase, com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener
    public void onAccountsUpdate(UserHandle userHandle) {
        super.onAccountsUpdate(userHandle);
        if (!accountExists(this.mAccount)) {
            finish();
            return;
        }
        updateAccountSwitches();
        onSyncStateUpdated();
    }

    private boolean accountExists(Account account) {
        if (account == null) {
            return false;
        }
        for (Account account2 : AccountManager.get(getActivity()).getAccountsByTypeAsUser(account.type, this.mUserHandle)) {
            if (account2.equals(account)) {
                return true;
            }
        }
        return false;
    }

    private void updateAccountSwitches() {
        this.mInvisibleAdapters.clear();
        SyncAdapterType[] syncAdapterTypesAsUser = ContentResolver.getSyncAdapterTypesAsUser(this.mUserHandle.getIdentifier());
        ArrayList arrayList = new ArrayList();
        for (SyncAdapterType syncAdapterType : syncAdapterTypesAsUser) {
            if (syncAdapterType.accountType.equals(this.mAccount.type)) {
                if (syncAdapterType.isUserVisible()) {
                    if (Log.isLoggable("AccountPreferenceBase", 3)) {
                        Log.d("AccountPreferenceBase", "updateAccountSwitches: added authority " + syncAdapterType.authority + " to accountType " + syncAdapterType.accountType);
                    }
                    arrayList.add(syncAdapterType);
                } else {
                    this.mInvisibleAdapters.add(syncAdapterType);
                }
            }
        }
        if (Log.isLoggable("AccountPreferenceBase", 3)) {
            Log.d("AccountPreferenceBase", "looking for sync adapters that match account " + this.mAccount);
        }
        cacheRemoveAllPrefs(getPreferenceScreen());
        getCachedPreference("pref_app_header");
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            SyncAdapterType syncAdapterType2 = (SyncAdapterType) arrayList.get(i);
            int isSyncableAsUser = ContentResolver.getIsSyncableAsUser(this.mAccount, syncAdapterType2.authority, this.mUserHandle.getIdentifier());
            if (Log.isLoggable("AccountPreferenceBase", 3)) {
                Log.d("AccountPreferenceBase", "  found authority " + syncAdapterType2.authority + " " + isSyncableAsUser);
            }
            if (isSyncableAsUser > 0) {
                try {
                    addSyncStateSwitch(this.mAccount, syncAdapterType2.authority, syncAdapterType2.getPackageName(), getContext().getPackageManager().getPackageUidAsUser(syncAdapterType2.getPackageName(), this.mUserHandle.getIdentifier()));
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("AccountPreferenceBase", "No uid for package" + syncAdapterType2.getPackageName(), e);
                }
            }
        }
        removeCachedPrefs(getPreferenceScreen());
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_accounts;
    }

    /* access modifiers changed from: package-private */
    public boolean enabledSyncNowMenu() {
        int preferenceCount = getPreferenceScreen().getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if ((preference instanceof SyncStateSwitchPreference) && ((SyncStateSwitchPreference) preference).isChecked()) {
                return true;
            }
        }
        return false;
    }

    private static String formatSyncDate(Context context, Date date) {
        return DateUtils.formatDateTime(context, date.getTime(), 21);
    }

    private int addUidAndGenerateRequestCode(int i) {
        if (this.mUidRequestCodeMap.containsKey(Integer.valueOf(i))) {
            return this.mUidRequestCodeMap.get(Integer.valueOf(i)).intValue();
        }
        int size = this.mUidRequestCodeMap.size() + 1;
        this.mUidRequestCodeMap.put(Integer.valueOf(i), Integer.valueOf(size));
        return size;
    }

    private int getRequestCodeByUid(int i) {
        if (!this.mUidRequestCodeMap.containsKey(Integer.valueOf(i))) {
            return -1;
        }
        return this.mUidRequestCodeMap.get(Integer.valueOf(i)).intValue();
    }
}
