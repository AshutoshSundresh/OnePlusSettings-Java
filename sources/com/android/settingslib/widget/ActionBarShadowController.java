package com.android.settingslib.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.view.View;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class ActionBarShadowController implements LifecycleObserver {
    static final float ELEVATION_HIGH = 8.0f;
    static final float ELEVATION_LOW = 0.0f;
    private boolean mIsScrollWatcherAttached;
    ScrollChangeWatcher mScrollChangeWatcher;
    private View mScrollView;

    public static ActionBarShadowController attachToView(Activity activity, Lifecycle lifecycle, View view) {
        return new ActionBarShadowController(activity, lifecycle, view);
    }

    private ActionBarShadowController(Activity activity, Lifecycle lifecycle, View view) {
        this.mScrollChangeWatcher = new ScrollChangeWatcher(this, activity);
        this.mScrollView = view;
        attachScrollWatcher();
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void attachScrollWatcher() {
        if (!this.mIsScrollWatcherAttached) {
            this.mIsScrollWatcherAttached = true;
            this.mScrollView.setOnScrollChangeListener(this.mScrollChangeWatcher);
            this.mScrollChangeWatcher.updateDropShadow(this.mScrollView);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void detachScrollWatcher() {
        this.mScrollView.setOnScrollChangeListener(null);
        this.mIsScrollWatcherAttached = false;
    }

    /* access modifiers changed from: package-private */
    public final class ScrollChangeWatcher implements View.OnScrollChangeListener {
        private final Activity mActivity;
        private final View mAnchorView = null;

        ScrollChangeWatcher(ActionBarShadowController actionBarShadowController, Activity activity) {
            this.mActivity = activity;
        }

        public void onScrollChange(View view, int i, int i2, int i3, int i4) {
            updateDropShadow(view);
        }

        public void updateDropShadow(View view) {
            ActionBar actionBar;
            boolean canScrollVertically = view.canScrollVertically(-1);
            View view2 = this.mAnchorView;
            float f = ActionBarShadowController.ELEVATION_HIGH;
            if (view2 != null) {
                if (!canScrollVertically) {
                    f = 0.0f;
                }
                view2.setElevation(f);
                return;
            }
            Activity activity = this.mActivity;
            if (activity != null && (actionBar = activity.getActionBar()) != null) {
                if (!canScrollVertically) {
                    f = 0.0f;
                }
                actionBar.setElevation(f);
            }
        }
    }
}
