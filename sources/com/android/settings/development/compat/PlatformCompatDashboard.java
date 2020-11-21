package com.android.settings.development.compat;

import android.app.AlertDialog;
import android.compat.Compatibility;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArraySet;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.internal.compat.AndroidBuildClassifier;
import com.android.internal.compat.CompatibilityChangeConfig;
import com.android.internal.compat.CompatibilityChangeInfo;
import com.android.internal.compat.IPlatformCompat;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.development.AppPicker;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class PlatformCompatDashboard extends DashboardFragment {
    private AndroidBuildClassifier mAndroidBuildClassifier = new AndroidBuildClassifier();
    private CompatibilityChangeInfo[] mChanges;
    private IPlatformCompat mPlatformCompat;
    String mSelectedApp;

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PlatformCompatDashboard";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1805;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.platform_compat_settings;
    }

    /* access modifiers changed from: package-private */
    public IPlatformCompat getPlatformCompat() {
        if (this.mPlatformCompat == null) {
            this.mPlatformCompat = IPlatformCompat.Stub.asInterface(ServiceManager.getService("platform_compat"));
        }
        return this.mPlatformCompat;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        try {
            this.mChanges = getPlatformCompat().listUIChanges();
            startAppPicker();
        } catch (RemoteException e) {
            throw new RuntimeException("Could not list changes!", e);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("compat_app", this.mSelectedApp);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 6) {
            super.onActivityResult(i, i2, intent);
        } else if (i2 == -1) {
            this.mSelectedApp = intent.getAction();
            try {
                addPreferences(getApplicationInfo());
            } catch (PackageManager.NameNotFoundException unused) {
                startAppPicker();
            }
        } else if (i2 == -2) {
            new AlertDialog.Builder(getContext()).setTitle(C0017R$string.platform_compat_dialog_title_no_apps).setMessage(C0017R$string.platform_compat_dialog_text_no_apps).setPositiveButton(C0017R$string.okay, new DialogInterface.OnClickListener() {
                /* class com.android.settings.development.compat.$$Lambda$PlatformCompatDashboard$ni_eJknx8IiLZ7ImMgWkoZEnxmo */

                public final void onClick(DialogInterface dialogInterface, int i) {
                    PlatformCompatDashboard.this.lambda$onActivityResult$0$PlatformCompatDashboard(dialogInterface, i);
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.android.settings.development.compat.$$Lambda$PlatformCompatDashboard$o6SdHzmXdfCR06Aya1S_cnBkvb8 */

                public final void onDismiss(DialogInterface dialogInterface) {
                    PlatformCompatDashboard.this.lambda$onActivityResult$1$PlatformCompatDashboard(dialogInterface);
                }
            }).setCancelable(false).show();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onActivityResult$0 */
    public /* synthetic */ void lambda$onActivityResult$0$PlatformCompatDashboard(DialogInterface dialogInterface, int i) {
        finish();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onActivityResult$1 */
    public /* synthetic */ void lambda$onActivityResult$1$PlatformCompatDashboard(DialogInterface dialogInterface) {
        finish();
    }

    private void addPreferences(ApplicationInfo applicationInfo) {
        List list;
        getPreferenceScreen().removeAll();
        getPreferenceScreen().addPreference(createAppPreference(applicationInfo));
        CompatibilityChangeConfig appChangeMappings = getAppChangeMappings();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        TreeMap treeMap = new TreeMap();
        CompatibilityChangeInfo[] compatibilityChangeInfoArr = this.mChanges;
        for (CompatibilityChangeInfo compatibilityChangeInfo : compatibilityChangeInfoArr) {
            if (compatibilityChangeInfo.getEnableAfterTargetSdk() != 0) {
                if (!treeMap.containsKey(Integer.valueOf(compatibilityChangeInfo.getEnableAfterTargetSdk()))) {
                    list = new ArrayList();
                    treeMap.put(Integer.valueOf(compatibilityChangeInfo.getEnableAfterTargetSdk()), list);
                } else {
                    list = (List) treeMap.get(Integer.valueOf(compatibilityChangeInfo.getEnableAfterTargetSdk()));
                }
                list.add(compatibilityChangeInfo);
            } else if (compatibilityChangeInfo.getDisabled()) {
                arrayList2.add(compatibilityChangeInfo);
            } else {
                arrayList.add(compatibilityChangeInfo);
            }
        }
        createChangeCategoryPreference(arrayList, appChangeMappings, getString(C0017R$string.platform_compat_default_enabled_title));
        createChangeCategoryPreference(arrayList2, appChangeMappings, getString(C0017R$string.platform_compat_default_disabled_title));
        for (Integer num : treeMap.keySet()) {
            createChangeCategoryPreference((List) treeMap.get(num), appChangeMappings, getString(C0017R$string.platform_compat_target_sdk_title, num));
        }
    }

    private CompatibilityChangeConfig getAppChangeMappings() {
        try {
            return getPlatformCompat().getAppConfig(getApplicationInfo());
        } catch (PackageManager.NameNotFoundException | RemoteException e) {
            throw new RuntimeException("Could not get app config!", e);
        }
    }

    /* access modifiers changed from: package-private */
    public Preference createPreferenceForChange(Context context, CompatibilityChangeInfo compatibilityChangeInfo, CompatibilityChangeConfig compatibilityChangeConfig) {
        String str;
        boolean isChangeEnabled = compatibilityChangeConfig.isChangeEnabled(compatibilityChangeInfo.getId());
        SwitchPreference switchPreference = new SwitchPreference(context);
        if (compatibilityChangeInfo.getName() != null) {
            str = compatibilityChangeInfo.getName();
        } else {
            str = "Change_" + compatibilityChangeInfo.getId();
        }
        switchPreference.setSummary(str);
        switchPreference.setKey(str);
        try {
            switchPreference.setEnabled(getPlatformCompat().getOverrideValidator().getOverrideAllowedState(compatibilityChangeInfo.getId(), this.mSelectedApp).state == 0);
            switchPreference.setChecked(isChangeEnabled);
            switchPreference.setOnPreferenceChangeListener(new CompatChangePreferenceChangeListener(compatibilityChangeInfo.getId()));
            return switchPreference;
        } catch (RemoteException e) {
            throw new RuntimeException("Could not check if change can be overridden for app.", e);
        }
    }

    /* access modifiers changed from: package-private */
    public ApplicationInfo getApplicationInfo() throws PackageManager.NameNotFoundException {
        return getPackageManager().getApplicationInfo(this.mSelectedApp, 0);
    }

    /* access modifiers changed from: package-private */
    public Preference createAppPreference(ApplicationInfo applicationInfo) {
        Context context = getPreferenceScreen().getContext();
        Drawable loadIcon = applicationInfo.loadIcon(context.getPackageManager());
        Preference preference = new Preference(context);
        preference.setIcon(loadIcon);
        preference.setSummary(getString(C0017R$string.platform_compat_selected_app_summary, this.mSelectedApp, Integer.valueOf(applicationInfo.targetSdkVersion)));
        preference.setKey(this.mSelectedApp);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.development.compat.$$Lambda$PlatformCompatDashboard$OBqByYHKh6V1z0fLfYVvCR9lHA */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return PlatformCompatDashboard.this.lambda$createAppPreference$2$PlatformCompatDashboard(preference);
            }
        });
        return preference;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createAppPreference$2 */
    public /* synthetic */ boolean lambda$createAppPreference$2$PlatformCompatDashboard(Preference preference) {
        startAppPicker();
        return true;
    }

    /* access modifiers changed from: package-private */
    public PreferenceCategory createChangeCategoryPreference(List<CompatibilityChangeInfo> list, CompatibilityChangeConfig compatibilityChangeConfig, String str) {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getPreferenceScreen().getContext());
        preferenceCategory.setTitle(str);
        getPreferenceScreen().addPreference(preferenceCategory);
        addChangePreferencesToCategory(list, preferenceCategory, compatibilityChangeConfig);
        return preferenceCategory;
    }

    private void addChangePreferencesToCategory(List<CompatibilityChangeInfo> list, PreferenceCategory preferenceCategory, CompatibilityChangeConfig compatibilityChangeConfig) {
        for (CompatibilityChangeInfo compatibilityChangeInfo : list) {
            preferenceCategory.addPreference(createPreferenceForChange(getPreferenceScreen().getContext(), compatibilityChangeInfo, compatibilityChangeConfig));
        }
    }

    private void startAppPicker() {
        Intent putExtra = new Intent(getContext(), AppPicker.class).putExtra("com.android.settings.extra.INCLUDE_NOTHING", false);
        if (!this.mAndroidBuildClassifier.isDebuggableBuild()) {
            putExtra.putExtra("com.android.settings.extra.DEBUGGABLE", true);
        }
        startActivityForResult(putExtra, 6);
    }

    /* access modifiers changed from: private */
    public class CompatChangePreferenceChangeListener implements Preference.OnPreferenceChangeListener {
        private final long changeId;

        CompatChangePreferenceChangeListener(long j) {
            this.changeId = j;
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            try {
                ArraySet arraySet = new ArraySet();
                ArraySet arraySet2 = new ArraySet();
                if (((Boolean) obj).booleanValue()) {
                    arraySet.add(Long.valueOf(this.changeId));
                } else {
                    arraySet2.add(Long.valueOf(this.changeId));
                }
                PlatformCompatDashboard.this.getPlatformCompat().setOverrides(new CompatibilityChangeConfig(new Compatibility.ChangeConfig(arraySet, arraySet2)), PlatformCompatDashboard.this.mSelectedApp);
                return true;
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
