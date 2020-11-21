package com.android.settings.panel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.FeatureFlagUtils;
import androidx.constraintlayout.widget.R$styleable;
import com.android.settings.C0017R$string;
import com.android.settings.SubSettings;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settings.wifi.WifiSettings;
import com.android.settings.wifi.WifiSettings2;
import java.util.ArrayList;
import java.util.List;

public class WifiPanel implements PanelContent {
    private final Context mContext;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1687;
    }

    public static WifiPanel create(Context context) {
        return new WifiPanel(context);
    }

    private WifiPanel(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override // com.android.settings.panel.PanelContent
    public CharSequence getTitle() {
        return this.mContext.getText(C0017R$string.wifi_settings);
    }

    @Override // com.android.settings.panel.PanelContent
    public List<Uri> getSlices() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(CustomSliceRegistry.WIFI_SLICE_URI);
        return arrayList;
    }

    @Override // com.android.settings.panel.PanelContent
    public Intent getSeeMoreIntent() {
        Intent intent;
        String charSequence = this.mContext.getText(C0017R$string.wifi_settings).toString();
        if (FeatureFlagUtils.isEnabled(this.mContext, "settings_wifitracker2")) {
            intent = SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, WifiSettings2.class.getName(), null, charSequence, R$styleable.Constraint_layout_goneMarginTop);
        } else {
            intent = SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, WifiSettings.class.getName(), null, charSequence, R$styleable.Constraint_layout_goneMarginTop);
        }
        intent.setClassName(this.mContext.getPackageName(), SubSettings.class.getName());
        intent.addFlags(268435456);
        return intent;
    }
}
