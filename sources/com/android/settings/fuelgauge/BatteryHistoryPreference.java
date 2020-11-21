package com.android.settings.fuelgauge;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.fuelgauge.BatteryInfo;
import com.android.settings.widget.UsageView;

public class BatteryHistoryPreference extends Preference {
    boolean hideSummary;
    BatteryInfo mBatteryInfo;
    private CharSequence mSummary;
    private TextView mSummaryView;

    public BatteryHistoryPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.battery_usage_graph);
        setSelectable(false);
    }

    public void setStats(BatteryStatsHelper batteryStatsHelper) {
        BatteryInfo.getBatteryInfo(getContext(), new BatteryInfo.Callback() {
            /* class com.android.settings.fuelgauge.$$Lambda$BatteryHistoryPreference$OfN0YWKsw9YRrCqoEdP8dybAPU0 */

            @Override // com.android.settings.fuelgauge.BatteryInfo.Callback
            public final void onBatteryInfoLoaded(BatteryInfo batteryInfo) {
                BatteryHistoryPreference.this.lambda$setStats$0$BatteryHistoryPreference(batteryInfo);
            }
        }, batteryStatsHelper, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setStats$0 */
    public /* synthetic */ void lambda$setStats$0$BatteryHistoryPreference(BatteryInfo batteryInfo) {
        this.mBatteryInfo = batteryInfo;
        notifyChanged();
    }

    public void setBottomSummary(CharSequence charSequence) {
        this.mSummary = charSequence;
        TextView textView = this.mSummaryView;
        if (textView != null) {
            textView.setVisibility(0);
            this.mSummaryView.setText(this.mSummary);
        }
        this.hideSummary = false;
    }

    public void hideBottomSummary() {
        TextView textView = this.mSummaryView;
        if (textView != null) {
            textView.setVisibility(8);
        }
        this.hideSummary = true;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        long currentTimeMillis = System.currentTimeMillis();
        if (this.mBatteryInfo != null) {
            ((TextView) preferenceViewHolder.findViewById(C0010R$id.charge)).setText(this.mBatteryInfo.batteryPercentString);
            TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.bottom_summary);
            this.mSummaryView = textView;
            CharSequence charSequence = this.mSummary;
            if (charSequence != null) {
                textView.setText(charSequence);
            }
            if (this.hideSummary) {
                this.mSummaryView.setVisibility(8);
            }
            UsageView usageView = (UsageView) preferenceViewHolder.findViewById(C0010R$id.battery_usage);
            usageView.findViewById(C0010R$id.label_group).setAlpha(0.7f);
            this.mBatteryInfo.bindHistory(usageView, new BatteryInfo.BatteryDataParser[0]);
            BatteryUtils.logRuntime("BatteryHistoryPreference", "onBindViewHolder", currentTimeMillis);
        }
    }
}
