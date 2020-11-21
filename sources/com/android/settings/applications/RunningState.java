package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseArray;
import com.android.settings.C0017R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.InterestingConfigChanges;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class RunningState {
    static Object sGlobalLock = new Object();
    static RunningState sInstance;
    final ArrayList<ProcessItem> mAllProcessItems = new ArrayList<>();
    final ActivityManager mAm;
    final Context mApplicationContext;
    final Comparator<MergedItem> mBackgroundComparator = new Comparator<MergedItem>() {
        /* class com.android.settings.applications.RunningState.AnonymousClass1 */

        public int compare(MergedItem mergedItem, MergedItem mergedItem2) {
            int i = mergedItem.mUserId;
            int i2 = mergedItem2.mUserId;
            if (i != i2) {
                int i3 = RunningState.this.mMyUserId;
                if (i == i3) {
                    return -1;
                }
                if (i2 == i3) {
                    return 1;
                }
                return i < i2 ? -1 : 1;
            }
            ProcessItem processItem = mergedItem.mProcess;
            ProcessItem processItem2 = mergedItem2.mProcess;
            if (processItem == processItem2) {
                String str = mergedItem.mLabel;
                String str2 = mergedItem2.mLabel;
                if (str == str2) {
                    return 0;
                }
                if (str != null) {
                    return str.compareTo(str2);
                }
                return -1;
            } else if (processItem == null) {
                return -1;
            } else {
                if (processItem2 == null) {
                    return 1;
                }
                ActivityManager.RunningAppProcessInfo runningAppProcessInfo = processItem.mRunningProcessInfo;
                ActivityManager.RunningAppProcessInfo runningAppProcessInfo2 = processItem2.mRunningProcessInfo;
                boolean z = runningAppProcessInfo.importance >= 400;
                if (z != (runningAppProcessInfo2.importance >= 400)) {
                    return z ? 1 : -1;
                }
                boolean z2 = (runningAppProcessInfo.flags & 4) != 0;
                if (z2 != ((runningAppProcessInfo2.flags & 4) != 0)) {
                    return z2 ? -1 : 1;
                }
                int i4 = runningAppProcessInfo.lru;
                int i5 = runningAppProcessInfo2.lru;
                if (i4 != i5) {
                    return i4 < i5 ? -1 : 1;
                }
                String str3 = mergedItem.mProcess.mLabel;
                String str4 = mergedItem2.mProcess.mLabel;
                if (str3 == str4) {
                    return 0;
                }
                if (str3 == null) {
                    return 1;
                }
                if (str4 == null) {
                    return -1;
                }
                return str3.compareTo(str4);
            }
        }
    };
    final BackgroundHandler mBackgroundHandler;
    ArrayList<MergedItem> mBackgroundItems;
    long mBackgroundProcessMemory;
    final HandlerThread mBackgroundThread;
    final Handler mHandler;
    boolean mHaveData;
    final boolean mHideManagedProfiles;
    final InterestingConfigChanges mInterestingConfigChanges = new InterestingConfigChanges();
    final ArrayList<ProcessItem> mInterestingProcesses = new ArrayList<>();
    final Object mLock = new Object();
    ArrayList<MergedItem> mMergedItems;
    final int mMyUserId;
    final SparseArray<MergedItem> mOtherUserBackgroundItems = new SparseArray<>();
    final SparseArray<MergedItem> mOtherUserMergedItems = new SparseArray<>();
    final PackageManager mPm;
    final ArrayList<ProcessItem> mProcessItems = new ArrayList<>();
    OnRefreshUiListener mRefreshUiListener;
    boolean mResumed;
    final SparseArray<ProcessItem> mRunningProcesses = new SparseArray<>();
    int mSequence = 0;
    final ServiceProcessComparator mServiceProcessComparator = new ServiceProcessComparator();
    long mServiceProcessMemory;
    final SparseArray<HashMap<String, ProcessItem>> mServiceProcessesByName = new SparseArray<>();
    final SparseArray<ProcessItem> mServiceProcessesByPid = new SparseArray<>();
    final SparseArray<AppProcessInfo> mTmpAppProcesses = new SparseArray<>();
    final UserManager mUm;
    private final UserManagerBroadcastReceiver mUmBroadcastReceiver;
    ArrayList<MergedItem> mUserBackgroundItems;
    boolean mWatchingBackgroundItems;

    /* access modifiers changed from: package-private */
    public interface OnRefreshUiListener {
        void onRefreshUi(int i);
    }

    /* access modifiers changed from: package-private */
    public static class AppProcessInfo {
        boolean hasForegroundServices;
        boolean hasServices;
        final ActivityManager.RunningAppProcessInfo info;

        AppProcessInfo(ActivityManager.RunningAppProcessInfo runningAppProcessInfo) {
            this.info = runningAppProcessInfo;
        }
    }

    /* access modifiers changed from: package-private */
    public final class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                RunningState.this.reset();
            } else if (i == 2) {
                synchronized (RunningState.this.mLock) {
                    if (RunningState.this.mResumed) {
                        Message obtainMessage = RunningState.this.mHandler.obtainMessage(3);
                        RunningState runningState = RunningState.this;
                        obtainMessage.arg1 = runningState.update(runningState.mApplicationContext, runningState.mAm) ? 1 : 0;
                        RunningState.this.mHandler.sendMessage(obtainMessage);
                        removeMessages(2);
                        sendMessageDelayed(obtainMessage(2), 2000);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public final class UserManagerBroadcastReceiver extends BroadcastReceiver {
        private volatile boolean usersChanged;

        private UserManagerBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (RunningState.this.mLock) {
                if (RunningState.this.mResumed) {
                    RunningState.this.mHaveData = false;
                    RunningState.this.mBackgroundHandler.removeMessages(1);
                    RunningState.this.mBackgroundHandler.sendEmptyMessage(1);
                    RunningState.this.mBackgroundHandler.removeMessages(2);
                    RunningState.this.mBackgroundHandler.sendEmptyMessage(2);
                } else {
                    this.usersChanged = true;
                }
            }
        }

        public boolean checkUsersChangedLocked() {
            boolean z = this.usersChanged;
            this.usersChanged = false;
            return z;
        }

        /* access modifiers changed from: package-private */
        public void register(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_STOPPED");
            intentFilter.addAction("android.intent.action.USER_STARTED");
            intentFilter.addAction("android.intent.action.USER_INFO_CHANGED");
            context.registerReceiverAsUser(this, UserHandle.ALL, intentFilter, null, null);
        }
    }

    /* access modifiers changed from: package-private */
    public static class UserState {
        Drawable mIcon;
        String mLabel;

        UserState() {
        }
    }

    /* access modifiers changed from: package-private */
    public static class BaseItem {
        long mActiveSince;
        boolean mBackground;
        int mCurSeq;
        String mCurSizeStr;
        String mDescription;
        CharSequence mDisplayLabel;
        final boolean mIsProcess;
        String mLabel;
        boolean mNeedDivider;
        PackageItemInfo mPackageInfo;
        long mSize;
        String mSizeStr;
        final int mUserId;

        public BaseItem(boolean z, int i) {
            this.mIsProcess = z;
            this.mUserId = i;
        }

        public Drawable loadIcon(Context context, RunningState runningState) {
            PackageItemInfo packageItemInfo = this.mPackageInfo;
            if (packageItemInfo == null) {
                return null;
            }
            return runningState.mPm.getUserBadgedIcon(packageItemInfo.loadUnbadgedIcon(runningState.mPm), new UserHandle(this.mUserId));
        }
    }

    /* access modifiers changed from: package-private */
    public static class ServiceItem extends BaseItem {
        MergedItem mMergedItem;
        ActivityManager.RunningServiceInfo mRunningService;
        ServiceInfo mServiceInfo;
        boolean mShownAsStarted;

        public ServiceItem(int i) {
            super(false, i);
        }
    }

    /* access modifiers changed from: package-private */
    public static class ProcessItem extends BaseItem {
        long mActiveSince;
        ProcessItem mClient;
        final SparseArray<ProcessItem> mDependentProcesses = new SparseArray<>();
        boolean mInteresting;
        boolean mIsStarted;
        boolean mIsSystem;
        int mLastNumDependentProcesses;
        MergedItem mMergedItem;
        int mPid;
        final String mProcessName;
        ActivityManager.RunningAppProcessInfo mRunningProcessInfo;
        int mRunningSeq;
        final HashMap<ComponentName, ServiceItem> mServices = new HashMap<>();
        final int mUid;

        public ProcessItem(Context context, int i, String str) {
            super(true, UserHandle.getUserId(i));
            this.mDescription = context.getResources().getString(C0017R$string.service_process_name, str);
            this.mUid = i;
            this.mProcessName = str;
        }

        /* access modifiers changed from: package-private */
        public void ensureLabel(PackageManager packageManager) {
            CharSequence text;
            if (this.mLabel == null) {
                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.mProcessName, 4194304);
                    if (applicationInfo.uid == this.mUid) {
                        CharSequence loadLabel = applicationInfo.loadLabel(packageManager);
                        this.mDisplayLabel = loadLabel;
                        this.mLabel = loadLabel.toString();
                        this.mPackageInfo = applicationInfo;
                        return;
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                }
                String[] packagesForUid = packageManager.getPackagesForUid(this.mUid);
                if (packagesForUid.length == 1) {
                    try {
                        ApplicationInfo applicationInfo2 = packageManager.getApplicationInfo(packagesForUid[0], 4194304);
                        CharSequence loadLabel2 = applicationInfo2.loadLabel(packageManager);
                        this.mDisplayLabel = loadLabel2;
                        this.mLabel = loadLabel2.toString();
                        this.mPackageInfo = applicationInfo2;
                        return;
                    } catch (PackageManager.NameNotFoundException unused2) {
                    }
                }
                for (String str : packagesForUid) {
                    try {
                        PackageInfo packageInfo = packageManager.getPackageInfo(str, 0);
                        if (!(packageInfo.sharedUserLabel == 0 || (text = packageManager.getText(str, packageInfo.sharedUserLabel, packageInfo.applicationInfo)) == null)) {
                            this.mDisplayLabel = text;
                            this.mLabel = text.toString();
                            this.mPackageInfo = packageInfo.applicationInfo;
                            return;
                        }
                    } catch (PackageManager.NameNotFoundException unused3) {
                    }
                }
                if (this.mServices.size() > 0) {
                    ApplicationInfo applicationInfo3 = this.mServices.values().iterator().next().mServiceInfo.applicationInfo;
                    this.mPackageInfo = applicationInfo3;
                    CharSequence loadLabel3 = applicationInfo3.loadLabel(packageManager);
                    this.mDisplayLabel = loadLabel3;
                    this.mLabel = loadLabel3.toString();
                    return;
                }
                try {
                    ApplicationInfo applicationInfo4 = packageManager.getApplicationInfo(packagesForUid[0], 4194304);
                    CharSequence loadLabel4 = applicationInfo4.loadLabel(packageManager);
                    this.mDisplayLabel = loadLabel4;
                    this.mLabel = loadLabel4.toString();
                    this.mPackageInfo = applicationInfo4;
                } catch (PackageManager.NameNotFoundException unused4) {
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean updateService(Context context, ActivityManager.RunningServiceInfo runningServiceInfo) {
            boolean z;
            PackageManager packageManager = context.getPackageManager();
            ServiceItem serviceItem = this.mServices.get(runningServiceInfo.service);
            boolean z2 = true;
            if (serviceItem == null) {
                serviceItem = new ServiceItem(this.mUserId);
                serviceItem.mRunningService = runningServiceInfo;
                try {
                    ServiceInfo serviceInfo = ActivityThread.getPackageManager().getServiceInfo(runningServiceInfo.service, 4194304, UserHandle.getUserId(runningServiceInfo.uid));
                    serviceItem.mServiceInfo = serviceInfo;
                    if (serviceInfo == null) {
                        Log.d("RunningService", "getServiceInfo returned null for: " + runningServiceInfo.service);
                        return false;
                    }
                } catch (RemoteException unused) {
                }
                serviceItem.mDisplayLabel = RunningState.makeLabel(packageManager, serviceItem.mRunningService.service.getClassName(), serviceItem.mServiceInfo);
                CharSequence charSequence = this.mDisplayLabel;
                this.mLabel = charSequence != null ? charSequence.toString() : null;
                serviceItem.mPackageInfo = serviceItem.mServiceInfo.applicationInfo;
                this.mServices.put(runningServiceInfo.service, serviceItem);
                z = true;
            } else {
                z = false;
            }
            serviceItem.mCurSeq = this.mCurSeq;
            serviceItem.mRunningService = runningServiceInfo;
            long j = runningServiceInfo.restarting == 0 ? runningServiceInfo.activeSince : -1;
            if (serviceItem.mActiveSince != j) {
                serviceItem.mActiveSince = j;
                z = true;
            }
            if (runningServiceInfo.clientPackage == null || runningServiceInfo.clientLabel == 0) {
                if (!serviceItem.mShownAsStarted) {
                    serviceItem.mShownAsStarted = true;
                } else {
                    z2 = z;
                }
                serviceItem.mDescription = context.getResources().getString(C0017R$string.service_started_by_app);
                return z2;
            }
            if (serviceItem.mShownAsStarted) {
                serviceItem.mShownAsStarted = false;
                z = true;
            }
            try {
                serviceItem.mDescription = context.getResources().getString(C0017R$string.service_client_name, packageManager.getResourcesForApplication(runningServiceInfo.clientPackage).getString(runningServiceInfo.clientLabel));
                return z;
            } catch (PackageManager.NameNotFoundException unused2) {
                serviceItem.mDescription = null;
                return z;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean updateSize(Context context, long j, int i) {
            long j2 = j * 1024;
            this.mSize = j2;
            if (this.mCurSeq == i) {
                String formatShortFileSize = Formatter.formatShortFileSize(context, j2);
                if (!formatShortFileSize.equals(this.mSizeStr)) {
                    this.mSizeStr = formatShortFileSize;
                }
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean buildDependencyChain(Context context, PackageManager packageManager, int i) {
            int size = this.mDependentProcesses.size();
            boolean z = false;
            for (int i2 = 0; i2 < size; i2++) {
                ProcessItem valueAt = this.mDependentProcesses.valueAt(i2);
                if (valueAt.mClient != this) {
                    valueAt.mClient = this;
                    z = true;
                }
                valueAt.mCurSeq = i;
                valueAt.ensureLabel(packageManager);
                z |= valueAt.buildDependencyChain(context, packageManager, i);
            }
            if (this.mLastNumDependentProcesses == this.mDependentProcesses.size()) {
                return z;
            }
            this.mLastNumDependentProcesses = this.mDependentProcesses.size();
            return true;
        }

        /* access modifiers changed from: package-private */
        public void addDependentProcesses(ArrayList<BaseItem> arrayList, ArrayList<ProcessItem> arrayList2) {
            int size = this.mDependentProcesses.size();
            for (int i = 0; i < size; i++) {
                ProcessItem valueAt = this.mDependentProcesses.valueAt(i);
                valueAt.addDependentProcesses(arrayList, arrayList2);
                arrayList.add(valueAt);
                if (valueAt.mPid > 0) {
                    arrayList2.add(valueAt);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public static class MergedItem extends BaseItem {
        final ArrayList<MergedItem> mChildren = new ArrayList<>();
        private int mLastNumProcesses = -1;
        private int mLastNumServices = -1;
        final ArrayList<ProcessItem> mOtherProcesses = new ArrayList<>();
        ProcessItem mProcess;
        final ArrayList<ServiceItem> mServices = new ArrayList<>();
        UserState mUser;

        MergedItem(int i) {
            super(false, i);
        }

        private void setDescription(Context context, int i, int i2) {
            if (this.mLastNumProcesses != i || this.mLastNumServices != i2) {
                this.mLastNumProcesses = i;
                this.mLastNumServices = i2;
                int i3 = C0017R$string.running_processes_item_description_s_s;
                if (i != 1) {
                    if (i2 != 1) {
                        i3 = C0017R$string.running_processes_item_description_p_p;
                    } else {
                        i3 = C0017R$string.running_processes_item_description_p_s;
                    }
                } else if (i2 != 1) {
                    i3 = C0017R$string.running_processes_item_description_s_p;
                }
                this.mDescription = context.getResources().getString(i3, Integer.valueOf(i), Integer.valueOf(i2));
            }
        }

        /* access modifiers changed from: package-private */
        public boolean update(Context context, boolean z) {
            this.mBackground = z;
            if (this.mUser != null) {
                this.mPackageInfo = this.mChildren.get(0).mProcess.mPackageInfo;
                UserState userState = this.mUser;
                String str = userState != null ? userState.mLabel : null;
                this.mLabel = str;
                this.mDisplayLabel = str;
                this.mActiveSince = -1;
                int i = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < this.mChildren.size(); i3++) {
                    MergedItem mergedItem = this.mChildren.get(i3);
                    i += mergedItem.mLastNumProcesses;
                    i2 += mergedItem.mLastNumServices;
                    long j = mergedItem.mActiveSince;
                    if (j >= 0 && this.mActiveSince < j) {
                        this.mActiveSince = j;
                    }
                }
                if (!this.mBackground) {
                    setDescription(context, i, i2);
                }
            } else {
                ProcessItem processItem = this.mProcess;
                this.mPackageInfo = processItem.mPackageInfo;
                this.mDisplayLabel = processItem.mDisplayLabel;
                this.mLabel = processItem.mLabel;
                if (!z) {
                    setDescription(context, (processItem.mPid > 0 ? 1 : 0) + this.mOtherProcesses.size(), this.mServices.size());
                }
                this.mActiveSince = -1;
                for (int i4 = 0; i4 < this.mServices.size(); i4++) {
                    long j2 = this.mServices.get(i4).mActiveSince;
                    if (j2 >= 0 && this.mActiveSince < j2) {
                        this.mActiveSince = j2;
                    }
                }
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean updateSize(Context context) {
            if (this.mUser != null) {
                this.mSize = 0;
                for (int i = 0; i < this.mChildren.size(); i++) {
                    MergedItem mergedItem = this.mChildren.get(i);
                    mergedItem.updateSize(context);
                    this.mSize += mergedItem.mSize;
                }
            } else {
                this.mSize = this.mProcess.mSize;
                for (int i2 = 0; i2 < this.mOtherProcesses.size(); i2++) {
                    this.mSize += this.mOtherProcesses.get(i2).mSize;
                }
            }
            String formatShortFileSize = Formatter.formatShortFileSize(context, this.mSize);
            if (!formatShortFileSize.equals(this.mSizeStr)) {
                this.mSizeStr = formatShortFileSize;
            }
            return false;
        }

        @Override // com.android.settings.applications.RunningState.BaseItem
        public Drawable loadIcon(Context context, RunningState runningState) {
            UserState userState = this.mUser;
            if (userState == null) {
                return super.loadIcon(context, runningState);
            }
            Drawable drawable = userState.mIcon;
            if (drawable == null) {
                return context.getDrawable(17302690);
            }
            Drawable.ConstantState constantState = drawable.getConstantState();
            if (constantState == null) {
                return this.mUser.mIcon;
            }
            return constantState.newDrawable();
        }
    }

    /* access modifiers changed from: package-private */
    public class ServiceProcessComparator implements Comparator<ProcessItem> {
        ServiceProcessComparator() {
        }

        public int compare(ProcessItem processItem, ProcessItem processItem2) {
            int i = processItem.mUserId;
            int i2 = processItem2.mUserId;
            if (i != i2) {
                int i3 = RunningState.this.mMyUserId;
                if (i == i3) {
                    return -1;
                }
                return (i2 != i3 && i < i2) ? -1 : 1;
            }
            boolean z = processItem.mIsStarted;
            if (z != processItem2.mIsStarted) {
                return z ? -1 : 1;
            }
            boolean z2 = processItem.mIsSystem;
            if (z2 != processItem2.mIsSystem) {
                return z2 ? 1 : -1;
            }
            long j = processItem.mActiveSince;
            long j2 = processItem2.mActiveSince;
            if (j != j2) {
                return j > j2 ? -1 : 1;
            }
            return 0;
        }
    }

    static CharSequence makeLabel(PackageManager packageManager, String str, PackageItemInfo packageItemInfo) {
        CharSequence loadLabel;
        if (packageItemInfo != null && ((packageItemInfo.labelRes != 0 || packageItemInfo.nonLocalizedLabel != null) && (loadLabel = packageItemInfo.loadLabel(packageManager)) != null)) {
            return loadLabel;
        }
        int lastIndexOf = str.lastIndexOf(46);
        return lastIndexOf >= 0 ? str.substring(lastIndexOf + 1, str.length()) : str;
    }

    static RunningState getInstance(Context context) {
        RunningState runningState;
        synchronized (sGlobalLock) {
            if (sInstance == null) {
                sInstance = new RunningState(context);
            }
            runningState = sInstance;
        }
        return runningState;
    }

    private RunningState(Context context) {
        new ArrayList();
        this.mMergedItems = new ArrayList<>();
        this.mBackgroundItems = new ArrayList<>();
        this.mUserBackgroundItems = new ArrayList<>();
        this.mHandler = new Handler() {
            /* class com.android.settings.applications.RunningState.AnonymousClass2 */
            int mNextUpdate = 0;

            /* JADX WARNING: Code restructure failed: missing block: B:12:0x0017, code lost:
                removeMessages(4);
                sendMessageDelayed(obtainMessage(4), 1000);
                r3 = r2.this$0.mRefreshUiListener;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:13:0x0027, code lost:
                if (r3 == null) goto L_?;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:14:0x0029, code lost:
                r3.onRefreshUi(r2.mNextUpdate);
                r2.mNextUpdate = 0;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
                return;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void handleMessage(android.os.Message r3) {
                /*
                    r2 = this;
                    int r0 = r3.what
                    r1 = 3
                    if (r0 == r1) goto L_0x0035
                    r3 = 4
                    if (r0 == r3) goto L_0x0009
                    goto L_0x003e
                L_0x0009:
                    com.android.settings.applications.RunningState r0 = com.android.settings.applications.RunningState.this
                    java.lang.Object r0 = r0.mLock
                    monitor-enter(r0)
                    com.android.settings.applications.RunningState r1 = com.android.settings.applications.RunningState.this     // Catch:{ all -> 0x0032 }
                    boolean r1 = r1.mResumed     // Catch:{ all -> 0x0032 }
                    if (r1 != 0) goto L_0x0016
                    monitor-exit(r0)     // Catch:{ all -> 0x0032 }
                    return
                L_0x0016:
                    monitor-exit(r0)     // Catch:{ all -> 0x0032 }
                    r2.removeMessages(r3)
                    android.os.Message r3 = r2.obtainMessage(r3)
                    r0 = 1000(0x3e8, double:4.94E-321)
                    r2.sendMessageDelayed(r3, r0)
                    com.android.settings.applications.RunningState r3 = com.android.settings.applications.RunningState.this
                    com.android.settings.applications.RunningState$OnRefreshUiListener r3 = r3.mRefreshUiListener
                    if (r3 == 0) goto L_0x003e
                    int r0 = r2.mNextUpdate
                    r3.onRefreshUi(r0)
                    r3 = 0
                    r2.mNextUpdate = r3
                    goto L_0x003e
                L_0x0032:
                    r2 = move-exception
                    monitor-exit(r0)
                    throw r2
                L_0x0035:
                    int r3 = r3.arg1
                    if (r3 == 0) goto L_0x003b
                    r3 = 2
                    goto L_0x003c
                L_0x003b:
                    r3 = 1
                L_0x003c:
                    r2.mNextUpdate = r3
                L_0x003e:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.RunningState.AnonymousClass2.handleMessage(android.os.Message):void");
            }
        };
        this.mUmBroadcastReceiver = new UserManagerBroadcastReceiver();
        Context applicationContext = context.getApplicationContext();
        this.mApplicationContext = applicationContext;
        this.mAm = (ActivityManager) applicationContext.getSystemService("activity");
        this.mPm = this.mApplicationContext.getPackageManager();
        this.mUm = (UserManager) this.mApplicationContext.getSystemService("user");
        int myUserId = UserHandle.myUserId();
        this.mMyUserId = myUserId;
        UserInfo userInfo = this.mUm.getUserInfo(myUserId);
        this.mHideManagedProfiles = userInfo == null || !userInfo.canHaveProfile();
        this.mResumed = false;
        HandlerThread handlerThread = new HandlerThread("RunningState:Background");
        this.mBackgroundThread = handlerThread;
        handlerThread.start();
        this.mBackgroundHandler = new BackgroundHandler(this.mBackgroundThread.getLooper());
        this.mUmBroadcastReceiver.register(this.mApplicationContext);
    }

    /* access modifiers changed from: package-private */
    public void resume(OnRefreshUiListener onRefreshUiListener) {
        synchronized (this.mLock) {
            this.mResumed = true;
            this.mRefreshUiListener = onRefreshUiListener;
            boolean checkUsersChangedLocked = this.mUmBroadcastReceiver.checkUsersChangedLocked();
            boolean applyNewConfig = this.mInterestingConfigChanges.applyNewConfig(this.mApplicationContext.getResources());
            if (checkUsersChangedLocked || applyNewConfig) {
                this.mHaveData = false;
                this.mBackgroundHandler.removeMessages(1);
                this.mBackgroundHandler.removeMessages(2);
                this.mBackgroundHandler.sendEmptyMessage(1);
            }
            if (!this.mBackgroundHandler.hasMessages(2)) {
                this.mBackgroundHandler.sendEmptyMessage(2);
            }
            this.mHandler.sendEmptyMessage(4);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateNow() {
        synchronized (this.mLock) {
            this.mBackgroundHandler.removeMessages(2);
            this.mBackgroundHandler.sendEmptyMessage(2);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasData() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mHaveData;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:2:0x0003 */
    /* JADX WARNING: Removed duplicated region for block: B:2:0x0003 A[LOOP:0: B:2:0x0003->B:12:0x0003, LOOP_START, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void waitForData() {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
        L_0x0003:
            boolean r1 = r4.mHaveData     // Catch:{ all -> 0x0011 }
            if (r1 != 0) goto L_0x000f
            java.lang.Object r1 = r4.mLock     // Catch:{ InterruptedException -> 0x0003 }
            r2 = 0
            r1.wait(r2)     // Catch:{ InterruptedException -> 0x0003 }
            goto L_0x0003
        L_0x000f:
            monitor-exit(r0)
            return
        L_0x0011:
            r4 = move-exception
            monitor-exit(r0)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.RunningState.waitForData():void");
    }

    /* access modifiers changed from: package-private */
    public void pause() {
        synchronized (this.mLock) {
            this.mResumed = false;
            this.mRefreshUiListener = null;
            this.mHandler.removeMessages(4);
        }
    }

    private boolean isInterestingProcess(ActivityManager.RunningAppProcessInfo runningAppProcessInfo) {
        int i;
        int i2 = runningAppProcessInfo.flags;
        if ((i2 & 1) != 0) {
            return true;
        }
        if ((i2 & 2) != 0 || (i = runningAppProcessInfo.importance) < 100 || i >= 350 || runningAppProcessInfo.importanceReasonCode != 0) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void reset() {
        this.mServiceProcessesByName.clear();
        this.mServiceProcessesByPid.clear();
        this.mInterestingProcesses.clear();
        this.mRunningProcesses.clear();
        this.mProcessItems.clear();
        this.mAllProcessItems.clear();
    }

    private void addOtherUserItem(Context context, ArrayList<MergedItem> arrayList, SparseArray<MergedItem> sparseArray, MergedItem mergedItem) {
        MergedItem mergedItem2 = sparseArray.get(mergedItem.mUserId);
        if (mergedItem2 == null || mergedItem2.mCurSeq != this.mSequence) {
            UserInfo userInfo = this.mUm.getUserInfo(mergedItem.mUserId);
            if (userInfo != null) {
                if (!this.mHideManagedProfiles || !userInfo.isManagedProfile()) {
                    if (mergedItem2 == null) {
                        mergedItem2 = new MergedItem(mergedItem.mUserId);
                        sparseArray.put(mergedItem.mUserId, mergedItem2);
                    } else {
                        mergedItem2.mChildren.clear();
                    }
                    mergedItem2.mCurSeq = this.mSequence;
                    UserState userState = new UserState();
                    mergedItem2.mUser = userState;
                    userState.mIcon = Utils.getUserIcon(context, this.mUm, userInfo);
                    mergedItem2.mUser.mLabel = Utils.getUserLabel(context, userInfo);
                    arrayList.add(mergedItem2);
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        mergedItem2.mChildren.add(mergedItem);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x067e  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0698  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x06e1  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x06eb A[LOOP:29: B:390:0x06e3->B:392:0x06eb, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x06fc A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean update(android.content.Context r22, android.app.ActivityManager r23) {
        /*
        // Method dump skipped, instructions count: 1822
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.RunningState.update(android.content.Context, android.app.ActivityManager):boolean");
    }

    /* access modifiers changed from: package-private */
    public void setWatchingBackgroundItems(boolean z) {
        synchronized (this.mLock) {
            this.mWatchingBackgroundItems = z;
        }
    }

    /* access modifiers changed from: package-private */
    public ArrayList<MergedItem> getCurrentMergedItems() {
        ArrayList<MergedItem> arrayList;
        synchronized (this.mLock) {
            arrayList = this.mMergedItems;
        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public ArrayList<MergedItem> getCurrentBackgroundItems() {
        ArrayList<MergedItem> arrayList;
        synchronized (this.mLock) {
            arrayList = this.mUserBackgroundItems;
        }
        return arrayList;
    }
}
