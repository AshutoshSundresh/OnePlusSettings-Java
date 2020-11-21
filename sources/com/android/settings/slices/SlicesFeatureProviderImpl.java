package com.android.settings.slices;

import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import com.android.settings.network.telephony.Enhanced4gLteSliceHelper;
import com.android.settings.wifi.calling.WifiCallingSliceHelper;
import com.android.settingslib.utils.ThreadUtils;

public class SlicesFeatureProviderImpl implements SlicesFeatureProvider {
    private SliceDataConverter mSliceDataConverter;
    private SlicesIndexer mSlicesIndexer;
    private long mUiSessionToken;

    @Override // com.android.settings.slices.SlicesFeatureProvider
    public SliceDataConverter getSliceDataConverter(Context context) {
        if (this.mSliceDataConverter == null) {
            this.mSliceDataConverter = new SliceDataConverter(context.getApplicationContext());
        }
        return this.mSliceDataConverter;
    }

    @Override // com.android.settings.slices.SlicesFeatureProvider
    public void newUiSession() {
        this.mUiSessionToken = SystemClock.elapsedRealtime();
    }

    @Override // com.android.settings.slices.SlicesFeatureProvider
    public long getUiSessionToken() {
        return this.mUiSessionToken;
    }

    @Override // com.android.settings.slices.SlicesFeatureProvider
    public void indexSliceDataAsync(Context context) {
        ThreadUtils.postOnBackgroundThread(getSliceIndexer(context));
    }

    @Override // com.android.settings.slices.SlicesFeatureProvider
    public void indexSliceData(Context context) {
        getSliceIndexer(context).indexSliceData();
    }

    @Override // com.android.settings.slices.SlicesFeatureProvider
    public WifiCallingSliceHelper getNewWifiCallingSliceHelper(Context context) {
        return new WifiCallingSliceHelper(context);
    }

    @Override // com.android.settings.slices.SlicesFeatureProvider
    public Enhanced4gLteSliceHelper getNewEnhanced4gLteSliceHelper(Context context) {
        return new Enhanced4gLteSliceHelper(context);
    }

    @Override // com.android.settings.slices.SlicesFeatureProvider
    public CustomSliceable getSliceableFromUri(Context context, Uri uri) {
        Class<? extends CustomSliceable> sliceClassByUri = CustomSliceRegistry.getSliceClassByUri(CustomSliceRegistry.removeParameterFromUri(uri));
        if (sliceClassByUri != null) {
            return CustomSliceable.createInstance(context, sliceClassByUri);
        }
        throw new IllegalArgumentException("No Slice found for uri: " + uri);
    }

    private SlicesIndexer getSliceIndexer(Context context) {
        if (this.mSlicesIndexer == null) {
            this.mSlicesIndexer = new SlicesIndexer(context.getApplicationContext());
        }
        return this.mSlicesIndexer;
    }
}
