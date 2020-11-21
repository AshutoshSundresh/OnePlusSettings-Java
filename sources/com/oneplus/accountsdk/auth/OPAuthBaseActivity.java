package com.oneplus.accountsdk.auth;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.oneplus.accountsdk.auth.OPAuthWebView;
import com.oneplus.accountsdk.auth.c;
import com.oneplus.accountsdk.utils.OnePlusAuthDeviceIdUtils;
import com.oneplus.accountsdk.utils.OnePlusAuthLogUtils;
import java.lang.ref.WeakReference;
import org.jetbrains.annotations.Nullable;

public class OPAuthBaseActivity extends Activity implements OPAuthWebView.c {
    private static final String TAG = OPAuthBaseActivity.class.getSimpleName();
    private boolean isShowChooseIntent;
    private BroadcastReceiver mAccountBroadcastReceiver = new BroadcastReceiver() {
        /* class com.oneplus.accountsdk.auth.OPAuthBaseActivity.AnonymousClass1 */

        public final void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String unused = OPAuthBaseActivity.TAG;
            if ("com.onplus.account.login.broadcast".equals(action)) {
                if (!OPAuthBaseActivity.this.isShowChooseIntent) {
                    OPAuthBaseActivity oPAuthBaseActivity = OPAuthBaseActivity.this;
                    if (OPAuth.getAccessAccountPremission(oPAuthBaseActivity, 2, oPAuthBaseActivity.mBindArray)) {
                        b.a(OPAuthBaseActivity.this.getApplicationContext(), OPAuthBaseActivity.this.mListener);
                    }
                }
                OPAuthBaseActivity.this.isShowChooseIntent = true;
            } else if ("com.onplus.account.cancel.broadcast".equals(action)) {
                if (OPAuthBaseActivity.this.mResponse != null) {
                    OPAuthResponse.a(OPAuthBaseActivity.this.mResponse.a);
                }
                OPAuthBaseActivity.this.finish();
            }
        }
    };
    private String[] mBindArray;
    private Handler mHandler;
    private c.a mListener;
    private OPAuthResponse mResponse;
    private WeakReference mWeakResponse;
    private OPAuthWebView mWebView;

    private int getOPAccountVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.oneplus.account", 0);
            if (applicationInfo == null || applicationInfo.enabled) {
                return packageManager.getPackageInfo("com.oneplus.account", 0).versionCode;
            }
            return -1;
        } catch (Exception unused) {
            return -1;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleOnCancel() {
        if (this.mResponse == null) {
            return;
        }
        if (getOPAccountVersionCode(getApplicationContext()) < 0) {
            Bundle bundle = new Bundle();
            bundle.putString("extra_statusCode", "2004");
            this.mResponse.a(bundle);
            return;
        }
        OPAuthResponse.a(this.mResponse.a);
    }

    private void initAccountBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.onplus.account.login.broadcast");
        intentFilter.addAction("com.oneplus.account.bind.info");
        intentFilter.addAction("com.onplus.account.cancel.broadcast");
        registerReceiver(this.mAccountBroadcastReceiver, intentFilter);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        StringBuilder sb = new StringBuilder("onActivityResult: requestCode = ");
        sb.append(i);
        sb.append(" resultCode = ");
        sb.append(i2);
        sb.append(" data = ");
        sb.append(intent != null ? intent.toString() : null);
        if (i == 3 && i2 == 1) {
            if (!this.isShowChooseIntent && OPAuth.getAccessAccountPremission(this, 2, this.mBindArray)) {
                b.a(getApplicationContext(), this.mListener);
            }
            this.isShowChooseIntent = true;
        }
        if (i == 2 && i2 == -1) {
            b.a(getApplicationContext(), this.mListener);
        } else if (i2 == 0 && (i == 2 || i == 3)) {
            Handler handler = new Handler(Looper.getMainLooper());
            this.mHandler = handler;
            handler.postDelayed(new Runnable() {
                /* class com.oneplus.accountsdk.auth.OPAuthBaseActivity.AnonymousClass3 */

                public final void run() {
                    OPAuthBaseActivity.this.handleOnCancel();
                    OPAuthBaseActivity.this.finish();
                }
            }, 800);
        }
        super.onActivityResult(i, i2, intent);
    }

    public void onBackPressed() {
        if (this.mWebView == null) {
            OPAuthResponse oPAuthResponse = this.mResponse;
            if (oPAuthResponse != null) {
                OPAuthResponse.a(oPAuthResponse.a);
            }
            super.onBackPressed();
            return;
        }
        new StringBuilder("onBackPressed: ").append(this.mWebView.canGoBack());
        if (!this.mWebView.onBackPressed()) {
            OPAuthResponse oPAuthResponse2 = this.mResponse;
            if (oPAuthResponse2 != null) {
                OPAuthResponse.a(oPAuthResponse2.a);
            }
            super.onBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra("extra_bundle");
        if (bundleExtra != null) {
            bundleExtra.setClassLoader(getClass().getClassLoader());
            WeakReference weakReference = new WeakReference(bundleExtra.getParcelable("response"));
            this.mWeakResponse = weakReference;
            this.mResponse = (OPAuthResponse) weakReference.get();
        }
        this.mBindArray = intent.getStringArrayExtra("extra_bind_info_array");
        if (intent.getBooleanExtra("extra_middle", false)) {
            this.mListener = new c.a() {
                /* class com.oneplus.accountsdk.auth.OPAuthBaseActivity.AnonymousClass2 */

                @Override // com.oneplus.accountsdk.auth.c.a
                public final void a() {
                    OPAuthBaseActivity.this.handleOnCancel();
                    OPAuthBaseActivity.this.finish();
                }

                @Override // com.oneplus.accountsdk.auth.c.a
                public final void a(Bundle bundle) {
                    if (OPAuthBaseActivity.this.mResponse != null) {
                        OPAuthBaseActivity.this.mResponse.a(bundle);
                    }
                    OPAuthBaseActivity.this.finish();
                }
            };
            if (b.a(getApplicationContext()).length > 0) {
                b.a(getApplicationContext(), this.mListener);
            } else if (a.b(getApplicationContext())) {
                OnePlusAuthLogUtils.e("Missing permission", new Object[0]);
            } else {
                OPAuth.getAccessAccountPremission(this, 3, this.mBindArray);
            }
            initAccountBroadcastReceiver();
            return;
        }
        this.mAccountBroadcastReceiver = null;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        BroadcastReceiver broadcastReceiver = this.mAccountBroadcastReceiver;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        OPAuthWebView oPAuthWebView = this.mWebView;
        if (oPAuthWebView != null) {
            oPAuthWebView.removeJavascriptInterface("SignCallback");
            this.mWebView.removeJavascriptInterface("JSBridge");
            this.mWebView.removeJavascriptInterface("Retry");
            this.mWebView.onDestroy();
        }
        this.mResponse = null;
        super.onDestroy();
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override // com.oneplus.accountsdk.auth.OPAuthWebView.c
    public void onShowError() {
        String language = OnePlusAuthDeviceIdUtils.getLanguage();
        this.mWebView.loadUrl((language.contains("Hans") || language.equals("zh_CN")) ? "file:///android_res/raw/oneplus_auth_errorpage_zh_cn.html" : (language.contains("Hant") || language.equals("zh_TW")) ? "file:///android_res/raw/oneplus_auth_errorpage_zh_tw.html" : "file:///android_res/raw/oneplus_auth_errorpage_en.html");
    }
}
