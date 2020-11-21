package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.OpFeatures;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.DonutView;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.oneplus.settings.utils.OPUtils;

public class StorageSummaryDonutPreference extends Preference implements View.OnClickListener {
    private double mPercent;

    public StorageSummaryDonutPreference(Context context) {
        this(context, null);
    }

    public StorageSummaryDonutPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPercent = -1.0d;
        setLayoutResource(C0012R$layout.op_storage_summary_donut);
        setEnabled(false);
    }

    public void setPercent(long j, long j2) {
        if (j2 != 0) {
            this.mPercent = ((double) j) / ((double) j2);
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setClickable(false);
        DonutView donutView = (DonutView) preferenceViewHolder.findViewById(C0010R$id.donut);
        if (donutView != null) {
            donutView.setPercentage(this.mPercent);
        }
        Button button = (Button) preferenceViewHolder.findViewById(C0010R$id.deletion_helper_button);
        if (button != null) {
            button.setOnClickListener(this);
        }
    }

    public void onClick(View view) {
        Intent intent;
        if (view != null && C0010R$id.deletion_helper_button == view.getId()) {
            Context context = getContext();
            MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
            metricsFeatureProvider.logClickedPreference(this, getExtras().getInt("category"));
            metricsFeatureProvider.action(context, 840, new Pair[0]);
            if (OpFeatures.isSupport(new int[]{1}) || !OPUtils.isAppExist(context, "com.oneplus.filemanager")) {
                intent = new Intent("android.os.storage.action.MANAGE_STORAGE");
            } else {
                intent = new Intent("com.oneplus.filemanager.action.SMART_CLEAN");
            }
            intent.addFlags(268435456);
            getContext().startActivity(intent);
        }
    }
}
