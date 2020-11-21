package androidx.leanback.app;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DividerPresenter;
import androidx.leanback.widget.DividerRow;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowHeaderPresenter;
import androidx.leanback.widget.SectionRow;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;

public class HeadersSupportFragment extends BaseRowSupportFragment {
    private static final PresenterSelector sHeaderPresenter;
    static View.OnLayoutChangeListener sLayoutChangeListener = new View.OnLayoutChangeListener() {
        /* class androidx.leanback.app.HeadersSupportFragment.AnonymousClass2 */

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            view.setPivotX(view.getLayoutDirection() == 1 ? (float) view.getWidth() : 0.0f);
            view.setPivotY((float) (view.getMeasuredHeight() / 2));
        }
    };
    private final ItemBridgeAdapter.AdapterListener mAdapterListener = new ItemBridgeAdapter.AdapterListener() {
        /* class androidx.leanback.app.HeadersSupportFragment.AnonymousClass1 */

        @Override // androidx.leanback.widget.ItemBridgeAdapter.AdapterListener
        public void onCreate(final ItemBridgeAdapter.ViewHolder viewHolder) {
            View view = viewHolder.getViewHolder().view;
            view.setOnClickListener(new View.OnClickListener() {
                /* class androidx.leanback.app.HeadersSupportFragment.AnonymousClass1.AnonymousClass1 */

                public void onClick(View view) {
                    OnHeaderClickedListener onHeaderClickedListener = HeadersSupportFragment.this.mOnHeaderClickedListener;
                    if (onHeaderClickedListener != null) {
                        onHeaderClickedListener.onHeaderClicked((RowHeaderPresenter.ViewHolder) viewHolder.getViewHolder(), (Row) viewHolder.getItem());
                    }
                }
            });
            if (HeadersSupportFragment.this.mWrapper != null) {
                viewHolder.itemView.addOnLayoutChangeListener(HeadersSupportFragment.sLayoutChangeListener);
            } else {
                view.addOnLayoutChangeListener(HeadersSupportFragment.sLayoutChangeListener);
            }
        }
    };
    private int mBackgroundColor;
    private boolean mBackgroundColorSet;
    private boolean mHeadersEnabled = true;
    private boolean mHeadersGone = false;
    OnHeaderClickedListener mOnHeaderClickedListener;
    private OnHeaderViewSelectedListener mOnHeaderViewSelectedListener;
    final ItemBridgeAdapter.Wrapper mWrapper = new ItemBridgeAdapter.Wrapper(this) {
        /* class androidx.leanback.app.HeadersSupportFragment.AnonymousClass3 */

        @Override // androidx.leanback.widget.ItemBridgeAdapter.Wrapper
        public void wrap(View view, View view2) {
            ((FrameLayout) view).addView(view2);
        }

        @Override // androidx.leanback.widget.ItemBridgeAdapter.Wrapper
        public View createWrapper(View view) {
            return new NoOverlappingFrameLayout(view.getContext());
        }
    };

    public interface OnHeaderClickedListener {
        void onHeaderClicked(RowHeaderPresenter.ViewHolder viewHolder, Row row);
    }

    public interface OnHeaderViewSelectedListener {
        void onHeaderSelected(RowHeaderPresenter.ViewHolder viewHolder, Row row);
    }

    static {
        ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
        classPresenterSelector.addClassPresenter(DividerRow.class, new DividerPresenter());
        classPresenterSelector.addClassPresenter(SectionRow.class, new RowHeaderPresenter(R$layout.lb_section_header, false));
        classPresenterSelector.addClassPresenter(Row.class, new RowHeaderPresenter(R$layout.lb_header));
        sHeaderPresenter = classPresenterSelector;
    }

    public HeadersSupportFragment() {
        setPresenterSelector(sHeaderPresenter);
        FocusHighlightHelper.setupHeaderItemFocusHighlight(getBridgeAdapter());
    }

    public void setOnHeaderClickedListener(OnHeaderClickedListener onHeaderClickedListener) {
        this.mOnHeaderClickedListener = onHeaderClickedListener;
    }

    public void setOnHeaderViewSelectedListener(OnHeaderViewSelectedListener onHeaderViewSelectedListener) {
        this.mOnHeaderViewSelectedListener = onHeaderViewSelectedListener;
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.leanback.app.BaseRowSupportFragment
    public VerticalGridView findGridViewFromRoot(View view) {
        return (VerticalGridView) view.findViewById(R$id.browse_headers);
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.leanback.app.BaseRowSupportFragment
    public void onRowSelected(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int i, int i2) {
        OnHeaderViewSelectedListener onHeaderViewSelectedListener = this.mOnHeaderViewSelectedListener;
        if (onHeaderViewSelectedListener == null) {
            return;
        }
        if (viewHolder == null || i < 0) {
            this.mOnHeaderViewSelectedListener.onHeaderSelected(null, null);
            return;
        }
        ItemBridgeAdapter.ViewHolder viewHolder2 = (ItemBridgeAdapter.ViewHolder) viewHolder;
        onHeaderViewSelectedListener.onHeaderSelected((RowHeaderPresenter.ViewHolder) viewHolder2.getViewHolder(), (Row) viewHolder2.getItem());
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.leanback.app.BaseRowSupportFragment
    public int getLayoutResourceId() {
        return R$layout.lb_headers_fragment;
    }

    @Override // androidx.fragment.app.Fragment, androidx.leanback.app.BaseRowSupportFragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        VerticalGridView verticalGridView = getVerticalGridView();
        if (verticalGridView != null) {
            if (this.mBackgroundColorSet) {
                verticalGridView.setBackgroundColor(this.mBackgroundColor);
                updateFadingEdgeToBrandColor(this.mBackgroundColor);
            } else {
                Drawable background = verticalGridView.getBackground();
                if (background instanceof ColorDrawable) {
                    updateFadingEdgeToBrandColor(((ColorDrawable) background).getColor());
                }
            }
            updateListViewVisibility();
        }
    }

    private void updateListViewVisibility() {
        VerticalGridView verticalGridView = getVerticalGridView();
        if (verticalGridView != null) {
            getView().setVisibility(this.mHeadersGone ? 8 : 0);
            if (this.mHeadersGone) {
                return;
            }
            if (this.mHeadersEnabled) {
                verticalGridView.setChildrenVisibility(0);
            } else {
                verticalGridView.setChildrenVisibility(4);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setHeadersEnabled(boolean z) {
        this.mHeadersEnabled = z;
        updateListViewVisibility();
    }

    /* access modifiers changed from: package-private */
    public void setHeadersGone(boolean z) {
        this.mHeadersGone = z;
        updateListViewVisibility();
    }

    static class NoOverlappingFrameLayout extends FrameLayout {
        public boolean hasOverlappingRendering() {
            return false;
        }

        public NoOverlappingFrameLayout(Context context) {
            super(context);
        }
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.leanback.app.BaseRowSupportFragment
    public void updateAdapter() {
        super.updateAdapter();
        ItemBridgeAdapter bridgeAdapter = getBridgeAdapter();
        bridgeAdapter.setAdapterListener(this.mAdapterListener);
        bridgeAdapter.setWrapper(this.mWrapper);
    }

    /* access modifiers changed from: package-private */
    public void setBackgroundColor(int i) {
        this.mBackgroundColor = i;
        this.mBackgroundColorSet = true;
        if (getVerticalGridView() != null) {
            getVerticalGridView().setBackgroundColor(this.mBackgroundColor);
            updateFadingEdgeToBrandColor(this.mBackgroundColor);
        }
    }

    private void updateFadingEdgeToBrandColor(int i) {
        Drawable background = getView().findViewById(R$id.fade_out_edge).getBackground();
        if (background instanceof GradientDrawable) {
            background.mutate();
            ((GradientDrawable) background).setColors(new int[]{0, i});
        }
    }

    @Override // androidx.leanback.app.BaseRowSupportFragment
    public void onTransitionStart() {
        VerticalGridView verticalGridView;
        super.onTransitionStart();
        if (!this.mHeadersEnabled && (verticalGridView = getVerticalGridView()) != null) {
            verticalGridView.setDescendantFocusability(131072);
            if (verticalGridView.hasFocus()) {
                verticalGridView.requestFocus();
            }
        }
    }

    @Override // androidx.leanback.app.BaseRowSupportFragment
    public void onTransitionEnd() {
        VerticalGridView verticalGridView;
        if (this.mHeadersEnabled && (verticalGridView = getVerticalGridView()) != null) {
            verticalGridView.setDescendantFocusability(262144);
            if (verticalGridView.hasFocus()) {
                verticalGridView.requestFocus();
            }
        }
        super.onTransitionEnd();
    }

    public boolean isScrolling() {
        return getVerticalGridView().getScrollState() != 0;
    }
}
