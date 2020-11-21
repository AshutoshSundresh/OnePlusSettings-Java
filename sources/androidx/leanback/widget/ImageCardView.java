package androidx.leanback.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$attr;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.R$style;
import androidx.leanback.R$styleable;

public class ImageCardView extends BaseCardView {
    private boolean mAttachedToWindow;
    private ImageView mBadgeImage;
    private TextView mContentView;
    ObjectAnimator mFadeInAnimator;
    private ImageView mImageView;
    private ViewGroup mInfoArea;
    private TextView mTitleView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public ImageCardView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        buildImageCardView(attributeSet, i, R$style.Widget_Leanback_ImageCardView);
    }

    private void buildImageCardView(AttributeSet attributeSet, int i, int i2) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        LayoutInflater from = LayoutInflater.from(getContext());
        from.inflate(R$layout.lb_image_card_view, this);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.lbImageCardView, i, i2);
        ViewCompat.saveAttributeDataForStyleable(this, getContext(), R$styleable.lbImageCardView, attributeSet, obtainStyledAttributes, i, i2);
        int i3 = obtainStyledAttributes.getInt(R$styleable.lbImageCardView_lbImageCardViewType, 0);
        boolean z = i3 == 0;
        boolean z2 = (i3 & 1) == 1;
        boolean z3 = (i3 & 2) == 2;
        boolean z4 = (i3 & 4) == 4;
        boolean z5 = !z4 && (i3 & 8) == 8;
        ImageView imageView = (ImageView) findViewById(R$id.main_image);
        this.mImageView = imageView;
        if (imageView.getDrawable() == null) {
            this.mImageView.setVisibility(4);
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mImageView, "alpha", 1.0f);
        this.mFadeInAnimator = ofFloat;
        ofFloat.setDuration((long) this.mImageView.getResources().getInteger(17694720));
        ViewGroup viewGroup = (ViewGroup) findViewById(R$id.info_field);
        this.mInfoArea = viewGroup;
        if (z) {
            removeView(viewGroup);
            obtainStyledAttributes.recycle();
            return;
        }
        if (z2) {
            TextView textView = (TextView) from.inflate(R$layout.lb_image_card_view_themed_title, viewGroup, false);
            this.mTitleView = textView;
            this.mInfoArea.addView(textView);
        }
        if (z3) {
            TextView textView2 = (TextView) from.inflate(R$layout.lb_image_card_view_themed_content, this.mInfoArea, false);
            this.mContentView = textView2;
            this.mInfoArea.addView(textView2);
        }
        if (z4 || z5) {
            int i4 = R$layout.lb_image_card_view_themed_badge_right;
            if (z5) {
                i4 = R$layout.lb_image_card_view_themed_badge_left;
            }
            ImageView imageView2 = (ImageView) from.inflate(i4, this.mInfoArea, false);
            this.mBadgeImage = imageView2;
            this.mInfoArea.addView(imageView2);
        }
        if (z2 && !z3 && this.mBadgeImage != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mTitleView.getLayoutParams();
            if (z5) {
                layoutParams.addRule(17, this.mBadgeImage.getId());
            } else {
                layoutParams.addRule(16, this.mBadgeImage.getId());
            }
            this.mTitleView.setLayoutParams(layoutParams);
        }
        if (z3) {
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.mContentView.getLayoutParams();
            if (!z2) {
                layoutParams2.addRule(10);
            }
            if (z5) {
                layoutParams2.removeRule(16);
                layoutParams2.removeRule(20);
                layoutParams2.addRule(17, this.mBadgeImage.getId());
            }
            this.mContentView.setLayoutParams(layoutParams2);
        }
        ImageView imageView3 = this.mBadgeImage;
        if (imageView3 != null) {
            RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) imageView3.getLayoutParams();
            if (z3) {
                layoutParams3.addRule(8, this.mContentView.getId());
            } else if (z2) {
                layoutParams3.addRule(8, this.mTitleView.getId());
            }
            this.mBadgeImage.setLayoutParams(layoutParams3);
        }
        Drawable drawable = obtainStyledAttributes.getDrawable(R$styleable.lbImageCardView_infoAreaBackground);
        if (drawable != null) {
            setInfoAreaBackground(drawable);
        }
        ImageView imageView4 = this.mBadgeImage;
        if (imageView4 != null && imageView4.getDrawable() == null) {
            this.mBadgeImage.setVisibility(8);
        }
        obtainStyledAttributes.recycle();
    }

    public ImageCardView(Context context) {
        this(context, null);
    }

    public ImageCardView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.imageCardViewStyle);
    }

    public final ImageView getMainImageView() {
        return this.mImageView;
    }

    public void setMainImageAdjustViewBounds(boolean z) {
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setAdjustViewBounds(z);
        }
    }

    public void setMainImageScaleType(ImageView.ScaleType scaleType) {
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setScaleType(scaleType);
        }
    }

    public void setMainImage(Drawable drawable) {
        setMainImage(drawable, true);
    }

    public void setMainImage(Drawable drawable, boolean z) {
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setImageDrawable(drawable);
            if (drawable == null) {
                this.mFadeInAnimator.cancel();
                this.mImageView.setAlpha(1.0f);
                this.mImageView.setVisibility(4);
                return;
            }
            this.mImageView.setVisibility(0);
            if (z) {
                fadeIn();
                return;
            }
            this.mFadeInAnimator.cancel();
            this.mImageView.setAlpha(1.0f);
        }
    }

    public Drawable getMainImage() {
        ImageView imageView = this.mImageView;
        if (imageView == null) {
            return null;
        }
        return imageView.getDrawable();
    }

    public Drawable getInfoAreaBackground() {
        ViewGroup viewGroup = this.mInfoArea;
        if (viewGroup != null) {
            return viewGroup.getBackground();
        }
        return null;
    }

    public void setInfoAreaBackground(Drawable drawable) {
        ViewGroup viewGroup = this.mInfoArea;
        if (viewGroup != null) {
            viewGroup.setBackground(drawable);
        }
    }

    public void setInfoAreaBackgroundColor(int i) {
        ViewGroup viewGroup = this.mInfoArea;
        if (viewGroup != null) {
            viewGroup.setBackgroundColor(i);
        }
    }

    public void setTitleText(CharSequence charSequence) {
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public CharSequence getTitleText() {
        TextView textView = this.mTitleView;
        if (textView == null) {
            return null;
        }
        return textView.getText();
    }

    public void setContentText(CharSequence charSequence) {
        TextView textView = this.mContentView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public CharSequence getContentText() {
        TextView textView = this.mContentView;
        if (textView == null) {
            return null;
        }
        return textView.getText();
    }

    public void setBadgeImage(Drawable drawable) {
        ImageView imageView = this.mBadgeImage;
        if (imageView != null) {
            imageView.setImageDrawable(drawable);
            if (drawable != null) {
                this.mBadgeImage.setVisibility(0);
            } else {
                this.mBadgeImage.setVisibility(8);
            }
        }
    }

    public Drawable getBadgeImage() {
        ImageView imageView = this.mBadgeImage;
        if (imageView == null) {
            return null;
        }
        return imageView.getDrawable();
    }

    private void fadeIn() {
        this.mImageView.setAlpha(0.0f);
        if (this.mAttachedToWindow) {
            this.mFadeInAnimator.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        if (this.mImageView.getAlpha() == 0.0f) {
            fadeIn();
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.leanback.widget.BaseCardView
    public void onDetachedFromWindow() {
        this.mAttachedToWindow = false;
        this.mFadeInAnimator.cancel();
        this.mImageView.setAlpha(1.0f);
        super.onDetachedFromWindow();
    }
}
