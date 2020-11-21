package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.drawer.Tile;
import java.util.ArrayList;
import java.util.List;

public class AccountDetailDashboardFragment extends DashboardFragment {
    Account mAccount;
    private String mAccountLabel;
    private AccountSyncPreferenceController mAccountSynController;
    String mAccountType;
    private RemoveAccountPreferenceController mRemoveAccountController;
    UserHandle mUserHandle;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AccountDetailDashboard";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 8;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getPreferenceManager().setPreferenceComparisonCallback(null);
        Bundle arguments = getArguments();
        FragmentActivity activity = getActivity();
        this.mUserHandle = Utils.getSecureTargetUser(activity.getActivityToken(), (UserManager) getSystemService("user"), arguments, activity.getIntent().getExtras());
        if (arguments != null) {
            if (arguments.containsKey("account")) {
                this.mAccount = (Account) arguments.getParcelable("account");
            }
            if (arguments.containsKey("account_label")) {
                this.mAccountLabel = arguments.getString("account_label");
            }
            if (arguments.containsKey("account_type")) {
                this.mAccountType = arguments.getString("account_type");
            }
        }
        this.mAccountSynController.init(this.mAccount, this.mUserHandle);
        this.mRemoveAccountController.init(this.mAccount, this.mUserHandle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mAccountLabel != null) {
            getActivity().setTitle(this.mAccountLabel);
        }
        updateUi();
    }

    /* access modifiers changed from: package-private */
    public void finishIfAccountMissing() {
        Context context = getContext();
        AccountManager accountManager = (AccountManager) context.getSystemService(AccountManager.class);
        for (UserHandle userHandle : ((UserManager) context.getSystemService(UserManager.class)).getUserProfiles()) {
            Account[] accountsAsUser = accountManager.getAccountsAsUser(userHandle.getIdentifier());
            int length = accountsAsUser.length;
            int i = 0;
            while (true) {
                if (i < length) {
                    if (!accountsAsUser[i].equals(this.mAccount)) {
                        i++;
                    } else {
                        return;
                    }
                }
            }
        }
        finish();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        finishIfAccountMissing();
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_account_detail;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.account_type_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        AccountSyncPreferenceController accountSyncPreferenceController = new AccountSyncPreferenceController(context);
        this.mAccountSynController = accountSyncPreferenceController;
        arrayList.add(accountSyncPreferenceController);
        RemoveAccountPreferenceController removeAccountPreferenceController = new RemoveAccountPreferenceController(context, this);
        this.mRemoveAccountController = removeAccountPreferenceController;
        arrayList.add(removeAccountPreferenceController);
        arrayList.add(new AccountHeaderPreferenceController(context, getSettingsLifecycle(), getActivity(), this, getArguments()));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public boolean displayTile(Tile tile) {
        Bundle metaData;
        if (!super.displayTile(tile) || this.mAccountType == null || (metaData = tile.getMetaData()) == null) {
            return false;
        }
        boolean equals = this.mAccountType.equals(metaData.getString("com.android.settings.ia.account"));
        if (equals) {
            Intent intent = tile.getIntent();
            intent.putExtra("extra.accountName", this.mAccount.name);
            intent.putExtra("android.intent.extra.USER", this.mUserHandle);
        }
        return equals;
    }

    /* access modifiers changed from: package-private */
    public void updateUi() {
        Context context = getContext();
        Bundle arguments = getArguments();
        UserHandle userHandle = (arguments == null || !arguments.containsKey("user_handle")) ? null : (UserHandle) arguments.getParcelable("user_handle");
        AccountTypePreferenceLoader accountTypePreferenceLoader = new AccountTypePreferenceLoader(this, new AuthenticatorHelper(context, userHandle, null), userHandle);
        PreferenceScreen addPreferencesForType = accountTypePreferenceLoader.addPreferencesForType(this.mAccountType, getPreferenceScreen());
        if (addPreferencesForType != null) {
            accountTypePreferenceLoader.updatePreferenceIntents(addPreferencesForType, this.mAccountType, this.mAccount);
        }
    }
}
