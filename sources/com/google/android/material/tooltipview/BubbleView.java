package com.google.android.material.tooltipview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;

public class BubbleView extends LinearLayout {
    private ImageView mImage;
    private TextView mMessage;
    private TextView mTitle;

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R$layout.control_bubble_view, this);
        initView();
        setWillNotDraw(false);
    }

    private void initView() {
        this.mTitle = (TextView) findViewById(R$id.title);
        this.mMessage = (TextView) findViewById(R$id.message);
        Button button = (Button) findViewById(R$id.button1);
        Button button2 = (Button) findViewById(R$id.button2);
        this.mImage = (ImageView) findViewById(R$id.bubble_image);
    }

    public void setTitle(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            this.mTitle.setVisibility(8);
            return;
        }
        this.mTitle.setVisibility(0);
        this.mTitle.setText(charSequence);
    }

    public void setMessage(CharSequence charSequence) {
        this.mMessage.setText(charSequence);
    }

    public void setImage(Drawable drawable) {
        this.mImage.setImageDrawable(drawable);
        this.mImage.setVisibility(0);
    }
}
