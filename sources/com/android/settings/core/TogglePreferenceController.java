package com.android.settings.core;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.widget.TwoStateButtonPreference;

public abstract class TogglePreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "TogglePrefController";

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

    @Override // com.android.settings.core.BasePreferenceController
    public int getSliceType() {
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public abstract boolean isChecked();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return false;
    }

    @Override // com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return true;
    }

    public abstract boolean setChecked(boolean z);

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public TogglePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof TwoStatePreference) {
            ((TwoStatePreference) preference).setChecked(isChecked());
        } else if (preference instanceof MasterSwitchPreference) {
            ((MasterSwitchPreference) preference).setChecked(isChecked());
        } else if (preference instanceof TwoStateButtonPreference) {
            ((TwoStateButtonPreference) preference).setChecked(isChecked());
        } else {
            refreshSummary(preference);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public final boolean onPreferenceChange(Preference preference, Object obj) {
        if ((preference instanceof MasterSwitchPreference) || (preference instanceof TwoStateButtonPreference)) {
            FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().logClickedPreference(preference, getMetricsCategory());
        }
        return setChecked(((Boolean) obj).booleanValue());
    }
}
