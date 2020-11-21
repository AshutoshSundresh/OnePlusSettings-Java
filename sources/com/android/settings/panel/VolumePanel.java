package com.android.settings.panel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.android.settings.C0017R$string;
import com.android.settings.slices.CustomSliceRegistry;
import java.util.ArrayList;
import java.util.List;

public class VolumePanel implements PanelContent {
    private final Context mContext;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1655;
    }

    @Override // com.android.settings.panel.PanelContent
    public int getViewType() {
        return 1;
    }

    public static VolumePanel create(Context context) {
        return new VolumePanel(context);
    }

    private VolumePanel(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override // com.android.settings.panel.PanelContent
    public CharSequence getTitle() {
        return this.mContext.getText(C0017R$string.sound_settings);
    }

    @Override // com.android.settings.panel.PanelContent
    public List<Uri> getSlices() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(CustomSliceRegistry.REMOTE_MEDIA_SLICE_URI);
        arrayList.add(CustomSliceRegistry.VOLUME_MEDIA_URI);
        arrayList.add(CustomSliceRegistry.MEDIA_OUTPUT_INDICATOR_SLICE_URI);
        arrayList.add(CustomSliceRegistry.VOLUME_CALL_URI);
        arrayList.add(CustomSliceRegistry.VOLUME_RINGER_URI);
        arrayList.add(CustomSliceRegistry.VOLUME_ALARM_URI);
        return arrayList;
    }

    @Override // com.android.settings.panel.PanelContent
    public Intent getSeeMoreIntent() {
        return new Intent("android.settings.SOUND_SETTINGS").addFlags(268435456);
    }
}
