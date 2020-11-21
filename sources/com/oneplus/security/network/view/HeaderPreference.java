package com.oneplus.security.network.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.security.network.operator.AccountDayLocalCache;
import com.oneplus.security.network.trafficalarm.TrafficUsageAlarmUtils;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.Utils;

public class HeaderPreference extends Preference {
    private String dataLeftTitle;
    private String dataUsedTitle;
    private Context mContext;
    private String mDataLimitLabel;
    private String mDataUsageLabel;
    private View mDataUsageLayout;
    private TextView mDataUsageLeftValue;
    private ProgressBar mDataUsageProgress;
    private TextView mDataUsageTitle;
    private TextView mDataUsageTotal;
    private TextView mDataUsageUsed;
    private String mDataWarnLabel;

    public HeaderPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    public HeaderPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public HeaderPreference(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        setLayoutResource(C0012R$layout.data_usage_sim_pref_header);
        setSelectable(false);
        this.dataLeftTitle = this.mContext.getResources().getString(C0017R$string.data_usage_left_title);
        this.dataUsedTitle = this.mContext.getResources().getString(C0017R$string.traffic_package_used);
        this.mDataLimitLabel = this.mContext.getString(C0017R$string.data_usage_limit_label);
        this.mDataUsageLabel = this.mContext.getString(C0017R$string.data_usage_used_label);
        this.mDataWarnLabel = this.mContext.getString(C0017R$string.data_usage_warn_label);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        View view = preferenceViewHolder.itemView;
        this.mDataUsageLayout = view.findViewById(C0010R$id.data_usage_layout);
        this.mDataUsageLeftValue = (TextView) view.findViewById(C0010R$id.data_usage_left_value);
        this.mDataUsageTitle = (TextView) view.findViewById(C0010R$id.data_usage_left_title);
        this.mDataUsageProgress = (ProgressBar) view.findViewById(C0010R$id.data_usage_progress);
        this.mDataUsageUsed = (TextView) view.findViewById(C0010R$id.data_usage_used_value);
        this.mDataUsageTotal = (TextView) view.findViewById(C0010R$id.data_usage_total);
    }

    public void updateData(int i, long j, long j2, long j3) {
        LogUtils.d("HeaderPreference", "mCurrentSlotId=" + i + ",total=" + j + ",used=" + j2);
        if (j2 == -1) {
            this.mDataUsageLeftValue.setText(getDataInvalidValueHintString());
            this.mDataUsageLayout.setVisibility(8);
            this.mDataUsageProgress.setVisibility(8);
            return;
        }
        String[] formattedFileSizeAndUnitForDisplay = Utils.getFormattedFileSizeAndUnitForDisplay(this.mContext, j - j2, true, true);
        String[] formattedFileSizeAndUnitForDisplay2 = Utils.getFormattedFileSizeAndUnitForDisplay(this.mContext, j2 > 0 ? j2 : 0, true, true);
        String[] formattedFileSizeAndUnitForDisplay3 = Utils.getFormattedFileSizeAndUnitForDisplay(this.mContext, j > 0 ? j : 0, true, true);
        if (j == -1 || !TrafficUsageAlarmUtils.getDataTotalState(this.mContext, i)) {
            this.mDataUsageTitle.setText(String.format("%s(%s)", this.dataUsedTitle, getDataUsageSection(i)));
            this.mDataUsageLeftValue.setText(String.format("%s%s", formattedFileSizeAndUnitForDisplay2[0], formattedFileSizeAndUnitForDisplay2[1]));
            if (j3 == -1) {
                this.mDataUsageLayout.setVisibility(8);
                this.mDataUsageProgress.setVisibility(8);
                return;
            }
            this.mDataUsageUsed.setVisibility(4);
            Context context = this.mContext;
            long j4 = 0;
            if (j3 > 0) {
                j4 = j3;
            }
            String[] formattedFileSizeAndUnitForDisplay4 = Utils.getFormattedFileSizeAndUnitForDisplay(context, j4, true, false);
            this.mDataUsageTotal.setText(String.format("%s %s%s", this.mDataWarnLabel, formattedFileSizeAndUnitForDisplay4[0], formattedFileSizeAndUnitForDisplay4[1]));
            int i2 = (int) ((j2 * 100) / j3);
            LogUtils.d("HeaderPreference", "usedPercent=" + i2);
            this.mDataUsageProgress.setProgress(i2);
            this.mDataUsageProgress.setVisibility(0);
            this.mDataUsageLayout.setVisibility(0);
            return;
        }
        this.mDataUsageTitle.setText(String.format("%s(%s)", this.dataLeftTitle, getDataUsageSection(i)));
        this.mDataUsageLeftValue.setText(String.format("%s%s", formattedFileSizeAndUnitForDisplay[0], formattedFileSizeAndUnitForDisplay[1]));
        this.mDataUsageTotal.setText(String.format("%s %s%s", this.mDataLimitLabel, formattedFileSizeAndUnitForDisplay3[0], formattedFileSizeAndUnitForDisplay3[1]));
        this.mDataUsageUsed.setText(String.format("%s %s%s", this.mDataUsageLabel, formattedFileSizeAndUnitForDisplay2[0], formattedFileSizeAndUnitForDisplay2[1]));
        if (j > j2) {
            this.mDataUsageProgress.setProgress((int) ((j2 * 100) / j));
            this.mDataUsageProgress.setProgressDrawable(this.mContext.getDrawable(C0008R$drawable.progress_horizontal));
        } else {
            this.mDataUsageProgress.setProgress((int) ((j * 100) / j2));
            this.mDataUsageProgress.setProgressDrawable(this.mContext.getDrawable(C0008R$drawable.progress_horizontal_limit));
        }
        this.mDataUsageProgress.setVisibility(0);
        this.mDataUsageLayout.setVisibility(0);
        this.mDataUsageUsed.setVisibility(0);
    }

    private String getDataInvalidValueHintString() {
        return this.mContext.getString(C0017R$string.data_invalid_value_hint);
    }

    public String getDataUsageSection(int i) {
        long[] dataUsageSectionTimeMillByAccountDay = AccountDayLocalCache.getDataUsageSectionTimeMillByAccountDay(this.mContext, i);
        return Utils.formatDateRange(this.mContext, dataUsageSectionTimeMillByAccountDay[0], dataUsageSectionTimeMillByAccountDay[1]);
    }
}
