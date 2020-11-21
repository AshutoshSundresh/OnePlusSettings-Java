package androidx.appcompat.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import androidx.animation.AnimatorUtils;
import androidx.appcompat.R$styleable;

@SuppressLint({"NewApi"})
public class EpicenterTranslateClipReveal extends Visibility {
    private final TimeInterpolator mInterpolatorX;
    private final TimeInterpolator mInterpolatorY;
    private final TimeInterpolator mInterpolatorZ;

    public EpicenterTranslateClipReveal() {
        this.mInterpolatorX = null;
        this.mInterpolatorY = null;
        this.mInterpolatorZ = null;
    }

    public EpicenterTranslateClipReveal(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.EpicenterTranslateClipReveal, 0, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.EpicenterTranslateClipReveal_interpolatorX, 0);
        if (resourceId != 0) {
            this.mInterpolatorX = AnimationUtils.loadInterpolator(context, resourceId);
        } else {
            this.mInterpolatorX = AnimatorUtils.LinearOutSlowInInterpolator;
        }
        int resourceId2 = obtainStyledAttributes.getResourceId(R$styleable.EpicenterTranslateClipReveal_interpolatorY, 0);
        if (resourceId2 != 0) {
            this.mInterpolatorY = AnimationUtils.loadInterpolator(context, resourceId2);
        } else {
            this.mInterpolatorY = AnimatorUtils.LinearOutSlowInInterpolator;
        }
        int resourceId3 = obtainStyledAttributes.getResourceId(R$styleable.EpicenterTranslateClipReveal_interpolatorZ, 0);
        if (resourceId3 != 0) {
            this.mInterpolatorZ = AnimationUtils.loadInterpolator(context, resourceId3);
        } else {
            this.mInterpolatorZ = AnimatorUtils.LinearOutSlowInInterpolator;
        }
        obtainStyledAttributes.recycle();
    }

    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        if (view.getVisibility() != 8) {
            transitionValues.values.put("android:epicenterReveal:bounds", new Rect(0, 0, view.getWidth(), view.getHeight()));
            transitionValues.values.put("android:epicenterReveal:translateX", Float.valueOf(view.getTranslationX()));
            transitionValues.values.put("android:epicenterReveal:translateY", Float.valueOf(view.getTranslationY()));
            transitionValues.values.put("android:epicenterReveal:translateZ", Float.valueOf(view.getTranslationZ()));
            transitionValues.values.put("android:epicenterReveal:z", Float.valueOf(view.getZ()));
            transitionValues.values.put("android:epicenterReveal:clip", view.getClipBounds());
        }
    }

    public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
        if (transitionValues2 == null) {
            return null;
        }
        Rect rect = (Rect) transitionValues2.values.get("android:epicenterReveal:bounds");
        Rect epicenterOrCenter = getEpicenterOrCenter(rect);
        float centerX = (float) (epicenterOrCenter.centerX() - rect.centerX());
        float centerY = (float) (epicenterOrCenter.centerY() - rect.centerY());
        float floatValue = 0.0f - ((Float) transitionValues2.values.get("android:epicenterReveal:z")).floatValue();
        view.setTranslationX(centerX);
        view.setTranslationY(centerY);
        view.setTranslationZ(floatValue);
        float floatValue2 = ((Float) transitionValues2.values.get("android:epicenterReveal:translateX")).floatValue();
        float floatValue3 = ((Float) transitionValues2.values.get("android:epicenterReveal:translateY")).floatValue();
        float floatValue4 = ((Float) transitionValues2.values.get("android:epicenterReveal:translateZ")).floatValue();
        Rect bestRect = getBestRect(transitionValues2);
        Rect epicenterOrCenter2 = getEpicenterOrCenter(bestRect);
        view.setClipBounds(epicenterOrCenter2);
        return createRectAnimator(view, new State(epicenterOrCenter2.left, epicenterOrCenter2.right, centerX), new State(epicenterOrCenter2.top, epicenterOrCenter2.bottom, centerY), floatValue, new State(bestRect.left, bestRect.right, floatValue2), new State(bestRect.top, bestRect.bottom, floatValue3), floatValue4, transitionValues2, this.mInterpolatorX, this.mInterpolatorY, this.mInterpolatorZ);
    }

    public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
        if (transitionValues == null) {
            return null;
        }
        Rect rect = (Rect) transitionValues2.values.get("android:epicenterReveal:bounds");
        Rect epicenterOrCenter = getEpicenterOrCenter(rect);
        float centerY = (float) (epicenterOrCenter.centerY() - rect.centerY());
        float floatValue = 0.0f - ((Float) transitionValues.values.get("android:epicenterReveal:z")).floatValue();
        float floatValue2 = ((Float) transitionValues2.values.get("android:epicenterReveal:translateX")).floatValue();
        float floatValue3 = ((Float) transitionValues2.values.get("android:epicenterReveal:translateY")).floatValue();
        float floatValue4 = ((Float) transitionValues2.values.get("android:epicenterReveal:translateZ")).floatValue();
        Rect bestRect = getBestRect(transitionValues);
        Rect epicenterOrCenter2 = getEpicenterOrCenter(bestRect);
        view.setClipBounds(bestRect);
        return createRectAnimator(view, new State(bestRect.left, bestRect.right, floatValue2), new State(bestRect.top, bestRect.bottom, floatValue3), floatValue4, new State(epicenterOrCenter2.left, epicenterOrCenter2.right, (float) (epicenterOrCenter.centerX() - rect.centerX())), new State(epicenterOrCenter2.top, epicenterOrCenter2.bottom, centerY), floatValue, transitionValues2, this.mInterpolatorX, this.mInterpolatorY, this.mInterpolatorZ);
    }

    private Rect getEpicenterOrCenter(Rect rect) {
        Rect epicenter = getEpicenter();
        if (epicenter != null) {
            return epicenter;
        }
        int centerX = rect.centerX();
        int centerY = rect.centerY();
        return new Rect(centerX, centerY, centerX, centerY);
    }

    private Rect getBestRect(TransitionValues transitionValues) {
        Rect rect = (Rect) transitionValues.values.get("android:epicenterReveal:clip");
        return rect == null ? (Rect) transitionValues.values.get("android:epicenterReveal:bounds") : rect;
    }

    private static Animator createRectAnimator(final View view, State state, State state2, float f, State state3, State state4, float f2, TransitionValues transitionValues, TimeInterpolator timeInterpolator, TimeInterpolator timeInterpolator2, TimeInterpolator timeInterpolator3) {
        StateEvaluator stateEvaluator = new StateEvaluator();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, f, f2);
        if (timeInterpolator3 != null) {
            ofFloat.setInterpolator(timeInterpolator3);
        }
        ObjectAnimator ofObject = ObjectAnimator.ofObject(view, new StateProperty('x'), stateEvaluator, state, state3);
        if (timeInterpolator != null) {
            ofObject.setInterpolator(timeInterpolator);
        }
        ObjectAnimator ofObject2 = ObjectAnimator.ofObject(view, new StateProperty('y'), stateEvaluator, state2, state4);
        if (timeInterpolator2 != null) {
            ofObject2.setInterpolator(timeInterpolator2);
        }
        final Rect rect = (Rect) transitionValues.values.get("android:epicenterReveal:clip");
        AnonymousClass1 r10 = new AnimatorListenerAdapter() {
            /* class androidx.appcompat.widget.EpicenterTranslateClipReveal.AnonymousClass1 */

            public void onAnimationEnd(Animator animator) {
                view.setClipBounds(rect);
            }
        };
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ofObject, ofObject2, ofFloat);
        animatorSet.addListener(r10);
        return animatorSet;
    }

    /* access modifiers changed from: private */
    public static class State {
        int lower;
        float trans;
        int upper;

        public State() {
        }

        public State(int i, int i2, float f) {
            this.lower = i;
            this.upper = i2;
            this.trans = f;
        }
    }

    /* access modifiers changed from: private */
    public static class StateEvaluator implements TypeEvaluator<State> {
        private final State mTemp;

        private StateEvaluator() {
            this.mTemp = new State();
        }

        public State evaluate(float f, State state, State state2) {
            State state3 = this.mTemp;
            int i = state.upper;
            state3.upper = i + ((int) (((float) (state2.upper - i)) * f));
            int i2 = state.lower;
            state3.lower = i2 + ((int) (((float) (state2.lower - i2)) * f));
            float f2 = state.trans;
            state3.trans = f2 + ((float) ((int) ((state2.trans - f2) * f)));
            return state3;
        }
    }

    /* access modifiers changed from: private */
    public static class StateProperty extends Property<View, State> {
        private final int mTargetDimension;
        private final Rect mTempRect = new Rect();
        private final State mTempState = new State();

        public StateProperty(char c) {
            super(State.class, "state_" + c);
            this.mTargetDimension = c;
        }

        public State get(View view) {
            Rect rect = this.mTempRect;
            if (Build.VERSION.SDK_INT >= 23 && !view.getClipBounds(rect)) {
                rect.setEmpty();
            }
            State state = this.mTempState;
            if (this.mTargetDimension == 120) {
                float translationX = view.getTranslationX();
                state.trans = translationX;
                state.lower = rect.left + ((int) translationX);
                state.upper = rect.right + ((int) translationX);
            } else {
                float translationY = view.getTranslationY();
                state.trans = translationY;
                state.lower = rect.top + ((int) translationY);
                state.upper = rect.bottom + ((int) translationY);
            }
            return state;
        }

        public void set(View view, State state) {
            Rect rect = this.mTempRect;
            if (Build.VERSION.SDK_INT >= 23 && view.getClipBounds(rect)) {
                if (this.mTargetDimension == 120) {
                    int i = state.lower;
                    float f = state.trans;
                    rect.left = i - ((int) f);
                    rect.right = state.upper - ((int) f);
                } else {
                    int i2 = state.lower;
                    float f2 = state.trans;
                    rect.top = i2 - ((int) f2);
                    rect.bottom = state.upper - ((int) f2);
                }
                view.setClipBounds(rect);
            }
            if (this.mTargetDimension == 120) {
                view.setTranslationX(state.trans);
            } else {
                view.setTranslationY(state.trans);
            }
        }
    }
}
