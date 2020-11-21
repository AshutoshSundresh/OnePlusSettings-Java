package com.oneplus.accountsdk.auth;

import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.AsyncTask;
import android.os.Bundle;
import com.oneplus.accountsdk.utils.OnePlusAuthLogUtils;
import java.io.IOException;

public final class c extends AsyncTask<AccountManagerFuture<Bundle>, Void, Bundle> {
    a a;

    public interface a {
        void a();

        void a(Bundle bundle);
    }

    c() {
    }

    private static Bundle a(AccountManagerFuture<Bundle>... accountManagerFutureArr) {
        try {
            return accountManagerFutureArr[0].getResult();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        } catch (OperationCanceledException e3) {
            e3.printStackTrace();
            return null;
        }
    }

    /* Return type fixed from 'java.lang.Object' to match base method */
    /* JADX DEBUG: Method arguments types fixed to match base method, original types: [java.lang.Object[]] */
    /* access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public final /* synthetic */ Bundle doInBackground(AccountManagerFuture<Bundle>[] accountManagerFutureArr) {
        return a(accountManagerFutureArr);
    }

    /* JADX DEBUG: Method arguments types fixed to match base method, original types: [java.lang.Object] */
    /* access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public final /* synthetic */ void onPostExecute(Bundle bundle) {
        Bundle bundle2 = bundle;
        super.onPostExecute(bundle2);
        if (this.a == null) {
            OnePlusAuthLogUtils.e("listener is null", new Object[0]);
        }
        a aVar = this.a;
        if (bundle2 != null) {
            aVar.a(bundle2);
        } else {
            aVar.a();
        }
    }
}
