package com.google.android.setupdesign.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;
import com.google.android.setupdesign.accessibility.LinkAccessibilityHelper;
import com.google.android.setupdesign.span.LinkSpan;
import com.google.android.setupdesign.span.SpanHelper;
import com.google.android.setupdesign.view.TouchableMovementMethod;

public class RichTextView extends AppCompatTextView implements LinkSpan.OnLinkClickListener {
    private LinkAccessibilityHelper accessibilityHelper;
    private LinkSpan.OnLinkClickListener onLinkClickListener;

    public static CharSequence getRichText(Context context, CharSequence charSequence) {
        if (!(charSequence instanceof Spanned)) {
            return charSequence;
        }
        SpannableString spannableString = new SpannableString(charSequence);
        Annotation[] annotationArr = (Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class);
        for (Annotation annotation : annotationArr) {
            String key = annotation.getKey();
            if ("textAppearance".equals(key)) {
                int identifier = context.getResources().getIdentifier(annotation.getValue(), "style", context.getPackageName());
                if (identifier == 0) {
                    Log.w("RichTextView", "Cannot find resource: " + identifier);
                }
                SpanHelper.replaceSpan(spannableString, annotation, new TextAppearanceSpan(context, identifier));
            } else if ("link".equals(key)) {
                SpanHelper.replaceSpan(spannableString, annotation, new LinkSpan(annotation.getValue()), new TypefaceSpan("sans-serif-medium"));
            }
        }
        return spannableString;
    }

    public RichTextView(Context context) {
        super(context);
        init();
    }

    public RichTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        LinkAccessibilityHelper linkAccessibilityHelper = new LinkAccessibilityHelper(this);
        this.accessibilityHelper = linkAccessibilityHelper;
        ViewCompat.setAccessibilityDelegate(this, linkAccessibilityHelper);
    }

    @Override // android.widget.TextView
    public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
        CharSequence richText = getRichText(getContext(), charSequence);
        super.setText(richText, bufferType);
        boolean hasLinks = hasLinks(richText);
        if (hasLinks) {
            setMovementMethod(TouchableMovementMethod.TouchableLinkMovementMethod.getInstance());
        } else {
            setMovementMethod(null);
        }
        setFocusable(hasLinks);
        if (Build.VERSION.SDK_INT >= 25) {
            setRevealOnFocusHint(false);
            setFocusableInTouchMode(hasLinks);
        }
    }

    private boolean hasLinks(CharSequence charSequence) {
        if (!(charSequence instanceof Spanned) || ((ClickableSpan[]) ((Spanned) charSequence).getSpans(0, charSequence.length(), ClickableSpan.class)).length <= 0) {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        MovementMethod movementMethod = getMovementMethod();
        if (movementMethod instanceof TouchableMovementMethod) {
            TouchableMovementMethod touchableMovementMethod = (TouchableMovementMethod) movementMethod;
            if (touchableMovementMethod.getLastTouchEvent() == motionEvent) {
                return touchableMovementMethod.isLastTouchEventHandled();
            }
        }
        return onTouchEvent;
    }

    /* access modifiers changed from: protected */
    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        LinkAccessibilityHelper linkAccessibilityHelper = this.accessibilityHelper;
        if (linkAccessibilityHelper == null || !linkAccessibilityHelper.dispatchHoverEvent(motionEvent)) {
            return super.dispatchHoverEvent(motionEvent);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.AppCompatTextView
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (Build.VERSION.SDK_INT >= 17) {
            int[] drawableState = getDrawableState();
            Drawable[] compoundDrawablesRelative = getCompoundDrawablesRelative();
            for (Drawable drawable : compoundDrawablesRelative) {
                if (drawable != null && drawable.setState(drawableState)) {
                    invalidateDrawable(drawable);
                }
            }
        }
    }

    public void setOnLinkClickListener(LinkSpan.OnLinkClickListener onLinkClickListener2) {
        this.onLinkClickListener = onLinkClickListener2;
    }

    public LinkSpan.OnLinkClickListener getOnLinkClickListener() {
        return this.onLinkClickListener;
    }

    @Override // com.google.android.setupdesign.span.LinkSpan.OnLinkClickListener
    public boolean onLinkClick(LinkSpan linkSpan) {
        LinkSpan.OnLinkClickListener onLinkClickListener2 = this.onLinkClickListener;
        if (onLinkClickListener2 != null) {
            return onLinkClickListener2.onLinkClick(linkSpan);
        }
        return false;
    }
}
