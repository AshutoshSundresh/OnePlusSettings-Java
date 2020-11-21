package com.android.settings.webview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.webkit.UserPackage;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.defaultapps.DefaultAppPickerFragment;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.ArrayList;
import java.util.List;

public class WebViewAppPicker extends DefaultAppPickerFragment {
    private WebViewUpdateServiceWrapper mWebViewUpdateServiceWrapper;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 405;
    }

    private WebViewUpdateServiceWrapper getWebViewUpdateServiceWrapper() {
        if (this.mWebViewUpdateServiceWrapper == null) {
            setWebViewUpdateServiceWrapper(createDefaultWebViewUpdateServiceWrapper());
        }
        return this.mWebViewUpdateServiceWrapper;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!this.mUserManager.isAdminUser()) {
            getActivity().finish();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.webview_app_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<DefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        Context context = getContext();
        WebViewUpdateServiceWrapper webViewUpdateServiceWrapper = getWebViewUpdateServiceWrapper();
        for (ApplicationInfo applicationInfo : webViewUpdateServiceWrapper.getValidWebViewApplicationInfos(context)) {
            arrayList.add(createDefaultAppInfo(context, this.mPm, applicationInfo, getDisabledReason(webViewUpdateServiceWrapper, context, applicationInfo.packageName)));
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        PackageInfo currentWebViewPackage = getWebViewUpdateServiceWrapper().getCurrentWebViewPackage();
        if (currentWebViewPackage == null) {
            return null;
        }
        return currentWebViewPackage.packageName;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        return getWebViewUpdateServiceWrapper().setWebViewProvider(str);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void onSelectionPerformed(boolean z) {
        Intent intent;
        if (z) {
            FragmentActivity activity = getActivity();
            if (activity == null) {
                intent = null;
            } else {
                intent = activity.getIntent();
            }
            if (intent != null && "android.settings.WEBVIEW_SETTINGS".equals(intent.getAction())) {
                getActivity().finish();
                return;
            }
            return;
        }
        getWebViewUpdateServiceWrapper().showInvalidChoiceToast(getActivity());
        updateCandidates();
    }

    private WebViewUpdateServiceWrapper createDefaultWebViewUpdateServiceWrapper() {
        return new WebViewUpdateServiceWrapper();
    }

    /* access modifiers changed from: package-private */
    public void setWebViewUpdateServiceWrapper(WebViewUpdateServiceWrapper webViewUpdateServiceWrapper) {
        this.mWebViewUpdateServiceWrapper = webViewUpdateServiceWrapper;
    }

    /* access modifiers changed from: private */
    public static class WebViewAppInfo extends DefaultAppInfo {
        public WebViewAppInfo(Context context, PackageManager packageManager, int i, PackageItemInfo packageItemInfo, String str, boolean z) {
            super(context, packageManager, i, packageItemInfo, str, z);
        }

        @Override // com.android.settingslib.widget.CandidateInfo, com.android.settingslib.applications.DefaultAppInfo
        public CharSequence loadLabel() {
            String str;
            try {
                str = this.mPm.getPackageInfo(this.packageItemInfo.packageName, 0).versionName;
            } catch (PackageManager.NameNotFoundException unused) {
                str = "";
            }
            return String.format("%s %s", super.loadLabel(), str);
        }
    }

    /* access modifiers changed from: package-private */
    public DefaultAppInfo createDefaultAppInfo(Context context, PackageManager packageManager, PackageItemInfo packageItemInfo, String str) {
        return new WebViewAppInfo(context, packageManager, this.mUserId, packageItemInfo, str, TextUtils.isEmpty(str));
    }

    /* access modifiers changed from: package-private */
    public String getDisabledReason(WebViewUpdateServiceWrapper webViewUpdateServiceWrapper, Context context, String str) {
        for (UserPackage userPackage : webViewUpdateServiceWrapper.getPackageInfosAllUsers(context, str)) {
            if (userPackage.getUserInfo().id != 999) {
                if (!userPackage.isInstalledPackage()) {
                    return context.getString(C0017R$string.webview_uninstalled_for_user, userPackage.getUserInfo().name);
                } else if (!userPackage.isEnabledPackage()) {
                    return context.getString(C0017R$string.webview_disabled_for_user, userPackage.getUserInfo().name);
                }
            }
        }
        return null;
    }
}
