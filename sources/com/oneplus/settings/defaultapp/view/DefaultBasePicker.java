package com.oneplus.settings.defaultapp.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.defaultapps.DefaultAppPickerFragment;
import com.android.settingslib.applications.DefaultAppInfo;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.defaultapp.DefaultAppActivityInfo;
import com.oneplus.settings.defaultapp.DefaultAppLogic;
import com.oneplus.settings.defaultapp.DefaultAppUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class DefaultBasePicker extends DefaultAppPickerFragment {
    protected List<DefaultAppActivityInfo> mAppInfoList;
    protected List<String> mAppNameInfoList;
    protected DefaultAppLogic mLogic;
    protected String mType = getType();

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    /* access modifiers changed from: protected */
    public abstract String getType();

    public DefaultBasePicker() {
        DefaultAppLogic instance = DefaultAppLogic.getInstance(SettingsBaseApplication.mApplication);
        this.mLogic = instance;
        List<DefaultAppActivityInfo> appInfoList = instance.getAppInfoList(this.mType);
        this.mAppInfoList = appInfoList;
        this.mAppNameInfoList = this.mLogic.getAppPackageNameList(this.mType, appInfoList);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        int keyTypeInt = DefaultAppUtils.getKeyTypeInt(this.mType);
        if (keyTypeInt == 0) {
            return C0019R$xml.op_default_camera_settings;
        }
        if (keyTypeInt == 1) {
            return C0019R$xml.op_default_gallery_settings;
        }
        if (keyTypeInt == 2) {
            return C0019R$xml.op_default_music_settings;
        }
        if (keyTypeInt != 3) {
            return 0;
        }
        return C0019R$xml.op_default_mail_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        return this.mLogic.getPmDefaultAppPackageName(this.mType);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        String defaultAppPackageName = this.mLogic.getDefaultAppPackageName(this.mType);
        String pmDefaultAppPackageName = this.mLogic.getPmDefaultAppPackageName(this.mType);
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (Objects.equals(str, defaultAppPackageName) && Objects.equals(pmDefaultAppPackageName, defaultAppPackageName)) {
            return false;
        }
        Log.d("BaseDefaultPreference", "persistString packageName:" + str + ", local defaultAppPackageName:" + defaultAppPackageName + ",pmDefaultAppPkg:" + pmDefaultAppPackageName);
        this.mLogic.setDefaultAppPosition(this.mType, this.mAppInfoList, this.mAppNameInfoList, str);
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<DefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        Context context = getContext();
        List<String> list = this.mAppNameInfoList;
        String[] strArr = (String[]) list.toArray(new String[list.size()]);
        int length = strArr.length;
        for (int i = 0; i < length; i++) {
            try {
                arrayList.add(new DefaultAppInfo(context, this.mPm, UserHandle.myUserId(), this.mPm.getApplicationInfoAsUser(strArr[i], 0, this.mUserId)));
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return arrayList;
    }
}
