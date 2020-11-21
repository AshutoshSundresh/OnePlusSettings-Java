package com.android.settings.localepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.settings.C0010R$id;

class LocaleDragCell extends RelativeLayout {
    private CheckBox mCheckbox;
    private ImageView mDragHandle;
    private TextView mLabel;
    private TextView mLocalized;
    private TextView mMiniLabel;

    public LocaleDragCell(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mLabel = (TextView) findViewById(C0010R$id.label);
        this.mLocalized = (TextView) findViewById(C0010R$id.l10nWarn);
        this.mMiniLabel = (TextView) findViewById(C0010R$id.miniLabel);
        this.mCheckbox = (CheckBox) findViewById(C0010R$id.checkbox);
        this.mDragHandle = (ImageView) findViewById(C0010R$id.dragHandle);
    }

    public void setShowHandle(boolean z) {
        this.mDragHandle.setVisibility(z ? 0 : 4);
        invalidate();
        requestLayout();
    }

    public void setShowCheckbox(boolean z) {
        if (z) {
            this.mCheckbox.setVisibility(0);
            this.mLabel.setVisibility(4);
        } else {
            this.mCheckbox.setVisibility(4);
            this.mLabel.setVisibility(0);
        }
        invalidate();
        requestLayout();
    }

    public void setShowMiniLabel(boolean z) {
        this.mMiniLabel.setVisibility(z ? 0 : 8);
        invalidate();
        requestLayout();
    }

    public void setMiniLabel(String str) {
        this.mMiniLabel.setText(str);
        invalidate();
    }

    public void setLabelAndDescription(String str, String str2) {
        this.mLabel.setText(str);
        this.mCheckbox.setText(str);
        this.mLabel.setContentDescription(str2);
        this.mCheckbox.setContentDescription(str2);
        invalidate();
    }

    public void setLocalized(boolean z) {
        this.mLocalized.setVisibility(z ? 8 : 0);
        invalidate();
    }

    public ImageView getDragHandle() {
        return this.mDragHandle;
    }

    public CheckBox getCheckbox() {
        return this.mCheckbox;
    }
}
