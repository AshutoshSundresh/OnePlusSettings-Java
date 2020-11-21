package androidx.slice.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.view.ViewCompat;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import androidx.slice.view.R$dimen;
import androidx.slice.view.R$id;
import androidx.slice.view.R$layout;
import androidx.slice.view.R$plurals;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RowView extends SliceChildView implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final boolean sCanSpecifyLargerRangeBarHeight = (Build.VERSION.SDK_INT >= 23);
    private View mActionDivider;
    private ProgressBar mActionSpinner;
    private ArrayMap<SliceActionImpl, SliceActionView> mActions = new ArrayMap<>();
    private boolean mAllowTwoLines;
    private View mBottomDivider;
    private LinearLayout mContent;
    private LinearLayout mEndContainer;
    Handler mHandler;
    private List<SliceAction> mHeaderActions;
    private int mIconSize = getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_icon_size);
    private int mImageSize = getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_small_image_size);
    private boolean mIsHeader;
    boolean mIsRangeSliding;
    long mLastSentRangeUpdate;
    private TextView mLastUpdatedText;
    protected Set<SliceItem> mLoadingActions = new HashSet();
    private int mMeasuredRangeHeight;
    private TextView mPrimaryText;
    private ProgressBar mRangeBar;
    boolean mRangeHasPendingUpdate;
    private SliceItem mRangeItem;
    int mRangeMaxValue;
    int mRangeMinValue;
    Runnable mRangeUpdater = new Runnable() {
        /* class androidx.slice.widget.RowView.AnonymousClass2 */

        public void run() {
            RowView.this.sendSliderValue();
            RowView.this.mRangeUpdaterRunning = false;
        }
    };
    boolean mRangeUpdaterRunning;
    int mRangeValue;
    private LinearLayout mRootView;
    private SliceActionImpl mRowAction;
    RowContent mRowContent;
    int mRowIndex;
    private TextView mSecondaryText;
    private View mSeeMoreView;
    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        /* class androidx.slice.widget.RowView.AnonymousClass3 */

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            RowView rowView = RowView.this;
            rowView.mRangeValue = i + rowView.mRangeMinValue;
            long currentTimeMillis = System.currentTimeMillis();
            RowView rowView2 = RowView.this;
            long j = rowView2.mLastSentRangeUpdate;
            if (j == 0 || currentTimeMillis - j <= 200) {
                RowView rowView3 = RowView.this;
                if (!rowView3.mRangeUpdaterRunning) {
                    rowView3.mRangeUpdaterRunning = true;
                    rowView3.mHandler.postDelayed(rowView3.mRangeUpdater, 200);
                    return;
                }
                return;
            }
            rowView2.mRangeUpdaterRunning = false;
            rowView2.mHandler.removeCallbacks(rowView2.mRangeUpdater);
            RowView.this.sendSliderValue();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            RowView.this.mIsRangeSliding = true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            RowView rowView = RowView.this;
            rowView.mIsRangeSliding = false;
            if (rowView.mRangeUpdaterRunning || rowView.mRangeHasPendingUpdate) {
                RowView rowView2 = RowView.this;
                rowView2.mRangeUpdaterRunning = false;
                rowView2.mRangeHasPendingUpdate = false;
                rowView2.mHandler.removeCallbacks(rowView2.mRangeUpdater);
                RowView rowView3 = RowView.this;
                int progress = seekBar.getProgress();
                RowView rowView4 = RowView.this;
                rowView3.mRangeValue = progress + rowView4.mRangeMinValue;
                rowView4.sendSliderValue();
            }
        }
    };
    private SliceItem mSelectionItem;
    private ArrayList<String> mSelectionOptionKeys;
    private ArrayList<CharSequence> mSelectionOptionValues;
    private Spinner mSelectionSpinner;
    boolean mShowActionSpinner;
    private LinearLayout mStartContainer;
    private SliceItem mStartItem;
    private LinearLayout mSubContent;
    private ArrayMap<SliceActionImpl, SliceActionView> mToggles = new ArrayMap<>();

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public RowView(Context context) {
        super(context);
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R$layout.abc_slice_small_template, (ViewGroup) this, false);
        this.mRootView = linearLayout;
        addView(linearLayout);
        this.mStartContainer = (LinearLayout) findViewById(R$id.icon_frame);
        this.mContent = (LinearLayout) findViewById(16908290);
        this.mSubContent = (LinearLayout) findViewById(R$id.subcontent);
        this.mPrimaryText = (TextView) findViewById(16908310);
        this.mSecondaryText = (TextView) findViewById(16908304);
        this.mLastUpdatedText = (TextView) findViewById(R$id.last_updated);
        this.mBottomDivider = findViewById(R$id.bottom_divider);
        this.mActionDivider = findViewById(R$id.action_divider);
        this.mActionSpinner = (ProgressBar) findViewById(R$id.action_sent_indicator);
        SliceViewUtil.tintIndeterminateProgressBar(getContext(), this.mActionSpinner);
        this.mEndContainer = (LinearLayout) findViewById(16908312);
        ViewCompat.setImportantForAccessibility(this, 2);
        ViewCompat.setImportantForAccessibility(this.mContent, 2);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setStyle(SliceStyle sliceStyle) {
        super.setStyle(sliceStyle);
        applyRowStyle();
    }

    private void applyRowStyle() {
        SliceStyle sliceStyle = this.mSliceStyle;
        if (sliceStyle != null && sliceStyle.getRowStyle() != null) {
            RowStyle rowStyle = this.mSliceStyle.getRowStyle();
            setViewSidePaddings(this.mStartContainer, rowStyle.getTitleItemStartPadding(), rowStyle.getTitleItemEndPadding());
            setViewSidePaddings(this.mContent, rowStyle.getContentStartPadding(), rowStyle.getContentEndPadding());
            setViewSidePaddings(this.mPrimaryText, rowStyle.getTitleStartPadding(), rowStyle.getTitleEndPadding());
            setViewSidePaddings(this.mSubContent, rowStyle.getSubContentStartPadding(), rowStyle.getSubContentEndPadding());
            setViewSidePaddings(this.mEndContainer, rowStyle.getEndItemStartPadding(), rowStyle.getEndItemEndPadding());
            setViewSideMargins(this.mBottomDivider, rowStyle.getBottomDividerStartPadding(), rowStyle.getBottomDividerEndPadding());
            setViewHeight(this.mActionDivider, rowStyle.getActionDividerHeight());
        }
    }

    private void setViewSidePaddings(View view, int i, int i2) {
        boolean z = i < 0 && i2 < 0;
        if (view != null && !z) {
            if (i < 0) {
                i = view.getPaddingStart();
            }
            int paddingTop = view.getPaddingTop();
            if (i2 < 0) {
                i2 = view.getPaddingEnd();
            }
            view.setPaddingRelative(i, paddingTop, i2, view.getPaddingBottom());
        }
    }

    private void setViewSideMargins(View view, int i, int i2) {
        boolean z = i < 0 && i2 < 0;
        if (view != null && !z) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (i >= 0) {
                marginLayoutParams.setMarginStart(i);
            }
            if (i2 >= 0) {
                marginLayoutParams.setMarginEnd(i2);
            }
            view.setLayoutParams(marginLayoutParams);
        }
    }

    private void setViewHeight(View view, int i) {
        if (view != null && i >= 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = i;
            view.setLayoutParams(layoutParams);
        }
    }

    private void setViewWidth(View view, int i) {
        if (view != null && i >= 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = i;
            view.setLayoutParams(layoutParams);
        }
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setInsets(int i, int i2, int i3, int i4) {
        super.setInsets(i, i2, i3, i4);
        setPadding(i, i2, i3, i4);
    }

    private int getRowContentHeight() {
        int height = this.mRowContent.getHeight(this.mSliceStyle, this.mViewPolicy);
        if (this.mRangeBar != null && this.mStartItem == null) {
            height -= this.mSliceStyle.getRowRangeHeight();
        }
        return this.mSelectionSpinner != null ? height - this.mSliceStyle.getRowSelectionHeight() : height;
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setTint(int i) {
        super.setTint(i);
        if (this.mRowContent != null) {
            populateViews(true);
        }
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setSliceActions(List<SliceAction> list) {
        this.mHeaderActions = list;
        if (this.mRowContent != null) {
            updateEndItems();
        }
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setShowLastUpdated(boolean z) {
        super.setShowLastUpdated(z);
        if (this.mRowContent != null) {
            populateViews(true);
        }
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setAllowTwoLines(boolean z) {
        this.mAllowTwoLines = z;
        if (this.mRowContent != null) {
            populateViews(true);
        }
    }

    private void measureChildWithExactHeight(View view, int i, int i2) {
        measureChild(view, i, View.MeasureSpec.makeMeasureSpec(i2 + this.mInsetTop + this.mInsetBottom, 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int rowContentHeight = getRowContentHeight();
        if (rowContentHeight != 0) {
            this.mRootView.setVisibility(0);
            measureChildWithExactHeight(this.mRootView, i, rowContentHeight);
            i3 = this.mRootView.getMeasuredWidth();
        } else {
            this.mRootView.setVisibility(8);
            i3 = 0;
        }
        ProgressBar progressBar = this.mRangeBar;
        if (progressBar == null || this.mStartItem != null) {
            Spinner spinner = this.mSelectionSpinner;
            if (spinner != null) {
                measureChildWithExactHeight(spinner, i, this.mSliceStyle.getRowSelectionHeight());
                i3 = Math.max(i3, this.mSelectionSpinner.getMeasuredWidth());
            }
        } else {
            if (sCanSpecifyLargerRangeBarHeight) {
                measureChildWithExactHeight(progressBar, i, this.mSliceStyle.getRowRangeHeight());
            } else {
                measureChild(progressBar, i, View.MeasureSpec.makeMeasureSpec(0, 0));
            }
            this.mMeasuredRangeHeight = this.mRangeBar.getMeasuredHeight();
            i3 = Math.max(i3, this.mRangeBar.getMeasuredWidth());
        }
        int max = Math.max(i3 + this.mInsetStart + this.mInsetEnd, getSuggestedMinimumWidth());
        RowContent rowContent = this.mRowContent;
        setMeasuredDimension(FrameLayout.resolveSizeAndState(max, i, 0), (rowContent != null ? rowContent.getHeight(this.mSliceStyle, this.mViewPolicy) : 0) + this.mInsetTop + this.mInsetBottom);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int paddingLeft = getPaddingLeft();
        LinearLayout linearLayout = this.mRootView;
        linearLayout.layout(paddingLeft, this.mInsetTop, linearLayout.getMeasuredWidth() + paddingLeft, getRowContentHeight() + this.mInsetTop);
        if (this.mRangeBar != null && this.mStartItem == null) {
            int rowContentHeight = getRowContentHeight() + ((this.mSliceStyle.getRowRangeHeight() - this.mMeasuredRangeHeight) / 2) + this.mInsetTop;
            ProgressBar progressBar = this.mRangeBar;
            progressBar.layout(paddingLeft, rowContentHeight, progressBar.getMeasuredWidth() + paddingLeft, this.mMeasuredRangeHeight + rowContentHeight);
        } else if (this.mSelectionSpinner != null) {
            int rowContentHeight2 = getRowContentHeight() + this.mInsetTop;
            Spinner spinner = this.mSelectionSpinner;
            spinner.layout(paddingLeft, rowContentHeight2, spinner.getMeasuredWidth() + paddingLeft, this.mSelectionSpinner.getMeasuredHeight() + rowContentHeight2);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0053, code lost:
        if (r2 != false) goto L_0x0057;
     */
    @Override // androidx.slice.widget.SliceChildView
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSliceItem(androidx.slice.widget.SliceContent r5, boolean r6, int r7, int r8, androidx.slice.widget.SliceView.OnSliceActionListener r9) {
        /*
        // Method dump skipped, instructions count: 101
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowView.setSliceItem(androidx.slice.widget.SliceContent, boolean, int, int, androidx.slice.widget.SliceView$OnSliceActionListener):void");
    }

    private void populateViews(boolean z) {
        int i;
        boolean z2 = z && this.mIsRangeSliding;
        if (!z2) {
            resetViewState();
        }
        if (this.mRowContent.getLayoutDir() != -1) {
            setLayoutDirection(this.mRowContent.getLayoutDir());
        }
        if (this.mRowContent.isDefaultSeeMore()) {
            showSeeMore();
            return;
        }
        CharSequence contentDescription = this.mRowContent.getContentDescription();
        if (contentDescription != null) {
            this.mContent.setContentDescription(contentDescription);
        }
        SliceItem startItem = this.mRowContent.getStartItem();
        this.mStartItem = startItem;
        boolean z3 = startItem != null && (this.mRowIndex > 0 || this.mRowContent.hasTitleItems());
        if (z3) {
            z3 = addItem(this.mStartItem, this.mTintColor, true);
        }
        int i2 = 8;
        this.mStartContainer.setVisibility(z3 ? 0 : 8);
        SliceItem titleItem = this.mRowContent.getTitleItem();
        if (titleItem != null) {
            this.mPrimaryText.setText(titleItem.getSanitizedText());
        }
        SliceStyle sliceStyle = this.mSliceStyle;
        if (sliceStyle != null) {
            TextView textView = this.mPrimaryText;
            if (this.mIsHeader) {
                i = sliceStyle.getHeaderTitleSize();
            } else {
                i = sliceStyle.getTitleSize();
            }
            textView.setTextSize(0, (float) i);
            this.mPrimaryText.setTextColor(this.mSliceStyle.getTitleColor());
        }
        this.mPrimaryText.setVisibility(titleItem != null ? 0 : 8);
        addSubtitle(titleItem != null);
        View view = this.mBottomDivider;
        if (this.mRowContent.hasBottomDivider()) {
            i2 = 0;
        }
        view.setVisibility(i2);
        SliceItem primaryAction = this.mRowContent.getPrimaryAction();
        if (!(primaryAction == null || primaryAction == this.mStartItem)) {
            SliceActionImpl sliceActionImpl = new SliceActionImpl(primaryAction);
            this.mRowAction = sliceActionImpl;
            if (sliceActionImpl.isToggle()) {
                addAction(this.mRowAction, this.mTintColor, this.mEndContainer, false);
                setViewClickable(this.mRootView, true);
                return;
            }
        }
        SliceItem range = this.mRowContent.getRange();
        if (range != null) {
            if (this.mRowAction != null) {
                setViewClickable(this.mRootView, true);
            }
            this.mRangeItem = range;
            if (!z2) {
                setRangeBounds();
                addRange();
            }
            if (this.mStartItem == null) {
                return;
            }
        }
        SliceItem selection = this.mRowContent.getSelection();
        if (selection != null) {
            this.mSelectionItem = selection;
            addSelection(selection);
            return;
        }
        updateEndItems();
        updateActionSpinner();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX DEBUG: Type inference failed for r1v0. Raw type applied. Possible types: java.util.List<androidx.slice.core.SliceAction> */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0146  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateEndItems() {
        /*
        // Method dump skipped, instructions count: 331
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowView.updateEndItems():void");
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setLastUpdated(long j) {
        super.setLastUpdated(j);
        RowContent rowContent = this.mRowContent;
        if (rowContent != null) {
            addSubtitle(rowContent.getTitleItem() != null && TextUtils.isEmpty(this.mRowContent.getTitleItem().getSanitizedText()));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x012a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addSubtitle(boolean r10) {
        /*
        // Method dump skipped, instructions count: 318
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowView.addSubtitle(boolean):void");
    }

    private CharSequence getRelativeTimeString(long j) {
        long currentTimeMillis = System.currentTimeMillis() - j;
        if (currentTimeMillis > 31449600000L) {
            int i = (int) (currentTimeMillis / 31449600000L);
            return getResources().getQuantityString(R$plurals.abc_slice_duration_years, i, Integer.valueOf(i));
        } else if (currentTimeMillis > 86400000) {
            int i2 = (int) (currentTimeMillis / 86400000);
            return getResources().getQuantityString(R$plurals.abc_slice_duration_days, i2, Integer.valueOf(i2));
        } else if (currentTimeMillis <= 60000) {
            return null;
        } else {
            int i3 = (int) (currentTimeMillis / 60000);
            return getResources().getQuantityString(R$plurals.abc_slice_duration_min, i3, Integer.valueOf(i3));
        }
    }

    private void setRangeBounds() {
        SliceItem findSubtype = SliceQuery.findSubtype(this.mRangeItem, "int", "min");
        int i = 0;
        int i2 = findSubtype != null ? findSubtype.getInt() : 0;
        this.mRangeMinValue = i2;
        SliceItem findSubtype2 = SliceQuery.findSubtype(this.mRangeItem, "int", "max");
        int i3 = 100;
        if (findSubtype2 != null) {
            i3 = findSubtype2.getInt();
        }
        this.mRangeMaxValue = i3;
        SliceItem findSubtype3 = SliceQuery.findSubtype(this.mRangeItem, "int", "value");
        if (findSubtype3 != null) {
            i = findSubtype3.getInt() - i2;
        }
        this.mRangeValue = i;
    }

    private void addRange() {
        ProgressBar progressBar;
        Drawable drawable;
        Drawable loadDrawable;
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        SliceItem findSubtype = SliceQuery.findSubtype(this.mRangeItem, "int", "range_mode");
        boolean z = findSubtype != null && findSubtype.getInt() == 1;
        boolean equals = "action".equals(this.mRangeItem.getFormat());
        boolean z2 = this.mStartItem == null;
        if (!equals) {
            if (z2) {
                progressBar = new ProgressBar(getContext(), null, 16842872);
            } else {
                progressBar = (ProgressBar) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_progress_inline_view, (ViewGroup) this, false);
                SliceStyle sliceStyle = this.mSliceStyle;
                if (!(sliceStyle == null || sliceStyle.getRowStyle() == null)) {
                    setViewWidth(progressBar, this.mSliceStyle.getRowStyle().getProgressBarInlineWidth());
                    setViewSidePaddings(progressBar, this.mSliceStyle.getRowStyle().getProgressBarStartPadding(), this.mSliceStyle.getRowStyle().getProgressBarEndPadding());
                }
            }
            if (z) {
                progressBar.setIndeterminate(true);
            }
        } else if (z2) {
            progressBar = new SeekBar(getContext());
        } else {
            progressBar = (SeekBar) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_seekbar_view, (ViewGroup) this, false);
            SliceStyle sliceStyle2 = this.mSliceStyle;
            if (!(sliceStyle2 == null || sliceStyle2.getRowStyle() == null)) {
                setViewWidth(progressBar, this.mSliceStyle.getRowStyle().getSeekBarInlineWidth());
            }
        }
        if (z) {
            drawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
        } else {
            drawable = DrawableCompat.wrap(progressBar.getProgressDrawable());
        }
        int i = this.mTintColor;
        if (!(i == -1 || drawable == null)) {
            DrawableCompat.setTint(drawable, i);
            if (z) {
                progressBar.setIndeterminateDrawable(drawable);
            } else {
                progressBar.setProgressDrawable(drawable);
            }
        }
        progressBar.setMax(this.mRangeMaxValue - this.mRangeMinValue);
        progressBar.setProgress(this.mRangeValue);
        progressBar.setVisibility(0);
        if (this.mStartItem == null) {
            addView(progressBar, new FrameLayout.LayoutParams(-1, -2));
        } else {
            this.mSubContent.setVisibility(8);
            this.mContent.addView(progressBar, 1);
        }
        this.mRangeBar = progressBar;
        if (equals) {
            SliceItem inputRangeThumb = this.mRowContent.getInputRangeThumb();
            SeekBar seekBar = (SeekBar) this.mRangeBar;
            if (!(inputRangeThumb == null || inputRangeThumb.getIcon() == null || (loadDrawable = inputRangeThumb.getIcon().loadDrawable(getContext())) == null)) {
                seekBar.setThumb(loadDrawable);
            }
            Drawable wrap = DrawableCompat.wrap(seekBar.getThumb());
            int i2 = this.mTintColor;
            if (!(i2 == -1 || wrap == null)) {
                DrawableCompat.setTint(wrap, i2);
                seekBar.setThumb(wrap);
            }
            seekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
        }
    }

    /* access modifiers changed from: package-private */
    public void sendSliderValue() {
        if (this.mRangeItem != null) {
            try {
                this.mLastSentRangeUpdate = System.currentTimeMillis();
                this.mRangeItem.fireAction(getContext(), new Intent().addFlags(268435456).putExtra("android.app.slice.extra.RANGE_VALUE", this.mRangeValue));
                if (this.mObserver != null) {
                    EventInfo eventInfo = new EventInfo(getMode(), 2, 4, this.mRowIndex);
                    eventInfo.state = this.mRangeValue;
                    this.mObserver.onSliceAction(eventInfo, this.mRangeItem);
                }
            } catch (PendingIntent.CanceledException e) {
                Log.e("RowView", "PendingIntent for slice cannot be sent", e);
            }
        }
    }

    private void addSelection(SliceItem sliceItem) {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        this.mSelectionOptionKeys = new ArrayList<>();
        this.mSelectionOptionValues = new ArrayList<>();
        List<SliceItem> items = sliceItem.getSlice().getItems();
        for (int i = 0; i < items.size(); i++) {
            SliceItem sliceItem2 = items.get(i);
            if (sliceItem2.hasHint("selection_option")) {
                SliceItem findSubtype = SliceQuery.findSubtype(sliceItem2, "text", "selection_option_key");
                SliceItem findSubtype2 = SliceQuery.findSubtype(sliceItem2, "text", "selection_option_value");
                if (!(findSubtype == null || findSubtype2 == null)) {
                    this.mSelectionOptionKeys.add(findSubtype.getText().toString());
                    this.mSelectionOptionValues.add(findSubtype2.getSanitizedText());
                }
            }
        }
        this.mSelectionSpinner = (Spinner) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_row_selection, (ViewGroup) this, false);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R$layout.abc_slice_row_selection_text, this.mSelectionOptionValues);
        arrayAdapter.setDropDownViewResource(R$layout.abc_slice_row_selection_dropdown_text);
        this.mSelectionSpinner.setAdapter((SpinnerAdapter) arrayAdapter);
        addView(this.mSelectionSpinner);
        this.mSelectionSpinner.setOnItemSelectedListener(this);
    }

    private void addAction(SliceActionImpl sliceActionImpl, int i, ViewGroup viewGroup, boolean z) {
        SliceActionView sliceActionView = new SliceActionView(getContext());
        viewGroup.addView(sliceActionView);
        if (viewGroup.getVisibility() == 8) {
            viewGroup.setVisibility(0);
        }
        boolean isToggle = sliceActionImpl.isToggle();
        EventInfo eventInfo = new EventInfo(getMode(), !isToggle, isToggle != 0 ? 3 : 0, this.mRowIndex);
        if (z) {
            eventInfo.setPosition(0, 0, 1);
        }
        sliceActionView.setAction(sliceActionImpl, eventInfo, this.mObserver, i, this.mLoadingListener);
        if (this.mLoadingActions.contains(sliceActionImpl.getSliceItem())) {
            sliceActionView.setLoading(true);
        }
        if (isToggle != 0) {
            this.mToggles.put(sliceActionImpl, sliceActionView);
        } else {
            this.mActions.put(sliceActionImpl, sliceActionView);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v0, resolved type: android.widget.ImageView */
    /* JADX WARN: Multi-variable type inference failed */
    private boolean addItem(SliceItem sliceItem, int i, boolean z) {
        IconCompat iconCompat;
        SliceItem sliceItem2;
        int i2;
        ViewGroup viewGroup = z ? this.mStartContainer : this.mEndContainer;
        if ("slice".equals(sliceItem.getFormat()) || "action".equals(sliceItem.getFormat())) {
            if (sliceItem.hasHint("shortcut")) {
                addAction(new SliceActionImpl(sliceItem), i, viewGroup, z);
                return true;
            } else if (sliceItem.getSlice().getItems().size() == 0) {
                return false;
            } else {
                sliceItem = sliceItem.getSlice().getItems().get(0);
            }
        }
        TextView textView = null;
        if ("image".equals(sliceItem.getFormat())) {
            iconCompat = sliceItem.getIcon();
            sliceItem2 = null;
        } else if ("long".equals(sliceItem.getFormat())) {
            sliceItem2 = sliceItem;
            iconCompat = null;
        } else {
            iconCompat = null;
            sliceItem2 = null;
        }
        if (iconCompat != null) {
            boolean z2 = !sliceItem.hasHint("no_tint");
            boolean hasHint = sliceItem.hasHint("raw");
            float f = getResources().getDisplayMetrics().density;
            ImageView imageView = new ImageView(getContext());
            Drawable loadDrawable = iconCompat.loadDrawable(getContext());
            imageView.setImageDrawable(loadDrawable);
            if (z2 && i != -1) {
                imageView.setColorFilter(i);
            }
            if (this.mIsRangeSliding) {
                viewGroup.removeAllViews();
                viewGroup.addView(imageView);
            } else {
                viewGroup.addView(imageView);
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.width = hasHint ? Math.round(((float) loadDrawable.getIntrinsicWidth()) / f) : this.mImageSize;
            if (hasHint) {
                i2 = Math.round(((float) loadDrawable.getIntrinsicHeight()) / f);
            } else {
                i2 = this.mImageSize;
            }
            layoutParams.height = i2;
            imageView.setLayoutParams(layoutParams);
            SliceStyle sliceStyle = this.mSliceStyle;
            if (!(sliceStyle == null || sliceStyle.getRowStyle() == null)) {
                int iconSize = this.mSliceStyle.getRowStyle().getIconSize();
                if (iconSize <= 0) {
                    iconSize = this.mIconSize;
                }
                this.mIconSize = iconSize;
            }
            int i3 = z2 ? this.mIconSize / 2 : 0;
            imageView.setPadding(i3, i3, i3, i3);
            textView = imageView;
        } else if (sliceItem2 != null) {
            textView = new TextView(getContext());
            textView.setText(SliceViewUtil.getTimestampString(getContext(), sliceItem.getLong()));
            SliceStyle sliceStyle2 = this.mSliceStyle;
            if (sliceStyle2 != null) {
                textView.setTextSize(0, (float) sliceStyle2.getSubtitleSize());
                textView.setTextColor(this.mSliceStyle.getSubtitleColor());
            }
            viewGroup.addView(textView);
        }
        return textView != null;
    }

    private void showSeeMore() {
        final Button button = (Button) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_row_show_more, (ViewGroup) this, false);
        button.setOnClickListener(new View.OnClickListener() {
            /* class androidx.slice.widget.RowView.AnonymousClass1 */

            public void onClick(View view) {
                try {
                    if (RowView.this.mObserver != null) {
                        RowView.this.mObserver.onSliceAction(new EventInfo(RowView.this.getMode(), 4, 0, RowView.this.mRowIndex), RowView.this.mRowContent.getSliceItem());
                    }
                    RowView.this.mShowActionSpinner = RowView.this.mRowContent.getSliceItem().fireActionInternal(RowView.this.getContext(), null);
                    if (RowView.this.mShowActionSpinner) {
                        if (RowView.this.mLoadingListener != null) {
                            RowView.this.mLoadingListener.onSliceActionLoading(RowView.this.mRowContent.getSliceItem(), RowView.this.mRowIndex);
                        }
                        RowView.this.mLoadingActions.add(RowView.this.mRowContent.getSliceItem());
                        button.setVisibility(8);
                    }
                    RowView.this.updateActionSpinner();
                } catch (PendingIntent.CanceledException e) {
                    Log.e("RowView", "PendingIntent for slice cannot be sent", e);
                }
            }
        });
        int i = this.mTintColor;
        if (i != -1) {
            button.setTextColor(i);
        }
        this.mSeeMoreView = button;
        this.mRootView.addView(button);
        if (this.mLoadingActions.contains(this.mRowContent.getSliceItem())) {
            this.mShowActionSpinner = true;
            button.setVisibility(8);
            updateActionSpinner();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateActionSpinner() {
        this.mActionSpinner.setVisibility(this.mShowActionSpinner ? 0 : 8);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setLoadingActions(Set<SliceItem> set) {
        if (set == null) {
            this.mLoadingActions.clear();
            this.mShowActionSpinner = false;
        } else {
            this.mLoadingActions = set;
        }
        updateEndItems();
        updateActionSpinner();
    }

    public void onClick(View view) {
        SliceActionView sliceActionView;
        SliceActionImpl sliceActionImpl = this.mRowAction;
        if (sliceActionImpl != null && sliceActionImpl.getActionItem() != null) {
            if (this.mRowAction.isToggle()) {
                sliceActionView = this.mToggles.get(this.mRowAction);
            } else {
                sliceActionView = this.mActions.get(this.mRowAction);
            }
            if (sliceActionView != null && !(view instanceof SliceActionView)) {
                sliceActionView.sendAction();
            } else if (this.mRowIndex == 0) {
                performClick();
            } else {
                try {
                    this.mShowActionSpinner = this.mRowAction.getActionItem().fireActionInternal(getContext(), null);
                    if (this.mObserver != null) {
                        this.mObserver.onSliceAction(new EventInfo(getMode(), 3, 0, this.mRowIndex), this.mRowAction.getSliceItem());
                    }
                    if (this.mShowActionSpinner && this.mLoadingListener != null) {
                        this.mLoadingListener.onSliceActionLoading(this.mRowAction.getSliceItem(), this.mRowIndex);
                        this.mLoadingActions.add(this.mRowAction.getSliceItem());
                    }
                    updateActionSpinner();
                } catch (PendingIntent.CanceledException e) {
                    Log.e("RowView", "PendingIntent for slice cannot be sent", e);
                }
            }
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.mSelectionItem != null && adapterView == this.mSelectionSpinner && i >= 0 && i < this.mSelectionOptionKeys.size()) {
            if (this.mObserver != null) {
                this.mObserver.onSliceAction(new EventInfo(getMode(), 5, 6, this.mRowIndex), this.mSelectionItem);
            }
            try {
                if (this.mSelectionItem.fireActionInternal(getContext(), new Intent().addFlags(268435456).putExtra("android.app.slice.extra.SELECTION", this.mSelectionOptionKeys.get(i)))) {
                    this.mShowActionSpinner = true;
                    if (this.mLoadingListener != null) {
                        this.mLoadingListener.onSliceActionLoading(this.mRowAction.getSliceItem(), this.mRowIndex);
                        this.mLoadingActions.add(this.mRowAction.getSliceItem());
                    }
                    updateActionSpinner();
                }
            } catch (PendingIntent.CanceledException e) {
                Log.e("RowView", "PendingIntent for slice cannot be sent", e);
            }
        }
    }

    private void setViewClickable(View view, boolean z) {
        Drawable drawable = null;
        view.setOnClickListener(z ? this : null);
        if (z) {
            drawable = SliceViewUtil.getDrawable(getContext(), 16843534);
        }
        view.setBackground(drawable);
        view.setClickable(z);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void resetView() {
        this.mRowContent = null;
        this.mLoadingActions.clear();
        resetViewState();
    }

    private void resetViewState() {
        this.mRootView.setVisibility(0);
        setLayoutDirection(2);
        setViewClickable(this.mRootView, false);
        setViewClickable(this.mContent, false);
        this.mStartContainer.removeAllViews();
        this.mEndContainer.removeAllViews();
        this.mEndContainer.setVisibility(8);
        this.mPrimaryText.setText((CharSequence) null);
        this.mSecondaryText.setText((CharSequence) null);
        this.mLastUpdatedText.setText((CharSequence) null);
        this.mLastUpdatedText.setVisibility(8);
        this.mToggles.clear();
        this.mActions.clear();
        this.mRowAction = null;
        this.mBottomDivider.setVisibility(8);
        this.mActionDivider.setVisibility(8);
        View view = this.mSeeMoreView;
        if (view != null) {
            this.mRootView.removeView(view);
            this.mSeeMoreView = null;
        }
        this.mIsRangeSliding = false;
        this.mRangeHasPendingUpdate = false;
        this.mRangeItem = null;
        this.mRangeMinValue = 0;
        this.mRangeMaxValue = 0;
        this.mRangeValue = 0;
        this.mLastSentRangeUpdate = 0;
        this.mHandler = null;
        ProgressBar progressBar = this.mRangeBar;
        if (progressBar != null) {
            if (this.mStartItem == null) {
                removeView(progressBar);
            } else {
                this.mContent.removeView(progressBar);
            }
            this.mRangeBar = null;
        }
        this.mSubContent.setVisibility(0);
        this.mStartItem = null;
        this.mActionSpinner.setVisibility(8);
        Spinner spinner = this.mSelectionSpinner;
        if (spinner != null) {
            removeView(spinner);
            this.mSelectionSpinner = null;
        }
        this.mSelectionItem = null;
    }
}
