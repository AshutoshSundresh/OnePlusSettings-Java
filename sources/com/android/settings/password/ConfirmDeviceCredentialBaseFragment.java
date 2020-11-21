package com.android.settings.password;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.password.ConfirmLockPassword;
import com.android.settings.password.ConfirmLockPattern;
import com.oneplus.settings.OPMemberController;

public abstract class ConfirmDeviceCredentialBaseFragment extends InstrumentedFragment {
    public static final String TAG = ConfirmDeviceCredentialBaseFragment.class.getSimpleName();
    protected Button mCancelButton;
    protected DevicePolicyManager mDevicePolicyManager;
    protected int mEffectiveUserId;
    protected TextView mErrorTextView;
    protected Button mForgotButton;
    protected boolean mFrp;
    private CharSequence mFrpAlternateButtonText;
    protected final Handler mHandler = new Handler();
    protected LockPatternUtils mLockPatternUtils;
    private final Runnable mResetErrorRunnable = new Runnable() {
        /* class com.android.settings.password.ConfirmDeviceCredentialBaseFragment.AnonymousClass1 */

        public void run() {
            ConfirmDeviceCredentialBaseFragment.this.mErrorTextView.setText("");
        }
    };
    protected boolean mReturnCredentials = false;
    protected int mUserId;
    protected UserManager mUserManager;

    /* access modifiers changed from: protected */
    public abstract int getLastTryErrorMessage(int i);

    /* access modifiers changed from: protected */
    public abstract void onShowError();

    public void prepareEnterAnimation() {
    }

    public void startEnterAnimation() {
    }

    private boolean isInternalActivity() {
        return (getActivity() instanceof ConfirmLockPassword.InternalActivity) || (getActivity() instanceof ConfirmLockPattern.InternalActivity);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mFrpAlternateButtonText = getActivity().getIntent().getCharSequenceExtra("android.app.extra.ALTERNATE_BUTTON_LABEL");
        boolean z = false;
        this.mReturnCredentials = getActivity().getIntent().getBooleanExtra("return_credentials", false);
        int userIdFromBundle = Utils.getUserIdFromBundle(getActivity(), getActivity().getIntent().getExtras(), isInternalActivity());
        this.mUserId = userIdFromBundle;
        if (userIdFromBundle == -9999) {
            z = true;
        }
        this.mFrp = z;
        UserManager userManager = UserManager.get(getActivity());
        this.mUserManager = userManager;
        this.mEffectiveUserId = userManager.getCredentialOwnerProfile(this.mUserId);
        this.mLockPatternUtils = new LockPatternUtils(getActivity());
        this.mDevicePolicyManager = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        BiometricManager biometricManager = (BiometricManager) getActivity().getSystemService(BiometricManager.class);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mCancelButton = (Button) view.findViewById(C0010R$id.cancelButton);
        int i = 0;
        boolean booleanExtra = getActivity().getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.showCancelButton", false);
        boolean z = this.mFrp && !TextUtils.isEmpty(this.mFrpAlternateButtonText);
        Button button = this.mCancelButton;
        if (!booleanExtra && !z) {
            i = 8;
        }
        button.setVisibility(i);
        if (z) {
            this.mCancelButton.setText(this.mFrpAlternateButtonText);
        }
        this.mCancelButton.setOnClickListener(new View.OnClickListener(z) {
            /* class com.android.settings.password.$$Lambda$ConfirmDeviceCredentialBaseFragment$hZ7B9euTajzJPRz2eeO181DHI4 */
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ConfirmDeviceCredentialBaseFragment.this.lambda$onViewCreated$0$ConfirmDeviceCredentialBaseFragment(this.f$1, view);
            }
        });
        setupForgotButtonIfManagedProfile(view);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onViewCreated$0 */
    public /* synthetic */ void lambda$onViewCreated$0$ConfirmDeviceCredentialBaseFragment(boolean z, View view) {
        if (z) {
            getActivity().setResult(1);
        }
        getActivity().finish();
    }

