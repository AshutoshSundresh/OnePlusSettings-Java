package com.android.settings.display;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0005R$bool;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.concurrent.Executor;

public class PeakRefreshRatePreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    static float DEFAULT_REFRESH_RATE = 60.0f;
    private static final float INVALIDATE_REFRESH_RATE = -1.0f;
    private static final String KEY_PEAK_REFRESH_RATE_DEFAULT = "peak_refresh_rate_default";
    private static final String TAG = "RefreshRatePrefCtr";
    private final DeviceConfigDisplaySettings mDeviceConfigDisplaySettings = new DeviceConfigDisplaySettings();
    private final Handler mHandler = new Handler();
    private final IDeviceConfigChange mOnDeviceConfigChange = new IDeviceConfigChange() {
        /* class com.android.settings.display.PeakRefreshRatePreferenceController.AnonymousClass1 */

        @Override // com.android.settings.display.PeakRefreshRatePreferenceController.IDeviceConfigChange
        public void onDefaultRefreshRateChanged() {
            PeakRefreshRatePreferenceController peakRefreshRatePreferenceController = PeakRefreshRatePreferenceController.this;
            peakRefreshRatePreferenceController.updateState(peakRefreshRatePreferenceController.mPreference);
        }
    };
    float mPeakRefreshRate;
    private Preference mPreference;

    /* access modifiers changed from: private */
    public interface IDeviceConfigChange {
        void onDefaultRefreshRateChanged();
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
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public PeakRefreshRatePreferenceController(Context context, String str) {
        super(context, str);
        Display display = ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(0);
        if (display == null) {
            Log.w(TAG, "No valid default display device");
            this.mPeakRefreshRate = DEFAULT_REFRESH_RATE;
        } else {
            this.mPeakRefreshRate = findPeakRefreshRate(display.getSupportedModes());
        }
        Log.d(TAG, "DEFAULT_REFRESH_RATE : " + DEFAULT_REFRESH_RATE + " mPeakRefreshRate : " + this.mPeakRefreshRate);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!this.mContext.getResources().getBoolean(C0005R$bool.config_show_smooth_display) || this.mPeakRefreshRate <= DEFAULT_REFRESH_RATE) {
            return 3;
        }
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.System.getFloat(this.mContext.getContentResolver(), "peak_refresh_rate", getDefaultPeakRefreshRate()) == this.mPeakRefreshRate;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        float f = z ? this.mPeakRefreshRate : DEFAULT_REFRESH_RATE;
        Log.d(TAG, "setChecked to : " + f);
        return Settings.System.putFloat(this.mContext.getContentResolver(), "peak_refresh_rate", f);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mDeviceConfigDisplaySettings.startListening();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mDeviceConfigDisplaySettings.stopListening();
    }

    private float findPeakRefreshRate(Display.Mode[] modeArr) {
        float f = DEFAULT_REFRESH_RATE;
        for (Display.Mode mode : modeArr) {
            if (((float) Math.round(mode.getRefreshRate())) > DEFAULT_REFRESH_RATE) {
                f = mode.getRefreshRate();
            }
        }
        return f;
    }

    /* access modifiers changed from: private */
    public class DeviceConfigDisplaySettings implements DeviceConfig.OnPropertiesChangedListener, Executor {
        private DeviceConfigDisplaySettings() {
        }

        public void startListening() {
            DeviceConfig.addOnPropertiesChangedListener("display_manager", this, this);
        }

        public void stopListening() {
            DeviceConfig.removeOnPropertiesChangedListener(this);
        }

        public float getDefaultPeakRefreshRate() {
            float f = DeviceConfig.getFloat("display_manager", PeakRefreshRatePreferenceController.KEY_PEAK_REFRESH_RATE_DEFAULT, (float) PeakRefreshRatePreferenceController.INVALIDATE_REFRESH_RATE);
            Log.d(PeakRefreshRatePreferenceController.TAG, "DeviceConfig getDefaultPeakRefreshRate : " + f);
            return f;
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            if (PeakRefreshRatePreferenceController.this.mOnDeviceConfigChange != null) {
                PeakRefreshRatePreferenceController.this.mOnDeviceConfigChange.onDefaultRefreshRateChanged();
                PeakRefreshRatePreferenceController peakRefreshRatePreferenceController = PeakRefreshRatePreferenceController.this;
                peakRefreshRatePreferenceController.updateState(peakRefreshRatePreferenceController.mPreference);
            }
        }

        public void execute(Runnable runnable) {
            if (PeakRefreshRatePreferenceController.this.mHandler != null) {
                PeakRefreshRatePreferenceController.this.mHandler.post(runnable);
            }
        }
    }

    private float getDefaultPeakRefreshRate() {
        float defaultPeakRefreshRate = this.mDeviceConfigDisplaySettings.getDefaultPeakRefreshRate();
        return defaultPeakRefreshRate == INVALIDATE_REFRESH_RATE ? (float) this.mContext.getResources().getInteger(17694783) : defaultPeakRefreshRate;
    }
}
