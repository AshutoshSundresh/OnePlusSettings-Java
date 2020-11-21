package androidx.mediarouter.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* access modifiers changed from: package-private */
public final class OverlayListView extends ListView {
    private final List<OverlayObject> mOverlayObjects = new ArrayList();

    public OverlayListView(Context context) {
        super(context);
    }

    public OverlayListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public OverlayListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void addOverlayObject(OverlayObject overlayObject) {
        this.mOverlayObjects.add(overlayObject);
    }

    public void startAnimationAll() {
        for (OverlayObject overlayObject : this.mOverlayObjects) {
            if (!overlayObject.isAnimationStarted()) {
                overlayObject.startAnimation(getDrawingTime());
            }
        }
    }

    public void stopAnimationAll() {
        for (OverlayObject overlayObject : this.mOverlayObjects) {
            overlayObject.stopAnimation();
        }
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mOverlayObjects.size() > 0) {
            Iterator<OverlayObject> it = this.mOverlayObjects.iterator();
            while (it.hasNext()) {
                OverlayObject next = it.next();
                BitmapDrawable bitmapDrawable = next.getBitmapDrawable();
                if (bitmapDrawable != null) {
                    bitmapDrawable.draw(canvas);
                }
                if (!next.update(getDrawingTime())) {
                    it.remove();
                }
            }
        }
    }

    public static class OverlayObject {
        private BitmapDrawable mBitmap;
        private float mCurrentAlpha = 1.0f;
        private Rect mCurrentBounds;
        private int mDeltaY;
        private long mDuration;
        private float mEndAlpha = 1.0f;
        private Interpolator mInterpolator;
        private boolean mIsAnimationEnded;
        private boolean mIsAnimationStarted;
        private OnAnimationEndListener mListener;
        private float mStartAlpha = 1.0f;
        private Rect mStartRect;
        private long mStartTime;

        public interface OnAnimationEndListener {
            void onAnimationEnd();
        }

        public OverlayObject(BitmapDrawable bitmapDrawable, Rect rect) {
            this.mBitmap = bitmapDrawable;
            this.mStartRect = rect;
            Rect rect2 = new Rect(rect);
            this.mCurrentBounds = rect2;
            BitmapDrawable bitmapDrawable2 = this.mBitmap;
            if (bitmapDrawable2 != null && rect2 != null) {
                bitmapDrawable2.setAlpha((int) (this.mCurrentAlpha * 255.0f));
                this.mBitmap.setBounds(this.mCurrentBounds);
            }
        }

        public BitmapDrawable getBitmapDrawable() {
            return this.mBitmap;
        }

        public boolean isAnimationStarted() {
            return this.mIsAnimationStarted;
        }

        public OverlayObject setAlphaAnimation(float f, float f2) {
            this.mStartAlpha = f;
            this.mEndAlpha = f2;
            return this;
        }

        public OverlayObject setTranslateYAnimation(int i) {
            this.mDeltaY = i;
            return this;
        }

        public OverlayObject setDuration(long j) {
            this.mDuration = j;
            return this;
        }

        public OverlayObject setInterpolator(Interpolator interpolator) {
            this.mInterpolator = interpolator;
            return this;
        }

        public OverlayObject setAnimationEndListener(OnAnimationEndListener onAnimationEndListener) {
            this.mListener = onAnimationEndListener;
            return this;
        }

        public void startAnimation(long j) {
            this.mStartTime = j;
            this.mIsAnimationStarted = true;
        }

        public void stopAnimation() {
            this.mIsAnimationStarted = true;
            this.mIsAnimationEnded = true;
            OnAnimationEndListener onAnimationEndListener = this.mListener;
            if (onAnimationEndListener != null) {
                onAnimationEndListener.onAnimationEnd();
            }
        }

        public boolean update(long j) {
            float f;
            if (this.mIsAnimationEnded) {
                return false;
            }
            float f2 = 0.0f;
            float max = Math.max(0.0f, Math.min(1.0f, ((float) (j - this.mStartTime)) / ((float) this.mDuration)));
            if (this.mIsAnimationStarted) {
                f2 = max;
            }
            Interpolator interpolator = this.mInterpolator;
            if (interpolator == null) {
                f = f2;
            } else {
                f = interpolator.getInterpolation(f2);
            }
            int i = (int) (((float) this.mDeltaY) * f);
            Rect rect = this.mCurrentBounds;
            Rect rect2 = this.mStartRect;
            rect.top = rect2.top + i;
            rect.bottom = rect2.bottom + i;
            float f3 = this.mStartAlpha;
            float f4 = f3 + ((this.mEndAlpha - f3) * f);
            this.mCurrentAlpha = f4;
            BitmapDrawable bitmapDrawable = this.mBitmap;
            if (!(bitmapDrawable == null || rect == null)) {
                bitmapDrawable.setAlpha((int) (f4 * 255.0f));
                this.mBitmap.setBounds(this.mCurrentBounds);
            }
            if (this.mIsAnimationStarted && f2 >= 1.0f) {
                this.mIsAnimationEnded = true;
                OnAnimationEndListener onAnimationEndListener = this.mListener;
                if (onAnimationEndListener != null) {
                    onAnimationEndListener.onAnimationEnd();
                }
            }
            return !this.mIsAnimationEnded;
        }
    }
}
