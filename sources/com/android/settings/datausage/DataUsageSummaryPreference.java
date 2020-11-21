package com.android.settings.datausage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.Utils;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.utils.StringUtil;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DataUsageSummaryPreference extends Preference {
    private static final long MILLIS_IN_A_DAY = TimeUnit.DAYS.toMillis(1);
    static final Typeface SANS_SERIF_MEDIUM = Typeface.create("sans-serif-medium", 0);
    private static final long WARNING_AGE = TimeUnit.HOURS.toMillis(6);
    private CharSequence mCarrierName;
    private boolean mChartEnabled = true;
    private long mCycleEndTimeMs;
    private long mDataplanSize;
    private long mDataplanUse;
    private CharSequence mEndLabel;
    private boolean mHasMobileData;
    private Intent mLaunchIntent;
    private CharSequence mLimitInfoText;
    private int mNumPlans;
    private float mProgress;
    private boolean mSingleWifi;
    private long mSnapshotTimeMs;
    private CharSequence mStartLabel;
    private String mUsagePeriod;
    private boolean mWifiMode;

    public DataUsageSummaryPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.data_usage_summary_preference);
    }

    public void setLimitInfo(CharSequence charSequence) {
        if (!Objects.equals(charSequence, this.mLimitInfoText)) {
            this.mLimitInfoText = charSequence;
            notifyChanged();
        }
    }

    public void setProgress(float f) {
        this.mProgress = f;
        notifyChanged();
    }

    public void setUsageInfo(long j, long j2, CharSequence charSequence, int i, Intent intent) {
        this.mCycleEndTimeMs = j;
        this.mSnapshotTimeMs = j2;
        this.mCarrierName = charSequence;
        this.mNumPlans = i;
        this.mLaunchIntent = intent;
        notifyChanged();
    }

    public void setChartEnabled(boolean z) {
        if (this.mChartEnabled != z) {
            this.mChartEnabled = z;
            notifyChanged();
        }
    }

    public void setLabels(CharSequence charSequence, CharSequence charSequence2) {
        this.mStartLabel = charSequence;
        this.mEndLabel = charSequence2;
        notifyChanged();
    }

    /* access modifiers changed from: package-private */
    public void setUsageNumbers(long j, long j2, boolean z) {
        this.mDataplanUse = j;
        this.mDataplanSize = j2;
        this.mHasMobileData = z;
        notifyChanged();
    }

    /* access modifiers changed from: package-private */
    public void setWifiMode(boolean z, String str, boolean z2) {
        this.mWifiMode = z;
        this.mUsagePeriod = str;
        this.mSingleWifi = z2;
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ProgressBar progressBar = (ProgressBar) preferenceViewHolder.findViewById(C0010R$id.determinateBar);
        int i = 0;
        if (!this.mChartEnabled || (TextUtils.isEmpty(this.mStartLabel) && TextUtils.isEmpty(this.mEndLabel))) {
            progressBar.setVisibility(8);
            preferenceViewHolder.findViewById(C0010R$id.label_bar).setVisibility(8);
        } else {
            progressBar.setVisibility(0);
            preferenceViewHolder.findViewById(C0010R$id.label_bar).setVisibility(0);
            progressBar.setProgress((int) (this.mProgress * 100.0f));
            ((TextView) preferenceViewHolder.findViewById(16908308)).setText(this.mStartLabel);
            ((TextView) preferenceViewHolder.findViewById(16908309)).setText(this.mEndLabel);
        }
        updateDataUsageLabels(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.usage_title);
        TextView textView2 = (TextView) preferenceViewHolder.findViewById(C0010R$id.carrier_and_update);
        Button button = (Button) preferenceViewHolder.findViewById(C0010R$id.launch_mdp_app_button);
        TextView textView3 = (TextView) preferenceViewHolder.findViewById(C0010R$id.data_limits);
        if (this.mWifiMode && this.mSingleWifi) {
            updateCycleTimeText(preferenceViewHolder);
            textView.setVisibility(8);
            button.setVisibility(8);
            textView2.setVisibility(8);
            if (TextUtils.isEmpty(this.mLimitInfoText)) {
                i = 8;
            }
            textView3.setVisibility(i);
            textView3.setText(this.mLimitInfoText);
        } else if (this.mWifiMode) {
            textView.setText(C0017R$string.data_usage_wifi_title);
            textView.setVisibility(0);
            ((TextView) preferenceViewHolder.findViewById(C0010R$id.cycle_left_time)).setText(this.mUsagePeriod);
            textView2.setVisibility(8);
            textView3.setVisibility(8);
            if (getHistoricalUsageLevel() > 0) {
                button.setOnClickListener(new View.OnClickListener() {
                    /* class com.android.settings.datausage.$$Lambda$DataUsageSummaryPreference$zBjNn20lFyV2SqYMtfKeIRkAo7w */

                    public final void onClick(View view) {
                        DataUsageSummaryPreference.this.lambda$onBindViewHolder$0$DataUsageSummaryPreference(view);
                    }
                });
            } else {
                button.setEnabled(false);
            }
            button.setText(C0017R$string.launch_wifi_text);
            button.setVisibility(0);
        } else {
            textView.setVisibility(this.mNumPlans > 1 ? 0 : 8);
            updateCycleTimeText(preferenceViewHolder);
            updateCarrierInfo(textView2);
            if (this.mLaunchIntent != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    /* class com.android.settings.datausage.$$Lambda$DataUsageSummaryPreference$1NKWVGupHVFnsudApVgFBRMGUJg */

                    public final void onClick(View view) {
                        DataUsageSummaryPreference.this.lambda$onBindViewHolder$1$DataUsageSummaryPreference(view);
                    }
                });
                button.setVisibility(0);
            } else {
                button.setVisibility(8);
            }
            if (TextUtils.isEmpty(this.mLimitInfoText)) {
                i = 8;
            }
            textView3.setVisibility(i);
            textView3.setText(this.mLimitInfoText);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$0 */
    public /* synthetic */ void lambda$onBindViewHolder$0$DataUsageSummaryPreference(View view) {
        launchWifiDataUsage(getContext());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$1 */
    public /* synthetic */ void lambda$onBindViewHolder$1$DataUsageSummaryPreference(View view) {
        getContext().startActivity(this.mLaunchIntent);
    }

    static void launchWifiDataUsage(Context context) {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable("network_template", NetworkTemplate.buildTemplateWifiWildcard());
        bundle.putInt("network_type", 1);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(context);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setDestination(DataUsageList.class.getName());
        subSettingLauncher.setSourceMetricsCategory(0);
        subSettingLauncher.setTitleRes(C0017R$string.wifi_data_usage);
        subSettingLauncher.launch();
    }

    private void updateDataUsageLabels(PreferenceViewHolder preferenceViewHolder) {
        TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.data_usage_view);
        Formatter.BytesResult formatBytes = Formatter.formatBytes(getContext().getResources(), this.mDataplanUse, 10);
        SpannableString spannableString = new SpannableString(formatBytes.value);
        spannableString.setSpan(new AbsoluteSizeSpan(getContext().getResources().getDimensionPixelSize(C0007R$dimen.usage_number_text_size)), 0, spannableString.length(), 33);
        textView.setText(TextUtils.expandTemplate(getContext().getText(C0017R$string.data_used_formatted), spannableString, formatBytes.units));
        MeasurableLinearLayout measurableLinearLayout = (MeasurableLinearLayout) preferenceViewHolder.findViewById(C0010R$id.usage_layout);
        if (!this.mHasMobileData || this.mNumPlans < 0 || this.mDataplanSize <= 0) {
            measurableLinearLayout.setChildren(textView, null);
            return;
        }
        TextView textView2 = (TextView) preferenceViewHolder.findViewById(C0010R$id.data_remaining_view);
        long j = this.mDataplanSize - this.mDataplanUse;
        if (j >= 0) {
            textView2.setText(TextUtils.expandTemplate(getContext().getText(C0017R$string.data_remaining), DataUsageUtils.formatDataUsage(getContext(), j)));
            textView2.setTextColor(Utils.getColorAttr(getContext(), 16843829));
        } else {
            textView2.setText(TextUtils.expandTemplate(getContext().getText(C0017R$string.data_overusage), DataUsageUtils.formatDataUsage(getContext(), -j)));
            textView2.setTextColor(Utils.getColorAttr(getContext(), 16844099));
        }
        measurableLinearLayout.setChildren(textView, textView2);
    }

    private void updateCycleTimeText(PreferenceViewHolder preferenceViewHolder) {
        String str;
        TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.cycle_left_time);
        if (this.mCycleEndTimeMs == 0) {
            textView.setVisibility(8);
            return;
        }
        textView.setVisibility(0);
        long currentTimeMillis = this.mCycleEndTimeMs - System.currentTimeMillis();
        if (currentTimeMillis <= 0) {
            textView.setText(getContext().getString(C0017R$string.billing_cycle_none_left));
            return;
        }
        int i = (int) (currentTimeMillis / MILLIS_IN_A_DAY);
        if (i < 1) {
            str = getContext().getString(C0017R$string.billing_cycle_less_than_one_day_left);
        } else {
            str = getContext().getResources().getQuantityString(C0015R$plurals.billing_cycle_days_left, i, Integer.valueOf(i));
        }
        textView.setText(str);
    }

    private void updateCarrierInfo(TextView textView) {
        int i;
        int i2;
        if (this.mNumPlans <= 0 || this.mSnapshotTimeMs < 0) {
            textView.setVisibility(8);
            return;
        }
        textView.setVisibility(0);
        long calculateTruncatedUpdateAge = calculateTruncatedUpdateAge();
        CharSequence charSequence = null;
        if (calculateTruncatedUpdateAge != 0) {
            if (this.mCarrierName != null) {
                i2 = C0017R$string.carrier_and_update_text;
            } else {
                i2 = C0017R$string.no_carrier_update_text;
            }
            i = i2;
            charSequence = StringUtil.formatElapsedTime(getContext(), (double) calculateTruncatedUpdateAge, false);
        } else if (this.mCarrierName != null) {
            i = C0017R$string.carrier_and_update_now_text;
        } else {
            i = C0017R$string.no_carrier_update_now_text;
        }
        textView.setText(TextUtils.expandTemplate(getContext().getText(i), this.mCarrierName, charSequence));
        if (calculateTruncatedUpdateAge <= WARNING_AGE) {
            setCarrierInfoTextStyle(textView, 16842808, Typeface.SANS_SERIF);
        } else {
            setCarrierInfoTextStyle(textView, 16844099, SANS_SERIF_MEDIUM);
        }
    }

    private long calculateTruncatedUpdateAge() {
        long millis;
        long millis2;
        long currentTimeMillis = System.currentTimeMillis() - this.mSnapshotTimeMs;
        if (currentTimeMillis >= TimeUnit.DAYS.toMillis(1)) {
            millis = currentTimeMillis / TimeUnit.DAYS.toMillis(1);
            millis2 = TimeUnit.DAYS.toMillis(1);
        } else if (currentTimeMillis >= TimeUnit.HOURS.toMillis(1)) {
            millis = currentTimeMillis / TimeUnit.HOURS.toMillis(1);
            millis2 = TimeUnit.HOURS.toMillis(1);
        } else if (currentTimeMillis < TimeUnit.MINUTES.toMillis(1)) {
            return 0;
        } else {
            millis = currentTimeMillis / TimeUnit.MINUTES.toMillis(1);
            millis2 = TimeUnit.MINUTES.toMillis(1);
        }
        return millis * millis2;
    }

    private void setCarrierInfoTextStyle(TextView textView, int i, Typeface typeface) {
        textView.setTextColor(Utils.getColorAttr(getContext(), i));
        textView.setTypeface(typeface);
    }

    /* access modifiers changed from: package-private */
    public long getHistoricalUsageLevel() {
        return new DataUsageController(getContext()).getHistoricalUsageLevel(NetworkTemplate.buildTemplateWifiWildcard());
    }
}
