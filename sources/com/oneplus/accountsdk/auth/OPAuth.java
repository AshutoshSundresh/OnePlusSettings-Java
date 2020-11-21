package com.oneplus.accountsdk.auth;

import android.app.Activity;
import android.content.Context;
import com.oneplus.accountsdk.b;
import com.oneplus.accountsdk.entity.UserBindInfo;

public class OPAuth {
    private static g mAuth = new e();

    private static void checkContextNotNull(Context context) {
        if (context == null) {
            throw new NullPointerException("Please check context, it must not be null");
        }
    }

    protected static boolean getAccessAccountPremission(Activity activity, int i, String[] strArr) {
        return mAuth.a(activity, i, strArr);
    }

    protected static void sendBindResult(UserBindInfo userBindInfo) {
        mAuth.a(userBindInfo);
    }

    public static void startAccountSettingsActivity(Context context) {
        checkContextNotNull(context);
        b.a = context.getApplicationContext();
        mAuth.b(context);
    }
}
