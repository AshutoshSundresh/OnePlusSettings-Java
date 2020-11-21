package com.android.settings.accounts;

import android.accounts.Account;
import android.content.Context;

public class AccountFeatureProviderImpl implements AccountFeatureProvider {
    @Override // com.android.settings.accounts.AccountFeatureProvider
    public String getAccountType() {
        return null;
    }

    @Override // com.android.settings.accounts.AccountFeatureProvider
    public Account[] getAccounts(Context context) {
        return new Account[0];
    }
}
