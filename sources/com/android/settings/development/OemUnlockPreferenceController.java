package com.android.settings.development;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.oemlock.OemLockManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;
import com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockService;
import com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockServiceCallback;
import java.util.List;

public class OemUnlockPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin, OnActivityResultListener {
    private boolean isCarrierDevices;
    private boolean isUimLocked;
    private Activity mActivity;
    private final ChooseLockSettingsHelper mChooseLockSettingsHelper;
    private final DevelopmentSettingsDashboardFragment mFragment;
    private final OemLockManager mOemLockManager;
    private RestrictedSwitchPreference mPreference;
    private ServiceConnection mSimlockConnection;
    private final TelephonyManager mTelephonyManager;
    private final UserManager mUserManager;
    private IUimRemoteSimlockService uimRemoteSimlockService;
    private IUimRemoteSimlockServiceCallback uimRemoteSimlockServiceCallback;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "oem_unlock_enable";
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00b9  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x004e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public OemUnlockPreferenceController(android.content.Context r6, android.app.Activity r7, com.android.settings.development.DevelopmentSettingsDashboardFragment r8) {
        /*
        // Method dump skipped, instructions count: 193
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.development.OemUnlockPreferenceController.<init>(android.content.Context, android.app.Activity, com.android.settings.development.DevelopmentSettingsDashboardFragment):void");
    }

    public void unBindSimlockConnection() {
        try {
            if (this.uimRemoteSimlockService != null) {
                this.uimRemoteSimlockService.deregisterCallback(this.uimRemoteSimlockServiceCallback);
                this.mContext.unbindService(this.mSimlockConnection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("OemUnlockPreferenceController", "unbindService mSimlockConnection.");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return !ProductUtils.isUsvMode() && this.mOemLockManager != null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!((Boolean) obj).booleanValue()) {
            this.mOemLockManager.setOemUnlockAllowedByUser(false);
            OemLockInfoDialog.show(this.mFragment);
            return true;
        } else if (showKeyguardConfirmation(this.mContext.getResources(), 0)) {
            return true;
        } else {
            confirmEnableOemUnlock();
            return true;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference.setChecked(isOemUnlockedAllowed());
        updateOemUnlockSettingDescription();
        this.mPreference.setDisabledByAdmin(null);
        this.mPreference.setEnabled(enableOemUnlockPreference());
        if (this.mPreference.isEnabled()) {
            this.mPreference.checkRestrictionAndSetDisabled("no_factory_reset");
        }
    }

    @Override // com.android.settings.development.OnActivityResultListener
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i != 0) {
            return false;
        }
        if (i2 != -1) {
            return true;
        }
        if (this.mPreference.isChecked()) {
            confirmEnableOemUnlock();
            return true;
        }
        this.mOemLockManager.setOemUnlockAllowedByUser(false);
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchEnabled() {
        handleDeveloperOptionsToggled();
    }

    public void onOemUnlockConfirmed() {
        this.mOemLockManager.setOemUnlockAllowedByUser(true);
    }

    public void onOemUnlockDismissed() {
        RestrictedSwitchPreference restrictedSwitchPreference = this.mPreference;
        if (restrictedSwitchPreference != null) {
            updateState(restrictedSwitchPreference);
        }
    }

    private void handleDeveloperOptionsToggled() {
        this.mPreference.setEnabled(enableOemUnlockPreference());
        if (this.mPreference.isEnabled()) {
            this.mPreference.checkRestrictionAndSetDisabled("no_factory_reset");
        }
    }

    private void updateOemUnlockSettingDescription() {
        int i = C0017R$string.oem_unlock_enable_summary;
        if (isBootloaderUnlocked()) {
            i = C0017R$string.oem_unlock_enable_disabled_summary_bootloader_unlocked;
        } else if (isSimLockedDevice()) {
            i = C0017R$string.oem_unlock_enable_disabled_summary_sim_locked_device;
        } else if (!isOemUnlockAllowedByUserAndCarrier()) {
            i = C0017R$string.oem_unlock_enable_disabled_summary_connectivity_or_locked;
        }
        this.mPreference.setSummary(this.mContext.getResources().getString(i));
    }

    private boolean isSimLockedDevice() {
        int phoneCount = this.mTelephonyManager.getPhoneCount();
        for (int i = 0; i < phoneCount; i++) {
            if (this.mTelephonyManager.getAllowedCarriers(i).size() > 0) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isBootloaderUnlocked() {
        return this.mOemLockManager.isDeviceOemUnlocked();
    }

    private boolean enableOemUnlockPreference() {
        return !isBootloaderUnlocked() && isOemUnlockAllowedByUserAndCarrier() && !this.isUimLocked && !OPUtils.isSupportUssOnly();
    }

    /* access modifiers changed from: package-private */
    public boolean showKeyguardConfirmation(Resources resources, int i) {
        return this.mChooseLockSettingsHelper.launchConfirmationActivity(i, resources.getString(C0017R$string.oem_unlock_enable));
    }

    /* access modifiers changed from: package-private */
    public void confirmEnableOemUnlock() {
        EnableOemUnlockSettingWarningDialog.show(this.mFragment);
    }

    /* access modifiers changed from: package-private */
    public boolean isOemUnlockAllowedByUserAndCarrier() {
        return this.mOemLockManager.isOemUnlockAllowedByCarrier() && !this.mUserManager.hasBaseUserRestriction("no_factory_reset", UserHandle.of(UserHandle.myUserId()));
    }

    /* access modifiers changed from: package-private */
    public boolean isOemUnlockedAllowed() {
        return this.mOemLockManager.isOemUnlockAllowed();
    }

    public static boolean isUimLockServiceEnable(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.qualcomm.qti.uim", "com.qualcomm.qti.uim.RemoteSimLockService"));
        List<ResolveInfo> queryIntentServices = context.getPackageManager().queryIntentServices(intent, 0);
        if (queryIntentServices == null || queryIntentServices.size() <= 0) {
            return false;
        }
        return true;
    }
}
