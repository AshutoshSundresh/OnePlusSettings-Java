package androidx.leanback.widget.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$attr;
import androidx.leanback.R$dimen;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.R$styleable;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Picker extends FrameLayout {
    private int mAlphaAnimDuration;
    private final OnChildViewHolderSelectedListener mColumnChangeListener;
    final List<VerticalGridView> mColumnViews;
    ArrayList<PickerColumn> mColumns;
    private Interpolator mDecelerateInterpolator;
    private float mFocusedAlpha;
    private float mInvisibleColumnAlpha;
    private ArrayList<PickerValueListener> mListeners;
    private int mPickerItemLayoutId;
    private int mPickerItemTextViewId;
    private ViewGroup mPickerView;
    private int mSelectedColumn;
    private List<CharSequence> mSeparators;
    private float mUnfocusedAlpha;
    private float mVisibleColumnAlpha;
    private float mVisibleItems;
    private float mVisibleItemsActivated;

    public interface PickerValueListener {
        void onValueChanged(Picker picker, int i);
    }

    public float getVisibleItemCount() {
        return 1.0f;
    }

    @Deprecated
    public final CharSequence getSeparator() {
        return this.mSeparators.get(0);
    }

    public final void setSeparator(CharSequence charSequence) {
        setSeparators(Arrays.asList(charSequence));
    }

    public final List<CharSequence> getSeparators() {
        return this.mSeparators;
    }

    public final void setSeparators(List<CharSequence> list) {
        this.mSeparators.clear();
        this.mSeparators.addAll(list);
    }

    public final int getPickerItemLayoutId() {
        return this.mPickerItemLayoutId;
    }

    public final void setPickerItemLayoutId(int i) {
        this.mPickerItemLayoutId = i;
    }

    public final int getPickerItemTextViewId() {
        return this.mPickerItemTextViewId;
    }

    public final void setPickerItemTextViewId(int i) {
        this.mPickerItemTextViewId = i;
    }

    public Picker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.pickerStyle);
    }

    public Picker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mColumnViews = new ArrayList();
        this.mVisibleItemsActivated = 3.0f;
        this.mVisibleItems = 1.0f;
        this.mSelectedColumn = 0;
        this.mSeparators = new ArrayList();
        this.mColumnChangeListener = new OnChildViewHolderSelectedListener() {
            /* class androidx.leanback.widget.picker.Picker.AnonymousClass1 */

            @Override // androidx.leanback.widget.OnChildViewHolderSelectedListener
            public void onChildViewHolderSelected(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int i, int i2) {
                int indexOf = Picker.this.mColumnViews.indexOf((VerticalGridView) recyclerView);
                Picker.this.updateColumnAlpha(indexOf, true);
                if (viewHolder != null) {
                    Picker.this.onColumnValueChanged(indexOf, Picker.this.mColumns.get(indexOf).getMinValue() + i);
                }
            }
        };
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.lbPicker, i, 0);
        ViewCompat.saveAttributeDataForStyleable(this, context, R$styleable.lbPicker, attributeSet, obtainStyledAttributes, i, 0);
        this.mPickerItemLayoutId = obtainStyledAttributes.getResourceId(R$styleable.lbPicker_pickerItemLayout, R$layout.lb_picker_item);
        this.mPickerItemTextViewId = obtainStyledAttributes.getResourceId(R$styleable.lbPicker_pickerItemTextViewId, 0);
        obtainStyledAttributes.recycle();
        setEnabled(true);
        setDescendantFocusability(262144);
        this.mFocusedAlpha = 1.0f;
        this.mUnfocusedAlpha = 1.0f;
        this.mVisibleColumnAlpha = 0.5f;
        this.mInvisibleColumnAlpha = 0.0f;
        this.mAlphaAnimDuration = 200;
        this.mDecelerateInterpolator = new DecelerateInterpolator(2.5f);
        this.mPickerView = (ViewGroup) ((ViewGroup) LayoutInflater.from(getContext()).inflate(R$layout.lb_picker, (ViewGroup) this, true)).findViewById(R$id.picker);
    }

    public PickerColumn getColumnAt(int i) {
        ArrayList<PickerColumn> arrayList = this.mColumns;
        if (arrayList == null) {
            return null;
        }
        return arrayList.get(i);
    }

    public int getColumnsCount() {
        ArrayList<PickerColumn> arrayList = this.mColumns;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    public void setColumns(List<PickerColumn> list) {
        if (this.mSeparators.size() != 0) {
            if (this.mSeparators.size() == 1) {
                CharSequence charSequence = this.mSeparators.get(0);
                this.mSeparators.clear();
                this.mSeparators.add("");
                for (int i = 0; i < list.size() - 1; i++) {
                    this.mSeparators.add(charSequence);
                }
                this.mSeparators.add("");
            } else if (this.mSeparators.size() != list.size() + 1) {
                throw new IllegalStateException("Separators size: " + this.mSeparators.size() + " mustequal the size of columns: " + list.size() + " + 1");
            }
            this.mColumnViews.clear();
            this.mPickerView.removeAllViews();
            ArrayList<PickerColumn> arrayList = new ArrayList<>(list);
            this.mColumns = arrayList;
            if (this.mSelectedColumn > arrayList.size() - 1) {
                this.mSelectedColumn = this.mColumns.size() - 1;
            }
            LayoutInflater from = LayoutInflater.from(getContext());
            int columnsCount = getColumnsCount();
            if (!TextUtils.isEmpty(this.mSeparators.get(0))) {
                TextView textView = (TextView) from.inflate(R$layout.lb_picker_separator, this.mPickerView, false);
                textView.setText(this.mSeparators.get(0));
                this.mPickerView.addView(textView);
            }
            int i2 = 0;
            while (i2 < columnsCount) {
                VerticalGridView verticalGridView = (VerticalGridView) from.inflate(R$layout.lb_picker_column, this.mPickerView, false);
                updateColumnSize(verticalGridView);
                verticalGridView.setWindowAlignment(0);
                verticalGridView.setHasFixedSize(false);
                verticalGridView.setFocusable(isActivated());
                verticalGridView.setItemViewCacheSize(0);
                this.mColumnViews.add(verticalGridView);
                this.mPickerView.addView(verticalGridView);
                int i3 = i2 + 1;
                if (!TextUtils.isEmpty(this.mSeparators.get(i3))) {
                    TextView textView2 = (TextView) from.inflate(R$layout.lb_picker_separator, this.mPickerView, false);
                    textView2.setText(this.mSeparators.get(i3));
                    this.mPickerView.addView(textView2);
                }
                verticalGridView.setAdapter(new PickerScrollArrayAdapter(getPickerItemLayoutId(), getPickerItemTextViewId(), i2));
                verticalGridView.setOnChildViewHolderSelectedListener(this.mColumnChangeListener);
                i2 = i3;
            }
            return;
        }
        throw new IllegalStateException("Separators size is: " + this.mSeparators.size() + ". At least one separator must be provided");
    }

    public void setColumnAt(int i, PickerColumn pickerColumn) {
        this.mColumns.set(i, pickerColumn);
        VerticalGridView verticalGridView = this.mColumnViews.get(i);
        PickerScrollArrayAdapter pickerScrollArrayAdapter = (PickerScrollArrayAdapter) verticalGridView.getAdapter();
        if (pickerScrollArrayAdapter != null) {
            pickerScrollArrayAdapter.notifyDataSetChanged();
        }
        verticalGridView.setSelectedPosition(pickerColumn.getCurrentValue() - pickerColumn.getMinValue());
    }

    public void setColumnValue(int i, int i2, boolean z) {
        PickerColumn pickerColumn = this.mColumns.get(i);
        if (pickerColumn.getCurrentValue() != i2) {
            pickerColumn.setCurrentValue(i2);
            notifyValueChanged(i);
            VerticalGridView verticalGridView = this.mColumnViews.get(i);
            if (verticalGridView != null) {
                int minValue = i2 - this.mColumns.get(i).getMinValue();
                if (z) {
                    verticalGridView.setSelectedPositionSmooth(minValue);
                } else {
                    verticalGridView.setSelectedPosition(minValue);
                }
            }
        }
    }

    private void notifyValueChanged(int i) {
        ArrayList<PickerValueListener> arrayList = this.mListeners;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                this.mListeners.get(size).onValueChanged(this, i);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateColumnAlpha(int i, boolean z) {
        VerticalGridView verticalGridView = this.mColumnViews.get(i);
        int selectedPosition = verticalGridView.getSelectedPosition();
        int i2 = 0;
        while (i2 < verticalGridView.getAdapter().getItemCount()) {
            View findViewByPosition = verticalGridView.getLayoutManager().findViewByPosition(i2);
            if (findViewByPosition != null) {
                setOrAnimateAlpha(findViewByPosition, selectedPosition == i2, i, z);
            }
            i2++;
        }
    }

    /* access modifiers changed from: package-private */
    public void setOrAnimateAlpha(View view, boolean z, int i, boolean z2) {
        boolean z3 = i == this.mSelectedColumn || !hasFocus();
        if (z) {
            if (z3) {
                setOrAnimateAlpha(view, z2, this.mFocusedAlpha, -1.0f, this.mDecelerateInterpolator);
            } else {
                setOrAnimateAlpha(view, z2, this.mUnfocusedAlpha, -1.0f, this.mDecelerateInterpolator);
            }
        } else if (z3) {
            setOrAnimateAlpha(view, z2, this.mVisibleColumnAlpha, -1.0f, this.mDecelerateInterpolator);
        } else {
            setOrAnimateAlpha(view, z2, this.mInvisibleColumnAlpha, -1.0f, this.mDecelerateInterpolator);
        }
    }

    private void setOrAnimateAlpha(View view, boolean z, float f, float f2, Interpolator interpolator) {
        view.animate().cancel();
        if (!z) {
            view.setAlpha(f);
            return;
        }
        if (f2 >= 0.0f) {
            view.setAlpha(f2);
        }
        view.animate().alpha(f).setDuration((long) this.mAlphaAnimDuration).setInterpolator(interpolator).start();
    }

    public void onColumnValueChanged(int i, int i2) {
        PickerColumn pickerColumn = this.mColumns.get(i);
        if (pickerColumn.getCurrentValue() != i2) {
            pickerColumn.setCurrentValue(i2);
            notifyValueChanged(i);
        }
    }

    /* access modifiers changed from: package-private */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

        ViewHolder(View view, TextView textView2) {
            super(view);
            this.textView = textView2;
        }
    }

    /* access modifiers changed from: package-private */
    public class PickerScrollArrayAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mColIndex;
        private PickerColumn mData;
        private final int mResource;
        private final int mTextViewResourceId;

        PickerScrollArrayAdapter(int i, int i2, int i3) {
            this.mResource = i;
            this.mColIndex = i3;
            this.mTextViewResourceId = i2;
            this.mData = Picker.this.mColumns.get(i3);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextView textView;
            View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(this.mResource, viewGroup, false);
            int i2 = this.mTextViewResourceId;
            if (i2 != 0) {
                textView = (TextView) inflate.findViewById(i2);
            } else {
                textView = (TextView) inflate;
            }
            return new ViewHolder(inflate, textView);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            PickerColumn pickerColumn;
            TextView textView = viewHolder.textView;
            if (!(textView == null || (pickerColumn = this.mData) == null)) {
                textView.setText(pickerColumn.getLabelFor(pickerColumn.getMinValue() + i));
            }
            Picker picker = Picker.this;
            picker.setOrAnimateAlpha(viewHolder.itemView, picker.mColumnViews.get(this.mColIndex).getSelectedPosition() == i, this.mColIndex, false);
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            viewHolder.itemView.setFocusable(Picker.this.isActivated());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            PickerColumn pickerColumn = this.mData;
            if (pickerColumn == null) {
                return 0;
            }
            return pickerColumn.getCount();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (!isActivated()) {
            return super.dispatchKeyEvent(keyEvent);
        }
        int keyCode = keyEvent.getKeyCode();
        if (keyCode != 23 && keyCode != 66) {
            return super.dispatchKeyEvent(keyEvent);
        }
        if (keyEvent.getAction() == 1) {
            performClick();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int i, Rect rect) {
        int selectedColumn = getSelectedColumn();
        if (selectedColumn < 0 || selectedColumn >= this.mColumnViews.size()) {
            return false;
        }
        return this.mColumnViews.get(selectedColumn).requestFocus(i, rect);
    }

    /* access modifiers changed from: protected */
    public int getPickerItemHeightPixels() {
        return getContext().getResources().getDimensionPixelSize(R$dimen.picker_item_height);
    }

    private void updateColumnSize() {
        for (int i = 0; i < getColumnsCount(); i++) {
            updateColumnSize(this.mColumnViews.get(i));
        }
    }

    private void updateColumnSize(VerticalGridView verticalGridView) {
        ViewGroup.LayoutParams layoutParams = verticalGridView.getLayoutParams();
        float activatedVisibleItemCount = isActivated() ? getActivatedVisibleItemCount() : getVisibleItemCount();
        layoutParams.height = (int) ((((float) getPickerItemHeightPixels()) * activatedVisibleItemCount) + (((float) verticalGridView.getVerticalSpacing()) * (activatedVisibleItemCount - 1.0f)));
        verticalGridView.setLayoutParams(layoutParams);
    }

    private void updateItemFocusable() {
        boolean isActivated = isActivated();
        for (int i = 0; i < getColumnsCount(); i++) {
            VerticalGridView verticalGridView = this.mColumnViews.get(i);
            for (int i2 = 0; i2 < verticalGridView.getChildCount(); i2++) {
                verticalGridView.getChildAt(i2).setFocusable(isActivated);
            }
        }
    }

    public float getActivatedVisibleItemCount() {
        return this.mVisibleItemsActivated;
    }

    public void setActivatedVisibleItemCount(float f) {
        if (f <= 0.0f) {
            throw new IllegalArgumentException();
        } else if (this.mVisibleItemsActivated != f) {
            this.mVisibleItemsActivated = f;
            if (isActivated()) {
                updateColumnSize();
            }
        }
    }

    public void setVisibleItemCount(float f) {
        if (f <= 0.0f) {
            throw new IllegalArgumentException();
        } else if (this.mVisibleItems != f) {
            this.mVisibleItems = f;
            if (!isActivated()) {
                updateColumnSize();
            }
        }
    }

    public void setActivated(boolean z) {
        if (z == isActivated()) {
            super.setActivated(z);
            return;
        }
        super.setActivated(z);
        boolean hasFocus = hasFocus();
        int selectedColumn = getSelectedColumn();
        setDescendantFocusability(131072);
        if (!z && hasFocus && isFocusable()) {
            requestFocus();
        }
        for (int i = 0; i < getColumnsCount(); i++) {
            this.mColumnViews.get(i).setFocusable(z);
        }
        updateColumnSize();
        updateItemFocusable();
        if (z && hasFocus && selectedColumn >= 0) {
            this.mColumnViews.get(selectedColumn).requestFocus();
        }
        setDescendantFocusability(262144);
    }

    public void requestChildFocus(View view, View view2) {
        super.requestChildFocus(view, view2);
        for (int i = 0; i < this.mColumnViews.size(); i++) {
            if (this.mColumnViews.get(i).hasFocus()) {
                setSelectedColumn(i);
            }
        }
    }

    public void setSelectedColumn(int i) {
        if (this.mSelectedColumn != i) {
            this.mSelectedColumn = i;
            for (int i2 = 0; i2 < this.mColumnViews.size(); i2++) {
                updateColumnAlpha(i2, true);
            }
        }
        VerticalGridView verticalGridView = this.mColumnViews.get(i);
        if (hasFocus() && !verticalGridView.hasFocus()) {
            verticalGridView.requestFocus();
        }
    }

    public int getSelectedColumn() {
        return this.mSelectedColumn;
    }
}
