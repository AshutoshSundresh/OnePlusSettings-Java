package com.android.settings.panel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.android.settings.C0017R$string;
import com.android.settings.network.AirplaneModePreferenceController;
import com.android.settings.slices.CustomSliceRegistry;
import java.util.ArrayList;
import java.util.List;

public class InternetConnectivityPanel implements PanelContent {
    private final Context mContext;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1654;
    }

    public static InternetConnectivityPanel create(Context context) {
        return new InternetConnectivityPanel(context);
    }

    private InternetConnectivityPanel(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override // com.android.settings.panel.PanelContent
    public CharSequence getTitle() {
        return this.mContext.getText(C0017R$string.internet_connectivity_panel_title);
    }

    @Override // com.android.settings.panel.PanelContent
    public List<Uri> getSlices() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(CustomSliceRegistry.WIFI_SLICE_URI);
        arrayList.add(CustomSliceRegistry.MOBILE_DATA_SLICE_URI);
        arrayList.add(AirplaneModePreferenceController.SLICE_URI);
        return arrayList;
    }

    @Override // com.android.settings.panel.PanelContent
    public Intent getSeeMoreIntent() {
        return new Intent("android.settings.WIRELESS_SETTINGS").addFlags(268435456);
    }
}
