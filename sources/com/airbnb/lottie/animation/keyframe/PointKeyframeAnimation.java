package com.airbnb.lottie.animation.keyframe;

import android.graphics.PointF;
import com.airbnb.lottie.value.Keyframe;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.List;

public class PointKeyframeAnimation extends KeyframeAnimation<PointF> {
    private final PointF point = new PointF();

    public PointKeyframeAnimation(List<Keyframe<PointF>> list) {
        super(list);
    }

    @Override // com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation
    public PointF getValue(Keyframe<PointF> keyframe, float f) {
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
        PointF pointF = this.point;
        float f2 = ((PointF) t3).x;
        float f3 = ((PointF) t3).y;
        pointF.set(f2 + ((((PointF) t4).x - f2) * f), f3 + (f * (((PointF) t4).y - f3)));
        return this.point;
    }
}
