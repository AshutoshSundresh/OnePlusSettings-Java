package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;
import com.android.settingslib.utils.ThreadUtils;
import java.text.NumberFormat;

public class TopLevelStoragePreferenceController extends BasePreferenceController {
    private final StorageManager mStorageManager;
    private final StorageManagerVolumeProvider mStorageManagerVolumeProvider;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public TopLevelStoragePreferenceController(Context context, String str) {
        super(context, str);
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        this.mStorageManager = storageManager;
        this.mStorageManagerVolumeProvider = new StorageManagerVolumeProvider(storageManager);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void refreshSummary(Preference preference) {
        if (preference != null) {
            ThreadUtils.postOnBackgroundThread(new Runnable(preference) {
                /* class com.android.settings.deviceinfo.$$Lambda$TopLevelStoragePreferenceController$U7ZEz_Sh5aDJDkrfySNJywBLfnA */
                public final /* synthetic */ Preference f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TopLevelStoragePreferenceController.this.lambda$refreshSummary$1$TopLevelStoragePreferenceController(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$refreshSummary$1 */
    public /* synthetic */ void lambda$refreshSummary$1$TopLevelStoragePreferenceController(Preference preference) {
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        PrivateStorageInfo privateStorageInfo = PrivateStorageInfo.getPrivateStorageInfo(this.mStorageManagerVolumeProvider);
        ThreadUtils.postOnMainThread(new Runnable(preference, percentInstance, (double) (privateStorageInfo.totalBytes - privateStorageInfo.freeBytes), privateStorageInfo) {
            /* class com.android.settings.deviceinfo.$$Lambda$TopLevelStoragePreferenceController$UEnBCjxQQtYsCjP43GxlWGD3euo */
            public final /* synthetic */ Preference f$1;
            public final /* synthetic */ NumberFormat f$2;
            public final /* synthetic */ double f$3;
            public final /* synthetic */ PrivateStorageInfo f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
            }

            public final void run() {
                TopLevelStoragePreferenceController.this.lambda$refreshSummary$0$TopLevelStoragePreferenceController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$refreshSummary$0 */
    public /* synthetic */ void lambda$refreshSummary$0$TopLevelStoragePreferenceController(Preference preference, NumberFormat numberFormat, double d, PrivateStorageInfo privateStorageInfo) {
        preference.setSummary(this.mContext.getString(C0017R$string.storage_summary, numberFormat.format(d / ((double) privateStorageInfo.totalBytes)), Formatter.formatFileSize(this.mContext, privateStorageInfo.freeBytes)));
    }
}
