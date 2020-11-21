package androidx.slice.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.slice.view.R$dimen;
import androidx.slice.view.R$styleable;
import java.util.ArrayList;
import java.util.List;

public class SliceStyle {
    private int mGridAllImagesHeight;
    private int mGridBigPicMaxHeight;
    private int mGridBigPicMinHeight;
    private int mGridBottomPadding;
    private int mGridImageTextHeight;
    private int mGridMaxHeight;
    private int mGridMinHeight;
    private int mGridSubtitleSize;
    private int mGridTitleSize;
    private int mGridTopPadding;
    private int mHeaderSubtitleSize;
    private int mHeaderTitleSize;
    private int mListLargeHeight;
    private int mListMinScrollHeight;
    private int mRowInlineRangeHeight;
    private int mRowMaxHeight;
    private int mRowMinHeight;
    private int mRowRangeHeight;
    private int mRowSelectionHeight;
    private int mRowSingleTextWithRangeHeight;
    private int mRowSingleTextWithSelectionHeight;
    private RowStyle mRowStyle;
    private int mRowTextWithRangeHeight;
    private int mRowTextWithSelectionHeight;
    private int mSubtitleColor;
    private int mSubtitleSize;
    private int mTintColor = -1;
    private int mTitleColor;
    private int mTitleSize;
    private int mVerticalGridTextPadding;
    private int mVerticalHeaderTextPadding;
    private int mVerticalTextPadding;

