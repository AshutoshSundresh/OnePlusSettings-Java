package com.oneplus.settings.defaultapp.controller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.applications.defaultapps.DefaultAppPreferenceController;
import com.android.settingslib.applications.DefaultAppInfo;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.defaultapp.DefaultAppActivityInfo;
import com.oneplus.settings.defaultapp.DefaultAppLogic;
import java.util.ArrayList;
import java.util.List;

public abstract class DefaultBasePreferenceController extends DefaultAppPreferenceController {
    private static final String TAG = "DefaultBasePreferenceController";
    protected List<DefaultAppActivityInfo> mAppInfoList;
    protected List<String> mAppNameInfoList;
    protected DefaultAppLogic mLogic;
    protected String mType = getType();

    /* access modifiers changed from: protected */
    public abstract String getType();

    public DefaultBasePreferenceController(Context context) {
        super(context);
        DefaultAppLogic instance = DefaultAppLogic.getInstance(SettingsBaseApplication.mApplication);
        this.mLogic = instance;
        List<DefaultAppActivityInfo> appInfoList = instance.getAppInfoList(this.mType);
        this.mAppInfoList = appInfoList;
        this.mAppNameInfoList = this.mLogic.getAppPackageNameList(this.mType, appInfoList);
        context.getPackageManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        List<DefaultAppInfo> candidates = getCandidates();
        return candidates != null && !candidates.isEmpty();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mType;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        CharSequence defaultAppLabel = getDefaultAppLabel();
        if (!TextUtils.isEmpty(defaultAppLabel)) {
            preference.setSummary(defaultAppLabel);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public DefaultAppInfo getDefaultAppInfo() {
        try {
            return new DefaultAppInfo(this.mContext, this.mPackageManager, UserHandle.myUserId(), this.mPackageManager.getApplicationInfo(this.mLogic.getPmDefaultAppPackageName(this.mType), 0));
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public CharSequence getDefaultAppLabel() {
        CharSequence charSequence = null;
        if (!isAvailable()) {
            return null;
        }
        DefaultAppInfo defaultAppInfo = getDefaultAppInfo();
        if (defaultAppInfo != null) {
            charSequence = defaultAppInfo.loadLabel();
        }
        if (!TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        return getOnlyAppLabel();
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public Drawable getDefaultAppIcon() {
        if (!isAvailable()) {
            return null;
        }
        DefaultAppInfo defaultAppInfo = getDefaultAppInfo();
        if (defaultAppInfo != null) {
            return defaultAppInfo.loadIcon();
        }
        return getOnlyAppIcon();
    }

    private List<DefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        Context context = this.mContext;
        List<String> list = this.mAppNameInfoList;
        String[] strArr = (String[]) list.toArray(new String[list.size()]);
        int length = strArr.length;
        for (int i = 0; i < length; i++) {
            try {
                arrayList.add(new DefaultAppInfo(context, this.mPackageManager, UserHandle.myUserId(), this.mPackageManager.getApplicationInfo(strArr[i], 0)));
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return arrayList;
    }

    private CharSequence getOnlyAppLabel() {
        List<DefaultAppInfo> candidates;
        if (!isAvailable() || (candidates = getCandidates()) == null || candidates.size() != 1) {
            return null;
        }
        DefaultAppInfo defaultAppInfo = candidates.get(0);
        String str = TAG;
        Log.d(str, "Getting label for the only app: " + defaultAppInfo.componentName);
        return defaultAppInfo.loadLabel();
    }

    private Drawable getOnlyAppIcon() {
        try {
            return new DefaultAppInfo(this.mContext, this.mPackageManager, UserHandle.myUserId(), this.mPackageManager.getApplicationInfo(this.mLogic.getDefaultAppPackageName(this.mType), 0)).loadIcon();
        } catch (PackageManager.NameNotFoundException e) {
            String str = TAG;
            Log.e(str, "getOnlyAppIcon error . e:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
