package com.android.settings.display;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.R$styleable;

public class ConversationMessageView extends FrameLayout {
    private TextView mContactIconView;
    private final boolean mIncoming;
    private LinearLayout mMessageBubble;
    private final CharSequence mMessageText;
    private ViewGroup mMessageTextAndInfoView;
    private TextView mMessageTextView;
    private TextView mStatusTextView;
    private final CharSequence mTimestampText;

    public ConversationMessageView(Context context) {
        this(context, null);
    }

    public ConversationMessageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ConversationMessageView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ConversationMessageView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ConversationMessageView);
        this.mIncoming = obtainStyledAttributes.getBoolean(R$styleable.ConversationMessageView_incoming, true);
        this.mMessageText = obtainStyledAttributes.getString(R$styleable.ConversationMessageView_messageText);
        this.mTimestampText = obtainStyledAttributes.getString(R$styleable.ConversationMessageView_timestampText);
        obtainStyledAttributes.getString(R$styleable.ConversationMessageView_iconText);
        obtainStyledAttributes.getColor(R$styleable.ConversationMessageView_iconTextColor, 0);
        obtainStyledAttributes.getColor(R$styleable.ConversationMessageView_iconBackgroundColor, 0);
        obtainStyledAttributes.recycle();
        LayoutInflater.from(context).inflate(C0012R$layout.conversation_message_icon, this);
        LayoutInflater.from(context).inflate(C0012R$layout.conversation_message_content, this);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        this.mMessageBubble = (LinearLayout) findViewById(C0010R$id.message_content);
        this.mMessageTextAndInfoView = (ViewGroup) findViewById(C0010R$id.message_text_and_info);
        this.mMessageTextView = (TextView) findViewById(C0010R$id.message_text);
        this.mStatusTextView = (TextView) findViewById(C0010R$id.message_status);
        this.mContactIconView = (TextView) findViewById(C0010R$id.conversation_icon);
        updateViewContent();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        updateViewAppearance();
        int size = View.MeasureSpec.getSize(i);
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(0, 0);
        this.mContactIconView.measure(makeMeasureSpec2, makeMeasureSpec2);
        int makeMeasureSpec3 = View.MeasureSpec.makeMeasureSpec(Math.max(this.mContactIconView.getMeasuredWidth(), this.mContactIconView.getMeasuredHeight()), 1073741824);
        this.mContactIconView.measure(makeMeasureSpec3, makeMeasureSpec3);
        this.mMessageBubble.measure(View.MeasureSpec.makeMeasureSpec((((size - (this.mContactIconView.getMeasuredWidth() * 2)) - getResources().getDimensionPixelSize(C0007R$dimen.message_bubble_arrow_width)) - getPaddingLeft()) - getPaddingRight(), Integer.MIN_VALUE), makeMeasureSpec);
        setMeasuredDimension(size, Math.max(this.mContactIconView.getMeasuredHeight(), this.mMessageBubble.getMeasuredHeight()) + getPaddingBottom() + getPaddingTop());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        boolean isLayoutRtl = isLayoutRtl(this);
        int measuredWidth = this.mContactIconView.getMeasuredWidth();
        int measuredHeight = this.mContactIconView.getMeasuredHeight();
        int paddingTop = (getPaddingTop() + this.mMessageBubble.getMeasuredHeight()) - measuredHeight;
        int i8 = i3 - i;
        int paddingLeft = ((i8 - measuredWidth) - getPaddingLeft()) - getPaddingRight();
        int measuredHeight2 = this.mMessageBubble.getMeasuredHeight();
        int paddingTop2 = getPaddingTop();
        float applyDimension = TypedValue.applyDimension(1, 10.0f, getResources().getDisplayMetrics());
        if (this.mIncoming) {
            if (isLayoutRtl) {
                i7 = getPaddingRight();
                i5 = (i8 - i7) - measuredWidth;
                i6 = (i5 - paddingLeft) - ((int) applyDimension);
                this.mContactIconView.layout(i5, paddingTop, measuredWidth + i5, measuredHeight + paddingTop);
                this.mMessageBubble.layout(i6, paddingTop2, paddingLeft + i6, measuredHeight2 + paddingTop2);
            }
            i5 = getPaddingLeft();
        } else if (isLayoutRtl) {
            i5 = getPaddingLeft();
        } else {
            i7 = getPaddingRight();
            i5 = (i8 - i7) - measuredWidth;
            i6 = (i5 - paddingLeft) - ((int) applyDimension);
            this.mContactIconView.layout(i5, paddingTop, measuredWidth + i5, measuredHeight + paddingTop);
            this.mMessageBubble.layout(i6, paddingTop2, paddingLeft + i6, measuredHeight2 + paddingTop2);
        }
        i6 = i5 + measuredWidth + ((int) applyDimension);
        this.mContactIconView.layout(i5, paddingTop, measuredWidth + i5, measuredHeight + paddingTop);
        this.mMessageBubble.layout(i6, paddingTop2, paddingLeft + i6, measuredHeight2 + paddingTop2);
    }

    private static boolean isLayoutRtl(View view) {
        return 1 == view.getLayoutDirection();
    }

    private void updateViewContent() {
        this.mMessageTextView.setText(this.mMessageText);
        this.mStatusTextView.setText(this.mTimestampText);
        this.mContactIconView.setBackground(getContext().getDrawable(this.mIncoming ? C0008R$drawable.conversation_message_icon_incoming : C0008R$drawable.conversation_message_icon_outgoing));
    }

    private void updateViewAppearance() {
        int i;
        int i2;
        Resources resources = getResources();
        resources.getDimensionPixelOffset(C0007R$dimen.message_bubble_arrow_width);
        int dimensionPixelOffset = resources.getDimensionPixelOffset(C0007R$dimen.message_text_left_right_padding);
        int dimensionPixelOffset2 = resources.getDimensionPixelOffset(C0007R$dimen.message_text_top_padding);
        int dimensionPixelOffset3 = resources.getDimensionPixelOffset(C0007R$dimen.message_text_bottom_padding);
        int i3 = this.mIncoming ? 8388627 : 8388629;
        int dimensionPixelSize = resources.getDimensionPixelSize(C0007R$dimen.message_padding_default);
        resources.getDimensionPixelOffset(C0007R$dimen.message_metadata_top_padding);
        if (this.mIncoming) {
            i = C0008R$drawable.message_bubble_incoming;
        } else {
            i = C0008R$drawable.message_bubble_outgoing;
        }
        if (this.mIncoming) {
            i2 = C0006R$color.message_bubble_incoming;
        } else {
            i2 = C0006R$color.message_bubble_outgoing;
        }
        Context context = getContext();
        this.mMessageTextAndInfoView.setBackground(getTintedDrawable(context, context.getDrawable(i), context.getColor(i2)));
        this.mMessageTextAndInfoView.setPadding(dimensionPixelOffset, dimensionPixelOffset2, dimensionPixelOffset, dimensionPixelOffset3);
        setPadding(getPaddingLeft(), dimensionPixelSize, getPaddingRight(), 0);
        this.mMessageBubble.setGravity(i3);
        updateTextAppearance();
    }

    private void updateTextAppearance() {
        int i;
        int i2;
        if (this.mIncoming) {
            i = C0006R$color.message_text_incoming;
        } else {
            i = C0006R$color.message_text_outgoing;
        }
        if (this.mIncoming) {
            i2 = C0006R$color.timestamp_text_incoming;
        } else {
            i2 = C0006R$color.timestamp_text_outgoing;
        }
        int color = getContext().getColor(i);
        this.mMessageTextView.setTextColor(color);
        this.mMessageTextView.setLinkTextColor(color);
        this.mStatusTextView.setTextColor(getResources().getColor(i2));
    }

    private static Drawable getTintedDrawable(Context context, Drawable drawable, int i) {
        Drawable.ConstantState constantState = drawable.getConstantState();
        if (constantState != null) {
            drawable = constantState.newDrawable(context.getResources()).mutate();
        }
        drawable.setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }
}
