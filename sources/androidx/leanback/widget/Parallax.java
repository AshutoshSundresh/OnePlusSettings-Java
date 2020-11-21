package androidx.leanback.widget;

import android.util.Property;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Parallax<PropertyT extends Property> {
    private final List<ParallaxEffect> mEffects = new ArrayList(4);
    private float[] mFloatValues = new float[4];
    final List<PropertyT> mProperties;
    private int[] mValues = new int[4];

    public Parallax() {
        ArrayList arrayList = new ArrayList();
        this.mProperties = arrayList;
        Collections.unmodifiableList(arrayList);
    }

    /* access modifiers changed from: package-private */
    public final void verifyFloatProperties() throws IllegalStateException {
        if (this.mProperties.size() >= 2) {
            float floatPropertyValue = getFloatPropertyValue(0);
            int i = 1;
            while (i < this.mProperties.size()) {
                float floatPropertyValue2 = getFloatPropertyValue(i);
                if (floatPropertyValue2 < floatPropertyValue) {
                    int i2 = i - 1;
                    throw new IllegalStateException(String.format("Parallax Property[%d]\"%s\" is smaller than Property[%d]\"%s\"", Integer.valueOf(i), this.mProperties.get(i).getName(), Integer.valueOf(i2), this.mProperties.get(i2).getName()));
                } else if (floatPropertyValue == -3.4028235E38f && floatPropertyValue2 == Float.MAX_VALUE) {
                    int i3 = i - 1;
                    throw new IllegalStateException(String.format("Parallax Property[%d]\"%s\" is UNKNOWN_BEFORE and Property[%d]\"%s\" is UNKNOWN_AFTER", Integer.valueOf(i3), this.mProperties.get(i3).getName(), Integer.valueOf(i), this.mProperties.get(i).getName()));
                } else {
                    i++;
                    floatPropertyValue = floatPropertyValue2;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final float getFloatPropertyValue(int i) {
        return this.mFloatValues[i];
    }

    public void updateValues() {
        for (int i = 0; i < this.mEffects.size(); i++) {
            this.mEffects.get(i).performMapping(this);
        }
    }
}
