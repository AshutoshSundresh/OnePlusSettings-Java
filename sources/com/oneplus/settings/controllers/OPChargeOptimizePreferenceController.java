package com.oneplus.settings.controllers;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.oneplus.common.ReflectUtil;
import com.oneplus.settings.utils.OPUtils;

public class OPChargeOptimizePreferenceController extends BasePreferenceController implements LifecycleObserver {
    public static final String CHARING_GUARDER_ENABLED = "charging_guarder_enabled";
    private static final String KEY_CHARGE_OPTIMIZE = "charging_optimization_summary";
    private Preference mPreference;

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

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_CHARGE_OPTIMIZE;
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

    public OPChargeOptimizePreferenceController(Context context) {
        super(context, KEY_CHARGE_OPTIMIZE);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!ReflectUtil.isFeatureSupported("OP_FEATURE_CHARGE_OPTIMIZATED") || !OPUtils.isSM8X50Products()) ? 2 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        ((SwitchPreference) this.mPreference).setChecked(getChargingOptimizationState(this.mContext));
        this.mPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /* class com.oneplus.settings.controllers.$$Lambda$OPChargeOptimizePreferenceController$F1tpBAUloLCbC03PvcVx7K4_EJU */

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return OPChargeOptimizePreferenceController.lambda$displayPreference$0(PreferenceScreen.this, preference, obj);
            }
        });
        Preference preference = this.mPreference;
        if (preference != null) {
            preference.setEnabled(!OPUtils.isGuestMode());
        }
    }

    public static boolean getChargingOptimizationState(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), CHARING_GUARDER_ENABLED, 0, 0) > 0;
    }

    public static void setChargingOptimizationState(Context context, boolean z) {
        Settings.System.putIntForUser(context.getContentResolver(), CHARING_GUARDER_ENABLED, z ? 1 : 0, 0);
        OPUtils.sendAnalytics("optimized_charging_switch", "switch", z ? "1" : "0");
    }
}
