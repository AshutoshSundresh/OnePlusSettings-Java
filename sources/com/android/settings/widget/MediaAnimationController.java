package com.android.settings.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import com.android.settings.widget.VideoPreference;

/* access modifiers changed from: package-private */
public class MediaAnimationController implements VideoPreference.AnimationController {
    private MediaPlayer mMediaPlayer;
    private Surface mSurface;
    private boolean mVideoReady;

    MediaAnimationController(Context context, int i) {
        MediaPlayer create = MediaPlayer.create(context, new Uri.Builder().scheme("android.resource").authority(context.getPackageName()).appendPath(String.valueOf(i)).build());
        this.mMediaPlayer = create;
        if (create != null) {
            create.seekTo(0);
            this.mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                /* class com.android.settings.widget.$$Lambda$MediaAnimationController$xif5b9FVp6MF0NgttTyEa9Fkqs */

                public final void onSeekComplete(MediaPlayer mediaPlayer) {
                    MediaAnimationController.this.lambda$new$0$MediaAnimationController(mediaPlayer);
                }
            });
            this.mMediaPlayer.setOnPreparedListener($$Lambda$MediaAnimationController$60QwoqjGmisNwYSwY_DAxBhwI8.INSTANCE);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$MediaAnimationController(MediaPlayer mediaPlayer) {
        this.mVideoReady = true;
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getVideoWidth() {
        return this.mMediaPlayer.getVideoWidth();
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getVideoHeight() {
        return this.mMediaPlayer.getVideoHeight();
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getDuration() {
        return this.mMediaPlayer.getDuration();
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public void attachView(TextureView textureView, final View view, final View view2) {
        updateViewStates(view, view2);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            /* class com.android.settings.widget.MediaAnimationController.AnonymousClass1 */

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                MediaAnimationController.this.setSurface(surfaceTexture);
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                view.setVisibility(0);
                MediaAnimationController.this.mSurface = null;
                return false;
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                MediaAnimationController.this.setSurface(surfaceTexture);
                if (MediaAnimationController.this.mVideoReady) {
                    if (view.getVisibility() == 0) {
                        view.setVisibility(8);
                    }
                    if (MediaAnimationController.this.mMediaPlayer != null && !MediaAnimationController.this.mMediaPlayer.isPlaying()) {
                        MediaAnimationController.this.mMediaPlayer.start();
                        view2.setVisibility(8);
                    }
                }
                if (MediaAnimationController.this.mMediaPlayer != null && !MediaAnimationController.this.mMediaPlayer.isPlaying() && view2.getVisibility() != 0) {
                    view2.setVisibility(0);
                }
            }
        });
        textureView.setOnClickListener(new View.OnClickListener(view, view2) {
            /* class com.android.settings.widget.$$Lambda$MediaAnimationController$4H8SVtsELnxw3RvZIDvVYXTWt6M */
            public final /* synthetic */ View f$1;
            public final /* synthetic */ View f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(View view) {
                MediaAnimationController.this.lambda$attachView$2$MediaAnimationController(this.f$1, this.f$2, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$attachView$2 */
    public /* synthetic */ void lambda$attachView$2$MediaAnimationController(View view, View view2, View view3) {
        updateViewStates(view, view2);
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public void release() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mVideoReady = false;
        }
    }

    private void updateViewStates(View view, View view2) {
        if (this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
            view2.setVisibility(0);
            view.setVisibility(0);
            return;
        }
        view.setVisibility(8);
        view2.setVisibility(8);
        this.mMediaPlayer.start();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setSurface(SurfaceTexture surfaceTexture) {
        if (this.mMediaPlayer != null && this.mSurface == null) {
            Surface surface = new Surface(surfaceTexture);
            this.mSurface = surface;
            this.mMediaPlayer.setSurface(surface);
        }
    }
}
