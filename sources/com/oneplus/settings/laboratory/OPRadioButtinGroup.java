package com.oneplus.settings.laboratory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class OPRadioButtinGroup extends LinearLayout implements View.OnClickListener {
    private Context mContext;
    public OnRadioGroupClickListener mOnRadioGroupClickListener;

    public interface OnRadioGroupClickListener {
        void onRadioGroupClick(int i);
    }

    public OPRadioButtinGroup(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mContext = context;
    }

    public OPRadioButtinGroup(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    public OPRadioButtinGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public OPRadioButtinGroup(Context context) {
        super(context);
        this.mContext = context;
    }

    public void addChild(int i, String[] strArr) {
        for (int i2 = 0; i2 < i; i2++) {
            View inflate = LayoutInflater.from(this.mContext).inflate(C0012R$layout.op_radio_button_item, (ViewGroup) null);
            ((TextView) inflate.findViewById(C0010R$id.title)).setText(strArr[i2]);
            inflate.setId(i2);
            inflate.setOnClickListener(this);
            addView(inflate, i2);
        }
    }

    public void onClick(View view) {
        setSelect(view);
        OnRadioGroupClickListener onRadioGroupClickListener = this.mOnRadioGroupClickListener;
        if (onRadioGroupClickListener != null) {
            onRadioGroupClickListener.onRadioGroupClick(view.getId());
        }
    }

    public void setSelect(View view) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioButton radioButton = (RadioButton) getChildAt(i).findViewById(C0010R$id.op_lab_feature_radio_button);
            if (view.getId() == i) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
        }
    }

    public void setSelect(int i) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            RadioButton radioButton = (RadioButton) getChildAt(i2).findViewById(C0010R$id.op_lab_feature_radio_button);
            if (i == i2) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
        }
    }

    public void setOnRadioGroupClickListener(OnRadioGroupClickListener onRadioGroupClickListener) {
        this.mOnRadioGroupClickListener = onRadioGroupClickListener;
    }
}
