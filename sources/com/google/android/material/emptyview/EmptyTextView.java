package com.google.android.material.emptyview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.R$attr;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import com.google.android.material.R$style;
import com.google.android.material.R$styleable;

public class EmptyTextView extends LinearLayout {
    private static final int DEF_STYLE = R$style.OnePlus_Widget_AppCompat_EmptyText_Default;
    private TextView mBodyView;
    private TextView mTitleView;

    public EmptyTextView(Context context) {
        this(context, null);
    }

    public EmptyTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.emptyTextStyle);
    }

    public EmptyTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R$layout.control_empty_text_view, this);
        initView();
        initTypedArray(context, attributeSet, i);
    }

    private void initTypedArray(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.EmptyTextView, i, DEF_STYLE);
        setTitleLineHeight(obtainStyledAttributes.getInt(R$styleable.EmptyTextView_titleLineHeight, 0));
        setTitleGravity(obtainStyledAttributes.getInt(R$styleable.EmptyTextView_titleGravity, 0));
        setBodyGravity(obtainStyledAttributes.getInt(R$styleable.EmptyTextView_titleGravity, 0));
        setBodyAppearance(obtainStyledAttributes.getSourceResourceId(R$styleable.EmptyTextView_bodyAppearance, R$style.op_control_text_style_body1));
        setTitle(obtainStyledAttributes.getString(R$styleable.EmptyTextView_emptyTitle));
        setBody(obtainStyledAttributes.getString(R$styleable.EmptyTextView_emptyBody));
        obtainStyledAttributes.recycle();
    }

    private void setBody(String str) {
        TextView textView = this.mBodyView;
        if (textView != null) {
            textView.setText(str);
        }
    }

    private void setTitle(String str) {
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setText(str);
        }
    }

    private void setBodyAppearance(int i) {
        TextView textView = this.mBodyView;
        if (textView != null && i > 0) {
            textView.setTextAppearance(i);
        }
    }

    private void setBodyGravity(int i) {
        TextView textView;
        if (i == 0) {
            TextView textView2 = this.mBodyView;
            if (textView2 != null) {
                textView2.setGravity(17);
            }
        } else if (i == 1 && (textView = this.mBodyView) != null) {
            textView.setGravity(8388611);
        }
    }

    private void setTitleGravity(int i) {
        TextView textView;
        if (i == 0) {
            TextView textView2 = this.mTitleView;
            if (textView2 != null) {
                textView2.setGravity(17);
            }
        } else if (i == 1 && (textView = this.mTitleView) != null) {
            textView.setGravity(8388611);
        }
    }

    private void initView() {
        this.mTitleView = (TextView) findViewById(R$id.empty_text_title);
        this.mBodyView = (TextView) findViewById(R$id.empty_text_body);
    }

    public TextView getTitleView() {
        return this.mTitleView;
    }

    public TextView getBodyView() {
        return this.mBodyView;
    }

    public void setTitleLineHeight(int i) {
        TextView textView;
        if (i > 0 && (textView = this.mTitleView) != null) {
            textView.setLineHeight(i);
        }
    }
}
