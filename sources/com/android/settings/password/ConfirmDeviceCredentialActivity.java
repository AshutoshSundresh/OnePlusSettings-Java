package com.android.settings.password;

import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.fragment.app.FragmentTransaction;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.oneplus.settings.BaseAppCompatActivity;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.utils.OPUtils;
import java.util.concurrent.Executor;

public class ConfirmDeviceCredentialActivity extends BaseAppCompatActivity {
    public static final String TAG = ConfirmDeviceCredentialActivity.class.getSimpleName();
    private BiometricPrompt.AuthenticationCallback mAuthenticationCallback = new BiometricPrompt.AuthenticationCallback() {
        /* class com.android.settings.password.ConfirmDeviceCredentialActivity.AnonymousClass1 */

        public void onAuthenticationError(int i, CharSequence charSequence) {
            if (!ConfirmDeviceCredentialActivity.this.mGoingToBackground) {
                ConfirmDeviceCredentialActivity.this.mWaitingForBiometricCallback = false;
                if (i == 10 || i == 5) {
                    ConfirmDeviceCredentialActivity.this.finish();
                } else {
                    ConfirmDeviceCredentialActivity.this.showConfirmCredentials();
                }
            } else if (ConfirmDeviceCredentialActivity.this.mWaitingForBiometricCallback) {
                ConfirmDeviceCredentialActivity.this.mWaitingForBiometricCallback = false;
                ConfirmDeviceCredentialActivity.this.finish();
            }
        }

        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult authenticationResult) {
            boolean z = false;
            ConfirmDeviceCredentialActivity.this.mWaitingForBiometricCallback = false;
            ConfirmDeviceCredentialActivity.this.mTrustManager.setDeviceLockedForUser(ConfirmDeviceCredentialActivity.this.mUserId, false);
            if (authenticationResult.getAuthenticationType() == 1) {
                z = true;
            }
            ConfirmDeviceCredentialUtils.reportSuccessfulAttempt(ConfirmDeviceCredentialActivity.this.mLockPatternUtils, ConfirmDeviceCredentialActivity.this.mUserManager, ConfirmDeviceCredentialActivity.this.mDevicePolicyManager, ConfirmDeviceCredentialActivity.this.mUserId, z);
            ConfirmDeviceCredentialUtils.checkForPendingIntent(ConfirmDeviceCredentialActivity.this);
            ConfirmDeviceCredentialActivity.this.setResult(-1);
            ConfirmDeviceCredentialActivity.this.finish();
        }

        public void onAuthenticationFailed() {
            ConfirmDeviceCredentialActivity.this.mWaitingForBiometricCallback = false;
            ConfirmDeviceCredentialActivity.this.mDevicePolicyManager.reportFailedBiometricAttempt(ConfirmDeviceCredentialActivity.this.mUserId);
        }

