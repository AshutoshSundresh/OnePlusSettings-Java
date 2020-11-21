package com.oneplus.settings.notification;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.utils.OPUtils;

public class OPSystemXVibratePreferenceController extends BasePreferenceController implements LifecycleObserver, Preference.OnPreferenceChangeListener, OnResume {
    private static final String KEY_SYSTEM_VIBRATE = "system_vibrate_toggle_only";
    private SwitchPreference mSwitch;

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
        return KEY_SYSTEM_VIBRATE;
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

    public OPSystemXVibratePreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_SYSTEM_VIBRATE);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(KEY_SYSTEM_VIBRATE);
        this.mSwitch = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!KEY_SYSTEM_VIBRATE.equals(preference.getKey())) {
            return true;
        }
        Settings.System.putInt(this.mContext.getContentResolver(), "haptic_feedback_enabled", ((Boolean) obj).booleanValue() ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable() && this.mSwitch != null) {
            boolean z = false;
            int i = Settings.System.getInt(this.mContext.getContentResolver(), "haptic_feedback_enabled", 0);
            SwitchPreference switchPreference = this.mSwitch;
            if (i != 0) {
                z = true;
            }
            switchPreference.setChecked(z);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!hasHaptic(this.mContext) || !OPUtils.isSupportXVibrate()) ? 2 : 0;
    }

    private static boolean hasHaptic(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        return vibrator != null && vibrator.hasVibrator();
    }
}
