package com.oneplus.settings.quicklaunch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0018R$style;

class OPAppDragCell extends RelativeLayout {
    private ImageView mAppIcon;
    private CheckBox mCheckbox;
    private ImageView mDeleteButton;
    private ImageView mDragHandle;
    private TextView mLabel;
    private ImageView mSmallIcon;

    public OPAppDragCell(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mLabel = (TextView) findViewById(C0010R$id.label);
        this.mAppIcon = (ImageView) findViewById(C0010R$id.quick_launch_app_icon);
        this.mSmallIcon = (ImageView) findViewById(C0010R$id.small_icon);
        this.mCheckbox = (CheckBox) findViewById(C0010R$id.checkbox);
        this.mDragHandle = (ImageView) findViewById(C0010R$id.dragHandle);
        this.mDeleteButton = (ImageView) findViewById(C0010R$id.delete_button);
        TextView textView = this.mLabel;
        if (textView != null) {
            textView.setTextAppearance(getContext(), C0018R$style.OnePlus_TextAppearance_List_Title);
        }
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

    public void setChecked(boolean z) {
        this.mCheckbox.setChecked(z);
    }

    public void setShowAppIcon(boolean z) {
        this.mAppIcon.setVisibility(z ? 0 : 8);
        invalidate();
        requestLayout();
    }

    public void setAppIcon(Drawable drawable) {
        this.mAppIcon.setImageDrawable(drawable);
        invalidate();
    }

    public void setSmallIcon(Drawable drawable) {
        this.mSmallIcon.setImageDrawable(drawable);
        invalidate();
    }

    public void setLabelAndDescription(String str, String str2) {
        this.mLabel.setText(str);
        this.mCheckbox.setText(str);
        this.mLabel.setContentDescription(str2);
        this.mCheckbox.setContentDescription(str2);
        invalidate();
    }

    public ImageView getDragHandle() {
        return this.mDragHandle;
    }

    public CheckBox getCheckbox() {
        return this.mCheckbox;
    }

    public ImageView getDeleteButton() {
        return this.mDeleteButton;
    }
}
