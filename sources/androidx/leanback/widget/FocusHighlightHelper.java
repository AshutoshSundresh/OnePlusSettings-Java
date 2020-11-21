package androidx.leanback.widget;

import android.animation.TimeAnimator;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.leanback.R$dimen;
import androidx.leanback.R$id;
import androidx.leanback.graphics.ColorOverlayDimmer;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.RowHeaderPresenter;
import androidx.recyclerview.widget.RecyclerView;

public class FocusHighlightHelper {

    /* access modifiers changed from: package-private */
    public static class FocusAnimator implements TimeAnimator.TimeListener {
        private final TimeAnimator mAnimator = new TimeAnimator();
        private final ColorOverlayDimmer mDimmer;
        private final int mDuration;
        private float mFocusLevel = 0.0f;
        private float mFocusLevelDelta;
        private float mFocusLevelStart;
        private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
        private final float mScaleDiff;
        private final View mView;
        private final ShadowOverlayContainer mWrapper;

        /* access modifiers changed from: package-private */
        public void animateFocus(boolean z, boolean z2) {
            endAnimation();
            float f = z ? 1.0f : 0.0f;
            if (z2) {
                setFocusLevel(f);
                return;
            }
            float f2 = this.mFocusLevel;
            if (f2 != f) {
                this.mFocusLevelStart = f2;
                this.mFocusLevelDelta = f - f2;
                this.mAnimator.start();
            }
        }

        FocusAnimator(View view, float f, boolean z, int i) {
            this.mView = view;
            this.mDuration = i;
            this.mScaleDiff = f - 1.0f;
            if (view instanceof ShadowOverlayContainer) {
                this.mWrapper = (ShadowOverlayContainer) view;
            } else {
                this.mWrapper = null;
            }
            this.mAnimator.setTimeListener(this);
            if (z) {
                this.mDimmer = ColorOverlayDimmer.createDefault(view.getContext());
            } else {
                this.mDimmer = null;
            }
        }

        /* access modifiers changed from: package-private */
        public void setFocusLevel(float f) {
            this.mFocusLevel = f;
            float f2 = (this.mScaleDiff * f) + 1.0f;
            this.mView.setScaleX(f2);
            this.mView.setScaleY(f2);
            ShadowOverlayContainer shadowOverlayContainer = this.mWrapper;
            if (shadowOverlayContainer != null) {
                shadowOverlayContainer.setShadowFocusLevel(f);
            } else {
                ShadowOverlayHelper.setNoneWrapperShadowFocusLevel(this.mView, f);
            }
            ColorOverlayDimmer colorOverlayDimmer = this.mDimmer;
            if (colorOverlayDimmer != null) {
                colorOverlayDimmer.setActiveLevel(f);
                int color = this.mDimmer.getPaint().getColor();
                ShadowOverlayContainer shadowOverlayContainer2 = this.mWrapper;
                if (shadowOverlayContainer2 != null) {
                    shadowOverlayContainer2.setOverlayColor(color);
                } else {
                    ShadowOverlayHelper.setNoneWrapperOverlayColor(this.mView, color);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void endAnimation() {
            this.mAnimator.end();
        }

        public void onTimeUpdate(TimeAnimator timeAnimator, long j, long j2) {
            float f;
            int i = this.mDuration;
            if (j >= ((long) i)) {
                f = 1.0f;
                this.mAnimator.end();
            } else {
                f = (float) (((double) j) / ((double) i));
            }
            Interpolator interpolator = this.mInterpolator;
            if (interpolator != null) {
                f = interpolator.getInterpolation(f);
            }
            setFocusLevel(this.mFocusLevelStart + (f * this.mFocusLevelDelta));
        }
    }

    public static void setupHeaderItemFocusHighlight(ItemBridgeAdapter itemBridgeAdapter) {
        setupHeaderItemFocusHighlight(itemBridgeAdapter, true);
    }

    public static void setupHeaderItemFocusHighlight(ItemBridgeAdapter itemBridgeAdapter, boolean z) {
        itemBridgeAdapter.setFocusHighlight(z ? new HeaderItemFocusHighlight() : null);
    }

    /* access modifiers changed from: package-private */
    public static class HeaderItemFocusHighlight implements FocusHighlightHandler {
        private int mDuration;
        private boolean mInitialized;
        private float mSelectScale;

        @Override // androidx.leanback.widget.FocusHighlightHandler
        public void onInitializeView(View view) {
        }

        HeaderItemFocusHighlight() {
        }

        /* access modifiers changed from: package-private */
        public void lazyInit(View view) {
            if (!this.mInitialized) {
                Resources resources = view.getResources();
                TypedValue typedValue = new TypedValue();
                resources.getValue(R$dimen.lb_browse_header_select_scale, typedValue, true);
                this.mSelectScale = typedValue.getFloat();
                resources.getValue(R$dimen.lb_browse_header_select_duration, typedValue, true);
                this.mDuration = typedValue.data;
                this.mInitialized = true;
            }
        }

        /* access modifiers changed from: package-private */
        public static class HeaderFocusAnimator extends FocusAnimator {
            ItemBridgeAdapter.ViewHolder mViewHolder;

            HeaderFocusAnimator(View view, float f, int i) {
                super(view, f, false, i);
                ViewParent parent = view.getParent();
                while (parent != null && !(parent instanceof RecyclerView)) {
                    parent = parent.getParent();
                }
                if (parent != null) {
                    this.mViewHolder = (ItemBridgeAdapter.ViewHolder) ((RecyclerView) parent).getChildViewHolder(view);
                }
            }

            /* access modifiers changed from: package-private */
            @Override // androidx.leanback.widget.FocusHighlightHelper.FocusAnimator
            public void setFocusLevel(float f) {
                Presenter presenter = this.mViewHolder.getPresenter();
                if (presenter instanceof RowHeaderPresenter) {
                    ((RowHeaderPresenter) presenter).setSelectLevel((RowHeaderPresenter.ViewHolder) this.mViewHolder.getViewHolder(), f);
                }
                super.setFocusLevel(f);
            }
        }

        private void viewFocused(View view, boolean z) {
            lazyInit(view);
            view.setSelected(z);
            FocusAnimator focusAnimator = (FocusAnimator) view.getTag(R$id.lb_focus_animator);
            if (focusAnimator == null) {
                focusAnimator = new HeaderFocusAnimator(view, this.mSelectScale, this.mDuration);
                view.setTag(R$id.lb_focus_animator, focusAnimator);
            }
            focusAnimator.animateFocus(z, false);
        }

        @Override // androidx.leanback.widget.FocusHighlightHandler
        public void onItemFocused(View view, boolean z) {
            viewFocused(view, z);
        }
    }
}
