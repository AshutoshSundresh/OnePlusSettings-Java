package androidx.leanback.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import androidx.leanback.R$layout;

public class VideoSupportFragment extends PlaybackSupportFragment {
    SurfaceHolder.Callback mMediaPlaybackCallback;
    int mState;
    SurfaceView mVideoSurface;

    @Override // androidx.leanback.app.PlaybackSupportFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        ViewGroup viewGroup2 = (ViewGroup) super.onCreateView(layoutInflater, viewGroup, bundle);
        SurfaceView surfaceView = (SurfaceView) LayoutInflater.from(getContext()).inflate(R$layout.lb_video_surface, viewGroup2, false);
        this.mVideoSurface = surfaceView;
        viewGroup2.addView(surfaceView, 0);
        this.mVideoSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
            /* class androidx.leanback.app.VideoSupportFragment.AnonymousClass1 */

            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                SurfaceHolder.Callback callback = VideoSupportFragment.this.mMediaPlaybackCallback;
                if (callback != null) {
                    callback.surfaceCreated(surfaceHolder);
                }
                VideoSupportFragment.this.mState = 1;
            }

            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                SurfaceHolder.Callback callback = VideoSupportFragment.this.mMediaPlaybackCallback;
                if (callback != null) {
                    callback.surfaceChanged(surfaceHolder, i, i2, i3);
                }
            }

            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                SurfaceHolder.Callback callback = VideoSupportFragment.this.mMediaPlaybackCallback;
                if (callback != null) {
                    callback.surfaceDestroyed(surfaceHolder);
                }
                VideoSupportFragment.this.mState = 0;
            }
        });
        setBackgroundType(2);
        return viewGroup2;
    }

    @Override // androidx.leanback.app.PlaybackSupportFragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        this.mVideoSurface = null;
        super.onDestroyView();
    }
}
