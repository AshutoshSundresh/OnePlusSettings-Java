package com.oneplus.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.utils.OPUtils;

public class OPQuickTurnOnAssistantAppPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume {
    private static final int ASK_ALEXA_VALUE = 2;
    private static final int CAMERA_VALUE = 0;
    private static final String KEY_DOUBLE_TAP_POWER_GESTURE = "double_tap_power_gesture";
    private static final int ONEPLUS_EMERGENCY_TAP_POWER_GESTURE_FIVE_TIMES = 5;
    private static final int ONEPLUS_EMERGENCY_TAP_POWER_GESTURE_NO_TIMES = -1;
    private static final int ONEPLUS_EMERGENCY_TAP_POWER_GESTURE_THREE_TIMES = 3;
    private static final int WALLET_VALUE = 1;
    private Preference mSwitch;

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

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_DOUBLE_TAP_POWER_GESTURE;
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

    public OPQuickTurnOnAssistantAppPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_DOUBLE_TAP_POWER_GESTURE);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitch = preferenceScreen.findPreference(KEY_DOUBLE_TAP_POWER_GESTURE);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_DOUBLE_TAP_POWER_GESTURE.equals(preference.getKey())) {
            return false;
        }
        OPUtils.startFragment(this.mContext, OPQuickTurnOnAssistantApp.class.getName(), 9999);
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable() && this.mSwitch != null) {
            updateSwitchSummary();
        }
    }

    private void updateSwitchSummary() {
        boolean z = Settings.Global.getInt(this.mContext.getContentResolver(), "emergency_affordance_needed", 0) != 0;
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_emergency_tap_power_gesture_times", ONEPLUS_EMERGENCY_TAP_POWER_GESTURE_NO_TIMES);
        if (i == ONEPLUS_EMERGENCY_TAP_POWER_GESTURE_NO_TIMES) {
            i = z ? 3 : 5;
        }
        if (i == 3) {
            this.mSwitch.setEnabled(false);
            this.mSwitch.setSummary(C0017R$string.oneplus_emergency_tap_power_gesture_tips);
            return;
        }
        this.mSwitch.setEnabled(true);
        int i2 = Settings.Secure.getInt(this.mContext.getContentResolver(), "op_app_double_tap_power_gesture", 0);
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "camera_double_tap_power_gesture_disabled", 0) == 1) {
            this.mSwitch.setSummary(C0017R$string.oneplus_double_tap_power_none);
        } else if (i2 == 0) {
            this.mSwitch.setSummary(C0017R$string.oneplus_double_tap_power_gesture_camera);
        } else if (i2 == 1) {
            this.mSwitch.setSummary(C0017R$string.oneplus_double_tap_power_gesture_wallet);
        } else if (i2 == 2) {
            this.mSwitch.setSummary(C0017R$string.oneplus_double_tap_power_ask_alexa);
        }
    }
}
