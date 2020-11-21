package com.oneplus.settings.quickpay;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction;
import com.android.settings.widget.RadioButtonPreference;
import com.google.android.collect.Lists;
import com.oneplus.settings.OPButtonsSettings;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.quickpay.QuickPayLottieAnimPreference;
import com.oneplus.settings.ui.OPPreferenceDivider;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class QuickPaySettings extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, QuickPayLottieAnimPreference.OnPreferenceViewClickListener {
    private static final int MY_USER_ID = UserHandle.myUserId();
    private SettingsActivity mActivity;
    private List<RadioButtonPreference> mAllPayWaysPreference = Lists.newArrayList();
    private int mDefaultLongPressOnHomeBehavior;
    private SwitchPreference mFingerprintLongpressQuickpay;
    private FingerprintManager mFingerprintManager;
    private boolean mHasFingerprint;
    private String[] mHomeKeyActionName;
    private String[] mHomeKeyActionValue;
    private IntentFilter mIntentFilter;
    private List<String> mPayWaysKeyList = Lists.newArrayList();
    private String[] mPayWaysName;
    private List<String> mPayWaysNameList = Lists.newArrayList();
    private List<Integer> mPayWaysValueList = Lists.newArrayList();
    private OPPreferenceDivider mPreferenceDividerLine2;
    private AppInstallAndUninstallReceiver mQuickPayAppsAddOrRemovedReceiver;
    private QuickPayLottieAnimPreference mQuickpayInstructions;
    private PreferenceCategory mQuickpaySelectDefaultWayCategory;
    private PreferenceCategory mQuickpayUninstallAppCategory;
    private RadioButtonPreference mQuickpayWayAlipayQrcode;
    private RadioButtonPreference mQuickpayWayAlipayScanning;
    private RadioButtonPreference mQuickpayWayPaytm;
    private RadioButtonPreference mQuickpayWayWecahtQrcode;
    private RadioButtonPreference mQuickpayWayWecahtRideCode;
    private RadioButtonPreference mQuickpayWayWecahtScanning;
    private SwitchPreference mSwitchLockscreen;
    private SwitchPreference mSwitchUnlockscreen;
    private String[] sPayWaysKey;
    private int[] sPayWaysValue;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.fragment.app.Fragment
    public void onConfigurationChanged(Configuration configuration) {
        initResourceData();
        initHomeActionName();
        super.onConfigurationChanged(configuration);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mFingerprintManager = (FingerprintManager) getActivity().getSystemService("fingerprint");
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        this.mIntentFilter.addDataScheme("package");
        this.mQuickPayAppsAddOrRemovedReceiver = new AppInstallAndUninstallReceiver();
        initResourceData();
        initPreference();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mActivity = (SettingsActivity) getActivity();
        initHomeActionName();
    }

    private void initHomeActionName() {
        if (!OPButtonsSettings.checkGMS(getPrefContext())) {
            this.mHomeKeyActionName = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.hardware_keys_action_entries_nogms_quickpay);
            this.mHomeKeyActionValue = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.hardware_keys_action_values_nogms_quickpay);
        } else {
            this.mHomeKeyActionName = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.hardware_keys_action_entries_quickpay);
            this.mHomeKeyActionValue = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.hardware_keys_action_values_quickpay);
        }
        if (!OPUtils.methodIsMigrated(SettingsBaseApplication.mApplication)) {
            String string = SettingsBaseApplication.mApplication.getString(C0017R$string.hardware_keys_action_shelf);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int i = 0;
            while (true) {
                String[] strArr = this.mHomeKeyActionName;
                if (i >= strArr.length) {
                    break;
                }
                if (!string.equals(strArr[i])) {
                    arrayList.add(this.mHomeKeyActionName[i]);
                    arrayList2.add(this.mHomeKeyActionValue[i]);
                }
                i++;
            }
            this.mHomeKeyActionName = new String[arrayList.size()];
            this.mHomeKeyActionValue = new String[arrayList2.size()];
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                this.mHomeKeyActionName[i2] = (String) arrayList.get(i2);
                this.mHomeKeyActionValue[i2] = (String) arrayList2.get(i2);
            }
        }
    }

    private int getLongPressHomeActionIndexByValue(int i) {
        int i2 = 0;
        while (true) {
            String[] strArr = this.mHomeKeyActionValue;
            if (i2 >= strArr.length) {
                return 0;
            }
            if (i == Integer.parseInt(strArr[i2])) {
                return i2;
            }
            i2++;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        checkFingerPrint();
        updatePreferenceState();
        SettingsBaseApplication.mApplication.registerReceiver(this.mQuickPayAppsAddOrRemovedReceiver, this.mIntentFilter);
    }

    private void initResourceData() {
        if (OPUtils.isO2()) {
            this.sPayWaysKey = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_quickpay_ways_key_O2);
            this.sPayWaysValue = SettingsBaseApplication.mApplication.getResources().getIntArray(C0003R$array.oneplus_quickpay_ways_value_O2);
            this.mPayWaysName = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_quickpay_ways_name_O2);
            return;
        }
        this.sPayWaysKey = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_quickpay_ways_key);
        this.sPayWaysValue = SettingsBaseApplication.mApplication.getResources().getIntArray(C0003R$array.oneplus_quickpay_ways_value);
        this.mPayWaysName = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_quickpay_ways_name);
    }

    private void checkFingerPrint() {
        if (this.mFingerprintManager.getEnrolledFingerprints(MY_USER_ID).size() > 0) {
            this.mHasFingerprint = true;
        } else {
            this.mHasFingerprint = false;
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        this.mQuickpayInstructions.stopAnim();
        SettingsBaseApplication.mApplication.unregisterReceiver(this.mQuickPayAppsAddOrRemovedReceiver);
        super.onPause();
    }

    private void initPreference() {
        addPreferencesFromResource(C0019R$xml.op_quickpay_settings);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("key_switch_lockscreen");
        this.mSwitchLockscreen = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("key_switch_unlockscreen");
        this.mSwitchUnlockscreen = switchPreference2;
        switchPreference2.setOnPreferenceChangeListener(this);
        if (OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) {
            this.mSwitchLockscreen.setSummary(C0017R$string.oneplus_fingerprint_quickpay_lock_swtch_summary);
            removePreference("key_switch_unlockscreen");
        }
        this.mQuickpayUninstallAppCategory = (PreferenceCategory) findPreference("key_quickpay_uninstall_app_category");
        this.mQuickpaySelectDefaultWayCategory = (PreferenceCategory) findPreference("key_quickpay_select_default_way_category");
        QuickPayLottieAnimPreference quickPayLottieAnimPreference = (QuickPayLottieAnimPreference) findPreference("key_quickpay_instructions");
        this.mQuickpayInstructions = quickPayLottieAnimPreference;
        quickPayLottieAnimPreference.setViewOnClick(this);
        this.mPreferenceDividerLine2 = (OPPreferenceDivider) findPreference("preference_divider_line2");
        this.mQuickpayWayWecahtQrcode = (RadioButtonPreference) findPreference("key_quickpay_way_wecaht_qrcode");
        this.mQuickpayWayWecahtScanning = (RadioButtonPreference) findPreference("key_quickpay_way_wecaht_scanning");
        this.mQuickpayWayWecahtRideCode = (RadioButtonPreference) findPreference("key_quickpay_way_wecaht_ridecode");
        this.mQuickpayWayAlipayQrcode = (RadioButtonPreference) findPreference("key_quickpay_way_alipay_qrcode");
        this.mQuickpayWayAlipayScanning = (RadioButtonPreference) findPreference("key_quickpay_way_alipay_scanning");
        this.mQuickpayWayPaytm = (RadioButtonPreference) findPreference("key_quickpay_way_paytm");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0151  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updatePreferenceState() {
        /*
        // Method dump skipped, instructions count: 345
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.quickpay.QuickPaySettings.updatePreferenceState():void");
    }

    private void initPayWayData() {
        this.mPayWaysNameList.clear();
        this.mPayWaysKeyList.clear();
        this.mPayWaysValueList.clear();
        if (OPUtils.isO2()) {
            if (OPUtils.isAppExist(this.mActivity, "com.tencent.mm")) {
                this.mPayWaysNameList.add(this.mPayWaysName[0]);
                this.mPayWaysKeyList.add(this.sPayWaysKey[0]);
                this.mPayWaysValueList.add(Integer.valueOf(this.sPayWaysValue[0]));
                this.mPayWaysNameList.add(this.mPayWaysName[1]);
                this.mPayWaysKeyList.add(this.sPayWaysKey[1]);
                this.mPayWaysValueList.add(Integer.valueOf(this.sPayWaysValue[1]));
                this.mQuickpayWayWecahtQrcode.setVisible(true);
                this.mQuickpayWayWecahtScanning.setVisible(true);
            } else {
                this.mQuickpayWayWecahtQrcode.setVisible(false);
                this.mQuickpayWayWecahtScanning.setVisible(false);
            }
            if (OPUtils.isAppExist(this.mActivity, "com.eg.android.AlipayGphone")) {
                this.mPayWaysNameList.add(this.mPayWaysName[2]);
                this.mPayWaysKeyList.add(this.sPayWaysKey[2]);
                this.mPayWaysValueList.add(Integer.valueOf(this.sPayWaysValue[2]));
                this.mPayWaysNameList.add(this.mPayWaysName[3]);
                this.mPayWaysKeyList.add(this.sPayWaysKey[3]);
                this.mPayWaysValueList.add(Integer.valueOf(this.sPayWaysValue[3]));
                this.mQuickpayWayAlipayQrcode.setVisible(true);
                this.mQuickpayWayAlipayScanning.setVisible(true);
            } else {
                this.mQuickpayWayAlipayQrcode.setVisible(false);
                this.mQuickpayWayAlipayScanning.setVisible(false);
            }
            if (OPUtils.isAppExist(this.mActivity, "net.one97.paytm")) {
                this.mPayWaysNameList.add(this.mPayWaysName[4]);
                this.mPayWaysKeyList.add(this.sPayWaysKey[4]);
                this.mPayWaysValueList.add(Integer.valueOf(this.sPayWaysValue[4]));
                this.mQuickpayWayPaytm.setVisible(true);
            } else {
                this.mQuickpayWayPaytm.setVisible(false);
            }
            this.mQuickpayWayWecahtRideCode.setVisible(false);
            return;
        }
        if (OPUtils.isAppExist(this.mActivity, "com.tencent.mm")) {
            this.mPayWaysNameList.add(this.mPayWaysName[0]);
            this.mPayWaysKeyList.add(this.sPayWaysKey[0]);
            this.mPayWaysValueList.add(Integer.valueOf(this.sPayWaysValue[0]));
            this.mPayWaysNameList.add(this.mPayWaysName[1]);
            this.mPayWaysKeyList.add(this.sPayWaysKey[1]);
            this.mPayWaysValueList.add(Integer.valueOf(this.sPayWaysValue[1]));
            this.mPayWaysNameList.add(this.mPayWaysName[2]);
            this.mPayWaysKeyList.add(this.sPayWaysKey[2]);
            this.mPayWaysValueList.add(Integer.valueOf(this.sPayWaysValue[2]));
            this.mQuickpayWayWecahtQrcode.setVisible(true);
            this.mQuickpayWayWecahtScanning.setVisible(true);
            this.mQuickpayWayWecahtRideCode.setVisible(true);
        } else {
            this.mQuickpayWayWecahtQrcode.setVisible(false);
            this.mQuickpayWayWecahtScanning.setVisible(false);
            this.mQuickpayWayWecahtRideCode.setVisible(false);
        }
        if (OPUtils.isAppExist(this.mActivity, "com.eg.android.AlipayGphone")) {
            this.mPayWaysNameList.add(this.mPayWaysName[3]);
            this.mPayWaysKeyList.add(this.sPayWaysKey[3]);
            this.mPayWaysValueList.add(Integer.valueOf(this.sPayWaysValue[3]));
            this.mPayWaysNameList.add(this.mPayWaysName[4]);
            this.mPayWaysKeyList.add(this.sPayWaysKey[4]);
            this.mPayWaysValueList.add(Integer.valueOf(this.sPayWaysValue[4]));
            this.mQuickpayWayAlipayQrcode.setVisible(true);
            this.mQuickpayWayAlipayScanning.setVisible(true);
        } else {
            this.mQuickpayWayAlipayQrcode.setVisible(false);
            this.mQuickpayWayAlipayScanning.setVisible(false);
        }
        if (OPUtils.isAppExist(this.mActivity, "net.one97.paytm")) {
            this.mPayWaysNameList.add(this.mPayWaysName[5]);
            this.mPayWaysKeyList.add(this.sPayWaysKey[5]);
            this.mPayWaysValueList.add(Integer.valueOf(this.sPayWaysValue[5]));
            this.mQuickpayWayPaytm.setVisible(true);
            return;
        }
        this.mQuickpayWayPaytm.setVisible(false);
    }

    private void refreshQuickPayEnableUI(boolean z) {
        removePreference("preference_divider_line2");
        if (!z) {
            removePreference("key_quickpay_select_default_way_category");
            removePreference("key_quickpay_uninstall_app_category");
        } else if (this.mPayWaysNameList.size() > 0) {
            getPreferenceScreen().addPreference(this.mQuickpaySelectDefaultWayCategory);
            getPreferenceScreen().addPreference(this.mPreferenceDividerLine2);
        } else {
            getPreferenceScreen().addPreference(this.mQuickpayUninstallAppCategory);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        for (int i = 0; i < this.mPayWaysKeyList.size(); i++) {
            if (key.equals(this.mPayWaysKeyList.get(i))) {
                Settings.Secure.putInt(getContentResolver(), "op_quickpay_default_way", this.mPayWaysValueList.get(i).intValue());
                for (RadioButtonPreference radioButtonPreference : this.mAllPayWaysPreference) {
                    radioButtonPreference.setChecked(false);
                }
                ((RadioButtonPreference) preference).setChecked(true);
                return true;
            }
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mSwitchLockscreen) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (!booleanValue || this.mHasFingerprint) {
                updateLockHomeAction(booleanValue);
                return true;
            }
            gotoFingerprintEnrollIntroduction(1);
            return false;
        } else if (preference == this.mSwitchUnlockscreen) {
            boolean booleanValue2 = ((Boolean) obj).booleanValue();
            int longPressHomeActionIndexByValue = getLongPressHomeActionIndexByValue(Settings.System.getIntForUser(getContentResolver(), "key_home_long_press_action", this.mDefaultLongPressOnHomeBehavior, 0));
            if (!booleanValue2 || longPressHomeActionIndexByValue == 0) {
                updateUnLockHomeAction(booleanValue2);
                return true;
            }
            showConfirmChangeHomeAction(booleanValue2, longPressHomeActionIndexByValue);
            return false;
        } else if (preference != this.mFingerprintLongpressQuickpay) {
            return false;
        } else {
            updateUnLockFingerprintLongpressAction(((Boolean) obj).booleanValue());
            return true;
        }
    }

    private void showConfirmChangeHomeAction(final boolean z, int i) {
        String[] strArr = this.mHomeKeyActionName;
        if (i >= strArr.length) {
            Log.e("QuickPaySettings", "longPressHomeActionIndex is out of max length.longPressHomeActionIndex=" + i);
            return;
        }
        String str = strArr[i];
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
        builder.setMessage(this.mActivity.getString(C0017R$string.oneplus_quickpay_confirm_changehomebutton, new Object[]{str}));
        builder.setPositiveButton(this.mActivity.getString(C0017R$string.oneplus_timer_shutdown_position), new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.quickpay.QuickPaySettings.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                QuickPaySettings.this.updateUnLockHomeAction(z);
                QuickPaySettings.this.mSwitchUnlockscreen.setChecked(z);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(this.mActivity.getString(C0017R$string.oneplus_timer_shutdown_nagative), new DialogInterface.OnClickListener(this) {
            /* class com.oneplus.settings.quickpay.QuickPaySettings.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void updateUnLockFingerprintLongpressAction(boolean z) {
        boolean z2 = false;
        Settings.System.putInt(getContentResolver(), "op_fingerprint_long_press_action", z ? 11 : 0);
        if (z || this.mSwitchLockscreen.isChecked()) {
            z2 = true;
        }
        refreshQuickPayEnableUI(z2);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateUnLockHomeAction(boolean z) {
        boolean z2 = false;
        Settings.System.putInt(getContentResolver(), "key_home_long_press_action", z ? 11 : 0);
        if (z || this.mSwitchLockscreen.isChecked()) {
            z2 = true;
        }
        refreshQuickPayEnableUI(z2);
    }

    private void updateLockHomeAction(boolean z) {
        ContentResolver contentResolver = getContentResolver();
        int i = z ? 1 : 0;
        int i2 = z ? 1 : 0;
        int i3 = z ? 1 : 0;
        Settings.Secure.putInt(contentResolver, "op_quickpay_enable", i);
        if (!OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) {
            z = z || this.mSwitchUnlockscreen.isChecked();
        }
        refreshQuickPayEnableUI(z);
    }

    public void gotoFingerprintEnrollIntroduction(int i) {
        Intent intent = new Intent();
        intent.setClassName(OPMemberController.PACKAGE_NAME, FingerprintEnrollIntroduction.class.getName());
        startActivityForResult(intent, i);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1) {
            checkFingerPrint();
            if (this.mHasFingerprint) {
                refreshQuickPayEnableUI(Settings.Secure.putInt(getContentResolver(), "op_quickpay_enable", 1));
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    public static boolean canShowQuickPay(Context context) {
        if (Settings.Secure.getIntForUser(context.getContentResolver(), "op_quickpay_show", 0, 0) == 1) {
            return true;
        }
        boolean isAppExist = OPUtils.isAppExist(context, "com.tencent.mm");
        boolean isAppExist2 = OPUtils.isAppExist(context, "com.eg.android.AlipayGphone");
        boolean isAppExist3 = OPUtils.isAppExist(context, "net.one97.paytm");
        if (isAppExist2 || isAppExist || isAppExist3) {
            return Settings.Secure.putInt(context.getContentResolver(), "op_quickpay_show", 1);
        }
        return false;
    }

    public static void gotoQuickPaySettingsPage(Context context) {
        Intent intent = null;
        try {
            Intent intent2 = new Intent("com.oneplus.action.QUICKPAY_SETTINGS");
            try {
                context.startActivity(intent2);
            } catch (ActivityNotFoundException unused) {
                intent = intent2;
            }
        } catch (ActivityNotFoundException unused2) {
            Log.d("QuickPaySettings", "No activity found for " + intent);
        }
    }

    @Override // com.oneplus.settings.quickpay.QuickPayLottieAnimPreference.OnPreferenceViewClickListener
    public void onPreferenceViewClick(View view) {
        this.mQuickpayInstructions.playOrStopAnim();
    }

    class AppInstallAndUninstallReceiver extends BroadcastReceiver {
        AppInstallAndUninstallReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.PACKAGE_REMOVED".equals(action) || "android.intent.action.PACKAGE_ADDED".equals(action)) {
                String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
                if (!TextUtils.isEmpty(schemeSpecificPart)) {
                    if ("com.tencent.mm".equals(schemeSpecificPart) || "com.eg.android.AlipayGphone".equals(schemeSpecificPart) || "net.one97.paytm".equals(schemeSpecificPart)) {
                        QuickPaySettings.this.updatePreferenceState();
                    }
                }
            }
        }
    }
}
