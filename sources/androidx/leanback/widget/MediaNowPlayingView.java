package androidx.leanback.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;

public class MediaNowPlayingView extends LinearLayout {
    private final ImageView mImage1;
    private final ImageView mImage2;
    private final ImageView mImage3;
    protected final LinearInterpolator mLinearInterpolator = new LinearInterpolator();
    private final ObjectAnimator mObjectAnimator1;
    private final ObjectAnimator mObjectAnimator2;
    private final ObjectAnimator mObjectAnimator3;

    public MediaNowPlayingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R$layout.lb_playback_now_playing_bars, (ViewGroup) this, true);
        this.mImage1 = (ImageView) findViewById(R$id.bar1);
        this.mImage2 = (ImageView) findViewById(R$id.bar2);
        this.mImage3 = (ImageView) findViewById(R$id.bar3);
        ImageView imageView = this.mImage1;
        imageView.setPivotY((float) imageView.getDrawable().getIntrinsicHeight());
        ImageView imageView2 = this.mImage2;
        imageView2.setPivotY((float) imageView2.getDrawable().getIntrinsicHeight());
        ImageView imageView3 = this.mImage3;
        imageView3.setPivotY((float) imageView3.getDrawable().getIntrinsicHeight());
        setDropScale(this.mImage1);
        setDropScale(this.mImage2);
        setDropScale(this.mImage3);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mImage1, "scaleY", 0.41666666f, 0.25f, 0.41666666f, 0.5833333f, 0.75f, 0.8333333f, 0.9166667f, 1.0f, 0.9166667f, 1.0f, 0.8333333f, 0.6666667f, 0.5f, 0.33333334f, 0.16666667f, 0.33333334f, 0.5f, 0.5833333f, 0.75f, 0.9166667f, 0.75f, 0.5833333f, 0.41666666f, 0.25f, 0.41666666f, 0.6666667f, 0.41666666f, 0.25f, 0.33333334f, 0.41666666f);
        this.mObjectAnimator1 = ofFloat;
        ofFloat.setRepeatCount(-1);
        this.mObjectAnimator1.setDuration(2320L);
        this.mObjectAnimator1.setInterpolator(this.mLinearInterpolator);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.mImage2, "scaleY", 1.0f, 0.9166667f, 0.8333333f, 0.9166667f, 1.0f, 0.9166667f, 0.75f, 0.5833333f, 0.75f, 0.9166667f, 1.0f, 0.8333333f, 0.6666667f, 0.8333333f, 1.0f, 0.9166667f, 0.75f, 0.41666666f, 0.25f, 0.41666666f, 0.6666667f, 0.8333333f, 1.0f, 0.8333333f, 0.75f, 0.6666667f, 1.0f);
        this.mObjectAnimator2 = ofFloat2;
        ofFloat2.setRepeatCount(-1);
        this.mObjectAnimator2.setDuration(2080L);
        this.mObjectAnimator2.setInterpolator(this.mLinearInterpolator);
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.mImage3, "scaleY", 0.6666667f, 0.75f, 0.8333333f, 1.0f, 0.9166667f, 0.75f, 0.5833333f, 0.41666666f, 0.5833333f, 0.6666667f, 0.75f, 1.0f, 0.9166667f, 1.0f, 0.75f, 0.5833333f, 0.75f, 0.9166667f, 1.0f, 0.8333333f, 0.6666667f, 0.75f, 0.5833333f, 0.41666666f, 0.25f, 0.6666667f);
        this.mObjectAnimator3 = ofFloat3;
        ofFloat3.setRepeatCount(-1);
        this.mObjectAnimator3.setDuration(2000L);
        this.mObjectAnimator3.setInterpolator(this.mLinearInterpolator);
    }

    static void setDropScale(View view) {
        view.setScaleY(0.083333336f);
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 8) {
            stopAnimation();
        } else {
            startAnimation();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == 0) {
            startAnimation();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    private void startAnimation() {
        startAnimation(this.mObjectAnimator1);
        startAnimation(this.mObjectAnimator2);
        startAnimation(this.mObjectAnimator3);
        this.mImage1.setVisibility(0);
        this.mImage2.setVisibility(0);
        this.mImage3.setVisibility(0);
    }

    private void stopAnimation() {
        stopAnimation(this.mObjectAnimator1, this.mImage1);
        stopAnimation(this.mObjectAnimator2, this.mImage2);
        stopAnimation(this.mObjectAnimator3, this.mImage3);
        this.mImage1.setVisibility(8);
        this.mImage2.setVisibility(8);
        this.mImage3.setVisibility(8);
    }

    private void startAnimation(Animator animator) {
        if (!animator.isStarted()) {
            animator.start();
        }
    }

    private void stopAnimation(Animator animator, View view) {
        if (animator.isStarted()) {
            animator.cancel();
            setDropScale(view);
        }
    }
}
