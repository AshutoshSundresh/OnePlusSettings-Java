package com.oneplus.accountsdk.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.oneplus.accountsdk.entity.UserBindInfo;

public class OPAuthOperateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String a;
        if ("com.oneplus.account.bind.info".equals(intent.getAction()) && intent != null) {
            try {
                boolean booleanExtra = intent.getBooleanExtra("extra_bind_result", false);
                String stringExtra = intent.getStringExtra("extra_error_code");
                UserBindInfo userBindInfo = new UserBindInfo();
                userBindInfo.bindSuccess = booleanExtra;
                if (booleanExtra) {
                    userBindInfo.resultCode = "5000";
                    a = f.a("5000");
                } else {
                    userBindInfo.resultCode = stringExtra;
                    a = f.a(stringExtra);
                }
                userBindInfo.resultMsg = a;
                OPAuth.sendBindResult(userBindInfo);
            } catch (Exception unused) {
            }
        }
    }
}
