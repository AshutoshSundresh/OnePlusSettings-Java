package com.google.android.material.edgeeffect;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
import android.widget.FrameLayout;
import com.google.android.material.edgeeffect.SpringRelativeLayout;

public class FilmstripView extends ViewGroup {
    private static final String TAG = FilmstripView.class.getSimpleName();
    private SpringRelativeLayout.SEdgeEffectFactory mEdgeEffectFactory;
    private EdgeEffect mLeftGlow;
    private EdgeEffect mRightGlow;
    private SpringRelativeLayout mSpringLayout;
    private ItemInfo m_ActiveItemInfoHead;
    private ItemInfo m_ActiveItemInfoTail;
    private Adapter m_Adapter;
    private ItemInfo m_AnchorItemInfo;
    private Rotation m_DisplayRotationHint;
    private int m_FastLayoutCounter;
    private ItemInfo m_FreeItemInfos;
    private final GestureDetector m_GestureDetector;
    private final GestureDetector.OnGestureListener m_GestureListener = new GestureDetector.OnGestureListener() {
        /* class com.google.android.material.edgeeffect.FilmstripView.AnonymousClass1 */

        public void onLongPress(MotionEvent motionEvent) {
        }

        public void onShowPress(MotionEvent motionEvent) {
        }

        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            FilmstripView.this.onGestureScroll(motionEvent, motionEvent2, f, f2);
            return false;
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            FilmstripView.this.onGestureFling(motionEvent, motionEvent2, f, f2);
            return false;
        }

