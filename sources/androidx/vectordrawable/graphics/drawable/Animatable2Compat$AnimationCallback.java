package androidx.vectordrawable.graphics.drawable;

import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;

public abstract class Animatable2Compat$AnimationCallback {
    Animatable2.AnimationCallback mPlatformCallback;

    public void onAnimationEnd(Drawable drawable) {
    }

    public void onAnimationStart(Drawable drawable) {
    }

    /* access modifiers changed from: package-private */
    public Animatable2.AnimationCallback getPlatformCallback() {
        if (this.mPlatformCallback == null) {
            this.mPlatformCallback = new Animatable2.AnimationCallback() {
                /* class androidx.vectordrawable.graphics.drawable.Animatable2Compat$AnimationCallback.AnonymousClass1 */

                public void onAnimationStart(Drawable drawable) {
                    Animatable2Compat$AnimationCallback.this.onAnimationStart(drawable);
                }

                public void onAnimationEnd(Drawable drawable) {
                    Animatable2Compat$AnimationCallback.this.onAnimationEnd(drawable);
                }
            };
        }
        return this.mPlatformCallback;
    }
}
