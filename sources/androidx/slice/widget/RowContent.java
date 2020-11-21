package androidx.slice.widget;

import android.text.TextUtils;
import android.util.Log;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import java.util.ArrayList;
import java.util.List;

public class RowContent extends SliceContent {
    private ArrayList<SliceItem> mEndItems = new ArrayList<>();
    private boolean mIsHeader;
    private int mLineCount;
    private SliceItem mPrimaryAction;
    private SliceItem mRange;
    private SliceItem mSelection;
    private boolean mShowActionDivider;
    private boolean mShowBottomDivider;
    private boolean mShowTitleItems;
    private SliceItem mStartItem;
    private SliceItem mSubtitleItem;
    private SliceItem mSummaryItem;
    private SliceItem mTitleItem;
    private ArrayList<SliceAction> mToggleItems = new ArrayList<>();

    public RowContent(SliceItem sliceItem, int i) {
        super(sliceItem, i);
        boolean z = false;
        this.mLineCount = 0;
        populate(sliceItem, i == 0 ? true : z);
    }

    private boolean populate(SliceItem sliceItem, boolean z) {
        boolean z2;
        this.mIsHeader = z;
        if (!isValidRow(sliceItem)) {
            Log.w("RowContent", "Provided SliceItem is invalid for RowContent");
            return false;
        }
        determineStartAndPrimaryAction(sliceItem);
        ArrayList<SliceItem> filterInvalidItems = filterInvalidItems(sliceItem);
        if (filterInvalidItems.size() != 1 || ((!"action".equals(filterInvalidItems.get(0).getFormat()) && !"slice".equals(filterInvalidItems.get(0).getFormat())) || filterInvalidItems.get(0).hasAnyHints("shortcut", "title") || !isValidRow(filterInvalidItems.get(0)))) {
            z2 = false;
        } else {
            sliceItem = filterInvalidItems.get(0);
            filterInvalidItems = filterInvalidItems(sliceItem);
            z2 = true;
        }
        if ("range".equals(sliceItem.getSubType())) {
            if (SliceQuery.findSubtype(sliceItem, "action", "range") == null || z2) {
                this.mRange = sliceItem;
            } else {
                filterInvalidItems.remove(this.mStartItem);
                if (filterInvalidItems.size() != 1) {
                    SliceItem findSubtype = SliceQuery.findSubtype(sliceItem, "action", "range");
                    this.mRange = findSubtype;
                    ArrayList<SliceItem> filterInvalidItems2 = filterInvalidItems(findSubtype);
                    filterInvalidItems2.remove(getInputRangeThumb());
                    filterInvalidItems.remove(this.mRange);
                    filterInvalidItems.addAll(filterInvalidItems2);
                } else if (isValidRow(filterInvalidItems.get(0))) {
                    sliceItem = filterInvalidItems.get(0);
                    filterInvalidItems = filterInvalidItems(sliceItem);
                    this.mRange = sliceItem;
                    filterInvalidItems.remove(getInputRangeThumb());
                }
            }
        }
        if ("selection".equals(sliceItem.getSubType())) {
            this.mSelection = sliceItem;
        }
        if (filterInvalidItems.size() > 0) {
            SliceItem sliceItem2 = this.mStartItem;
            if (sliceItem2 != null) {
                filterInvalidItems.remove(sliceItem2);
            }
            SliceItem sliceItem3 = this.mPrimaryAction;
            if (sliceItem3 != null) {
                filterInvalidItems.remove(sliceItem3);
            }
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < filterInvalidItems.size(); i++) {
                SliceItem sliceItem4 = filterInvalidItems.get(i);
                if ("text".equals(sliceItem4.getFormat())) {
                    SliceItem sliceItem5 = this.mTitleItem;
                    if ((sliceItem5 == null || !sliceItem5.hasHint("title")) && sliceItem4.hasHint("title") && !sliceItem4.hasHint("summary")) {
                        this.mTitleItem = sliceItem4;
                    } else if (this.mSubtitleItem == null && !sliceItem4.hasHint("summary")) {
                        this.mSubtitleItem = sliceItem4;
                    } else if (this.mSummaryItem == null && sliceItem4.hasHint("summary")) {
                        this.mSummaryItem = sliceItem4;
                    }
                } else {
                    arrayList.add(sliceItem4);
                }
            }
            if (hasText(this.mTitleItem)) {
                this.mLineCount++;
            }
            if (hasText(this.mSubtitleItem)) {
                this.mLineCount++;
            }
            SliceItem sliceItem6 = this.mStartItem;
            boolean z3 = sliceItem6 != null && "long".equals(sliceItem6.getFormat());
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                SliceItem sliceItem7 = (SliceItem) arrayList.get(i2);
                boolean z4 = SliceQuery.find(sliceItem7, "action") != null;
                if (!"long".equals(sliceItem7.getFormat())) {
                    processContent(sliceItem7, z4);
                } else if (!z3) {
                    this.mEndItems.add(sliceItem7);
                    z3 = true;
                }
            }
        }
        return isValid();
    }

    private void processContent(SliceItem sliceItem, boolean z) {
        if (z) {
            SliceActionImpl sliceActionImpl = new SliceActionImpl(sliceItem);
            if (sliceActionImpl.isToggle()) {
                this.mToggleItems.add(sliceActionImpl);
            }
        }
        this.mEndItems.add(sliceItem);
    }

    private void determineStartAndPrimaryAction(SliceItem sliceItem) {
        List<SliceItem> findAll = SliceQuery.findAll(sliceItem, (String) null, "title", (String) null);
        if (findAll.size() > 0) {
            String format = findAll.get(0).getFormat();
            if (("action".equals(format) && SliceQuery.find(findAll.get(0), "image") != null) || "slice".equals(format) || "long".equals(format) || "image".equals(format)) {
                this.mStartItem = findAll.get(0);
            }
        }
        String[] strArr = {"shortcut", "title"};
        List<SliceItem> findAll2 = SliceQuery.findAll(sliceItem, "slice", strArr, (String[]) null);
        findAll2.addAll(SliceQuery.findAll(sliceItem, "action", strArr, (String[]) null));
        if (findAll2.isEmpty() && "action".equals(sliceItem.getFormat()) && sliceItem.getSlice().getItems().size() == 1) {
            this.mPrimaryAction = sliceItem;
        } else if (this.mStartItem != null && findAll2.size() > 1 && findAll2.get(0) == this.mStartItem) {
            this.mPrimaryAction = findAll2.get(1);
        } else if (findAll2.size() > 0) {
            this.mPrimaryAction = findAll2.get(0);
        }
    }

    @Override // androidx.slice.widget.SliceContent
    public boolean isValid() {
        return super.isValid() && !(this.mStartItem == null && this.mPrimaryAction == null && this.mTitleItem == null && this.mSubtitleItem == null && this.mEndItems.size() <= 0 && this.mRange == null && this.mSelection == null && !isDefaultSeeMore());
    }

    public boolean getIsHeader() {
        return this.mIsHeader;
    }

    public void setIsHeader(boolean z) {
        this.mIsHeader = z;
    }

    public SliceItem getRange() {
        return this.mRange;
    }

    public SliceItem getSelection() {
        return this.mSelection;
    }

    public SliceItem getInputRangeThumb() {
        SliceItem sliceItem = this.mRange;
        if (sliceItem == null) {
            return null;
        }
        List<SliceItem> items = sliceItem.getSlice().getItems();
        for (int i = 0; i < items.size(); i++) {
            if ("image".equals(items.get(i).getFormat())) {
                return items.get(i);
            }
        }
        return null;
    }

    public SliceItem getPrimaryAction() {
        return this.mPrimaryAction;
    }

    public SliceItem getStartItem() {
        if (!this.mIsHeader || this.mShowTitleItems) {
            return this.mStartItem;
        }
        return null;
    }

    public SliceItem getTitleItem() {
        return this.mTitleItem;
    }

    public SliceItem getSubtitleItem() {
        return this.mSubtitleItem;
    }

    public SliceItem getSummaryItem() {
        SliceItem sliceItem = this.mSummaryItem;
        return sliceItem == null ? this.mSubtitleItem : sliceItem;
    }

    public ArrayList<SliceItem> getEndItems() {
        return this.mEndItems;
    }

    public ArrayList<SliceAction> getToggleItems() {
        return this.mToggleItems;
    }

    public int getLineCount() {
        return this.mLineCount;
    }

    @Override // androidx.slice.widget.SliceContent
    public int getHeight(SliceStyle sliceStyle, SliceViewPolicy sliceViewPolicy) {
        return sliceStyle.getRowHeight(this, sliceViewPolicy);
    }

    public boolean isDefaultSeeMore() {
        return "action".equals(this.mSliceItem.getFormat()) && this.mSliceItem.getSlice().hasHint("see_more") && this.mSliceItem.getSlice().getItems().isEmpty();
    }

    public void showTitleItems(boolean z) {
        this.mShowTitleItems = z;
    }

    public boolean hasTitleItems() {
        return this.mShowTitleItems;
    }

    public void showBottomDivider(boolean z) {
        this.mShowBottomDivider = z;
    }

    public boolean hasBottomDivider() {
        return this.mShowBottomDivider;
    }

    public void showActionDivider(boolean z) {
        this.mShowActionDivider = z;
    }

    public boolean hasActionDivider() {
        return this.mShowActionDivider;
    }

    private static boolean hasText(SliceItem sliceItem) {
        return sliceItem != null && (sliceItem.hasHint("partial") || !TextUtils.isEmpty(sliceItem.getText()));
    }

    private static boolean isValidRow(SliceItem sliceItem) {
        if (sliceItem == null) {
            return false;
        }
        if ("slice".equals(sliceItem.getFormat()) || "action".equals(sliceItem.getFormat())) {
            List<SliceItem> items = sliceItem.getSlice().getItems();
            if (sliceItem.hasHint("see_more") && items.isEmpty()) {
                return true;
            }
            for (int i = 0; i < items.size(); i++) {
                if (isValidRowContent(sliceItem, items.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ArrayList<SliceItem> filterInvalidItems(SliceItem sliceItem) {
        ArrayList<SliceItem> arrayList = new ArrayList<>();
        for (SliceItem sliceItem2 : sliceItem.getSlice().getItems()) {
            if (isValidRowContent(sliceItem, sliceItem2)) {
                arrayList.add(sliceItem2);
            }
        }
        return arrayList;
    }

    private static boolean isValidRowContent(SliceItem sliceItem, SliceItem sliceItem2) {
        if (sliceItem2.hasAnyHints("keywords", "ttl", "last_updated", "horizontal") || "content_description".equals(sliceItem2.getSubType()) || "selection_option_key".equals(sliceItem2.getSubType()) || "selection_option_value".equals(sliceItem2.getSubType())) {
            return false;
        }
        String format = sliceItem2.getFormat();
        if ("image".equals(format) || "text".equals(format) || "long".equals(format) || "action".equals(format) || "input".equals(format) || "slice".equals(format) || ("int".equals(format) && "range".equals(sliceItem.getSubType()))) {
            return true;
        }
        return false;
    }
}
