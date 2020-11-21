package com.android.settingslib.widget;

import android.content.Context;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class LinkTextView extends TextView {
    public LinkTextView(Context context) {
        this(context, null);
    }

    public LinkTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.widget.TextView
    public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
        super.setText(charSequence, bufferType);
        if ((charSequence instanceof Spanned) && ((ClickableSpan[]) ((Spanned) charSequence).getSpans(0, charSequence.length(), ClickableSpan.class)).length > 0) {
            setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
