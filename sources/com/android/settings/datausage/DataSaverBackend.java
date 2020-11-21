package com.android.settings.datausage;

import android.content.Context;
import android.net.INetworkPolicyListener;
import android.net.NetworkPolicyManager;
import android.os.RemoteException;
import android.telephony.SubscriptionPlan;
import android.util.SparseIntArray;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;

public class DataSaverBackend {
    private boolean mBlacklistInitialized;
    private final Context mContext;
    private final ArrayList<Listener> mListeners = new ArrayList<>();
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final INetworkPolicyListener mPolicyListener = new INetworkPolicyListener.Stub() {
        /* class com.android.settings.datausage.DataSaverBackend.AnonymousClass1 */

        public void onMeteredIfacesChanged(String[] strArr) throws RemoteException {
        }

        public void onSubscriptionOverride(int i, int i2, int i3) {
        }

        public void onSubscriptionPlansChanged(int i, SubscriptionPlan[] subscriptionPlanArr) {
        }

        public void onUidRulesChanged(int i, int i2) throws RemoteException {
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onUidPoliciesChanged$0 */
        public /* synthetic */ void lambda$onUidPoliciesChanged$0$DataSaverBackend$1(int i, int i2) {
            DataSaverBackend.this.handleUidPoliciesChanged(i, i2);
        }

        public void onUidPoliciesChanged(int i, int i2) {
            ThreadUtils.postOnMainThread(new Runnable(i, i2) {
                /* class com.android.settings.datausage.$$Lambda$DataSaverBackend$1$ZKxRzvcfxNqOKXdNkDOsoxL7i9I */
                public final /* synthetic */ int f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    DataSaverBackend.AnonymousClass1.this.lambda$onUidPoliciesChanged$0$DataSaverBackend$1(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onRestrictBackgroundChanged$1 */
        public /* synthetic */ void lambda$onRestrictBackgroundChanged$1$DataSaverBackend$1(boolean z) {
            DataSaverBackend.this.handleRestrictBackgroundChanged(z);
        }

        public void onRestrictBackgroundChanged(boolean z) throws RemoteException {
            ThreadUtils.postOnMainThread(new Runnable(z) {
                /* class com.android.settings.datausage.$$Lambda$DataSaverBackend$1$1851XOwRm2qYDEpp81v4WIVwIHs */
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    DataSaverBackend.AnonymousClass1.this.lambda$onRestrictBackgroundChanged$1$DataSaverBackend$1(this.f$1);
                }
            });
        }
    };
    private final NetworkPolicyManager mPolicyManager;
    private SparseIntArray mUidPolicies = new SparseIntArray();
    private boolean mWhitelistInitialized;

    public interface Listener {
        void onBlacklistStatusChanged(int i, boolean z);

        void onDataSaverChanged(boolean z);

        void onWhitelistStatusChanged(int i, boolean z);
    }

    public DataSaverBackend(Context context) {
        this.mContext = context;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mPolicyManager = NetworkPolicyManager.from(context);
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
        if (this.mListeners.size() == 1) {
            this.mPolicyManager.registerListener(this.mPolicyListener);
        }
        listener.onDataSaverChanged(isDataSaverEnabled());
    }

    public void remListener(Listener listener) {
        this.mListeners.remove(listener);
        if (this.mListeners.size() == 0) {
            this.mPolicyManager.unregisterListener(this.mPolicyListener);
        }
    }

    public boolean isDataSaverEnabled() {
        return this.mPolicyManager.getRestrictBackground();
    }

    public void setDataSaverEnabled(boolean z) {
        this.mPolicyManager.setRestrictBackground(z);
        this.mMetricsFeatureProvider.action(this.mContext, 394, z ? 1 : 0);
    }

    public void refreshWhitelist() {
        loadWhitelist();
    }

    public void setIsWhitelisted(int i, String str, boolean z) {
        int i2 = z ? 4 : 0;
        this.mPolicyManager.setUidPolicy(i, i2);
        this.mUidPolicies.put(i, i2);
        if (z) {
            this.mMetricsFeatureProvider.action(this.mContext, 395, str);
        }
    }

    public boolean isWhitelisted(int i) {
        loadWhitelist();
        return this.mUidPolicies.get(i, 0) == 4;
    }

    private void loadWhitelist() {
        if (!this.mWhitelistInitialized) {
            for (int i : this.mPolicyManager.getUidsWithPolicy(4)) {
                this.mUidPolicies.put(i, 4);
            }
            this.mWhitelistInitialized = true;
        }
    }

    public void refreshBlacklist() {
        loadBlacklist();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r4v0, resolved type: boolean */
    /* JADX WARN: Multi-variable type inference failed */
    public void setIsBlacklisted(int i, String str, boolean z) {
        this.mPolicyManager.setUidPolicy(i, z ? 1 : 0);
        this.mUidPolicies.put(i, z);
        if (z != 0) {
            this.mMetricsFeatureProvider.action(this.mContext, 396, str);
        }
    }

    public boolean isBlacklisted(int i) {
        loadBlacklist();
        return this.mUidPolicies.get(i, 0) == 1;
    }

    private void loadBlacklist() {
        if (!this.mBlacklistInitialized) {
            for (int i : this.mPolicyManager.getUidsWithPolicy(1)) {
                this.mUidPolicies.put(i, 1);
            }
            this.mBlacklistInitialized = true;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleRestrictBackgroundChanged(boolean z) {
        for (int i = 0; i < this.mListeners.size(); i++) {
            this.mListeners.get(i).onDataSaverChanged(z);
        }
    }

    private void handleWhitelistChanged(int i, boolean z) {
        for (int i2 = 0; i2 < this.mListeners.size(); i2++) {
            this.mListeners.get(i2).onWhitelistStatusChanged(i, z);
        }
    }

    private void handleBlacklistChanged(int i, boolean z) {
        for (int i2 = 0; i2 < this.mListeners.size(); i2++) {
            this.mListeners.get(i2).onBlacklistStatusChanged(i, z);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleUidPoliciesChanged(int i, int i2) {
        loadWhitelist();
        loadBlacklist();
        boolean z = false;
        int i3 = this.mUidPolicies.get(i, 0);
        if (i2 == 0) {
            this.mUidPolicies.delete(i);
        } else {
            this.mUidPolicies.put(i, i2);
        }
        boolean z2 = i3 == 4;
        boolean z3 = i3 == 1;
        boolean z4 = i2 == 4;
        if (i2 == 1) {
            z = true;
        }
        if (z2 != z4) {
            handleWhitelistChanged(i, z4);
        }
        if (z3 != z) {
            handleBlacklistChanged(i, z);
        }
    }
}
