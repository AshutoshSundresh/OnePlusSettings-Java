package com.oneplus.settings.notification;

import android.app.INotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0012R$layout;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.notification.NotificationBackend;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OPNotificationAppList extends SettingsPreferenceFragment {
    private static final Intent APP_NOTIFICATION_PREFS_CATEGORY_INTENT = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.NOTIFICATION_PREFERENCES");
    private static long PROGRESS_MIN_SHOW_TIME = 500;
    private static long WILL_SHOW_PROGRESS_TIME = 300;
    private static final Comparator<AppRow> mRowComparator = new Comparator<AppRow>() {
        /* class com.oneplus.settings.notification.OPNotificationAppList.AnonymousClass3 */
        private final Collator sCollator = Collator.getInstance();

        public int compare(AppRow appRow, AppRow appRow2) {
            return this.sCollator.compare(appRow.label, appRow2.label);
        }
    };
    private View emptyView;
    private PreferenceCategory mAllowLEDApps;
    private Backend mBackend = new Backend();
    private final Runnable mCollectAppsRunnable = new Runnable() {
        /* class com.oneplus.settings.notification.OPNotificationAppList.AnonymousClass6 */

        public void run() {
            synchronized (OPNotificationAppList.this.mRows) {
                long uptimeMillis = SystemClock.uptimeMillis();
                Log.d("OPNotificationAppList", "Collecting apps...");
                OPNotificationAppList.this.mRows.clear();
                OPNotificationAppList.this.mSortedRows.clear();
                ArrayList<ApplicationInfo> arrayList = new ArrayList();
                List<LauncherActivityInfo> activityList = OPNotificationAppList.this.mLauncherApps.getActivityList(null, UserHandle.OWNER);
                Log.d("OPNotificationAppList", "  launchable activities:");
                for (LauncherActivityInfo launcherActivityInfo : activityList) {
                    Log.d("OPNotificationAppList", "oneplus- " + launcherActivityInfo.getComponentName().toString());
                    arrayList.add(launcherActivityInfo.getApplicationInfo());
                }
                List<ResolveInfo> queryNotificationConfigActivities = OPNotificationAppList.queryNotificationConfigActivities(OPNotificationAppList.this.mPM);
                Log.d("OPNotificationAppList", "  config activities:");
                for (ResolveInfo resolveInfo : queryNotificationConfigActivities) {
                    Log.d("OPNotificationAppList", "oneplus-" + resolveInfo.activityInfo.packageName + "/" + resolveInfo.activityInfo.name);
                    arrayList.add(resolveInfo.activityInfo.applicationInfo);
                }
                for (ApplicationInfo applicationInfo : arrayList) {
                    String str = applicationInfo.packageName;
                    if (!OPNotificationAppList.this.mRows.containsKey(str)) {
                        OPNotificationAppList.this.mRows.put(str, OPNotificationAppList.loadAppRow(OPNotificationAppList.this.mPM, applicationInfo, OPNotificationAppList.this.mBackend));
                    }
                }
                OPNotificationAppList.applyConfigActivities(OPNotificationAppList.this.mPM, OPNotificationAppList.this.mRows, queryNotificationConfigActivities);
                OPNotificationAppList.this.mSortedRows.addAll(OPNotificationAppList.this.mRows.values());
                Collections.sort(OPNotificationAppList.this.mSortedRows, OPNotificationAppList.mRowComparator);
                OPNotificationAppList.this.mHandler.post(OPNotificationAppList.this.mRefreshAppsListRunnable);
                Log.d("OPNotificationAppList", "oneplus-Collected " + OPNotificationAppList.this.mRows.size() + " apps in " + (SystemClock.uptimeMillis() - uptimeMillis) + "ms");
            }
        }
    };
    private Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Handler mHandler1 = new Handler(Looper.getMainLooper());
    private boolean mHasShowProgress;
    private LauncherApps mLauncherApps;
    private PreferenceCategory mNotAllowLEDApps;
    private NotificationBackend mNotificationBackend = new NotificationBackend();
    private PackageManager mPM;
    private final Runnable mRefreshAppsListRunnable = new Runnable() {
        /* class com.oneplus.settings.notification.OPNotificationAppList.AnonymousClass4 */

        public void run() {
            OPNotificationAppList oPNotificationAppList = OPNotificationAppList.this;
            oPNotificationAppList.refreshDisplayedItems(oPNotificationAppList.mSortedRows);
        }
    };
    private PreferenceScreen mRoot;
    private final ArrayMap<String, AppRow> mRows = new ArrayMap<>();
    private boolean mShowAllowLEDApps = false;
    private boolean mShowNotAllowLEDApps = false;
    private Runnable mShowPromptRunnable;
    private long mShowPromptTime;
    private final ArrayList<AppRow> mSortedRows = new ArrayList<>();

    public static class AppRow {
        public Drawable icon;
        public CharSequence label;
        public boolean ledDisabled;
        public String pkg;
        public Intent settingsIntent;
        public int uid;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    /* access modifiers changed from: protected */
    public final void onPreExecute() {
        this.mHasShowProgress = false;
        AnonymousClass1 r0 = new Runnable() {
            /* class com.oneplus.settings.notification.OPNotificationAppList.AnonymousClass1 */

            public void run() {
                OPNotificationAppList.this.mHasShowProgress = true;
                if (OPNotificationAppList.this.emptyView != null) {
                    OPNotificationAppList oPNotificationAppList = OPNotificationAppList.this;
                    oPNotificationAppList.setEmptyView(oPNotificationAppList.emptyView);
                }
                OPNotificationAppList.this.mShowPromptTime = System.currentTimeMillis();
            }
        };
        this.mShowPromptRunnable = r0;
        this.mHandler1.postDelayed(r0, WILL_SHOW_PROGRESS_TIME);
    }

    /* access modifiers changed from: protected */
    public final void onPostExecute() {
        if (this.mHasShowProgress) {
            long currentTimeMillis = PROGRESS_MIN_SHOW_TIME - (System.currentTimeMillis() - this.mShowPromptTime);
            if (currentTimeMillis > 0) {
                this.mHandler1.postDelayed(new Runnable() {
                    /* class com.oneplus.settings.notification.OPNotificationAppList.AnonymousClass2 */

                    public void run() {
                        if (OPNotificationAppList.this.emptyView != null) {
                            OPNotificationAppList.this.emptyView.setVisibility(8);
                        }
                        OPNotificationAppList.this.loadAppsList();
                    }
                }, currentTimeMillis);
                return;
            }
            View view = this.emptyView;
            if (view != null) {
                view.setVisibility(8);
            }
            loadAppsList();
            return;
        }
        View view2 = this.emptyView;
        if (view2 != null) {
            view2.setVisibility(8);
        }
        loadAppsList();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_app_notification_list_settings);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        UserManager.get(activity);
        this.mPM = this.mContext.getPackageManager();
        this.mLauncherApps = (LauncherApps) this.mContext.getSystemService("launcherapps");
        this.mRoot = getPreferenceScreen();
        this.mAllowLEDApps = (PreferenceCategory) findPreference("op_notification_allow_led");
        this.mNotAllowLEDApps = (PreferenceCategory) findPreference("op_notification_not_allow_led");
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ViewGroup viewGroup = (ViewGroup) getListView().getParent();
        View inflate = getActivity().getLayoutInflater().inflate(C0012R$layout.loading_container, viewGroup, false);
        this.emptyView = inflate;
        inflate.setVisibility(0);
        viewGroup.addView(this.emptyView);
        onPreExecute();
        resetUI();
        onPostExecute();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void loadAppsList() {
        AsyncTask.execute(this.mCollectAppsRunnable);
    }

    public static AppRow loadAppRow(PackageManager packageManager, ApplicationInfo applicationInfo, Backend backend) {
        AppRow appRow = new AppRow();
        appRow.pkg = applicationInfo.packageName;
        appRow.uid = applicationInfo.uid;
        try {
            appRow.label = applicationInfo.loadLabel(packageManager);
        } catch (Throwable th) {
            Log.e("OPNotificationAppList", "Error loading application label for " + appRow.pkg, th);
            appRow.label = appRow.pkg;
        }
        appRow.icon = applicationInfo.loadIcon(packageManager);
        backend.getNotificationsBanned(appRow.pkg, appRow.uid);
        backend.getHighPriority(appRow.pkg, appRow.uid);
        backend.getSensitive(appRow.pkg, appRow.uid);
        appRow.ledDisabled = backend.getLedDisabled(appRow.pkg);
        return appRow;
    }

    public static List<ResolveInfo> queryNotificationConfigActivities(PackageManager packageManager) {
        Log.d("OPNotificationAppList", "APP_NOTIFICATION_PREFS_CATEGORY_INTENT is " + APP_NOTIFICATION_PREFS_CATEGORY_INTENT);
        return packageManager.queryIntentActivities(APP_NOTIFICATION_PREFS_CATEGORY_INTENT, 0);
    }

    public static void applyConfigActivities(PackageManager packageManager, ArrayMap<String, AppRow> arrayMap, List<ResolveInfo> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("Found ");
        sb.append(list.size());
        sb.append(" preference activities");
        sb.append(list.size() == 0 ? " ;_;" : "");
        Log.d("OPNotificationAppList", sb.toString());
        for (ResolveInfo resolveInfo : list) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            AppRow appRow = arrayMap.get(activityInfo.applicationInfo.packageName);
            if (appRow == null) {
                Log.v("OPNotificationAppList", "Ignoring notification preference activity (" + activityInfo.name + ") for unknown package " + activityInfo.packageName);
            } else if (appRow.settingsIntent != null) {
                Log.v("OPNotificationAppList", "Ignoring duplicate notification preference activity (" + activityInfo.name + ") for package " + activityInfo.packageName);
            } else {
                appRow.settingsIntent = new Intent(APP_NOTIFICATION_PREFS_CATEGORY_INTENT).setClassName(activityInfo.packageName, activityInfo.name);
            }
        }
    }

    private void resetUI() {
        this.mAllowLEDApps.removeAll();
        this.mNotAllowLEDApps.removeAll();
        this.mRoot.removePreference(this.mNotAllowLEDApps);
        this.mRoot.removePreference(this.mAllowLEDApps);
        this.mShowNotAllowLEDApps = false;
        this.mShowAllowLEDApps = false;
    }

    private void removeCatagoryIfNoneApp() {
        if (this.mShowNotAllowLEDApps) {
            this.mRoot.addPreference(this.mNotAllowLEDApps);
        } else {
            this.mRoot.removePreference(this.mNotAllowLEDApps);
        }
        if (this.mShowAllowLEDApps) {
            this.mRoot.addPreference(this.mAllowLEDApps);
        } else {
            this.mRoot.removePreference(this.mAllowLEDApps);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshDisplayedItems(ArrayList<AppRow> arrayList) {
        resetUI();
        for (final int i = 0; i < arrayList.size(); i++) {
            final AppRow appRow = arrayList.get(i);
            SwitchPreference switchPreference = new SwitchPreference(this.mContext);
            if (!"com.oneplus.deskclock".equals(appRow.pkg) && !"com.android.incallui".equals(appRow.pkg) && !"com.google.android.calendar".equals(appRow.pkg) && !"com.oneplus.calendar".equals(appRow.pkg) && !"com.android.dialer".equals(appRow.pkg) && !"com.oneplus.dialer".equals(appRow.pkg)) {
                switchPreference.setKey(appRow.pkg);
                switchPreference.setTitle(appRow.label);
                switchPreference.setIcon(appRow.icon);
                switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    /* class com.oneplus.settings.notification.OPNotificationAppList.AnonymousClass5 */

                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public boolean onPreferenceChange(Preference preference, Object obj) {
                        boolean booleanValue = ((Boolean) obj).booleanValue();
                        appRow.ledDisabled = !booleanValue;
                        OPNotificationAppList.this.mNotificationBackend.setLedEnabled(appRow.pkg, booleanValue);
                        OPNotificationAppList.this.mSortedRows.set(i, appRow);
                        OPNotificationAppList.this.mHandler.post(OPNotificationAppList.this.mRefreshAppsListRunnable);
                        return true;
                    }
                });
                if (appRow.ledDisabled) {
                    this.mShowNotAllowLEDApps = true;
                    this.mNotAllowLEDApps.addPreference(switchPreference);
                } else {
                    this.mShowAllowLEDApps = true;
                    this.mAllowLEDApps.addPreference(switchPreference);
                }
                switchPreference.setChecked(!appRow.ledDisabled);
            }
        }
        removeCatagoryIfNoneApp();
        Log.d("OPNotificationAppList", "Refreshed " + this.mSortedRows.size() + " displayed items");
    }

    public static class Backend {
        static INotificationManager sINM = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));

        public boolean getHighPriority(String str, int i) {
            return true;
        }

        public boolean getLedDisabled(String str) {
            return false;
        }

        public boolean getSensitive(String str, int i) {
            return true;
        }

        public boolean getNotificationsBanned(String str, int i) {
            try {
                return !sINM.areNotificationsEnabledForPackage(str, i);
            } catch (Exception e) {
                Log.w("OPNotificationAppList", "Error calling NoMan", e);
                return false;
            }
        }
    }
}
