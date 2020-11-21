package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.R$attr;
import androidx.appcompat.R$dimen;
import androidx.appcompat.R$styleable;
import java.util.List;

public class SmoothRoundLayout extends FrameLayout {
    private List<Path> mCornerPathList;
    private float mCornerRadius;
    private PorterDuffXfermode mDuffXferMode;
    private Paint mPaint;
    private RectF mRectF;

    public SmoothRoundLayout(Context context) {
        this(context, null);
    }

    public SmoothRoundLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.SmoothRoundStyle);
    }

    public SmoothRoundLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SmoothRoundLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mPaint = new Paint(1);
        this.mRectF = new RectF();
        this.mDuffXferMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SmoothRoundLayout, i, i2);
        this.mCornerRadius = obtainStyledAttributes.getDimension(R$styleable.SmoothRoundLayout_cornerRadius, (float) context.getResources().getDimensionPixelOffset(R$dimen.oneplus_smooth_round_radius));
        obtainStyledAttributes.recycle();
    }

    public void setBackground(Drawable drawable) {
        RectF rectF;
        super.setBackground(drawable);
        if (!(drawable instanceof ColorDrawable) && (rectF = this.mRectF) != null) {
            rectF.set(0.0f, 0.0f, (float) drawable.getIntrinsicWidth(), (float) drawable.getIntrinsicHeight());
            this.mCornerPathList = SmoothCornerUtils.calculateBezierCornerPaths(this.mRectF, this.mCornerRadius);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        int paddingBottom = getPaddingBottom();
        int paddingTop = getPaddingTop();
        int paddingEnd = getPaddingEnd();
        this.mRectF.set((float) getPaddingStart(), (float) paddingTop, (float) (i - paddingEnd), (float) (i2 - paddingBottom));
        this.mCornerPathList = SmoothCornerUtils.calculateBezierCornerPaths(this.mRectF, this.mCornerRadius);
        super.onSizeChanged(i, i2, i3, i4);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        if (getHeight() > view.getHeight()) {
            this.mRectF.set((float) view.getLeft(), (float) view.getTop(), (float) view.getRight(), (float) view.getBottom());
            this.mCornerPathList = SmoothCornerUtils.calculateBezierCornerPaths(this.mRectF, this.mCornerRadius);
        }
        return super.drawChild(canvas, view, j);
    }

    public void setCornerRadius(float f) {
        this.mCornerRadius = f;
        this.mCornerPathList = SmoothCornerUtils.calculateBezierCornerPaths(this.mRectF, f);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        int saveLayer = canvas.saveLayer(this.mRectF, this.mPaint);
        super.dispatchDraw(canvas);
        this.mPaint.setXfermode(this.mDuffXferMode);
        for (Path path : this.mCornerPathList) {
            canvas.drawPath(path, this.mPaint);
        }
        this.mPaint.setXfermode(null);
        canvas.restoreToCount(saveLayer);
    }
}
