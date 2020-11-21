package androidx.preference;

import android.content.Context;
import android.util.AttributeSet;

public class PreferenceTopBlank extends PreferenceCategory {
    public PreferenceTopBlank(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public PreferenceTopBlank(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(R$layout.ctrl_preference_blank);
    }
}
