package com.android.settings.accounts;

import android.accounts.Account;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.C0005R$bool;
import com.android.settings.C0008R$drawable;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.utils.ThreadUtils;
import java.util.List;

public class AvatarViewMixin implements LifecycleObserver {
    static final Intent INTENT_GET_ACCOUNT_DATA = new Intent("android.content.action.SETTINGS_ACCOUNT_DATA");
    String mAccountName;
    private final ActivityManager mActivityManager;
    private final MutableLiveData<Bitmap> mAvatarImage;
    private final ImageView mAvatarView;
    private final Context mContext;

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (!this.mContext.getResources().getBoolean(C0005R$bool.config_show_avatar_in_homepage)) {
            Log.d("AvatarViewMixin", "Feature disabled by config. Skipping");
        } else if (this.mActivityManager.isLowRamDevice()) {
            Log.d("AvatarViewMixin", "Feature disabled on low ram device. Skipping");
        } else if (hasAccount()) {
            loadAccount();
        } else {
            this.mAccountName = null;
            this.mAvatarView.setImageResource(C0008R$drawable.ic_account_circle_24dp);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasAccount() {
        Account[] accounts = FeatureFactory.getFactory(this.mContext).getAccountFeatureProvider().getAccounts(this.mContext);
        return accounts != null && accounts.length > 0;
    }

    private void loadAccount() {
        String queryProviderAuthority = queryProviderAuthority();
        if (!TextUtils.isEmpty(queryProviderAuthority)) {
            ThreadUtils.postOnBackgroundThread(new Runnable(queryProviderAuthority) {
                /* class com.android.settings.accounts.$$Lambda$AvatarViewMixin$_8nBC_LnKcD01q7LexZiPdLLSY */
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AvatarViewMixin.this.lambda$loadAccount$2$AvatarViewMixin(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAccount$2 */
    public /* synthetic */ void lambda$loadAccount$2$AvatarViewMixin(String str) {
        Bundle call = this.mContext.getContentResolver().call(new Uri.Builder().scheme("content").authority(str).build(), "getAccountAvatar", (String) null, (Bundle) null);
        this.mAccountName = call.getString("account_name", "");
        this.mAvatarImage.postValue((Bitmap) call.getParcelable("account_avatar"));
    }

    /* access modifiers changed from: package-private */
    public String queryProviderAuthority() {
        List<ResolveInfo> queryIntentContentProviders = this.mContext.getPackageManager().queryIntentContentProviders(INTENT_GET_ACCOUNT_DATA, 1048576);
        if (queryIntentContentProviders.size() == 1) {
            return queryIntentContentProviders.get(0).providerInfo.authority;
        }
        Log.w("AvatarViewMixin", "The size of the provider is " + queryIntentContentProviders.size());
        return null;
    }
}
