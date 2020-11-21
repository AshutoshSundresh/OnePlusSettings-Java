package com.android.settings.biometrics;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.TextView;
import com.android.settings.C0002R$anim;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.SetupWizardUtils;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.password.SetupChooseLockGeneric;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.span.LinkSpan;

public abstract class BiometricEnrollIntroduction extends BiometricEnrollBase implements LinkSpan.OnClickListener {
    private boolean mBiometricUnlockDisabledByAdmin;
    protected boolean mConfirmingCredentials;
    private TextView mErrorText;
    private boolean mHasPassword;
    protected boolean mNextClicked;
    private UserManager mUserManager;

    /* access modifiers changed from: protected */
    public abstract int checkMaxEnrolled();

    /* access modifiers changed from: protected */
    public abstract long getChallenge();

    /* access modifiers changed from: protected */
    public abstract int getConfirmLockTitleResId();

    /* access modifiers changed from: protected */
    public abstract int getDescriptionResDisabledByAdmin();

    /* access modifiers changed from: protected */
    public abstract Intent getEnrollingIntent();

    /* access modifiers changed from: protected */
    public abstract TextView getErrorTextView();

    /* access modifiers changed from: protected */
    public abstract String getExtraKeyForBiometric();

    /* access modifiers changed from: protected */
    public abstract int getHeaderResDefault();

    /* access modifiers changed from: protected */
    public abstract int getHeaderResDisabledByAdmin();

    /* access modifiers changed from: protected */
    public abstract int getLayoutResource();

    /* access modifiers changed from: protected */
    public abstract boolean isDisabledByAdmin();

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent.getStringExtra("theme") == null) {
            intent.putExtra("theme", SetupWizardUtils.getThemeString(intent));
        }
        this.mBiometricUnlockDisabledByAdmin = isDisabledByAdmin();
        setContentView(getLayoutResource());
        if (this.mBiometricUnlockDisabledByAdmin) {
            setHeaderText(getHeaderResDisabledByAdmin());
        } else {
            setHeaderText(getHeaderResDefault());
        }
        this.mErrorText = getErrorTextView();
        this.mUserManager = UserManager.get(this);
        updatePasswordQuality();
        if (!this.mHasPassword) {
            this.mConfirmingCredentials = true;
            launchChooseLock();
        } else if (this.mToken == null) {
            this.mConfirmingCredentials = true;
            launchConfirmLock(getConfirmLockTitleResId(), getChallenge());
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        int checkMaxEnrolled = checkMaxEnrolled();
        if (checkMaxEnrolled == 0) {
            this.mErrorText.setText((CharSequence) null);
            this.mErrorText.setVisibility(8);
            getNextButton().setVisibility(0);
            return;
        }
        this.mErrorText.setText(checkMaxEnrolled);
        this.mErrorText.setVisibility(0);
        getNextButton().setText(getResources().getString(C0017R$string.done));
        getNextButton().setVisibility(0);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public boolean shouldFinishWhenBackgrounded() {
        return super.shouldFinishWhenBackgrounded() && !this.mConfirmingCredentials && !this.mNextClicked;
    }

    private void updatePasswordQuality() {
        this.mHasPassword = new ChooseLockSettingsHelper(this).utils().getActivePasswordQuality(this.mUserManager.getCredentialOwnerProfile(this.mUserId)) != 0;
    }

    /* access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        this.mNextClicked = true;
        if (checkMaxEnrolled() == 0) {
            launchNextEnrollingActivity(this.mToken);
            return;
        }
        setResult(1);
        finish();
    }

    private void launchChooseLock() {
        Intent chooseLockIntent = getChooseLockIntent();
        long challenge = getChallenge();
        chooseLockIntent.putExtra("minimum_quality", 65536);
        chooseLockIntent.putExtra("hide_disabled_prefs", true);
        chooseLockIntent.putExtra("has_challenge", true);
        chooseLockIntent.putExtra("challenge", challenge);
        chooseLockIntent.putExtra(getExtraKeyForBiometric(), true);
        int i = this.mUserId;
        if (i != -10000) {
            chooseLockIntent.putExtra("android.intent.extra.USER_ID", i);
        }
        startActivityForResult(chooseLockIntent, 1);
    }

    private void launchNextEnrollingActivity(byte[] bArr) {
        Intent enrollingIntent = getEnrollingIntent();
        if (bArr != null) {
            enrollingIntent.putExtra("hw_auth_token", bArr);
        }
        int i = this.mUserId;
        if (i != -10000) {
            enrollingIntent.putExtra("android.intent.extra.USER_ID", i);
        }
        enrollingIntent.putExtra("from_settings_summary", this.mFromSettingsSummary);
        startActivityForResult(enrollingIntent, 2);
    }

    /* access modifiers changed from: protected */
    public Intent getChooseLockIntent() {
        if (!WizardManagerHelper.isAnySetupWizard(getIntent())) {
            return new Intent(this, ChooseLockGeneric.class);
        }
        Intent intent = new Intent(this, SetupChooseLockGeneric.class);
        if (StorageManager.isFileEncryptedNativeOrEmulated()) {
            intent.putExtra("lockscreen.password_type", 131072);
            intent.putExtra("show_options_button", true);
        }
        WizardManagerHelper.copyWizardManagerExtras(getIntent(), intent);
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 2) {
            if (i2 == 1 || i2 == 2 || i2 == 3) {
                setResult(i2, intent);
                finish();
                return;
            }
        } else if (i == 1) {
            if (i2 == 1) {
                updatePasswordQuality();
                this.mToken = intent.getByteArrayExtra("hw_auth_token");
                overridePendingTransition(C0002R$anim.sud_slide_next_in, C0002R$anim.sud_slide_next_out);
                this.mConfirmingCredentials = false;
                return;
            }
            setResult(i2, intent);
            finish();
        } else if (i == 4) {
            this.mConfirmingCredentials = false;
            if (i2 != -1 || intent == null) {
                setResult(i2, intent);
                finish();
            } else {
                this.mToken = intent.getByteArrayExtra("hw_auth_token");
                overridePendingTransition(C0002R$anim.sud_slide_next_in, C0002R$anim.sud_slide_next_out);
            }
        } else if (i == 3) {
            overridePendingTransition(C0002R$anim.sud_slide_back_in, C0002R$anim.sud_slide_back_out);
        }
        super.onActivityResult(i, i2, intent);
    }

    /* access modifiers changed from: protected */
    public void onCancelButtonClick(View view) {
        finish();
    }

    /* access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        setResult(2);
        finish();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public void initViews() {
        super.initViews();
        TextView textView = (TextView) findViewById(C0010R$id.sud_layout_description);
        if (this.mBiometricUnlockDisabledByAdmin) {
            textView.setText(getDescriptionResDisabledByAdmin());
        }
    }
}
