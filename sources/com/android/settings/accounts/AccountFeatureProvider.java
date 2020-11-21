package com.android.settings.accounts;

import android.accounts.Account;
import android.content.Context;

public interface AccountFeatureProvider {
    String getAccountType();

    Account[] getAccounts(Context context);
}
