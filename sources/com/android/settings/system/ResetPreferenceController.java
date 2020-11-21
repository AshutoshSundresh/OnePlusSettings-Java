package com.android.settings.system;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.NetworkResetPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class ResetPreferenceController extends BasePreferenceController {
    private final FactoryResetPreferenceController mFactpruReset;
    private final NetworkResetPreferenceController mNetworkReset;
    private final UserManager mUm;

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

    public ResetPreferenceController(Context context, String str) {
        super(context, str);
        this.mUm = (UserManager) context.getSystemService("user");
        this.mNetworkReset = new NetworkResetPreferenceController(context);
        this.mFactpruReset = new FactoryResetPreferenceController(context);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_reset_dashboard) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mNetworkReset.isAvailable() || this.mFactpruReset.isAvailable()) {
            return this.mContext.getText(C0017R$string.reset_dashboard_summary);
        }
        return this.mContext.getText(C0017R$string.reset_dashboard_summary_onlyApps);
    }
}
