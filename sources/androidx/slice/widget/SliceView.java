package androidx.slice.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.SliceMetadata;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import androidx.slice.view.R$attr;
import androidx.slice.view.R$dimen;
import androidx.slice.view.R$style;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class SliceView extends ViewGroup implements Observer<Slice>, View.OnClickListener {
    public static final Comparator<SliceAction> SLICE_ACTION_PRIORITY_COMPARATOR = new Comparator<SliceAction>() {
        /* class androidx.slice.widget.SliceView.AnonymousClass3 */

        public int compare(SliceAction sliceAction, SliceAction sliceAction2) {
            int priority = sliceAction.getPriority();
            int priority2 = sliceAction2.getPriority();
            if (priority < 0 && priority2 < 0) {
                return 0;
            }
            if (priority < 0) {
                return 1;
            }
            if (priority2 < 0) {
                return -1;
            }
            if (priority2 < priority) {
                return 1;
            }
            return priority2 > priority ? -1 : 0;
        }
    };
    private ActionRow mActionRow;
    private int mActionRowHeight;
    private List<SliceAction> mActions;
    int[] mClickInfo;
    private Slice mCurrentSlice;
    private boolean mCurrentSliceLoggedVisible;
    private SliceMetrics mCurrentSliceMetrics;
    SliceChildView mCurrentView;
    private int mDownX;
    private int mDownY;
    Handler mHandler;
    boolean mInLongpress;
    private int mLargeHeight;
    ListContent mListContent;
    View.OnLongClickListener mLongClickListener;
    Runnable mLongpressCheck;
    private int mMinTemplateHeight;
    private View.OnClickListener mOnClickListener;
    boolean mPressing;
    Runnable mRefreshLastUpdated;
    private int mShortcutSize;
    private boolean mShowActionDividers;
    private boolean mShowActions;
    private boolean mShowHeaderDivider;
    private boolean mShowLastUpdated;
    private boolean mShowTitleItems;
    SliceMetadata mSliceMetadata;
    private OnSliceActionListener mSliceObserver;
    private SliceStyle mSliceStyle;
    private int mThemeTintColor;
    private int mTouchSlopSquared;
    private SliceViewPolicy mViewPolicy;

    public interface OnSliceActionListener {
        void onSliceAction(EventInfo eventInfo, SliceItem sliceItem);
    }

    public SliceView(Context context) {
        this(context, null);
    }

    public SliceView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.sliceViewStyle);
    }

    public SliceView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowActions = false;
        this.mShowLastUpdated = true;
        this.mCurrentSliceLoggedVisible = false;
        this.mShowTitleItems = false;
        this.mShowHeaderDivider = false;
        this.mShowActionDividers = false;
        this.mThemeTintColor = -1;
        this.mLongpressCheck = new Runnable() {
            /* class androidx.slice.widget.SliceView.AnonymousClass1 */

            public void run() {
                View.OnLongClickListener onLongClickListener;
                SliceView sliceView = SliceView.this;
                if (sliceView.mPressing && (onLongClickListener = sliceView.mLongClickListener) != null) {
                    sliceView.mInLongpress = true;
                    onLongClickListener.onLongClick(sliceView);
                    SliceView.this.performHapticFeedback(0);
                }
            }
        };
        this.mRefreshLastUpdated = new Runnable() {
            /* class androidx.slice.widget.SliceView.AnonymousClass2 */

            public void run() {
                SliceMetadata sliceMetadata = SliceView.this.mSliceMetadata;
                if (sliceMetadata != null && sliceMetadata.isExpired()) {
                    SliceView.this.mCurrentView.setShowLastUpdated(true);
                    SliceView sliceView = SliceView.this;
                    sliceView.mCurrentView.setSliceContent(sliceView.mListContent);
                }
                SliceView.this.mHandler.postDelayed(this, 60000);
            }
        };
        init(context, attributeSet, i, R$style.Widget_SliceView);
    }

    public SliceView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mShowActions = false;
        this.mShowLastUpdated = true;
        this.mCurrentSliceLoggedVisible = false;
        this.mShowTitleItems = false;
        this.mShowHeaderDivider = false;
        this.mShowActionDividers = false;
        this.mThemeTintColor = -1;
        this.mLongpressCheck = new Runnable() {
            /* class androidx.slice.widget.SliceView.AnonymousClass1 */

            public void run() {
                View.OnLongClickListener onLongClickListener;
                SliceView sliceView = SliceView.this;
                if (sliceView.mPressing && (onLongClickListener = sliceView.mLongClickListener) != null) {
                    sliceView.mInLongpress = true;
                    onLongClickListener.onLongClick(sliceView);
                    SliceView.this.performHapticFeedback(0);
                }
            }
        };
        this.mRefreshLastUpdated = new Runnable() {
            /* class androidx.slice.widget.SliceView.AnonymousClass2 */

            public void run() {
                SliceMetadata sliceMetadata = SliceView.this.mSliceMetadata;
                if (sliceMetadata != null && sliceMetadata.isExpired()) {
                    SliceView.this.mCurrentView.setShowLastUpdated(true);
                    SliceView sliceView = SliceView.this;
                    sliceView.mCurrentView.setSliceContent(sliceView.mListContent);
                }
                SliceView.this.mHandler.postDelayed(this, 60000);
            }
        };
        init(context, attributeSet, i, i2);
    }

    private void init(Context context, AttributeSet attributeSet, int i, int i2) {
        SliceStyle sliceStyle = new SliceStyle(context, attributeSet, i, i2);
        this.mSliceStyle = sliceStyle;
        this.mThemeTintColor = sliceStyle.getTintColor();
        this.mShortcutSize = getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_shortcut_size);
        this.mMinTemplateHeight = getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_row_min_height);
        this.mLargeHeight = getResources().getDimensionPixelSize(R$dimen.abc_slice_large_height);
        this.mActionRowHeight = getResources().getDimensionPixelSize(R$dimen.abc_slice_action_row_height);
        this.mViewPolicy = new SliceViewPolicy();
        TemplateView templateView = new TemplateView(getContext());
        this.mCurrentView = templateView;
        templateView.setPolicy(this.mViewPolicy);
        SliceChildView sliceChildView = this.mCurrentView;
        addView(sliceChildView, getChildLp(sliceChildView));
        applyConfigurations();
        ActionRow actionRow = new ActionRow(getContext(), true);
        this.mActionRow = actionRow;
        actionRow.setBackground(new ColorDrawable(-1118482));
        ActionRow actionRow2 = this.mActionRow;
        addView(actionRow2, getChildLp(actionRow2));
        updateActions();
        int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.mTouchSlopSquared = scaledTouchSlop * scaledTouchSlop;
        this.mHandler = new Handler();
        setClipToPadding(false);
        super.setOnClickListener(this);
    }

    /* access modifiers changed from: package-private */
    public void setSliceViewPolicy(SliceViewPolicy sliceViewPolicy) {
        this.mViewPolicy = sliceViewPolicy;
    }

    public boolean isSliceViewClickable() {
        ListContent listContent;
        return (this.mOnClickListener == null && ((listContent = this.mListContent) == null || listContent.getShortcut(getContext()) == null)) ? false : true;
    }

    public void setClickInfo(int[] iArr) {
        this.mClickInfo = iArr;
    }

    public void onClick(View view) {
        ListContent listContent = this.mListContent;
        if (listContent == null || listContent.getShortcut(getContext()) == null) {
            View.OnClickListener onClickListener = this.mOnClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(this);
                return;
            }
            return;
        }
        try {
            SliceActionImpl sliceActionImpl = (SliceActionImpl) this.mListContent.getShortcut(getContext());
            SliceItem actionItem = sliceActionImpl.getActionItem();
            if (actionItem != null && actionItem.fireActionInternal(getContext(), null)) {
                this.mCurrentView.setActionLoading(sliceActionImpl.getSliceItem());
            }
            if (actionItem != null && this.mSliceObserver != null && this.mClickInfo != null && this.mClickInfo.length > 1) {
                EventInfo eventInfo = new EventInfo(getMode(), 3, this.mClickInfo[0], this.mClickInfo[1]);
                this.mSliceObserver.onSliceAction(eventInfo, sliceActionImpl.getSliceItem());
                logSliceMetricsOnTouch(sliceActionImpl.getSliceItem(), eventInfo);
            }
        } catch (PendingIntent.CanceledException e) {
            Log.e("SliceView", "PendingIntent for slice cannot be sent", e);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        super.setOnLongClickListener(onLongClickListener);
        this.mLongClickListener = onLongClickListener;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return (this.mLongClickListener != null && handleTouchForLongpress(motionEvent)) || super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return (this.mLongClickListener != null && handleTouchForLongpress(motionEvent)) || super.onTouchEvent(motionEvent);
    }

    private boolean handleTouchForLongpress(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    int rawX = ((int) motionEvent.getRawX()) - this.mDownX;
                    int rawY = ((int) motionEvent.getRawY()) - this.mDownY;
                    if ((rawX * rawX) + (rawY * rawY) > this.mTouchSlopSquared) {
                        this.mPressing = false;
                        this.mHandler.removeCallbacks(this.mLongpressCheck);
                    }
                    return this.mInLongpress;
                } else if (actionMasked != 3) {
                    return false;
                }
            }
            boolean z = this.mInLongpress;
            this.mPressing = false;
            this.mInLongpress = false;
            this.mHandler.removeCallbacks(this.mLongpressCheck);
            return z;
        }
        this.mHandler.removeCallbacks(this.mLongpressCheck);
        this.mDownX = (int) motionEvent.getRawX();
        this.mDownY = (int) motionEvent.getRawY();
        this.mPressing = true;
        this.mInLongpress = false;
        this.mHandler.postDelayed(this.mLongpressCheck, (long) ViewConfiguration.getLongPressTimeout());
        return false;
    }

    private void configureViewPolicy(int i) {
        ListContent listContent = this.mListContent;
        if (listContent != null && listContent.isValid() && getMode() != 3) {
            if (i <= 0 || i >= this.mSliceStyle.getRowMaxHeight()) {
                this.mViewPolicy.setMaxSmallHeight(0);
            } else {
                int i2 = this.mMinTemplateHeight;
                if (i <= i2) {
                    i = i2;
                }
                this.mViewPolicy.setMaxSmallHeight(i);
            }
            this.mViewPolicy.setMaxHeight(i);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0080, code lost:
        if (r2 >= (r9 + r0)) goto L_0x0062;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r8, int r9) {
        /*
        // Method dump skipped, instructions count: 201
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.SliceView.onMeasure(int, int):void");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        SliceChildView sliceChildView = this.mCurrentView;
        sliceChildView.layout(0, 0, sliceChildView.getMeasuredWidth(), sliceChildView.getMeasuredHeight());
        if (this.mActionRow.getVisibility() != 8) {
            int measuredHeight = sliceChildView.getMeasuredHeight();
            ActionRow actionRow = this.mActionRow;
            actionRow.layout(0, measuredHeight, actionRow.getMeasuredWidth(), this.mActionRow.getMeasuredHeight() + measuredHeight);
        }
    }

    public void onChanged(Slice slice) {
        setSlice(slice);
    }

    public void setSlice(Slice slice) {
        LocationBasedViewTracker.trackInputFocused(this);
        LocationBasedViewTracker.trackA11yFocus(this);
        initSliceMetrics(slice);
        boolean z = false;
        boolean z2 = (slice == null || this.mCurrentSlice == null || !slice.getUri().equals(this.mCurrentSlice.getUri())) ? false : true;
        SliceMetadata sliceMetadata = this.mSliceMetadata;
        this.mCurrentSlice = slice;
        SliceMetadata from = slice != null ? SliceMetadata.from(getContext(), this.mCurrentSlice) : null;
        this.mSliceMetadata = from;
        if (!z2) {
            this.mCurrentView.resetView();
        } else if (sliceMetadata.getLoadingState() == 2 && from.getLoadingState() == 0) {
            return;
        }
        SliceMetadata sliceMetadata2 = this.mSliceMetadata;
        this.mListContent = sliceMetadata2 != null ? sliceMetadata2.getListContent() : null;
        if (this.mShowTitleItems) {
            showTitleItems(true);
        }
        if (this.mShowHeaderDivider) {
            showHeaderDivider(true);
        }
        if (this.mShowActionDividers) {
            showActionDividers(true);
        }
        ListContent listContent = this.mListContent;
        if (listContent == null || !listContent.isValid()) {
            this.mActions = null;
            this.mCurrentView.resetView();
            updateActions();
            return;
        }
        this.mCurrentView.setLoadingActions(null);
        this.mActions = this.mSliceMetadata.getSliceActions();
        this.mCurrentView.setLastUpdated(this.mSliceMetadata.getLastUpdatedTime());
        SliceChildView sliceChildView = this.mCurrentView;
        if (this.mShowLastUpdated && this.mSliceMetadata.isExpired()) {
            z = true;
        }
        sliceChildView.setShowLastUpdated(z);
        this.mCurrentView.setAllowTwoLines(this.mSliceMetadata.isPermissionSlice());
        this.mCurrentView.setTint(getTintColor());
        if (this.mListContent.getLayoutDir() != -1) {
            this.mCurrentView.setLayoutDirection(this.mListContent.getLayoutDir());
        } else {
            this.mCurrentView.setLayoutDirection(2);
        }
        this.mCurrentView.setSliceContent(this.mListContent);
        updateActions();
        logSliceMetricsVisibilityChange(true);
        refreshLastUpdatedLabel(true);
    }

    public Slice getSlice() {
        return this.mCurrentSlice;
    }

    public List<SliceAction> getSliceActions() {
        List<SliceAction> list = this.mActions;
        if (list == null || !list.isEmpty()) {
            return this.mActions;
        }
        return null;
    }

    public void setSliceActions(List<SliceAction> list) {
        SliceMetadata sliceMetadata;
        if (this.mCurrentSlice == null || (sliceMetadata = this.mSliceMetadata) == null) {
            throw new IllegalStateException("Trying to set actions on a view without a slice");
        }
        List<SliceAction> sliceActions = sliceMetadata.getSliceActions();
        if (!(sliceActions == null || list == null)) {
            for (int i = 0; i < list.size(); i++) {
                if (!sliceActions.contains(list.get(i))) {
                    throw new IllegalArgumentException("Trying to set an action that isn't available: " + list.get(i));
                }
            }
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        this.mActions = list;
        updateActions();
    }

    public void setMode(int i) {
        setMode(i, false);
    }

    public void setScrollable(boolean z) {
        if (z != this.mViewPolicy.isScrollable()) {
            this.mViewPolicy.setScrollable(z);
        }
    }

    public void setOnSliceActionListener(OnSliceActionListener onSliceActionListener) {
        this.mSliceObserver = onSliceActionListener;
        this.mCurrentView.setSliceActionListener(onSliceActionListener);
    }

    public void setAccentColor(int i) {
        this.mThemeTintColor = i;
        this.mSliceStyle.setTintColor(i);
        this.mCurrentView.setTint(getTintColor());
    }

    public void setMode(int i, boolean z) {
        if (z) {
            Log.e("SliceView", "Animation not supported yet");
        }
        if (this.mViewPolicy.getMode() != i) {
            if (!(i == 1 || i == 2 || i == 3)) {
                Log.w("SliceView", "Unknown mode: " + i + " please use one of MODE_SHORTCUT, MODE_SMALL, MODE_LARGE");
                i = 2;
            }
            this.mViewPolicy.setMode(i);
            updateViewConfig();
        }
    }

    public int getMode() {
        return this.mViewPolicy.getMode();
    }

    public void setShowTitleItems(boolean z) {
        this.mShowTitleItems = z;
        ListContent listContent = this.mListContent;
        if (listContent != null) {
            listContent.showTitleItems(z);
        }
    }

    @Deprecated
    public void showTitleItems(boolean z) {
        setShowTitleItems(z);
    }

    public void setShowHeaderDivider(boolean z) {
        this.mShowHeaderDivider = z;
        ListContent listContent = this.mListContent;
        if (listContent != null) {
            listContent.showHeaderDivider(z);
        }
    }

    @Deprecated
    public void showHeaderDivider(boolean z) {
        setShowHeaderDivider(z);
    }

    public void setShowActionDividers(boolean z) {
        this.mShowActionDividers = z;
        ListContent listContent = this.mListContent;
        if (listContent != null) {
            listContent.showActionDividers(z);
        }
    }

    @Deprecated
    public void showActionDividers(boolean z) {
        setShowActionDividers(z);
    }

    public void setShowActionRow(boolean z) {
        this.mShowActions = z;
        updateActions();
    }

    private void updateViewConfig() {
        int mode = getMode();
        SliceChildView sliceChildView = this.mCurrentView;
        boolean z = sliceChildView instanceof ShortcutView;
        Set<SliceItem> loadingActions = sliceChildView.getLoadingActions();
        boolean z2 = true;
        if (mode == 3 && !z) {
            removeView(this.mCurrentView);
            ShortcutView shortcutView = new ShortcutView(getContext());
            this.mCurrentView = shortcutView;
            addView(shortcutView, getChildLp(shortcutView));
        } else if (mode == 3 || !z) {
            z2 = false;
        } else {
            removeView(this.mCurrentView);
            TemplateView templateView = new TemplateView(getContext());
            this.mCurrentView = templateView;
            addView(templateView, getChildLp(templateView));
        }
        if (z2) {
            this.mCurrentView.setPolicy(this.mViewPolicy);
            applyConfigurations();
            ListContent listContent = this.mListContent;
            if (listContent != null && listContent.isValid()) {
                this.mCurrentView.setSliceContent(this.mListContent);
            }
            this.mCurrentView.setLoadingActions(loadingActions);
        }
        updateActions();
    }

    private void applyConfigurations() {
        this.mCurrentView.setSliceActionListener(this.mSliceObserver);
        this.mCurrentView.setStyle(this.mSliceStyle);
        this.mCurrentView.setTint(getTintColor());
        ListContent listContent = this.mListContent;
        if (listContent == null || listContent.getLayoutDir() == -1) {
            this.mCurrentView.setLayoutDirection(2);
        } else {
            this.mCurrentView.setLayoutDirection(this.mListContent.getLayoutDir());
        }
    }

    private void updateActions() {
        if (this.mActions == null) {
            this.mActionRow.setVisibility(8);
            this.mCurrentView.setSliceActions(null);
            this.mCurrentView.setInsets(getPaddingStart(), getPaddingTop(), getPaddingEnd(), getPaddingBottom());
            return;
        }
        ArrayList arrayList = new ArrayList(this.mActions);
        Collections.sort(arrayList, SLICE_ACTION_PRIORITY_COMPARATOR);
        if (!this.mShowActions || getMode() == 3 || this.mActions.size() < 2) {
            this.mCurrentView.setSliceActions(arrayList);
            this.mCurrentView.setInsets(getPaddingStart(), getPaddingTop(), getPaddingEnd(), getPaddingBottom());
            this.mActionRow.setVisibility(8);
            return;
        }
        this.mActionRow.setActions(arrayList, getTintColor());
        this.mActionRow.setVisibility(0);
        this.mCurrentView.setSliceActions(null);
        this.mCurrentView.setInsets(getPaddingStart(), getPaddingTop(), getPaddingEnd(), 0);
        this.mActionRow.setPaddingRelative(getPaddingStart(), 0, getPaddingEnd(), getPaddingBottom());
    }

    private int getTintColor() {
        int i = this.mThemeTintColor;
        if (i != -1) {
            return i;
        }
        SliceItem findSubtype = SliceQuery.findSubtype(this.mCurrentSlice, "int", "color");
        if (findSubtype != null) {
            return findSubtype.getInt();
        }
        return SliceViewUtil.getColorAccent(getContext());
    }

    private ViewGroup.LayoutParams getChildLp(View view) {
        if (!(view instanceof ShortcutView)) {
            return new ViewGroup.LayoutParams(-1, -1);
        }
        int i = this.mShortcutSize;
        return new ViewGroup.LayoutParams(i, i);
    }

    public static String modeToString(int i) {
        if (i == 1) {
            return "MODE SMALL";
        }
        if (i == 2) {
            return "MODE LARGE";
        }
        if (i == 3) {
            return "MODE SHORTCUT";
        }
        return "unknown mode: " + i;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isShown()) {
            logSliceMetricsVisibilityChange(true);
            refreshLastUpdatedLabel(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        logSliceMetricsVisibilityChange(false);
        refreshLastUpdatedLabel(false);
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (isAttachedToWindow()) {
            boolean z = true;
            logSliceMetricsVisibilityChange(i == 0);
            if (i != 0) {
                z = false;
            }
            refreshLastUpdatedLabel(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        boolean z = true;
        logSliceMetricsVisibilityChange(i == 0);
        if (i != 0) {
            z = false;
        }
        refreshLastUpdatedLabel(z);
    }

    private void initSliceMetrics(Slice slice) {
        if (slice == null || slice.getUri() == null) {
            logSliceMetricsVisibilityChange(false);
            this.mCurrentSliceMetrics = null;
            return;
        }
        Slice slice2 = this.mCurrentSlice;
        if (slice2 == null || !slice2.getUri().equals(slice.getUri())) {
            logSliceMetricsVisibilityChange(false);
            this.mCurrentSliceMetrics = SliceMetrics.getInstance(getContext(), slice.getUri());
        }
    }

    private void logSliceMetricsVisibilityChange(boolean z) {
        SliceMetrics sliceMetrics = this.mCurrentSliceMetrics;
        if (sliceMetrics != null) {
            if (z && !this.mCurrentSliceLoggedVisible) {
                sliceMetrics.logVisible();
                this.mCurrentSliceLoggedVisible = true;
            }
            if (!z && this.mCurrentSliceLoggedVisible) {
                this.mCurrentSliceMetrics.logHidden();
                this.mCurrentSliceLoggedVisible = false;
            }
        }
    }

    private void logSliceMetricsOnTouch(SliceItem sliceItem, EventInfo eventInfo) {
        if (this.mCurrentSliceMetrics != null && sliceItem.getSlice() != null && sliceItem.getSlice().getUri() != null) {
            this.mCurrentSliceMetrics.logTouch(eventInfo.actionType, sliceItem.getSlice().getUri());
        }
    }

    private void refreshLastUpdatedLabel(boolean z) {
        SliceMetadata sliceMetadata;
        if (this.mShowLastUpdated && (sliceMetadata = this.mSliceMetadata) != null && !sliceMetadata.neverExpires()) {
            if (z) {
                Handler handler = this.mHandler;
                Runnable runnable = this.mRefreshLastUpdated;
                long j = 60000;
                if (!this.mSliceMetadata.isExpired()) {
                    j = 60000 + this.mSliceMetadata.getTimeToExpiry();
                }
                handler.postDelayed(runnable, j);
                return;
            }
            this.mHandler.removeCallbacks(this.mRefreshLastUpdated);
        }
    }
}
