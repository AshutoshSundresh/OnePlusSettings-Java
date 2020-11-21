package com.android.settings;

import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class LinkifyUtils {

    public interface OnClickListener {
        void onClick();
    }

    public static boolean linkify(TextView textView, StringBuilder sb, final OnClickListener onClickListener) {
        int indexOf = sb.indexOf("LINK_BEGIN");
        if (indexOf == -1) {
            textView.setText(sb);
            return false;
        }
        sb.delete(indexOf, indexOf + 10);
        int indexOf2 = sb.indexOf("LINK_END");
        if (indexOf2 == -1) {
            textView.setText(sb);
            return false;
        }
        sb.delete(indexOf2, indexOf2 + 8);
        textView.setText(sb.toString(), TextView.BufferType.SPANNABLE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        ((Spannable) textView.getText()).setSpan(new ClickableSpan() {
            /* class com.android.settings.LinkifyUtils.AnonymousClass1 */

            public void onClick(View view) {
                OnClickListener.this.onClick();
            }

            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setUnderlineText(true);
            }
        }, indexOf, indexOf2, 33);
        return true;
    }
}
