package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.View;
import com.android.settings.C0017R$string;
import com.android.settings.vpn2.VpnUtils;
import java.util.Date;
import java.util.List;

public class EnterprisePrivacyFeatureProviderImpl implements EnterprisePrivacyFeatureProvider {
    private static final int MY_USER_ID = UserHandle.myUserId();
    private final ConnectivityManager mCm;
    private final Context mContext;
    private final DevicePolicyManager mDpm;
    private final PackageManager mPm;
    private final Resources mResources;
    private final UserManager mUm;

    public EnterprisePrivacyFeatureProviderImpl(Context context, DevicePolicyManager devicePolicyManager, PackageManager packageManager, UserManager userManager, ConnectivityManager connectivityManager, Resources resources) {
        this.mContext = context.getApplicationContext();
        this.mDpm = devicePolicyManager;
        this.mPm = packageManager;
        this.mUm = userManager;
        this.mCm = connectivityManager;
        this.mResources = resources;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean hasDeviceOwner() {
        return getDeviceOwnerComponent() != null;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isInCompMode() {
        return hasDeviceOwner() && getManagedProfileUserId() != -10000;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public String getDeviceOwnerOrganizationName() {
        CharSequence deviceOwnerOrganizationName = this.mDpm.getDeviceOwnerOrganizationName();
        if (deviceOwnerOrganizationName == null) {
            return null;
        }
        return deviceOwnerOrganizationName.toString();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public CharSequence getDeviceOwnerDisclosure() {
        if (!hasDeviceOwner()) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        CharSequence deviceOwnerOrganizationName = this.mDpm.getDeviceOwnerOrganizationName();
        if (deviceOwnerOrganizationName != null) {
            spannableStringBuilder.append((CharSequence) this.mResources.getString(C0017R$string.do_disclosure_with_name, deviceOwnerOrganizationName));
        } else {
            spannableStringBuilder.append((CharSequence) this.mResources.getString(C0017R$string.do_disclosure_generic));
        }
        spannableStringBuilder.append((CharSequence) this.mResources.getString(C0017R$string.do_disclosure_learn_more_separator));
        spannableStringBuilder.append(this.mResources.getString(C0017R$string.learn_more), new EnterprisePrivacySpan(this.mContext), 0);
        return spannableStringBuilder;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastSecurityLogRetrievalTime() {
        long lastSecurityLogRetrievalTime = this.mDpm.getLastSecurityLogRetrievalTime();
        if (lastSecurityLogRetrievalTime < 0) {
            return null;
        }
        return new Date(lastSecurityLogRetrievalTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastBugReportRequestTime() {
        long lastBugReportRequestTime = this.mDpm.getLastBugReportRequestTime();
        if (lastBugReportRequestTime < 0) {
            return null;
        }
        return new Date(lastBugReportRequestTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastNetworkLogRetrievalTime() {
        long lastNetworkLogRetrievalTime = this.mDpm.getLastNetworkLogRetrievalTime();
        if (lastNetworkLogRetrievalTime < 0) {
            return null;
        }
        return new Date(lastNetworkLogRetrievalTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isSecurityLoggingEnabled() {
        return this.mDpm.isSecurityLoggingEnabled(null);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isNetworkLoggingEnabled() {
        return this.mDpm.isNetworkLoggingEnabled(null);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isAlwaysOnVpnSetInCurrentUser() {
        return VpnUtils.isAlwaysOnVpnSet(this.mCm, MY_USER_ID);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isAlwaysOnVpnSetInManagedProfile() {
        int managedProfileUserId = getManagedProfileUserId();
        return managedProfileUserId != -10000 && VpnUtils.isAlwaysOnVpnSet(this.mCm, managedProfileUserId);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isGlobalHttpProxySet() {
        return this.mCm.getGlobalProxy() != null;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getMaximumFailedPasswordsBeforeWipeInCurrentUser() {
        ComponentName deviceOwnerComponentOnCallingUser = this.mDpm.getDeviceOwnerComponentOnCallingUser();
        if (deviceOwnerComponentOnCallingUser == null) {
            deviceOwnerComponentOnCallingUser = this.mDpm.getProfileOwnerAsUser(MY_USER_ID);
        }
        if (deviceOwnerComponentOnCallingUser == null) {
            return 0;
        }
        return this.mDpm.getMaximumFailedPasswordsForWipe(deviceOwnerComponentOnCallingUser, MY_USER_ID);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getMaximumFailedPasswordsBeforeWipeInManagedProfile() {
        ComponentName profileOwnerAsUser;
        int managedProfileUserId = getManagedProfileUserId();
        if (managedProfileUserId == -10000 || (profileOwnerAsUser = this.mDpm.getProfileOwnerAsUser(managedProfileUserId)) == null) {
            return 0;
        }
        return this.mDpm.getMaximumFailedPasswordsForWipe(profileOwnerAsUser, managedProfileUserId);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public String getImeLabelIfOwnerSet() {
        String stringForUser;
        if (!this.mDpm.isCurrentInputMethodSetByOwner() || (stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "default_input_method", MY_USER_ID)) == null) {
            return null;
        }
        try {
            return this.mPm.getApplicationInfoAsUser(stringForUser, 0, MY_USER_ID).loadLabel(this.mPm).toString();
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfOwnerInstalledCaCertsForCurrentUser() {
        List ownerInstalledCaCerts = this.mDpm.getOwnerInstalledCaCerts(new UserHandle(MY_USER_ID));
        if (ownerInstalledCaCerts == null) {
            return 0;
        }
        return ownerInstalledCaCerts.size();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfOwnerInstalledCaCertsForManagedProfile() {
        List ownerInstalledCaCerts;
        int managedProfileUserId = getManagedProfileUserId();
        if (managedProfileUserId == -10000 || (ownerInstalledCaCerts = this.mDpm.getOwnerInstalledCaCerts(new UserHandle(managedProfileUserId))) == null) {
            return 0;
        }
        return ownerInstalledCaCerts.size();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfActiveDeviceAdminsForCurrentUserAndManagedProfile() {
        int i = 0;
        for (UserInfo userInfo : this.mUm.getProfiles(MY_USER_ID)) {
            List activeAdminsAsUser = this.mDpm.getActiveAdminsAsUser(userInfo.id);
            if (!(activeAdminsAsUser == null || userInfo.id == 999)) {
                i += activeAdminsAsUser.size();
            }
        }
        return i;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean hasWorkPolicyInfo() {
        return (getWorkPolicyInfoIntentDO() == null && getWorkPolicyInfoIntentPO() == null) ? false : true;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean showWorkPolicyInfo() {
        Intent workPolicyInfoIntentDO = getWorkPolicyInfoIntentDO();
        if (workPolicyInfoIntentDO != null) {
            this.mContext.startActivity(workPolicyInfoIntentDO);
            return true;
        }
        Intent workPolicyInfoIntentPO = getWorkPolicyInfoIntentPO();
        UserInfo managedProfileUserInfo = getManagedProfileUserInfo();
        if (workPolicyInfoIntentPO == null || managedProfileUserInfo == null) {
            return false;
        }
        this.mContext.startActivityAsUser(workPolicyInfoIntentPO, managedProfileUserInfo.getUserHandle());
        return true;
    }

    private ComponentName getDeviceOwnerComponent() {
        if (!this.mPm.hasSystemFeature("android.software.device_admin")) {
            return null;
        }
        return this.mDpm.getDeviceOwnerComponentOnAnyUser();
    }

    private UserInfo getManagedProfileUserInfo() {
        for (UserInfo userInfo : this.mUm.getProfiles(MY_USER_ID)) {
            if (userInfo.isManagedProfile()) {
                return userInfo;
            }
        }
        return null;
    }

    private int getManagedProfileUserId() {
        UserInfo managedProfileUserInfo = getManagedProfileUserInfo();
        if (managedProfileUserInfo != null) {
            return managedProfileUserInfo.id;
        }
        return -10000;
    }

    private Intent getWorkPolicyInfoIntentDO() {
        ComponentName deviceOwnerComponent = getDeviceOwnerComponent();
        if (deviceOwnerComponent == null) {
            return null;
        }
        Intent addFlags = new Intent("android.settings.SHOW_WORK_POLICY_INFO").setPackage(deviceOwnerComponent.getPackageName()).addFlags(268435456);
        if (this.mPm.queryIntentActivities(addFlags, 0).size() != 0) {
            return addFlags;
        }
        return null;
    }

    private Intent getWorkPolicyInfoIntentPO() {
        ComponentName profileOwnerAsUser;
        int managedProfileUserId = getManagedProfileUserId();
        if (managedProfileUserId == -10000 || (profileOwnerAsUser = this.mDpm.getProfileOwnerAsUser(managedProfileUserId)) == null) {
            return null;
        }
        Intent addFlags = new Intent("android.settings.SHOW_WORK_POLICY_INFO").setPackage(profileOwnerAsUser.getPackageName()).addFlags(268435456);
        if (this.mPm.queryIntentActivitiesAsUser(addFlags, 0, managedProfileUserId).size() != 0) {
            return addFlags;
        }
        return null;
    }

    protected static class EnterprisePrivacySpan extends ClickableSpan {
        private final Context mContext;

        public EnterprisePrivacySpan(Context context) {
            this.mContext = context;
        }

        public void onClick(View view) {
            this.mContext.startActivity(new Intent("android.settings.ENTERPRISE_PRIVACY_SETTINGS").addFlags(268435456));
        }

        public boolean equals(Object obj) {
            return (obj instanceof EnterprisePrivacySpan) && ((EnterprisePrivacySpan) obj).mContext == this.mContext;
        }
    }
}
