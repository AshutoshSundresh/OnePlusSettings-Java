package com.android.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import com.android.settings.C0016R$raw;

public class FingerprintLocationAnimationVideoView extends TextureView implements FingerprintFindSensorAnimation {
    protected float mAspect = 1.0f;
    protected MediaPlayer mMediaPlayer;

    public FingerprintLocationAnimationVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.round(this.mAspect * ((float) View.MeasureSpec.getSize(i))), 1073741824));
    }

    /* access modifiers changed from: protected */
    public Uri getFingerprintLocationAnimation() {
        return resourceEntryToUri(getContext(), C0016R$raw.fingerprint_location_animation);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationVideoView.AnonymousClass1 */
            private SurfaceTexture mTextureToDestroy = null;

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                FingerprintLocationAnimationVideoView.this.setVisibility(4);
                Uri fingerprintLocationAnimation = FingerprintLocationAnimationVideoView.this.getFingerprintLocationAnimation();
                MediaPlayer mediaPlayer = FingerprintLocationAnimationVideoView.this.mMediaPlayer;
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                SurfaceTexture surfaceTexture2 = this.mTextureToDestroy;
                if (surfaceTexture2 != null) {
                    surfaceTexture2.release();
                    this.mTextureToDestroy = null;
                }
                FingerprintLocationAnimationVideoView fingerprintLocationAnimationVideoView = FingerprintLocationAnimationVideoView.this;
                fingerprintLocationAnimationVideoView.mMediaPlayer = fingerprintLocationAnimationVideoView.createMediaPlayer(((TextureView) fingerprintLocationAnimationVideoView).mContext, fingerprintLocationAnimation);
                MediaPlayer mediaPlayer2 = FingerprintLocationAnimationVideoView.this.mMediaPlayer;
                if (mediaPlayer2 != null) {
                    mediaPlayer2.setSurface(new Surface(surfaceTexture));
                    FingerprintLocationAnimationVideoView.this.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(this) {
                        /* class com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationVideoView.AnonymousClass1.AnonymousClass1 */

                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.setLooping(true);
                        }
                    });
                    FingerprintLocationAnimationVideoView.this.mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        /* class com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationVideoView.AnonymousClass1.AnonymousClass2 */

                        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
                            if (i == 3) {
                                FingerprintLocationAnimationVideoView.this.setVisibility(0);
                            }
                            return false;
                        }
                    });
                    FingerprintLocationAnimationVideoView fingerprintLocationAnimationVideoView2 = FingerprintLocationAnimationVideoView.this;
                    fingerprintLocationAnimationVideoView2.mAspect = ((float) fingerprintLocationAnimationVideoView2.mMediaPlayer.getVideoHeight()) / ((float) FingerprintLocationAnimationVideoView.this.mMediaPlayer.getVideoWidth());
                    FingerprintLocationAnimationVideoView.this.requestLayout();
                    FingerprintLocationAnimationVideoView.this.startAnimation();
                }
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                this.mTextureToDestroy = surfaceTexture;
                return false;
            }
        });
    }

    /* access modifiers changed from: package-private */
    public MediaPlayer createMediaPlayer(Context context, Uri uri) {
        return MediaPlayer.create(((TextureView) this).mContext, uri);
    }

    protected static Uri resourceEntryToUri(Context context, int i) {
        Resources resources = context.getResources();
        return Uri.parse("android.resource://" + resources.getResourcePackageName(i) + '/' + resources.getResourceTypeName(i) + '/' + resources.getResourceEntryName(i));
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void startAnimation() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            this.mMediaPlayer.start();
        }
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void stopAnimation() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void pauseAnimation() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
        }
    }
}
