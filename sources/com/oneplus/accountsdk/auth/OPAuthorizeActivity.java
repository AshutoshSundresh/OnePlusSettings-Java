package com.oneplus.accountsdk.auth;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class OPAuthorizeActivity extends OPAuthBaseActivity {
    /* access modifiers changed from: protected */
    @Override // com.oneplus.accountsdk.auth.OPAuthBaseActivity
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        new StringBuilder("onCreate: ").append(Build.VERSION.SDK_INT);
    }
}
