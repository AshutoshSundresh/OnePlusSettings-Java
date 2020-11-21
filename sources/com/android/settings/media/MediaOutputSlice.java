package com.android.settings.media;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.bluetooth.BluetoothPairingDetail;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settingslib.Utils;
import com.android.settingslib.media.MediaDevice;
import java.util.Collection;

public class MediaOutputSlice implements CustomSliceable {
    private final Context mContext;
    private MediaDeviceUpdateWorker mWorker;

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return null;
    }

    public MediaOutputSlice(Context context) {
        this.mContext = context;
    }

    /* access modifiers changed from: package-private */
    public void init(MediaDeviceUpdateWorker mediaDeviceUpdateWorker) {
        this.mWorker = mediaDeviceUpdateWorker;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        ListBuilder listBuilder = new ListBuilder(this.mContext, getUri(), -1);
        listBuilder.setAccentColor(-1);
        if (!isVisible()) {
            Log.d("MediaOutputSlice", "getSlice() is not visible");
            return listBuilder.build();
        }
        Collection<MediaDevice> mediaDevices = getMediaDevices();
        MediaDeviceUpdateWorker worker = getWorker();
        if (worker.getSelectedMediaDevice().size() > 1) {
            if (worker.getSessionVolumeMax() <= 0 || worker.hasAdjustVolumeUserRestriction()) {
                listBuilder.addRow(getGroupRow());
            } else {
                listBuilder.addInputRange(getGroupSliderRow());
            }
            for (MediaDevice mediaDevice : mediaDevices) {
                addRow(mediaDevice, null, listBuilder);
            }
        } else {
            MediaDevice currentConnectedMediaDevice = worker.getCurrentConnectedMediaDevice();
            if (mediaDevices.size() == 1) {
                for (MediaDevice mediaDevice2 : mediaDevices) {
                    addRow(mediaDevice2, mediaDevice2, listBuilder);
                }
                listBuilder.addRow(getPairNewRow());
            } else {
                MediaDevice topDevice = worker.getIsTouched() ? worker.getTopDevice() : currentConnectedMediaDevice;
                if (topDevice != null) {
                    addRow(topDevice, currentConnectedMediaDevice, listBuilder);
                    worker.setTopDevice(topDevice);
                }
                for (MediaDevice mediaDevice3 : mediaDevices) {
                    if (topDevice == null || !TextUtils.equals(topDevice.getId(), mediaDevice3.getId())) {
                        addRow(mediaDevice3, currentConnectedMediaDevice, listBuilder);
                    }
                }
            }
        }
        return listBuilder.build();
    }

    private ListBuilder.RowBuilder getPairNewRow() {
        Drawable drawable = this.mContext.getDrawable(C0008R$drawable.ic_add_24dp);
        drawable.setColorFilter(new PorterDuffColorFilter(Utils.getColorAccentDefaultColor(this.mContext), PorterDuff.Mode.SRC_IN));
        IconCompat createIconWithDrawable = com.android.settings.Utils.createIconWithDrawable(drawable);
        String string = this.mContext.getString(C0017R$string.bluetooth_pairing_pref_title);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(BluetoothPairingDetail.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.bluetooth_pairing_page_title);
        subSettingLauncher.setSourceMetricsCategory(1657);
        SliceAction createDeeplink = SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, 0, subSettingLauncher.toIntent(), 0), IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_add_24dp), 0, this.mContext.getText(C0017R$string.bluetooth_pairing_pref_title));
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitleItem(createIconWithDrawable, 0);
        rowBuilder.setTitle(string);
        rowBuilder.setPrimaryAction(createDeeplink);
        return rowBuilder;
    }

    private ListBuilder.InputRangeBuilder getGroupSliderRow() {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_speaker_group_black_24dp);
        CharSequence sessionName = getWorker().getSessionName();
        if (TextUtils.isEmpty(sessionName)) {
            sessionName = this.mContext.getString(C0017R$string.media_output_group);
        }
        SliceAction createDeeplink = SliceAction.createDeeplink(getBroadcastIntent(this.mContext, "media_group_device", 1855100049), createWithResource, 0, sessionName);
        ListBuilder.InputRangeBuilder inputRangeBuilder = new ListBuilder.InputRangeBuilder();
        inputRangeBuilder.setTitleItem(createWithResource, 0);
        inputRangeBuilder.setTitle(sessionName);
        inputRangeBuilder.setPrimaryAction(createDeeplink);
        inputRangeBuilder.setInputAction(getSliderInputAction(1855100049, "media_group_device"));
        inputRangeBuilder.setMax(getWorker().getSessionVolumeMax());
        inputRangeBuilder.setValue(getWorker().getSessionVolume());
        inputRangeBuilder.addEndItem(getEndItemSliceAction());
        return inputRangeBuilder;
    }

    private ListBuilder.RowBuilder getGroupRow() {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_speaker_group_black_24dp);
        CharSequence sessionName = getWorker().getSessionName();
        if (TextUtils.isEmpty(sessionName)) {
            sessionName = this.mContext.getString(C0017R$string.media_output_group);
        }
        SliceAction createDeeplink = SliceAction.createDeeplink(getBroadcastIntent(this.mContext, "media_group_device", 1855100049), createWithResource, 0, sessionName);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitleItem(createWithResource, 0);
        rowBuilder.setTitle(sessionName);
        rowBuilder.setPrimaryAction(createDeeplink);
        rowBuilder.addEndItem(getEndItemSliceAction());
        return rowBuilder;
    }

    private void addRow(MediaDevice mediaDevice, MediaDevice mediaDevice2, ListBuilder listBuilder) {
        if (mediaDevice2 != null && TextUtils.equals(mediaDevice.getId(), mediaDevice2.getId())) {
            String name = mediaDevice.getName();
            IconCompat deviceIconCompat = getDeviceIconCompat(mediaDevice);
            SliceAction createDeeplink = SliceAction.createDeeplink(getBroadcastIntent(this.mContext, mediaDevice.getId(), mediaDevice.hashCode()), deviceIconCompat, 0, name);
            if (mediaDevice.getMaxVolume() <= 0 || getWorker().hasAdjustVolumeUserRestriction()) {
                Log.d("MediaOutputSlice", "addRow device = " + mediaDevice.getName() + " MaxVolume = " + mediaDevice.getMaxVolume());
                ListBuilder.RowBuilder mediaDeviceRow = getMediaDeviceRow(mediaDevice);
                if (mediaDevice.getDeviceType() == 5 && !getWorker().getSelectableMediaDevice().isEmpty()) {
                    mediaDeviceRow.addEndItem(getEndItemSliceAction());
                }
                listBuilder.addRow(mediaDeviceRow);
                return;
            }
            ListBuilder.InputRangeBuilder inputRangeBuilder = new ListBuilder.InputRangeBuilder();
            inputRangeBuilder.setTitleItem(deviceIconCompat, 0);
            inputRangeBuilder.setTitle(name);
            inputRangeBuilder.setPrimaryAction(createDeeplink);
            inputRangeBuilder.setInputAction(getSliderInputAction(mediaDevice.hashCode(), mediaDevice.getId()));
            inputRangeBuilder.setMax(mediaDevice.getMaxVolume());
            inputRangeBuilder.setValue(mediaDevice.getCurrentVolume());
            if (mediaDevice.getDeviceType() == 5 && !getWorker().getSelectableMediaDevice().isEmpty()) {
                inputRangeBuilder.addEndItem(getEndItemSliceAction());
            }
            listBuilder.addInputRange(inputRangeBuilder);
        } else if (mediaDevice.getState() == 1) {
            listBuilder.addRange(getTransferringMediaDeviceRow(mediaDevice));
        } else {
            listBuilder.addRow(getMediaDeviceRow(mediaDevice));
        }
    }

    private PendingIntent getSliderInputAction(int i, String str) {
        return PendingIntent.getBroadcast(this.mContext, i, new Intent(getUri().toString()).setData(getUri()).putExtra("media_device_id", str).setClass(this.mContext, SliceBroadcastReceiver.class), 0);
    }

    private SliceAction getEndItemSliceAction() {
        return SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, 0, new Intent().setAction("com.android.settings.panel.action.MEDIA_OUTPUT_GROUP").addFlags(268435456).putExtra("com.android.settings.panel.extra.PACKAGE_NAME", getWorker().getPackageName()), 0), IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_add_blue_24dp), 0, this.mContext.getText(C0017R$string.add));
    }

    private IconCompat getDeviceIconCompat(MediaDevice mediaDevice) {
        Drawable icon = mediaDevice.getIcon();
        if (icon == null) {
            Log.d("MediaOutputSlice", "getDeviceIconCompat() device : " + mediaDevice.getName() + ", drawable is null");
            icon = this.mContext.getDrawable(17302323);
        }
        return com.android.settings.Utils.createIconWithDrawable(icon);
    }

    private MediaDeviceUpdateWorker getWorker() {
        if (this.mWorker == null) {
            this.mWorker = (MediaDeviceUpdateWorker) SliceBackgroundWorker.getInstance(getUri());
        }
        return this.mWorker;
    }

    private Collection<MediaDevice> getMediaDevices() {
        return getWorker().getMediaDevices();
    }

    private ListBuilder.RangeBuilder getTransferringMediaDeviceRow(MediaDevice mediaDevice) {
        IconCompat deviceIconCompat = getDeviceIconCompat(mediaDevice);
        SliceAction create = SliceAction.create(getBroadcastIntent(this.mContext, mediaDevice.getId(), mediaDevice.hashCode()), deviceIconCompat, 0, this.mContext.getText(C0017R$string.media_output_switching));
        ListBuilder.RangeBuilder rangeBuilder = new ListBuilder.RangeBuilder();
        rangeBuilder.setTitleItem(deviceIconCompat, 0);
        rangeBuilder.setMode(1);
        rangeBuilder.setTitle(mediaDevice.getName());
        rangeBuilder.setPrimaryAction(create);
        return rangeBuilder;
    }

    private ListBuilder.RowBuilder getMediaDeviceRow(MediaDevice mediaDevice) {
        String name = mediaDevice.getName();
        PendingIntent broadcastIntent = getBroadcastIntent(this.mContext, mediaDevice.getId(), mediaDevice.hashCode());
        IconCompat deviceIconCompat = getDeviceIconCompat(mediaDevice);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitleItem(deviceIconCompat, 0);
        if (mediaDevice.getDeviceType() != 4 || mediaDevice.isConnected()) {
            rowBuilder.setTitle(name);
            rowBuilder.setPrimaryAction(SliceAction.create(broadcastIntent, deviceIconCompat, 0, name));
            if (mediaDevice.getState() == 3) {
                rowBuilder.setSubtitle(this.mContext.getText(C0017R$string.media_output_switch_error_text));
            }
        } else if (mediaDevice.getState() == 3) {
            rowBuilder.setTitle(name);
            rowBuilder.setPrimaryAction(SliceAction.create(broadcastIntent, deviceIconCompat, 0, name));
            rowBuilder.setSubtitle(this.mContext.getText(C0017R$string.bluetooth_connect_failed));
        } else {
            SpannableString spannableString = new SpannableString(this.mContext.getString(C0017R$string.media_output_disconnected_status, name));
            spannableString.setSpan(new ForegroundColorSpan(Utils.getColorAttrDefaultColor(this.mContext, 16842808)), name.length(), spannableString.length(), 33);
            rowBuilder.setTitle(spannableString);
            rowBuilder.setPrimaryAction(SliceAction.create(broadcastIntent, deviceIconCompat, 0, spannableString));
        }
        return rowBuilder;
    }

    private PendingIntent getBroadcastIntent(Context context, String str, int i) {
        Intent intent = new Intent(getUri().toString());
        intent.setClass(context, SliceBroadcastReceiver.class);
        intent.putExtra("media_device_id", str);
        intent.addFlags(268435456);
        return PendingIntent.getBroadcast(context, i, intent, 134217728);
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.MEDIA_OUTPUT_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        MediaDeviceUpdateWorker worker = getWorker();
        String stringExtra = intent != null ? intent.getStringExtra("media_device_id") : "";
        if (!TextUtils.isEmpty(stringExtra)) {
            int intExtra = intent.getIntExtra("android.app.slice.extra.RANGE_VALUE", -1);
            if (TextUtils.equals(stringExtra, "media_group_device")) {
                worker.adjustSessionVolume(intExtra);
                return;
            }
            MediaDevice mediaDeviceById = worker.getMediaDeviceById(stringExtra);
            if (mediaDeviceById == null) {
                Log.d("MediaOutputSlice", "onNotifyChange: Unable to get device " + stringExtra);
            } else if (intExtra == -1) {
                Log.d("MediaOutputSlice", "onNotifyChange: Switch to " + mediaDeviceById.getName());
                worker.setIsTouched(true);
                worker.connectDevice(mediaDeviceById);
            } else {
                worker.adjustVolume(mediaDeviceById, intExtra);
            }
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return MediaOutputSliceWorker.class;
    }

    private boolean isVisible() {
        return getWorker() != null && !Utils.isAudioModeOngoingCall(this.mContext) && getWorker().getMediaDevices().size() > 0;
    }
}
