package androidx.slice.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceQuery;
import androidx.slice.view.R$dimen;
import androidx.slice.view.R$id;
import androidx.slice.view.R$layout;
import androidx.slice.view.R$string;
import androidx.slice.widget.GridContent;
import androidx.slice.widget.SliceView;
import java.util.ArrayList;
import java.util.Iterator;

public class GridRowView extends SliceChildView implements View.OnClickListener, View.OnTouchListener {
    private static final int TEXT_LAYOUT = R$layout.abc_slice_secondary_text;
    private static final int TITLE_TEXT_LAYOUT = R$layout.abc_slice_title;
    private View mForeground;
    private GridContent mGridContent;
    private int mGutter;
    private int mIconSize;
    private int mLargeImageHeight;
    private int[] mLoc;
    boolean mMaxCellUpdateScheduled;
    int mMaxCells;
    private ViewTreeObserver.OnPreDrawListener mMaxCellsUpdater;
    private int mRowCount;
    private int mRowIndex;
    private int mSmallImageMinWidth;
    private int mSmallImageSize;
    private int mTextPadding;
    private LinearLayout mViewContainer;

    public GridRowView(Context context) {
        this(context, null);
    }

    public GridRowView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMaxCells = -1;
        this.mLoc = new int[2];
        this.mMaxCellsUpdater = new ViewTreeObserver.OnPreDrawListener() {
            /* class androidx.slice.widget.GridRowView.AnonymousClass1 */

            public boolean onPreDraw() {
                GridRowView gridRowView = GridRowView.this;
                gridRowView.mMaxCells = gridRowView.getMaxCells();
                GridRowView.this.populateViews();
                GridRowView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                GridRowView.this.mMaxCellUpdateScheduled = false;
                return true;
            }
        };
        Resources resources = getContext().getResources();
        LinearLayout linearLayout = new LinearLayout(getContext());
        this.mViewContainer = linearLayout;
        linearLayout.setOrientation(0);
        addView(this.mViewContainer, new FrameLayout.LayoutParams(-1, -1));
        this.mViewContainer.setGravity(16);
        this.mIconSize = resources.getDimensionPixelSize(R$dimen.abc_slice_icon_size);
        this.mSmallImageSize = resources.getDimensionPixelSize(R$dimen.abc_slice_small_image_size);
        this.mLargeImageHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_image_only_height);
        this.mSmallImageMinWidth = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_image_min_width);
        this.mGutter = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_gutter);
        this.mTextPadding = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_text_padding);
        View view = new View(getContext());
        this.mForeground = view;
        addView(view, new FrameLayout.LayoutParams(-1, -1));
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setInsets(int i, int i2, int i3, int i4) {
        super.setInsets(i, i2, i3, i4);
        this.mViewContainer.setPadding(i, i2 + getExtraTopPadding(), i3, i4 + getExtraBottomPadding());
    }

    private int getExtraTopPadding() {
        SliceStyle sliceStyle;
        GridContent gridContent = this.mGridContent;
        if (gridContent == null || !gridContent.isAllImages() || this.mRowIndex != 0 || (sliceStyle = this.mSliceStyle) == null) {
            return 0;
        }
        return sliceStyle.getGridTopPadding();
    }

    private int getExtraBottomPadding() {
        SliceStyle sliceStyle;
        GridContent gridContent = this.mGridContent;
        if (gridContent == null || !gridContent.isAllImages()) {
            return 0;
        }
        if ((this.mRowIndex == this.mRowCount - 1 || getMode() == 1) && (sliceStyle = this.mSliceStyle) != null) {
            return sliceStyle.getGridBottomPadding();
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int height = this.mGridContent.getHeight(this.mSliceStyle, this.mViewPolicy) + this.mInsetTop + this.mInsetBottom;
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
        this.mViewContainer.getLayoutParams().height = height;
        super.onMeasure(i, makeMeasureSpec);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setTint(int i) {
        super.setTint(i);
        if (this.mGridContent != null) {
            resetView();
            populateViews();
        }
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setSliceItem(SliceContent sliceContent, boolean z, int i, int i2, SliceView.OnSliceActionListener onSliceActionListener) {
        resetView();
        setSliceActionListener(onSliceActionListener);
        this.mRowIndex = i;
        this.mRowCount = i2;
        this.mGridContent = (GridContent) sliceContent;
        if (!scheduleMaxCellsUpdate()) {
            populateViews();
        }
        this.mViewContainer.setPadding(this.mInsetStart, this.mInsetTop + getExtraTopPadding(), this.mInsetEnd, this.mInsetBottom + getExtraBottomPadding());
    }

    private boolean scheduleMaxCellsUpdate() {
        GridContent gridContent = this.mGridContent;
        if (gridContent == null || !gridContent.isValid()) {
            return true;
        }
        if (getWidth() == 0) {
            this.mMaxCellUpdateScheduled = true;
            getViewTreeObserver().addOnPreDrawListener(this.mMaxCellsUpdater);
            return true;
        }
        this.mMaxCells = getMaxCells();
        return false;
    }

    /* access modifiers changed from: package-private */
    public int getMaxCells() {
        int i;
        GridContent gridContent = this.mGridContent;
        if (gridContent == null || !gridContent.isValid() || getWidth() == 0) {
            return -1;
        }
        if (this.mGridContent.getGridContent().size() <= 1) {
            return 1;
        }
        if (this.mGridContent.getLargestImageMode() == 2) {
            i = this.mLargeImageHeight;
        } else {
            i = this.mSmallImageMinWidth;
        }
        return getWidth() / (i + this.mGutter);
    }

    /* access modifiers changed from: package-private */
    public void populateViews() {
        GridContent gridContent = this.mGridContent;
        if (gridContent == null || !gridContent.isValid()) {
            resetView();
        } else if (!scheduleMaxCellsUpdate()) {
            if (this.mGridContent.getLayoutDir() != -1) {
                setLayoutDirection(this.mGridContent.getLayoutDir());
            }
            boolean z = true;
            if (this.mGridContent.getContentIntent() != null) {
                this.mViewContainer.setTag(new Pair(this.mGridContent.getContentIntent(), new EventInfo(getMode(), 3, 1, this.mRowIndex)));
                makeEntireGridClickable(true);
            }
            CharSequence contentDescription = this.mGridContent.getContentDescription();
            if (contentDescription != null) {
                this.mViewContainer.setContentDescription(contentDescription);
            }
            ArrayList<GridContent.CellContent> gridContent2 = this.mGridContent.getGridContent();
            if (this.mGridContent.getLargestImageMode() == 2) {
                this.mViewContainer.setGravity(48);
            } else {
                this.mViewContainer.setGravity(16);
            }
            int i = this.mMaxCells;
            if (this.mGridContent.getSeeMoreItem() == null) {
                z = false;
            }
            for (int i2 = 0; i2 < gridContent2.size(); i2++) {
                if (this.mViewContainer.getChildCount() < i) {
                    addCell(gridContent2.get(i2), i2, Math.min(gridContent2.size(), i));
                } else if (z) {
                    addSeeMoreCount(gridContent2.size() - i);
                    return;
                } else {
                    return;
                }
            }
        }
    }

    private void addSeeMoreCount(int i) {
        ViewGroup viewGroup;
        TextView textView;
        LinearLayout linearLayout = this.mViewContainer;
        View childAt = linearLayout.getChildAt(linearLayout.getChildCount() - 1);
        this.mViewContainer.removeView(childAt);
        SliceItem seeMoreItem = this.mGridContent.getSeeMoreItem();
        int childCount = this.mViewContainer.getChildCount();
        int i2 = this.mMaxCells;
        if (("slice".equals(seeMoreItem.getFormat()) || "action".equals(seeMoreItem.getFormat())) && seeMoreItem.getSlice().getItems().size() > 0) {
            addCell(new GridContent.CellContent(seeMoreItem), childCount, i2);
            return;
        }
        LayoutInflater from = LayoutInflater.from(getContext());
        if (this.mGridContent.isAllImages()) {
            viewGroup = (FrameLayout) from.inflate(R$layout.abc_slice_grid_see_more_overlay, (ViewGroup) this.mViewContainer, false);
            viewGroup.addView(childAt, 0, new FrameLayout.LayoutParams(-1, -1));
            textView = (TextView) viewGroup.findViewById(R$id.text_see_more_count);
        } else {
            viewGroup = (LinearLayout) from.inflate(R$layout.abc_slice_grid_see_more, (ViewGroup) this.mViewContainer, false);
            textView = (TextView) viewGroup.findViewById(R$id.text_see_more_count);
            TextView textView2 = (TextView) viewGroup.findViewById(R$id.text_see_more);
            SliceStyle sliceStyle = this.mSliceStyle;
            if (sliceStyle != null) {
                textView2.setTextSize(0, (float) sliceStyle.getGridTitleSize());
                textView2.setTextColor(this.mSliceStyle.getTitleColor());
            }
        }
        this.mViewContainer.addView(viewGroup, new LinearLayout.LayoutParams(0, -1, 1.0f));
        textView.setText(getResources().getString(R$string.abc_slice_more_content, Integer.valueOf(i)));
        EventInfo eventInfo = new EventInfo(getMode(), 4, 1, this.mRowIndex);
        eventInfo.setPosition(2, childCount, i2);
        viewGroup.setTag(new Pair(seeMoreItem, eventInfo));
        makeClickable(viewGroup, true);
    }

    private void addCell(GridContent.CellContent cellContent, int i, int i2) {
        ArrayList arrayList;
        ArrayList arrayList2;
        int i3;
        String str;
        int i4;
        int i5;
        SliceItem sliceItem;
        int i6 = (getMode() != 1 || !this.mGridContent.hasImage()) ? 2 : 1;
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        linearLayout.setGravity(1);
        ArrayList<SliceItem> cellItems = cellContent.getCellItems();
        SliceItem contentIntent = cellContent.getContentIntent();
        boolean z = cellItems.size() == 1;
        String str2 = "text";
        if (z || getMode() != 1) {
            arrayList = null;
        } else {
            ArrayList arrayList3 = new ArrayList();
            Iterator<SliceItem> it = cellItems.iterator();
            while (it.hasNext()) {
                SliceItem next = it.next();
                if (str2.equals(next.getFormat())) {
                    arrayList3.add(next);
                }
            }
            Iterator it2 = arrayList3.iterator();
            while (arrayList3.size() > i6) {
                if (!((SliceItem) it2.next()).hasAnyHints("title", "large")) {
                    it2.remove();
                }
            }
            arrayList = arrayList3;
        }
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        SliceItem sliceItem2 = null;
        boolean z2 = false;
        while (i9 < cellItems.size()) {
            SliceItem sliceItem3 = cellItems.get(i9);
            String format = sliceItem3.getFormat();
            int determinePadding = determinePadding(sliceItem2);
            if (i8 >= i6 || (!str2.equals(format) && !"long".equals(format))) {
                i5 = i7;
                i4 = i8;
                i3 = i9;
                sliceItem = sliceItem2;
                arrayList2 = arrayList;
                str = str2;
                if (i5 < 1 && "image".equals(sliceItem3.getFormat()) && addItem(sliceItem3, this.mTintColor, linearLayout, 0, z)) {
                    i7 = i5 + 1;
                    sliceItem2 = sliceItem3;
                    i8 = i4;
                }
                sliceItem2 = sliceItem;
                i7 = i5;
                i8 = i4;
                i9 = i3 + 1;
                str2 = str;
                arrayList = arrayList2;
            } else if (arrayList == null || arrayList.contains(sliceItem3)) {
                i5 = i7;
                i4 = i8;
                i3 = i9;
                sliceItem = sliceItem2;
                arrayList2 = arrayList;
                str = str2;
                if (addItem(sliceItem3, this.mTintColor, linearLayout, determinePadding, z)) {
                    i8 = i4 + 1;
                    sliceItem2 = sliceItem3;
                    i7 = i5;
                }
                sliceItem2 = sliceItem;
                i7 = i5;
                i8 = i4;
                i9 = i3 + 1;
                str2 = str;
                arrayList = arrayList2;
            } else {
                i5 = i7;
                i4 = i8;
                i3 = i9;
                sliceItem = sliceItem2;
                arrayList2 = arrayList;
                str = str2;
                sliceItem2 = sliceItem;
                i7 = i5;
                i8 = i4;
                i9 = i3 + 1;
                str2 = str;
                arrayList = arrayList2;
            }
            z2 = true;
            i9 = i3 + 1;
            str2 = str;
            arrayList = arrayList2;
        }
        if (z2) {
            CharSequence contentDescription = cellContent.getContentDescription();
            if (contentDescription != null) {
                linearLayout.setContentDescription(contentDescription);
            }
            this.mViewContainer.addView(linearLayout, new LinearLayout.LayoutParams(0, -2, 1.0f));
            if (i != i2 - 1) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
                marginLayoutParams.setMarginEnd(this.mGutter);
                linearLayout.setLayoutParams(marginLayoutParams);
            }
            if (contentIntent != null) {
                EventInfo eventInfo = new EventInfo(getMode(), 1, 1, this.mRowIndex);
                eventInfo.setPosition(2, i, i2);
                linearLayout.setTag(new Pair(contentIntent, eventInfo));
                makeClickable(linearLayout, true);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v4, resolved type: android.widget.ImageView */
    /* JADX WARN: Multi-variable type inference failed */
    private boolean addItem(SliceItem sliceItem, int i, ViewGroup viewGroup, int i2, boolean z) {
        CharSequence charSequence;
        Drawable loadDrawable;
        ViewGroup.LayoutParams layoutParams;
        ViewGroup.LayoutParams layoutParams2;
        int i3;
        String format = sliceItem.getFormat();
        TextView textView = null;
        textView = null;
        textView = null;
        if ("text".equals(format) || "long".equals(format)) {
            boolean hasAnyHints = SliceQuery.hasAnyHints(sliceItem, "large", "title");
            TextView textView2 = (TextView) LayoutInflater.from(getContext()).inflate(hasAnyHints ? TITLE_TEXT_LAYOUT : TEXT_LAYOUT, (ViewGroup) null);
            SliceStyle sliceStyle = this.mSliceStyle;
            if (sliceStyle != null) {
                textView2.setTextSize(0, (float) (hasAnyHints ? sliceStyle.getGridTitleSize() : sliceStyle.getGridSubtitleSize()));
                textView2.setTextColor(hasAnyHints ? this.mSliceStyle.getTitleColor() : this.mSliceStyle.getSubtitleColor());
            }
            if ("long".equals(format)) {
                charSequence = SliceViewUtil.getTimestampString(getContext(), sliceItem.getLong());
            } else {
                charSequence = sliceItem.getSanitizedText();
            }
            textView2.setText(charSequence);
            viewGroup.addView(textView2);
            textView2.setPadding(0, i2, 0, 0);
            textView = textView2;
        } else if (!(!"image".equals(format) || sliceItem.getIcon() == null || (loadDrawable = sliceItem.getIcon().loadDrawable(getContext())) == null)) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(loadDrawable);
            if (sliceItem.hasHint("raw")) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                layoutParams = new LinearLayout.LayoutParams(-1, -2);
            } else {
                if (sliceItem.hasHint("large")) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    if (z) {
                        i3 = -1;
                    } else {
                        i3 = this.mLargeImageHeight;
                    }
                    layoutParams2 = new LinearLayout.LayoutParams(-1, i3);
                } else {
                    boolean z2 = !sliceItem.hasHint("no_tint");
                    int i4 = !z2 ? this.mSmallImageSize : this.mIconSize;
                    imageView.setScaleType(z2 ? ImageView.ScaleType.CENTER_INSIDE : ImageView.ScaleType.CENTER_CROP);
                    layoutParams2 = new LinearLayout.LayoutParams(i4, i4);
                }
                layoutParams = layoutParams2;
            }
            if (i != -1 && !sliceItem.hasHint("no_tint")) {
                imageView.setColorFilter(i);
            }
            viewGroup.addView(imageView, layoutParams);
            textView = imageView;
        }
        return textView != null;
    }

    private int determinePadding(SliceItem sliceItem) {
        SliceStyle sliceStyle;
        if (sliceItem == null) {
            return 0;
        }
        if ("image".equals(sliceItem.getFormat())) {
            return this.mTextPadding;
        }
        if (("text".equals(sliceItem.getFormat()) || "long".equals(sliceItem.getFormat())) && (sliceStyle = this.mSliceStyle) != null) {
            return sliceStyle.getVerticalGridTextPadding();
        }
        return 0;
    }

    private void makeEntireGridClickable(boolean z) {
        Drawable drawable = null;
        this.mViewContainer.setOnTouchListener(z ? this : null);
        this.mViewContainer.setOnClickListener(z ? this : null);
        View view = this.mForeground;
        if (z) {
            drawable = SliceViewUtil.getDrawable(getContext(), 16843534);
        }
        view.setBackground(drawable);
        this.mViewContainer.setClickable(z);
    }

    private void makeClickable(View view, boolean z) {
        Drawable drawable = null;
        view.setOnClickListener(z ? this : null);
        int i = 16843534;
        if (Build.VERSION.SDK_INT >= 21) {
            i = 16843868;
        }
        if (z) {
            drawable = SliceViewUtil.getDrawable(getContext(), i);
        }
        view.setBackground(drawable);
        view.setClickable(z);
    }

    public void onClick(View view) {
        SliceItem find;
        Pair pair = (Pair) view.getTag();
        SliceItem sliceItem = (SliceItem) pair.first;
        EventInfo eventInfo = (EventInfo) pair.second;
        if (sliceItem != null && (find = SliceQuery.find(sliceItem, "action", (String) null, (String) null)) != null) {
            try {
                find.fireAction(null, null);
                if (this.mObserver != null) {
                    this.mObserver.onSliceAction(eventInfo, find);
                }
            } catch (PendingIntent.CanceledException e) {
                Log.e("GridRowView", "PendingIntent for slice cannot be sent", e);
            }
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        onForegroundActivated(motionEvent);
        return false;
    }

    private void onForegroundActivated(MotionEvent motionEvent) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.mForeground.getLocationOnScreen(this.mLoc);
            this.mForeground.getBackground().setHotspot((float) ((int) (motionEvent.getRawX() - ((float) this.mLoc[0]))), (float) ((int) (motionEvent.getRawY() - ((float) this.mLoc[1]))));
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mForeground.setPressed(true);
        } else if (actionMasked == 3 || actionMasked == 1 || actionMasked == 2) {
            this.mForeground.setPressed(false);
        }
    }

    @Override // androidx.slice.widget.SliceChildView
    public void resetView() {
        if (this.mMaxCellUpdateScheduled) {
            this.mMaxCellUpdateScheduled = false;
            getViewTreeObserver().removeOnPreDrawListener(this.mMaxCellsUpdater);
        }
        this.mViewContainer.removeAllViews();
        setLayoutDirection(2);
        makeEntireGridClickable(false);
    }
}
