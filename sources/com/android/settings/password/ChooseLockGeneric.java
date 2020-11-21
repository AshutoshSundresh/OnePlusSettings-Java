package com.android.settings.password;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.service.persistentdata.PersistentDataBlockManager;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.settings.C0002R$anim;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.EncryptionInterstitial;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollActivity;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.ChooseLockPassword;
import com.android.settings.password.ChooseLockPattern;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.oneplus.faceunlock.internal.IOPFaceSettingService;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.gestures.OPGestureUtils;
import com.oneplus.settings.utils.OPApplicationUtils;
import com.oneplus.settings.utils.OPUtils;

public class ChooseLockGeneric extends SettingsActivity {

    public static class InternalActivity extends ChooseLockGeneric {
    }

    @Override // com.android.settings.SettingsActivity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", getFragmentClass().getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ChooseLockGenericFragment.class.getName().equals(str);
    }

    /* access modifiers changed from: package-private */
    public Class<? extends Fragment> getFragmentClass() {
        return ChooseLockGenericFragment.class;
    }

    public static class ChooseLockGenericFragment extends SettingsPreferenceFragment {
        static final int CHOOSE_LOCK_BEFORE_BIOMETRIC_REQUEST = 103;
        static final int CHOOSE_LOCK_REQUEST = 102;
        static final int CONFIRM_EXISTING_REQUEST = 100;
        static final int ENABLE_ENCRYPTION_REQUEST = 101;
        static final int SKIP_FINGERPRINT_REQUEST = 104;
        private boolean isFaceServiceBinded = false;
        private ActivityManager mAm;
        private String mCallerAppName = null;
        private long mChallenge;
        private ChooseLockSettingsHelper mChooseLockSettingsHelper;
        private ChooseLockGenericController mController;
        private ProgressDialog mCryptfsChangepwDefaultProgressDialog;
        private DevicePolicyManager mDpm;
        private IOPFaceSettingService mFaceSettingService;
        private ServiceConnection mFaceUnlockConnection = new ServiceConnection() {
            /* class com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment.AnonymousClass1 */

            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i("ChooseLockGenericFragment", "Oneplus face unlock service connected");
                ChooseLockGenericFragment.this.isFaceServiceBinded = true;
                ChooseLockGenericFragment.this.mFaceSettingService = IOPFaceSettingService.Stub.asInterface(iBinder);
            }

            public void onServiceDisconnected(ComponentName componentName) {
                Log.i("ChooseLockGenericFragment", "Oneplus face unlock service disconnected");
                ChooseLockGenericFragment.this.isFaceServiceBinded = false;
                ChooseLockGenericFragment.this.mFaceSettingService = null;
            }
        };
        private FingerprintManager mFingerprintManager;
        private boolean mForChangeCredRequiredForBoot = false;
        protected boolean mForFace = false;
        protected boolean mForFingerprint = false;
        private Handler mHandler = new Handler();
        private boolean mHasChallenge = false;
        private boolean mIsCallingAppAdmin;
        private boolean mIsSetNewPassword = false;
        private LockPatternUtils mLockPatternUtils;
        private ManagedLockPasswordProvider mManagedPasswordProvider;
        private boolean mPasswordConfirmed = false;
        private int mRequestedMinComplexity;
        private LockscreenCredential mUnificationProfileCredential;
        private int mUnificationProfileId = -10000;
        private int mUserId;
        private LockscreenCredential mUserPassword;
        private boolean mWaitingForConfirmation = false;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 27;
        }

        private void removeFaceData() {
            IOPFaceSettingService iOPFaceSettingService = this.mFaceSettingService;
            if (iOPFaceSettingService != null) {
                try {
                    iOPFaceSettingService.removeFace(0);
                    unbindFaceUnlockService();
                    this.mHandler.postDelayed(new Runnable() {
                        /* class com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment.AnonymousClass2 */

                        public void run() {
                            OPApplicationUtils.killRunningTargetProcess(ChooseLockGenericFragment.this.mAm, "com.oneplus.faceunlock");
                            Log.i("ChooseLockGenericFragment", "Oneplus face unlock killRunningTargetProcess");
                        }
                    }, 500);
                } catch (RemoteException e) {
                    Log.i("ChooseLockGenericFragment", "Start remove face RemoteException:" + e);
                }
            }
        }

        private void bindFaceUnlockService() {
            try {
                Intent intent = new Intent();
                intent.setClassName("com.oneplus.faceunlock", "com.oneplus.faceunlock.FaceSettingService");
                getActivity().bindService(intent, this.mFaceUnlockConnection, 1);
                Log.i("ChooseLockGenericFragment", "Start bind oneplus face unlockservice");
            } catch (Exception unused) {
                Log.i("ChooseLockGenericFragment", "Bind oneplus face unlockservice exception");
            }
        }

        private void unbindFaceUnlockService() {
            Log.i("ChooseLockGenericFragment", "Start unbind oneplus face unlockservice");
            try {
                if (this.isFaceServiceBinded) {
                    getActivity().unbindService(this.mFaceUnlockConnection);
                }
            } catch (Exception unused) {
                Log.i("ChooseLockGenericFragment", "UnBind oneplus face unlockservice exception");
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            FragmentActivity activity = getActivity();
            Bundle arguments = getArguments();
            if (WizardManagerHelper.isDeviceProvisioned(activity) || canRunBeforeDeviceProvisioned()) {
                Intent intent = activity.getIntent();
                this.mAm = (ActivityManager) getSystemService("activity");
                bindFaceUnlockService();
                this.mCryptfsChangepwDefaultProgressDialog = new ProgressDialog(getActivity());
                String action = intent.getAction();
                this.mFingerprintManager = Utils.getFingerprintManagerOrNull(activity);
                Utils.getFaceManagerOrNull(activity);
                this.mDpm = (DevicePolicyManager) getSystemService("device_policy");
                this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(activity);
                this.mLockPatternUtils = new LockPatternUtils(activity);
                boolean z = true;
                this.mIsSetNewPassword = "android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD".equals(action) || "android.app.action.SET_NEW_PASSWORD".equals(action);
                this.mLockPatternUtils.sanitizePassword();
                boolean booleanExtra = intent.getBooleanExtra("confirm_credentials", true);
                if (activity instanceof InternalActivity) {
                    this.mPasswordConfirmed = !booleanExtra;
                    this.mUserPassword = intent.getParcelableExtra("password");
                }
                this.mHasChallenge = intent.getBooleanExtra("has_challenge", false);
                this.mChallenge = intent.getLongExtra("challenge", 0);
                this.mForFingerprint = intent.getBooleanExtra("for_fingerprint", false);
                this.mForFace = intent.getBooleanExtra("for_face", false);
                this.mRequestedMinComplexity = intent.getIntExtra("requested_min_complexity", 0);
                this.mCallerAppName = intent.getStringExtra("caller_app_name");
                this.mIsCallingAppAdmin = intent.getBooleanExtra("is_calling_app_admin", false);
                this.mForChangeCredRequiredForBoot = arguments != null && arguments.getBoolean("for_cred_req_boot");
                UserManager.get(activity);
                if (arguments != null) {
                    this.mUnificationProfileCredential = arguments.getParcelable("unification_profile_credential");
                    this.mUnificationProfileId = arguments.getInt("unification_profile_id", -10000);
                }
                if (bundle != null) {
                    this.mPasswordConfirmed = bundle.getBoolean("password_confirmed");
                    this.mWaitingForConfirmation = bundle.getBoolean("waiting_for_confirmation");
                    if (this.mUserPassword == null) {
                        this.mUserPassword = bundle.getParcelable("password");
                    }
                }
                this.mUserId = Utils.getSecureTargetUser(activity.getActivityToken(), UserManager.get(activity), arguments, intent.getExtras()).getIdentifier();
                this.mController = new ChooseLockGenericController(getContext(), this.mUserId, this.mRequestedMinComplexity, this.mLockPatternUtils);
                if ("android.app.action.SET_NEW_PASSWORD".equals(action) && UserManager.get(activity).isManagedProfile(this.mUserId) && this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mUserId)) {
                    activity.setTitle(C0017R$string.lock_settings_picker_title_profile);
                }
                this.mManagedPasswordProvider = ManagedLockPasswordProvider.get(activity, this.mUserId);
                if (this.mPasswordConfirmed) {
                    if (bundle == null) {
                        z = false;
                    }
                    updatePreferencesOrFinish(z);
                    if (this.mForChangeCredRequiredForBoot) {
                        maybeEnableEncryption(this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId), false);
                    }
                } else if (!this.mWaitingForConfirmation) {
                    ChooseLockSettingsHelper chooseLockSettingsHelper = new ChooseLockSettingsHelper(activity, this);
                    if (((UserManager.get(activity).isManagedProfile(this.mUserId) && !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mUserId)) && !this.mIsSetNewPassword) || !chooseLockSettingsHelper.launchConfirmationActivity(100, getString(C0017R$string.unlock_set_unlock_launch_picker_title), true, this.mUserId)) {
                        this.mPasswordConfirmed = true;
                        if (bundle == null) {
                            z = false;
                        }
                        updatePreferencesOrFinish(z);
                    } else {
                        this.mWaitingForConfirmation = true;
                    }
                }
                addHeaderView();
                return;
            }
            Log.i("ChooseLockGenericFragment", "Refusing to start because device is not provisioned");
            activity.finish();
        }

        /* access modifiers changed from: protected */
        public boolean canRunBeforeDeviceProvisioned() {
            PersistentDataBlockManager persistentDataBlockManager = (PersistentDataBlockManager) getSystemService("persistent_data_block");
            return persistentDataBlockManager == null || persistentDataBlockManager.getDataBlockSize() == 0;
        }

        /* access modifiers changed from: protected */
        public Class<? extends InternalActivity> getInternalActivityClass() {
            return InternalActivity.class;
        }

        /* access modifiers changed from: protected */
        public void addHeaderView() {
            if (this.mForFingerprint) {
                setHeaderView(C0012R$layout.choose_lock_generic_fingerprint_header);
                if (this.mIsSetNewPassword) {
                    ((TextView) getHeaderView().findViewById(C0010R$id.fingerprint_header_description)).setText(C0017R$string.fingerprint_unlock_title);
                }
            } else if (this.mForFace) {
                setHeaderView(C0012R$layout.choose_lock_generic_face_header);
                if (this.mIsSetNewPassword) {
                    ((TextView) getHeaderView().findViewById(C0010R$id.face_header_description)).setText(C0017R$string.face_unlock_title);
                }
            }
        }

        @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
        public boolean onPreferenceTreeClick(Preference preference) {
            writePreferenceClickMetric(preference);
            String key = preference.getKey();
            if (!isUnlockMethodSecure(key) && this.mLockPatternUtils.isSecure(this.mUserId)) {
                showFactoryResetProtectionWarningDialog(key);
                return true;
            } else if (!"unlock_skip_fingerprint".equals(key) && !"unlock_skip_face".equals(key)) {
                return setUnlockMethod(key);
            } else {
                Intent intent = new Intent(getActivity(), getInternalActivityClass());
                intent.setAction(getIntent().getAction());
                intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
                intent.putExtra("confirm_credentials", !this.mPasswordConfirmed);
                intent.putExtra("requested_min_complexity", this.mRequestedMinComplexity);
                intent.putExtra("caller_app_name", this.mCallerAppName);
                LockscreenCredential lockscreenCredential = this.mUserPassword;
                if (lockscreenCredential != null) {
                    intent.putExtra("password", (Parcelable) lockscreenCredential);
                }
                startActivityForResult(intent, 104);
                return true;
            }
        }

        private void maybeEnableEncryption(int i, boolean z) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService("device_policy");
            if (UserManager.get(getActivity()).isAdminUser() && this.mUserId == UserHandle.myUserId() && LockPatternUtils.isDeviceEncryptionEnabled() && !LockPatternUtils.isFileEncryptionEnabled() && !devicePolicyManager.getDoNotAskCredentialsOnBoot()) {
                Intent intentForUnlockMethod = getIntentForUnlockMethod(i);
                intentForUnlockMethod.putExtra("for_cred_req_boot", this.mForChangeCredRequiredForBoot);
                FragmentActivity activity = getActivity();
                Intent encryptionInterstitialIntent = getEncryptionInterstitialIntent(activity, i, this.mLockPatternUtils.isCredentialRequiredToDecrypt(!AccessibilityManager.getInstance(activity).isEnabled()), intentForUnlockMethod);
                encryptionInterstitialIntent.putExtra("for_fingerprint", this.mForFingerprint);
                encryptionInterstitialIntent.putExtra("for_face", this.mForFace);
                startActivityForResult(encryptionInterstitialIntent, (!this.mIsSetNewPassword || !this.mHasChallenge) ? 101 : 103);
            } else if (this.mForChangeCredRequiredForBoot) {
                finish();
            } else {
                updateUnlockMethodAndFinish(i, z, false);
            }
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            this.mWaitingForConfirmation = false;
            if (i == 100 && i2 == -1) {
                this.mPasswordConfirmed = true;
                this.mUserPassword = intent != null ? (LockscreenCredential) intent.getParcelableExtra("password") : null;
                updatePreferencesOrFinish(false);
                if (this.mForChangeCredRequiredForBoot) {
                    LockscreenCredential lockscreenCredential = this.mUserPassword;
                    if (lockscreenCredential == null || lockscreenCredential.isNone()) {
                        finish();
                    } else {
                        maybeEnableEncryption(this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId), false);
                    }
                }
            } else if (i == 102 || i == 101) {
                if (i2 != 0 || this.mForChangeCredRequiredForBoot) {
                    getActivity().setResult(i2, intent);
                    finish();
                } else if (getIntent().getIntExtra("lockscreen.password_type", -1) != -1) {
                    getActivity().setResult(0, intent);
                    finish();
                }
            } else if (i == 103 && i2 == 1) {
                if (OPUtils.isSupportCustomFingerprint()) {
                    launchFingerprintEnroll(intent.getByteArrayExtra("hw_auth_token"));
                } else {
                    Intent biometricEnrollIntent = getBiometricEnrollIntent(getActivity());
                    if (intent != null) {
                        biometricEnrollIntent.putExtras(intent.getExtras());
                    }
                    biometricEnrollIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
                    startActivity(biometricEnrollIntent);
                    finish();
                }
            } else if (i == 104) {
                if (i2 != 0) {
                    FragmentActivity activity = getActivity();
                    if (i2 == 1) {
                        i2 = -1;
                    }
                    activity.setResult(i2, intent);
                    finish();
                }
            } else if (i != 501) {
                getActivity().setResult(0);
                finish();
            } else {
                return;
            }
            if (i == 0 && this.mForChangeCredRequiredForBoot) {
                finish();
            }
        }

        /* access modifiers changed from: protected */
        public void launchFingerprintEnroll(byte[] bArr) {
            Intent intent = new Intent();
            intent.setClassName(OPMemberController.PACKAGE_NAME, FingerprintEnrollEnrolling.class.getName());
            intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
            intent.putExtra("hw_auth_token", bArr);
            startActivity(intent);
            getActivity().overridePendingTransition(C0002R$anim.op_activity_fingeprint_open_enter, C0002R$anim.op_activity_fingeprint_close_exit);
            finish();
        }

        /* access modifiers changed from: protected */
        public Intent getBiometricEnrollIntent(Context context) {
            Intent intent = new Intent(context, BiometricEnrollActivity.InternalActivity.class);
            intent.putExtra("skip_intro", true);
            return intent;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putBoolean("password_confirmed", this.mPasswordConfirmed);
            bundle.putBoolean("waiting_for_confirmation", this.mWaitingForConfirmation);
            LockscreenCredential lockscreenCredential = this.mUserPassword;
            if (lockscreenCredential != null) {
                bundle.putParcelable("password", lockscreenCredential);
            }
        }

        /* access modifiers changed from: package-private */
        public void updatePreferencesOrFinish(boolean z) {
            int i;
            Intent intent = getActivity().getIntent();
            if (StorageManager.isFileEncryptedNativeOrEmulated()) {
                i = intent.getIntExtra("lockscreen.password_type", -1);
            } else {
                Log.i("ChooseLockGenericFragment", "Ignoring PASSWORD_TYPE_KEY because device is not file encrypted");
                i = -1;
            }
            if (i == -1) {
                int upgradeQuality = this.mController.upgradeQuality(intent.getIntExtra("minimum_quality", -1));
                boolean booleanExtra = intent.getBooleanExtra("hide_disabled_prefs", false);
                Log.d("ChooseLockGenericFragment", "zhuyang-updatePreferencesOrFinish-hideDisabledPrefs:" + booleanExtra);
                PreferenceScreen preferenceScreen = getPreferenceScreen();
                if (preferenceScreen != null) {
                    preferenceScreen.removeAll();
                }
                addPreferences();
                disableUnusablePreferences(upgradeQuality, booleanExtra);
                updatePreferenceText();
                updateCurrentPreference();
                updatePreferenceSummaryIfNeeded();
            } else if (!z) {
                updateUnlockMethodAndFinish(i, false, true);
            }
        }

        /* access modifiers changed from: protected */
        public void addPreferences() {
            addPreferencesFromResource(C0019R$xml.security_settings_picker);
            Preference findPreference = findPreference("lock_settings_footer");
            if (TextUtils.isEmpty(this.mCallerAppName) || this.mIsCallingAppAdmin) {
                findPreference.setVisible(false);
            } else {
                findPreference.setVisible(true);
                findPreference.setTitle(getFooterString());
            }
            findPreference(ScreenLockType.NONE.preferenceKey).setViewId(C0010R$id.lock_none);
            findPreference("unlock_skip_fingerprint").setViewId(C0010R$id.lock_none);
            findPreference("unlock_skip_face").setViewId(C0010R$id.lock_none);
            findPreference(ScreenLockType.PIN.preferenceKey).setViewId(C0010R$id.lock_pin);
            findPreference(ScreenLockType.PASSWORD.preferenceKey).setViewId(C0010R$id.lock_password);
        }

        private String getFooterString() {
            int i;
            int i2 = this.mRequestedMinComplexity;
            if (i2 == 65536) {
                i = C0017R$string.unlock_footer_low_complexity_requested;
            } else if (i2 == 196608) {
                i = C0017R$string.unlock_footer_medium_complexity_requested;
            } else if (i2 != 327680) {
                i = C0017R$string.unlock_footer_none_complexity_requested;
            } else {
                i = C0017R$string.unlock_footer_high_complexity_requested;
            }
            return getResources().getString(i, this.mCallerAppName);
        }

        private void updatePreferenceText() {
            if (this.mForFingerprint) {
                setPreferenceTitle(ScreenLockType.PATTERN, C0017R$string.fingerprint_unlock_set_unlock_pattern);
                setPreferenceTitle(ScreenLockType.PIN, C0017R$string.fingerprint_unlock_set_unlock_pin);
                setPreferenceTitle(ScreenLockType.PASSWORD, C0017R$string.fingerprint_unlock_set_unlock_password);
            } else if (this.mForFace) {
                setPreferenceTitle(ScreenLockType.PATTERN, C0017R$string.op_face_unlock_set_unlock_pattern);
                setPreferenceTitle(ScreenLockType.PIN, C0017R$string.op_face_unlock_set_unlock_pin);
                setPreferenceTitle(ScreenLockType.PASSWORD, C0017R$string.op_face_unlock_set_unlock_password);
            }
            if (this.mManagedPasswordProvider.isSettingManagedPasswordSupported()) {
                setPreferenceTitle(ScreenLockType.MANAGED, this.mManagedPasswordProvider.getPickerOptionTitle(this.mForFingerprint));
            } else {
                removePreference(ScreenLockType.MANAGED.preferenceKey);
            }
            if (!this.mForFingerprint || !this.mIsSetNewPassword) {
                removePreference("unlock_skip_fingerprint");
            }
            if (!this.mForFace || !this.mIsSetNewPassword) {
                removePreference("unlock_skip_face");
            }
        }

        private void setPreferenceTitle(ScreenLockType screenLockType, int i) {
            Preference findPreference = findPreference(screenLockType.preferenceKey);
            if (findPreference != null) {
                findPreference.setTitle(i);
            }
        }

        private void setPreferenceTitle(ScreenLockType screenLockType, CharSequence charSequence) {
            Preference findPreference = findPreference(screenLockType.preferenceKey);
            if (findPreference != null) {
                findPreference.setTitle(charSequence);
            }
        }

        private void setPreferenceSummary(ScreenLockType screenLockType, int i) {
            Preference findPreference = findPreference(screenLockType.preferenceKey);
            if (findPreference != null) {
                findPreference.setSummary(i);
            }
        }

        private void updateCurrentPreference() {
            Preference findPreference = findPreference(getKeyForCurrent());
            if (findPreference != null) {
                findPreference.setSummary(C0017R$string.current_screen_lock);
            }
        }

        private String getKeyForCurrent() {
            int credentialOwnerProfile = UserManager.get(getContext()).getCredentialOwnerProfile(this.mUserId);
            if (this.mLockPatternUtils.isLockScreenDisabled(credentialOwnerProfile)) {
                return ScreenLockType.NONE.preferenceKey;
            }
            ScreenLockType fromQuality = ScreenLockType.fromQuality(this.mLockPatternUtils.getKeyguardStoredPasswordQuality(credentialOwnerProfile));
            if (fromQuality != null) {
                return fromQuality.preferenceKey;
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void disableUnusablePreferences(int i, boolean z) {
            disableUnusablePreferencesImpl(i, z);
        }

        /* access modifiers changed from: protected */
        public void disableUnusablePreferencesImpl(int i, boolean z) {
            int passwordQuality;
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            int passwordQuality2 = this.mDpm.getPasswordQuality(null, this.mUserId);
            RestrictedLockUtils.EnforcedAdmin checkIfPasswordQualityIsSet = RestrictedLockUtilsInternal.checkIfPasswordQualityIsSet(getActivity(), this.mUserId);
            int i2 = this.mUnificationProfileId;
            if (i2 != -10000 && (passwordQuality = this.mDpm.getPasswordQuality(null, i2)) > passwordQuality2) {
                checkIfPasswordQualityIsSet = RestrictedLockUtils.EnforcedAdmin.combine(checkIfPasswordQualityIsSet, RestrictedLockUtilsInternal.checkIfPasswordQualityIsSet(getActivity(), this.mUnificationProfileId));
                passwordQuality2 = passwordQuality;
            }
            ScreenLockType[] values = ScreenLockType.values();
            for (ScreenLockType screenLockType : values) {
                Preference findPreference = findPreference(screenLockType.preferenceKey);
                if (findPreference instanceof Preference) {
                    boolean isScreenLockVisible = this.mController.isScreenLockVisible(screenLockType);
                    boolean isScreenLockEnabled = this.mController.isScreenLockEnabled(screenLockType, i);
                    boolean isScreenLockDisabledByAdmin = this.mController.isScreenLockDisabledByAdmin(screenLockType, passwordQuality2);
                    boolean z2 = true;
                    if (z) {
                        isScreenLockVisible = isScreenLockVisible && isScreenLockEnabled;
                    }
                    if (!isScreenLockVisible) {
                        preferenceScreen.removePreference(findPreference);
                    } else if (isScreenLockDisabledByAdmin && checkIfPasswordQualityIsSet != null) {
                        if (checkIfPasswordQualityIsSet != null) {
                            z2 = false;
                        }
                        findPreference.setEnabled(z2);
                    } else if (!isScreenLockEnabled) {
                        findPreference.setSummary(C0017R$string.unlock_set_unlock_disabled_summary);
                        findPreference.setEnabled(false);
                    } else {
                        findPreference.setEnabled(true);
                    }
                }
            }
        }

        private void updatePreferenceSummaryIfNeeded() {
            if (StorageManager.isBlockEncrypted() && !StorageManager.isNonDefaultBlockEncrypted() && !AccessibilityManager.getInstance(getActivity()).getEnabledAccessibilityServiceList(-1).isEmpty()) {
                setPreferenceSummary(ScreenLockType.PATTERN, C0017R$string.secure_lock_encryption_warning);
                setPreferenceSummary(ScreenLockType.PIN, C0017R$string.secure_lock_encryption_warning);
                setPreferenceSummary(ScreenLockType.PASSWORD, C0017R$string.secure_lock_encryption_warning);
                setPreferenceSummary(ScreenLockType.MANAGED, C0017R$string.secure_lock_encryption_warning);
            }
        }

        /* access modifiers changed from: protected */
        public Intent getLockManagedPasswordIntent(LockscreenCredential lockscreenCredential) {
            return this.mManagedPasswordProvider.createIntent(false, lockscreenCredential);
        }

        /* access modifiers changed from: protected */
        public Intent getLockPasswordIntent(int i) {
            ChooseLockPassword.IntentBuilder intentBuilder = new ChooseLockPassword.IntentBuilder(getContext());
            intentBuilder.setPasswordQuality(i);
            intentBuilder.setRequestedMinComplexity(this.mRequestedMinComplexity);
            intentBuilder.setForFingerprint(this.mForFingerprint);
            intentBuilder.setForFace(this.mForFace);
            intentBuilder.setUserId(this.mUserId);
            if (this.mHasChallenge) {
                intentBuilder.setChallenge(this.mChallenge);
            }
            LockscreenCredential lockscreenCredential = this.mUserPassword;
            if (lockscreenCredential != null) {
                intentBuilder.setPassword(lockscreenCredential);
            }
            int i2 = this.mUnificationProfileId;
            if (i2 != -10000) {
                intentBuilder.setProfileToUnify(i2, this.mUnificationProfileCredential);
            }
            return intentBuilder.build();
        }

        /* access modifiers changed from: protected */
        public Intent getLockPatternIntent() {
            ChooseLockPattern.IntentBuilder intentBuilder = new ChooseLockPattern.IntentBuilder(getContext());
            intentBuilder.setForFingerprint(this.mForFingerprint);
            intentBuilder.setForFace(this.mForFace);
            intentBuilder.setUserId(this.mUserId);
            if (this.mHasChallenge) {
                intentBuilder.setChallenge(this.mChallenge);
            }
            LockscreenCredential lockscreenCredential = this.mUserPassword;
            if (lockscreenCredential != null) {
                intentBuilder.setPattern(lockscreenCredential);
            }
            int i = this.mUnificationProfileId;
            if (i != -10000) {
                intentBuilder.setProfileToUnify(i, this.mUnificationProfileCredential);
            }
            return intentBuilder.build();
        }

        /* access modifiers changed from: protected */
        public Intent getEncryptionInterstitialIntent(Context context, int i, boolean z, Intent intent) {
            return EncryptionInterstitial.createStartIntent(context, i, z, intent);
        }

        /* access modifiers changed from: package-private */
        public void updateUnlockMethodAndFinish(int i, boolean z, boolean z2) {
            if (this.mPasswordConfirmed) {
                int upgradeQuality = this.mController.upgradeQuality(i);
                Intent intentForUnlockMethod = getIntentForUnlockMethod(upgradeQuality);
                if (intentForUnlockMethod != null) {
                    if (getIntent().getBooleanExtra("show_options_button", false)) {
                        intentForUnlockMethod.putExtra("show_options_button", z2);
                    }
                    intentForUnlockMethod.putExtra("choose_lock_generic_extras", getIntent().getExtras());
                    startActivityForResult(intentForUnlockMethod, (!this.mIsSetNewPassword || !this.mHasChallenge) ? 102 : 103);
                } else if (upgradeQuality == 0) {
                    if (this.mUserPassword != null) {
                        this.mChooseLockSettingsHelper.utils().setLockCredential(LockscreenCredential.createNone(), this.mUserPassword, this.mUserId);
                    }
                    this.mChooseLockSettingsHelper.utils().setLockScreenDisabled(z, this.mUserId);
                    Log.d("ChooseLockGenericFragment", "zhuyang--updateUnlockMethodAndFinish--disabled:" + z);
                    showResetPasswordDefaultDialog();
                    OPUtils.savePINPasswordLength(this.mLockPatternUtils, 0, this.mUserId);
                    removeFaceData();
                    OPGestureUtils.set0(SettingsBaseApplication.mApplication, 15);
                }
            } else {
                throw new IllegalStateException("Tried to update password without confirming it");
            }
        }

        private Intent getIntentForUnlockMethod(int i) {
            if (i >= 524288) {
                return getLockManagedPasswordIntent(this.mUserPassword);
            }
            if (i >= 131072) {
                return getLockPasswordIntent(i);
            }
            if (i == 65536) {
                return getLockPatternIntent();
            }
            return null;
        }

        private void showResetPasswordDefaultDialog() {
            ProgressDialog progressDialog;
            OPUtils.savePINPasswordLength(this.mLockPatternUtils, 0, this.mUserId);
            final FragmentActivity activity = getActivity();
            if (activity != null && !activity.isFinishing() && (progressDialog = this.mCryptfsChangepwDefaultProgressDialog) != null) {
                progressDialog.setCancelable(false);
                this.mCryptfsChangepwDefaultProgressDialog.setMessage(getResources().getString(C0017R$string.oneplus_switch_screen_lock_type));
                if (activity != null && !activity.isDestroyed()) {
                    this.mCryptfsChangepwDefaultProgressDialog.show();
                }
                this.mHandler.postDelayed(new Runnable() {
                    /* class com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment.AnonymousClass3 */

                    public void run() {
                        Activity activity = activity;
                        if (activity != null && !activity.isDestroyed()) {
                            if (ChooseLockGenericFragment.this.mCryptfsChangepwDefaultProgressDialog != null) {
                                ChooseLockGenericFragment.this.mCryptfsChangepwDefaultProgressDialog.dismiss();
                            }
                            activity.setResult(-1);
                            activity.finish();
                        }
                    }
                }, 2500);
            }
        }

        @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
        public void onDestroy() {
            super.onDestroy();
            this.mLockPatternUtils.sanitizePassword();
            LockscreenCredential lockscreenCredential = this.mUserPassword;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            System.gc();
            System.runFinalization();
            System.gc();
        }

        @Override // com.android.settings.support.actionbar.HelpResourceProvider
        public int getHelpResource() {
            return C0017R$string.help_url_choose_lockscreen;
        }

        private int getResIdForFactoryResetProtectionWarningTitle() {
            if (UserManager.get(getActivity()).isManagedProfile(this.mUserId)) {
                return C0017R$string.unlock_disable_frp_warning_title_profile;
            }
            return C0017R$string.unlock_disable_frp_warning_title;
        }

        private int getResIdForFactoryResetProtectionWarningMessage() {
            FingerprintManager fingerprintManager = this.mFingerprintManager;
            boolean hasEnrolledFingerprints = (fingerprintManager == null || !fingerprintManager.isHardwareDetected()) ? false : this.mFingerprintManager.hasEnrolledFingerprints(this.mUserId);
            boolean isManagedProfile = UserManager.get(getActivity()).isManagedProfile(this.mUserId);
            int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId);
            if (keyguardStoredPasswordQuality != 65536) {
                if (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) {
                    if (hasEnrolledFingerprints && isManagedProfile) {
                        return C0017R$string.unlock_disable_frp_warning_content_pin_fingerprint_profile;
                    }
                    if (hasEnrolledFingerprints && !isManagedProfile) {
                        return C0017R$string.unlock_disable_frp_warning_content_pin_fingerprint;
                    }
                    if (isManagedProfile) {
                        return C0017R$string.unlock_disable_frp_warning_content_pin_profile;
                    }
                    return C0017R$string.unlock_disable_frp_warning_content_pin;
                } else if (keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) {
                    if (hasEnrolledFingerprints && isManagedProfile) {
                        return C0017R$string.unlock_disable_frp_warning_content_password_fingerprint_profile;
                    }
                    if (hasEnrolledFingerprints && !isManagedProfile) {
                        return C0017R$string.unlock_disable_frp_warning_content_password_fingerprint;
                    }
                    if (isManagedProfile) {
                        return C0017R$string.unlock_disable_frp_warning_content_password_profile;
                    }
                    return C0017R$string.unlock_disable_frp_warning_content_password;
                } else if (hasEnrolledFingerprints && isManagedProfile) {
                    return C0017R$string.unlock_disable_frp_warning_content_unknown_fingerprint_profile;
                } else {
                    if (hasEnrolledFingerprints && !isManagedProfile) {
                        return C0017R$string.unlock_disable_frp_warning_content_unknown_fingerprint;
                    }
                    if (isManagedProfile) {
                        return C0017R$string.unlock_disable_frp_warning_content_unknown_profile;
                    }
                    return C0017R$string.unlock_disable_frp_warning_content_unknown;
                }
            } else if (hasEnrolledFingerprints && isManagedProfile) {
                return C0017R$string.unlock_disable_frp_warning_content_pattern_fingerprint_profile;
            } else {
                if (hasEnrolledFingerprints && !isManagedProfile) {
                    return C0017R$string.unlock_disable_frp_warning_content_pattern_fingerprint;
                }
                if (isManagedProfile) {
                    return C0017R$string.unlock_disable_frp_warning_content_pattern_profile;
                }
                return C0017R$string.unlock_disable_frp_warning_content_pattern;
            }
        }

        private boolean isUnlockMethodSecure(String str) {
            return !ScreenLockType.SWIPE.preferenceKey.equals(str) && !ScreenLockType.NONE.preferenceKey.equals(str);
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private boolean setUnlockMethod(String str) {
            EventLog.writeEvent(90200, str);
            ScreenLockType fromKey = ScreenLockType.fromKey(str);
            if (fromKey != null) {
                switch (AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType[fromKey.ordinal()]) {
                    case 1:
                    case 2:
                        updateUnlockMethodAndFinish(fromKey.defaultQuality, fromKey == ScreenLockType.NONE, false);
                        return true;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        maybeEnableEncryption(fromKey.defaultQuality, false);
                        return true;
                }
            }
            Log.e("ChooseLockGenericFragment", "Encountered unknown unlock method to set: " + str);
            return false;
        }

        private void showFactoryResetProtectionWarningDialog(String str) {
            FactoryResetProtectionWarningDialog.newInstance(getResIdForFactoryResetProtectionWarningTitle(), getResIdForFactoryResetProtectionWarningMessage(), str).show(getChildFragmentManager(), "frp_warning_dialog");
        }

        public static class FactoryResetProtectionWarningDialog extends InstrumentedDialogFragment {
            @Override // com.android.settingslib.core.instrumentation.Instrumentable
            public int getMetricsCategory() {
                return 528;
            }

            public static FactoryResetProtectionWarningDialog newInstance(int i, int i2, String str) {
                FactoryResetProtectionWarningDialog factoryResetProtectionWarningDialog = new FactoryResetProtectionWarningDialog();
                Bundle bundle = new Bundle();
                bundle.putInt("titleRes", i);
                bundle.putInt("messageRes", i2);
                bundle.putString("unlockMethodToSet", str);
                factoryResetProtectionWarningDialog.setArguments(bundle);
                return factoryResetProtectionWarningDialog;
            }

            @Override // androidx.fragment.app.DialogFragment
            public void show(FragmentManager fragmentManager, String str) {
                if (fragmentManager.findFragmentByTag(str) == null) {
                    super.show(fragmentManager, str);
                }
            }

            @Override // androidx.fragment.app.DialogFragment
            public Dialog onCreateDialog(Bundle bundle) {
                Bundle arguments = getArguments();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(arguments.getInt("titleRes"));
                builder.setMessage(arguments.getInt("messageRes"));
                builder.setPositiveButton(C0017R$string.unlock_disable_frp_warning_ok, new DialogInterface.OnClickListener(arguments) {
                    /* class com.android.settings.password.$$Lambda$ChooseLockGeneric$ChooseLockGenericFragment$FactoryResetProtectionWarningDialog$Abdbf1FnDmiVy0c3RZHU7n2B2k */
                    public final /* synthetic */ Bundle f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChooseLockGeneric.ChooseLockGenericFragment.FactoryResetProtectionWarningDialog.this.lambda$onCreateDialog$0$ChooseLockGeneric$ChooseLockGenericFragment$FactoryResetProtectionWarningDialog(this.f$1, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener() {
                    /* class com.android.settings.password.$$Lambda$ChooseLockGeneric$ChooseLockGenericFragment$FactoryResetProtectionWarningDialog$YUiXVX_8NlQHl0UI000UMbpVL0U */

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChooseLockGeneric.ChooseLockGenericFragment.FactoryResetProtectionWarningDialog.this.lambda$onCreateDialog$1$ChooseLockGeneric$ChooseLockGenericFragment$FactoryResetProtectionWarningDialog(dialogInterface, i);
                    }
                });
                return builder.create();
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onCreateDialog$0 */
            public /* synthetic */ void lambda$onCreateDialog$0$ChooseLockGeneric$ChooseLockGenericFragment$FactoryResetProtectionWarningDialog(Bundle bundle, DialogInterface dialogInterface, int i) {
                ((ChooseLockGenericFragment) getParentFragment()).setUnlockMethod(bundle.getString("unlockMethodToSet"));
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onCreateDialog$1 */
            public /* synthetic */ void lambda$onCreateDialog$1$ChooseLockGeneric$ChooseLockGenericFragment$FactoryResetProtectionWarningDialog(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.android.settings.password.ChooseLockGeneric$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$password$ScreenLockType;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|14) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.settings.password.ScreenLockType[] r0 = com.android.settings.password.ScreenLockType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.android.settings.password.ChooseLockGeneric.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType = r0
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.NONE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.android.settings.password.ChooseLockGeneric.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.SWIPE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.android.settings.password.ChooseLockGeneric.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.PATTERN     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = com.android.settings.password.ChooseLockGeneric.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.PIN     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = com.android.settings.password.ChooseLockGeneric.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.PASSWORD     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = com.android.settings.password.ChooseLockGeneric.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.MANAGED     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.password.ChooseLockGeneric.AnonymousClass1.<clinit>():void");
        }
    }
}
