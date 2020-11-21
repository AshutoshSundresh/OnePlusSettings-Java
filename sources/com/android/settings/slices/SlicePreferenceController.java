package com.android.settings.slices;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceScreen;
import androidx.slice.Slice;
import androidx.slice.widget.SliceLiveData;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class SlicePreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, Observer<Slice> {
    private static final String TAG = "SlicePreferenceController";
    LiveData<Slice> mLiveData;
    SlicePreference mSlicePreference;
    private Uri mUri;

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

    public SlicePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSlicePreference = (SlicePreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mUri != null ? 0 : 3;
    }

    public void setSliceUri(Uri uri) {
        this.mUri = uri;
        LiveData<Slice> fromUri = SliceLiveData.fromUri(this.mContext, uri, new SliceLiveData.OnErrorListener(uri) {
            /* class com.android.settings.slices.$$Lambda$SlicePreferenceController$m_43wPv9OceTDRhAvK9GAlDkl9U */
            public final /* synthetic */ Uri f$0;

            {
                this.f$0 = r1;
            }

            @Override // androidx.slice.widget.SliceLiveData.OnErrorListener
            public final void onSliceError(int i, Throwable th) {
                Log.w((Uri) SlicePreferenceController.TAG, (int) ("Slice may be null. uri = " + this.f$0 + ", error = " + i));
            }
        });
        this.mLiveData = fromUri;
        fromUri.removeObserver(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        LiveData<Slice> liveData = this.mLiveData;
        if (liveData != null) {
            liveData.observeForever(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        LiveData<Slice> liveData = this.mLiveData;
        if (liveData != null) {
            liveData.removeObserver(this);
        }
    }

    public void onChanged(Slice slice) {
        this.mSlicePreference.onSliceUpdated(slice);
    }
}
