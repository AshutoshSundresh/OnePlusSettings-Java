package com.oneplus.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.R$styleable;

public class OPGraphicsOptimizationPreference extends Preference {
    boolean mAnimationAvailable;
    private float mAspectRadio = 1.0f;
    private final Context mContext;
    MediaPlayer mMediaPlayer;
    private Uri mVideoPath;
    private boolean mVideoPaused;
    private boolean mVideoReady;

    public OPGraphicsOptimizationPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R$styleable.VideoPreference, 0, 0);
        try {
            Uri build = new Uri.Builder().scheme("android.resource").authority(context.getPackageName()).appendPath(String.valueOf(obtainStyledAttributes.getResourceId(R$styleable.VideoPreference_animation, 0))).build();
            this.mVideoPath = build;
            MediaPlayer create = MediaPlayer.create(this.mContext, build);
            this.mMediaPlayer = create;
            if (create == null || create.getDuration() <= 0) {
                setVisible(false);
            } else {
                setVisible(true);
                setLayoutResource(C0012R$layout.op_graphics_optimization_preference);
                obtainStyledAttributes.getResourceId(R$styleable.VideoPreference_preview, 0);
                this.mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    /* class com.oneplus.settings.widget.$$Lambda$OPGraphicsOptimizationPreference$GUBBeQUEtD02iFbJyhbrQmvTYU */

                    public final void onSeekComplete(MediaPlayer mediaPlayer) {
                        OPGraphicsOptimizationPreference.this.lambda$new$0$OPGraphicsOptimizationPreference(mediaPlayer);
                    }
                });
                this.mMediaPlayer.setOnPreparedListener($$Lambda$OPGraphicsOptimizationPreference$WN8lgavhDOjIctbQ2ZfY7ZCHB1c.INSTANCE);
                this.mAnimationAvailable = true;
                updateAspectRatio();
            }
        } catch (Exception unused) {
            Log.w("OPGraphicsOptimizationPreference", "Animation resource not found. Will not show animation.");
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$OPGraphicsOptimizationPreference(MediaPlayer mediaPlayer) {
        this.mVideoReady = true;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mAnimationAvailable) {
            TextureView textureView = (TextureView) preferenceViewHolder.findViewById(C0010R$id.video_texture_view);
            this.mMediaPlayer.start();
            int width = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getWidth();
            textureView.getLayoutParams().height = (int) (((float) width) / this.mAspectRadio);
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                /* class com.oneplus.settings.widget.OPGraphicsOptimizationPreference.AnonymousClass1 */

                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                }

                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                    MediaPlayer mediaPlayer = OPGraphicsOptimizationPreference.this.mMediaPlayer;
                    if (mediaPlayer != null) {
                        mediaPlayer.setSurface(new Surface(surfaceTexture));
                        OPGraphicsOptimizationPreference.this.mVideoReady = false;
                        OPGraphicsOptimizationPreference.this.mMediaPlayer.seekTo(0);
                    }
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    MediaPlayer mediaPlayer;
                    if (OPGraphicsOptimizationPreference.this.mVideoReady && !OPGraphicsOptimizationPreference.this.mVideoPaused && (mediaPlayer = OPGraphicsOptimizationPreference.this.mMediaPlayer) != null && !mediaPlayer.isPlaying()) {
                        OPGraphicsOptimizationPreference.this.mMediaPlayer.start();
                    }
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    public void updateAspectRatio() {
        this.mAspectRadio = ((float) this.mMediaPlayer.getVideoWidth()) / ((float) this.mMediaPlayer.getVideoHeight());
    }

    public void setVideoResume() {
        MediaPlayer mediaPlayer;
        if (!this.mVideoPaused && (mediaPlayer = this.mMediaPlayer) != null && !mediaPlayer.isPlaying()) {
            this.mMediaPlayer.start();
        }
    }

    public void release() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
    }

    public void setVideoPaused() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
        }
    }
}
