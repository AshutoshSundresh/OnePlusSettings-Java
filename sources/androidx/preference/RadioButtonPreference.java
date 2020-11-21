package androidx.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import java.lang.reflect.Field;

public class RadioButtonPreference extends CheckBoxPreference {
    public RadioButtonPreference(Context context) {
        this(context, null);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.radioButtonPreferenceStyle);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R$style.Preference_Material_RadioButtonPreference);
    }

    public RadioButtonPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, resolveDefStyleAttr(context, i), i2);
        setCanRecycleLayout(true);
    }

    private void setCanRecycleLayout(boolean z) {
        try {
            Field declaredField = Preference.class.getDeclaredField("mCanRecycleLayout");
            declaredField.setAccessible(true);
            declaredField.setBoolean(this, z);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }

    static int resolveDefStyleAttr(Context context, int i) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(i, typedValue, true);
        if ((typedValue.resourceId >>> 24) == 1) {
            return 0;
        }
        return i;
    }
}
