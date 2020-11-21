package com.android.settings.deviceinfo.legal;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageManager;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.internal.util.ArrayUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.io.IOException;
import java.util.Comparator;
import java.util.function.Consumer;

public class ModuleLicensesPreferenceController extends BasePreferenceController {
    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
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

    public ModuleLicensesPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mContext.getPackageManager().getInstalledModules(0).stream().sorted(Comparator.comparing($$Lambda$ModuleLicensesPreferenceController$pL16SYxY49RqinLaZMvrs6bc0c.INSTANCE)).filter(new Predicate(this.mContext)).forEach(new Consumer() {
            /* class com.android.settings.deviceinfo.legal.$$Lambda$ModuleLicensesPreferenceController$w7w_sPbPSDjsJT4DO8L9NvJUpS0 */

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PreferenceGroup preferenceGroup;
                preferenceGroup.addPreference(new ModuleLicensePreference(PreferenceGroup.this.getContext(), (ModuleInfo) obj));
            }
        });
    }

    static class Predicate implements java.util.function.Predicate<ModuleInfo> {
        private final Context mContext;

        public Predicate(Context context) {
            this.mContext = context;
        }

        public boolean test(ModuleInfo moduleInfo) {
            try {
                return ArrayUtils.contains(ModuleLicenseProvider.getPackageAssetManager(this.mContext.getPackageManager(), moduleInfo.getPackageName()).list(""), "NOTICE.html.gz");
            } catch (PackageManager.NameNotFoundException | IOException unused) {
                return false;
            }
        }
    }
}
