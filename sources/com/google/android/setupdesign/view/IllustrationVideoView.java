package com.google.android.setupdesign.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Animatable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import com.google.android.setupdesign.R$styleable;
import java.io.IOException;
import java.util.Map;

@TargetApi(14)
public class IllustrationVideoView extends TextureView implements Animatable, TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener {
    private float aspectRatio = 1.0f;
    private boolean isMediaPlayerLoading = false;
    protected MediaPlayer mediaPlayer;
    private boolean prepared;
    Surface surface;
    private int videoResId = 0;
    private String videoResPackageName;
    private int visibility = 0;

    /* access modifiers changed from: protected */
    public void onRenderingStart() {
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    /* access modifiers changed from: protected */
    public boolean shouldLoop() {
        return true;
    }

    public IllustrationVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SudIllustrationVideoView);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.SudIllustrationVideoView_sudVideo, 0);
        obtainStyledAttributes.recycle();
        setVideoResource(resourceId);
        setScaleX(0.9999999f);
        setScaleX(0.9999999f);
        setSurfaceTextureListener(this);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        float f = (float) size2;
        float f2 = (float) size;
        float f3 = this.aspectRatio;
        if (f < f2 * f3) {
            size = (int) (f / f3);
        } else {
            size2 = (int) (f2 * f3);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
    }

    public void setVideoResource(int i, String str) {
        if (i != this.videoResId || (str != null && !str.equals(this.videoResPackageName))) {
            this.videoResId = i;
            this.videoResPackageName = str;
            createMediaPlayer();
        }
    }

    public void setVideoResource(int i) {
        setVideoResource(i, getContext().getPackageName());
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            start();
        } else {
            stop();
        }
    }

    /* access modifiers changed from: protected */
    public void createMediaPlayer() {
        MediaPlayer mediaPlayer2 = this.mediaPlayer;
        if (mediaPlayer2 != null) {
            mediaPlayer2.release();
        }
        if (this.surface != null && this.videoResId != 0) {
            MediaPlayer mediaPlayer3 = new MediaPlayer();
            this.mediaPlayer = mediaPlayer3;
            mediaPlayer3.setSurface(this.surface);
            this.mediaPlayer.setOnPreparedListener(this);
            this.mediaPlayer.setOnSeekCompleteListener(this);
            this.mediaPlayer.setOnInfoListener(this);
            this.mediaPlayer.setOnErrorListener(this);
            setVideoResourceInternal(this.videoResId, this.videoResPackageName);
        }
    }

    private void setVideoResourceInternal(int i, String str) {
        try {
            this.mediaPlayer.setDataSource(getContext(), Uri.parse("android.resource://" + str + "/" + i), (Map<String, String>) null);
            this.mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("IllustrationVideoView", "Unable to set video data source: " + i, e);
        }
    }

    /* access modifiers changed from: protected */
    public void createSurface() {
        Surface surface2 = this.surface;
        if (surface2 != null) {
            surface2.release();
            this.surface = null;
        }
        SurfaceTexture surfaceTexture = getSurfaceTexture();
        if (surfaceTexture != null) {
            setIsMediaPlayerLoading(true);
            this.surface = new Surface(surfaceTexture);
        }
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        if (i == 0) {
            reattach();
        } else {
            release();
        }
    }

    public void setVisibility(int i) {
        this.visibility = i;
        if (this.isMediaPlayerLoading && i == 0) {
            i = 4;
        }
        super.setVisibility(i);
    }

    private void setIsMediaPlayerLoading(boolean z) {
        this.isMediaPlayerLoading = z;
        setVisibility(this.visibility);
    }

    public void release() {
        MediaPlayer mediaPlayer2 = this.mediaPlayer;
        if (mediaPlayer2 != null) {
            mediaPlayer2.release();
            this.mediaPlayer = null;
            this.prepared = false;
        }
        Surface surface2 = this.surface;
        if (surface2 != null) {
            surface2.release();
            this.surface = null;
        }
    }

    private void reattach() {
        if (this.surface == null) {
            initVideo();
        }
    }

    private void initVideo() {
        if (getWindowVisibility() == 0) {
            createSurface();
            if (this.surface != null) {
                createMediaPlayer();
            } else {
                Log.i("IllustrationVideoView", "Surface is null");
            }
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        setIsMediaPlayerLoading(true);
        initVideo();
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        release();
        return true;
    }

    public void start() {
        MediaPlayer mediaPlayer2;
        if (this.prepared && (mediaPlayer2 = this.mediaPlayer) != null && !mediaPlayer2.isPlaying()) {
            this.mediaPlayer.start();
        }
    }

    public void stop() {
        MediaPlayer mediaPlayer2;
        if (this.prepared && (mediaPlayer2 = this.mediaPlayer) != null) {
            mediaPlayer2.pause();
        }
    }

    public boolean isRunning() {
        MediaPlayer mediaPlayer2 = this.mediaPlayer;
        return mediaPlayer2 != null && mediaPlayer2.isPlaying();
    }

    public boolean onInfo(MediaPlayer mediaPlayer2, int i, int i2) {
        if (i == 3) {
            setIsMediaPlayerLoading(false);
            onRenderingStart();
        }
        return false;
    }

    public void onPrepared(MediaPlayer mediaPlayer2) {
        float f;
        this.prepared = true;
        mediaPlayer2.setLooping(shouldLoop());
        if (mediaPlayer2.getVideoWidth() <= 0 || mediaPlayer2.getVideoHeight() <= 0) {
            Log.w("IllustrationVideoView", "Unexpected video size=" + mediaPlayer2.getVideoWidth() + "x" + mediaPlayer2.getVideoHeight());
            f = 0.0f;
        } else {
            f = ((float) mediaPlayer2.getVideoHeight()) / ((float) mediaPlayer2.getVideoWidth());
        }
        if (Float.compare(this.aspectRatio, f) != 0) {
            this.aspectRatio = f;
            requestLayout();
        }
        if (getWindowVisibility() == 0) {
            start();
        }
    }

    public void onSeekComplete(MediaPlayer mediaPlayer2) {
        if (isPrepared()) {
            mediaPlayer2.start();
        } else {
            Log.e("IllustrationVideoView", "Seek complete but media player not prepared");
        }
    }

    public int getCurrentPosition() {
        MediaPlayer mediaPlayer2 = this.mediaPlayer;
        if (mediaPlayer2 == null) {
            return 0;
        }
        return mediaPlayer2.getCurrentPosition();
    }

    /* access modifiers changed from: protected */
    public boolean isPrepared() {
        return this.prepared;
    }

    public boolean onError(MediaPlayer mediaPlayer2, int i, int i2) {
        Log.w("IllustrationVideoView", "MediaPlayer error. what=" + i + " extra=" + i2);
        return false;
    }

    public MediaPlayer getMediaPlayer() {
        return this.mediaPlayer;
    }

    /* access modifiers changed from: protected */
    public float getAspectRatio() {
        return this.aspectRatio;
    }
}
