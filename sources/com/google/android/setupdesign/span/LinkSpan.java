package com.google.android.setupdesign.span;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class LinkSpan extends ClickableSpan {
    private final String id;

    @Deprecated
    public interface OnClickListener {
        void onClick(LinkSpan linkSpan);
    }

    public interface OnLinkClickListener {
        boolean onLinkClick(LinkSpan linkSpan);
    }

    public LinkSpan(String str) {
        this.id = str;
    }

    public void onClick(View view) {
        if (!dispatchClick(view)) {
            Log.w("LinkSpan", "Dropping click event. No listener attached.");
        } else if (Build.VERSION.SDK_INT >= 19) {
            view.cancelPendingInputEvents();
        }
        if (view instanceof TextView) {
            CharSequence text = ((TextView) view).getText();
            if (text instanceof Spannable) {
                Selection.setSelection((Spannable) text, 0);
            }
        }
    }

    private boolean dispatchClick(View view) {
        OnClickListener legacyListenerFromContext;
        boolean onLinkClick = view instanceof OnLinkClickListener ? ((OnLinkClickListener) view).onLinkClick(this) : false;
        if (onLinkClick || (legacyListenerFromContext = getLegacyListenerFromContext(view.getContext())) == null) {
            return onLinkClick;
        }
        legacyListenerFromContext.onClick(this);
        return true;
    }

    @Deprecated
    private OnClickListener getLegacyListenerFromContext(Context context) {
        while (!(context instanceof OnClickListener)) {
            if (!(context instanceof ContextWrapper)) {
                return null;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return (OnClickListener) context;
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setUnderlineText(false);
    }

    public String getId() {
        return this.id;
    }
}
