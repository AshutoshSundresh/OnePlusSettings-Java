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
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.R$styleable;
import com.android.settings.widget.AspectRatioFrameLayout;

public class OPVideoPreference extends Preference {
    private ImageView imageView;
    private AspectRatioFrameLayout layout;
    boolean mAnimationAvailable;
    private float mAspectRadio = 1.0f;
    private final Context mContext;
    MediaPlayer mMediaPlayer;
    private int mPreviewResource;
    private Uri mVideoPath;
    private boolean mVideoPaused;
    private boolean mVideoReady;

    public OPVideoPreference(Context context, AttributeSet attributeSet) {
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
                setLayoutResource(C0012R$layout.op_video_preference);
                this.mPreviewResource = obtainStyledAttributes.getResourceId(R$styleable.VideoPreference_preview, 0);
                this.mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    /* class com.oneplus.settings.widget.$$Lambda$OPVideoPreference$ynCil1Vg3ClpXktrurvZlqx29d4 */

                    public final void onSeekComplete(MediaPlayer mediaPlayer) {
                        OPVideoPreference.this.lambda$new$0$OPVideoPreference(mediaPlayer);
                    }
                });
                this.mMediaPlayer.setOnPreparedListener($$Lambda$OPVideoPreference$uc3qMTOa2JodIJYV5EhB8pjdWdg.INSTANCE);
                this.mAnimationAvailable = true;
                updateAspectRatio();
            }
        } catch (Exception unused) {
            Log.w("OPVideoPreference", "Animation resource not found. Will not show animation.");
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$OPVideoPreference(MediaPlayer mediaPlayer) {
        this.mVideoReady = true;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mAnimationAvailable) {
            this.imageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.video_preview_image);
            this.layout = (AspectRatioFrameLayout) preferenceViewHolder.findViewById(C0010R$id.video_container);
            this.imageView.setImageResource(this.mPreviewResource);
            this.layout.setAspectRatio(this.mAspectRadio);
            this.mMediaPlayer.start();
            ((TextureView) preferenceViewHolder.findViewById(C0010R$id.video_texture_view)).setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                /* class com.oneplus.settings.widget.OPVideoPreference.AnonymousClass1 */

                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                }

                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                    MediaPlayer mediaPlayer = OPVideoPreference.this.mMediaPlayer;
                    if (mediaPlayer != null) {
                        mediaPlayer.setSurface(new Surface(surfaceTexture));
                        OPVideoPreference.this.mVideoReady = false;
                        OPVideoPreference.this.mMediaPlayer.seekTo(0);
                    }
                }

                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    OPVideoPreference.this.imageView.setVisibility(0);
                    return false;
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    MediaPlayer mediaPlayer;
                    if (OPVideoPreference.this.mVideoReady) {
                        if (OPVideoPreference.this.imageView.getVisibility() == 0) {
                            OPVideoPreference.this.imageView.setVisibility(8);
                        }
                        if (!OPVideoPreference.this.mVideoPaused && (mediaPlayer = OPVideoPreference.this.mMediaPlayer) != null && !mediaPlayer.isPlaying()) {
                            OPVideoPreference.this.mMediaPlayer.start();
                        }
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
            ImageView imageView2 = this.imageView;
            if (imageView2 != null && this.layout != null) {
                imageView2.setImageResource(this.mPreviewResource);
                this.imageView.setVisibility(0);
                this.layout.setAspectRatio(this.mAspectRadio);
            }
        }
    }
}
