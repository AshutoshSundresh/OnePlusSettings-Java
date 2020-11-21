package com.android.settings.wallpaper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WallpaperTypePreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart {
    private PreferenceScreen mScreen;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public WallpaperTypePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference.getIntent() == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        this.mContext.startActivity(preference.getIntent());
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        populateWallpaperTypes();
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:13:0x0068 */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r4v4 */
    private void populateWallpaperTypes() {
        Intent intent = new Intent("android.intent.action.SET_WALLPAPER");
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 65536);
        removeUselessExistingPreference(queryIntentActivities);
        this.mScreen.setOrderingAsAdded(false);
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            String str = resolveInfo.activityInfo.packageName;
            Preference findPreference = this.mScreen.findPreference(str);
            Preference preference = findPreference;
            if (findPreference == null) {
                preference = new Preference(this.mScreen.getContext());
            }
            Intent addFlags = new Intent(intent).addFlags(33554432);
            addFlags.setComponent(new ComponentName(str, resolveInfo.activityInfo.name));
            preference.setIntent(addFlags);
            preference.setKey(str);
            CharSequence loadLabel = resolveInfo.loadLabel(packageManager);
            if (loadLabel != null) {
                str = loadLabel;
            }
            preference.setTitle((CharSequence) str);
            preference.setIcon(resolveInfo.loadIcon(packageManager));
            this.mScreen.addPreference(preference);
        }
    }

    private void removeUselessExistingPreference(List<ResolveInfo> list) {
        int preferenceCount = this.mScreen.getPreferenceCount();
        if (preferenceCount > 0) {
            for (int i = preferenceCount - 1; i >= 0; i--) {
                Preference preference = this.mScreen.getPreference(i);
                if (((List) list.stream().filter(new Predicate() {
                    /* class com.android.settings.wallpaper.$$Lambda$WallpaperTypePreferenceController$Wn1n3vRRr977Ar6EptICOr_EA8 */

                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return WallpaperTypePreferenceController.lambda$removeUselessExistingPreference$0(Preference.this, (ResolveInfo) obj);
                    }
                }).collect(Collectors.toList())).isEmpty()) {
                    this.mScreen.removePreference(preference);
                }
            }
        }
    }
}
