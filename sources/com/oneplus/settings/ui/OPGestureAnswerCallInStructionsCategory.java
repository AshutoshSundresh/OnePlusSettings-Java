package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.utils.OPUtils;

public class OPGestureAnswerCallInStructionsCategory extends Preference {
    private int mLayoutResId = C0012R$layout.op_gesture_answer_call_instructions_category;

    public OPGestureAnswerCallInStructionsCategory(Context context) {
        super(context);
        initViews(context);
    }

    public OPGestureAnswerCallInStructionsCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPGestureAnswerCallInStructionsCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(this.mLayoutResId);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        TextView textView;
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedBelow(true);
        if (!OPUtils.supportGestureAudioRoute() && (textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.op_fingerprint_name)) != null) {
            textView.setText(C0017R$string.oneplus_gesture_of_answer_call_info);
        }
    }
}
