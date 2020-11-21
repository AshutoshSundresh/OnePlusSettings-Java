package com.android.settings.users;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.users.EditUserInfoController;
import com.android.settings.users.MultiUserSwitchBarController;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBarController;
import com.android.settingslib.R$string;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.Utils;
import com.android.settingslib.drawable.CircleFramedDrawable;
import com.android.settingslib.drawable.UserIcons;
import com.android.settingslib.utils.ThreadUtils;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class UserSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, MultiUserSwitchBarController.OnMultiUserSwitchChangedListener, DialogInterface.OnDismissListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.user_settings) {
        /* class com.android.settings.users.UserSettings.AnonymousClass15 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return UserCapabilities.create(context).mEnabled;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeysFromXml(Context context, int i, boolean z) {
            List<String> nonIndexableKeysFromXml = super.getNonIndexableKeysFromXml(context, i, z);
            new AddUserWhenLockedPreferenceController(context, "user_settings_add_users_when_locked").updateNonIndexableKeys(nonIndexableKeysFromXml);
            new AutoSyncDataPreferenceController(context, null).updateNonIndexableKeys(nonIndexableKeysFromXml);
            new AutoSyncPersonalDataPreferenceController(context, null).updateNonIndexableKeys(nonIndexableKeysFromXml);
            new AutoSyncWorkDataPreferenceController(context, null).updateNonIndexableKeys(nonIndexableKeysFromXml);
            return nonIndexableKeysFromXml;
        }
    };
    private static final IntentFilter USER_REMOVED_INTENT_FILTER;
    private static SparseArray<Bitmap> sDarkDefaultUserBitmapCache = new SparseArray<>();
    RestrictedPreference mAddGuest;
    RestrictedPreference mAddUser;
    private AddUserWhenLockedPreferenceController mAddUserWhenLockedPreferenceController;
    private boolean mAddingUser;
    private String mAddingUserName;
    private Drawable mDefaultIconDrawable;
    private EditUserInfoController mEditUserInfoController = new EditUserInfoController();
    private Handler mHandler = new Handler() {
        /* class com.android.settings.users.UserSettings.AnonymousClass1 */

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                UserSettings.this.updateUserList();
            } else if (i == 2) {
                UserSettings.this.onUserCreated(message.arg1);
            }
        }
    };
    UserPreference mMePreference;
    private MultiUserFooterPreferenceController mMultiUserFooterPreferenceController;
    private Drawable mPendingUserIcon;
    private CharSequence mPendingUserName;
    private int mRemovingUserId = -1;
    private boolean mShouldUpdateUserList = true;
    private MultiUserSwitchBarController mSwitchBarController;
    private UserCapabilities mUserCaps;
    private BroadcastReceiver mUserChangeReceiver = new BroadcastReceiver() {
        /* class com.android.settings.users.UserSettings.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            int intExtra;
            if (intent.getAction().equals("android.intent.action.USER_REMOVED")) {
                UserSettings.this.mRemovingUserId = -1;
            } else if (intent.getAction().equals("android.intent.action.USER_INFO_CHANGED") && (intExtra = intent.getIntExtra("android.intent.extra.user_handle", -1)) != -1) {
                UserSettings.this.mUserIcons.remove(intExtra);
            }
            UserSettings.this.mHandler.sendEmptyMessage(1);
        }
    };
    SparseArray<Bitmap> mUserIcons = new SparseArray<>();
    PreferenceGroup mUserListCategory;
    private final Object mUserLock = new Object();
    private UserManager mUserManager;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i == 1) {
            return 591;
        }
        if (i == 2) {
            return 595;
        }
        switch (i) {
            case 5:
                return 594;
            case 6:
                return 598;
            case 7:
                return 599;
            case 8:
                return 600;
            case 9:
            case 10:
            case 11:
                return 601;
            default:
                return 0;
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 96;
    }

    static {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.USER_REMOVED");
        USER_REMOVED_INTENT_FILTER = intentFilter;
        intentFilter.addAction("android.intent.action.USER_INFO_CHANGED");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SwitchBar switchBar = settingsActivity.getSwitchBar();
        this.mSwitchBarController = new MultiUserSwitchBarController(settingsActivity, new SwitchBarController(switchBar), this);
        getSettingsLifecycle().addObserver(this.mSwitchBarController);
        switchBar.show();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.user_settings);
        FragmentActivity activity = getActivity();
        if (!WizardManagerHelper.isDeviceProvisioned(activity)) {
            activity.finish();
            return;
        }
        this.mAddUserWhenLockedPreferenceController = new AddUserWhenLockedPreferenceController(activity, "user_settings_add_users_when_locked");
        this.mMultiUserFooterPreferenceController = new MultiUserFooterPreferenceController(activity, "multiuser_footer");
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mAddUserWhenLockedPreferenceController.displayPreference(preferenceScreen);
        this.mMultiUserFooterPreferenceController.displayPreference(preferenceScreen);
        preferenceScreen.findPreference(this.mAddUserWhenLockedPreferenceController.getPreferenceKey()).setOnPreferenceChangeListener(this.mAddUserWhenLockedPreferenceController);
        if (bundle != null) {
            if (bundle.containsKey("removing_user")) {
                this.mRemovingUserId = bundle.getInt("removing_user");
            }
            this.mEditUserInfoController.onRestoreInstanceState(bundle);
        }
        this.mUserCaps = UserCapabilities.create(activity);
        this.mUserManager = (UserManager) activity.getSystemService("user");
        if (this.mUserCaps.mEnabled) {
            int myUserId = UserHandle.myUserId();
            this.mUserListCategory = (PreferenceGroup) findPreference("user_list");
            UserPreference userPreference = new UserPreference(getPrefContext(), null, myUserId);
            this.mMePreference = userPreference;
            userPreference.setKey("user_me");
            this.mMePreference.setOnPreferenceClickListener(this);
            if (this.mUserCaps.mIsAdmin) {
                this.mMePreference.setSummary(C0017R$string.user_admin);
            }
            RestrictedPreference restrictedPreference = (RestrictedPreference) findPreference("guest_add");
            this.mAddGuest = restrictedPreference;
            restrictedPreference.setOnPreferenceClickListener(this);
            RestrictedPreference restrictedPreference2 = (RestrictedPreference) findPreference("user_add");
            this.mAddUser = restrictedPreference2;
            if (!this.mUserCaps.mCanAddRestrictedProfile) {
                restrictedPreference2.setTitle(C0017R$string.user_add_user_menu);
            }
            this.mAddUser.setOnPreferenceClickListener(this);
            activity.registerReceiverAsUser(this.mUserChangeReceiver, UserHandle.ALL, USER_REMOVED_INTENT_FILTER, null, this.mHandler);
            updateUI();
            this.mShouldUpdateUserList = false;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (this.mUserCaps.mEnabled) {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            AddUserWhenLockedPreferenceController addUserWhenLockedPreferenceController = this.mAddUserWhenLockedPreferenceController;
            addUserWhenLockedPreferenceController.updateState(preferenceScreen.findPreference(addUserWhenLockedPreferenceController.getPreferenceKey()));
            if (this.mShouldUpdateUserList) {
                updateUI();
            }
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        this.mShouldUpdateUserList = true;
        super.onPause();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        UserCapabilities userCapabilities = this.mUserCaps;
        if (userCapabilities != null && userCapabilities.mEnabled) {
            getActivity().unregisterReceiver(this.mUserChangeReceiver);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mEditUserInfoController.onSaveInstanceState(bundle);
        bundle.putInt("removing_user", this.mRemovingUserId);
    }

    @Override // androidx.fragment.app.Fragment
    public void startActivityForResult(Intent intent, int i) {
        this.mEditUserInfoController.startingActivityForResult();
        super.startActivityForResult(intent, i);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (!this.mUserCaps.mIsAdmin && canSwitchUserNow()) {
            String userName = this.mUserManager.getUserName();
            MenuItem add = menu.add(0, 1, 0, getResources().getString(C0017R$string.user_remove_user_menu, userName));
            add.setShowAsAction(0);
            RestrictedLockUtilsInternal.setMenuItemAsDisabledByAdmin(getContext(), add, RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getContext(), "no_remove_user", UserHandle.myUserId()));
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return super.onOptionsItemSelected(menuItem);
        }
        onRemoveUserClicked(UserHandle.myUserId());
        return true;
    }

    @Override // com.android.settings.users.MultiUserSwitchBarController.OnMultiUserSwitchChangedListener
    public void onMultiUserSwitchChanged(boolean z) {
        updateUI();
    }

    private void updateUI() {
        this.mUserCaps.updateAddUserCapabilities(getActivity());
        loadProfile();
        updateUserList();
    }

    private void loadProfile() {
        if (isCurrentUserGuest()) {
            this.mMePreference.setIcon(getEncircledDefaultIcon());
            this.mMePreference.setTitle(C0017R$string.user_exit_guest_title);
            this.mMePreference.setSelectable(true);
            this.mMePreference.setEnabled(canSwitchUserNow());
            return;
        }
        new AsyncTask<Void, Void, String>() {
            /* class com.android.settings.users.UserSettings.AnonymousClass3 */

            /* access modifiers changed from: protected */
            public void onPostExecute(String str) {
                UserSettings.this.finishLoadProfile(str);
            }

            /* access modifiers changed from: protected */
            public String doInBackground(Void... voidArr) {
                UserInfo userInfo = UserSettings.this.mUserManager.getUserInfo(UserHandle.myUserId());
                String str = userInfo.iconPath;
                if (str == null || str.equals("")) {
                    UserSettings.copyMeProfilePhoto(UserSettings.this.getActivity(), userInfo);
                }
                return userInfo.name;
            }
        }.execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void finishLoadProfile(String str) {
        if (getActivity() != null) {
            this.mMePreference.setTitle(getString(C0017R$string.user_you, str));
            int myUserId = UserHandle.myUserId();
            Bitmap userIcon = this.mUserManager.getUserIcon(myUserId);
            if (userIcon != null) {
                this.mMePreference.setIcon(encircle(userIcon));
                this.mUserIcons.put(myUserId, userIcon);
            }
        }
    }

    private boolean hasLockscreenSecurity() {
        return new LockPatternUtils(getActivity()).isSecure(UserHandle.myUserId());
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void launchChooseLockscreen() {
        Intent intent = new Intent("android.app.action.SET_NEW_PASSWORD");
        intent.putExtra("minimum_quality", 65536);
        startActivityForResult(intent, 10);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 10) {
            this.mEditUserInfoController.onActivityResult(i, i2, intent);
        } else if (i2 != 0 && hasLockscreenSecurity()) {
            addUserNow(2);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onAddUserClicked(int i) {
        synchronized (this.mUserLock) {
            if (this.mRemovingUserId == -1 && !this.mAddingUser) {
                if (i == 1) {
                    showDialog(2);
                } else if (i == 2) {
                    if (hasLockscreenSecurity()) {
                        showDialog(11);
                    } else {
                        showDialog(7);
                    }
                }
            }
        }
    }

    private void onRemoveUserClicked(int i) {
        synchronized (this.mUserLock) {
            if (this.mRemovingUserId == -1 && !this.mAddingUser) {
                this.mRemovingUserId = i;
                showDialog(1);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onUserCreated(int i) {
        this.mAddingUser = false;
        openUserDetails(this.mUserManager.getUserInfo(i), true);
    }

    private void openUserDetails(UserInfo userInfo, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", userInfo.id);
        bundle.putBoolean("new_user", z);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(UserDetailsSettings.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setTitleText(userInfo.name);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onDialogShowing() {
        super.onDialogShowing();
        setOnDismissListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        int i2;
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return null;
        }
        if (i == 1) {
            return UserDialogs.createRemoveDialog(getActivity(), this.mRemovingUserId, new DialogInterface.OnClickListener() {
                /* class com.android.settings.users.UserSettings.AnonymousClass4 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    UserSettings.this.removeUserNow();
                }
            });
        }
        if (i != 2) {
            switch (i) {
                case 5:
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(C0017R$string.user_cannot_manage_message);
                    builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
                    return builder.create();
                case 6:
                    ArrayList arrayList = new ArrayList();
                    HashMap hashMap = new HashMap();
                    hashMap.put("title", getString(R$string.user_add_user_item_title));
                    hashMap.put("summary", getString(R$string.user_add_user_item_summary));
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put("title", getString(R$string.user_add_profile_item_title));
                    hashMap2.put("summary", getString(R$string.user_add_profile_item_summary));
                    arrayList.add(hashMap);
                    arrayList.add(hashMap2);
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(activity);
                    SimpleAdapter simpleAdapter = new SimpleAdapter(builder2.getContext(), arrayList, C0012R$layout.two_line_list_item, new String[]{"title", "summary"}, new int[]{C0010R$id.title, C0010R$id.summary});
                    builder2.setTitle(R$string.user_add_user_type_title);
                    builder2.setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
                        /* class com.android.settings.users.UserSettings.AnonymousClass6 */

                        public void onClick(DialogInterface dialogInterface, int i) {
                            UserSettings.this.onAddUserClicked(i == 0 ? 1 : 2);
                        }
                    });
                    return builder2.create();
                case 7:
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(activity);
                    builder3.setMessage(R$string.user_need_lock_message);
                    builder3.setPositiveButton(R$string.user_set_lock_button, new DialogInterface.OnClickListener() {
                        /* class com.android.settings.users.UserSettings.AnonymousClass7 */

                        public void onClick(DialogInterface dialogInterface, int i) {
                            UserSettings.this.launchChooseLockscreen();
                        }
                    });
                    builder3.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
                    return builder3.create();
                case 8:
                    AlertDialog.Builder builder4 = new AlertDialog.Builder(activity);
                    builder4.setTitle(C0017R$string.user_exit_guest_confirm_title);
                    builder4.setMessage(C0017R$string.user_exit_guest_confirm_message);
                    builder4.setPositiveButton(C0017R$string.user_exit_guest_dialog_remove, new DialogInterface.OnClickListener() {
                        /* class com.android.settings.users.UserSettings.AnonymousClass8 */

                        public void onClick(DialogInterface dialogInterface, int i) {
                            UserSettings.this.exitGuest();
                        }
                    });
                    builder4.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
                    return builder4.create();
                case 9:
                    UserHandle myUserHandle = Process.myUserHandle();
                    UserInfo userInfo = this.mUserManager.getUserInfo(myUserHandle.getIdentifier());
                    return this.mEditUserInfoController.createDialog(this, Utils.getUserIcon(getPrefContext(), this.mUserManager, userInfo), userInfo.name, getString(R$string.profile_info_settings_title), new EditUserInfoController.OnContentChangedCallback() {
                        /* class com.android.settings.users.UserSettings.AnonymousClass9 */

                        @Override // com.android.settings.users.EditUserInfoController.OnContentChangedCallback
                        public void onPhotoChanged(final UserHandle userHandle, final Drawable drawable) {
                            ThreadUtils.postOnBackgroundThread(new Runnable() {
                                /* class com.android.settings.users.UserSettings.AnonymousClass9.AnonymousClass1 */

                                public void run() {
                                    UserSettings.this.mUserManager.setUserIcon(userHandle.getIdentifier(), UserIcons.convertToBitmap(drawable));
                                }
                            });
                            UserSettings.this.mMePreference.setIcon(drawable);
                        }

                        @Override // com.android.settings.users.EditUserInfoController.OnContentChangedCallback
                        public void onLabelChanged(UserHandle userHandle, CharSequence charSequence) {
                            UserSettings.this.mMePreference.setTitle(charSequence.toString());
                            UserSettings.this.mUserManager.setUserName(userHandle.getIdentifier(), charSequence.toString());
                        }
                    }, myUserHandle, null);
                case 10:
                    synchronized (this.mUserLock) {
                        this.mPendingUserIcon = UserIcons.getDefaultUserIcon(getPrefContext().getResources(), new Random(System.currentTimeMillis()).nextInt(8), false);
                        this.mPendingUserName = getString(R$string.user_new_user_name);
                    }
                    return buildAddUserProfileEditorDialog(1);
                case 11:
                    synchronized (this.mUserLock) {
                        this.mPendingUserIcon = UserIcons.getDefaultUserIcon(getPrefContext().getResources(), new Random(System.currentTimeMillis()).nextInt(8), false);
                        this.mPendingUserName = getString(R$string.user_new_profile_name);
                    }
                    return buildAddUserProfileEditorDialog(2);
                default:
                    return null;
            }
        } else {
            final SharedPreferences preferences = getActivity().getPreferences(0);
            final boolean z = preferences.getBoolean("key_add_user_long_message_displayed", false);
            if (z) {
                i2 = R$string.user_add_user_message_short;
            } else {
                i2 = R$string.user_add_user_message_long;
            }
            AlertDialog.Builder builder5 = new AlertDialog.Builder(activity);
            builder5.setTitle(R$string.user_add_user_title);
            builder5.setMessage(i2);
            builder5.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
                /* class com.android.settings.users.UserSettings.AnonymousClass5 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    UserSettings.this.showDialog(10);
                    if (!z) {
                        preferences.edit().putBoolean("key_add_user_long_message_displayed", true).apply();
                    }
                }
            });
            builder5.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            return builder5.create();
        }
    }

    private Dialog buildAddUserProfileEditorDialog(final int i) {
        int i2;
        Dialog createDialog;
        synchronized (this.mUserLock) {
            EditUserInfoController editUserInfoController = this.mEditUserInfoController;
            Drawable drawable = this.mPendingUserIcon;
            CharSequence charSequence = this.mPendingUserName;
            if (i == 1) {
                i2 = R$string.user_info_settings_title;
            } else {
                i2 = R$string.profile_info_settings_title;
            }
            createDialog = editUserInfoController.createDialog(this, drawable, charSequence, getString(i2), new EditUserInfoController.OnContentChangedCallback() {
                /* class com.android.settings.users.UserSettings.AnonymousClass10 */

                @Override // com.android.settings.users.EditUserInfoController.OnContentChangedCallback
                public void onPhotoChanged(UserHandle userHandle, Drawable drawable) {
                    UserSettings.this.mPendingUserIcon = drawable;
                }

                @Override // com.android.settings.users.EditUserInfoController.OnContentChangedCallback
                public void onLabelChanged(UserHandle userHandle, CharSequence charSequence) {
                    UserSettings.this.mPendingUserName = charSequence;
                }
            }, Process.myUserHandle(), new EditUserInfoController.OnDialogCompleteCallback() {
                /* class com.android.settings.users.UserSettings.AnonymousClass11 */

                @Override // com.android.settings.users.EditUserInfoController.OnDialogCompleteCallback
                public void onPositive() {
                    UserSettings.this.addUserNow(i);
                }

                @Override // com.android.settings.users.EditUserInfoController.OnDialogCompleteCallback
                public void onNegativeOrCancel() {
                    synchronized (UserSettings.this.mUserLock) {
                        UserSettings.this.mPendingUserIcon = null;
                        UserSettings.this.mPendingUserName = null;
                    }
                }
            });
        }
        return createDialog;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void removeUserNow() {
        if (this.mRemovingUserId == UserHandle.myUserId()) {
            removeThisUser();
        } else {
            ThreadUtils.postOnBackgroundThread(new Runnable() {
                /* class com.android.settings.users.UserSettings.AnonymousClass12 */

                public void run() {
                    synchronized (UserSettings.this.mUserLock) {
                        UserSettings.this.mUserManager.removeUser(UserSettings.this.mRemovingUserId);
                        UserSettings.this.mHandler.sendEmptyMessage(1);
                    }
                }
            });
        }
    }

    private void removeThisUser() {
        if (!canSwitchUserNow()) {
            Log.w("UserSettings", "Cannot remove current user when switching is disabled");
            return;
        }
        try {
            ActivityManager.getService().switchUser(0);
            ((UserManager) getContext().getSystemService(UserManager.class)).removeUser(UserHandle.myUserId());
        } catch (RemoteException unused) {
            Log.e("UserSettings", "Unable to remove self user");
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void addUserNow(final int i) {
        String str;
        synchronized (this.mUserLock) {
            this.mAddingUser = true;
            if (i == 1) {
                if (this.mPendingUserName != null) {
                    str = this.mPendingUserName.toString();
                } else {
                    str = getString(C0017R$string.user_new_user_name);
                }
            } else if (this.mPendingUserName != null) {
                str = this.mPendingUserName.toString();
            } else {
                str = getString(C0017R$string.user_new_profile_name);
            }
            this.mAddingUserName = str;
        }
        ThreadUtils.postOnBackgroundThread(new Runnable() {
            /* class com.android.settings.users.UserSettings.AnonymousClass13 */

            public void run() {
                String str;
                UserInfo userInfo;
                synchronized (UserSettings.this.mUserLock) {
                    str = UserSettings.this.mAddingUserName;
                }
                if (i == 1) {
                    userInfo = UserSettings.this.mUserManager.createUser(str, 0);
                } else {
                    userInfo = UserSettings.this.mUserManager.createRestrictedProfile(str);
                }
                synchronized (UserSettings.this.mUserLock) {
                    if (userInfo == null) {
                        UserSettings.this.mAddingUser = false;
                        UserSettings.this.mPendingUserIcon = null;
                        UserSettings.this.mPendingUserName = null;
                        return;
                    }
                    if (UserSettings.this.mPendingUserIcon != null) {
                        UserSettings.this.mUserManager.setUserIcon(userInfo.id, UserIcons.convertToBitmap(UserSettings.this.mPendingUserIcon));
                    }
                    if (i == 1) {
                        UserSettings.this.mHandler.sendEmptyMessage(1);
                    }
                    UserSettings.this.mHandler.sendMessage(UserSettings.this.mHandler.obtainMessage(2, userInfo.id, userInfo.serialNumber));
                    UserSettings.this.mPendingUserIcon = null;
                    UserSettings.this.mPendingUserName = null;
                }
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void exitGuest() {
        if (isCurrentUserGuest()) {
            removeThisUser();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateUserList() {
        UserPreference userPreference;
        UserPreference userPreference2;
        FragmentActivity activity = getActivity();
        if (activity != null) {
            List<UserInfo> users = this.mUserManager.getUsers(true);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(this.mMePreference);
            boolean z = this.mUserCaps.mIsAdmin || (canSwitchUserNow() && !this.mUserCaps.mDisallowSwitchUser);
            for (UserInfo userInfo : users) {
                if (userInfo.supportsSwitchToByUser()) {
                    if (userInfo.id == UserHandle.myUserId()) {
                        userPreference = this.mMePreference;
                    } else {
                        if (userInfo.isGuest()) {
                            userPreference2 = new UserPreference(getPrefContext(), null, userInfo.id);
                            userPreference2.setTitle(C0017R$string.user_guest);
                            userPreference2.setIcon(getEncircledDefaultIcon());
                            userPreference2.setKey("user_guest");
                            arrayList2.add(userPreference2);
                            userPreference2.setEnabled(z);
                            userPreference2.setSelectable(true);
                            if (this.mUserCaps.mDisallowSwitchUser) {
                                userPreference2.setDisabledByAdmin(RestrictedLockUtilsInternal.getDeviceOwner(activity));
                            } else {
                                userPreference2.setDisabledByAdmin(null);
                            }
                            userPreference2.setOnPreferenceClickListener(this);
                        } else {
                            userPreference2 = new UserPreference(getPrefContext(), null, userInfo.id);
                            userPreference2.setKey("id=" + userInfo.id);
                            arrayList2.add(userPreference2);
                            if (userInfo.isAdmin()) {
                                userPreference2.setSummary(C0017R$string.user_admin);
                            }
                            userPreference2.setTitle(userInfo.name);
                            userPreference2.setOnPreferenceClickListener(this);
                            userPreference2.setEnabled(z);
                            userPreference2.setSelectable(true);
                        }
                        userPreference = userPreference2;
                    }
                    if (userPreference != null) {
                        if (userInfo.id == UserHandle.myUserId() || userInfo.isGuest() || userInfo.isInitialized()) {
                            if (userInfo.isRestricted()) {
                                userPreference.setSummary(C0017R$string.user_summary_restricted_profile);
                            }
                        } else if (userInfo.isRestricted()) {
                            userPreference.setSummary(C0017R$string.user_summary_restricted_not_set_up);
                        } else {
                            userPreference.setSummary(C0017R$string.user_summary_not_set_up);
                            userPreference.setEnabled(!this.mUserCaps.mDisallowSwitchUser && canSwitchUserNow());
                        }
                        if (userInfo.iconPath == null) {
                            userPreference.setIcon(getEncircledDefaultIcon());
                        } else if (this.mUserIcons.get(userInfo.id) == null) {
                            arrayList.add(Integer.valueOf(userInfo.id));
                            userPreference.setIcon(getEncircledDefaultIcon());
                        } else {
                            setPhotoId(userPreference, userInfo);
                        }
                    }
                }
            }
            if (this.mAddingUser) {
                UserPreference userPreference3 = new UserPreference(getPrefContext(), null, -10);
                userPreference3.setEnabled(false);
                userPreference3.setTitle(this.mAddingUserName);
                userPreference3.setIcon(getEncircledDefaultIcon());
                arrayList2.add(userPreference3);
            }
            Collections.sort(arrayList2, UserPreference.SERIAL_NUMBER_COMPARATOR);
            getActivity().invalidateOptionsMenu();
            if (arrayList.size() > 0) {
                loadIconsAsync(arrayList);
            }
            if (this.mUserCaps.mCanAddRestrictedProfile) {
                this.mUserListCategory.setTitle(C0017R$string.user_list_title);
            } else {
                this.mUserListCategory.setTitle((CharSequence) null);
            }
            this.mUserListCategory.removeAll();
            this.mAddUserWhenLockedPreferenceController.updateState(getPreferenceScreen().findPreference(this.mAddUserWhenLockedPreferenceController.getPreferenceKey()));
            this.mMultiUserFooterPreferenceController.updateState(getPreferenceScreen().findPreference(this.mMultiUserFooterPreferenceController.getPreferenceKey()));
            this.mUserListCategory.setVisible(this.mUserCaps.mUserSwitcherEnabled);
            updateAddGuest(activity, users.stream().anyMatch($$Lambda$r_pzZf2EH57SXB6m9pn4NfJPfk.INSTANCE));
            updateAddUser(activity);
            if (this.mUserCaps.mUserSwitcherEnabled) {
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    UserPreference userPreference4 = (UserPreference) it.next();
                    userPreference4.setOrder(Integer.MAX_VALUE);
                    this.mUserListCategory.addPreference(userPreference4);
                }
            }
        }
    }

    private boolean isCurrentUserGuest() {
        return this.mUserCaps.mIsGuest;
    }

    private boolean canSwitchUserNow() {
        return this.mUserManager.getUserSwitchability() == 0;
    }

    private void updateAddGuest(Context context, boolean z) {
        if (z || !this.mUserCaps.mCanAddGuest || !WizardManagerHelper.isDeviceProvisioned(context) || !this.mUserCaps.mUserSwitcherEnabled) {
            this.mAddGuest.setVisible(false);
            return;
        }
        this.mAddGuest.setVisible(true);
        this.mAddGuest.setIcon(getEncircledDefaultIcon());
        this.mAddGuest.setEnabled(canSwitchUserNow());
        this.mAddGuest.setSelectable(true);
    }

    private void updateAddUser(Context context) {
        UserCapabilities userCapabilities = this.mUserCaps;
        if ((userCapabilities.mCanAddUser || userCapabilities.mDisallowAddUserSetByAdmin) && WizardManagerHelper.isDeviceProvisioned(context) && this.mUserCaps.mUserSwitcherEnabled) {
            this.mAddUser.setVisible(true);
            this.mAddUser.setSelectable(true);
            boolean canAddMoreUsers = this.mUserManager.canAddMoreUsers();
            this.mAddUser.setEnabled(canAddMoreUsers && !this.mAddingUser && canSwitchUserNow());
            RestrictedLockUtils.EnforcedAdmin enforcedAdmin = null;
            if (!canAddMoreUsers) {
                this.mAddUser.setSummary(getString(C0017R$string.user_add_max_count, Integer.valueOf(getRealUsersCount())));
            } else {
                this.mAddUser.setSummary((CharSequence) null);
            }
            if (this.mAddUser.isEnabled()) {
                RestrictedPreference restrictedPreference = this.mAddUser;
                UserCapabilities userCapabilities2 = this.mUserCaps;
                if (userCapabilities2.mDisallowAddUser) {
                    enforcedAdmin = userCapabilities2.mEnforcedAdmin;
                }
                restrictedPreference.setDisabledByAdmin(enforcedAdmin);
                return;
            }
            return;
        }
        this.mAddUser.setVisible(false);
    }

    /* access modifiers changed from: package-private */
    public int getRealUsersCount() {
        return (int) this.mUserManager.getUsers().stream().filter($$Lambda$UserSettings$lGCqaYnDkJhYWSs9qTkpFiei7yE.INSTANCE).count();
    }

    static /* synthetic */ boolean lambda$getRealUsersCount$0(UserInfo userInfo) {
        return !userInfo.isGuest() && !userInfo.isProfile();
    }

    private void loadIconsAsync(List<Integer> list) {
        new AsyncTask<List<Integer>, Void, Void>() {
            /* class com.android.settings.users.UserSettings.AnonymousClass14 */

            /* access modifiers changed from: protected */
            public void onPostExecute(Void r1) {
                UserSettings.this.updateUserList();
            }

            /* access modifiers changed from: protected */
            public Void doInBackground(List<Integer>... listArr) {
                for (Integer num : listArr[0]) {
                    int intValue = num.intValue();
                    Bitmap userIcon = UserSettings.this.mUserManager.getUserIcon(intValue);
                    if (userIcon == null) {
                        userIcon = UserSettings.getDefaultUserIconAsBitmap(UserSettings.this.getContext().getResources(), intValue);
                    }
                    UserSettings.this.mUserIcons.append(intValue, userIcon);
                }
                return null;
            }
        }.execute(list);
    }

    private Drawable getEncircledDefaultIcon() {
        if (this.mDefaultIconDrawable == null) {
            this.mDefaultIconDrawable = encircle(getDefaultUserIconAsBitmap(getContext().getResources(), -10000));
        }
        return this.mDefaultIconDrawable;
    }

    private void setPhotoId(Preference preference, UserInfo userInfo) {
        Bitmap bitmap = this.mUserIcons.get(userInfo.id);
        if (bitmap != null) {
            preference.setIcon(encircle(bitmap));
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mMePreference) {
            if (isCurrentUserGuest()) {
                showDialog(8);
            } else {
                showDialog(9);
            }
            return true;
        } else if (preference instanceof UserPreference) {
            openUserDetails(this.mUserManager.getUserInfo(((UserPreference) preference).getUserId()), false);
            return true;
        } else if (preference == this.mAddUser) {
            if (this.mUserCaps.mCanAddRestrictedProfile) {
                showDialog(6);
            } else {
                onAddUserClicked(1);
            }
            return true;
        } else if (preference != this.mAddGuest) {
            return false;
        } else {
            openUserDetails(this.mUserManager.createGuest(getContext(), getString(R$string.user_guest)), true);
            return true;
        }
    }

    private Drawable encircle(Bitmap bitmap) {
        return CircleFramedDrawable.getInstance(getActivity(), bitmap);
    }

    public void onDismiss(DialogInterface dialogInterface) {
        synchronized (this.mUserLock) {
            this.mRemovingUserId = -1;
            updateUserList();
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_users;
    }

    /* access modifiers changed from: private */
    public static Bitmap getDefaultUserIconAsBitmap(Resources resources, int i) {
        Bitmap bitmap = sDarkDefaultUserBitmapCache.get(i);
        if (bitmap != null) {
            return bitmap;
        }
        Bitmap convertToBitmap = UserIcons.convertToBitmap(UserIcons.getDefaultUserIcon(resources, i, false));
        sDarkDefaultUserBitmapCache.put(i, convertToBitmap);
        return convertToBitmap;
    }

    static boolean assignDefaultPhoto(Context context, int i) {
        if (context == null) {
            return false;
        }
        ((UserManager) context.getSystemService("user")).setUserIcon(i, getDefaultUserIconAsBitmap(context.getResources(), i));
        return true;
    }

    static void copyMeProfilePhoto(Context context, UserInfo userInfo) {
        Uri uri = ContactsContract.Profile.CONTENT_URI;
        int myUserId = userInfo != null ? userInfo.id : UserHandle.myUserId();
        InputStream openContactPhotoInputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri, true);
        if (openContactPhotoInputStream == null) {
            assignDefaultPhoto(context, myUserId);
            return;
        }
        ((UserManager) context.getSystemService("user")).setUserIcon(myUserId, BitmapFactory.decodeStream(openContactPhotoInputStream));
        try {
            openContactPhotoInputStream.close();
        } catch (IOException unused) {
        }
    }
}
