package com.android.settings.gestures;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0006R$color;

public class BackGestureIndicatorDrawable extends Drawable {
    private Context mContext;
    private float mCurrentWidth;
    private float mFinalWidth;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.android.settings.gestures.BackGestureIndicatorDrawable.AnonymousClass1 */

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                BackGestureIndicatorDrawable.this.mTimeAnimator.end();
                BackGestureIndicatorDrawable.this.mFinalWidth = (float) message.arg1;
                BackGestureIndicatorDrawable backGestureIndicatorDrawable = BackGestureIndicatorDrawable.this;
                backGestureIndicatorDrawable.mWidthChangePerMs = Math.abs(backGestureIndicatorDrawable.mCurrentWidth - BackGestureIndicatorDrawable.this.mFinalWidth) / 200.0f;
                BackGestureIndicatorDrawable.this.mTimeAnimator.start();
            } else if (i == 3) {
                BackGestureIndicatorDrawable backGestureIndicatorDrawable2 = BackGestureIndicatorDrawable.this;
                backGestureIndicatorDrawable2.mCurrentWidth = backGestureIndicatorDrawable2.mFinalWidth;
                removeMessages(1);
                sendMessageDelayed(obtainMessage(1, 0, 0), 700);
                BackGestureIndicatorDrawable.this.invalidateSelf();
            }
        }
    };
    private Paint mPaint = new Paint();
    private boolean mReversed;
    private TimeAnimator mTimeAnimator = new TimeAnimator();
    private float mWidthChangePerMs;

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public BackGestureIndicatorDrawable(Context context, boolean z) {
        this.mContext = context;
        this.mReversed = z;
        this.mTimeAnimator.setTimeListener(new TimeAnimator.TimeListener() {
            /* class com.android.settings.gestures.$$Lambda$BackGestureIndicatorDrawable$Uerbvrd7VOnuAeux9yurAc9gnIo */

            public final void onTimeUpdate(TimeAnimator timeAnimator, long j, long j2) {
                BackGestureIndicatorDrawable.this.lambda$new$0$BackGestureIndicatorDrawable(timeAnimator, j, j2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$BackGestureIndicatorDrawable(TimeAnimator timeAnimator, long j, long j2) {
        updateCurrentWidth(j, j2);
        invalidateSelf();
    }

    private void updateCurrentWidth(long j, long j2) {
        synchronized (this.mTimeAnimator) {
            float f = ((float) j2) * this.mWidthChangePerMs;
            if (j < 200) {
                if (f < Math.abs(this.mFinalWidth - this.mCurrentWidth)) {
                    this.mCurrentWidth += (this.mCurrentWidth < this.mFinalWidth ? 1.0f : -1.0f) * f;
                }
            }
            this.mCurrentWidth = this.mFinalWidth;
            this.mTimeAnimator.end();
        }
    }

    public void draw(Canvas canvas) {
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mContext.getResources().getColor(C0006R$color.back_gesture_indicator));
        this.mPaint.setAlpha(64);
        int height = canvas.getHeight();
        int i = (int) this.mCurrentWidth;
        Rect rect = new Rect(0, 0, i, height);
        if (this.mReversed) {
            rect.offset(canvas.getWidth() - i, 0);
        }
        canvas.drawRect(rect, this.mPaint);
    }

    public void setWidth(int i) {
        if (i == 0) {
            this.mHandler.sendEmptyMessage(3);
            return;
        }
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(1, i, 0));
    }

    @VisibleForTesting
    public int getWidth() {
        return (int) this.mFinalWidth;
    }
}
