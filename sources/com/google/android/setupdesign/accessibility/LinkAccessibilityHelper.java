package com.google.android.setupdesign.accessibility;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeProviderCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import java.util.List;

public class LinkAccessibilityHelper extends AccessibilityDelegateCompat {
    private final AccessibilityDelegateCompat delegate;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public LinkAccessibilityHelper(android.widget.TextView r3) {
        /*
            r2 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 26
            if (r0 < r1) goto L_0x000c
            androidx.core.view.AccessibilityDelegateCompat r3 = new androidx.core.view.AccessibilityDelegateCompat
            r3.<init>()
            goto L_0x0012
        L_0x000c:
            com.google.android.setupdesign.accessibility.LinkAccessibilityHelper$PreOLinkAccessibilityHelper r0 = new com.google.android.setupdesign.accessibility.LinkAccessibilityHelper$PreOLinkAccessibilityHelper
            r0.<init>(r3)
            r3 = r0
        L_0x0012:
            r2.<init>(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.setupdesign.accessibility.LinkAccessibilityHelper.<init>(android.widget.TextView):void");
    }

    LinkAccessibilityHelper(AccessibilityDelegateCompat accessibilityDelegateCompat) {
        this.delegate = accessibilityDelegateCompat;
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public void sendAccessibilityEvent(View view, int i) {
        this.delegate.sendAccessibilityEvent(view, i);
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public void sendAccessibilityEventUnchecked(View view, AccessibilityEvent accessibilityEvent) {
        this.delegate.sendAccessibilityEventUnchecked(view, accessibilityEvent);
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public boolean dispatchPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        return this.delegate.dispatchPopulateAccessibilityEvent(view, accessibilityEvent);
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        this.delegate.onPopulateAccessibilityEvent(view, accessibilityEvent);
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        this.delegate.onInitializeAccessibilityEvent(view, accessibilityEvent);
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        this.delegate.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
        return this.delegate.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View view) {
        return this.delegate.getAccessibilityNodeProvider(view);
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
        return this.delegate.performAccessibilityAction(view, i, bundle);
    }

    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        AccessibilityDelegateCompat accessibilityDelegateCompat = this.delegate;
        return (accessibilityDelegateCompat instanceof ExploreByTouchHelper) && ((ExploreByTouchHelper) accessibilityDelegateCompat).dispatchHoverEvent(motionEvent);
    }

    static class PreOLinkAccessibilityHelper extends ExploreByTouchHelper {
        private final Rect tempRect = new Rect();
        private final TextView view;

        PreOLinkAccessibilityHelper(TextView textView) {
            super(textView);
            this.view = textView;
        }

        /* access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public int getVirtualViewAt(float f, float f2) {
            CharSequence text = this.view.getText();
            if (!(text instanceof Spanned)) {
                return Integer.MIN_VALUE;
            }
            Spanned spanned = (Spanned) text;
            int offsetForPosition = getOffsetForPosition(this.view, f, f2);
            ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spanned.getSpans(offsetForPosition, offsetForPosition, ClickableSpan.class);
            if (clickableSpanArr.length == 1) {
                return spanned.getSpanStart(clickableSpanArr[0]);
            }
            return Integer.MIN_VALUE;
        }

        /* access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void getVisibleVirtualViews(List<Integer> list) {
            CharSequence text = this.view.getText();
            if (text instanceof Spanned) {
                Spanned spanned = (Spanned) text;
                for (ClickableSpan clickableSpan : (ClickableSpan[]) spanned.getSpans(0, spanned.length(), ClickableSpan.class)) {
                    list.add(Integer.valueOf(spanned.getSpanStart(clickableSpan)));
                }
            }
        }

        /* access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
            ClickableSpan spanForOffset = getSpanForOffset(i);
            if (spanForOffset != null) {
                accessibilityEvent.setContentDescription(getTextForSpan(spanForOffset));
                return;
            }
            Log.e("LinkAccessibilityHelper", "LinkSpan is null for offset: " + i);
            accessibilityEvent.setContentDescription(this.view.getText());
        }

        /* access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            ClickableSpan spanForOffset = getSpanForOffset(i);
            if (spanForOffset != null) {
                accessibilityNodeInfoCompat.setContentDescription(getTextForSpan(spanForOffset));
            } else {
                Log.e("LinkAccessibilityHelper", "LinkSpan is null for offset: " + i);
                accessibilityNodeInfoCompat.setContentDescription(this.view.getText());
            }
            accessibilityNodeInfoCompat.setFocusable(true);
            accessibilityNodeInfoCompat.setClickable(true);
            getBoundsForSpan(spanForOffset, this.tempRect);
            if (this.tempRect.isEmpty()) {
                Log.e("LinkAccessibilityHelper", "LinkSpan bounds is empty for: " + i);
                this.tempRect.set(0, 0, 1, 1);
            }
            accessibilityNodeInfoCompat.setBoundsInParent(this.tempRect);
            accessibilityNodeInfoCompat.addAction(16);
        }

        /* access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
            if (i2 != 16) {
                return false;
            }
            ClickableSpan spanForOffset = getSpanForOffset(i);
            if (spanForOffset != null) {
                spanForOffset.onClick(this.view);
                return true;
            }
            Log.e("LinkAccessibilityHelper", "LinkSpan is null for offset: " + i);
            return false;
        }

        private ClickableSpan getSpanForOffset(int i) {
            CharSequence text = this.view.getText();
            if (!(text instanceof Spanned)) {
                return null;
            }
            ClickableSpan[] clickableSpanArr = (ClickableSpan[]) ((Spanned) text).getSpans(i, i, ClickableSpan.class);
            if (clickableSpanArr.length == 1) {
                return clickableSpanArr[0];
            }
            return null;
        }

        private CharSequence getTextForSpan(ClickableSpan clickableSpan) {
            CharSequence text = this.view.getText();
            if (!(text instanceof Spanned)) {
                return text;
            }
            Spanned spanned = (Spanned) text;
            return spanned.subSequence(spanned.getSpanStart(clickableSpan), spanned.getSpanEnd(clickableSpan));
        }

        private Rect getBoundsForSpan(ClickableSpan clickableSpan, Rect rect) {
            Layout layout;
            CharSequence text = this.view.getText();
            rect.setEmpty();
            if ((text instanceof Spanned) && (layout = this.view.getLayout()) != null) {
                Spanned spanned = (Spanned) text;
                int spanStart = spanned.getSpanStart(clickableSpan);
                int spanEnd = spanned.getSpanEnd(clickableSpan);
                float primaryHorizontal = layout.getPrimaryHorizontal(spanStart);
                float primaryHorizontal2 = layout.getPrimaryHorizontal(spanEnd);
                int lineForOffset = layout.getLineForOffset(spanStart);
                int lineForOffset2 = layout.getLineForOffset(spanEnd);
                layout.getLineBounds(lineForOffset, rect);
                if (lineForOffset2 == lineForOffset) {
                    rect.left = (int) Math.min(primaryHorizontal, primaryHorizontal2);
                    rect.right = (int) Math.max(primaryHorizontal, primaryHorizontal2);
                } else if (layout.getParagraphDirection(lineForOffset) == -1) {
                    rect.right = (int) primaryHorizontal;
                } else {
                    rect.left = (int) primaryHorizontal;
                }
                rect.offset(this.view.getTotalPaddingLeft(), this.view.getTotalPaddingTop());
            }
            return rect;
        }

        private static int getOffsetForPosition(TextView textView, float f, float f2) {
            if (textView.getLayout() == null) {
                return -1;
            }
            return getOffsetAtCoordinate(textView, getLineAtCoordinate(textView, f2), f);
        }

        private static float convertToLocalHorizontalCoordinate(TextView textView, float f) {
            return Math.min((float) ((textView.getWidth() - textView.getTotalPaddingRight()) - 1), Math.max(0.0f, f - ((float) textView.getTotalPaddingLeft()))) + ((float) textView.getScrollX());
        }

        private static int getLineAtCoordinate(TextView textView, float f) {
            return textView.getLayout().getLineForVertical((int) (Math.min((float) ((textView.getHeight() - textView.getTotalPaddingBottom()) - 1), Math.max(0.0f, f - ((float) textView.getTotalPaddingTop()))) + ((float) textView.getScrollY())));
        }

        private static int getOffsetAtCoordinate(TextView textView, int i, float f) {
            return textView.getLayout().getOffsetForHorizontal(i, convertToLocalHorizontalCoordinate(textView, f));
        }
    }
}
