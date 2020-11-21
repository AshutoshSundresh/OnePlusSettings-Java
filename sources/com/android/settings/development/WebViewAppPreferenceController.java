package com.android.settings.development;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.webview.WebViewUpdateServiceWrapper;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class WebViewAppPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    private final PackageManager mPackageManager;
    private final WebViewUpdateServiceWrapper mWebViewUpdateServiceWrapper = new WebViewUpdateServiceWrapper();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "select_webview_provider";
    }

    public WebViewAppPreferenceController(Context context) {
        super(context);
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        CharSequence defaultAppLabel = getDefaultAppLabel();
        if (!TextUtils.isEmpty(defaultAppLabel)) {
            this.mPreference.setSummary(defaultAppLabel);
            return;
        }
        Log.d("WebViewAppPrefCtrl", "No default app");
        this.mPreference.setSummary(C0017R$string.app_list_preference_none);
    }

    /* access modifiers changed from: package-private */
    public DefaultAppInfo getDefaultAppInfo() {
        ApplicationInfo applicationInfo;
        PackageInfo currentWebViewPackage = this.mWebViewUpdateServiceWrapper.getCurrentWebViewPackage();
        Context context = this.mContext;
        PackageManager packageManager = this.mPackageManager;
        int myUserId = UserHandle.myUserId();
        if (currentWebViewPackage == null) {
            applicationInfo = null;
        } else {
            applicationInfo = currentWebViewPackage.applicationInfo;
        }
        return new DefaultAppInfo(context, packageManager, myUserId, applicationInfo);
    }

    private CharSequence getDefaultAppLabel() {
        return getDefaultAppInfo().loadLabel();
    }
}
