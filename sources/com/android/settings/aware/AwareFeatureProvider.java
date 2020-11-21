package com.android.settings.aware;

import android.content.Context;
import androidx.fragment.app.Fragment;

public interface AwareFeatureProvider {
    CharSequence getGestureSummary(Context context, boolean z, boolean z2, boolean z3);

    boolean isEnabled(Context context);

    boolean isSupported(Context context);

    void showRestrictionDialog(Fragment fragment);
}
