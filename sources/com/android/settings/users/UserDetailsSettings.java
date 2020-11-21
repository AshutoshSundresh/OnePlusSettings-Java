package com.android.settings.users;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.R$string;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;

public class UserDetailsSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private static final String TAG = UserDetailsSettings.class.getSimpleName();
    Preference mAppAndContentAccessPref;
    private Bundle mDefaultGuestRestrictions;
    private SwitchPreference mPhonePref;
    Preference mRemoveUserPref;
    RestrictedPreference mSwitchUserPref;
    private UserCapabilities mUserCaps;
    UserInfo mUserInfo;
    private UserManager mUserManager;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i == 1) {
            return 591;
        }
        if (i == 2) {
            return 592;
        }
        if (i != 3) {
            return i != 4 ? 0 : 596;
        }
        return 593;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 98;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mUserManager = (UserManager) activity.getSystemService("user");
        this.mUserCaps = UserCapabilities.create(activity);
        addPreferencesFromResource(C0019R$xml.user_details_settings);
        initialize(activity, getArguments());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mSwitchUserPref.setEnabled(canSwitchUserNow());
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mRemoveUserPref) {
            if (canDeleteUser()) {
                showDialog(1);
                return true;
            }
        } else if (preference == this.mSwitchUserPref) {
            if (canSwitchUserNow()) {
                if (shouldShowSetupPromptDialog()) {
                    showDialog(4);
                } else {
                    switchUser();
                }
                return true;
            }
        } else if (preference == this.mAppAndContentAccessPref) {
            openAppAndContentAccessScreen(false);
            return true;
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (Boolean.TRUE.equals(obj)) {
            showDialog(this.mUserInfo.isGuest() ? 2 : 3);
            return false;
        }
        enableCallsAndSms(false);
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (getActivity() == null) {
            return null;
        }
        if (i == 1) {
            return UserDialogs.createRemoveDialog(getActivity(), this.mUserInfo.id, new DialogInterface.OnClickListener() {
                /* class com.android.settings.users.$$Lambda$UserDetailsSettings$MUjonou8YK_sNRuAwHIXfNdvlnM */

                public final void onClick(DialogInterface dialogInterface, int i) {
                    UserDetailsSettings.this.lambda$onCreateDialog$0$UserDetailsSettings(dialogInterface, i);
                }
            });
        }
        if (i == 2) {
            return UserDialogs.createEnablePhoneCallsDialog(getActivity(), new DialogInterface.OnClickListener() {
                /* class com.android.settings.users.$$Lambda$UserDetailsSettings$Lst_0g9V6Nd1dtjvTDuG3Fc4Gc */

                public final void onClick(DialogInterface dialogInterface, int i) {
                    UserDetailsSettings.this.lambda$onCreateDialog$1$UserDetailsSettings(dialogInterface, i);
                }
            });
        }
        if (i == 3) {
            return UserDialogs.createEnablePhoneCallsAndSmsDialog(getActivity(), new DialogInterface.OnClickListener() {
                /* class com.android.settings.users.$$Lambda$UserDetailsSettings$4cGmvHUO2YI0g3woyIsIOJtxew */

                public final void onClick(DialogInterface dialogInterface, int i) {
                    UserDetailsSettings.this.lambda$onCreateDialog$2$UserDetailsSettings(dialogInterface, i);
                }
            });
        }
        if (i == 4) {
            return UserDialogs.createSetupUserDialog(getActivity(), new DialogInterface.OnClickListener() {
                /* class com.android.settings.users.$$Lambda$UserDetailsSettings$Tt5fqN1bC8Z1BwnBOucrtjxj0Iw */

                public final void onClick(DialogInterface dialogInterface, int i) {
                    UserDetailsSettings.this.lambda$onCreateDialog$3$UserDetailsSettings(dialogInterface, i);
                }
            });
        }
        throw new IllegalArgumentException("Unsupported dialogId " + i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$0 */
    public /* synthetic */ void lambda$onCreateDialog$0$UserDetailsSettings(DialogInterface dialogInterface, int i) {
        removeUser();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$1 */
    public /* synthetic */ void lambda$onCreateDialog$1$UserDetailsSettings(DialogInterface dialogInterface, int i) {
        enableCallsAndSms(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$2 */
    public /* synthetic */ void lambda$onCreateDialog$2$UserDetailsSettings(DialogInterface dialogInterface, int i) {
        enableCallsAndSms(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$3 */
    public /* synthetic */ void lambda$onCreateDialog$3$UserDetailsSettings(DialogInterface dialogInterface, int i) {
        if (canSwitchUserNow()) {
            switchUser();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsPreferenceFragment
    public void showDialog(int i) {
        super.showDialog(i);
    }

    /* access modifiers changed from: package-private */
    public void initialize(Context context, Bundle bundle) {
        int i = bundle != null ? bundle.getInt("user_id", -10000) : -10000;
        if (i != -10000) {
            boolean z = bundle.getBoolean("new_user", false);
            this.mUserInfo = this.mUserManager.getUserInfo(i);
            this.mSwitchUserPref = (RestrictedPreference) findPreference("switch_user");
            this.mPhonePref = (SwitchPreference) findPreference("enable_calling");
            this.mRemoveUserPref = findPreference("remove_user");
            this.mAppAndContentAccessPref = findPreference("app_and_content_access");
            this.mSwitchUserPref.setTitle(context.getString(R$string.user_switch_to_user, this.mUserInfo.name));
            if (this.mUserCaps.mDisallowSwitchUser) {
                this.mSwitchUserPref.setDisabledByAdmin(RestrictedLockUtilsInternal.getDeviceOwner(context));
            } else {
                this.mSwitchUserPref.setDisabledByAdmin(null);
                this.mSwitchUserPref.setSelectable(true);
                this.mSwitchUserPref.setOnPreferenceClickListener(this);
            }
            if (!this.mUserManager.isAdminUser()) {
                removePreference("enable_calling");
                removePreference("remove_user");
                removePreference("app_and_content_access");
                return;
            }
            if (!Utils.isVoiceCapable(context)) {
                removePreference("enable_calling");
            }
            if (this.mUserInfo.isRestricted()) {
                removePreference("enable_calling");
                if (z) {
                    openAppAndContentAccessScreen(true);
                }
            } else {
                removePreference("app_and_content_access");
            }
            if (this.mUserInfo.isGuest()) {
                this.mPhonePref.setTitle(C0017R$string.user_enable_calling);
                Bundle defaultGuestRestrictions = this.mUserManager.getDefaultGuestRestrictions();
                this.mDefaultGuestRestrictions = defaultGuestRestrictions;
                this.mPhonePref.setChecked(!defaultGuestRestrictions.getBoolean("no_outgoing_calls"));
                this.mRemoveUserPref.setTitle(C0017R$string.user_exit_guest_title);
            } else {
                this.mPhonePref.setChecked(!this.mUserManager.hasUserRestriction("no_outgoing_calls", new UserHandle(i)));
                this.mRemoveUserPref.setTitle(C0017R$string.user_remove_user);
            }
            if (RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_remove_user", UserHandle.myUserId())) {
                removePreference("remove_user");
            }
            this.mRemoveUserPref.setOnPreferenceClickListener(this);
            this.mPhonePref.setOnPreferenceChangeListener(this);
            this.mAppAndContentAccessPref.setOnPreferenceClickListener(this);
            return;
        }
        throw new IllegalStateException("Arguments to this fragment must contain the user id");
    }

    /* access modifiers changed from: package-private */
    public boolean canDeleteUser() {
        FragmentActivity activity;
        if (!this.mUserManager.isAdminUser() || (activity = getActivity()) == null) {
            return false;
        }
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(activity, "no_remove_user", UserHandle.myUserId());
        if (checkIfRestrictionEnforced == null) {
            return true;
        }
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(activity, checkIfRestrictionEnforced);
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean canSwitchUserNow() {
        return this.mUserManager.getUserSwitchability() == 0;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:?, code lost:
        android.util.Log.e(com.android.settings.users.UserDetailsSettings.TAG, "Error while switching to other user.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001a, code lost:
        finishFragment();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001d, code lost:
        throw r0;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x0011 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void switchUser() {
        /*
            r2 = this;
            android.app.IActivityManager r0 = android.app.ActivityManager.getService()     // Catch:{ RemoteException -> 0x0011 }
            android.content.pm.UserInfo r1 = r2.mUserInfo     // Catch:{ RemoteException -> 0x0011 }
            int r1 = r1.id     // Catch:{ RemoteException -> 0x0011 }
            r0.switchUser(r1)     // Catch:{ RemoteException -> 0x0011 }
        L_0x000b:
            r2.finishFragment()
            goto L_0x0019
        L_0x000f:
            r0 = move-exception
            goto L_0x001a
        L_0x0011:
            java.lang.String r0 = com.android.settings.users.UserDetailsSettings.TAG     // Catch:{ all -> 0x000f }
            java.lang.String r1 = "Error while switching to other user."
            android.util.Log.e(r0, r1)     // Catch:{ all -> 0x000f }
            goto L_0x000b
        L_0x0019:
            return
        L_0x001a:
            r2.finishFragment()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.users.UserDetailsSettings.switchUser():void");
    }

    private void enableCallsAndSms(boolean z) {
        this.mPhonePref.setChecked(z);
        if (this.mUserInfo.isGuest()) {
            this.mDefaultGuestRestrictions.putBoolean("no_outgoing_calls", !z);
            this.mDefaultGuestRestrictions.putBoolean("no_sms", true);
            this.mUserManager.setDefaultGuestRestrictions(this.mDefaultGuestRestrictions);
            for (UserInfo userInfo : this.mUserManager.getUsers(true)) {
                if (userInfo.isGuest()) {
                    UserHandle of = UserHandle.of(userInfo.id);
                    for (String str : this.mDefaultGuestRestrictions.keySet()) {
                        this.mUserManager.setUserRestriction(str, this.mDefaultGuestRestrictions.getBoolean(str), of);
                    }
                }
            }
            return;
        }
        UserHandle of2 = UserHandle.of(this.mUserInfo.id);
        this.mUserManager.setUserRestriction("no_outgoing_calls", !z, of2);
        this.mUserManager.setUserRestriction("no_sms", !z, of2);
    }

    private void removeUser() {
        this.mUserManager.removeUser(this.mUserInfo.id);
        finishFragment();
    }

    private void openAppAndContentAccessScreen(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", this.mUserInfo.id);
        bundle.putBoolean("new_user", z);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(AppRestrictionsFragment.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setTitleRes(C0017R$string.user_restrictions_title);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }

    private boolean isSecondaryUser(UserInfo userInfo) {
        return "android.os.usertype.full.SECONDARY".equals(userInfo.userType);
    }

    private boolean shouldShowSetupPromptDialog() {
        return isSecondaryUser(this.mUserInfo) && !this.mUserInfo.isInitialized();
    }
}
