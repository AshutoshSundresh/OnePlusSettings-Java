package com.android.settings.dream;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.GearPreference;
import com.android.settingslib.dream.DreamBackend;
import java.util.Optional;

public class CurrentDreamPreferenceController extends BasePreferenceController {
    private final DreamBackend mBackend;

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

    public CurrentDreamPreferenceController(Context context, String str) {
        super(context, str);
        this.mBackend = DreamBackend.getInstance(context);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mBackend.getDreamInfos().size() > 0 ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        setGearClickListenerForPreference(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mBackend.getActiveDreamName();
    }

    private void setGearClickListenerForPreference(Preference preference) {
        if (preference instanceof GearPreference) {
            GearPreference gearPreference = (GearPreference) preference;
            Optional<DreamBackend.DreamInfo> activeDreamInfo = getActiveDreamInfo();
            if (!activeDreamInfo.isPresent() || activeDreamInfo.get().settingsComponentName == null) {
                gearPreference.setOnGearClickListener(null);
            } else {
                gearPreference.setOnGearClickListener(new GearPreference.OnGearClickListener() {
                    /* class com.android.settings.dream.$$Lambda$CurrentDreamPreferenceController$faOOwvjkeM0i38i1bxACLza6vQ4 */

                    @Override // com.android.settings.widget.GearPreference.OnGearClickListener
                    public final void onGearClick(GearPreference gearPreference) {
                        CurrentDreamPreferenceController.this.lambda$setGearClickListenerForPreference$0$CurrentDreamPreferenceController(gearPreference);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setGearClickListenerForPreference$0 */
    public /* synthetic */ void lambda$setGearClickListenerForPreference$0$CurrentDreamPreferenceController(GearPreference gearPreference) {
        launchScreenSaverSettings();
    }

    private void launchScreenSaverSettings() {
        Optional<DreamBackend.DreamInfo> activeDreamInfo = getActiveDreamInfo();
        if (activeDreamInfo.isPresent()) {
            this.mBackend.launchSettings(this.mContext, activeDreamInfo.get());
        }
    }

    private Optional<DreamBackend.DreamInfo> getActiveDreamInfo() {
        return this.mBackend.getDreamInfos().stream().filter($$Lambda$CurrentDreamPreferenceController$JJd0D4Ql1FstWgOpYrMCLEB2pnU.INSTANCE).findFirst();
    }

    private void setActiveDreamIcon(Preference preference) {
        if (preference instanceof GearPreference) {
            GearPreference gearPreference = (GearPreference) preference;
            gearPreference.setIconSize(2);
            Utils.setSafeIcon(gearPreference, this.mBackend.getActiveIcon());
        }
    }
}
