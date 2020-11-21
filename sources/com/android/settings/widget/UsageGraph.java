package com.android.settings.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settingslib.R$color;
import com.android.settingslib.R$dimen;

public class UsageGraph extends View {
    private int mAccentColor;
    private final int mCornerRadius;
    private final Drawable mDivider;
    private final int mDividerSize;
    private final Paint mDottedPaint;
    private final Paint mFillPaint;
    private final Paint mLinePaint;
    private final SparseIntArray mLocalPaths = new SparseIntArray();
    private final SparseIntArray mLocalProjectedPaths = new SparseIntArray();
    private float mMaxX = 100.0f;
    private float mMaxY = 100.0f;
    private float mMiddleDividerLoc = 0.5f;
    private int mMiddleDividerTint = -1;
    private final Path mPath = new Path();
    private final SparseIntArray mPaths = new SparseIntArray();
    private final SparseIntArray mProjectedPaths = new SparseIntArray();
    private final Drawable mTintedDivider;
    private int mTopDividerTint = -1;

    private int getColor(int i, float f) {
        return ((((int) (f * 255.0f)) << 24) | 16777215) & i;
    }

    public UsageGraph(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Resources resources = context.getResources();
        Paint paint = new Paint();
        this.mLinePaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        this.mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        this.mLinePaint.setAntiAlias(true);
        this.mCornerRadius = resources.getDimensionPixelSize(R$dimen.usage_graph_line_corner_radius);
        this.mLinePaint.setPathEffect(new CornerPathEffect((float) this.mCornerRadius));
        this.mLinePaint.setStrokeWidth((float) resources.getDimensionPixelSize(R$dimen.usage_graph_line_width));
        Paint paint2 = new Paint(this.mLinePaint);
        this.mFillPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        Paint paint3 = new Paint(this.mLinePaint);
        this.mDottedPaint = paint3;
        paint3.setStyle(Paint.Style.STROKE);
        float dimensionPixelSize = (float) resources.getDimensionPixelSize(R$dimen.usage_graph_dot_size);
        this.mDottedPaint.setStrokeWidth(3.0f * dimensionPixelSize);
        this.mDottedPaint.setPathEffect(new DashPathEffect(new float[]{dimensionPixelSize, (float) resources.getDimensionPixelSize(R$dimen.usage_graph_dot_interval)}, 0.0f));
        this.mDottedPaint.setColor(context.getColor(R$color.usage_graph_dots));
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843284, typedValue, true);
        this.mDivider = context.getDrawable(typedValue.resourceId);
        this.mTintedDivider = context.getDrawable(typedValue.resourceId);
        this.mDividerSize = resources.getDimensionPixelSize(R$dimen.usage_graph_divider_size);
    }

    /* access modifiers changed from: package-private */
    public void clearPaths() {
        this.mPaths.clear();
        this.mLocalPaths.clear();
        this.mProjectedPaths.clear();
        this.mLocalProjectedPaths.clear();
    }

    /* access modifiers changed from: package-private */
    public void setMax(int i, int i2) {
        long currentTimeMillis = System.currentTimeMillis();
        this.mMaxX = (float) i;
        this.mMaxY = (float) i2;
        calculateLocalPaths();
        postInvalidate();
        BatteryUtils.logRuntime("UsageGraph", "setMax", currentTimeMillis);
    }

    /* access modifiers changed from: package-private */
    public void setDividerLoc(int i) {
        this.mMiddleDividerLoc = 1.0f - (((float) i) / this.mMaxY);
    }

    /* access modifiers changed from: package-private */
    public void setDividerColors(int i, int i2) {
        this.mMiddleDividerTint = i;
        this.mTopDividerTint = i2;
    }

    public void addPath(SparseIntArray sparseIntArray) {
        addPathAndUpdate(sparseIntArray, this.mPaths, this.mLocalPaths);
    }

    public void addProjectedPath(SparseIntArray sparseIntArray) {
        addPathAndUpdate(sparseIntArray, this.mProjectedPaths, this.mLocalProjectedPaths);
    }

    private void addPathAndUpdate(SparseIntArray sparseIntArray, SparseIntArray sparseIntArray2, SparseIntArray sparseIntArray3) {
        long currentTimeMillis = System.currentTimeMillis();
        int size = sparseIntArray.size();
        for (int i = 0; i < size; i++) {
            sparseIntArray2.put(sparseIntArray.keyAt(i), sparseIntArray.valueAt(i));
        }
        sparseIntArray2.put(sparseIntArray.keyAt(sparseIntArray.size() - 1) + 1, -1);
        calculateLocalPaths(sparseIntArray2, sparseIntArray3);
        postInvalidate();
        BatteryUtils.logRuntime("UsageGraph", "addPathAndUpdate", currentTimeMillis);
    }

    /* access modifiers changed from: package-private */
    public void setAccentColor(int i) {
        this.mAccentColor = i;
        this.mLinePaint.setColor(i);
        updateGradient();
        postInvalidate();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        long currentTimeMillis = System.currentTimeMillis();
        super.onSizeChanged(i, i2, i3, i4);
        updateGradient();
        calculateLocalPaths();
        BatteryUtils.logRuntime("UsageGraph", "onSizeChanged", currentTimeMillis);
    }

    private void calculateLocalPaths() {
        calculateLocalPaths(this.mPaths, this.mLocalPaths);
        calculateLocalPaths(this.mProjectedPaths, this.mLocalProjectedPaths);
    }

    /* access modifiers changed from: package-private */
    public void calculateLocalPaths(SparseIntArray sparseIntArray, SparseIntArray sparseIntArray2) {
        long currentTimeMillis = System.currentTimeMillis();
        if (getWidth() != 0) {
            sparseIntArray2.clear();
            int i = -1;
            boolean z = false;
            int i2 = 0;
            for (int i3 = 0; i3 < sparseIntArray.size(); i3++) {
                int keyAt = sparseIntArray.keyAt(i3);
                int valueAt = sparseIntArray.valueAt(i3);
                if (valueAt != -1) {
                    i2 = getX((float) keyAt);
                    i = getY((float) valueAt);
                    if (sparseIntArray2.size() > 0) {
                        int keyAt2 = sparseIntArray2.keyAt(sparseIntArray2.size() - 1);
                        int valueAt2 = sparseIntArray2.valueAt(sparseIntArray2.size() - 1);
                        if (valueAt2 != -1 && !hasDiff(keyAt2, i2) && !hasDiff(valueAt2, i)) {
                            z = true;
                        }
                    }
                    sparseIntArray2.put(i2, i);
                } else if (i3 == 1) {
                    sparseIntArray2.put(getX((float) (keyAt + 1)) - 1, getY(0.0f));
                } else {
                    if (i3 == sparseIntArray.size() - 1 && z) {
                        sparseIntArray2.put(i2, i);
                    }
                    sparseIntArray2.put(i2 + 1, -1);
                }
                z = false;
            }
            BatteryUtils.logRuntime("UsageGraph", "calculateLocalPaths", currentTimeMillis);
        }
    }

    private boolean hasDiff(int i, int i2) {
        return Math.abs(i2 - i) >= this.mCornerRadius;
    }

    private int getX(float f) {
        return (int) ((f / this.mMaxX) * ((float) getWidth()));
    }

    private int getY(float f) {
        return (int) (((float) getHeight()) * (1.0f - (f / this.mMaxY)));
    }

    private void updateGradient() {
        this.mFillPaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) getHeight(), getColor(this.mAccentColor, 0.2f), 0, Shader.TileMode.CLAMP));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        long currentTimeMillis = System.currentTimeMillis();
        if (this.mMiddleDividerLoc != 0.0f) {
            drawDivider(0, canvas, this.mTopDividerTint);
        }
        drawDivider((int) (((float) (canvas.getHeight() - this.mDividerSize)) * this.mMiddleDividerLoc), canvas, this.mMiddleDividerTint);
        drawDivider(canvas.getHeight() - this.mDividerSize, canvas, -1);
        if (this.mLocalPaths.size() != 0 || this.mLocalProjectedPaths.size() != 0) {
            canvas.save();
            if (getLayoutDirection() == 1) {
                canvas.scale(-1.0f, 1.0f, ((float) canvas.getWidth()) * 0.5f, 0.0f);
            }
            drawLinePath(canvas, this.mLocalProjectedPaths, this.mDottedPaint);
            drawFilledPath(canvas, this.mLocalPaths, this.mFillPaint);
            drawLinePath(canvas, this.mLocalPaths, this.mLinePaint);
            canvas.restore();
            BatteryUtils.logRuntime("UsageGraph", "onDraw", currentTimeMillis);
        }
    }

    private void drawLinePath(Canvas canvas, SparseIntArray sparseIntArray, Paint paint) {
        if (sparseIntArray.size() != 0) {
            this.mPath.reset();
            this.mPath.moveTo((float) sparseIntArray.keyAt(0), (float) sparseIntArray.valueAt(0));
            int i = 1;
            while (i < sparseIntArray.size()) {
                int keyAt = sparseIntArray.keyAt(i);
                int valueAt = sparseIntArray.valueAt(i);
                if (valueAt == -1) {
                    i++;
                    if (i < sparseIntArray.size()) {
                        this.mPath.moveTo((float) sparseIntArray.keyAt(i), (float) sparseIntArray.valueAt(i));
                    }
                } else {
                    this.mPath.lineTo((float) keyAt, (float) valueAt);
                }
                i++;
            }
            canvas.drawPath(this.mPath, paint);
        }
    }

    /* access modifiers changed from: package-private */
    public void drawFilledPath(Canvas canvas, SparseIntArray sparseIntArray, Paint paint) {
        if (sparseIntArray.size() != 0) {
            this.mPath.reset();
            float keyAt = (float) sparseIntArray.keyAt(0);
            this.mPath.moveTo((float) sparseIntArray.keyAt(0), (float) sparseIntArray.valueAt(0));
            int i = 1;
            while (i < sparseIntArray.size()) {
                int keyAt2 = sparseIntArray.keyAt(i);
                int valueAt = sparseIntArray.valueAt(i);
                if (valueAt == -1) {
                    this.mPath.lineTo((float) sparseIntArray.keyAt(i - 1), (float) getHeight());
                    this.mPath.lineTo(keyAt, (float) getHeight());
                    this.mPath.close();
                    i++;
                    if (i < sparseIntArray.size()) {
                        keyAt = (float) sparseIntArray.keyAt(i);
                        this.mPath.moveTo((float) sparseIntArray.keyAt(i), (float) sparseIntArray.valueAt(i));
                    }
                } else {
                    this.mPath.lineTo((float) keyAt2, (float) valueAt);
                }
                i++;
            }
            canvas.drawPath(this.mPath, paint);
        }
    }

    private void drawDivider(int i, Canvas canvas, int i2) {
        Drawable drawable = this.mDivider;
        if (i2 != -1) {
            this.mTintedDivider.setTint(i2);
            drawable = this.mTintedDivider;
        }
        drawable.setBounds(0, i, canvas.getWidth(), this.mDividerSize + i);
        drawable.draw(canvas);
    }
}
