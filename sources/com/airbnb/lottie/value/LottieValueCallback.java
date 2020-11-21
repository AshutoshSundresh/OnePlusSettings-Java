package com.airbnb.lottie.value;

import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;

public class LottieValueCallback<T> {
    private final LottieFrameInfo<T> frameInfo = new LottieFrameInfo<>();
    protected T value = null;

    public final void setAnimation(BaseKeyframeAnimation<?, ?> baseKeyframeAnimation) {
    }

    public LottieValueCallback(T t) {
        this.value = t;
    }

    public T getValue(LottieFrameInfo<T> lottieFrameInfo) {
        return this.value;
    }

    public final T getValueInternal(float f, float f2, T t, T t2, float f3, float f4, float f5) {
        LottieFrameInfo<T> lottieFrameInfo = this.frameInfo;
        lottieFrameInfo.set(f, f2, t, t2, f3, f4, f5);
        return getValue(lottieFrameInfo);
    }
}
