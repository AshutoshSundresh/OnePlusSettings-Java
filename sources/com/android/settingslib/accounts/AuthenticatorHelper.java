package com.android.settingslib.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncAdapterType;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class AuthenticatorHelper extends BroadcastReceiver {
    private final Map<String, Drawable> mAccTypeIconCache = new HashMap();
    private final HashMap<String, ArrayList<String>> mAccountTypeToAuthorities = new HashMap<>();
    private final Context mContext;
    private final ArrayList<String> mEnabledAccountTypes = new ArrayList<>();
    private final OnAccountsUpdateListener mListener;
    private boolean mListeningToAccountUpdates;
    private final Map<String, AuthenticatorDescription> mTypeToAuthDescription = new HashMap();
    private final UserHandle mUserHandle;

    public interface OnAccountsUpdateListener {
        void onAccountsUpdate(UserHandle userHandle);
    }

    public AuthenticatorHelper(Context context, UserHandle userHandle, OnAccountsUpdateListener onAccountsUpdateListener) {
        this.mContext = context;
        this.mUserHandle = userHandle;
        this.mListener = onAccountsUpdateListener;
        onAccountsUpdated(null);
    }

    public String[] getEnabledAccountTypes() {
        ArrayList<String> arrayList = this.mEnabledAccountTypes;
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public void preloadDrawableForType(final Context context, final String str) {
        new AsyncTask<Void, Void, Void>() {
            /* class com.android.settingslib.accounts.AuthenticatorHelper.AnonymousClass1 */

            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                AuthenticatorHelper.this.getDrawableForType(context, str);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001d, code lost:
        if (r5.mTypeToAuthDescription.containsKey(r7) == false) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001f, code lost:
        r0 = r5.mTypeToAuthDescription.get(r7);
        r1 = r5.mContext.getPackageManager().getUserBadgedIcon(r6.createPackageContextAsUser(r0.packageName, 0, r5.mUserHandle).getDrawable(r0.iconId), r5.mUserHandle);
        r0 = r5.mAccTypeIconCache;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0044, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r5.mAccTypeIconCache.put(r7, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004a, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0016, code lost:
        r1 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.drawable.Drawable getDrawableForType(android.content.Context r6, java.lang.String r7) {
        /*
        // Method dump skipped, instructions count: 101
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.accounts.AuthenticatorHelper.getDrawableForType(android.content.Context, java.lang.String):android.graphics.drawable.Drawable");
    }

    public CharSequence getLabelForType(Context context, String str) {
        if (this.mTypeToAuthDescription.containsKey(str)) {
            try {
                AuthenticatorDescription authenticatorDescription = this.mTypeToAuthDescription.get(str);
                return context.createPackageContextAsUser(authenticatorDescription.packageName, 0, this.mUserHandle).getResources().getText(authenticatorDescription.labelId);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("AuthenticatorHelper", "No label name for account type " + str);
            } catch (Resources.NotFoundException unused2) {
                Log.w("AuthenticatorHelper", "No label icon for account type " + str);
            }
        }
        return null;
    }

    public String getPackageForType(String str) {
        if (this.mTypeToAuthDescription.containsKey(str)) {
            return this.mTypeToAuthDescription.get(str).packageName;
        }
        return null;
    }

    public int getLabelIdForType(String str) {
        if (this.mTypeToAuthDescription.containsKey(str)) {
            return this.mTypeToAuthDescription.get(str).labelId;
        }
        return -1;
    }

    public void updateAuthDescriptions(Context context) {
        AuthenticatorDescription[] authenticatorTypesAsUser = AccountManager.get(context).getAuthenticatorTypesAsUser(this.mUserHandle.getIdentifier());
        for (int i = 0; i < authenticatorTypesAsUser.length; i++) {
            this.mTypeToAuthDescription.put(authenticatorTypesAsUser[i].type, authenticatorTypesAsUser[i]);
        }
    }

    public boolean containsAccountType(String str) {
        return this.mTypeToAuthDescription.containsKey(str);
    }

    public AuthenticatorDescription getAccountTypeDescription(String str) {
        return this.mTypeToAuthDescription.get(str);
    }

    /* access modifiers changed from: package-private */
    public void onAccountsUpdated(Account[] accountArr) {
        updateAuthDescriptions(this.mContext);
        if (accountArr == null) {
            accountArr = AccountManager.get(this.mContext).getAccountsAsUser(this.mUserHandle.getIdentifier());
        }
        this.mEnabledAccountTypes.clear();
        this.mAccTypeIconCache.clear();
        for (Account account : accountArr) {
            if ("com.oneplus.account".equals(account.type)) {
                Log.v("AuthenticatorHelper", "Ignore OnePlus account entry point");
            } else if (!this.mEnabledAccountTypes.contains(account.type)) {
                this.mEnabledAccountTypes.add(account.type);
            }
        }
        buildAccountTypeToAuthoritiesMap();
        if (this.mListeningToAccountUpdates) {
            this.mListener.onAccountsUpdate(this.mUserHandle);
        }
    }

    public void onReceive(Context context, Intent intent) {
        onAccountsUpdated(AccountManager.get(this.mContext).getAccountsAsUser(this.mUserHandle.getIdentifier()));
    }

    public void listenToAccountUpdates() {
        if (!this.mListeningToAccountUpdates) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.accounts.LOGIN_ACCOUNTS_CHANGED");
            intentFilter.addAction("android.intent.action.DEVICE_STORAGE_OK");
            this.mContext.registerReceiverAsUser(this, this.mUserHandle, intentFilter, null, null);
            this.mListeningToAccountUpdates = true;
        }
    }

    public void stopListeningToAccountUpdates() {
        if (this.mListeningToAccountUpdates) {
            this.mContext.unregisterReceiver(this);
            this.mListeningToAccountUpdates = false;
        }
    }

    public ArrayList<String> getAuthoritiesForAccountType(String str) {
        return this.mAccountTypeToAuthorities.get(str);
    }

    private void buildAccountTypeToAuthoritiesMap() {
        this.mAccountTypeToAuthorities.clear();
        SyncAdapterType[] syncAdapterTypesAsUser = ContentResolver.getSyncAdapterTypesAsUser(this.mUserHandle.getIdentifier());
        for (SyncAdapterType syncAdapterType : syncAdapterTypesAsUser) {
            ArrayList<String> arrayList = this.mAccountTypeToAuthorities.get(syncAdapterType.accountType);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mAccountTypeToAuthorities.put(syncAdapterType.accountType, arrayList);
            }
            if (Log.isLoggable("AuthenticatorHelper", 2)) {
                Log.v("AuthenticatorHelper", "Added authority " + syncAdapterType.authority + " to accountType " + syncAdapterType.accountType);
            }
            arrayList.add(syncAdapterType.authority);
        }
    }
}
