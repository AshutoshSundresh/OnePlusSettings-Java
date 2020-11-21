package com.google.android.material.emptyview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewConfigurationCompat;
import com.google.android.material.R$attr;
import com.google.android.material.R$dimen;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import com.google.android.material.R$styleable;
import com.google.android.material.appbar.CollapsingAppbarLayout;
import com.google.android.material.edgeeffect.SpringNestScrollView;
import com.oneplus.common.OPViewGroupUtils;

public class EmptyPageView extends LinearLayout implements View.OnClickListener {
    private int mActivePointerId;
    private LinearLayout mBaseView;
    private TextView mBottomActionTextView;
    private LinearLayout mContentView;
    private int mEmptyBottomPadding;
    private EmptyImageView mImageView;
    private boolean mIsHomePageStatus;
    private int mLastMotionX;
    private int mLastMotionY;
    private TextView mMiddleActionTextView;
    private final int[] mScrollConsumed;
    private int mScrollDirection;
    private final int[] mScrollOffset;
    private boolean mShowInDetail;
    private View mTempView;
    private TextView mTextView;
    private TextView mTopActionTextView;
    private OnEmptyViewActionButtonClickedListener onActionButtonClickedListener;
    private SpringNestScrollView scrollView;
    private Space spaceView;

    public interface OnEmptyViewActionButtonClickedListener {
        void onEmptyViewActionButtonClicked(int i, int i2);
    }

    public EmptyPageView(Context context) {
        this(context, null);
    }

