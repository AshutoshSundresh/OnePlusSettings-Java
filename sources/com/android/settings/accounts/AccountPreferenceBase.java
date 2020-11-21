package com.android.settings.accounts;

import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.format.DateFormat;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.utils.ThreadUtils;

/* access modifiers changed from: package-private */
public abstract class AccountPreferenceBase extends SettingsPreferenceFragment implements AuthenticatorHelper.OnAccountsUpdateListener {
    protected AuthenticatorHelper mAuthenticatorHelper;
    private Object mStatusChangeListenerHandle;
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /* class com.android.settings.accounts.$$Lambda$AccountPreferenceBase$duCjsGZhZVNysJ2Rj1t7N9PkFAY */

        public final void onStatusChanged(int i) {
            AccountPreferenceBase.this.lambda$new$1$AccountPreferenceBase(i);
        }
    };
    private UserManager mUm;
    protected UserHandle mUserHandle;

    @Override // com.android.settingslib.accounts.AuthenticatorHelper.OnAccountsUpdateListener
    public void onAccountsUpdate(UserHandle userHandle) {
    }

    /* access modifiers changed from: protected */
    public void onAuthDescriptionsUpdated() {
    }

    /* access modifiers changed from: protected */
    /* renamed from: onSyncStateUpdated */
    public void lambda$new$0() {
    }

    AccountPreferenceBase() {
    }

    static {
        Log.isLoggable("AccountPreferenceBase", 2);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mUm = (UserManager) getSystemService("user");
        FragmentActivity activity = getActivity();
        this.mUserHandle = Utils.getSecureTargetUser(activity.getActivityToken(), this.mUm, getArguments(), activity.getIntent().getExtras());
        this.mAuthenticatorHelper = new AuthenticatorHelper(activity, this.mUserHandle, this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        FragmentActivity activity = getActivity();
        DateFormat.getDateFormat(activity);
        DateFormat.getTimeFormat(activity);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mStatusChangeListenerHandle = ContentResolver.addStatusChangeListener(13, this.mSyncStatusObserver);
        lambda$new$0();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        ContentResolver.removeStatusChangeListener(this.mStatusChangeListenerHandle);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$AccountPreferenceBase(int i) {
        ThreadUtils.postOnMainThread(new Runnable() {
            /* class com.android.settings.accounts.$$Lambda$AccountPreferenceBase$7XBpqCguERDVZFsa_jC8V8rk8o8 */

            public final void run() {
                AccountPreferenceBase.this.lambda$new$0$AccountPreferenceBase();
            }
        });
    }

    public void updateAuthDescriptions() {
        this.mAuthenticatorHelper.updateAuthDescriptions(getActivity());
        onAuthDescriptionsUpdated();
    }

    /* access modifiers changed from: protected */
    public Drawable getDrawableForType(String str) {
        return this.mAuthenticatorHelper.getDrawableForType(getActivity(), str);
    }

    /* access modifiers changed from: protected */
    public CharSequence getLabelForType(String str) {
        return this.mAuthenticatorHelper.getLabelForType(getActivity(), str);
    }
}
