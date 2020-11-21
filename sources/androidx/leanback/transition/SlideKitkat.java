package androidx.leanback.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import androidx.leanback.R$id;
import androidx.leanback.R$styleable;

/* access modifiers changed from: package-private */
public class SlideKitkat extends Visibility {
    private static final TimeInterpolator sAccelerate = new AccelerateInterpolator();
    private static final CalculateSlide sCalculateBottom = new CalculateSlideVertical() {
        /* class androidx.leanback.transition.SlideKitkat.AnonymousClass4 */

        @Override // androidx.leanback.transition.SlideKitkat.CalculateSlide
        public float getGone(View view) {
            return view.getTranslationY() + ((float) view.getHeight());
        }
    };
    private static final CalculateSlide sCalculateEnd = new CalculateSlideHorizontal() {
        /* class androidx.leanback.transition.SlideKitkat.AnonymousClass6 */

        @Override // androidx.leanback.transition.SlideKitkat.CalculateSlide
        public float getGone(View view) {
            if (view.getLayoutDirection() == 1) {
                return view.getTranslationX() - ((float) view.getWidth());
            }
            return view.getTranslationX() + ((float) view.getWidth());
        }
    };
    private static final CalculateSlide sCalculateLeft = new CalculateSlideHorizontal() {
        /* class androidx.leanback.transition.SlideKitkat.AnonymousClass1 */

        @Override // androidx.leanback.transition.SlideKitkat.CalculateSlide
        public float getGone(View view) {
            return view.getTranslationX() - ((float) view.getWidth());
        }
    };
    private static final CalculateSlide sCalculateRight = new CalculateSlideHorizontal() {
        /* class androidx.leanback.transition.SlideKitkat.AnonymousClass3 */

        @Override // androidx.leanback.transition.SlideKitkat.CalculateSlide
        public float getGone(View view) {
            return view.getTranslationX() + ((float) view.getWidth());
        }
    };
    private static final CalculateSlide sCalculateStart = new CalculateSlideHorizontal() {
        /* class androidx.leanback.transition.SlideKitkat.AnonymousClass5 */

        @Override // androidx.leanback.transition.SlideKitkat.CalculateSlide
        public float getGone(View view) {
            if (view.getLayoutDirection() == 1) {
                return view.getTranslationX() + ((float) view.getWidth());
            }
            return view.getTranslationX() - ((float) view.getWidth());
        }
    };
    private static final CalculateSlide sCalculateTop = new CalculateSlideVertical() {
        /* class androidx.leanback.transition.SlideKitkat.AnonymousClass2 */

        @Override // androidx.leanback.transition.SlideKitkat.CalculateSlide
        public float getGone(View view) {
            return view.getTranslationY() - ((float) view.getHeight());
        }
    };
    private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();
    private CalculateSlide mSlideCalculator;

    /* access modifiers changed from: private */
    public interface CalculateSlide {
        float getGone(View view);

        float getHere(View view);

        Property<View, Float> getProperty();
    }

    private static abstract class CalculateSlideHorizontal implements CalculateSlide {
        CalculateSlideHorizontal() {
        }

        @Override // androidx.leanback.transition.SlideKitkat.CalculateSlide
        public float getHere(View view) {
            return view.getTranslationX();
        }

        @Override // androidx.leanback.transition.SlideKitkat.CalculateSlide
        public Property<View, Float> getProperty() {
            return View.TRANSLATION_X;
        }
    }

    private static abstract class CalculateSlideVertical implements CalculateSlide {
        CalculateSlideVertical() {
        }

        @Override // androidx.leanback.transition.SlideKitkat.CalculateSlide
        public float getHere(View view) {
            return view.getTranslationY();
        }

        @Override // androidx.leanback.transition.SlideKitkat.CalculateSlide
        public Property<View, Float> getProperty() {
            return View.TRANSLATION_Y;
        }
    }

    public SlideKitkat() {
        setSlideEdge(80);
    }

