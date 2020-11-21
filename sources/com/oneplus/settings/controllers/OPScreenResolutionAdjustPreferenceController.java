package com.oneplus.settings.controllers;

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
import com.oneplus.settings.utils.OPUtils;

public class OPScreenResolutionAdjustPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private static final String KEY_SCREEN_RESOLUTION_ADJUST = "oneplus_screen_resolution_adjust";
    private ContentObserver mScreenResolutionAdjustObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        /* class com.oneplus.settings.controllers.OPScreenResolutionAdjustPreferenceController.AnonymousClass1 */

        public void onChange(boolean z) {
            if (OPScreenResolutionAdjustPreferenceController.this.mScreenResolutionAdjustPreference != null) {
                OPScreenResolutionAdjustPreferenceController oPScreenResolutionAdjustPreferenceController = OPScreenResolutionAdjustPreferenceController.this;
                oPScreenResolutionAdjustPreferenceController.updateState(oPScreenResolutionAdjustPreferenceController.mScreenResolutionAdjustPreference);
            }
        }
    };
    private Preference mScreenResolutionAdjustPreference;

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

    public OPScreenResolutionAdjustPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_SCREEN_RESOLUTION_ADJUST);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!OPUtils.isSupportMultiScreenResolution(this.mContext) || OPUtils.isGuestMode()) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreenResolutionAdjustPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(KEY_SCREEN_RESOLUTION_ADJUST), true, this.mScreenResolutionAdjustObserver, -1);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mScreenResolutionAdjustObserver);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i;
        int i2 = Settings.Global.getInt(this.mContext.getContentResolver(), KEY_SCREEN_RESOLUTION_ADJUST, 2);
        if (i2 == 0) {
            i = C0017R$string.oneplus_screen_resolution_adjust_other;
        } else if (i2 == 1) {
            i = C0017R$string.oneplus_screen_resolution_adjust_1080p;
        } else if (i2 != 2) {
            i = -1;
        } else {
            i = C0017R$string.oneplus_screen_resolution_adjust_other;
        }
        if (i > 0) {
            return this.mContext.getString(i);
        }
        return super.getSummary();
    }
}
