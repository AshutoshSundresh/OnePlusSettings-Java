package com.android.settings.development.graphicsdriver;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.development.graphicsdriver.GraphicsDriverContentObserver;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import dalvik.system.VMRuntime;
import java.util.ArrayList;

public class GraphicsDriverEnableForAllAppsPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, GraphicsDriverContentObserver.OnGraphicsDriverContentChangedListener, LifecycleObserver, OnStart, OnStop {
    public static final int GAME_DRIVER_ALL_APPS = 1;
    public static final int GAME_DRIVER_DEFAULT = 0;
    public static final int GAME_DRIVER_OFF = 3;
    public static final int GAME_DRIVER_PRERELEASE_ALL_APPS = 2;
    public static final String PROPERTY_GFX_DRIVER_GAME = "ro.gfx.driver.0";
    public static final String PROPERTY_GFX_DRIVER_PRERELEASE = "ro.gfx.driver.1";
    private final ContentResolver mContentResolver;
    private final Context mContext;
    CharSequence[] mEntryList = constructEntryList(this.mContext, false);
    GraphicsDriverContentObserver mGraphicsDriverContentObserver = new GraphicsDriverContentObserver(new Handler(Looper.getMainLooper()), this);
    private ListPreference mPreference;
    private final String mPreferenceDefault;
    private final String mPreferenceGameDriver;
    private final String mPreferencePrereleaseDriver;

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

    public GraphicsDriverEnableForAllAppsPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        Resources resources = context.getResources();
        this.mPreferenceDefault = resources.getString(C0017R$string.graphics_driver_app_preference_default);
        this.mPreferenceGameDriver = resources.getString(C0017R$string.graphics_driver_app_preference_game_driver);
        this.mPreferencePrereleaseDriver = resources.getString(C0017R$string.graphics_driver_app_preference_prerelease_driver);
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
        ListPreference listPreference = (ListPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = listPreference;
        listPreference.setEntries(this.mEntryList);
        this.mPreference.setEntryValues(this.mEntryList);
        this.mPreference.setOnPreferenceChangeListener(this);
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
        ListPreference listPreference = (ListPreference) preference;
        listPreference.setVisible(isAvailable());
        int i = Settings.Global.getInt(this.mContentResolver, "game_driver_all_apps", 0);
        if (i == 1) {
            listPreference.setValue(this.mPreferenceGameDriver);
            listPreference.setSummary(this.mPreferenceGameDriver);
        } else if (i == 2) {
            listPreference.setValue(this.mPreferencePrereleaseDriver);
            listPreference.setSummary(this.mPreferencePrereleaseDriver);
        } else {
            listPreference.setValue(this.mPreferenceDefault);
            listPreference.setSummary(this.mPreferenceDefault);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ListPreference listPreference = (ListPreference) preference;
        String obj2 = obj.toString();
        int i = 0;
        int i2 = Settings.Global.getInt(this.mContentResolver, "game_driver_all_apps", 0);
        if (obj2.equals(this.mPreferenceGameDriver)) {
            i = 1;
        } else if (obj2.equals(this.mPreferencePrereleaseDriver)) {
            i = 2;
        }
        listPreference.setValue(obj2);
        listPreference.setSummary(obj2);
        if (i != i2) {
            Settings.Global.putInt(this.mContentResolver, "game_driver_all_apps", i);
        }
        return true;
    }

    @Override // com.android.settings.development.graphicsdriver.GraphicsDriverContentObserver.OnGraphicsDriverContentChangedListener
    public void onGraphicsDriverContentChanged() {
        updateState(this.mPreference);
    }

    public static CharSequence[] constructEntryList(Context context, boolean z) {
        Resources resources = context.getResources();
        String str = SystemProperties.get(PROPERTY_GFX_DRIVER_PRERELEASE);
        String str2 = SystemProperties.get(PROPERTY_GFX_DRIVER_GAME);
        ArrayList arrayList = new ArrayList();
        arrayList.add(resources.getString(C0017R$string.graphics_driver_app_preference_default));
        PackageManager packageManager = context.getPackageManager();
        if (!TextUtils.isEmpty(str) && hasDriverPackage(packageManager, str)) {
            arrayList.add(resources.getString(C0017R$string.graphics_driver_app_preference_prerelease_driver));
        }
        if (!TextUtils.isEmpty(str2) && hasDriverPackage(packageManager, str2)) {
            arrayList.add(resources.getString(C0017R$string.graphics_driver_app_preference_game_driver));
        }
        if (z) {
            arrayList.add(resources.getString(C0017R$string.graphics_driver_app_preference_system));
        }
        return (CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]);
    }

    private static boolean hasDriverPackage(PackageManager packageManager, String str) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 1048576);
            if (applicationInfo.targetSdkVersion >= 26 && chooseAbi(applicationInfo) != null) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private static String chooseAbi(ApplicationInfo applicationInfo) {
        String currentInstructionSet = VMRuntime.getCurrentInstructionSet();
        String str = applicationInfo.primaryCpuAbi;
        if (str != null && currentInstructionSet.equals(VMRuntime.getInstructionSet(str))) {
            return applicationInfo.primaryCpuAbi;
        }
        String str2 = applicationInfo.secondaryCpuAbi;
        if (str2 == null || !currentInstructionSet.equals(VMRuntime.getInstructionSet(str2))) {
            return null;
        }
        return applicationInfo.secondaryCpuAbi;
    }
}
