package com.android.settings.network.telephony;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.internal.util.CollectionUtils;
import com.android.settings.C0010R$id;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settings.network.SubscriptionUtil;
import java.util.List;

public class MobileNetworkActivity extends SettingsBaseActivity implements ProxySubscriptionManager.OnActiveSubscriptionChangedListener {
    static final String MOBILE_SETTINGS_TAG = "mobile_settings:";
    static final int SUB_ID_NULL = Integer.MIN_VALUE;
    private int mCurSubscriptionId;
    private boolean mFragmentForceReload = true;
    ProxySubscriptionManager mProxySubscriptionMgr;

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        validate(intent);
        setIntent(intent);
        int i = SUB_ID_NULL;
        if (intent != null) {
            i = intent.getIntExtra("android.provider.extra.SUB_ID", SUB_ID_NULL);
        }
        int i2 = this.mCurSubscriptionId;
        this.mCurSubscriptionId = i;
        this.mFragmentForceReload = i == i2;
        SubscriptionInfo subscription = getSubscription();
        updateSubscriptions(subscription);
        if (i != i2 || !doesIntentContainOptInAction(intent)) {
            removeContactDiscoveryDialog(i2);
        }
        if (doesIntentContainOptInAction(intent)) {
            maybeShowContactDiscoveryDialog(subscription);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.android.settings.core.SettingsBaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!((UserManager) getSystemService(UserManager.class)).isAdminUser()) {
            finish();
            return;
        }
        getProxySubscriptionManager().setLifecycle(getLifecycle());
        Intent intent = getIntent();
        validate(intent);
        int i = SUB_ID_NULL;
        if (bundle != null) {
            i = bundle.getInt("android.provider.extra.SUB_ID", SUB_ID_NULL);
        } else if (intent != null) {
            i = intent.getIntExtra("android.provider.extra.SUB_ID", SUB_ID_NULL);
        }
        this.mCurSubscriptionId = i;
        SubscriptionInfo subscription = getSubscription();
        maybeShowContactDiscoveryDialog(subscription);
        registerActiveSubscriptionsListener();
        updateSubscriptions(subscription);
    }

    /* access modifiers changed from: package-private */
    public ProxySubscriptionManager getProxySubscriptionManager() {
        if (this.mProxySubscriptionMgr == null) {
            this.mProxySubscriptionMgr = ProxySubscriptionManager.getInstance(this);
        }
        return this.mProxySubscriptionMgr;
    }

    /* access modifiers changed from: package-private */
    public void registerActiveSubscriptionsListener() {
        getProxySubscriptionManager().addActiveSubscriptionsListener(this);
    }

    @Override // com.android.settings.network.ProxySubscriptionManager.OnActiveSubscriptionChangedListener
    public void onChanged() {
        SubscriptionInfo subscription = getSubscription();
        int i = this.mCurSubscriptionId;
        updateSubscriptions(subscription);
        if (subscription != null && subscription.getSubscriptionId() != i) {
            removeContactDiscoveryDialog(i);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStart() {
        getProxySubscriptionManager().setLifecycle(getLifecycle());
        super.onStart();
        updateSubscriptions(getSubscription());
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
        ProxySubscriptionManager proxySubscriptionManager = this.mProxySubscriptionMgr;
        if (proxySubscriptionManager != null) {
            proxySubscriptionManager.removeActiveSubscriptionsListener(this);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        saveInstanceState(bundle);
    }

    /* access modifiers changed from: package-private */
    public void saveInstanceState(Bundle bundle) {
        bundle.putInt("android.provider.extra.SUB_ID", this.mCurSubscriptionId);
    }

    private void updateTitleAndNavigation(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo != null) {
            setTitle(subscriptionInfo.getDisplayName());
        }
    }

    /* access modifiers changed from: package-private */
    public void updateSubscriptions(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo != null) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            updateTitleAndNavigation(subscriptionInfo);
            switchFragment(subscriptionInfo);
            this.mCurSubscriptionId = subscriptionId;
            this.mFragmentForceReload = false;
        }
    }

    /* access modifiers changed from: package-private */
    public SubscriptionInfo getSubscription() {
        int i = this.mCurSubscriptionId;
        if (i != SUB_ID_NULL) {
            return getSubscriptionForSubId(i);
        }
        List<SubscriptionInfo> activeSubscriptionsInfo = getProxySubscriptionManager().getActiveSubscriptionsInfo();
        if (CollectionUtils.isEmpty(activeSubscriptionsInfo)) {
            return null;
        }
        return activeSubscriptionsInfo.get(0);
    }

    /* access modifiers changed from: package-private */
    public SubscriptionInfo getSubscriptionForSubId(int i) {
        return SubscriptionUtil.getAvailableSubscription(this, getProxySubscriptionManager(), i);
    }

    /* access modifiers changed from: package-private */
    public void switchFragment(SubscriptionInfo subscriptionInfo) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        Bundle bundle = new Bundle();
        bundle.putInt("android.provider.extra.SUB_ID", subscriptionId);
        String buildFragmentTag = buildFragmentTag(subscriptionId);
        if (supportFragmentManager.findFragmentByTag(buildFragmentTag) != null) {
            if (!this.mFragmentForceReload) {
                Log.d("MobileNetworkActivity", "Keep current fragment: " + buildFragmentTag);
                return;
            }
            Log.d("MobileNetworkActivity", "Construct fragment: " + buildFragmentTag);
        }
        MobileNetworkSettings mobileNetworkSettings = new MobileNetworkSettings();
        mobileNetworkSettings.setArguments(bundle);
        beginTransaction.replace(C0010R$id.content_frame, mobileNetworkSettings, buildFragmentTag);
        beginTransaction.commit();
    }

    private void removeContactDiscoveryDialog(int i) {
        ContactDiscoveryDialogFragment contactDiscoveryFragment = getContactDiscoveryFragment(i);
        if (contactDiscoveryFragment != null) {
            contactDiscoveryFragment.dismiss();
        }
    }

    private ContactDiscoveryDialogFragment getContactDiscoveryFragment(int i) {
        return (ContactDiscoveryDialogFragment) getSupportFragmentManager().findFragmentByTag(ContactDiscoveryDialogFragment.getFragmentTag(i));
    }

    private void maybeShowContactDiscoveryDialog(SubscriptionInfo subscriptionInfo) {
        CharSequence charSequence;
        int i;
        if (subscriptionInfo != null) {
            i = subscriptionInfo.getSubscriptionId();
            charSequence = subscriptionInfo.getDisplayName();
        } else {
            i = -1;
            charSequence = "";
        }
        boolean z = doesIntentContainOptInAction(getIntent()) && MobileNetworkUtils.isContactDiscoveryVisible(this, i) && !MobileNetworkUtils.isContactDiscoveryEnabled(this, i);
        ContactDiscoveryDialogFragment contactDiscoveryFragment = getContactDiscoveryFragment(i);
        if (z) {
            if (contactDiscoveryFragment == null) {
                contactDiscoveryFragment = ContactDiscoveryDialogFragment.newInstance(i, charSequence);
            }
            if (!contactDiscoveryFragment.isAdded()) {
                contactDiscoveryFragment.show(getSupportFragmentManager(), ContactDiscoveryDialogFragment.getFragmentTag(i));
            }
        }
    }

    private boolean doesIntentContainOptInAction(Intent intent) {
        return TextUtils.equals(intent != null ? intent.getAction() : null, "android.telephony.ims.action.SHOW_CAPABILITY_DISCOVERY_OPT_IN");
    }

    private void validate(Intent intent) {
        if (doesIntentContainOptInAction(intent) && SUB_ID_NULL == intent.getIntExtra("android.provider.extra.SUB_ID", SUB_ID_NULL)) {
            throw new IllegalArgumentException("Intent with action SHOW_CAPABILITY_DISCOVERY_OPT_IN must also include the extra Settings#EXTRA_SUB_ID");
        }
    }

    /* access modifiers changed from: package-private */
    public String buildFragmentTag(int i) {
        return MOBILE_SETTINGS_TAG + i;
    }
}
