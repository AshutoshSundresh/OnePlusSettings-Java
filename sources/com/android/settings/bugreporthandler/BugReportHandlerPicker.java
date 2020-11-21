package com.android.settings.bugreporthandler;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.util.Log;
import android.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.defaultapps.DefaultAppPickerFragment;
import com.android.settings.widget.RadioButtonPreference;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.FooterPreference;
import java.util.ArrayList;
import java.util.List;

public class BugReportHandlerPicker extends DefaultAppPickerFragment {
    private BugReportHandlerUtil mBugReportHandlerUtil;
    private FooterPreference mFooter;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1808;
    }

    private static String getHandlerApp(String str) {
        return str.substring(0, str.lastIndexOf(35));
    }

    private static int getHandlerUser(String str) {
        try {
            return Integer.parseInt(str.substring(str.lastIndexOf(35) + 1));
        } catch (NumberFormatException unused) {
            Log.e("BugReportHandlerPicker", "Failed to get handlerUser");
            return 0;
        }
    }

    static String getKey(String str, int i) {
        return str + "#" + i;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context)) {
            getActivity().finish();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.bug_report_handler_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void addStaticPreferences(PreferenceScreen preferenceScreen) {
        if (this.mFooter == null) {
            FooterPreference footerPreference = new FooterPreference(preferenceScreen.getContext());
            this.mFooter = footerPreference;
            footerPreference.setIcon(C0008R$drawable.ic_info_outline_24dp);
            this.mFooter.setSingleLineTitle(false);
            this.mFooter.setTitle(C0017R$string.bug_report_handler_picker_footer_text);
            this.mFooter.setSelectable(false);
        }
        preferenceScreen.addPreference(this.mFooter);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<DefaultAppInfo> getCandidates() {
        Context context = getContext();
        List<Pair<ApplicationInfo, Integer>> validBugReportHandlerInfos = getBugReportHandlerUtil().getValidBugReportHandlerInfos(context);
        ArrayList arrayList = new ArrayList();
        for (Pair<ApplicationInfo, Integer> pair : validBugReportHandlerInfos) {
            arrayList.add(createDefaultAppInfo(context, this.mPm, ((Integer) pair.second).intValue(), (PackageItemInfo) pair.first));
        }
        return arrayList;
    }

    private BugReportHandlerUtil getBugReportHandlerUtil() {
        if (this.mBugReportHandlerUtil == null) {
            setBugReportHandlerUtil(createDefaultBugReportHandlerUtil());
        }
        return this.mBugReportHandlerUtil;
    }

    /* access modifiers changed from: package-private */
    public void setBugReportHandlerUtil(BugReportHandlerUtil bugReportHandlerUtil) {
        this.mBugReportHandlerUtil = bugReportHandlerUtil;
    }

    /* access modifiers changed from: package-private */
    public BugReportHandlerUtil createDefaultBugReportHandlerUtil() {
        return new BugReportHandlerUtil();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        Pair<String, Integer> currentBugReportHandlerAppAndUser = getBugReportHandlerUtil().getCurrentBugReportHandlerAppAndUser(getContext());
        return getKey((String) currentBugReportHandlerAppAndUser.first, ((Integer) currentBugReportHandlerAppAndUser.second).intValue());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        return getBugReportHandlerUtil().setCurrentBugReportHandlerAppAndUser(getContext(), getHandlerApp(str), getHandlerUser(str));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void onSelectionPerformed(boolean z) {
        Intent intent;
        super.onSelectionPerformed(z);
        if (z) {
            FragmentActivity activity = getActivity();
            if (activity == null) {
                intent = null;
            } else {
                intent = activity.getIntent();
            }
            if (intent != null && "android.settings.BUGREPORT_HANDLER_SETTINGS".equals(intent.getAction())) {
                getActivity().finish();
                return;
            }
            return;
        }
        getBugReportHandlerUtil().showInvalidChoiceToast(getContext());
        updateCandidates();
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void bindPreferenceExtra(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        super.bindPreferenceExtra(radioButtonPreference, str, candidateInfo, str2, str3);
        radioButtonPreference.setAppendixVisibility(8);
    }

    /* access modifiers changed from: package-private */
    public DefaultAppInfo createDefaultAppInfo(Context context, PackageManager packageManager, int i, PackageItemInfo packageItemInfo) {
        return new BugreportHandlerAppInfo(context, packageManager, i, packageItemInfo, getDescription(packageItemInfo.packageName, i));
    }

    private String getDescription(String str, int i) {
        Context context = getContext();
        if ("com.android.shell".equals(str)) {
            return context.getString(C0017R$string.system_default_app_subtext);
        }
        if (this.mUserManager.getUserProfiles().size() < 2) {
            return "";
        }
        UserInfo userInfo = this.mUserManager.getUserInfo(i);
        if (userInfo == null || !userInfo.isManagedProfile()) {
            return context.getString(C0017R$string.personal_profile_app_subtext);
        }
        return context.getString(C0017R$string.work_profile_app_subtext);
    }

    /* access modifiers changed from: private */
    public static class BugreportHandlerAppInfo extends DefaultAppInfo {
        private final Context mContext;

        BugreportHandlerAppInfo(Context context, PackageManager packageManager, int i, PackageItemInfo packageItemInfo, String str) {
            super(context, packageManager, i, packageItemInfo, str, true);
            this.mContext = context;
        }

        @Override // com.android.settingslib.widget.CandidateInfo, com.android.settingslib.applications.DefaultAppInfo
        public String getKey() {
            PackageItemInfo packageItemInfo = this.packageItemInfo;
            if (packageItemInfo != null) {
                return BugReportHandlerPicker.getKey(packageItemInfo.packageName, this.userId);
            }
            return null;
        }

        @Override // com.android.settingslib.widget.CandidateInfo, com.android.settingslib.applications.DefaultAppInfo
        public CharSequence loadLabel() {
            PackageItemInfo packageItemInfo;
            if (this.mContext == null || (packageItemInfo = this.packageItemInfo) == null) {
                return null;
            }
            if ("com.android.shell".equals(packageItemInfo.packageName)) {
                return this.mContext.getString(17039631);
            }
            return super.loadLabel();
        }
    }
}
