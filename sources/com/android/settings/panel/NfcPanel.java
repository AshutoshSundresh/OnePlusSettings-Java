package com.android.settings.panel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.android.settings.C0017R$string;
import com.android.settings.SubSettings;
import com.android.settings.connecteddevice.AdvancedConnectedDeviceDashboardFragment;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.SliceBuilderUtils;
import java.util.ArrayList;
import java.util.List;

public class NfcPanel implements PanelContent {
    private final Context mContext;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1656;
    }

    public static NfcPanel create(Context context) {
        return new NfcPanel(context);
    }

    private NfcPanel(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override // com.android.settings.panel.PanelContent
    public CharSequence getTitle() {
        return this.mContext.getText(C0017R$string.nfc_quick_toggle_title);
    }

    @Override // com.android.settings.panel.PanelContent
    public List<Uri> getSlices() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(CustomSliceRegistry.NFC_SLICE_URI);
        return arrayList;
    }

    @Override // com.android.settings.panel.PanelContent
    public Intent getSeeMoreIntent() {
        Intent buildSearchResultPageIntent = SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, AdvancedConnectedDeviceDashboardFragment.class.getName(), null, this.mContext.getText(C0017R$string.connected_device_connections_title).toString(), 747);
        buildSearchResultPageIntent.setClassName(this.mContext.getPackageName(), SubSettings.class.getName());
        buildSearchResultPageIntent.addFlags(268435456);
        return buildSearchResultPageIntent;
    }
}
