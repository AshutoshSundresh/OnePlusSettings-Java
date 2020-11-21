package com.android.settings.notification;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import androidx.preference.Preference;
import com.android.settings.accounts.AccountRestrictionHelper;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedPreference;

public abstract class AdjustVolumeRestrictedPreferenceController extends SliderPreferenceController {
    private AccountRestrictionHelper mHelper;

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AdjustVolumeRestrictedPreferenceController(Context context, String str) {
        this(context, new AccountRestrictionHelper(context), str);
    }

    AdjustVolumeRestrictedPreferenceController(Context context, AccountRestrictionHelper accountRestrictionHelper, String str) {
        super(context, str);
        this.mHelper = accountRestrictionHelper;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.SliderPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof RestrictedPreference) {
            this.mHelper.enforceRestrictionOnPreference((RestrictedPreference) preference, "no_adjust_volume", UserHandle.myUserId());
        }
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        intentFilter.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
        intentFilter.addAction("android.media.MASTER_MUTE_CHANGED_ACTION");
        return intentFilter;
    }
}
