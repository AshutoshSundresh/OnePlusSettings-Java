package com.android.settings.biometrics.fingerprint;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImeAwareEditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.R$styleable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0002R$anim;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settings.biometrics.fingerprint.FingerprintRemoveSidecar;
import com.android.settings.biometrics.fingerprint.FingerprintSettings;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.utils.AnnotationSpan;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.TwoTargetPreference;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.gestures.OPGestureUtils;
import com.oneplus.settings.ui.OPPreferenceHeaderMargin;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.widget.OPBorderlessButtonPreference;
import com.oneplus.settings.widget.OPFooterPreference;
import java.util.HashMap;
import java.util.List;

public class FingerprintSettings extends SubSettings {
    @Override // com.android.settings.SettingsActivity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", FingerprintSettingsFragment.class.getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SubSettings, com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return FingerprintSettingsFragment.class.getName().equals(str);
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.android.settings.core.SettingsBaseActivity, com.android.settings.SettingsActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(getText(C0017R$string.security_settings_fingerprint_preference_title));
    }

    public static class FingerprintSettingsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, FingerprintPreference.OnDeleteClickListener {
        private FingerprintAuthenticateSidecar mAuthenticateSidecar;
        private final Runnable mFingerprintLockoutReset = new Runnable() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.AnonymousClass5 */

            public void run() {
                FingerprintSettingsFragment.this.mInFingerprintLockout = false;
                FingerprintSettingsFragment.this.retryFingerprint();
            }
        };
        private FingerprintManager mFingerprintManager;
        private HashMap<Integer, String> mFingerprintsRenaming;
        private final Handler mHandler = new Handler() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.AnonymousClass3 */

            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1000) {
                    if (FingerprintSettingsFragment.this.mFingerprintManager.getEnrolledFingerprints(FingerprintSettingsFragment.this.mUserId).size() == 0) {
                        OPGestureUtils.set0(SettingsBaseApplication.mApplication, 15);
                    }
                    FingerprintSettingsFragment.this.removeFingerprintPreference(message.arg1);
                    FingerprintSettingsFragment.this.updateAddPreference();
                    FingerprintSettingsFragment.this.retryFingerprint();
                } else if (i == 1001) {
                    FingerprintSettingsFragment.this.highlightFingerprintItem(message.arg1);
                    FingerprintSettingsFragment.this.retryFingerprint();
                } else if (i == 1003) {
                    FingerprintSettingsFragment.this.handleError(message.arg1, (CharSequence) message.obj);
                }
            }
        };
        private Drawable mHighlightDrawable;
        private boolean mInFingerprintLockout;
        private boolean mLaunchedConfirm;
        FingerprintRemoveSidecar.Listener mRemovalListener = new FingerprintRemoveSidecar.Listener() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.AnonymousClass2 */

            @Override // com.android.settings.biometrics.fingerprint.FingerprintRemoveSidecar.Listener
            public void onRemovalSucceeded(Fingerprint fingerprint) {
                FingerprintSettingsFragment.this.mHandler.obtainMessage(1000, fingerprint.getBiometricId(), 0).sendToTarget();
                updateDialog();
            }

            @Override // com.android.settings.biometrics.fingerprint.FingerprintRemoveSidecar.Listener
            public void onRemovalError(Fingerprint fingerprint, int i, CharSequence charSequence) {
                FragmentActivity activity = FingerprintSettingsFragment.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, charSequence, 0);
                }
                updateDialog();
            }

            private void updateDialog() {
                RenameDialog renameDialog = (RenameDialog) FingerprintSettingsFragment.this.getFragmentManager().findFragmentByTag(RenameDialog.class.getName());
                if (renameDialog != null) {
                    renameDialog.enableDelete();
                }
            }
        };
        private FingerprintRemoveSidecar mRemovalSidecar;
        private byte[] mToken;
        private int mUserId;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 49;
        }

        /* access modifiers changed from: protected */
        public void handleError(int i, CharSequence charSequence) {
            FragmentActivity activity;
            if (i != 5) {
                if (i == 7) {
                    this.mInFingerprintLockout = true;
                    if (!this.mHandler.hasCallbacks(this.mFingerprintLockoutReset)) {
                        this.mHandler.postDelayed(this.mFingerprintLockoutReset, 30000);
                    }
                } else if (i == 9) {
                    this.mInFingerprintLockout = true;
                }
                if (this.mInFingerprintLockout && (activity = getActivity()) != null) {
                    Toast.makeText(activity, charSequence, 0).show();
                }
                retryFingerprint();
            }
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void retryFingerprint() {
            if (this.mRemovalSidecar.inProgress() || this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() == 0 || this.mLaunchedConfirm) {
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
        public void onCreate(Bundle bundle) {
            CharSequence charSequence;
            super.onCreate(bundle);
            FragmentActivity activity = getActivity();
            activity.setRequestedOrientation(1);
            ActivityManager.getCurrentUser();
            this.mFingerprintManager = Utils.getFingerprintManagerOrNull(activity);
            this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
            FingerprintAuthenticateSidecar fingerprintAuthenticateSidecar = (FingerprintAuthenticateSidecar) getFragmentManager().findFragmentByTag("authenticate_sidecar");
            this.mAuthenticateSidecar = fingerprintAuthenticateSidecar;
            if (fingerprintAuthenticateSidecar == null) {
                this.mAuthenticateSidecar = new FingerprintAuthenticateSidecar();
                FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
                beginTransaction.add(this.mAuthenticateSidecar, "authenticate_sidecar");
                beginTransaction.commit();
            }
            this.mAuthenticateSidecar.setFingerprintManager(this.mFingerprintManager);
            FingerprintRemoveSidecar fingerprintRemoveSidecar = (FingerprintRemoveSidecar) getFragmentManager().findFragmentByTag("removal_sidecar");
            this.mRemovalSidecar = fingerprintRemoveSidecar;
            if (fingerprintRemoveSidecar == null) {
                this.mRemovalSidecar = new FingerprintRemoveSidecar();
                FragmentTransaction beginTransaction2 = getFragmentManager().beginTransaction();
                beginTransaction2.add(this.mRemovalSidecar, "removal_sidecar");
                beginTransaction2.commit();
            }
            this.mRemovalSidecar.setFingerprintManager(this.mFingerprintManager);
            this.mRemovalSidecar.setListener(this.mRemovalListener);
            RenameDialog renameDialog = (RenameDialog) getFragmentManager().findFragmentByTag(RenameDialog.class.getName());
            if (renameDialog != null) {
                renameDialog.setDeleteInProgress(this.mRemovalSidecar.inProgress());
            }
            this.mFingerprintsRenaming = new HashMap<>();
            this.mToken = getActivity().getIntent().getByteArrayExtra("hw_auth_token");
            if (bundle != null) {
                this.mFingerprintsRenaming = (HashMap) bundle.getSerializable("mFingerprintsRenaming");
                this.mToken = bundle.getByteArray("hw_auth_token");
                this.mLaunchedConfirm = bundle.getBoolean("launched_confirm", false);
            }
            this.mUserId = getActivity().getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
            if (this.mToken == null && !this.mLaunchedConfirm) {
                this.mLaunchedConfirm = true;
                launchChooseOrConfirmLock();
            }
            OPFooterPreference createFooterPreference = this.mFooterPreferenceMixin.createFooterPreference();
            RestrictedLockUtils.EnforcedAdmin checkIfKeyguardFeaturesDisabled = RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(activity, 32, this.mUserId);
            AnnotationSpan.LinkInfo linkInfo = new AnnotationSpan.LinkInfo("admin_details", new View.OnClickListener(activity, checkIfKeyguardFeaturesDisabled) {
                /* class com.android.settings.biometrics.fingerprint.$$Lambda$FingerprintSettings$FingerprintSettingsFragment$YFiZ1sxA950vGXlWJxWC0lIgQ0 */
                public final /* synthetic */ Activity f$0;
                public final /* synthetic */ RestrictedLockUtils.EnforcedAdmin f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    FingerprintSettings.FingerprintSettingsFragment.lambda$onCreate$0(this.f$0, this.f$1, view);
                }
            });
            AnnotationSpan.LinkInfo linkInfo2 = new AnnotationSpan.LinkInfo(activity, "url", HelpUtils.getHelpIntent(activity, getString(getHelpResource()), activity.getClass().getName()));
            getResources().getString(C0017R$string.security_settings_fingerprint_enroll_disclaimer);
            String string = getResources().getString(C0017R$string.op_fingerprint_enroll_note_msg);
            if (checkIfKeyguardFeaturesDisabled != null) {
                charSequence = getResources().getText(C0017R$string.security_settings_fingerprint_enroll_disclaimer_lockscreen_disabled);
            } else {
                charSequence = getResources().getText(C0017R$string.security_settings_fingerprint_enroll_disclaimer);
            }
            StringBuilder sb = new StringBuilder();
            sb.append((Object) AnnotationSpan.linkifyRemoveFingerprintUrl(charSequence, linkInfo2, linkInfo));
            sb.append("\n");
            if (checkIfKeyguardFeaturesDisabled != null) {
                string = "";
            }
            sb.append(string);
            createFooterPreference.setTitle(sb.toString());
        }

        /* access modifiers changed from: protected */
        public void removeFingerprintPreference(int i) {
            String genKey = genKey(i);
            Preference findPreference = findPreference(genKey);
            if (findPreference == null) {
                Log.w("FingerprintSettings", "Can't find preference to remove: " + genKey);
            } else if (!getPreferenceScreen().removePreference(findPreference)) {
                Log.w("FingerprintSettings", "Failed to remove preference with key " + genKey);
            }
        }

        private PreferenceScreen createPreferenceHierarchy() {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            if (preferenceScreen != null) {
                preferenceScreen.removeAll();
            }
            addPreferencesFromResource(C0019R$xml.security_settings_fingerprint);
            PreferenceScreen preferenceScreen2 = getPreferenceScreen();
            addFingerprintItemPreferences(preferenceScreen2);
            setPreferenceScreen(preferenceScreen2);
            return preferenceScreen2;
        }

        private void addFingerprintItemPreferences(PreferenceGroup preferenceGroup) {
            preferenceGroup.removeAll();
            preferenceGroup.addPreference(new OPPreferenceHeaderMargin(preferenceGroup.getContext()));
            List enrolledFingerprints = this.mFingerprintManager.getEnrolledFingerprints(this.mUserId);
            int size = enrolledFingerprints.size();
            for (int i = 0; i < size; i++) {
                Fingerprint fingerprint = (Fingerprint) enrolledFingerprints.get(i);
                FingerprintPreference fingerprintPreference = new FingerprintPreference(preferenceGroup.getContext(), this);
                fingerprintPreference.setKey(genKey(fingerprint.getBiometricId()));
                fingerprintPreference.setTitle(fingerprint.getName());
                fingerprintPreference.setFingerprint(fingerprint);
                fingerprintPreference.setPersistent(false);
                if (this.mRemovalSidecar.isRemovingFingerprint(fingerprint.getBiometricId())) {
                    fingerprintPreference.setEnabled(false);
                }
                if (this.mFingerprintsRenaming.containsKey(Integer.valueOf(fingerprint.getBiometricId()))) {
                    fingerprintPreference.setTitle(this.mFingerprintsRenaming.get(Integer.valueOf(fingerprint.getBiometricId())));
                }
                preferenceGroup.addPreference(fingerprintPreference);
                fingerprintPreference.setOnPreferenceChangeListener(this);
            }
            Preference oPBorderlessButtonPreference = new OPBorderlessButtonPreference(preferenceGroup.getContext());
            oPBorderlessButtonPreference.setKey("key_fingerprint_add");
            oPBorderlessButtonPreference.setTitle(C0017R$string.fingerprint_add_title);
            oPBorderlessButtonPreference.setIcon(C0008R$drawable.ic_menu_add);
            preferenceGroup.addPreference(oPBorderlessButtonPreference);
            oPBorderlessButtonPreference.setOnPreferenceChangeListener(this);
            updateAddPreference();
            if (OPUtils.isSupportCustomFingerprint()) {
                addCustomAnimationPickPage(preferenceGroup);
            }
        }

        private void addCustomAnimationPickPage(PreferenceGroup preferenceGroup) {
            Preference preference = new Preference(preferenceGroup.getContext());
            preference.setKey("key_custom_animation");
            preference.setTitle(C0017R$string.oneplus_fingerprint_animation_effect_title);
            preference.setSummary(getCustomAnimationName());
            preference.setOnPreferenceChangeListener(this);
            preferenceGroup.addPreference(preference);
        }

        private int getCustomAnimationName() {
            int intForUser = Settings.System.getIntForUser(getContext().getContentResolver(), "op_custom_unlock_animation_style", 0, -2);
            if (intForUser == 0) {
                return C0017R$string.oneplus_select_fingerprint_animation_effect_1;
            }
            if (intForUser == 1) {
                return C0017R$string.oneplus_select_fingerprint_animation_effect_4;
            }
            if (intForUser == 2) {
                return C0017R$string.oneplus_select_fingerprint_animation_effect_3;
            }
            if (intForUser == 3) {
                return C0017R$string.op_theme_3_title;
            }
            if (intForUser == 4) {
                return C0017R$string.oneplus_select_fingerprint_animation_effect_6;
            }
            if (intForUser != 9) {
                return C0017R$string.oneplus_select_fingerprint_animation_effect_1;
            }
            return C0017R$string.oneplus_select_fingerprint_animation_effect_none;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void updateAddPreference() {
            if (getActivity() != null) {
                int integer = getContext().getResources().getInteger(17694816);
                boolean z = true;
                boolean z2 = this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() >= integer;
                boolean inProgress = this.mRemovalSidecar.inProgress();
                String string = z2 ? getContext().getString(C0017R$string.fingerprint_add_max, Integer.valueOf(integer)) : "";
                Preference findPreference = findPreference("key_fingerprint_add");
                findPreference.setSummary(string);
                if (z2 || inProgress) {
                    z = false;
                }
                findPreference.setEnabled(z);
            }
        }

        private static String genKey(int i) {
            return "key_fingerprint_item_" + i;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
        public void onResume() {
            super.onResume();
            this.mInFingerprintLockout = false;
            updatePreferences();
            FingerprintRemoveSidecar fingerprintRemoveSidecar = this.mRemovalSidecar;
            if (fingerprintRemoveSidecar != null) {
                fingerprintRemoveSidecar.setListener(this.mRemovalListener);
            }
        }

        private void updatePreferences() {
            createPreferenceHierarchy();
            retryFingerprint();
        }

        @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
        public void onPause() {
            super.onPause();
            FingerprintRemoveSidecar fingerprintRemoveSidecar = this.mRemovalSidecar;
            if (fingerprintRemoveSidecar != null) {
                fingerprintRemoveSidecar.setListener(null);
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
        public void onSaveInstanceState(Bundle bundle) {
            bundle.putByteArray("hw_auth_token", this.mToken);
            bundle.putBoolean("launched_confirm", this.mLaunchedConfirm);
            bundle.putSerializable("mFingerprintsRenaming", this.mFingerprintsRenaming);
        }

        @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
        public boolean onPreferenceTreeClick(Preference preference) {
            String key = preference.getKey();
            if ("key_fingerprint_add".equals(key)) {
                Intent intent = new Intent();
                intent.setClassName(OPMemberController.PACKAGE_NAME, FingerprintEnrollEnrolling.class.getName());
                intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
                intent.putExtra("hw_auth_token", this.mToken);
                startActivityForResult(intent, 10);
                getActivity().overridePendingTransition(C0002R$anim.op_activity_fingeprint_open_enter, C0002R$anim.op_activity_fingeprint_close_exit);
            } else if ("key_custom_animation".equals(key)) {
                Intent intent2 = new Intent();
                intent2.setClassName(OPMemberController.PACKAGE_NAME, "com.android.settings.Settings$OPCustomFingerprintAnimSettingsActivity");
                startActivityForResult(intent2, 11);
            } else if (preference instanceof FingerprintPreference) {
                showRenameDialog(((FingerprintPreference) preference).getFingerprint());
            }
            return super.onPreferenceTreeClick(preference);
        }

        @Override // com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintPreference.OnDeleteClickListener
        public void onDeleteClick(FingerprintPreference fingerprintPreference) {
            boolean z = true;
            if (this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() <= 1) {
                z = false;
            }
            Parcelable fingerprint = fingerprintPreference.getFingerprint();
            if (!z) {
                ConfirmLastDeleteDialog confirmLastDeleteDialog = new ConfirmLastDeleteDialog();
                boolean isManagedProfile = UserManager.get(getContext()).isManagedProfile(this.mUserId);
                Bundle bundle = new Bundle();
                bundle.putParcelable("fingerprint", fingerprint);
                bundle.putBoolean("isProfileChallengeUser", isManagedProfile);
                confirmLastDeleteDialog.setArguments(bundle);
                confirmLastDeleteDialog.setTargetFragment(this, 0);
                confirmLastDeleteDialog.show(getFragmentManager(), ConfirmLastDeleteDialog.class.getName());
            } else if (this.mRemovalSidecar.inProgress()) {
                Log.d("FingerprintSettings", "Fingerprint delete in progress, skipping");
            } else {
                DeleteFingerprintDialog.newInstance(fingerprint, this).show(getFragmentManager(), DeleteFingerprintDialog.class.getName());
            }
        }

        private void showRenameDialog(Fingerprint fingerprint) {
            RenameDialog renameDialog = new RenameDialog();
            Bundle bundle = new Bundle();
            if (this.mFingerprintsRenaming.containsKey(Integer.valueOf(fingerprint.getBiometricId()))) {
                bundle.putParcelable("fingerprint", new Fingerprint(this.mFingerprintsRenaming.get(Integer.valueOf(fingerprint.getBiometricId())), fingerprint.getGroupId(), fingerprint.getBiometricId(), fingerprint.getDeviceId()));
            } else {
                bundle.putParcelable("fingerprint", fingerprint);
            }
            renameDialog.setDeleteInProgress(this.mRemovalSidecar.inProgress());
            renameDialog.setArguments(bundle);
            renameDialog.setTargetFragment(this, 0);
            renameDialog.show(getFragmentManager(), RenameDialog.class.getName());
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            String key = preference.getKey();
            if ("fingerprint_enable_keyguard_toggle".equals(key)) {
                return true;
            }
            Log.v("FingerprintSettings", "Unknown key:" + key);
            return true;
        }

        @Override // com.android.settings.support.actionbar.HelpResourceProvider
        public int getHelpResource() {
            return C0017R$string.help_url_fingerprint;
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 102 || i == 101) {
                this.mLaunchedConfirm = false;
                if ((i2 == 1 || i2 == -1) && intent != null) {
                    this.mToken = intent.getByteArrayExtra("hw_auth_token");
                }
            } else if (i == 10 && i2 == 3) {
                FragmentActivity activity = getActivity();
                activity.setResult(i2);
                activity.finish();
            }
            if (this.mToken == null) {
                getActivity().finish();
            }
        }

        @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
        public void onDestroy() {
            int postEnroll;
            super.onDestroy();
            if (getActivity().isFinishing() && (postEnroll = this.mFingerprintManager.postEnroll()) < 0) {
                Log.w("FingerprintSettings", "postEnroll failed: result = " + postEnroll);
            }
        }

        private Drawable getHighlightDrawable() {
            FragmentActivity activity;
            if (this.mHighlightDrawable == null && (activity = getActivity()) != null) {
                this.mHighlightDrawable = activity.getDrawable(C0008R$drawable.preference_highlight);
            }
            return this.mHighlightDrawable;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        /* JADX WARNING: Code restructure failed: missing block: B:3:0x0012, code lost:
            r4 = r4.getView();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void highlightFingerprintItem(int r4) {
            /*
                r3 = this;
                java.lang.String r4 = genKey(r4)
                androidx.preference.Preference r4 = r3.findPreference(r4)
                com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintPreference r4 = (com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintPreference) r4
                android.graphics.drawable.Drawable r0 = r3.getHighlightDrawable()
                if (r0 == 0) goto L_0x0041
                if (r4 == 0) goto L_0x0041
                android.view.View r4 = r4.getView()
                if (r4 != 0) goto L_0x0019
                return
            L_0x0019:
                int r1 = r4.getWidth()
                int r1 = r1 / 2
                int r2 = r4.getHeight()
                int r2 = r2 / 2
                float r1 = (float) r1
                float r2 = (float) r2
                r0.setHotspot(r1, r2)
                r4.setBackground(r0)
                r0 = 1
                r4.setPressed(r0)
                r0 = 0
                r4.setPressed(r0)
                android.os.Handler r0 = r3.mHandler
                com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$4 r1 = new com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$4
                r1.<init>(r3, r4)
                r3 = 500(0x1f4, double:2.47E-321)
                r0.postDelayed(r1, r3)
            L_0x0041:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.highlightFingerprintItem(int):void");
        }

        private void launchChooseOrConfirmLock() {
            Intent intent = new Intent();
            long preEnroll = this.mFingerprintManager.preEnroll();
            if (!new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(R$styleable.Constraint_layout_goneMarginRight, (CharSequence) getString(C0017R$string.security_settings_fingerprint_preference_title), (CharSequence) null, (CharSequence) null, preEnroll, this.mUserId, true)) {
                intent.setClassName(OPMemberController.PACKAGE_NAME, ChooseLockGeneric.class.getName());
                intent.putExtra("minimum_quality", 65536);
                intent.putExtra("hide_disabled_prefs", true);
                intent.putExtra("has_challenge", true);
                intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
                intent.putExtra("challenge", preEnroll);
                intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
                startActivityForResult(intent, R$styleable.Constraint_layout_goneMarginStart);
            }
        }

        /* access modifiers changed from: package-private */
        public void deleteFingerPrint(Fingerprint fingerprint) {
            this.mRemovalSidecar.startRemove(fingerprint, this.mUserId);
            findPreference(genKey(fingerprint.getBiometricId())).setEnabled(false);
            updateAddPreference();
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void renameFingerPrint(int i, String str) {
            this.mFingerprintManager.rename(i, this.mUserId, str);
            if (!TextUtils.isEmpty(str)) {
                this.mFingerprintsRenaming.put(Integer.valueOf(i), str);
            }
            updatePreferences();
        }

        public static class DeleteFingerprintDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
            private AlertDialog mAlertDialog;
            private Fingerprint mFp;

            @Override // com.android.settingslib.core.instrumentation.Instrumentable
            public int getMetricsCategory() {
                return 570;
            }

            public static DeleteFingerprintDialog newInstance(Fingerprint fingerprint, FingerprintSettingsFragment fingerprintSettingsFragment) {
                DeleteFingerprintDialog deleteFingerprintDialog = new DeleteFingerprintDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable("fingerprint", fingerprint);
                deleteFingerprintDialog.setArguments(bundle);
                deleteFingerprintDialog.setTargetFragment(fingerprintSettingsFragment, 0);
                return deleteFingerprintDialog;
            }

            @Override // androidx.fragment.app.DialogFragment
            public Dialog onCreateDialog(Bundle bundle) {
                Fingerprint parcelable = getArguments().getParcelable("fingerprint");
                this.mFp = parcelable;
                String string = getString(C0017R$string.fingerprint_delete_title, parcelable.getName());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(string);
                builder.setMessage(C0017R$string.fingerprint_delete_message);
                builder.setPositiveButton(C0017R$string.security_settings_fingerprint_enroll_dialog_delete, this);
                builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                this.mAlertDialog = create;
                return create;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    int biometricId = this.mFp.getBiometricId();
                    Log.v("FingerprintSettings", "Removing fpId=" + biometricId);
                    this.mMetricsFeatureProvider.action(getContext(), 253, biometricId);
                    ((FingerprintSettingsFragment) getTargetFragment()).deleteFingerPrint(this.mFp);
                }
            }
        }

        public static class RenameDialog extends InstrumentedDialogFragment {
            private AlertDialog mAlertDialog;
            private boolean mDeleteInProgress;
            private ImeAwareEditText mDialogTextField;
            private Fingerprint mFp;
            private Boolean mTextHadFocus;

            @Override // com.android.settingslib.core.instrumentation.Instrumentable
            public int getMetricsCategory() {
                return 570;
            }

            public void setDeleteInProgress(boolean z) {
                this.mDeleteInProgress = z;
            }

            @Override // androidx.fragment.app.DialogFragment
            public Dialog onCreateDialog(Bundle bundle) {
                final int i;
                final String str;
                this.mFp = getArguments().getParcelable("fingerprint");
                final int i2 = -1;
                if (bundle != null) {
                    str = bundle.getString("fingerName");
                    int i3 = bundle.getInt("startSelection", -1);
                    i = bundle.getInt("endSelection", -1);
                    i2 = i3;
                } else {
                    str = null;
                    i = -1;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(C0012R$layout.fingerprint_rename_dialog);
                builder.setTitle(C0017R$string.security_settings_fingerprint_enroll_dialog_name_label);
                builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener(this) {
                    /* class com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.RenameDialog.AnonymousClass1 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton(C0017R$string.security_settings_fingerprint_enroll_dialog_ok, (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                this.mAlertDialog = create;
                create.setOnShowListener(new DialogInterface.OnShowListener() {
                    /* class com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.RenameDialog.AnonymousClass2 */

                    public void onShow(DialogInterface dialogInterface) {
                        RenameDialog renameDialog = RenameDialog.this;
                        renameDialog.mDialogTextField = renameDialog.mAlertDialog.findViewById(C0010R$id.fingerprint_rename_field);
                        CharSequence charSequence = str;
                        if (charSequence == null) {
                            charSequence = RenameDialog.this.mFp.getName();
                        }
                        RenameDialog.this.mDialogTextField.setText(charSequence);
                        if (i2 == -1 || i == -1) {
                            RenameDialog.this.mDialogTextField.selectAll();
                        } else {
                            RenameDialog.this.mDialogTextField.setSelection(i2, i);
                        }
                        if (RenameDialog.this.mDeleteInProgress) {
                            RenameDialog.this.mAlertDialog.getButton(-2).setEnabled(false);
                        }
                        RenameDialog.this.mDialogTextField.requestFocus();
                        RenameDialog.this.mDialogTextField.scheduleShowSoftInput();
                        RenameDialog.this.mAlertDialog.getButton(-1).setOnClickListener(new View.OnClickListener() {
                            /* class com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.RenameDialog.AnonymousClass2.AnonymousClass1 */

                            public void onClick(View view) {
                                String obj = RenameDialog.this.mDialogTextField.getText().toString();
                                if (TextUtils.isEmpty(obj)) {
                                    Toast.makeText(RenameDialog.this.getActivity(), C0017R$string.oneplus_opfinger_input_only_space, 0).show();
                                    return;
                                }
                                CharSequence name = RenameDialog.this.mFp.getName();
                                if (!TextUtils.equals(obj, name)) {
                                    Log.d("FingerprintSettings", "rename " + ((Object) name) + " to " + obj);
                                    ((FingerprintSettingsFragment) RenameDialog.this.getTargetFragment()).renameFingerPrint(RenameDialog.this.mFp.getBiometricId(), obj);
                                }
                                RenameDialog.this.mAlertDialog.dismiss();
                            }
                        });
                    }
                });
                Boolean bool = this.mTextHadFocus;
                if (bool == null || bool.booleanValue()) {
                    this.mAlertDialog.getWindow().setSoftInputMode(5);
                }
                return this.mAlertDialog;
            }

            public void enableDelete() {
                this.mDeleteInProgress = false;
                AlertDialog alertDialog = this.mAlertDialog;
                if (alertDialog != null) {
                    alertDialog.getButton(-2).setEnabled(true);
                }
            }

            @Override // androidx.fragment.app.Fragment, androidx.fragment.app.DialogFragment
            public void onSaveInstanceState(Bundle bundle) {
                super.onSaveInstanceState(bundle);
                ImeAwareEditText imeAwareEditText = this.mDialogTextField;
                if (imeAwareEditText != null) {
                    bundle.putString("fingerName", imeAwareEditText.getText().toString());
                    bundle.putBoolean("textHadFocus", this.mDialogTextField.hasFocus());
                    bundle.putInt("startSelection", this.mDialogTextField.getSelectionStart());
                    bundle.putInt("endSelection", this.mDialogTextField.getSelectionEnd());
                }
            }
        }

        public static class ConfirmLastDeleteDialog extends InstrumentedDialogFragment {
            private Fingerprint mFp;

            @Override // com.android.settingslib.core.instrumentation.Instrumentable
            public int getMetricsCategory() {
                return 571;
            }

            @Override // androidx.fragment.app.DialogFragment
            public Dialog onCreateDialog(Bundle bundle) {
                int i;
                this.mFp = getArguments().getParcelable("fingerprint");
                boolean z = getArguments().getBoolean("isProfileChallengeUser");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(C0017R$string.fingerprint_last_delete_title);
                if (z) {
                    i = C0017R$string.fingerprint_last_delete_message_profile_challenge;
                } else {
                    i = C0017R$string.fingerprint_last_delete_message;
                }
                builder.setMessage(i);
                builder.setPositiveButton(C0017R$string.fingerprint_last_delete_confirm, new DialogInterface.OnClickListener() {
                    /* class com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog.AnonymousClass2 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((FingerprintSettingsFragment) ConfirmLastDeleteDialog.this.getTargetFragment()).deleteFingerPrint(ConfirmLastDeleteDialog.this.mFp);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener(this) {
                    /* class com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog.AnonymousClass1 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                return builder.create();
            }
        }
    }

    public static class FingerprintPreference extends TwoTargetPreference {
        private View mDeleteView;
        private Fingerprint mFingerprint;
        private final OnDeleteClickListener mOnDeleteClickListener;
        private View mView;

        public interface OnDeleteClickListener {
            void onDeleteClick(FingerprintPreference fingerprintPreference);
        }

        public FingerprintPreference(Context context, OnDeleteClickListener onDeleteClickListener) {
            super(context);
            setLayoutResource(C0012R$layout.op_preference_two_target);
            this.mOnDeleteClickListener = onDeleteClickListener;
        }

        public View getView() {
            return this.mView;
        }

        public void setFingerprint(Fingerprint fingerprint) {
            this.mFingerprint = fingerprint;
        }

        public Fingerprint getFingerprint() {
            return this.mFingerprint;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settingslib.TwoTargetPreference
        public int getSecondTargetResId() {
            return C0012R$layout.preference_widget_delete;
        }

        @Override // com.android.settingslib.TwoTargetPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            View view = preferenceViewHolder.itemView;
            this.mView = view;
            View findViewById = view.findViewById(C0010R$id.delete_button);
            this.mDeleteView = findViewById;
            findViewById.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintPreference.AnonymousClass1 */

                public void onClick(View view) {
                    if (FingerprintPreference.this.mOnDeleteClickListener != null) {
                        FingerprintPreference.this.mOnDeleteClickListener.onDeleteClick(FingerprintPreference.this);
                    }
                }
            });
        }
    }
}
