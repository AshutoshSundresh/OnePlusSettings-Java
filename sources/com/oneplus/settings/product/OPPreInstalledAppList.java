package com.oneplus.settings.product;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0003R$array;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.common.ReflectUtil;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPBuildModelUtils;
import com.oneplus.settings.utils.OPUtils;

public class OPPreInstalledAppList extends SettingsPreferenceFragment {
    public static String[] sOneplusH2PreIinstalledAppsCompany;
    public static String[] sOneplusH2PreIinstalledAppsFunction;
    public static String[] sOneplusH2PreIinstalledAppsName;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (OPUtils.isSM8250Products() && !OPBuildModelUtils.is19811() && !OPBuildModelUtils.is19821()) {
            sOneplusH2PreIinstalledAppsName = getContext().getResources().getStringArray(C0003R$array.pre_installed_app_name_kebab);
            sOneplusH2PreIinstalledAppsCompany = getContext().getResources().getStringArray(C0003R$array.pre_installed_app_company_kebab);
            sOneplusH2PreIinstalledAppsFunction = getContext().getResources().getStringArray(C0003R$array.pre_installed_app_function_kebab);
        } else if (OPBuildModelUtils.is19811()) {
            sOneplusH2PreIinstalledAppsName = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_name_19811);
            sOneplusH2PreIinstalledAppsCompany = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_company_19811);
            sOneplusH2PreIinstalledAppsFunction = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_function_19811);
        } else if (OPBuildModelUtils.is19821()) {
            sOneplusH2PreIinstalledAppsName = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_name_19821);
            sOneplusH2PreIinstalledAppsCompany = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_company_19821);
            sOneplusH2PreIinstalledAppsFunction = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_function_19821);
        } else if (OPUtils.isHDProject()) {
            sOneplusH2PreIinstalledAppsName = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_name_hotdog);
            sOneplusH2PreIinstalledAppsCompany = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_company_hotdog);
            sOneplusH2PreIinstalledAppsFunction = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_function_hotdog);
        } else if (OPUtils.isGuaProject()) {
            sOneplusH2PreIinstalledAppsName = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_name_gua);
            sOneplusH2PreIinstalledAppsCompany = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_company_gua);
            sOneplusH2PreIinstalledAppsFunction = getContext().getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_function_gua);
        } else if (Build.MODEL.equalsIgnoreCase(SettingsBaseApplication.mApplication.getResources().getString(C0017R$string.oneplus_model_for_china_and_india)) || Build.MODEL.equalsIgnoreCase(SettingsBaseApplication.mApplication.getResources().getString(C0017R$string.oneplus_model_for_europe_and_america))) {
            sOneplusH2PreIinstalledAppsName = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_name_fat);
            sOneplusH2PreIinstalledAppsCompany = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_company_fat);
            sOneplusH2PreIinstalledAppsFunction = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_function_fat);
        } else if (Build.MODEL.equalsIgnoreCase("ONEPLUS A6000")) {
            sOneplusH2PreIinstalledAppsName = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_name_new);
            sOneplusH2PreIinstalledAppsCompany = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_company_new);
            sOneplusH2PreIinstalledAppsFunction = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_function_new);
        } else if (ReflectUtil.isFeatureSupported("OP_FEATURE_SETTINGS_QUICKPAY_ANIM_FOR_ENCHILADA")) {
            sOneplusH2PreIinstalledAppsName = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_name_new);
            sOneplusH2PreIinstalledAppsCompany = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_company_new);
            sOneplusH2PreIinstalledAppsFunction = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_function_new);
        } else if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5000") || Build.MODEL.equalsIgnoreCase("ONEPLUS A5010")) {
            sOneplusH2PreIinstalledAppsName = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_name_5_5T);
            sOneplusH2PreIinstalledAppsCompany = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_company_5_5T);
            sOneplusH2PreIinstalledAppsFunction = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_function_5_5T);
        } else {
            sOneplusH2PreIinstalledAppsName = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_name);
            sOneplusH2PreIinstalledAppsCompany = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_company);
            sOneplusH2PreIinstalledAppsFunction = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_h2_pre_installed_app_function);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        addPreferencesFromResource(C0019R$xml.op_pre_install_app_list);
        PreferenceCategory preferenceCategory = (PreferenceCategory) getPreferenceScreen().findPreference("oneplus_pre_install_app_category");
        for (int i = 0; i < sOneplusH2PreIinstalledAppsName.length; i++) {
            Preference preference = new Preference(getContext());
            preference.setTitle(sOneplusH2PreIinstalledAppsName[i]);
            preference.setSummary(sOneplusH2PreIinstalledAppsFunction[i] + " / " + sOneplusH2PreIinstalledAppsCompany[i]);
            preference.setLayoutResource(C0012R$layout.op_preference_pre_app);
            preference.setSelectable(false);
            preferenceCategory.addPreference(preference);
        }
        super.onViewCreated(view, bundle);
    }
}
