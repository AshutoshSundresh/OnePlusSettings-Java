package androidx.leanback.widget;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.util.Property;
import android.view.ActionMode;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import androidx.core.widget.TextViewCompat;
import androidx.leanback.R$drawable;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* access modifiers changed from: package-private */
@SuppressLint({"AppCompatCustomView"})
public class StreamingTextView extends EditText {
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\S+");
    private static final Property<StreamingTextView, Integer> STREAM_POSITION_PROPERTY = new Property<StreamingTextView, Integer>(Integer.class, "streamPosition") {
        /* class androidx.leanback.widget.StreamingTextView.AnonymousClass1 */

        public Integer get(StreamingTextView streamingTextView) {
            return Integer.valueOf(streamingTextView.getStreamPosition());
        }

        public void set(StreamingTextView streamingTextView, Integer num) {
            streamingTextView.setStreamPosition(num.intValue());
        }
    };
    Bitmap mOneDot;
    final Random mRandom = new Random();
    int mStreamPosition;
    private ObjectAnimator mStreamingAnimation;
    Bitmap mTwoDot;

    public StreamingTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StreamingTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mOneDot = getScaledBitmap(R$drawable.lb_text_dot_one, 1.3f);
        this.mTwoDot = getScaledBitmap(R$drawable.lb_text_dot_two, 1.3f);
        reset();
    }

    private Bitmap getScaledBitmap(int i, float f) {
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), i);
        return Bitmap.createScaledBitmap(decodeResource, (int) (((float) decodeResource.getWidth()) * f), (int) (((float) decodeResource.getHeight()) * f), false);
    }

    public void reset() {
        this.mStreamPosition = -1;
        cancelStreamAnimation();
        setText("");
    }

    public void updateRecognizedText(String str, String str2) {
        if (str == null) {
            str = "";
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        if (str2 != null) {
            int length = spannableStringBuilder.length();
            spannableStringBuilder.append((CharSequence) str2);
            addDottySpans(spannableStringBuilder, str2, length);
        }
        this.mStreamPosition = Math.max(str.length(), this.mStreamPosition);
        updateText(new SpannedString(spannableStringBuilder));
        startStreamAnimation();
    }

    /* access modifiers changed from: package-private */
    public int getStreamPosition() {
        return this.mStreamPosition;
    }

    /* access modifiers changed from: package-private */
    public void setStreamPosition(int i) {
        this.mStreamPosition = i;
        invalidate();
    }

    private void startStreamAnimation() {
        cancelStreamAnimation();
        int streamPosition = getStreamPosition();
        int length = length();
        int i = length - streamPosition;
        if (i > 0) {
            if (this.mStreamingAnimation == null) {
                ObjectAnimator objectAnimator = new ObjectAnimator();
                this.mStreamingAnimation = objectAnimator;
                objectAnimator.setTarget(this);
                this.mStreamingAnimation.setProperty(STREAM_POSITION_PROPERTY);
            }
            this.mStreamingAnimation.setIntValues(streamPosition, length);
            this.mStreamingAnimation.setDuration(((long) i) * 50);
            this.mStreamingAnimation.start();
        }
    }

    private void cancelStreamAnimation() {
        ObjectAnimator objectAnimator = this.mStreamingAnimation;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private void addDottySpans(SpannableStringBuilder spannableStringBuilder, String str, int i) {
        Matcher matcher = SPLIT_PATTERN.matcher(str);
        while (matcher.find()) {
            int start = matcher.start() + i;
            spannableStringBuilder.setSpan(new DottySpan(str.charAt(matcher.start()), start), start, matcher.end() + i, 33);
        }
    }

    public void setFinalRecognizedText(CharSequence charSequence) {
        updateText(charSequence);
    }

    private void updateText(CharSequence charSequence) {
        setText(charSequence);
        bringPointIntoView(length());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("androidx.leanback.widget.StreamingTextView");
    }

    /* access modifiers changed from: private */
    public class DottySpan extends ReplacementSpan {
        private final int mPosition;
        private final int mSeed;

        public DottySpan(int i, int i2) {
            this.mSeed = i;
            this.mPosition = i2;
        }

        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            int measureText = (int) paint.measureText(charSequence, i, i2);
            int width = StreamingTextView.this.mOneDot.getWidth();
            int i6 = width * 2;
            int i7 = measureText / i6;
            int i8 = (measureText % i6) / 2;
            boolean isLayoutRtl = StreamingTextView.isLayoutRtl(StreamingTextView.this);
            StreamingTextView.this.mRandom.setSeed((long) this.mSeed);
            int alpha = paint.getAlpha();
            int i9 = 0;
            while (i9 < i7 && this.mPosition + i9 < StreamingTextView.this.mStreamPosition) {
                float f2 = (float) ((i9 * i6) + i8 + (width / 2));
                float f3 = isLayoutRtl ? ((((float) measureText) + f) - f2) - ((float) width) : f + f2;
                paint.setAlpha((StreamingTextView.this.mRandom.nextInt(4) + 1) * 63);
                if (StreamingTextView.this.mRandom.nextBoolean()) {
                    Bitmap bitmap = StreamingTextView.this.mTwoDot;
                    canvas.drawBitmap(bitmap, f3, (float) (i4 - bitmap.getHeight()), paint);
                } else {
                    Bitmap bitmap2 = StreamingTextView.this.mOneDot;
                    canvas.drawBitmap(bitmap2, f3, (float) (i4 - bitmap2.getHeight()), paint);
                }
                i9++;
            }
            paint.setAlpha(alpha);
        }

        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            return (int) paint.measureText(charSequence, i, i2);
        }
    }

    public static boolean isLayoutRtl(View view) {
        if (Build.VERSION.SDK_INT < 17 || 1 != view.getLayoutDirection()) {
            return false;
        }
        return true;
    }

    public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback(this, callback));
    }
}
