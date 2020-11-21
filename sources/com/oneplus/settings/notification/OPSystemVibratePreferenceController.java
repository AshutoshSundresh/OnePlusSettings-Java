package com.oneplus.settings.notification;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.MasterSwitchController;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.utils.OPUtils;

public class OPSystemVibratePreferenceController extends BasePreferenceController implements LifecycleObserver, SwitchWidgetController.OnSwitchChangeListener, OnResume {
    private static final String KEY_SYSTEM_VIBRATE = "system_vibrate";
    private MasterSwitchPreference mSwitch;
    private MasterSwitchController mSwitchController;

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

    public OPSystemVibratePreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_SYSTEM_VIBRATE);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitch = (MasterSwitchPreference) preferenceScreen.findPreference(KEY_SYSTEM_VIBRATE);
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        Settings.System.putInt(this.mContext.getContentResolver(), "haptic_feedback_enabled", z ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_SYSTEM_VIBRATE.equals(preference.getKey())) {
            return false;
        }
        OPUtils.startFragment(this.mContext, OPSystemVbrateSettings.class.getName(), 9999);
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable() && this.mSwitch != null) {
            boolean z = false;
            int i = Settings.System.getInt(this.mContext.getContentResolver(), "haptic_feedback_enabled", 0);
            MasterSwitchPreference masterSwitchPreference = this.mSwitch;
            if (i != 0) {
                z = true;
            }
            masterSwitchPreference.setChecked(z);
            MasterSwitchController masterSwitchController = new MasterSwitchController(this.mSwitch);
            this.mSwitchController = masterSwitchController;
            masterSwitchController.setListener(this);
            this.mSwitchController.startListening();
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!hasHaptic(this.mContext) || OPUtils.isSupportXVibrate()) ? 2 : 0;
    }

    private static boolean hasHaptic(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        return vibrator != null && vibrator.hasVibrator();
    }
}