    /* JADX INFO: finally extract failed */
    public SliceStyle(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R$styleable.SliceView, i, i2);
        try {
            int color = obtainStyledAttributes.getColor(R$styleable.SliceView_tintColor, -1);
            if (color == -1) {
                color = this.mTintColor;
            }
            this.mTintColor = color;
            this.mTitleColor = obtainStyledAttributes.getColor(R$styleable.SliceView_titleColor, 0);
            this.mSubtitleColor = obtainStyledAttributes.getColor(R$styleable.SliceView_subtitleColor, 0);
            this.mHeaderTitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_headerTitleSize, 0.0f);
            this.mHeaderSubtitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_headerSubtitleSize, 0.0f);
            this.mVerticalHeaderTextPadding = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_headerTextVerticalPadding, 0.0f);
            this.mTitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_titleSize, 0.0f);
            this.mSubtitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_subtitleSize, 0.0f);
            this.mVerticalTextPadding = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_textVerticalPadding, 0.0f);
            this.mGridTitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_gridTitleSize, 0.0f);
            this.mGridSubtitleSize = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_gridSubtitleSize, 0.0f);
            this.mVerticalGridTextPadding = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_gridTextVerticalPadding, (float) context.getResources().getDimensionPixelSize(R$dimen.abc_slice_grid_text_inner_padding));
            this.mGridTopPadding = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_gridTopPadding, 0.0f);
            this.mGridBottomPadding = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_gridBottomPadding, 0.0f);
            int resourceId = obtainStyledAttributes.getResourceId(R$styleable.SliceView_rowStyle, 0);
            if (resourceId != 0) {
                this.mRowStyle = new RowStyle(context, resourceId);
            }
            this.mRowMaxHeight = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_rowMaxHeight, (float) context.getResources().getDimensionPixelSize(R$dimen.abc_slice_row_max_height));
            this.mRowRangeHeight = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_rowRangeHeight, (float) context.getResources().getDimensionPixelSize(R$dimen.abc_slice_row_range_height));
            this.mRowSingleTextWithRangeHeight = (int) obtainStyledAttributes.getDimension(R$styleable.SliceView_rowRangeSingleTextHeight, (float) context.getResources().getDimensionPixelSize(R$dimen.abc_slice_row_range_single_text_height));
            obtainStyledAttributes.recycle();
            Resources resources = context.getResources();
            this.mRowTextWithRangeHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_range_multi_text_height);
            this.mRowMinHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_min_height);
            this.mRowSelectionHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_selection_height);
            this.mRowTextWithSelectionHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_selection_multi_text_height);
            this.mRowSingleTextWithSelectionHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_selection_single_text_height);
            this.mRowInlineRangeHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_range_inline_height);
            this.mGridBigPicMinHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_big_pic_min_height);
            this.mGridBigPicMaxHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_big_pic_max_height);
            this.mGridAllImagesHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_image_only_height);
            this.mGridImageTextHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_image_text_height);
            this.mGridMinHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_min_height);
            this.mGridMaxHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_max_height);
            this.mListMinScrollHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_row_min_height);
            this.mListLargeHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_large_height);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    public int getRowMaxHeight() {
        return this.mRowMaxHeight;
    }

    public void setTintColor(int i) {
        this.mTintColor = i;
    }

    public int getTintColor() {
        return this.mTintColor;
    }

    public int getTitleColor() {
        return this.mTitleColor;
    }

    public int getSubtitleColor() {
        return this.mSubtitleColor;
    }

    public int getHeaderTitleSize() {
        return this.mHeaderTitleSize;
    }

    public int getHeaderSubtitleSize() {
        return this.mHeaderSubtitleSize;
    }

    public int getVerticalHeaderTextPadding() {
        return this.mVerticalHeaderTextPadding;
    }

    public int getTitleSize() {
        return this.mTitleSize;
    }

    public int getSubtitleSize() {
        return this.mSubtitleSize;
    }

    public int getVerticalTextPadding() {
        return this.mVerticalTextPadding;
    }

    public int getGridTitleSize() {
        return this.mGridTitleSize;
    }

    public int getGridSubtitleSize() {
        return this.mGridSubtitleSize;
    }

    public int getVerticalGridTextPadding() {
        return this.mVerticalGridTextPadding;
    }

    public int getGridTopPadding() {
        return this.mGridTopPadding;
    }

    public int getGridBottomPadding() {
        return this.mGridBottomPadding;
    }

    public RowStyle getRowStyle() {
        return this.mRowStyle;
    }

    public int getRowRangeHeight() {
        return this.mRowRangeHeight;
    }

    public int getRowSelectionHeight() {
        return this.mRowSelectionHeight;
    }

    public int getRowHeight(RowContent rowContent, SliceViewPolicy sliceViewPolicy) {
        int i;
        int i2;
        int maxSmallHeight = sliceViewPolicy.getMaxSmallHeight() > 0 ? sliceViewPolicy.getMaxSmallHeight() : this.mRowMaxHeight;
        if (rowContent.getRange() == null && rowContent.getSelection() == null && sliceViewPolicy.getMode() != 2) {
            return maxSmallHeight;
        }
        if (rowContent.getRange() != null) {
            if (rowContent.getStartItem() != null) {
                return this.mRowInlineRangeHeight;
            }
            if (rowContent.getLineCount() > 1) {
                i = this.mRowTextWithRangeHeight;
            } else {
                i = this.mRowSingleTextWithRangeHeight;
            }
            i2 = this.mRowRangeHeight;
        } else if (rowContent.getSelection() == null) {
            return (rowContent.getLineCount() > 1 || rowContent.getIsHeader()) ? maxSmallHeight : this.mRowMinHeight;
        } else {
            if (rowContent.getLineCount() > 1) {
                i = this.mRowTextWithSelectionHeight;
            } else {
                i = this.mRowSingleTextWithSelectionHeight;
            }
            i2 = this.mRowSelectionHeight;
        }
        return i + i2;
    }

    public int getGridHeight(GridContent gridContent, SliceViewPolicy sliceViewPolicy) {
        int i;
        int i2 = 0;
        boolean z = true;
        boolean z2 = sliceViewPolicy.getMode() == 1;
        if (!gridContent.isValid()) {
            return 0;
        }
        int largestImageMode = gridContent.getLargestImageMode();
        if (gridContent.isAllImages()) {
            i = gridContent.getGridContent().size() == 1 ? z2 ? this.mGridBigPicMinHeight : this.mGridBigPicMaxHeight : largestImageMode == 0 ? this.mGridMinHeight : this.mGridAllImagesHeight;
        } else {
            boolean z3 = gridContent.getMaxCellLineCount() > 1;
            boolean hasImage = gridContent.hasImage();
            if (!(largestImageMode == 0 || largestImageMode == 5)) {
                z = false;
            }
            if (!z3 || z2) {
                i = z ? this.mGridMinHeight : this.mGridImageTextHeight;
            } else {
                i = hasImage ? this.mGridMaxHeight : this.mGridMinHeight;
            }
        }
        int i3 = (!gridContent.isAllImages() || gridContent.getRowIndex() != 0) ? 0 : this.mGridTopPadding;
        if (gridContent.isAllImages() && gridContent.getIsLastIndex()) {
            i2 = this.mGridBottomPadding;
        }
        return i + i3 + i2;
    }

    public int getListHeight(ListContent listContent, SliceViewPolicy sliceViewPolicy) {
        int i;
        boolean z = true;
        if (sliceViewPolicy.getMode() == 1) {
            return listContent.getHeader().getHeight(this, sliceViewPolicy);
        }
        int maxHeight = sliceViewPolicy.getMaxHeight();
        boolean isScrollable = sliceViewPolicy.isScrollable();
        int listItemsHeight = getListItemsHeight(listContent.getRowItems(), sliceViewPolicy);
        if (maxHeight > 0) {
            maxHeight = Math.max(listContent.getHeader().getHeight(this, sliceViewPolicy), maxHeight);
        }
        if (maxHeight > 0) {
            i = maxHeight;
        } else {
            i = this.mListLargeHeight;
        }
        if (listItemsHeight - i < this.mListMinScrollHeight) {
            z = false;
        }
        if (z) {
            listItemsHeight = i;
        } else if (maxHeight > 0) {
            listItemsHeight = Math.min(i, listItemsHeight);
        }
        return !isScrollable ? getListItemsHeight(getListItemsForNonScrollingList(listContent, listItemsHeight, sliceViewPolicy), sliceViewPolicy) : listItemsHeight;
    }

    public int getListItemsHeight(List<SliceContent> list, SliceViewPolicy sliceViewPolicy) {
        if (list == null) {
            return 0;
        }
        SliceContent sliceContent = null;
        if (!list.isEmpty()) {
            sliceContent = list.get(0);
        }
        if (list.size() == 1 && !sliceContent.getSliceItem().hasHint("horizontal")) {
            return sliceContent.getHeight(this, sliceViewPolicy);
        }
        int i = 0;
        for (int i2 = 0; i2 < list.size(); i2++) {
            i += list.get(i2).getHeight(this, sliceViewPolicy);
        }
        return i;
    }

    public ArrayList<SliceContent> getListItemsForNonScrollingList(ListContent listContent, int i, SliceViewPolicy sliceViewPolicy) {
        ArrayList<SliceContent> arrayList = new ArrayList<>();
        if (!(listContent.getRowItems() == null || listContent.getRowItems().size() == 0)) {
            int i2 = listContent.getRowItems() != null ? 2 : 1;
            int height = listContent.getSeeMoreItem() != null ? listContent.getSeeMoreItem().getHeight(this, sliceViewPolicy) + 0 : 0;
            int size = listContent.getRowItems().size();
            for (int i3 = 0; i3 < size; i3++) {
                int height2 = listContent.getRowItems().get(i3).getHeight(this, sliceViewPolicy);
                if (i > 0 && height + height2 > i) {
                    break;
                }
                height += height2;
                arrayList.add(listContent.getRowItems().get(i3));
            }
            if (!(listContent.getSeeMoreItem() == null || arrayList.size() < i2 || arrayList.size() == size)) {
                arrayList.add(listContent.getSeeMoreItem());
            }
            if (arrayList.size() == 0) {
                arrayList.add(listContent.getRowItems().get(0));
            }
        }
        return arrayList;
    }
}
