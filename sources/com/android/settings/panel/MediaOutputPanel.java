package com.android.settings.panel;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settingslib.Utils;
import com.android.settingslib.media.InfoMediaDevice;
import com.android.settingslib.media.LocalMediaManager;
import com.android.settingslib.media.MediaDevice;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaOutputPanel implements PanelContent, LocalMediaManager.DeviceCallback, LifecycleObserver {
    private PanelContentCallback mCallback;
    private final MediaController.Callback mCb = new MediaController.Callback() {
        /* class com.android.settings.panel.MediaOutputPanel.AnonymousClass1 */

        public void onMetadataChanged(MediaMetadata mediaMetadata) {
            if (MediaOutputPanel.this.mCallback != null) {
                MediaOutputPanel.this.mCallback.onHeaderChanged();
            }
        }

        public void onPlaybackStateChanged(PlaybackState playbackState) {
            int state = playbackState.getState();
            if (MediaOutputPanel.this.mCallback == null) {
                return;
            }
            if (state == 1 || state == 2) {
                MediaOutputPanel.this.mCallback.forceClose();
            }
        }
    };
    private final Context mContext;
    private boolean mIsCustomizedButtonUsed = true;
    @VisibleForTesting
    LocalMediaManager mLocalMediaManager;
    private MediaController mMediaController;
    private MediaSessionManager mMediaSessionManager;
    private final String mPackageName;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1657;
    }

    @Override // com.android.settings.panel.PanelContent
    public Intent getSeeMoreIntent() {
        return null;
    }

    @Override // com.android.settings.panel.PanelContent
    public int getViewType() {
        return 2;
    }

    public static MediaOutputPanel create(Context context, String str) {
        return new MediaOutputPanel(context, str);
    }

    private MediaOutputPanel(Context context, String str) {
        this.mContext = context.getApplicationContext();
        this.mPackageName = TextUtils.isEmpty(str) ? "" : str;
    }

    @Override // com.android.settings.panel.PanelContent
    public CharSequence getTitle() {
        MediaMetadata metadata;
        MediaController mediaController = this.mMediaController;
        if (mediaController == null || (metadata = mediaController.getMetadata()) == null) {
            return this.mContext.getText(C0017R$string.media_volume_title);
        }
        return metadata.getDescription().getTitle();
    }

    @Override // com.android.settings.panel.PanelContent
    public CharSequence getSubTitle() {
        MediaMetadata metadata;
        MediaController mediaController = this.mMediaController;
        if (mediaController == null || (metadata = mediaController.getMetadata()) == null) {
            return this.mContext.getText(C0017R$string.media_output_panel_title);
        }
        return metadata.getDescription().getSubtitle();
    }

    @Override // com.android.settings.panel.PanelContent
    public IconCompat getIcon() {
        Bitmap iconBitmap;
        MediaController mediaController = this.mMediaController;
        if (mediaController == null) {
            IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_media_stream);
            createWithResource.setTint(Utils.getColorAccentDefaultColor(this.mContext));
            return createWithResource;
        }
        MediaMetadata metadata = mediaController.getMetadata();
        if (metadata != null && (iconBitmap = metadata.getDescription().getIconBitmap()) != null) {
            return IconCompat.createWithBitmap(iconBitmap);
        }
        Log.d("MediaOutputPanel", "Media meta data does not contain icon information");
        return getPackageIcon();
    }

    private IconCompat getPackageIcon() {
        try {
            Drawable applicationIcon = this.mContext.getPackageManager().getApplicationIcon(this.mPackageName);
            if (applicationIcon instanceof BitmapDrawable) {
                return IconCompat.createWithBitmap(((BitmapDrawable) applicationIcon).getBitmap());
            }
            Bitmap createBitmap = Bitmap.createBitmap(applicationIcon.getIntrinsicWidth(), applicationIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            applicationIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            applicationIcon.draw(canvas);
            return IconCompat.createWithBitmap(createBitmap);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("MediaOutputPanel", "Package is not found. Unable to get package icon.");
            return null;
        }
    }

    @Override // com.android.settings.panel.PanelContent
    public List<Uri> getSlices() {
        ArrayList arrayList = new ArrayList();
        Uri build = CustomSliceRegistry.MEDIA_OUTPUT_SLICE_URI.buildUpon().clearQuery().appendQueryParameter("media_package_name", this.mPackageName).build();
        CustomSliceRegistry.MEDIA_OUTPUT_SLICE_URI = build;
        arrayList.add(build);
        return arrayList;
    }

    @Override // com.android.settings.panel.PanelContent
    public boolean isCustomizedButtonUsed() {
        return this.mIsCustomizedButtonUsed;
    }

    @Override // com.android.settings.panel.PanelContent
    public CharSequence getCustomizedButtonTitle() {
        return this.mContext.getText(C0017R$string.service_stop);
    }

    @Override // com.android.settings.panel.PanelContent
    public void onClickCustomizedButton() {
        this.mLocalMediaManager.releaseSession();
    }

    @Override // com.android.settings.panel.PanelContent
    public void registerCallback(PanelContentCallback panelContentCallback) {
        this.mCallback = panelContentCallback;
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
        dispatchCustomButtonStateChanged();
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onDeviceListUpdate(List<MediaDevice> list) {
        dispatchCustomButtonStateChanged();
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onDeviceAttributesChanged() {
        dispatchCustomButtonStateChanged();
    }

    private void dispatchCustomButtonStateChanged() {
        hideCustomButtonIfNecessary();
        PanelContentCallback panelContentCallback = this.mCallback;
        if (panelContentCallback != null) {
            panelContentCallback.onCustomizedButtonStateChanged();
        }
    }

    private void hideCustomButtonIfNecessary() {
        this.mIsCustomizedButtonUsed = this.mLocalMediaManager.getCurrentConnectedDevice() instanceof InfoMediaDevice;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (!TextUtils.isEmpty(this.mPackageName)) {
            MediaSessionManager mediaSessionManager = (MediaSessionManager) this.mContext.getSystemService(MediaSessionManager.class);
            this.mMediaSessionManager = mediaSessionManager;
            Iterator<MediaController> it = mediaSessionManager.getActiveSessions(null).iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                MediaController next = it.next();
                if (TextUtils.equals(next.getPackageName(), this.mPackageName)) {
                    this.mMediaController = next;
                    next.registerCallback(this.mCb);
                    this.mCallback.onHeaderChanged();
                    break;
                }
            }
        }
        if (this.mMediaController == null) {
            Log.d("MediaOutputPanel", "No media controller for " + this.mPackageName);
        }
        if (this.mLocalMediaManager == null) {
            this.mLocalMediaManager = new LocalMediaManager(this.mContext, this.mPackageName, null);
        }
        this.mLocalMediaManager.registerCallback(this);
        this.mLocalMediaManager.startScan();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        MediaController mediaController = this.mMediaController;
        if (mediaController != null) {
            mediaController.unregisterCallback(this.mCb);
        }
        this.mLocalMediaManager.unregisterCallback(this);
        this.mLocalMediaManager.stopScan();
    }
}
