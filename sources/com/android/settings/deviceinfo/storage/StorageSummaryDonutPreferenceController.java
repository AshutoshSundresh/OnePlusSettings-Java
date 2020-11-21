package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.content.IntentFilter;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.SpannableStringBuilder;
import android.text.format.Formatter;
import android.text.style.AbsoluteSizeSpan;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;
import com.android.settingslib.deviceinfo.StorageVolumeProvider;
import com.android.settingslib.utils.ThreadUtils;
import java.text.NumberFormat;

public class StorageSummaryDonutPreferenceController extends BasePreferenceController {
    private final StorageManager mStorageManager;
    private final StorageManagerVolumeProvider mStorageManagerVolumeProvider;
    private StorageSummaryDonutPreference mSummary;
    private long mTotalBytes;
    private long mUsedBytes;

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

    public StorageSummaryDonutPreferenceController(Context context, String str) {
        super(context, str);
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        this.mStorageManager = storageManager;
        this.mStorageManagerVolumeProvider = new StorageManagerVolumeProvider(storageManager);
    }

    public static CharSequence convertUsedBytesToFormattedText(Context context, long j) {
        Formatter.BytesResult formatBytes = Formatter.formatBytes(context.getResources(), j, 0);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(formatBytes.value, new AbsoluteSizeSpan(144), 33);
        spannableStringBuilder.append(formatBytes.units, new AbsoluteSizeSpan(84), 33);
        return spannableStringBuilder;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        StorageSummaryDonutPreference storageSummaryDonutPreference = (StorageSummaryDonutPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mSummary = storageSummaryDonutPreference;
        storageSummaryDonutPreference.setEnabled(true);
        ThreadUtils.postOnBackgroundThread(new Runnable() {
            /* class com.android.settings.deviceinfo.storage.$$Lambda$StorageSummaryDonutPreferenceController$ULdQpm9X1e2pbg1jKuT7ElRmKVI */

            public final void run() {
                StorageSummaryDonutPreferenceController.this.lambda$displayPreference$1$StorageSummaryDonutPreferenceController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$1 */
    public /* synthetic */ void lambda$displayPreference$1$StorageSummaryDonutPreferenceController() {
        NumberFormat.getPercentInstance();
        PrivateStorageInfo privateStorageInfo = PrivateStorageInfo.getPrivateStorageInfo(this.mStorageManagerVolumeProvider);
        long j = privateStorageInfo.totalBytes;
        long j2 = privateStorageInfo.freeBytes;
        this.mTotalBytes = j;
        this.mUsedBytes = j - j2;
        ThreadUtils.postOnMainThread(new Runnable() {
            /* class com.android.settings.deviceinfo.storage.$$Lambda$StorageSummaryDonutPreferenceController$NKLqJabDqde5RIIkBzQ1MlzIOB8 */

            public final void run() {
                StorageSummaryDonutPreferenceController.this.lambda$displayPreference$0$StorageSummaryDonutPreferenceController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$StorageSummaryDonutPreferenceController() {
        updateState(this.mSummary);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mSummary.setTitle(convertUsedBytesToFormattedText(this.mContext, this.mUsedBytes));
        StorageSummaryDonutPreference storageSummaryDonutPreference = this.mSummary;
        Context context = this.mContext;
        storageSummaryDonutPreference.setSummary(context.getString(C0017R$string.storage_volume_total, Formatter.formatShortFileSize(context, this.mTotalBytes)));
        this.mSummary.setPercent(this.mUsedBytes, this.mTotalBytes);
        this.mSummary.setEnabled(true);
    }

    public void invalidateData() {
        StorageSummaryDonutPreference storageSummaryDonutPreference = this.mSummary;
        if (storageSummaryDonutPreference != null) {
            updateState(storageSummaryDonutPreference);
        }
    }

    public void updateBytes(long j, long j2) {
        this.mUsedBytes = j;
        this.mTotalBytes = j2;
        invalidateData();
    }

    public void updateSizes(StorageVolumeProvider storageVolumeProvider, VolumeInfo volumeInfo) {
        long totalSpace = volumeInfo.getPath().getTotalSpace();
        long primaryStorageSize = storageVolumeProvider.getPrimaryStorageSize();
        if (primaryStorageSize > 0) {
            totalSpace = primaryStorageSize;
        }
        updateBytes(totalSpace - volumeInfo.getPath().getFreeSpace(), totalSpace);
    }
}
