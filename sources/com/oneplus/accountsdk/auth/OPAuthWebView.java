package com.oneplus.accountsdk.auth;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.oneplus.accountsdk.utils.OnePlusAuthNetWorkUtils;

public class OPAuthWebView extends WebView {
    private c mCallback;

    /* access modifiers changed from: package-private */
    public class a extends WebChromeClient {
        a(OPAuthWebView oPAuthWebView) {
        }

        public final void onProgressChanged(WebView webView, int i) {
            super.onProgressChanged(webView, i);
        }
    }

    /* access modifiers changed from: package-private */
    public class b extends WebViewClient {
        b() {
        }

        public final void onReceivedError(WebView webView, int i, String str, String str2) {
            StringBuilder sb = new StringBuilder("onReceivedError: ");
            sb.append(str2);
            sb.append(" errorCode = ");
            sb.append(i);
            sb.append(" description = ");
            sb.append(str2);
            if (Build.VERSION.SDK_INT <= 23) {
                if (!OnePlusAuthNetWorkUtils.isNetworkConnected(com.oneplus.accountsdk.b.a) && !str2.contains("file:///android_res/raw")) {
                    OPAuthWebView.this.mCallback.onShowError();
                }
                super.onReceivedError(webView, i, str, str2);
            }
        }

        @TargetApi(23)
        public final void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
            StringBuilder sb = new StringBuilder("onReceivedError > M: ");
            sb.append(webResourceRequest.getUrl());
            sb.append(" error = ");
            sb.append(webResourceError.getErrorCode());
            sb.append(" description = ");
            sb.append((Object) webResourceError.getDescription());
            if (!OnePlusAuthNetWorkUtils.isNetworkConnected(com.oneplus.accountsdk.b.a) && !webResourceRequest.getUrl().toString().contains("file:///android_res/raw")) {
                OPAuthWebView.this.mCallback.onShowError();
            }
            super.onReceivedError(webView, webResourceRequest, webResourceError);
        }

        @Override // android.webkit.WebViewClient
        public final boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
            return false;
        }

        @Override // android.webkit.WebViewClient
        public final boolean shouldOverrideUrlLoading(WebView webView, String str) {
            return false;
        }
    }

    public interface c {
        void onShowError();
    }

    public OPAuthWebView(Context context) {
        super(context);
        init();
    }

    public OPAuthWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public OPAuthWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    @RequiresApi(api = 21)
    public OPAuthWebView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private boolean checkisErrorPageUrl(String str) {
        return str != null && str.startsWith("file:///android_res/raw");
    }

    private void init() {
        getSettings().setTextZoom(100);
        getSettings().setCacheMode(2);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setAllowFileAccess(false);
        getSettings().setSavePassword(false);
        getSettings().setSaveFormData(false);
        setWebChromeClient(new a(this));
        setWebViewClient(new b());
    }

    public boolean onBackPressed() {
        if (!canGoBack() || checkisErrorPageUrl(getUrl())) {
            return false;
        }
        goBack();
        return true;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:2:0x0009 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDestroy() {
        /*
            r1 = this;
            android.view.ViewParent r0 = r1.getParent()     // Catch:{ Exception -> 0x0009 }
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0     // Catch:{ Exception -> 0x0009 }
            r0.removeView(r1)     // Catch:{ Exception -> 0x0009 }
        L_0x0009:
            r1.removeAllViews()     // Catch:{ Exception -> 0x000c }
        L_0x000c:
            r1.destroy()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.accountsdk.auth.OPAuthWebView.onDestroy():void");
    }

    @SuppressLint({"NewApi"})
    public void onPause() {
        pauseTimers();
        if (Build.VERSION.SDK_INT >= 11) {
            super.onPause();
        }
    }

    @SuppressLint({"NewApi"})
    public void onResume() {
        if (Build.VERSION.SDK_INT >= 11) {
            super.onResume();
        }
        resumeTimers();
    }

    public void setCallback(c cVar) {
        this.mCallback = cVar;
    }
}
