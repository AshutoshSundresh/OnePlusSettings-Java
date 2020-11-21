package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppData;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class NetworkScorerPickerPreferenceController extends BasePreferenceController {
    private final NetworkScoreManager mNetworkScoreManager = ((NetworkScoreManager) this.mContext.getSystemService("network_score"));

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

    public NetworkScorerPickerPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z = !this.mNetworkScoreManager.getAllValidScorers().isEmpty();
        preference.setEnabled(z);
        if (!z) {
            preference.setSummary((CharSequence) null);
            return;
        }
        NetworkScorerAppData activeScorer = this.mNetworkScoreManager.getActiveScorer();
        if (activeScorer == null) {
            preference.setSummary(this.mContext.getString(C0017R$string.network_scorer_picker_none_preference));
        } else {
            preference.setSummary(activeScorer.getRecommendationServiceLabel());
        }
    }
}
