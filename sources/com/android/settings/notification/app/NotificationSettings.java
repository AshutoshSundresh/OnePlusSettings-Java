package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.role.RoleManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.notification.ConversationIconFactory;
import java.util.ArrayList;
import java.util.List;

public abstract class NotificationSettings extends DashboardFragment {
    private static final boolean DEBUG = Log.isLoggable("NotifiSettingsBase", 3);
    protected NotificationBackend.AppRow mAppRow;
    protected Bundle mArgs;
    protected NotificationBackend mBackend = new NotificationBackend();
    protected NotificationChannel mChannel;
    protected NotificationChannelGroup mChannelGroup;
    protected Context mContext;
    protected List<NotificationPreferenceController> mControllers = new ArrayList();
    protected Drawable mConversationDrawable;
    protected ShortcutInfo mConversationInfo;
    protected DependentFieldListener mDependentFieldListener = new DependentFieldListener();
    protected IPackageManager mIPm;
    protected String mInstantAppPKG;
    protected Intent mIntent;
    protected boolean mListeningToPackageRemove;
    protected final BroadcastReceiver mPackageRemovedReceiver = new BroadcastReceiver() {
        /* class com.android.settings.notification.app.NotificationSettings.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
            PackageInfo packageInfo = NotificationSettings.this.mPkgInfo;
            if (packageInfo == null || TextUtils.equals(packageInfo.packageName, schemeSpecificPart)) {
                if (NotificationSettings.DEBUG) {
                    Log.d("NotifiSettingsBase", "Package (" + schemeSpecificPart + ") removed. RemovingNotificationSettingsBase.");
                }
                NotificationSettings.this.onPackageRemoved();
            }
        }
    };
    protected String mPkg;
    protected PackageInfo mPkgInfo;
    protected PackageManager mPm;
    protected RoleManager mRm;
    protected boolean mShowLegacyChannelConfig = false;
    protected RestrictedLockUtils.EnforcedAdmin mSuspendedAppsAdmin;
    protected int mUid;
    protected int mUserId;

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        String str;
        int i;
        super.onAttach(context);
        this.mContext = getActivity();
        this.mIntent = getActivity().getIntent();
        this.mArgs = getArguments();
        this.mPm = getPackageManager();
        NotificationManager.from(this.mContext);
        this.mRm = (RoleManager) this.mContext.getSystemService(RoleManager.class);
        this.mIPm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        Bundle bundle = this.mArgs;
        if (bundle == null || !bundle.containsKey("package")) {
            str = this.mIntent.getStringExtra("android.provider.extra.APP_PACKAGE");
        } else {
            str = this.mArgs.getString("package");
        }
        this.mPkg = str;
        Bundle bundle2 = this.mArgs;
        if (bundle2 == null || !bundle2.containsKey("uid")) {
            i = this.mIntent.getIntExtra("app_uid", -1);
        } else {
            i = this.mArgs.getInt("uid");
        }
        this.mUid = i;
        Bundle bundle3 = this.mArgs;
        String string = (bundle3 == null || !bundle3.containsKey("arg_instant_package_name")) ? "" : this.mArgs.getString("arg_instant_package_name");
        this.mInstantAppPKG = string;
        if (!TextUtils.isEmpty(string)) {
            this.mBackend = new NotificationBackend(this.mInstantAppPKG);
        }
        if (this.mUid < 0) {
            try {
                this.mUid = this.mPm.getPackageUid(this.mPkg, 0);
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        PackageInfo findPackageInfo = findPackageInfo(this.mPkg, this.mUid);
        this.mPkgInfo = findPackageInfo;
        if (findPackageInfo != null) {
            int userId = UserHandle.getUserId(this.mUid);
            this.mUserId = userId;
            this.mSuspendedAppsAdmin = RestrictedLockUtilsInternal.checkIfApplicationIsSuspended(this.mContext, this.mPkg, userId);
            loadChannel();
            loadAppRow();
            loadChannelGroup();
            collectConfigActivities();
            if (use(HeaderPreferenceController.class) != null) {
                getSettingsLifecycle().addObserver((LifecycleObserver) use(HeaderPreferenceController.class));
            }
            if (use(ConversationHeaderPreferenceController.class) != null) {
                getSettingsLifecycle().addObserver((LifecycleObserver) use(ConversationHeaderPreferenceController.class));
            }
            for (NotificationPreferenceController notificationPreferenceController : this.mControllers) {
                notificationPreferenceController.onResume(this.mAppRow, this.mChannel, this.mChannelGroup, null, null, this.mSuspendedAppsAdmin, this.mInstantAppPKG);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mIntent == null && this.mArgs == null) {
            Log.w("NotifiSettingsBase", "No intent");
            toastAndFinish();
        } else if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null) {
            Log.w("NotifiSettingsBase", "Missing package or uid or packageinfo");
            toastAndFinish();
        } else {
            startListeningToPackageRemove();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        stopListeningToPackageRemove();
        super.onDestroy();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null || this.mAppRow == null) {
            Log.w("NotifiSettingsBase", "Missing package or uid or packageinfo");
            finish();
            return;
        }
        loadAppRow();
        if (this.mAppRow == null) {
            Log.w("NotifiSettingsBase", "Can't load package");
            finish();
            return;
        }
        loadChannel();
        loadConversation();
        loadChannelGroup();
        collectConfigActivities();
    }

    private void loadChannel() {
        Intent intent = getActivity().getIntent();
        String str = null;
        String stringExtra = intent != null ? intent.getStringExtra("android.provider.extra.CHANNEL_ID") : null;
        if (stringExtra == null && intent != null) {
            Bundle bundleExtra = intent.getBundleExtra(":settings:show_fragment_args");
            stringExtra = bundleExtra != null ? bundleExtra.getString("android.provider.extra.CHANNEL_ID") : null;
        }
        if (intent != null) {
            str = intent.getStringExtra("android.provider.extra.CONVERSATION_ID");
        }
        this.mChannel = this.mBackend.getChannel(this.mPkg, this.mUid, stringExtra, str);
    }

    private void loadConversation() {
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel != null && !TextUtils.isEmpty(notificationChannel.getConversationId()) && !this.mChannel.isDemoted()) {
            ShortcutInfo conversationInfo = this.mBackend.getConversationInfo(this.mContext, this.mPkg, this.mUid, this.mChannel.getConversationId());
            this.mConversationInfo = conversationInfo;
            if (conversationInfo != null) {
                NotificationBackend notificationBackend = this.mBackend;
                Context context = this.mContext;
                NotificationBackend.AppRow appRow = this.mAppRow;
                this.mConversationDrawable = notificationBackend.getConversationDrawable(context, conversationInfo, appRow.pkg, appRow.uid, this.mChannel.isImportantConversation());
            }
        }
    }

    private void loadAppRow() {
        this.mAppRow = this.mBackend.loadAppRow(this.mContext, this.mPm, this.mRm, this.mPkgInfo);
    }

    private void loadChannelGroup() {
        NotificationChannelGroup group;
        NotificationChannel notificationChannel;
        NotificationBackend notificationBackend = this.mBackend;
        NotificationBackend.AppRow appRow = this.mAppRow;
        boolean z = notificationBackend.onlyHasDefaultChannel(appRow.pkg, appRow.uid) || ((notificationChannel = this.mChannel) != null && "miscellaneous".equals(notificationChannel.getId()));
        this.mShowLegacyChannelConfig = z;
        if (z) {
            NotificationBackend notificationBackend2 = this.mBackend;
            NotificationBackend.AppRow appRow2 = this.mAppRow;
            this.mChannel = notificationBackend2.getChannel(appRow2.pkg, appRow2.uid, "miscellaneous", null);
        }
        NotificationChannel notificationChannel2 = this.mChannel;
        if (notificationChannel2 != null && !TextUtils.isEmpty(notificationChannel2.getGroup()) && (group = this.mBackend.getGroup(this.mPkg, this.mUid, this.mChannel.getGroup())) != null) {
            this.mChannelGroup = group;
        }
    }

    /* access modifiers changed from: protected */
    public void toastAndFinish() {
        Toast.makeText(this.mContext, C0017R$string.app_not_found_dlg_text, 0).show();
        getActivity().finish();
    }

    /* access modifiers changed from: protected */
    public void collectConfigActivities() {
        Intent intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.NOTIFICATION_PREFERENCES").setPackage(this.mAppRow.pkg);
        List<ResolveInfo> queryIntentActivities = this.mPm.queryIntentActivities(intent, 0);
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("Found ");
            sb.append(queryIntentActivities.size());
            sb.append(" preference activities");
            sb.append(queryIntentActivities.size() == 0 ? " ;_;" : "");
            Log.d("NotifiSettingsBase", sb.toString());
        }
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            NotificationBackend.AppRow appRow = this.mAppRow;
            if (appRow.settingsIntent == null) {
                appRow.settingsIntent = intent.setPackage(null).setClassName(activityInfo.packageName, activityInfo.name);
                NotificationChannel notificationChannel = this.mChannel;
                if (notificationChannel != null) {
                    this.mAppRow.settingsIntent.putExtra("android.intent.extra.CHANNEL_ID", notificationChannel.getId());
                }
                NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
                if (notificationChannelGroup != null) {
                    this.mAppRow.settingsIntent.putExtra("android.intent.extra.CHANNEL_GROUP_ID", notificationChannelGroup.getId());
                }
            } else if (DEBUG) {
                Log.d("NotifiSettingsBase", "Ignoring duplicate notification preference activity (" + activityInfo.name + ") for package " + activityInfo.packageName);
            }
        }
    }

    private PackageInfo findPackageInfo(String str, int i) {
        String[] packagesForUid;
        if (!(str == null || i < 0 || (packagesForUid = this.mPm.getPackagesForUid(i)) == null || str == null)) {
            for (String str2 : packagesForUid) {
                if (str.equals(str2)) {
                    try {
                        return this.mIPm.getPackageInfo(str, 4194368, UserHandle.getUserId(this.mUid));
                    } catch (RemoteException e) {
                        Log.w("NotifiSettingsBase", "Failed to load package " + str, e);
                    }
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void startListeningToPackageRemove() {
        if (!this.mListeningToPackageRemove) {
            this.mListeningToPackageRemove = true;
            IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addDataScheme("package");
            getContext().registerReceiver(this.mPackageRemovedReceiver, intentFilter);
        }
    }

    /* access modifiers changed from: protected */
    public void stopListeningToPackageRemove() {
        if (this.mListeningToPackageRemove) {
            this.mListeningToPackageRemove = false;
            getContext().unregisterReceiver(this.mPackageRemovedReceiver);
        }
    }

    /* access modifiers changed from: protected */
    public void onPackageRemoved() {
        getActivity().finishAndRemoveTask();
    }

    /* access modifiers changed from: protected */
    public class DependentFieldListener {
        protected DependentFieldListener() {
        }

        /* access modifiers changed from: protected */
        public void onFieldValueChanged() {
            NotificationSettings notificationSettings = NotificationSettings.this;
            Drawable drawable = notificationSettings.mConversationDrawable;
            if (drawable != null && (drawable instanceof ConversationIconFactory.ConversationIconDrawable)) {
                ((ConversationIconFactory.ConversationIconDrawable) drawable).setImportant(notificationSettings.mChannel.isImportantConversation());
            }
            PreferenceScreen preferenceScreen = NotificationSettings.this.getPreferenceScreen();
            for (NotificationPreferenceController notificationPreferenceController : NotificationSettings.this.mControllers) {
                notificationPreferenceController.displayPreference(preferenceScreen);
            }
            NotificationSettings.this.updatePreferenceStates();
        }

        /* access modifiers changed from: protected */
        public void onImportanceChangedForInstant() {
            PreferenceScreen preferenceScreen = NotificationSettings.this.getPreferenceScreen();
            for (NotificationPreferenceController notificationPreferenceController : NotificationSettings.this.mControllers) {
                notificationPreferenceController.displayPreference(preferenceScreen);
            }
            NotificationSettings.this.updatePreferenceStates();
        }
    }
}
