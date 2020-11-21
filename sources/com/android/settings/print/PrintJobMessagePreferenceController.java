package com.android.settings.print;

import android.content.Context;
import android.content.IntentFilter;
import android.print.PrintJob;
import android.text.TextUtils;
import com.android.settings.slices.SliceBackgroundWorker;

public class PrintJobMessagePreferenceController extends PrintJobPreferenceControllerBase {
    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public PrintJobMessagePreferenceController(Context context, String str) {
        super(context, str);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.print.PrintJobPreferenceControllerBase
    public void updateUi() {
        PrintJob printJob = getPrintJob();
        if (printJob == null) {
            this.mFragment.finish();
        } else if (printJob.isCancelled() || printJob.isCompleted()) {
            this.mFragment.finish();
        } else {
            CharSequence status = printJob.getInfo().getStatus(this.mContext.getPackageManager());
            this.mPreference.setVisible(!TextUtils.isEmpty(status));
            this.mPreference.setSummary(status);
        }
    }
}
