package com.oneplus.accountsdk.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;

final class a {
    static void a(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.oneplus.account.action.info.page");
        intent.putExtra("extra_request_from", "oneplus_sdk");
        intent.putExtra("extra_package_name", context.getPackageName());
        if (!(context instanceof Activity)) {
            intent.setFlags(268435456);
        }
        try {
            context.startActivity(intent);
        } catch (Exception unused) {
        }
    }

    static void a(String[] strArr, Context context, int i) {
        Intent intent = new Intent();
        intent.putExtra("flag", "");
        intent.setAction("com.oneplus.account.action.login");
        intent.putExtra("extra_request_from", "oneplus_sdk");
        intent.putExtra("extra_package_name", context.getPackageName());
        intent.putExtra("extra_bind_info_array", strArr);
        if (!(context instanceof Activity)) {
            intent.setFlags(268435456);
        }
        try {
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, i, null);
            } else {
                context.startActivity(intent);
            }
        } catch (Exception unused) {
        }
    }

    static boolean b(Context context) {
        return Build.VERSION.SDK_INT < 26 && ContextCompat.checkSelfPermission(context, "android.permission.GET_ACCOUNTS") != 0;
    }
}
