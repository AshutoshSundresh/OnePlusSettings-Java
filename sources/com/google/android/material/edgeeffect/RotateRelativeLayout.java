package com.google.android.material.edgeeffect;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

public class RotateRelativeLayout extends SpringRelativeLayout implements RotatableLayout {
    private Matrix m_InvMatrix = new Matrix();
    private RectF m_NewRectF = new RectF();
    private int m_RotDiff;
    private Matrix m_RotMatrix = new Matrix();
    private Rotation m_Rotation;

    public View getView() {
        return this;
    }

    public RotateRelativeLayout(Context context) {
        super(context);
    }

    public RotateRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private MotionEvent createRotatedMotionEvent(MotionEvent motionEvent) {
        MotionEvent.PointerProperties[] pointerPropertiesArr = new MotionEvent.PointerProperties[motionEvent.getPointerCount()];
        MotionEvent.PointerCoords[] pointerCoordsArr = new MotionEvent.PointerCoords[motionEvent.getPointerCount()];
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
            pointerPropertiesArr[i] = new MotionEvent.PointerProperties();
            motionEvent.getPointerProperties(i, pointerPropertiesArr[i]);
            pointerCoordsArr[i] = new MotionEvent.PointerCoords();
            motionEvent.getPointerCoords(i, pointerCoordsArr[i]);
            float[] fArr = new float[2];
            this.m_RotMatrix.mapPoints(fArr, new float[]{pointerCoordsArr[i].x, pointerCoordsArr[i].y});
            int i2 = this.m_RotDiff;
            if (i2 == 90) {
                fArr[0] = fArr[0] - 1.0f;
            } else if (i2 == 270) {
                fArr[1] = fArr[1] - 1.0f;
            }
            if (fArr[0] < 0.0f) {
                fArr[0] = 0.0f;
            }
            if (fArr[1] < 0.0f) {
                fArr[1] = 0.0f;
            }
            if (i == 0) {
                pointerCoordsArr[0].x = motionEvent.getRawX();
                pointerCoordsArr[0].y = motionEvent.getRawY();
                x = fArr[0];
                y = fArr[1];
            } else {
                pointerCoordsArr[i].x = fArr[0] + (motionEvent.getRawX() - x);
                pointerCoordsArr[i].y = fArr[1] + (motionEvent.getRawY() - y);
            }
        }
        MotionEvent obtain = MotionEvent.obtain(motionEvent.getDownTime(), motionEvent.getEventTime(), motionEvent.getAction(), motionEvent.getPointerCount(), pointerPropertiesArr, pointerCoordsArr, motionEvent.getMetaState(), motionEvent.getButtonState(), motionEvent.getXPrecision(), motionEvent.getYPrecision(), motionEvent.getDeviceId(), motionEvent.getEdgeFlags(), motionEvent.getSource(), motionEvent.getFlags());
        obtain.setLocation(x, y);
        return obtain;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.concat(this.m_InvMatrix);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        MotionEvent createRotatedMotionEvent = createRotatedMotionEvent(motionEvent);
        motionEvent.getPointerCount();
        boolean dispatchTouchEvent = super.dispatchTouchEvent(createRotatedMotionEvent);
        createRotatedMotionEvent.recycle();
        return dispatchTouchEvent;
    }

    private Rotation getActivityRotation() {
        Context context = getContext();
        if (context instanceof Activity) {
            return Rotation.fromScreenOrientation(((Activity) context).getRequestedOrientation());
        }
        return this.m_Rotation;
    }

    public Rotation getLayoutRotation() {
        return this.m_Rotation;
    }

    public ViewParent invalidateChildInParent(int[] iArr, Rect rect) {
        rect.offset(iArr[0], iArr[1]);
        this.m_NewRectF.set(rect);
        this.m_InvMatrix.mapRect(this.m_NewRectF);
        this.m_NewRectF.roundOut(rect);
        invalidate(rect);
        return super.invalidateChildInParent(iArr, rect);
    }

    /* access modifiers changed from: protected */
    public void onAnimationEnd() {
        super.onAnimationEnd();
        requestLayout();
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        Rotation activityRotation = getActivityRotation();
        if (activityRotation == null || this.m_Rotation == null || activityRotation.isLandscape() == this.m_Rotation.isLandscape()) {
            super.onLayout(z, i, i2, i3, i4);
        } else {
            super.onLayout(z, i2, i, i4, i3);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        Rotation activityRotation = getActivityRotation();
        if (activityRotation == null || this.m_Rotation == null || activityRotation.isLandscape() == this.m_Rotation.isLandscape()) {
            super.onMeasure(i, i2);
        } else {
            super.onMeasure(i2, i);
        }
        rotateMeasurement();
    }

    /* access modifiers changed from: protected */
    public void rotateMeasurement() {
        Rotation activityRotation = getActivityRotation();
        if (!(activityRotation == null || this.m_Rotation == null || activityRotation.isLandscape() == this.m_Rotation.isLandscape())) {
            setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
        }
        if (activityRotation == null || this.m_Rotation == null) {
            this.m_RotDiff = 0;
        } else {
            this.m_RotDiff = Math.abs(activityRotation.getDeviceOrientation() - this.m_Rotation.getDeviceOrientation());
        }
        this.m_RotMatrix.reset();
        int i = this.m_RotDiff;
        if (i == 0) {
            this.m_RotMatrix.setRotate(0.0f);
            this.m_RotMatrix.postTranslate(0.0f, 0.0f);
        } else if (i == 90) {
            this.m_RotMatrix.setRotate(90.0f);
            this.m_RotMatrix.postTranslate((float) getMeasuredHeight(), 0.0f);
        } else if (i == 180) {
            this.m_RotMatrix.setRotate(180.0f);
            this.m_RotMatrix.postTranslate((float) getMeasuredWidth(), (float) getMeasuredHeight());
        } else if (i == 270) {
            this.m_RotMatrix.setRotate(270.0f);
            this.m_RotMatrix.postTranslate(0.0f, (float) getMeasuredWidth());
        }
        Matrix matrix = new Matrix(this.m_RotMatrix);
        this.m_InvMatrix = matrix;
        this.m_RotMatrix.invert(matrix);
    }

    public final void setLayoutRotation(Rotation rotation) {
        if (this.m_Rotation != rotation) {
            this.m_Rotation = rotation;
            requestLayout();
            invalidate();
        }
    }
}
