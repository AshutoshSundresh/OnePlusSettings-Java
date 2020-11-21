package com.android.settingslib.applications;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.Application;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.utils.ThreadUtils;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class ApplicationsState {
    public static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass2 */
        private final Collator sCollator = Collator.getInstance();

        public int compare(AppEntry appEntry, AppEntry appEntry2) {
            ApplicationInfo applicationInfo;
            int compare;
            int compare2 = this.sCollator.compare(appEntry.label, appEntry2.label);
            if (compare2 != 0) {
                return compare2;
            }
            ApplicationInfo applicationInfo2 = appEntry.info;
            if (applicationInfo2 == null || (applicationInfo = appEntry2.info) == null || (compare = this.sCollator.compare(applicationInfo2.packageName, applicationInfo.packageName)) == 0) {
                return appEntry.info.uid - appEntry2.info.uid;
            }
            return compare;
        }
    };
    public static final Comparator<AppEntry> EXTERNAL_SIZE_COMPARATOR = new Comparator<AppEntry>() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass6 */

        public int compare(AppEntry appEntry, AppEntry appEntry2) {
            long j = appEntry.externalSize;
            long j2 = appEntry2.externalSize;
            if (j < j2) {
                return 1;
            }
            if (j > j2) {
                return -1;
            }
            return ApplicationsState.ALPHA_COMPARATOR.compare(appEntry, appEntry2);
        }
    };
    public static final AppFilter FILTER_ALL_ENABLED = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass15 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            ApplicationInfo applicationInfo = appEntry.info;
            return applicationInfo.enabled && !AppUtils.isInstant(applicationInfo);
        }
    };
    public static final AppFilter FILTER_AUDIO = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass20 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            boolean z;
            synchronized (appEntry) {
                z = true;
                if (appEntry.info.category != 1) {
                    z = false;
                }
            }
            return z;
        }
    };
    public static final AppFilter FILTER_DISABLED = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass13 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            ApplicationInfo applicationInfo = appEntry.info;
            return !applicationInfo.enabled && !AppUtils.isInstant(applicationInfo);
        }
    };
    public static final AppFilter FILTER_DOWNLOADED_AND_LAUNCHER = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass10 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            if (AppUtils.isInstant(appEntry.info)) {
                return false;
            }
            if (ApplicationsState.hasFlag(appEntry.info.flags, 128) || !ApplicationsState.hasFlag(appEntry.info.flags, 1) || appEntry.hasLauncherEntry) {
                return true;
            }
            if (!ApplicationsState.hasFlag(appEntry.info.flags, 1) || !appEntry.isHomeApp) {
                return false;
            }
            return true;
        }
    };
    public static final AppFilter FILTER_DOWNLOADED_AND_LAUNCHER_AND_INSTANT = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass11 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            return AppUtils.isInstant(appEntry.info) || ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER.filterApp(appEntry);
        }
    };
    public static final AppFilter FILTER_EVERYTHING = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass16 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            return true;
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }
    };
    public static final AppFilter FILTER_GAMES = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass19 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            boolean z;
            synchronized (appEntry.info) {
                if (!ApplicationsState.hasFlag(appEntry.info.flags, 33554432)) {
                    if (appEntry.info.category != 0) {
                        z = false;
                    }
                }
                z = true;
            }
            return z;
        }
    };
    public static final AppFilter FILTER_INSTANT = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass14 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            return AppUtils.isInstant(appEntry.info);
        }
    };
    public static final AppFilter FILTER_MOVIES = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass21 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            boolean z;
            synchronized (appEntry) {
                z = appEntry.info.category == 2;
            }
            return z;
        }
    };
    public static final AppFilter FILTER_NOT_HIDE = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass18 */
        private String[] mHidePackageNames;

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init(Context context) {
            this.mHidePackageNames = context.getResources().getStringArray(17236043);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            if (!ArrayUtils.contains(this.mHidePackageNames, appEntry.info.packageName)) {
                return true;
            }
            ApplicationInfo applicationInfo = appEntry.info;
            if (applicationInfo.enabled && applicationInfo.enabledSetting != 4) {
                return true;
            }
            return false;
        }
    };
    public static final AppFilter FILTER_OTHER_APPS = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass23 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            boolean z;
            synchronized (appEntry) {
                if (!ApplicationsState.FILTER_AUDIO.filterApp(appEntry) && !ApplicationsState.FILTER_GAMES.filterApp(appEntry) && !ApplicationsState.FILTER_MOVIES.filterApp(appEntry)) {
                    if (!ApplicationsState.FILTER_PHOTOS.filterApp(appEntry)) {
                        z = false;
                    }
                }
                z = true;
            }
            return !z;
        }
    };
    public static final AppFilter FILTER_PERSONAL = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass7 */
        private int mCurrentUser;

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
            this.mCurrentUser = ActivityManager.getCurrentUser();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            return UserHandle.getUserId(appEntry.info.uid) == this.mCurrentUser;
        }
    };
    public static final AppFilter FILTER_PHOTOS = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass22 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            boolean z;
            synchronized (appEntry) {
                z = appEntry.info.category == 3;
            }
            return z;
        }
    };
    public static final AppFilter FILTER_WITHOUT_DISABLED_UNTIL_USED = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass8 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            return appEntry.info.enabledSetting != 4;
        }
    };
    public static final AppFilter FILTER_WITH_DOMAIN_URLS = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass17 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            return !AppUtils.isInstant(appEntry.info) && ApplicationsState.hasFlag(appEntry.info.privateFlags, 16);
        }
    };
    public static final AppFilter FILTER_WORK = new AppFilter() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass9 */
        private int mCurrentUser;

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
            this.mCurrentUser = ActivityManager.getCurrentUser();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            return UserHandle.getUserId(appEntry.info.uid) != this.mCurrentUser;
        }
    };
    public static final Comparator<AppEntry> INTERNAL_SIZE_COMPARATOR = new Comparator<AppEntry>() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass4 */

        public int compare(AppEntry appEntry, AppEntry appEntry2) {
            long j = appEntry.internalSize;
            long j2 = appEntry2.internalSize;
            if (j < j2) {
                return 1;
            }
            if (j > j2) {
                return -1;
            }
            return ApplicationsState.ALPHA_COMPARATOR.compare(appEntry, appEntry2);
        }
    };
    public static final Comparator<AppEntry> SIZE_COMPARATOR = new Comparator<AppEntry>() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass3 */

        public int compare(AppEntry appEntry, AppEntry appEntry2) {
            long j = appEntry.size;
            long j2 = appEntry2.size;
            if (j < j2) {
                return 1;
            }
            if (j > j2) {
                return -1;
            }
            return ApplicationsState.ALPHA_COMPARATOR.compare(appEntry, appEntry2);
        }
    };
    private static final ArrayList<String> THEME_OVERLAY_CATEGORYS = new ArrayList<String>() {
        /* class com.android.settingslib.applications.ApplicationsState.AnonymousClass1 */

        {
            add("oneplus_basiccolor_black");
            add("oneplus_basiccolor_white");
            add("oneplus_aodnotification_gold");
            add("oneplus_aodnotification_purple");
            add("oneplus_aodnotification_red");
            add("oneplus_shape_circle");
            add("oneplus_shape_roundedrect");
            add("oneplus_shape_square");
            add("oneplus_shape_squircle");
            add("oneplus_shape_teardrop");
        }
    };
    static ApplicationsState sInstance;
    private static final Object sLock = new Object();
    final ArrayList<WeakReference<Session>> mActiveSessions = new ArrayList<>();
    final int mAdminRetrieveFlags;
    final ArrayList<AppEntry> mAppEntries = new ArrayList<>();
    List<ApplicationInfo> mApplications = new ArrayList();
    final BackgroundHandler mBackgroundHandler;
    final Context mContext;
    String mCurComputingSizePkg;
    int mCurComputingSizeUserId;
    UUID mCurComputingSizeUuid;
    long mCurId = 1;
    final SparseArray<HashMap<String, AppEntry>> mEntriesMap = new SparseArray<>();
    boolean mHaveDisabledApps;
    boolean mHaveInstantApps;
    private InterestingConfigChanges mInterestingConfigChanges = new InterestingConfigChanges();
    final IPackageManager mIpm;
    final MainHandler mMainHandler = new MainHandler(Looper.getMainLooper());
    PackageIntentReceiver mPackageIntentReceiver;
    final PackageManager mPm;
    final ArrayList<Session> mRebuildingSessions = new ArrayList<>();
    boolean mResumed;
    final int mRetrieveFlags;
    final ArrayList<Session> mSessions = new ArrayList<>();
    boolean mSessionsChanged;
    final StorageStatsManager mStats;
    final HashMap<String, Boolean> mSystemModules = new HashMap<>();
    final HashMap<String, Boolean> mSystemPersistApplications = new HashMap<>();
    final HandlerThread mThread;
    final UserManager mUm;

    public interface Callbacks {
        void onAllSizesComputed();

        void onLauncherInfoChanged();

        void onLoadEntriesCompleted();

        void onPackageIconChanged();

        void onPackageListChanged();

        void onPackageSizeChanged(String str);

        void onRebuildComplete(ArrayList<AppEntry> arrayList);

        void onRunningStateChanged(boolean z);
    }

    public static class SizeInfo {
        public long cacheSize;
        public long codeSize;
        public long dataSize;
        public long externalCacheSize;
        public long externalCodeSize;
        public long externalDataSize;
    }

    /* access modifiers changed from: private */
    public static boolean hasFlag(int i, int i2) {
        return (i & i2) != 0;
    }

    static {
        Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    }

    public static ApplicationsState getInstance(Application application) {
        return getInstance(application, AppGlobals.getPackageManager());
    }

    static ApplicationsState getInstance(Application application, IPackageManager iPackageManager) {
        ApplicationsState applicationsState;
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new ApplicationsState(application, iPackageManager);
            }
            applicationsState = sInstance;
        }
        return applicationsState;
    }

    /* access modifiers changed from: package-private */
    public void setInterestingConfigChanges(InterestingConfigChanges interestingConfigChanges) {
        this.mInterestingConfigChanges = interestingConfigChanges;
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:20:0x0118 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private ApplicationsState(android.app.Application r8, android.content.pm.IPackageManager r9) {
        /*
        // Method dump skipped, instructions count: 284
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.<init>(android.app.Application, android.content.pm.IPackageManager):void");
    }

    private void addForceStopWhiteList(ApplicationInfo applicationInfo) {
        this.mSystemPersistApplications.put(applicationInfo.packageName, Boolean.TRUE);
    }

    private boolean getSystemPersistMetaData(ApplicationInfo applicationInfo) {
        try {
            ApplicationInfo applicationInfo2 = this.mPm.getApplicationInfo(applicationInfo.packageName, 128);
            if (applicationInfo2 == null || applicationInfo2.metaData == null) {
                return false;
            }
            return applicationInfo2.metaData.getBoolean("persist");
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private boolean isSystemApp(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 1) != 0;
    }

    public Looper getBackgroundLooper() {
        return this.mThread.getLooper();
    }

    public Session newSession(Callbacks callbacks) {
        return newSession(callbacks, null);
    }

    public Session newSession(Callbacks callbacks, Lifecycle lifecycle) {
        Session session = new Session(callbacks, lifecycle);
        synchronized (this.mEntriesMap) {
            this.mSessions.add(session);
        }
        return session;
    }

    /* access modifiers changed from: package-private */
    public void doResumeIfNeededLocked() {
        if (!this.mResumed) {
            this.mResumed = true;
            if (this.mPackageIntentReceiver == null) {
                PackageIntentReceiver packageIntentReceiver = new PackageIntentReceiver();
                this.mPackageIntentReceiver = packageIntentReceiver;
                packageIntentReceiver.registerReceiver();
            }
            List<ApplicationInfo> list = this.mApplications;
            this.mApplications = new ArrayList();
            for (UserInfo userInfo : this.mUm.getProfiles(UserHandle.myUserId())) {
                try {
                    if (this.mEntriesMap.indexOfKey(userInfo.id) < 0) {
                        this.mEntriesMap.put(userInfo.id, new HashMap<>());
                    }
                    this.mApplications.addAll(this.mIpm.getInstalledApplications(userInfo.isAdmin() ? this.mAdminRetrieveFlags : this.mRetrieveFlags, userInfo.id).getList());
                } catch (Exception e) {
                    Log.e("ApplicationsState", "Error during doResumeIfNeededLocked", e);
                }
            }
            int i = 0;
            if (this.mInterestingConfigChanges.applyNewConfig(this.mContext.getResources())) {
                clearEntries();
            } else {
                for (int i2 = 0; i2 < this.mAppEntries.size(); i2++) {
                    this.mAppEntries.get(i2).sizeStale = true;
                }
            }
            this.mHaveDisabledApps = false;
            this.mHaveInstantApps = false;
            while (i < this.mApplications.size()) {
                ApplicationInfo applicationInfo = this.mApplications.get(i);
                if (!applicationInfo.enabled) {
                    if (applicationInfo.enabledSetting != 3) {
                        this.mApplications.remove(i);
                        i--;
                        i++;
                    } else {
                        this.mHaveDisabledApps = true;
                    }
                }
                if (isHiddenModule(applicationInfo.packageName)) {
                    this.mApplications.remove(i);
                    i--;
                } else {
                    if (!this.mHaveInstantApps && AppUtils.isInstant(applicationInfo)) {
                        this.mHaveInstantApps = true;
                    }
                    int userId = UserHandle.getUserId(applicationInfo.uid);
                    if (userId != 999 || (applicationInfo.flags & 1) <= 0) {
                        AppEntry appEntry = this.mEntriesMap.get(userId).get(applicationInfo.packageName);
                        if (appEntry != null) {
                            appEntry.info = applicationInfo;
                        }
                    } else {
                        this.mApplications.remove(i);
                        i--;
                    }
                }
                i++;
            }
            if (anyAppIsRemoved(list, this.mApplications)) {
                clearEntries();
            }
            this.mCurComputingSizePkg = null;
            if (!this.mBackgroundHandler.hasMessages(2)) {
                this.mBackgroundHandler.sendEmptyMessage(2);
            }
        }
    }

    private static boolean anyAppIsRemoved(List<ApplicationInfo> list, List<ApplicationInfo> list2) {
        HashSet hashSet;
        if (list.size() == 0) {
            return false;
        }
        if (list2.size() < list.size()) {
            return true;
        }
        HashMap hashMap = new HashMap();
        for (ApplicationInfo applicationInfo : list2) {
            String valueOf = String.valueOf(UserHandle.getUserId(applicationInfo.uid));
            HashSet hashSet2 = (HashSet) hashMap.get(valueOf);
            if (hashSet2 == null) {
                hashSet2 = new HashSet();
                hashMap.put(valueOf, hashSet2);
            }
            if (hasFlag(applicationInfo.flags, 8388608)) {
                hashSet2.add(applicationInfo.packageName);
            }
        }
        for (ApplicationInfo applicationInfo2 : list) {
            if (hasFlag(applicationInfo2.flags, 8388608) && ((hashSet = (HashSet) hashMap.get(String.valueOf(UserHandle.getUserId(applicationInfo2.uid)))) == null || !hashSet.remove(applicationInfo2.packageName))) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void clearEntries() {
        for (int i = 0; i < this.mEntriesMap.size(); i++) {
            this.mEntriesMap.valueAt(i).clear();
        }
        this.mAppEntries.clear();
    }

    public boolean haveDisabledApps() {
        return this.mHaveDisabledApps;
    }

    public boolean haveInstantApps() {
        return this.mHaveInstantApps;
    }

    /* access modifiers changed from: package-private */
    public boolean isHiddenModule(String str) {
        Boolean bool = this.mSystemModules.get(str);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    /* access modifiers changed from: package-private */
    public boolean isSystemModule(String str) {
        return this.mSystemModules.containsKey(str);
    }

    /* access modifiers changed from: package-private */
    public void doPauseIfNeededLocked() {
        if (this.mResumed) {
            for (int i = 0; i < this.mSessions.size(); i++) {
                if (this.mSessions.get(i).mResumed) {
                    return;
                }
            }
            doPauseLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void doPauseLocked() {
        this.mResumed = false;
        PackageIntentReceiver packageIntentReceiver = this.mPackageIntentReceiver;
        if (packageIntentReceiver != null) {
            packageIntentReceiver.unregisterReceiver();
            this.mPackageIntentReceiver = null;
        }
    }

    public AppEntry getEntry(String str, int i) {
        AppEntry appEntry;
        Log.v("ApplicationsState", "getEntry about to acquire lock...");
        synchronized (this.mEntriesMap) {
            HashMap<String, AppEntry> hashMap = this.mEntriesMap.get(i);
            appEntry = hashMap != null ? hashMap.get(str) : null;
            if (appEntry == null) {
                ApplicationInfo appInfoLocked = getAppInfoLocked(str, i);
                if (appInfoLocked == null) {
                    try {
                        appInfoLocked = this.mIpm.getApplicationInfo(str, 0, i);
                    } catch (RemoteException e) {
                        Log.w("ApplicationsState", "getEntry couldn't reach PackageManager", e);
                        return null;
                    }
                }
                if (appInfoLocked != null) {
                    appEntry = getEntryLocked(appInfoLocked);
                }
            }
            Log.v("ApplicationsState", "...getEntry releasing lock");
        }
        return appEntry;
    }

    private ApplicationInfo getAppInfoLocked(String str, int i) {
        for (int i2 = 0; i2 < this.mApplications.size(); i2++) {
            ApplicationInfo applicationInfo = this.mApplications.get(i2);
            if (str.equals(applicationInfo.packageName) && i == UserHandle.getUserId(applicationInfo.uid)) {
                return applicationInfo;
            }
        }
        return null;
    }

    public void ensureIcon(AppEntry appEntry) {
        if (appEntry.icon == null) {
            synchronized (appEntry) {
                appEntry.ensureIconLocked(this.mContext);
            }
        }
    }

    public void ensureLabelDescription(AppEntry appEntry) {
        if (appEntry.labelDescription == null) {
            synchronized (appEntry) {
                appEntry.ensureLabelDescriptionLocked(this.mContext);
            }
        }
    }

    public void requestSize(String str, int i) {
        Log.v("ApplicationsState", "requestSize about to acquire lock...");
        synchronized (this.mEntriesMap) {
            AppEntry appEntry = this.mEntriesMap.get(i).get(str);
            if (appEntry != null && hasFlag(appEntry.info.flags, 8388608)) {
                this.mBackgroundHandler.post(new Runnable(appEntry, str, i) {
                    /* class com.android.settingslib.applications.$$Lambda$ApplicationsState$LuXUFbWTiS5lunO9WUp0g2nHmU */
                    public final /* synthetic */ ApplicationsState.AppEntry f$1;
                    public final /* synthetic */ String f$2;
                    public final /* synthetic */ int f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        ApplicationsState.this.lambda$requestSize$0$ApplicationsState(this.f$1, this.f$2, this.f$3);
                    }
                });
            }
            Log.v("ApplicationsState", "...requestSize releasing lock");
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$requestSize$0 */
    public /* synthetic */ void lambda$requestSize$0$ApplicationsState(AppEntry appEntry, String str, int i) {
        try {
            StorageStats queryStatsForPackage = this.mStats.queryStatsForPackage(appEntry.info.storageUuid, str, UserHandle.of(i));
            long cacheQuotaBytes = this.mStats.getCacheQuotaBytes(appEntry.info.storageUuid.toString(), appEntry.info.uid);
            PackageStats packageStats = new PackageStats(str, i);
            packageStats.codeSize = queryStatsForPackage.getCodeBytes();
            packageStats.dataSize = queryStatsForPackage.getDataBytes();
            packageStats.cacheSize = Math.min(queryStatsForPackage.getCacheBytes(), cacheQuotaBytes);
            this.mBackgroundHandler.mStatsObserver.onGetStatsCompleted(packageStats, true);
        } catch (PackageManager.NameNotFoundException | IOException e) {
            Log.w("ApplicationsState", "Failed to query stats: " + e);
            try {
                this.mBackgroundHandler.mStatsObserver.onGetStatsCompleted((PackageStats) null, false);
            } catch (RemoteException unused) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int indexOfApplicationInfoLocked(String str, int i) {
        for (int size = this.mApplications.size() - 1; size >= 0; size--) {
            ApplicationInfo applicationInfo = this.mApplications.get(size);
            if (applicationInfo.packageName.equals(str) && UserHandle.getUserId(applicationInfo.uid) == i) {
                return size;
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public void addPackage(String str, int i) {
        try {
            synchronized (this.mEntriesMap) {
                Log.v("ApplicationsState", "addPackage acquired lock");
                Log.i("ApplicationsState", "Adding package " + str);
                if (!this.mResumed) {
                    Log.v("ApplicationsState", "addPackage release lock: not resumed");
                } else if (indexOfApplicationInfoLocked(str, i) >= 0) {
                    Log.i("ApplicationsState", "Package already exists!");
                    Log.v("ApplicationsState", "addPackage release lock: already exists");
                } else {
                    ApplicationInfo applicationInfo = this.mIpm.getApplicationInfo(str, this.mUm.isUserAdmin(i) ? this.mAdminRetrieveFlags : this.mRetrieveFlags, i);
                    if (applicationInfo != null) {
                        if (!applicationInfo.enabled) {
                            if (applicationInfo.enabledSetting == 3) {
                                this.mHaveDisabledApps = true;
                            } else {
                                return;
                            }
                        }
                        if (AppUtils.isInstant(applicationInfo)) {
                            this.mHaveInstantApps = true;
                        }
                        this.mApplications.add(applicationInfo);
                        if (!this.mBackgroundHandler.hasMessages(2)) {
                            this.mBackgroundHandler.sendEmptyMessage(2);
                        }
                        if (!this.mMainHandler.hasMessages(2)) {
                            this.mMainHandler.sendEmptyMessage(2);
                        }
                        Log.v("ApplicationsState", "addPackage releasing lock");
                    }
                }
            }
        } catch (RemoteException unused) {
        }
    }

    public void removePackage(String str, int i) {
        synchronized (this.mEntriesMap) {
            Log.v("ApplicationsState", "removePackage acquired lock");
            int indexOfApplicationInfoLocked = indexOfApplicationInfoLocked(str, i);
            Log.i("ApplicationsState", "removePackage: " + str + " @ " + indexOfApplicationInfoLocked);
            if (indexOfApplicationInfoLocked >= 0) {
                AppEntry appEntry = this.mEntriesMap.get(i).get(str);
                Log.i("ApplicationsState", "removePackage: " + appEntry);
                if (appEntry != null) {
                    this.mEntriesMap.get(i).remove(str);
                    this.mAppEntries.remove(appEntry);
                }
                ApplicationInfo applicationInfo = this.mApplications.get(indexOfApplicationInfoLocked);
                this.mApplications.remove(indexOfApplicationInfoLocked);
                if (!applicationInfo.enabled) {
                    this.mHaveDisabledApps = false;
                    Iterator<ApplicationInfo> it = this.mApplications.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (!it.next().enabled) {
                                this.mHaveDisabledApps = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                if (AppUtils.isInstant(applicationInfo)) {
                    this.mHaveInstantApps = false;
                    Iterator<ApplicationInfo> it2 = this.mApplications.iterator();
                    while (true) {
                        if (it2.hasNext()) {
                            if (AppUtils.isInstant(it2.next())) {
                                this.mHaveInstantApps = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
            Log.v("ApplicationsState", "removePackage releasing lock");
        }
    }

    public void invalidatePackage(String str, int i) {
        removePackage(str, i);
        addPackage(str, i);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void addUser(int i) {
        if (ArrayUtils.contains(this.mUm.getProfileIdsWithDisabled(UserHandle.myUserId()), i)) {
            synchronized (this.mEntriesMap) {
                this.mEntriesMap.put(i, new HashMap<>());
                if (this.mResumed) {
                    doPauseLocked();
                    doResumeIfNeededLocked();
                }
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void removeUser(int i) {
        synchronized (this.mEntriesMap) {
            HashMap<String, AppEntry> hashMap = this.mEntriesMap.get(i);
            if (hashMap != null) {
                for (AppEntry appEntry : hashMap.values()) {
                    this.mAppEntries.remove(appEntry);
                    this.mApplications.remove(appEntry.info);
                }
                this.mEntriesMap.remove(i);
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private AppEntry getEntryLocked(ApplicationInfo applicationInfo) {
        int userId = UserHandle.getUserId(applicationInfo.uid);
        AppEntry appEntry = this.mEntriesMap.get(userId).get(applicationInfo.packageName);
        Log.i("ApplicationsState", "Looking up entry of pkg " + applicationInfo.packageName + ": " + appEntry);
        if (appEntry == null) {
            if (isHiddenModule(applicationInfo.packageName)) {
                Log.i("ApplicationsState", "No AppEntry for " + applicationInfo.packageName + " (hidden module)");
                return null;
            }
            Log.i("ApplicationsState", "Creating AppEntry for " + applicationInfo.packageName);
            Context context = this.mContext;
            long j = this.mCurId;
            this.mCurId = 1 + j;
            AppEntry appEntry2 = new AppEntry(context, applicationInfo, j);
            this.mEntriesMap.get(userId).put(applicationInfo.packageName, appEntry2);
            this.mAppEntries.add(appEntry2);
            return appEntry2;
        } else if (appEntry.info == applicationInfo) {
            return appEntry;
        } else {
            appEntry.info = applicationInfo;
            return appEntry;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private long getTotalInternalSize(PackageStats packageStats) {
        if (packageStats != null) {
            return (packageStats.codeSize + packageStats.dataSize) - packageStats.cacheSize;
        }
        return -2;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private long getTotalExternalSize(PackageStats packageStats) {
        if (packageStats != null) {
            return packageStats.externalCodeSize + packageStats.externalDataSize + packageStats.externalCacheSize + packageStats.externalMediaSize + packageStats.externalObbSize;
        }
        return -2;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String getSizeStr(long j) {
        if (j >= 0) {
            return Utils.formatFileSize(this.mContext, j);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void rebuildActiveSessions() {
        synchronized (this.mEntriesMap) {
            if (this.mSessionsChanged) {
                this.mActiveSessions.clear();
                for (int i = 0; i < this.mSessions.size(); i++) {
                    Session session = this.mSessions.get(i);
                    if (session.mResumed) {
                        this.mActiveSessions.add(new WeakReference<>(session));
                    }
                }
            }
        }
    }

    public class Session implements LifecycleObserver {
        final Callbacks mCallbacks;
        private int mFlags = 15;
        private final boolean mHasLifecycle;
        ArrayList<AppEntry> mLastAppList;
        boolean mRebuildAsync;
        Comparator<AppEntry> mRebuildComparator;
        AppFilter mRebuildFilter;
        boolean mRebuildForeground;
        boolean mRebuildRequested;
        final Object mRebuildSync = new Object();
        boolean mResumed;

        Session(Callbacks callbacks, Lifecycle lifecycle) {
            this.mCallbacks = callbacks;
            if (lifecycle != null) {
                lifecycle.addObserver(this);
                this.mHasLifecycle = true;
                return;
            }
            this.mHasLifecycle = false;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume() {
            Log.v("ApplicationsState", "resume about to acquire lock...");
            synchronized (ApplicationsState.this.mEntriesMap) {
                if (!this.mResumed) {
                    this.mResumed = true;
                    ApplicationsState.this.mSessionsChanged = true;
                    ApplicationsState.this.doPauseLocked();
                    ApplicationsState.this.doResumeIfNeededLocked();
                }
            }
            Log.v("ApplicationsState", "...resume releasing lock");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void onPause() {
            Log.v("ApplicationsState", "pause about to acquire lock...");
            synchronized (ApplicationsState.this.mEntriesMap) {
                if (this.mResumed) {
                    this.mResumed = false;
                    ApplicationsState.this.mSessionsChanged = true;
                    ApplicationsState.this.mBackgroundHandler.removeMessages(1, this);
                    ApplicationsState.this.doPauseIfNeededLocked();
                }
                Log.v("ApplicationsState", "...pause releasing lock");
            }
        }

        public ArrayList<AppEntry> getAllApps() {
            ArrayList<AppEntry> arrayList;
            synchronized (ApplicationsState.this.mEntriesMap) {
                arrayList = new ArrayList<>(ApplicationsState.this.mAppEntries);
            }
            return arrayList;
        }

        public ArrayList<AppEntry> rebuild(AppFilter appFilter, Comparator<AppEntry> comparator) {
            return rebuild(appFilter, comparator, true);
        }

        public ArrayList<AppEntry> rebuild(AppFilter appFilter, Comparator<AppEntry> comparator, boolean z) {
            synchronized (this.mRebuildSync) {
                synchronized (ApplicationsState.this.mRebuildingSessions) {
                    ApplicationsState.this.mRebuildingSessions.add(this);
                    this.mRebuildRequested = true;
                    this.mRebuildAsync = true;
                    this.mRebuildFilter = appFilter;
                    this.mRebuildComparator = comparator;
                    this.mRebuildForeground = z;
                    if (!ApplicationsState.this.mBackgroundHandler.hasMessages(1)) {
                        ApplicationsState.this.mBackgroundHandler.sendMessage(ApplicationsState.this.mBackgroundHandler.obtainMessage(1));
                    }
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0025, code lost:
            if (r1 == null) goto L_0x002e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0027, code lost:
            r1.init(r9.this$0.mContext);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x002e, code lost:
            r3 = r9.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0032, code lost:
            monitor-enter(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            r0 = new java.util.ArrayList(r9.this$0.mAppEntries);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x003c, code lost:
            monitor-exit(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x003d, code lost:
            r3 = new java.util.ArrayList<>();
            android.util.Log.i("ApplicationsState", "Rebuilding...");
            r0 = r0.iterator();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0051, code lost:
            if (r0.hasNext() == false) goto L_0x00a9;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x0053, code lost:
            r4 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r0.next();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0059, code lost:
            if (r4 == null) goto L_0x004d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x005b, code lost:
            if (r1 == null) goto L_0x0063;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0061, code lost:
            if (r1.filterApp(r4) == false) goto L_0x004d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0063, code lost:
            r5 = r9.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x0067, code lost:
            monitor-enter(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
            android.util.Log.v("ApplicationsState", "rebuild acquired lock");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x006f, code lost:
            if (r2 == null) goto L_0x0078;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x0071, code lost:
            r4.ensureLabel(r9.this$0.mContext);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x0078, code lost:
            android.util.Log.i("ApplicationsState", "Using " + r4.info.packageName + ": " + r4);
            r3.add(r4);
            android.util.Log.v("ApplicationsState", "rebuild releasing lock");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x00a4, code lost:
            monitor-exit(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x00a9, code lost:
            if (r2 == null) goto L_0x00b8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:43:0x00ab, code lost:
            r0 = r9.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:0x00af, code lost:
            monitor-enter(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
            java.util.Collections.sort(r3, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:47:0x00b3, code lost:
            monitor-exit(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:52:0x00b8, code lost:
            r0 = r9.mRebuildSync;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x00ba, code lost:
            monitor-enter(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x00bd, code lost:
            if (r9.mRebuildRequested != false) goto L_0x00e5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x00bf, code lost:
            r9.mLastAppList = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x00c3, code lost:
            if (r9.mRebuildAsync != false) goto L_0x00cb;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x00c5, code lost:
            r9.mRebuildSync.notifyAll();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:61:0x00d4, code lost:
            if (r9.this$0.mMainHandler.hasMessages(1, r9) != false) goto L_0x00e5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x00d6, code lost:
            r9.this$0.mMainHandler.sendMessage(r9.this$0.mMainHandler.obtainMessage(1, r9));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:63:0x00e5, code lost:
            monitor-exit(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:64:0x00e6, code lost:
            android.os.Process.setThreadPriority(10);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:65:0x00eb, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleRebuildList() {
            /*
            // Method dump skipped, instructions count: 245
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.Session.handleRebuildList():void");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy() {
            if (!this.mHasLifecycle) {
                onPause();
            }
            synchronized (ApplicationsState.this.mEntriesMap) {
                ApplicationsState.this.mSessions.remove(this);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            ApplicationsState.this.rebuildActiveSessions();
            switch (message.what) {
                case 1:
                    Session session = (Session) message.obj;
                    Iterator<WeakReference<Session>> it = ApplicationsState.this.mActiveSessions.iterator();
                    while (it.hasNext()) {
                        Session session2 = it.next().get();
                        if (session2 != null && session2 == session) {
                            session.mCallbacks.onRebuildComplete(session.mLastAppList);
                        }
                    }
                    return;
                case 2:
                    Iterator<WeakReference<Session>> it2 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it2.hasNext()) {
                        Session session3 = it2.next().get();
                        if (session3 != null) {
                            session3.mCallbacks.onPackageListChanged();
                        }
                    }
                    return;
                case 3:
                    Iterator<WeakReference<Session>> it3 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it3.hasNext()) {
                        Session session4 = it3.next().get();
                        if (session4 != null) {
                            session4.mCallbacks.onPackageIconChanged();
                        }
                    }
                    return;
                case 4:
                    Iterator<WeakReference<Session>> it4 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it4.hasNext()) {
                        Session session5 = it4.next().get();
                        if (session5 != null) {
                            session5.mCallbacks.onPackageSizeChanged((String) message.obj);
                        }
                    }
                    return;
                case 5:
                    Iterator<WeakReference<Session>> it5 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it5.hasNext()) {
                        Session session6 = it5.next().get();
                        if (session6 != null) {
                            session6.mCallbacks.onAllSizesComputed();
                        }
                    }
                    return;
                case 6:
                    Iterator<WeakReference<Session>> it6 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it6.hasNext()) {
                        Session session7 = it6.next().get();
                        if (session7 != null) {
                            session7.mCallbacks.onRunningStateChanged(message.arg1 != 0);
                        }
                    }
                    return;
                case 7:
                    Iterator<WeakReference<Session>> it7 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it7.hasNext()) {
                        Session session8 = it7.next().get();
                        if (session8 != null) {
                            session8.mCallbacks.onLauncherInfoChanged();
                        }
                    }
                    return;
                case 8:
                    Iterator<WeakReference<Session>> it8 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it8.hasNext()) {
                        Session session9 = it8.next().get();
                        if (session9 != null) {
                            session9.mCallbacks.onLoadEntriesCompleted();
                        }
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: private */
    public class BackgroundHandler extends Handler {
        boolean mRunning;
        final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
            /* class com.android.settingslib.applications.ApplicationsState.BackgroundHandler.AnonymousClass1 */

            public void onGetStatsCompleted(PackageStats packageStats, boolean z) {
                boolean z2;
                if (z) {
                    synchronized (ApplicationsState.this.mEntriesMap) {
                        Log.v("ApplicationsState", "onGetStatsCompleted acquired lock");
                        HashMap<String, AppEntry> hashMap = ApplicationsState.this.mEntriesMap.get(packageStats.userHandle);
                        if (hashMap != null) {
                            AppEntry appEntry = hashMap.get(packageStats.packageName);
                            if (appEntry != null) {
                                synchronized (appEntry) {
                                    z2 = false;
                                    try {
                                        File file = new File(ApplicationsState.this.mPm.getApplicationInfo(packageStats.packageName, 0).sourceDir);
                                        if (file.exists()) {
                                            appEntry.apkUpdateTimestamp = file.lastModified();
                                        } else {
                                            appEntry.apkUpdateTimestamp = 0;
                                        }
                                    } catch (PackageManager.NameNotFoundException unused) {
                                        Log.d("ApplicationsState", "mStatsObserver: package not found");
                                    }
                                    appEntry.sizeStale = false;
                                    appEntry.sizeLoadStart = 0;
                                    long j = packageStats.externalCodeSize + packageStats.externalObbSize;
                                    long j2 = packageStats.externalDataSize + packageStats.externalMediaSize;
                                    long totalInternalSize = j + j2 + ApplicationsState.this.getTotalInternalSize(packageStats);
                                    if (!(appEntry.size == totalInternalSize && appEntry.cacheSize == packageStats.cacheSize && appEntry.codeSize == packageStats.codeSize && appEntry.dataSize == packageStats.dataSize && appEntry.externalCodeSize == j && appEntry.externalDataSize == j2 && appEntry.externalCacheSize == packageStats.externalCacheSize)) {
                                        appEntry.size = totalInternalSize;
                                        appEntry.cacheSize = packageStats.cacheSize;
                                        appEntry.codeSize = packageStats.codeSize;
                                        appEntry.dataSize = packageStats.dataSize;
                                        appEntry.externalCodeSize = j;
                                        appEntry.externalDataSize = j2;
                                        appEntry.externalCacheSize = packageStats.externalCacheSize;
                                        appEntry.sizeStr = ApplicationsState.this.getSizeStr(totalInternalSize);
                                        long totalInternalSize2 = ApplicationsState.this.getTotalInternalSize(packageStats);
                                        appEntry.internalSize = totalInternalSize2;
                                        appEntry.internalSizeStr = ApplicationsState.this.getSizeStr(totalInternalSize2);
                                        long totalExternalSize = ApplicationsState.this.getTotalExternalSize(packageStats);
                                        appEntry.externalSize = totalExternalSize;
                                        appEntry.externalSizeStr = ApplicationsState.this.getSizeStr(totalExternalSize);
                                        Log.i("ApplicationsState", "Set size of " + appEntry.label + " " + appEntry + ": " + appEntry.sizeStr);
                                        z2 = true;
                                    }
                                }
                                if (z2) {
                                    ApplicationsState.this.mMainHandler.sendMessage(ApplicationsState.this.mMainHandler.obtainMessage(4, packageStats.packageName));
                                }
                            }
                            if (ApplicationsState.this.mCurComputingSizePkg != null && ApplicationsState.this.mCurComputingSizePkg.equals(packageStats.packageName) && ApplicationsState.this.mCurComputingSizeUserId == packageStats.userHandle) {
                                ApplicationsState.this.mCurComputingSizePkg = null;
                                BackgroundHandler.this.sendEmptyMessage(7);
                            }
                            Log.v("ApplicationsState", "onGetStatsCompleted releasing lock");
                        }
                    }
                }
            }
        };

        BackgroundHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            ArrayList arrayList;
            int i;
            int i2;
            synchronized (ApplicationsState.this.mRebuildingSessions) {
                if (ApplicationsState.this.mRebuildingSessions.size() > 0) {
                    arrayList = new ArrayList(ApplicationsState.this.mRebuildingSessions);
                    ApplicationsState.this.mRebuildingSessions.clear();
                } else {
                    arrayList = null;
                }
            }
            if (arrayList != null) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    ((Session) it.next()).handleRebuildList();
                }
            }
            int combinedSessionFlags = getCombinedSessionFlags(ApplicationsState.this.mSessions);
            int i3 = message.what;
            int i4 = 0;
            boolean z = true;
            switch (i3) {
                case 2:
                    synchronized (ApplicationsState.this.mEntriesMap) {
                        Log.v("ApplicationsState", "MSG_LOAD_ENTRIES acquired lock");
                        i = 0;
                        for (int i5 = 0; i5 < ApplicationsState.this.mApplications.size() && i < 6; i5++) {
                            if (!this.mRunning) {
                                this.mRunning = true;
                                ApplicationsState.this.mMainHandler.sendMessage(ApplicationsState.this.mMainHandler.obtainMessage(6, 1));
                            }
                            ApplicationInfo applicationInfo = ApplicationsState.this.mApplications.get(i5);
                            int userId = UserHandle.getUserId(applicationInfo.uid);
                            if (ApplicationsState.this.mEntriesMap.get(userId).get(applicationInfo.packageName) == null) {
                                try {
                                    PackageInfo packageInfo = ApplicationsState.this.mContext.getPackageManager().getPackageInfo(applicationInfo.packageName, 0);
                                    if (TextUtils.isEmpty(packageInfo.overlayCategory) || !ApplicationsState.THEME_OVERLAY_CATEGORYS.contains(packageInfo.overlayCategory)) {
                                        i++;
                                        ApplicationsState.this.getEntryLocked(applicationInfo);
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (userId != 0) {
                                if (ApplicationsState.this.mEntriesMap.indexOfKey(0) >= 0) {
                                    AppEntry appEntry = ApplicationsState.this.mEntriesMap.get(0).get(applicationInfo.packageName);
                                    if (appEntry != null && !ApplicationsState.hasFlag(appEntry.info.flags, 8388608)) {
                                        ApplicationsState.this.mEntriesMap.get(0).remove(applicationInfo.packageName);
                                        ApplicationsState.this.mAppEntries.remove(appEntry);
                                    }
                                }
                            }
                        }
                        Log.v("ApplicationsState", "MSG_LOAD_ENTRIES releasing lock");
                    }
                    if (i >= 6) {
                        sendEmptyMessage(2);
                        return;
                    }
                    if (!ApplicationsState.this.mMainHandler.hasMessages(8)) {
                        ApplicationsState.this.mMainHandler.sendEmptyMessage(8);
                    }
                    sendEmptyMessage(3);
                    return;
                case 3:
                    if (ApplicationsState.hasFlag(combinedSessionFlags, 1)) {
                        ArrayList<ResolveInfo> arrayList2 = new ArrayList();
                        ApplicationsState.this.mPm.getHomeActivities(arrayList2);
                        synchronized (ApplicationsState.this.mEntriesMap) {
                            int size = ApplicationsState.this.mEntriesMap.size();
                            for (int i6 = 0; i6 < size; i6++) {
                                Log.v("ApplicationsState", "MSG_LOAD_HOME_APP acquired lock");
                                HashMap<String, AppEntry> valueAt = ApplicationsState.this.mEntriesMap.valueAt(i6);
                                for (ResolveInfo resolveInfo : arrayList2) {
                                    AppEntry appEntry2 = valueAt.get(resolveInfo.activityInfo.packageName);
                                    if (appEntry2 != null) {
                                        appEntry2.isHomeApp = true;
                                    }
                                }
                                Log.v("ApplicationsState", "MSG_LOAD_HOME_APP releasing lock");
                            }
                        }
                    }
                    sendEmptyMessage(4);
                    return;
                case 4:
                case 5:
                    if ((i3 == 4 && ApplicationsState.hasFlag(combinedSessionFlags, 8)) || (message.what == 5 && ApplicationsState.hasFlag(combinedSessionFlags, 16))) {
                        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
                        intent.addCategory(message.what == 4 ? "android.intent.category.LAUNCHER" : "android.intent.category.LEANBACK_LAUNCHER");
                        int i7 = 0;
                        while (i7 < ApplicationsState.this.mEntriesMap.size()) {
                            int keyAt = ApplicationsState.this.mEntriesMap.keyAt(i7);
                            List queryIntentActivitiesAsUser = ApplicationsState.this.mPm.queryIntentActivitiesAsUser(intent, 786944, keyAt);
                            synchronized (ApplicationsState.this.mEntriesMap) {
                                Log.v("ApplicationsState", "MSG_LOAD_LAUNCHER acquired lock");
                                HashMap<String, AppEntry> valueAt2 = ApplicationsState.this.mEntriesMap.valueAt(i7);
                                int size2 = queryIntentActivitiesAsUser.size();
                                int i8 = i4;
                                while (i8 < size2) {
                                    ResolveInfo resolveInfo2 = (ResolveInfo) queryIntentActivitiesAsUser.get(i8);
                                    String str = resolveInfo2.activityInfo.packageName;
                                    AppEntry appEntry3 = valueAt2.get(str);
                                    if (appEntry3 != null) {
                                        appEntry3.hasLauncherEntry = z;
                                        appEntry3.launcherEntryEnabled |= resolveInfo2.activityInfo.enabled;
                                    } else {
                                        Log.w("ApplicationsState", "Cannot find pkg: " + str + " on user " + keyAt);
                                    }
                                    i8++;
                                    z = true;
                                }
                                Log.v("ApplicationsState", "MSG_LOAD_LAUNCHER releasing lock");
                            }
                            i7++;
                            i4 = 0;
                            z = true;
                        }
                        if (!ApplicationsState.this.mMainHandler.hasMessages(7)) {
                            ApplicationsState.this.mMainHandler.sendEmptyMessage(7);
                        }
                    }
                    if (message.what == 4) {
                        sendEmptyMessage(5);
                        return;
                    } else {
                        sendEmptyMessage(6);
                        return;
                    }
                case 6:
                    if (ApplicationsState.hasFlag(combinedSessionFlags, 2)) {
                        synchronized (ApplicationsState.this.mEntriesMap) {
                            Log.v("ApplicationsState", "MSG_LOAD_ICONS acquired lock");
                            i2 = 0;
                            while (i4 < ApplicationsState.this.mAppEntries.size() && i2 < 2) {
                                AppEntry appEntry4 = ApplicationsState.this.mAppEntries.get(i4);
                                if (appEntry4.icon == null || !appEntry4.mounted) {
                                    synchronized (appEntry4) {
                                        if (appEntry4.ensureIconLocked(ApplicationsState.this.mContext)) {
                                            if (!this.mRunning) {
                                                this.mRunning = true;
                                                ApplicationsState.this.mMainHandler.sendMessage(ApplicationsState.this.mMainHandler.obtainMessage(6, 1));
                                            }
                                            i2++;
                                        }
                                    }
                                }
                                i4++;
                            }
                            Log.v("ApplicationsState", "MSG_LOAD_ICONS releasing lock");
                        }
                        if (i2 > 0 && !ApplicationsState.this.mMainHandler.hasMessages(3)) {
                            ApplicationsState.this.mMainHandler.sendEmptyMessage(3);
                        }
                        if (i2 >= 2) {
                            sendEmptyMessage(6);
                            return;
                        }
                    }
                    sendEmptyMessage(7);
                    return;
                case 7:
                    if (ApplicationsState.hasFlag(combinedSessionFlags, 4)) {
                        synchronized (ApplicationsState.this.mEntriesMap) {
                            Log.v("ApplicationsState", "MSG_LOAD_SIZES acquired lock");
                            if (ApplicationsState.this.mCurComputingSizePkg != null) {
                                Log.v("ApplicationsState", "MSG_LOAD_SIZES releasing: currently computing");
                                return;
                            }
                            long uptimeMillis = SystemClock.uptimeMillis();
                            for (int i9 = 0; i9 < ApplicationsState.this.mAppEntries.size(); i9++) {
                                AppEntry appEntry5 = ApplicationsState.this.mAppEntries.get(i9);
                                if (ApplicationsState.hasFlag(appEntry5.info.flags, 8388608) && (appEntry5.size == -1 || appEntry5.sizeStale)) {
                                    if (appEntry5.sizeLoadStart == 0 || appEntry5.sizeLoadStart < uptimeMillis - 20000) {
                                        if (!this.mRunning) {
                                            this.mRunning = true;
                                            ApplicationsState.this.mMainHandler.sendMessage(ApplicationsState.this.mMainHandler.obtainMessage(6, 1));
                                        }
                                        appEntry5.sizeLoadStart = uptimeMillis;
                                        ApplicationsState.this.mCurComputingSizeUuid = appEntry5.info.storageUuid;
                                        ApplicationsState.this.mCurComputingSizePkg = appEntry5.info.packageName;
                                        ApplicationsState.this.mCurComputingSizeUserId = UserHandle.getUserId(appEntry5.info.uid);
                                        ApplicationsState.this.mBackgroundHandler.post(new Runnable() {
                                            /* class com.android.settingslib.applications.$$Lambda$ApplicationsState$BackgroundHandler$7jhXQzAcRoT6ACDzmPBTQMi7Ldc */

                                            public final void run() {
                                                ApplicationsState.BackgroundHandler.this.lambda$handleMessage$0$ApplicationsState$BackgroundHandler();
                                            }
                                        });
                                    }
                                    Log.v("ApplicationsState", "MSG_LOAD_SIZES releasing: now computing");
                                    return;
                                }
                            }
                            if (!ApplicationsState.this.mMainHandler.hasMessages(5)) {
                                ApplicationsState.this.mMainHandler.sendEmptyMessage(5);
                                this.mRunning = false;
                                ApplicationsState.this.mMainHandler.sendMessage(ApplicationsState.this.mMainHandler.obtainMessage(6, 0));
                            }
                            Log.v("ApplicationsState", "MSG_LOAD_SIZES releasing lock");
                            return;
                        }
                    }
                    return;
                default:
                    return;
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$handleMessage$0 */
        public /* synthetic */ void lambda$handleMessage$0$ApplicationsState$BackgroundHandler() {
            try {
                StorageStats queryStatsForPackage = ApplicationsState.this.mStats.queryStatsForPackage(ApplicationsState.this.mCurComputingSizeUuid, ApplicationsState.this.mCurComputingSizePkg, UserHandle.of(ApplicationsState.this.mCurComputingSizeUserId));
                PackageStats packageStats = new PackageStats(ApplicationsState.this.mCurComputingSizePkg, ApplicationsState.this.mCurComputingSizeUserId);
                packageStats.codeSize = queryStatsForPackage.getCodeBytes();
                packageStats.dataSize = queryStatsForPackage.getDataBytes();
                packageStats.cacheSize = queryStatsForPackage.getCacheBytes();
                this.mStatsObserver.onGetStatsCompleted(packageStats, true);
            } catch (PackageManager.NameNotFoundException | IOException e) {
                Log.w("ApplicationsState", "Failed to query stats: " + e);
                try {
                    this.mStatsObserver.onGetStatsCompleted((PackageStats) null, false);
                } catch (RemoteException unused) {
                }
            }
        }

        private int getCombinedSessionFlags(List<Session> list) {
            int i;
            synchronized (ApplicationsState.this.mEntriesMap) {
                i = 0;
                for (Session session : list) {
                    i |= session.mFlags;
                }
            }
            return i;
        }
    }

    /* access modifiers changed from: private */
    public class PackageIntentReceiver extends BroadcastReceiver {
        private PackageIntentReceiver() {
        }

        /* access modifiers changed from: package-private */
        public void registerReceiver() {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
            intentFilter.addDataScheme("package");
            ApplicationsState.this.mContext.registerReceiver(this, intentFilter);
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
            intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
            ApplicationsState.this.mContext.registerReceiver(this, intentFilter2);
            IntentFilter intentFilter3 = new IntentFilter();
            intentFilter3.addAction("android.intent.action.USER_ADDED");
            intentFilter3.addAction("android.intent.action.USER_REMOVED");
            ApplicationsState.this.mContext.registerReceiver(this, intentFilter3);
        }

        /* access modifiers changed from: package-private */
        public void unregisterReceiver() {
            ApplicationsState.this.mContext.unregisterReceiver(this);
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int i = 0;
            if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                String encodedSchemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState applicationsState = ApplicationsState.this;
                    applicationsState.addPackage(encodedSchemeSpecificPart, applicationsState.mEntriesMap.keyAt(i));
                    i++;
                }
            } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                String encodedSchemeSpecificPart2 = intent.getData().getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState applicationsState2 = ApplicationsState.this;
                    applicationsState2.removePackage(encodedSchemeSpecificPart2, applicationsState2.mEntriesMap.keyAt(i));
                    i++;
                }
            } else if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
                String encodedSchemeSpecificPart3 = intent.getData().getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState applicationsState3 = ApplicationsState.this;
                    applicationsState3.invalidatePackage(encodedSchemeSpecificPart3, applicationsState3.mEntriesMap.keyAt(i));
                    i++;
                }
            } else if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(action) || "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action)) {
                String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                if (!(stringArrayExtra == null || stringArrayExtra.length == 0 || !"android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(action))) {
                    for (String str : stringArrayExtra) {
                        for (int i2 = 0; i2 < ApplicationsState.this.mEntriesMap.size(); i2++) {
                            ApplicationsState applicationsState4 = ApplicationsState.this;
                            applicationsState4.invalidatePackage(str, applicationsState4.mEntriesMap.keyAt(i2));
                        }
                    }
                }
            } else if ("android.intent.action.USER_ADDED".equals(action)) {
                ApplicationsState.this.addUser(intent.getIntExtra("android.intent.extra.user_handle", -10000));
            } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                ApplicationsState.this.removeUser(intent.getIntExtra("android.intent.extra.user_handle", -10000));
            }
        }
    }

    public static class AppEntry extends SizeInfo {
        public final File apkFile;
        public long apkUpdateTimestamp;
        public long externalSize;
        public String externalSizeStr;
        public Object extraInfo;
        public boolean hasLauncherEntry;
        public Drawable icon;
        public final long id;
        public ApplicationInfo info;
        public long internalSize;
        public String internalSizeStr;
        public boolean isHomeApp;
        public String label;
        public String labelDescription;
        public boolean launcherEntryEnabled;
        public boolean mounted;
        public long size = -1;
        public long sizeLoadStart;
        public boolean sizeStale = true;
        public String sizeStr;

        public AppEntry(Context context, ApplicationInfo applicationInfo, long j) {
            this.apkFile = new File(applicationInfo.sourceDir);
            this.id = j;
            this.info = applicationInfo;
            ensureLabel(context);
            ThreadUtils.postOnBackgroundThread(new Runnable(context) {
                /* class com.android.settingslib.applications.$$Lambda$ApplicationsState$AppEntry$CHcrAV6RtpyvpsjQKTHb_OQPQ1I */
                public final /* synthetic */ Context f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ApplicationsState.AppEntry.this.lambda$new$0$ApplicationsState$AppEntry(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ApplicationsState$AppEntry(Context context) {
            if (this.icon == null) {
                ensureIconLocked(context);
            }
            if (this.labelDescription == null) {
                ensureLabelDescriptionLocked(context);
            }
        }

        public void ensureLabel(Context context) {
            if (this.label != null && this.mounted) {
                return;
            }
            if (!this.apkFile.exists()) {
                this.mounted = false;
                this.label = this.info.packageName;
                return;
            }
            this.mounted = true;
            CharSequence loadLabel = this.info.loadLabel(context.getPackageManager());
            this.label = loadLabel != null ? loadLabel.toString() : this.info.packageName;
        }

        /* access modifiers changed from: package-private */
        public boolean ensureIconLocked(Context context) {
            if (this.icon == null) {
                if (this.apkFile.exists()) {
                    this.icon = Utils.getBadgedIcon(context, this.info);
                    return true;
                }
                this.mounted = false;
                this.icon = context.getDrawable(17303667);
            } else if (!this.mounted && this.apkFile.exists()) {
                this.mounted = true;
                this.icon = Utils.getBadgedIcon(context, this.info);
                return true;
            }
            return false;
        }

        public void ensureLabelDescriptionLocked(Context context) {
            if (UserManager.get(context).isManagedProfile(UserHandle.getUserId(this.info.uid))) {
                this.labelDescription = context.getString(R$string.accessibility_work_profile_app_description, this.label);
                return;
            }
            this.labelDescription = this.label;
        }
    }

    public interface AppFilter {
        boolean filterApp(AppEntry appEntry);

        void init();

        default void init(Context context) {
            init();
        }
    }

    public static class VolumeFilter implements AppFilter {
        private final String mVolumeUuid;

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        public VolumeFilter(String str) {
            this.mVolumeUuid = str;
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            return Objects.equals(appEntry.info.volumeUuid, this.mVolumeUuid);
        }
    }

    public static class CompoundFilter implements AppFilter {
        private final AppFilter mFirstFilter;
        private final AppFilter mSecondFilter;

        public CompoundFilter(AppFilter appFilter, AppFilter appFilter2) {
            this.mFirstFilter = appFilter;
            this.mSecondFilter = appFilter2;
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init(Context context) {
            this.mFirstFilter.init(context);
            this.mSecondFilter.init(context);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
            this.mFirstFilter.init();
            this.mSecondFilter.init();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry appEntry) {
            return this.mFirstFilter.filterApp(appEntry) && this.mSecondFilter.filterApp(appEntry);
        }
    }
}
