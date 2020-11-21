package androidx.leanback.app;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public final class ProgressBarManager {
    boolean mEnableProgressBar = true;
    private Handler mHandler = new Handler();
    private long mInitialDelay = 1000;
    boolean mIsShowing;
    View mProgressBarView;
    boolean mUserProvidedProgressBar;
    ViewGroup rootView;
    private Runnable runnable = new Runnable() {
        /* class androidx.leanback.app.ProgressBarManager.AnonymousClass1 */

        public void run() {
            ProgressBarManager progressBarManager = ProgressBarManager.this;
            if (!progressBarManager.mEnableProgressBar) {
                return;
            }
            if (progressBarManager.mUserProvidedProgressBar || progressBarManager.rootView != null) {
                ProgressBarManager progressBarManager2 = ProgressBarManager.this;
                if (progressBarManager2.mIsShowing) {
                    View view = progressBarManager2.mProgressBarView;
                    if (view == null) {
                        progressBarManager2.mProgressBarView = new ProgressBar(ProgressBarManager.this.rootView.getContext(), null, 16842874);
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
                        layoutParams.gravity = 17;
                        ProgressBarManager progressBarManager3 = ProgressBarManager.this;
                        progressBarManager3.rootView.addView(progressBarManager3.mProgressBarView, layoutParams);
                    } else if (progressBarManager2.mUserProvidedProgressBar) {
                        view.setVisibility(0);
                    }
                }
            }
        }
    };

    public void setRootView(ViewGroup viewGroup) {
        this.rootView = viewGroup;
    }

    public void show() {
        if (this.mEnableProgressBar) {
            this.mIsShowing = true;
            this.mHandler.postDelayed(this.runnable, this.mInitialDelay);
        }
    }

    public void hide() {
        this.mIsShowing = false;
        if (this.mUserProvidedProgressBar) {
            this.mProgressBarView.setVisibility(4);
        } else {
            View view = this.mProgressBarView;
            if (view != null) {
                this.rootView.removeView(view);
                this.mProgressBarView = null;
            }
        }
        this.mHandler.removeCallbacks(this.runnable);
    }

    public void setInitialDelay(long j) {
        this.mInitialDelay = j;
    }
}
