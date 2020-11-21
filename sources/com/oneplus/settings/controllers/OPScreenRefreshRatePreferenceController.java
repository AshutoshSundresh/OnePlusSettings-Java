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

public class OPScreenRefreshRatePreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private static final String KEY_SCREEN_REFRESH_RATE = "oneplus_screen_refresh_rate";
    private static final int OP_AUTO_MODE_VALUE = 2;
    private ContentObserver mScreenRefreshRateObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        /* class com.oneplus.settings.controllers.OPScreenRefreshRatePreferenceController.AnonymousClass1 */

        public void onChange(boolean z) {
            if (OPScreenRefreshRatePreferenceController.this.mScreenRefreshRatePreference != null) {
                OPScreenRefreshRatePreferenceController oPScreenRefreshRatePreferenceController = OPScreenRefreshRatePreferenceController.this;
                oPScreenRefreshRatePreferenceController.updateState(oPScreenRefreshRatePreferenceController.mScreenRefreshRatePreference);
            }
        }
    };
    private Preference mScreenRefreshRatePreference;

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

    public OPScreenRefreshRatePreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_SCREEN_REFRESH_RATE);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return OPUtils.isSupportScreenRefreshRate() ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreenRefreshRatePreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(getPreferenceKey()), true, this.mScreenRefreshRateObserver, -1);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mScreenRefreshRateObserver);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i;
        int i2 = Settings.Global.getInt(this.mContext.getContentResolver(), getPreferenceKey(), 2);
        Context context = this.mContext;
        if (i2 == 2) {
            i = OPUtils.isSupportHighVsync() ? C0017R$string.oneplus_screen_refresh_rate_high_vsync : C0017R$string.oneplus_screen_refresh_rate_auto;
        } else {
            i = C0017R$string.oneplus_screen_refresh_rate_60hz;
        }
        return context.getString(i);
    }
}
