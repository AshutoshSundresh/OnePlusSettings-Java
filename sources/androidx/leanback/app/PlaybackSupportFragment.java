package androidx.leanback.app;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.leanback.R$animator;
import androidx.leanback.R$attr;
import androidx.leanback.R$color;
import androidx.leanback.R$dimen;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.R$string;
import androidx.leanback.animation.LogAccelerateInterpolator;
import androidx.leanback.animation.LogDecelerateInterpolator;
import androidx.leanback.media.PlaybackGlueHost$HostCallback;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.BaseGridView;
import androidx.leanback.widget.BaseOnItemViewClickedListener;
import androidx.leanback.widget.BaseOnItemViewSelectedListener;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.ItemAlignmentFacet;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.PlaybackRowPresenter;
import androidx.leanback.widget.PlaybackSeekUi;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.SparseArrayObjectAdapter;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;

public class PlaybackSupportFragment extends Fragment {
    ObjectAdapter mAdapter;
    private final ItemBridgeAdapter.AdapterListener mAdapterListener = new ItemBridgeAdapter.AdapterListener() {
        /* class androidx.leanback.app.PlaybackSupportFragment.AnonymousClass10 */

        @Override // androidx.leanback.widget.ItemBridgeAdapter.AdapterListener
        public void onBind(ItemBridgeAdapter.ViewHolder viewHolder) {
        }

        @Override // androidx.leanback.widget.ItemBridgeAdapter.AdapterListener
        public void onAttachedToWindow(ItemBridgeAdapter.ViewHolder viewHolder) {
            if (!PlaybackSupportFragment.this.mControlVisible) {
                viewHolder.getViewHolder().view.setAlpha(0.0f);
            }
        }

        @Override // androidx.leanback.widget.ItemBridgeAdapter.AdapterListener
        public void onCreate(ItemBridgeAdapter.ViewHolder viewHolder) {
            Presenter.ViewHolder viewHolder2 = viewHolder.getViewHolder();
            if (viewHolder2 instanceof PlaybackSeekUi) {
                ((PlaybackSeekUi) viewHolder2).setPlaybackSeekUiClient(PlaybackSupportFragment.this.mChainedClient);
            }
        }

        @Override // androidx.leanback.widget.ItemBridgeAdapter.AdapterListener
        public void onDetachedFromWindow(ItemBridgeAdapter.ViewHolder viewHolder) {
            viewHolder.getViewHolder().view.setAlpha(1.0f);
            viewHolder.getViewHolder().view.setTranslationY(0.0f);
            viewHolder.getViewHolder().view.setAlpha(1.0f);
        }
    };
    int mAnimationTranslateY;
    int mAutohideTimerAfterPlayingInMs;
    int mAutohideTimerAfterTickleInMs;
    int mBackgroundType = 1;
    View mBackgroundView;
    int mBgAlpha;
    int mBgDarkColor;
    ValueAnimator mBgFadeInAnimator;
    ValueAnimator mBgFadeOutAnimator;
    int mBgLightColor;
    final PlaybackSeekUi.Client mChainedClient = new PlaybackSeekUi.Client(this) {
        /* class androidx.leanback.app.PlaybackSupportFragment.AnonymousClass11 */
    };
    ValueAnimator mControlRowFadeInAnimator;
    ValueAnimator mControlRowFadeOutAnimator;
    boolean mControlVisible = true;
    boolean mControlVisibleBeforeOnCreateView = true;
    BaseOnItemViewClickedListener mExternalItemClickedListener;
    BaseOnItemViewSelectedListener mExternalItemSelectedListener;
    OnFadeCompleteListener mFadeCompleteListener;
    private final Animator.AnimatorListener mFadeListener = new Animator.AnimatorListener() {
        /* class androidx.leanback.app.PlaybackSupportFragment.AnonymousClass3 */

        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
            PlaybackSupportFragment.this.enableVerticalGridAnimations(false);
        }

