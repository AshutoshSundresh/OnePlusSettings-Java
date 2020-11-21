package com.android.settings.password;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockscreenCredential;
import com.android.settings.C0004R$attr;
import com.android.settings.C0005R$bool;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.notification.RedactionInterstitial;
import com.android.settings.password.ChooseLockPattern;
import com.android.settings.password.SaveChosenLockWorkerBase;
import com.google.android.collect.Lists;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.oneplus.settings.utils.OPUtils;
import java.util.Collections;
import java.util.List;

public class ChooseLockPattern extends SettingsActivity {
    @Override // com.android.settings.SettingsActivity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", getFragmentClass().getName());
        return intent;
    }

    public static class IntentBuilder {
        private final Intent mIntent;

        public IntentBuilder(Context context) {
            Intent intent = new Intent(context, ChooseLockPattern.class);
            this.mIntent = intent;
            intent.putExtra("extra_require_password", false);
            this.mIntent.putExtra("confirm_credentials", false);
            this.mIntent.putExtra("has_challenge", false);
        }

        public IntentBuilder setUserId(int i) {
            this.mIntent.putExtra("android.intent.extra.USER_ID", i);
            return this;
        }

        public IntentBuilder setChallenge(long j) {
            this.mIntent.putExtra("has_challenge", true);
            this.mIntent.putExtra("challenge", j);
            return this;
        }

        public IntentBuilder setPattern(LockscreenCredential lockscreenCredential) {
            this.mIntent.putExtra("password", (Parcelable) lockscreenCredential);
            return this;
        }

        public IntentBuilder setForFingerprint(boolean z) {
            this.mIntent.putExtra("for_fingerprint", z);
            return this;
        }

        public IntentBuilder setForFace(boolean z) {
            this.mIntent.putExtra("for_face", z);
            return this;
        }

        public IntentBuilder setProfileToUnify(int i, LockscreenCredential lockscreenCredential) {
            this.mIntent.putExtra("unification_profile_id", i);
            this.mIntent.putExtra("unification_profile_credential", (Parcelable) lockscreenCredential);
            return this;
        }

        public Intent build() {
            return this.mIntent;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ChooseLockPatternFragment.class.getName().equals(str);
    }

    /* access modifiers changed from: package-private */
    public Class<? extends Fragment> getFragmentClass() {
        return ChooseLockPatternFragment.class;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.android.settings.core.SettingsBaseActivity, com.android.settings.SettingsActivity
    public void onCreate(Bundle bundle) {
        this.mNeedShowAppBar = false;
        super.onCreate(bundle);
        if (isInMultiWindowMode()) {
            Toast.makeText(this, C0017R$string.feature_not_support_split_screen, 0).show();
            finish();
            return;
        }
        getIntent().getBooleanExtra("for_fingerprint", false);
        getIntent().getBooleanExtra("for_face", false);
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.action_bar);
        if (toolbar != null) {
            toolbar.setVisibility(8);
        }
        setTitle(C0017R$string.lockpassword_choose_your_screen_lock_header);
        findViewById(C0010R$id.content_parent).setFitsSystemWindows(true);
    }

    @Override // androidx.appcompat.app.AppCompatActivity
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return super.onKeyDown(i, keyEvent);
    }

    public static class ChooseLockPatternFragment extends InstrumentedFragment implements SaveChosenLockWorkerBase.Listener {
        private final List<LockPatternView.Cell> mAnimatePattern = Collections.unmodifiableList(Lists.newArrayList(new LockPatternView.Cell[]{LockPatternView.Cell.of(0, 0), LockPatternView.Cell.of(0, 1), LockPatternView.Cell.of(1, 1), LockPatternView.Cell.of(2, 1)}));
        private long mChallenge;
        private ChooseLockSettingsHelper mChooseLockSettingsHelper;
        protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {
            /* class com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.AnonymousClass1 */

            public void onPatternCellAdded(List<LockPatternView.Cell> list) {
            }

            public void onPatternStart() {
                ChooseLockPatternFragment chooseLockPatternFragment = ChooseLockPatternFragment.this;
                chooseLockPatternFragment.mLockPatternView.removeCallbacks(chooseLockPatternFragment.mClearPatternRunnable);
                patternInProgress();
            }

            public void onPatternCleared() {
                ChooseLockPatternFragment chooseLockPatternFragment = ChooseLockPatternFragment.this;
                chooseLockPatternFragment.mLockPatternView.removeCallbacks(chooseLockPatternFragment.mClearPatternRunnable);
            }

            public void onPatternDetected(List<LockPatternView.Cell> list) {
                if (ChooseLockPatternFragment.this.mUiStage == Stage.NeedToConfirm || ChooseLockPatternFragment.this.mUiStage == Stage.ConfirmWrong) {
                    if (ChooseLockPatternFragment.this.mChosenPattern != null) {
                        LockscreenCredential createPattern = LockscreenCredential.createPattern(list);
                        try {
                            if (ChooseLockPatternFragment.this.mChosenPattern.equals(createPattern)) {
                                ChooseLockPatternFragment.this.updateStage(Stage.ChoiceConfirmed);
                            } else {
                                ChooseLockPatternFragment.this.updateStage(Stage.ConfirmWrong);
                            }
                            if (createPattern != null) {
                                createPattern.close();
                                return;
                            }
                            return;
                        } catch (Throwable th) {
                            th.addSuppressed(th);
                        }
                    } else {
                        throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
                    }
                } else if (ChooseLockPatternFragment.this.mUiStage != Stage.Introduction && ChooseLockPatternFragment.this.mUiStage != Stage.ChoiceTooShort) {
                    throw new IllegalStateException("Unexpected stage " + ChooseLockPatternFragment.this.mUiStage + " when entering the pattern.");
                } else if (list.size() < 4) {
                    ChooseLockPatternFragment.this.updateStage(Stage.ChoiceTooShort);
                    return;
                } else {
                    ChooseLockPatternFragment.this.mChosenPattern = LockscreenCredential.createPattern(list);
                    ChooseLockPatternFragment.this.updateStage(Stage.FirstChoiceValid);
                    return;
                }
                throw th;
            }

            private void patternInProgress() {
                ChooseLockPatternFragment.this.mHeaderText.setText(C0017R$string.lockpattern_recording_inprogress);
                if (ChooseLockPatternFragment.this.mDefaultHeaderColorList != null) {
                    ChooseLockPatternFragment chooseLockPatternFragment = ChooseLockPatternFragment.this;
                    chooseLockPatternFragment.mHeaderText.setTextColor(chooseLockPatternFragment.mDefaultHeaderColorList);
                }
                ChooseLockPatternFragment.this.mFooterText.setText("");
                ChooseLockPatternFragment.this.mNextButton.setEnabled(false);
                if (ChooseLockPatternFragment.this.mTitleHeaderScrollView != null) {
                    ChooseLockPatternFragment.this.mTitleHeaderScrollView.post(new Runnable() {
                        /* class com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.AnonymousClass1.AnonymousClass1 */

                        public void run() {
                            ChooseLockPatternFragment.this.mTitleHeaderScrollView.fullScroll(130);
                        }
                    });
                }
            }
        };
        @VisibleForTesting
        protected LockscreenCredential mChosenPattern;
        private Runnable mClearPatternRunnable = new Runnable() {
            /* class com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.AnonymousClass2 */

            public void run() {
                ChooseLockPatternFragment.this.mLockPatternView.clearPattern();
            }
        };
        private LockscreenCredential mCurrentCredential;
        private ColorStateList mDefaultHeaderColorList;
        protected TextView mFooterText;
        protected boolean mForFace;
        protected boolean mForFingerprint;
        private boolean mHasChallenge;
        protected TextView mHeaderText;
        protected LockPatternView mLockPatternView;
        protected TextView mMessageText;
        private FooterButton mNextButton;
        private SaveAndFinishWorker mSaveAndFinishWorker;
        protected FooterButton mSkipOrClearButton;
        private ScrollView mTitleHeaderScrollView;
        protected TextView mTitleText;
        private Stage mUiStage = Stage.Introduction;
        protected int mUserId;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 29;
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 55) {
                if (i2 != -1) {
                    getActivity().setResult(1);
                    getActivity().finish();
                } else {
                    this.mCurrentCredential = intent.getParcelableExtra("password");
                }
                updateStage(Stage.Introduction);
            }
        }

        /* access modifiers changed from: protected */
        public void setRightButtonEnabled(boolean z) {
            this.mNextButton.setEnabled(z);
        }

        /* access modifiers changed from: protected */
        public void setRightButtonText(int i) {
            this.mNextButton.setText(getActivity(), i);
        }

        /* access modifiers changed from: package-private */
        public enum LeftButtonMode {
            Retry(C0017R$string.lockpattern_retry_button_text, true),
            RetryDisabled(C0017R$string.lockpattern_retry_button_text, false),
            Gone(-1, false);
            
            final boolean enabled;
            final int text;

            private LeftButtonMode(int i, boolean z) {
                this.text = i;
                this.enabled = z;
            }
        }

        /* access modifiers changed from: package-private */
        public enum RightButtonMode {
            Continue(C0017R$string.next_label, true),
            ContinueDisabled(C0017R$string.next_label, false),
            Confirm(C0017R$string.lockpattern_confirm_button_text, true),
            ConfirmDisabled(C0017R$string.lockpattern_confirm_button_text, false),
            Ok(17039370, true);
            
            final boolean enabled;
            final int text;

            private RightButtonMode(int i, boolean z) {
                this.text = i;
                this.enabled = z;
            }
        }

        /* access modifiers changed from: protected */
        public enum Stage {
            Introduction(C0017R$string.lock_settings_picker_biometrics_added_security_message, C0017R$string.lockpassword_choose_your_pattern_message, C0017R$string.lockpattern_recording_intro_header, LeftButtonMode.Gone, RightButtonMode.ContinueDisabled, -1, true),
            HelpScreen(-1, -1, C0017R$string.lockpattern_settings_help_how_to_record, LeftButtonMode.Gone, RightButtonMode.Ok, -1, false),
            ChoiceTooShort(C0017R$string.lock_settings_picker_biometrics_added_security_message, C0017R$string.lockpassword_choose_your_pattern_message, C0017R$string.lockpattern_recording_incorrect_too_short, LeftButtonMode.Retry, RightButtonMode.ContinueDisabled, -1, true),
            FirstChoiceValid(C0017R$string.lock_settings_picker_biometrics_added_security_message, C0017R$string.lockpassword_choose_your_pattern_message, C0017R$string.lockpattern_pattern_entered_header, LeftButtonMode.Retry, RightButtonMode.Continue, -1, false),
            NeedToConfirm(-1, -1, C0017R$string.lockpattern_need_to_confirm, LeftButtonMode.Gone, RightButtonMode.ConfirmDisabled, -1, true),
            ConfirmWrong(-1, -1, C0017R$string.lockpattern_need_to_unlock_wrong, LeftButtonMode.Gone, RightButtonMode.ConfirmDisabled, -1, true),
            ChoiceConfirmed(-1, -1, C0017R$string.lockpattern_pattern_confirmed_header, LeftButtonMode.Gone, RightButtonMode.Confirm, -1, false);
            
            final int footerMessage;
            final int headerMessage;
            final LeftButtonMode leftMode;
            final int message;
            final int messageForBiometrics;
            final boolean patternEnabled;
            final RightButtonMode rightMode;

            private Stage(int i, int i2, int i3, LeftButtonMode leftButtonMode, RightButtonMode rightButtonMode, int i4, boolean z) {
                this.headerMessage = i3;
                this.messageForBiometrics = i;
                this.message = i2;
                this.leftMode = leftButtonMode;
                this.rightMode = rightButtonMode;
                this.footerMessage = i4;
                this.patternEnabled = z;
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
            if (getActivity() instanceof ChooseLockPattern) {
                Intent intent = getActivity().getIntent();
                this.mUserId = Utils.getUserIdFromBundle(getActivity(), intent.getExtras());
                if (intent.getBooleanExtra("for_cred_req_boot", false)) {
                    SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
                    boolean booleanExtra = getActivity().getIntent().getBooleanExtra("extra_require_password", true);
                    LockscreenCredential lockscreenCredential = (LockscreenCredential) intent.getParcelableExtra("password");
                    saveAndFinishWorker.setBlocking(true);
                    saveAndFinishWorker.setListener(this);
                    saveAndFinishWorker.start(this.mChooseLockSettingsHelper.utils(), booleanExtra, false, 0, lockscreenCredential, lockscreenCredential, this.mUserId);
                }
                this.mForFingerprint = intent.getBooleanExtra("for_fingerprint", false);
                this.mForFace = intent.getBooleanExtra("for_face", false);
                return;
            }
            throw new SecurityException("Fragment contained in wrong activity");
        }

        @Override // androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            GlifLayout glifLayout = (GlifLayout) layoutInflater.inflate(C0012R$layout.choose_lock_pattern, viewGroup, false);
            glifLayout.setHeaderText(getActivity().getTitle());
            if (getResources().getBoolean(C0005R$bool.config_lock_pattern_minimal_ui)) {
                View findViewById = glifLayout.findViewById(C0010R$id.sud_layout_icon);
                if (findViewById != null) {
                    findViewById.setVisibility(8);
                }
            } else if (this.mForFingerprint) {
                glifLayout.setIcon(getActivity().getDrawable(C0008R$drawable.ic_opfinger_logo_bg));
            } else if (this.mForFace) {
                glifLayout.setIcon(getActivity().getDrawable(C0008R$drawable.op_face_settings));
            }
            FooterBarMixin footerBarMixin = (FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class);
            FooterButton.Builder builder = new FooterButton.Builder(getActivity());
            builder.setText(C0017R$string.lockpattern_tutorial_cancel_label);
            builder.setListener(new View.OnClickListener() {
                /* class com.android.settings.password.$$Lambda$8EQMMpT3cZZbdnVNBjOcXjnOBDg */

                public final void onClick(View view) {
                    ChooseLockPattern.ChooseLockPatternFragment.this.onSkipOrClearButtonClick(view);
                }
            });
            builder.setButtonType(0);
            builder.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
            footerBarMixin.setSecondaryButton(builder.build());
            FooterButton.Builder builder2 = new FooterButton.Builder(getActivity());
            builder2.setText(C0017R$string.lockpattern_tutorial_continue_label);
            builder2.setListener(new View.OnClickListener() {
                /* class com.android.settings.password.$$Lambda$rzI7CSFc7QDANc8VMpAahOX_Bug */

                public final void onClick(View view) {
                    ChooseLockPattern.ChooseLockPatternFragment.this.onNextButtonClick(view);
                }
            });
            builder2.setButtonType(5);
            builder2.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
            footerBarMixin.setPrimaryButton(builder2.build());
            this.mSkipOrClearButton = footerBarMixin.getSecondaryButton();
            this.mNextButton = footerBarMixin.getPrimaryButton();
            return glifLayout;
        }

        @Override // androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            this.mTitleText = (TextView) view.findViewById(C0010R$id.suc_layout_title);
            TextView textView = (TextView) view.findViewById(C0010R$id.headerText);
            this.mHeaderText = textView;
            this.mDefaultHeaderColorList = textView.getTextColors();
            this.mMessageText = (TextView) view.findViewById(C0010R$id.sud_layout_description);
            LockPatternView findViewById = view.findViewById(C0010R$id.lockPattern);
            this.mLockPatternView = findViewById;
            findViewById.setOnPatternListener(this.mChooseNewLockPatternListener);
            this.mLockPatternView.setTactileFeedbackEnabled(this.mChooseLockSettingsHelper.utils().isTactileFeedbackEnabled());
            this.mLockPatternView.setFadePattern(false);
            this.mFooterText = (TextView) view.findViewById(C0010R$id.footerText);
            this.mTitleHeaderScrollView = (ScrollView) view.findViewById(C0010R$id.scroll_layout_title_header);
            view.findViewById(C0010R$id.topLayout).setDefaultTouchRecepient(this.mLockPatternView);
            boolean booleanExtra = getActivity().getIntent().getBooleanExtra("confirm_credentials", true);
            Intent intent = getActivity().getIntent();
            this.mCurrentCredential = intent.getParcelableExtra("password");
            this.mHasChallenge = intent.getBooleanExtra("has_challenge", false);
            this.mChallenge = intent.getLongExtra("challenge", 0);
            if (bundle != null) {
                this.mChosenPattern = bundle.getParcelable("chosenPattern");
                if (this.mCurrentCredential == null) {
                    this.mCurrentCredential = bundle.getParcelable("currentPattern");
                }
                updateStage(Stage.values()[bundle.getInt("uiStage")]);
                this.mSaveAndFinishWorker = (SaveAndFinishWorker) getFragmentManager().findFragmentByTag("save_and_finish_worker");
            } else if (booleanExtra) {
                updateStage(Stage.NeedToConfirm);
                if (!this.mChooseLockSettingsHelper.launchConfirmationActivity(55, getString(C0017R$string.unlock_set_unlock_launch_picker_title), true, this.mUserId)) {
                    updateStage(Stage.Introduction);
                }
            } else {
                updateStage(Stage.Introduction);
            }
        }

        @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            updateStage(this.mUiStage);
            if (this.mSaveAndFinishWorker != null) {
                setRightButtonEnabled(false);
                this.mSaveAndFinishWorker.setListener(this);
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            SaveAndFinishWorker saveAndFinishWorker = this.mSaveAndFinishWorker;
            if (saveAndFinishWorker != null) {
                saveAndFinishWorker.setListener(null);
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            LockscreenCredential lockscreenCredential = this.mCurrentCredential;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            System.gc();
            System.runFinalization();
            System.gc();
        }

        /* access modifiers changed from: protected */
        public Intent getRedactionInterstitialIntent(Context context) {
            return RedactionInterstitial.createStartIntent(context, this.mUserId);
        }

        public void handleLeftButton() {
            if (this.mUiStage.leftMode == LeftButtonMode.Retry) {
                LockscreenCredential lockscreenCredential = this.mChosenPattern;
                if (lockscreenCredential != null) {
                    lockscreenCredential.zeroize();
                    this.mChosenPattern = null;
                }
                this.mLockPatternView.clearPattern();
                updateStage(Stage.Introduction);
                return;
            }
            throw new IllegalStateException("left footer button pressed, but stage of " + this.mUiStage + " doesn't make sense");
        }

        public void handleRightButton() {
            Stage stage = this.mUiStage;
            RightButtonMode rightButtonMode = stage.rightMode;
            if (rightButtonMode == RightButtonMode.Continue) {
                if (stage == Stage.FirstChoiceValid) {
                    updateStage(Stage.NeedToConfirm);
                    return;
                }
                throw new IllegalStateException("expected ui stage " + Stage.FirstChoiceValid + " when button is " + RightButtonMode.Continue);
            } else if (rightButtonMode == RightButtonMode.Confirm) {
                if (stage == Stage.ChoiceConfirmed) {
                    startSaveAndFinish();
                    OPUtils.savePINPasswordLength(new LockPatternUtils(getActivity()), 0, this.mUserId);
                    return;
                }
                throw new IllegalStateException("expected ui stage " + Stage.ChoiceConfirmed + " when button is " + RightButtonMode.Confirm);
            } else if (rightButtonMode != RightButtonMode.Ok) {
            } else {
                if (stage == Stage.HelpScreen) {
                    this.mLockPatternView.clearPattern();
                    this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    updateStage(Stage.Introduction);
                    return;
                }
                throw new IllegalStateException("Help screen is only mode with ok button, but stage is " + this.mUiStage);
            }
        }

        /* access modifiers changed from: protected */
        public void onSkipOrClearButtonClick(View view) {
            handleLeftButton();
        }

        /* access modifiers changed from: protected */
        public void onNextButtonClick(View view) {
            handleRightButton();
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putInt("uiStage", this.mUiStage.ordinal());
            LockscreenCredential lockscreenCredential = this.mChosenPattern;
            if (lockscreenCredential != null) {
                bundle.putParcelable("chosenPattern", lockscreenCredential);
            }
            LockscreenCredential lockscreenCredential2 = this.mCurrentCredential;
            if (lockscreenCredential2 != null) {
                bundle.putParcelable("currentPattern", lockscreenCredential2);
            }
        }

        /* access modifiers changed from: protected */
        public void updateStage(Stage stage) {
            Stage stage2 = this.mUiStage;
            this.mUiStage = stage;
            boolean z = false;
            if (stage == Stage.ChoiceTooShort) {
                this.mHeaderText.setText(getResources().getString(stage.headerMessage, 4));
            } else {
                this.mHeaderText.setText(stage.headerMessage);
            }
            boolean z2 = this.mForFingerprint || this.mForFace;
            int i = z2 ? stage.messageForBiometrics : stage.message;
            if (i == -1) {
                this.mMessageText.setText("");
            } else {
                this.mMessageText.setText(i);
            }
            int i2 = stage.footerMessage;
            if (i2 == -1) {
                this.mFooterText.setText("");
            } else {
                this.mFooterText.setText(i2);
            }
            if (stage == Stage.ConfirmWrong || stage == Stage.ChoiceTooShort) {
                TypedValue typedValue = new TypedValue();
                getActivity().getTheme().resolveAttribute(C0004R$attr.colorError, typedValue, true);
                this.mHeaderText.setTextColor(typedValue.data);
            } else {
                ColorStateList colorStateList = this.mDefaultHeaderColorList;
                if (colorStateList != null) {
                    this.mHeaderText.setTextColor(colorStateList);
                }
                if (stage == Stage.NeedToConfirm && z2) {
                    this.mHeaderText.setText("");
                    this.mTitleText.setText(C0017R$string.lockpassword_draw_your_pattern_again_header);
                }
            }
            updateFooterLeftButton(stage);
            setRightButtonText(stage.rightMode.text);
            setRightButtonEnabled(stage.rightMode.enabled);
            if (stage.patternEnabled) {
                this.mLockPatternView.enableInput();
            } else {
                this.mLockPatternView.disableInput();
            }
            this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
            int i3 = AnonymousClass1.$SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage[this.mUiStage.ordinal()];
            if (i3 == 1) {
                this.mLockPatternView.clearPattern();
            } else if (i3 != 2) {
                if (i3 == 3) {
                    this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    postClearPatternRunnable();
                } else if (i3 == 5) {
                    this.mLockPatternView.clearPattern();
                } else if (i3 == 6) {
                    this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    postClearPatternRunnable();
                }
                z = true;
            } else {
                this.mLockPatternView.setPattern(LockPatternView.DisplayMode.Animate, this.mAnimatePattern);
            }
            if (stage2 != stage || z) {
                TextView textView = this.mHeaderText;
                textView.announceForAccessibility(textView.getText());
            }
        }

        /* access modifiers changed from: protected */
        public void updateFooterLeftButton(Stage stage) {
            if (stage.leftMode == LeftButtonMode.Gone) {
                this.mSkipOrClearButton.setVisibility(8);
                return;
            }
            this.mSkipOrClearButton.setVisibility(0);
            this.mSkipOrClearButton.setText(getActivity(), stage.leftMode.text);
            this.mSkipOrClearButton.setEnabled(stage.leftMode.enabled);
        }

        private void postClearPatternRunnable() {
            this.mLockPatternView.removeCallbacks(this.mClearPatternRunnable);
            this.mLockPatternView.postDelayed(this.mClearPatternRunnable, 2000);
        }

        private void startSaveAndFinish() {
            if (this.mSaveAndFinishWorker != null) {
                Log.w("ChooseLockPattern", "startSaveAndFinish with an existing SaveAndFinishWorker.");
                return;
            }
            setRightButtonEnabled(false);
            SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
            this.mSaveAndFinishWorker = saveAndFinishWorker;
            saveAndFinishWorker.setListener(this);
            FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
            beginTransaction.add(this.mSaveAndFinishWorker, "save_and_finish_worker");
            beginTransaction.commit();
            getFragmentManager().executePendingTransactions();
            Intent intent = getActivity().getIntent();
            boolean booleanExtra = intent.getBooleanExtra("extra_require_password", true);
            if (intent.hasExtra("unification_profile_id")) {
                LockscreenCredential parcelableExtra = intent.getParcelableExtra("unification_profile_credential");
                try {
                    this.mSaveAndFinishWorker.setProfileToUnify(intent.getIntExtra("unification_profile_id", -10000), parcelableExtra);
                    if (parcelableExtra != null) {
                        parcelableExtra.close();
                    }
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            }
            this.mSaveAndFinishWorker.start(this.mChooseLockSettingsHelper.utils(), booleanExtra, this.mHasChallenge, this.mChallenge, this.mChosenPattern, this.mCurrentCredential, this.mUserId);
            return;
            throw th;
        }

        @Override // com.android.settings.password.SaveChosenLockWorkerBase.Listener
        public void onChosenLockSaveFinished(boolean z, Intent intent) {
            Intent redactionInterstitialIntent;
            getActivity().setResult(1, intent);
            LockscreenCredential lockscreenCredential = this.mChosenPattern;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            LockscreenCredential lockscreenCredential2 = this.mCurrentCredential;
            if (lockscreenCredential2 != null) {
                lockscreenCredential2.zeroize();
            }
            if (!z && (redactionInterstitialIntent = getRedactionInterstitialIntent(getActivity())) != null) {
                startActivity(redactionInterstitialIntent);
            }
            getActivity().finish();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.android.settings.password.ChooseLockPattern$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$Stage[] r0 = com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.Stage.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.android.settings.password.ChooseLockPattern.AnonymousClass1.$SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage = r0
                com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$Stage r1 = com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.Stage.Introduction     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.android.settings.password.ChooseLockPattern.AnonymousClass1.$SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$Stage r1 = com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.Stage.HelpScreen     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.android.settings.password.ChooseLockPattern.AnonymousClass1.$SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$Stage r1 = com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.Stage.ChoiceTooShort     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = com.android.settings.password.ChooseLockPattern.AnonymousClass1.$SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$Stage r1 = com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.Stage.FirstChoiceValid     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = com.android.settings.password.ChooseLockPattern.AnonymousClass1.$SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$Stage r1 = com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.Stage.NeedToConfirm     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = com.android.settings.password.ChooseLockPattern.AnonymousClass1.$SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$Stage r1 = com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.Stage.ConfirmWrong     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = com.android.settings.password.ChooseLockPattern.AnonymousClass1.$SwitchMap$com$android$settings$password$ChooseLockPattern$ChooseLockPatternFragment$Stage     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.settings.password.ChooseLockPattern$ChooseLockPatternFragment$Stage r1 = com.android.settings.password.ChooseLockPattern.ChooseLockPatternFragment.Stage.ChoiceConfirmed     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.password.ChooseLockPattern.AnonymousClass1.<clinit>():void");
        }
    }

    public static class SaveAndFinishWorker extends SaveChosenLockWorkerBase {
        private LockscreenCredential mChosenPattern;
        private LockscreenCredential mCurrentCredential;
        private boolean mLockVirgin;

        public void start(LockPatternUtils lockPatternUtils, boolean z, boolean z2, long j, LockscreenCredential lockscreenCredential, LockscreenCredential lockscreenCredential2, int i) {
            prepare(lockPatternUtils, z, z2, j, i);
            if (lockscreenCredential2 == null) {
                lockscreenCredential2 = LockscreenCredential.createNone();
            }
            this.mCurrentCredential = lockscreenCredential2;
            this.mChosenPattern = lockscreenCredential;
            this.mUserId = i;
            this.mLockVirgin = !this.mUtils.isPatternEverChosen(i);
            start();
        }

        /* JADX DEBUG: Multi-variable search result rejected for r7v3, resolved type: android.content.Intent */
        /* JADX WARN: Multi-variable type inference failed */
        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.SaveChosenLockWorkerBase
        public Pair<Boolean, Intent> saveAndVerifyInBackground() {
            int i = this.mUserId;
            boolean lockCredential = this.mUtils.setLockCredential(this.mChosenPattern, this.mCurrentCredential, i);
            if (lockCredential) {
                unifyProfileCredentialIfRequested();
            }
            byte[] bArr = null;
            if (lockCredential && this.mHasChallenge) {
                try {
                    bArr = this.mUtils.verifyCredential(this.mChosenPattern, this.mChallenge, i);
                } catch (LockPatternUtils.RequestThrottledException unused) {
                }
                if (bArr == null) {
                    Log.e("ChooseLockPattern", "critical: no token returned for known good pattern");
                }
                Intent intent = new Intent();
                intent.putExtra("hw_auth_token", bArr);
                bArr = intent;
            }
            return Pair.create(Boolean.valueOf(lockCredential), bArr);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.SaveChosenLockWorkerBase
        public void finish(Intent intent) {
            if (this.mLockVirgin) {
                this.mUtils.setVisiblePatternEnabled(true, this.mUserId);
            }
            super.finish(intent);
        }
    }
}
