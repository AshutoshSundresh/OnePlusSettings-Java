package com.oneplus.settings.aboutphone;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.BidiFormatter;
import android.text.TextUtils;
import android.util.OpFeatures;
import androidx.fragment.app.Fragment;
import com.android.settings.C0003R$array;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.deviceinfo.firmwareversion.FirmwareVersionSettings;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPAuthenticationInformationUtils;
import com.oneplus.settings.utils.OPBuildModelUtils;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class AboutPhonePresenter {
    private Activity mActivity;
    private RestrictedLockUtils.EnforcedAdmin mDebuggingFeaturesDisallowedAdmin;
    private boolean mDebuggingFeaturesDisallowedBySystem;
    private int mDevHitCountdown;
    private Fragment mFragment;
    private List<SoftwareInfoEntity> mList = new ArrayList();
    public boolean mProcessingLastDevHit;
    private final UserManager mUm;
    private Contract$View mView;

    public AboutPhonePresenter(Activity activity, Fragment fragment, Contract$View contract$View) {
        this.mActivity = activity;
        this.mView = contract$View;
        this.mFragment = fragment;
        this.mUm = (UserManager) activity.getSystemService("user");
        this.mDebuggingFeaturesDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mActivity, "no_debugging_features", UserHandle.myUserId());
        this.mDevHitCountdown = DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this.mActivity) ? -1 : 7;
        this.mDebuggingFeaturesDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mActivity, "no_debugging_features", UserHandle.myUserId());
    }

    public void onResume() {
        showHardwareInfo();
        showSoftwareInfo();
    }

    private static boolean isGuaLiftCameraProject() {
        String[] stringArray = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_guacamole_lift_camera_project);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i] != null && stringArray[i].equalsIgnoreCase(Build.MODEL)) {
                return true;
            }
        }
        return false;
    }

    private static boolean is7TMCLVersionProject() {
        return Build.MODEL.equalsIgnoreCase(SettingsBaseApplication.mApplication.getString(C0017R$string.oneplus_model_19861_for_tmo)) || Build.MODEL.equalsIgnoreCase(SettingsBaseApplication.mApplication.getString(C0017R$string.oneplus_model_19801_for_cn)) || Build.MODEL.equalsIgnoreCase(SettingsBaseApplication.mApplication.getString(C0017R$string.oneplus_model_19801_for_in)) || Build.MODEL.equalsIgnoreCase(SettingsBaseApplication.mApplication.getString(C0017R$string.oneplus_model_19801_for_eu)) || Build.MODEL.equalsIgnoreCase(SettingsBaseApplication.mApplication.getString(C0017R$string.oneplus_model_19801_for_us));
    }

    private void showHardwareInfo() {
        int i;
        if (OPBuildModelUtils.is19811()) {
            i = C0008R$drawable.oneplus_8_pro;
        } else if (OPBuildModelUtils.is19821() || OPBuildModelUtils.is19855() || OPBuildModelUtils.is19867()) {
            i = C0008R$drawable.oneplus_8;
        } else if (OPUtils.isSM8250Products()) {
            i = C0008R$drawable.oneplus_other;
        } else if (is7TMCLVersionProject() && OPThemeUtils.isSupportMclTheme()) {
            i = C0008R$drawable.hd_mcl;
        } else if (OPUtils.isHDProject() && !OPUtils.isMEARom()) {
            i = C0008R$drawable.oneplus_other;
        } else if (Build.MODEL.equalsIgnoreCase("ONEPLUS A6000") || Build.MODEL.equalsIgnoreCase("ONEPLUS A6003")) {
            i = C0008R$drawable.oneplus_6;
        } else if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5000")) {
            i = C0008R$drawable.oneplus_5;
        } else if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5010")) {
            i = C0008R$drawable.oneplus_5t;
        } else if (OPUtils.isOP3()) {
            i = C0008R$drawable.oneplus_3;
        } else if (OPUtils.isOP3T()) {
            i = C0008R$drawable.oneplus_3t;
        } else if (isGuaLiftCameraProject()) {
            i = C0008R$drawable.oneplus_gua_lift_camera;
        } else if (OPUtils.is18857Project()) {
            i = C0008R$drawable.oneplus_18857;
        } else if (!OPAuthenticationInformationUtils.isOlder6tProducts()) {
            i = C0008R$drawable.oneplus_other;
        } else {
            i = C0008R$drawable.oneplus_6;
        }
        this.mView.displayHardWarePreference(i, getCameraInfo(), getCpuName(), getScreenInfo(), getTotalMemory());
    }

    private void showSoftwareInfo() {
        this.mList.clear();
        addDeviceName();
        if (OPAuthenticationInformationUtils.isNeedAddAuthenticationInfo(this.mActivity)) {
            addAuthenticationInfo();
        }
        addAndroidVersion();
        if (!OPUtils.isSM8X50Products()) {
            addOneplusSystemVersion();
        }
        if (!OPUtils.isSupportUss() && !OPUtils.isSupportUstUnify()) {
            addVersionNumber();
        }
        addDeviceModel();
        addLegalInfo();
        addStatusInfo();
        if (!OPAuthenticationInformationUtils.isOlder6tProducts()) {
            if (OPUtils.isO2() || !OPUtils.isSurportProductInfo(this.mActivity)) {
                addAwardInfo();
            } else {
                addProductIntroduce();
                addAwardInfo();
            }
        } else if (!OPUtils.isO2()) {
            addProductIntroduce();
        }
        if (OPUtils.isSupportUss() || ProductUtils.isUsvMode() || OPUtils.isSupportUstUnify()) {
            addSoftwareVersion();
            addHardwareVersion();
        }
        this.mView.displaySoftWarePreference(this.mList);
    }

    private void addDeviceName() {
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        softwareInfoEntity.setTitle(this.mActivity.getString(C0017R$string.my_device_info_device_name_preference_title));
        String string = Settings.System.getString(this.mActivity.getContentResolver(), "oem_oneplus_devicename");
        if (OPUtils.isEF009Project()) {
            boolean isContainSymbol = OPUtils.isContainSymbol(string);
            CharSequence charSequence = string;
            if (isContainSymbol) {
                charSequence = OPUtils.getSymbolDeviceName(string);
            }
            softwareInfoEntity.setSummary(charSequence);
        } else {
            softwareInfoEntity.setSummary(string);
        }
        softwareInfoEntity.setResIcon(C0008R$drawable.op_device_name);
        softwareInfoEntity.setIntent("com.oneplus.intent.OPDeviceNameActivity");
        this.mList.add(softwareInfoEntity);
    }

    private void addAuthenticationInfo() {
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        softwareInfoEntity.setSummary(this.mActivity.getString(C0017R$string.oneplus_regulatory_information));
        softwareInfoEntity.setResIcon(C0008R$drawable.op_authentication_information);
        String str = "";
        String str2 = "com.oneplus.intent.OPAuthenticationInformationSettings";
        if (Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_for_china_and_india)) || Build.MODEL.equals("ONEPLUS A6000") || Build.MODEL.equals("ONEPLUS A5010") || Build.MODEL.equals("ONEPLUS A5000")) {
            if (OPUtils.isO2()) {
                str = this.mActivity.getString(C0017R$string.regulatory_labels);
            } else {
                str = this.mActivity.getString(C0017R$string.oneplus_authentication_information);
                softwareInfoEntity.setTitle(str);
                softwareInfoEntity.setIntent(str2);
                this.mList.add(softwareInfoEntity);
            }
        } else if (Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_for_europe_and_america)) || Build.MODEL.equals("ONEPLUS A6003")) {
            str = this.mActivity.getString(C0017R$string.regulatory_labels);
        } else if (OPUtils.isOP3() || OPUtils.isOP3T()) {
            if (SystemProperties.get("ro.rf_version").contains("Am")) {
                str = this.mActivity.getString(C0017R$string.regulatory_labels);
            } else {
                str = this.mActivity.getString(C0017R$string.oneplus_authentication_information);
                softwareInfoEntity.setTitle(str);
                softwareInfoEntity.setIntent(str2);
                this.mList.add(softwareInfoEntity);
            }
        } else if (Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_oneplus_model_18821_for_eu)) || Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_18865_for_eu)) || Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_19801_for_eu)) || Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_oneplus_model_18857_for_eu)) || Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_oneplus_model_18821_for_us)) || Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_oneplus_model_18831_for_us)) || Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_oneplus_model_18857_for_us)) || ((Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_oneplus_model_18825_for_us)) && OPUtils.isO2()) || (((Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_oneplus_model_ee145)) || Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_19801_for_us)) || Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_18865_for_us)) || Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_18865_for_tmo)) || Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_19861_for_tmo)) || Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_19863_for_tmo))) && !OPUtils.isMEARom()) || ((Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_19855_for_tmo)) && OPUtils.isOnePlusBrand()) || ((Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_19821_for_us)) && OPUtils.isOnePlusBrand()) || ((Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_19867_for_vzw)) && OPUtils.isOnePlusBrand()) || (Build.MODEL.equals(this.mActivity.getString(C0017R$string.oneplus_model_19811_for_us)) && OPUtils.isOnePlusBrand()))))))) {
            str = this.mActivity.getString(C0017R$string.regulatory_labels);
        } else {
            if (OPAuthenticationInformationUtils.isNeedShowAuthenticationInformation(this.mActivity)) {
                str = this.mActivity.getString(C0017R$string.oneplus_authentication_information);
            } else {
                str2 = str;
            }
            softwareInfoEntity.setTitle(str);
            softwareInfoEntity.setIntent(str2);
            this.mList.add(softwareInfoEntity);
        }
        str2 = "android.settings.SHOW_REGULATORY_INFO";
        softwareInfoEntity.setTitle(str);
        softwareInfoEntity.setIntent(str2);
        this.mList.add(softwareInfoEntity);
    }

    private void addAndroidVersion() {
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        softwareInfoEntity.setTitle(this.mActivity.getString(C0017R$string.firmware_version));
        softwareInfoEntity.setSummary(Build.VERSION.RELEASE);
        softwareInfoEntity.setResIcon(C0008R$drawable.op_android_version);
        softwareInfoEntity.setIntent("com.android.FirmwareVersionDialogFragment");
        this.mList.add(softwareInfoEntity);
    }

    private void addOneplusSystemVersion() {
        String str;
        String str2;
        int i;
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        if (OpFeatures.isSupport(new int[]{1})) {
            i = C0008R$drawable.op_o2_version;
            str2 = this.mActivity.getResources().getString(C0017R$string.oneplus_oxygen_version);
            str = SystemProperties.get("ro.oxygen.version", this.mActivity.getResources().getString(C0017R$string.device_info_default)).replace("O2", "O₂");
        } else {
            i = C0008R$drawable.op_h2_version;
            str2 = this.mActivity.getResources().getString(C0017R$string.oneplus_hydrogen_version).replace("H2", "H₂");
            str = SystemProperties.get("ro.rom.version", this.mActivity.getResources().getString(C0017R$string.device_info_default)).replace("H2", "H₂");
        }
        softwareInfoEntity.setTitle(str2);
        softwareInfoEntity.setSummary(str);
        softwareInfoEntity.setResIcon(i);
        softwareInfoEntity.setIntent(null);
        this.mList.add(softwareInfoEntity);
    }

    private void addVersionNumber() {
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        softwareInfoEntity.setTitle(this.mActivity.getString(C0017R$string.build_number));
        String unicodeWrap = BidiFormatter.getInstance().unicodeWrap(Build.DISPLAY);
        if (OPUtils.isSM8X50Products()) {
            unicodeWrap = SystemProperties.get("ro.rom.version", this.mActivity.getResources().getString(C0017R$string.device_info_default));
        }
        softwareInfoEntity.setSummary(unicodeWrap);
        softwareInfoEntity.setResIcon(C0008R$drawable.op_soft_version);
        softwareInfoEntity.setIntent("build.number");
        this.mList.add(softwareInfoEntity);
    }

    private void addDeviceModel() {
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        softwareInfoEntity.setTitle(this.mActivity.getString(C0017R$string.model_info));
        softwareInfoEntity.setResIcon(C0008R$drawable.op_model);
        softwareInfoEntity.setIntent(null);
        if (Build.MODEL.contains("A30") || Build.MODEL.contains("A50") || Build.MODEL.contains("A60")) {
            softwareInfoEntity.setSummary("ONEPLUS\n" + Build.MODEL.replaceAll("ONEPLUS ", ""));
        } else {
            softwareInfoEntity.setSummary(Build.MODEL);
        }
        this.mList.add(softwareInfoEntity);
    }

    private void addLegalInfo() {
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        softwareInfoEntity.setTitle(this.mActivity.getString(C0017R$string.legal_information));
        softwareInfoEntity.setSummary(this.mActivity.getString(C0017R$string.oneplus_legal_summary));
        softwareInfoEntity.setResIcon(C0008R$drawable.op_legal_settings);
        softwareInfoEntity.setIntent("com.oneplus.intent.LegalSettingsActivity");
        this.mList.add(softwareInfoEntity);
    }

    private void addStatusInfo() {
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        softwareInfoEntity.setTitle(this.mActivity.getString(C0017R$string.device_status));
        softwareInfoEntity.setSummary(this.mActivity.getString(C0017R$string.oneplus_status_summary));
        softwareInfoEntity.setResIcon(C0008R$drawable.op_status_settings);
        softwareInfoEntity.setIntent("com.oneplus.intent.MyDeviceInfoFragmentActivity");
        this.mList.add(softwareInfoEntity);
    }

    private void addAwardInfo() {
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        softwareInfoEntity.setTitle(this.mActivity.getString(C0017R$string.oneplus_forum_award_title));
        if (OPUtils.isO2()) {
            softwareInfoEntity.setSummary(this.mActivity.getString(C0017R$string.oneplus_o2_contributors));
        } else {
            softwareInfoEntity.setSummary(this.mActivity.getString(C0017R$string.oneplus_h2_contributors));
        }
        softwareInfoEntity.setResIcon(C0008R$drawable.op_award_icon);
        softwareInfoEntity.setIntent("com.oneplus.intent.OPForumContributorsActivity");
        this.mList.add(softwareInfoEntity);
    }

    private void addProductIntroduce() {
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        softwareInfoEntity.setTitle(this.mActivity.getString(C0017R$string.oneplus_product_info));
        softwareInfoEntity.setSummary(this.mActivity.getString(C0017R$string.oneplus_product_info_summary));
        softwareInfoEntity.setResIcon(C0008R$drawable.op_product_info);
        softwareInfoEntity.setIntent("com.oneplus.action.PRODUCT_INFO");
        this.mList.add(softwareInfoEntity);
    }

    private void addSoftwareVersion() {
        String[] split;
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        softwareInfoEntity.setTitle(this.mActivity.getString(C0017R$string.onplus_software_version_info));
        softwareInfoEntity.setResIcon(C0008R$drawable.op_software_icon);
        String unicodeWrap = BidiFormatter.getInstance().unicodeWrap(Build.DISPLAY);
        if (OPUtils.isSM8X50Products()) {
            String string = this.mActivity.getResources().getString(C0017R$string.device_info_default);
            if (ProductUtils.isUsvMode()) {
                String str = SystemProperties.get("ro.build.display.id", string);
                if (str.isEmpty() || (split = str.split("_")) == null || split.length != 3) {
                    unicodeWrap = str;
                } else {
                    SystemProperties.get("ro.boot.hw_version", this.mActivity.getResources().getString(C0017R$string.device_info_default));
                    String string2 = Settings.System.getString(this.mActivity.getContentResolver(), "hw_version_ui");
                    if (!TextUtils.isEmpty(string2)) {
                        string = string2;
                    }
                    try {
                        if (!string.isEmpty() && Integer.parseInt(string) > 13) {
                            string = "15";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    unicodeWrap = split[0] + "_" + string + "_" + split[2];
                }
            } else {
                unicodeWrap = SystemProperties.get("ro.rom.version", string);
            }
        }
        softwareInfoEntity.setSummary(unicodeWrap);
        softwareInfoEntity.setIntent("build.number");
        this.mList.add(softwareInfoEntity);
    }

    private void addHardwareVersion() {
        SoftwareInfoEntity softwareInfoEntity = new SoftwareInfoEntity();
        softwareInfoEntity.setTitle(this.mActivity.getString(C0017R$string.onplus_hardware_version_info));
        softwareInfoEntity.setResIcon(C0008R$drawable.op_hardware_icon);
        String str = SystemProperties.get("ro.boot.hw_version", this.mActivity.getResources().getString(C0017R$string.device_info_default));
        String string = Settings.System.getString(this.mActivity.getContentResolver(), "hw_version_ui");
        if (!TextUtils.isEmpty(string)) {
            str = string;
        }
        if (!ProductUtils.isUsvMode()) {
            softwareInfoEntity.setSummary(str);
        } else if (Integer.parseInt(str) <= 13) {
            softwareInfoEntity.setSummary(str);
        } else {
            softwareInfoEntity.setSummary("15");
        }
        softwareInfoEntity.setIntent(null);
        this.mList.add(softwareInfoEntity);
    }

    private String getCpuName() {
        if (OPBuildModelUtils.is19811() || OPBuildModelUtils.is19821() || OPBuildModelUtils.is19855() || OPBuildModelUtils.is19867()) {
            return this.mActivity.getString(C0017R$string.oneplus_in_project_8_series_cpu_info);
        }
        if (Build.MODEL.startsWith("ONEPLUS A60")) {
            return "Snapdragon™ 845";
        }
        if (Build.MODEL.startsWith("ONEPLUS A50")) {
            return "Snapdragon™ 835";
        }
        if (OPUtils.isOP3T()) {
            return "Snapdragon™ 821";
        }
        if (OPUtils.isOP3()) {
            return "Snapdragon™ 820";
        }
        if (OPUtils.isGuaProject()) {
            return "Snapdragon™ 855";
        }
        if (!OPUtils.isHDProject() || OPUtils.isMEARom()) {
            return (!OPUtils.isOP_19_2nd() || OPUtils.isMEARom()) ? "none" : this.mActivity.getString(C0017R$string.oneplus_19_2nd_cpu_info);
        }
        return this.mActivity.getString(C0017R$string.oneplus_hd_project_cpu_info);
    }

    private static String getTotalMemory() {
        String str = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
            str = bufferedReader.readLine().split("\\s+")[1];
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(str != null ? (int) Math.ceil(new Float(Float.valueOf(str).floatValue() / 1048576.0f).doubleValue()) : 0);
    }

    private String getScreenInfo() {
        if (OPBuildModelUtils.is19821() || OPBuildModelUtils.is19855() || OPBuildModelUtils.is19867()) {
            return this.mActivity.getString(C0017R$string.oneplus_in_project_8_screen_info);
        }
        if (OPBuildModelUtils.is19811()) {
            return this.mActivity.getString(C0017R$string.oneplus_in_project_8pro_screen_info);
        }
        if (Build.MODEL.equalsIgnoreCase("ONEPLUS A6000") || Build.MODEL.equalsIgnoreCase("ONEPLUS A6003")) {
            return this.mActivity.getString(C0017R$string.oneplus_6_28_inch_amoled_display);
        }
        if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5010")) {
            return this.mActivity.getString(C0017R$string.oneplus_6_01_inch_amoled_display);
        }
        if (Build.MODEL.contains("A50") || Build.MODEL.contains("A30")) {
            return this.mActivity.getString(C0017R$string.oneplus_5_5_inch_amoled_display);
        }
        if (Build.MODEL.equalsIgnoreCase("ONEPLUS A6010") || Build.MODEL.equalsIgnoreCase("ONEPLUS A6013") || OPUtils.is18857Project()) {
            return this.mActivity.getString(C0017R$string.oneplus_6_41_inch_amoled_display);
        }
        if (OPUtils.isGuaProject()) {
            return this.mActivity.getString(C0017R$string.oneplus_7_pro_screen_info);
        }
        return (!OPUtils.isHDProject() || OPUtils.isMEARom()) ? "none" : this.mActivity.getString(C0017R$string.oneplus_hd_project_screen_info);
    }

    private String getCameraInfo() {
        if (OPBuildModelUtils.is19821() || OPBuildModelUtils.is19855() || OPBuildModelUtils.is19867()) {
            return this.mActivity.getString(C0017R$string.oneplus_in_project_8_camera_info);
        }
        if (OPBuildModelUtils.is19811()) {
            return this.mActivity.getString(C0017R$string.oneplus_in_project_8pro_camera_info);
        }
        if (Build.MODEL.contains("A60") || Build.MODEL.contains("A50")) {
            return "16 + 20 MP Dual Camera";
        }
        if (OPUtils.isOP3T()) {
            return this.mActivity.getString(C0017R$string.oneplus_3t_camera_info);
        }
        if (OPUtils.isOP3()) {
            return this.mActivity.getString(C0017R$string.oneplus_3_camera_info);
        }
        if (OPUtils.is18857Project()) {
            return this.mActivity.getString(C0017R$string.oneplus_18857_camera_info);
        }
        if (OPUtils.isGuaProject()) {
            return this.mActivity.getString(C0017R$string.oneplus_7_camera_info);
        }
        if (!OPUtils.isHDProject() || OPUtils.isMEARom()) {
            return (!OPUtils.isOP_19_2nd() || OPUtils.isMEARom()) ? "none" : this.mActivity.getString(C0017R$string.oneplus_19_2nd_camera_info);
        }
        return this.mActivity.getString(C0017R$string.oneplus_hd_project_camera_info);
    }

    public void enableDevelopmentSettings() {
        this.mDevHitCountdown = 0;
        this.mProcessingLastDevHit = false;
        DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(this.mActivity, true);
        this.mView.cancelToast();
        if (OPUtils.isSupportXVibrate()) {
            this.mView.performHapticFeedback();
        }
        this.mView.showLongToast(C0017R$string.show_dev_on);
    }

    public void onItemClick(int i) {
        ComponentName deviceOwnerComponent;
        String intent = this.mList.get(i).getIntent();
        if (intent != null && !"".equals(intent)) {
            if ("com.android.FirmwareVersionDialogFragment".equals(intent)) {
                OPUtils.startFragment(this.mActivity, FirmwareVersionSettings.class.getName(), 9999);
            } else if (!"build.number".equals(intent)) {
                this.mFragment.startActivity(new Intent(intent));
            } else if (!Utils.isMonkeyRunning()) {
                if ((this.mUm.isAdminUser() || this.mUm.isDemoUser()) && OPUtils.isDeviceProvisioned(this.mActivity)) {
                    if (this.mUm.hasUserRestriction("no_debugging_features")) {
                        if (this.mUm.isDemoUser() && (deviceOwnerComponent = Utils.getDeviceOwnerComponent(this.mActivity)) != null) {
                            Intent action = new Intent().setPackage(deviceOwnerComponent.getPackageName()).setAction("com.android.settings.action.REQUEST_DEBUG_FEATURES");
                            if (this.mActivity.getPackageManager().resolveActivity(action, 0) != null) {
                                this.mActivity.startActivity(action);
                                return;
                            }
                        }
                        RestrictedLockUtils.EnforcedAdmin enforcedAdmin = this.mDebuggingFeaturesDisallowedAdmin;
                        if (enforcedAdmin != null && !this.mDebuggingFeaturesDisallowedBySystem) {
                            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mActivity, enforcedAdmin);
                        }
                    }
                    int i2 = this.mDevHitCountdown;
                    if (i2 > 0) {
                        int i3 = i2 - 1;
                        this.mDevHitCountdown = i3;
                        if (i3 != 0 || this.mProcessingLastDevHit) {
                            int i4 = this.mDevHitCountdown;
                            if (i4 > 0 && i4 < 5) {
                                this.mView.cancelToast();
                                Contract$View contract$View = this.mView;
                                Resources resources = this.mActivity.getResources();
                                int i5 = C0015R$plurals.show_dev_countdown;
                                int i6 = this.mDevHitCountdown;
                                contract$View.showLongToast(resources.getQuantityString(i5, i6, Integer.valueOf(i6)));
                                return;
                            }
                            return;
                        }
                        this.mDevHitCountdown = i3 + 1;
                        boolean launchConfirmationActivity = new ChooseLockSettingsHelper(this.mActivity, this.mFragment).launchConfirmationActivity(100, this.mActivity.getString(C0017R$string.unlock_set_unlock_launch_picker_title));
                        this.mProcessingLastDevHit = launchConfirmationActivity;
                        if (!launchConfirmationActivity) {
                            enableDevelopmentSettings();
                        }
                    } else if (i2 < 0) {
                        this.mView.cancelToast();
                        this.mView.showLongToast(C0017R$string.show_dev_already);
                    }
                }
            }
        }
    }
}
