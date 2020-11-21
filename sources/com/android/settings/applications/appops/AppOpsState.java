package com.android.settings.applications.appops;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AppOpsState {
    public static final OpsTemplate DEVICE_TEMPLATE = new OpsTemplate(new int[]{11, 25, 13, 23, 24, 40, 46, 47, 49, 50}, new boolean[]{false, true, true, true, true, true, false, false, false, false});
    public static final Comparator<AppOpEntry> LABEL_COMPARATOR = new Comparator<AppOpEntry>() {
        /* class com.android.settings.applications.appops.AppOpsState.AnonymousClass2 */
        private final Collator sCollator = Collator.getInstance();

        public int compare(AppOpEntry appOpEntry, AppOpEntry appOpEntry2) {
            return this.sCollator.compare(appOpEntry.getAppEntry().getLabel(), appOpEntry2.getAppEntry().getLabel());
        }
    };
    public static final OpsTemplate LOCATION_TEMPLATE = new OpsTemplate(new int[]{0, 1, 2, 10, 12, 41, 42}, new boolean[]{true, true, false, false, false, false, false});
    public static final OpsTemplate MEDIA_TEMPLATE = new OpsTemplate(new int[]{3, 26, 27, 28, 31, 32, 33, 34, 35, 36, 37, 38, 39, 64, 44}, new boolean[]{false, true, true, false, false, false, false, false, false, false, false, false, false, false});
    public static final OpsTemplate MESSAGING_TEMPLATE = new OpsTemplate(new int[]{14, 16, 17, 18, 19, 15, 20, 21, 22}, new boolean[]{true, true, true, true, true, true, true, true, true});
    public static final OpsTemplate PERSONAL_TEMPLATE = new OpsTemplate(new int[]{4, 5, 6, 7, 8, 9, 29, 30}, new boolean[]{true, true, true, true, true, true, false, false});
    public static final OpsTemplate RUN_IN_BACKGROUND_TEMPLATE = new OpsTemplate(new int[]{63}, new boolean[]{false});
    final AppOpsManager mAppOps;
    final Context mContext;
    final PackageManager mPm;

    public AppOpsState(Context context) {
        this.mContext = context;
        this.mAppOps = (AppOpsManager) context.getSystemService("appops");
        this.mPm = context.getPackageManager();
        context.getResources().getTextArray(C0003R$array.app_ops_summaries);
        context.getResources().getTextArray(C0003R$array.app_ops_labels);
    }

    public static class OpsTemplate implements Parcelable {
        public static final Parcelable.Creator<OpsTemplate> CREATOR = new Parcelable.Creator<OpsTemplate>() {
            /* class com.android.settings.applications.appops.AppOpsState.OpsTemplate.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public OpsTemplate createFromParcel(Parcel parcel) {
                return new OpsTemplate(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public OpsTemplate[] newArray(int i) {
                return new OpsTemplate[i];
            }
        };
        public final int[] ops;
        public final boolean[] showPerms;

        public int describeContents() {
            return 0;
        }

        public OpsTemplate(int[] iArr, boolean[] zArr) {
            this.ops = iArr;
            this.showPerms = zArr;
        }

        OpsTemplate(Parcel parcel) {
            this.ops = parcel.createIntArray();
            this.showPerms = parcel.createBooleanArray();
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeIntArray(this.ops);
            parcel.writeBooleanArray(this.showPerms);
        }
    }

    static {
        new Comparator<AppOpEntry>() {
            /* class com.android.settings.applications.appops.AppOpsState.AnonymousClass1 */
            private final Collator sCollator = Collator.getInstance();

            public int compare(AppOpEntry appOpEntry, AppOpEntry appOpEntry2) {
                if (appOpEntry.getSwitchOrder() != appOpEntry2.getSwitchOrder()) {
                    return appOpEntry.getSwitchOrder() < appOpEntry2.getSwitchOrder() ? -1 : 1;
                }
                if (appOpEntry.isRunning() != appOpEntry2.isRunning()) {
                    return appOpEntry.isRunning() ? -1 : 1;
                }
                if (appOpEntry.getTime() != appOpEntry2.getTime()) {
                    return appOpEntry.getTime() > appOpEntry2.getTime() ? -1 : 1;
                }
                return this.sCollator.compare(appOpEntry.getAppEntry().getLabel(), appOpEntry2.getAppEntry().getLabel());
            }
        };
    }

    public static class AppEntry {
        private final File mApkFile;
        private Drawable mIcon;
        private final ApplicationInfo mInfo;
        private String mLabel;
        private boolean mMounted;
        private final SparseArray<AppOpEntry> mOpSwitches = new SparseArray<>();
        private final SparseArray<AppOpsManager.OpEntry> mOps = new SparseArray<>();
        private final AppOpsState mState;

        public AppEntry(AppOpsState appOpsState, ApplicationInfo applicationInfo) {
            this.mState = appOpsState;
            this.mInfo = applicationInfo;
            this.mApkFile = new File(applicationInfo.sourceDir);
        }

        public void addOp(AppOpEntry appOpEntry, AppOpsManager.OpEntry opEntry) {
            this.mOps.put(opEntry.getOp(), opEntry);
            this.mOpSwitches.put(AppOpsManager.opToSwitch(opEntry.getOp()), appOpEntry);
        }

        public boolean hasOp(int i) {
            return this.mOps.indexOfKey(i) >= 0;
        }

        public AppOpEntry getOpSwitch(int i) {
            return this.mOpSwitches.get(AppOpsManager.opToSwitch(i));
        }

        public ApplicationInfo getApplicationInfo() {
            return this.mInfo;
        }

        public String getLabel() {
            return this.mLabel;
        }

        public Drawable getIcon() {
            Drawable drawable = this.mIcon;
            if (drawable == null) {
                if (this.mApkFile.exists()) {
                    Drawable loadIcon = this.mInfo.loadIcon(this.mState.mPm);
                    this.mIcon = loadIcon;
                    return loadIcon;
                }
                this.mMounted = false;
            } else if (this.mMounted) {
                return drawable;
            } else {
                if (this.mApkFile.exists()) {
                    this.mMounted = true;
                    Drawable loadIcon2 = this.mInfo.loadIcon(this.mState.mPm);
                    this.mIcon = loadIcon2;
                    return loadIcon2;
                }
            }
            return this.mState.mContext.getDrawable(17301651);
        }

        public String toString() {
            return this.mLabel;
        }

        /* access modifiers changed from: package-private */
        public void loadLabel(Context context) {
            if (this.mLabel != null && this.mMounted) {
                return;
            }
            if (!this.mApkFile.exists()) {
                this.mMounted = false;
                this.mLabel = this.mInfo.packageName;
                return;
            }
            this.mMounted = true;
            CharSequence loadLabel = this.mInfo.loadLabel(context.getPackageManager());
            this.mLabel = loadLabel != null ? loadLabel.toString() : this.mInfo.packageName;
        }
    }

    public static class AppOpEntry {
        private final AppEntry mApp;
        private final ArrayList<AppOpsManager.OpEntry> mOps = new ArrayList<>();
        private int mOverriddenPrimaryMode = -1;
        private final ArrayList<AppOpsManager.OpEntry> mSwitchOps = new ArrayList<>();
        private final int mSwitchOrder;

        public AppOpEntry(AppOpsManager.PackageOps packageOps, AppOpsManager.OpEntry opEntry, AppEntry appEntry, int i) {
            this.mApp = appEntry;
            this.mSwitchOrder = i;
            appEntry.addOp(this, opEntry);
            this.mOps.add(opEntry);
            this.mSwitchOps.add(opEntry);
        }

        private static void addOp(ArrayList<AppOpsManager.OpEntry> arrayList, AppOpsManager.OpEntry opEntry) {
            for (int i = 0; i < arrayList.size(); i++) {
                AppOpsManager.OpEntry opEntry2 = arrayList.get(i);
                if (opEntry2.isRunning() != opEntry.isRunning()) {
                    if (opEntry.isRunning()) {
                        arrayList.add(i, opEntry);
                        return;
                    }
                } else if (opEntry2.getTime() < opEntry.getTime()) {
                    arrayList.add(i, opEntry);
                    return;
                }
            }
            arrayList.add(opEntry);
        }

        public void addOp(AppOpsManager.OpEntry opEntry) {
            this.mApp.addOp(this, opEntry);
            addOp(this.mOps, opEntry);
            if (this.mApp.getOpSwitch(AppOpsManager.opToSwitch(opEntry.getOp())) == null) {
                addOp(this.mSwitchOps, opEntry);
            }
        }

        public AppEntry getAppEntry() {
            return this.mApp;
        }

        public int getSwitchOrder() {
            return this.mSwitchOrder;
        }

        public AppOpsManager.OpEntry getOpEntry(int i) {
            return this.mOps.get(i);
        }

        public int getPrimaryOpMode() {
            int i = this.mOverriddenPrimaryMode;
            return i >= 0 ? i : this.mOps.get(0).getMode();
        }

        public void overridePrimaryOpMode(int i) {
            this.mOverriddenPrimaryMode = i;
        }

        public CharSequence getTimeText(Resources resources, boolean z) {
            if (isRunning()) {
                return resources.getText(C0017R$string.app_ops_running);
            }
            if (getTime() > 0) {
                return DateUtils.getRelativeTimeSpanString(getTime(), System.currentTimeMillis(), 60000, 262144);
            }
            return z ? resources.getText(C0017R$string.app_ops_never_used) : "";
        }

        public boolean isRunning() {
            return this.mOps.get(0).isRunning();
        }

        public long getTime() {
            return this.mOps.get(0).getTime();
        }

        public String toString() {
            return this.mApp.getLabel();
        }
    }

    private void addOp(List<AppOpEntry> list, AppOpsManager.PackageOps packageOps, AppEntry appEntry, AppOpsManager.OpEntry opEntry, boolean z, int i) {
        if (z && list.size() > 0) {
            boolean z2 = true;
            AppOpEntry appOpEntry = list.get(list.size() - 1);
            if (appOpEntry.getAppEntry() == appEntry) {
                boolean z3 = appOpEntry.getTime() != 0;
                if (opEntry.getTime() == 0) {
                    z2 = false;
                }
                if (z3 == z2) {
                    appOpEntry.addOp(opEntry);
                    return;
                }
            }
        }
        AppOpEntry opSwitch = appEntry.getOpSwitch(opEntry.getOp());
        if (opSwitch != null) {
            opSwitch.addOp(opEntry);
        } else {
            list.add(new AppOpEntry(packageOps, opEntry, appEntry, i));
        }
    }

    public AppOpsManager getAppOpsManager() {
        return this.mAppOps;
    }

    private AppEntry getAppEntry(Context context, HashMap<String, AppEntry> hashMap, String str, ApplicationInfo applicationInfo) {
        AppEntry appEntry = hashMap.get(str);
        if (appEntry != null) {
            return appEntry;
        }
        if (applicationInfo == null) {
            try {
                applicationInfo = this.mPm.getApplicationInfo(str, 4194816);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("AppOpsState", "Unable to find info for package " + str);
                return null;
            }
        }
        AppEntry appEntry2 = new AppEntry(this, applicationInfo);
        appEntry2.loadLabel(context);
        hashMap.put(str, appEntry2);
        return appEntry2;
    }

    public List<AppOpEntry> buildState(OpsTemplate opsTemplate, int i, String str, Comparator<AppOpEntry> comparator) {
        int[] iArr;
        List list;
        List<PackageInfo> list2;
        int i2;
        List<PackageInfo> list3;
        int i3;
        PackageInfo packageInfo;
        AppEntry appEntry;
        int i4;
        int i5;
        List<PackageInfo> list4;
        PackageInfo packageInfo2;
        AppEntry appEntry2;
        AppOpsManager.PackageOps packageOps;
        int i6;
        List<PackageInfo> list5;
        AppOpsManager.PackageOps packageOps2;
        int i7;
        int i8;
        String opToPermission;
        AppOpsState appOpsState = this;
        Context context = appOpsState.mContext;
        HashMap<String, AppEntry> hashMap = new HashMap<>();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        int[] iArr2 = new int[100];
        int i9 = 0;
        int i10 = 0;
        while (true) {
            iArr = opsTemplate.ops;
            if (i10 >= iArr.length) {
                break;
            }
            if (opsTemplate.showPerms[i10] && (opToPermission = AppOpsManager.opToPermission(iArr[i10])) != null && !arrayList2.contains(opToPermission)) {
                arrayList2.add(opToPermission);
                arrayList3.add(Integer.valueOf(opsTemplate.ops[i10]));
                iArr2[opsTemplate.ops[i10]] = i10;
            }
            i10++;
        }
        if (str != null) {
            list = appOpsState.mAppOps.getOpsForPackage(i, str, iArr);
        } else {
            list = appOpsState.mAppOps.getPackagesForOps(iArr);
        }
        List list6 = list;
        AppOpsManager.PackageOps packageOps3 = null;
        if (list6 != null) {
            int i11 = 0;
            while (i11 < list6.size()) {
                AppOpsManager.PackageOps packageOps4 = (AppOpsManager.PackageOps) list6.get(i11);
                AppEntry appEntry3 = appOpsState.getAppEntry(context, hashMap, packageOps4.getPackageName(), packageOps3);
                if (appEntry3 != null) {
                    int i12 = 0;
                    while (i12 < packageOps4.getOps().size()) {
                        AppOpsManager.OpEntry opEntry = (AppOpsManager.OpEntry) packageOps4.getOps().get(i12);
                        boolean z = str == null;
                        if (str == null) {
                            i8 = 0;
                        } else {
                            i8 = iArr2[opEntry.getOp()];
                        }
                        addOp(arrayList, packageOps4, appEntry3, opEntry, z, i8);
                        i12++;
                        packageOps3 = packageOps3;
                        list6 = list6;
                        i11 = i11;
                    }
                }
                i11++;
                packageOps3 = packageOps3;
                list6 = list6;
            }
        }
        if (str != null) {
            list2 = new ArrayList<>();
            try {
                list2.add(appOpsState.mPm.getPackageInfo(str, 4096));
            } catch (PackageManager.NameNotFoundException unused) {
            }
        } else {
            String[] strArr = new String[arrayList2.size()];
            arrayList2.toArray(strArr);
            list2 = appOpsState.mPm.getPackagesHoldingPermissions(strArr, 0);
        }
        List<PackageInfo> list7 = list2;
        int i13 = 0;
        while (i13 < list7.size()) {
            PackageInfo packageInfo3 = list7.get(i13);
            AppEntry appEntry4 = appOpsState.getAppEntry(context, hashMap, packageInfo3.packageName, packageInfo3.applicationInfo);
            if (appEntry4 == null || packageInfo3.requestedPermissions == null) {
                i2 = i13;
                list3 = list7;
                i3 = i9;
            } else {
                int i14 = i9;
                AppOpsManager.PackageOps packageOps5 = packageOps3;
                AppOpsManager.PackageOps packageOps6 = packageOps5;
                while (i14 < packageInfo3.requestedPermissions.length) {
                    int[] iArr3 = packageInfo3.requestedPermissionsFlags;
                    if (iArr3 == null || (iArr3[i14] & 2) != 0) {
                        AppOpsManager.PackageOps packageOps7 = packageOps6;
                        int i15 = 0;
                        while (i15 < arrayList2.size()) {
                            if (((String) arrayList2.get(i15)).equals(packageInfo3.requestedPermissions[i14]) && !appEntry4.hasOp(((Integer) arrayList3.get(i15)).intValue())) {
                                if (packageOps5 == null) {
                                    AppOpsManager.PackageOps arrayList4 = new ArrayList();
                                    i6 = i14;
                                    packageOps2 = arrayList4;
                                    packageOps = new AppOpsManager.PackageOps(packageInfo3.packageName, packageInfo3.applicationInfo.uid, arrayList4);
                                } else {
                                    i6 = i14;
                                    packageOps = packageOps7;
                                    packageOps2 = packageOps5;
                                }
                                AppOpsManager.OpEntry opEntry2 = new AppOpsManager.OpEntry(((Integer) arrayList3.get(i15)).intValue(), 0, Collections.emptyMap());
                                packageOps2.add(opEntry2);
                                boolean z2 = str == null;
                                if (str == null) {
                                    i7 = 0;
                                } else {
                                    i7 = iArr2[opEntry2.getOp()];
                                }
                                appEntry2 = appEntry4;
                                packageInfo2 = packageInfo3;
                                list5 = list7;
                                addOp(arrayList, packageOps, appEntry4, opEntry2, z2, i7);
                                packageOps5 = packageOps2;
                            } else {
                                packageOps = packageOps7;
                                i6 = i14;
                                appEntry2 = appEntry4;
                                packageInfo2 = packageInfo3;
                                list5 = list7;
                            }
                            i15++;
                            list7 = list5;
                            i13 = i13;
                            i14 = i6;
                            packageOps7 = packageOps;
                            appEntry4 = appEntry2;
                            packageInfo3 = packageInfo2;
                        }
                        i4 = i14;
                        appEntry = appEntry4;
                        packageInfo = packageInfo3;
                        i5 = i13;
                        list4 = list7;
                        packageOps6 = packageOps7;
                    } else {
                        i4 = i14;
                        appEntry = appEntry4;
                        packageInfo = packageInfo3;
                        i5 = i13;
                        list4 = list7;
                    }
                    i14 = i4 + 1;
                    list7 = list4;
                    i13 = i5;
                    appEntry4 = appEntry;
                    packageInfo3 = packageInfo;
                }
                i2 = i13;
                list3 = list7;
                i3 = 0;
            }
            i13 = i2 + 1;
            i9 = i3;
            list7 = list3;
            appOpsState = this;
        }
        Collections.sort(arrayList, comparator);
        return arrayList;
    }
}