        public boolean onDown(MotionEvent motionEvent) {
            FilmstripView.this.onGestureDown(motionEvent);
            return false;
        }
    };
    private Handler m_Handler;
    private boolean m_HasMultiPointers;
    private int m_Height;
    private boolean m_IsFlying;
    private boolean m_IsOverScrolled;
    private Boolean m_IsScrollLeftRight;
    private boolean m_IsScrolling;
    private int m_ItemMargin = 50;
    private int m_LastPosition;
    private View.OnTouchListener m_OnTouchListener;
    private int m_ReportedCurrentPosition = -1;
    private int m_ScreenOrientation = -1;
    private ScrollListener m_ScrollListener;
    private int m_ScrollMode = 0;
    private long m_ScrollToItemStartTime;
    private float m_TotalScrollDistanceX;
    private int m_Width;

    public static abstract class Adapter {
        /* access modifiers changed from: package-private */
        public final void attach(FilmstripView filmstripView) {
            throw null;
        }

        /* access modifiers changed from: package-private */
        public final void detach(FilmstripView filmstripView) {
            throw null;
        }

        public abstract int getCount();

        public abstract int getItemWidth(int i, int i2);

        public abstract void prepareItemView(int i, ViewGroup viewGroup);

        public abstract void releaseItemView(int i, ViewGroup viewGroup);
    }

    /* access modifiers changed from: package-private */
    public enum SWIPE_DIRECTION {
        LEFT,
        RIGHT
    }

    public static abstract class ScrollListener {
        boolean isScrollStartedCalled;

        public abstract void onCurrentItemChanged(int i, int i2);

        public abstract void onItemSelected(int i);

        public abstract void onOverScroll(boolean z);

        public abstract void onScrollStarted();

        public abstract void onScrollStopped();
    }

    /* access modifiers changed from: private */
    public static final class ItemContainerView extends FrameLayout {
        public ItemContainerView(Context context, ItemInfo itemInfo) {
            super(context);
        }
    }

    /* access modifiers changed from: private */
    public static final class ItemInfo {
        public ItemContainerView container;
        public boolean isRemoving;
        public float left;
        public ItemInfo next;
        public int position;
        public ItemInfo previous;
        public int targetWidth;
        public int width;

        private ItemInfo() {
        }

        public void addAfter(ItemInfo itemInfo) {
            if (itemInfo != null) {
                this.next = itemInfo.next;
                itemInfo.next = this;
            }
            ItemInfo itemInfo2 = this.next;
            if (itemInfo2 != null) {
                itemInfo2.previous = this;
            }
            this.previous = itemInfo;
        }

        public void addBefore(ItemInfo itemInfo) {
            if (itemInfo != null) {
                this.previous = itemInfo.previous;
                itemInfo.previous = this;
            }
            ItemInfo itemInfo2 = this.previous;
            if (itemInfo2 != null) {
                itemInfo2.next = this;
            }
            this.next = itemInfo;
        }

        public void layout(int i, int i2, boolean z, boolean z2) {
            ViewGroup.LayoutParams layoutParams;
            if (z2 || (layoutParams = this.container.getLayoutParams()) == null) {
                if (z) {
                    this.container.measure(View.MeasureSpec.makeMeasureSpec(this.width, 1073741824), View.MeasureSpec.makeMeasureSpec(i2, 1073741824));
                }
                this.container.layout(0, 0, this.width, i2);
                return;
            }
            layoutParams.width = this.width;
            layoutParams.height = i2;
            this.container.requestLayout();
        }

        public void moveBy(float f) {
            float f2 = this.left + f;
            this.left = f2;
            this.container.setTranslationX(f2);
        }

        public void moveTo(float f) {
            this.left = f;
            this.container.setTranslationX(f);
        }

        public void remove() {
            ItemInfo itemInfo = this.previous;
            if (itemInfo != null) {
                itemInfo.next = this.next;
            }
            ItemInfo itemInfo2 = this.next;
            if (itemInfo2 != null) {
                itemInfo2.previous = this.previous;
            }
            this.previous = null;
            this.next = null;
        }

        public String toString() {
            return "[Position=" + this.position + ", isRemoving=" + this.isRemoving + "]";
        }
    }

    public FilmstripView(Context context) {
        super(context);
        SWIPE_DIRECTION swipe_direction = SWIPE_DIRECTION.LEFT;
        this.mSpringLayout = null;
        setupHandler();
        this.m_GestureDetector = new GestureDetector(context, this.m_GestureListener);
    }

    public FilmstripView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        SWIPE_DIRECTION swipe_direction = SWIPE_DIRECTION.LEFT;
        this.mSpringLayout = null;
        setupHandler();
        this.m_GestureDetector = new GestureDetector(context, this.m_GestureListener);
    }

    private void fly(float f, long j) {
        float f2;
        if (this.m_IsFlying) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            float f3 = ((float) (elapsedRealtime - j)) / 1000.0f;
            float f4 = -8000.0f * f3;
            scrollBy((float) Math.round((f * f3) + (f3 * f4 * 0.5f)));
            if (f > 0.0f) {
                f2 = Math.max(0.0f, f + f4);
            } else {
                f2 = Math.min(0.0f, f - f4);
            }
            int currentItem = getCurrentItem();
            Adapter adapter = this.m_Adapter;
            int count = adapter != null ? adapter.getCount() : 0;
            if (Math.abs(f2) <= 0.001f || ((f2 > 0.0f && currentItem <= 0) || (f2 < 0.0f && currentItem >= count - 1))) {
                this.m_IsFlying = false;
                if (currentItem >= 0) {
                    scrollToItem(currentItem, true);
                    return;
                }
                return;
            }
            Handler handler = this.m_Handler;
            handler.sendMessageDelayed(Message.obtain(handler, 10001, new Object[]{Float.valueOf(f2), Long.valueOf(elapsedRealtime)}), 10);
        }
    }

    private int calculateItemDefaultLeft(ItemInfo itemInfo, boolean z) {
        return (this.m_Width / 2) - ((z ? itemInfo.targetWidth : itemInfo.width) / 2);
    }

    private void clearItems() {
        ItemInfo itemInfo = this.m_ActiveItemInfoHead;
        while (itemInfo != null) {
            ItemInfo itemInfo2 = itemInfo.next;
            releaseItem(itemInfo);
            itemInfo = itemInfo2;
        }
        this.m_ActiveItemInfoHead = null;
        this.m_ActiveItemInfoTail = null;
        this.m_AnchorItemInfo = null;
        this.m_IsOverScrolled = false;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.m_HasMultiPointers = false;
        }
        if (motionEvent.getPointerCount() > 1 && !this.m_HasMultiPointers) {
            scrollToItem(getCurrentItem(), true);
            this.m_HasMultiPointers = true;
        }
        boolean z = !this.m_IsScrolling;
        this.m_GestureDetector.onTouchEvent(motionEvent);
        int action = motionEvent.getAction();
        if (action == 1 || action == 3) {
            onGestureUp(motionEvent);
        }
        Log.d("FilmStrip", "dispatchTouchEvent " + motionEvent.getAction() + " m_IsScrolling " + this.m_IsScrolling + " isFirstScrolling " + z);
        if (!this.m_IsScrolling) {
            super.dispatchTouchEvent(motionEvent);
        } else if (z) {
            Log.v(TAG, "dispatchTouchEvent() - Dispatch ACTION_CANCEL to child");
            motionEvent.setAction(3);
            super.dispatchTouchEvent(motionEvent);
        }
        View.OnTouchListener onTouchListener = this.m_OnTouchListener;
        if (onTouchListener != null) {
            onTouchListener.onTouch(this, motionEvent);
        }
        return true;
    }

    private void fastLayout() {
        for (ItemInfo itemInfo = this.m_ActiveItemInfoHead; itemInfo != null; itemInfo = itemInfo.next) {
            itemInfo.layout(this.m_Width, this.m_Height, true, true);
        }
    }

    private ItemInfo findItemInfo(int i) {
        return findItemInfo(i, false);
    }

    private ItemInfo findItemInfo(int i, boolean z) {
        for (ItemInfo itemInfo = this.m_ActiveItemInfoHead; itemInfo != null; itemInfo = itemInfo.next) {
            if (itemInfo.position == i && (!itemInfo.isRemoving || z)) {
                return itemInfo;
            }
        }
        return null;
    }

    private ItemInfo findItemInfo(float f, float f2) {
        if (f2 < 0.0f || f2 >= ((float) this.m_Height)) {
            return null;
        }
        float f3 = (float) (this.m_ItemMargin / 2);
        for (ItemInfo itemInfo = this.m_ActiveItemInfoHead; itemInfo != null; itemInfo = itemInfo.next) {
            float f4 = itemInfo.left;
            if (f >= f4 - f3 && f < f4 + ((float) itemInfo.width) + f3) {
                return itemInfo;
            }
        }
        return null;
    }

    public int getCurrentItem() {
        ItemInfo currentItemInfo;
        Adapter adapter = this.m_Adapter;
        int count = adapter != null ? adapter.getCount() : 0;
        if (count > 0 && (currentItemInfo = getCurrentItemInfo()) != null) {
            return Math.min(currentItemInfo.position, count - 1);
        }
        return -1;
    }

    private ItemInfo getCurrentItemInfo() {
        int i = this.m_Width;
        float f = ((float) i) / 2.0f;
        float f2 = (float) i;
        ItemInfo itemInfo = null;
        for (ItemInfo itemInfo2 = this.m_ActiveItemInfoHead; itemInfo2 != null; itemInfo2 = itemInfo2.next) {
            float abs = Math.abs(f - (itemInfo2.left + (((float) itemInfo2.width) / 2.0f)));
            if (itemInfo == null || f2 > abs) {
                itemInfo = itemInfo2;
                f2 = abs;
            }
        }
        return itemInfo;
    }

    public int getFirstVisibltItem() {
        ItemInfo itemInfo = this.m_ActiveItemInfoHead;
        while (itemInfo != null && itemInfo.isRemoving) {
            itemInfo = itemInfo.next;
        }
        if (itemInfo != null) {
            return itemInfo.position;
        }
        return -1;
    }

    public int getLastVisibltItem() {
        ItemInfo itemInfo = this.m_ActiveItemInfoTail;
        while (itemInfo != null && itemInfo.isRemoving) {
            itemInfo = itemInfo.previous;
        }
        if (itemInfo != null) {
            return itemInfo.position;
        }
        return -1;
    }

    public int getScrollMode() {
        return this.m_ScrollMode;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleMessage(Message message) {
        int i = message.what;
        if (i != 10010) {
            boolean z = false;
            switch (i) {
                case 10000:
                    Object obj = message.obj;
                    if (obj instanceof ItemInfo) {
                        updateItemsLayout((ItemInfo) obj, true);
                        return;
                    } else if (obj instanceof Integer) {
                        updateItemsLayout(((Integer) obj).intValue(), true);
                        return;
                    } else {
                        updateItemsLayout(true);
                        return;
                    }
                case 10001:
                    Object[] objArr = (Object[]) message.obj;
                    fly(((Float) objArr[0]).floatValue(), ((Long) objArr[1]).longValue());
                    return;
                case 10002:
                    int i2 = message.arg1;
                    if (message.arg2 != 0) {
                        z = true;
                    }
                    scrollToItem(i2, z);
                    return;
                default:
                    return;
            }
        } else {
            fastLayout();
        }
    }

    private ItemInfo obtainItemInfo(int i) {
        ItemInfo itemInfo = this.m_FreeItemInfos;
        if (itemInfo != null) {
            this.m_FreeItemInfos = itemInfo.next;
            itemInfo.remove();
            itemInfo.isRemoving = false;
            itemInfo.container.setAlpha(1.0f);
        } else {
            itemInfo = new ItemInfo();
            itemInfo.container = new ItemContainerView(getContext(), itemInfo);
        }
        itemInfo.position = i;
        return itemInfo;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        Log.d("filmstripView", "onInterceptTouchEvent  " + actionMasked);
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        Log.d("filmstripView", "onTouchEvent  " + actionMasked);
        return onInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onGestureDown(MotionEvent motionEvent) {
        stopAutoScroll();
        this.m_TotalScrollDistanceX = 0.0f;
        this.m_AnchorItemInfo = findItemInfo(motionEvent.getX(), motionEvent.getY());
        this.m_LastPosition = getCurrentItem();
        this.m_IsScrollLeftRight = null;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0061, code lost:
        if (r6 != 270) goto L_0x0069;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onGestureFling(android.view.MotionEvent r6, android.view.MotionEvent r7, float r8, float r9) {
        /*
        // Method dump skipped, instructions count: 225
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.FilmstripView.onGestureFling(android.view.MotionEvent, android.view.MotionEvent, float, float):void");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onGestureScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (!this.m_HasMultiPointers && this.m_ScrollMode != -1) {
            if (this.m_IsScrollLeftRight == null) {
                if (Math.abs(f) > Math.abs(f2)) {
                    this.m_IsScrollLeftRight = Boolean.TRUE;
                } else {
                    this.m_IsScrollLeftRight = Boolean.FALSE;
                }
            }
            if (this.m_IsScrollLeftRight.booleanValue()) {
                float f3 = this.m_TotalScrollDistanceX + f;
                this.m_TotalScrollDistanceX = f3;
                if (!this.m_IsScrolling && Math.abs(f3) >= 50.0f) {
                    this.m_IsScrolling = true;
                    ScrollListener scrollListener = this.m_ScrollListener;
                    if (scrollListener != null && !scrollListener.isScrollStartedCalled) {
                        scrollListener.isScrollStartedCalled = true;
                        scrollListener.onScrollStarted();
                    }
                }
                Log.d("FilmStrip", "onGestureScroll " + f);
                scrollBy((float) Math.round(-f));
            }
        }
    }

    private void onGestureUp(MotionEvent motionEvent) {
        int currentItem;
        this.m_IsScrolling = false;
        this.m_IsOverScrolled = false;
        this.m_AnchorItemInfo = null;
        if (!this.m_IsFlying && !this.m_Handler.hasMessages(10002) && (currentItem = getCurrentItem()) >= 0) {
            scrollToItem(currentItem, true);
        }
        resetTouch();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        for (ItemInfo itemInfo = this.m_ActiveItemInfoHead; itemInfo != null; itemInfo = itemInfo.next) {
            itemInfo.layout(this.m_Width, this.m_Height, true, true);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3 = 32767;
        int size = View.MeasureSpec.getMode(i) == 1073741824 ? View.MeasureSpec.getSize(i) : 32767;
        if (View.MeasureSpec.getMode(i2) == 1073741824) {
            i3 = View.MeasureSpec.getSize(i2);
        }
        setMeasuredDimension(size, i3);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        int currentItem = getCurrentItem();
        this.m_Width = i;
        this.m_Height = i2;
        refreshItems(currentItem);
    }

    private ItemInfo prepareItem(int i) {
        ItemInfo obtainItemInfo = obtainItemInfo(i);
        int itemWidth = this.m_Adapter.getItemWidth(i, this.m_Width);
        obtainItemInfo.targetWidth = itemWidth;
        obtainItemInfo.width = itemWidth;
        this.m_FastLayoutCounter++;
        this.m_Adapter.prepareItemView(i, obtainItemInfo.container);
        this.m_FastLayoutCounter--;
        if (obtainItemInfo.container.getParent() != null) {
            obtainItemInfo.container.setAlpha(1.0f);
        } else {
            addView(obtainItemInfo.container);
        }
        return obtainItemInfo;
    }

    private void refreshItems(int i) {
        Adapter adapter = this.m_Adapter;
        int count = adapter != null ? adapter.getCount() : 0;
        if (i < 0) {
            i = 0;
        }
        if (i >= count) {
            i = count - 1;
        }
        clearItems();
        if (count <= 0 || i < 0) {
            reportCurrentPosition(-1);
            return;
        }
        ItemInfo prepareItem = prepareItem(i);
        prepareItem.moveTo((float) calculateItemDefaultLeft(prepareItem, false));
        this.m_ActiveItemInfoHead = prepareItem;
        this.m_ActiveItemInfoTail = prepareItem;
        int i2 = i - 1;
        float f = prepareItem.left;
        while (f > 0.0f && i2 >= 0) {
            ItemInfo prepareItem2 = prepareItem(i2);
            prepareItem2.moveTo((f - ((float) prepareItem2.width)) - ((float) this.m_ItemMargin));
            prepareItem2.addBefore(this.m_ActiveItemInfoHead);
            this.m_ActiveItemInfoHead = prepareItem2;
            f = prepareItem2.left;
            i2--;
        }
        int i3 = i + 1;
        float f2 = prepareItem.left + ((float) prepareItem.targetWidth);
        while (f2 < ((float) this.m_Width) && i3 < count) {
            ItemInfo prepareItem3 = prepareItem(i3);
            prepareItem3.moveTo(f2 + ((float) this.m_ItemMargin));
            prepareItem3.addAfter(this.m_ActiveItemInfoTail);
            this.m_ActiveItemInfoTail = prepareItem3;
            f2 = prepareItem3.left + ((float) prepareItem3.targetWidth);
            i3++;
        }
        reportCurrentPosition(i);
        ScrollListener scrollListener = this.m_ScrollListener;
        if (scrollListener != null) {
            scrollListener.onItemSelected(i);
            if (!this.m_IsScrolling) {
                ScrollListener scrollListener2 = this.m_ScrollListener;
                if (scrollListener2.isScrollStartedCalled) {
                    scrollListener2.isScrollStartedCalled = false;
                    scrollListener2.onScrollStopped();
                }
            }
        }
    }

    private void releaseItem(ItemInfo itemInfo) {
        this.m_FastLayoutCounter++;
        itemInfo.container.setAlpha(0.0f);
        Adapter adapter = this.m_Adapter;
        if (adapter != null) {
            adapter.releaseItemView(itemInfo.position, itemInfo.container);
        } else {
            Log.w(TAG, "releaseItem() - No adapter to release item " + itemInfo.position);
        }
        this.m_FastLayoutCounter--;
        releaseItemInfo(itemInfo);
    }

    private void releaseItemInfo(ItemInfo itemInfo) {
        itemInfo.container.animate().cancel();
        itemInfo.remove();
        itemInfo.addBefore(this.m_FreeItemInfos);
        this.m_FreeItemInfos = itemInfo;
    }

    private void reportCurrentPosition(int i) {
        int i2 = this.m_ReportedCurrentPosition;
        if (i2 != i) {
            this.m_ReportedCurrentPosition = i;
            ScrollListener scrollListener = this.m_ScrollListener;
            if (scrollListener != null) {
                scrollListener.onCurrentItemChanged(i2, i);
            }
        }
    }

    public void requestLayout() {
        if (this.m_FastLayoutCounter <= 0 || isLayoutRequested()) {
            Handler handler = this.m_Handler;
            if (handler != null) {
                handler.removeMessages(10010);
            }
            super.requestLayout();
        } else if (!this.m_Handler.hasMessages(10010)) {
            Handler handler2 = this.m_Handler;
            handler2.sendMessageAtFrontOfQueue(Message.obtain(handler2, 10010));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:69:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0144  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private float scrollBy(float r12) {
        /*
        // Method dump skipped, instructions count: 339
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.FilmstripView.scrollBy(float):float");
    }

    private void scrollToItem(int i, boolean z) {
        float f;
        long j;
        Log.d("FilmStrip", "scrollToItem " + i);
        this.m_Handler.removeMessages(10002);
        stopFly();
        if (this.m_ActiveItemInfoHead != null) {
            ItemInfo findItemInfo = findItemInfo(i);
            if (findItemInfo != null) {
                this.m_AnchorItemInfo = findItemInfo;
                f = ((float) calculateItemDefaultLeft(findItemInfo, false)) - findItemInfo.left;
            } else {
                f = 0.0f;
            }
            if (z) {
                this.m_ScrollToItemStartTime = SystemClock.elapsedRealtime();
                j = 0;
            } else {
                j = SystemClock.elapsedRealtime() - this.m_ScrollToItemStartTime;
            }
            if (findItemInfo == null) {
                ItemInfo currentItemInfo = getCurrentItemInfo();
                if (currentItemInfo == null || j >= 600) {
                    refreshItems(i);
                    return;
                }
                int i2 = currentItemInfo.position;
                int i3 = i - i2;
                int i4 = 1;
                if (currentItemInfo.isRemoving) {
                    i3 += i2 <= i ? 1 : -1;
                }
                if (Math.abs(i3) > 2) {
                    refreshItems(currentItemInfo.position + (i3 / 2));
                    Handler handler = this.m_Handler;
                    handler.sendMessageDelayed(Message.obtain(handler, 10002, i, 0), 0);
                    return;
                }
                int i5 = this.m_Width / 2;
                if (i3 >= 0) {
                    i4 = -1;
                }
                scrollBy((float) (i5 * i4));
                Handler handler2 = this.m_Handler;
                handler2.sendMessageDelayed(Message.obtain(handler2, 10002, i, 0), 0);
            } else if (Math.abs(f) <= 1.0f || j >= 600) {
                scrollBy(f);
                ScrollListener scrollListener = this.m_ScrollListener;
                if (scrollListener != null) {
                    scrollListener.onItemSelected(i);
                    if (!this.m_IsScrolling) {
                        ScrollListener scrollListener2 = this.m_ScrollListener;
                        if (scrollListener2.isScrollStartedCalled) {
                            scrollListener2.isScrollStartedCalled = false;
                            scrollListener2.onScrollStopped();
                        }
                    }
                }
            } else {
                double d = (double) 1.0f;
                float f2 = f / (d > 3.5d ? 4.0f : d > 2.5d ? 5.0f : 6.5f);
                if (Math.abs(f2) <= 5.0f) {
                    if (f2 > 0.0f) {
                        f2 = Math.min(f, 5.0f);
                    } else {
                        f2 = Math.max(f, -5.0f);
                    }
                }
                scrollBy(f2);
                Handler handler3 = this.m_Handler;
                handler3.sendMessageDelayed(Message.obtain(handler3, 10002, i, 0), 0);
            }
        }
    }

    public void setAdapter(Adapter adapter) {
        Adapter adapter2 = this.m_Adapter;
        if (adapter2 == adapter) {
            return;
        }
        if (adapter2 == null) {
            clearItems();
            this.m_Adapter = adapter;
            if (adapter != null) {
                adapter.attach(this);
                throw null;
            }
            return;
        }
        adapter2.detach(this);
        throw null;
    }

    public void setCurrentItem(int i, boolean z) {
        stopAutoScroll();
        if (z) {
            scrollToItem(i, true);
            return;
        }
        ItemInfo findItemInfo = findItemInfo(i);
        if (findItemInfo != null) {
            scrollBy(((float) calculateItemDefaultLeft(findItemInfo, false)) - findItemInfo.left);
            ScrollListener scrollListener = this.m_ScrollListener;
            if (scrollListener != null) {
                scrollListener.onItemSelected(i);
                if (!this.m_IsScrolling) {
                    ScrollListener scrollListener2 = this.m_ScrollListener;
                    if (scrollListener2.isScrollStartedCalled) {
                        scrollListener2.isScrollStartedCalled = false;
                        scrollListener2.onScrollStopped();
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        refreshItems(i);
    }

    public void setDisplayRotationHint(Rotation rotation) {
        this.m_DisplayRotationHint = rotation;
        this.m_ScreenOrientation = -1;
    }

    public void setItemMargin(int i) {
        this.m_ItemMargin = i;
        updateItemsLayout(true);
    }

    public void setOnTouchListener(View.OnTouchListener onTouchListener) {
        this.m_OnTouchListener = onTouchListener;
    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.m_ScrollListener = scrollListener;
    }

    public void setScrollMode(int i) {
        if (i == -1) {
            this.m_IsScrolling = false;
            this.m_ScrollMode = i;
            int currentItem = getCurrentItem();
            if (currentItem >= 0) {
                setCurrentItem(currentItem, true);
            }
        } else if (i == 0 || i == 1) {
            this.m_ScrollMode = i;
        } else {
            throw new IllegalArgumentException("Unknown scroll mode : " + i + ".");
        }
    }

    private void setupHandler() {
        this.m_Handler = new Handler() {
            /* class com.google.android.material.edgeeffect.FilmstripView.AnonymousClass3 */

            public void handleMessage(Message message) {
                FilmstripView.this.handleMessage(message);
            }
        };
    }

    private void startFly(float f) {
        stopFly();
        this.m_IsFlying = true;
        Handler handler = this.m_Handler;
        handler.sendMessageDelayed(Message.obtain(handler, 10001, new Object[]{Float.valueOf(f), Long.valueOf(SystemClock.elapsedRealtime())}), 10);
    }

    private void stopAutoScroll() {
        stopFly();
        this.m_Handler.removeMessages(10002);
    }

    private void stopFly() {
        if (this.m_IsFlying) {
            this.m_IsFlying = false;
            this.m_Handler.removeMessages(10001);
        }
    }

    private void updateItemsLayout(boolean z) {
        updateItemsLayout(-1, z);
    }

    private void updateItemsLayout(int i, boolean z) {
        ItemInfo itemInfo;
        Adapter adapter;
        if (i < 0 || ((adapter = this.m_Adapter) != null && i >= adapter.getCount())) {
            itemInfo = getCurrentItemInfo();
        } else {
            itemInfo = findItemInfo(i);
            if (itemInfo == null) {
                itemInfo = getCurrentItemInfo();
            }
        }
        updateItemsLayout(itemInfo, z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:73:0x0151  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateItemsLayout(com.google.android.material.edgeeffect.FilmstripView.ItemInfo r9, boolean r10) {
        /*
        // Method dump skipped, instructions count: 387
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.FilmstripView.updateItemsLayout(com.google.android.material.edgeeffect.FilmstripView$ItemInfo, boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0048  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void pullGlows(float r8) {
        /*
            r7 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 0
            int r2 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            r3 = 1
            r4 = 21
            if (r2 >= 0) goto L_0x0025
            r7.ensureLeftGlow()
            android.widget.EdgeEffect r2 = r7.mLeftGlow
            if (r2 == 0) goto L_0x0045
            if (r0 < r4) goto L_0x0046
            float r4 = -r8
            int r5 = r7.getWidth()
            float r5 = (float) r5
            float r4 = r4 / r5
            int r5 = r7.getHeight()
            float r5 = (float) r5
            float r5 = r1 / r5
            r2.onPull(r4, r5)
            goto L_0x0046
        L_0x0025:
            int r2 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0045
            r7.ensureRightGlow()
            android.widget.EdgeEffect r2 = r7.mRightGlow
            if (r2 == 0) goto L_0x0045
            if (r0 < r4) goto L_0x0046
            int r4 = r7.getWidth()
            float r4 = (float) r4
            float r4 = r8 / r4
            r5 = 1065353216(0x3f800000, float:1.0)
            int r6 = r7.getHeight()
            float r6 = (float) r6
            float r5 = r5 / r6
            r2.onPull(r4, r5)
            goto L_0x0046
        L_0x0045:
            r3 = 0
        L_0x0046:
            if (r3 != 0) goto L_0x004c
            int r8 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r8 == 0) goto L_0x0053
        L_0x004c:
            r8 = 16
            if (r0 < r8) goto L_0x0053
            r7.postInvalidateOnAnimation()
        L_0x0053:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.FilmstripView.pullGlows(float):void");
    }

    /* access modifiers changed from: package-private */
    public void ensureLeftGlow() {
        SpringRelativeLayout.SEdgeEffectFactory sEdgeEffectFactory = this.mEdgeEffectFactory;
        if (sEdgeEffectFactory == null) {
            Log.e("FilmstripView", "setEdgeEffectFactory first, please!");
        } else if (this.mLeftGlow == null) {
            this.mLeftGlow = sEdgeEffectFactory.createEdgeEffect(this, 0);
            if (Build.VERSION.SDK_INT < 21 || !getClipToPadding()) {
                this.mLeftGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            } else {
                this.mLeftGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void ensureRightGlow() {
        SpringRelativeLayout.SEdgeEffectFactory sEdgeEffectFactory = this.mEdgeEffectFactory;
        if (sEdgeEffectFactory == null) {
            Log.e("FilmstripView", "setEdgeEffectFactory first, please!");
        } else if (this.mRightGlow == null) {
            this.mRightGlow = sEdgeEffectFactory.createEdgeEffect(this, 2);
            if (Build.VERSION.SDK_INT < 21 || !getClipToPadding()) {
                this.mRightGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            } else {
                this.mRightGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            }
        }
    }

    public void setEdgeEffectFactory(SpringRelativeLayout.SEdgeEffectFactory sEdgeEffectFactory) {
        this.mEdgeEffectFactory = sEdgeEffectFactory;
        invalidateGlows();
    }

    /* access modifiers changed from: package-private */
    public void invalidateGlows() {
        this.mRightGlow = null;
        this.mLeftGlow = null;
    }

    private void releaseGlows() {
        EdgeEffect edgeEffect = this.mLeftGlow;
        boolean z = false;
        if (edgeEffect != null) {
            edgeEffect.onRelease();
            z = false | this.mLeftGlow.isFinished();
        }
        EdgeEffect edgeEffect2 = this.mRightGlow;
        if (edgeEffect2 != null) {
            edgeEffect2.onRelease();
            z |= this.mRightGlow.isFinished();
        }
        if (z && Build.VERSION.SDK_INT >= 16) {
            postInvalidateOnAnimation();
        }
    }

    private void resetTouch() {
        releaseGlows();
    }
}
