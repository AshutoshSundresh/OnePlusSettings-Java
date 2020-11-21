package com.android.settings.biometrics;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling;
import com.android.settings.core.InstrumentedActivity;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.oneplus.settings.OPMemberController;

public abstract class BiometricEnrollBase extends InstrumentedActivity {
    protected FooterBarMixin mFooterBarMixin;
    protected boolean mFromSettingsSummary;
    protected boolean mLaunchedConfirmLock;
    protected byte[] mToken;
    protected int mUserId;

    /* access modifiers changed from: protected */
    public boolean shouldFinishWhenBackgrounded() {
        return false;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
        this.mFromSettingsSummary = getIntent().getBooleanExtra("from_settings_summary", false);
        if (bundle != null && this.mToken == null) {
            this.mLaunchedConfirmLock = bundle.getBoolean("launched_confirm_lock");
            this.mToken = bundle.getByteArray("hw_auth_token");
            this.mFromSettingsSummary = bundle.getBoolean("from_settings_summary", false);
        }
        this.mUserId = getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("launched_confirm_lock", this.mLaunchedConfirmLock);
        bundle.putByteArray("hw_auth_token", this.mToken);
        bundle.putBoolean("from_settings_summary", this.mFromSettingsSummary);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity
    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        initViews();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStop() {
        super.onStop();
        if (!isChangingConfigurations() && shouldFinishWhenBackgrounded()) {
            setResult(3);
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void initViews() {
        getWindow().setStatusBarColor(0);
    }

    /* access modifiers changed from: protected */
    public GlifLayout getLayout() {
        return (GlifLayout) findViewById(C0010R$id.setup_wizard_layout);
    }

    /* access modifiers changed from: protected */
    public void setHeaderText(int i, boolean z) {
        TextView headerTextView = getLayout().getHeaderTextView();
        CharSequence text = headerTextView.getText();
        CharSequence text2 = getText(i);
        if (text != text2 || z) {
            if (!TextUtils.isEmpty(text)) {
                headerTextView.setAccessibilityLiveRegion(1);
            }
            getLayout().setHeaderText(text2);
            setTitle(text2);
        }
    }

    /* access modifiers changed from: protected */
    public void setHeaderText(int i) {
        setHeaderText(i, false);
    }

    /* access modifiers changed from: protected */
    public FooterButton getNextButton() {
        FooterBarMixin footerBarMixin = this.mFooterBarMixin;
        if (footerBarMixin != null) {
            return footerBarMixin.getPrimaryButton();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public Intent getFingerprintEnrollingIntent() {
        Intent intent = new Intent();
        intent.setClassName(OPMemberController.PACKAGE_NAME, FingerprintEnrollEnrolling.class.getName());
        intent.putExtra("hw_auth_token", this.mToken);
        intent.putExtra("from_settings_summary", this.mFromSettingsSummary);
        int i = this.mUserId;
        if (i != -10000) {
            intent.putExtra("android.intent.extra.USER_ID", i);
        }
        return intent;
    }

    /* access modifiers changed from: protected */
    public void launchConfirmLock(int i, long j) {
        boolean z;
        ChooseLockSettingsHelper chooseLockSettingsHelper = new ChooseLockSettingsHelper(this);
        if (this.mUserId == -10000) {
            z = chooseLockSettingsHelper.launchConfirmationActivity(4, getString(i), null, null, j, true);
        } else {
            z = chooseLockSettingsHelper.launchConfirmationActivity(4, (CharSequence) getString(i), (CharSequence) null, (CharSequence) null, j, this.mUserId, true);
        }
        if (!z) {
            finish();
        } else {
            this.mLaunchedConfirmLock = true;
        }
    }
}
