package com.oneplus.settings.faceunlock;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.biometrics.fingerprint.SetupFingerprintEnrollIntroduction;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.utils.OPUtils;

public class OPFaceUnlockModeSettingsActivity extends BaseActivity implements View.OnClickListener {
    private Button mDoneButton;
    private boolean mFromSetupWizard = false;
    private LottieAnimationView mLottieAnim;
    private View mPressPowerkey;
    private RadioButton mPressPowerkeyButton;
    private View mSwipeUp;
    private RadioButton mSwipeUpButton;
    protected byte[] mToken;
    protected int mUserId;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mFromSetupWizard = getIntent().getBooleanExtra("enter_faceunlock_mode_settings_from_suw", false);
        this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
        this.mUserId = getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
        if (this.mFromSetupWizard) {
            setTheme(C0018R$style.SetupOnePlusPasswordTheme);
        }
        setContentView(C0012R$layout.op_faceunlock_mode_set_activity);
        getWindow().getDecorView().setSystemUiVisibility(0);
        View findViewById = findViewById(C0010R$id.key_faceunlock_swipe_up_mode);
        this.mSwipeUp = findViewById;
        findViewById.setOnClickListener(this);
        View findViewById2 = findViewById(C0010R$id.key_faceunlock_use_power_button_mode);
        this.mPressPowerkey = findViewById2;
        findViewById2.setOnClickListener(this);
        this.mSwipeUpButton = (RadioButton) findViewById(C0010R$id.key_faceunlock_swipe_up_mode_radiobutton);
        this.mPressPowerkeyButton = (RadioButton) findViewById(C0010R$id.key_faceunlock_use_power_button_mode_radiobutton);
        this.mLottieAnim = (LottieAnimationView) findViewById(C0010R$id.op_single_lottie_view);
        Button button = (Button) findViewById(C0010R$id.done_button);
        this.mDoneButton = button;
        button.setOnClickListener(this);
        if (this.mFromSetupWizard) {
            this.mDoneButton.setText(C0017R$string.next_label);
        }
        adjustTitleSize();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        setViewType(getUnlockMode());
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        stopAnim();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
        releaseAnim();
    }

    private void setModeSelected() {
        if (getUnlockMode() == 0) {
            this.mSwipeUpButton.setChecked(true);
            this.mPressPowerkeyButton.setChecked(false);
            return;
        }
        this.mSwipeUpButton.setChecked(false);
        this.mPressPowerkeyButton.setChecked(true);
    }

    private void setAnimationResource() {
        if (getUnlockMode() == 0) {
            this.mLottieAnim.setAnimation("op_face_unlock_by_swipe_up_dark.json");
        } else {
            this.mLottieAnim.setAnimation("op_face_unlock_by_use_power_key_dark.json");
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == C0010R$id.key_faceunlock_swipe_up_mode) {
            setViewType(0);
        } else if (id == C0010R$id.key_faceunlock_use_power_button_mode) {
            setViewType(1);
        } else if (id != C0010R$id.done_button) {
        } else {
            if (this.mFromSetupWizard) {
                Intent intent = new Intent(this, SetupFingerprintEnrollIntroduction.class);
                byte[] bArr = this.mToken;
                if (bArr != null) {
                    intent.putExtra("hw_auth_token", bArr);
                }
                int i = this.mUserId;
                if (i != -10000) {
                    intent.putExtra("android.intent.extra.USER_ID", i);
                }
                startActivityForResult(intent, 6);
                finish();
                return;
            }
            finish();
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 6) {
            if (i2 != 0) {
                setResult(i2);
            }
            finish();
        }
    }

    public int getUnlockMode() {
        return Settings.System.getInt(getContentResolver(), "oneplus_face_unlock_powerkey_recognize_enable", 0);
    }

    public void setViewType(int i) {
        Settings.System.putInt(getContentResolver(), "oneplus_face_unlock_powerkey_recognize_enable", i);
        OPUtils.sendAppTracker("pop_up_face_unlock", i);
        setModeSelected();
        stopAnim();
        setAnimationResource();
        this.mLottieAnim.playAnimation();
    }

    public void stopAnim() {
        LottieAnimationView lottieAnimationView = this.mLottieAnim;
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
        }
    }

    public void releaseAnim() {
        LottieAnimationView lottieAnimationView = this.mLottieAnim;
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
            this.mLottieAnim = null;
        }
    }

    private void adjustTitleSize() {
        if (OPUtils.isLargerFontSize(this) && OPUtils.isLargerScreenZoom(this)) {
            TextView textView = (TextView) findViewById(C0010R$id.op_faceunlock_mode_title);
            textView.setTextSize(20.0f);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.topMargin /= 2;
            textView.setLayoutParams(layoutParams);
        }
    }
}
