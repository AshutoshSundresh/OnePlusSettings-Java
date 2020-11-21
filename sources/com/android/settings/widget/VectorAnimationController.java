package com.android.settings.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.TextureView;
import android.view.View;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat$AnimationCallback;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import com.android.settings.widget.VideoPreference;

/* access modifiers changed from: package-private */
public class VectorAnimationController implements VideoPreference.AnimationController {
    private AnimatedVectorDrawableCompat mAnimatedVectorDrawableCompat;
    private Animatable2Compat$AnimationCallback mAnimationCallback = new Animatable2Compat$AnimationCallback() {
        /* class com.android.settings.widget.VectorAnimationController.AnonymousClass1 */

        @Override // androidx.vectordrawable.graphics.drawable.Animatable2Compat$AnimationCallback
        public void onAnimationEnd(Drawable drawable) {
            VectorAnimationController.this.mAnimatedVectorDrawableCompat.start();
        }
    };
    private Drawable mPreviewDrawable;

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getDuration() {
        return 5000;
    }

    VectorAnimationController(Context context, int i) {
        this.mAnimatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(context, i);
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getVideoWidth() {
        return this.mAnimatedVectorDrawableCompat.getIntrinsicWidth();
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public int getVideoHeight() {
        return this.mAnimatedVectorDrawableCompat.getIntrinsicHeight();
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public void attachView(TextureView textureView, View view, View view2) {
        this.mPreviewDrawable = view.getForeground();
        textureView.setVisibility(8);
        updateViewStates(view, view2);
        view.setOnClickListener(new View.OnClickListener(view, view2) {
            /* class com.android.settings.widget.$$Lambda$VectorAnimationController$IaAayyF_KKRIil8wW7wXIVV2H38 */
            public final /* synthetic */ View f$1;
            public final /* synthetic */ View f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(View view) {
                VectorAnimationController.this.lambda$attachView$0$VectorAnimationController(this.f$1, this.f$2, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$attachView$0 */
    public /* synthetic */ void lambda$attachView$0$VectorAnimationController(View view, View view2, View view3) {
        updateViewStates(view, view2);
    }

    @Override // com.android.settings.widget.VideoPreference.AnimationController
    public void release() {
        this.mAnimatedVectorDrawableCompat.stop();
        this.mAnimatedVectorDrawableCompat.clearAnimationCallbacks();
    }

    private void updateViewStates(View view, View view2) {
        if (this.mAnimatedVectorDrawableCompat.isRunning()) {
            this.mAnimatedVectorDrawableCompat.stop();
            this.mAnimatedVectorDrawableCompat.clearAnimationCallbacks();
            view2.setVisibility(0);
            view.setForeground(this.mPreviewDrawable);
            return;
        }
        view2.setVisibility(8);
        view.setForeground(this.mAnimatedVectorDrawableCompat);
        this.mAnimatedVectorDrawableCompat.start();
        this.mAnimatedVectorDrawableCompat.registerAnimationCallback(this.mAnimationCallback);
    }
}
