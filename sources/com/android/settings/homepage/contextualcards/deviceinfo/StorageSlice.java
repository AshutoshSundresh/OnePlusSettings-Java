package com.android.settings.homepage.contextualcards.deviceinfo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.SubSettings;
import com.android.settings.deviceinfo.StorageDashboardFragment;
import com.android.settings.deviceinfo.storage.StorageSummaryDonutPreferenceController;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;

public class StorageSlice implements CustomSliceable {
    private final Context mContext;

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
    }

    public StorageSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.STORAGE_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_homepage_storage);
        String string = this.mContext.getString(C0017R$string.storage_label);
        SliceAction createDeeplink = SliceAction.createDeeplink(getPrimaryAction(), createWithResource, 0, string);
        PrivateStorageInfo privateStorageInfo = getPrivateStorageInfo();
        ListBuilder listBuilder = new ListBuilder(this.mContext, CustomSliceRegistry.STORAGE_SLICE_URI, -1);
        listBuilder.setAccentColor(Utils.getColorAccentDefaultColor(this.mContext));
        ListBuilder.HeaderBuilder headerBuilder = new ListBuilder.HeaderBuilder();
        headerBuilder.setTitle(string);
        listBuilder.setHeader(headerBuilder);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitle(getStorageUsedText(privateStorageInfo));
        rowBuilder.setSubtitle(getStorageSummaryText(privateStorageInfo));
        rowBuilder.setPrimaryAction(createDeeplink);
        listBuilder.addRow(rowBuilder);
        return listBuilder.build();
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, StorageDashboardFragment.class.getName(), "", this.mContext.getText(C0017R$string.storage_label).toString(), 1401).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(CustomSliceRegistry.STORAGE_SLICE_URI);
    }

    private PendingIntent getPrimaryAction() {
        return PendingIntent.getActivity(this.mContext, 0, getIntent(), 0);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public PrivateStorageInfo getPrivateStorageInfo() {
        return PrivateStorageInfo.getPrivateStorageInfo(new StorageManagerVolumeProvider((StorageManager) this.mContext.getSystemService(StorageManager.class)));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public CharSequence getStorageUsedText(PrivateStorageInfo privateStorageInfo) {
        return StorageSummaryDonutPreferenceController.convertUsedBytesToFormattedText(this.mContext, privateStorageInfo.totalBytes - privateStorageInfo.freeBytes);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public CharSequence getStorageSummaryText(PrivateStorageInfo privateStorageInfo) {
        Context context = this.mContext;
        return context.getString(C0017R$string.storage_volume_total, Formatter.formatShortFileSize(context, privateStorageInfo.totalBytes));
    }
}
