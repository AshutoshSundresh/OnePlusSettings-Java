package com.android.settings.password;

import android.app.KeyguardManager;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.android.settings.C0002R$anim;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.SettingsActivity;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settings.password.ConfirmLockPassword;
import com.android.settings.password.ConfirmLockPattern;

public abstract class ConfirmDeviceCredentialBaseActivity extends SettingsActivity {
    private ConfirmCredentialTheme mConfirmCredentialTheme;
    private boolean mEnterAnimationPending;
    private boolean mFirstTimeVisible = true;
    private boolean mIsKeyguardLocked = false;
    private boolean mRestoring;

    /* access modifiers changed from: package-private */
    public enum ConfirmCredentialTheme {
        NORMAL,
        DARK,
        WORK
    }

    private boolean isInternalActivity() {
        return (this instanceof ConfirmLockPassword.InternalActivity) || (this instanceof ConfirmLockPattern.InternalActivity);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.android.settings.core.SettingsBaseActivity, com.android.settings.SettingsActivity
    public void onCreate(Bundle bundle) {
        boolean z;
        try {
            boolean z2 = false;
            if (UserManager.get(this).isManagedProfile(Utils.getCredentialOwnerUserId(this, Utils.getUserIdFromBundle(this, getIntent().getExtras(), isInternalActivity())))) {
                setTheme(SetupWizardUtils.getTheme(getIntent()));
                this.mConfirmCredentialTheme = ConfirmCredentialTheme.WORK;
            } else if (getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.darkTheme", false)) {
                setTheme(C0018R$style.Theme_ConfirmDeviceCredentialsDark);
                this.mConfirmCredentialTheme = ConfirmCredentialTheme.DARK;
            } else {
                setTheme(C0018R$style.OnePlusPasswordTheme);
                ConfirmCredentialTheme confirmCredentialTheme = ConfirmCredentialTheme.NORMAL;
                this.mConfirmCredentialTheme = confirmCredentialTheme;
                this.mConfirmCredentialTheme = confirmCredentialTheme;
            }
            this.mNeedShowAppBar = false;
            super.onCreate(bundle);
            if (isInMultiWindowMode()) {
                Toast.makeText(this, C0017R$string.feature_not_support_split_screen, 0).show();
                finish();
            }
            if (this.mConfirmCredentialTheme == ConfirmCredentialTheme.NORMAL) {
                findViewById(C0010R$id.content_parent).setFitsSystemWindows(true);
            }
            getWindow().addFlags(8192);
            if (bundle == null) {
                z = ((KeyguardManager) getSystemService(KeyguardManager.class)).isKeyguardLocked();
            } else {
                z = bundle.getBoolean("STATE_IS_KEYGUARD_LOCKED", false);
            }
            this.mIsKeyguardLocked = z;
            if (z && getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.showWhenLocked", false)) {
                getWindow().addFlags(524288);
            }
            setTitle(getIntent().getStringExtra("com.android.settings.ConfirmCredentials.title"));
            if (getActionBar() != null) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setHomeButtonEnabled(true);
            }
            if (bundle != null) {
                z2 = true;
            }
            this.mRestoring = z2;
        } catch (SecurityException e) {
            Log.e("ConfirmDeviceCredentialBaseActivity", "Invalid user Id supplied", e);
            finish();
        }
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.SettingsActivity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("STATE_IS_KEYGUARD_LOCKED", this.mIsKeyguardLocked);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    @Override // androidx.fragment.app.FragmentActivity, com.android.settings.core.SettingsBaseActivity, com.android.settings.SettingsActivity
    public void onResume() {
        super.onResume();
        if (!isChangingConfigurations() && !this.mRestoring && this.mConfirmCredentialTheme == ConfirmCredentialTheme.DARK && this.mFirstTimeVisible) {
            this.mFirstTimeVisible = false;
            prepareEnterAnimation();
            this.mEnterAnimationPending = true;
        }
    }

    private ConfirmDeviceCredentialBaseFragment getFragment() {
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(C0010R$id.main_content);
        if (findFragmentById == null || !(findFragmentById instanceof ConfirmDeviceCredentialBaseFragment)) {
            return null;
        }
        return (ConfirmDeviceCredentialBaseFragment) findFragmentById;
    }

    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        if (this.mEnterAnimationPending) {
            startEnterAnimation();
            this.mEnterAnimationPending = false;
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
        System.gc();
        System.runFinalization();
        System.gc();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStop() {
        super.onStop();
        boolean booleanExtra = getIntent().getBooleanExtra("foreground_only", false);
        if (!isChangingConfigurations() && booleanExtra) {
            finish();
        }
    }

    public void finish() {
        super.finish();
        if (getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.useFadeAnimation", false)) {
            overridePendingTransition(0, C0002R$anim.confirm_credential_biometric_transition_exit);
        }
    }

    public void prepareEnterAnimation() {
        getFragment().prepareEnterAnimation();
    }

    public void startEnterAnimation() {
        getFragment().startEnterAnimation();
    }

    public ConfirmCredentialTheme getConfirmCredentialTheme() {
        return this.mConfirmCredentialTheme;
    }
}
