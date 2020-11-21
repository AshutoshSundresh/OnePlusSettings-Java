package com.oneplus.settings.faceunlock;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.oneplus.faceunlock.internal.IOPFaceSettingService;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.utils.OPApplicationUtils;
import com.oneplus.settings.utils.OPUtils;

public class OPFaceUnlockSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private Preference mAddFace;
    private ActivityManager mAm;
    private SwitchPreference mAutoFaceUnlock;
    private IOPFaceSettingService mFaceSettingService;
    private String mFaceUnlocKSwipeUp;
    private SwitchPreference mFaceUnlock;
    private SwitchPreference mFaceUnlockAssistiveLighting;
    private PreferenceCategory mFaceUnlockCategory;
    private ServiceConnection mFaceUnlockConnection = new ServiceConnection() {
        /* class com.oneplus.settings.faceunlock.OPFaceUnlockSettings.AnonymousClass1 */

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("OPFaceUnlockSettings", "Oneplus face unlock service connected");
            OPFaceUnlockSettings.this.mFaceSettingService = IOPFaceSettingService.Stub.asInterface(iBinder);
            OPFaceUnlockSettings.this.mUIHandler.sendEmptyMessage(100);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("OPFaceUnlockSettings", "Oneplus face unlock service disconnected");
            OPFaceUnlockSettings.this.mFaceSettingService = null;
        }
    };
    private Preference mFaceUnlockMode;
    private String mFaceUnlockTitle;
    private AlertDialog mRemoveDialog;
    private Preference mRemoveFace;
    private Handler mUIHandler = new Handler() {
        /* class com.oneplus.settings.faceunlock.OPFaceUnlockSettings.AnonymousClass2 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                OPFaceUnlockSettings.this.refreshUI();
            }
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getActivity().isInMultiWindowMode()) {
            Log.w("OPFaceUnlockSettings", "onCreate  isInMultiWindowMode");
            Toast.makeText(getContext(), C0017R$string.feature_not_support_split_screen, 0).show();
            finish();
            return;
        }
        this.mAm = (ActivityManager) getSystemService("activity");
        this.mFaceUnlockTitle = getString(C0017R$string.oneplus_face_auto_unlock_while_screen_on_title);
        this.mFaceUnlocKSwipeUp = getString(C0017R$string.oneplus_face_unlock_choose_swipe_up_mode);
        addPreferencesFromResource(C0019R$xml.op_faceunlock_settings);
        initView();
        launchChooseOrConfirmLock(4);
    }

    private void bindFaceUnlockService() {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.oneplus.faceunlock", "com.oneplus.faceunlock.FaceSettingService");
            getActivity().bindService(intent, this.mFaceUnlockConnection, 1);
            Log.i("OPFaceUnlockSettings", "Start bind oneplus face unlockservice");
        } catch (Exception unused) {
            Log.i("OPFaceUnlockSettings", "Bind oneplus face unlockservice exception");
        }
    }

    private void unbindFaceUnlockService() {
        Log.i("OPFaceUnlockSettings", "Start unbind oneplus face unlockservice");
        if (this.mFaceSettingService != null) {
            try {
                getActivity().unbindService(this.mFaceUnlockConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        this.mFaceUnlockCategory = (PreferenceCategory) findPreference("key_faceunlock_category");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("key_faceunlock_management_category");
        Preference findPreference = findPreference("key_add_face");
        this.mAddFace = findPreference;
        findPreference.setOnPreferenceClickListener(this);
        Preference findPreference2 = findPreference("key_remove_face");
        this.mRemoveFace = findPreference2;
        findPreference2.setOnPreferenceClickListener(this);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("key_face_unlock_enable");
        this.mFaceUnlock = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("key_auto_face_unlock_enable");
        this.mAutoFaceUnlock = switchPreference2;
        switchPreference2.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference3 = (SwitchPreference) findPreference("key_face_unlock_assistive_lighting");
        this.mFaceUnlockAssistiveLighting = switchPreference3;
        switchPreference3.setOnPreferenceChangeListener(this);
        this.mFaceUnlockMode = findPreference("key_face_unlock_mode");
        if (OPUtils.isSupportXCamera()) {
            this.mAutoFaceUnlock.setVisible(false);
        } else {
            this.mFaceUnlockMode.setVisible(false);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        bindFaceUnlockService();
        if (isAdded()) {
            refreshUI();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshUI() {
        if (isFaceAdded()) {
            this.mFaceUnlockCategory.addPreference(this.mRemoveFace);
            this.mFaceUnlockCategory.removePreference(this.mAddFace);
            this.mFaceUnlock.setEnabled(true);
            disableAutoUnlockSettings(this.mFaceUnlock.isChecked());
            disableFaceUnlockAssistiveLightingSettings(this.mFaceUnlock.isChecked());
            disableFaceUnlockModeSettings(this.mFaceUnlock.isChecked());
        } else {
            this.mFaceUnlockCategory.addPreference(this.mAddFace);
            this.mFaceUnlockCategory.removePreference(this.mRemoveFace);
            this.mFaceUnlock.setEnabled(false);
            this.mAutoFaceUnlock.setEnabled(false);
            SwitchPreference switchPreference = this.mFaceUnlockAssistiveLighting;
            if (switchPreference != null) {
                switchPreference.setEnabled(false);
            }
            this.mFaceUnlockMode.setEnabled(false);
        }
        this.mFaceUnlock.setChecked(isFaceUnlockEnabled());
        this.mAutoFaceUnlock.setChecked(isAutoFaceUnlockEnabled());
        SwitchPreference switchPreference2 = this.mFaceUnlockAssistiveLighting;
        if (switchPreference2 != null) {
            switchPreference2.setChecked(isFaceUnlockAssistiveLightingEnabled());
        }
        if (this.mFaceUnlockMode != null && !isDetached() && !isRemoving()) {
            this.mFaceUnlockMode.setSummary(Settings.System.getInt(getContentResolver(), "oneplus_face_unlock_powerkey_recognize_enable", 0) == 1 ? this.mFaceUnlockTitle : this.mFaceUnlocKSwipeUp);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        unbindFaceUnlockService();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if ("key_add_face".equals(key)) {
            if (OPUtils.isFaceUnlockEnabled(getActivity())) {
                showDisableAospFaceUnlockDialog();
            } else {
                gotoAddFaceData();
            }
            return true;
        } else if (!"key_remove_face".equals(key)) {
            return false;
        } else {
            showRemoveFaceDataDialog();
            return true;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if ("key_face_unlock_enable".equals(key)) {
            switchFaceUnlock(booleanValue);
            return true;
        } else if ("key_auto_face_unlock_enable".equals(key)) {
            switchAutoFaceUnlock(booleanValue);
            return true;
        } else if (!"key_face_unlock_assistive_lighting".equals(key)) {
            return false;
        } else {
            switchFaceUnlockAssistiveLighting(booleanValue);
            return true;
        }
    }

    private boolean isFaceAdded() {
        IOPFaceSettingService iOPFaceSettingService = this.mFaceSettingService;
        if (iOPFaceSettingService == null) {
            return false;
        }
        int i = 2;
        try {
            i = iOPFaceSettingService.checkState(0);
            Log.i("OPFaceUnlockSettings", "Start check face state:" + i);
        } catch (RemoteException e) {
            Log.i("OPFaceUnlockSettings", "Start check face State RemoteException:" + e);
        }
        if (i == 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void removeFaceData() {
        IOPFaceSettingService iOPFaceSettingService = this.mFaceSettingService;
        if (iOPFaceSettingService != null) {
            try {
                iOPFaceSettingService.removeFace(0);
                this.mUIHandler.sendEmptyMessage(100);
                unbindFaceUnlockService();
                this.mAddFace.setEnabled(false);
                this.mRemoveFace.setEnabled(false);
                this.mUIHandler.postDelayed(new Runnable() {
                    /* class com.oneplus.settings.faceunlock.OPFaceUnlockSettings.AnonymousClass3 */

                    public void run() {
                        OPApplicationUtils.killRunningTargetProcess(OPFaceUnlockSettings.this.mAm, "com.oneplus.faceunlock");
                        OPFaceUnlockSettings.this.mAddFace.setEnabled(true);
                        OPFaceUnlockSettings.this.mRemoveFace.setEnabled(true);
                        Log.i("OPFaceUnlockSettings", "Oneplus face unlock killRunningTargetProcess");
                    }
                }, 500);
            } catch (RemoteException e) {
                Log.i("OPFaceUnlockSettings", "Start remove face RemoteException:" + e);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void gotoAddFaceData() {
        Intent intent = null;
        try {
            Intent intent2 = new Intent();
            try {
                intent2.setClassName("com.oneplus.faceunlock", "com.oneplus.faceunlock.FaceUnlockActivity");
                intent2.putExtra("FaceUnlockActivity.StartMode", 0);
                startActivity(intent2);
            } catch (ActivityNotFoundException unused) {
                intent = intent2;
            }
        } catch (ActivityNotFoundException unused2) {
            Log.d("OPFaceUnlockSettings", "No activity found for " + intent);
        }
    }

    public void showDisableAospFaceUnlockDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(C0017R$string.oneplus_disable_aosp_face_lock_message);
        builder.setPositiveButton(C0017R$string.security_settings_fingerprint_enroll_introduction_continue, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.faceunlock.OPFaceUnlockSettings.AnonymousClass5 */

            public void onClick(DialogInterface dialogInterface, int i) {
                OPUtils.disableAospFaceUnlock(OPFaceUnlockSettings.this.getActivity());
                OPFaceUnlockSettings.this.gotoAddFaceData();
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener(this) {
            /* class com.oneplus.settings.faceunlock.OPFaceUnlockSettings.AnonymousClass4 */

            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    public void showRemoveFaceDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.oneplus_face_unlock_remove_dialog_title);
        builder.setMessage(C0017R$string.oneplus_face_unlock_remove_dialog_message);
        builder.setPositiveButton(C0017R$string.okay, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.faceunlock.OPFaceUnlockSettings.AnonymousClass7 */

            public void onClick(DialogInterface dialogInterface, int i) {
                OPFaceUnlockSettings.this.removeFaceData();
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener(this) {
            /* class com.oneplus.settings.faceunlock.OPFaceUnlockSettings.AnonymousClass6 */

            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog create = builder.create();
        this.mRemoveDialog = create;
        create.show();
    }

    private void disableAutoUnlockSettings(boolean z) {
        SwitchPreference switchPreference = this.mAutoFaceUnlock;
        if (switchPreference != null) {
            switchPreference.setEnabled(z);
        }
    }

    private void disableFaceUnlockAssistiveLightingSettings(boolean z) {
        SwitchPreference switchPreference = this.mFaceUnlockAssistiveLighting;
        if (switchPreference != null) {
            switchPreference.setEnabled(z);
        }
    }

    private void disableFaceUnlockModeSettings(boolean z) {
        Preference preference = this.mFaceUnlockMode;
        if (preference != null) {
            preference.setEnabled(z);
        }
    }

    private boolean isFaceUnlockEnabled() {
        return Settings.System.getInt(getContentResolver(), "oneplus_face_unlock_enable", 0) == 1;
    }

    private void switchFaceUnlock(boolean z) {
        Settings.System.putInt(getContentResolver(), "oneplus_face_unlock_enable", z ? 1 : 0);
        disableAutoUnlockSettings(z);
        disableFaceUnlockAssistiveLightingSettings(z);
        disableFaceUnlockModeSettings(z);
    }

    private boolean isAutoFaceUnlockEnabled() {
        return Settings.System.getInt(getContentResolver(), "oneplus_auto_face_unlock_enable", 0) == 1;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: boolean */
    /* JADX WARN: Multi-variable type inference failed */
    private void switchAutoFaceUnlock(boolean z) {
        Settings.System.putInt(getContentResolver(), "oneplus_auto_face_unlock_enable", z ? 1 : 0);
        OPUtils.sendAppTracker("auto_face_unlock", (int) z);
    }

    private boolean isFaceUnlockAssistiveLightingEnabled() {
        return Settings.System.getInt(getContentResolver(), "oneplus_face_unlock_assistive_lighting_enable", 0) == 1;
    }

    private void switchFaceUnlockAssistiveLighting(boolean z) {
        Settings.System.putInt(getContentResolver(), "oneplus_face_unlock_assistive_lighting_enable", z ? 1 : 0);
    }

    private void launchChooseOrConfirmLock(int i) {
        long preEnroll = Utils.getFingerprintManagerOrNull(getActivity()).preEnroll();
        Intent intent = new Intent();
        if (!new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(i, getActivity().getString(C0017R$string.op_security_lock_settings_title), null, null, preEnroll, true)) {
            intent.setClassName(OPMemberController.PACKAGE_NAME, ChooseLockGeneric.class.getName());
            intent.putExtra("minimum_quality", 65536);
            intent.putExtra("hide_disabled_prefs", true);
            intent.putExtra("has_challenge", true);
            intent.putExtra("challenge", preEnroll);
            intent.putExtra("for_face", true);
            startActivityForResult(intent, 3);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (5 != i) {
            if (!((i == 4 || i == 3) && (i2 == 1 || i2 == -1))) {
                finish();
            }
            super.onActivityResult(i, i2, intent);
        } else if (isFaceAdded() && OPUtils.isSupportXCamera()) {
            Intent intent2 = new Intent();
            intent2.setClassName(OPMemberController.PACKAGE_NAME, "com.oneplus.settings.faceunlock.OPFaceUnlockModeSettingsActivity");
            startActivity(intent2);
        }
    }
}
