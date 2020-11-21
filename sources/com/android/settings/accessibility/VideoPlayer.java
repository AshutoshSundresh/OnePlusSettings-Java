package com.android.settings.accessibility;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.TextureView;

public class VideoPlayer implements TextureView.SurfaceTextureListener {
    private Surface animationSurface;
    private final Context context;
    private MediaPlayer mediaPlayer;
    private final Object mediaPlayerLock = new Object();
    private State mediaPlayerState = State.NONE;
    private final int videoRes;

    public enum State {
        NONE,
        PREPARED,
        STARTED,
        PAUSED,
        STOPPED,
        END
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public static VideoPlayer create(Context context2, int i, TextureView textureView) {
        return new VideoPlayer(context2, i, textureView);
    }

    private VideoPlayer(Context context2, int i, TextureView textureView) {
        this.context = context2;
        this.videoRes = i;
        textureView.setSurfaceTextureListener(this);
    }

    public void release() {
        synchronized (this.mediaPlayerLock) {
            if (!(this.mediaPlayerState == State.NONE || this.mediaPlayerState == State.END)) {
                this.mediaPlayerState = State.END;
                this.mediaPlayer.release();
                this.mediaPlayer = null;
            }
        }
        Surface surface = this.animationSurface;
        if (surface != null) {
            surface.release();
            this.animationSurface = null;
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        this.animationSurface = new Surface(surfaceTexture);
        synchronized (this.mediaPlayerLock) {
            MediaPlayer create = MediaPlayer.create(this.context, this.videoRes);
            this.mediaPlayer = create;
            this.mediaPlayerState = State.PREPARED;
            create.setSurface(this.animationSurface);
            this.mediaPlayer.setLooping(true);
            this.mediaPlayer.start();
            this.mediaPlayerState = State.STARTED;
        }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        release();
        return false;
    }
}
