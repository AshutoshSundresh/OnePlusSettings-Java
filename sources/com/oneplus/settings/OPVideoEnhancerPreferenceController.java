package com.oneplus.settings;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.widget.OPVideoPreference;

public class OPVideoEnhancerPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause, OnDestroy {
    static final String KEY_VIDEO_SOURCE = "video_source";
    private OPVideoPreference mVideoPreference;

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

    public OPVideoEnhancerPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_VIDEO_SOURCE);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return OPUtils.isSupportVideoEnhancer() ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mVideoPreference = (OPVideoPreference) preferenceScreen.findPreference(KEY_VIDEO_SOURCE);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        OPVideoPreference oPVideoPreference = this.mVideoPreference;
        if (oPVideoPreference != null) {
            oPVideoPreference.setVideoPaused();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        OPVideoPreference oPVideoPreference = this.mVideoPreference;
        if (oPVideoPreference != null) {
            oPVideoPreference.release();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        OPVideoPreference oPVideoPreference = this.mVideoPreference;
        if (oPVideoPreference != null) {
            oPVideoPreference.setVideoResume();
        }
    }
}
