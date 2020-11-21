package com.android.settings.notification;

import android.content.Context;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import com.android.settings.notification.VolumeSeekBarPreference;
import com.android.settings.slices.SliceBackgroundWorker;

public abstract class VolumeSeekBarPreferenceController extends AdjustVolumeRestrictedPreferenceController implements LifecycleObserver {
    protected AudioHelper mHelper;
    protected VolumeSeekBarPreference mPreference;
    protected VolumeSeekBarPreference.Callback mVolumePreferenceCallback;

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    /* access modifiers changed from: protected */
    public abstract int getAudioStream();

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    /* access modifiers changed from: protected */
    public abstract int getMuteIcon();

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public VolumeSeekBarPreferenceController(Context context, String str) {
        super(context, str);
        setAudioHelper(new AudioHelper(context));
    }

    /* access modifiers changed from: package-private */
    public void setAudioHelper(AudioHelper audioHelper) {
        this.mHelper = audioHelper;
    }

    public void setCallback(VolumeSeekBarPreference.Callback callback) {
        this.mVolumePreferenceCallback = callback;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            VolumeSeekBarPreference volumeSeekBarPreference = (VolumeSeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
            this.mPreference = volumeSeekBarPreference;
            volumeSeekBarPreference.setCallback(this.mVolumePreferenceCallback);
            this.mPreference.setStream(getAudioStream());
            this.mPreference.setMuteIcon(getMuteIcon());
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            volumeSeekBarPreference.onActivityResume();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            volumeSeekBarPreference.onActivityPause();
        }
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getSliderPosition() {
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            return volumeSeekBarPreference.getProgress();
        }
        return this.mHelper.getStreamVolume(getAudioStream());
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public boolean setSliderPosition(int i) {
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            volumeSeekBarPreference.setProgress(i);
        }
        return this.mHelper.setStreamVolume(getAudioStream(), i);
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMax() {
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            return volumeSeekBarPreference.getMax();
        }
        return this.mHelper.getMaxVolume(getAudioStream());
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMin() {
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            return volumeSeekBarPreference.getMin();
        }
        return this.mHelper.getMinVolume(getAudioStream());
    }
}
