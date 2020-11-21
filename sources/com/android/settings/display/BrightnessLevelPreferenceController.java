package com.android.settings.display;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.vr.IVrManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.display.BrightnessUtils;
import java.text.NumberFormat;

public class BrightnessLevelPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnStart, OnStop {
    private static final Uri BRIGHTNESS_ADJ_URI = Settings.System.getUriFor("screen_auto_brightness_adj");
    private static final Uri BRIGHTNESS_FOR_VR_URI = Settings.System.getUriFor("screen_brightness_for_vr");
    private static final Uri BRIGHTNESS_URI = Settings.System.getUriFor("screen_brightness_float");
    private ContentObserver mBrightnessObserver;
    private final ContentResolver mContentResolver;
    private final float mMaxBrightness;
    private final float mMaxVrBrightness;
    private final float mMinBrightness;
    private final float mMinVrBrightness;

    private double getPercentage(double d, int i, int i2) {
        if (d > ((double) i2)) {
            return 1.0d;
        }
        double d2 = (double) i;
        if (d < d2) {
            return 0.0d;
        }
        return (d - d2) / ((double) (i2 - i));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "brightness";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        preferenceScreen.findPreference("brightness");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updatedSummary(preference);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContentResolver.registerContentObserver(BRIGHTNESS_URI, false, this.mBrightnessObserver);
        this.mContentResolver.registerContentObserver(BRIGHTNESS_FOR_VR_URI, false, this.mBrightnessObserver);
        this.mContentResolver.registerContentObserver(BRIGHTNESS_ADJ_URI, false, this.mBrightnessObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContentResolver.unregisterContentObserver(this.mBrightnessObserver);
    }

    private void updatedSummary(Preference preference) {
        if (preference != null) {
            preference.setSummary(NumberFormat.getPercentInstance().format(getCurrentBrightness()));
        }
    }

    private double getCurrentBrightness() {
        int i;
        if (isInVrMode()) {
            i = BrightnessUtils.convertLinearToGammaFloat(Settings.System.getFloat(this.mContentResolver, "screen_brightness_for_vr_float", this.mMaxBrightness), this.mMinVrBrightness, this.mMaxVrBrightness);
        } else {
            i = BrightnessUtils.convertLinearToGammaFloat(Settings.System.getFloat(this.mContentResolver, "screen_brightness_float", this.mMinBrightness), this.mMinBrightness, this.mMaxBrightness);
        }
        return getPercentage((double) i, 0, 65535);
    }

    /* access modifiers changed from: package-private */
    public IVrManager safeGetVrManager() {
        return IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
    }

    /* access modifiers changed from: package-private */
    public boolean isInVrMode() {
        IVrManager safeGetVrManager = safeGetVrManager();
        if (safeGetVrManager == null) {
            return false;
        }
        try {
            return safeGetVrManager.getVrModeState();
        } catch (RemoteException e) {
            Log.e("BrightnessPrefCtrl", "Failed to check vr mode!", e);
            return false;
        }
    }
}
