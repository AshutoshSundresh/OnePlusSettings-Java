package com.android.settings.fuelgauge;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.BatteryStats;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.internal.os.BatterySipper;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class BatteryEntry {
    static final ArrayList<BatteryEntry> mRequestQueue = new ArrayList<>();
    private static NameAndIconLoader mRequestThread;
    static Locale sCurrentLocale = null;
    static Handler sHandler;
    static final HashMap<String, UidToDetail> sUidCache = new HashMap<>();
    public final Context context;
    public String defaultPackageName;
    public Drawable icon;
    public int iconId;
    public String name;
    public final BatterySipper sipper;

    private static class NameAndIconLoader extends Thread {
        private boolean mAbort = false;

        public NameAndIconLoader() {
            super("BatteryUsage Icon Loader");
        }

        public void abort() {
            this.mAbort = true;
        }

        public void run() {
            BatteryEntry remove;
            while (true) {
                synchronized (BatteryEntry.mRequestQueue) {
                    if (BatteryEntry.mRequestQueue.isEmpty()) {
                        break;
                    } else if (this.mAbort) {
                        break;
                    } else {
                        remove = BatteryEntry.mRequestQueue.remove(0);
                    }
                }
                remove.loadNameAndIcon();
            }
            if (BatteryEntry.sHandler != null) {
                BatteryEntry.sHandler.sendEmptyMessage(2);
            }
            BatteryEntry.mRequestQueue.clear();
        }
    }

    public static void startRequestQueue() {
        if (sHandler != null) {
            synchronized (mRequestQueue) {
                if (!mRequestQueue.isEmpty()) {
                    if (mRequestThread != null) {
                        mRequestThread.abort();
                    }
                    NameAndIconLoader nameAndIconLoader = new NameAndIconLoader();
                    mRequestThread = nameAndIconLoader;
                    nameAndIconLoader.setPriority(1);
                    mRequestThread.start();
                    mRequestQueue.notify();
                }
            }
        }
    }

    public static void stopRequestQueue() {
        synchronized (mRequestQueue) {
            if (mRequestThread != null) {
                mRequestThread.abort();
                mRequestThread = null;
                sHandler = null;
            }
        }
    }

    public static void clearUidCache() {
        sUidCache.clear();
    }

    /* access modifiers changed from: package-private */
    public static class UidToDetail {
        Drawable icon;
        String name;
        String packageName;

        UidToDetail() {
        }
    }

    public BatteryEntry(Context context2, Handler handler, UserManager userManager, BatterySipper batterySipper) {
        BatteryStats.Uid uid;
        sHandler = handler;
        this.context = context2;
        this.sipper = batterySipper;
        switch (AnonymousClass1.$SwitchMap$com$android$internal$os$BatterySipper$DrainType[batterySipper.drainType.ordinal()]) {
            case 1:
                this.name = context2.getResources().getString(C0017R$string.power_idle);
                this.iconId = C0008R$drawable.ic_settings_phone_idle;
                break;
            case 2:
                this.name = context2.getResources().getString(C0017R$string.power_cell);
                this.iconId = C0008R$drawable.ic_cellular_1_bar;
                break;
            case 3:
                this.name = context2.getResources().getString(C0017R$string.power_phone);
                this.iconId = C0008R$drawable.ic_settings_voice_calls;
                break;
            case 4:
                this.name = context2.getResources().getString(C0017R$string.power_wifi);
                this.iconId = C0008R$drawable.ic_settings_wireless;
                break;
            case 5:
                this.name = context2.getResources().getString(C0017R$string.power_bluetooth);
                this.iconId = 17302820;
                break;
            case 6:
                this.name = context2.getResources().getString(C0017R$string.power_screen);
                this.iconId = C0008R$drawable.ic_settings_display;
                break;
            case 7:
                this.name = context2.getResources().getString(C0017R$string.power_flashlight);
                this.iconId = C0008R$drawable.ic_settings_display;
                break;
            case 8:
                PackageManager packageManager = context2.getPackageManager();
                String[] packagesForUid = packageManager.getPackagesForUid(batterySipper.uidObj.getUid());
                batterySipper.mPackages = packagesForUid;
                if (packagesForUid != null && packagesForUid.length == 1) {
                    String str = packageManager.getPackagesForUid(batterySipper.uidObj.getUid())[0];
                    this.defaultPackageName = str;
                    try {
                        this.name = packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 0)).toString();
                        break;
                    } catch (PackageManager.NameNotFoundException unused) {
                        Log.d("BatteryEntry", "PackageManager failed to retrieve ApplicationInfo for: " + this.defaultPackageName);
                        this.name = this.defaultPackageName;
                        break;
                    }
                } else {
                    this.name = batterySipper.packageWithHighestDrain;
                    break;
                }
                break;
            case 9:
                UserInfo userInfo = userManager.getUserInfo(batterySipper.userId);
                if (userInfo == null) {
                    this.icon = null;
                    this.name = context2.getResources().getString(C0017R$string.running_process_item_removed_user_label);
                    break;
                } else {
                    this.icon = Utils.getUserIcon(context2, userManager, userInfo);
                    this.name = Utils.getUserLabel(context2, userInfo);
                    break;
                }
            case 10:
                this.name = context2.getResources().getString(C0017R$string.power_unaccounted);
                this.iconId = C0008R$drawable.ic_android;
                break;
            case 11:
                this.name = context2.getResources().getString(C0017R$string.power_overcounted);
                this.iconId = C0008R$drawable.ic_android;
                break;
            case 12:
                this.name = context2.getResources().getString(C0017R$string.power_camera);
                this.iconId = C0008R$drawable.ic_settings_camera;
                break;
            case 13:
                this.name = context2.getResources().getString(C0017R$string.ambient_display_screen_title);
                this.iconId = C0008R$drawable.ic_settings_aod;
                break;
        }
        int i = this.iconId;
        if (i > 0) {
            this.icon = context2.getDrawable(i);
        }
        if ((this.name == null || this.iconId == 0) && (uid = this.sipper.uidObj) != null) {
            getQuickNameIconForUid(uid.getUid());
        }
    }

    /* renamed from: com.android.settings.fuelgauge.BatteryEntry$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$os$BatterySipper$DrainType;

        /* JADX WARNING: Can't wrap try/catch for region: R(26:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|(3:25|26|28)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0078 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0084 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x0090 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
            // Method dump skipped, instructions count: 157
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.fuelgauge.BatteryEntry.AnonymousClass1.<clinit>():void");
        }
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public String getLabel() {
        return this.name;
    }

    /* access modifiers changed from: package-private */
    public void getQuickNameIconForUid(int i) {
        Locale locale = Locale.getDefault();
        if (sCurrentLocale != locale) {
            clearUidCache();
            sCurrentLocale = locale;
        }
        String num = Integer.toString(i);
        if (sUidCache.containsKey(num)) {
            UidToDetail uidToDetail = sUidCache.get(num);
            this.defaultPackageName = uidToDetail.packageName;
            this.name = uidToDetail.name;
            this.icon = uidToDetail.icon;
            return;
        }
        PackageManager packageManager = this.context.getPackageManager();
        this.icon = packageManager.getDefaultActivityIcon();
        if (packageManager.getPackagesForUid(i) == null) {
            if (i == 0) {
                this.name = this.context.getResources().getString(C0017R$string.process_kernel_label);
            } else if ("mediaserver".equals(this.name)) {
                this.name = this.context.getResources().getString(C0017R$string.process_mediaserver_label);
            } else if ("dex2oat".equals(this.name)) {
                this.name = this.context.getResources().getString(C0017R$string.process_dex2oat_label);
            }
            int i2 = C0008R$drawable.ic_power_system;
            this.iconId = i2;
            this.icon = this.context.getDrawable(i2);
        }
        if (sHandler != null) {
            synchronized (mRequestQueue) {
                mRequestQueue.add(this);
            }
        }
    }

    public void loadNameAndIcon() {
        CharSequence text;
        if (this.sipper.uidObj != null) {
            PackageManager packageManager = this.context.getPackageManager();
            int uid = this.sipper.uidObj.getUid();
            BatterySipper batterySipper = this.sipper;
            if (batterySipper.mPackages == null) {
                batterySipper.mPackages = packageManager.getPackagesForUid(uid);
            }
            String[] extractPackagesFromSipper = extractPackagesFromSipper(this.sipper);
            if (extractPackagesFromSipper != null) {
                int length = extractPackagesFromSipper.length;
                String[] strArr = new String[length];
                System.arraycopy(extractPackagesFromSipper, 0, strArr, 0, extractPackagesFromSipper.length);
                IPackageManager packageManager2 = AppGlobals.getPackageManager();
                int userId = UserHandle.getUserId(uid);
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    try {
                        ApplicationInfo applicationInfo = packageManager2.getApplicationInfo(strArr[i], 0, userId);
                        if (applicationInfo == null) {
                            Log.d("BatteryEntry", "Retrieving null app info for package " + strArr[i] + ", user " + userId);
                        } else {
                            CharSequence loadLabel = applicationInfo.loadLabel(packageManager);
                            if (loadLabel != null) {
                                strArr[i] = loadLabel.toString();
                            }
                            if (applicationInfo.icon != 0) {
                                this.defaultPackageName = extractPackagesFromSipper[i];
                                this.icon = applicationInfo.loadIcon(packageManager);
                                break;
                            }
                        }
                    } catch (RemoteException e) {
                        Log.d("BatteryEntry", "Error while retrieving app info for package " + strArr[i] + ", user " + userId, e);
                    }
                    i++;
                }
                if (length != 1) {
                    int length2 = extractPackagesFromSipper.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length2) {
                            break;
                        }
                        String str = extractPackagesFromSipper[i2];
                        try {
                            PackageInfo packageInfo = packageManager2.getPackageInfo(str, 0, userId);
                            if (packageInfo == null) {
                                Log.d("BatteryEntry", "Retrieving null package info for package " + str + ", user " + userId);
                            } else if (!(packageInfo.sharedUserLabel == 0 || (text = packageManager.getText(str, packageInfo.sharedUserLabel, packageInfo.applicationInfo)) == null)) {
                                this.name = text.toString();
                                if (packageInfo.applicationInfo.icon != 0) {
                                    this.defaultPackageName = str;
                                    this.icon = packageInfo.applicationInfo.loadIcon(packageManager);
                                }
                            }
                        } catch (RemoteException e2) {
                            Log.d("BatteryEntry", "Error while retrieving package info for package " + str + ", user " + userId, e2);
                        }
                        i2++;
                    }
                } else {
                    this.name = strArr[0];
                }
            }
            String num = Integer.toString(uid);
            if (this.name == null) {
                this.name = num;
            }
            if (this.icon == null) {
                this.icon = packageManager.getDefaultActivityIcon();
            }
            UidToDetail uidToDetail = new UidToDetail();
            uidToDetail.name = this.name;
            uidToDetail.icon = this.icon;
            uidToDetail.packageName = this.defaultPackageName;
            sUidCache.put(num, uidToDetail);
            Handler handler = sHandler;
            if (handler != null) {
                handler.sendMessage(handler.obtainMessage(1, this));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public String[] extractPackagesFromSipper(BatterySipper batterySipper) {
        if (batterySipper.getUid() == 1000) {
            return new String[]{"android"};
        }
        return batterySipper.mPackages;
    }
}
