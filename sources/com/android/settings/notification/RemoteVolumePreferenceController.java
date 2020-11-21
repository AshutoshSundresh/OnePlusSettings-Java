package com.android.settings.notification;

import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0008R$drawable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.volume.MediaSessions;
import java.io.IOException;
import java.util.Objects;

public class RemoteVolumePreferenceController extends VolumeSeekBarPreferenceController {
    private static final String KEY_REMOTE_VOLUME = "remote_volume";
    static final int REMOTE_VOLUME = 100;
    private static final String TAG = "RemoteVolumePrefCtr";
    MediaSession.Token mActiveToken;
    MediaSessions.Callbacks mCallbacks = new MediaSessions.Callbacks() {
        /* class com.android.settings.notification.RemoteVolumePreferenceController.AnonymousClass1 */

        @Override // com.android.settingslib.volume.MediaSessions.Callbacks
        public void onRemoteUpdate(MediaSession.Token token, String str, MediaController.PlaybackInfo playbackInfo) {
            RemoteVolumePreferenceController remoteVolumePreferenceController = RemoteVolumePreferenceController.this;
            if (remoteVolumePreferenceController.mActiveToken == null) {
                remoteVolumePreferenceController.updateToken(token);
            }
            if (Objects.equals(RemoteVolumePreferenceController.this.mActiveToken, token)) {
                RemoteVolumePreferenceController remoteVolumePreferenceController2 = RemoteVolumePreferenceController.this;
                remoteVolumePreferenceController2.updatePreference(remoteVolumePreferenceController2.mPreference, remoteVolumePreferenceController2.mActiveToken, playbackInfo);
            }
        }

        @Override // com.android.settingslib.volume.MediaSessions.Callbacks
        public void onRemoteRemoved(MediaSession.Token token) {
            if (Objects.equals(RemoteVolumePreferenceController.this.mActiveToken, token)) {
                RemoteVolumePreferenceController.this.updateToken(null);
                VolumeSeekBarPreference volumeSeekBarPreference = RemoteVolumePreferenceController.this.mPreference;
                if (volumeSeekBarPreference != null) {
                    volumeSeekBarPreference.setVisible(false);
                }
            }
        }

        @Override // com.android.settingslib.volume.MediaSessions.Callbacks
        public void onRemoteVolumeChanged(MediaSession.Token token, int i) {
            if (Objects.equals(RemoteVolumePreferenceController.this.mActiveToken, token)) {
                RemoteVolumePreferenceController remoteVolumePreferenceController = RemoteVolumePreferenceController.this;
                if (remoteVolumePreferenceController.mPreference == null) {
                    Log.e(RemoteVolumePreferenceController.TAG, "Preference is null");
                    return;
                }
                MediaController mediaController = remoteVolumePreferenceController.mMediaController;
                if (mediaController == null) {
                    Log.e(RemoteVolumePreferenceController.TAG, "MediaController is null");
                    return;
                }
                MediaController.PlaybackInfo playbackInfo = mediaController.getPlaybackInfo();
                if (playbackInfo == null) {
                    Log.e(RemoteVolumePreferenceController.TAG, "PlaybackInfo is null");
                } else {
                    RemoteVolumePreferenceController.this.mPreference.setProgress(playbackInfo.getCurrentVolume());
                }
            }
        }
    };
    MediaController mMediaController;
    private MediaSessions mMediaSessions;

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getAudioStream() {
        return 100;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_REMOTE_VOLUME;
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public boolean useDynamicSliceSummary() {
        return true;
    }

    public RemoteVolumePreferenceController(Context context) {
        super(context, KEY_REMOTE_VOLUME);
        this.mMediaSessions = new MediaSessions(context, Looper.getMainLooper(), this.mCallbacks);
        updateToken(getActiveRemoteToken(this.mContext));
    }

    public static MediaSession.Token getActiveRemoteToken(Context context) {
        for (MediaController mediaController : ((MediaSessionManager) context.getSystemService(MediaSessionManager.class)).getActiveSessions(null)) {
            if (isRemote(mediaController.getPlaybackInfo())) {
                return mediaController.getSessionToken();
            }
        }
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference.setVisible(this.mActiveToken != null);
        MediaController mediaController = this.mMediaController;
        if (mediaController != null) {
            updatePreference(this.mPreference, this.mActiveToken, mediaController.getPlaybackInfo());
        }
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        super.onResume();
        this.mMediaSessions.init();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        super.onPause();
        this.mMediaSessions.destroy();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getSliderPosition() {
        MediaController.PlaybackInfo playbackInfo;
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            return volumeSeekBarPreference.getProgress();
        }
        MediaController mediaController = this.mMediaController;
        if (mediaController == null || (playbackInfo = mediaController.getPlaybackInfo()) == null) {
            return 0;
        }
        return playbackInfo.getCurrentVolume();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController
    public boolean setSliderPosition(int i) {
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            volumeSeekBarPreference.setProgress(i);
        }
        MediaController mediaController = this.mMediaController;
        if (mediaController == null) {
            return false;
        }
        mediaController.setVolumeTo(i, 0);
        return true;
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getMax() {
        MediaController.PlaybackInfo playbackInfo;
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            return volumeSeekBarPreference.getMax();
        }
        MediaController mediaController = this.mMediaController;
        if (mediaController == null || (playbackInfo = mediaController.getPlaybackInfo()) == null) {
            return 0;
        }
        return playbackInfo.getMaxVolume();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getMin() {
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            return volumeSeekBarPreference.getMin();
        }
        return 0;
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), KEY_REMOTE_VOLUME);
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getMuteIcon() {
        return C0008R$drawable.ic_volume_remote_mute;
    }

