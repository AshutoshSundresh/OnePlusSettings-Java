package com.android.settings.media;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.session.MediaController;
import android.net.Uri;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.OPMemberController;

public class MediaOutputIndicatorSlice implements CustomSliceable {
    private Context mContext;
    private MediaOutputIndicatorWorker mWorker;

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return null;
    }

    public MediaOutputIndicatorSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        if (!isVisible()) {
            ListBuilder listBuilder = new ListBuilder(this.mContext, getUri(), -1);
            listBuilder.setIsError(true);
            return listBuilder.build();
        }
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, 17302820);
        Context context = this.mContext;
        String string = context.getString(C0017R$string.media_output_label_title, Utils.getApplicationLabel(context, getWorker().getPackageName()));
        SliceAction createDeeplink = SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, 0, getMediaOutputSliceIntent(), 134217728), createWithResource, 0, string);
        int colorAccentDefaultColor = com.android.settingslib.Utils.getColorAccentDefaultColor(this.mContext);
        ListBuilder listBuilder2 = new ListBuilder(this.mContext, getUri(), -1);
        listBuilder2.setAccentColor(colorAccentDefaultColor);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitle(string);
        rowBuilder.setTitleItem(createEmptyIcon(), 0);
        rowBuilder.setSubtitle(getWorker().getCurrentConnectedMediaDevice().getName());
        rowBuilder.setPrimaryAction(createDeeplink);
        listBuilder2.addRow(rowBuilder);
        return listBuilder2.build();
    }

    /* access modifiers changed from: package-private */
    public Intent getMediaOutputSliceIntent() {
        MediaController activeLocalMediaController = getWorker().getActiveLocalMediaController();
        Intent addFlags = new Intent().setPackage(OPMemberController.PACKAGE_NAME).setAction("com.android.settings.panel.action.MEDIA_OUTPUT").addFlags(268435456);
        if (activeLocalMediaController != null) {
            addFlags.putExtra("key_media_session_token", activeLocalMediaController.getSessionToken());
            addFlags.putExtra("com.android.settings.panel.extra.PACKAGE_NAME", activeLocalMediaController.getPackageName());
        }
        return addFlags;
    }

    private IconCompat createEmptyIcon() {
        return IconCompat.createWithBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.MEDIA_OUTPUT_INDICATOR_SLICE_URI;
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return MediaOutputIndicatorWorker.class;
    }

    private MediaOutputIndicatorWorker getWorker() {
        if (this.mWorker == null) {
            this.mWorker = (MediaOutputIndicatorWorker) SliceBackgroundWorker.getInstance(getUri());
        }
        return this.mWorker;
    }

    /* access modifiers changed from: package-private */
    public boolean isVisible() {
        return getWorker() != null && !com.android.settingslib.Utils.isAudioModeOngoingCall(this.mContext) && getWorker().getMediaDevices().size() > 0 && getWorker().getActiveLocalMediaController() != null;
    }
}
