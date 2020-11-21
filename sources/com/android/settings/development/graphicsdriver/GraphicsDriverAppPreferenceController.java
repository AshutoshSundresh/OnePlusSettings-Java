package com.android.settings.development.graphicsdriver;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.development.graphicsdriver.GraphicsDriverContentObserver;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphicsDriverAppPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, GraphicsDriverContentObserver.OnGraphicsDriverContentChangedListener, LifecycleObserver, OnStart, OnStop {
    private final Comparator<AppInfo> mAppInfoComparator = new Comparator<AppInfo>(this) {
        /* class com.android.settings.development.graphicsdriver.GraphicsDriverAppPreferenceController.AnonymousClass1 */

        public int compare(AppInfo appInfo, AppInfo appInfo2) {
            return Collator.getInstance().compare(appInfo.label, appInfo2.label);
        }
    };
    private final List<AppInfo> mAppInfos;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final Set<String> mDevOptInApps;
    private final Set<String> mDevOptOutApps;
    private final Set<String> mDevPrereleaseOptInApps;
    CharSequence[] mEntryList;
    GraphicsDriverContentObserver mGraphicsDriverContentObserver;
    private final String mPreferenceDefault;
    private final String mPreferenceGameDriver;
    private PreferenceGroup mPreferenceGroup;
    private final String mPreferencePrereleaseDriver;
    private final String mPreferenceSystem;
    private final String mPreferenceTitle;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public GraphicsDriverAppPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mGraphicsDriverContentObserver = new GraphicsDriverContentObserver(new Handler(Looper.getMainLooper()), this);
        Resources resources = context.getResources();
        this.mPreferenceTitle = resources.getString(C0017R$string.graphics_driver_app_preference_title);
        this.mPreferenceDefault = resources.getString(C0017R$string.graphics_driver_app_preference_default);
        this.mPreferenceGameDriver = resources.getString(C0017R$string.graphics_driver_app_preference_game_driver);
        this.mPreferencePrereleaseDriver = resources.getString(C0017R$string.graphics_driver_app_preference_prerelease_driver);
        this.mPreferenceSystem = resources.getString(C0017R$string.graphics_driver_app_preference_system);
        this.mEntryList = GraphicsDriverEnableForAllAppsPreferenceController.constructEntryList(this.mContext, true);
        this.mAppInfos = getAppInfos(context);
        this.mDevOptInApps = getGlobalSettingsString(this.mContentResolver, "game_driver_opt_in_apps");
        this.mDevPrereleaseOptInApps = getGlobalSettingsString(this.mContentResolver, "game_driver_prerelease_opt_in_apps");
        this.mDevOptOutApps = getGlobalSettingsString(this.mContentResolver, "game_driver_opt_out_apps");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this.mContext) || Settings.Global.getInt(this.mContentResolver, "game_driver_all_apps", 0) == 3) {
            return 2;
        }
        return 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceGroup = preferenceGroup;
        Context context = preferenceGroup.getContext();
        for (AppInfo appInfo : this.mAppInfos) {
            this.mPreferenceGroup.addPreference(createListPreference(context, appInfo.info.packageName, appInfo.label));
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mGraphicsDriverContentObserver.register(this.mContentResolver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mGraphicsDriverContentObserver.unregister(this.mContentResolver);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setVisible(isAvailable());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ListPreference listPreference = (ListPreference) preference;
        String obj2 = obj.toString();
        String key = preference.getKey();
        if (obj2.equals(this.mPreferenceSystem)) {
            this.mDevOptInApps.remove(key);
            this.mDevPrereleaseOptInApps.remove(key);
            this.mDevOptOutApps.add(key);
        } else if (obj2.equals(this.mPreferenceGameDriver)) {
            this.mDevOptInApps.add(key);
            this.mDevPrereleaseOptInApps.remove(key);
            this.mDevOptOutApps.remove(key);
        } else if (obj2.equals(this.mPreferencePrereleaseDriver)) {
            this.mDevOptInApps.remove(key);
            this.mDevPrereleaseOptInApps.add(key);
            this.mDevOptOutApps.remove(key);
        } else {
            this.mDevOptInApps.remove(key);
            this.mDevPrereleaseOptInApps.remove(key);
            this.mDevOptOutApps.remove(key);
        }
        listPreference.setValue(obj2);
        listPreference.setSummary(obj2);
        Settings.Global.putString(this.mContentResolver, "game_driver_opt_in_apps", String.join(",", this.mDevOptInApps));
        Settings.Global.putString(this.mContentResolver, "game_driver_prerelease_opt_in_apps", String.join(",", this.mDevPrereleaseOptInApps));
        Settings.Global.putString(this.mContentResolver, "game_driver_opt_out_apps", String.join(",", this.mDevOptOutApps));
        return true;
    }

    @Override // com.android.settings.development.graphicsdriver.GraphicsDriverContentObserver.OnGraphicsDriverContentChangedListener
    public void onGraphicsDriverContentChanged() {
        updateState(this.mPreferenceGroup);
    }

    /* access modifiers changed from: package-private */
    public class AppInfo {
        public final ApplicationInfo info;
        public final String label;

        AppInfo(GraphicsDriverAppPreferenceController graphicsDriverAppPreferenceController, PackageManager packageManager, ApplicationInfo applicationInfo) {
            this.info = applicationInfo;
            this.label = packageManager.getApplicationLabel(applicationInfo).toString();
        }
    }

    private List<AppInfo> getAppInfos(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
        ArrayList arrayList = new ArrayList();
        for (ApplicationInfo applicationInfo : installedApplications) {
            if ((applicationInfo.flags & 1) == 0) {
                arrayList.add(new AppInfo(this, packageManager, applicationInfo));
            }
        }
        Collections.sort(arrayList, this.mAppInfoComparator);
        return arrayList;
    }

    private Set<String> getGlobalSettingsString(ContentResolver contentResolver, String str) {
        String string = Settings.Global.getString(contentResolver, str);
        if (string == null) {
            return new HashSet();
        }
        HashSet hashSet = new HashSet(Arrays.asList(string.split(",")));
        hashSet.remove("");
        return hashSet;
    }

    /* access modifiers changed from: protected */
    public ListPreference createListPreference(Context context, String str, String str2) {
        ListPreference listPreference = new ListPreference(context);
        listPreference.setKey(str);
        listPreference.setTitle(str2);
        listPreference.setDialogTitle(this.mPreferenceTitle);
        listPreference.setEntries(this.mEntryList);
        listPreference.setEntryValues(this.mEntryList);
        if (this.mDevOptOutApps.contains(str)) {
            listPreference.setValue(this.mPreferenceSystem);
            listPreference.setSummary(this.mPreferenceSystem);
        } else if (this.mDevPrereleaseOptInApps.contains(str)) {
            listPreference.setValue(this.mPreferencePrereleaseDriver);
            listPreference.setSummary(this.mPreferencePrereleaseDriver);
        } else if (this.mDevOptInApps.contains(str)) {
            listPreference.setValue(this.mPreferenceGameDriver);
            listPreference.setSummary(this.mPreferenceGameDriver);
        } else {
            listPreference.setValue(this.mPreferenceDefault);
            listPreference.setSummary(this.mPreferenceDefault);
        }
        listPreference.setOnPreferenceChangeListener(this);
        return listPreference;
    }
}