        public void onSystemEvent(int i) {
            String str = ConfirmDeviceCredentialActivity.TAG;
            Log.d(str, "SystemEvent: " + i);
            if (i == 1) {
                ConfirmDeviceCredentialActivity.this.finish();
            }
        }
    };
    private BiometricFragment mBiometricFragment;
    private BiometricManager mBiometricManager;
    private boolean mCheckDevicePolicyManager;
    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    private Context mContext;
    private int mCredentialMode;
    private String mDetails;
    private DevicePolicyManager mDevicePolicyManager;
    private Executor mExecutor = new Executor() {
        /* class com.android.settings.password.$$Lambda$ConfirmDeviceCredentialActivity$VQO3VIMPrDVxMFsVWCFMzR8CT74 */

        public final void execute(Runnable runnable) {
            ConfirmDeviceCredentialActivity.this.lambda$new$0$ConfirmDeviceCredentialActivity(runnable);
        }
    };
    private boolean mGoingToBackground;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LockPatternUtils mLockPatternUtils;
    private String mTitle;
    private TrustManager mTrustManager;
    private int mUserId;
    private UserManager mUserManager;
    private boolean mWaitingForBiometricCallback;

    public static class InternalActivity extends ConfirmDeviceCredentialActivity {
    }

    public static Intent createIntent(CharSequence charSequence, CharSequence charSequence2) {
        Intent intent = new Intent();
        intent.setClassName(OPMemberController.PACKAGE_NAME, ConfirmDeviceCredentialActivity.class.getName());
        intent.putExtra("android.app.extra.TITLE", charSequence);
        intent.putExtra("android.app.extra.DESCRIPTION", charSequence2);
        return intent;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ConfirmDeviceCredentialActivity(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        boolean z;
        String str = TAG;
        OPUtils.setLightNavigationBar(getWindow(), OPUtils.getThemeMode(getContentResolver()));
        super.onCreate(bundle);
        getWindow().addFlags(Integer.MIN_VALUE);
        boolean z2 = false;
        getWindow().setStatusBarColor(0);
        this.mBiometricManager = (BiometricManager) getSystemService(BiometricManager.class);
        this.mDevicePolicyManager = (DevicePolicyManager) getSystemService(DevicePolicyManager.class);
        this.mUserManager = UserManager.get(this);
        this.mTrustManager = (TrustManager) getSystemService(TrustManager.class);
        this.mLockPatternUtils = new LockPatternUtils(this);
        Intent intent = getIntent();
        this.mContext = this;
        this.mCheckDevicePolicyManager = intent.getBooleanExtra("check_dpm", false);
        this.mTitle = intent.getStringExtra("android.app.extra.TITLE");
        this.mDetails = intent.getStringExtra("android.app.extra.DESCRIPTION");
        String stringExtra = intent.getStringExtra("android.app.extra.ALTERNATE_BUTTON_LABEL");
        boolean equals = "android.app.action.CONFIRM_FRP_CREDENTIAL".equals(intent.getAction());
        this.mUserId = UserHandle.myUserId();
        if (isInternalActivity()) {
            try {
                this.mUserId = Utils.getUserIdFromBundle(this, intent.getExtras());
            } catch (SecurityException e) {
                Log.e(str, "Invalid intent extra", e);
            }
        }
        int credentialOwnerProfile = this.mUserManager.getCredentialOwnerProfile(this.mUserId);
        boolean isManagedProfile = UserManager.get(this).isManagedProfile(this.mUserId);
        if (this.mTitle == null && isManagedProfile) {
            this.mTitle = getTitleFromOrganizationName(this.mUserId);
        }
        this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(this);
        LockPatternUtils lockPatternUtils = new LockPatternUtils(this);
        Bundle bundle2 = new Bundle();
        bundle2.putString("title", this.mTitle);
        bundle2.putString("description", this.mDetails);
        bundle2.putBoolean("check_dpm", this.mCheckDevicePolicyManager);
        int credentialType = Utils.getCredentialType(this.mContext, credentialOwnerProfile);
        if (this.mTitle == null) {
            bundle2.putString("device_credential_title", getTitleFromCredentialType(credentialType, isManagedProfile));
        }
        if (this.mDetails == null) {
            bundle2.putString("device_credential_subtitle", getDetailsFromCredentialType(credentialType, isManagedProfile));
        }
        if (equals) {
            z2 = this.mChooseLockSettingsHelper.launchFrpConfirmationActivity(0, this.mTitle, this.mDetails, stringExtra);
            z = false;
        } else {
            if (!isManagedProfile || !isInternalActivity() || lockPatternUtils.isSeparateProfileChallengeEnabled(this.mUserId)) {
                this.mCredentialMode = 1;
                if (isBiometricAllowed(credentialOwnerProfile, this.mUserId)) {
                    showBiometricPrompt(bundle2);
                } else {
                    showConfirmCredentials();
                    z = false;
                    z2 = true;
                }
            } else {
                this.mCredentialMode = 2;
                if (isBiometricAllowed(credentialOwnerProfile, this.mUserId)) {
                    showBiometricPrompt(bundle2);
                } else {
                    showConfirmCredentials();
                    z = false;
                    z2 = true;
                }
            }
            z = true;
        }
        if (z2) {
            finish();
        } else if (z) {
            this.mWaitingForBiometricCallback = true;
        } else {
            Log.d(str, "No pattern, password or PIN set.");
            setResult(-1);
            finish();
        }
    }

    private String getTitleFromCredentialType(int i, boolean z) {
        if (i != 1) {
            if (i != 3) {
                if (i != 4) {
                    return null;
                }
                if (z) {
                    return getString(C0017R$string.lockpassword_confirm_your_work_password_header);
                }
                return getString(C0017R$string.lockpassword_confirm_your_password_header);
            } else if (z) {
                return getString(C0017R$string.lockpassword_confirm_your_work_pin_header);
            } else {
                return getString(C0017R$string.lockpassword_confirm_your_pin_header);
            }
        } else if (z) {
            return getString(C0017R$string.lockpassword_confirm_your_work_pattern_header);
        } else {
            return getString(C0017R$string.lockpassword_confirm_your_pattern_header);
        }
    }

    private String getDetailsFromCredentialType(int i, boolean z) {
        if (i != 1) {
            if (i != 3) {
                if (i != 4) {
                    return null;
                }
                if (z) {
                    return getString(C0017R$string.lockpassword_confirm_your_password_generic_profile);
                }
                return getString(C0017R$string.lockpassword_confirm_your_password_generic);
            } else if (z) {
                return getString(C0017R$string.lockpassword_confirm_your_pin_generic_profile);
            } else {
                return getString(C0017R$string.lockpassword_confirm_your_pin_generic);
            }
        } else if (z) {
            return getString(C0017R$string.lockpassword_confirm_your_pattern_generic_profile);
        } else {
            return getString(C0017R$string.lockpassword_confirm_your_pattern_generic);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStart() {
        super.onStart();
        setVisible(true);
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        if (!isChangingConfigurations()) {
            this.mGoingToBackground = true;
            if (!this.mWaitingForBiometricCallback) {
                finish();
                return;
            }
            return;
        }
        this.mGoingToBackground = false;
    }

    private boolean isStrongAuthRequired(int i) {
        return !this.mLockPatternUtils.isBiometricAllowedForUser(i) || !this.mUserManager.isUserUnlocked(this.mUserId);
    }

    private boolean isBiometricAllowed(int i, int i2) {
        return !isStrongAuthRequired(i) && !this.mLockPatternUtils.hasPendingEscrowToken(i2);
    }

    private void showBiometricPrompt(Bundle bundle) {
        boolean z;
        this.mBiometricManager.setActiveUser(this.mUserId);
        BiometricFragment biometricFragment = (BiometricFragment) getSupportFragmentManager().findFragmentByTag("fragment");
        this.mBiometricFragment = biometricFragment;
        if (biometricFragment == null) {
            this.mBiometricFragment = BiometricFragment.newInstance(bundle);
            z = true;
        } else {
            z = false;
        }
        this.mBiometricFragment.setCallbacks(this.mExecutor, this.mAuthenticationCallback);
        this.mBiometricFragment.setUser(this.mUserId);
        if (z) {
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.add(this.mBiometricFragment, "fragment");
            beginTransaction.commit();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showConfirmCredentials() {
        boolean z;
        int i = this.mCredentialMode;
        if (i == 2) {
            z = this.mChooseLockSettingsHelper.launchConfirmationActivityWithExternalAndChallenge(0, null, this.mTitle, this.mDetails, true, 0, this.mUserId);
        } else {
            z = i == 1 ? this.mChooseLockSettingsHelper.launchConfirmationActivity(0, (CharSequence) null, (CharSequence) this.mTitle, (CharSequence) this.mDetails, false, true, this.mUserId) : false;
        }
        if (!z) {
            Log.d(TAG, "No pin/pattern/pass set");
            setResult(-1);
        }
        finish();
    }

    private boolean isInternalActivity() {
        return this instanceof InternalActivity;
    }

    private String getTitleFromOrganizationName(int i) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService("device_policy");
        CharSequence organizationNameForUser = devicePolicyManager != null ? devicePolicyManager.getOrganizationNameForUser(i) : null;
        if (organizationNameForUser != null) {
            return organizationNameForUser.toString();
        }
        return null;
    }
}
