package com.android.settings.slices;

import android.content.Context;
import android.content.IntentFilter;
import androidx.slice.Slice;
import com.android.settings.core.BasePreferenceController;

public class BlockingSlicePrefController extends SlicePreferenceController implements BasePreferenceController.UiBlocker {
    @Override // com.android.settings.slices.SlicePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.SlicePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.SlicePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.SlicePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.SlicePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.SlicePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.SlicePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.SlicePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BlockingSlicePrefController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.slices.SlicePreferenceController
    public void onChanged(Slice slice) {
        super.onChanged(slice);
        BasePreferenceController.UiBlockListener uiBlockListener = this.mUiBlockListener;
        if (uiBlockListener != null) {
            uiBlockListener.onBlockerWorkFinished(this);
        }
    }
}
