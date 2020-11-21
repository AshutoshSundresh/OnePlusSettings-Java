package com.android.settings.display;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.R$attr;
import com.oneplus.settings.utils.OPDisplayDensityUtils;

public class ScreenZoomPreference extends Preference {
    public ScreenZoomPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.preferenceStyle, 16842894));
        OPDisplayDensityUtils oPDisplayDensityUtils = new OPDisplayDensityUtils(context);
        if (oPDisplayDensityUtils.getCurrentIndex() < 0) {
            setVisible(false);
            setEnabled(false);
        } else if (TextUtils.isEmpty(getSummary())) {
            setSummary(oPDisplayDensityUtils.getEntries()[oPDisplayDensityUtils.getCurrentIndex()]);
        }
    }
}
