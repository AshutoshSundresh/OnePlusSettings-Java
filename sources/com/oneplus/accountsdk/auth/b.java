package com.oneplus.accountsdk.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import com.oneplus.accountsdk.auth.c;
import java.util.concurrent.Executors;

public final class b {
    static void a(AccountManagerFuture accountManagerFuture, c.a aVar) {
        c cVar = new c();
        cVar.executeOnExecutor(Executors.newSingleThreadExecutor(), accountManagerFuture);
        cVar.a = aVar;
    }

    protected static void a(Context context, c.a aVar) {
        if (context != null) {
            Account[] accountsByType = AccountManager.get(context).getAccountsByType("com.oneplus.account");
            if (accountsByType.length > 0) {
                a(AccountManager.get(context).getAuthToken(accountsByType[0], "com.oneplus.account", (Bundle) null, (Activity) null, (AccountManagerCallback<Bundle>) null, (Handler) null), aVar);
            } else if (aVar != null) {
                aVar.a();
            }
        } else if (aVar != null) {
            aVar.a();
        }
    }

    protected static Account[] a(Context context) {
        return AccountManager.get(context).getAccountsByType("com.oneplus.account");
    }
}
