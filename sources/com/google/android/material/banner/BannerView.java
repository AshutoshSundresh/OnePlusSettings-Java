package com.google.android.material.banner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.R$attr;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;

public class BannerView extends FrameLayout {
    private ImageView mIconView;
    private Button mMultiButtonLeftView;
    private Button mMultiButtonRightView;
    private LinearLayout mRootView;
    private Button mSingleButtonView;
    private TextView mTitleView;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.bannerViewStyle);
    }

    public BannerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R$layout.control_banner_view, this);
        initView();
        setVisibility(4);
    }

    private void initView() {
        this.mIconView = (ImageView) findViewById(R$id.banner_icon);
        this.mTitleView = (TextView) findViewById(R$id.banner_title);
        this.mSingleButtonView = (Button) findViewById(R$id.single_action_button);
        this.mMultiButtonLeftView = (Button) findViewById(R$id.multi_action_button_left);
        this.mMultiButtonRightView = (Button) findViewById(R$id.multi_action_button_right);
        LinearLayout linearLayout = (LinearLayout) findViewById(R$id.banner_vertical_button_layout);
        LinearLayout linearLayout2 = (LinearLayout) findViewById(R$id.banner_text_layout);
        this.mRootView = (LinearLayout) findViewById(R$id.banner_layout);
    }

    public LinearLayout getRootLayout() {
        return this.mRootView;
    }

    public void setIcon(Drawable drawable) {
        ImageView imageView = this.mIconView;
        if (imageView != null) {
            imageView.setVisibility(0);
            this.mIconView.setImageDrawable(drawable);
        }
    }

    public void setTitle(CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence)) {
            this.mTitleView.setText(charSequence);
        } else {
            this.mTitleView.setVisibility(8);
        }
    }

    public ImageView getIconView() {
        return this.mIconView;
    }

    public TextView getTiTleView() {
        return this.mTitleView;
    }

    public Button getSignleButton() {
        return this.mSingleButtonView;
    }

    public Button getMultiLeftButton() {
        return this.mMultiButtonLeftView;
    }

    public Button getMultiRightButton() {
        return this.mMultiButtonRightView;
    }
}
