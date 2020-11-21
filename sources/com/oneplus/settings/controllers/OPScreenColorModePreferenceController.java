package com.oneplus.settings.controllers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.better.OPReadingModeTurnOnPreferenceController;
import com.oneplus.settings.utils.OPUtils;

public class OPScreenColorModePreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private static final String KEY_SCREEN_COLOR_MODE = "screen_color_mode";
    private ContentObserver mScreenColorModeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        /* class com.oneplus.settings.controllers.OPScreenColorModePreferenceController.AnonymousClass1 */

        public void onChange(boolean z) {
            if (OPScreenColorModePreferenceController.this.mScreenColorModePreference != null) {
                OPScreenColorModePreferenceController oPScreenColorModePreferenceController = OPScreenColorModePreferenceController.this;
                oPScreenColorModePreferenceController.updateState(oPScreenColorModePreferenceController.mScreenColorModePreference);
            }
        }
    };
    private Preference mScreenColorModePreference;

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

    public OPScreenColorModePreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_SCREEN_COLOR_MODE);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return OPUtils.isGuestMode() ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreenColorModePreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("night_display_activated"), true, this.mScreenColorModeObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS), true, this.mScreenColorModeObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("accessibility_display_daltonizer_enabled"), true, this.mScreenColorModeObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("accessibility_display_inversion_enabled"), true, this.mScreenColorModeObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("accessibility_display_grayscale_enabled"), true, this.mScreenColorModeObserver, -1);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mScreenColorModeObserver);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean z = true;
        boolean z2 = Settings.Secure.getIntForUser(contentResolver, "night_display_activated", 0, -2) != 0;
        boolean z3 = Settings.System.getIntForUser(contentResolver, OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS, 0, -2) != 0;
        if (Settings.System.getInt(contentResolver, "accessibility_display_grayscale_enabled", 1) == 0) {
            preference.setEnabled(false);
            updateScreenColorModePreference(preference);
        } else if (z2) {
            preference.setEnabled(false);
            preference.setSummary(C0017R$string.oneplus_screen_color_mode_title_summary);
        } else {
            boolean hasSystemFeature = this.mContext.getPackageManager().hasSystemFeature("oem.read_mode.support");
            if (!z3 || !hasSystemFeature) {
                updateScreenColorModePreference(preference);
                preference.setEnabled(true);
            } else {
                preference.setEnabled(false);
                preference.setSummary(C0017R$string.oneplus_screen_color_mode_reading_mode_on_summary);
            }
        }
        boolean z4 = Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer_enabled", 12) == 1;
        if (Settings.Secure.getInt(contentResolver, "accessibility_display_inversion_enabled", 0) != 1) {
            z = false;
        }
        if (z4 || z) {
            preference.setEnabled(false);
        }
    }

    private void updateScreenColorModePreference(Preference preference) {
        int i;
        int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1, -2);
        int i2 = -1;
        if (!OPUtils.isSupportMMDisplayColorScreenMode()) {
            switch (intForUser) {
                case 1:
                    i2 = C0017R$string.oneplus_screen_color_mode_default;
                    break;
                case 2:
                    i2 = C0017R$string.oneplus_screen_color_mode_basic;
                    break;
                case 3:
                    i2 = C0017R$string.oneplus_screen_color_mode_defined;
                    break;
                case 4:
                    i2 = C0017R$string.oneplus_screen_color_mode_dci_p3;
                    break;
                case 5:
                    i2 = C0017R$string.oneplus_adaptive_model;
                    break;
                case 6:
                    i2 = C0017R$string.oneplus_screen_color_mode_soft;
                    break;
            }
        } else {
            if (intForUser == 1) {
                i = C0017R$string.screen_color_mode_vivid;
            } else if (intForUser == 3) {
                i = C0017R$string.screen_color_mode_advanced;
            } else if (intForUser == 10) {
                i = C0017R$string.oneplus_screen_better_nature;
            }
            i2 = i;
        }
        if (i2 > 0) {
            preference.setSummary(i2);
        }
    }
}