    private void setupForgotButtonIfManagedProfile(View view) {
        if (this.mUserManager.isManagedProfile(this.mUserId) && this.mUserManager.isQuietModeEnabled(UserHandle.of(this.mUserId)) && this.mDevicePolicyManager.canProfileOwnerResetPasswordWhenLocked(this.mUserId)) {
            Button button = (Button) view.findViewById(C0010R$id.forgotButton);
            this.mForgotButton = button;
            if (button == null) {
                Log.wtf(TAG, "Forgot button not found in managed profile credential dialog");
                return;
            }
            button.setVisibility(0);
            this.mForgotButton.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.password.$$Lambda$ConfirmDeviceCredentialBaseFragment$NR3zizZGY1VqoYRnW6qkNakHykg */

                public final void onClick(View view) {
                    ConfirmDeviceCredentialBaseFragment.this.lambda$setupForgotButtonIfManagedProfile$1$ConfirmDeviceCredentialBaseFragment(view);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setupForgotButtonIfManagedProfile$1 */
    public /* synthetic */ void lambda$setupForgotButtonIfManagedProfile$1$ConfirmDeviceCredentialBaseFragment(View view) {
        Intent intent = new Intent();
        intent.setClassName(OPMemberController.PACKAGE_NAME, ForgotPasswordActivity.class.getName());
        intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    /* access modifiers changed from: protected */
    public boolean isStrongAuthRequired() {
        return this.mFrp || !this.mLockPatternUtils.isBiometricAllowedForUser(this.mEffectiveUserId) || !this.mUserManager.isUserUnlocked(this.mUserId);
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refreshLockScreen();
    }

    /* access modifiers changed from: protected */
    public void refreshLockScreen() {
        updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId));
    }

    /* access modifiers changed from: protected */
    public void setAccessibilityTitle(CharSequence charSequence) {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            CharSequence charSequenceExtra = intent.getCharSequenceExtra("com.android.settings.ConfirmCredentials.title");
            if (charSequence != null) {
                if (charSequenceExtra == null) {
                    getActivity().setTitle(charSequence);
                    return;
                }
                getActivity().setTitle(Utils.createAccessibleSequence(charSequenceExtra, charSequenceExtra + "," + charSequence));
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void reportFailedAttempt() {
        updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId) + 1);
        this.mLockPatternUtils.reportFailedPasswordAttempt(this.mEffectiveUserId);
    }

    /* access modifiers changed from: protected */
    public void updateErrorMessage(int i) {
        int maximumFailedPasswordsForWipe = this.mLockPatternUtils.getMaximumFailedPasswordsForWipe(this.mEffectiveUserId);
        if (maximumFailedPasswordsForWipe > 0 && i > 0) {
            if (this.mErrorTextView != null) {
                showError(getActivity().getString(C0017R$string.lock_failed_attempts_before_wipe, new Object[]{Integer.valueOf(i), Integer.valueOf(maximumFailedPasswordsForWipe)}), 0);
            }
            int i2 = maximumFailedPasswordsForWipe - i;
            if (i2 <= 1) {
                FragmentManager childFragmentManager = getChildFragmentManager();
                int userTypeForWipe = getUserTypeForWipe();
                if (i2 == 1) {
                    LastTryDialog.show(childFragmentManager, getActivity().getString(C0017R$string.lock_last_attempt_before_wipe_warning_title), getLastTryErrorMessage(userTypeForWipe), 17039370, false);
                } else {
                    LastTryDialog.show(childFragmentManager, null, getWipeMessage(userTypeForWipe), C0017R$string.lock_failed_attempts_now_wiping_dialog_dismiss, true);
                }
            }
        }
    }

    private int getUserTypeForWipe() {
        UserInfo userInfo = this.mUserManager.getUserInfo(this.mDevicePolicyManager.getProfileWithMinimumFailedPasswordsForWipe(this.mEffectiveUserId));
        if (userInfo == null || userInfo.isPrimary()) {
            return 1;
        }
        return userInfo.isManagedProfile() ? 2 : 3;
    }

    private int getWipeMessage(int i) {
        if (i == 1) {
            return C0017R$string.lock_failed_attempts_now_wiping_device;
        }
        if (i == 2) {
            return C0017R$string.lock_failed_attempts_now_wiping_profile;
        }
        if (i == 3) {
            return C0017R$string.lock_failed_attempts_now_wiping_user;
        }
        throw new IllegalArgumentException("Unrecognized user type:" + i);
    }

    /* access modifiers changed from: protected */
    public void showError(CharSequence charSequence, long j) {
        this.mErrorTextView.setText(charSequence);
        onShowError();
        this.mHandler.removeCallbacks(this.mResetErrorRunnable);
        if (j != 0) {
            this.mHandler.postDelayed(this.mResetErrorRunnable, j);
        }
    }

    /* access modifiers changed from: protected */
    public void showError(int i, long j) {
        showError(getText(i), j);
    }

    public static class LastTryDialog extends DialogFragment {
        private static final String TAG = LastTryDialog.class.getSimpleName();

        static boolean show(FragmentManager fragmentManager, String str, int i, int i2, boolean z) {
            String str2 = TAG;
            LastTryDialog lastTryDialog = (LastTryDialog) fragmentManager.findFragmentByTag(str2);
            if (lastTryDialog != null && !lastTryDialog.isRemoving()) {
                return false;
            }
            Bundle bundle = new Bundle();
            bundle.putString("title", str);
            bundle.putInt("message", i);
            bundle.putInt("button", i2);
            bundle.putBoolean("dismiss", z);
            LastTryDialog lastTryDialog2 = new LastTryDialog();
            lastTryDialog2.setArguments(bundle);
            lastTryDialog2.show(fragmentManager, str2);
            fragmentManager.executePendingTransactions();
            return true;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getArguments().getString("title"));
            builder.setMessage(getArguments().getInt("message"));
            builder.setPositiveButton(getArguments().getInt("button"), (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            create.setCanceledOnTouchOutside(false);
            return create;
        }

        @Override // androidx.fragment.app.DialogFragment
        public void onDismiss(DialogInterface dialogInterface) {
            super.onDismiss(dialogInterface);
            if (getActivity() != null && getArguments().getBoolean("dismiss")) {
                getActivity().finish();
            }
        }
    }
}
