package com.android.settings.aware;

import android.content.Context;
import androidx.fragment.app.Fragment;

public class AwareFeatureProviderImpl implements AwareFeatureProvider {
    @Override // com.android.settings.aware.AwareFeatureProvider
    public CharSequence getGestureSummary(Context context, boolean z, boolean z2, boolean z3) {
        return null;
    }

    @Override // com.android.settings.aware.AwareFeatureProvider
    public boolean isEnabled(Context context) {
        return false;
    }

    @Override // com.android.settings.aware.AwareFeatureProvider
    public boolean isSupported(Context context) {
        return false;
    }

    @Override // com.android.settings.aware.AwareFeatureProvider
    public void showRestrictionDialog(Fragment fragment) {
    }
}
