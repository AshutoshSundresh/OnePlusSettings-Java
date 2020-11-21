package com.android.settings.applications.specialaccess.deviceadmin;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AppSecurityPermissions;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DeviceAdminAdd extends Activity {
    Button mActionButton;
    TextView mAddMsg;
    boolean mAddMsgEllipsized = true;
    ImageView mAddMsgExpander;
    CharSequence mAddMsgText;
    boolean mAdding;
    boolean mAddingProfileOwner;
    TextView mAdminDescription;
    ImageView mAdminIcon;
    TextView mAdminName;
    ViewGroup mAdminPolicies;
    boolean mAdminPoliciesInitialized;
    TextView mAdminWarning;
    AppOpsManager mAppOps;
    Button mCancelButton;
    DevicePolicyManager mDPM;
    DeviceAdminInfo mDeviceAdmin;
    Handler mHandler;
    boolean mIsCalledFromSupportDialog = false;
    String mProfileOwnerName;
    boolean mRefreshing;
    TextView mSupportMessage;
    private final IBinder mToken = new Binder();
    Button mUninstallButton;
    boolean mUninstalling = false;
    boolean mWaitingForRemoveMsg;

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        r9.activityInfo = r2;
        new android.app.admin.DeviceAdminInfo(r12, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x014a, code lost:
        r13 = true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r13) {
        /*
        // Method dump skipped, instructions count: 988
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd.onCreate(android.os.Bundle):void");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showPolicyTransparencyDialogIfRequired() {
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
        if (isManagedProfile(this.mDeviceAdmin) && this.mDeviceAdmin.getComponent().equals(this.mDPM.getProfileOwner())) {
            ComponentName profileOwnerAsUser = this.mDPM.getProfileOwnerAsUser(getUserId());
            if (profileOwnerAsUser != null && this.mDPM.isOrganizationOwnedDeviceWithManagedProfile()) {
                enforcedAdmin = new RestrictedLockUtils.EnforcedAdmin(profileOwnerAsUser, "no_remove_managed_profile", UserHandle.of(getUserId()));
            } else if (!hasBaseCantRemoveProfileRestriction()) {
                enforcedAdmin = getAdminEnforcingCantRemoveProfile();
            } else {
                return;
            }
            if (enforcedAdmin != null) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this, enforcedAdmin);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void addAndFinish() {
        try {
            logSpecialPermissionChange(true, this.mDeviceAdmin.getComponent().getPackageName());
            this.mDPM.setActiveAdmin(this.mDeviceAdmin.getComponent(), this.mRefreshing);
            EventLog.writeEvent(90201, this.mDeviceAdmin.getActivityInfo().applicationInfo.uid);
            unrestrictAppIfPossible(BatteryUtils.getInstance(this));
            setResult(-1);
        } catch (RuntimeException e) {
            Log.w("DeviceAdminAdd", "Exception trying to activate admin " + this.mDeviceAdmin.getComponent(), e);
            if (this.mDPM.isAdminActive(this.mDeviceAdmin.getComponent())) {
                setResult(-1);
            }
        }
        if (this.mAddingProfileOwner) {
            try {
                this.mDPM.setProfileOwner(this.mDeviceAdmin.getComponent(), this.mProfileOwnerName, UserHandle.myUserId());
            } catch (RuntimeException unused) {
                setResult(0);
            }
        }
        finish();
    }

    /* access modifiers changed from: package-private */
    public void unrestrictAppIfPossible(BatteryUtils batteryUtils) {
        batteryUtils.clearForceAppStandby(this.mDeviceAdmin.getComponent().getPackageName());
    }

    /* access modifiers changed from: package-private */
    public void continueRemoveAction(CharSequence charSequence) {
        if (this.mWaitingForRemoveMsg) {
            this.mWaitingForRemoveMsg = false;
            if (charSequence == null) {
                try {
                    ActivityManager.getService().resumeAppSwitches();
                } catch (RemoteException unused) {
                }
                logSpecialPermissionChange(false, this.mDeviceAdmin.getComponent().getPackageName());
                this.mDPM.removeActiveAdmin(this.mDeviceAdmin.getComponent());
                finish();
                return;
            }
            try {
                ActivityManager.getService().stopAppSwitches();
            } catch (RemoteException unused2) {
            }
            Bundle bundle = new Bundle();
            bundle.putCharSequence("android.app.extra.DISABLE_WARNING", charSequence);
            showDialog(1, bundle);
        }
    }

    /* access modifiers changed from: package-private */
    public void logSpecialPermissionChange(boolean z, String str) {
        FeatureFactory.getFactory(this).getMetricsFeatureProvider().action(0, z ? 766 : 767, 0, str, 0);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mActionButton.setEnabled(true);
        if (!this.mAddingProfileOwner) {
            updateInterface();
        }
        this.mAppOps.setUserRestriction(24, true, this.mToken);
        this.mAppOps.setUserRestriction(45, true, this.mToken);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mActionButton.setEnabled(false);
        this.mAppOps.setUserRestriction(24, false, this.mToken);
        this.mAppOps.setUserRestriction(45, false, this.mToken);
        try {
            ActivityManager.getService().resumeAppSwitches();
        } catch (RemoteException unused) {
        }
    }

    /* access modifiers changed from: protected */
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (this.mIsCalledFromSupportDialog) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int i, Bundle bundle) {
        if (i != 1) {
            return super.onCreateDialog(i, bundle);
        }
        CharSequence charSequence = bundle.getCharSequence("android.app.extra.DISABLE_WARNING");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(charSequence);
        builder.setPositiveButton(C0017R$string.dlg_ok, new DialogInterface.OnClickListener() {
            /* class com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd.AnonymousClass8 */

            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    ActivityManager.getService().resumeAppSwitches();
                } catch (RemoteException unused) {
                }
                DeviceAdminAdd deviceAdminAdd = DeviceAdminAdd.this;
                deviceAdminAdd.mDPM.removeActiveAdmin(deviceAdminAdd.mDeviceAdmin.getComponent());
                DeviceAdminAdd.this.finish();
            }
        });
        builder.setNegativeButton(C0017R$string.dlg_cancel, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    /* access modifiers changed from: package-private */
    public void updateInterface() {
        findViewById(C0010R$id.restricted_icon).setVisibility(8);
        this.mAdminIcon.setImageDrawable(this.mDeviceAdmin.loadIcon(getPackageManager()));
        this.mAdminName.setText(this.mDeviceAdmin.loadLabel(getPackageManager()));
        try {
            this.mAdminDescription.setText(this.mDeviceAdmin.loadDescription(getPackageManager()));
            this.mAdminDescription.setVisibility(0);
        } catch (Resources.NotFoundException unused) {
            this.mAdminDescription.setVisibility(8);
        }
        CharSequence charSequence = this.mAddMsgText;
        if (charSequence != null) {
            this.mAddMsg.setText(charSequence);
            this.mAddMsg.setVisibility(0);
        } else {
            this.mAddMsg.setVisibility(8);
            this.mAddMsgExpander.setVisibility(8);
        }
        boolean z = true;
        if (this.mRefreshing || this.mAddingProfileOwner || !this.mDPM.isAdminActive(this.mDeviceAdmin.getComponent())) {
            addDeviceAdminPolicies(true);
            this.mAdminWarning.setText(getString(C0017R$string.device_admin_warning, new Object[]{this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager())}));
            setTitle(getText(C0017R$string.add_device_admin_msg));
            this.mActionButton.setText(getText(C0017R$string.add_device_admin));
            if (isAdminUninstallable()) {
                this.mUninstallButton.setVisibility(0);
            }
            this.mSupportMessage.setVisibility(8);
            this.mAdding = true;
            return;
        }
        this.mAdding = false;
        boolean equals = this.mDeviceAdmin.getComponent().equals(this.mDPM.getProfileOwner());
        boolean isManagedProfile = isManagedProfile(this.mDeviceAdmin);
        if (equals && isManagedProfile) {
            this.mAdminWarning.setText(C0017R$string.admin_profile_owner_message);
            this.mActionButton.setText(C0017R$string.remove_managed_profile_label);
            RestrictedLockUtils.EnforcedAdmin adminEnforcingCantRemoveProfile = getAdminEnforcingCantRemoveProfile();
            boolean hasBaseCantRemoveProfileRestriction = hasBaseCantRemoveProfileRestriction();
            if ((hasBaseCantRemoveProfileRestriction && this.mDPM.isOrganizationOwnedDeviceWithManagedProfile()) || (adminEnforcingCantRemoveProfile != null && !hasBaseCantRemoveProfileRestriction)) {
                findViewById(C0010R$id.restricted_icon).setVisibility(0);
            }
            Button button = this.mActionButton;
            if (adminEnforcingCantRemoveProfile != null || hasBaseCantRemoveProfileRestriction) {
                z = false;
            }
            button.setEnabled(z);
        } else if (equals || this.mDeviceAdmin.getComponent().equals(this.mDPM.getDeviceOwnerComponentOnCallingUser())) {
            if (equals) {
                this.mAdminWarning.setText(C0017R$string.admin_profile_owner_user_message);
            } else {
                this.mAdminWarning.setText(C0017R$string.admin_device_owner_message);
            }
            this.mActionButton.setText(C0017R$string.remove_device_admin);
            this.mActionButton.setEnabled(false);
        } else {
            addDeviceAdminPolicies(false);
            this.mAdminWarning.setText(getString(C0017R$string.device_admin_status, new Object[]{this.mDeviceAdmin.getActivityInfo().applicationInfo.loadLabel(getPackageManager())}));
            setTitle(C0017R$string.active_device_admin_msg);
            if (this.mUninstalling) {
                this.mActionButton.setText(C0017R$string.remove_and_uninstall_device_admin);
            } else {
                this.mActionButton.setText(C0017R$string.remove_device_admin);
            }
        }
        CharSequence longSupportMessageForUser = this.mDPM.getLongSupportMessageForUser(this.mDeviceAdmin.getComponent(), UserHandle.myUserId());
        if (!TextUtils.isEmpty(longSupportMessageForUser)) {
            this.mSupportMessage.setText(longSupportMessageForUser);
            this.mSupportMessage.setVisibility(0);
            return;
        }
        this.mSupportMessage.setVisibility(8);
    }

    private RestrictedLockUtils.EnforcedAdmin getAdminEnforcingCantRemoveProfile() {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this, "no_remove_managed_profile", getParentUserId());
    }

    private boolean hasBaseCantRemoveProfileRestriction() {
        return RestrictedLockUtilsInternal.hasBaseUserRestriction(this, "no_remove_managed_profile", getParentUserId());
    }

    private int getParentUserId() {
        return UserManager.get(this).getProfileParent(UserHandle.myUserId()).id;
    }

    private void addDeviceAdminPolicies(boolean z) {
        if (!this.mAdminPoliciesInitialized) {
            boolean isAdminUser = UserManager.get(this).isAdminUser();
            Iterator it = this.mDeviceAdmin.getUsedPolicies().iterator();
            while (it.hasNext()) {
                DeviceAdminInfo.PolicyInfo policyInfo = (DeviceAdminInfo.PolicyInfo) it.next();
                this.mAdminPolicies.addView(AppSecurityPermissions.getPermissionItemView(this, getText(isAdminUser ? policyInfo.label : policyInfo.labelForSecondaryUsers), z ? getText(isAdminUser ? policyInfo.description : policyInfo.descriptionForSecondaryUsers) : "", true));
            }
            this.mAdminPoliciesInitialized = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void toggleMessageEllipsis(View view) {
        TextView textView = (TextView) view;
        boolean z = !this.mAddMsgEllipsized;
        this.mAddMsgEllipsized = z;
        textView.setEllipsize(z ? TextUtils.TruncateAt.END : null);
        textView.setMaxLines(this.mAddMsgEllipsized ? getEllipsizedLines() : 15);
        this.mAddMsgExpander.setImageResource(this.mAddMsgEllipsized ? 17302243 : 17302242);
    }

    /* access modifiers changed from: package-private */
    public int getEllipsizedLines() {
        Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        return defaultDisplay.getHeight() > defaultDisplay.getWidth() ? 5 : 2;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isManagedProfile(DeviceAdminInfo deviceAdminInfo) {
        UserInfo userInfo = UserManager.get(this).getUserInfo(UserHandle.getUserId(deviceAdminInfo.getActivityInfo().applicationInfo.uid));
        if (userInfo != null) {
            return userInfo.isManagedProfile();
        }
        return false;
    }

    private Optional<ComponentName> findAdminWithPackageName(String str) {
        List<ComponentName> activeAdmins = this.mDPM.getActiveAdmins();
        if (activeAdmins == null) {
            return Optional.empty();
        }
        return activeAdmins.stream().filter(new Predicate(str) {
            /* class com.android.settings.applications.specialaccess.deviceadmin.$$Lambda$DeviceAdminAdd$juT03BgdUU2vZFkHdvB7Xj_I1dA */
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((ComponentName) obj).getPackageName().equals(this.f$0);
            }
        }).findAny();
    }

    private boolean isAdminUninstallable() {
        return !this.mDeviceAdmin.getActivityInfo().applicationInfo.isSystemApp();
    }
}
