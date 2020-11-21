package com.google.android.material.tooltipview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.R$color;
import com.google.android.material.R$dimen;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;

public class TooltipView extends LinearLayout {
    private int color;
    private int mArrowHeight;
    private Path mArrowPath;
    private int mArrowWidth;
    private RectF mBody;
    private ImageView mCancelAction;
    private LinearLayout mContentView;
    private int mDirection;
    private TextView mMessage;
    private Paint mPaint;
    private float mPercent;
    private int mRadius;
    private PorterDuffXfermode porterDuffXfermode;

    public TooltipView(Context context) {
        this(context, null);
    }

    public TooltipView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mBody = new RectF();
        this.mPercent = 0.5f;
        this.mDirection = 3;
        LayoutInflater.from(context).inflate(R$layout.control_tooltip_view, this);
        initView();
        setWillNotDraw(false);
        this.mRadius = getResources().getDimensionPixelSize(R$dimen.op_control_radius_r12);
        this.mArrowHeight = getResources().getDimensionPixelSize(R$dimen.control_tooltip_height);
        this.mArrowWidth = getResources().getDimensionPixelSize(R$dimen.control_tooltip_width);
        initPaint();
        setDirection(this.mDirection, 0.5f);
    }

    public void setmMessage(CharSequence charSequence) {
        this.mMessage.setText(charSequence);
    }

    public void setWithCancelAction(View.OnClickListener onClickListener) {
        this.mCancelAction.setVisibility(0);
        this.mCancelAction.setOnClickListener(onClickListener);
    }

    private void initView() {
        this.mContentView = (LinearLayout) findViewById(R$id.content);
        this.mMessage = (TextView) findViewById(R$id.message);
        this.mCancelAction = (ImageView) findViewById(R$id.tooltips_cancel);
    }

    private void initPaint() {
        setLayerType(1, null);
        this.porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setStyle(Paint.Style.FILL);
        int color2 = getResources().getColor(R$color.op_control_system_color_tips_default);
        this.color = color2;
        this.mPaint.setColor(color2);
        this.mPaint.setAlpha(221);
        this.mPaint.setAntiAlias(true);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.save();
        int i = this.mDirection;
        if (3 == i) {
            canvas.translate(this.mPercent * ((float) getWidth()), 0.0f);
        } else if (4 == i) {
            canvas.translate(this.mPercent * ((float) getWidth()), (float) (getHeight() - this.mArrowHeight));
        } else if (1 == i) {
            canvas.translate(0.0f, this.mPercent * ((float) getHeight()));
        } else if (2 == i) {
            canvas.translate((float) (getWidth() - this.mArrowHeight), this.mPercent * ((float) getHeight()));
        }
        canvas.drawPath(this.mArrowPath, this.mPaint);
        canvas.restore();
        this.mPaint.setXfermode(this.porterDuffXfermode);
        int i2 = this.mDirection;
        if (3 == i2) {
            this.mBody.set(0.0f, (float) this.mArrowHeight, (float) getWidth(), (float) getHeight());
        } else if (4 == i2) {
            this.mBody.set(0.0f, 0.0f, (float) getWidth(), (float) (getHeight() - this.mArrowHeight));
        } else if (1 == i2) {
            this.mBody.set((float) this.mArrowHeight, 0.0f, (float) getWidth(), (float) getHeight());
        } else if (2 == i2) {
            this.mBody.set(0.0f, 0.0f, (float) (getWidth() - this.mArrowHeight), (float) getHeight());
        }
        RectF rectF = this.mBody;
        int i3 = this.mRadius;
        canvas.drawRoundRect(rectF, (float) i3, (float) i3, this.mPaint);
        this.mPaint.setXfermode(null);
    }

    public void setDirection(int i, float f) {
        Point point;
        Point point2;
        Point point3;
        Point point4;
        Point point5;
        Point point6;
        Point point7;
        Point point8;
        Point point9;
        Point point10;
        Point point11;
        Point point12;
        this.mDirection = i;
        this.mPercent = f;
        if (f > 1.0f) {
            this.mPercent = 1.0f;
        } else if (f < 0.0f) {
            this.mPercent = 0.0f;
        }
        Path path = new Path();
        this.mArrowPath = path;
        path.setFillType(Path.FillType.EVEN_ODD);
        int i2 = this.mDirection;
        if (3 == i2) {
            if (getWidth() != 0 && (this.mPercent * ((float) getWidth())) + ((float) (this.mArrowWidth / 2)) + ((float) this.mRadius) > ((float) getWidth())) {
                this.mPercent = 1.0f;
                point11 = new Point(0, 0);
                point10 = new Point(-this.mArrowWidth, this.mArrowHeight);
                point12 = new Point(0, this.mArrowHeight + this.mRadius);
            } else if (getWidth() == 0 || this.mPercent * ((float) getWidth()) >= ((float) ((this.mArrowWidth / 2) + this.mRadius))) {
                point11 = new Point(0, 0);
                point10 = new Point((-this.mArrowWidth) / 2, this.mArrowHeight);
                point12 = new Point(this.mArrowWidth / 2, this.mArrowHeight);
            } else {
                this.mPercent = 0.0f;
                point11 = new Point(0, 0);
                point10 = new Point(0, this.mArrowHeight + this.mRadius);
                point12 = new Point(this.mArrowWidth, this.mArrowHeight);
            }
            this.mArrowPath.moveTo((float) point11.x, (float) point11.y);
            this.mArrowPath.lineTo((float) point10.x, (float) point10.y);
            this.mArrowPath.lineTo((float) point12.x, (float) point12.y);
            this.mArrowPath.lineTo((float) point11.x, (float) point11.y);
        } else if (4 == i2) {
            if (getWidth() != 0 && (this.mPercent * ((float) getWidth())) + ((float) (this.mArrowWidth / 2)) + ((float) this.mRadius) > ((float) getWidth())) {
                this.mPercent = 1.0f;
                point8 = new Point(0, this.mArrowHeight);
                point7 = new Point(-this.mArrowWidth, 0);
                point9 = new Point(0, -this.mRadius);
            } else if (getWidth() == 0 || this.mPercent * ((float) getWidth()) >= ((float) ((this.mArrowWidth / 2) + this.mRadius))) {
                point8 = new Point(0, this.mArrowHeight);
                point7 = new Point((-this.mArrowWidth) / 2, 0);
                point9 = new Point(this.mArrowWidth / 2, 0);
            } else {
                this.mPercent = 0.0f;
                point8 = new Point(0, this.mArrowHeight);
                point7 = new Point(0, -this.mRadius);
                point9 = new Point(this.mArrowWidth, 0);
            }
            this.mArrowPath.moveTo((float) point8.x, (float) point8.y);
            this.mArrowPath.lineTo((float) point7.x, (float) point7.y);
            this.mArrowPath.lineTo((float) point9.x, (float) point9.y);
            this.mArrowPath.lineTo((float) point8.x, (float) point8.y);
        } else if (1 == i2) {
            if (getHeight() != 0 && (this.mPercent * ((float) getHeight())) + ((float) (this.mArrowWidth / 2)) + ((float) this.mRadius) > ((float) getHeight())) {
                this.mPercent = 1.0f;
                point5 = new Point(0, 0);
                point4 = new Point(this.mArrowHeight + this.mRadius, 0);
                point6 = new Point(this.mArrowHeight, -this.mArrowWidth);
            } else if (getHeight() == 0 || this.mPercent * ((float) getHeight()) >= ((float) ((this.mArrowWidth / 2) + this.mRadius))) {
                point5 = new Point(0, 0);
                point4 = new Point(this.mArrowHeight, this.mArrowWidth / 2);
                point6 = new Point(this.mArrowHeight, (-this.mArrowWidth) / 2);
            } else {
                this.mPercent = 0.0f;
                point5 = new Point(0, 0);
                point4 = new Point(this.mArrowHeight + this.mRadius, 0);
                point6 = new Point(this.mArrowHeight, this.mArrowWidth);
            }
            this.mArrowPath.moveTo((float) point5.x, (float) point5.y);
            this.mArrowPath.lineTo((float) point4.x, (float) point4.y);
            this.mArrowPath.lineTo((float) point6.x, (float) point6.y);
            this.mArrowPath.lineTo((float) point5.x, (float) point5.y);
        } else if (2 == i2) {
            if (getHeight() != 0 && (this.mPercent * ((float) getHeight())) + ((float) (this.mArrowWidth / 2)) + ((float) this.mRadius) > ((float) getHeight())) {
                this.mPercent = 1.0f;
                point2 = new Point(this.mArrowHeight, 0);
                point = new Point(-this.mRadius, 0);
                point3 = new Point(0, -this.mArrowWidth);
            } else if (getHeight() == 0 || this.mPercent * ((float) getHeight()) >= ((float) ((this.mArrowWidth / 2) + this.mRadius))) {
                point2 = new Point(this.mArrowHeight, 0);
                point = new Point(0, (-this.mArrowWidth) / 2);
                point3 = new Point(0, this.mArrowWidth / 2);
            } else {
                this.mPercent = 0.0f;
                point2 = new Point(this.mArrowHeight, 0);
                point = new Point(-this.mRadius, 0);
                point3 = new Point(0, this.mArrowWidth);
            }
            this.mArrowPath.moveTo((float) point2.x, (float) point2.y);
            this.mArrowPath.lineTo((float) point.x, (float) point.y);
            this.mArrowPath.lineTo((float) point3.x, (float) point3.y);
            this.mArrowPath.lineTo((float) point2.x, (float) point2.y);
        }
        this.mArrowPath.close();
        updateMargin();
        invalidate();
    }

    private void updateMargin() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mContentView.getLayoutParams();
        int i = this.mDirection;
        if (i == 1) {
            layoutParams.leftMargin = this.mArrowHeight;
            layoutParams.topMargin = 0;
            layoutParams.rightMargin = 0;
            layoutParams.bottomMargin = 0;
        } else if (i == 2) {
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = 0;
            layoutParams.rightMargin = this.mArrowHeight;
            layoutParams.bottomMargin = 0;
        } else if (i == 3) {
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = this.mArrowHeight;
            layoutParams.rightMargin = 0;
            layoutParams.bottomMargin = 0;
        } else if (i == 4) {
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = 0;
            layoutParams.rightMargin = 0;
            layoutParams.bottomMargin = this.mArrowHeight;
        }
        this.mContentView.setLayoutParams(layoutParams);
    }
}
