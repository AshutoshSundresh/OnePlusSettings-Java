package com.android.settings.password;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.UserManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.FragmentTransaction;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockscreenCredential;
import com.android.settings.C0002R$anim;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.password.ConfirmDeviceCredentialBaseActivity;
import com.android.settings.password.ConfirmLockPattern;
import com.android.settings.password.CredentialCheckResultTracker;
import com.android.settingslib.animation.AppearAnimationCreator;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfirmLockPattern extends ConfirmDeviceCredentialBaseActivity {

    public static class InternalActivity extends ConfirmLockPattern {
    }

    /* access modifiers changed from: private */
    public enum Stage {
        NeedToUnlock,
        NeedToUnlockWrong,
        LockedOut
    }

    @Override // com.android.settings.SettingsActivity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ConfirmLockPatternFragment.class.getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ConfirmLockPatternFragment.class.getName().equals(str);
    }

    public static class ConfirmLockPatternFragment extends ConfirmDeviceCredentialBaseFragment implements AppearAnimationCreator<Object>, CredentialCheckResultTracker.Listener {
        private AppearAnimationUtils mAppearAnimationUtils;
        private Runnable mClearPatternRunnable = new Runnable() {
            /* class com.android.settings.password.ConfirmLockPattern.ConfirmLockPatternFragment.AnonymousClass2 */

            public void run() {
                ConfirmLockPatternFragment.this.mLockPatternView.clearPattern();
            }
        };
        private LockPatternView.OnPatternListener mConfirmExistingLockPatternListener = new LockPatternView.OnPatternListener() {
            /* class com.android.settings.password.ConfirmLockPattern.ConfirmLockPatternFragment.AnonymousClass3 */

            public void onPatternCellAdded(List<LockPatternView.Cell> list) {
            }

            public void onPatternStart() {
                ConfirmLockPatternFragment.this.mLockPatternView.removeCallbacks(ConfirmLockPatternFragment.this.mClearPatternRunnable);
            }

            public void onPatternCleared() {
                ConfirmLockPatternFragment.this.mLockPatternView.removeCallbacks(ConfirmLockPatternFragment.this.mClearPatternRunnable);
            }

            public void onPatternDetected(List<LockPatternView.Cell> list) {
                if (ConfirmLockPatternFragment.this.mPendingLockCheck == null && !ConfirmLockPatternFragment.this.mDisappearing) {
                    ConfirmLockPatternFragment.this.mLockPatternView.setEnabled(false);
                    boolean booleanExtra = ConfirmLockPatternFragment.this.getActivity().getIntent().getBooleanExtra("has_challenge", false);
                    LockscreenCredential createPattern = LockscreenCredential.createPattern(list);
                    ConfirmLockPatternFragment.this.mPattenString = new String(LockPatternUtils.patternToByteArray(list));
                    Intent intent = new Intent();
                    if (!booleanExtra) {
                        startCheckPattern(createPattern, intent);
                    } else if (isInternalActivity()) {
                        startVerifyPattern(createPattern, intent);
                    } else {
                        ConfirmLockPatternFragment.this.mCredentialCheckResultTracker.setResult(false, intent, 0, ConfirmLockPatternFragment.this.mEffectiveUserId);
                    }
                }
            }

            /* access modifiers changed from: private */
            /* access modifiers changed from: public */
            private boolean isInternalActivity() {
                return ConfirmLockPatternFragment.this.getActivity() instanceof InternalActivity;
            }

            private void startVerifyPattern(LockscreenCredential lockscreenCredential, final Intent intent) {
                AsyncTask asyncTask;
                ConfirmLockPatternFragment confirmLockPatternFragment = ConfirmLockPatternFragment.this;
                final int i = confirmLockPatternFragment.mEffectiveUserId;
                int i2 = confirmLockPatternFragment.mUserId;
                long longExtra = confirmLockPatternFragment.getActivity().getIntent().getLongExtra("challenge", 0);
                AnonymousClass1 r7 = new LockPatternChecker.OnVerifyCallback() {
                    /* class com.android.settings.password.ConfirmLockPattern.ConfirmLockPatternFragment.AnonymousClass3.AnonymousClass1 */

                    public void onVerified(byte[] bArr, int i) {
                        boolean z;
                        ConfirmLockPatternFragment.this.mPendingLockCheck = null;
                        if (bArr != null) {
                            z = true;
                            if (ConfirmLockPatternFragment.this.mReturnCredentials) {
                                intent.putExtra("hw_auth_token", bArr);
                            }
                        } else {
                            z = false;
                        }
                        ConfirmLockPatternFragment.this.mCredentialCheckResultTracker.setResult(z, intent, i, i);
                    }
                };
                ConfirmLockPatternFragment confirmLockPatternFragment2 = ConfirmLockPatternFragment.this;
                if (i == i2) {
                    asyncTask = LockPatternChecker.verifyCredential(confirmLockPatternFragment2.mLockPatternUtils, lockscreenCredential, longExtra, i2, r7);
                } else {
                    asyncTask = LockPatternChecker.verifyTiedProfileChallenge(confirmLockPatternFragment2.mLockPatternUtils, lockscreenCredential, longExtra, i2, r7);
                }
                confirmLockPatternFragment2.mPendingLockCheck = asyncTask;
            }

            private void startCheckPattern(final LockscreenCredential lockscreenCredential, final Intent intent) {
                if (lockscreenCredential.size() < 4) {
                    ConfirmLockPatternFragment confirmLockPatternFragment = ConfirmLockPatternFragment.this;
                    confirmLockPatternFragment.onPatternChecked(false, intent, 0, confirmLockPatternFragment.mEffectiveUserId, false);
                    return;
                }
                ConfirmLockPatternFragment confirmLockPatternFragment2 = ConfirmLockPatternFragment.this;
                final int i = confirmLockPatternFragment2.mEffectiveUserId;
                confirmLockPatternFragment2.mPendingLockCheck = LockPatternChecker.checkCredential(confirmLockPatternFragment2.mLockPatternUtils, lockscreenCredential, i, new LockPatternChecker.OnCheckCallback() {
                    /* class com.android.settings.password.ConfirmLockPattern.ConfirmLockPatternFragment.AnonymousClass3.AnonymousClass2 */

                    public void onChecked(boolean z, int i) {
                        ConfirmLockPatternFragment.this.mPendingLockCheck = null;
                        if (z && AnonymousClass3.this.isInternalActivity() && ConfirmLockPatternFragment.this.mReturnCredentials) {
                            intent.putExtra("type", 2);
                            intent.putExtra("password", (Parcelable) lockscreenCredential);
                        }
                        ConfirmLockPatternFragment.this.mCredentialCheckResultTracker.setResult(z, intent, i, i);
                    }
                });
            }
        };
        private CountDownTimer mCountdownTimer;
        private CredentialCheckResultTracker mCredentialCheckResultTracker;
        private CharSequence mDetailsText;
        private TextView mDetailsTextView;
        private DisappearAnimationUtils mDisappearAnimationUtils;
        private boolean mDisappearing = false;
        private CharSequence mHeaderText;
        private TextView mHeaderTextView;
        private boolean mIsManagedProfile;
        private LockPatternView mLockPatternView;
        private String mPattenString;
        private AsyncTask<?, ?, ?> mPendingLockCheck;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 31;
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        public void onShowError() {
        }

        @Override // androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            int i;
            if (((ConfirmLockPattern) getActivity()).getConfirmCredentialTheme() == ConfirmDeviceCredentialBaseActivity.ConfirmCredentialTheme.NORMAL) {
                i = C0012R$layout.confirm_lock_pattern_normal;
            } else {
                i = C0012R$layout.confirm_lock_pattern;
            }
            View inflate = layoutInflater.inflate(i, viewGroup, false);
            this.mHeaderTextView = (TextView) inflate.findViewById(C0010R$id.headerText);
            this.mLockPatternView = inflate.findViewById(C0010R$id.lockPattern);
            this.mDetailsTextView = (TextView) inflate.findViewById(C0010R$id.sud_layout_description);
            this.mErrorTextView = (TextView) inflate.findViewById(C0010R$id.errorText);
            this.mIsManagedProfile = UserManager.get(getActivity()).isManagedProfile(this.mEffectiveUserId);
            inflate.findViewById(C0010R$id.topLayout).setDefaultTouchRecepient(this.mLockPatternView);
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                this.mHeaderText = intent.getCharSequenceExtra("com.android.settings.ConfirmCredentials.header");
                this.mDetailsText = intent.getCharSequenceExtra("com.android.settings.ConfirmCredentials.details");
            }
            if (TextUtils.isEmpty(this.mHeaderText) && this.mIsManagedProfile) {
                this.mHeaderText = this.mDevicePolicyManager.getOrganizationNameForUser(this.mUserId);
            }
            this.mLockPatternView.setTactileFeedbackEnabled(this.mLockPatternUtils.isTactileFeedbackEnabled());
            this.mLockPatternView.setInStealthMode(!this.mLockPatternUtils.isVisiblePatternEnabled(this.mEffectiveUserId));
            this.mLockPatternView.setOnPatternListener(this.mConfirmExistingLockPatternListener);
            updateStage(Stage.NeedToUnlock);
            if (bundle == null && !this.mFrp && !this.mLockPatternUtils.isLockPatternEnabled(this.mEffectiveUserId)) {
                getActivity().setResult(-1);
                getActivity().finish();
            }
            this.mAppearAnimationUtils = new AppearAnimationUtils(getContext(), 220, 2.0f, 1.3f, AnimationUtils.loadInterpolator(getContext(), 17563662));
            this.mDisappearAnimationUtils = new DisappearAnimationUtils(getContext(), 125, 4.0f, 0.3f, AnimationUtils.loadInterpolator(getContext(), 17563663), new AppearAnimationUtils.RowTranslationScaler(this) {
                /* class com.android.settings.password.ConfirmLockPattern.ConfirmLockPatternFragment.AnonymousClass1 */

                @Override // com.android.settingslib.animation.AppearAnimationUtils.RowTranslationScaler
                public float getRowTranslationScale(int i, int i2) {
                    return ((float) (i2 - i)) / ((float) i2);
                }
            });
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
            super.onViewCreated(view, bundle);
            Button button = this.mForgotButton;
            if (button != null) {
                button.setText(C0017R$string.lockpassword_forgot_pattern);
            }
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            CountDownTimer countDownTimer = this.mCountdownTimer;
            if (countDownTimer != null) {
                countDownTimer.cancel();
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
            } else if (!this.mLockPatternView.isEnabled()) {
                updateStage(Stage.NeedToUnlock);
            }
            this.mCredentialCheckResultTracker.setListener(this);
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        public void prepareEnterAnimation() {
            super.prepareEnterAnimation();
            this.mHeaderTextView.setAlpha(0.0f);
            this.mCancelButton.setAlpha(0.0f);
            Button button = this.mForgotButton;
            if (button != null) {
                button.setAlpha(0.0f);
            }
            this.mLockPatternView.setAlpha(0.0f);
            this.mDetailsTextView.setAlpha(0.0f);
        }

        private int getDefaultDetails() {
            if (this.mFrp) {
                return C0017R$string.lockpassword_confirm_your_pattern_details_frp;
            }
            boolean isStrongAuthRequired = isStrongAuthRequired();
            if (this.mIsManagedProfile) {
                if (isStrongAuthRequired) {
                    return C0017R$string.lockpassword_strong_auth_required_work_pattern;
                }
                return C0017R$string.lockpassword_confirm_your_pattern_generic_profile;
            } else if (isStrongAuthRequired) {
                return C0017R$string.lockpassword_strong_auth_required_device_pattern;
            } else {
                return C0017R$string.lockpassword_confirm_your_pattern_generic;
            }
        }

        private Object[][] getActiveViews() {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new ArrayList(Collections.singletonList(this.mHeaderTextView)));
            arrayList.add(new ArrayList(Collections.singletonList(this.mDetailsTextView)));
            if (this.mCancelButton.getVisibility() == 0) {
                arrayList.add(new ArrayList(Collections.singletonList(this.mCancelButton)));
            }
            if (this.mForgotButton != null) {
                arrayList.add(new ArrayList(Collections.singletonList(this.mForgotButton)));
            }
            LockPatternView.CellState[][] cellStates = this.mLockPatternView.getCellStates();
            for (int i = 0; i < cellStates.length; i++) {
                ArrayList arrayList2 = new ArrayList();
                for (int i2 = 0; i2 < cellStates[i].length; i2++) {
                    arrayList2.add(cellStates[i][i2]);
                }
                arrayList.add(arrayList2);
            }
            int size = arrayList.size();
            int[] iArr = new int[2];
            iArr[1] = cellStates[0].length;
            iArr[0] = size;
            Object[][] objArr = (Object[][]) Array.newInstance(Object.class, iArr);
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                ArrayList arrayList3 = (ArrayList) arrayList.get(i3);
                for (int i4 = 0; i4 < arrayList3.size(); i4++) {
                    objArr[i3][i4] = arrayList3.get(i4);
                }
            }
            return objArr;
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        public void startEnterAnimation() {
            super.startEnterAnimation();
            this.mLockPatternView.setAlpha(1.0f);
            this.mAppearAnimationUtils.startAnimation2d(getActiveViews(), null, this);
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void updateStage(Stage stage) {
            int i = AnonymousClass1.$SwitchMap$com$android$settings$password$ConfirmLockPattern$Stage[stage.ordinal()];
            if (i == 1) {
                CharSequence charSequence = this.mHeaderText;
                if (charSequence != null) {
                    this.mHeaderTextView.setText(charSequence);
                } else {
                    this.mHeaderTextView.setText(getDefaultHeader());
                }
                CharSequence charSequence2 = this.mDetailsText;
                if (charSequence2 != null) {
                    this.mDetailsTextView.setText(charSequence2);
                } else {
                    this.mDetailsTextView.setText(getDefaultDetails());
                }
                this.mErrorTextView.setText("");
                updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId));
                this.mLockPatternView.setEnabled(true);
                this.mLockPatternView.enableInput();
                this.mLockPatternView.clearPattern();
            } else if (i == 2) {
                showError(C0017R$string.lockpattern_need_to_unlock_wrong, 3000);
                this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                this.mLockPatternView.setEnabled(true);
                this.mLockPatternView.enableInput();
            } else if (i == 3) {
                this.mLockPatternView.clearPattern();
                this.mLockPatternView.setEnabled(false);
            }
            TextView textView = this.mHeaderTextView;
            textView.announceForAccessibility(textView.getText());
        }

        private int getDefaultHeader() {
            if (this.mFrp) {
                return C0017R$string.lockpassword_confirm_your_pattern_header_frp;
            }
            if (this.mIsManagedProfile) {
                return C0017R$string.lockpassword_confirm_your_work_pattern_header;
            }
            return C0017R$string.lockpassword_confirm_your_pattern_header;
        }

        private void postClearPatternRunnable() {
            this.mLockPatternView.removeCallbacks(this.mClearPatternRunnable);
            this.mLockPatternView.postDelayed(this.mClearPatternRunnable, 3000);
        }

        private void startDisappearAnimation(Intent intent) {
            if (!this.mDisappearing) {
                this.mDisappearing = true;
                ConfirmLockPattern confirmLockPattern = (ConfirmLockPattern) getActivity();
                if (confirmLockPattern != null && !confirmLockPattern.isFinishing()) {
                    if (confirmLockPattern.getConfirmCredentialTheme() == ConfirmDeviceCredentialBaseActivity.ConfirmCredentialTheme.DARK) {
                        this.mLockPatternView.clearPattern();
                        this.mDisappearAnimationUtils.startAnimation2d(getActiveViews(), new Runnable(intent) {
                            /* class com.android.settings.password.$$Lambda$ConfirmLockPattern$ConfirmLockPatternFragment$5mgp_p2Jjy9apKG7HsLV4ZusXo */
                            public final /* synthetic */ Intent f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                ConfirmLockPattern.ConfirmLockPatternFragment.lambda$startDisappearAnimation$0(ConfirmLockPattern.this, this.f$1);
                            }
                        }, this);
                        return;
                    }
                    intent.putExtra("power_on_psw", this.mPattenString);
                    confirmLockPattern.setResult(-1, intent);
                    confirmLockPattern.finish();
                }
            }
        }

        static /* synthetic */ void lambda$startDisappearAnimation$0(ConfirmLockPattern confirmLockPattern, Intent intent) {
            confirmLockPattern.setResult(-1, intent);
            confirmLockPattern.finish();
            confirmLockPattern.overridePendingTransition(C0002R$anim.confirm_credential_close_enter, C0002R$anim.confirm_credential_close_exit);
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void onPatternChecked(boolean z, Intent intent, int i, int i2, boolean z2) {
            this.mLockPatternView.setEnabled(true);
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
                updateStage(Stage.NeedToUnlockWrong);
                postClearPatternRunnable();
            }
            if (z2) {
                reportFailedAttempt();
            }
        }

        @Override // com.android.settings.password.CredentialCheckResultTracker.Listener
        public void onCredentialChecked(boolean z, Intent intent, int i, int i2, boolean z2) {
            onPatternChecked(z, intent, i, i2, z2);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        public int getLastTryErrorMessage(int i) {
            if (i == 1) {
                return C0017R$string.lock_last_pattern_attempt_before_wipe_device;
            }
            if (i == 2) {
                return C0017R$string.lock_last_pattern_attempt_before_wipe_profile;
            }
            if (i == 3) {
                return C0017R$string.lock_last_pattern_attempt_before_wipe_user;
            }
            throw new IllegalArgumentException("Unrecognized user type:" + i);
        }

        private void handleAttemptLockout(long j) {
            updateStage(Stage.LockedOut);
            this.mCountdownTimer = new CountDownTimer(j - SystemClock.elapsedRealtime(), 1000) {
                /* class com.android.settings.password.ConfirmLockPattern.ConfirmLockPatternFragment.AnonymousClass4 */

                public void onTick(long j) {
                    ConfirmLockPatternFragment confirmLockPatternFragment = ConfirmLockPatternFragment.this;
                    confirmLockPatternFragment.mErrorTextView.setText(confirmLockPatternFragment.getString(C0017R$string.lockpattern_too_many_failed_confirmation_attempts, Integer.valueOf((int) (j / 1000))));
                }

                public void onFinish() {
                    ConfirmLockPatternFragment.this.updateStage(Stage.NeedToUnlock);
                }
            }.start();
        }

        @Override // com.android.settingslib.animation.AppearAnimationCreator
        public void createAnimation(Object obj, long j, long j2, float f, boolean z, Interpolator interpolator, Runnable runnable) {
            if (obj instanceof LockPatternView.CellState) {
                this.mLockPatternView.startCellStateAnimation((LockPatternView.CellState) obj, 1.0f, z ? 1.0f : 0.0f, z ? f : 0.0f, z ? 0.0f : f, z ? 0.0f : 1.0f, 1.0f, j, j2, interpolator, runnable);
                return;
            }
            this.mAppearAnimationUtils.createAnimation((View) obj, j, j2, f, z, interpolator, runnable);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.android.settings.password.ConfirmLockPattern$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$password$ConfirmLockPattern$Stage;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                com.android.settings.password.ConfirmLockPattern$Stage[] r0 = com.android.settings.password.ConfirmLockPattern.Stage.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.android.settings.password.ConfirmLockPattern.AnonymousClass1.$SwitchMap$com$android$settings$password$ConfirmLockPattern$Stage = r0
                com.android.settings.password.ConfirmLockPattern$Stage r1 = com.android.settings.password.ConfirmLockPattern.Stage.NeedToUnlock     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.android.settings.password.ConfirmLockPattern.AnonymousClass1.$SwitchMap$com$android$settings$password$ConfirmLockPattern$Stage     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.settings.password.ConfirmLockPattern$Stage r1 = com.android.settings.password.ConfirmLockPattern.Stage.NeedToUnlockWrong     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.android.settings.password.ConfirmLockPattern.AnonymousClass1.$SwitchMap$com$android$settings$password$ConfirmLockPattern$Stage     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.settings.password.ConfirmLockPattern$Stage r1 = com.android.settings.password.ConfirmLockPattern.Stage.LockedOut     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.password.ConfirmLockPattern.AnonymousClass1.<clinit>():void");
        }
    }
}
