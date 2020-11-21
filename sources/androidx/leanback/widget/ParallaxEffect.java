package androidx.leanback.widget;

import java.util.ArrayList;
import java.util.List;

public abstract class ParallaxEffect {
    final List<Object> mMarkerValues = new ArrayList(2);
    final List<ParallaxTarget> mTargets;

    /* access modifiers changed from: package-private */
    public abstract Number calculateDirectValue(Parallax parallax);

    /* access modifiers changed from: package-private */
    public abstract float calculateFraction(Parallax parallax);

    ParallaxEffect() {
        new ArrayList(2);
        new ArrayList(2);
        this.mTargets = new ArrayList(4);
    }

    public final void performMapping(Parallax parallax) {
        if (this.mMarkerValues.size() >= 2) {
            parallax.verifyFloatProperties();
            float f = 0.0f;
            Number number = null;
            boolean z = false;
            for (int i = 0; i < this.mTargets.size(); i++) {
                ParallaxTarget parallaxTarget = this.mTargets.get(i);
                if (parallaxTarget.isDirectMapping()) {
                    if (number == null) {
                        number = calculateDirectValue(parallax);
                    }
                    parallaxTarget.directUpdate(number);
                } else {
                    if (!z) {
                        f = calculateFraction(parallax);
                        z = true;
                    }
                    parallaxTarget.update(f);
                }
            }
        }
    }
}
