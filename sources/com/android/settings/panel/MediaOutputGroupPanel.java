package com.android.settings.panel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.graphics.drawable.IconCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settingslib.media.LocalMediaManager;
import com.android.settingslib.media.MediaDevice;
import java.util.ArrayList;
import java.util.List;

public class MediaOutputGroupPanel implements PanelContent, LocalMediaManager.DeviceCallback, LifecycleObserver {
    private PanelContentCallback mCallback;
    private final Context mContext;
    private LocalMediaManager mLocalMediaManager;
    private final String mPackageName;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1835;
    }

    @Override // com.android.settings.panel.PanelContent
    public Intent getSeeMoreIntent() {
        return null;
    }

    @Override // com.android.settings.panel.PanelContent
    public int getViewType() {
        return 2;
    }

    public static MediaOutputGroupPanel create(Context context, String str) {
        return new MediaOutputGroupPanel(context, str);
    }

    private MediaOutputGroupPanel(Context context, String str) {
        this.mContext = context.getApplicationContext();
        this.mPackageName = str;
    }

    @Override // com.android.settings.panel.PanelContent
    public CharSequence getTitle() {
        return this.mContext.getText(C0017R$string.media_output_group_panel_title);
    }

    @Override // com.android.settings.panel.PanelContent
    public CharSequence getSubTitle() {
        int size = this.mLocalMediaManager.getSelectedMediaDevice().size();
        if (size == 1) {
            return this.mContext.getText(C0017R$string.media_output_group_panel_single_device_summary);
        }
        return this.mContext.getString(C0017R$string.media_output_group_panel_multiple_devices_summary, Integer.valueOf(size));
    }

    @Override // com.android.settings.panel.PanelContent
    public IconCompat getIcon() {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_arrow_back);
        createWithResource.setTint(-16777216);
        return createWithResource;
    }

    @Override // com.android.settings.panel.PanelContent
    public List<Uri> getSlices() {
        ArrayList arrayList = new ArrayList();
        Uri build = CustomSliceRegistry.MEDIA_OUTPUT_GROUP_SLICE_URI.buildUpon().clearQuery().appendQueryParameter("media_package_name", this.mPackageName).build();
        CustomSliceRegistry.MEDIA_OUTPUT_GROUP_SLICE_URI = build;
        arrayList.add(build);
        return arrayList;
    }

    @Override // com.android.settings.panel.PanelContent
    public Intent getHeaderIconIntent() {
        return new Intent().setAction("com.android.settings.panel.action.MEDIA_OUTPUT").addFlags(268435456).putExtra("com.android.settings.panel.extra.PACKAGE_NAME", this.mPackageName);
    }

    @Override // com.android.settings.panel.PanelContent
    public void registerCallback(PanelContentCallback panelContentCallback) {
        this.mCallback = panelContentCallback;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (this.mLocalMediaManager == null) {
            this.mLocalMediaManager = new LocalMediaManager(this.mContext, this.mPackageName, null);
        }
        this.mLocalMediaManager.registerCallback(this);
        this.mLocalMediaManager.startScan();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mLocalMediaManager.unregisterCallback(this);
        this.mLocalMediaManager.stopScan();
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onDeviceListUpdate(List<MediaDevice> list) {
        PanelContentCallback panelContentCallback = this.mCallback;
        if (panelContentCallback != null) {
            panelContentCallback.onHeaderChanged();
        }
    }
}
