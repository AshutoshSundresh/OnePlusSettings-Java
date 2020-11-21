package com.android.settings.datausage;

import android.content.Context;
import android.content.res.Resources;
import android.net.NetworkPolicy;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.widget.UsageView;
import com.android.settingslib.Utils;
import com.android.settingslib.net.NetworkCycleChartData;
import com.android.settingslib.net.NetworkCycleData;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ChartDataUsagePreference extends Preference {
    private long mEnd;
    private final int mLimitColor;
    private NetworkCycleChartData mNetworkCycleChartData;
    private NetworkPolicy mPolicy;
    private Resources mResources;
    private long mStart;
    private TextView mUsageAmount;
    private String mUsageAmountString;
    private final int mWarningColor;

    public void setShowWifi(boolean z) {
    }

    public void setSubId(int i) {
    }

    public ChartDataUsagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mResources = context.getResources();
        setSelectable(false);
        this.mLimitColor = Utils.getColorAttrDefaultColor(context, 16844099);
        this.mWarningColor = Utils.getColorAttrDefaultColor(context, 16842808);
        setLayoutResource(C0012R$layout.data_usage_graph);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        UsageView usageView = (UsageView) preferenceViewHolder.findViewById(C0010R$id.data_usage);
        TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.header_title);
        this.mUsageAmount = textView;
        textView.setText(this.mUsageAmountString);
        if (this.mNetworkCycleChartData != null) {
            int top = getTop();
            usageView.clearPaths();
            usageView.configureGraph(toInt(this.mEnd - this.mStart), top);
            calcPoints(usageView, this.mNetworkCycleChartData.getUsageBuckets());
            setupContentDescription(usageView, this.mNetworkCycleChartData.getUsageBuckets());
            Context context = getContext();
            long j = this.mStart;
            Context context2 = getContext();
            long j2 = this.mEnd;
            usageView.setBottomLabels(new CharSequence[]{com.android.settings.Utils.formatDateRange(context, j, j), com.android.settings.Utils.formatDateRange(context2, j2, j2)});
            bindNetworkPolicy(usageView, this.mPolicy, top);
        }
    }

    public int getTop() {
        long totalUsage = this.mNetworkCycleChartData.getTotalUsage();
        NetworkPolicy networkPolicy = this.mPolicy;
        return (int) (Math.max(totalUsage, networkPolicy != null ? Math.max(networkPolicy.limitBytes, networkPolicy.warningBytes) : 0) / 524288);
    }

    /* access modifiers changed from: package-private */
    public void calcPoints(UsageView usageView, List<NetworkCycleData> list) {
        if (list != null) {
            SparseIntArray sparseIntArray = new SparseIntArray();
            sparseIntArray.put(0, 0);
            long currentTimeMillis = System.currentTimeMillis();
            long j = 0;
            for (NetworkCycleData networkCycleData : list) {
                long startTime = networkCycleData.getStartTime();
                if (startTime > currentTimeMillis) {
                    break;
                }
                long endTime = networkCycleData.getEndTime();
                j += networkCycleData.getTotalUsage();
                if (sparseIntArray.size() == 1) {
                    sparseIntArray.put(toInt(startTime - this.mStart) - 1, -1);
                }
                int i = (int) (j / 524288);
                sparseIntArray.put(toInt((startTime - this.mStart) + 1), i);
                sparseIntArray.put(toInt(endTime - this.mStart), i);
            }
            if (sparseIntArray.size() > 1) {
                usageView.addPath(sparseIntArray);
            }
        }
    }

    private void setupContentDescription(UsageView usageView, List<NetworkCycleData> list) {
        String str;
        Context context = getContext();
        StringBuilder sb = new StringBuilder();
        String formatDateTime = DateUtils.formatDateTime(context, this.mStart, 65552);
        String formatDateTime2 = DateUtils.formatDateTime(context, this.mEnd, 65552);
        sb.append(this.mResources.getString(C0017R$string.data_usage_chart_brief_content_description, formatDateTime, formatDateTime2));
        if (list == null || list.isEmpty()) {
            sb.append(this.mResources.getString(C0017R$string.data_usage_chart_no_data_content_description));
            usageView.setContentDescription(sb);
            return;
        }
        for (DataUsageSummaryNode dataUsageSummaryNode : getDensedStatsData(list)) {
            int dataUsagePercentage = dataUsageSummaryNode.getDataUsagePercentage();
            if (!dataUsageSummaryNode.isFromMultiNode() || dataUsagePercentage == 100) {
                str = DateUtils.formatDateTime(context, dataUsageSummaryNode.getStartTime(), 65552);
            } else {
                str = DateUtils.formatDateRange(context, dataUsageSummaryNode.getStartTime(), dataUsageSummaryNode.getEndTime(), 65552);
            }
            sb.append(String.format(";%s %d%%", str, Integer.valueOf(dataUsagePercentage)));
        }
        usageView.setContentDescription(sb);
    }

    /* access modifiers changed from: package-private */
    public List<DataUsageSummaryNode> getDensedStatsData(List<NetworkCycleData> list) {
        ArrayList arrayList = new ArrayList();
        long sum = list.stream().mapToLong($$Lambda$ghh2toOjwyjlPmXMtnOuNkEnT_o.INSTANCE).sum();
        long j = 0;
        for (NetworkCycleData networkCycleData : list) {
            j += networkCycleData.getTotalUsage();
            arrayList.add(new DataUsageSummaryNode(this, networkCycleData.getStartTime(), networkCycleData.getEndTime(), (int) ((100 * j) / sum)));
        }
        ArrayList arrayList2 = new ArrayList();
        ((Map) arrayList.stream().collect(Collectors.groupingBy($$Lambda$92EMHt1mPXlA130EakwwqtgNg.INSTANCE))).forEach(new BiConsumer(arrayList2) {
            /* class com.android.settings.datausage.$$Lambda$ChartDataUsagePreference$kyLbYCkB_z0vLhtvxIwB6dl0waA */
            public final /* synthetic */ List f$1;

            {
                this.f$1 = r2;
            }

            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ChartDataUsagePreference.this.lambda$getDensedStatsData$0$ChartDataUsagePreference(this.f$1, (Integer) obj, (List) obj2);
            }
        });
        return (List) arrayList2.stream().sorted(Comparator.comparingInt($$Lambda$7k7RmT7rN3prw6YvDhpJTq7E_bI.INSTANCE)).collect(Collectors.toList());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getDensedStatsData$0 */
    public /* synthetic */ void lambda$getDensedStatsData$0$ChartDataUsagePreference(List list, Integer num, List list2) {
        DataUsageSummaryNode dataUsageSummaryNode = new DataUsageSummaryNode(this, list2.stream().mapToLong($$Lambda$0fsSg0zNz3crDgww2accED2sC8.INSTANCE).min().getAsLong(), list2.stream().mapToLong($$Lambda$Zg9pPJv8QPElol3BBOi0EKPeRfc.INSTANCE).max().getAsLong(), num.intValue());
        if (list2.size() > 1) {
            dataUsageSummaryNode.setFromMultiNode(true);
        }
        list.add(dataUsageSummaryNode);
    }

    /* access modifiers changed from: package-private */
    public class DataUsageSummaryNode {
        private int mDataUsagePercentage;
        private long mEndTime;
        private boolean mIsFromMultiNode = false;
        private long mStartTime;

        public DataUsageSummaryNode(ChartDataUsagePreference chartDataUsagePreference, long j, long j2, int i) {
            this.mStartTime = j;
            this.mEndTime = j2;
            this.mDataUsagePercentage = i;
        }

        public long getStartTime() {
            return this.mStartTime;
        }

        public long getEndTime() {
            return this.mEndTime;
        }

        public int getDataUsagePercentage() {
            return this.mDataUsagePercentage;
        }

        public void setFromMultiNode(boolean z) {
            this.mIsFromMultiNode = z;
        }

        public boolean isFromMultiNode() {
            return this.mIsFromMultiNode;
        }
    }

    private int toInt(long j) {
        return (int) (j / 60000);
    }

    private void bindNetworkPolicy(UsageView usageView, NetworkPolicy networkPolicy, int i) {
        int i2;
        CharSequence[] charSequenceArr = new CharSequence[3];
        if (networkPolicy != null) {
            long j = networkPolicy.limitBytes;
            int i3 = 0;
            if (j != -1) {
                i2 = this.mLimitColor;
                charSequenceArr[2] = getLabel(j, C0017R$string.data_usage_sweep_limit, i2);
            } else {
                i2 = 0;
            }
            long j2 = networkPolicy.warningBytes;
            if (j2 != -1) {
                usageView.setDividerLoc((int) (j2 / 524288));
                float f = ((float) (networkPolicy.warningBytes / 524288)) / ((float) i);
                usageView.setSideLabelWeights(1.0f - f, f);
                i3 = this.mWarningColor;
                charSequenceArr[1] = getLabel(networkPolicy.warningBytes, C0017R$string.data_usage_sweep_warning, i3);
            }
            usageView.setSideLabels(charSequenceArr);
            usageView.setDividerColors(i3, i2);
        }
    }

    private CharSequence getLabel(long j, int i, int i2) {
        Formatter.BytesResult formatBytes = Formatter.formatBytes(getContext().getResources(), j, 9);
        String str = formatBytes.units;
        if (Build.VERSION.SDK_INT > 26) {
            str = OPUtils.replaceFileSize(str);
        }
        return new SpannableStringBuilder().append(TextUtils.expandTemplate(getContext().getText(i), formatBytes.value, str), new ForegroundColorSpan(i2), 0);
    }

    public void setNetworkPolicy(NetworkPolicy networkPolicy) {
        this.mPolicy = networkPolicy;
        notifyChanged();
    }

    public long getInspectStart() {
        return this.mStart;
    }

    public long getInspectEnd() {
        return this.mEnd;
    }

    public void setNetworkCycleData(NetworkCycleChartData networkCycleChartData) {
        this.mNetworkCycleChartData = networkCycleChartData;
        this.mStart = networkCycleChartData.getStartTime();
        this.mEnd = networkCycleChartData.getEndTime();
        notifyChanged();
    }

    public void setVisibleRange(long j, long j2) {
        this.mStart = j;
        this.mEnd = j2;
        notifyChanged();
    }

    public void setColors(int i, int i2) {
        notifyChanged();
    }

    public void setUsageAmount(String str) {
        this.mUsageAmountString = str;
        TextView textView = this.mUsageAmount;
        if (textView != null) {
            textView.setText(str);
        }
    }
}
