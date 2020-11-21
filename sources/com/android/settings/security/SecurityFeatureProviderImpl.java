package com.android.settings.security;

import android.content.Context;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.security.trustagent.TrustAgentManager;

public class SecurityFeatureProviderImpl implements SecurityFeatureProvider {
    private LockPatternUtils mLockPatternUtils;
    private TrustAgentManager mTrustAgentManager;

    @Override // com.android.settings.security.SecurityFeatureProvider
    public TrustAgentManager getTrustAgentManager() {
        if (this.mTrustAgentManager == null) {
            this.mTrustAgentManager = new TrustAgentManager();
        }
        return this.mTrustAgentManager;
    }

    @Override // com.android.settings.security.SecurityFeatureProvider
    public LockPatternUtils getLockPatternUtils(Context context) {
        if (this.mLockPatternUtils == null) {
            this.mLockPatternUtils = new LockPatternUtils(context.getApplicationContext());
        }
        return this.mLockPatternUtils;
    }
}
