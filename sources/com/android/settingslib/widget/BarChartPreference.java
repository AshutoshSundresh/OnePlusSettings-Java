package com.android.settingslib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import java.util.Arrays;

public class BarChartPreference extends Preference {
    private static final int[] BAR_VIEWS = {R$id.bar_view1, R$id.bar_view2, R$id.bar_view3, R$id.bar_view4};
    private BarChartInfo mBarChartInfo;
    private boolean mIsLoading;
    private int mMaxBarHeight;

    public BarChartPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public BarChartPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public BarChartPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(true);
        preferenceViewHolder.setDividerAllowedBelow(true);
        bindChartTitleView(preferenceViewHolder);
        bindChartDetailsView(preferenceViewHolder);
        if (this.mIsLoading) {
            preferenceViewHolder.itemView.setVisibility(4);
            return;
        }
        preferenceViewHolder.itemView.setVisibility(0);
        BarViewInfo[] barViewInfos = this.mBarChartInfo.getBarViewInfos();
        if (barViewInfos == null || barViewInfos.length == 0) {
            setEmptyViewVisible(preferenceViewHolder, true);
            return;
        }
        setEmptyViewVisible(preferenceViewHolder, false);
        updateBarChart(preferenceViewHolder);
    }

    private void init() {
        setSelectable(false);
        setLayoutResource(R$layout.settings_bar_chart);
        this.mMaxBarHeight = getContext().getResources().getDimensionPixelSize(R$dimen.settings_bar_view_max_height);
    }

    private void bindChartTitleView(PreferenceViewHolder preferenceViewHolder) {
        ((TextView) preferenceViewHolder.findViewById(R$id.bar_chart_title)).setText(this.mBarChartInfo.getTitle());
    }

    private void bindChartDetailsView(PreferenceViewHolder preferenceViewHolder) {
        Button button = (Button) preferenceViewHolder.findViewById(R$id.bar_chart_details);
        int details = this.mBarChartInfo.getDetails();
        if (details == 0) {
            button.setVisibility(8);
            return;
        }
        button.setVisibility(0);
        button.setText(details);
        button.setOnClickListener(this.mBarChartInfo.getDetailsOnClickListener());
    }

    private void updateBarChart(PreferenceViewHolder preferenceViewHolder) {
        normalizeBarViewHeights();
        BarViewInfo[] barViewInfos = this.mBarChartInfo.getBarViewInfos();
        for (int i = 0; i < 4; i++) {
            BarView barView = (BarView) preferenceViewHolder.findViewById(BAR_VIEWS[i]);
            if (barViewInfos == null || i >= barViewInfos.length) {
                barView.setVisibility(8);
            } else {
                barView.setVisibility(0);
                barView.updateView(barViewInfos[i]);
            }
        }
    }

    private void normalizeBarViewHeights() {
        BarViewInfo[] barViewInfos = this.mBarChartInfo.getBarViewInfos();
        if (!(barViewInfos == null || barViewInfos.length == 0)) {
            Arrays.sort(barViewInfos);
            int height = barViewInfos[0].getHeight();
            int i = height == 0 ? 0 : this.mMaxBarHeight / height;
            for (BarViewInfo barViewInfo : barViewInfos) {
                barViewInfo.setNormalizedHeight(barViewInfo.getHeight() * i);
            }
        }
    }

    private void setEmptyViewVisible(PreferenceViewHolder preferenceViewHolder, boolean z) {
        View findViewById = preferenceViewHolder.findViewById(R$id.bar_views_container);
        TextView textView = (TextView) preferenceViewHolder.findViewById(R$id.empty_view);
        int emptyText = this.mBarChartInfo.getEmptyText();
        if (emptyText != 0) {
            textView.setText(emptyText);
        }
        int i = 0;
        textView.setVisibility(z ? 0 : 8);
        if (z) {
            i = 8;
        }
        findViewById.setVisibility(i);
    }
}
