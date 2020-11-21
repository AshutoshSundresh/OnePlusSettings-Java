package com.android.settings.panel;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.widget.EventInfo;
import androidx.slice.widget.RowView;
import androidx.slice.widget.SliceChildView;
import androidx.slice.widget.SliceView;
import androidx.slice.widget.TemplateView;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0018R$style;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.panel.PanelSlicesAdapter;
import com.android.settings.slices.CustomSliceRegistry;
import com.google.android.setupdesign.DividerItemDecoration;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelSlicesAdapter extends RecyclerView.Adapter<SliceRowViewHolder> {
    static final int MAX_NUM_OF_SLICES = 6;
    private final int mMetricsCategory;
    private final PanelFragment mPanelFragment;
    private final List<LiveData<Slice>> mSliceLiveData;

    public PanelSlicesAdapter(PanelFragment panelFragment, Map<Uri, LiveData<Slice>> map, int i) {
        this.mPanelFragment = panelFragment;
        this.mSliceLiveData = new ArrayList(map.values());
        this.mMetricsCategory = i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public SliceRowViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        if (i == 1) {
            view = from.inflate(C0012R$layout.panel_slice_slider_row, viewGroup, false);
        } else if (i == 2) {
            view = from.inflate(C0012R$layout.panel_slice_slider_row_large_icon, viewGroup, false);
        } else {
            view = from.inflate(C0012R$layout.panel_slice_row, viewGroup, false);
        }
        return new SliceRowViewHolder(view);
    }

    public void onBindViewHolder(SliceRowViewHolder sliceRowViewHolder, int i) {
        sliceRowViewHolder.onBind(this.mSliceLiveData.get(i), i);
        changeSliceTextStyle(sliceRowViewHolder.sliceView);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return Math.min(this.mSliceLiveData.size(), 6);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return this.mPanelFragment.getPanelViewType();
    }

    /* access modifiers changed from: package-private */
    public List<LiveData<Slice>> getData() {
        return this.mSliceLiveData.subList(0, getItemCount());
    }

    public class SliceRowViewHolder extends RecyclerView.ViewHolder implements DividerItemDecoration.DividedViewHolder {
        private boolean mDividerAllowedAbove = true;
        final LinearLayout mSliceSliderLayout;
        final SliceView sliceView;

        public SliceRowViewHolder(View view) {
            super(view);
            SliceView sliceView2 = (SliceView) view.findViewById(C0010R$id.slice_view);
            this.sliceView = sliceView2;
            sliceView2.setMode(2);
            this.sliceView.setShowTitleItems(true);
            this.mSliceSliderLayout = (LinearLayout) view.findViewById(C0010R$id.slice_slider_layout);
        }

        public void onBind(LiveData<Slice> liveData, int i) {
            liveData.observe(PanelSlicesAdapter.this.mPanelFragment.getViewLifecycleOwner(), this.sliceView);
            Slice value = liveData.getValue();
            if (value == null || value.getUri().equals(CustomSliceRegistry.MEDIA_OUTPUT_INDICATOR_SLICE_URI)) {
                this.mDividerAllowedAbove = false;
            } else if (i == 0 && (value.getUri().equals(CustomSliceRegistry.MEDIA_OUTPUT_SLICE_URI) || value.getUri().equals(CustomSliceRegistry.MEDIA_OUTPUT_GROUP_SLICE_URI))) {
                this.sliceView.setClickable(false);
                int dimensionPixelSize = PanelSlicesAdapter.this.mPanelFragment.getResources().getDimensionPixelSize(C0007R$dimen.output_switcher_slice_padding_top);
                LinearLayout linearLayout = this.mSliceSliderLayout;
                linearLayout.setPadding(linearLayout.getPaddingLeft(), dimensionPixelSize, this.mSliceSliderLayout.getPaddingRight(), this.mSliceSliderLayout.getPaddingBottom());
            }
            this.sliceView.setOnSliceActionListener(new SliceView.OnSliceActionListener(liveData) {
                /* class com.android.settings.panel.$$Lambda$PanelSlicesAdapter$SliceRowViewHolder$ouV_HuHVg07ybpD3Y7B9ziPSnYw */
                public final /* synthetic */ LiveData f$1;

                {
                    this.f$1 = r2;
                }

                @Override // androidx.slice.widget.SliceView.OnSliceActionListener
                public final void onSliceAction(EventInfo eventInfo, SliceItem sliceItem) {
                    PanelSlicesAdapter.SliceRowViewHolder.this.lambda$onBind$0$PanelSlicesAdapter$SliceRowViewHolder(this.f$1, eventInfo, sliceItem);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onBind$0 */
        public /* synthetic */ void lambda$onBind$0$PanelSlicesAdapter$SliceRowViewHolder(LiveData liveData, EventInfo eventInfo, SliceItem sliceItem) {
            FeatureFactory.getFactory(this.sliceView.getContext()).getMetricsFeatureProvider().action(0, 1658, PanelSlicesAdapter.this.mMetricsCategory, ((Slice) liveData.getValue()).getUri().getLastPathSegment(), eventInfo.actionType);
        }

        @Override // com.google.android.setupdesign.DividerItemDecoration.DividedViewHolder
        public boolean isDividerAllowedAbove() {
            return this.mDividerAllowedAbove;
        }

        @Override // com.google.android.setupdesign.DividerItemDecoration.DividedViewHolder
        public boolean isDividerAllowedBelow() {
            return PanelSlicesAdapter.this.mPanelFragment.getPanelViewType() != 2;
        }
    }

    private void changeSliceTextStyle(SliceView sliceView) {
        SliceChildView sliceChildView;
        ReflectiveOperationException e;
        try {
            Field declaredField = SliceView.class.getDeclaredField("mCurrentView");
            declaredField.setAccessible(true);
            sliceChildView = (SliceChildView) declaredField.get(sliceView);
            try {
                Log.d("PanelSlicesAdapter", "sliceChildView = " + sliceChildView);
            } catch (IllegalAccessException | NoSuchFieldException e2) {
                e = e2;
            }
        } catch (IllegalAccessException | NoSuchFieldException e3) {
            sliceChildView = null;
            e = e3;
            e.printStackTrace();
            if (sliceChildView != null) {
                return;
            }
        }
        if (sliceChildView != null && (sliceChildView instanceof TemplateView)) {
            try {
                Field declaredField2 = TemplateView.class.getDeclaredField("mRecyclerView");
                declaredField2.setAccessible(true);
                final RecyclerView recyclerView = (RecyclerView) declaredField2.get(sliceChildView);
                sliceView.post(new Runnable(this) {
                    /* class com.android.settings.panel.PanelSlicesAdapter.AnonymousClass1 */

                    public void run() {
                        if (recyclerView != null) {
                            Log.d("PanelSlicesAdapter", "recyclerView != null");
                            int childCount = recyclerView.getChildCount();
                            Log.d("PanelSlicesAdapter", "count =  " + childCount);
                            for (int i = 0; i < childCount; i++) {
                                View childAt = recyclerView.getChildAt(i);
                                if (childAt instanceof RowView) {
                                    Log.d("PanelSlicesAdapter", "view instanceof RowView ");
                                    try {
                                        Field declaredField = RowView.class.getDeclaredField("mPrimaryText");
                                        declaredField.setAccessible(true);
                                        TextView textView = (TextView) declaredField.get(childAt);
                                        if (textView != null) {
                                            textView.setTextAppearance(C0018R$style.op_control_text_style_h6);
                                            Log.d("PanelSlicesAdapter", "mPrimaryText = " + declaredField);
                                        }
                                    } catch (IllegalAccessException | NoSuchFieldException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });
            } catch (IllegalAccessException | NoSuchFieldException e4) {
                e4.printStackTrace();
            }
        }
    }
}