    public EmptyPageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.emptyPageStyle);
    }

    public EmptyPageView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public EmptyPageView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mActivePointerId = -1;
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mScrollDirection = -1;
        LayoutInflater.from(context).inflate(R$layout.control_empty_view, this);
        initView();
        initTypedArray(context, attributeSet, i, i2);
    }

    private void initTypedArray(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.EmptyPageView, i, i2);
        Drawable drawable = obtainStyledAttributes.getDrawable(R$styleable.EmptyPageView_emptyDrawable);
        String string = obtainStyledAttributes.getString(R$styleable.EmptyPageView_emptyText);
        String string2 = obtainStyledAttributes.getString(R$styleable.EmptyPageView_topActionText);
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.EmptyPageView_topActionClick, true);
        String string3 = obtainStyledAttributes.getString(R$styleable.EmptyPageView_middleActionText);
        boolean z2 = obtainStyledAttributes.getBoolean(R$styleable.EmptyPageView_middleActionClick, true);
        String string4 = obtainStyledAttributes.getString(R$styleable.EmptyPageView_bottomActionText);
        boolean z3 = obtainStyledAttributes.getBoolean(R$styleable.EmptyPageView_bottomActionClick, true);
        setEmptyDrawable(drawable);
        if (obtainStyledAttributes.hasValue(R$styleable.EmptyPageView_topActionColor)) {
            setTopActionColor(obtainStyledAttributes.getColorStateList(R$styleable.EmptyPageView_topActionColor));
        }
        if (obtainStyledAttributes.hasValue(R$styleable.EmptyPageView_middleActionColor)) {
            setMiddleActionColor(obtainStyledAttributes.getColorStateList(R$styleable.EmptyPageView_middleActionColor));
        }
        if (obtainStyledAttributes.hasValue(R$styleable.EmptyPageView_bottomActionColor)) {
            setBottomActionColor(obtainStyledAttributes.getColorStateList(R$styleable.EmptyPageView_bottomActionColor));
        }
        setEmptyText(string);
        setTopActionText(string2);
        this.mTopActionTextView.setClickable(z);
        setMiddleActionText(string3);
        this.mMiddleActionTextView.setClickable(z2);
        setBottomActionText(string4);
        this.mBottomActionTextView.setClickable(z3);
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (View view : OPViewGroupUtils.getAllChildViews(getRootView())) {
            if ((view instanceof CollapsingAppbarLayout) && !this.mShowInDetail) {
                Space space = this.spaceView;
                if (space != null) {
                    space.setVisibility(8);
                }
                this.mIsHomePageStatus = true;
            }
        }
    }

    public void setShowInDetail(boolean z) {
        this.mShowInDetail = z;
    }

    private void initView() {
        Log.i("OPEmptyPageView", "initView !!");
        this.mBaseView = (LinearLayout) findViewById(R$id.empty_base);
        this.mImageView = (EmptyImageView) findViewById(R$id.empty_image);
        this.spaceView = (Space) findViewById(R$id.control_empty_space1);
        this.mTempView = findViewById(R$id.empty_temp);
        this.mTextView = (TextView) findViewById(R$id.empty_content);
        this.mContentView = (LinearLayout) findViewById(R$id.content_view);
        this.mTopActionTextView = (TextView) findViewById(R$id.empty_top_text);
        this.mMiddleActionTextView = (TextView) findViewById(R$id.empty_middle_text);
        this.mBottomActionTextView = (TextView) findViewById(R$id.empty_bottom_text);
        this.mTopActionTextView.setOnClickListener(this);
        this.mMiddleActionTextView.setOnClickListener(this);
        this.mBottomActionTextView.setOnClickListener(this);
        ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(getContext()));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        LinearLayout linearLayout;
        if (this.mImageView.getVisibility() == 8 && !this.mImageView.isSetGoneFromUser()) {
            this.mImageView.setVisibility(0);
            if (this.mBaseView.getOrientation() == 0) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mContentView.getLayoutParams();
                layoutParams.width = 0;
                this.mContentView.setLayoutParams(layoutParams);
            }
        }
        if (this.mBaseView.getOrientation() == 1) {
            super.onMeasure(i, i2);
            if (this.mImageView.getVisibility() == 0) {
                int childCount = this.mBaseView.getChildCount();
                int measuredHeight = this.mBaseView.getMeasuredHeight();
                if (this.scrollView != null) {
                    measuredHeight = getMeasuredHeight() - this.scrollView.getPaddingBottom();
                }
                this.mBaseView.forceLayout();
                LinearLayout linearLayout2 = this.mBaseView;
                linearLayout2.measure(View.MeasureSpec.makeMeasureSpec(linearLayout2.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(measuredHeight + 1, 1073741824));
                int i3 = 0;
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = this.mBaseView.getChildAt(i4);
                    if (childAt.getVisibility() != 8) {
                        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) childAt.getLayoutParams();
                        i3 = i3 + childAt.getMeasuredHeight() + layoutParams2.topMargin + layoutParams2.bottomMargin;
                    }
                }
                if (this.mIsHomePageStatus) {
                    i3 = getResources().getDimensionPixelOffset(R$dimen.control_empty_image_height) + getResources().getDimensionPixelOffset(R$dimen.control_empty_image_margin_bottom) + getResources().getDimensionPixelOffset(R$dimen.op_control_margin_space5) + getResources().getDimensionPixelOffset(R$dimen.op_control_margin_space4) + getResources().getDimensionPixelOffset(R$dimen.control_empty_home_status_height);
                }
                Log.d("chenhb", "totalHeight = " + i3 + ",baseViewHeight = " + measuredHeight);
                if (i3 > measuredHeight) {
                    Space space = this.spaceView;
                    if (space != null) {
                        space.setVisibility(0);
                    }
                    this.mImageView.setHideForNoSpace();
                    SpringNestScrollView springNestScrollView = this.scrollView;
                    if (springNestScrollView != null) {
                        View findViewById = springNestScrollView.findViewById(R$id.empty_image);
                        if (findViewById != null) {
                            findViewById.setVisibility(8);
                        }
                        View findViewById2 = this.scrollView.findViewById(R$id.control_empty_space1);
                        if (findViewById2 != null) {
                            findViewById2.setVisibility(0);
                        }
                    }
                }
                this.mBaseView.forceLayout();
                LinearLayout linearLayout3 = this.mBaseView;
                linearLayout3.measure(View.MeasureSpec.makeMeasureSpec(linearLayout3.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(measuredHeight, 1073741824));
            }
        } else if (this.mBaseView.getOrientation() == 0) {
            super.onMeasure(i, i2);
            if (this.mImageView.getVisibility() == 0 && (linearLayout = this.mContentView) != null) {
                int childCount2 = linearLayout.getChildCount();
                int measuredWidth = this.mBaseView.getMeasuredWidth();
                int measuredHeight2 = this.mBaseView.getMeasuredHeight();
                int measuredHeight3 = this.mContentView.getMeasuredHeight();
                this.mContentView.forceLayout();
                LinearLayout linearLayout4 = this.mContentView;
                linearLayout4.measure(View.MeasureSpec.makeMeasureSpec(linearLayout4.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(measuredHeight3 + 1, 1073741824));
                int i5 = 0;
                for (int i6 = 0; i6 < childCount2; i6++) {
                    View childAt2 = this.mContentView.getChildAt(i6);
                    if (childAt2.getVisibility() != 8) {
                        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) childAt2.getLayoutParams();
                        i5 = i5 + childAt2.getMeasuredHeight() + layoutParams3.topMargin + layoutParams3.bottomMargin;
                    }
                }
                if (i5 > measuredHeight3) {
                    this.mImageView.setHideForNoSpace();
                    LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) this.mContentView.getLayoutParams();
                    layoutParams4.width = measuredWidth;
                    this.mContentView.setLayoutParams(layoutParams4);
                    this.mBaseView.forceLayout();
                    this.mBaseView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(measuredHeight2, 1073741824));
                    return;
                }
                this.mContentView.forceLayout();
                LinearLayout linearLayout5 = this.mContentView;
                linearLayout5.measure(View.MeasureSpec.makeMeasureSpec(linearLayout5.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(measuredHeight3, 1073741824));
            }
        } else {
            super.onMeasure(i, i2);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    public ImageView getEmptyImageView() {
        return this.mImageView;
    }

    public void setEmptyImageVisibility(int i) {
        this.mImageView.setVisibility(i);
        if (i == 8) {
            this.mTempView.setVisibility(0);
        } else {
            this.mTempView.setVisibility(8);
        }
    }

    public void setEmptyDrawable(Drawable drawable) {
        EmptyImageView emptyImageView = this.mImageView;
        if (emptyImageView != null) {
            emptyImageView.setImageDrawable(drawable);
        }
    }

    public TextView getEmptyTextView() {
        return this.mTextView;
    }

    public void setEmptyText(CharSequence charSequence) {
        TextView textView = this.mTextView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public TextView getTopActionTextView() {
        return this.mTopActionTextView;
    }

    public void setTopActionText(CharSequence charSequence) {
        if (this.mTopActionTextView != null) {
            if (TextUtils.isEmpty(charSequence)) {
                this.mTopActionTextView.setVisibility(8);
            } else {
                this.mTopActionTextView.setVisibility(0);
            }
            this.mTopActionTextView.setText(charSequence);
        }
    }

    public void setTopActionColor(int i) {
        TextView textView = this.mTopActionTextView;
        if (textView != null) {
            textView.setTextColor(i);
        }
    }

    public void setTopActionColor(ColorStateList colorStateList) {
        TextView textView = this.mTopActionTextView;
        if (textView != null) {
            textView.setTextColor(colorStateList);
        }
    }

    public void setTopActionOnClickListener(View.OnClickListener onClickListener) {
        TextView textView = this.mTopActionTextView;
        if (textView != null) {
            if (!textView.isClickable()) {
                this.mTopActionTextView.setClickable(true);
            }
            this.mTopActionTextView.setOnClickListener(onClickListener);
        }
    }

    public TextView getMiddleActionTextView() {
        return this.mMiddleActionTextView;
    }

    public void setMiddleActionText(CharSequence charSequence) {
        if (this.mMiddleActionTextView != null) {
            if (TextUtils.isEmpty(charSequence)) {
                this.mMiddleActionTextView.setVisibility(8);
            } else {
                this.mMiddleActionTextView.setVisibility(0);
            }
            this.mMiddleActionTextView.setText(charSequence);
        }
    }

    public void setMiddleActionColor(int i) {
        TextView textView = this.mMiddleActionTextView;
        if (textView != null) {
            textView.setTextColor(i);
        }
    }

    public void setMiddleActionColor(ColorStateList colorStateList) {
        TextView textView = this.mMiddleActionTextView;
        if (textView != null) {
            textView.setTextColor(colorStateList);
        }
    }

    public void setMiddleActionOnClickListener(View.OnClickListener onClickListener) {
        TextView textView = this.mMiddleActionTextView;
        if (textView != null) {
            if (!textView.isClickable()) {
                this.mMiddleActionTextView.setClickable(true);
            }
            this.mMiddleActionTextView.setOnClickListener(onClickListener);
        }
    }

    public TextView getBottomActionTextView() {
        return this.mBottomActionTextView;
    }

    public void setBottomActionOnClickListener(View.OnClickListener onClickListener) {
        TextView textView = this.mBottomActionTextView;
        if (textView != null) {
            if (!textView.isClickable()) {
                this.mBottomActionTextView.setClickable(true);
            }
            this.mBottomActionTextView.setOnClickListener(onClickListener);
        }
    }

    public void setBottomActionText(CharSequence charSequence) {
        if (this.mBottomActionTextView != null) {
            if (TextUtils.isEmpty(charSequence)) {
                this.mBottomActionTextView.setVisibility(8);
            } else {
                this.mBottomActionTextView.setVisibility(0);
            }
            this.mBottomActionTextView.setText(charSequence);
        }
    }

    public void setBottomActionColor(int i) {
        TextView textView = this.mBottomActionTextView;
        if (textView != null) {
            textView.setTextColor(i);
        }
    }

    public void setBottomActionColor(ColorStateList colorStateList) {
        TextView textView = this.mBottomActionTextView;
        if (textView != null) {
            textView.setTextColor(colorStateList);
        }
    }

    public void setActionClickedListener(OnEmptyViewActionButtonClickedListener onEmptyViewActionButtonClickedListener) {
        this.onActionButtonClickedListener = onEmptyViewActionButtonClickedListener;
    }

    public void setImage(int i) {
        EmptyImageView emptyImageView = this.mImageView;
        if (emptyImageView != null) {
            emptyImageView.setImageResource(i);
            if (i == 0) {
                this.mImageView.setVisibility(8);
            } else {
                this.mImageView.setVisibility(0);
            }
        }
    }

    public void setDescription(int i) {
        if (i == 0) {
            this.mTextView.setText((CharSequence) null);
            this.mTextView.setVisibility(8);
            return;
        }
        this.mTextView.setText(i);
        this.mTextView.setVisibility(0);
    }

    public int getEmptyPaddingBottom() {
        return this.mEmptyBottomPadding;
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        this.mEmptyBottomPadding = i4;
        super.setPadding(i, i2, i3, i4);
    }

    public void setEmptyPadding(int i, int i2, int i3, int i4) {
        SpringNestScrollView springNestScrollView = this.scrollView;
        if (springNestScrollView != null) {
            View findViewById = springNestScrollView.findViewById(R$id.empty_base);
            if (findViewById != null) {
                findViewById.setPadding(i, i2, i3, 0);
                SpringNestScrollView springNestScrollView2 = this.scrollView;
                springNestScrollView2.setPadding(springNestScrollView2.getPaddingLeft(), this.scrollView.getPaddingTop(), this.scrollView.getPaddingRight(), this.mEmptyBottomPadding - i2);
                return;
            }
            return;
        }
        super.setPadding(i, i2, i3, i4);
    }

    public void onClick(View view) {
        int i;
        if (this.onActionButtonClickedListener != null) {
            int i2 = -1;
            if (this.mTopActionTextView == view) {
                i = 0;
            } else if (this.mMiddleActionTextView == view) {
                i = 1;
            } else {
                i = this.mBottomActionTextView == view ? 2 : -1;
            }
            Object tag = view.getTag();
            if (tag instanceof Integer) {
                i2 = ((Integer) tag).intValue();
            }
            this.onActionButtonClickedListener.onEmptyViewActionButtonClicked(i, i2);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (Build.VERSION.SDK_INT >= 21 && isNestedScrollingEnabled()) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.mScrollDirection = -1;
                startNestedScroll(2);
                this.mLastMotionX = (int) motionEvent.getX();
                this.mLastMotionY = (int) motionEvent.getY();
                this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, 0);
            } else if (actionMasked == 1 || actionMasked == 3) {
                stopNestedScroll();
                this.mScrollDirection = -1;
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        if (Build.VERSION.SDK_INT < 21 || !isNestedScrollingEnabled()) {
            return super.onTouchEvent(motionEvent);
        }
        startNestedScroll(2);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId);
                    try {
                        float abs = Math.abs(MotionEventCompat.getX(motionEvent, findPointerIndex) - ((float) this.mLastMotionX));
                        float y = MotionEventCompat.getY(motionEvent, findPointerIndex);
                        float abs2 = Math.abs(y - ((float) this.mLastMotionY));
                        if (this.mScrollDirection == -1) {
                            if (abs < abs2) {
                                this.mScrollDirection = 0;
                            } else {
                                this.mScrollDirection = 1;
                            }
                            z = true;
                        } else {
                            z = false;
                        }
                        if (this.mScrollDirection == 0) {
                            if (!z) {
                                int i = (int) y;
                                int i2 = this.mLastMotionY - i;
                                this.mLastMotionY = i;
                                if (dispatchNestedPreScroll(0, i2, this.mScrollConsumed, this.mScrollOffset)) {
                                    int[] iArr = this.mScrollOffset;
                                    i2 += iArr[1];
                                    this.mLastMotionY -= iArr[1];
                                }
                                if (dispatchNestedScroll(0, 0, 0, i2, this.mScrollOffset)) {
                                    this.mLastMotionY -= this.mScrollOffset[1];
                                }
                            } else if (Float.compare(abs2, Float.NaN) != 0) {
                                this.mLastMotionY = (int) y;
                            }
                            disallowInterceptParent();
                        }
                    } catch (Exception e) {
                        Log.e("OPEmptyPageView", "onTouchEvent MotionEventCompat.getX Exception e = " + e.toString());
                        return false;
                    }
                } else if (actionMasked != 3) {
                    if (actionMasked == 5) {
                        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
                        this.mLastMotionX = (int) MotionEventCompat.getX(motionEvent, actionIndex);
                        this.mLastMotionY = (int) MotionEventCompat.getY(motionEvent, actionIndex);
                        this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                    } else if (actionMasked == 6) {
                        onSecondaryPointerUp(motionEvent);
                    }
                }
            }
            this.mScrollDirection = -1;
        } else {
            this.mLastMotionX = (int) motionEvent.getX();
            this.mLastMotionY = (int) motionEvent.getY();
            this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, 0);
            this.mScrollDirection = -1;
        }
        return true;
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (MotionEventCompat.getPointerId(motionEvent, actionIndex) == this.mActivePointerId) {
            int i = actionIndex == 0 ? 1 : 0;
            this.mLastMotionX = (int) MotionEventCompat.getX(motionEvent, i);
            this.mLastMotionY = (int) MotionEventCompat.getY(motionEvent, i);
            this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, i);
        }
    }

    private void disallowInterceptParent() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }
}
