package com.android.settings.development.featureflags;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.util.FeatureFlagUtils;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.Map;
import java.util.function.Consumer;

public class FeatureFlagsPreferenceController extends BasePreferenceController {
    private PreferenceGroup mGroup;

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

    public FeatureFlagsPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return Build.IS_DEBUGGABLE ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        Map allFeatureFlags = FeatureFlagUtils.getAllFeatureFlags();
        if (allFeatureFlags != null) {
            this.mGroup.removeAll();
            allFeatureFlags.keySet().stream().sorted().forEach(new Consumer(this.mGroup.getContext()) {
                /* class com.android.settings.development.featureflags.$$Lambda$FeatureFlagsPreferenceController$6ZXep8K3Kx7KiucsshYGPcS5c0Y */
                public final /* synthetic */ Context f$1;

                {
                    this.f$1 = r2;
                }

                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    FeatureFlagsPreferenceController.this.lambda$displayPreference$0$FeatureFlagsPreferenceController(this.f$1, (String) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$FeatureFlagsPreferenceController(Context context, String str) {
        this.mGroup.addPreference(new FeatureFlagPreference(context, str));
    }
}
