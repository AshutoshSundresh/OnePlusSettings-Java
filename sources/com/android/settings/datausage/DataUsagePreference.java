package com.android.settings.datausage;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.util.AttributeSet;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.R$attr;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.datausage.TemplatePreference;
import com.android.settingslib.net.DataUsageController;

public class DataUsagePreference extends Preference implements TemplatePreference {
    private int mSubId;
    private NetworkTemplate mTemplate;
    private int mTitleRes;

    public DataUsagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{16843233}, TypedArrayUtils.getAttr(context, R$attr.preferenceStyle, 16842894), 0);
        this.mTitleRes = obtainStyledAttributes.getResourceId(0, 0);
        obtainStyledAttributes.recycle();
    }

    @Override // com.android.settings.datausage.TemplatePreference
    public void setTemplate(NetworkTemplate networkTemplate, int i, TemplatePreference.NetworkServices networkServices) {
        this.mTemplate = networkTemplate;
        this.mSubId = i;
        DataUsageController dataUsageController = getDataUsageController();
        if (this.mTemplate.isMatchRuleMobile()) {
            setTitle(C0017R$string.app_cellular_data_usage);
        } else {
            dataUsageController.getDataUsageInfo(this.mTemplate);
            setTitle(this.mTitleRes);
        }
        if (dataUsageController.getHistoricalUsageLevel(networkTemplate) > 0) {
            setIntent(getIntent());
            return;
        }
        setIntent(null);
        setEnabled(false);
    }

    @Override // androidx.preference.Preference
    public Intent getIntent() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("network_template", this.mTemplate);
        bundle.putInt("sub_id", this.mSubId);
        bundle.putInt("network_type", !this.mTemplate.isMatchRuleMobile() ? 1 : 0);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setDestination(DataUsageList.class.getName());
        subSettingLauncher.setSourceMetricsCategory(0);
        if (this.mTemplate.isMatchRuleMobile()) {
            subSettingLauncher.setTitleRes(C0017R$string.app_cellular_data_usage);
        } else {
            subSettingLauncher.setTitleRes(this.mTitleRes);
        }
        return subSettingLauncher.toIntent();
    }

    /* access modifiers changed from: package-private */
    public DataUsageController getDataUsageController() {
        return new DataUsageController(getContext());
    }
}
