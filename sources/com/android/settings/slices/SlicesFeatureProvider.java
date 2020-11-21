package com.android.settings.slices;

import android.content.Context;
import android.net.Uri;
import com.android.settings.network.telephony.Enhanced4gLteSliceHelper;
import com.android.settings.wifi.calling.WifiCallingSliceHelper;

public interface SlicesFeatureProvider {
    Enhanced4gLteSliceHelper getNewEnhanced4gLteSliceHelper(Context context);

    WifiCallingSliceHelper getNewWifiCallingSliceHelper(Context context);

    SliceDataConverter getSliceDataConverter(Context context);

    CustomSliceable getSliceableFromUri(Context context, Uri uri);

    long getUiSessionToken();

    void indexSliceData(Context context);

    void indexSliceDataAsync(Context context);

    void newUiSession();
}
