package com.airbnb.lottie.animation.keyframe;

import com.airbnb.lottie.utils.MiscUtils;
import com.airbnb.lottie.value.Keyframe;
import com.airbnb.lottie.value.LottieValueCallback;
import com.airbnb.lottie.value.ScaleXY;
import java.util.List;

public class ScaleKeyframeAnimation extends KeyframeAnimation<ScaleXY> {
    private final ScaleXY scaleXY = new ScaleXY();

    public ScaleKeyframeAnimation(List<Keyframe<ScaleXY>> list) {
        super(list);
    }

    @Override // com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation
    public ScaleXY getValue(Keyframe<ScaleXY> keyframe, float f) {
        T t;
        A valueInternal;
        T t2 = keyframe.startValue;
        if (t2 == null || (t = keyframe.endValue) == null) {
            throw new IllegalStateException("Missing values for keyframe.");
        }
        T t3 = t2;
        T t4 = t;
        LottieValueCallback<A> lottieValueCallback = this.valueCallback;
        if (lottieValueCallback != null && (valueInternal = lottieValueCallback.getValueInternal(keyframe.startFrame, keyframe.endFrame.floatValue(), t3, t4, f, getLinearCurrentKeyframeProgress(), getProgress())) != null) {
            return valueInternal;
        }
        this.scaleXY.set(MiscUtils.lerp(t3.getScaleX(), t4.getScaleX(), f), MiscUtils.lerp(t3.getScaleY(), t4.getScaleY(), f));
        return this.scaleXY;
    }
}
