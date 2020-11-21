package com.airbnb.lottie.animation.keyframe;

import com.airbnb.lottie.utils.GammaEvaluator;
import com.airbnb.lottie.utils.MiscUtils;
import com.airbnb.lottie.value.Keyframe;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.List;

public class ColorKeyframeAnimation extends KeyframeAnimation<Integer> {
    public ColorKeyframeAnimation(List<Keyframe<Integer>> list) {
        super(list);
    }

    /* access modifiers changed from: package-private */
    @Override // com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation
    public Integer getValue(Keyframe<Integer> keyframe, float f) {
        return Integer.valueOf(getIntValue(keyframe, f));
    }

    public int getIntValue(Keyframe<Integer> keyframe, float f) {
        A valueInternal;
        T t = keyframe.startValue;
        if (t == null || keyframe.endValue == null) {
            throw new IllegalStateException("Missing values for keyframe.");
        }
        int intValue = t.intValue();
        int intValue2 = keyframe.endValue.intValue();
        LottieValueCallback<A> lottieValueCallback = this.valueCallback;
        if (lottieValueCallback == null || (valueInternal = lottieValueCallback.getValueInternal(keyframe.startFrame, keyframe.endFrame.floatValue(), (A) Integer.valueOf(intValue), (A) Integer.valueOf(intValue2), f, getLinearCurrentKeyframeProgress(), getProgress())) == null) {
            return GammaEvaluator.evaluate(MiscUtils.clamp(f, 0.0f, 1.0f), intValue, intValue2);
        }
        return valueInternal.intValue();
    }

    public int getIntValue() {
        return getIntValue(getCurrentKeyframe(), getInterpolatedCurrentKeyframeProgress());
    }
}
