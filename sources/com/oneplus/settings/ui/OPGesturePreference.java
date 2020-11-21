package com.oneplus.settings.ui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.oneplus.settings.gestures.OPGestureUtils;

public class OPGesturePreference extends Preference implements Preference.OnPreferenceClickListener {
    private Context mContext;

    public OPGesturePreference(Context context) {
        super(context);
        init(context);
    }

    public OPGesturePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public OPGesturePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setOnPreferenceClickListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        Intent intent = new Intent("oneplus.intent.action.ONEPLUS_GESTURE_APP_LIST_ACTION");
        intent.putExtra("op_gesture_key", OPGestureUtils.getGestureTypebyGestureKey(preference.getKey()));
        intent.putExtra("op_gesture_action", preference.getTitle());
        this.mContext.startActivity(intent);
        return false;
    }
}
