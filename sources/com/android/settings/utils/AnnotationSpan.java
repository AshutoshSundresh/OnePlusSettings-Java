package com.android.settings.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import com.android.settings.utils.AnnotationSpan;

public class AnnotationSpan extends URLSpan {
    private final View.OnClickListener mClickListener;

    private AnnotationSpan(View.OnClickListener onClickListener) {
        super((String) null);
        this.mClickListener = onClickListener;
    }

    public void onClick(View view) {
        View.OnClickListener onClickListener = this.mClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setUnderlineText(false);
    }

    public static CharSequence linkify(CharSequence charSequence, LinkInfo... linkInfoArr) {
        SpannableString spannableString = new SpannableString(charSequence);
        Annotation[] annotationArr = (Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);
        for (Annotation annotation : annotationArr) {
            String value = annotation.getValue();
            int spanStart = spannableString.getSpanStart(annotation);
            int spanEnd = spannableString.getSpanEnd(annotation);
            AnnotationSpan annotationSpan = null;
            int length = linkInfoArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                LinkInfo linkInfo = linkInfoArr[i];
                if (linkInfo.mAnnotation.equals(value)) {
                    annotationSpan = new AnnotationSpan(linkInfo.mListener);
                    break;
                }
                i++;
            }
            if (annotationSpan != null) {
                spannableStringBuilder.setSpan(annotationSpan, spanStart, spanEnd, spannableString.getSpanFlags(annotationSpan));
            }
        }
        return spannableStringBuilder;
    }

    public static CharSequence linkifyRemoveFingerprintUrl(CharSequence charSequence, LinkInfo... linkInfoArr) {
        SpannableString spannableString = new SpannableString(charSequence);
        Annotation[] annotationArr = (Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);
        for (Annotation annotation : annotationArr) {
            String value = annotation.getValue();
            int spanStart = spannableString.getSpanStart(annotation);
            int spanEnd = spannableString.getSpanEnd(annotation);
            AnnotationSpan annotationSpan = null;
            int length = linkInfoArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                LinkInfo linkInfo = linkInfoArr[i];
                if (linkInfo.mAnnotation.equals("url") && spanStart >= 0 && spanStart < spanEnd && spanEnd <= spannableString.length()) {
                    spannableStringBuilder.delete(spanStart, spanEnd);
                    Log.d("AnnotationSpan", "refresh summary");
                    return spannableStringBuilder;
                } else if (linkInfo.mAnnotation.equals(value)) {
                    annotationSpan = new AnnotationSpan(linkInfo.mListener);
                    break;
                } else {
                    i++;
                }
            }
            if (annotationSpan != null) {
                spannableStringBuilder.setSpan(annotationSpan, spanStart, spanEnd, spannableString.getSpanFlags(annotationSpan));
            }
        }
        return spannableStringBuilder;
    }

    public static class LinkInfo {
        private final Boolean mActionable;
        private final String mAnnotation;
        private final View.OnClickListener mListener;

        public LinkInfo(String str, View.OnClickListener onClickListener) {
            this.mAnnotation = str;
            this.mListener = onClickListener;
            this.mActionable = Boolean.TRUE;
        }

        public LinkInfo(Context context, String str, Intent intent) {
            this.mAnnotation = str;
            if (intent != null) {
                this.mActionable = Boolean.valueOf(context.getPackageManager().resolveActivity(intent, 0) != null);
            } else {
                this.mActionable = Boolean.FALSE;
            }
            if (!this.mActionable.booleanValue()) {
                this.mListener = null;
            } else {
                this.mListener = new View.OnClickListener(intent) {
                    /* class com.android.settings.utils.$$Lambda$AnnotationSpan$LinkInfo$z7jQ60cPKy5FsRC4nTEr8I88qP0 */
                    public final /* synthetic */ Intent f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(View view) {
                        AnnotationSpan.LinkInfo.lambda$new$0(this.f$0, view);
                    }
                };
            }
        }

        static /* synthetic */ void lambda$new$0(Intent intent, View view) {
            try {
                view.startActivityForResult(intent, 0);
            } catch (ActivityNotFoundException unused) {
                Log.w("AnnotationSpan.LinkInfo", "Activity was not found for intent, " + intent);
            }
        }

        public boolean isActionable() {
            return this.mActionable.booleanValue();
        }
    }
}
