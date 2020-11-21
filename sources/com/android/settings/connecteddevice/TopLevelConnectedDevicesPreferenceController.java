package com.android.settings.connecteddevice;

import android.content.Context;
import android.content.IntentFilter;
import android.icu.text.ListFormatter;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.nfc.NfcPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.ArrayList;

public class TopLevelConnectedDevicesPreferenceController extends BasePreferenceController {
    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public TopLevelConnectedDevicesPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_top_level_connected_devices) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (new NfcPreferenceController(this.mContext, NfcPreferenceController.KEY_TOGGLE_NFC).isAvailable()) {
            return this.mContext.getString(C0017R$string.oneplus_top_level_connected_summary);
        }
        ArrayList arrayList = new ArrayList();
        String string = this.mContext.getString(C0017R$string.op_wifi_display_summary);
        String lowerCase = this.mContext.getString(C0017R$string.nfc_payment_settings_title).toLowerCase();
        arrayList.add(string);
        arrayList.add(lowerCase);
        return ListFormatter.getInstance().format(arrayList);
    }
}
