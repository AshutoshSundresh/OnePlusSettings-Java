package com.oneplus.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatSeekBar;
import com.android.settings.C0008R$drawable;
import com.oneplus.settings.utils.OPUtils;

public class OPSeekBar2 extends AppCompatSeekBar {
    public OPSeekBar2(Context context) {
        super(context);
        init();
    }

    public OPSeekBar2(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public OPSeekBar2(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        if (OPUtils.isBlackModeOn(getContext().getContentResolver())) {
            setProgressDrawable(getResources().getDrawable(C0008R$drawable.op_seekbar_track_dark, getContext().getTheme()));
            setThumb(getResources().getDrawable(C0008R$drawable.op_seekbar_thumb_dark, getContext().getTheme()));
            return;
        }
        setProgressDrawable(getResources().getDrawable(C0008R$drawable.op_seekbar_track_light, getContext().getTheme()));
        setThumb(getResources().getDrawable(C0008R$drawable.op_seekbar_thumb_light, getContext().getTheme()));
    }
}
