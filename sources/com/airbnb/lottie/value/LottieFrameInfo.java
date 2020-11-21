package com.airbnb.lottie.value;

public class LottieFrameInfo<T> {
    private T endValue;
    private T startValue;

    public LottieFrameInfo<T> set(float f, float f2, T t, T t2, float f3, float f4, float f5) {
        this.startValue = t;
        this.endValue = t2;
        return this;
    }
}