    public static boolean isRemote(MediaController.PlaybackInfo playbackInfo) {
        return playbackInfo != null && playbackInfo.getPlaybackType() == 2;
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return RemoteVolumeSliceWorker.class;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updatePreference(VolumeSeekBarPreference volumeSeekBarPreference, MediaSession.Token token, MediaController.PlaybackInfo playbackInfo) {
        if (volumeSeekBarPreference != null && token != null && playbackInfo != null) {
            volumeSeekBarPreference.setMax(playbackInfo.getMaxVolume());
            volumeSeekBarPreference.setVisible(true);
            setSliderPosition(playbackInfo.getCurrentVolume());
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateToken(MediaSession.Token token) {
        this.mActiveToken = token;
        if (token != null) {
            this.mMediaController = new MediaController(this.mContext, this.mActiveToken);
        } else {
            this.mMediaController = null;
        }
    }

    public static class RemoteVolumeSliceWorker extends SliceBackgroundWorker<Void> implements MediaSessions.Callbacks {
        private MediaSessions mMediaSessions;

        public RemoteVolumeSliceWorker(Context context, Uri uri) {
            super(context, uri);
            this.mMediaSessions = new MediaSessions(context, Looper.getMainLooper(), this);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.slices.SliceBackgroundWorker
        public void onSlicePinned() {
            this.mMediaSessions.init();
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.slices.SliceBackgroundWorker
        public void onSliceUnpinned() {
            this.mMediaSessions.destroy();
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.mMediaSessions = null;
        }

        @Override // com.android.settingslib.volume.MediaSessions.Callbacks
        public void onRemoteUpdate(MediaSession.Token token, String str, MediaController.PlaybackInfo playbackInfo) {
            notifySliceChange();
        }

        @Override // com.android.settingslib.volume.MediaSessions.Callbacks
        public void onRemoteRemoved(MediaSession.Token token) {
            notifySliceChange();
        }

        @Override // com.android.settingslib.volume.MediaSessions.Callbacks
        public void onRemoteVolumeChanged(MediaSession.Token token, int i) {
            notifySliceChange();
        }
    }
}
