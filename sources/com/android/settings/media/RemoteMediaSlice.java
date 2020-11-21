package com.android.settings.media;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RoutingSessionInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settings.notification.SoundSettings;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settings.slices.SliceBuilderUtils;
import java.util.List;

public class RemoteMediaSlice implements CustomSliceable {
    private final Context mContext;
    private MediaDeviceUpdateWorker mWorker;

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return null;
    }

    public RemoteMediaSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        int intExtra = intent.getIntExtra("android.app.slice.extra.RANGE_VALUE", -1);
        String stringExtra = intent.getStringExtra("media_id");
        if (!TextUtils.isEmpty(stringExtra)) {
            getWorker().adjustSessionVolume(stringExtra, intExtra);
        }
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        ListBuilder listBuilder = new ListBuilder(this.mContext, getUri(), -1);
        listBuilder.setAccentColor(-1);
        if (getWorker() == null) {
            Log.e("RemoteMediaSlice", "Unable to get the slice worker.");
            return listBuilder.build();
        }
        List<RoutingSessionInfo> activeRemoteMediaDevice = getWorker().getActiveRemoteMediaDevice();
        if (activeRemoteMediaDevice.isEmpty()) {
            Log.d("RemoteMediaSlice", "No active remote media device");
            return listBuilder.build();
        }
        CharSequence text = this.mContext.getText(C0017R$string.remote_media_volume_option_title);
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_volume_remote);
        IconCompat createEmptyIcon = createEmptyIcon();
        int i = 0;
        for (RoutingSessionInfo routingSessionInfo : activeRemoteMediaDevice) {
            int volumeMax = routingSessionInfo.getVolumeMax();
            if (volumeMax <= 0) {
                Log.d("RemoteMediaSlice", "Unable to add Slice. " + ((Object) routingSessionInfo.getName()) + ": max volume is " + volumeMax);
            } else {
                Context context = this.mContext;
                String string = context.getString(C0017R$string.media_output_label_title, Utils.getApplicationLabel(context, routingSessionInfo.getClientPackageName()));
                ListBuilder.InputRangeBuilder inputRangeBuilder = new ListBuilder.InputRangeBuilder();
                inputRangeBuilder.setTitleItem(createWithResource, 0);
                inputRangeBuilder.setTitle(text);
                int i2 = i + 1;
                inputRangeBuilder.setInputAction(getSliderInputAction(i, routingSessionInfo.getId()));
                inputRangeBuilder.setPrimaryAction(getSoundSettingAction(text, createWithResource, routingSessionInfo.getId()));
                inputRangeBuilder.setMax(volumeMax);
                inputRangeBuilder.setValue(routingSessionInfo.getVolume());
                listBuilder.addInputRange(inputRangeBuilder);
                ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
                rowBuilder.setTitle(string);
                rowBuilder.setSubtitle(routingSessionInfo.getName());
                rowBuilder.setTitleItem(createEmptyIcon, 0);
                rowBuilder.setPrimaryAction(getMediaOutputSliceAction(routingSessionInfo.getClientPackageName()));
                listBuilder.addRow(rowBuilder);
                i = i2;
            }
        }
        return listBuilder.build();
    }

    private IconCompat createEmptyIcon() {
        return IconCompat.createWithBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
    }

    private PendingIntent getSliderInputAction(int i, String str) {
        return PendingIntent.getBroadcast(this.mContext, i, new Intent(getUri().toString()).setData(getUri()).putExtra("media_id", str).setClass(this.mContext, SliceBroadcastReceiver.class), 0);
    }

    private SliceAction getSoundSettingAction(CharSequence charSequence, IconCompat iconCompat, String str) {
        Uri build = new Uri.Builder().appendPath(str).build();
        Intent buildSearchResultPageIntent = SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, SoundSettings.class.getName(), str, this.mContext.getText(C0017R$string.sound_settings).toString(), 0);
        buildSearchResultPageIntent.setClassName(this.mContext.getPackageName(), SubSettings.class.getName());
        buildSearchResultPageIntent.setData(build);
        return SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, 0, buildSearchResultPageIntent, 0), iconCompat, 0, charSequence);
    }

    private SliceAction getMediaOutputSliceAction(String str) {
        Intent putExtra = new Intent().setAction("com.android.settings.panel.action.MEDIA_OUTPUT").addFlags(268435456).putExtra("com.android.settings.panel.extra.PACKAGE_NAME", str);
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_volume_remote);
        PendingIntent activity = PendingIntent.getActivity(this.mContext, 0, putExtra, 0);
        Context context = this.mContext;
        return SliceAction.createDeeplink(activity, createWithResource, 0, context.getString(C0017R$string.media_output_label_title, Utils.getApplicationLabel(context, str)));
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.REMOTE_MEDIA_SLICE_URI;
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return MediaDeviceUpdateWorker.class;
    }

    private MediaDeviceUpdateWorker getWorker() {
        if (this.mWorker == null) {
            this.mWorker = (MediaDeviceUpdateWorker) SliceBackgroundWorker.getInstance(getUri());
        }
        return this.mWorker;
    }
}
