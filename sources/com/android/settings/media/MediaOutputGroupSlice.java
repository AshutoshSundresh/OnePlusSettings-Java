package com.android.settings.media;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settingslib.media.MediaDevice;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MediaOutputGroupSlice implements CustomSliceable {
    static final int ACTION_MEDIA_SESSION_OPERATION = 2;
    static final int ACTION_VOLUME_ADJUSTMENT = 1;
    static final String CUSTOMIZED_ACTION = "customized_action";
    static final int ERROR = -1;
    static final String GROUP_DEVICES = "group_devices";
    static final String MEDIA_DEVICE_ID = "media_device_id";
    private final Context mContext;
    private MediaDeviceUpdateWorker mWorker;

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return null;
    }

    public MediaOutputGroupSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        ListBuilder listBuilder = new ListBuilder(this.mContext, getUri(), -1);
        listBuilder.setAccentColor(ERROR);
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_speaker_group_black_24dp);
        Bitmap createBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        if (getWorker() == null) {
            return listBuilder.build();
        }
        int sessionVolumeMax = getWorker().getSessionVolumeMax();
        String string = this.mContext.getString(C0017R$string.media_output_group);
        SliceAction createDeeplink = SliceAction.createDeeplink(getBroadcastIntent(GROUP_DEVICES, 1611029277, 2), createWithResource, 0, GROUP_DEVICES);
        SliceAction createDeeplink2 = SliceAction.createDeeplink(getBroadcastIntent(GROUP_DEVICES, 1611029279, 2), IconCompat.createWithBitmap(createBitmap), 0, "");
        if (sessionVolumeMax <= 0 || getWorker().hasAdjustVolumeUserRestriction()) {
            ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
            rowBuilder.setTitleItem(createWithResource, 0);
            rowBuilder.setTitle(string);
            rowBuilder.setPrimaryAction(createDeeplink);
            listBuilder.addRow(rowBuilder);
        } else {
            ListBuilder.InputRangeBuilder inputRangeBuilder = new ListBuilder.InputRangeBuilder();
            inputRangeBuilder.setTitleItem(createWithResource, 0);
            inputRangeBuilder.addEndItem(createDeeplink2);
            inputRangeBuilder.setTitle(string);
            inputRangeBuilder.setPrimaryAction(createDeeplink);
            inputRangeBuilder.setInputAction(getBroadcastIntent(GROUP_DEVICES, 1611029278, 1));
            inputRangeBuilder.setMax(sessionVolumeMax);
            inputRangeBuilder.setValue(getWorker().getSessionVolume());
            listBuilder.addInputRange(inputRangeBuilder);
        }
        addRow(listBuilder, getWorker().getSelectedMediaDevice(), true);
        addRow(listBuilder, getWorker().getSelectableMediaDevice(), false);
        return listBuilder.build();
    }

    private void addRow(ListBuilder listBuilder, List<MediaDevice> list, boolean z) {
        boolean hasAdjustVolumeUserRestriction = getWorker().hasAdjustVolumeUserRestriction();
        Collection<MediaDevice> arrayList = new ArrayList<>();
        if (z) {
            arrayList = getWorker().getDeselectableMediaDevice();
        }
        for (MediaDevice mediaDevice : list) {
            int maxVolume = mediaDevice.getMaxVolume();
            IconCompat createIconWithDrawable = Utils.createIconWithDrawable(mediaDevice.getIcon());
            String name = mediaDevice.getName();
            SliceAction createDeeplink = SliceAction.createDeeplink(getBroadcastIntent(null, 0, 0), getDisabledCheckboxIcon(), 0, "");
            SliceAction createToggle = SliceAction.createToggle(getBroadcastIntent(mediaDevice.getId(), mediaDevice.hashCode() + 2, 2), IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_check_box_anim), "", z);
            if (maxVolume <= 0 || !z || hasAdjustVolumeUserRestriction) {
                ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
                rowBuilder.setTitleItem(createIconWithDrawable, 0);
                rowBuilder.setTitle(name);
                if (!z || (getWorker().isDeviceIncluded(arrayList, mediaDevice) && list.size() != 1)) {
                    rowBuilder.addEndItem(createToggle);
                } else {
                    rowBuilder.addEndItem(createDeeplink);
                }
                listBuilder.addRow(rowBuilder);
            } else {
                ListBuilder.InputRangeBuilder inputRangeBuilder = new ListBuilder.InputRangeBuilder();
                inputRangeBuilder.setTitleItem(createIconWithDrawable, 0);
                inputRangeBuilder.setTitle(name);
                inputRangeBuilder.setInputAction(getBroadcastIntent(mediaDevice.getId(), mediaDevice.hashCode() + 1, 1));
                inputRangeBuilder.setMax(mediaDevice.getMaxVolume());
                inputRangeBuilder.setValue(mediaDevice.getCurrentVolume());
                if (!z || (getWorker().isDeviceIncluded(arrayList, mediaDevice) && list.size() != 1)) {
                    inputRangeBuilder.addEndItem(createToggle);
                } else {
                    inputRangeBuilder.addEndItem(createDeeplink);
                }
                listBuilder.addInputRange(inputRangeBuilder);
            }
        }
    }

    private IconCompat getDisabledCheckboxIcon() {
        Drawable mutate = this.mContext.getDrawable(C0008R$drawable.ic_check_box_blue_24dp).mutate();
        Bitmap createBitmap = Bitmap.createBitmap(mutate.getIntrinsicWidth(), mutate.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        mutate.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        mutate.setAlpha(76);
        mutate.draw(canvas);
        return IconCompat.createWithBitmap(createBitmap);
    }

    private PendingIntent getBroadcastIntent(String str, int i, int i2) {
        Intent intent = new Intent(getUri().toString());
        intent.setClass(this.mContext, SliceBroadcastReceiver.class);
        intent.putExtra(MEDIA_DEVICE_ID, str);
        intent.putExtra(CUSTOMIZED_ACTION, i2);
        intent.addFlags(268435456);
        return PendingIntent.getBroadcast(this.mContext, i, intent, 134217728);
    }

    private MediaDeviceUpdateWorker getWorker() {
        if (this.mWorker == null) {
            this.mWorker = (MediaDeviceUpdateWorker) SliceBackgroundWorker.getInstance(getUri());
        }
        return this.mWorker;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.MEDIA_OUTPUT_GROUP_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        String stringExtra = intent.getStringExtra(MEDIA_DEVICE_ID);
        if (TextUtils.isEmpty(stringExtra)) {
            Log.e("MediaOutputGroupSlice", "Unable to handle notification. The device is unavailable");
            return;
        }
        MediaDeviceUpdateWorker worker = getWorker();
        MediaDevice mediaDeviceById = worker.getMediaDeviceById(stringExtra);
        int intExtra = intent.getIntExtra(CUSTOMIZED_ACTION, ERROR);
        if (intExtra == 1) {
            int intExtra2 = intent.getIntExtra("android.app.slice.extra.RANGE_VALUE", ERROR);
            if (intExtra2 == ERROR) {
                Log.e("MediaOutputGroupSlice", "Unable to adjust volume. The volume value is unavailable");
            } else if (TextUtils.equals(stringExtra, GROUP_DEVICES)) {
                worker.adjustSessionVolume(intExtra2);
            } else if (mediaDeviceById == null) {
                Log.e("MediaOutputGroupSlice", "Unable to adjust volume. The device(" + stringExtra + ") is unavailable");
            } else {
                worker.adjustVolume(mediaDeviceById, intExtra2);
            }
        } else if (intExtra == 2) {
            if (mediaDeviceById == null) {
                Log.e("MediaOutputGroupSlice", "Unable to adjust session volume. The device(" + stringExtra + ") is unavailable");
            } else if (worker.isDeviceIncluded(worker.getSelectableMediaDevice(), mediaDeviceById)) {
                worker.addDeviceToPlayMedia(mediaDeviceById);
            } else if (worker.isDeviceIncluded(worker.getDeselectableMediaDevice(), mediaDeviceById)) {
                worker.removeDeviceFromPlayMedia(mediaDeviceById);
            } else {
                Log.d("MediaOutputGroupSlice", mediaDeviceById.getName() + " is not selectable nor deselectable");
            }
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return MediaDeviceUpdateWorker.class;
    }
}
