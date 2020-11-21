package com.android.settings.display;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class AmbientDisplayAlwaysOnPreferenceController extends TogglePreferenceController {
    private static final int MY_USER = UserHandle.myUserId();
    private static final String PROP_AWARE_AVAILABLE = "ro.vendor.aware_available";
    private final int OFF = 0;
    private final int ON = 1;
    private OnPreferenceChangedCallback mCallback;
    private AmbientDisplayConfiguration mConfig;

    public interface OnPreferenceChangedCallback {
        void onPreferenceChanged();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AmbientDisplayAlwaysOnPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!isAvailable(getConfig()) || SystemProperties.getBoolean(PROP_AWARE_AVAILABLE, false)) {
            return 3;
        }
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "ambient_display_always_on");
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return getConfig().alwaysOnEnabled(MY_USER);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "doze_always_on", z ? 1 : 0);
        OnPreferenceChangedCallback onPreferenceChangedCallback = this.mCallback;
        if (onPreferenceChangedCallback == null) {
            return true;
        }
        onPreferenceChangedCallback.onPreferenceChanged();
        return true;
    }

    public AmbientDisplayAlwaysOnPreferenceController setConfig(AmbientDisplayConfiguration ambientDisplayConfiguration) {
        this.mConfig = ambientDisplayConfiguration;
        return this;
    }

    public AmbientDisplayAlwaysOnPreferenceController setCallback(OnPreferenceChangedCallback onPreferenceChangedCallback) {
        this.mCallback = onPreferenceChangedCallback;
        return this;
    }

    public static boolean isAvailable(AmbientDisplayConfiguration ambientDisplayConfiguration) {
        return ambientDisplayConfiguration.alwaysOnAvailableForUser(MY_USER);
    }

    private AmbientDisplayConfiguration getConfig() {
        if (this.mConfig == null) {
            this.mConfig = new AmbientDisplayConfiguration(this.mContext);
        }
        return this.mConfig;
    }
}