        public void onAnimationEnd(Animator animator) {
            ItemBridgeAdapter.ViewHolder viewHolder;
            PlaybackSupportFragment playbackSupportFragment = PlaybackSupportFragment.this;
            if (playbackSupportFragment.mBgAlpha > 0) {
                playbackSupportFragment.enableVerticalGridAnimations(true);
                OnFadeCompleteListener onFadeCompleteListener = PlaybackSupportFragment.this.mFadeCompleteListener;
                if (onFadeCompleteListener != null) {
                    onFadeCompleteListener.onFadeInComplete();
                    return;
                }
                return;
            }
            VerticalGridView verticalGridView = playbackSupportFragment.getVerticalGridView();
            if (verticalGridView != null && verticalGridView.getSelectedPosition() == 0 && (viewHolder = (ItemBridgeAdapter.ViewHolder) verticalGridView.findViewHolderForAdapterPosition(0)) != null && (viewHolder.getPresenter() instanceof PlaybackRowPresenter)) {
                ((PlaybackRowPresenter) viewHolder.getPresenter()).onReappear((RowPresenter.ViewHolder) viewHolder.getViewHolder());
            }
            OnFadeCompleteListener onFadeCompleteListener2 = PlaybackSupportFragment.this.mFadeCompleteListener;
            if (onFadeCompleteListener2 != null) {
                onFadeCompleteListener2.onFadeOutComplete();
            }
        }
    };
    boolean mFadingEnabled = true;
    private final Handler mHandler = new Handler() {
        /* class androidx.leanback.app.PlaybackSupportFragment.AnonymousClass4 */

        public void handleMessage(Message message) {
            if (message.what == 1) {
                PlaybackSupportFragment playbackSupportFragment = PlaybackSupportFragment.this;
                if (playbackSupportFragment.mFadingEnabled) {
                    playbackSupportFragment.hideControlsOverlay(true);
                }
            }
        }
    };
    PlaybackGlueHost$HostCallback mHostCallback;
    boolean mInSeek;
    View.OnKeyListener mInputEventHandler;
    private TimeInterpolator mLogAccelerateInterpolator = new LogAccelerateInterpolator(100, 0);
    private TimeInterpolator mLogDecelerateInterpolator = new LogDecelerateInterpolator(100, 0);
    int mMajorFadeTranslateY;
    int mMinorFadeTranslateY;
    private final BaseOnItemViewClickedListener mOnItemViewClickedListener = new BaseOnItemViewClickedListener() {
        /* class androidx.leanback.app.PlaybackSupportFragment.AnonymousClass1 */

        @Override // androidx.leanback.widget.BaseOnItemViewClickedListener
        public void onItemClicked(Presenter.ViewHolder viewHolder, Object obj, RowPresenter.ViewHolder viewHolder2, Object obj2) {
            BaseOnItemViewClickedListener baseOnItemViewClickedListener = PlaybackSupportFragment.this.mPlaybackItemClickedListener;
            BaseOnItemViewClickedListener baseOnItemViewClickedListener2 = PlaybackSupportFragment.this.mExternalItemClickedListener;
            if (baseOnItemViewClickedListener2 != null) {
                baseOnItemViewClickedListener2.onItemClicked(viewHolder, obj, viewHolder2, obj2);
            }
        }
    };
    private final BaseOnItemViewSelectedListener mOnItemViewSelectedListener = new BaseOnItemViewSelectedListener() {
        /* class androidx.leanback.app.PlaybackSupportFragment.AnonymousClass2 */

        @Override // androidx.leanback.widget.BaseOnItemViewSelectedListener
        public void onItemSelected(Presenter.ViewHolder viewHolder, Object obj, RowPresenter.ViewHolder viewHolder2, Object obj2) {
            BaseOnItemViewSelectedListener baseOnItemViewSelectedListener = PlaybackSupportFragment.this.mExternalItemSelectedListener;
            if (baseOnItemViewSelectedListener != null) {
                baseOnItemViewSelectedListener.onItemSelected(viewHolder, obj, viewHolder2, obj2);
            }
        }
    };
    private final BaseGridView.OnKeyInterceptListener mOnKeyInterceptListener = new BaseGridView.OnKeyInterceptListener() {
        /* class androidx.leanback.app.PlaybackSupportFragment.AnonymousClass6 */

        @Override // androidx.leanback.widget.BaseGridView.OnKeyInterceptListener
        public boolean onInterceptKeyEvent(KeyEvent keyEvent) {
            return PlaybackSupportFragment.this.onInterceptInputEvent(keyEvent);
        }
    };
    private final BaseGridView.OnTouchInterceptListener mOnTouchInterceptListener = new BaseGridView.OnTouchInterceptListener() {
        /* class androidx.leanback.app.PlaybackSupportFragment.AnonymousClass5 */

        @Override // androidx.leanback.widget.BaseGridView.OnTouchInterceptListener
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return PlaybackSupportFragment.this.onInterceptInputEvent(motionEvent);
        }
    };
    ValueAnimator mOtherRowFadeInAnimator;
    ValueAnimator mOtherRowFadeOutAnimator;
    int mOtherRowsCenterToBottom;
    int mPaddingBottom;
    BaseOnItemViewClickedListener mPlaybackItemClickedListener;
    PlaybackRowPresenter mPresenter;
    ProgressBarManager mProgressBarManager = new ProgressBarManager();
    View mRootView;
    Row mRow;
    RowsSupportFragment mRowsSupportFragment;
    boolean mShowOrHideControlsOverlayOnUserInteraction = true;

    public static class OnFadeCompleteListener {
        public abstract void onFadeInComplete();

        public abstract void onFadeOutComplete();
    }

    public PlaybackSupportFragment() {
        this.mProgressBarManager.setInitialDelay(500);
    }

    /* access modifiers changed from: package-private */
    public VerticalGridView getVerticalGridView() {
        RowsSupportFragment rowsSupportFragment = this.mRowsSupportFragment;
        if (rowsSupportFragment == null) {
            return null;
        }
        return rowsSupportFragment.getVerticalGridView();
    }

    /* access modifiers changed from: package-private */
    public void setBgAlpha(int i) {
        this.mBgAlpha = i;
        View view = this.mBackgroundView;
        if (view != null) {
            view.getBackground().setAlpha(i);
        }
    }

    /* access modifiers changed from: package-private */
    public void enableVerticalGridAnimations(boolean z) {
        if (getVerticalGridView() != null) {
            getVerticalGridView().setAnimateChildLayout(z);
        }
    }

    public void tickle() {
        stopFadeTimer();
        showControlsOverlay(true);
        int i = this.mAutohideTimerAfterTickleInMs;
        if (i > 0 && this.mFadingEnabled) {
            startFadeTimer(i);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean onInterceptInputEvent(InputEvent inputEvent) {
        int i;
        int i2;
        boolean z;
        boolean z2 = true;
        boolean z3 = !this.mControlVisible;
        if (inputEvent instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) inputEvent;
            i2 = keyEvent.getKeyCode();
            i = keyEvent.getAction();
            View.OnKeyListener onKeyListener = this.mInputEventHandler;
            z = onKeyListener != null ? onKeyListener.onKey(getView(), i2, keyEvent) : false;
        } else {
            z = false;
            i2 = 0;
            i = 0;
        }
        if (i2 != 4 && i2 != 111) {
            switch (i2) {
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                    if (!z3) {
                        z2 = z;
                    }
                    if (!this.mShowOrHideControlsOverlayOnUserInteraction || i != 0) {
                        return z2;
                    }
                    tickle();
                    return z2;
                default:
                    if (this.mShowOrHideControlsOverlayOnUserInteraction && z && i == 0) {
                        tickle();
                        break;
                    }
            }
        } else if (this.mInSeek) {
            return false;
        } else {
            if (this.mShowOrHideControlsOverlayOnUserInteraction && !z3) {
                if (((KeyEvent) inputEvent).getAction() != 1) {
                    return true;
                }
                hideControlsOverlay(true);
                return true;
            }
        }
        return z;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mControlVisible = true;
        if (!this.mControlVisibleBeforeOnCreateView) {
            showControlsOverlay(false, false);
            this.mControlVisibleBeforeOnCreateView = true;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mControlVisible && this.mFadingEnabled) {
            startFadeTimer(this.mAutohideTimerAfterPlayingInMs);
        }
        getVerticalGridView().setOnTouchInterceptListener(this.mOnTouchInterceptListener);
        getVerticalGridView().setOnKeyInterceptListener(this.mOnKeyInterceptListener);
        PlaybackGlueHost$HostCallback playbackGlueHost$HostCallback = this.mHostCallback;
        if (playbackGlueHost$HostCallback != null) {
            playbackGlueHost$HostCallback.onHostResume();
        }
    }

    private void stopFadeTimer() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(1);
        }
    }

    private void startFadeTimer(int i) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(1);
            this.mHandler.sendEmptyMessageDelayed(1, (long) i);
        }
    }

    private static ValueAnimator loadAnimator(Context context, int i) {
        ValueAnimator valueAnimator = (ValueAnimator) AnimatorInflater.loadAnimator(context, i);
        valueAnimator.setDuration(valueAnimator.getDuration() * 1);
        return valueAnimator;
    }

    private void loadBgAnimator() {
        AnonymousClass7 r0 = new ValueAnimator.AnimatorUpdateListener() {
            /* class androidx.leanback.app.PlaybackSupportFragment.AnonymousClass7 */

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PlaybackSupportFragment.this.setBgAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        };
        Context context = getContext();
        ValueAnimator loadAnimator = loadAnimator(context, R$animator.lb_playback_bg_fade_in);
        this.mBgFadeInAnimator = loadAnimator;
        loadAnimator.addUpdateListener(r0);
        this.mBgFadeInAnimator.addListener(this.mFadeListener);
        ValueAnimator loadAnimator2 = loadAnimator(context, R$animator.lb_playback_bg_fade_out);
        this.mBgFadeOutAnimator = loadAnimator2;
        loadAnimator2.addUpdateListener(r0);
        this.mBgFadeOutAnimator.addListener(this.mFadeListener);
    }

    private void loadControlRowAnimator() {
        AnonymousClass8 r0 = new ValueAnimator.AnimatorUpdateListener() {
            /* class androidx.leanback.app.PlaybackSupportFragment.AnonymousClass8 */

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RecyclerView.ViewHolder findViewHolderForAdapterPosition;
                View view;
                if (PlaybackSupportFragment.this.getVerticalGridView() != null && (findViewHolderForAdapterPosition = PlaybackSupportFragment.this.getVerticalGridView().findViewHolderForAdapterPosition(0)) != null && (view = findViewHolderForAdapterPosition.itemView) != null) {
                    float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    view.setAlpha(floatValue);
                    view.setTranslationY(((float) PlaybackSupportFragment.this.mAnimationTranslateY) * (1.0f - floatValue));
                }
            }
        };
        Context context = getContext();
        ValueAnimator loadAnimator = loadAnimator(context, R$animator.lb_playback_controls_fade_in);
        this.mControlRowFadeInAnimator = loadAnimator;
        loadAnimator.addUpdateListener(r0);
        this.mControlRowFadeInAnimator.setInterpolator(this.mLogDecelerateInterpolator);
        ValueAnimator loadAnimator2 = loadAnimator(context, R$animator.lb_playback_controls_fade_out);
        this.mControlRowFadeOutAnimator = loadAnimator2;
        loadAnimator2.addUpdateListener(r0);
        this.mControlRowFadeOutAnimator.setInterpolator(this.mLogAccelerateInterpolator);
    }

    private void loadOtherRowAnimator() {
        AnonymousClass9 r0 = new ValueAnimator.AnimatorUpdateListener() {
            /* class androidx.leanback.app.PlaybackSupportFragment.AnonymousClass9 */

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (PlaybackSupportFragment.this.getVerticalGridView() != null) {
                    float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    int childCount = PlaybackSupportFragment.this.getVerticalGridView().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = PlaybackSupportFragment.this.getVerticalGridView().getChildAt(i);
                        if (PlaybackSupportFragment.this.getVerticalGridView().getChildAdapterPosition(childAt) > 0) {
                            childAt.setAlpha(floatValue);
                            childAt.setTranslationY(((float) PlaybackSupportFragment.this.mAnimationTranslateY) * (1.0f - floatValue));
                        }
                    }
                }
            }
        };
        Context context = getContext();
        ValueAnimator loadAnimator = loadAnimator(context, R$animator.lb_playback_controls_fade_in);
        this.mOtherRowFadeInAnimator = loadAnimator;
        loadAnimator.addUpdateListener(r0);
        this.mOtherRowFadeInAnimator.setInterpolator(this.mLogDecelerateInterpolator);
        ValueAnimator loadAnimator2 = loadAnimator(context, R$animator.lb_playback_controls_fade_out);
        this.mOtherRowFadeOutAnimator = loadAnimator2;
        loadAnimator2.addUpdateListener(r0);
        this.mOtherRowFadeOutAnimator.setInterpolator(new AccelerateInterpolator());
    }

    public void showControlsOverlay(boolean z) {
        showControlsOverlay(true, z);
    }

    public void hideControlsOverlay(boolean z) {
        showControlsOverlay(false, z);
    }

    static void reverseFirstOrStartSecond(ValueAnimator valueAnimator, ValueAnimator valueAnimator2, boolean z) {
        if (valueAnimator.isStarted()) {
            valueAnimator.reverse();
            if (!z) {
                valueAnimator.end();
                return;
            }
            return;
        }
        valueAnimator2.start();
        if (!z) {
            valueAnimator2.end();
        }
    }

    static void endAll(ValueAnimator valueAnimator, ValueAnimator valueAnimator2) {
        if (valueAnimator.isStarted()) {
            valueAnimator.end();
        } else if (valueAnimator2.isStarted()) {
            valueAnimator2.end();
        }
    }

    /* access modifiers changed from: package-private */
    public void showControlsOverlay(boolean z, boolean z2) {
        int i;
        if (getView() == null) {
            this.mControlVisibleBeforeOnCreateView = z;
            return;
        }
        if (!isResumed()) {
            z2 = false;
        }
        if (z != this.mControlVisible) {
            this.mControlVisible = z;
            if (!z) {
                stopFadeTimer();
            }
            this.mAnimationTranslateY = (getVerticalGridView() == null || getVerticalGridView().getSelectedPosition() == 0) ? this.mMajorFadeTranslateY : this.mMinorFadeTranslateY;
            if (z) {
                reverseFirstOrStartSecond(this.mBgFadeOutAnimator, this.mBgFadeInAnimator, z2);
                reverseFirstOrStartSecond(this.mControlRowFadeOutAnimator, this.mControlRowFadeInAnimator, z2);
                reverseFirstOrStartSecond(this.mOtherRowFadeOutAnimator, this.mOtherRowFadeInAnimator, z2);
            } else {
                reverseFirstOrStartSecond(this.mBgFadeInAnimator, this.mBgFadeOutAnimator, z2);
                reverseFirstOrStartSecond(this.mControlRowFadeInAnimator, this.mControlRowFadeOutAnimator, z2);
                reverseFirstOrStartSecond(this.mOtherRowFadeInAnimator, this.mOtherRowFadeOutAnimator, z2);
            }
            if (z2) {
                View view = getView();
                if (z) {
                    i = R$string.lb_playback_controls_shown;
                } else {
                    i = R$string.lb_playback_controls_hidden;
                }
                view.announceForAccessibility(getString(i));
            }
        } else if (!z2) {
            endAll(this.mBgFadeInAnimator, this.mBgFadeOutAnimator);
            endAll(this.mControlRowFadeInAnimator, this.mControlRowFadeOutAnimator);
            endAll(this.mOtherRowFadeInAnimator, this.mOtherRowFadeOutAnimator);
        }
    }

    private void setupChildFragmentLayout() {
        setVerticalGridViewLayout(this.mRowsSupportFragment.getVerticalGridView());
    }

    /* access modifiers changed from: package-private */
    public void setVerticalGridViewLayout(VerticalGridView verticalGridView) {
        if (verticalGridView != null) {
            verticalGridView.setWindowAlignmentOffset(-this.mPaddingBottom);
            verticalGridView.setWindowAlignmentOffsetPercent(-1.0f);
            verticalGridView.setItemAlignmentOffset(this.mOtherRowsCenterToBottom - this.mPaddingBottom);
            verticalGridView.setItemAlignmentOffsetPercent(50.0f);
            verticalGridView.setPadding(verticalGridView.getPaddingLeft(), verticalGridView.getPaddingTop(), verticalGridView.getPaddingRight(), this.mPaddingBottom);
            verticalGridView.setWindowAlignment(2);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mOtherRowsCenterToBottom = getResources().getDimensionPixelSize(R$dimen.lb_playback_other_rows_center_to_bottom);
        this.mPaddingBottom = getResources().getDimensionPixelSize(R$dimen.lb_playback_controls_padding_bottom);
        this.mBgDarkColor = getResources().getColor(R$color.lb_playback_controls_background_dark);
        this.mBgLightColor = getResources().getColor(R$color.lb_playback_controls_background_light);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R$attr.playbackControlsAutoHideTimeout, typedValue, true);
        this.mAutohideTimerAfterPlayingInMs = typedValue.data;
        getContext().getTheme().resolveAttribute(R$attr.playbackControlsAutoHideTickleTimeout, typedValue, true);
        this.mAutohideTimerAfterTickleInMs = typedValue.data;
        this.mMajorFadeTranslateY = getResources().getDimensionPixelSize(R$dimen.lb_playback_major_fade_translate_y);
        this.mMinorFadeTranslateY = getResources().getDimensionPixelSize(R$dimen.lb_playback_minor_fade_translate_y);
        loadBgAnimator();
        loadControlRowAnimator();
        loadOtherRowAnimator();
    }

    public void setBackgroundType(int i) {
        if (i != 0 && i != 1 && i != 2) {
            throw new IllegalArgumentException("Invalid background type");
        } else if (i != this.mBackgroundType) {
            this.mBackgroundType = i;
            updateBackground();
        }
    }

    private void updateBackground() {
        if (this.mBackgroundView != null) {
            int i = this.mBgDarkColor;
            int i2 = this.mBackgroundType;
            if (i2 == 0) {
                i = 0;
            } else if (i2 == 2) {
                i = this.mBgLightColor;
            }
            this.mBackgroundView.setBackground(new ColorDrawable(i));
            setBgAlpha(this.mBgAlpha);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R$layout.lb_playback_fragment, viewGroup, false);
        this.mRootView = inflate;
        this.mBackgroundView = inflate.findViewById(R$id.playback_fragment_background);
        RowsSupportFragment rowsSupportFragment = (RowsSupportFragment) getChildFragmentManager().findFragmentById(R$id.playback_controls_dock);
        this.mRowsSupportFragment = rowsSupportFragment;
        if (rowsSupportFragment == null) {
            this.mRowsSupportFragment = new RowsSupportFragment();
            FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
            beginTransaction.replace(R$id.playback_controls_dock, this.mRowsSupportFragment);
            beginTransaction.commit();
        }
        ObjectAdapter objectAdapter = this.mAdapter;
        if (objectAdapter == null) {
            setAdapter(new ArrayObjectAdapter(new ClassPresenterSelector()));
        } else {
            this.mRowsSupportFragment.setAdapter(objectAdapter);
        }
        this.mRowsSupportFragment.setOnItemViewSelectedListener(this.mOnItemViewSelectedListener);
        this.mRowsSupportFragment.setOnItemViewClickedListener(this.mOnItemViewClickedListener);
        this.mBgAlpha = 255;
        updateBackground();
        this.mRowsSupportFragment.setExternalAdapterListener(this.mAdapterListener);
        ProgressBarManager progressBarManager = getProgressBarManager();
        if (progressBarManager != null) {
            progressBarManager.setRootView((ViewGroup) this.mRootView);
        }
        return this.mRootView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        setupChildFragmentLayout();
        this.mRowsSupportFragment.setAdapter(this.mAdapter);
        PlaybackGlueHost$HostCallback playbackGlueHost$HostCallback = this.mHostCallback;
        if (playbackGlueHost$HostCallback != null) {
            playbackGlueHost$HostCallback.onHostStart();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        PlaybackGlueHost$HostCallback playbackGlueHost$HostCallback = this.mHostCallback;
        if (playbackGlueHost$HostCallback != null) {
            playbackGlueHost$HostCallback.onHostStop();
        }
        super.onStop();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        PlaybackGlueHost$HostCallback playbackGlueHost$HostCallback = this.mHostCallback;
        if (playbackGlueHost$HostCallback != null) {
            playbackGlueHost$HostCallback.onHostPause();
        }
        if (this.mHandler.hasMessages(1)) {
            this.mHandler.removeMessages(1);
        }
        super.onPause();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        this.mRootView = null;
        this.mBackgroundView = null;
        super.onDestroyView();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        PlaybackGlueHost$HostCallback playbackGlueHost$HostCallback = this.mHostCallback;
        if (playbackGlueHost$HostCallback != null) {
            playbackGlueHost$HostCallback.onHostDestroy();
        }
        super.onDestroy();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v1, resolved type: java.lang.Object[] */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: package-private */
    public void setPlaybackRowPresenterAlignment() {
        Presenter[] presenters;
        ObjectAdapter objectAdapter = this.mAdapter;
        if (!(objectAdapter == null || objectAdapter.getPresenterSelector() == null || (presenters = this.mAdapter.getPresenterSelector().getPresenters()) == null)) {
            for (int i = 0; i < presenters.length; i++) {
                if ((presenters[i] instanceof PlaybackRowPresenter) && presenters[i].getFacet(ItemAlignmentFacet.class) == null) {
                    ItemAlignmentFacet itemAlignmentFacet = new ItemAlignmentFacet();
                    ItemAlignmentFacet.ItemAlignmentDef itemAlignmentDef = new ItemAlignmentFacet.ItemAlignmentDef();
                    itemAlignmentDef.setItemAlignmentOffset(0);
                    itemAlignmentDef.setItemAlignmentOffsetPercent(100.0f);
                    itemAlignmentFacet.setAlignmentDefs(new ItemAlignmentFacet.ItemAlignmentDef[]{itemAlignmentDef});
                    presenters[i].setFacet(ItemAlignmentFacet.class, itemAlignmentFacet);
                }
            }
        }
    }

    public void setAdapter(ObjectAdapter objectAdapter) {
        this.mAdapter = objectAdapter;
        setupRow();
        setupPresenter();
        setPlaybackRowPresenterAlignment();
        RowsSupportFragment rowsSupportFragment = this.mRowsSupportFragment;
        if (rowsSupportFragment != null) {
            rowsSupportFragment.setAdapter(objectAdapter);
        }
    }

    private void setupRow() {
        Row row;
        ObjectAdapter objectAdapter = this.mAdapter;
        if (!(objectAdapter instanceof ArrayObjectAdapter) || this.mRow == null) {
            ObjectAdapter objectAdapter2 = this.mAdapter;
            if ((objectAdapter2 instanceof SparseArrayObjectAdapter) && (row = this.mRow) != null) {
                ((SparseArrayObjectAdapter) objectAdapter2).set(0, row);
                return;
            }
            return;
        }
        ArrayObjectAdapter arrayObjectAdapter = (ArrayObjectAdapter) objectAdapter;
        if (arrayObjectAdapter.size() == 0) {
            arrayObjectAdapter.add(this.mRow);
        } else {
            arrayObjectAdapter.replace(0, this.mRow);
        }
    }

    private void setupPresenter() {
        ObjectAdapter objectAdapter = this.mAdapter;
        if (objectAdapter != null && this.mRow != null && this.mPresenter != null) {
            PresenterSelector presenterSelector = objectAdapter.getPresenterSelector();
            if (presenterSelector == null) {
                ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
                classPresenterSelector.addClassPresenter(this.mRow.getClass(), this.mPresenter);
                this.mAdapter.setPresenterSelector(classPresenterSelector);
            } else if (presenterSelector instanceof ClassPresenterSelector) {
                ((ClassPresenterSelector) presenterSelector).addClassPresenter(this.mRow.getClass(), this.mPresenter);
            }
        }
    }

    public ProgressBarManager getProgressBarManager() {
        return this.mProgressBarManager;
    }
}
