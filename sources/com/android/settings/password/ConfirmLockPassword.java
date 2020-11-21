package com.android.settings.password;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.UserManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImeAwareEditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.TextViewInputDisabler;
import com.android.settings.C0002R$anim;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.password.ConfirmDeviceCredentialBaseActivity;
import com.android.settings.password.ConfirmLockPassword;
import com.android.settings.password.CredentialCheckResultTracker;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ConfirmLockPassword extends ConfirmDeviceCredentialBaseActivity {
    private static final int[] DETAIL_TEXTS = {C0017R$string.lockpassword_confirm_your_pin_generic, C0017R$string.lockpassword_confirm_your_password_generic, C0017R$string.lockpassword_confirm_your_pin_generic_profile, C0017R$string.lockpassword_confirm_your_password_generic_profile, C0017R$string.lockpassword_strong_auth_required_device_pin, C0017R$string.lockpassword_strong_auth_required_device_password, C0017R$string.lockpassword_strong_auth_required_work_pin, C0017R$string.lockpassword_strong_auth_required_work_password};

    public static class InternalActivity extends ConfirmLockPassword {
    }

    @Override // com.android.settings.SettingsActivity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ConfirmLockPasswordFragment.class.getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ConfirmLockPasswordFragment.class.getName().equals(str);
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(C0010R$id.main_content);
        if (findFragmentById != null && (findFragmentById instanceof ConfirmLockPasswordFragment)) {
            ((ConfirmLockPasswordFragment) findFragmentById).onWindowFocusChanged(z);
        }
    }

    public static class ConfirmLockPasswordFragment extends ConfirmDeviceCredentialBaseFragment implements View.OnClickListener, TextView.OnEditorActionListener, CredentialCheckResultTracker.Listener {
        private AppearAnimationUtils mAppearAnimationUtils;
        private CountDownTimer mCountdownTimer;
        private CredentialCheckResultTracker mCredentialCheckResultTracker;
        private TextView mDetailsTextView;
        private DisappearAnimationUtils mDisappearAnimationUtils;
        private boolean mDisappearing = false;
        private TextView mHeaderTextView;
        private InputMethodManager mImm;
        private boolean mIsAlpha;
        private boolean mIsManagedProfile;
        private ImeAwareEditText mPasswordEntry;
        private TextViewInputDisabler mPasswordEntryInputDisabler;
        private String mPasswordString;
        private AsyncTask<?, ?, ?> mPendingLockCheck;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 30;
        }

        @Override // androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            int i;
            int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mEffectiveUserId);
            if (((ConfirmLockPassword) getActivity()).getConfirmCredentialTheme() == ConfirmDeviceCredentialBaseActivity.ConfirmCredentialTheme.NORMAL) {
                i = C0012R$layout.confirm_lock_password_normal;
            } else {
                i = C0012R$layout.confirm_lock_password;
            }
            View inflate = layoutInflater.inflate(i, viewGroup, false);
            ImeAwareEditText findViewById = inflate.findViewById(C0010R$id.password_entry);
            this.mPasswordEntry = findViewById;
            findViewById.setOnEditorActionListener(this);
            this.mPasswordEntry.requestFocus();
            this.mPasswordEntryInputDisabler = new TextViewInputDisabler(this.mPasswordEntry);
            TextView textView = (TextView) inflate.findViewById(C0010R$id.headerText);
            this.mHeaderTextView = textView;
            if (textView == null) {
                this.mHeaderTextView = (TextView) inflate.findViewById(C0010R$id.suc_layout_title);
            }
            this.mDetailsTextView = (TextView) inflate.findViewById(C0010R$id.sud_layout_description);
            this.mErrorTextView = (TextView) inflate.findViewById(C0010R$id.errorText);
            this.mIsAlpha = 262144 == keyguardStoredPasswordQuality || 327680 == keyguardStoredPasswordQuality || 393216 == keyguardStoredPasswordQuality || 524288 == keyguardStoredPasswordQuality;
            this.mImm = (InputMethodManager) getActivity().getSystemService("input_method");
            this.mIsManagedProfile = UserManager.get(getActivity()).isManagedProfile(this.mEffectiveUserId);
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                CharSequence charSequenceExtra = intent.getCharSequenceExtra("com.android.settings.ConfirmCredentials.header");
                CharSequence charSequenceExtra2 = intent.getCharSequenceExtra("com.android.settings.ConfirmCredentials.details");
                if (TextUtils.isEmpty(charSequenceExtra) && this.mIsManagedProfile) {
                    charSequenceExtra = this.mDevicePolicyManager.getOrganizationNameForUser(this.mUserId);
                }
                if (TextUtils.isEmpty(charSequenceExtra)) {
                    charSequenceExtra = getString(getDefaultHeader());
                }
                if (TextUtils.isEmpty(charSequenceExtra2)) {
                    charSequenceExtra2 = getString(getDefaultDetails());
                }
                this.mHeaderTextView.setText(charSequenceExtra);
                this.mDetailsTextView.setText(charSequenceExtra2);
            }
            int inputType = this.mPasswordEntry.getInputType();
            if (this.mIsAlpha) {
                this.mPasswordEntry.setInputType(inputType);
                this.mPasswordEntry.setContentDescription(getContext().getString(C0017R$string.unlock_set_unlock_password_title));
            } else {
                this.mPasswordEntry.setInputType(18);
                this.mPasswordEntry.setContentDescription(getContext().getString(C0017R$string.unlock_set_unlock_pin_title));
            }
            this.mPasswordEntry.setTypeface(Typeface.create(getContext().getString(17039912), 0));
            this.mAppearAnimationUtils = new AppearAnimationUtils(getContext(), 220, 2.0f, 1.0f, AnimationUtils.loadInterpolator(getContext(), 17563662));
            this.mDisappearAnimationUtils = new DisappearAnimationUtils(getContext(), 110, 1.0f, 0.5f, AnimationUtils.loadInterpolator(getContext(), 17563663));
            setAccessibilityTitle(this.mHeaderTextView.getText());
            CredentialCheckResultTracker credentialCheckResultTracker = (CredentialCheckResultTracker) getFragmentManager().findFragmentByTag("check_lock_result");
            this.mCredentialCheckResultTracker = credentialCheckResultTracker;
            if (credentialCheckResultTracker == null) {
                this.mCredentialCheckResultTracker = new CredentialCheckResultTracker();
                FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
                beginTransaction.add(this.mCredentialCheckResultTracker, "check_lock_result");
                beginTransaction.commit();
            }
            return inflate;
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment, androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            int i;
            super.onViewCreated(view, bundle);
            Button button = this.mForgotButton;
            if (button != null) {
                if (this.mIsAlpha) {
                    i = C0017R$string.lockpassword_forgot_password;
                } else {
                    i = C0017R$string.lockpassword_forgot_pin;
                }
                button.setText(i);
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            ImeAwareEditText imeAwareEditText = this.mPasswordEntry;
            if (imeAwareEditText != null) {
                imeAwareEditText.setText((CharSequence) null);
            }
            System.gc();
            System.runFinalization();
            System.gc();
        }

        private int getDefaultHeader() {
            if (this.mFrp) {
                if (this.mIsAlpha) {
                    return C0017R$string.lockpassword_confirm_your_password_header_frp;
                }
                return C0017R$string.lockpassword_confirm_your_pin_header_frp;
            } else if (this.mIsManagedProfile) {
                if (this.mIsAlpha) {
                    return C0017R$string.lockpassword_confirm_your_work_password_header;
                }
                return C0017R$string.lockpassword_confirm_your_work_pin_header;
            } else if (this.mIsAlpha) {
                return C0017R$string.lockpassword_confirm_your_password_header;
            } else {
                return C0017R$string.lockpassword_confirm_your_pin_header;
            }
        }

        private int getDefaultDetails() {
            if (!this.mFrp) {
                return ConfirmLockPassword.DETAIL_TEXTS[((isStrongAuthRequired() ? 1 : 0) << 2) + ((this.mIsManagedProfile ? 1 : 0) << 1) + (this.mIsAlpha ? 1 : 0)];
            } else if (this.mIsAlpha) {
                return C0017R$string.lockpassword_confirm_your_password_details_frp;
            } else {
                return C0017R$string.lockpassword_confirm_your_pin_details_frp;
            }
        }

        private int getErrorMessage() {
            if (this.mIsAlpha) {
                return C0017R$string.lockpassword_invalid_password;
            }
            return C0017R$string.lockpassword_invalid_pin;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        public int getLastTryErrorMessage(int i) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        throw new IllegalArgumentException("Unrecognized user type:" + i);
                    } else if (this.mIsAlpha) {
                        return C0017R$string.lock_last_password_attempt_before_wipe_user;
                    } else {
                        return C0017R$string.lock_last_pin_attempt_before_wipe_user;
                    }
                } else if (this.mIsAlpha) {
                    return C0017R$string.lock_last_password_attempt_before_wipe_profile;
                } else {
                    return C0017R$string.lock_last_pin_attempt_before_wipe_profile;
                }
            } else if (this.mIsAlpha) {
                return C0017R$string.lock_last_password_attempt_before_wipe_device;
            } else {
                return C0017R$string.lock_last_pin_attempt_before_wipe_device;
            }
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        public void prepareEnterAnimation() {
            super.prepareEnterAnimation();
            this.mHeaderTextView.setAlpha(0.0f);
            this.mDetailsTextView.setAlpha(0.0f);
            this.mCancelButton.setAlpha(0.0f);
            Button button = this.mForgotButton;
            if (button != null) {
                button.setAlpha(0.0f);
            }
            this.mPasswordEntry.setAlpha(0.0f);
            this.mErrorTextView.setAlpha(0.0f);
        }

        private View[] getActiveViews() {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.mHeaderTextView);
            arrayList.add(this.mDetailsTextView);
            if (this.mCancelButton.getVisibility() == 0) {
                arrayList.add(this.mCancelButton);
            }
            Button button = this.mForgotButton;
            if (button != null) {
                arrayList.add(button);
            }
            arrayList.add(this.mPasswordEntry);
            arrayList.add(this.mErrorTextView);
            return (View[]) arrayList.toArray(new View[0]);
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        public void startEnterAnimation() {
            super.startEnterAnimation();
            this.mAppearAnimationUtils.startAnimation(getActiveViews(), new Runnable() {
                /* class com.android.settings.password.$$Lambda$ConfirmLockPassword$ConfirmLockPasswordFragment$Myp25CGN_sn9Gs6wDwuZ61aKfg8 */

                public final void run() {
                    ConfirmLockPassword.ConfirmLockPasswordFragment.this.updatePasswordEntry();
                }
            });
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            CountDownTimer countDownTimer = this.mCountdownTimer;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                this.mCountdownTimer = null;
            }
            this.mCredentialCheckResultTracker.setListener(null);
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            long lockoutAttemptDeadline = this.mLockPatternUtils.getLockoutAttemptDeadline(this.mEffectiveUserId);
            if (lockoutAttemptDeadline != 0) {
                this.mCredentialCheckResultTracker.clearResult();
                handleAttemptLockout(lockoutAttemptDeadline);
            } else {
                updatePasswordEntry();
                this.mErrorTextView.setText("");
                updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId));
            }
            this.mCredentialCheckResultTracker.setListener(this);
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        public void updatePasswordEntry() {
            boolean z = this.mLockPatternUtils.getLockoutAttemptDeadline(this.mEffectiveUserId) != 0;
            this.mPasswordEntry.setEnabled(!z);
            this.mPasswordEntryInputDisabler.setInputEnabled(!z);
            if (z) {
                this.mImm.hideSoftInputFromWindow(this.mPasswordEntry.getWindowToken(), 0);
            } else {
                this.mPasswordEntry.scheduleShowSoftInput();
            }
        }

        public void onWindowFocusChanged(boolean z) {
            if (z) {
                this.mPasswordEntry.post(new Runnable() {
                    /* class com.android.settings.password.$$Lambda$ConfirmLockPassword$ConfirmLockPasswordFragment$Myp25CGN_sn9Gs6wDwuZ61aKfg8 */

                    public final void run() {
                        ConfirmLockPassword.ConfirmLockPasswordFragment.this.updatePasswordEntry();
                    }
                });
            }
        }

        private void handleNext() {
            LockscreenCredential lockscreenCredential;
            if (this.mPendingLockCheck == null && !this.mDisappearing) {
                Editable text = this.mPasswordEntry.getText();
                if (!TextUtils.isEmpty(text)) {
                    if (this.mIsAlpha) {
                        lockscreenCredential = LockscreenCredential.createPassword(text);
                    } else {
                        lockscreenCredential = LockscreenCredential.createPin(text);
                    }
                    this.mPasswordEntryInputDisabler.setInputEnabled(false);
                    boolean booleanExtra = getActivity().getIntent().getBooleanExtra("has_challenge", false);
                    Intent intent = new Intent();
                    if (!booleanExtra) {
                        startCheckPassword(lockscreenCredential, intent);
                    } else if (isInternalActivity()) {
                        startVerifyPassword(lockscreenCredential, intent);
                    } else {
                        this.mCredentialCheckResultTracker.setResult(false, intent, 0, this.mEffectiveUserId);
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private boolean isInternalActivity() {
            return getActivity() instanceof InternalActivity;
        }

        private void startVerifyPassword(LockscreenCredential lockscreenCredential, final Intent intent) {
            AsyncTask<?, ?, ?> asyncTask;
            long longExtra = getActivity().getIntent().getLongExtra("challenge", 0);
            final int i = this.mEffectiveUserId;
            int i2 = this.mUserId;
            AnonymousClass1 r9 = new LockPatternChecker.OnVerifyCallback() {
                /* class com.android.settings.password.ConfirmLockPassword.ConfirmLockPasswordFragment.AnonymousClass1 */

                public void onVerified(byte[] bArr, int i) {
                    boolean z;
                    ConfirmLockPasswordFragment.this.mPendingLockCheck = null;
                    if (bArr != null) {
                        z = true;
                        if (ConfirmLockPasswordFragment.this.mReturnCredentials) {
                            intent.putExtra("hw_auth_token", bArr);
                        }
                    } else {
                        z = false;
                    }
                    ConfirmLockPasswordFragment.this.mCredentialCheckResultTracker.setResult(z, intent, i, i);
                }
            };
            if (i == i2) {
                asyncTask = LockPatternChecker.verifyCredential(this.mLockPatternUtils, lockscreenCredential, longExtra, i2, r9);
            } else {
                asyncTask = LockPatternChecker.verifyTiedProfileChallenge(this.mLockPatternUtils, lockscreenCredential, longExtra, i2, r9);
            }
            this.mPendingLockCheck = asyncTask;
        }

        private void startCheckPassword(final LockscreenCredential lockscreenCredential, final Intent intent) {
            final int i = this.mEffectiveUserId;
            try {
                this.mPasswordString = new String(lockscreenCredential.getCredential(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            this.mPendingLockCheck = LockPatternChecker.checkCredential(this.mLockPatternUtils, lockscreenCredential, i, new LockPatternChecker.OnCheckCallback() {
                /* class com.android.settings.password.ConfirmLockPassword.ConfirmLockPasswordFragment.AnonymousClass2 */

                public void onChecked(boolean z, int i) {
                    ConfirmLockPasswordFragment.this.mPendingLockCheck = null;
                    if (z && ConfirmLockPasswordFragment.this.isInternalActivity()) {
                        ConfirmLockPasswordFragment confirmLockPasswordFragment = ConfirmLockPasswordFragment.this;
                        if (confirmLockPasswordFragment.mReturnCredentials) {
                            intent.putExtra("type", confirmLockPasswordFragment.mIsAlpha ? 0 : 3);
                            intent.putExtra("password", (Parcelable) lockscreenCredential);
                        }
                    }
                    ConfirmLockPasswordFragment.this.mCredentialCheckResultTracker.setResult(z, intent, i, i);
                }
            });
        }

        private void startDisappearAnimation(Intent intent) {
            if (!this.mDisappearing) {
                this.mDisappearing = true;
                ConfirmLockPassword confirmLockPassword = (ConfirmLockPassword) getActivity();
                if (confirmLockPassword != null && !confirmLockPassword.isFinishing()) {
                    if (confirmLockPassword.getConfirmCredentialTheme() == ConfirmDeviceCredentialBaseActivity.ConfirmCredentialTheme.DARK) {
                        this.mDisappearAnimationUtils.startAnimation(getActiveViews(), new Runnable(intent) {
                            /* class com.android.settings.password.$$Lambda$ConfirmLockPassword$ConfirmLockPasswordFragment$hwD4uLqRx_u_wyU3V7MV_afxC5o */
                            public final /* synthetic */ Intent f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                ConfirmLockPassword.ConfirmLockPasswordFragment.lambda$startDisappearAnimation$0(ConfirmLockPassword.this, this.f$1);
                            }
                        });
                        return;
                    }
                    intent.putExtra("power_on_psw", this.mPasswordString);
                    confirmLockPassword.setResult(-1, intent);
                    confirmLockPassword.finish();
                }
            }
        }

        static /* synthetic */ void lambda$startDisappearAnimation$0(ConfirmLockPassword confirmLockPassword, Intent intent) {
            confirmLockPassword.setResult(-1, intent);
            confirmLockPassword.finish();
            confirmLockPassword.overridePendingTransition(C0002R$anim.confirm_credential_close_enter, C0002R$anim.confirm_credential_close_exit);
        }

        private void onPasswordChecked(boolean z, Intent intent, int i, int i2, boolean z2) {
            this.mPasswordEntryInputDisabler.setInputEnabled(true);
            if (z) {
                if (z2) {
                    ConfirmDeviceCredentialUtils.reportSuccessfulAttempt(this.mLockPatternUtils, this.mUserManager, this.mDevicePolicyManager, this.mEffectiveUserId, true);
                }
                startDisappearAnimation(intent);
                ConfirmDeviceCredentialUtils.checkForPendingIntent(getActivity());
                return;
            }
            if (i > 0) {
                refreshLockScreen();
                handleAttemptLockout(this.mLockPatternUtils.setLockoutAttemptDeadline(i2, i));
            } else {
                showError(getErrorMessage(), 3000);
            }
            if (z2) {
                reportFailedAttempt();
            }
        }

        @Override // com.android.settings.password.CredentialCheckResultTracker.Listener
        public void onCredentialChecked(boolean z, Intent intent, int i, int i2, boolean z2) {
            onPasswordChecked(z, intent, i, i2, z2);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        public void onShowError() {
            this.mPasswordEntry.setText((CharSequence) null);
        }

        private void handleAttemptLockout(long j) {
            this.mCountdownTimer = new CountDownTimer(j - SystemClock.elapsedRealtime(), 1000) {
                /* class com.android.settings.password.ConfirmLockPassword.ConfirmLockPasswordFragment.AnonymousClass3 */

                public void onTick(long j) {
                    ConfirmLockPasswordFragment confirmLockPasswordFragment = ConfirmLockPasswordFragment.this;
                    confirmLockPasswordFragment.showError(confirmLockPasswordFragment.getString(C0017R$string.lockpattern_too_many_failed_confirmation_attempts, Integer.valueOf((int) (j / 1000))), 0);
                }

                public void onFinish() {
                    ConfirmLockPasswordFragment.this.updatePasswordEntry();
                    ConfirmLockPasswordFragment.this.mErrorTextView.setText("");
                    ConfirmLockPasswordFragment confirmLockPasswordFragment = ConfirmLockPasswordFragment.this;
                    confirmLockPasswordFragment.updateErrorMessage(confirmLockPasswordFragment.mLockPatternUtils.getCurrentFailedPasswordAttempts(confirmLockPasswordFragment.mEffectiveUserId));
                }
            }.start();
            updatePasswordEntry();
        }

        public void onClick(View view) {
            if (view.getId() == C0010R$id.next_button) {
                handleNext();
            } else if (view.getId() == C0010R$id.cancel_button) {
                getActivity().setResult(0);
                getActivity().finish();
            }
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 0 && i != 6 && i != 5) {
                return false;
            }
            handleNext();
            return true;
        }
    }
}
