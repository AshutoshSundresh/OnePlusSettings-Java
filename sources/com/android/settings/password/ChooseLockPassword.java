package com.android.settings.password;

import android.app.admin.PasswordMetrics;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImeAwareEditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.PasswordValidationError;
import com.android.internal.widget.TextViewInputDisabler;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.notification.RedactionInterstitial;
import com.android.settings.password.ChooseLockPassword;
import com.android.settings.password.SaveChosenLockWorkerBase;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseLockPassword extends SettingsActivity {
    @Override // com.android.settings.SettingsActivity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", getFragmentClass().getName());
        return intent;
    }

    public static class IntentBuilder {
        private final Intent mIntent;

        public IntentBuilder(Context context) {
            Intent intent = new Intent(context, ChooseLockPassword.class);
            this.mIntent = intent;
            intent.putExtra("confirm_credentials", false);
            this.mIntent.putExtra("extra_require_password", false);
            this.mIntent.putExtra("has_challenge", false);
        }

        public IntentBuilder setPasswordQuality(int i) {
            this.mIntent.putExtra("lockscreen.password_type", i);
            return this;
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

        public IntentBuilder setPassword(LockscreenCredential lockscreenCredential) {
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

        public IntentBuilder setRequestedMinComplexity(int i) {
            this.mIntent.putExtra("requested_min_complexity", i);
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
        return ChooseLockPasswordFragment.class.getName().equals(str);
    }

    /* access modifiers changed from: package-private */
    public Class<? extends Fragment> getFragmentClass() {
        return ChooseLockPasswordFragment.class;
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
        setTitle(getText(C0017R$string.lockpassword_choose_your_screen_lock_header));
        findViewById(C0010R$id.content_parent).setFitsSystemWindows(true);
    }

    public static class ChooseLockPasswordFragment extends InstrumentedFragment implements TextView.OnEditorActionListener, TextWatcher, SaveChosenLockWorkerBase.Listener {
        private long mChallenge;
        private ChooseLockSettingsHelper mChooseLockSettingsHelper;
        private LockscreenCredential mChosenPassword;
        private LockscreenCredential mCurrentCredential;
        private LockscreenCredential mFirstPassword;
        protected boolean mForFace;
        protected boolean mForFingerprint;
        private boolean mHasChallenge;
        protected boolean mIsAlphaMode;
        private GlifLayout mLayout;
        private LockPatternUtils mLockPatternUtils;
        private TextView mMessage;
        private int mMinComplexity = 0;
        private PasswordMetrics mMinMetrics;
        private FooterButton mNextButton;
        private ImeAwareEditText mPasswordEntry;
        private TextViewInputDisabler mPasswordEntryInputDisabler;
        private byte[] mPasswordHistoryHashFactor;
        private PasswordRequirementAdapter mPasswordRequirementAdapter;
        private RecyclerView mPasswordRestrictionView;
        private int mRequestedQuality = 131072;
        private SaveAndFinishWorker mSaveAndFinishWorker;
        protected FooterButton mSkipOrClearButton;
        private TextChangedHandler mTextChangedHandler;
        protected Stage mUiStage = Stage.Introduction;
        private int mUnificationProfileId = -10000;
        protected int mUserId;
        private List<PasswordValidationError> mValidationErrors;

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 28;
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        /* access modifiers changed from: protected */
        public int toVisibility(boolean z) {
            return z ? 0 : 8;
        }

        /* JADX WARN: Init of enum Introduction can be incorrect */
        /* JADX WARN: Init of enum NeedToConfirm can be incorrect */
        /* JADX WARN: Init of enum ConfirmWrong can be incorrect */
        /* access modifiers changed from: protected */
        public enum Stage {
            Introduction(r8, r8, r8, r8, r8, r8, C0017R$string.lockpassword_choose_your_password_message, C0017R$string.lock_settings_picker_biometrics_added_security_message, C0017R$string.lockpassword_choose_your_pin_message, C0017R$string.lock_settings_picker_biometrics_added_security_message, C0017R$string.next_label),
            NeedToConfirm(r20, r20, r20, r23, r23, r23, 0, 0, 0, 0, C0017R$string.lockpassword_confirm_label),
            ConfirmWrong(r6, r6, r6, r9, r9, r9, 0, 0, 0, 0, C0017R$string.lockpassword_confirm_label);
            
            public final int alphaHint;
            public final int alphaHintForFace;
            public final int alphaHintForFingerprint;
            public final int alphaMessage;
            public final int alphaMessageForBiometrics;
            public final int buttonText;
            public final int numericHint;
            public final int numericHintForFace;
            public final int numericHintForFingerprint;
            public final int numericMessage;
            public final int numericMessageForBiometrics;

            static {
                int i = C0017R$string.lockpassword_choose_your_screen_lock_header;
                int i2 = C0017R$string.lockpassword_confirm_your_password_header;
                int i3 = C0017R$string.lockpassword_confirm_your_pin_header;
                int i4 = C0017R$string.lockpassword_confirm_passwords_dont_match;
                int i5 = C0017R$string.lockpassword_confirm_pins_dont_match;
            }

            private Stage(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11) {
                this.alphaHint = i;
                this.alphaHintForFingerprint = i2;
                this.alphaHintForFace = i3;
                this.numericHint = i4;
                this.numericHintForFingerprint = i5;
                this.numericHintForFace = i6;
                this.alphaMessage = i7;
                this.alphaMessageForBiometrics = i8;
                this.numericMessage = i9;
                this.numericMessageForBiometrics = i10;
                this.buttonText = i11;
            }

            public int getHint(boolean z, int i) {
                if (z) {
                    if (i == 1) {
                        return this.alphaHintForFingerprint;
                    }
                    if (i == 2) {
                        return this.alphaHintForFace;
                    }
                    return this.alphaHint;
                } else if (i == 1) {
                    return this.numericHintForFingerprint;
                } else {
                    if (i == 2) {
                        return this.numericHintForFace;
                    }
                    return this.numericHint;
                }
            }

            public int getMessage(boolean z, int i) {
                return z ? i != 0 ? this.alphaMessageForBiometrics : this.alphaMessage : i != 0 ? this.numericMessageForBiometrics : this.numericMessage;
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mLockPatternUtils = new LockPatternUtils(getActivity());
            Intent intent = getActivity().getIntent();
            if (getActivity() instanceof ChooseLockPassword) {
                this.mUserId = Utils.getUserIdFromBundle(getActivity(), intent.getExtras());
                this.mForFingerprint = intent.getBooleanExtra("for_fingerprint", false);
                this.mForFace = intent.getBooleanExtra("for_face", false);
                this.mMinComplexity = intent.getIntExtra("requested_min_complexity", 0);
                this.mRequestedQuality = intent.getIntExtra("lockscreen.password_type", 131072);
                this.mUnificationProfileId = intent.getIntExtra("unification_profile_id", -10000);
                PasswordMetrics requestedPasswordMetrics = this.mLockPatternUtils.getRequestedPasswordMetrics(this.mUserId);
                this.mMinMetrics = requestedPasswordMetrics;
                int i = this.mUnificationProfileId;
                if (i != -10000) {
                    requestedPasswordMetrics.maxWith(this.mLockPatternUtils.getRequestedPasswordMetrics(i));
                }
                this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
                if (intent.getBooleanExtra("for_cred_req_boot", false)) {
                    SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
                    boolean booleanExtra = getActivity().getIntent().getBooleanExtra("extra_require_password", true);
                    LockscreenCredential lockscreenCredential = (LockscreenCredential) intent.getParcelableExtra("password");
                    saveAndFinishWorker.setBlocking(true);
                    saveAndFinishWorker.setListener(this);
                    saveAndFinishWorker.start(this.mChooseLockSettingsHelper.utils(), booleanExtra, false, 0, lockscreenCredential, lockscreenCredential, this.mUserId);
                }
                this.mTextChangedHandler = new TextChangedHandler();
                return;
            }
            throw new SecurityException("Fragment contained in wrong activity");
        }

        @Override // androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(C0012R$layout.choose_lock_password, viewGroup, false);
        }

        @Override // androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            this.mLayout = (GlifLayout) view;
            ((ViewGroup) view.findViewById(C0010R$id.password_container)).setOpticalInsets(Insets.NONE);
            FooterBarMixin footerBarMixin = (FooterBarMixin) this.mLayout.getMixin(FooterBarMixin.class);
            FooterButton.Builder builder = new FooterButton.Builder(getActivity());
            builder.setText(C0017R$string.lockpassword_clear_label);
            builder.setListener(new View.OnClickListener() {
                /* class com.android.settings.password.$$Lambda$ikzTUby0RNf2Od_PDDYThVTUDX4 */

                public final void onClick(View view) {
                    ChooseLockPassword.ChooseLockPasswordFragment.this.onSkipOrClearButtonClick(view);
                }
            });
            builder.setButtonType(7);
            builder.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
            footerBarMixin.setSecondaryButton(builder.build());
            FooterButton.Builder builder2 = new FooterButton.Builder(getActivity());
            builder2.setText(C0017R$string.next_label);
            builder2.setListener(new View.OnClickListener() {
                /* class com.android.settings.password.$$Lambda$7NRrBWB0RaxJKWMsiZuTfUSqmeE */

                public final void onClick(View view) {
                    ChooseLockPassword.ChooseLockPasswordFragment.this.onNextButtonClick(view);
                }
            });
            builder2.setButtonType(5);
            builder2.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
            footerBarMixin.setPrimaryButton(builder2.build());
            this.mSkipOrClearButton = footerBarMixin.getSecondaryButton();
            this.mNextButton = footerBarMixin.getPrimaryButton();
            this.mMessage = (TextView) view.findViewById(C0010R$id.sud_layout_description);
            if (this.mForFingerprint) {
                this.mLayout.setIcon(getActivity().getDrawable(C0008R$drawable.ic_opfinger_logo_bg));
            } else if (this.mForFace) {
                this.mLayout.setIcon(getActivity().getDrawable(C0008R$drawable.op_face_settings));
            }
            int i = this.mRequestedQuality;
            this.mIsAlphaMode = 262144 == i || 327680 == i || 393216 == i;
            setupPasswordRequirementsView(view);
            this.mPasswordRestrictionView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ImeAwareEditText findViewById = view.findViewById(C0010R$id.password_entry);
            this.mPasswordEntry = findViewById;
            findViewById.setOnEditorActionListener(this);
            this.mPasswordEntry.addTextChangedListener(this);
            this.mPasswordEntry.requestFocus();
            this.mPasswordEntryInputDisabler = new TextViewInputDisabler(this.mPasswordEntry);
            FragmentActivity activity = getActivity();
            int inputType = this.mPasswordEntry.getInputType();
            ImeAwareEditText imeAwareEditText = this.mPasswordEntry;
            if (!this.mIsAlphaMode) {
                inputType = 18;
            }
            imeAwareEditText.setInputType(inputType);
            if (this.mIsAlphaMode) {
                this.mPasswordEntry.setContentDescription(getString(C0017R$string.unlock_set_unlock_password_title));
            } else {
                this.mPasswordEntry.setContentDescription(getString(C0017R$string.unlock_set_unlock_pin_title));
            }
            this.mPasswordEntry.setTypeface(Typeface.create(getContext().getString(17039912), 0));
            Intent intent = getActivity().getIntent();
            boolean booleanExtra = intent.getBooleanExtra("confirm_credentials", true);
            this.mCurrentCredential = intent.getParcelableExtra("password");
            this.mHasChallenge = intent.getBooleanExtra("has_challenge", false);
            this.mChallenge = intent.getLongExtra("challenge", 0);
            if (bundle == null) {
                updateStage(Stage.Introduction);
                if (booleanExtra) {
                    this.mChooseLockSettingsHelper.launchConfirmationActivity(58, getString(C0017R$string.unlock_set_unlock_launch_picker_title), true, this.mUserId);
                }
            } else {
                this.mFirstPassword = bundle.getParcelable("first_password");
                String string = bundle.getString("ui_stage");
                if (string != null) {
                    Stage valueOf = Stage.valueOf(string);
                    this.mUiStage = valueOf;
                    updateStage(valueOf);
                }
                if (this.mCurrentCredential == null) {
                    this.mCurrentCredential = bundle.getParcelable("current_credential");
                }
                this.mSaveAndFinishWorker = (SaveAndFinishWorker) getFragmentManager().findFragmentByTag("save_and_finish_worker");
            }
            if (activity instanceof SettingsActivity) {
                int hint = Stage.Introduction.getHint(this.mIsAlphaMode, getStageType());
                ((SettingsActivity) activity).setTitle(hint);
                this.mLayout.setHeaderText(hint);
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
        public int getStageType() {
            if (this.mForFingerprint) {
                return 1;
            }
            return this.mForFace ? 2 : 0;
        }

        private void setupPasswordRequirementsView(View view) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(C0010R$id.password_requirements_view);
            this.mPasswordRestrictionView = recyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            PasswordRequirementAdapter passwordRequirementAdapter = new PasswordRequirementAdapter();
            this.mPasswordRequirementAdapter = passwordRequirementAdapter;
            this.mPasswordRestrictionView.setAdapter(passwordRequirementAdapter);
        }

        @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            updateStage(this.mUiStage);
            SaveAndFinishWorker saveAndFinishWorker = this.mSaveAndFinishWorker;
            if (saveAndFinishWorker != null) {
                saveAndFinishWorker.setListener(this);
                return;
            }
            this.mPasswordEntry.requestFocus();
            this.mPasswordEntry.scheduleShowSoftInput();
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onPause() {
            SaveAndFinishWorker saveAndFinishWorker = this.mSaveAndFinishWorker;
            if (saveAndFinishWorker != null) {
                saveAndFinishWorker.setListener(null);
            }
            super.onPause();
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putString("ui_stage", this.mUiStage.name());
            bundle.putParcelable("first_password", this.mFirstPassword);
            bundle.putParcelable("current_credential", this.mCurrentCredential);
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 58) {
                if (i2 != -1) {
                    getActivity().setResult(1);
                    getActivity().finish();
                    return;
                }
                this.mCurrentCredential = intent.getParcelableExtra("password");
            }
        }

        /* access modifiers changed from: protected */
        public Intent getRedactionInterstitialIntent(Context context) {
            return RedactionInterstitial.createStartIntent(context, this.mUserId);
        }

        /* access modifiers changed from: protected */
        public void updateStage(Stage stage) {
            Stage stage2 = this.mUiStage;
            this.mUiStage = stage;
            updateUi();
            if (stage2 != stage) {
                GlifLayout glifLayout = this.mLayout;
                glifLayout.announceForAccessibility(glifLayout.getHeaderText());
            }
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public boolean validatePassword(LockscreenCredential lockscreenCredential) {
            byte[] credential = lockscreenCredential.getCredential();
            List<PasswordValidationError> validatePassword = PasswordMetrics.validatePassword(this.mMinMetrics, this.mMinComplexity, !this.mIsAlphaMode, credential);
            this.mValidationErrors = validatePassword;
            if (validatePassword.isEmpty() && this.mLockPatternUtils.checkPasswordHistory(credential, getPasswordHistoryHashFactor(), this.mUserId)) {
                this.mValidationErrors = Collections.singletonList(new PasswordValidationError(13));
            }
            return this.mValidationErrors.isEmpty();
        }

        private byte[] getPasswordHistoryHashFactor() {
            if (this.mPasswordHistoryHashFactor == null) {
                LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
                LockscreenCredential lockscreenCredential = this.mCurrentCredential;
                if (lockscreenCredential == null) {
                    lockscreenCredential = LockscreenCredential.createNone();
                }
                this.mPasswordHistoryHashFactor = lockPatternUtils.getPasswordHistoryHashFactor(lockscreenCredential, this.mUserId);
            }
            return this.mPasswordHistoryHashFactor;
        }

        public void handleNext() {
            LockscreenCredential lockscreenCredential;
            if (this.mSaveAndFinishWorker == null) {
                Editable text = this.mPasswordEntry.getText();
                if (!TextUtils.isEmpty(text)) {
                    if (this.mIsAlphaMode) {
                        lockscreenCredential = LockscreenCredential.createPassword(text);
                    } else {
                        lockscreenCredential = LockscreenCredential.createPin(text);
                    }
                    this.mChosenPassword = lockscreenCredential;
                    Stage stage = this.mUiStage;
                    if (stage == Stage.Introduction) {
                        if (validatePassword(lockscreenCredential)) {
                            this.mFirstPassword = this.mChosenPassword;
                            this.mPasswordEntry.setText("");
                            updateStage(Stage.NeedToConfirm);
                            return;
                        }
                        this.mChosenPassword.zeroize();
                    } else if (stage != Stage.NeedToConfirm) {
                    } else {
                        if (lockscreenCredential.equals(this.mFirstPassword)) {
                            startSaveAndFinish();
                            if (!this.mIsAlphaMode) {
                                OPUtils.savePINPasswordLength(this.mLockPatternUtils, text.length(), this.mUserId);
                            } else {
                                OPUtils.savePINPasswordLength(this.mLockPatternUtils, 0, this.mUserId);
                            }
                        } else {
                            Editable text2 = this.mPasswordEntry.getText();
                            if (text2 != null) {
                                Selection.setSelection(text2, 0, text2.length());
                            }
                            updateStage(Stage.ConfirmWrong);
                            this.mChosenPassword.zeroize();
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void setNextEnabled(boolean z) {
            this.mNextButton.setEnabled(z);
        }

        /* access modifiers changed from: protected */
        public void setNextText(int i) {
            this.mNextButton.setText(getActivity(), i);
        }

        /* access modifiers changed from: protected */
        public void onSkipOrClearButtonClick(View view) {
            this.mPasswordEntry.setText("");
        }

        /* access modifiers changed from: protected */
        public void onNextButtonClick(View view) {
            handleNext();
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 0 && i != 6 && i != 5) {
                return false;
            }
            handleNext();
            return true;
        }

        /* access modifiers changed from: package-private */
        public String[] convertErrorCodeToMessages() {
            int i;
            int i2;
            int i3;
            ArrayList arrayList = new ArrayList();
            for (PasswordValidationError passwordValidationError : this.mValidationErrors) {
                switch (passwordValidationError.errorCode) {
                    case 2:
                        arrayList.add(getString(C0017R$string.lockpassword_illegal_character));
                        break;
                    case 3:
                        Resources resources = getResources();
                        if (this.mIsAlphaMode) {
                            i = C0015R$plurals.lockpassword_password_too_short;
                        } else {
                            i = C0015R$plurals.lockpassword_pin_too_short;
                        }
                        int i4 = passwordValidationError.requirement;
                        arrayList.add(resources.getQuantityString(i, i4, Integer.valueOf(i4)));
                        break;
                    case 4:
                        Resources resources2 = getResources();
                        if (this.mIsAlphaMode) {
                            i2 = C0015R$plurals.lockpassword_password_too_long;
                        } else {
                            i2 = C0015R$plurals.lockpassword_pin_too_long;
                        }
                        int i5 = passwordValidationError.requirement;
                        arrayList.add(resources2.getQuantityString(i2, i5 + 1, Integer.valueOf(i5 + 1)));
                        break;
                    case 5:
                        arrayList.add(getString(C0017R$string.lockpassword_pin_no_sequential_digits));
                        break;
                    case 6:
                        Resources resources3 = getResources();
                        int i6 = C0015R$plurals.lockpassword_password_requires_letters;
                        int i7 = passwordValidationError.requirement;
                        arrayList.add(resources3.getQuantityString(i6, i7, Integer.valueOf(i7)));
                        break;
                    case 7:
                        Resources resources4 = getResources();
                        int i8 = C0015R$plurals.lockpassword_password_requires_uppercase;
                        int i9 = passwordValidationError.requirement;
                        arrayList.add(resources4.getQuantityString(i8, i9, Integer.valueOf(i9)));
                        break;
                    case 8:
                        Resources resources5 = getResources();
                        int i10 = C0015R$plurals.lockpassword_password_requires_lowercase;
                        int i11 = passwordValidationError.requirement;
                        arrayList.add(resources5.getQuantityString(i10, i11, Integer.valueOf(i11)));
                        break;
                    case 9:
                        Resources resources6 = getResources();
                        int i12 = C0015R$plurals.lockpassword_password_requires_numeric;
                        int i13 = passwordValidationError.requirement;
                        arrayList.add(resources6.getQuantityString(i12, i13, Integer.valueOf(i13)));
                        break;
                    case 10:
                        Resources resources7 = getResources();
                        int i14 = C0015R$plurals.lockpassword_password_requires_symbols;
                        int i15 = passwordValidationError.requirement;
                        arrayList.add(resources7.getQuantityString(i14, i15, Integer.valueOf(i15)));
                        break;
                    case 11:
                        Resources resources8 = getResources();
                        int i16 = C0015R$plurals.lockpassword_password_requires_nonletter;
                        int i17 = passwordValidationError.requirement;
                        arrayList.add(resources8.getQuantityString(i16, i17, Integer.valueOf(i17)));
                        break;
                    case 12:
                        Resources resources9 = getResources();
                        int i18 = C0015R$plurals.lockpassword_password_requires_nonnumerical;
                        int i19 = passwordValidationError.requirement;
                        arrayList.add(resources9.getQuantityString(i18, i19, Integer.valueOf(i19)));
                        break;
                    case 13:
                        if (this.mIsAlphaMode) {
                            i3 = C0017R$string.lockpassword_password_recently_used;
                        } else {
                            i3 = C0017R$string.lockpassword_pin_recently_used;
                        }
                        arrayList.add(getString(i3));
                        break;
                    default:
                        Log.wtf("ChooseLockPassword", "unknown error validating password: " + passwordValidationError);
                        break;
                }
            }
            return (String[]) arrayList.toArray(new String[0]);
        }

        /* access modifiers changed from: protected */
        public void updateUi() {
            LockscreenCredential lockscreenCredential;
            boolean z = true;
            boolean z2 = this.mSaveAndFinishWorker == null;
            if (this.mIsAlphaMode) {
                lockscreenCredential = LockscreenCredential.createPasswordOrNone(this.mPasswordEntry.getText());
            } else {
                lockscreenCredential = LockscreenCredential.createPinOrNone(this.mPasswordEntry.getText());
            }
            int size = lockscreenCredential.size();
            if (this.mUiStage == Stage.Introduction) {
                this.mPasswordRestrictionView.setVisibility(0);
                boolean validatePassword = validatePassword(lockscreenCredential);
                this.mPasswordRequirementAdapter.setRequirements(convertErrorCodeToMessages());
                setNextEnabled(validatePassword);
            } else {
                this.mPasswordRestrictionView.setVisibility(8);
                setHeaderText(getString(this.mUiStage.getHint(this.mIsAlphaMode, getStageType())));
                setNextEnabled(z2 && size >= 4);
                FooterButton footerButton = this.mSkipOrClearButton;
                if (!z2 || size <= 0) {
                    z = false;
                }
                footerButton.setVisibility(toVisibility(z));
            }
            int message = this.mUiStage.getMessage(this.mIsAlphaMode, getStageType());
            if (message != 0) {
                this.mMessage.setVisibility(8);
                this.mMessage.setText(message);
            } else {
                this.mMessage.setVisibility(8);
            }
            setNextText(this.mUiStage.buttonText);
            this.mPasswordEntryInputDisabler.setInputEnabled(z2);
            lockscreenCredential.zeroize();
        }

        private void setHeaderText(String str) {
            if (TextUtils.isEmpty(this.mLayout.getHeaderText()) || !this.mLayout.getHeaderText().toString().equals(str)) {
                this.mLayout.setHeaderText(str);
            }
        }

        public void afterTextChanged(Editable editable) {
            if (this.mUiStage == Stage.ConfirmWrong) {
                this.mUiStage = Stage.NeedToConfirm;
            }
            this.mTextChangedHandler.notifyAfterTextChanged();
        }

        private void startSaveAndFinish() {
            if (this.mSaveAndFinishWorker != null) {
                Log.w("ChooseLockPassword", "startSaveAndFinish with an existing SaveAndFinishWorker.");
                return;
            }
            this.mPasswordEntryInputDisabler.setInputEnabled(false);
            setNextEnabled(false);
            SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
            this.mSaveAndFinishWorker = saveAndFinishWorker;
            saveAndFinishWorker.setListener(this);
            FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
            beginTransaction.add(this.mSaveAndFinishWorker, "save_and_finish_worker");
            beginTransaction.commit();
            getFragmentManager().executePendingTransactions();
            Intent intent = getActivity().getIntent();
            boolean booleanExtra = intent.getBooleanExtra("extra_require_password", true);
            if (this.mUnificationProfileId != -10000) {
                LockscreenCredential parcelableExtra = intent.getParcelableExtra("unification_profile_credential");
                try {
                    this.mSaveAndFinishWorker.setProfileToUnify(this.mUnificationProfileId, parcelableExtra);
                    if (parcelableExtra != null) {
                        parcelableExtra.close();
                    }
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            }
            this.mSaveAndFinishWorker.start(this.mLockPatternUtils, booleanExtra, this.mHasChallenge, this.mChallenge, this.mChosenPassword, this.mCurrentCredential, this.mUserId);
            return;
            throw th;
        }

        @Override // com.android.settings.password.SaveChosenLockWorkerBase.Listener
        public void onChosenLockSaveFinished(boolean z, Intent intent) {
            Intent redactionInterstitialIntent;
            getActivity().setResult(1, intent);
            LockscreenCredential lockscreenCredential = this.mChosenPassword;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            LockscreenCredential lockscreenCredential2 = this.mCurrentCredential;
            if (lockscreenCredential2 != null) {
                lockscreenCredential2.zeroize();
            }
            LockscreenCredential lockscreenCredential3 = this.mFirstPassword;
            if (lockscreenCredential3 != null) {
                lockscreenCredential3.zeroize();
            }
            this.mPasswordEntry.setText("");
            if (!z && (redactionInterstitialIntent = getRedactionInterstitialIntent(getActivity())) != null) {
                startActivity(redactionInterstitialIntent);
            }
            getActivity().finish();
        }

        class TextChangedHandler extends Handler {
            TextChangedHandler() {
            }

            /* access modifiers changed from: private */
            /* access modifiers changed from: public */
            private void notifyAfterTextChanged() {
                removeMessages(1);
                sendEmptyMessageDelayed(1, 100);
            }

            public void handleMessage(Message message) {
                if (ChooseLockPasswordFragment.this.getActivity() != null && message.what == 1) {
                    ChooseLockPasswordFragment.this.updateUi();
                }
            }
        }
    }

    public static class SaveAndFinishWorker extends SaveChosenLockWorkerBase {
        private LockscreenCredential mChosenPassword;
        private LockscreenCredential mCurrentCredential;

        public void start(LockPatternUtils lockPatternUtils, boolean z, boolean z2, long j, LockscreenCredential lockscreenCredential, LockscreenCredential lockscreenCredential2, int i) {
            prepare(lockPatternUtils, z, z2, j, i);
            this.mChosenPassword = lockscreenCredential;
            if (lockscreenCredential2 == null) {
                lockscreenCredential2 = LockscreenCredential.createNone();
            }
            this.mCurrentCredential = lockscreenCredential2;
            this.mUserId = i;
            start();
        }

        /* JADX DEBUG: Multi-variable search result rejected for r6v3, resolved type: android.content.Intent */
        /* JADX WARN: Multi-variable type inference failed */
        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.SaveChosenLockWorkerBase
        public Pair<Boolean, Intent> saveAndVerifyInBackground() {
            boolean lockCredential = this.mUtils.setLockCredential(this.mChosenPassword, this.mCurrentCredential, this.mUserId);
            if (lockCredential) {
                unifyProfileCredentialIfRequested();
            }
            byte[] bArr = null;
            if (lockCredential && this.mHasChallenge) {
                try {
                    bArr = this.mUtils.verifyCredential(this.mChosenPassword, this.mChallenge, this.mUserId);
                } catch (LockPatternUtils.RequestThrottledException unused) {
                }
                if (bArr == null) {
                    Log.e("ChooseLockPassword", "critical: no token returned for known good password.");
                }
                Intent intent = new Intent();
                intent.putExtra("hw_auth_token", bArr);
                bArr = intent;
            }
            return Pair.create(Boolean.valueOf(lockCredential), bArr);
        }
    }
}
