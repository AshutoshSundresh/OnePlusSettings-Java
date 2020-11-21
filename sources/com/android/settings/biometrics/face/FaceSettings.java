package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.Intent;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.biometrics.face.FaceSettingsEnrollButtonPreferenceController;
import com.android.settings.biometrics.face.FaceSettingsRemoveButtonPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FaceSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.security_settings_face) {
        /* class com.android.settings.biometrics.face.FaceSettings.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            if (FaceSettings.isFaceHardwareDetected(context)) {
                return FaceSettings.buildPreferenceControllers(context, null);
            }
            return null;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            if (FaceSettings.isFaceHardwareDetected(context)) {
                return hasEnrolledBiometrics(context);
            }
            return false;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            boolean isFaceHardwareDetected = FaceSettings.isFaceHardwareDetected(context);
            Log.d("FaceSettings", "Get non indexable keys. isFaceHardwareDetected: " + isFaceHardwareDetected + ", size:" + nonIndexableKeys.size());
            if (isFaceHardwareDetected) {
                nonIndexableKeys.add(hasEnrolledBiometrics(context) ? "security_settings_face_enroll_faces_container" : "security_settings_face_delete_faces_container");
            }
            if (!isAttentionSupported(context)) {
                nonIndexableKeys.add(FaceSettingsAttentionPreferenceController.KEY);
            }
            return nonIndexableKeys;
        }

        private boolean isAttentionSupported(Context context) {
            FaceFeatureProvider faceFeatureProvider = FeatureFactory.getFactory(context).getFaceFeatureProvider();
            if (faceFeatureProvider != null) {
                return faceFeatureProvider.isAttentionSupported(context);
            }
            return false;
        }

        private boolean hasEnrolledBiometrics(Context context) {
            FaceManager faceManagerOrNull = Utils.getFaceManagerOrNull(context);
            if (faceManagerOrNull != null) {
                return faceManagerOrNull.hasEnrolledTemplates(UserHandle.myUserId());
            }
            return false;
        }
    };
    private FaceSettingsAttentionPreferenceController mAttentionController;
    private boolean mConfirmingPassword;
    private List<AbstractPreferenceController> mControllers;
    private Preference mEnrollButton;
    private FaceSettingsEnrollButtonPreferenceController mEnrollController;
    private final FaceSettingsEnrollButtonPreferenceController.Listener mEnrollListener = new FaceSettingsEnrollButtonPreferenceController.Listener() {
        /* class com.android.settings.biometrics.face.$$Lambda$FaceSettings$jF61l4Lp3aD6OmNPFSqLsoQfV3U */

        @Override // com.android.settings.biometrics.face.FaceSettingsEnrollButtonPreferenceController.Listener
        public final void onStartEnrolling(Intent intent) {
            FaceSettings.this.lambda$new$1$FaceSettings(intent);
        }
    };
    private FaceFeatureProvider mFaceFeatureProvider;
    private FaceManager mFaceManager;
    private FaceSettingsLockscreenBypassPreferenceController mLockscreenController;
    private final FaceSettingsRemoveButtonPreferenceController.Listener mRemovalListener = new FaceSettingsRemoveButtonPreferenceController.Listener() {
        /* class com.android.settings.biometrics.face.$$Lambda$FaceSettings$tVIggAOHWpKaxl2s3S5Mc0KLQtA */

        @Override // com.android.settings.biometrics.face.FaceSettingsRemoveButtonPreferenceController.Listener
        public final void onRemoved() {
            FaceSettings.this.lambda$new$0$FaceSettings();
        }
    };
    private Preference mRemoveButton;
    private FaceSettingsRemoveButtonPreferenceController mRemoveController;
    private List<Preference> mTogglePreferences;
    private byte[] mToken;
    private int mUserId;
    private UserManager mUserManager;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "FaceSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1511;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$FaceSettings() {
        for (Preference preference : this.mTogglePreferences) {
            preference.setEnabled(false);
        }
        this.mRemoveButton.setVisible(false);
        this.mEnrollButton.setVisible(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$FaceSettings(Intent intent) {
        startActivityForResult(intent, 5);
    }

    public static boolean isFaceHardwareDetected(Context context) {
        boolean z;
        FaceManager faceManagerOrNull = Utils.getFaceManagerOrNull(context);
        if (faceManagerOrNull == null) {
            Log.d("FaceSettings", "FaceManager is null");
            z = false;
        } else {
            z = faceManagerOrNull.isHardwareDetected();
            Log.d("FaceSettings", "FaceManager is not null. Hardware detected: " + z);
        }
        if (faceManagerOrNull == null || !z) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.security_settings_face;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putByteArray("hw_auth_token", this.mToken);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context prefContext = getPrefContext();
        if (!isFaceHardwareDetected(prefContext)) {
            Log.w("FaceSettings", "no faceManager, finish this");
            finish();
            return;
        }
        this.mUserManager = (UserManager) prefContext.getSystemService(UserManager.class);
        this.mFaceManager = (FaceManager) prefContext.getSystemService(FaceManager.class);
        this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
        this.mUserId = getActivity().getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
        this.mFaceFeatureProvider = FeatureFactory.getFactory(getContext()).getFaceFeatureProvider();
        if (this.mUserManager.getUserInfo(this.mUserId).isManagedProfile()) {
            getActivity().setTitle(getActivity().getResources().getString(C0017R$string.security_settings_face_profile_preference_title));
        }
        this.mTogglePreferences = new ArrayList(Arrays.asList(findPreference("security_settings_face_keyguard"), findPreference("security_settings_face_app"), findPreference(FaceSettingsAttentionPreferenceController.KEY), findPreference("security_settings_face_require_confirmation"), findPreference(this.mLockscreenController.getPreferenceKey())));
        this.mRemoveButton = findPreference("security_settings_face_delete_faces_container");
        this.mEnrollButton = findPreference("security_settings_face_enroll_faces_container");
        for (AbstractPreferenceController abstractPreferenceController : this.mControllers) {
            if (abstractPreferenceController instanceof FaceSettingsPreferenceController) {
                ((FaceSettingsPreferenceController) abstractPreferenceController).setUserId(this.mUserId);
            } else if (abstractPreferenceController instanceof FaceSettingsEnrollButtonPreferenceController) {
                ((FaceSettingsEnrollButtonPreferenceController) abstractPreferenceController).setUserId(this.mUserId);
            }
        }
        this.mRemoveController.setUserId(this.mUserId);
        if (this.mUserManager.isManagedProfile(this.mUserId)) {
            removePreference("security_settings_face_keyguard");
            removePreference(this.mLockscreenController.getPreferenceKey());
        }
        if (bundle != null) {
            this.mToken = bundle.getByteArray("hw_auth_token");
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        FaceSettingsLockscreenBypassPreferenceController faceSettingsLockscreenBypassPreferenceController = (FaceSettingsLockscreenBypassPreferenceController) use(FaceSettingsLockscreenBypassPreferenceController.class);
        this.mLockscreenController = faceSettingsLockscreenBypassPreferenceController;
        faceSettingsLockscreenBypassPreferenceController.setUserId(this.mUserId);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (this.mToken != null || this.mConfirmingPassword) {
            this.mAttentionController.setToken(this.mToken);
            this.mEnrollController.setToken(this.mToken);
        } else {
            long generateChallenge = this.mFaceManager.generateChallenge();
            ChooseLockSettingsHelper chooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity(), this);
            this.mConfirmingPassword = true;
            if (!chooseLockSettingsHelper.launchConfirmationActivity(4, (CharSequence) getString(C0017R$string.security_settings_face_preference_title), (CharSequence) null, (CharSequence) null, generateChallenge, this.mUserId, true)) {
                Log.e("FaceSettings", "Password not set");
                finish();
            }
        }
        boolean hasEnrolledTemplates = this.mFaceManager.hasEnrolledTemplates(this.mUserId);
        this.mEnrollButton.setVisible(!hasEnrolledTemplates);
        this.mRemoveButton.setVisible(hasEnrolledTemplates);
        if (!this.mFaceFeatureProvider.isAttentionSupported(getContext())) {
            removePreference(FaceSettingsAttentionPreferenceController.KEY);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 4) {
            this.mConfirmingPassword = false;
            if (i2 == 1 || i2 == -1) {
                this.mFaceManager.setActiveUser(this.mUserId);
                if (intent != null) {
                    byte[] byteArrayExtra = intent.getByteArrayExtra("hw_auth_token");
                    this.mToken = byteArrayExtra;
                    if (byteArrayExtra != null) {
                        this.mAttentionController.setToken(byteArrayExtra);
                        this.mEnrollController.setToken(this.mToken);
                    }
                }
            }
        } else if (i == 5 && i2 == 3) {
            setResult(i2, intent);
            finish();
        }
        if (this.mToken == null) {
            finish();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        if (!this.mEnrollController.isClicked() && !getActivity().isChangingConfigurations() && !this.mConfirmingPassword) {
            if (this.mToken != null) {
                int revokeChallenge = this.mFaceManager.revokeChallenge();
                if (revokeChallenge < 0) {
                    Log.w("FaceSettings", "revokeChallenge failed, result: " + revokeChallenge);
                }
                this.mToken = null;
            }
            finish();
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_face;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        if (!isFaceHardwareDetected(context)) {
            return null;
        }
        List<AbstractPreferenceController> buildPreferenceControllers = buildPreferenceControllers(context, getSettingsLifecycle());
        this.mControllers = buildPreferenceControllers;
        for (AbstractPreferenceController abstractPreferenceController : buildPreferenceControllers) {
            if (abstractPreferenceController instanceof FaceSettingsAttentionPreferenceController) {
                this.mAttentionController = (FaceSettingsAttentionPreferenceController) abstractPreferenceController;
            } else if (abstractPreferenceController instanceof FaceSettingsRemoveButtonPreferenceController) {
                FaceSettingsRemoveButtonPreferenceController faceSettingsRemoveButtonPreferenceController = (FaceSettingsRemoveButtonPreferenceController) abstractPreferenceController;
                this.mRemoveController = faceSettingsRemoveButtonPreferenceController;
                faceSettingsRemoveButtonPreferenceController.setListener(this.mRemovalListener);
                this.mRemoveController.setActivity((SettingsActivity) getActivity());
            } else if (abstractPreferenceController instanceof FaceSettingsEnrollButtonPreferenceController) {
                FaceSettingsEnrollButtonPreferenceController faceSettingsEnrollButtonPreferenceController = (FaceSettingsEnrollButtonPreferenceController) abstractPreferenceController;
                this.mEnrollController = faceSettingsEnrollButtonPreferenceController;
                faceSettingsEnrollButtonPreferenceController.setListener(this.mEnrollListener);
                this.mEnrollController.setActivity((SettingsActivity) getActivity());
            }
        }
        return this.mControllers;
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new FaceSettingsKeyguardPreferenceController(context));
        arrayList.add(new FaceSettingsAppPreferenceController(context));
        arrayList.add(new FaceSettingsAttentionPreferenceController(context));
        arrayList.add(new FaceSettingsRemoveButtonPreferenceController(context));
        arrayList.add(new FaceSettingsFooterPreferenceController(context));
        arrayList.add(new FaceSettingsConfirmPreferenceController(context));
        arrayList.add(new FaceSettingsEnrollButtonPreferenceController(context));
        return arrayList;
    }
}