    public SlideKitkat(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.lbSlide);
        setSlideEdge(obtainStyledAttributes.getInt(R$styleable.lbSlide_lb_slideEdge, 80));
        long j = (long) obtainStyledAttributes.getInt(R$styleable.lbSlide_android_duration, -1);
        if (j >= 0) {
            setDuration(j);
        }
        long j2 = (long) obtainStyledAttributes.getInt(R$styleable.lbSlide_android_startDelay, -1);
        if (j2 > 0) {
            setStartDelay(j2);
        }
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.lbSlide_android_interpolator, 0);
        if (resourceId > 0) {
            setInterpolator(AnimationUtils.loadInterpolator(context, resourceId));
        }
        obtainStyledAttributes.recycle();
    }

    public void setSlideEdge(int i) {
        if (i == 3) {
            this.mSlideCalculator = sCalculateLeft;
        } else if (i == 5) {
            this.mSlideCalculator = sCalculateRight;
        } else if (i == 48) {
            this.mSlideCalculator = sCalculateTop;
        } else if (i == 80) {
            this.mSlideCalculator = sCalculateBottom;
        } else if (i == 8388611) {
            this.mSlideCalculator = sCalculateStart;
        } else if (i == 8388613) {
            this.mSlideCalculator = sCalculateEnd;
        } else {
            throw new IllegalArgumentException("Invalid slide direction");
        }
    }

    private Animator createAnimation(View view, Property<View, Float> property, float f, float f2, float f3, TimeInterpolator timeInterpolator, int i) {
        float[] fArr = (float[]) view.getTag(R$id.lb_slide_transition_value);
        if (fArr != null) {
            f = View.TRANSLATION_Y == property ? fArr[1] : fArr[0];
            view.setTag(R$id.lb_slide_transition_value, null);
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, property, f, f2);
        SlideAnimatorListener slideAnimatorListener = new SlideAnimatorListener(view, property, f3, f2, i);
        ofFloat.addListener(slideAnimatorListener);
        ofFloat.addPauseListener(slideAnimatorListener);
        ofFloat.setInterpolator(timeInterpolator);
        return ofFloat;
    }

    public Animator onAppear(ViewGroup viewGroup, TransitionValues transitionValues, int i, TransitionValues transitionValues2, int i2) {
        View view = transitionValues2 != null ? transitionValues2.view : null;
        if (view == null) {
            return null;
        }
        float here = this.mSlideCalculator.getHere(view);
        return createAnimation(view, this.mSlideCalculator.getProperty(), this.mSlideCalculator.getGone(view), here, here, sDecelerate, 0);
    }

    public Animator onDisappear(ViewGroup viewGroup, TransitionValues transitionValues, int i, TransitionValues transitionValues2, int i2) {
        View view = transitionValues != null ? transitionValues.view : null;
        if (view == null) {
            return null;
        }
        float here = this.mSlideCalculator.getHere(view);
        return createAnimation(view, this.mSlideCalculator.getProperty(), here, this.mSlideCalculator.getGone(view), here, sAccelerate, 4);
    }

    /* access modifiers changed from: private */
    public static class SlideAnimatorListener extends AnimatorListenerAdapter {
        private boolean mCanceled = false;
        private final float mEndValue;
        private final int mFinalVisibility;
        private float mPausedValue;
        private final Property<View, Float> mProp;
        private final float mTerminalValue;
        private final View mView;

        public SlideAnimatorListener(View view, Property<View, Float> property, float f, float f2, int i) {
            this.mProp = property;
            this.mView = view;
            this.mTerminalValue = f;
            this.mEndValue = f2;
            this.mFinalVisibility = i;
            view.setVisibility(0);
        }

        public void onAnimationCancel(Animator animator) {
            this.mView.setTag(R$id.lb_slide_transition_value, new float[]{this.mView.getTranslationX(), this.mView.getTranslationY()});
            this.mProp.set(this.mView, Float.valueOf(this.mTerminalValue));
            this.mCanceled = true;
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.mCanceled) {
                this.mProp.set(this.mView, Float.valueOf(this.mTerminalValue));
            }
            this.mView.setVisibility(this.mFinalVisibility);
        }

        public void onAnimationPause(Animator animator) {
            this.mPausedValue = this.mProp.get(this.mView).floatValue();
            this.mProp.set(this.mView, Float.valueOf(this.mEndValue));
            this.mView.setVisibility(this.mFinalVisibility);
        }

        public void onAnimationResume(Animator animator) {
            this.mProp.set(this.mView, Float.valueOf(this.mPausedValue));
            this.mView.setVisibility(0);
        }
    }
}
