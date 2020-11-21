package com.android.settings.panel;

import android.net.Uri;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class PanelSlicesLoaderCountdownLatch {
    private final CountDownLatch mCountDownLatch;
    private final Set<Uri> mLoadedSlices = new HashSet();
    private boolean slicesReadyToLoad = false;

    public PanelSlicesLoaderCountdownLatch(int i) {
        this.mCountDownLatch = new CountDownLatch(i);
    }

    public void markSliceLoaded(Uri uri) {
        if (!this.mLoadedSlices.contains(uri)) {
            this.mLoadedSlices.add(uri);
            this.mCountDownLatch.countDown();
        }
    }

    public boolean isSliceLoaded(Uri uri) {
        return this.mLoadedSlices.contains(uri);
    }

    public boolean isPanelReadyToLoad() {
        if (this.mCountDownLatch.getCount() != 0 || this.slicesReadyToLoad) {
            return false;
        }
        this.slicesReadyToLoad = true;
        return true;
    }
}
